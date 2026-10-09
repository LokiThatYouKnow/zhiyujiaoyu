<template>
  <div class="publisher-container">
    <!-- In-content header -->
    <div class="publisher-topbar">
      <div class="topbar-left">
        <button class="btn-back" @click="handleBack">← 返回工作台</button>
        <h1 class="topbar-title">发布考试</h1>
      </div>
      <div class="topbar-actions">
        <button class="btn-template" @click="showTemplateModal = true" title="保存当前题目为可复用模板">另存为模板</button>
        <button class="btn-clear" @click="handleClear" title="清空所有内容，重新开始">清空</button>
        <button class="btn-save" @click="handleSave">保存</button>
        <button class="btn-publish" :disabled="!canPublish" @click="handlePublish">保存并发布</button>
      </div>
    </div>

    <div class="publisher-body">
      <!-- 左栏：基础信息 + 分值分配 -->
      <div class="publisher-left">
        <div class="publisher-card">
          <PublisherMetaForm
            :course-name="form.courseName"
            :title="form.title"
            :description="form.description"
            :courses="courses"
            @update:course-name="form.courseName = $event"
            @update:title="form.title = $event"
            @update:description="form.description = $event"
          >
            <template #timing>
              <div class="timing-grid">
                <div class="form-field">
                  <label>开始时间 <span style="color:red">*</span></label>
                  <a-date-picker
                    v-model:value="form.startTime"
                    show-time
                    placeholder="选择开始时间"
                    format="YYYY-MM-DD HH:mm"
                    value-format="YYYY-MM-DD HH:mm"
                    style="width:100%"
                  />
                </div>
                <div class="form-field">
                  <label>结束时间 <span style="color:red">*</span></label>
                  <a-date-picker
                    v-model:value="form.endTime"
                    show-time
                    placeholder="选择结束时间"
                    format="YYYY-MM-DD HH:mm"
                    value-format="YYYY-MM-DD HH:mm"
                    style="width:100%"
                  />
                </div>
                <div class="form-field">
                  <label>考试时长（分钟）</label>
                  <a-input-number v-model:value="form.duration" :min="10" :max="300" style="width:100%" />
                </div>
              </div>
            </template>
          </PublisherMetaForm>
        </div>

        <div class="publisher-card">
          <ScoreAllocationPanel
            :questions="manager.questions.value"
            :total-score="scoreAlloc.totalScore"
            :type-scores="scoreAlloc.typeScores"
            @update:total-score="handleTotalScoreChange"
            @update:type-scores="handleTypeScoresChange"
            @auto-distribute="handleAutoDistribute"
          />
        </div>
      </div>

      <!-- 右栏：题目列表 -->
      <div class="publisher-right">
        <div class="publisher-card">
          <QuestionList
            :questions="manager.questions.value"
            @add-question="handleAddQuestion"
            @edit-question="handleEditQuestion"
            @delete-question="handleDeleteQuestion"
            @move-question="handleMoveQuestion"
          />
        </div>
      </div>
    </div>

    <!-- Editor Modal -->
    <QuestionEditorShell
      :question="editingQuestion"
      :visible="editorVisible"
      :is-new="isNewQuestion"
      @update:question="handleQuestionUpdate"
      @close="editorVisible = false"
    />

    <!-- Save as Template Modal -->
    <div v-if="showTemplateModal" class="modal-overlay" @click.self="showTemplateModal = false">
      <div class="modal-card">
        <h3 class="modal-title">另存为模板</h3>
        <div class="form-field">
          <label>模板名称 <span style="color:red">*</span></label>
          <input v-model="templateForm.name" placeholder="例如：数据结构期中考试模板" class="form-input" />
        </div>
        <div class="form-field">
          <label>模板描述</label>
          <textarea v-model="templateForm.description" placeholder="简要描述模板内容和适用场景" rows="3" class="form-textarea"></textarea>
        </div>
        <div class="modal-actions">
          <button class="btn-cancel" @click="showTemplateModal = false">取消</button>
          <button class="btn-confirm" @click="handleSaveAsTemplate" :disabled="!templateForm.name.trim()">保存模板</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { useQuestionManager } from '../../../composables/useQuestionManager'
import { loadExamList, saveExamList } from '../../../composables/usePublisherPersistence'
import { publishHomeworkApi } from '../../../api/submissionApi'
import type { Question, QuestionType, ExamItem, PendingImportData, PendingEditData } from '../../../types/question'
import PublisherMetaForm from './components/PublisherMetaForm.vue'
import QuestionList from './components/QuestionList.vue'
import QuestionEditorShell from './components/QuestionEditorShell.vue'
import ScoreAllocationPanel from './components/ScoreAllocationPanel.vue'

const router = useRouter()

// ===== 分值分配状态 =====
const TOTAL_SCORE_OPTIONS: QuestionType[] = ['choice', 'fill-blank', 'true-false', 'essay', 'calculation']
const DEFAULT_TYPE_SCORES: Record<QuestionType, number> = { choice: 30, 'fill-blank': 20, 'true-false': 10, essay: 20, calculation: 20 }

const scoreAlloc = reactive({
  totalScore: 100,
  typeScores: { ...DEFAULT_TYPE_SCORES } as Record<QuestionType, number>,
})

function recalcTypeBudgets() {
  const typesWithQuestions = TOTAL_SCORE_OPTIONS.filter(t => manager.getTypeCount(t) > 0)
  if (typesWithQuestions.length === 0) return
  const perType = Math.floor(scoreAlloc.totalScore / typesWithQuestions.length)
  const remainder = scoreAlloc.totalScore - perType * typesWithQuestions.length
  typesWithQuestions.forEach((t, i) => {
    scoreAlloc.typeScores[t] = perType + (i < remainder ? 1 : 0)
  })
}

function redistributeType(type: QuestionType) {
  const typeQuestions = manager.getTypeQuestions(type)
  if (typeQuestions.length === 0) return
  const manualQs = typeQuestions.filter(q => q.scoreManual)
  const autoQs = typeQuestions.filter(q => !q.scoreManual)
  const manualTotal = manualQs.reduce((s, q) => s + q.score, 0)
  const available = Math.max(0, scoreAlloc.typeScores[type] - manualTotal)
  if (autoQs.length === 0) return
  const base = Math.floor(available / autoQs.length)
  const remainder = available - base * autoQs.length
  autoQs.forEach((q, i) => {
    q.score = base + (i < remainder ? 1 : 0)
  })
}

function redistributeAll() {
  recalcTypeBudgets()
  TOTAL_SCORE_OPTIONS.forEach(t => redistributeType(t))
}

function handleTotalScoreChange(v: number) {
  scoreAlloc.totalScore = v
  recalcTypeBudgets()
  TOTAL_SCORE_OPTIONS.forEach(t => redistributeType(t))
}

function handleTypeScoresChange(v: Record<QuestionType, number>) {
  scoreAlloc.typeScores = { ...v }
  scoreAlloc.totalScore = TOTAL_SCORE_OPTIONS.reduce((s, t) => s + (v[t] || 0), 0)
  TOTAL_SCORE_OPTIONS.forEach(t => redistributeType(t))
}

function handleAutoDistribute() {
  manager.questions.value.forEach(q => { q.scoreManual = false })
  redistributeAll()
}

const courses = ref<Array<{ course_name: string }>>([])
const manager = useQuestionManager()

const form = reactive({
  courseName: '',
  title: '',
  description: '',
  startTime: '',
  endTime: '',
  duration: 60,
})

let editingItemId: string | null = null

const editingQuestion = ref<Question | null>(null)
const editorVisible = ref(false)
const isNewQuestion = ref(false)

// Template save modal
const showTemplateModal = ref(false)
const templateForm = reactive({ name: '', description: '' })

const canSave = computed(() => !!form.courseName && !!form.title)
const canPublish = computed(() => canSave.value && manager.questions.value.length > 0)

const EXAM_DRAFT_KEY = 'exam_publisher_draft'
const EXAM_ACTIVE_KEY = 'exam_publisher_active'
let hasExplicitAction = false
const isExplicitLeave = ref(false)

function saveDraft() {
  try {
    const draft = {
      courseName: form.courseName,
      title: form.title,
      description: form.description,
      startTime: form.startTime,
      endTime: form.endTime,
      duration: form.duration,
      questions: manager.questions.value.map(q => ({ ...q })),
      scoreAlloc: { totalScore: scoreAlloc.totalScore, typeScores: { ...scoreAlloc.typeScores } },
      editingItemId: editingItemId,
    }
    sessionStorage.setItem(EXAM_DRAFT_KEY, JSON.stringify(draft))
  } catch { /* ignore */ }
}

function clearDraft() {
  sessionStorage.removeItem(EXAM_DRAFT_KEY)
}

/** 显式离开时清除草稿和活跃标记（保存/发布用） */
function finalizeLeave() {
  isExplicitLeave.value = true
  sessionStorage.removeItem(EXAM_DRAFT_KEY)
  sessionStorage.removeItem(EXAM_ACTIVE_KEY)
}

/** 返回工作台：仅新建模式保存草稿，编辑模式不保存（避免污染新考试草稿） */
function softLeave() {
  if (!editingItemId) saveDraft()
  else clearDraft()
  isExplicitLeave.value = true
  sessionStorage.removeItem(EXAM_ACTIVE_KEY)
}

/** 清空所有表单内容 */
function handleClear() {
  form.courseName = ''
  form.title = ''
  form.description = ''
  form.startTime = ''
  form.endTime = ''
  form.duration = 60
  manager.clearQuestions()
  scoreAlloc.totalScore = 100
  scoreAlloc.typeScores = { choice: 30, 'fill-blank': 20, 'true-false': 10, essay: 20, calculation: 20 }
  editingItemId = null
  clearDraft()
  sessionStorage.removeItem(EXAM_ACTIVE_KEY)
  message.success('已清空所有内容')
}

const TEMPLATE_STORAGE_KEY = 'teacher_templates'

function handleSaveAsTemplate() {
  const name = templateForm.name.trim()
  if (!name) { message.warning('请输入模板名称'); return }
  if (manager.questions.value.length === 0) { message.warning('请至少添加一道题目'); return }

  try {
    const existing = JSON.parse(localStorage.getItem(TEMPLATE_STORAGE_KEY) || '[]')
    const tpl = {
      id: 'tpl_' + Date.now(),
      name,
      type: 'exam' as const,
      description: templateForm.description.trim(),
      courseName: form.courseName,
      questionCount: manager.questions.value.length,
      questions: manager.questions.value.map(q => ({ ...q })),
      createdAt: Date.now(),
      updatedAt: Date.now(),
    }
    existing.unshift(tpl)
    localStorage.setItem(TEMPLATE_STORAGE_KEY, JSON.stringify(existing))
    showTemplateModal.value = false
    templateForm.name = ''
    templateForm.description = ''
    message.success(`模板"${name}"已保存，可在工作台「模板复用」中查看和使用`)
  } catch { message.error('保存模板失败') }
}

function restoreDraft(): boolean {
  try {
    const raw = sessionStorage.getItem(EXAM_DRAFT_KEY)
    if (!raw) return false
    const draft = JSON.parse(raw)
    if (!draft.courseName && !draft.title && (!draft.questions || draft.questions.length === 0)) return false
    form.courseName = draft.courseName || ''
    form.title = draft.title || ''
    form.description = draft.description || ''
    form.startTime = draft.startTime || ''
    form.endTime = draft.endTime || ''
    form.duration = draft.duration || 60
    if (draft.questions?.length > 0) {
      manager.importQuestions(draft.questions)
    }
    if (draft.scoreAlloc) {
      scoreAlloc.totalScore = draft.scoreAlloc.totalScore || 100
      if (draft.scoreAlloc.typeScores) {
        Object.assign(scoreAlloc.typeScores, draft.scoreAlloc.typeScores)
      }
    }
    // 草稿不恢复编辑 ID，始终作为新考试处理（避免覆盖已有考试）
    redistributeAll()
    return true
  } catch { return false }
}

onMounted(() => {
  loadCourses()

  const importRaw = sessionStorage.getItem('pending_import')
  if (importRaw) {
    try {
      const data: PendingImportData = JSON.parse(importRaw)
      if (data.target === 'exam') {
        form.courseName = data.courseName
        form.title = data.title
        form.description = ''
        form.startTime = data.startTime || ''
        form.endTime = data.endTime || ''
        manager.importQuestions(data.parsedQuestions)
        sessionStorage.removeItem('pending_import')
        hasExplicitAction = true
        clearDraft()
        message.success(`已从 AI 导入 ${data.parsedQuestions.length} 道题目`)
        return
      }
    } catch { /* ignore */ }
  }

  const editRaw = sessionStorage.getItem('pending_edit')
  if (editRaw) {
    try {
      const data: PendingEditData = JSON.parse(editRaw)
      if (data.target === 'exam') {
        const item = data.item as ExamItem
        form.courseName = item.courseName
        form.title = item.title
        form.description = item.description || ''
        form.startTime = item.startTime || ''
        form.endTime = item.endTime || ''
        form.duration = item.duration || 60
        editingItemId = data.itemId
        if (item.questions && item.questions.length > 0) {
          manager.importQuestions(item.questions)
        }
        sessionStorage.removeItem('pending_edit')
        hasExplicitAction = true
        // 编辑模式不清理草稿：用户的新考试草稿应保留，编辑和新建是两个独立流程
        redistributeAll()
        return
      }
    } catch { /* ignore */ }
  }
  // 从模板加载
  const tplRaw = sessionStorage.getItem('apply_template')
  if (tplRaw) {
    try {
      const tpl = JSON.parse(tplRaw)
      if (tpl.type === 'exam' && tpl.questions?.length > 0) {
        form.courseName = tpl.courseName || ''
        form.title = tpl.name || ''
        form.description = tpl.description || ''
        manager.importQuestions(tpl.questions)
        sessionStorage.removeItem('apply_template')
        hasExplicitAction = true
        clearDraft()
        redistributeAll()
        message.success(`已从模板"${tpl.name}"加载 ${tpl.questions.length} 道题目`)
      }
    } catch { /* ignore */ }
  }

  if (!hasExplicitAction) {
    if (restoreDraft()) {
      message.info('已恢复上次编辑内容')
    }
  }

  if (manager.questions.value.length > 0) {
    redistributeAll()
  }

  // 标记当前处于发布里界面
  sessionStorage.setItem(EXAM_ACTIVE_KEY, '1')
})

onBeforeUnmount(() => {
  if (isExplicitLeave.value) return
  if (editingItemId) return // 编辑模式不保存草稿
  if (form.courseName || form.title || manager.questions.value.length > 0) {
    saveDraft()
  }
})

async function loadCourses() {
  try {
    const token = (sessionStorage.getItem('token') || localStorage.getItem('token'))
    let teacherId = ''
    if (token) {
      try {
        const { jwtDecode } = await import('jwt-decode')
        const decoded: any = jwtDecode(token)
        teacherId = decoded?.sub || decoded?.username || ''
      } catch { /* ignore */ }
    }
    if (!teacherId) {
      teacherId = localStorage.getItem('teacher_id') || ''
    }
    if (!teacherId) return

    const { getSelfTeacherDetailApi: api } = await import('../../../api/teacher/getSelfTeacherDetailApi')
    const res = await api(teacherId)
    if (res?.data?.code === 200 && res.data.data) {
      const info = res.data.data
      const courseList = info.courseList || info.teacher_courseList
      if (courseList && Array.isArray(courseList)) {
        courses.value = courseList
      }
    }
  } catch { /* ignore */ }
}

function handleAddQuestion(type: QuestionType) {
  const q = manager.addQuestion(type)
  recalcTypeBudgets()
  TOTAL_SCORE_OPTIONS.forEach(t => redistributeType(t))
  editingQuestion.value = q
  isNewQuestion.value = true
  editorVisible.value = true
}

function handleEditQuestion(index: number) {
  const q = manager.questions.value[index]
  if (q) {
    editingQuestion.value = JSON.parse(JSON.stringify(q))
    isNewQuestion.value = false
    editorVisible.value = true
  }
}

function handleDeleteQuestion(index: number) {
  const q = manager.questions.value[index]
  if (q) {
    manager.removeQuestion(q.id)
    recalcTypeBudgets()
    TOTAL_SCORE_OPTIONS.forEach(t => redistributeType(t))
    message.success('已删除题目')
  }
}

function handleMoveQuestion(index: number, direction: 'up' | 'down') {
  const q = manager.questions.value[index]
  if (q) {
    manager.moveQuestion(q.id, direction)
  }
}

function handleQuestionUpdate(q: Question) {
  const old = manager.getQuestion(q.id)
  if (old && old.score !== q.score) {
    q.scoreManual = true
  }
  manager.replaceQuestion(q)
  if (q.scoreManual) {
    const typeQs = manager.getTypeQuestions(q.type)
    const typeActualSum = typeQs.reduce((s, tq) => s + tq.score, 0)
    if (typeActualSum > (scoreAlloc.typeScores[q.type] || 0)) {
      scoreAlloc.typeScores[q.type] = typeActualSum
    }
    redistributeType(q.type)
    scoreAlloc.totalScore = manager.questions.value.reduce((s, tq) => s + tq.score, 0)
  }
}

function handleBack() {
  softLeave()
  router.push('/teacher?tab=exam')
}

/** 构建 ExamItem 对象 */
function buildItem(status: 'draft' | 'published'): ExamItem {
  const list = loadExamList()
  const id = editingItemId || ('ex_' + Date.now())
  const now = Date.now()
  return {
    id,
    title: form.title,
    description: form.description,
    courseName: form.courseName,
    questions: manager.questions.value,
    startTime: form.startTime,
    endTime: form.endTime,
    duration: form.duration,
    status,
    createdAt: editingItemId ? (list.find(e => e.id === id)?.createdAt || now) : now,
    updatedAt: now,
  }
}

/** 保存草稿（不验证题目完整性） */
function handleSave() {
  if (!form.courseName || !form.title) {
    message.warning('请填写课程和标题')
    return
  }

  const item = buildItem('draft')
  const list = loadExamList()

  if (editingItemId) {
    const idx = list.findIndex(e => e.id === editingItemId)
    if (idx !== -1) list[idx] = item
  } else {
    list.unshift(item)
  }

  saveExamList(list)
  finalizeLeave()
  message.success('已保存草稿')
  router.push('/teacher?tab=exam')
}

/** 保存并发布（验证题目完整性 + 推送给学生） */
async function handlePublish() {
  if (!form.courseName || !form.title) {
    message.warning('请填写课程和标题')
    return
  }
  const { valid, errors } = manager.validateAll()
  if (!valid) {
    message.warning(errors[0])
    return
  }
  if (manager.questions.value.length === 0) {
    message.warning('请至少添加一道题目')
    return
  }

  const item = buildItem('published')
  const list = loadExamList()

  if (editingItemId) {
    const idx = list.findIndex(e => e.id === editingItemId)
    if (idx !== -1) list[idx] = item
  } else {
    list.unshift(item)
  }

  saveExamList(list)

  // 推送给学生（发布即推送）
  let teacherUsername = ''
  const token = sessionStorage.getItem('token') || localStorage.getItem('token')
  if (token) {
    try {
      const { jwtDecode } = await import('jwt-decode')
      const decoded: any = jwtDecode(token)
      teacherUsername = decoded?.sub || decoded?.username || ''
    } catch { /* ignore */ }
  }
  if (!teacherUsername) {
    teacherUsername = localStorage.getItem('teacher_id') || ''
  }
  if (teacherUsername) {
    try {
      await publishHomeworkApi({
        homework_id: item.id,
        type: 'exam',
        teacher_username: teacherUsername,
        course_name: item.courseName,
        title: item.title,
        description: item.description || '',
        questions_json: JSON.stringify(item.questions || []),
        end_time: item.endTime || '',
        duration: String(item.duration || 60),
      })
    } catch { /* non-blocking: local save succeeded even if push fails */ }
  }

  finalizeLeave()
  message.success('已保存并发布，已推送给学生')
  router.push('/teacher?tab=exam')
}
</script>

<style scoped>
.publisher-container {
  padding: 24px 32px;
  width: 100%;
}
.publisher-topbar {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 24px;
}
.topbar-left { display: flex; align-items: center; gap: 16px; }
.topbar-title { font-size: 18px; font-weight: 700; color: #0F172A; margin: 0; }
.btn-back {
  padding: 8px 16px; border: 1px solid #E2E8F0; border-radius: 8px; background: #fff;
  color: #475569; font-size: 13px; font-weight: 500; cursor: pointer; font-family: inherit;
}
.btn-back:hover { background: #F8FAFC; border-color: #CBD5E1; }
.topbar-actions { display: flex; gap: 8px; }
.btn-save {
  padding: 8px 20px; border: 1px solid #CBD5E1; border-radius: 8px; background: #fff;
  color: #475569; font-size: 14px; font-weight: 600; cursor: pointer; font-family: inherit;
}
.btn-save:hover { background: #F8FAFC; border-color: #94A3B8; }
.btn-publish {
  padding: 8px 24px; border: none; border-radius: 8px; background: #4F46E5; color: #fff;
  font-size: 14px; font-weight: 600; cursor: pointer; font-family: inherit;
}
.btn-publish:hover { background: #4338CA; }
.btn-publish:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-clear {
  padding: 8px 16px; border: 1px solid #FECACA; border-radius: 8px; background: #FEF2F2;
  color: #DC2626; font-size: 13px; font-weight: 500; cursor: pointer; font-family: inherit;
}
.btn-clear:hover { background: #FEE2E2; border-color: #FCA5A5; }
.btn-template {
  padding: 8px 16px; border: 1px solid #C4B5FD; border-radius: 8px; background: #F5F3FF;
  color: #6D28D9; font-size: 13px; font-weight: 500; cursor: pointer; font-family: inherit;
}
.btn-template:hover { background: #EDE9FE; border-color: #A78BFA; }

.publisher-body { display: grid; grid-template-columns: 5fr 7fr; gap: 24px; align-items: start; }
.publisher-left { display: flex; flex-direction: column; gap: 20px; }
.publisher-right { display: flex; flex-direction: column; gap: 20px; }
.publisher-card { background: #fff; border-radius: 12px; border: 1px solid #E2E8F0; padding: 24px; }

.timing-grid { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 16px; }
.form-field { display: flex; flex-direction: column; gap: 6px; margin-bottom: 16px; }
.form-field label { font-size: 13px; font-weight: 600; color: #334155; }

@media (max-width: 900px) {
  .publisher-body { grid-template-columns: 1fr; }
  .timing-grid { grid-template-columns: 1fr; }
}

/* Template Modal */
.modal-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.45);
  display: flex; align-items: center; justify-content: center; z-index: 1000;
}
.modal-card {
  background: #fff; border-radius: 12px; padding: 24px;
  width: 420px; max-width: 90vw; box-shadow: 0 8px 30px rgba(0,0,0,0.18);
}
.modal-title { margin: 0 0 20px; font-size: 17px; font-weight: 700; color: #0F172A; }
.form-input {
  width: 100%; padding: 8px 12px; border: 1px solid #E2E8F0; border-radius: 8px;
  font-size: 14px; font-family: inherit; box-sizing: border-box;
}
.form-input:focus { outline: none; border-color: #6366F1; box-shadow: 0 0 0 3px rgba(99,102,241,0.1); }
.form-textarea {
  width: 100%; padding: 8px 12px; border: 1px solid #E2E8F0; border-radius: 8px;
  font-size: 14px; font-family: inherit; resize: vertical; box-sizing: border-box;
}
.form-textarea:focus { outline: none; border-color: #6366F1; box-shadow: 0 0 0 3px rgba(99,102,241,0.1); }
.modal-actions { display: flex; justify-content: flex-end; gap: 8px; margin-top: 20px; }
.btn-cancel {
  padding: 8px 20px; border: 1px solid #E2E8F0; border-radius: 8px; background: #fff;
  color: #475569; font-size: 14px; cursor: pointer; font-family: inherit;
}
.btn-cancel:hover { background: #F8FAFC; }
.btn-confirm {
  padding: 8px 20px; border: none; border-radius: 8px; background: #6366F1; color: #fff;
  font-size: 14px; font-weight: 600; cursor: pointer; font-family: inherit;
}
.btn-confirm:hover { background: #4F46E5; }
.btn-confirm:disabled { opacity: 0.5; cursor: not-allowed; }
</style>
