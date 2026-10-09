<template>
  <div class="personal-center">
    <!-- ===== 左栏：个人信息 ===== -->
    <div class="pc-left">
      <!-- Profile Header -->
      <div class="profile-card">
        <div class="profile-bg"></div>
        <div class="profile-content">
          <div class="profile-avatar">
            <span class="avatar-text">{{ (teacherDetail.teacher_name || '?').charAt(0) }}</span>
          </div>
          <div class="profile-info">
            <h1 class="profile-name">{{ teacherDetail.teacher_name }}</h1>
            <span class="profile-badge">
              <font-awesome-icon :icon="['fas', 'chalkboard-user']" /> 教师
            </span>
            <span class="profile-id">ID: {{ teacherDetail.teacher_id }}</span>
          </div>
          <a-button class="back-btn" @click="backToHome">
            <span>←</span> 返回首页
          </a-button>
        </div>
      </div>

      <!-- Info Cards -->
      <div class="info-cards">
        <div class="info-card">
          <div class="info-card-icon account">
            <font-awesome-icon :icon="['fas', 'user']" />
          </div>
          <div class="info-card-body">
            <span class="info-card-label">教师账号</span>
            <span class="info-card-value">{{ teacherDetail.teacher_account }}</span>
          </div>
        </div>
        <div class="info-card">
          <div class="info-card-icon gender">
            <font-awesome-icon :icon="['fas', 'venus-mars']" />
          </div>
          <div class="info-card-body">
            <span class="info-card-label">性别</span>
            <span class="info-card-value">{{ teacherDetail.teacher_gender }}</span>
          </div>
        </div>
        <div class="info-card">
          <div class="info-card-icon age">
            <font-awesome-icon :icon="['fas', 'birthday-cake']" />
          </div>
          <div class="info-card-body">
            <span class="info-card-label">年龄</span>
            <span class="info-card-value">{{ teacherDetail.teacher_age }}</span>
          </div>
        </div>
        <div class="info-card">
          <div class="info-card-icon position">
            <font-awesome-icon :icon="['fas', 'briefcase']" />
          </div>
          <div class="info-card-body">
            <span class="info-card-label">职位</span>
            <span class="info-card-value">{{ teacherDetail.teacher_position }}</span>
          </div>
        </div>
        <div class="info-card">
          <div class="info-card-icon major">
            <font-awesome-icon :icon="['fas', 'book']" />
          </div>
          <div class="info-card-body">
            <span class="info-card-label">专业</span>
            <span class="info-card-value">{{ teacherDetail.teacher_major || '暂无数据' }}</span>
          </div>
        </div>
        <div class="info-card">
          <div class="info-card-icon class">
            <font-awesome-icon :icon="['fas', 'graduation-cap']" />
          </div>
          <div class="info-card-body">
            <span class="info-card-label">所属班级</span>
            <span class="info-card-value">{{ teacherDetail.teacher_class_name || '暂无数据' }}</span>
          </div>
        </div>
        <div class="info-card">
          <div class="info-card-icon students">
            <font-awesome-icon :icon="['fas', 'users']" />
          </div>
          <div class="info-card-body">
            <span class="info-card-label">班级人数</span>
            <span class="info-card-value">{{ teacherDetail.teacher_class_student_number || '暂无数据' }}</span>
          </div>
        </div>
        <div class="info-card">
          <div class="info-card-icon binding">
            <font-awesome-icon :icon="['fas', 'key']" />
          </div>
          <div class="info-card-body">
            <span class="info-card-label">绑定教师码</span>
            <span class="info-card-value">{{ teacherDetail.teacher_binding_code || '暂无数据' }}</span>
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
              <h3 class="chat-title">AI 助手</h3>
              <div class="chat-model-info">
                <span class="model-status-dot" :class="{ online: currentModel.online, offline: !currentModel.online }"></span>
                <span class="model-name">{{ currentModel.name }}</span>
                <span class="model-status-text">{{ currentModel.online ? '在线' : '离线' }}</span>
              </div>
            </div>
          </div>
          <!-- Model Selector -->
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
                  <span class="model-option-status">{{ m.online ? '在线' : '离线' }}</span>
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
            <p>选择模型后发送消息，与 AI 助手进行对话</p>
            <div class="chat-suggestions">
              <button
                v-for="s in quickSuggestions"
                :key="s"
                class="chat-suggestion-btn"
                @click="sendQuick(s)"
              >{{ s }}</button>
            </div>
          </div>
          <div
            v-for="(msg, idx) in messages"
            :key="idx"
            class="chat-msg"
            :class="msg.role"
          >
            <div class="chat-msg-avatar">
              {{ msg.role === 'user' ? '👤' : '🤖' }}
            </div>
            <div class="chat-msg-bubble">
              <div class="chat-msg-content">{{ msg.content }}</div>
              <div class="chat-msg-time">{{ msg.time }}</div>
            </div>
          </div>
          <div v-if="sending" class="chat-msg assistant">
            <div class="chat-msg-avatar">🤖</div>
            <div class="chat-msg-bubble typing">
              <span class="typing-dot"></span>
              <span class="typing-dot"></span>
              <span class="typing-dot"></span>
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
          <button
            class="chat-send-btn"
            :disabled="!inputText.trim() || sending || !currentModel.online"
            @click="sendMessage"
          >
            <svg viewBox="0 0 24 24" fill="none" width="18" height="18">
              <path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import type { teacher } from '../../modules/teacher'
import { jwtDecode } from 'jwt-decode'
import { getSelfTeacherDetailApi } from '../../api/teacher/getSelfTeacherDetailApi'
import type { R } from '../../response/R'
import { message } from 'ant-design-vue'
import router from '../../routers/appRouters'
import { sendChatApi, getModelsApi } from '../../api/chatApi'
import type { ChatMessage, ModelInfo } from '../../api/chatApi'

const token = ref((sessionStorage.getItem('token') || localStorage.getItem('token')))
const teacher_id = (() => { try { return token.value ? (jwtDecode(token.value) as any).sub as string : '' } catch { return '' } })()
const teacherDetail = ref<teacher>({
  teacher_id: 0, teacher_account: '', teacher_password: '', teacher_name: '',
  teacher_gender: '', teacher_age: '', teacher_major: '', teacher_position: '',
  teacher_class_name: '', teacher_class_student_number: '', teacher_binding_code: '',
  teacher_studentList: [], teacher_courseList: [], teacher_courseResource: [],
})

const getSelfTeacherDetail = async () => {
  if (!teacher_id) { message.error('无法获取教师身份信息'); return }
  getSelfTeacherDetailApi(teacher_id).then(res => {
    const R: R = res.data
    if (R.code === 200) {
      teacherDetail.value = R.data
      sessionStorage.setItem('teacherDetail', JSON.stringify(teacherDetail.value))
    } else { message.error('获取教师信息失败') }
  }).catch(err => { message.error('获取教师信息失败'); console.log(err) })
}

// 仅从 sessionStorage 恢复缓存的个人信息（标签页隔离，不从 localStorage 读取以避免跨用户数据污染）
const cachedDetail = (sessionStorage.getItem('teacherDetail') || localStorage.getItem('teacherDetail'))
if (cachedDetail) {
  try {
    const parsed = JSON.parse(cachedDetail)
    // 验证缓存数据属于当前登录用户
    if (parsed.teacher_account === teacher_id) {
      teacherDetail.value = parsed
    }
  } catch {}
}
if (!teacherDetail.value.teacher_account) {
  getSelfTeacherDetail()
}

const backToHome = () => router.push({ name: 'TeacherHomeView' })

// ===== Chat State =====
const CHAT_STORAGE_KEY = 'teacher_chat_messages'
const CHAT_MODEL_KEY = 'teacher_chat_model'

const models = ref<ModelInfo[]>([])
const selectedModelId = ref(localStorage.getItem(CHAT_MODEL_KEY) || 'deepseek-v4-pro')
const inputText = ref('')
const sending = ref(false)
const chatMessagesRef = ref<HTMLElement | null>(null)

interface DisplayMessage {
  role: 'user' | 'assistant'
  content: string
  time: string
}
const messages = ref<DisplayMessage[]>([])

// 加载历史消息
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

const quickSuggestions = [
  '介绍一下你的能力',
  '帮我设计一个教学方案',
  '解释一下深度学习的原理',
]

// 加载模型列表
onMounted(async () => {
  try {
    models.value = await getModelsApi('teacher')
    if (models.value.length === 0) {
      // Fallback defaults
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

function onModelChange() {
  localStorage.setItem(CHAT_MODEL_KEY, selectedModelId.value)
}

function scrollToBottom() {
  nextTick(() => {
    const el = chatMessagesRef.value
    if (el) el.scrollTop = el.scrollHeight
  })
}

function handleEnter(e: KeyboardEvent) {
  if (!e.shiftKey) {
    e.preventDefault()
    sendMessage()
  }
}

function sendQuick(text: string) {
  inputText.value = text
  sendMessage()
}

async function sendMessage() {
  const text = inputText.value.trim()
  if (!text || sending.value || !currentModel.value.online) return

  // Add user message
  messages.value.push({ role: 'user', content: text, time: formatNow() })
  inputText.value = ''
  saveMessages()
  scrollToBottom()

  // Build API messages
  const apiMessages: ChatMessage[] = [
    { role: 'system', content: '你是一个专业的 AI 教学助手，请用中文回答，提供准确、有帮助的信息。' },
    ...messages.value.map(m => ({ role: m.role === 'assistant' ? 'assistant' as const : 'user' as const, content: m.content })),
  ]

  sending.value = true
  scrollToBottom()

  try {
    const res = await sendChatApi('teacher', {
      model: selectedModelId.value,
      messages: apiMessages,
    })
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
  padding: 24px;
  overflow: hidden;
  background: linear-gradient(180deg, #FFF7ED 0%, #F1F5F9 100%);
}
.pc-left {
  display: flex;
  flex-direction: column;
  gap: 16px;
  overflow-y: auto;
  min-height: 0;
}
.pc-right {
  min-height: 0;
  display: flex;
}

/* ===== Profile Card ===== */
.profile-card {
  background: #FFFFFF;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 2px 16px rgba(0,0,0,0.04), 0 1px 4px rgba(0,0,0,0.02);
  border: 1px solid #FED7AA;
  flex-shrink: 0;
}
.profile-bg {
  height: 60px;
  background: linear-gradient(135deg, #F59E0B 0%, #D97706 40%, #B45309 100%);
  position: relative; overflow: hidden;
}
.profile-bg::after {
  content: '';
  position: absolute; inset: 0;
  background: radial-gradient(circle at 30% 50%, rgba(255,255,255,0.2) 0%, transparent 60%),
              radial-gradient(circle at 70% 80%, rgba(255,255,255,0.1) 0%, transparent 50%);
}
.profile-content {
  display: flex; align-items: center; gap: 16px;
  padding: 0 20px 16px; margin-top: -30px; position: relative; z-index: 1;
}
.profile-avatar {
  width: 60px; height: 60px; min-width: 60px;
  border-radius: 16px;
  background: linear-gradient(135deg, #FFFFFF, #FEF3C7);
  border: 3px solid #FFFFFF;
  box-shadow: 0 4px 12px rgba(245,158,11,0.3);
  display: flex; align-items: center; justify-content: center;
}
.avatar-text {
  font-size: 26px; font-weight: 800;
  background: linear-gradient(135deg, #D97706, #B45309);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text;
}
.profile-info { display: flex; flex-direction: column; gap: 2px; flex: 1; }
.profile-name { font-size: 20px; font-weight: 800; color: #0F172A; margin: 0; }
.profile-badge {
  display: inline-flex; align-items: center; gap: 4px;
  font-size: 11px; font-weight: 600; color: #D97706; background: #FEF3C7;
  padding: 2px 10px; border-radius: 999px; width: fit-content;
}
.profile-id { font-size: 11px; color: #94A3B8; }
.back-btn {
  border-radius: 999px !important; font-weight: 600 !important; height: 34px !important;
  padding: 0 18px !important; font-size: 13px !important;
  background: linear-gradient(135deg, #FEF3C7, #FDE68A) !important;
  border: 1px solid #FCD34D !important; color: #92400E !important;
  flex-shrink: 0;
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
  background: #FFFFFF; border-radius: 14px; padding: 14px 16px;
  border: 1px solid #F1F5F9; box-shadow: 0 1px 3px rgba(0,0,0,0.03);
  display: flex; align-items: center; gap: 12px;
  transition: all 0.3s;
}
.info-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(245,158,11,0.1);
  border-color: rgba(245,158,11,0.12);
}
.info-card-icon {
  width: 40px; height: 40px; min-width: 40px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center; font-size: 16px;
}
.info-card-icon.account { background: linear-gradient(135deg, #EEF2FF, #E0E7FF); color: #4F46E5; }
.info-card-icon.gender { background: linear-gradient(135deg, #FDF2F8, #FCE7F3); color: #EC4899; }
.info-card-icon.age { background: linear-gradient(135deg, #F0FDF4, #DCFCE7); color: #10B981; }
.info-card-icon.position { background: linear-gradient(135deg, #FFF7ED, #FFEDD5); color: #F97316; }
.info-card-icon.major { background: linear-gradient(135deg, #EFF6FF, #DBEAFE); color: #3B82F6; }
.info-card-icon.class { background: linear-gradient(135deg, #F5F3FF, #EDE9FE); color: #8B5CF6; }
.info-card-icon.students { background: linear-gradient(135deg, #ECFDF5, #D1FAE5); color: #059669; }
.info-card-icon.binding { background: linear-gradient(135deg, #FEF2F2, #FEE2E2); color: #DC2626; }
.info-card-body { display: flex; flex-direction: column; gap: 2px; }
.info-card-label { font-size: 10px; font-weight: 600; color: #94A3B8; text-transform: uppercase; letter-spacing: 0.5px; }
.info-card-value { font-size: 14px; font-weight: 600; color: #0F172A; }

/* ===== Chat Panel ===== */
.chat-panel {
  flex: 1;
  display: flex; flex-direction: column;
  background: #FFFFFF; border-radius: 20px;
  border: 1px solid #E2E8F0;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
  overflow: hidden;
}

/* Chat Header */
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

/* Model Select Option */
.model-option { display: flex; align-items: center; gap: 6px; }
.model-option-dot {
  width: 6px; height: 6px; border-radius: 50%; flex-shrink: 0;
}
.model-option-dot.online { background: #10B981; }
.model-option-dot.offline { background: #EF4444; }
.model-option-name { font-size: 12px; font-weight: 500; }
.model-option-status { font-size: 10px; margin-left: auto; }
.model-option-status { color: #10B981; }
.model-option-dot.offline ~ .model-option-status { color: #EF4444; }

/* Messages Area */
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

/* Message Bubbles */
.chat-msg { display: flex; gap: 8px; max-width: 90%; }
.chat-msg.user { align-self: flex-end; flex-direction: row-reverse; }
.chat-msg.assistant { align-self: flex-start; }
.chat-msg-avatar {
  width: 32px; height: 32px; min-width: 32px;
  border-radius: 10px; display: flex; align-items: center; justify-content: center;
  font-size: 16px; background: #F8FAFC;
}
.chat-msg.user .chat-msg-avatar { background: #FEF3C7; }
.chat-msg-bubble {
  padding: 10px 14px; border-radius: 14px;
  font-size: 13px; line-height: 1.6; color: #0F172A;
}
.chat-msg.user .chat-msg-bubble {
  background: linear-gradient(135deg, #F59E0B, #D97706); color: white;
  border-bottom-right-radius: 4px;
}
.chat-msg.assistant .chat-msg-bubble {
  background: #F8FAFC; border: 1px solid #F1F5F9;
  border-bottom-left-radius: 4px;
}
.chat-msg-content { white-space: pre-wrap; word-break: break-word; }
.chat-msg-time {
  font-size: 10px; margin-top: 4px; opacity: 0.6;
  text-align: right;
}
.chat-msg.assistant .chat-msg-time { text-align: left; }

/* Typing indicator */
.chat-msg-bubble.typing {
  display: flex; align-items: center; gap: 4px;
  padding: 14px 18px;
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

/* Input Area */
.chat-input-area {
  display: flex; align-items: flex-end; gap: 10px;
  padding: 12px 16px;
  border-top: 1px solid #F1F5F9;
  background: #FAFBFC;
  flex-shrink: 0;
}
.chat-input-area :deep(.ant-input) {
  border-radius: 12px !important; resize: none;
  font-size: 13px;
}
.chat-send-btn {
  width: 40px; height: 40px; min-width: 40px;
  border-radius: 12px; border: none;
  background: linear-gradient(135deg, #F59E0B, #D97706);
  color: white; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  transition: all 0.2s;
}
.chat-send-btn:hover:not(:disabled) {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(245,158,11,0.3);
}
.chat-send-btn:disabled { opacity: 0.4; cursor: not-allowed; }

/* ===== Responsive ===== */
@media (max-width: 900px) {
  .personal-center { grid-template-columns: 1fr; grid-template-rows: auto 1fr; }
  .pc-left { max-height: 50vh; }
}
@media (max-width: 600px) {
  .personal-center { padding: 12px; gap: 12px; }
  .info-cards { grid-template-columns: 1fr; }
}
</style>
