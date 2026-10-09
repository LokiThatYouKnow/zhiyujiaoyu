<template>
  <div class="chat-app">
    <!-- ===== Header ===== -->
    <header class="chat-header">
      <div class="header-left">
        <button class="btn-header" @click="backToHome">
          <svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M19 12H5M12 19l-7-7 7-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
          <span>返回</span>
        </button>
      </div>
      <div class="header-center">
        <div class="bot-avatar-sm">
          <img :src="botAvatar" alt="AI" />
        </div>
        <div class="header-info">
          <h2 class="header-title">{{ chatTitle }}</h2>
          <div class="header-status">
            <span class="status-dot" :class="{ online: isOnline }"></span>
            <span class="status-text">{{ isOnline ? '在线' : '离线' }}</span>
            <span class="header-course" v-if="currentKnowledge">
              <svg viewBox="0 0 24 24" fill="none" width="12" height="12"><path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2z" fill="currentColor"/></svg>
              {{ currentKnowledge }}
            </span>
            <span class="header-course no-course" v-else @click="shiftKnowledgePoint">
              未选择课程 — 点击选择
            </span>
          </div>
        </div>
      </div>
      <div class="header-right">
        <button class="btn-header btn-course" @click="shiftKnowledgePoint">
          <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M4 19.5A2.5 2.5 0 0 0 6.5 22H20V2H6.5A2.5 2.5 0 0 0 4 4.5v15z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
          <span>切换课程</span>
        </button>
      </div>
    </header>

    <!-- ===== Messages ===== -->
    <div ref="messagesContainer" class="messages-area" @scroll="handleScroll">
      <!-- No course selected hint -->
      <div class="no-course-banner" v-if="!currentKnowledge" @click="shiftKnowledgePoint">
        <div class="no-course-icon">
          <svg viewBox="0 0 24 24" fill="none" width="28" height="28"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M4 19.5A2.5 2.5 0 0 0 6.5 22H20V2H6.5A2.5 2.5 0 0 0 4 4.5v15z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
        </div>
        <div class="no-course-text">
          <strong>尚未选择咨询课程</strong>
          <span>点击此处或右上角「切换课程」选择一门课程，AI 将基于该课程知识为您答疑</span>
        </div>
        <div class="no-course-arrow">
          <svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M9 18l6-6-6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
        </div>
      </div>
      <div class="messages-list">
        <div v-for="(message, index) in messages" :key="(message.timestamp as any) || index"
          :class="['msg-row', message.from === 'user' ? 'msg-user' : message.from === 'system' ? 'msg-system' : 'msg-bot']">
          <!-- Bot avatar -->
          <div class="msg-avatar" v-if="message.from === 'bot'">
            <img :src="botAvatar" alt="AI" />
          </div>

          <!-- Message bubble -->
          <div class="msg-bubble">
            <div class="msg-bubble-inner">
              <span v-if="message.from === 'user'" class="msg-text">{{ message.content }}</span>
              <span v-else class="msg-text" v-html="message.content"></span>
            </div>
            <div class="msg-time">{{ formatTime((message.timestamp as Date)) }}</div>
          </div>

          <!-- User avatar -->
          <div class="msg-avatar" v-if="message.from === 'user'">
            <img :src="userAvatar" alt="我" />
          </div>
        </div>

        <!-- Typing indicator -->
        <div v-if="allStatus().student_AnswerQuestion_isSending" class="msg-row msg-bot">
          <div class="msg-avatar">
            <img :src="botAvatar" alt="AI" />
          </div>
          <div class="msg-bubble typing-bubble">
            <div class="typing-dots">
              <span></span><span></span><span></span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ===== Course Selector Modal ===== -->
    <ChooseKnowledgePoint></ChooseKnowledgePoint>

    <!-- ===== Input Area ===== -->
    <div class="input-area">
      <div class="input-card">
        <textarea
          v-model="allData().student_AnswerQuestion_inputText_data"
          placeholder="输入您的问题... (Ctrl+Enter 发送)"
          v-on:keydown.ctrl.enter="ctrlEnter"
          rows="1"
          class="input-textarea"
          ref="textareaRef"
          @input="autoResize"
        ></textarea>
        <button
          class="send-btn"
          :disabled="allStatus().student_AnswerQuestion_isSending || !allData().student_AnswerQuestion_inputText_data.trim()"
          @click="sendMessage"
          :title="allStatus().student_AnswerQuestion_isSending ? '发送中...' : '发送'"
        >
          <svg v-if="!allStatus().student_AnswerQuestion_isSending" viewBox="0 0 24 24" fill="none" width="20" height="20">
            <path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span v-else class="sending-spinner"></span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, reactive, watch } from 'vue';
import botImg from '../../assets/images/bot.png';
import userImg from '../../assets/images/user.png';
import type { R } from '../../response/R';
import { getCozeAnswerApi } from '../../api/student/getCozeAnswerApi';
import { allWait } from '../../usePinia/allWait';
import { allVisible } from '../../usePinia/allVisible';
import { convertToHtml } from '../../utils/toHTML';
import { allStatus } from '../../usePinia/allStatus';
import { allData } from '../../usePinia/allData';
import router from '../../routers/appRouters';

//@ts-ignore
const props = defineProps({
  chatTitle: { type: String, default: 'LOKI' },
  botAvatar: { type: String, default: botImg },
  userAvatar: { type: String, default: userImg }
});

type Message = {
  from: 'user' | 'bot' | 'system';
  content: string;
  isStreaming?: boolean;
  timestamp: Date;
};

const messages = ref<Message[]>(allData().student_AnswerQuestion_messages_data.length !== 0 ? allData().student_AnswerQuestion_messages_data : [
  {
    from: 'bot',
    content: '你好！我是你的智能助手 <strong>LOKI</strong>，有什么问题都可以向我提问哦。',
    timestamp: new Date()
  }
]);

if (allData().student_AnswerQuestion_messages_data.length === 0){
  allData().student_AnswerQuestion_messages_data = messages.value;
}else{
  messages.value = allData().student_AnswerQuestion_messages_data;
}

const isOnline = ref(true);
const messagesContainer = ref<HTMLElement | null>(null);
const textareaRef = ref<HTMLTextAreaElement | null>(null);
const shouldScrollToBottom = ref(true);
const currentKnowledge = ref('');

currentKnowledge.value = localStorage.getItem('knowledgePoint') as string;

watch(
  () => allWait().student_shift_knowledgePoint_wait,
  (newVal, oldVal) => {
    if (newVal > oldVal) {
      currentKnowledge.value = localStorage.getItem('knowledgePoint') as string;
      allWait().student_shift_knowledgePoint_wait = false;
    }
  },
  { flush: 'sync' }
);

onMounted(() => scrollToBottom());

const formatTime = (date: Date) => {
  if (!date) return '';
  const d = new Date(date);
  return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
};

const autoResize = () => {
  const el = textareaRef.value;
  if (el) {
    el.style.height = 'auto';
    el.style.height = Math.min(el.scrollHeight, 120) + 'px';
  }
};

const sendMessage = async () => {
  const requestMessage = reactive({
    input: '',
    data: currentKnowledge.value,
  });

  requestMessage.input = allData().student_AnswerQuestion_inputText_data.trim();
  if (!requestMessage.input || allStatus().student_AnswerQuestion_isSending) {
    if (!requestMessage.input) {
      showSystemMessage('请输入有效的消息内容');
    }
    return;
  }

  // If no course selected, show the course picker instead
  if (!currentKnowledge.value) {
    allVisible().student_choose_knowledgePoint_visible = true;
    showSystemMessage('请先选择一门课程后再提问');
    return;
  }

  const userMessage: Message = {
    from: 'user',
    content: requestMessage.input,
    timestamp: new Date()
  };
  allData().student_AnswerQuestion_messages_data.push(userMessage);

  allData().student_AnswerQuestion_inputText_data = '';
  // Reset textarea height
  if (textareaRef.value) { textareaRef.value.style.height = 'auto'; }

  scrollToBottom();

  const aiMessageIndex = allData().student_AnswerQuestion_messages_data.length;
  allData().student_AnswerQuestion_messages_data.push({
    from: 'bot',
    content: '',
    isStreaming: false,
    timestamp: new Date()
  });

  allStatus().student_AnswerQuestion_isSending = true;
  try {
    const R: R = (await getCozeAnswerApi(requestMessage)).data;
    allData().student_AnswerQuestion_messages_data[aiMessageIndex].content = convertToHtml(R.data[0].answerQuestion);
    await nextTick();
    if (shouldScrollToBottom.value) scrollToBottom();
  } catch (error) {
    console.error('请求错误:', error);
    handleError(error, aiMessageIndex);
  } finally {
    allStatus().student_AnswerQuestion_isSending = false;
  }
};

const handleError = (error: any, messageIndex: number) => {
  if (error instanceof Error) {
    allData().student_AnswerQuestion_messages_data[messageIndex].content = `<span style="color:#EF4444">发生错误: ${error.message}</span>`;
  } else {
    allData().student_AnswerQuestion_messages_data[messageIndex].content = '<span style="color:#EF4444">消息发送失败，请重试</span>';
  }
  allData().student_AnswerQuestion_messages_data[messageIndex].isStreaming = false;
};

const showSystemMessage = (content: string) => {
  allData().student_AnswerQuestion_messages_data.push({
    from: 'system',
    content,
    timestamp: new Date()
  });
  scrollToBottom();
};

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
    }
  });
};

const handleScroll = () => {
  const container = messagesContainer.value;
  if (!container) return;
  const threshold = 50;
  shouldScrollToBottom.value =
    container.scrollHeight - container.scrollTop - container.clientHeight < threshold;
};

const shiftKnowledgePoint = () => {
  allVisible().student_choose_knowledgePoint_visible = true;
};

const ctrlEnter = () => {
  if (allStatus().student_AnswerQuestion_isSending || !allData().student_AnswerQuestion_inputText_data.trim()) {
    return;
  } else {
    sendMessage();
  }
};

const backToHome = () => {
  router.push({ name: 'StudentHomeView' });
};
</script>

<style scoped>
/* ===== Chat App Container ===== */
.chat-app {
  display: flex; flex-direction: column;
  height: 100%;
  max-width: 900px;
  margin: 0 auto;
  background: #F8FAFC;
  overflow: hidden;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 2px 16px rgba(0,0,0,0.06);
  border: 1px solid #E2E8F0;
}

/* ===== Header ===== */
.chat-header {
  display: flex; align-items: center; gap: 16px;
  padding: 16px 24px;
  background: linear-gradient(145deg, #0D9488 0%, #14B8A6 50%, #06B6D4 100%);
  color: white;
  flex-shrink: 0;
}
.header-left, .header-right { flex-shrink: 0; }
.header-center {
  flex: 1;
  display: flex; align-items: center; gap: 12px;
  min-width: 0;
}
.btn-header {
  display: inline-flex; align-items: center; gap: 6px;
  background: rgba(255,255,255,0.15);
  backdrop-filter: blur(6px);
  border: 1px solid rgba(255,255,255,0.2);
  color: white;
  padding: 8px 16px;
  border-radius: 10px;
  cursor: pointer;
  font-size: 13px; font-weight: 600;
  transition: all 0.25s;
  white-space: nowrap;
}
.btn-header:hover { background: rgba(255,255,255,0.25); }
.bot-avatar-sm {
  width: 42px; height: 42px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
  background: rgba(255,255,255,0.2);
  border: 2px solid rgba(255,255,255,0.3);
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}
.bot-avatar-sm img { width: 100%; height: 100%; object-fit: cover; }
.header-info { min-width: 0; }
.header-title { font-size: 17px; font-weight: 700; margin: 0; line-height: 1.2; }
.header-status { display: flex; align-items: center; gap: 6px; margin-top: 2px; }
.status-dot {
  width: 8px; height: 8px;
  border-radius: 50%;
  background: #94A3B8;
  flex-shrink: 0;
}
.status-dot.online { background: #10B981; box-shadow: 0 0 6px rgba(16,185,129,0.5); }
.status-text { font-size: 11px; opacity: 0.8; }
.header-course {
  display: inline-flex; align-items: center; gap: 4px;
  font-size: 11px; opacity: 0.85;
  margin-left: 8px;
  background: rgba(255,255,255,0.15);
  padding: 2px 10px; border-radius: 10px;
}
.header-course.no-course {
  background: rgba(255,255,255,0.2);
  opacity: 1;
  cursor: pointer;
  animation: pulseHint 2s ease-in-out infinite;
  transition: background 0.25s;
}
.header-course.no-course:hover {
  background: rgba(255,255,255,0.35);
}
@keyframes pulseHint {
  0%, 100% { opacity: 0.7; }
  50% { opacity: 1; }
}

/* ===== No Course Banner ===== */
.no-course-banner {
  display: flex; align-items: center; gap: 16px;
  background: white;
  border: 1px dashed #0D9488;
  border-radius: 16px;
  padding: 18px 22px;
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}
.no-course-banner:hover {
  border-style: solid;
  background: linear-gradient(135deg, rgba(13,148,136,0.03), rgba(6,182,212,0.02));
  box-shadow: 0 4px 16px rgba(13,148,136,0.08);
  transform: translateY(-2px);
}
.no-course-icon {
  width: 52px; height: 52px;
  border-radius: 14px;
  background: linear-gradient(135deg, rgba(13,148,136,0.1), rgba(6,182,212,0.06));
  display: flex; align-items: center; justify-content: center;
  color: #0D9488;
  flex-shrink: 0;
}
.no-course-text {
  flex: 1; min-width: 0;
  display: flex; flex-direction: column; gap: 4px;
}
.no-course-text strong {
  font-size: 14px; color: #0F172A; font-weight: 700;
}
.no-course-text span {
  font-size: 12px; color: #94A3B8; line-height: 1.5;
}
.no-course-arrow {
  color: #0D9488;
  flex-shrink: 0;
  transition: transform 0.3s;
}
.no-course-banner:hover .no-course-arrow {
  transform: translateX(3px);
}

/* ===== Messages Area ===== */
.messages-area {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px;
  background:
    radial-gradient(ellipse at 50% 0%, rgba(13,148,136,0.02) 0%, transparent 60%),
    #F8FAFC;
}
.messages-list { display: flex; flex-direction: column; gap: 4px; }

/* Message rows */
.msg-row {
  display: flex; gap: 10px;
  max-width: 88%;
  margin-bottom: 16px;
  animation: msgIn 0.35s ease both;
}
@keyframes msgIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}
.msg-user { align-self: flex-end; flex-direction: row-reverse; }
.msg-bot { align-self: flex-start; }
.msg-system {
  align-self: center;
  max-width: 70%;
}
.msg-system .msg-bubble-inner {
  background: #F1F5F9;
  color: #64748B;
  font-size: 12px;
  font-style: italic;
  text-align: center;
  padding: 8px 16px;
}

/* Avatars */
.msg-avatar {
  width: 38px; height: 38px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
  margin-top: 4px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.06);
}
.msg-avatar img { width: 100%; height: 100%; object-fit: cover; }

/* Bubbles */
.msg-bubble { display: flex; flex-direction: column; }
.msg-bubble-inner {
  padding: 12px 18px;
  border-radius: 18px;
  font-size: 14px; line-height: 1.65;
  word-wrap: break-word;
  position: relative;
}
.msg-bot .msg-bubble-inner {
  background: white;
  color: #0F172A;
  border: 1px solid #E2E8F0;
  border-bottom-left-radius: 6px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
}
.msg-user .msg-bubble-inner {
  background: linear-gradient(135deg, #0D9488, #14B8A6);
  color: white;
  border-bottom-right-radius: 6px;
  box-shadow: 0 3px 12px rgba(13,148,136,0.2);
}
.msg-system .msg-bubble-inner {
  border-radius: 10px;
}
.msg-time {
  font-size: 10px; color: #94A3B8;
  margin-top: 4px; padding: 0 4px;
}
.msg-user .msg-time { text-align: right; }

/* Typing indicator */
.typing-bubble .msg-bubble-inner {
  padding: 14px 22px;
}
.typing-dots { display: flex; gap: 5px; }
.typing-dots span {
  width: 8px; height: 8px;
  border-radius: 50%;
  background: #CBD5E1;
  animation: typingBounce 1.3s infinite;
}
.typing-dots span:nth-child(2) { animation-delay: 0.15s; }
.typing-dots span:nth-child(3) { animation-delay: 0.3s; }
@keyframes typingBounce {
  0%, 60%, 100% { transform: translateY(0); }
  30% { transform: translateY(-8px); }
}

/* ===== Input Area ===== */
.input-area {
  padding: 16px 24px 20px;
  flex-shrink: 0;
  background: white;
  border-top: 1px solid #E2E8F0;
}
.input-card {
  display: flex; align-items: flex-end; gap: 12px;
  background: #F8FAFC;
  border: 1px solid #E2E8F0;
  border-radius: 16px;
  padding: 10px 12px 10px 18px;
  transition: border-color 0.3s, box-shadow 0.3s;
}
.input-card:focus-within {
  border-color: #0D9488;
  box-shadow: 0 0 0 3px rgba(13,148,136,0.1);
}
.input-textarea {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 14px; line-height: 1.5;
  resize: none;
  outline: none;
  color: #0F172A;
  min-height: 24px;
  max-height: 120px;
  font-family: inherit;
}
.input-textarea::placeholder { color: #CBD5E1; }
.send-btn {
  width: 42px; height: 42px;
  border-radius: 12px;
  border: none;
  background: linear-gradient(135deg, #0D9488, #14B8A6);
  color: white;
  cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
  transition: all 0.25s;
  box-shadow: 0 3px 10px rgba(13,148,136,0.25);
}
.send-btn:hover:not(:disabled) {
  transform: scale(1.05);
  box-shadow: 0 5px 16px rgba(13,148,136,0.35);
}
.send-btn:disabled {
  background: #CBD5E1;
  box-shadow: none;
  cursor: not-allowed;
}
.sending-spinner {
  width: 18px; height: 18px;
  border: 2px solid rgba(255,255,255,0.4);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* ===== Responsive ===== */
@media (max-width: 768px) {
  .chat-app { height: calc(100vh - 60px); border-radius: 0; }
  .chat-header { padding: 12px 16px; }
  .btn-header span { display: none; }
  .msg-row { max-width: 92%; }
  .messages-area { padding: 16px; }
  .input-area { padding: 12px 16px 16px; }
}
</style>
