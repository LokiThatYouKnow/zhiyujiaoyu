/**
 * Markdown → 结构化 Question[] 解析器
 * 用于从 AI 智能出题的 Markdown 格式结果中提取题目
 */
import type { Question, QuestionType } from '../types/question'
import { DEFAULT_SCORES, genQuestionId } from '../types/question'

/** 题型关键词映射 */
const SECTION_PATTERNS: { pattern: RegExp; type: QuestionType }[] = [
  { pattern: /选择[题目]|单选题|多项选择题/i, type: 'choice' },
  { pattern: /填空[题目]|填空题/i, type: 'fill-blank' },
  { pattern: /判断[题目]|判断题|是非题/i, type: 'true-false' },
  { pattern: /应用[题目]|应用题|简答题|问答题|论述题/i, type: 'essay' },
  { pattern: /计算[题目]|计算题/i, type: 'calculation' },
]

/** 答案关键词 */
const ANSWER_PATTERNS = [
  /[*_]{0,2}答案[*_]{0,2}[：:]\s*(.+)/,
  /[*_]{0,2}正确答案[*_]{0,2}[：:]\s*(.+)/,
  /^答案[：:]\s*(.+)/m,
  /^Answer[：:]\s*(.+)/im,
]

/**
 * 从 Markdown 解析题目列表
 */
export function parseQuestionsFromMarkdown(markdown: string): Question[] {
  if (!markdown || !markdown.trim()) return []

  const questions: Question[] = []
  let order = 0

  // 1. 按 ## 标题分段
  const sections = markdown.split(/^##\s+/gm)
  // sections[0] 是第一个 ## 之前的内容（前言），后面每段是 "标题\n内容"

  // 2. 构建完整分段列表
  const chunks: { type: QuestionType | null; content: string }[] = []

  for (let i = 0; i < sections.length; i++) {
    const section = sections[i]
    const lines = section.split('\n')
    const headerLine = lines[0]?.trim() || ''
    const body = lines.length > 1 ? lines.slice(1).join('\n') : ''

    // 匹配题型
    let matchedType: QuestionType | null = null
    for (const sp of SECTION_PATTERNS) {
      if (sp.pattern.test(headerLine)) {
        matchedType = sp.type
        break
      }
    }

    chunks.push({ type: matchedType, content: body || section })
  }

  // 如果第一个 chunk 没有类型且内容非空，尝试按全局匹配
  // 如果整个输入没有 ## 标题，则 sections 只有一个元素（整个文本）

  // 3. 对每个题型分段解析具体题目
  for (const chunk of chunks) {
    if (!chunk.type || !chunk.content.trim()) continue
    const parsed = parseSection(chunk.type, chunk.content, order)
    questions.push(...parsed)
    order += parsed.length
  }

  return questions
}

/** 解析某个题型分段中的题目 */
function parseSection(type: QuestionType, content: string, startOrder: number): Question[] {
  const questions: Question[] = []

  // 按编号拆分题目: **1.** / 1. / （1）/ (1) / **第1题**
  const items = content.split(/\n(?=[*_]{0,2}(?:\d+)[.、．]|[*_]{0,2}第\d+题|[*_]{0,2}[（(]\d+[）)]\s*)/)

  let order = startOrder
  for (const item of items) {
    const trimmed = item.trim()
    if (!trimmed || trimmed.length < 3) continue

    const question = parseSingleQuestion(type, trimmed, order)
    if (question) {
      questions.push(question)
      order++
    }
  }

  return questions
}

/** 判断一行内容是否是选项行（A. xxx 等） */
function isOptionLine(line: string): boolean {
  return /^[A-D][.、．)]\s*.+/.test(line)
}

/** 判断一行内容是否包含答案标记 */
function isAnswerLine(line: string): boolean {
  return extractAnswer(line) !== null
}

/** 从 body 行中分离出"题目描述内容"和"元数据行"（选项/答案） */
function splitBodyContent(
  body: string[],
  type: QuestionType,
): { contentLines: string[]; metaLines: string[] } {
  // 对于选择题：选项之前的行属于题目描述
  // 对于其他题型：答案行之前的内容属于题目描述
  let splitIdx = body.length // 默认全部属于 meta
  for (let i = 0; i < body.length; i++) {
    const line = body[i]
    if (type === 'choice' && isOptionLine(line)) {
      splitIdx = i
      break
    }
    if (isAnswerLine(line)) {
      splitIdx = i
      break
    }
  }
  return {
    contentLines: body.slice(0, splitIdx),
    metaLines: body.slice(splitIdx),
  }
}

/** 解析单个题目 */
function parseSingleQuestion(type: QuestionType, text: string, order: number): Question | null {
  const lines = text.split('\n').map(l => l.trim()).filter(Boolean)
  if (lines.length === 0) return null

  // 提取题干（第一行，去掉编号标记）
  let title = lines[0].replace(/^[*_]{0,2}(?:\d+)[.、．]\s*/, '').replace(/^[*_]{0,2}第\d+题[*_]{0,2}[：:]*\s*/, '').replace(/^[*_]{0,2}[（(]\d+[）)]\s*/, '').replace(/^\*+(.+?)\*+$/, '$1')
  if (!title || title.length < 2) return null

  // 分离题目描述内容（代码块等）和元数据（选项/答案）
  const { contentLines, metaLines } = splitBodyContent(lines.slice(1), type)

  // 将题目描述内容追加到 title
  const fullTitle = contentLines.length > 0
    ? title + '\n' + contentLines.join('\n')
    : title

  // 根据题型解析，传入元数据行
  switch (type) {
    case 'choice': return parseChoice(fullTitle, metaLines, order)
    case 'fill-blank': return parseFillBlank(fullTitle, metaLines, order)
    case 'true-false': return parseTrueFalse(fullTitle, metaLines, order)
    case 'essay': {
      // 对于应用题：如果没有显式答案行，内容行本身就是参考答案
      const q = parseEssay(fullTitle, metaLines, contentLines, order)
      return q
    }
    case 'calculation': return parseCalculation(fullTitle, metaLines, order)
    default: return null
  }
}

/** 解析选择题 */
function parseChoice(title: string, body: string[], order: number): Question {
  const options: { key: 'A' | 'B' | 'C' | 'D'; text: string }[] = []
  let correctAnswer: 'A' | 'B' | 'C' | 'D' = 'A'

  const optionKeys: ('A' | 'B' | 'C' | 'D')[] = ['A', 'B', 'C', 'D']
  let keyIdx = 0

  for (let i = 0; i < body.length && keyIdx < 4; i++) {
    const line = body[i]
    // 匹配 "A. xxx" 或 "A、xxx" 或 "A) xxx"
    const match = line.match(/^([A-D])[.、．)]\s*(.+)/)
    if (match && match[1] === optionKeys[keyIdx]) {
      options.push({ key: optionKeys[keyIdx], text: match[2].trim() })
      keyIdx++
    } else {
      // 检查是否是答案行
      const ans = extractAnswer(line)
      if (ans && /^[A-D]$/i.test(ans)) {
        correctAnswer = ans.toUpperCase() as 'A' | 'B' | 'C' | 'D'
      }
    }
  }

  // 如果没匹配到答案行，在全文中搜索
  if (!options.some(o => o.key === correctAnswer)) {
    for (const line of body) {
      const ans = extractAnswer(line)
      if (ans && /^[A-D]$/i.test(ans)) {
        correctAnswer = ans.toUpperCase() as 'A' | 'B' | 'C' | 'D'
        break
      }
    }
  }

  return {
    id: genQuestionId(),
    type: 'choice',
    title,
    score: DEFAULT_SCORES.choice,
    order: order + 1,
    options,
    correctAnswer,
  }
}

/** 解析填空题 */
function parseFillBlank(title: string, body: string[], order: number): Question {
  let correctAnswer = ''
  for (const line of body) {
    const ans = extractAnswer(line)
    if (ans) { correctAnswer = ans; break }
  }
  return {
    id: genQuestionId(),
    type: 'fill-blank',
    title,
    score: DEFAULT_SCORES['fill-blank'],
    order: order + 1,
    correctAnswer,
  }
}

/** 解析判断题 */
function parseTrueFalse(title: string, body: string[], order: number): Question {
  let correctAnswer = true
  for (const line of body) {
    const ans = extractAnswer(line)
    if (ans) {
      correctAnswer = /^[对✅✓是TtYy]/.test(ans) || ans === '正确' || ans.toLowerCase() === 'true'
      break
    }
  }
  // 从题目本身推断
  if (/[错误❌].*[正确✅]/.test(title)) {
    // 混合判断，取答案行
  } else if (/正确[。.]?$|✅|对[。.]?$/.test(title)) {
    correctAnswer = true
  }
  return {
    id: genQuestionId(),
    type: 'true-false',
    title,
    score: DEFAULT_SCORES['true-false'],
    order: order + 1,
    correctAnswer,
  }
}

/** 解析应用题 */
function parseEssay(title: string, body: string[], contentLines: string[], order: number): Question {
  let referenceAnswer = ''
  for (const line of body) {
    const ans = extractAnswer(line)
    if (ans) { referenceAnswer = ans; break }
  }
  // 如果没有明确的答案行，内容行作为参考答案
  if (!referenceAnswer && contentLines.length > 0) {
    referenceAnswer = contentLines.join('\n')
  } else if (!referenceAnswer && body.length > 0) {
    referenceAnswer = body.join('\n')
  }
  return {
    id: genQuestionId(),
    type: 'essay',
    title,
    score: DEFAULT_SCORES.essay,
    order: order + 1,
    referenceAnswer,
  }
}

/** 解析计算题 */
function parseCalculation(title: string, body: string[], order: number): Question {
  let correctAnswer = ''
  let formula: string | undefined
  let steps: string | undefined

  for (const line of body) {
    const ans = extractAnswer(line)
    if (ans) { correctAnswer = ans; continue }
    // 检测公式
    if (/^[*_]{0,2}(?:公式|formula)[*_]{0,2}[：:]/i.test(line)) {
      formula = line.replace(/^[*_]{0,2}(?:公式|formula)[*_]{0,2}[：:]\s*/i, '')
      continue
    }
    // 检测步骤
    if (/^[*_]{0,2}(?:步骤|解题|解析|steps)[*_]{0,2}[：:]/i.test(line)) {
      steps = line.replace(/^[*_]{0,2}(?:步骤|解题|解析|steps)[*_]{0,2}[：:]\s*/i, '') +
        '\n' + body.slice(body.indexOf(line) + 1).join('\n')
      break
    }
  }

  return {
    id: genQuestionId(),
    type: 'calculation',
    title,
    score: DEFAULT_SCORES.calculation,
    order: order + 1,
    formula,
    correctAnswer,
    steps,
  }
}

/** 提取答案文本 */
function extractAnswer(line: string): string | null {
  for (const pattern of ANSWER_PATTERNS) {
    // 对于全局模式（带 m flag），匹配整行
    if (pattern.flags.includes('m')) {
      const match = line.match(pattern)
      if (match) return match[1].trim()
    } else {
      const match = line.match(pattern)
      if (match) return match[1].trim()
    }
  }
  // 额外匹配：行首就是 "A" / "B" 等简单情况
  const simpleMatch = line.match(/^[（(]([A-D])[)）]/)
  if (simpleMatch) return simpleMatch[1]
  return null
}
