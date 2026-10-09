console.log('[App] main.ts starting...')

// ===== 启动时校验 token 是否过期 =====
;(async () => {
  // 优先从 sessionStorage 读取（标签页隔离），兼容 localStorage 旧数据
  const token = sessionStorage.getItem('token') || localStorage.getItem('token')
  if (token) {
    try {
      const { jwtDecode } = await import('jwt-decode')
      const decoded: any = jwtDecode(token)
      const now = Math.floor(Date.now() / 1000)
      if (!decoded.exp || decoded.exp <= now) {
        // Token 已过期，清除所有登录状态
        console.log('[App] token expired on startup, clearing')
        localStorage.clear()
        sessionStorage.clear()
      }
    } catch {
      // Token 格式无效，清除
      console.log('[App] invalid token on startup, clearing')
      localStorage.clear()
      sessionStorage.clear()
    }
  }
  // 如果没有 token，清除 isAuthenticated 标志
  if (!sessionStorage.getItem('token') && !localStorage.getItem('token')) {
    localStorage.removeItem('isAuthenticated')
    sessionStorage.removeItem('isAuthenticated')
  }
})()

// ===== Mock 模式（必须在所有 axios 调用之前初始化）=====
import { setupMock } from './mock'

// ===== 应用初始化 =====
import { createApp } from 'vue'
import './style.css'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
import router from './router'
import { createPinia } from 'pinia'
import AppRoot from './AppRoot.vue'

// 初始化 Mock 模式（如果已启用）
setupMock()

const app = createApp(AppRoot)
app.use(Antd)
app.use(router)
app.use(createPinia())
console.log('[App] mounting to #Home...')
app.mount('#Home')
console.log('[App] mounted successfully')
