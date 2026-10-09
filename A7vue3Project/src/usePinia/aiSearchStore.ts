/**
 * AI 搜索课件后台任务管理 Store
 * 管理"创建课程 → AI搜索 → 满意/重搜 → 创建"的完整异步流程
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import { searchCoursewareApi } from '../api/teacher/searchCoursewareApi'
import type { R } from '../response/R'

export interface AISearchTask {
  /** 唯一任务 ID */
  taskId: string
  /** 课程名称 */
  courseName: string
  /** 课时数 */
  courseHours: number
  /** 学分 */
  courseScore: number
  /** 搜索提示词 */
  searchPrompt: string
  /** 教师用户名 */
  teacherUsername: string
  /** 任务状态 */
  status: 'searching' | 'completed' | 'failed' | 'satisfied'
  /** AI 搜索结果 (markdown) */
  result: string
  /** 错误信息 */
  error: string
  /** 创建时间戳 */
  createdAt: number
  /** 是否正在被查看（modal 打开中） */
  isViewing: boolean
}

function genTaskId(): string {
  return 'ai_search_' + Date.now() + '_' + Math.random().toString(36).slice(2, 8)
}

export const useAiSearchStore = defineStore('aiSearch', () => {
  // ===== State =====
  const tasks = ref<AISearchTask[]>([])

  // ===== Getters（直接访问 tasks.value） =====
  function getTask(taskId: string): AISearchTask | undefined {
    return tasks.value.find(t => t.taskId === taskId)
  }

  function hasSearchingTask(): boolean {
    return tasks.value.some(t => t.status === 'searching')
  }

  // ===== Actions =====

  /**
   * 启动后台 AI 搜索
   * 1. 创建 pending 任务
   * 2. 发起 API 调用
   * 3. 根据结果更新任务状态
   * 4. 搜索完成后，如果用户未在查看，弹出通知
   */
  async function startSearch(params: {
    courseName: string
    courseHours: number
    courseScore: number
    searchPrompt: string
    teacherUsername: string
  }): Promise<string> {
    const taskId = genTaskId()
    const task: AISearchTask = {
      taskId,
      courseName: params.courseName,
      courseHours: params.courseHours,
      courseScore: params.courseScore,
      searchPrompt: params.searchPrompt,
      teacherUsername: params.teacherUsername,
      status: 'searching',
      result: '',
      error: '',
      createdAt: Date.now(),
      isViewing: false,
    }

    // 添加到任务列表
    tasks.value.unshift(task)

    // 异步发起搜索（不阻塞）
    doSearch(taskId)

    return taskId
  }

  /**
   * 重新搜索（保留原有参数，清空结果）
   */
  async function retrySearch(taskId: string) {
    const task = getTask(taskId)
    if (!task) return

    task.status = 'searching'
    task.result = ''
    task.error = ''

    doSearch(taskId)
  }

  /**
   * 标记为满意（准备创建课程）
   */
  function markSatisfied(taskId: string) {
    const task = getTask(taskId)
    if (task) {
      task.status = 'satisfied'
    }
  }

  /**
   * 移除任务（课程创建成功或用户取消后）
   */
  function removeTask(taskId: string) {
    const idx = tasks.value.findIndex(t => t.taskId === taskId)
    if (idx !== -1) {
      tasks.value.splice(idx, 1)
    }
  }

  /**
   * 设置是否正在查看
   */
  function setViewing(taskId: string, viewing: boolean) {
    const task = getTask(taskId)
    if (task) {
      task.isViewing = viewing
    }
  }

  // ===== Private =====

  async function doSearch(taskId: string) {
    const task = getTask(taskId)
    if (!task) return

    try {
      const res = await searchCoursewareApi({
        course_name: task.courseName,
        teacher_username: task.teacherUsername,
        search_prompt: task.searchPrompt,
      })

      // 重新获取 task（可能在此期间被移除）
      const currentTask = getTask(taskId)
      if (!currentTask) return

      const R: R = res.data
      if (R.code === 200 && R.data?.[0]) {
        currentTask.status = 'completed'
        currentTask.result = R.data[0].coursewareContent || ''
        if (!currentTask.result) {
          currentTask.status = 'failed'
          currentTask.error = '搜索未返回有效内容，请调整提示词后重试'
        } else if (!currentTask.isViewing) {
          // 用户未在查看 → 弹出通知
          message.success(`"${currentTask.courseName}" 的 AI 课件搜索完成，点击课程卡片查看结果`, 5)
        }
      } else {
        currentTask.status = 'failed'
        currentTask.error = R.msg || 'AI 搜索失败，请重试'
        if (!currentTask.isViewing) {
          message.error(`"${currentTask.courseName}" 的 AI 搜索失败: ${currentTask.error}`, 5)
        }
      }
    } catch (e: any) {
      const currentTask = getTask(taskId)
      if (!currentTask) return

      currentTask.status = 'failed'
      if (e?.code === 'ECONNABORTED' || e?.message?.includes('timeout')) {
        currentTask.error = '搜索请求超时，请检查网络后重试'
      } else if (e?.response?.status === 500) {
        currentTask.error = 'AI 服务内部错误，请稍后重试'
      } else if (!e?.response) {
        currentTask.error = '无法连接到 AI 服务，请检查网络连接'
      } else {
        currentTask.error = 'AI 搜索发生未知错误，请重试'
      }

      if (!currentTask.isViewing) {
        message.error(`"${currentTask.courseName}" 的 AI 搜索失败: ${currentTask.error}`, 5)
      }
    }
  }

  return {
    tasks,
    getTask,
    hasSearchingTask,
    startSearch,
    retrySearch,
    markSatisfied,
    removeTask,
    setViewing,
  }
})
