<template>
  <div
    class="fab-root"
    :style="{ left: pos.x + 'px', top: pos.y + 'px' }"
  >
    <!-- Chat Popup -->
    <transition name="fab-popup">
      <div v-if="open" class="fab-popup" :style="popupStyle">
        <!-- Header -->
        <div class="fab-header">
          <div class="fab-h-left">
            <span class="fab-h-dot" :class="{ online: currentModelOnline }"></span>
            <span class="fab-h-title">AI 助手</span>
            <span class="fab-h-model">{{ currentModelName }}</span>
          </div>
          <div class="fab-h-right">
            <select v-model="selectedModelId" class="fab-model-select">
              <option v-for="m in models" :key="m.id" :value="m.id">{{ m.name }}</option>
            </select>
            <button v-if="messages.length > 0" class="fab-btn-clear" title="清空对话" @click="clearHistory">🗑</button>
            <button class="fab-btn-close" @click="open = false">&times;</button>
          </div>
        </div>

        <!-- Messages -->
        <div ref="msgList" class="fab-messages">
          <div v-if="messages.length === 0" class="fab-empty">
            <div class="fab-empty-icon">💬</div>
            <p>有问题随时问我</p>
            <div class="fab-suggestions">
              <button
                v-for="s in quickSuggestions"
                :key="s"
                class="fab-sugg"
                @click="send(s)"
              >{{ s }}</button>
            </div>
          </div>
          <div
            v-for="(m, i) in messages"
            :key="i"
            class="fab-msg"
            :class="'fab-msg-' + m.role"
          >
            <div class="fab-msg-text">{{ m.content }}</div>
          </div>
          <div v-if="loading" class="fab-msg fab-msg-assistant">
            <div class="fab-typing"><span></span><span></span><span></span></div>
          </div>
        </div>

        <!-- Input -->
        <div class="fab-input-row">
          <textarea
            v-model="inputText"
            class="fab-input"
            rows="1"
            placeholder="输入消息..."
            @keydown.enter.exact.prevent="send()"
            @input="autoResize"
          ></textarea>
          <button class="fab-btn-send" :disabled="!inputText.trim() || loading" @click="send()">
            <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
          </button>
        </div>
      </div>
    </transition>

    <!-- Floating Ball -->
    <button
      class="fab-ball"
      :class="{ active: open }"
      @pointerdown="onPointerDown"
      @click.stop="onBallClick"
    >
      <svg v-if="!open" viewBox="0 0 24 24" fill="none" width="22" height="22">
        <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <svg v-else viewBox="0 0 24 24" fill="none" width="20" height="20">
        <path d="M18 6L6 18M6 6l12 12" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
      </svg>
    </button>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, onMounted, watch, nextTick } from 'vue'
import { sendChatApi, getModelsApi, type ChatMessage, type ModelInfo } from '../../api/chatApi'

const POS_KEY = 'fab_position'
const MODEL_KEY = 'fab_selected_model'

// Token-scoped chat storage: one login = one session, survives tab close, resets on re-login
function getChatStorageKey(): string {
  try {
    const token = (sessionStorage.getItem('token') || localStorage.getItem('token'))
    if (token) {
      // Simple hash of token to avoid putting raw token in key
      const hash = token.split('').reduce((a, c) => ((a << 5) - a + c.charCodeAt(0)) | 0, 0).toString(36)
      return `fab_chat_${role}_${hash}`
    }
  } catch {}
  return `fab_chat_${role}_anonymous`
}
function chatStorageKey(): string { return getChatStorageKey() }

// Position
const pos = reactive({
  x: Math.max(0, window.innerWidth - 88),
  y: Math.max(0, window.innerHeight - 200),
})

// Restore saved position
try {
  const saved = JSON.parse(localStorage.getItem(POS_KEY) || '{}')
  if (typeof saved.x === 'number' && typeof saved.y === 'number') {
    // Clamp to current viewport
    pos.x = Math.max(0, Math.min(saved.x, window.innerWidth - 64))
    pos.y = Math.max(0, Math.min(saved.y, window.innerHeight - 64))
  }
} catch {}

function savePosition() {
  localStorage.setItem(POS_KEY, JSON.stringify({ x: pos.x, y: pos.y }))
}

// Drag
interface DragState { startX: number; startY: number; origX: number; origY: number }
let drag: DragState | null = null
let wasDragged = false  // persists after pointerup, so click handler can check it

function onPointerDown(e: PointerEvent) {
  wasDragged = false
  drag = { startX: e.clientX, startY: e.clientY, origX: pos.x, origY: pos.y }
  const onMove = (ev: PointerEvent) => {
    if (!drag) return
    const dx = ev.clientX - drag.startX
    const dy = ev.clientY - drag.startY
    if (Math.abs(dx) > 3 || Math.abs(dy) > 3) {
      wasDragged = true
    }
    pos.x = Math.max(0, Math.min(drag.origX + dx, window.innerWidth - 64))
    pos.y = Math.max(0, Math.min(drag.origY + dy, window.innerHeight - 64))
  }
  const onUp = () => {
    window.removeEventListener('pointermove', onMove)
    window.removeEventListener('pointerup', onUp)
    if (wasDragged) savePosition()
    drag = null
  }
  window.addEventListener('pointermove', onMove)
  window.addEventListener('pointerup', onUp)
}

function onBallClick() {
  if (wasDragged) return
  open.value = !open.value
  if (open.value) {
    nextTick(() => scrollBottom())
  }
}

// Detect role — parse JWT manually to avoid async import
function detectRole(): 'teacher' | 'student' {
  try {
    const token = (sessionStorage.getItem('token') || localStorage.getItem('token'))
    if (token) {
      // JWT payload is the second segment, base64-encoded
      const payload = JSON.parse(atob(token.split('.')[1]))
      const r = payload?.role
      if (r === 'TEACHER') return 'teacher'
    }
  } catch {}
  return 'student'
}
const role = detectRole()

// Chat state
const open = ref(false)
const inputText = ref('')
const loading = ref(false)
const messages = ref<ChatMessage[]>([])
const models = ref<ModelInfo[]>([])
const selectedModelId = ref(localStorage.getItem(MODEL_KEY) || 'deepseek-v4-pro')
const msgList = ref<HTMLElement>()

// Popup boundary handling: flip if near a viewport edge
const popupStyle = computed(() => {
  const s: Record<string, string> = {}
  if (pos.y < 520) { s.bottom = 'auto'; s.top = '64px' }
  else { s.bottom = '64px'; s.top = 'auto' }
  if (pos.x < 400) { s.right = 'auto'; s.left = '0' }
  else { s.right = '0'; s.left = 'auto' }
  return s
})

const currentModel = computed(() => models.value.find(m => m.id === selectedModelId.value))
const currentModelName = computed(() => currentModel.value?.name || selectedModelId.value)
const currentModelOnline = computed(() => currentModel.value?.online ?? false)

const quickSuggestions = [
  '如何提高学生学习兴趣？',
  '帮我设计一个课堂互动环节',
  '简要解释布鲁姆教学目标分类',
]

// Load messages from localStorage (persists across tab close, resets on re-login)
function loadMessages() {
  try {
    const raw = localStorage.getItem(chatStorageKey())
    if (raw) messages.value = JSON.parse(raw)
  } catch {}
}
function saveMessages() {
  localStorage.setItem(chatStorageKey(), JSON.stringify(messages.value))
}

watch(selectedModelId, (v) => {
  localStorage.setItem(MODEL_KEY, v)
})

function autoResize(e: Event) {
  const el = e.target as HTMLTextAreaElement
  el.style.height = 'auto'
  el.style.height = Math.min(el.scrollHeight, 100) + 'px'
}

function scrollBottom() {
  if (msgList.value) {
    msgList.value.scrollTop = msgList.value.scrollHeight
  }
}

function clearHistory() {
  messages.value = []
  localStorage.removeItem(chatStorageKey())
}

async function send(preset?: string) {
  const text = (preset || inputText.value).trim()
  if (!text || loading.value) return
  if (!preset) inputText.value = ''

  messages.value.push({ role: 'user', content: text })
  saveMessages()
  await nextTick()
  scrollBottom()

  loading.value = true
  try {
    const res = await sendChatApi(role, {
      model: selectedModelId.value,
      messages: messages.value.slice(-10), // send last 10 for context
    })
    messages.value.push({ role: 'assistant', content: res.content })
    saveMessages()
  } catch {
    messages.value.push({ role: 'assistant', content: '抱歉，请求超时或服务不可用，请稍后重试。' })
    saveMessages()
  }
  loading.value = false
  await nextTick()
  scrollBottom()
}

async function loadModels() {
  try {
    const list = await getModelsApi(role)
    if (list?.length) models.value = list
  } catch {
    // mock fallback
    models.value = [
      { id: 'deepseek-v4-pro', name: 'DeepSeek V4 Pro', description: '', provider: 'deepseek', online: true },
      { id: 'deepseek-chat', name: 'DeepSeek Chat', description: '', provider: 'deepseek', online: true },
    ]
  }
}

onMounted(() => {
  loadMessages()
  loadModels()
})
</script>

<style scoped>
.fab-root {
  position: fixed;
  z-index: 9999;
  user-select: none;
  -webkit-user-select: none;
}

/* Ball */
.fab-ball {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, #6366F1, #8B5CF6);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: grab;
  box-shadow: 0 4px 20px rgba(99,102,241,0.45);
  transition: transform 0.2s, box-shadow 0.2s;
  position: relative;
}
.fab-ball:active { cursor: grabbing; }
.fab-ball:hover { transform: scale(1.08); box-shadow: 0 6px 28px rgba(99,102,241,0.55); }
.fab-ball.active { background: #fff; color: #6366F1; box-shadow: 0 4px 16px rgba(0,0,0,0.2); }

/* Popup */
.fab-popup {
  position: absolute;
  bottom: 64px;
  right: 0;
  width: 380px;
  height: 480px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 40px rgba(0,0,0,0.16);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.fab-popup-enter-active,
.fab-popup-leave-active {
  transition: all 0.25s ease;
}
.fab-popup-enter-from,
.fab-popup-leave-to {
  opacity: 0;
  transform: translateY(12px) scale(0.95);
}

/* Header */
.fab-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid #F1F5F9;
  gap: 8px;
  flex-shrink: 0;
}
.fab-h-left { display: flex; align-items: center; gap: 8px; min-width: 0; }
.fab-h-dot {
  width: 8px; height: 8px; border-radius: 50%; background: #EF4444; flex-shrink: 0;
}
.fab-h-dot.online { background: #22C55E; }
.fab-h-title { font-size: 14px; font-weight: 700; color: #0F172A; }
.fab-h-model { font-size: 11px; color: #94A3B8; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.fab-h-right { display: flex; align-items: center; gap: 6px; flex-shrink: 0; }
.fab-model-select {
  font-size: 11px; padding: 4px 6px; border: 1px solid #E2E8F0; border-radius: 6px;
  background: #F8FAFC; color: #475569; font-family: inherit; min-width: 100px;
  cursor: pointer;
}
.fab-btn-clear {
  width: 28px; height: 28px; border: none; border-radius: 8px;
  background: transparent; color: #94A3B8; font-size: 14px; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
}
.fab-btn-clear:hover { background: #FEF2F2; color: #DC2626; }
.fab-btn-close {
  width: 28px; height: 28px; border: none; border-radius: 8px;
  background: transparent; color: #94A3B8; font-size: 18px; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
}
.fab-btn-close:hover { background: #F1F5F9; color: #0F172A; }

/* Messages */
.fab-messages {
  flex: 1;
  overflow-y: auto;
  padding: 12px 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.fab-empty { text-align: center; padding: 30px 16px; }
.fab-empty-icon { font-size: 36px; margin-bottom: 8px; }
.fab-empty p { font-size: 13px; color: #94A3B8; margin: 0 0 12px; }
.fab-suggestions { display: flex; flex-wrap: wrap; gap: 6px; justify-content: center; }
.fab-sugg {
  padding: 6px 12px; border: 1px solid #E2E8F0; border-radius: 20px;
  background: #F8FAFC; color: #475569; font-size: 12px; cursor: pointer; font-family: inherit;
  transition: all 0.15s;
}
.fab-sugg:hover { background: #EEF2FF; border-color: #C4B5FD; color: #4F46E5; }

.fab-msg { max-width: 90%; display: flex; }
.fab-msg-user { align-self: flex-end; }
.fab-msg-assistant { align-self: flex-start; }
.fab-msg-text {
  padding: 8px 14px; border-radius: 14px; font-size: 13px; line-height: 1.55;
  white-space: pre-wrap; word-break: break-word;
}
.fab-msg-user .fab-msg-text { background: #6366F1; color: #fff; border-bottom-right-radius: 4px; }
.fab-msg-assistant .fab-msg-text { background: #F1F5F9; color: #1E293B; border-bottom-left-radius: 4px; }

/* Typing */
.fab-typing { display: flex; gap: 4px; padding: 4px 0; }
.fab-typing span {
  width: 6px; height: 6px; border-radius: 50%; background: #94A3B8;
  animation: fab-typing 1.4s infinite ease-in-out both;
}
.fab-typing span:nth-child(2) { animation-delay: 0.16s; }
.fab-typing span:nth-child(3) { animation-delay: 0.32s; }
@keyframes fab-typing {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.4; }
  40% { transform: scale(1); opacity: 1; }
}

/* Input */
.fab-input-row {
  display: flex; align-items: flex-end; gap: 8px;
  padding: 12px 16px; border-top: 1px solid #F1F5F9; flex-shrink: 0;
}
.fab-input {
  flex: 1; padding: 8px 12px; border: 1px solid #E2E8F0; border-radius: 10px;
  font-size: 13px; font-family: inherit; resize: none; outline: none;
  max-height: 100px; line-height: 1.4;
}
.fab-input:focus { border-color: #6366F1; box-shadow: 0 0 0 3px rgba(99,102,241,0.08); }
.fab-btn-send {
  width: 36px; height: 36px; border: none; border-radius: 10px;
  background: linear-gradient(135deg, #6366F1, #8B5CF6); color: #fff;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; flex-shrink: 0; transition: all 0.15s;
}
.fab-btn-send:hover { transform: scale(1.05); }
.fab-btn-send:disabled { opacity: 0.4; cursor: not-allowed; transform: none; }
</style>
