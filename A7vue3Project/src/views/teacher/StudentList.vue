<template>
    <div class="student-page">
        <div class="student-container">
            <!-- Header -->
            <div class="page-header">
                <div class="header-left">
                    <h2 class="page-title">
                        <span class="title-icon">👥</span> 学生列表
                    </h2>
                    <span class="class-badge">
                        <font-awesome-icon :icon="['fas', 'graduation-cap']" /> {{ teacherDetail.teacher_class_name || '暂无数据' }}
                    </span>
                </div>
                <a-button class="back-btn" @click="backToHome">
                    <span>←</span> 返回首页
                </a-button>
            </div>

            <!-- Stats Row -->
            <div class="stats-row" v-if="studentList.length > 0">
                <div class="stat-card total">
                    <div class="stat-card-inner">
                        <span class="stat-number">{{ studentList.length }}</span>
                        <span class="stat-label">学生总数</span>
                    </div>
                    <div class="stat-icon-wrap">👥</div>
                </div>
                <div class="stat-card male">
                    <div class="stat-card-inner">
                        <span class="stat-number">{{ studentList.filter(s => s.student_gender === '男').length }}</span>
                        <span class="stat-label">男生</span>
                    </div>
                    <div class="stat-icon-wrap">👦</div>
                </div>
                <div class="stat-card female">
                    <div class="stat-card-inner">
                        <span class="stat-number">{{ studentList.filter(s => s.student_gender === '女').length }}</span>
                        <span class="stat-label">女生</span>
                    </div>
                    <div class="stat-icon-wrap">👧</div>
                </div>
                <div class="stat-card class">
                    <div class="stat-card-inner">
                        <span class="stat-number stat-text">{{ teacherDetail.teacher_class_name || '-' }}</span>
                        <span class="stat-label">班级</span>
                    </div>
                    <div class="stat-icon-wrap">🏫</div>
                </div>
            </div>

            <!-- Student Cards Grid -->
            <div class="student-grid-wrapper">
                <div class="student-grid" v-if="studentList.length > 0">
                    <div v-for="student in studentList" :key="student.student_id" class="student-card">
                        <div class="card-top">
                            <div class="student-avatar" :class="avatarColor(student.student_name)">
                                {{ (student.student_name || '?').charAt(0) }}
                            </div>
                            <div class="student-brief">
                                <h3 class="student-name">{{ student.student_name || '未命名学生' }}</h3>
                                <span class="student-gender" :class="student.student_gender === '男' ? 'male' : 'female'">
                                    {{ student.student_gender || '未知' }}
                                </span>
                            </div>
                        </div>
                        <div class="card-details">
                            <div class="detail-row">
                                <span class="detail-label">账号</span>
                                <span class="detail-value">{{ student.student_account || '暂无' }}</span>
                            </div>
                            <div class="detail-row">
                                <span class="detail-label">年龄</span>
                                <span class="detail-value">{{ student.student_age || '暂无' }}</span>
                            </div>
                            <div class="detail-row">
                                <span class="detail-label">专业</span>
                                <span class="detail-value">{{ student.student_major || '暂无' }}</span>
                            </div>
                            <div class="detail-row">
                                <span class="detail-label">班级</span>
                                <span class="detail-value">{{ student.student_class_name || '暂无' }}</span>
                            </div>
                        </div>
                    </div>
                </div>
                <div v-else class="empty-state">
                    <div class="empty-icon">📭</div>
                    <span class="empty-title">暂无学生数据</span>
                    <span class="empty-sub">当学生加入你的班级后，将在这里显示</span>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import type { teacher } from '../../modules/teacher';
import { jwtDecode } from 'jwt-decode';
import { getSelfTeacherDetailApi } from '../../api/teacher/getSelfTeacherDetailApi';
import type { R } from '../../response/R';
import { message } from 'ant-design-vue';
import type { student } from '../../modules/student';
import router from '../../routers/appRouters';

const token = ref((sessionStorage.getItem('token') || localStorage.getItem('token')))
const teacher_id = jwtDecode(token.value as string).sub as string
const teacherDetail = ref<teacher>({
    teacher_id: 0,
    teacher_account: '',
    teacher_password: '',
    teacher_name: '',
    teacher_gender: '',
    teacher_age: '',
    teacher_major: '',
    teacher_position: '',
    teacher_class_name: '',
    teacher_studentList: [],
    teacher_courseList: [],
    teacher_courseResource: [],
})

const studentList = ref<student[]>([])

if ((sessionStorage.getItem('teacherDetail') || localStorage.getItem('teacherDetail'))) {
    teacherDetail.value = JSON.parse((sessionStorage.getItem('teacherDetail') || localStorage.getItem('teacherDetail')) as string)
    teacherDetail.value.teacher_studentList?.forEach(student => {
        studentList.value.push(student)
    })
} else {
    getSelfTeacherDetailApi(teacher_id).then(res => {
        const R: R = res.data
        if (R.code === 200) {
            teacherDetail.value = R.data
            sessionStorage.setItem('teacherDetail', JSON.stringify(teacherDetail.value))
        } else {
            message.error('获取教师信息失败')
        }
    }).catch(err => {
        message.error('获取教师信息失败')
        console.log(err)
    })
}

const avatarColors = ['amber', 'blue', 'emerald', 'violet', 'rose', 'cyan', 'orange', 'pink']
const avatarColor = (name: string | undefined) => {
    if (!name) return 'amber'
    return avatarColors[name.charCodeAt(0) % avatarColors.length]
}

const backToHome = () => {
    router.push({ name: 'TeacherHomeView' })
}
</script>

<style scoped>
/* ===== Page ===== */
.student-page {
    height: 100%;
    overflow-y: auto;
    background: linear-gradient(180deg, #FFF7ED 0%, #F1F5F9 100%);
    padding: 28px 32px;
}
.student-container {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    gap: 24px;
}

/* ===== Header ===== */
.page-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-wrap: wrap;
    gap: 16px;
}
.header-left {
    display: flex;
    align-items: center;
    gap: 16px;
}
.page-title {
    font-size: 24px;
    font-weight: 800;
    color: #0F172A;
    display: flex;
    align-items: center;
    gap: 10px;
    margin: 0;
    letter-spacing: -0.3px;
}
.title-icon { font-size: 26px; }

.class-badge {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 6px 16px;
    border-radius: 999px;
    background: #FEF3C7;
    color: #92400E;
    font-size: 13px;
    font-weight: 600;
}

.back-btn {
    border-radius: 999px !important;
    font-weight: 600 !important;
    height: 38px !important;
    padding: 0 22px !important;
    background: linear-gradient(135deg, #FEF3C7, #FDE68A) !important;
    border: 1px solid #FCD34D !important;
    color: #92400E !important;
    transition: all 0.25s ease !important;
}
.back-btn:hover {
    background: linear-gradient(135deg, #FDE68A, #FCD34D) !important;
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(245,158,11,0.25) !important;
}

/* ===== Stats Row ===== */
.stats-row {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
}
.stat-card {
    background: #FFFFFF;
    border-radius: 16px;
    padding: 20px 24px;
    border: 1px solid #F1F5F9;
    box-shadow: 0 1px 4px rgba(0,0,0,0.03);
    display: flex;
    align-items: center;
    justify-content: space-between;
    transition: all 0.25s ease;
}
.stat-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(0,0,0,0.08);
}
.stat-card-inner {
    display: flex;
    flex-direction: column;
    gap: 2px;
}
.stat-number {
    font-size: 28px;
    font-weight: 800;
    color: #0F172A;
    line-height: 1.2;
}
.stat-text {
    font-size: 18px;
}
.stat-card.total .stat-number { color: #D97706; }
.stat-card.male .stat-number { color: #3B82F6; }
.stat-card.female .stat-number { color: #EC4899; }
.stat-card.class .stat-number { color: #8B5CF6; }

.stat-label {
    font-size: 12px;
    color: #94A3B8;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}
.stat-icon-wrap {
    font-size: 32px;
    opacity: 0.85;
    flex-shrink: 0;
}

/* ===== Grid Wrapper ===== */
.student-grid-wrapper {
    background: #FFFFFF;
    border-radius: 20px;
    padding: 24px;
    border: 1px solid #F1F5F9;
    box-shadow: 0 1px 4px rgba(0,0,0,0.03);
    min-height: 200px;
}
.student-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
}

/* ===== Student Card ===== */
.student-card {
    background: #F8FAFC;
    border-radius: 16px;
    padding: 20px;
    border: 1px solid #F1F5F9;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    display: flex;
    flex-direction: column;
    gap: 14px;
}
.student-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 32px rgba(245,158,11,0.1), 0 4px 8px rgba(0,0,0,0.05);
    border-color: rgba(245,158,11,0.2);
    background: #FFFFFF;
}

.card-top {
    display: flex;
    align-items: center;
    gap: 12px;
}
.student-avatar {
    width: 44px;
    height: 44px;
    min-width: 44px;
    border-radius: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 18px;
    font-weight: 800;
    color: #FFFFFF;
    flex-shrink: 0;
}
.student-avatar.amber { background: linear-gradient(135deg, #F59E0B, #D97706); }
.student-avatar.blue { background: linear-gradient(135deg, #3B82F6, #2563EB); }
.student-avatar.emerald { background: linear-gradient(135deg, #10B981, #059669); }
.student-avatar.violet { background: linear-gradient(135deg, #8B5CF6, #7C3AED); }
.student-avatar.rose { background: linear-gradient(135deg, #EC4899, #DB2777); }
.student-avatar.cyan { background: linear-gradient(135deg, #06B6D4, #0891B2); }
.student-avatar.orange { background: linear-gradient(135deg, #F97316, #EA580C); }
.student-avatar.pink { background: linear-gradient(135deg, #F472B6, #EC4899); }

.student-brief {
    display: flex;
    flex-direction: column;
    gap: 4px;
    min-width: 0;
}
.student-name {
    font-size: 15px;
    font-weight: 700;
    color: #0F172A;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    margin: 0;
}
.student-gender {
    font-size: 11px;
    font-weight: 600;
    padding: 1px 10px;
    border-radius: 999px;
    width: fit-content;
}
.student-gender.male { background: #EFF6FF; color: #3B82F6; }
.student-gender.female { background: #FDF2F8; color: #EC4899; }

.card-details {
    display: flex;
    flex-direction: column;
    gap: 8px;
}
.detail-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 6px 10px;
    border-radius: 8px;
    background: rgba(255,255,255,0.7);
    transition: all 0.15s ease;
}
.detail-row:hover {
    background: #FFFBEB;
}
.detail-label {
    font-size: 11px;
    font-weight: 600;
    color: #94A3B8;
    text-transform: uppercase;
    letter-spacing: 0.3px;
}
.detail-value {
    font-size: 13px;
    font-weight: 600;
    color: #334155;
    max-width: 120px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

/* ===== Empty State ===== */
.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 64px 24px;
    gap: 12px;
    color: #94A3B8;
}
.empty-icon { font-size: 56px; opacity: 0.5; }
.empty-title { font-size: 16px; font-weight: 600; color: #64748B; }
.empty-sub { font-size: 13px; color: #94A3B8; }

/* ===== Responsive ===== */
@media (max-width: 1200px) { .student-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 900px) { .student-grid { grid-template-columns: repeat(2, 1fr); } .stats-row { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 600px) {
    .student-grid { grid-template-columns: 1fr; }
    .stats-row { grid-template-columns: 1fr; }
    .student-page { padding: 16px; }
}
</style>
