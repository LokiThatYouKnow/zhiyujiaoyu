<template>
    <div class="login-page" :class="{ 'admin-theme': visibleType === 'admin' }">
        <!-- Back Button -->
        <button class="back-btn" @click="backToHomeView">
            <svg viewBox="0 0 24 24" fill="none" width="18" height="18">
                <path d="M19 12H5M12 19l-7-7 7-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            返回首页
        </button>

        <!-- Background Decoration -->
        <div class="login-bg">
            <div class="bg-blob bg-blob-1"></div>
            <div class="bg-blob bg-blob-2"></div>
            <div v-if="visibleType !== 'admin'" class="bg-blob bg-blob-3"></div>
            <div class="bg-grid"></div>
        </div>

        <!-- Center Content -->
        <div class="login-center">
            <!-- Brand -->
            <div class="login-brand animate-fade-in-up">
                <div class="brand-logo">
                    <svg viewBox="0 0 24 24" fill="none" width="32" height="32">
                        <path d="M12 2L2 7L12 12L22 7L12 2Z" fill="white" fill-opacity="0.9"/>
                        <path d="M2 17L12 22L22 17M2 12L12 17L22 12" stroke="white" stroke-width="2" stroke-linecap="round"/>
                    </svg>
                </div>
                <h1 class="brand-title">
                    <template v-if="visibleType === 'admin'">管理员登录</template>
                    <template v-else>智语教学平台</template>
                </h1>
                <p class="brand-subtitle">
                    <template v-if="visibleType === 'admin'">请输入账号和密码登录管理系统</template>
                    <template v-else>连接教师与学生的智能教育桥梁</template>
                </p>
            </div>

            <!-- Login Card - Unified single form -->
            <div class="login-card animate-fade-in-up" :class="visibleType !== 'admin' ? 'login-card-wide' : 'login-card-admin'" style="animation-delay: 0.1s">
                <!-- Form Body -->
                <div class="form-body">
                    <div class="form-unified">
                        <!-- Decorative side -->
                        <div class="form-decoration">
                            <div class="deco-icon-circle" :class="{ 'deco-icon-admin': visibleType === 'admin' }">
                                <svg v-if="visibleType === 'admin'" viewBox="0 0 24 24" fill="none" width="48" height="48">
                                    <path d="M12 15l-3-3m0 0l3-3m-3 3h12M9 19H5a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2h4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                                </svg>
                                <svg v-else viewBox="0 0 24 24" fill="none" width="48" height="48">
                                    <path d="M12 2L2 7L12 12L22 7L12 2Z" fill="currentColor" fill-opacity="0.15"/>
                                    <path d="M2 17L12 22L22 17M2 12L12 17L22 12" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                                </svg>
                            </div>
                            <h3>{{ visibleType === 'admin' ? '管理员入口' : '登录' }}</h3>
                        </div>

                        <!-- Input fields -->
                        <div class="form-inputs">
                            <div class="form-group">
                                <label class="form-label">账号</label>
                                <div class="input-wrapper">
                                    <svg class="input-icon" viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
                                    <input type="text" class="form-input" placeholder="请输入你的账号" v-model="loginRequest.username" @keyup.enter="focusPassword">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="form-label">密码</label>
                                <div class="input-wrapper">
                                    <svg class="input-icon" viewBox="0 0 24 24" fill="none" width="18" height="18"><rect x="3" y="11" width="18" height="11" rx="2" stroke="currentColor" stroke-width="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
                                    <input ref="passwordInput" type="password" class="form-input" placeholder="请输入你的密码" v-model="loginRequest.password" @keyup.enter="login()">
                                </div>
                            </div>
                            <!-- CAPTCHA -->
                            <div class="form-group">
                                <label class="form-label">验证码</label>
                                <div class="captcha-row">
                                    <div class="input-wrapper captcha-input-wrapper">
                                        <svg class="input-icon" viewBox="0 0 24 24" fill="none" width="18" height="18"><rect x="3" y="3" width="18" height="18" rx="2" stroke="currentColor" stroke-width="2"/><path d="M9 12h2M15 12h.01M10 16h4" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
                                        <input type="text" class="form-input captcha-input" placeholder="请输入验证码" maxlength="4" v-model="loginRequest.captchaCode" @keyup.enter="login()">
                                    </div>
                                    <div class="captcha-img-wrapper" @click="refreshCaptcha" title="点击刷新验证码">
                                        <img v-if="captchaImage" :src="captchaImage" alt="验证码" class="captcha-img" />
                                        <div v-else class="captcha-placeholder">
                                            <span>加载中...</span>
                                        </div>
                                        <div class="captcha-overlay">
                                            <svg viewBox="0 0 24 24" fill="none" width="14" height="14"><path d="M1 4v6h6M23 20v-6h-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/><path d="M20.49 9A9 9 0 0 0 5.64 5.64L1 10m22 4l-4.64 4.36A9 9 0 0 1 3.51 15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                                            <span>换一张</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <button type="button" class="submit-btn" @click="login()" :disabled="loading">
                                <template v-if="loading">
                                    <span class="btn-spinner"></span>
                                    登录中...
                                </template>
                                <template v-else>
                                    <template v-if="visibleType === 'admin'">登录管理系统</template>
                                    <template v-else>登录</template>
                                </template>
                            </button>
                            <p v-if="visibleType !== 'admin'" class="register-hint">
                                没有账号？
                                <span class="register-link" @click="handleRegister">点此注册</span>
                            </p>
                            <p v-else class="admin-hint">
                                <svg viewBox="0 0 24 24" fill="none" width="14" height="14" style="vertical-align: -2px;"><circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/><path d="M12 16v-4M12 8h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
                                仅供系统管理员使用
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Footer -->
        <div class="login-footer">
            <p>© {{ new Date().getFullYear() }} 智语教学平台 - 版权所有</p>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue';
import { useLogin } from '../usePinia/login';
import { useIsLogin } from '../usePinia/isLogin';
import { useNeedToWait } from '../usePinia/useNeedToWait';
import { message } from 'ant-design-vue';
import { loginApi, getCaptchaApi } from '../api/loginApi';
import type { R } from '../response/R';
import { jwtDecode } from 'jwt-decode';
import strictStrToBoolean from '../utils/strictStrToBoolean';
import router from '../router';
import { useRegister } from '../usePinia/register';

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'switchToRegister'): void
}>()

const visibleType = ref(useLogin().loginObeject);

const loginRequest = reactive({
    username: '',
    password: '',
    captchaCode: '',
});
const captchaKey = ref('');
const captchaImage = ref('');
const loading = ref(false);
const passwordInput = ref<HTMLInputElement | null>(null);

interface CustomJwtPayload {
    role?: string;
    exp?: number;
    sub?: string;
}

const focusPassword = () => {
    passwordInput.value?.focus();
};

const refreshCaptcha = async () => {
    try {
        const res = await getCaptchaApi();
        const data = res.data as R;
        if (data.code === 200 && data.data) {
            captchaKey.value = data.data.captchaKey;
            captchaImage.value = data.data.captchaImage;
        }
    } catch (error) {
        console.error('获取验证码失败', error);
    }
};

onMounted(() => {
    refreshCaptcha();
});

const login = async () => {
    if (loginRequest.username === '') { message.error('请填写账号', 2); return; }
    if (loginRequest.password === '') { message.error('请填写密码', 2); return; }
    if (loginRequest.captchaCode === '') { message.error('请填写验证码', 2); return; }

    if (useNeedToWait().useNeedToWait) { message.error('登录过于频繁，请稍后重试', 2); return; }

    loading.value = true;
    try {
        const R: R = (await loginApi({
            username: loginRequest.username,
            password: loginRequest.password,
            captchaKey: captchaKey.value,
            captchaCode: loginRequest.captchaCode,
        })).data;

        if (R.code === 200) {
            // 从 JWT 解析角色
            const token = R.data as string;
            let role = 'STUDENT';
            try {
                const decoded = jwtDecode(token) as CustomJwtPayload;
                role = decoded.role || 'STUDENT';
                // 持久化用户名到 sessionStorage（标签页隔离，避免多开窗口串号）
                const usernameFromToken = decoded.sub || '';
                if (usernameFromToken) {
                    sessionStorage.setItem('username', usernameFromToken);
                }
            } catch (e) {
                console.error('解析JWT失败', e);
            }

            // 使用 sessionStorage 而非 localStorage，避免多标签页之间 token 覆盖串号
            sessionStorage.setItem('token', token);
            sessionStorage.setItem('isAuthenticated', 'true');
            useIsLogin().isLogin = strictStrToBoolean(sessionStorage.getItem('isAuthenticated'));
            useLogin().login_visible = false;

            // 根据角色跳转（使用新路由的扁平路径，个人中心默认 tab 在各 Workspace 中设置）
            let pushPath = '/student?tab=profile';
            if (role === 'TEACHER') {
                pushPath = '/teacher?tab=profile';
            } else if (role === 'ADMIN') {
                pushPath = '/admin';
            }

            emit('close')
            router.push(pushPath);
            message.success(R.msg, 3);
        } else {
            sessionStorage.setItem('isAuthenticated', 'false');
            useIsLogin().isLogin = strictStrToBoolean(sessionStorage.getItem('isAuthenticated'));
            message.error(R.msg, 3);
            useNeedToWait().setWaitTimerBySecond(2);
            // 刷新验证码
            refreshCaptcha();
            loginRequest.captchaCode = '';
        }
    } catch (error) {
        console.error('登录失败', error);
        refreshCaptcha();
        loginRequest.captchaCode = '';
    } finally {
        loading.value = false;
    }
};

const backToHomeView = () => {
    emit('close')
};

const handleRegister = () => {
    useRegister().loginObeject = 'student';
    emit('switchToRegister')
};
</script>

<style scoped>
/* ===== Book Theme Variables ===== */
.login-page {
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

/* ===== Page Container ===== */
.login-page {
    min-height: 100vh;
    background: linear-gradient(170deg, #F5F0E8 0%, #EDE4D3 35%, #E8DCC8 65%, #F0EAE0 100%);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 40px 20px;
    position: relative;
    overflow: hidden;
    font-family: var(--font-family);
}

/* Book spine lines */
.login-page::before,
.login-page::after {
    content: '';
    position: fixed;
    top: 10%;
    bottom: 10%;
    width: 2px;
    z-index: 2;
    opacity: 0.4;
    background: linear-gradient(180deg, transparent, var(--book-gold-light) 20%, var(--book-gold) 50%, var(--book-gold-light) 80%, transparent);
}
.login-page::before { left: 60px; }
.login-page::after { right: 60px; }

/* ===== Admin Theme ===== */
.login-page.admin-theme {
    background: linear-gradient(170deg, #2A1F14 0%, #3D2B1A 35%, #2C1F12 65%, #35281B 100%);
}
.login-page.admin-theme::before,
.login-page.admin-theme::after {
    opacity: 0.25;
    background: linear-gradient(180deg, transparent, rgba(196, 163, 90, 0.5) 20%, rgba(196, 163, 90, 0.35) 50%, rgba(196, 163, 90, 0.5) 80%, transparent);
}

/* ===== Background Decoration ===== */
.login-bg { position: fixed; inset: 0; pointer-events: none; z-index: 0; }

.bg-blob {
    position: absolute;
    border-radius: 50%;
    filter: blur(100px);
    opacity: 0.25;
}

.bg-blob-1 {
    width: 400px; height: 400px;
    background: radial-gradient(circle, rgba(196, 163, 90, 0.18), transparent);
    top: -100px; right: -80px;
    animation: float 8s ease-in-out infinite;
}

.bg-blob-2 {
    width: 350px; height: 350px;
    background: radial-gradient(circle, rgba(139, 94, 60, 0.1), transparent);
    bottom: -80px; left: -60px;
    animation: float 10s ease-in-out infinite reverse;
}

.bg-blob-3 {
    width: 250px; height: 250px;
    background: radial-gradient(circle, rgba(139, 94, 60, 0.12), transparent);
    top: 50%; left: 50%;
    transform: translate(-50%, -50%);
    animation: float 12s ease-in-out infinite;
}

.admin-theme .bg-blob-1 { background: radial-gradient(circle, rgba(196, 163, 90, 0.08), transparent); }
.admin-theme .bg-blob-2 { background: radial-gradient(circle, rgba(139, 94, 60, 0.06), transparent); }
.admin-theme .bg-blob-3 { background: radial-gradient(circle, rgba(196, 163, 90, 0.05), transparent); }

.bg-grid {
    position: absolute; inset: 0;
    background-image:
        linear-gradient(rgba(139, 115, 85, 0.05) 1px, transparent 1px),
        linear-gradient(90deg, rgba(139, 115, 85, 0.05) 1px, transparent 1px);
    background-size: 30px 30px;
}
.admin-theme .bg-grid {
    background-image:
        linear-gradient(rgba(196, 163, 90, 0.04) 1px, transparent 1px),
        linear-gradient(90deg, rgba(196, 163, 90, 0.04) 1px, transparent 1px);
}

/* ===== Back Button ===== */
.back-btn {
    position: absolute; top: 24px; left: 24px; z-index: 20;
    display: inline-flex; align-items: center; gap: 6px;
    padding: 8px 18px;
    border-radius: 4px;
    border: 1px solid var(--book-border);
    background: rgba(245, 240, 232, 0.6);
    color: var(--book-ink-light);
    font-size: 14px; font-weight: 500; cursor: pointer;
    transition: all 0.3s;
    backdrop-filter: blur(10px);
}
.back-btn:hover { background: rgba(245, 240, 232, 0.9); transform: translateX(-3px); }

.admin-theme .back-btn {
    border-color: rgba(212, 197, 176, 0.3);
    background: rgba(42, 31, 20, 0.5);
    color: rgba(250, 247, 242, 0.8);
}
.admin-theme .back-btn:hover { background: rgba(42, 31, 20, 0.8); color: #FAF7F2; }

/* ===== Center Layout ===== */
.login-center { position: relative; z-index: 10; width: 100%; max-width: 500px; }

.login-brand { text-align: center; margin-bottom: 32px; color: var(--book-ink); }
.admin-theme .login-brand { color: #FAF7F2; }

.brand-logo {
    width: 56px; height: 56px; border-radius: 6px;
    background: linear-gradient(135deg, var(--book-accent), var(--book-accent-deep));
    display: flex; align-items: center; justify-content: center;
    margin: 0 auto 16px;
    border: 1.5px solid var(--book-gold);
    box-shadow: 0 4px 12px rgba(107, 58, 42, 0.25);
}
.admin-theme .brand-logo {
    background: linear-gradient(135deg, #4A3525, #2A1F14);
    border-color: rgba(196, 163, 90, 0.5);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.brand-title {
    font-size: 26px; font-weight: 700; margin-bottom: 6px;
    font-family: Georgia, 'Times New Roman', serif;
    letter-spacing: 0.02em;
}
.admin-theme .brand-title { text-shadow: 0 2px 4px rgba(0,0,0,0.3); }

.brand-subtitle { font-size: 14px; opacity: 0.75; }

/* ===== Login Card ===== */
.login-card {
    background: var(--book-page);
    border-radius: 4px;
    border: 1px solid var(--book-border);
    box-shadow: 2px 3px 16px var(--book-shadow);
    overflow: hidden;
    padding: 0;
}
.admin-theme .login-card {
    background: rgba(250, 247, 242, 0.06);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    border-color: rgba(212, 197, 176, 0.2);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
}

.login-card-wide { max-width: 750px; margin: 0 auto; }
.login-card-admin { max-width: 500px; margin: 0 auto; }

/* ===== Form Body ===== */
.form-body { padding: 32px 40px; }

.form-unified { display: flex; gap: 40px; align-items: center; }

.form-decoration {
    display: flex; flex-direction: column; align-items: center;
    text-align: center; color: var(--book-ink-light); flex-shrink: 0;
}
.admin-theme .form-decoration { color: rgba(250, 247, 242, 0.7); }

.deco-icon-circle {
    width: 100px; height: 100px; border-radius: 6px;
    background: rgba(139, 94, 60, 0.06);
    display: flex; align-items: center; justify-content: center;
    margin-bottom: 16px;
    border: 1.5px solid var(--book-border-light);
}
.admin-theme .deco-icon-circle,
.deco-icon-admin {
    background: rgba(196, 163, 90, 0.08);
    border-color: rgba(212, 197, 176, 0.2);
}

.form-decoration h3 { font-size: 18px; font-weight: 600; margin-bottom: 4px; }
.form-decoration p { font-size: 13px; opacity: 0.7; }

.form-inputs { flex: 1; }

/* ===== Form Elements ===== */
.form-group { margin-bottom: 18px; }

.form-label {
    display: block; font-size: 13px; font-weight: 600;
    color: var(--book-ink);
    margin-bottom: 6px;
}
.admin-theme .form-label { color: rgba(250, 247, 242, 0.9); }

.input-wrapper { position: relative; }

.input-icon {
    position: absolute; left: 14px; top: 50%;
    transform: translateY(-50%);
    color: var(--book-ink-muted);
    pointer-events: none;
    z-index: 1;
}
.admin-theme .input-icon { color: rgba(250, 247, 242, 0.4); }

.form-input {
    width: 100%;
    padding: 10px 14px 10px 42px;
    background: var(--book-page);
    border: 1px solid var(--book-border);
    border-radius: 4px;
    color: var(--book-ink);
    font-size: 14px; outline: none;
    transition: all 0.3s;
    box-sizing: border-box;
    line-height: 1.5;
}
.form-input::placeholder { color: var(--book-ink-muted); }
.form-input:focus {
    border-color: var(--book-accent);
    box-shadow: 0 0 0 3px rgba(139, 94, 60, 0.08);
    background: #FDFAF5;
}

.admin-theme .form-input {
    background: rgba(250, 247, 242, 0.06);
    border-color: rgba(212, 197, 176, 0.25);
    color: #FAF7F2;
}
.admin-theme .form-input::placeholder { color: rgba(250, 247, 242, 0.3); }
.admin-theme .form-input:focus {
    border-color: var(--book-gold);
    box-shadow: 0 0 0 3px rgba(196, 163, 90, 0.1);
    background: rgba(250, 247, 242, 0.1);
}

/* ===== CAPTCHA Row ===== */
.captcha-row {
    display: flex;
    gap: 12px;
    align-items: center;
}

.captcha-input-wrapper {
    flex: 1;
}

.captcha-input {
    padding-right: 14px;
}

.captcha-img-wrapper {
    position: relative;
    width: 130px;
    height: 41px;
    flex-shrink: 0;
    border-radius: 4px;
    overflow: hidden;
    cursor: pointer;
    border: 1px solid var(--book-border);
    background: rgba(139, 94, 60, 0.04);
    transition: all 0.3s;
}
.captcha-img-wrapper:hover {
    border-color: var(--book-accent);
    box-shadow: 0 0 0 3px rgba(139, 94, 60, 0.08);
}

.admin-theme .captcha-img-wrapper {
    border-color: rgba(212, 197, 176, 0.25);
    background: rgba(250, 247, 242, 0.06);
}
.admin-theme .captcha-img-wrapper:hover {
    border-color: var(--book-gold);
    box-shadow: 0 0 0 3px rgba(196, 163, 90, 0.1);
}

.captcha-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
}

.captcha-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    color: var(--book-ink-muted);
}

.captcha-overlay {
    position: absolute;
    inset: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 4px;
    background: rgba(0, 0, 0, 0);
    color: transparent;
    font-size: 12px;
    transition: all 0.3s;
}
.captcha-overlay svg { opacity: 0; transition: all 0.3s; }
.captcha-overlay span { opacity: 0; transition: all 0.3s; }

.captcha-img-wrapper:hover .captcha-overlay {
    background: rgba(0, 0, 0, 0.45);
    color: #fff;
}
.captcha-img-wrapper:hover .captcha-overlay svg,
.captcha-img-wrapper:hover .captcha-overlay span {
    opacity: 1;
}

/* ===== Submit Button ===== */
.submit-btn {
    width: 100%;
    display: inline-flex; align-items: center; justify-content: center; gap: 8px;
    padding: 14px;
    background: linear-gradient(135deg, var(--book-accent), var(--book-accent-deep));
    color: #FAF7F2;
    font-size: 15px; font-weight: 600; border: none;
    border-radius: 4px; cursor: pointer;
    transition: all 0.3s;
    margin-top: 8px;
    letter-spacing: 0.03em;
    border: 1.5px solid var(--book-gold);
}
.submit-btn:hover:not(:disabled) {
    transform: translateY(-1px);
    box-shadow: 0 4px 20px rgba(107, 58, 42, 0.3);
}
.submit-btn:disabled {
    opacity: 0.7;
    cursor: not-allowed;
}

.admin-theme .submit-btn {
    background: linear-gradient(135deg, var(--book-gold), #8B6914);
    color: #2A1F14;
    border-color: rgba(196, 163, 90, 0.6);
}
.admin-theme .submit-btn:hover:not(:disabled) { box-shadow: 0 4px 20px rgba(196, 163, 90, 0.25); }

/* Button spinner */
.btn-spinner {
    width: 16px; height: 16px;
    border: 2px solid rgba(255,255,255,0.3);
    border-top-color: #fff;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
}
.admin-theme .btn-spinner {
    border-color: rgba(42, 31, 20, 0.2);
    border-top-color: #2A1F14;
}

/* ===== Register Hint ===== */
.register-hint { text-align: center; margin-top: 16px; font-size: 13px; color: var(--book-ink-muted); }
.register-link {
    color: var(--book-accent-deep);
    font-weight: 600; cursor: pointer;
    text-decoration: underline; text-underline-offset: 2px;
}
.register-link:hover { color: var(--book-accent); }

.admin-theme .register-hint { color: rgba(250, 247, 242, 0.55); }
.admin-theme .register-link { color: var(--book-gold); }

/* ===== Admin Hint ===== */
.admin-hint {
    display: flex; align-items: center; justify-content: center; gap: 6px;
    text-align: center; margin-top: 16px; font-size: 12px;
    color: rgba(250, 247, 242, 0.4);
}

/* ===== Login Footer ===== */
.login-footer {
    position: relative; z-index: 10; margin-top: 32px;
    text-align: center; color: var(--book-ink-muted); font-size: 13px;
    opacity: 0.7;
}
.admin-theme .login-footer { color: rgba(250, 247, 242, 0.35); }

/* ===== Animations ===== */
@keyframes float {
    0%, 100% { transform: translateY(0); }
    50% { transform: translateY(-20px); }
}
@keyframes spin {
    to { transform: rotate(360deg); }
}
@keyframes fadeInUp {
    from { opacity: 0; transform: translateY(20px); }
    to { opacity: 1; transform: translateY(0); }
}

.animate-fade-in-up {
    animation: fadeInUp 0.5s ease forwards;
    opacity: 0;
}

/* ===== Responsive ===== */
@media (max-width: 768px) {
    .login-page::before, .login-page::after { display: none; }
    .form-unified { flex-direction: column; gap: 24px; }
    .form-decoration { display: none; }
    .login-card-wide { max-width: 500px; }
    .form-body { padding: 24px; }
    .captcha-row { flex-direction: column; }
    .captcha-img-wrapper { width: 100%; height: 52px; }
}
</style>
