/**
 * 题目管理器 — reactive 题目 CRUD + 校验
 */
import { ref, computed, type Ref, type ComputedRef } from 'vue'
import type { Question, QuestionType } from '../types/question'
import { DEFAULT_SCORES, genQuestionId, QUESTION_TYPE_LABELS, isQuestionComplete } from '../types/question'

/** 创建一个空题目 */
function createEmptyQuestion(type: QuestionType, order: number): Question {
  const base = {
    id: genQuestionId(),
    type,
    title: '',
    score: DEFAULT_SCORES[type],
    scoreManual: false,
    order,
  }

  switch (type) {
    case 'choice':
      return {
        ...base,
        type: 'choice',
        options: [
          { key: 'A', text: '' },
          { key: 'B', text: '' },
          { key: 'C', text: '' },
          { key: 'D', text: '' },
        ],
        correctAnswer: 'A',
      }
    case 'fill-blank':
      return { ...base, type: 'fill-blank', correctAnswer: '' }
    case 'true-false':
      return { ...base, type: 'true-false', correctAnswer: true }
    case 'essay':
      return { ...base, type: 'essay', referenceAnswer: '' }
    case 'calculation':
      return { ...base, type: 'calculation', correctAnswer: '' }
  }
}

export function useQuestionManager(initialQuestions: Question[] = []) {
  const questions: Ref<Question[]> = ref([...initialQuestions])

  const questionCount: ComputedRef<number> = computed(() => questions.value.length)

  /** 添加题目 */
  function addQuestion(type: QuestionType): Question {
    const q = createEmptyQuestion(type, questions.value.length + 1)
    questions.value.push(q)
    return q
  }

  /** 更新题目 */
  function updateQuestion(id: string, updates: Partial<Question>): void {
    const idx = questions.value.findIndex(q => q.id === id)
    if (idx === -1) return
    questions.value[idx] = { ...questions.value[idx], ...updates } as Question
  }

  /** 替换完整题目（编辑器 emit 整个对象） */
  function replaceQuestion(q: Question): void {
    const idx = questions.value.findIndex(item => item.id === q.id)
    if (idx === -1) return
    questions.value[idx] = q
  }

  /** 删除题目 */
  function removeQuestion(id: string): void {
    questions.value = questions.value.filter(q => q.id !== id)
    // 重新编号
    questions.value.forEach((q, i) => { q.order = i + 1 })
  }

  /** 上移/下移 */
  function moveQuestion(id: string, direction: 'up' | 'down'): void {
    const idx = questions.value.findIndex(q => q.id === id)
    if (idx === -1) return
    const target = direction === 'up' ? idx - 1 : idx + 1
    if (target < 0 || target >= questions.value.length) return
    const arr = [...questions.value]
    ;[arr[idx], arr[target]] = [arr[target], arr[idx]]
    arr.forEach((q, i) => { q.order = i + 1 })
    questions.value = arr
  }

  /** 获取单题 */
  function getQuestion(id: string): Question | undefined {
    return questions.value.find(q => q.id === id)
  }

  /** 批量导入 */
  function importQuestions(parsed: Question[]): void {
    const startOrder = questions.value.length
    parsed.forEach((q, i) => {
      q.id = q.id || genQuestionId()
      q.order = startOrder + i + 1
      if (!q.score) q.score = DEFAULT_SCORES[q.type] || 5
      if (q.scoreManual === undefined) q.scoreManual = false
    })
    questions.value.push(...parsed)
  }

  /** 校验所有题目 */
  function validateAll(): { valid: boolean; errors: string[] } {
    const errors: string[] = []
    questions.value.forEach((q, i) => {
      const no = i + 1
      if (!isQuestionComplete(q)) {
        const label = QUESTION_TYPE_LABELS[q.type] || q.type
        errors.push(`第${no}题（${label}）：题目未完成`)
      }
    })
    return { valid: errors.length === 0, errors }
  }

  /** 获取指定类型的所有题目 */
  function getTypeQuestions(type: QuestionType): Question[] {
    return questions.value.filter(q => q.type === type)
  }

  /** 获取指定类型的题目数量 */
  function getTypeCount(type: QuestionType): number {
    return questions.value.filter(q => q.type === type).length
  }

  /** 获取指定类型中是否有手动设分的题目 */
  function hasManualScoreInType(type: QuestionType): boolean {
    return questions.value.some(q => q.type === type && q.scoreManual === true)
  }

  /** 获取题目类型数量统计 */
  const typeCounts = computed(() => {
    const counts: Record<string, number> = {}
    questions.value.forEach(q => {
      counts[q.type] = (counts[q.type] || 0) + 1
    })
    return counts
  })

  /** 清空所有题目 */
  function clearQuestions() {
    questions.value = []
  }

  return {
    questions,
    questionCount,
    typeCounts,
    addQuestion,
    updateQuestion,
    replaceQuestion,
    removeQuestion,
    moveQuestion,
    getQuestion,
    importQuestions,
    clearQuestions,
    validateAll,
    getTypeQuestions,
    getTypeCount,
    hasManualScoreInType,
  }
}
