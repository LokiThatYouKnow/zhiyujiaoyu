<template>
  <!--
    Full-page loading: shown while route is resolving.
    Gates the ENTIRE AppLayout (sidebar included) so sidebar and content
    always appear together instead of sidebar rendering first then content
    flashing in a moment later.
  -->
  <div v-if="isLoginStore.isLogin && !isAppReady" class="app-init-loading">
    <div class="ail-spinner"></div>
    <span>加载中...</span>
  </div>

  <!-- Authenticated + route ready: Show layout + workspace -->
  <template v-else-if="isLoginStore.isLogin">
    <AppLayout
      :role="currentRole"
      :nav-items="navItems"
      :active-tab="activeTab"
      @nav="handleNav"
    >
      <RouterView />
    </AppLayout>
    <!-- Global floating AI chat ball -->
    <FloatingAiBall />
  </template>

  <!-- Not authenticated: Show landing -->
  <template v-else>
    <Landing />
  </template>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useIsLogin } from './usePinia/isLogin'
import { jwtDecode } from 'jwt-decode'
import AppLayout from './layouts/AppLayout.vue'
import FloatingAiBall from './components/common/FloatingAiBall.vue'
import Landing from './views/Landing.vue'

const isLoginStore = useIsLogin()
const router = useRouter()

// 应用就绪状态：控制整个 AppLayout（含侧边栏）的显示时机。
// 只有当路由已确认跳到正确的角色页面时才显示，确保侧边栏和右侧内容同时出现，避免闪屏。
const isAppReady = ref(false)

// 场景1：页面刷新 — router.isReady() 等待初始导航（含 beforeEach 重定向 + 懒加载组件）
router.isReady().then(() => {
  if (isLoginStore.isLogin && router.currentRoute.value.name === 'home') {
    // 已登录但路由仍停在首页 → beforeEach 尚未完成重定向，持续监听直到跳走
    const stop = watch(
      () => router.currentRoute.value.name,
      (name) => {
        if (name && name !== 'home') {
          isAppReady.value = true
          stop()
        }
      }
    )
    // 安全兜底：2秒后仍未跳转则强制显示（避免永久白屏）
    setTimeout(() => {
      if (!isAppReady.value) { isAppReady.value = true; stop() }
    }, 2000)
  } else {
    isAppReady.value = true
  }
  // 从 URL query 参数读取初始 tab（如 /student?tab=profile）
  const tabParam = router.currentRoute.value.query.tab as string | undefined
  if (tabParam && navItems.value.some(n => n.key === tabParam)) {
    activeTab.value = tabParam
    setTimeout(() => {
      window.dispatchEvent(new CustomEvent('workspace-nav', { detail: tabParam }))
    }, 100)
  }
}).catch(() => {
  // router.isReady() 异常时强制显示（避免永久卡在加载动画）
  console.error('[AppRoot] router.isReady() failed, forcing ready state')
  isAppReady.value = true
})

// 全局安全兜底：3 秒后无论任何情况都强制显示（避免永久白屏）
setTimeout(() => {
  if (!isAppReady.value) { isAppReady.value = true }
}, 3000)

// 场景2：登录后跳转 / 程序化导航 — afterEach 在每次导航完成后触发
router.afterEach((to) => {
  if (to.name && to.name !== 'home') {
    isAppReady.value = true
  }
  // 处理登录后的 ?tab=xxx 参数
  const tabParam = to.query.tab as string | undefined
  if (tabParam && navItems.value.some(n => n.key === tabParam)) {
    activeTab.value = tabParam
    setTimeout(() => {
      window.dispatchEvent(new CustomEvent('workspace-nav', { detail: tabParam }))
    }, 100)
  }
})

// 使用 ref 而非 computed，因为 localStorage 不是响应式的
// 通过 watch 监听登录状态变化来同步角色
const currentRole = ref<'teacher' | 'student' | 'admin'>('student')

// 是否为超级管理员（从 JWT 解析，控制"管理员管理"导航显示）
const isSuperAdmin = ref(false)

function syncRoleFromToken() {
  const token = sessionStorage.getItem('token') || localStorage.getItem('token')
  if (!token) { currentRole.value = 'student'; isSuperAdmin.value = false; return }
  try {
    const decoded: any = jwtDecode(token)
    const r = decoded?.role
    if (r === 'TEACHER') currentRole.value = 'teacher'
    else if (r === 'ADMIN') currentRole.value = 'admin'
    else currentRole.value = 'student'
    // 解析超级管理员标识
    const isSA = decoded?.isSuperAdmin
    isSuperAdmin.value = isSA === true || isSA === 'true'
  } catch { currentRole.value = 'student'; isSuperAdmin.value = false }
}

// 初始化角色（页面刷新时从已有 token 读取，避免短暂显示错误角色）
syncRoleFromToken()

// 登录状态变化时同步角色 + 控制应用就绪状态
watch(() => isLoginStore.isLogin, (val) => {
  if (val) {
    syncRoleFromToken()
    // 刚登录：如果路由还在首页，标记为未就绪，等待 afterEach 在跳转完成后恢复
    if (!router.currentRoute.value.name || router.currentRoute.value.name === 'home') {
      isAppReady.value = false
    }
  } else {
    // 退出登录：重置就绪状态，为下次登录做准备
    isAppReady.value = false
  }
})

// 根据当前角色决定 sessionStorage key
const sidebarStorageKey = computed(() => `${currentRole.value}_active_tab`)

const activeTab = ref('profile')

// 从 sessionStorage 恢复上次停留的页面（与 workspace 共享同一 key）
function restoreSidebarTab() {
  const saved = sessionStorage.getItem(sidebarStorageKey.value)
  if (saved && navItems.value.some(n => n.key === saved)) {
    activeTab.value = saved
  }
}

// 挂载时恢复（使用 onMounted 确保 navItems 等已初始化）
onMounted(() => {
  restoreSidebarTab()
})

// activeTab 变化时持久化
watch(activeTab, (tab) => {
  sessionStorage.setItem(sidebarStorageKey.value, tab)
})

// 监听 workspace 内部 tab 切换，同步到侧边栏高亮
window.addEventListener('sync-sidebar', ((e: CustomEvent) => {
  const tab = e.detail
  if (tab && navItems.value.some(n => n.key === tab)) {
    activeTab.value = tab
  }
}) as EventListener)

// 角色变化时重新恢复（切换登录后）
watch(currentRole, () => {
  restoreSidebarTab()
})

// Navigation items per role
const teacherNavBase = [
  {
    key: 'profile', label: '个人中心',
    icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  },
  {
    key: 'students', label: '学生管理',
    icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  },
  {
    key: 'courses', label: '课程管理',
    icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M4 19.5A2.5 2.5 0 0 0 6.5 22H20V2H6.5A2.5 2.5 0 0 0 4 4.5v15z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  },
  {
    key: 'courseware', label: '课件管理',
    icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/><path d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z" stroke="currentColor" stroke-width="2"/></svg>',
  },
  {
    key: 'homework', label: '发布作业',
    icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8zM14 2v6h6M16 13H8M16 17H8M10 9H8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  },
  {
    key: 'exam', label: '发布考试',
    icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M9 11l3 3L22 4M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  },
  {
    key: 'practical-training', label: '实训计划',
    icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2M9 5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2M9 15l2 2 4-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  },
  {
    key: 'attendance', label: '考勤记录',
    icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2M9 5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v0a2 2 0 0 1-2 2h-2a2 2 0 0 1-2-2zM9 14l2 2 4-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  },
  {
    key: 'analytics', label: '教学分析',
    icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M18 20V10M12 20V4M6 20v-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  },
  {
    key: 'grading', label: '成绩管理',
    icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M9 11l3 3L22 4M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  },
  {
    key: 'question-bank', label: '题库积累',
    icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  },
  {
    key: 'notifications', label: '通知中心',
    icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9M13.73 21a2 2 0 0 1-3.46 0" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  },
  {
    key: 'templates', label: '模板复用',
    icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8zM14 2v6h6M12 11v6M9 14h6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  },
  {
    key: 'ai-tools', label: 'AI 教学工具',
    icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  },
  {
    key: 'discussions', label: '讨论区',
    icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  },
]

// 版本计数器：当 TeacherWorkspace 写入 teacherDetail 后递增，触发 computed 重新求值
const teacherDetailVersion = ref(0)
window.addEventListener('teacher-detail-updated', () => {
  teacherDetailVersion.value++
})

// 检测教师是否为班主任（通过 teacher_position 判断）
const isClassAdvisor = computed(() => {
  if (currentRole.value !== 'teacher') return false
  // 引用 teacherDetailVersion 使其成为响应式依赖
  void teacherDetailVersion.value
  try {
    const detail = JSON.parse((sessionStorage.getItem('teacherDetail') || localStorage.getItem('teacherDetail')) || '{}')
    return detail.teacher_position === '班主任'
  } catch { return false }
})

const teacherNav = computed(() => {
  if (!isClassAdvisor.value) return teacherNavBase
  // 在学生管理后插入班级管理
  const idx = teacherNavBase.findIndex(n => n.key === 'students')
  const items = [...teacherNavBase]
  items.splice(idx + 1, 0, {
    key: 'class-management', label: '班级管理',
    icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M3 21h18M3 10h18M5 6l7-3 7 3M4 10v11M20 10v11M8 14v4M12 14v4M16 14v4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  })
  return items
})

const studentNav = [
  {
    key: 'profile', label: '个人中心',
    icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  },
  {
    key: 'courses', label: '我的课程',
    icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  },
  {
    key: 'homework', label: '课程作业',
    icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8zM14 2v6h6M16 13H8M16 17H8M10 9H8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  },
  {
    key: 'exam', label: '课程考试',
    icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M9 11l3 3L22 4M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  },
  {
    key: 'attendance', label: '考勤记录',
    icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2M9 5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v0a2 2 0 0 1-2 2h-2a2 2 0 0 1-2-2zM9 14l2 2 4-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  },
  {
    key: 'notifications', label: '通知中心',
    icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9M13.73 21a2 2 0 0 1-3.46 0" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  },
  {
    key: 'tutor', label: 'AI 学习助手',
    icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  },
  {
    key: 'discussions', label: '讨论区',
    icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  },
]

const adminNav = computed(() => {
  const items = [
    {
      key: 'overview', label: '数据大屏',
      icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><rect x="3" y="3" width="7" height="7" rx="1" stroke="currentColor" stroke-width="2"/><rect x="14" y="3" width="7" height="7" rx="1" stroke="currentColor" stroke-width="2"/><rect x="3" y="14" width="7" height="7" rx="1" stroke="currentColor" stroke-width="2"/><rect x="14" y="14" width="7" height="7" rx="1" stroke="currentColor" stroke-width="2"/></svg>',
    },
    {
      key: 'users', label: '用户管理',
      icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8zM23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
    },
    {
      key: 'resources', label: '资源管理',
      icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
    },
    {
      key: 'analytics', label: '系统分析',
      icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M18 20V10M12 20V4M6 20v-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
    },
  ]
  // 仅超级管理员可见"管理员管理"
  if (isSuperAdmin.value) {
    items.push({
      key: 'admin-management',
      label: '管理员管理',
      icon: '<svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M12 15v-3m0 0V9m0 3H9m3 0h3M5.5 4.5h13a1 1 0 0 1 1 1v13a1 1 0 0 1-1 1h-13a1 1 0 0 1-1-1v-13a1 1 0 0 1 1-1z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
    })
  }
  return items
})

const navItems = computed(() => {
  if (currentRole.value === 'teacher') return teacherNav.value
  if (currentRole.value === 'admin') return adminNav.value
  return studentNav
})

function handleNav(key: string) {
  activeTab.value = key
  // 检测当前是否在子路由页面（如 /teacher/homework-publisher），
  // 若是则先跳回工作台基路由，否则 workspace-nav 事件无人监听
  const path = window.location.hash.replace('#', '')
  const baseRoute = currentRole.value === 'teacher' ? '/teacher'
    : currentRole.value === 'admin' ? '/admin' : '/student'
  if (path !== baseRoute && !path.startsWith(baseRoute + '?')) {
    router.push(`${baseRoute}?tab=${key}`)
    return
  }
  // Dispatch event so workspace components can listen
  window.dispatchEvent(new CustomEvent('workspace-nav', { detail: key }))
}
</script>

<style scoped>
/* 全屏加载占位：盖住整个视口，确保侧边栏和内容同时出现，消除闪屏 */
.app-init-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  width: 100vw;
  gap: 16px;
  color: #94A3B8;
  font-size: 14px;
  background: #F1F5F9;
}
.ail-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #E2E8F0;
  border-top-color: #6366F1;
  border-radius: 50%;
  animation: ail-spin 0.8s linear infinite;
}
@keyframes ail-spin {
  to { transform: rotate(360deg); }
}
</style>
