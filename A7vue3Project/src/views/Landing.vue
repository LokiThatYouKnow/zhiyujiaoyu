<template>
  <div class="landing">
    <!-- Nav -->
    <nav class="landing-nav">
      <div class="nav-inner">
        <div class="nav-brand">
          <div class="brand-icon">
            <svg viewBox="0 0 24 24" fill="none" width="24" height="24">
              <path d="M12 2L2 7L12 12L22 7L12 2Z" fill="white" fill-opacity="0.9"/>
              <path d="M2 17L12 22L22 17" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 12L12 17L22 12" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <span class="brand-name">智语教学</span>
        </div>
        <div class="nav-actions">
          <button class="btn-ghost" @click="adminLogin">管理入口</button>
          <button class="btn-outline" @click="openLogin">登录</button>
          <button class="btn-primary" @click="openRegister">注册</button>
        </div>
      </div>
    </nav>

    <!-- Hero -->
    <section class="hero">
      <div class="hero-bg">
        <div class="hero-blob hero-blob-1"></div>
        <div class="hero-blob hero-blob-2"></div>
        <div class="hero-grid"></div>
      </div>
      <div class="hero-content">
        <div class="hero-badge">
          <span class="badge-dot"></span>基于开源大模型的新一代智能教育平台
        </div>
        <h1 class="hero-title">
          让 AI 赋能<br/><span class="hero-title-grad">每一堂课</span>
        </h1>
        <p class="hero-desc">
          基于 Qwen3 大模型 + LangChain 构建的全场景教学智能体<br/>
          为教师减负，为学生增效，让教学更智慧
        </p>
        <div class="hero-actions">
          <button class="hero-btn hero-btn-primary" @click="openRegister">
            <svg viewBox="0 0 24 24" fill="none" width="20" height="20">
              <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2M8.5 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8zM20 8v6M23 11h-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            免费注册
          </button>
          <button class="hero-btn hero-btn-ghost" @click="openLogin">
            <svg viewBox="0 0 24 24" fill="none" width="20" height="20">
              <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4M10 17l5-5-5-5M15 12H3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            立即登录
          </button>
        </div>
        <div class="hero-stats">
          <div class="hero-stat"><span class="hs-val">3</span><span class="hs-lbl">用户角色</span></div>
          <div class="hero-div"></div>
          <div class="hero-stat"><span class="hs-val">6</span><span class="hs-lbl">核心功能</span></div>
          <div class="hero-div"></div>
          <div class="hero-stat"><span class="hs-val">24/7</span><span class="hs-lbl">智能服务</span></div>
        </div>
      </div>
    </section>

    <!-- Features -->
    <section class="features">
      <div class="section-header">
        <span class="section-badge">核心能力</span>
        <h2 class="section-title">六大智能教育模块</h2>
        <p class="section-desc">全方位覆盖教学场景，赋能教师与学生</p>
      </div>
      <div class="features-grid">
        <div v-for="(f, i) in features" :key="i" class="feature-card" :style="{ animationDelay: i * 0.08 + 's' }">
          <div class="fc-icon" :style="{ background: f.gradient }">
            <span v-html="f.icon"></span>
          </div>
          <h3 class="fc-title">{{ f.title }}</h3>
          <p class="fc-desc">{{ f.desc }}</p>
          <div class="fc-tags">
            <span v-for="t in f.tags" :key="t" class="fc-tag">{{ t }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="landing-footer">
      <div class="footer-inner">
        <div class="footer-brand">
          <span style="font-weight:700;color:#0F172A;">智语教学</span>
          <span style="color:#94A3B8;font-size:13px;">基于开源AI大模型的教学实训智能体软件</span>
        </div>
        <span style="color:#94A3B8;font-size:13px;">© {{ new Date().getFullYear() }} 智语教学 Team. All rights reserved.</span>
      </div>
    </footer>

    <!-- Login Modal Overlay -->
    <div v-if="showLogin" class="modal-overlay">
      <NewLogin @close="showLogin = false" @switch-to-register="showLogin = false; showRegister = true" />
    </div>
    <!-- Register Modal Overlay -->
    <div v-if="showRegister" class="modal-overlay">
      <Register @close="showRegister = false" @switch-to-login="showRegister = false; showLogin = true" />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import NewLogin from '../components/NewLogin.vue'
import Register from '../components/Register.vue'
import { useLogin } from '../usePinia/login'
import { useRegister } from '../usePinia/register'

const showLogin = ref(false)
const showRegister = ref(false)

function openLogin() {
  useLogin().loginObeject = 'student'
  showLogin.value = true
}
function openRegister() {
  useRegister().loginObeject = 'student'
  showRegister.value = true
}
function adminLogin() {
  useLogin().loginObeject = 'admin'
  showLogin.value = true
}

const features = [
  {
    icon: '<svg viewBox="0 0 24 24" fill="none" width="26" height="26"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M4 19.5A2.5 2.5 0 0 0 6.5 22H20V2H6.5A2.5 2.5 0 0 0 4 4.5v15zM8 7h6M8 11h8M8 15h5" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
    title: '智能知识库构建', desc: '自动梳理课程大纲和文档中的知识点，构建结构化知识库，支持知识点关联和层级管理。',
    tags: ['知识点提取', '关联网络', '可视化'],
    gradient: 'linear-gradient(135deg, #4F46E5, #6366F1)',
  },
  {
    icon: '<svg viewBox="0 0 24 24" fill="none" width="26" height="26"><path d="M9 11l3 3L22 4M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
    title: '智能练习与评测', desc: '根据教学内容自动生成练习题和考核题目，支持多种题型，提供精准的自动化评测。',
    tags: ['多题型生成', '自动批改', '错误定位'],
    gradient: 'linear-gradient(135deg, #7C3AED, #8B5CF6)',
  },
  {
    icon: '<svg viewBox="0 0 24 24" fill="none" width="26" height="26"><path d="M18 20V10M12 20V4M6 20v-6" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
    title: '智能数据分析', desc: '收集和分析教学与学习数据，生成多维度报表，帮助教师了解学生掌握情况。',
    tags: ['多维度分析', '掌握评估', '教学建议'],
    gradient: 'linear-gradient(135deg, #0D9488, #14B8A6)',
  },
  {
    icon: '<svg viewBox="0 0 24 24" fill="none" width="26" height="26"><circle cx="12" cy="12" r="10" stroke="white" stroke-width="2"/><path d="M12 6v6l4 2" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
    title: '教学环节规划', desc: '智能规划教学环节和时间分配，生成科学合理的教学进度安排，提高课堂效率。',
    tags: ['时间分配', '环节优化', '进度跟踪'],
    gradient: 'linear-gradient(135deg, #F59E0B, #F97316)',
  },
  {
    icon: '<svg viewBox="0 0 24 24" fill="none" width="26" height="26"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
    title: '智能问答助手', desc: '基于教学内容的智能问答系统，为学生提供全天候在线答疑，解答学习疑惑。',
    tags: ['深度解析', '多轮对话', '学习建议'],
    gradient: 'linear-gradient(135deg, #EF4444, #F87171)',
  },
  {
    icon: '<svg viewBox="0 0 24 24" fill="none" width="26" height="26"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
    title: '资源管理中心', desc: '集中管理教学资源，支持课件、练习、试题等内容的分类存储、检索和共享。',
    tags: ['分类标签', '智能检索', '批量导出'],
    gradient: 'linear-gradient(135deg, #06B6D4, #22D3EE)',
  },
]
</script>

<style scoped>
.landing { min-height: 100vh; background: #F8FAFC; font-family: inherit; }

/* Nav */
.landing-nav { position: fixed; top: 0; left: 0; right: 0; z-index: 100; background: rgba(255,255,255,0.9); backdrop-filter: blur(20px); border-bottom: 1px solid #E2E8F0; }
.nav-inner { max-width: 1280px; margin: 0 auto; display: flex; align-items: center; justify-content: space-between; padding: 0 32px; height: 64px; }
.nav-brand { display: flex; align-items: center; gap: 12px; }
.brand-icon { width: 36px; height: 36px; border-radius: 8px; background: linear-gradient(135deg, #4F46E5, #7C3AED); display: flex; align-items: center; justify-content: center; }
.brand-name { font-size: 18px; font-weight: 700; color: #0F172A; }
.nav-actions { display: flex; gap: 10px; }
.btn-ghost { padding: 8px 16px; border: none; background: none; color: #64748B; font-size: 14px; font-weight: 500; cursor: pointer; border-radius: 8px; transition: all 0.2s; font-family: inherit; }
.btn-ghost:hover { background: #F1F5F9; color: #334155; }
.btn-outline { padding: 8px 20px; border: 1.5px solid #E2E8F0; background: #fff; color: #334155; font-size: 14px; font-weight: 600; cursor: pointer; border-radius: 8px; transition: all 0.2s; font-family: inherit; }
.btn-outline:hover { border-color: #4F46E5; color: #4F46E5; }
.btn-primary { padding: 8px 20px; border: none; background: linear-gradient(135deg, #4F46E5, #6366F1); color: #fff; font-size: 14px; font-weight: 600; cursor: pointer; border-radius: 8px; transition: all 0.2s; font-family: inherit; box-shadow: 0 2px 8px rgba(79,70,229,0.3); }
.btn-primary:hover { transform: translateY(-1px); box-shadow: 0 4px 16px rgba(79,70,229,0.4); }

/* Hero */
.hero { position: relative; min-height: 100vh; display: flex; align-items: center; justify-content: center; overflow: hidden; padding-top: 64px; }
.hero-bg { position: absolute; inset: 0; }
.hero-blob { position: absolute; border-radius: 50%; filter: blur(120px); opacity: 0.3; }
.hero-blob-1 { width: 600px; height: 600px; background: radial-gradient(circle, rgba(79,70,229,0.2), transparent); top: -200px; right: -100px; animation: float 8s ease-in-out infinite; }
.hero-blob-2 { width: 500px; height: 500px; background: radial-gradient(circle, rgba(124,58,237,0.15), transparent); bottom: -150px; left: -100px; animation: float 10s ease-in-out infinite reverse; }
.hero-grid { position: absolute; inset: 0; background-image: linear-gradient(rgba(0,0,0,0.03) 1px, transparent 1px), linear-gradient(90deg, rgba(0,0,0,0.03) 1px, transparent 1px); background-size: 48px 48px; mask-image: radial-gradient(circle at 50% 50%, black 30%, transparent 70%); }
@keyframes float { 0%, 100% { transform: translate(0, 0); } 50% { transform: translate(30px, -30px); } }

.hero-content { position: relative; z-index: 10; text-align: center; max-width: 800px; padding: 0 32px; }
.hero-badge { display: inline-flex; align-items: center; gap: 8px; padding: 6px 18px; border-radius: 999px; background: rgba(79,70,229,0.08); color: #4F46E5; font-size: 13px; font-weight: 600; margin-bottom: 24px; border: 1px solid rgba(79,70,229,0.15); }
.badge-dot { width: 6px; height: 6px; border-radius: 50%; background: #4F46E5; animation: pulse 2s infinite; }
@keyframes pulse { 0%, 100% { opacity: 1; } 50% { opacity: 0.3; } }

.hero-title { font-size: clamp(2.4rem, 5vw, 3.8rem); font-weight: 800; color: #0F172A; line-height: 1.15; margin-bottom: 20px; letter-spacing: -0.02em; }
.hero-title-grad { background: linear-gradient(135deg, #4F46E5, #7C3AED, #0D9488); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; }
.hero-desc { font-size: clamp(1rem, 1.5vw, 1.1rem); color: #64748B; line-height: 1.8; margin-bottom: 36px; }
.hero-actions { display: flex; gap: 14px; justify-content: center; flex-wrap: wrap; margin-bottom: 48px; }
.hero-btn { display: inline-flex; align-items: center; gap: 8px; padding: 14px 32px; border-radius: 12px; font-size: 16px; font-weight: 600; border: none; cursor: pointer; transition: all 0.3s; font-family: inherit; }
.hero-btn-primary { background: linear-gradient(135deg, #4F46E5, #6366F1); color: #fff; box-shadow: 0 4px 20px rgba(79,70,229,0.35); }
.hero-btn-primary:hover { transform: translateY(-2px); box-shadow: 0 8px 30px rgba(79,70,229,0.45); }
.hero-btn-ghost { background: #fff; color: #334155; border: 2px solid #E2E8F0; }
.hero-btn-ghost:hover { border-color: #4F46E5; color: #4F46E5; }
.hero-stats { display: flex; align-items: center; justify-content: center; gap: 40px; }
.hero-stat { display: flex; flex-direction: column; align-items: center; gap: 2px; }
.hs-val { font-size: 26px; font-weight: 700; color: #4F46E5; }
.hs-lbl { font-size: 13px; color: #94A3B8; }
.hero-div { width: 1px; height: 36px; background: #E2E8F0; }

/* Features */
.features { padding: 80px 32px 100px; max-width: 1200px; margin: 0 auto; }
.section-header { text-align: center; margin-bottom: 56px; }
.section-badge { display: inline-block; padding: 5px 16px; border-radius: 999px; background: rgba(79,70,229,0.08); color: #4F46E5; font-size: 12px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.1em; margin-bottom: 16px; }
.section-title { font-size: clamp(1.8rem, 3vw, 2.4rem); font-weight: 700; color: #0F172A; margin-bottom: 8px; }
.section-desc { font-size: 16px; color: #94A3B8; }
.features-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; }
@media (max-width: 1100px) { .features-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 700px) { .features-grid { grid-template-columns: 1fr; } }

.feature-card { background: #fff; border: 1px solid #E2E8F0; border-radius: 16px; padding: 28px 24px 24px; transition: all 0.3s; animation: fadeInUp 0.5s ease forwards; opacity: 0; }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(16px); } to { opacity: 1; transform: translateY(0); } }
.feature-card:hover { transform: translateY(-4px); box-shadow: 0 12px 32px rgba(0,0,0,0.06); border-color: #CBD5E1; }
.fc-icon { width: 52px; height: 52px; border-radius: 14px; display: flex; align-items: center; justify-content: center; margin-bottom: 18px; }
.fc-title { font-size: 17px; font-weight: 700; color: #0F172A; margin-bottom: 8px; }
.fc-desc { font-size: 14px; color: #64748B; line-height: 1.7; margin-bottom: 14px; }
.fc-tags { display: flex; flex-wrap: wrap; gap: 6px; }
.fc-tag { padding: 3px 10px; border-radius: 6px; font-size: 11px; font-weight: 500; background: #F1F5F9; color: #64748B; }

/* Modal Overlay */
.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
  overflow-y: auto;
  overflow-x: hidden;
}

/* Footer */
.landing-footer { border-top: 1px solid #E2E8F0; background: #fff; }
.footer-inner { max-width: 1280px; margin: 0 auto; display: flex; align-items: center; justify-content: space-between; padding: 24px 32px; }
.footer-brand { display: flex; flex-direction: column; gap: 4px; }
</style>
