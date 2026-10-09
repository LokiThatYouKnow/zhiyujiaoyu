<template>
    <template v-if="useIsLogin().isLogin">
        <RouterView></RouterView>
        <MockIndicator />
    </template>

    <template v-else-if="useLogin().homeView_visible">
        <div class="landing-page">
            <!-- ===== Navigation Bar ===== -->
            <nav class="landing-nav">
                <div class="nav-inner">
                    <div class="nav-brand">
                        <div class="brand-icon">
                            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" width="28" height="28">
                                <path d="M12 2L2 7L12 12L22 7L12 2Z" fill="white" fill-opacity="0.9"/>
                                <path d="M2 17L12 22L22 17" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                                <path d="M2 12L12 17L22 12" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            </svg>
                        </div>
                        <div class="brand-text">
                            <span class="brand-name">智语教学</span>
                            <span class="brand-desc">大模型驱动的智能教育平台</span>
                        </div>
                    </div>
                    <div class="nav-actions">
                        <button class="nav-btn nav-btn-admin" @click="adminLogin">
                            <svg viewBox="0 0 24 24" fill="none" width="16" height="16" style="margin-right:6px">
                                <path d="M12 15l-3-3m0 0l3-3m-3 3h12M9 19H5a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2h4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            </svg>
                            管理
                        </button>
                        <button class="nav-btn nav-btn-login" @click="chooseEnter">
                            <svg viewBox="0 0 24 24" fill="none" width="16" height="16" style="margin-right:6px">
                                <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4M10 17l5-5-5-5M15 12H3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            </svg>
                            登录
                        </button>
                        <button class="nav-btn nav-btn-register" @click="chooseRegister">
                            <svg viewBox="0 0 24 24" fill="none" width="16" height="16" style="margin-right:6px">
                                <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2M8.5 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8zM20 8v6M23 11h-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            </svg>
                            注册
                        </button>
                    </div>
                </div>
            </nav>

            <!-- ===== Hero Section ===== -->
            <section class="hero-section">
                <!-- Animated background -->
                <div class="hero-bg">
                    <div class="hero-blob hero-blob-1"></div>
                    <div class="hero-blob hero-blob-2"></div>
                    <div class="hero-blob hero-blob-3"></div>
                    <div class="hero-grid"></div>
                </div>

                <div class="hero-content">
                    <div class="hero-badge animate-fade-in-up">
                        <span class="badge-dot"></span>
                        基于大模型的下一代智能教育平台
                    </div>
                    <h1 class="hero-title animate-fade-in-up" style="animation-delay: 0.1s">
                        大模型驱动的<br/>
                        <span class="hero-title-accent">智能教育平台</span>
                    </h1>
                    <p class="hero-desc animate-fade-in-up" style="animation-delay: 0.2s">
                        基于开源大模型构建的全场景教育智能体，为教师提供高效备课工具，<br/>
                        为学生提供个性化学习体验，全面提升教学质量与效率。
                    </p>
                    <div class="hero-actions animate-fade-in-up" style="animation-delay: 0.3s">
                        <button class="hero-btn hero-btn-student" @click="chooseEnter">
                            <svg viewBox="0 0 24 24" fill="none" width="20" height="20">
                                <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4M10 17l5-5-5-5M15 12H3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            </svg>
                            登录平台
                        </button>
                        <button class="hero-btn hero-btn-teacher" @click="chooseRegister">
                            <svg viewBox="0 0 24 24" fill="none" width="20" height="20">
                                <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2M8.5 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8zM20 8v6M23 11h-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            </svg>
                            注册账号
                        </button>
                    </div>
                    <!-- Stats row -->
                    <div class="hero-stats animate-fade-in-up" style="animation-delay: 0.4s">
                        <div class="stat-item">
                            <span class="stat-number">3+</span>
                            <span class="stat-label">用户角色</span>
                        </div>
                        <div class="stat-divider"></div>
                        <div class="stat-item">
                            <span class="stat-number">6</span>
                            <span class="stat-label">核心功能</span>
                        </div>
                        <div class="stat-divider"></div>
                        <div class="stat-item">
                            <span class="stat-number">24/7</span>
                            <span class="stat-label">智能服务</span>
                        </div>
                    </div>
                </div>

                <!-- Scroll indicator -->
                <div class="scroll-indicator animate-float">
                    <svg viewBox="0 0 24 24" width="20" height="20" fill="none">
                        <path d="M7 10l5 5 5-5" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                </div>
            </section>

            <!-- ===== Features Section ===== -->
            <section class="features-section">
                <div class="features-header">
                    <span class="section-badge">核心功能</span>
                    <h2 class="section-title">六大智能教育模块</h2>
                    <p class="section-desc">全方位覆盖教学场景，赋能每一位教师和学生</p>
                </div>

                <div class="features-grid">
                    <div class="feature-card" v-for="(feature, idx) in features" :key="idx"
                         :style="{ animationDelay: (idx * 0.08) + 's' }">
                        <div class="feature-icon" :style="{ background: feature.gradient }">
                            <component :is="feature.icon" />
                        </div>
                        <h3 class="feature-name">{{ feature.title }}</h3>
                        <p class="feature-desc">{{ feature.desc }}</p>
                        <ul class="feature-tags">
                            <li v-for="tag in feature.tags" :key="tag" class="feature-tag">{{ tag }}</li>
                        </ul>
                    </div>
                </div>
            </section>

            <!-- ===== Footer ===== -->
            <footer class="landing-footer">
                <div class="footer-inner">
                    <div class="footer-brand">
                        <span class="brand-name" style="font-size: 18px;">智语教学</span>
                        <span style="color: var(--color-text-tertiary); font-size: 13px;">大模型驱动的智能教育平台</span>
                    </div>
                    <p style="color: var(--color-text-tertiary); font-size: 13px;">
                        Created by Team Of Dream © {{ new Date().getFullYear() }}
                    </p>
                </div>
            </footer>

            <MockIndicator />
        </div>
    </template>

    <template v-if="useLogin().login_visible">
        <NewLogin></NewLogin>
    </template>

    <template v-if="useRegister().visible">
        <Register></Register>
    </template>
</template>

<script lang="ts" setup>
import { h } from 'vue';
import NewLogin from './components/NewLogin.vue';
import Register from './components/Register.vue';
import { useLogin } from './usePinia/login';
import { useRegister } from './usePinia/register';
import { useIsLogin } from './usePinia/isLogin';
import MockIndicator from './mock/MockIndicator.vue';

// SVG icon components for features
const KnowledgeIcon = () => h('svg', { viewBox: '0 0 24 24', fill: 'none', width: 28, height: 28, innerHTML: '<path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M4 19.5A2.5 2.5 0 0 0 6.5 22H20V2H6.5A2.5 2.5 0 0 0 4 4.5v15zM8 7h6M8 11h8M8 15h5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>' });
const PracticeIcon = () => h('svg', { viewBox: '0 0 24 24', fill: 'none', width: 28, height: 28, innerHTML: '<path d="M9 11l3 3L22 4M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>' });
const DataIcon = () => h('svg', { viewBox: '0 0 24 24', fill: 'none', width: 28, height: 28, innerHTML: '<path d="M18 20V10M12 20V4M6 20v-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>' });
const PlanIcon = () => h('svg', { viewBox: '0 0 24 24', fill: 'none', width: 28, height: 28, innerHTML: '<circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/><path d="M12 6v6l4 2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>' });
const QAIcon = () => h('svg', { viewBox: '0 0 24 24', fill: 'none', width: 28, height: 28, innerHTML: '<path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/><path d="M9.5 9h.01M14.5 9h.01M9.5 13h5" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>' });
const ResourceIcon = () => h('svg', { viewBox: '0 0 24 24', fill: 'none', width: 28, height: 28, innerHTML: '<path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>' });

const features = [
    {
        icon: KnowledgeIcon,
        title: '智能知识库构建',
        desc: '自动梳理课程大纲和文档中的知识点，构建结构化知识库，支持知识点关联和层级管理。',
        tags: ['知识点提取', '关联网络', '可视化展示'],
        gradient: 'linear-gradient(135deg, #165DFF, #3C7EFF)',
    },
    {
        icon: PracticeIcon,
        title: '智能练习与评测',
        desc: '根据教学内容自动生成练习题和考核题目，支持代码、文本等多种题型，提供精准的自动化评测。',
        tags: ['多题型生成', '自动批改', '错误定位'],
        gradient: 'linear-gradient(135deg, #722ED1, #9E7BFF)',
    },
    {
        icon: DataIcon,
        title: '智能数据分析',
        desc: '收集和分析教学与学习数据，生成多维度报表，帮助教师了解学生掌握情况，优化教学策略。',
        tags: ['多维度分析', '掌握评估', '教学建议'],
        gradient: 'linear-gradient(135deg, #00B42A, #36CFC9)',
    },
    {
        icon: PlanIcon,
        title: '教学环节规划',
        desc: '智能规划教学环节和时间分配，生成科学合理的教学进度安排，提高课堂效率。',
        tags: ['时间分配', '环节优化', '进度跟踪'],
        gradient: 'linear-gradient(135deg, #FF7D00, #FFB020)',
    },
    {
        icon: QAIcon,
        title: '智能问答助手',
        desc: '基于教学内容的智能问答系统，为学生提供24/7在线答疑，解答学习疑惑，拓展知识深度。',
        tags: ['深度解析', '多轮对话', '学习建议'],
        gradient: 'linear-gradient(135deg, #F53F3F, #F97979)',
    },
    {
        icon: ResourceIcon,
        title: '资源管理中心',
        desc: '集中管理教学资源，支持课件、练习、试题等内容的分类存储、检索和共享，提高资源利用率。',
        tags: ['分类标签', '智能检索', '批量导出'],
        gradient: 'linear-gradient(135deg, #0FC6C2, #089B97)',
    },
];

const chooseEnter = () => {
    useLogin().loginObeject = 'student';
    useLogin().login_visible = true;
    useLogin().homeView_visible = false;
};

const chooseRegister = () => {
    useRegister().loginObeject = 'student';
    useRegister().visible = true;
    useLogin().homeView_visible = false;
};

const adminLogin = () => {
    useLogin().loginObeject = 'admin';
    useLogin().login_visible = true;
    useLogin().homeView_visible = false;
};
</script>

<style scoped>
/* ===== CSS Variables — Book Theme ===== */
.landing-page {
    --book-bg: #F5F0E8;
    --book-page: #FAF7F2;
    --book-page-dark: #EDE4D3;
    --book-ink: #3D3226;
    --book-ink-light: #5C4B3A;
    --book-ink-muted: #8B7355;
    --book-accent: #8B5E3C;
    --book-accent-deep: #6B3A2A;
    --book-gold: #C4A35A;
    --book-gold-light: #D4C5A0;
    --book-border: #D4C5B2;
    --book-border-light: #E0D6C5;
    --book-shadow: rgba(61, 50, 38, 0.08);
    --book-shadow-lg: rgba(61, 50, 38, 0.15);
}

/* ===== Landing Page Container ===== */
.landing-page {
    min-height: 100vh;
    background: var(--book-bg);
    font-family: var(--font-family);
    overflow-x: hidden;
}

/* ===== Navigation Bar ===== */
.landing-nav {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 100;
    background: rgba(245, 240, 232, 0.85);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    border-bottom: 2px solid var(--book-gold-light);
    box-shadow: 0 1px 8px var(--book-shadow);
    transition: all var(--transition-base);
}

.nav-inner {
    max-width: 1280px;
    margin: 0 auto;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 32px;
    height: 60px;
}

.nav-brand {
    display: flex;
    align-items: center;
    gap: 12px;
}

.brand-icon {
    width: 40px;
    height: 40px;
    border-radius: 6px;
    background: linear-gradient(135deg, var(--book-accent), var(--book-accent-deep));
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4px 12px rgba(107, 58, 42, 0.25);
    border: 1.5px solid var(--book-gold);
}

.brand-text {
    display: flex;
    flex-direction: column;
}

.brand-name {
    font-size: 20px;
    font-weight: 700;
    color: var(--book-ink);
    line-height: 1.2;
}

.brand-desc {
    font-size: 11px;
    color: var(--book-ink-muted);
}

.nav-actions {
    display: flex;
    gap: 12px;
}

.nav-btn {
    display: inline-flex;
    align-items: center;
    padding: 8px 20px;
    border-radius: 4px;
    font-size: 14px;
    font-weight: 600;
    border: none;
    cursor: pointer;
    transition: all var(--transition-base);
    letter-spacing: 0.02em;
}

.nav-btn-login {
    background: transparent;
    color: var(--book-ink);
    border: 1.5px solid var(--book-border);
}

.nav-btn-login:hover {
    border-color: var(--book-accent);
    color: var(--book-accent);
    background: rgba(139, 94, 60, 0.06);
}

.nav-btn-admin {
    background: transparent;
    color: var(--book-ink-muted);
    border: 1px solid transparent;
    font-size: 13px;
}

.nav-btn-admin:hover {
    color: var(--book-ink-light);
    background: rgba(139, 94, 60, 0.04);
    border-color: var(--book-border-light);
}

.nav-btn-register {
    background: linear-gradient(135deg, var(--book-accent), var(--book-accent-deep));
    color: #FAF7F2;
    box-shadow: 0 2px 8px rgba(107, 58, 42, 0.25);
}

.nav-btn-register:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 14px rgba(107, 58, 42, 0.35);
}

/* ===== Hero Section ===== */
.hero-section {
    position: relative;
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    padding-top: 60px;
    background: linear-gradient(180deg,
        var(--book-bg) 0%,
        #EDE4D3 40%,
        #E8DCC8 70%,
        var(--book-bg) 100%);
}

/* Book spine decoration line */
.hero-section::before {
    content: '';
    position: absolute;
    left: 80px;
    top: 15%;
    bottom: 15%;
    width: 2px;
    background: linear-gradient(180deg,
        transparent,
        var(--book-gold-light) 20%,
        var(--book-gold) 50%,
        var(--book-gold-light) 80%,
        transparent);
    z-index: 2;
    opacity: 0.6;
}

.hero-section::after {
    content: '';
    position: absolute;
    right: 80px;
    top: 15%;
    bottom: 15%;
    width: 2px;
    background: linear-gradient(180deg,
        transparent,
        var(--book-gold-light) 20%,
        var(--book-gold) 50%,
        var(--book-gold-light) 80%,
        transparent);
    z-index: 2;
    opacity: 0.6;
}

.hero-bg {
    position: absolute;
    inset: 0;
    overflow: hidden;
}

.hero-blob {
    position: absolute;
    border-radius: 50%;
    filter: blur(100px);
    opacity: 0.35;
}

.hero-blob-1 {
    width: 600px;
    height: 600px;
    background: radial-gradient(circle, rgba(196, 163, 90, 0.2), transparent);
    top: -200px;
    right: -100px;
    animation: float 8s ease-in-out infinite;
}

.hero-blob-2 {
    width: 500px;
    height: 500px;
    background: radial-gradient(circle, rgba(139, 94, 60, 0.12), transparent);
    bottom: -150px;
    left: -100px;
    animation: float 10s ease-in-out infinite reverse;
}

.hero-blob-3 {
    width: 400px;
    height: 400px;
    background: radial-gradient(circle, rgba(212, 197, 165, 0.18), transparent);
    top: 40%;
    left: 50%;
    transform: translate(-50%, -50%);
    animation: float 12s ease-in-out infinite;
}

.hero-grid {
    position: absolute;
    inset: 0;
    background-image:
        linear-gradient(rgba(139, 115, 85, 0.06) 1px, transparent 1px),
        linear-gradient(90deg, rgba(139, 115, 85, 0.06) 1px, transparent 1px);
    background-size: 40px 40px;
    mask-image: radial-gradient(circle at 50% 50%, black 30%, transparent 70%);
}

.hero-content {
    position: relative;
    z-index: 10;
    text-align: center;
    max-width: 900px;
    padding: 0 32px;
}

.hero-badge {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 6px 18px;
    border-radius: 3px;
    background: rgba(196, 163, 90, 0.12);
    color: var(--book-accent);
    font-size: 13px;
    font-weight: 600;
    margin-bottom: 28px;
    border: 1px solid var(--book-gold-light);
    letter-spacing: 0.04em;
}

.badge-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: var(--book-gold);
    animation: pulse-glow 2s infinite;
}

.hero-title {
    font-size: clamp(2.8rem, 6vw, 4.5rem);
    font-weight: 800;
    line-height: 1.2;
    color: var(--book-ink);
    margin-bottom: 24px;
    letter-spacing: -0.01em;
}

.hero-title-accent {
    background: linear-gradient(135deg, var(--book-accent-deep), var(--book-gold));
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
}

.hero-desc {
    font-size: clamp(1rem, 2vw, 1.15rem);
    color: var(--book-ink-light);
    line-height: 1.8;
    margin-bottom: 40px;
    max-width: 650px;
    margin-left: auto;
    margin-right: auto;
}

.hero-actions {
    display: flex;
    gap: 16px;
    justify-content: center;
    flex-wrap: wrap;
    margin-bottom: 56px;
}

.hero-btn {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 14px 36px;
    border-radius: 4px;
    font-size: 16px;
    font-weight: 600;
    border: none;
    cursor: pointer;
    transition: all var(--transition-base);
    white-space: nowrap;
    letter-spacing: 0.03em;
    position: relative;
}

.hero-btn::after {
    content: '';
    position: absolute;
    bottom: 6px;
    left: 20%;
    right: 20%;
    height: 1px;
    background: transparent;
    transition: all 0.3s;
}

.hero-btn-student {
    background: linear-gradient(135deg, var(--book-accent-deep), #8B4513);
    color: #FAF7F2;
    box-shadow: 0 4px 20px rgba(107, 58, 42, 0.3);
    border: 1.5px solid rgba(196, 163, 90, 0.5);
}

.hero-btn-student:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 28px rgba(107, 58, 42, 0.4);
}

.hero-btn-student:hover::after {
    background: rgba(250, 247, 242, 0.4);
}

.hero-btn-teacher {
    background: var(--book-page);
    color: var(--book-accent-deep);
    border: 2px solid var(--book-gold);
    box-shadow: 0 4px 16px var(--book-shadow);
}

.hero-btn-teacher:hover {
    background: linear-gradient(135deg, var(--book-gold), var(--book-gold-light));
    color: var(--book-ink);
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(139, 94, 60, 0.2);
    border-color: transparent;
}

.hero-stats {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 48px;
}

.stat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
}

.stat-number {
    font-size: 28px;
    font-weight: 700;
    color: var(--book-accent-deep);
    font-family: Georgia, 'Times New Roman', serif;
}

.stat-label {
    font-size: 13px;
    color: var(--book-ink-muted);
    letter-spacing: 0.04em;
}

.stat-divider {
    width: 1px;
    height: 40px;
    background: var(--book-border-light);
}

.scroll-indicator {
    position: absolute;
    bottom: 32px;
    left: 50%;
    transform: translateX(-50%);
    color: var(--book-ink-muted);
    opacity: 0.4;
}

/* ===== Features Section ===== */
.features-section {
    padding: 80px 32px 100px;
    max-width: 1280px;
    margin: 0 auto;
    position: relative;
}

/* Page number decoration */
.features-section::before {
    content: '— 核心功能 —';
    display: block;
    text-align: center;
    font-size: 13px;
    color: var(--book-ink-muted);
    letter-spacing: 0.15em;
    margin-bottom: 48px;
    font-family: Georgia, 'Times New Roman', serif;
}

.features-header {
    text-align: center;
    margin-bottom: 56px;
}

.section-badge {
    display: inline-block;
    padding: 5px 16px;
    border-radius: 3px;
    background: rgba(196, 163, 90, 0.12);
    color: var(--book-accent);
    font-size: 12px;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.1em;
    margin-bottom: 16px;
    border: 1px solid var(--book-gold-light);
}

.section-title {
    font-size: clamp(2rem, 4vw, 2.8rem);
    font-weight: 700;
    color: var(--book-ink);
    margin-bottom: 12px;
    font-family: Georgia, 'Times New Roman', serif;
}

.section-desc {
    font-size: 16px;
    color: var(--book-ink-muted);
}

.features-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 28px;
}

@media (max-width: 1100px) {
    .features-grid {
        grid-template-columns: repeat(2, 1fr);
    }
    .hero-section::before,
    .hero-section::after {
        display: none;
    }
}

@media (max-width: 700px) {
    .features-grid {
        grid-template-columns: 1fr;
    }
    .hero-section::before,
    .hero-section::after {
        display: none;
    }
}

.feature-card {
    background: var(--book-page);
    border: 1px solid var(--book-border);
    border-radius: 4px;
    padding: 32px 28px 28px;
    transition: all var(--transition-base);
    animation: fadeInUp 0.5s ease forwards;
    opacity: 0;
    cursor: default;
    position: relative;
    overflow: hidden;
    box-shadow: 2px 3px 12px var(--book-shadow);
}

/* Bookmark ribbon */
.feature-card::before {
    content: '';
    position: absolute;
    top: -4px;
    right: 20px;
    width: 24px;
    height: 40px;
    background: linear-gradient(180deg, var(--book-gold) 0%, var(--book-gold-light) 100%);
    border-radius: 0 0 4px 4px;
    opacity: 0;
    transition: all 0.3s;
    box-shadow: 0 2px 6px rgba(139, 94, 60, 0.2);
}

.feature-card::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg, var(--book-accent-deep), var(--book-gold), var(--book-accent-deep));
    opacity: 0;
    transition: opacity 0.3s;
}

.feature-card:hover::before {
    opacity: 1;
}

.feature-card:hover::after {
    opacity: 1;
}

.feature-card:hover {
    transform: translateY(-6px);
    box-shadow: 4px 8px 28px var(--book-shadow-lg);
    border-color: var(--book-gold);
}

.feature-icon {
    width: 56px;
    height: 56px;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #FAF7F2;
    margin-bottom: 20px;
    box-shadow: 0 4px 12px rgba(107, 58, 42, 0.2);
}

.feature-name {
    font-size: 18px;
    font-weight: 700;
    color: var(--book-ink);
    margin-bottom: 10px;
    letter-spacing: 0.02em;
}

.feature-desc {
    font-size: 14px;
    color: var(--book-ink-light);
    line-height: 1.8;
    margin-bottom: 16px;
}

.feature-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
    list-style: none;
    padding: 0;
}

.feature-tag {
    display: inline-block;
    padding: 3px 10px;
    border-radius: 2px;
    font-size: 11px;
    font-weight: 500;
    color: var(--book-accent);
    background: rgba(139, 94, 60, 0.08);
    border: 1px solid rgba(212, 197, 176, 0.6);
    letter-spacing: 0.03em;
}

/* ===== Footer ===== */
.landing-footer {
    border-top: 2px solid var(--book-border);
    background: linear-gradient(180deg, var(--book-page-dark) 0%, #E8DCC8 100%);
}

.footer-inner {
    max-width: 1280px;
    margin: 0 auto;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 24px 32px;
}

.footer-brand {
    display: flex;
    align-items: center;
    gap: 16px;
}
</style>
