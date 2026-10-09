<template>
  <div v-if="visible" class="mock-indicator" @click="handleToggle" :title="isMock ? '点击关闭 Mock 模式' : '点击开启 Mock 模式'">
    <span class="mock-dot" :class="{ active: isMock }"></span>
    <span class="mock-label">{{ isMock ? 'Mock' : 'Real' }}</span>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { isMockEnabled, toggleMock } from '../mock/index'

const isMock = ref(false)
const visible = ref(false)

onMounted(() => {
  isMock.value = isMockEnabled()
  visible.value = true
})

function handleToggle() {
  toggleMock()
}
</script>

<style scoped>
.mock-indicator {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 99999;
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(0, 0, 0, 0.85);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 24px;
  padding: 8px 16px 8px 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.25);
  user-select: none;
}

.mock-indicator:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.35);
}

.mock-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #10B981;
  transition: all 0.3s ease;
  box-shadow: 0 0 8px rgba(16, 185, 129, 0.6);
}

.mock-dot.active {
  background: #F59E0B;
  box-shadow: 0 0 8px rgba(245, 158, 11, 0.6);
  animation: pulse-dot 2s ease-in-out infinite;
}

@keyframes pulse-dot {
  0%, 100% { box-shadow: 0 0 8px rgba(245, 158, 11, 0.6); }
  50% { box-shadow: 0 0 16px rgba(245, 158, 11, 0.9); }
}

.mock-label {
  color: rgba(255, 255, 255, 0.85);
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.5px;
}
</style>
