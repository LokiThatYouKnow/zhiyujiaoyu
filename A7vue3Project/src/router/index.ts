import { createRouter, createWebHashHistory } from 'vue-router'
import { message } from 'ant-design-vue'
import { jwtDecode } from 'jwt-decode'

const routes = [
  {
    path: '/',
    name: 'home',
    component: () => import('../views/Landing.vue'),
  },
  {
    path: '/teacher',
    name: 'teacher',
    component: () => import('../views/teacher/TeacherWorkspace.vue'),
    meta: { requiresAuth: true, role: 'TEACHER' },
  },
  {
    path: '/teacher/homework-publisher',
    name: 'homeworkPublisher',
    component: () => import('../views/teacher/publisher/HomeworkPublisherPage.vue'),
    meta: { requiresAuth: true, role: 'TEACHER' },
  },
  {
    path: '/teacher/exam-publisher',
    name: 'examPublisher',
    component: () => import('../views/teacher/publisher/ExamPublisherPage.vue'),
    meta: { requiresAuth: true, role: 'TEACHER' },
  },
  {
    path: '/teacher/courseware-publisher',
    name: 'coursewarePublisher',
    component: () => import('../views/teacher/publisher/CoursewarePublisherPage.vue'),
    meta: { requiresAuth: true, role: 'TEACHER' },
  },
  {
    path: '/teacher/grading',
    name: 'grading',
    component: () => import('../views/teacher/GradingPage.vue'),
    meta: { requiresAuth: true, role: 'TEACHER' },
  },
  {
    path: '/student',
    name: 'student',
    component: () => import('../views/student/StudentWorkspace.vue'),
    meta: { requiresAuth: true, role: 'STUDENT' },
  },
  {
    path: '/admin',
    name: 'admin',
    component: () => import('../views/admin/AdminConsole.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' },
  },
  {
    path: '/notFound',
    component: () => import('../views/notFound.vue'),
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/notFound',
  },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

// 全局前置守卫
router.beforeEach((to, _from, next) => {
  const token = sessionStorage.getItem('token') || localStorage.getItem('token')
  const isAuthenticated = sessionStorage.getItem('isAuthenticated') === 'true'

  interface CustomJwtPayload {
    role?: string
    exp?: number
  }

  let tokenValid = false
  let role: string | undefined

  if (token && isAuthenticated) {
    try {
      const decodeJwt = jwtDecode(token) as CustomJwtPayload
      const now = Math.floor(Date.now() / 1000)
      if (decodeJwt.exp && decodeJwt.exp > now) {
        tokenValid = true
        role = decodeJwt.role
      } else {
        localStorage.clear()
        sessionStorage.clear()
      }
    } catch {
      localStorage.clear()
      sessionStorage.clear()
    }
  } else if (isAuthenticated && !token) {
    localStorage.clear()
    sessionStorage.clear()
  }

  // 需要认证但 token 无效
  if (to.meta.requiresAuth && !tokenValid) {
    message.error('登录已过期，请重新登录', 3)
    next('/')
    return
  }

  // 已登录访问首页 → 按角色跳转到个人中心
  if (tokenValid && to.name === 'home') {
    if (role === 'STUDENT') next('/student?tab=profile')
    else if (role === 'TEACHER') next('/teacher?tab=profile')
    else if (role === 'ADMIN') next('/admin')
    else next('/')
    return
  }

  // restoreTabState() 会通过 sessionStorage 恢复上次停留的 tab，无需在此强制跳转
  // （移除该守卫可避免 router.replace({query:{}}) 清理 URL 时再次触发重定向）

  next()
})

export default router
