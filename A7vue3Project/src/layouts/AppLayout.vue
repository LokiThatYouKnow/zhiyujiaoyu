<template>
  <div class="app-shell">
    <AppSidebar
      :role="role"
      :nav-items="navItems"
      :active-tab="activeTab"
      :collapsed="collapsed"
      @nav="(key: string) => $emit('nav', key)"
      @toggle="collapsed = !collapsed"
    />
    <div class="app-main">
      <AppHeader
        :role="role"
        :title="headerTitle"
        :subtitle="headerSubtitle"
      />
      <main class="app-content">
        <slot />
      </main>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue'
import AppSidebar from '../components/layout/AppSidebar.vue'
import AppHeader from '../components/layout/AppHeader.vue'

interface NavItem {
  key: string
  label: string
  icon: string
  children?: NavItem[]
}

const props = defineProps<{
  role: 'teacher' | 'student' | 'admin'
  navItems: NavItem[]
  activeTab: string
}>()

defineEmits<{
  nav: [key: string]
}>()

const collapsed = ref(false)

const titles: Record<string, string> = {
  teacher: '教师工作台',
  student: '学习中心',
  admin: '管理控制台',
}
const subtitles: Record<string, string> = {
  teacher: '课程管理 · AI 教学 · 学情分析',
  student: '我的课程 · AI 助手 · 练习中心',
  admin: '数据大屏 · 用户管理 · 资源管理',
}

const headerTitle = computed(() => titles[props.role])
const headerSubtitle = computed(() => subtitles[props.role])
</script>

<style scoped>
.app-shell {
  display: flex;
  height: 100vh;
  overflow: hidden;
}
.app-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  background: #F1F5F9;
}
.app-content {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}
</style>
