<template>
  <div class="error-state">
    <div class="error-icon">
      <svg viewBox="0 0 24 24" fill="none" width="48" height="48">
        <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="1.5"/>
        <path d="M12 8v4M12 16h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
      </svg>
    </div>
    <h3 class="error-title">{{ title }}</h3>
    <p v-if="message" class="error-msg">{{ message }}</p>
    <button v-if="retry" class="error-retry" @click="$emit('retry')">
      <svg viewBox="0 0 24 24" fill="none" width="16" height="16">
        <path d="M1 4v6h6M23 20v-6h-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        <path d="M20.49 9A9 9 0 0 0 5.64 5.64L1 10m22 4l-4.64 4.36A9 9 0 0 1 3.51 15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      重试
    </button>
  </div>
</template>

<script lang="ts" setup>
withDefaults(defineProps<{
  title?: string
  message?: string
  retry?: boolean
}>(), {
  title: '加载失败',
  retry: true,
})

defineEmits<{ retry: [] }>()
</script>

<style scoped>
.error-state {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 48px 24px;
  text-align: center;
}
.error-icon { color: #F59E0B; margin-bottom: 16px; }
.error-title { font-size: 16px; font-weight: 600; color: #475569; margin-bottom: 8px; }
.error-msg { font-size: 14px; color: #94A3B8; max-width: 400px; margin-bottom: 20px; }
.error-retry {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 8px 20px;
  border-radius: 8px; border: 1px solid #E2E8F0;
  background: #fff; color: #475569;
  font-size: 14px; font-weight: 500; cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
}
.error-retry:hover { border-color: #CBD5E1; background: #F8FAFC; }
</style>
