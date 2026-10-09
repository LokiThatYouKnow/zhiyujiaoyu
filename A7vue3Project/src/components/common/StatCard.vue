<template>
  <div class="stat-card" :class="{ clickable: !!$attrs.onClick }">
    <div class="stat-icon-wrap" :style="{ background: bg }">
      <span v-html="icon"></span>
    </div>
    <div class="stat-body">
      <span class="stat-value">{{ value }}</span>
      <span class="stat-label">{{ label }}</span>
    </div>
    <div v-if="trend !== undefined" class="stat-trend" :class="trend >= 0 ? 'up' : 'down'">
      <svg v-if="trend >= 0" viewBox="0 0 24 24" fill="none" width="14" height="14">
        <path d="M7 17L17 7M17 7H7M17 7V17" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <svg v-else viewBox="0 0 24 24" fill="none" width="14" height="14">
        <path d="M7 7L17 17M17 17H7M17 17V7" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      {{ Math.abs(trend) }}%
    </div>
  </div>
</template>

<script lang="ts" setup>
defineProps<{
  icon: string
  label: string
  value: string | number
  bg?: string
  trend?: number
}>()
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #E2E8F0;
  transition: all 0.2s;
  position: relative;
}
.stat-card.clickable { cursor: pointer; }
.stat-card:hover { box-shadow: 0 8px 24px rgba(0,0,0,0.06); border-color: #CBD5E1; transform: translateY(-1px); }
.stat-icon-wrap {
  width: 48px; height: 48px;
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.stat-icon-wrap :deep(svg) { width: 22px; height: 22px; }
.stat-body { display: flex; flex-direction: column; gap: 2px; }
.stat-value { font-size: 24px; font-weight: 700; color: #0F172A; line-height: 1.2; }
.stat-label { font-size: 13px; color: #64748B; }
.stat-trend {
  position: absolute;
  top: 16px; right: 16px;
  display: flex; align-items: center; gap: 2px;
  font-size: 12px; font-weight: 600;
}
.stat-trend.up { color: #10B981; }
.stat-trend.down { color: #EF4444; }
</style>
