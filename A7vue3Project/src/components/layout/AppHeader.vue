<template>
  <header class="app-header" :style="{ '--header-accent': themeColor }">
    <div class="header-left">
      <h1 class="header-title">{{ title }}</h1>
      <span v-if="subtitle" class="header-subtitle">{{ subtitle }}</span>
    </div>
    <div class="header-right">
      <!-- Role Badge -->
      <span class="role-badge" :style="{ background: themeBg, color: themeColor }">
        <span class="role-dot" :style="{ background: themeColor }"></span>
        {{ roleLabel }}
      </span>
      <!-- Logout -->
      <a-popconfirm title="确定要退出登录吗?" ok-text="确定" cancel-text="取消"
        @confirm="handleLogout" ok-type="danger" placement="bottomRight">
        <button class="logout-btn">
          <svg viewBox="0 0 24 24" fill="none" width="16" height="16">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4M16 17l5-5-5-5M21 12H9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          退出
        </button>
      </a-popconfirm>
    </div>
  </header>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { message } from 'ant-design-vue'
import { useIsLogin } from '../../usePinia/isLogin'
import { allData } from '../../usePinia/allData'
import { allStatus } from '../../usePinia/allStatus'
import { allVisible } from '../../usePinia/allVisible'
import { allWait } from '../../usePinia/allWait'
import router from '../../router'

const isLoginStore = useIsLogin()
const dataStore = allData()
const statusStore = allStatus()
const visibleStore = allVisible()
const waitStore = allWait()

const props = defineProps<{
  role: 'teacher' | 'student' | 'admin'
  title: string
  subtitle?: string
}>()

const themes = {
  teacher: { color: '#D97706', bg: 'rgba(245,158,11,0.1)', label: '教师端' },
  student: { color: '#059669', bg: 'rgba(16,185,129,0.1)', label: '学生端' },
  admin: { color: '#7C3AED', bg: 'rgba(124,58,237,0.1)', label: '管理端' },
} as const

const themeColor = computed(() => themes[props.role].color)
const themeBg = computed(() => themes[props.role].bg)
const roleLabel = computed(() => themes[props.role].label)

async function handleLogout() {
  const logoutApis: Record<string, () => Promise<any>> = {
    admin: () => import('../../api/loginOutApi').then(m => m.loginAdminOutApi({ token: sessionStorage.getItem('token') || localStorage.getItem('token') || '' })),
    student: () => import('../../api/loginOutApi').then(m => m.loginStudentOutApi({ token: sessionStorage.getItem('token') || localStorage.getItem('token') || '' })),
    teacher: () => import('../../api/loginOutApi').then(m => m.loginTeacherOutApi({ token: sessionStorage.getItem('token') || localStorage.getItem('token') || '' })),
  }
  try {
    if (logoutApis[props.role]) {
      const res = await logoutApis[props.role]()
      if (res.data?.code === 200) message.success('已退出')
    }
  } catch { /* ignore */ }
  localStorage.clear()
  sessionStorage.clear()
  // 重置所有 Pinia 状态，防止角色切换后残留旧端数据
  dataStore.$reset()
  statusStore.$reset()
  visibleStore.$reset()
  waitStore.$reset()
  isLoginStore.isLogin = false
  router.push('/')
}
</script>

<style scoped>
.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  padding: 0 28px;
  background: rgba(255,255,255,0.85);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-bottom: 1px solid rgba(0,0,0,0.06);
  z-index: 50;
  flex-shrink: 0;
}
.header-left { display: flex; align-items: center; gap: 12px; }
.header-title { font-size: 18px; font-weight: 700; color: #0F172A; }
.header-subtitle { font-size: 13px; color: #94A3B8; margin-left: 4px; }
.header-right { display: flex; align-items: center; gap: 12px; }
.role-badge {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 5px 14px; border-radius: 9999px;
  font-size: 12px; font-weight: 600;
}
.role-dot { width: 6px; height: 6px; border-radius: 50%; }
.logout-btn {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 5px 14px; border-radius: 9999px;
  font-size: 12px; font-weight: 600;
  background: rgba(239,68,68,0.1); color: #DC2626;
  border: none; cursor: pointer; font-family: inherit;
  transition: background 0.2s;
}
.logout-btn:hover { background: rgba(239,68,68,0.18); }
</style>
