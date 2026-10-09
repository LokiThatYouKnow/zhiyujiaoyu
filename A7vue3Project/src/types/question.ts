/** 题型标识 */
export type QuestionType = 'choice' | 'fill-blank' | 'true-false' | 'essay' | 'calculation'

/** 题型中文标签映射 */
export const QUESTION_TYPE_LABELS: Record<QuestionType, string> = {
  'choice': '选择题',
  'fill-blank': '填空题',
  'true-false': '判断题',
  'essay': '应用题',
  'calculation': '计算题',
}

/** 默认分值 */
export const DEFAULT_SCORES: Record<QuestionType, number> = {
  'choice': 5,
  'fill-blank': 5,
  'true-false': 3,
  'essay': 10,
  'calculation': 10,
}

/** 题目基础字段 */
export interface BaseQuestion {
  id: string
  type: QuestionType
  title: string         // 题干预览文本
  score: number         // 分值
  scoreManual?: boolean  // true = 用户手动设分，不参与自动均分
  order: number         // 排序序号
  difficulty?: 'easy' | 'medium' | 'hard'
}

/** 选择题 */
export interface ChoiceQuestion extends BaseQuestion {
  type: 'choice'
  options: { key: 'A' | 'B' | 'C' | 'D'; text: string }[]
  correctAnswer: 'A' | 'B' | 'C' | 'D'
}

/** 填空题 */
export interface FillBlankQuestion extends BaseQuestion {
  type: 'fill-blank'
  correctAnswer: string       // 多空用逗号分隔
}

/** 判断题 */
export interface TrueFalseQuestion extends BaseQuestion {
  type: 'true-false'
  correctAnswer: boolean
}

/** 应用题/简答题 */
export interface EssayQuestion extends BaseQuestion {
  type: 'essay'
  referenceAnswer: string     // 参考答案
  wordLimit?: number          // 字数限制
}

/** 计算题 */
export interface CalculationQuestion extends BaseQuestion {
  type: 'calculation'
  formula?: string            // 公式（LaTeX 或纯文本）
  correctAnswer: string       // 期望结果
  steps?: string              // 解题步骤
}

/** 判别联合类型 */
export type Question = ChoiceQuestion | FillBlankQuestion | TrueFalseQuestion | EssayQuestion | CalculationQuestion

/** 作业项 */
export interface HomeworkItem {
  id: string
  title: string
  description: string
  courseName: string
  questions: Question[]
  deadline: string
  questionCount: number
  status: 'draft' | 'published'
  createdAt: number
  updatedAt: number
}

/** 考试项 */
export interface ExamItem {
  id: string
  title: string
  description: string
  courseName: string
  questions: Question[]
  startTime: string
  endTime: string
  duration: number
  status: 'draft' | 'published'
  createdAt: number
  updatedAt: number
}

/** sessionStorage 跨页面 AI 导入数据 */
export interface PendingImportData {
  target: 'homework' | 'exam'
  courseName: string
  title: string
  startTime?: string
  endTime?: string
  rawMarkdown: string
  parsedQuestions: Question[]
}

/** sessionStorage 跨页面编辑数据 */
export interface PendingEditData {
  target: 'homework' | 'exam'
  itemId: string
  item: HomeworkItem | ExamItem
}

/** 生成唯一 ID */
export function genQuestionId(prefix: string = 'q'): string {
  return prefix + '_' + Math.random().toString(36).slice(2, 9)
}

/** 判断单个题目是否填写完整 */
export function isQuestionComplete(q: Question): boolean {
  if (!q.title.trim()) return false
  switch (q.type) {
    case 'choice': {
      const filledOptions = q.options.filter(o => o.text.trim())
      return filledOptions.length >= 2 && !!q.correctAnswer
    }
    case 'fill-blank':
      return q.title.includes('___') && !!q.correctAnswer.trim()
    case 'true-false':
      return q.correctAnswer !== undefined && q.correctAnswer !== null
    case 'essay':
      return !!q.referenceAnswer.trim()
    case 'calculation':
      return !!q.correctAnswer.trim()
    default:
      return false
  }
}
