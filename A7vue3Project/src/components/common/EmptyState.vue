<template>
  <div class="empty-state">
    <div class="empty-icon">
      <svg viewBox="0 0 24 24" fill="none" width="48" height="48">
        <path d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
    </div>
    <h3 class="empty-title">{{ title }}</h3>
    <p v-if="description" class="empty-desc">{{ description }}</p>
    <slot />
  </div>
</template>

<script lang="ts" setup>
withDefaults(defineProps<{
  title?: string
  description?: string
}>(), {
  title: '暂无数据',
})
</script>

<style scoped>
.empty-state {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 48px 24px;
  text-align: center;
}
.empty-icon { color: #CBD5E1; margin-bottom: 16px; }
.empty-title { font-size: 16px; font-weight: 600; color: #64748B; margin-bottom: 8px; }
.empty-desc { font-size: 14px; color: #94A3B8; max-width: 320px; margin-bottom: 20px; }
</style>
