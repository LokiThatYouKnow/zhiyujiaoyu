<template>
    <div class="student-dashboard">
        <!-- ===== Hero Profile Card ===== -->
        <section class="hero-card animate-fade-in-up">
            <div class="hero-pattern"></div>
            <div class="hero-top">
                <div class="hero-avatar">
                    <div class="avatar-circle">
                        <span class="avatar-initial">{{ studentDetail.student_name?.charAt(0) || '学' }}</span>
                    </div>
                    <div class="online-dot"></div>
                </div>
                <div class="hero-greeting">
                    <span class="greeting-chip">
                        <span class="wave">👋</span> 欢迎回来
                    </span>
                    <h1 class="hero-name">{{ studentDetail.student_name }}</h1>
                    <span class="hero-account">@{{ studentDetail.student_account }}</span>
                </div>
            </div>
            <div class="hero-bottom">
                <div class="hero-badges">
                    <div class="badge">
                        <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2z" fill="currentColor"/></svg>
                        {{ studentDetail.student_major || '未设置专业' }}
                    </div>
                    <div class="badge">
                        <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M3 21h18M3 10h18M5 6l7-3 7 3M4 10v11M20 10v11M8 14v4M12 14v4M16 14v4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                        {{ studentDetail.student_class_name || '未加入班级' }}
                    </div>
                </div>
                <div class="hero-stats">
                    <div class="stat">
                        <div class="stat-icon">
                            <svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M4 19.5A2.5 2.5 0 0 0 6.5 22H20V2H6.5A2.5 2.5 0 0 0 4 4.5v15z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                        </div>
                        <div class="stat-value">{{ studentDetail.student_classList?.length || 0 }}</div>
                        <div class="stat-label">门课程</div>
                    </div>
                    <div class="stat">
                        <div class="stat-icon">
                            <svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
                        </div>
                        <div class="stat-value">{{ studentDetail.student_gender || '-' }}</div>
                        <div class="stat-label">性别</div>
                    </div>
                    <div class="stat">
                        <div class="stat-icon">
                            <svg viewBox="0 0 24 24" fill="none" width="20" height="20"><circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/><path d="M12 6v6l4 2" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
                        </div>
                        <div class="stat-value">{{ studentDetail.student_age || '-' }}</div>
                        <div class="stat-label">年龄</div>
                    </div>
                </div>
            </div>
        </section>

        <!-- ===== Quick Actions ===== -->
        <div class="section-head">
            <div class="section-head-left">
                <h2 class="section-title">快捷功能</h2>
                <p class="section-sub">选择功能模块开始学习</p>
            </div>
        </div>

        <div class="actions-grid">
            <button class="action-card animate-fade-in-up" style="animation-delay: 0.05s" @click="navigateTo('SelfStudentDetail')">
                <div class="action-top">
                    <div class="action-icon" style="background: linear-gradient(135deg, #0D9488, #14B8A6); box-shadow: 0 6px 20px rgba(13,148,136,0.3);">
                        <svg viewBox="0 0 24 24" fill="none" width="24" height="24"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="white" stroke-width="2" stroke-linecap="round"/></svg>
                    </div>
                    <span class="action-badge">个人</span>
                </div>
                <div class="action-body">
                    <h3>个人详情</h3>
                    <p>查看您的个人资料、班级信息和课程列表</p>
                </div>
                <div class="action-footer">
                    <span>进入</span>
                    <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M5 12h14M12 5l7 7-7 7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                </div>
            </button>

            <button class="action-card animate-fade-in-up" style="animation-delay: 0.1s" @click="navigateTo('CoursePractise')">
                <div class="action-top">
                    <div class="action-icon" style="background: linear-gradient(135deg, #6366F1, #818CF8); box-shadow: 0 6px 20px rgba(99,102,241,0.3);">
                        <svg viewBox="0 0 24 24" fill="none" width="24" height="24"><path d="M9 11l3 3L22 4M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                    </div>
                    <span class="action-badge">练习</span>
                </div>
                <div class="action-body">
                    <h3>课程练习</h3>
                    <p>浏览所有课程并完成课后练习题目</p>
                </div>
                <div class="action-footer">
                    <span>进入</span>
                    <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M5 12h14M12 5l7 7-7 7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                </div>
            </button>

            <button class="action-card animate-fade-in-up" style="animation-delay: 0.15s" @click="navigateTo('AnswerQuestion')">
                <div class="action-top">
                    <div class="action-icon" style="background: linear-gradient(135deg, #F59E0B, #FBBF24); box-shadow: 0 6px 20px rgba(245,158,11,0.3);">
                        <svg viewBox="0 0 24 24" fill="none" width="24" height="24"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                    </div>
                    <span class="action-badge">AI</span>
                </div>
                <div class="action-body">
                    <h3>疑惑答解</h3>
                    <p>AI 智能助手在线解答学习中遇到的疑问</p>
                </div>
                <div class="action-footer">
                    <span>进入</span>
                    <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M5 12h14M12 5l7 7-7 7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                </div>
            </button>

            <button class="action-card animate-fade-in-up" style="animation-delay: 0.2s" @click="navigateTo('SelfPractise')">
                <div class="action-top">
                    <div class="action-icon" style="background: linear-gradient(135deg, #EC4899, #F472B6); box-shadow: 0 6px 20px rgba(236,72,153,0.3);">
                        <svg viewBox="0 0 24 24" fill="none" width="24" height="24"><path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                    </div>
                    <span class="action-badge">训练</span>
                </div>
                <div class="action-body">
                    <h3>针对性训练</h3>
                    <p>AI 根据薄弱点生成个性化提升训练计划</p>
                </div>
                <div class="action-footer">
                    <span>进入</span>
                    <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M5 12h14M12 5l7 7-7 7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                </div>
            </button>
        </div>

        <!-- ===== My Courses ===== -->
        <div class="section-head">
            <div class="section-head-left">
                <h2 class="section-title">我的课程</h2>
                <p class="section-sub">
                    {{ studentDetail.student_classList?.length ? '已加入 ' + studentDetail.student_classList.length + ' 门课程' : '暂未加入课程' }}
                </p>
            </div>
            <button class="view-all-btn" v-if="studentDetail.student_classList?.length" @click="navigateTo('SelfStudentDetail')">
                查看全部 <span>→</span>
            </button>
        </div>

        <div class="courses-wrap" v-if="studentDetail.student_classList && studentDetail.student_classList.length > 0">
            <div class="courses-row">
                <div v-for="(course, idx) in studentDetail.student_classList" :key="course.course_id"
                    class="course-card animate-fade-in-up" :style="{ animationDelay: (0.05 * idx) + 's' }">
                    <div class="course-card-bar" :style="{ background: courseBars[idx % courseBars.length] }"></div>
                    <div class="course-card-body">
                        <div class="course-card-emoji">📚</div>
                        <h4 class="course-title">{{ course.course_name }}</h4>
                        <div class="course-info">
                            <span v-if="course.course_teacher_name">
                                <svg viewBox="0 0 24 24" fill="none" width="14" height="14"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
                                {{ course.course_teacher_name }}
                            </span>
                            <span v-if="course.course_hours">
                                <svg viewBox="0 0 24 24" fill="none" width="14" height="14"><circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/><path d="M12 6v6l4 2" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
                                {{ course.course_hours }}学时
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="empty-state" v-else>
            <div class="empty-icon">📖</div>
            <h3>暂无课程</h3>
            <p>联系教师获取班级绑定码，加入班级开始学习</p>
            <a-button type="primary" size="large" @click="navigateTo('SelfStudentDetail')" class="join-btn">
                前往加入班级
            </a-button>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import router from '../../routers/appRouters';
import type { student } from '../../modules/student';
import { jwtDecode } from 'jwt-decode';
import type { R } from '../../response/R';
import { message } from 'ant-design-vue';
import { getSelfStudentDetailApi } from '../../api/student/getSelfStudentDetailApi';

const token = ref((sessionStorage.getItem('token') || localStorage.getItem('token')));
const student_id = (() => { try { return token.value ? (jwtDecode(token.value) as any).sub as string : '' } catch { return '' } })();
const studentDetail = ref<student>({
    student_id: 0, student_account: '', student_name: '', student_gender: '',
    student_age: '', student_classList: [], student_major: '', student_class_name: '',
});

if (sessionStorage.getItem('studentDetail')) {
    studentDetail.value = JSON.parse(sessionStorage.getItem('studentDetail') as string);
    if (!studentDetail.value.student_account) { getSelfStudentDetailApi(student_id).then(res => {
        const R: R = res.data;
        if (R.code === 200) { studentDetail.value = R.data; sessionStorage.setItem('studentDetail', JSON.stringify(studentDetail.value)); }
    }).catch(err => { message.error('获取学生信息失败'); console.log(err); }); }
} else {
    getSelfStudentDetailApi(student_id).then(res => {
        const R: R = res.data;
        if (R.code === 200) {
            studentDetail.value = R.data;
            sessionStorage.setItem('studentDetail', JSON.stringify(studentDetail.value));
        } else { message.error('获取学生信息失败'); }
    }).catch(err => { message.error('获取学生信息失败'); console.log(err); });
}

const courseBars = [
    'linear-gradient(135deg, #0D9488, #14B8A6)',
    'linear-gradient(135deg, #6366F1, #818CF8)',
    'linear-gradient(135deg, #F59E0B, #FBBF24)',
    'linear-gradient(135deg, #EC4899, #F472B6)',
    'linear-gradient(135deg, #8B5CF6, #A78BFA)',
    'linear-gradient(135deg, #06B6D4, #22D3EE)',
];

const navigateTo = (routeName: string) => router.push({ name: routeName });
</script>

<style scoped>
.student-dashboard {
    height: 100%;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    padding: 20px 24px;
    max-width: 1024px;
    margin: 0 auto;
    width: 100%;
}

/* ===== Hero Card ===== */
.hero-card {
    background: linear-gradient(145deg, #0D9488 0%, #14B8A6 35%, #06B6D4 70%, #0D9488 100%);
    border-radius: 24px;
    padding: 36px 36px 28px;
    margin-bottom: 28px;
    position: relative;
    overflow: hidden;
    box-shadow: 0 16px 48px rgba(13, 148, 136, 0.3);
    flex-shrink: 0;
}
.hero-pattern {
    position: absolute;
    inset: 0;
    background:
        radial-gradient(circle at 85% 20%, rgba(255,255,255,0.08) 0%, transparent 60%),
        radial-gradient(circle at 15% 80%, rgba(255,255,255,0.05) 0%, transparent 50%),
        radial-gradient(circle at 60% 50%, rgba(255,255,255,0.03) 0%, transparent 70%);
    pointer-events: none;
}
.hero-top {
    position: relative;
    z-index: 1;
    display: flex;
    align-items: center;
    gap: 24px;
    margin-bottom: 28px;
}
.hero-avatar { position: relative; flex-shrink: 0; }
.avatar-circle {
    width: 80px; height: 80px;
    border-radius: 50%;
    background: rgba(255,255,255,0.2);
    backdrop-filter: blur(8px);
    display: flex; align-items: center; justify-content: center;
    border: 3px solid rgba(255,255,255,0.35);
    box-shadow: 0 8px 24px rgba(0,0,0,0.1);
}
.avatar-initial {
    font-size: 36px;
    font-weight: 800;
    color: white;
    text-shadow: 0 2px 4px rgba(0,0,0,0.1);
}
.online-dot {
    position: absolute;
    bottom: 4px; right: 4px;
    width: 16px; height: 16px;
    background: #10B981;
    border-radius: 50%;
    border: 3px solid white;
    box-shadow: 0 0 0 4px rgba(16,185,129,0.2);
    animation: pulse-dot 2s infinite;
}
@keyframes pulse-dot {
    0%, 100% { box-shadow: 0 0 0 4px rgba(16,185,129,0.2); }
    50% { box-shadow: 0 0 0 10px rgba(16,185,129,0); }
}
.hero-greeting { color: white; }
.greeting-chip {
    display: inline-flex; align-items: center; gap: 4px;
    background: rgba(255,255,255,0.18);
    backdrop-filter: blur(6px);
    padding: 4px 14px;
    border-radius: 20px;
    font-size: 13px; font-weight: 600;
    margin-bottom: 8px;
    color: rgba(255,255,255,0.95);
}
.wave { font-size: 16px; display: inline-block; animation: wave-anim 1.5s infinite; }
@keyframes wave-anim {
    0%, 100% { transform: rotate(0deg); }
    25% { transform: rotate(15deg); }
    75% { transform: rotate(-10deg); }
}
.hero-name {
    font-size: 32px; font-weight: 800;
    color: white;
    margin: 0 0 4px;
    letter-spacing: 1px;
}
.hero-account {
    font-size: 14px;
    color: rgba(255,255,255,0.7);
    font-weight: 500;
}
.hero-bottom {
    position: relative; z-index: 1;
    display: flex;
    align-items: flex-end;
    justify-content: space-between;
    flex-wrap: wrap;
    gap: 20px;
}
.hero-badges {
    display: flex; gap: 10px; flex-wrap: wrap;
}
.badge {
    display: inline-flex; align-items: center; gap: 6px;
    background: rgba(255,255,255,0.15);
    backdrop-filter: blur(6px);
    padding: 7px 16px;
    border-radius: 10px;
    font-size: 13px; font-weight: 600;
    color: white;
}
.hero-stats {
    display: flex; gap: 6px;
}
.stat {
    background: rgba(255,255,255,0.12);
    backdrop-filter: blur(6px);
    border-radius: 14px;
    padding: 12px 18px;
    text-align: center;
    min-width: 80px;
    color: white;
    transition: background 0.25s;
}
.stat:hover { background: rgba(255,255,255,0.2); }
.stat-icon { margin-bottom: 6px; opacity: 0.85; }
.stat-value { font-size: 22px; font-weight: 800; line-height: 1.1; }
.stat-label { font-size: 11px; opacity: 0.75; margin-top: 2px; }

/* ===== Section Head ===== */
.section-head {
    display: flex;
    align-items: flex-end;
    justify-content: space-between;
    margin-bottom: 18px;
    padding: 0 4px;
    flex-shrink: 0;
}
.section-title {
    font-size: 1.25rem;
    font-weight: 800;
    color: #0F172A;
    margin: 0 0 4px;
    display: flex;
    align-items: center;
    gap: 10px;
}
.section-title::before {
    content: '';
    width: 4px; height: 24px;
    background: linear-gradient(180deg, #0D9488, #06B6D4);
    border-radius: 2px;
    display: inline-block;
}
.section-sub {
    font-size: 13px; color: #94A3B8; margin: 0;
}
.view-all-btn {
    background: none; border: 1px solid #E2E8F0;
    padding: 8px 18px; border-radius: 10px;
    font-size: 13px; font-weight: 600; color: #0D9488;
    cursor: pointer; transition: all 0.25s;
    display: flex; align-items: center; gap: 6px;
}
.view-all-btn:hover {
    background: rgba(13,148,136,0.06);
    border-color: rgba(13,148,136,0.25);
}
.view-all-btn span { font-size: 15px; transition: transform 0.25s; }
.view-all-btn:hover span { transform: translateX(3px); }

/* ===== Actions Grid ===== */
.actions-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
    margin-bottom: 28px;
    flex-shrink: 0;
}
.action-card {
    background: white;
    border: 1px solid #E2E8F0;
    border-radius: 18px;
    padding: 24px;
    cursor: pointer;
    text-align: left;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 1px 3px rgba(0,0,0,0.03);
    display: flex; flex-direction: column; gap: 14px;
    position: relative;
    overflow: hidden;
}
.action-card::before {
    content: '';
    position: absolute;
    top: 0; left: 0; right: 0;
    height: 0;
    background: linear-gradient(180deg, rgba(13,148,136,0.03), transparent);
    transition: height 0.3s ease;
}
.action-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 32px rgba(0,0,0,0.08);
    border-color: rgba(13,148,136,0.2);
}
.action-card:hover::before { height: 100%; }
.action-top {
    display: flex; align-items: center; justify-content: space-between;
    position: relative; z-index: 1;
}
.action-icon {
    width: 52px; height: 52px;
    border-radius: 15px;
    display: flex; align-items: center; justify-content: center;
    flex-shrink: 0;
    transition: transform 0.3s ease;
}
.action-card:hover .action-icon { transform: scale(1.08); }
.action-badge {
    font-size: 11px; font-weight: 700; color: #94A3B8;
    background: #F1F5F9; padding: 4px 12px; border-radius: 8px;
    letter-spacing: 0.5px; text-transform: uppercase;
    transition: all 0.3s;
}
.action-card:hover .action-badge {
    background: rgba(13,148,136,0.08);
    color: #0D9488;
}
.action-body { position: relative; z-index: 1; }
.action-body h3 { font-size: 16px; font-weight: 700; color: #0F172A; margin: 0 0 6px; }
.action-body p { font-size: 13px; color: #94A3B8; margin: 0; line-height: 1.5; }
.action-footer {
    display: flex; align-items: center; gap: 6px;
    font-size: 13px; font-weight: 600; color: #0D9488;
    position: relative; z-index: 1;
    transition: gap 0.25s;
}
.action-card:hover .action-footer { gap: 10px; }

/* ===== Courses Section ===== */
.courses-wrap {
    flex: 1;
    overflow-y: auto;
    min-height: 0;
    padding-bottom: 8px;
    margin-bottom: 0;
}
.courses-wrap::-webkit-scrollbar { width: 5px; }
.courses-wrap::-webkit-scrollbar-track { background: transparent; }
.courses-wrap::-webkit-scrollbar-thumb { background: #CBD5E1; border-radius: 10px; }
.courses-row {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 14px;
}
.course-card {
    background: white;
    border-radius: 16px;
    overflow: hidden;
    border: 1px solid #E2E8F0;
    box-shadow: 0 1px 3px rgba(0,0,0,0.03);
    transition: all 0.3s ease;
    cursor: pointer;
}
.course-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 10px 28px rgba(0,0,0,0.08);
    border-color: transparent;
}
.course-card-bar {
    height: 4px;
    transition: height 0.3s;
}
.course-card:hover .course-card-bar { height: 6px; }
.course-card-body { padding: 20px; }
.course-card-emoji { font-size: 28px; margin-bottom: 10px; }
.course-title {
    font-size: 15px; font-weight: 700; color: #0F172A;
    margin: 0 0 12px;
    line-height: 1.3;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}
.course-info {
    display: flex; flex-direction: column; gap: 6px;
}
.course-info span {
    font-size: 12px; color: #94A3B8;
    display: flex; align-items: center; gap: 5px;
}

/* ===== Empty State ===== */
.empty-state {
    background: white;
    border-radius: 20px;
    padding: 48px 24px;
    text-align: center;
    border: 1px dashed #E2E8F0;
}
.empty-icon { font-size: 56px; margin-bottom: 12px; }
.empty-state h3 { font-size: 18px; font-weight: 700; color: #0F172A; margin: 0 0 8px; }
.empty-state p { font-size: 14px; color: #94A3B8; margin: 0 0 20px; }
.join-btn {
    background: linear-gradient(135deg, #0D9488, #14B8A6) !important;
    border: none !important;
    font-weight: 700 !important;
    border-radius: 12px !important;
    height: 44px !important;
    padding: 0 28px !important;
    box-shadow: 0 4px 16px rgba(13,148,136,0.3) !important;
}
.join-btn:hover {
    box-shadow: 0 6px 24px rgba(13,148,136,0.4) !important;
    transform: translateY(-2px);
}

/* ===== Responsive ===== */
@media (max-width: 768px) {
    .hero-card { padding: 24px 20px 20px; border-radius: 20px; }
    .hero-top { flex-direction: column; text-align: center; gap: 16px; margin-bottom: 20px; }
    .hero-bottom { flex-direction: column; align-items: stretch; }
    .hero-badges { justify-content: center; }
    .hero-stats { justify-content: center; }
    .stat { min-width: 70px; padding: 10px 14px; }
    .actions-grid { grid-template-columns: 1fr; }
    .courses-row { grid-template-columns: 1fr; }
}
@media (max-width: 480px) {
    .hero-name { font-size: 24px; }
    .hero-stats { flex-wrap: wrap; }
    .stat { flex: 1; min-width: 60px; }
}
</style>
