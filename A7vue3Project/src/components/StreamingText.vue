<template>
  <div>
    <span v-if="!isStreaming" :class="['whitespace-pre-wrap', { 'think-bg': hasThinkTag }]" v-html="processedText"></span>
    <span v-else :class="['whitespace-pre-wrap', { 'think-bg': hasThinkTag }]">
      <template v-for="(paragraph, paraIndex) in splitText" :key="paraIndex">
        <span v-if="!paragraph.isThink" class="normal-paragraph">
          <span 
            v-for="(char, charIndex) in paragraph.text" 
            :key="charIndex" 
            class="char-appear"
            :style="{ 
              animationDelay: `${Math.min((paraIndex * paragraph.text.length + charIndex) * 20, 500)}ms` 
            }"
            @animationend="handleCharAnimationEnd" 
          >
            {{ char }}
          </span>
        </span>
        <span v-else class="think-paragraph">
          <span 
            v-for="(char, charIndex) in paragraph.text" 
            :key="charIndex" 
            class="char-appear"
            :style="{ 
              animationDelay: `${Math.min((paraIndex * paragraph.text.length + charIndex) * 20, 500)}ms` 
            }"
            @animationend="handleCharAnimationEnd" 
          >
            {{ char }}
          </span>
        </span>
      </template>
      <span v-if="isStreaming" class="typing-indicator ml-2">
        <span></span>
        <span></span>
        <span></span>
      </span>
    </span>
  </div>
</template>

<script setup lang="ts">
import { computed, inject, ref, watch } from 'vue';

const props = defineProps({
  text: { type: String, default: '' },
  isStreaming: { type: Boolean, default: false }
});

const hasThinkTag = computed(() => 
  props.text.includes('<think>') || props.text.includes('</think>')
);

const processedText = computed(() => {
  return props.text
    .replace(/data:/g, '')
    .replace(/\n/g, '')
    .replace(/<think>/g, '思考中......    \n\n ')
    .replace(/<\/think>/g, '\n\n\n');
});

const splitText = computed(() => {
  if (!props.isStreaming) return [];
  const text = processedText.value;
  const paragraphs: { text: string; isThink?: boolean }[] = [];
  const thinkMarker = '<think>';
  const thinkWord = '思考中......    \n\n ';
  const parts = text.split(thinkMarker);
  parts.forEach((part, index) => {
    if (index === 0) paragraphs.push({ text: part, isThink: false });
    else if (index > 0 && index < parts.length - 1) {
      paragraphs.push({ text: thinkWord, isThink: true });
      paragraphs.push({ text: part , isThink: false });
    } else if (index === parts.length - 1) {
      paragraphs.push({ text: part + '\n\n\n', isThink: false });
    }
  });
  return paragraphs;
});

// 计数变量
const totalChars = ref(0);
const completedChars = ref(0);

// 监听流式状态，重置计数
watch(
  () => props.isStreaming, // 使用箭头函数返回 props.isStreaming
  (newIsStreaming) => {
    if (newIsStreaming) {
      totalChars.value = splitText.value.reduce((acc, para) => acc + para.text.length, 0);
      completedChars.value = 0;
    }
  }
);
type ScrollToBottom = () => void
const scrollToBottom = inject<ScrollToBottom>('scrollToBottom');
// 动画结束回调
const handleCharAnimationEnd = () => {
  completedChars.value++;
  if (completedChars.value === totalChars.value) {
    scrollToBottom?.() // 执行滚动
  }
};
</script>

<style scoped>
.char-appear {
  display: inline-block;
  opacity: 0;
  animation: char-appear 0.15s ease-out forwards;
}

@keyframes char-appear {
  from { opacity: 0; transform: translateY(1px); }
  to { opacity: 1; transform: translateY(0); }
}

.typing-indicator {
  display: inline-flex;
  align-items: center;
}

.typing-indicator span {
  height: 6px;
  width: 6px;
  margin: 0 1.5px;
  background-color: #165DFF;
  border-radius: 50%;
  display: inline-block;
  animation: typing-bounce 1.4s infinite ease-in-out both;
}

.typing-indicator span:nth-child(1) { animation-delay: -0.32s; }
.typing-indicator span:nth-child(2) { animation-delay: -0.16s; }

@keyframes typing-bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

.whitespace-pre-wrap {
  white-space: pre-wrap;
}

.think-bg {
  color: #333!important; 
  padding: 4px 12px;
  border-radius: 8px;
}
</style>