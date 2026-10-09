<template>
  <div class="loading-skeleton" :style="{ width, height }">
    <div class="skeleton-shimmer"></div>
  </div>
</template>

<script lang="ts" setup>
withDefaults(defineProps<{
  width?: string
  height?: string
  lines?: number
}>(), {
  width: '100%',
  height: '200px',
})
</script>

<style scoped>
.loading-skeleton {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  background: #E2E8F0;
}
.skeleton-shimmer {
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, transparent 0%, rgba(255,255,255,0.5) 50%, transparent 100%);
  animation: shimmer 1.5s infinite;
}
@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}
</style>
