<template>
    <div class="dashboard">
        <section class="welcome-banner animate-fade-in-up">
            <div class="banner-content">
                <div>
                    <h1>欢迎回来，{{ teacherDetail.teacher_name }}</h1>
                    <p>{{ today }}</p>
                </div>
                <div class="banner-emoji">👩‍🏫</div>
            </div>
        </section>

        <div class="feature-grid">
            <button class="feature-card animate-fade-in-up" style="animation-delay: 0.05s" @click="navigateTo('SelfTeacherDetail')">
                <div class="feature-icon" style="background: linear-gradient(135deg, #4F46E5, #6366F1); box-shadow: 0 4px 14px rgba(79,70,229,0.3);">
                    <svg viewBox="0 0 24 24" fill="none" width="28" height="28"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="white" stroke-width="2" stroke-linecap="round"/></svg>
                </div>
                <h3>个人详情</h3>
                <p>查看您的个人信息</p>
            </button>
            <button class="feature-card animate-fade-in-up" style="animation-delay: 0.1s" @click="navigateTo('CourseList')">
                <div class="feature-icon" style="background: linear-gradient(135deg, #8B5CF6, #A78BFA); box-shadow: 0 4px 14px rgba(139,92,246,0.3);">
                    <svg viewBox="0 0 24 24" fill="none" width="28" height="28"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M4 19.5A2.5 2.5 0 0 0 6.5 22H20V2H6.5A2.5 2.5 0 0 0 4 4.5v15zM8 7h6M8 11h8M8 15h5" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                </div>
                <h3>课程列表</h3>
                <p>管理您的所有课程和教学内容</p>
            </button>
            <button class="feature-card animate-fade-in-up" style="animation-delay: 0.15s" @click="navigateTo('StudentListT')">
                <div class="feature-icon" style="background: linear-gradient(135deg, #10B981, #34D399); box-shadow: 0 4px 14px rgba(16,185,129,0.3);">
                    <svg viewBox="0 0 24 24" fill="none" width="28" height="28"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8zM23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                </div>
                <h3>学生列表</h3>
                <p>查看和管理您的学生信息</p>
            </button>
            <button class="feature-card animate-fade-in-up" style="animation-delay: 0.2s" @click="navigateTo('KnowledgeOrganization')">
                <div class="feature-icon" style="background: linear-gradient(135deg, #F59E0B, #FBBF24); box-shadow: 0 4px 14px rgba(245,158,11,0.3);">
                    <svg viewBox="0 0 24 24" fill="none" width="28" height="28"><circle cx="12" cy="12" r="10" stroke="white" stroke-width="2"/><path d="M12 6v6l4 2" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                </div>
                <h3>备课与设计</h3>
                <p>创建和管理您的课程备课内容</p>
            </button>
            <button class="feature-card animate-fade-in-up" style="animation-delay: 0.25s" @click="navigateTo('PractiseDesign')">
                <div class="feature-icon" style="background: linear-gradient(135deg, #EC4899, #F472B6); box-shadow: 0 4px 14px rgba(236,72,153,0.3);">
                    <svg viewBox="0 0 24 24" fill="none" width="28" height="28"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/><path d="M14 2v6h6M16 13H8M16 17H8M10 9H8" stroke="white" stroke-width="2" stroke-linecap="round"/></svg>
                </div>
                <h3>实训设计</h3>
                <p>设计和管理实践教学内容</p>
            </button>
            <button class="feature-card animate-fade-in-up" style="animation-delay: 0.3s" @click="navigateTo('DesignIssue')">
                <div class="feature-icon" style="background: linear-gradient(135deg, #06B6D4, #22D3EE); box-shadow: 0 4px 14px rgba(6,182,212,0.3);">
                    <svg viewBox="0 0 24 24" fill="none" width="28" height="28"><path d="M12 20h9M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                </div>
                <h3>智能出题</h3>
                <p>使用AI辅助生成测试题目</p>
            </button>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import router from '../../routers/appRouters';
import { jwtDecode } from 'jwt-decode';
import type { teacher } from '../../modules/teacher';
import { getSelfTeacherDetailApi } from '../../api/teacher/getSelfTeacherDetailApi';
import type { R } from '../../response/R';
import { message } from 'ant-design-vue';

const token = ref((sessionStorage.getItem('token') || localStorage.getItem('token')));
const teacher_id = jwtDecode(token.value as string).sub as string;
const teacherDetail = ref<teacher>({
    teacher_id: 0, teacher_account: '', teacher_password: '', teacher_name: '',
    teacher_gender: '', teacher_age: '', teacher_major: '', teacher_position: '',
    teacher_class_name: '', teacher_studentList: [], teacher_courseList: [], teacher_courseResource: [],
});

if ((sessionStorage.getItem('teacherDetail') || localStorage.getItem('teacherDetail'))) {
    teacherDetail.value = JSON.parse((sessionStorage.getItem('teacherDetail') || localStorage.getItem('teacherDetail')) as string);
} else {
    getSelfTeacherDetailApi(teacher_id).then(res => {
        const R: R = res.data;
        if (R.code === 200) {
            teacherDetail.value = R.data;
            sessionStorage.setItem('teacherDetail', JSON.stringify(teacherDetail.value));
        } else { message.error('获取教师信息失败'); }
    }).catch(err => { message.error('获取教师信息失败'); console.log(err); });
}

const today = ref(new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric', month: 'long', day: 'numeric', weekday: 'long'
}).format(new Date()));

const navigateTo = (routeName: string) => router.push({ name: routeName });
</script>

<style scoped>
.dashboard { max-width: 1100px; margin: 0 auto; }

.welcome-banner {
  background: linear-gradient(135deg, #F59E0B 0%, #F97316 50%, #EF4444 100%);
  border-radius: 20px;
  padding: 40px 44px;
  margin-bottom: 32px;
  color: white;
  position: relative;
  overflow: hidden;
  box-shadow: 0 12px 40px rgba(245, 158, 11, 0.25);
}
.welcome-banner::after {
  content: '';
  position: absolute;
  top: -60px; right: -30px;
  width: 240px; height: 240px;
  border-radius: 50%;
  background: rgba(255,255,255,0.06);
}
.banner-content { position: relative; z-index: 1; display: flex; align-items: center; justify-content: space-between; }
.banner-content h1 { font-size: 28px; font-weight: 800; margin-bottom: 6px; letter-spacing: 0.5px; }
.banner-content p { font-size: 15px; opacity: 0.85; }
.banner-emoji { font-size: 56px; opacity: 0.4; }

.feature-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }

.feature-card {
  background: white;
  border: 1px solid #E2E8F0;
  border-radius: 16px;
  padding: 30px 26px;
  cursor: pointer;
  transition: all 0.25s ease;
  text-align: left;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
  position: relative;
  overflow: hidden;
}
.feature-card::after {
  content: '';
  position: absolute;
  top: -30px; right: -30px;
  width: 100px; height: 100px;
  border-radius: 50%;
  background: rgba(79,70,229,0.03);
  opacity: 0;
  transition: all 0.25s ease;
}
.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 14px 32px rgba(0,0,0,0.1);
  border-color: transparent;
}
.feature-card:hover::after { opacity: 1; }
.feature-icon {
  width: 58px; height: 58px;
  border-radius: 15px;
  display: flex; align-items: center; justify-content: center;
  margin-bottom: 20px;
  position: relative; z-index: 1;
}
.feature-card h3 { font-size: 18px; font-weight: 700; color: #0F172A; margin-bottom: 8px; position: relative; z-index: 1; }
.feature-card p { font-size: 14px; color: #94A3B8; position: relative; z-index: 1; }

@media (max-width: 768px) { .feature-grid { grid-template-columns: repeat(2, 1fr); } }
</style>
