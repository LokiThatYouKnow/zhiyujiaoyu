<template>
  <div class="grading-container">
    <div class="grading-topbar">
      <button class="btn-back" @click="handleBack">← 返回工作台</button>
      <h1 class="grading-title">批改管理</h1>
    </div>

    <div v-if="loading" class="loading-state">加载中...</div>

    <div v-else-if="!homeworkData" class="empty-state">
      <p>未找到作业数据</p>
      <button class="btn-back" @click="handleBack">返回</button>
    </div>

    <div v-else class="grading-body">
      <!-- Left: Student List -->
      <div class="grading-sidebar">
        <div class="sidebar-header">
          <h3>{{ homeworkData.title }}</h3>
          <span class="sidebar-badge">{{ homeworkData.courseName }}</span>
        </div>
        <div class="sidebar-stats">
          <span>提交: {{ submissions.length }} 份</span>
          <span>已批改: {{ submissions.filter(s => s.status === 'graded').length }} 份</span>
        </div>
        <div class="sidebar-actions">
          <button v-if="submissions.filter(s => s.status !== 'graded').length > 0"
            class="btn-ai-grade" @click="aiAutoGradeAll" :disabled="aiGrading">
            {{ aiGrading ? 'AI 批改中...' : '🤖 AI 智能批改全部' }}
          </button>
          <button v-if="submissions.filter(s => s.status === 'graded').length > 0"
            class="btn-retract-all" @click="retractAllGrades" :disabled="retracting">
            {{ retracting ? '撤回中...' : '↩ 撤回全部批改' }}
          </button>
        </div>
        <div class="student-list">
          <div v-for="(sub, idx) in submissions" :key="sub.id" class="student-item"
            :class="{ active: selectedIdx === idx, graded: sub.status === 'graded' }"
            @click="selectSubmission(idx)">
            <div class="si-left">
              <span class="si-name">{{ sub.student_name }}</span>
              <span class="si-username">{{ sub.student_username }}</span>
            </div>
            <div class="si-right">
              <span v-if="sub.status === 'graded'" class="si-score">{{ sub.total_score }}/{{ sub.max_total_score }}</span>
              <span v-else class="si-badge">待批改</span>
              <button v-if="sub.status === 'graded'" class="btn-retract-sm"
                @click.stop="retractSingleGrade(sub)" title="撤回该学生的批改">↩</button>
            </div>
          </div>
          <div v-if="submissions.length === 0" class="sidebar-empty">暂无学生提交</div>
        </div>
      </div>

      <!-- Right: Answer Details + Grading -->
      <div class="grading-main" v-if="selectedSubmission">
        <div class="grading-main-header">
          <div class="gmh-student">
            <span class="gmh-name">{{ selectedSubmission.student_name }}</span>
            <span class="gmh-time">{{ formatTime(selectedSubmission.submitted_at) }}</span>
          </div>
          <button class="btn-save" @click="saveGrade" :disabled="gradingSaving">
            {{ gradingSaving ? '保存中...' : '保存批改' }}
          </button>
          <button v-if="selectedSubmission.status === 'graded'" class="btn-retract"
            @click="retractSingleGrade(selectedSubmission)" :disabled="retracting">
            {{ retracting ? '撤回中...' : '↩ 撤回批改' }}
          </button>
        </div>

        <div class="answer-list">
          <div v-for="(ans, idx) in parsedAnswers" :key="idx" class="answer-card">
            <div class="ac-header">
              <span class="ac-num">第 {{ idx + 1 }} 题</span>
              <span class="ac-type">{{ getQuestionLabel(idx) }}</span>
              <span v-if="getCorrectAnswer(idx)" class="ac-correct">参考答案: {{ getCorrectAnswer(idx) }}</span>
            </div>
            <!-- Student Answer -->
            <div class="ac-answer">
              <label>学生答案:</label>
              <div class="ac-answer-text">{{ ans.answer || '(未作答)' }}</div>
            </div>
            <!-- Score Input -->
            <div class="ac-score">
              <label>得分:</label>
              <a-input-number v-model:value="parsedScores[idx].score" :min="0" :max="parsedScores[idx].maxScore" style="width:100px" />
              <span class="ac-max">/ {{ parsedScores[idx].maxScore }} 分</span>
              <span v-if="isAutoGraded(idx)" class="ac-auto-badge">自动判分</span>
            </div>
          </div>
        </div>

        <!-- Total Score -->
        <div class="grading-total">
          <span>总分: {{ computedTotalScore }} / {{ computedMaxTotalScore }}</span>
        </div>
      </div>

      <div v-else class="grading-main grading-main-empty">
        <p>← 请从左侧选择一份提交</p>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { getSubmissionsApi, gradeSubmissionApi, retractGradeApi, retractAllGradesApi } from '../../api/submissionApi'

interface SubmissionItem {
  id: number; homework_id: string; type: string; student_username: string; student_name: string
  course_name: string; issue_name: string; answers: string; scores: string
  total_score: number; max_total_score: number; status: string; submitted_at: number; graded_at: number
}

const router = useRouter()

const homeworkData = ref<any>(null)
const submissions = ref<SubmissionItem[]>([])
const selectedIdx = ref(-1)
const loading = ref(true)
const gradingSaving = ref(false)
const aiGrading = ref(false)
const retracting = ref(false)

// Parsed answers and scores for the selected submission
const parsedAnswers = ref<Array<{ questionId: string; answer: string }>>([])
const parsedScores = ref<Array<{ questionId: string; score: number; maxScore: number }>>([])

const selectedSubmission = computed(() => {
  if (selectedIdx.value < 0 || selectedIdx.value >= submissions.value.length) return null
  return submissions.value[selectedIdx.value]
})

const computedTotalScore = computed(() => parsedScores.value.reduce((s, sc) => s + sc.score, 0))
const computedMaxTotalScore = computed(() => parsedScores.value.reduce((s, sc) => s + sc.maxScore, 0))

function formatTime(ts: number): string {
  if (!ts) return '—'
  const d = new Date(ts)
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

function getQuestionLabel(idx: number): string {
  const qs = homeworkData.value?.questions
  if (qs && qs[idx]) {
    const labels: Record<string, string> = { choice: '选择题', 'fill-blank': '填空题', 'true-false': '判断题', essay: '应用题', calculation: '计算题' }
    return labels[qs[idx].type] || qs[idx].type || ''
  }
  return ''
}

function getCorrectAnswer(idx: number): string {
  const qs = homeworkData.value?.questions
  if (!qs || !qs[idx]) return ''
  const q = qs[idx]
  if (q.type === 'choice' || q.type === 'fill-blank' || q.type === 'calculation') return q.correctAnswer || ''
  if (q.type === 'true-false') return q.correctAnswer ? '正确' : '错误'
  return ''
}

/** Resolve the correct answer for comparison against student's actual answer text.
 *  For choice questions, correctAnswer may be a letter (A/B/C/D) while the student
 *  answer stores the full option text. This maps letters to option text. */
function resolveCorrectAnswerForComparison(idx: number): string | null {
  const qs = homeworkData.value?.questions
  if (!qs || !qs[idx]) return null
  const q = qs[idx]

  if (q.type === 'choice') {
    const correct = (q.correctAnswer || '').trim()
    if (!correct) return null
    // If correctAnswer is a single letter (A-D), map to option text
    const letterIndex = 'ABCDEFGH'.indexOf(correct.toUpperCase())
    if (letterIndex >= 0 && q.options && letterIndex < q.options.length) {
      return q.options[letterIndex]
    }
    // Otherwise, compare directly (correctAnswer might already be the full text)
    return correct
  }
  if (q.type === 'true-false') {
    return q.correctAnswer ? '正确' : '错误'
  }
  // fill-blank, calculation: direct comparison
  return (q.correctAnswer || '').trim() || null
}

function isAutoGraded(idx: number): boolean {
  const qs = homeworkData.value?.questions
  if (!qs || !qs[idx]) return false
  return ['choice', 'true-false', 'fill-blank'].includes(qs[idx].type)
}

async function selectSubmission(idx: number) {
  selectedIdx.value = idx
  const sub = submissions.value[idx]
  try {
    // Parse answers
    parsedAnswers.value = JSON.parse(sub.answers || '[]')
    // Parse or initialize scores
    const existingScores = JSON.parse(sub.scores || '[]')
    if (existingScores.length > 0) {
      parsedScores.value = existingScores
    } else {
      // Auto-grade objective questions
      parsedScores.value = parsedAnswers.value.map((ans, i) => {
        const maxScore = homeworkData.value?.questions?.[i]?.score || 10
        const correct = resolveCorrectAnswerForComparison(i)
        let score = 0
        if (correct && (ans.answer || '').trim() === correct.trim()) score = maxScore
        // For fill-blank, also do case-insensitive comparison
        if (!score && correct && (ans.answer || '').trim().toLowerCase() === correct.trim().toLowerCase()) score = maxScore
        return { questionId: ans.questionId, score, maxScore }
      })
    }
  } catch { /* ignore */ }
}

async function saveGrade() {
  if (!selectedSubmission.value) return
  gradingSaving.value = true
  const savedId = selectedSubmission.value.id
  try {
    const res = await gradeSubmissionApi({
      submission_id: String(selectedSubmission.value.id),
      scores: JSON.stringify(parsedScores.value),
      total_score: String(computedTotalScore.value),
      max_total_score: String(computedMaxTotalScore.value),
    })
    if (res.data?.code === 200) {
      message.success('批改已保存')
      // Update the current submission in-place instead of full reload
      const sub = submissions.value[selectedIdx.value]
      if (sub) {
        sub.status = 'graded'
        sub.total_score = computedTotalScore.value
        sub.max_total_score = computedMaxTotalScore.value
        sub.scores = JSON.stringify(parsedScores.value)
      }
      // Refresh in background to stay in sync with server
      await loadSubmissions()
      // Restore selection to the same submission
      const newIdx = submissions.value.findIndex((s: any) => s.id === savedId)
      if (newIdx >= 0) {
        selectedIdx.value = newIdx
      }
    } else {
      message.error(res.data?.msg || '保存失败')
    }
  } catch { message.error('保存失败') }
  gradingSaving.value = false
}

async function retractSingleGrade(sub: SubmissionItem) {
  if (retracting.value) return
  retracting.value = true
  try {
    const res = await retractGradeApi({ submission_id: String(sub.id) })
    if (res.data?.code === 200) {
      message.success('批改已撤回')
      // Update local state immediately
      sub.status = 'submitted'
      sub.total_score = 0
      sub.max_total_score = 0
      sub.scores = '[]'
      // Refresh from server to stay in sync
      await loadSubmissions()
      // Reset selection if current selection was retracted
      if (selectedSubmission.value?.id === sub.id) {
        selectedIdx.value = -1
      }
    } else {
      message.error(res.data?.msg || '撤回失败')
    }
  } catch { message.error('撤回失败，请检查网络') }
  retracting.value = false
}

async function retractAllGrades() {
  const hwId = homeworkData.value?.id
  if (!hwId || retracting.value) return
  const count = submissions.value.filter(s => s.status === 'graded').length
  if (count === 0) { message.info('没有已批改的提交'); return }
  retracting.value = true
  try {
    const res = await retractAllGradesApi({ homework_id: hwId })
    if (res.data?.code === 200) {
      message.success(res.data?.msg || `已撤回 ${count} 份批改`)
      await loadSubmissions()
      selectedIdx.value = -1
    } else {
      message.error(res.data?.msg || '撤回失败')
    }
  } catch { message.error('撤回失败，请检查网络') }
  retracting.value = false
}

async function loadSubmissions() {
  const hwId = homeworkData.value?.id
  if (!hwId) return
  try {
    const res = await getSubmissionsApi({ homework_id: hwId })
    if (res.data?.code === 200) {
      submissions.value = res.data.data || []
    }
  } catch { /* ignore */ }
}

async function aiAutoGradeAll() {
  const hwId = homeworkData.value?.id
  if (!hwId || aiGrading.value) return
  aiGrading.value = true
  try {
    const { aiAutoGradeApi } = await import('../../api/submissionApi')
    const res = await aiAutoGradeApi({ homework_id: hwId })
    if (res.data?.code === 200) {
      const r = res.data.data
      const graded = r.graded || 0
      const failed = r.failed || 0
      if (graded > 0 && failed === 0) {
        message.success(`AI 批改完成：成功 ${graded} 份`)
      } else if (graded > 0) {
        message.warning(`AI 批改部分完成：成功 ${graded} 份，失败 ${failed} 份`)
      } else if (failed > 0) {
        message.error(`AI 批改失败：${failed} 份提交均未能批改。请检查 LangChain 服务是否启动（端口 9084）`)
      } else {
        message.info('没有需要批改的提交')
      }
      await loadSubmissions()
      // Reset selection
      selectedIdx.value = -1
    } else {
      message.error(res.data?.msg || 'AI 批改失败')
    }
  } catch { message.error('AI 批改请求失败，请检查网络连接') }
  aiGrading.value = false
}

function handleBack() {
  const course = homeworkData.value?.courseName || ''
  router.push(`/teacher?tab=grading${course ? `&course=${encodeURIComponent(course)}` : ''}`)
}

onMounted(async () => {
  try {
    // Homework data passed via sessionStorage
    const raw = sessionStorage.getItem('grading_homework')
    if (raw) {
      homeworkData.value = JSON.parse(raw)
      await loadSubmissions()
    }
  } catch { /* ignore */ }
  loading.value = false
})
</script>

<style scoped>
.grading-container { padding: 24px 32px; width: 100%; }
.grading-topbar { display: flex; align-items: center; gap: 16px; margin-bottom: 20px; }
.grading-title { font-size: 18px; font-weight: 700; color: #0F172A; margin: 0; }
.btn-back {
  padding: 8px 16px; border: 1px solid #E2E8F0; border-radius: 8px; background: #fff;
  color: #475569; font-size: 13px; cursor: pointer; font-family: inherit;
}
.btn-back:hover { background: #F8FAFC; }

.loading-state, .empty-state { text-align: center; padding: 60px 20px; color: #94A3B8; }

.grading-body { display: grid; grid-template-columns: 320px 1fr; gap: 20px; min-height: 500px; }

/* Sidebar */
.grading-sidebar { background: #fff; border: 1px solid #E2E8F0; border-radius: 12px; overflow: hidden; }
.sidebar-header { padding: 16px; border-bottom: 1px solid #F1F5F9; }
.sidebar-header h3 { font-size: 15px; font-weight: 600; color: #0F172A; margin: 0 0 4px; }
.sidebar-badge { font-size: 12px; background: #EEF2FF; color: #4F46E5; padding: 2px 8px; border-radius: 10px; }
.sidebar-stats { display: flex; gap: 16px; padding: 10px 16px; font-size: 12px; color: #64748B; border-bottom: 1px solid #F1F5F9; }
.sidebar-actions { padding: 10px 16px; border-bottom: 1px solid #F1F5F9; }
.btn-ai-grade {
  width: 100%; padding: 10px 16px; border: 2px solid #818CF8; border-radius: 8px;
  background: linear-gradient(135deg, #EEF2FF 0%, #E0E7FF 100%);
  color: #4F46E5; font-size: 14px; font-weight: 600; cursor: pointer; font-family: inherit;
  transition: all .2s;
}
.btn-ai-grade:hover:not(:disabled) { background: linear-gradient(135deg, #E0E7FF 0%, #C7D2FE 100%); border-color: #6366F1; }
.btn-ai-grade:disabled { opacity: 0.6; cursor: not-allowed; }

.btn-retract-all {
  width: 100%; padding: 10px 16px; border: 2px solid #FCA5A5; border-radius: 8px;
  background: linear-gradient(135deg, #FEF2F2 0%, #FEE2E2 100%);
  color: #DC2626; font-size: 14px; font-weight: 600; cursor: pointer; font-family: inherit;
  transition: all .2s; margin-top: 8px;
}
.btn-retract-all:hover:not(:disabled) { background: linear-gradient(135deg, #FEE2E2 0%, #FECACA 100%); border-color: #EF4444; }
.btn-retract-all:disabled { opacity: 0.6; cursor: not-allowed; }

.btn-retract {
  padding: 8px 16px; border: 1px solid #FCA5A5; border-radius: 8px; background: #FEF2F2;
  color: #DC2626; font-size: 13px; font-weight: 500; cursor: pointer; font-family: inherit; margin-left: 8px;
}
.btn-retract:hover:not(:disabled) { background: #FEE2E2; border-color: #EF4444; }
.btn-retract:disabled { opacity: 0.6; cursor: not-allowed; }

.btn-retract-sm {
  border: none; background: none; color: #DC2626; cursor: pointer; font-size: 14px;
  padding: 2px 6px; border-radius: 4px; margin-left: 6px; opacity: 0;
  transition: opacity .15s, background .15s;
}
.student-item:hover .btn-retract-sm { opacity: 1; }
.btn-retract-sm:hover { background: #FEE2E2; }

.student-list { max-height: 500px; overflow-y: auto; }
.student-item {
  display: flex; align-items: center; justify-content: space-between;
  padding: 12px 16px; border-bottom: 1px solid #F8FAFC; cursor: pointer; transition: background .15s;
}
.student-item:hover { background: #F8FAFC; }
.student-item.active { background: #EEF2FF; border-left: 3px solid #4F46E5; }
.si-name { font-size: 14px; font-weight: 600; color: #1E293B; display: block; }
.si-username { font-size: 12px; color: #94A3B8; }
.si-score { font-size: 14px; font-weight: 700; color: #10B981; }
.si-badge { font-size: 11px; background: #FEF3C7; color: #D97706; padding: 2px 8px; border-radius: 8px; }
.sidebar-empty { padding: 32px 16px; text-align: center; color: #94A3B8; font-size: 13px; }

/* Main */
.grading-main { background: #fff; border: 1px solid #E2E8F0; border-radius: 12px; padding: 20px; }
.grading-main-empty { display: flex; align-items: center; justify-content: center; color: #94A3B8; }
.grading-main-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; padding-bottom: 12px; border-bottom: 1px solid #F1F5F9; }
.gmh-name { font-size: 16px; font-weight: 700; color: #0F172A; display: block; }
.gmh-time { font-size: 12px; color: #94A3B8; }

.btn-save {
  padding: 8px 24px; border: none; background: #4F46E5; color: #fff; border-radius: 8px;
  font-size: 14px; font-weight: 600; cursor: pointer; font-family: inherit;
}
.btn-save:hover { background: #4338CA; }
.btn-save:disabled { opacity: 0.5; cursor: not-allowed; }

/* Answer Cards */
.answer-card { border: 1px solid #E2E8F0; border-radius: 8px; padding: 16px; margin-bottom: 12px; }
.ac-header { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; }
.ac-num { font-size: 13px; font-weight: 700; color: #4F46E5; }
.ac-type { font-size: 12px; background: #F1F5F9; padding: 2px 8px; border-radius: 4px; color: #64748B; }
.ac-correct { font-size: 12px; color: #10B981; font-weight: 500; }
.ac-answer { margin-bottom: 10px; }
.ac-answer label { font-size: 12px; font-weight: 600; color: #64748B; display: block; margin-bottom: 4px; }
.ac-answer-text { background: #F8FAFC; padding: 10px; border-radius: 6px; font-size: 14px; color: #1E293B; min-height: 36px; }
.ac-score { display: flex; align-items: center; gap: 8px; }
.ac-score label { font-size: 13px; font-weight: 600; color: #475569; }
.ac-max { font-size: 13px; color: #64748B; }
.ac-auto-badge { font-size: 11px; background: #D1FAE5; color: #059669; padding: 2px 8px; border-radius: 6px; }

.grading-total { margin-top: 16px; padding-top: 12px; border-top: 2px solid #E2E8F0; text-align: right; font-size: 16px; font-weight: 700; color: #0F172A; }
</style>
