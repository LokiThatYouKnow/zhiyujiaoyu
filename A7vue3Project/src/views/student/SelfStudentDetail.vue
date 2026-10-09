<template>
  <!-- ===== Main View: Profile + Chat ===== -->
  <template v-if="allVisible().student_SelfStudentDetail_visible">
    <div class="personal-center">
      <!-- ===== 左栏：个人信息 ===== -->
      <div class="pc-left">
        <!-- Hero Header -->
        <div class="profile-hero">
          <div class="hero-actions-top">
            <a-button class="btn-back" @click="backToHome">
              <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M19 12H5M12 19l-7-7 7-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              返回首页
            </a-button>
            <a-button class="btn-join" @click="openJoinClassTable" :disabled="!!studentDetail.teacher_binding_code">
              <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6M15 3h6v6M10 14L21 3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              {{ studentDetail.teacher_binding_code ? '已加入班级' : '加入班级' }}
            </a-button>
          </div>
          <div class="hero-profile">
            <div class="hero-avatar-wrap">
              <div class="hero-avatar">
                <img src="../../assets/student.png" alt="头像">
              </div>
              <div class="hero-status"></div>
            </div>
            <div class="hero-text">
              <h1 class="hero-name">{{ studentDetail.student_name }}</h1>
              <span class="hero-account">@{{ studentDetail.student_account }}</span>
              <div class="hero-chips">
                <span class="chip" v-if="studentDetail.student_major">
                  <svg viewBox="0 0 24 24" fill="none" width="14" height="14"><path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2z" fill="currentColor"/></svg>
                  {{ studentDetail.student_major }}
                </span>
                <span class="chip" v-if="studentDetail.student_class_name">
                  <svg viewBox="0 0 24 24" fill="none" width="14" height="14"><path d="M3 21h18M3 10h18M5 6l7-3 7 3M4 10v11M20 10v11M8 14v4M12 14v4M16 14v4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                  {{ studentDetail.student_class_name }}
                </span>
                <span class="chip" v-if="!studentDetail.student_class_name">未加入班级</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Info Cards -->
        <div class="info-cards">
          <div class="info-card">
            <div class="info-card-icon" style="background:linear-gradient(135deg,#0D9488,#14B8A6);color:white;">
              <svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="white" stroke-width="2" stroke-linecap="round"/></svg>
            </div>
            <div class="info-card-body">
              <span class="info-card-label">性别</span>
              <span class="info-card-value">{{ studentDetail.student_gender || '-' }}</span>
            </div>
          </div>
          <div class="info-card">
            <div class="info-card-icon" style="background:linear-gradient(135deg,#6366F1,#818CF8);color:white;">
              <svg viewBox="0 0 24 24" fill="none" width="18" height="18"><circle cx="12" cy="12" r="10" stroke="white" stroke-width="2"/><path d="M12 6v6l4 2" stroke="white" stroke-width="2" stroke-linecap="round"/></svg>
            </div>
            <div class="info-card-body">
              <span class="info-card-label">年龄</span>
              <span class="info-card-value">{{ studentDetail.student_age || '-' }}</span>
            </div>
          </div>
          <div class="info-card">
            <div class="info-card-icon" style="background:linear-gradient(135deg,#F59E0B,#FBBF24);color:white;">
              <svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M4 19.5A2.5 2.5 0 0 0 6.5 22H20V2H6.5A2.5 2.5 0 0 0 4 4.5v15z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
            </div>
            <div class="info-card-body">
              <span class="info-card-label">账号</span>
              <span class="info-card-value">{{ studentDetail.student_account }}</span>
            </div>
          </div>
          <div class="info-card">
            <div class="info-card-icon" style="background:linear-gradient(135deg,#EC4899,#F472B6);color:white;">
              <svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
            </div>
            <div class="info-card-body">
              <span class="info-card-label">专业</span>
              <span class="info-card-value">{{ studentDetail.student_major || '-' }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- ===== 右栏：AI 聊天 ===== -->
      <div class="pc-right">
        <div class="chat-panel">
          <!-- Chat Header -->
          <div class="chat-header">
            <div class="chat-header-left">
              <div class="chat-header-icon">💬</div>
              <div>
                <h3 class="chat-title">AI 学习助手</h3>
                <div class="chat-model-info">
                  <span class="model-status-dot" :class="{ online: currentModel.online, offline: !currentModel.online }"></span>
                  <span class="model-name">{{ currentModel.name }}</span>
                  <span class="model-status-text">{{ currentModel.online ? '在线' : '离线' }}</span>
                </div>
              </div>
            </div>
            <div class="chat-header-right">
              <a-select
                v-model:value="selectedModelId"
                size="small"
                style="width: 180px"
                @change="onModelChange"
              >
                <a-select-option v-for="m in models" :key="m.id" :value="m.id">
                  <div class="model-option">
                    <span class="model-option-dot" :class="{ online: m.online, offline: !m.online }"></span>
                    <span class="model-option-name">{{ m.name }}</span>
                  </div>
                </a-select-option>
              </a-select>
            </div>
          </div>

          <!-- Chat Messages -->
          <div class="chat-messages" ref="chatMessagesRef">
            <div v-if="messages.length === 0" class="chat-empty">
              <div class="chat-empty-icon">🤖</div>
              <h3>开始 AI 对话</h3>
              <p>选择模型后发送消息，与 AI 学习助手进行对话</p>
              <div class="chat-suggestions">
                <button v-for="s in quickSuggestions" :key="s" class="chat-suggestion-btn" @click="sendQuick(s)">{{ s }}</button>
              </div>
            </div>
            <div v-for="(msg, idx) in messages" :key="idx" class="chat-msg" :class="msg.role">
              <div class="chat-msg-avatar">{{ msg.role === 'user' ? '👤' : '🤖' }}</div>
              <div class="chat-msg-bubble">
                <div class="chat-msg-content">{{ msg.content }}</div>
                <div class="chat-msg-time">{{ msg.time }}</div>
              </div>
            </div>
            <div v-if="sending" class="chat-msg assistant">
              <div class="chat-msg-avatar">🤖</div>
              <div class="chat-msg-bubble typing">
                <span class="typing-dot"></span><span class="typing-dot"></span><span class="typing-dot"></span>
              </div>
            </div>
          </div>

          <!-- Chat Input -->
          <div class="chat-input-area">
            <a-textarea
              v-model:value="inputText"
              :rows="2"
              placeholder="输入消息，Enter 发送，Shift+Enter 换行..."
              @pressEnter="handleEnter"
              :disabled="sending || !currentModel.online"
            />
            <button class="chat-send-btn" :disabled="!inputText.trim() || sending || !currentModel.online" @click="sendMessage">
              <svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- ===== Join Class Modal ===== -->
    <a-modal :open="joinClassTableVisible" @ok="joinClass" @cancel="joinClassCancel"
      okText="加入" cancelText="取消" style="top: 40%;" class="custom-course-modal">
      <template #title>
        <div class="modal-header-custom">
          <div class="modal-icon-wrap">
            <svg viewBox="0 0 24 24" fill="none" width="22" height="22"><path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6M15 3h6v6M10 14L21 3" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
          </div>
          <span>加入班级</span>
        </div>
      </template>
      <a-form class="course-form">
        <a-form-item label="班级邀请码（教师绑定码）" name="inviteCode">
          <a-input v-model:value="teacherBindingCodeInput" placeholder="请输入教师提供的绑定码" size="large" />
        </a-form-item>
      </a-form>
    </a-modal>
  </template>

  <!-- ===== Course Detail View ===== -->
  <template v-else-if="allVisible().student_SelfStudentDetail_CourseDetail_visible">
    <div class="course-detail-page">
      <div class="cd-card">
        <div class="cd-header">
          <button class="cd-back" @click="backSelfStudentDetail">
            <svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M19 12H5M12 19l-7-7 7-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
            返回列表
          </button>
          <div class="cd-header-main">
            <div class="cd-icon">📚</div>
            <h2>{{ courseDetail.course_name }}</h2>
          </div>
        </div>
        <div class="cd-body">
          <div class="cd-info-grid">
            <div class="cd-info-item" v-for="item in courseInfoItems" :key="item.label">
              <div class="cd-info-icon" :style="item.iconStyle">
                <svg viewBox="0 0 24 24" fill="none" width="18" height="18" v-html="item.iconPath"></svg>
              </div>
              <div>
                <span class="cd-info-label">{{ item.label }}</span>
                <span class="cd-info-value">{{ item.value }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { getSelfStudentDetailApi } from '../../api/student/getSelfStudentDetailApi'
import { jwtDecode } from 'jwt-decode'
import type { R } from '../../response/R'
import type { student } from '../../modules/student'
import { message } from 'ant-design-vue'
import type { course } from '../../modules/course'
import { allVisible } from '../../usePinia/allVisible'
import { allData } from '../../usePinia/allData'
import { joinClassApi } from '../../api/student/joinClassApi'
import router from '../../routers/appRouters'
import { sendChatApi, getModelsApi } from '../../api/chatApi'
import type { ChatMessage, ModelInfo } from '../../api/chatApi'

const token = ref((sessionStorage.getItem('token') || localStorage.getItem('token')))
const student_id = (() => { try { return token.value ? (jwtDecode(token.value) as any).sub as string : '' } catch { return '' } })()
const studentDetail = ref<student>({
  student_id: 0, student_account: '', student_name: '', student_gender: '',
  student_age: '', student_classList: [], student_major: '', student_class_name: '',
})
const courseDetail = ref<course>({
  course_id: 0, course_name: '', course_teacher_name: '', course_hours: '', course_score: '',
})

const courseInfoItems = computed(() => [
  { label: '课程编号', value: '#' + courseDetail.value.course_id, iconStyle: 'background:linear-gradient(135deg,#0D9488,#14B8A6);box-shadow:0 3px 10px rgba(13,148,136,0.25);', iconPath: '<path d="M22 12h-4l-3 9L9 3l-3 9H2" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>' },
  { label: '授课教师', value: courseDetail.value.course_teacher_name || '-', iconStyle: 'background:linear-gradient(135deg,#6366F1,#818CF8);box-shadow:0 3px 10px rgba(99,102,241,0.25);', iconPath: '<path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="white" stroke-width="2" stroke-linecap="round"/>' },
  { label: '课程学时', value: (courseDetail.value.course_hours || '-') + ' 学时', iconStyle: 'background:linear-gradient(135deg,#F59E0B,#FBBF24);box-shadow:0 3px 10px rgba(245,158,11,0.25);', iconPath: '<circle cx="12" cy="12" r="10" stroke="white" stroke-width="2"/><path d="M12 6v6l4 2" stroke="white" stroke-width="2" stroke-linecap="round"/>' },
  { label: '课程学分', value: (courseDetail.value.course_score || '-') + ' 学分', iconStyle: 'background:linear-gradient(135deg,#EC4899,#F472B6);box-shadow:0 3px 10px rgba(236,72,153,0.25);', iconPath: '<path d="M12 20h9M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>' },
])

const getSelfStudentDetail = async () => {
  getSelfStudentDetailApi(student_id).then(res => {
    const R: R = res.data
    if (R.code === 200) {
      studentDetail.value = R.data
      sessionStorage.setItem('studentDetail', JSON.stringify(studentDetail.value))
    } else { message.error('获取学生信息失败') }
  }).catch(err => { message.error('获取学生信息失败'); console.log(err) })
}

// 仅从 sessionStorage 恢复缓存的个人信息（标签页隔离，不从 localStorage 读取以避免跨用户数据污染）
const cachedDetail = sessionStorage.getItem('studentDetail')
if (cachedDetail) {
  try {
    const parsed = JSON.parse(cachedDetail)
    // 验证缓存数据属于当前登录用户
    if (parsed.student_account === student_id) {
      studentDetail.value = parsed
    }
  } catch {}
}
if (!studentDetail.value.student_account) {
  getSelfStudentDetail()
}

const goToCourseDetail = (course_id: number) => {
  for (const c of studentDetail.value.student_classList as course[]) {
    if (c.course_id === course_id) {
      courseDetail.value = c
      allData().student_SelfStudentDetail_course_data = c
      allVisible().student_SelfStudentDetail_visible = false
      allVisible().student_SelfStudentDetail_CourseDetail_visible = true
      return
    }
  }
  message.error('查看课程详情信息失败')
}

if (allVisible().student_SelfStudentDetail_CourseDetail_visible) {
  courseDetail.value = allData().student_SelfStudentDetail_course_data
}

const backSelfStudentDetail = () => {
  courseDetail.value = { course_id: 0, course_name: '', course_teacher_name: '', course_hours: '', course_score: '' }
  allVisible().student_SelfStudentDetail_CourseDetail_visible = false
  allVisible().student_SelfStudentDetail_visible = true
}

const joinClassTableVisible = ref(false)
const teacherBindingCodeInput = ref('')
const openJoinClassTable = () => { joinClassTableVisible.value = true }
const joinClassRequest = ref({ student_username: '', teacher_binding_code: '' })
const joinClass = async () => {
  joinClassRequest.value.student_username = studentDetail.value.student_account as string
  joinClassRequest.value.teacher_binding_code = teacherBindingCodeInput.value
  const R: R = (await joinClassApi(joinClassRequest.value)).data
  if (R.code === 200) {
    message.success(R.msg, 2)
    localStorage.removeItem('studentDetail')
    sessionStorage.removeItem('studentDetail')
    getSelfStudentDetail()
    joinClassTableVisible.value = false
  } else { message.error(R.msg, 2) }
}
const joinClassCancel = () => { joinClassTableVisible.value = false }
const backToHome = () => router.push({ name: 'StudentHomeView' })

// ===== Chat State =====
const CHAT_STORAGE_KEY = 'student_chat_messages'
const CHAT_MODEL_KEY = 'student_chat_model'
const models = ref<ModelInfo[]>([])
const selectedModelId = ref(localStorage.getItem(CHAT_MODEL_KEY) || 'deepseek-v4-pro')
const inputText = ref('')
const sending = ref(false)
const chatMessagesRef = ref<HTMLElement | null>(null)

interface DisplayMessage { role: 'user' | 'assistant'; content: string; time: string }
const messages = ref<DisplayMessage[]>([])

try {
  const saved = sessionStorage.getItem(CHAT_STORAGE_KEY)
  if (saved) messages.value = JSON.parse(saved)
} catch { /**/ }

function saveMessages() {
  try { sessionStorage.setItem(CHAT_STORAGE_KEY, JSON.stringify(messages.value)) } catch { /**/ }
}

const currentModel = computed<ModelInfo>(() => {
  const found = models.value.find(m => m.id === selectedModelId.value)
  return found || { id: selectedModelId.value, name: selectedModelId.value, description: '', provider: '', online: false }
})

const quickSuggestions = ['帮我解释一下数据结构', '如何学好编程？', '给我出几道练习题']

onMounted(async () => {
  try {
    models.value = await getModelsApi('student')
    if (models.value.length === 0) {
      models.value = [
        { id: 'deepseek-v4-pro', name: 'DeepSeek V4 Pro', description: '最新旗舰模型', provider: 'deepseek', online: false },
        { id: 'deepseek-chat', name: 'DeepSeek Chat', description: '通用对话模型', provider: 'deepseek', online: false },
      ]
    }
  } catch {
    models.value = [
      { id: 'deepseek-v4-pro', name: 'DeepSeek V4 Pro', description: '最新旗舰模型', provider: 'deepseek', online: false },
      { id: 'deepseek-chat', name: 'DeepSeek Chat', description: '通用对话模型', provider: 'deepseek', online: false },
    ]
  }
})

function onModelChange() { localStorage.setItem(CHAT_MODEL_KEY, selectedModelId.value) }
function scrollToBottom() {
  nextTick(() => {
    const el = chatMessagesRef.value
    if (el) el.scrollTop = el.scrollHeight
  })
}
function handleEnter(e: KeyboardEvent) {
  if (!e.shiftKey) { e.preventDefault(); sendMessage() }
}
function sendQuick(text: string) { inputText.value = text; sendMessage() }

async function sendMessage() {
  const text = inputText.value.trim()
  if (!text || sending.value || !currentModel.value.online) return
  messages.value.push({ role: 'user', content: text, time: formatNow() })
  inputText.value = ''
  saveMessages()
  scrollToBottom()

  const apiMessages: ChatMessage[] = [
    { role: 'system', content: '你是一个专业的 AI 学习助手，请用中文回答，提供准确、有帮助的信息。' },
    ...messages.value.map(m => ({ role: m.role === 'assistant' ? 'assistant' as const : 'user' as const, content: m.content })),
  ]
  sending.value = true
  scrollToBottom()
  try {
    const res = await sendChatApi('student', { model: selectedModelId.value, messages: apiMessages })
    messages.value.push({ role: 'assistant', content: res.content || '(空回复)', time: formatNow() })
    saveMessages()
  } catch {
    messages.value.push({ role: 'assistant', content: '抱歉，请求失败，请稍后重试。', time: formatNow() })
  }
  sending.value = false
  scrollToBottom()
}

function formatNow(): string {
  const d = new Date()
  return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}
</script>

<style scoped>
/* ===== Layout ===== */
.personal-center {
  height: 100%;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  padding: 16px 20px 0;
  overflow: hidden;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}
.pc-left {
  display: flex; flex-direction: column; gap: 16px;
  overflow-y: auto; min-height: 0;
}
.pc-right { min-height: 0; display: flex; }

/* ===== Hero ===== */
.profile-hero {
  background: linear-gradient(145deg, #0D9488 0%, #14B8A6 35%, #06B6D4 70%, #0D9488 100%);
  border-radius: 24px;
  padding: 24px 28px 32px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 12px 40px rgba(13,148,136,0.28);
  flex-shrink: 0;
}
.profile-hero::after {
  content: '';
  position: absolute;
  top: -40px; right: -40px;
  width: 200px; height: 200px;
  border-radius: 50%;
  background: rgba(255,255,255,0.04);
  pointer-events: none;
}
.hero-actions-top {
  position: relative; z-index: 1;
  display: flex; justify-content: space-between; gap: 12px;
  margin-bottom: 24px;
}
.btn-back {
  background: rgba(255,255,255,0.15) !important;
  backdrop-filter: blur(6px);
  border: 1px solid rgba(255,255,255,0.2) !important;
  color: white !important; font-weight: 600 !important;
  border-radius: 10px !important;
  display: flex; align-items: center; gap: 6px;
  transition: all 0.25s !important;
}
.btn-back:hover { background: rgba(255,255,255,0.25) !important; }
.btn-join {
  background: rgba(255,255,255,0.2) !important;
  backdrop-filter: blur(6px);
  border: 1px solid rgba(255,255,255,0.25) !important;
  color: white !important; font-weight: 700 !important;
  border-radius: 10px !important;
  display: flex; align-items: center; gap: 6px;
  transition: all 0.25s !important;
  box-shadow: 0 2px 10px rgba(0,0,0,0.08);
}
.btn-join:hover { background: rgba(255,255,255,0.3) !important; transform: translateY(-1px); }
.btn-join:disabled { opacity: 0.5; cursor: not-allowed; }

.hero-profile {
  position: relative; z-index: 1;
  display: flex; align-items: center; gap: 24px;
}
.hero-avatar-wrap { position: relative; flex-shrink: 0; }
.hero-avatar {
  width: 80px; height: 80px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid rgba(255,255,255,0.4);
  box-shadow: 0 8px 28px rgba(0,0,0,0.15);
  background: rgba(255,255,255,0.1);
}
.hero-avatar img { width: 100%; height: 100%; object-fit: cover; }
.hero-status {
  position: absolute;
  bottom: 4px; right: 4px;
  width: 16px; height: 16px;
  background: #10B981;
  border-radius: 50%;
  border: 3px solid white;
  box-shadow: 0 0 0 4px rgba(16,185,129,0.2);
  animation: pulse-dot 2s infinite;
}
@keyframes pulse-dot {
  0%, 100% { box-shadow: 0 0 0 4px rgba(16,185,129,0.2); }
  50% { box-shadow: 0 0 0 10px rgba(16,185,129,0); }
}
.hero-text { color: white; }
.hero-name { font-size: 24px; font-weight: 800; margin: 0 0 4px; letter-spacing: 0.5px; }
.hero-account { font-size: 13px; opacity: 0.7; font-weight: 500; }
.hero-chips { display: flex; gap: 8px; flex-wrap: wrap; margin-top: 8px; }
.chip {
  display: inline-flex; align-items: center; gap: 5px;
  background: rgba(255,255,255,0.15);
  backdrop-filter: blur(4px);
  padding: 4px 12px; border-radius: 20px;
  font-size: 12px; font-weight: 600;
}

/* ===== Info Cards ===== */
.info-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  flex: 1;
  overflow-y: auto;
  align-content: start;
}
.info-card {
  background: white; border-radius: 14px; padding: 16px;
  border: 1px solid #E2E8F0;
  display: flex; align-items: center; gap: 12px;
  transition: all 0.3s;
}
.info-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0,0,0,0.06);
  border-color: rgba(13,148,136,0.12);
}
.info-card-icon {
  width: 40px; height: 40px; min-width: 40px;
  border-radius: 11px;
  display: flex; align-items: center; justify-content: center;
}
.info-card-body { display: flex; flex-direction: column; gap: 2px; }
.info-card-label { font-size: 10px; font-weight: 600; color: #94A3B8; text-transform: uppercase; letter-spacing: 0.5px; }
.info-card-value { font-size: 14px; font-weight: 700; color: #0F172A; }

/* ===== Chat Panel (shared styles with teacher) ===== */
.chat-panel {
  flex: 1;
  display: flex; flex-direction: column;
  background: #FFFFFF; border-radius: 20px;
  border: 1px solid #E2E8F0;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
  overflow: hidden;
}
.chat-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 14px 20px;
  border-bottom: 1px solid #F1F5F9;
  background: #FAFBFC;
  flex-shrink: 0;
}
.chat-header-left { display: flex; align-items: center; gap: 12px; }
.chat-header-icon { font-size: 24px; }
.chat-title { font-size: 16px; font-weight: 700; color: #0F172A; margin: 0; }
.chat-model-info { display: flex; align-items: center; gap: 6px; margin-top: 2px; }
.model-status-dot {
  width: 7px; height: 7px; border-radius: 50%; flex-shrink: 0;
}
.model-status-dot.online { background: #10B981; box-shadow: 0 0 6px rgba(16,185,129,0.4); }
.model-status-dot.offline { background: #EF4444; }
.model-name { font-size: 12px; font-weight: 600; color: #475569; }
.model-status-text { font-size: 11px; color: #94A3B8; }
.chat-header-right { flex-shrink: 0; }

.model-option { display: flex; align-items: center; gap: 6px; }
.model-option-dot { width: 6px; height: 6px; border-radius: 50%; flex-shrink: 0; }
.model-option-dot.online { background: #10B981; }
.model-option-dot.offline { background: #EF4444; }
.model-option-name { font-size: 12px; font-weight: 500; }

.chat-messages {
  flex: 1; min-height: 0;
  overflow-y: auto;
  padding: 16px 20px;
  display: flex; flex-direction: column; gap: 14px;
}
.chat-messages::-webkit-scrollbar { width: 4px; }
.chat-messages::-webkit-scrollbar-thumb { background: #E2E8F0; border-radius: 4px; }

.chat-empty {
  flex: 1; display: flex; flex-direction: column;
  align-items: center; justify-content: center;
  text-align: center; padding: 40px 20px; color: #94A3B8;
}
.chat-empty-icon { font-size: 48px; margin-bottom: 12px; }
.chat-empty h3 { font-size: 16px; font-weight: 700; color: #475569; margin: 0 0 6px; }
.chat-empty p { font-size: 13px; margin: 0 0 20px; }
.chat-suggestions { display: flex; flex-wrap: wrap; gap: 8px; justify-content: center; }
.chat-suggestion-btn {
  padding: 6px 14px; font-size: 12px; font-weight: 500;
  background: #F8FAFC; border: 1px solid #E2E8F0; border-radius: 9999px;
  color: #475569; cursor: pointer; transition: all 0.2s; font-family: inherit;
}
.chat-suggestion-btn:hover { background: #EFF6FF; border-color: #93C5FD; color: #2563EB; }

.chat-msg { display: flex; gap: 8px; max-width: 90%; }
.chat-msg.user { align-self: flex-end; flex-direction: row-reverse; }
.chat-msg.assistant { align-self: flex-start; }
.chat-msg-avatar {
  width: 32px; height: 32px; min-width: 32px;
  border-radius: 10px; display: flex; align-items: center; justify-content: center;
  font-size: 16px; background: #F8FAFC;
}
.chat-msg.user .chat-msg-avatar { background: #D1FAE5; }
.chat-msg-bubble {
  padding: 10px 14px; border-radius: 14px;
  font-size: 13px; line-height: 1.6; color: #0F172A;
}
.chat-msg.user .chat-msg-bubble {
  background: linear-gradient(135deg, #0D9488, #14B8A6); color: white;
  border-bottom-right-radius: 4px;
}
.chat-msg.assistant .chat-msg-bubble {
  background: #F8FAFC; border: 1px solid #F1F5F9;
  border-bottom-left-radius: 4px;
}
.chat-msg-content { white-space: pre-wrap; word-break: break-word; }
.chat-msg-time { font-size: 10px; margin-top: 4px; opacity: 0.6; text-align: right; }
.chat-msg.assistant .chat-msg-time { text-align: left; }

.chat-msg-bubble.typing {
  display: flex; align-items: center; gap: 4px; padding: 14px 18px;
}
.typing-dot {
  width: 6px; height: 6px; border-radius: 50%;
  background: #94A3B8;
  animation: typing-bounce 1.4s infinite ease-in-out both;
}
.typing-dot:nth-child(1) { animation-delay: -0.32s; }
.typing-dot:nth-child(2) { animation-delay: -0.16s; }
@keyframes typing-bounce {
  0%, 80%, 100% { transform: scale(0.6); }
  40% { transform: scale(1); }
}

.chat-input-area {
  display: flex; align-items: flex-end; gap: 10px;
  padding: 12px 16px;
  border-top: 1px solid #F1F5F9;
  background: #FAFBFC; flex-shrink: 0;
}
.chat-input-area :deep(.ant-input) { border-radius: 12px !important; resize: none; font-size: 13px; }
.chat-send-btn {
  width: 40px; height: 40px; min-width: 40px;
  border-radius: 12px; border: none;
  background: linear-gradient(135deg, #0D9488, #14B8A6);
  color: white; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  transition: all 0.2s;
}
.chat-send-btn:hover:not(:disabled) { transform: scale(1.05); box-shadow: 0 4px 12px rgba(13,148,136,0.3); }
.chat-send-btn:disabled { opacity: 0.4; cursor: not-allowed; }

/* ===== Modal ===== */
.modal-header-custom {
  display: flex; align-items: center; gap: 12px;
  font-size: 18px; font-weight: 700; color: #0F172A;
}
.modal-icon-wrap {
  width: 40px; height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #0D9488, #14B8A6);
  display: flex; align-items: center; justify-content: center;
}

/* ===== Course Detail ===== */
.course-detail-page {
  height: 100%; display: flex; flex-direction: column;
  overflow: hidden; max-width: 780px; margin: 0 auto;
  width: 100%; padding: 16px 20px 0;
}
.cd-card {
  flex: 1; min-height: 0; display: flex; flex-direction: column;
  background: white; border-radius: 20px; overflow: hidden;
  border: 1px solid #E2E8F0; box-shadow: 0 2px 12px rgba(0,0,0,0.04);
}
.cd-header {
  background: linear-gradient(145deg, #0D9488 0%, #14B8A6 50%, #06B6D4 100%);
  padding: 24px 28px 28px; color: white;
  position: relative; overflow: hidden;
}
.cd-header::after {
  content: ''; position: absolute; top: -30px; right: -30px;
  width: 140px; height: 140px; border-radius: 50%;
  background: rgba(255,255,255,0.05); pointer-events: none;
}
.cd-back {
  background: rgba(255,255,255,0.15); backdrop-filter: blur(4px);
  border: 1px solid rgba(255,255,255,0.2); color: white;
  padding: 8px 16px; border-radius: 10px; cursor: pointer;
  font-size: 14px; font-weight: 600;
  display: inline-flex; align-items: center; gap: 6px;
  margin-bottom: 16px; transition: all 0.25s;
}
.cd-back:hover { background: rgba(255,255,255,0.25); }
.cd-header-main { display: flex; align-items: center; gap: 16px; position: relative; z-index: 1; }
.cd-icon { font-size: 36px; }
.cd-header-main h2 { margin: 0; font-size: 24px; font-weight: 800; }
.cd-body { padding: 28px; }
.cd-info-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
.cd-info-item {
  display: flex; align-items: center; gap: 16px;
  background: #F8FAFC; border-radius: 16px; padding: 18px 20px;
  border: 1px solid #F1F5F9; transition: all 0.3s;
}
.cd-info-item:hover {
  background: white; box-shadow: 0 6px 20px rgba(0,0,0,0.05);
  border-color: rgba(13,148,136,0.12); transform: translateY(-2px);
}
.cd-info-icon {
  width: 44px; height: 44px; min-width: 44px;
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
}
.cd-info-item > div:last-child { display: flex; flex-direction: column; gap: 2px; }
.cd-info-label { font-size: 11px; font-weight: 600; color: #94A3B8; text-transform: uppercase; letter-spacing: 0.5px; }
.cd-info-value { font-size: 16px; font-weight: 700; color: #0F172A; }

/* ===== Responsive ===== */
@media (max-width: 900px) {
  .personal-center { grid-template-columns: 1fr; grid-template-rows: auto 1fr; }
  .pc-left { max-height: 45vh; }
}
@media (max-width: 600px) {
  .personal-center { padding: 10px; gap: 10px; }
  .info-cards { grid-template-columns: 1fr; }
  .cd-info-grid { grid-template-columns: 1fr; }
}
</style>
