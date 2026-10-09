/**
 * 发布页 localStorage 持久化工具
 * 负责读写作业/考试列表，并迁移旧格式数据
 */
import type { HomeworkItem, ExamItem } from '../types/question'

const HOMEWORK_KEY = 'teacher_homework_list'
const EXAM_KEY = 'teacher_exam_list'

/** 迁移旧格式：questions 从 string → Question[]（旧文本不可自动转换，清空） */
function migrateHomeworkItem(item: any): HomeworkItem {
  if (typeof item.questions === 'string') {
    return { ...item, questions: [] }
  }
  return item as HomeworkItem
}

function migrateExamItem(item: any): ExamItem {
  if (typeof item.questions === 'string') {
    return { ...item, questions: [] }
  }
  return item as ExamItem
}

/** 读取作业列表 */
export function loadHomeworkList(): HomeworkItem[] {
  try {
    const raw = localStorage.getItem(HOMEWORK_KEY)
    if (!raw) return []
    const list = JSON.parse(raw)
    if (!Array.isArray(list)) return []
    return list.map(migrateHomeworkItem)
  } catch {
    return []
  }
}

/** 写入作业列表 */
export function saveHomeworkList(list: HomeworkItem[]): void {
  try {
    localStorage.setItem(HOMEWORK_KEY, JSON.stringify(list))
  } catch { /* silently fail — storage full or unavailable */ }
}

/** 读取考试列表 */
export function loadExamList(): ExamItem[] {
  try {
    const raw = localStorage.getItem(EXAM_KEY)
    if (!raw) return []
    const list = JSON.parse(raw)
    if (!Array.isArray(list)) return []
    return list.map(migrateExamItem)
  } catch {
    return []
  }
}

/** 写入考试列表 */
export function saveExamList(list: ExamItem[]): void {
  try {
    localStorage.setItem(EXAM_KEY, JSON.stringify(list))
  } catch { /* silently fail — storage full or unavailable */ }
}
