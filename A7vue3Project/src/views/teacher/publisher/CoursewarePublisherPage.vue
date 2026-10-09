<template>
  <div class="publisher-container">
    <!-- In-content header -->
    <div class="publisher-topbar">
      <div class="topbar-left">
        <button class="btn-back" @click="handleBack">← 返回工作台</button>
        <h1 class="topbar-title">添加课件</h1>
      </div>
      <div class="topbar-actions">
        <button class="btn-clear" @click="handleClear" title="清空所有内容，重新开始">清空</button>
        <button class="btn-save" @click="handleSave">保存</button>
      </div>
    </div>

    <div class="publisher-body">
      <div class="publisher-card" style="max-width:720px">
        <div class="form-field">
          <label>所属课程 <span style="color:red">*</span></label>
          <a-select v-model:value="form.courseName" placeholder="选择课程" style="width:100%">
            <a-select-option v-for="c in courses" :key="c.course_name" :value="c.course_name">{{ c.course_name }}</a-select-option>
          </a-select>
        </div>

        <div class="form-field">
          <label>课件名称 <span style="color:red">*</span></label>
          <a-input v-model:value="form.title" placeholder="例如：第一章 数据结构导论" />
        </div>

        <div class="form-field">
          <label>课件描述</label>
          <a-textarea v-model:value="form.description" placeholder="简要描述课件内容..." :rows="3" />
        </div>

        <div class="form-field">
          <label>上传课件文件 <span style="color:red">*</span></label>
          <input type="file" @change="handleFileSelect" ref="fileInputRef" style="display:none"
            accept=".pdf,.docx,.doc,.pptx,.ppt,.txt,.md,.jpg,.jpeg,.png,.gif,.mp4,.avi,.mov" />
          <div class="upload-zone" @click="focusFileInput" @dragover.prevent @drop.prevent="handleDrop">
            <template v-if="!form.file">
              <svg viewBox="0 0 24 24" fill="none" width="28" height="28"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4M17 8l-5-5-5 5M12 3v12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              <span>点击或拖拽文件到此处上传</span>
              <span class="upload-hint">支持 PDF、Word、PPT、图片、视频等格式</span>
            </template>
            <template v-else>
              <svg viewBox="0 0 24 24" fill="none" width="28" height="28"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8zM14 2v6h6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              <span class="file-name">{{ form.file.name }}</span>
              <span class="file-size">{{ formatFileSize(form.file.size) }}</span>
              <button class="btn-sm" style="margin-top:8px" @click.stop="form.file = null">移除</button>
            </template>
          </div>
        </div>

        <!-- 课件预览（图片/视频） -->
        <div v-if="previewUrl" class="form-field">
          <label>预览</label>
          <div class="preview-box">
            <img v-if="previewType === 'image'" :src="previewUrl" alt="预览" class="preview-image" />
            <video v-else-if="previewType === 'video'" :src="previewUrl" controls class="preview-video"></video>
            <div v-else class="preview-generic">
              <svg viewBox="0 0 24 24" fill="none" width="40" height="40"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8zM14 2v6h6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              <span>{{ form.file?.name || '文件' }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'

interface CoursewareItem {
  id: string
  courseName: string
  title: string
  description: string
  fileName: string
  fileSize: number
  fileType: string
  fileDataUrl: string
  createdAt: number
}

const COURSEWARE_STORAGE = 'teacher_courseware_list'
const MAX_FILE_SIZE = 50 * 1024 * 1024 // 50MB

const router = useRouter()

const courses = ref<Array<{ course_name: string }>>([])
const fileInputRef = ref<HTMLInputElement | null>(null)

const form = reactive({
  courseName: '',
  title: '',
  description: '',
  file: null as File | null,
})

const previewUrl = ref('')
const previewType = ref<'image' | 'video' | 'generic'>('generic')

function formatFileSize(bytes: number): string {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

function handleFileSelect(e: Event) {
  const target = e.target as HTMLInputElement
  const file = target.files?.[0]
  if (file) {
    if (file.size > MAX_FILE_SIZE) {
      message.warning('文件大小不能超过 50MB')
      return
    }
    form.file = file
    generatePreview(file)
  }
}

function handleDrop(e: DragEvent) {
  const file = e.dataTransfer?.files?.[0]
  if (file) {
    if (file.size > MAX_FILE_SIZE) {
      message.warning('文件大小不能超过 50MB')
      return
    }
    form.file = file
    generatePreview(file)
  }
}

function focusFileInput() {
  if (form.file) return
  fileInputRef.value?.click()
}

function generatePreview(file: File) {
  URL.revokeObjectURL(previewUrl.value)
  const ext = file.name.split('.').pop()?.toLowerCase() || ''
  const imageExts = ['jpg', 'jpeg', 'png', 'gif', 'webp', 'svg', 'bmp']
  const videoExts = ['mp4', 'avi', 'mov', 'webm', 'mkv']

  if (imageExts.includes(ext)) {
    previewType.value = 'image'
    previewUrl.value = URL.createObjectURL(file)
  } else if (videoExts.includes(ext)) {
    previewType.value = 'video'
    previewUrl.value = URL.createObjectURL(file)
  } else {
    previewType.value = 'generic'
    previewUrl.value = ''
  }
}

function loadCoursewareList(): CoursewareItem[] {
  try { return JSON.parse(localStorage.getItem(COURSEWARE_STORAGE) || '[]') } catch { return [] }
}

function saveCoursewareList(list: CoursewareItem[]) {
  localStorage.setItem(COURSEWARE_STORAGE, JSON.stringify(list))
}

async function buildItem(): Promise<CoursewareItem> {
  const file = form.file!
  return new Promise((resolve) => {
    const reader = new FileReader()
    reader.onload = () => {
      resolve({
        id: 'cw_' + Date.now(),
        courseName: form.courseName,
        title: form.title,
        description: form.description,
        fileName: file.name,
        fileSize: file.size,
        fileType: file.type || file.name.split('.').pop() || '',
        fileDataUrl: reader.result as string,
        createdAt: Date.now(),
      })
    }
    reader.readAsDataURL(file)
  })
}

async function handleSave() {
  if (!form.courseName) { message.warning('请选择课程'); return }
  if (!form.title.trim()) { message.warning('请输入课件名称'); return }
  if (!form.file) { message.warning('请上传课件文件'); return }

  const item = await buildItem()
  const list = loadCoursewareList()
  list.unshift(item)
  saveCoursewareList(list)
  finalizeLeave()
  message.success('课件已保存')
  router.push('/teacher')
}

const CW_DRAFT_KEY = 'courseware_publisher_draft'
const CW_ACTIVE_KEY = 'courseware_publisher_active'
const isExplicitLeave = ref(false)

function saveDraft() {
  try {
    const draft = {
      courseName: form.courseName,
      title: form.title,
      description: form.description,
      // File objects can't be serialized — user will need to re-upload
    }
    sessionStorage.setItem(CW_DRAFT_KEY, JSON.stringify(draft))
  } catch { /* ignore */ }
}

function clearDraft() {
  sessionStorage.removeItem(CW_DRAFT_KEY)
}

/** 显式离开时清除草稿和活跃标记（保存/发布用） */
function finalizeLeave() {
  isExplicitLeave.value = true
  sessionStorage.removeItem(CW_DRAFT_KEY)
  sessionStorage.removeItem(CW_ACTIVE_KEY)
}

/** 返回工作台：先保存草稿再清除活跃标记，下次进入可恢复 */
function softLeave() {
  saveDraft()
  isExplicitLeave.value = true
  sessionStorage.removeItem(CW_ACTIVE_KEY)
}

/** 清空所有表单内容 */
function handleClear() {
  form.courseName = ''
  form.title = ''
  form.description = ''
  form.file = null
  URL.revokeObjectURL(previewUrl.value)
  previewUrl.value = ''
  previewType.value = 'generic'
  clearDraft()
  sessionStorage.removeItem(CW_ACTIVE_KEY)
  message.success('已清空所有内容')
}

function restoreDraft(): boolean {
  try {
    const raw = sessionStorage.getItem(CW_DRAFT_KEY)
    if (!raw) return false
    const draft = JSON.parse(raw)
    if (!draft.courseName && !draft.title) return false
    form.courseName = draft.courseName || ''
    form.title = draft.title || ''
    form.description = draft.description || ''
    return true
  } catch { return false }
}

function handleBack() {
  softLeave()
  router.push('/teacher')
}

onMounted(async () => {
  // Load courses
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
    if (teacherId) {
      const { getSelfTeacherDetailApi: api } = await import('../../../api/teacher/getSelfTeacherDetailApi')
      const res = await api(teacherId)
      if (res?.data?.code === 200 && res.data.data) {
        const info = res.data.data
        const courseList = info.courseList || info.teacher_courseList
        if (courseList && Array.isArray(courseList)) {
          courses.value = courseList
        }
      }
    }
  } catch { /* ignore */ }

  // Prefill course from sessionStorage (passed from course card)
  const preset = sessionStorage.getItem('courseware_preset_course')
  let hasPreset = false
  if (preset) {
    try {
      const data = JSON.parse(preset)
      form.courseName = data.courseName || ''
      sessionStorage.removeItem('courseware_preset_course')
      hasPreset = true
    } catch { /* ignore */ }
  }

  // Restore draft if no preset
  if (!hasPreset) {
    if (restoreDraft()) {
      message.info('已恢复上次编辑内容（请重新上传课件文件）')
    }
  }

  // 标记当前处于发布里界面
  sessionStorage.setItem(CW_ACTIVE_KEY, '1')
})

onBeforeUnmount(() => {
  if (isExplicitLeave.value) return
  if (form.courseName || form.title) {
    saveDraft()
  }
})
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
  padding: 8px 24px; border: none; border-radius: 8px; background: #4F46E5; color: #fff;
  font-size: 14px; font-weight: 600; cursor: pointer; font-family: inherit;
}
.btn-save:hover { background: #4338CA; }
.btn-clear {
  padding: 8px 16px; border: 1px solid #FECACA; border-radius: 8px; background: #FEF2F2;
  color: #DC2626; font-size: 13px; font-weight: 500; cursor: pointer; font-family: inherit;
}
.btn-clear:hover { background: #FEE2E2; border-color: #FCA5A5; }

.publisher-body { display: flex; justify-content: center; }
.publisher-card { background: #fff; border-radius: 12px; border: 1px solid #E2E8F0; padding: 28px; width: 100%; }

.form-field { display: flex; flex-direction: column; gap: 6px; margin-bottom: 20px; }
.form-field label { font-size: 13px; font-weight: 600; color: #334155; }

.upload-zone {
  border: 2px dashed #CBD5E1; border-radius: 10px; padding: 32px;
  display: flex; flex-direction: column; align-items: center; gap: 8px;
  color: #64748B; cursor: pointer; transition: border-color .2s, background .2s;
  text-align: center;
}
.upload-zone:hover { border-color: #4F46E5; background: #EEF2FF; }
.upload-hint { font-size: 12px; color: #94A3B8; }
.file-name { font-weight: 600; color: #0F172A; }
.file-size { font-size: 12px; color: #94A3B8; }
.btn-sm {
  padding: 4px 12px; border: 1px solid #E2E8F0; border-radius: 6px; background: #fff;
  color: #475569; font-size: 12px; cursor: pointer; font-family: inherit;
}
.btn-sm:hover { background: #F8FAFC; }

.preview-box {
  border: 1px solid #E2E8F0; border-radius: 8px; overflow: hidden;
}
.preview-image { width: 100%; max-height: 360px; object-fit: contain; display: block; }
.preview-video { width: 100%; max-height: 360px; display: block; }
.preview-generic {
  display: flex; flex-direction: column; align-items: center; gap: 8px;
  padding: 32px; color: #64748B;
}
</style>
