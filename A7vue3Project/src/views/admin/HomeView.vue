<template>
    <div class="dashboard">
        <!-- Welcome Banner -->
        <section class="welcome-banner animate-fade-in-up">
            <div class="banner-bg"></div>
            <div class="banner-content">
                <div>
                    <h1>欢迎回来，{{ adminVODetail.admin_name }}</h1>
                    <p>今天是 {{ currentDate }}，祝您工作顺利！</p>
                </div>
                <div class="banner-emoji">📊</div>
            </div>
        </section>

        <!-- Stats Cards -->
        <section class="stats-grid">
            <div class="stat-card animate-fade-in-up" style="animation-delay: 0.05s">
                <div class="stat-icon" style="background: linear-gradient(135deg, #4F46E5, #6366F1); box-shadow: 0 4px 14px rgba(79,70,229,0.3);">
                    <svg viewBox="0 0 24 24" fill="none" width="22" height="22"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="white" stroke-width="2" stroke-linecap="round"/></svg>
                </div>
                <div class="stat-info">
                    <span class="stat-label">管理员姓名</span>
                    <span class="stat-value">{{ adminVODetail.admin_name }}</span>
                </div>
            </div>

            <div class="stat-card animate-fade-in-up" style="animation-delay: 0.1s">
                <div class="stat-icon" style="background: linear-gradient(135deg, #06B6D4, #22D3EE); box-shadow: 0 4px 14px rgba(6,182,212,0.3);">
                    <svg viewBox="0 0 24 24" fill="none" width="22" height="22"><rect x="3" y="11" width="18" height="11" rx="2" stroke="white" stroke-width="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4" stroke="white" stroke-width="2" stroke-linecap="round"/></svg>
                </div>
                <div class="stat-info">
                    <span class="stat-label">登录账户</span>
                    <span class="stat-value">{{ adminVODetail.admin_account }}</span>
                </div>
            </div>

            <div class="stat-card animate-fade-in-up" style="animation-delay: 0.15s">
                <div class="stat-icon" style="background: linear-gradient(135deg, #10B981, #34D399); box-shadow: 0 4px 14px rgba(16,185,129,0.3);">
                    <svg viewBox="0 0 24 24" fill="none" width="22" height="22"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/><path d="M22 4L12 14.01l-3-3" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                </div>
                <div class="stat-info">
                    <span class="stat-label">账户状态</span>
                    <span class="stat-value status-ok">正常</span>
                </div>
            </div>
        </section>

        <!-- Quick Actions -->
        <section class="quick-section animate-fade-in-up" style="animation-delay: 0.2s">
            <h2 class="section-title">快捷操作</h2>
            <div class="quick-grid">
                <button class="quick-card" @click="navigateTo('DataAnalysis')">
                    <div class="quick-icon" style="background: linear-gradient(135deg, rgba(79,70,229,0.1), rgba(99,102,241,0.05)); color: #4F46E5;">
                        <svg viewBox="0 0 24 24" fill="none" width="24" height="24"><path d="M18 20V10M12 20V4M6 20v-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                    </div>
                    <span>数据概览</span>
                </button>
                <button class="quick-card" @click="navigateTo('ResourceManager')">
                    <div class="quick-icon" style="background: linear-gradient(135deg, rgba(6,182,212,0.1), rgba(34,211,238,0.05)); color: #06B6D4;">
                        <svg viewBox="0 0 24 24" fill="none" width="24" height="24"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                    </div>
                    <span>课件资源管理</span>
                </button>
                <button class="quick-card" @click="navigateTo('StudentList')">
                    <div class="quick-icon" style="background: linear-gradient(135deg, rgba(139,92,246,0.1), rgba(167,139,250,0.05)); color: #8B5CF6;">
                        <svg viewBox="0 0 24 24" fill="none" width="24" height="24"><path d="M4 20h16M4 20v-1a4 4 0 0 1 4-4h8a4 4 0 0 1 4 4v1M12 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
                    </div>
                    <span>学生管理</span>
                </button>
                <button class="quick-card" @click="navigateTo('TeacherList')">
                    <div class="quick-icon" style="background: linear-gradient(135deg, rgba(245,158,11,0.1), rgba(251,191,36,0.05)); color: #F59E0B;">
                        <svg viewBox="0 0 24 24" fill="none" width="24" height="24"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8zM23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                    </div>
                    <span>教师管理</span>
                </button>
                <button class="quick-card" @click="navigateTo('AdminList')">
                    <div class="quick-icon" style="background: linear-gradient(135deg, rgba(239,68,68,0.1), rgba(252,129,129,0.05)); color: #EF4444;">
                        <svg viewBox="0 0 24 24" fill="none" width="24" height="24"><path d="M12 15v2m-6 4h12a2 2 0 0 0 2-2v-6a2 2 0 0 0-2-2H6a2 2 0 0 0-2 2v6a2 2 0 0 0 2 2zm10-10V7a4 4 0 0 0-8 0v4h8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                    </div>
                    <span>管理员列表</span>
                </button>
            </div>
        </section>
    </div>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue';
import { getSelfAdminDetailApi } from '../../api/admin/getSelfAdminDetailApi';
import type { admin } from '../../modules/admin';
import type { adminVO } from '../../modules/adminVO';
import type { teacher } from '../../modules/teacher';
import type { student } from '../../modules/student';
import { jwtDecode } from 'jwt-decode';
import { message } from 'ant-design-vue';
import type { R } from '../../response/R';
import router from '../../routers/appRouters';

const token = ref((sessionStorage.getItem('token') || localStorage.getItem('token')));
const admin_usernmae = jwtDecode(token.value as string).sub as string;
const adminId = ref<string>('');
const adminVODetail = ref<adminVO>({
    admin_id: 0, admin_account: '', admin_password: '', admin_name: '',
    all_admin_list: [] as admin[], all_teacher_list: [] as teacher[], all_student_list: [] as student[],
});

const getSelfAdminDetail = async () => {
    await getSelfAdminDetailApi(admin_usernmae).then(res => {
        const R: R = res.data;
        if (R.code === 200) {
            adminVODetail.value = R.data;
            localStorage.setItem('adminDetail', JSON.stringify(adminVODetail.value));
            adminId.value = R.data.admin_id;
            localStorage.setItem("adminId", JSON.stringify(adminId.value));
        } else { message.error('获取管理员信息失败'); }
    }).catch(err => { message.error('获取管理员信息失败'); console.log(err); });
};

const setSelfAdminDetail = () => {
    adminVODetail.value = JSON.parse(localStorage.getItem('adminDetail') as string);
    adminId.value = JSON.parse(localStorage.getItem('adminId') as string);
};

if (localStorage.getItem('adminDetail')) { setSelfAdminDetail(); }
else { getSelfAdminDetail(); setSelfAdminDetail(); }

const currentDate = computed(() => {
    return new Intl.DateTimeFormat('zh-CN', {
        year: 'numeric', month: 'long', day: 'numeric', weekday: 'long'
    }).format(new Date());
});

const navigateTo = (routeName: string) => router.push({ name: routeName });
</script>

<style scoped>
.dashboard { max-width: 1100px; margin: 0 auto; }

.welcome-banner {
  position: relative;
  border-radius: 20px;
  padding: 40px 44px;
  margin-bottom: 28px;
  background: linear-gradient(135deg, #4F46E5 0%, #7C3AED 50%, #06B6D4 100%);
  color: white;
  overflow: hidden;
  box-shadow: 0 12px 40px rgba(79, 70, 229, 0.25);
}
.welcome-banner::after {
  content: '';
  position: absolute;
  top: -60px; right: -30px;
  width: 240px; height: 240px;
  border-radius: 50%;
  background: rgba(255,255,255,0.06);
}
.welcome-banner::before {
  content: '';
  position: absolute;
  bottom: -80px; left: 20%;
  width: 300px; height: 300px;
  border-radius: 50%;
  background: rgba(255,255,255,0.04);
}
.banner-bg { display: none; }
.banner-content { position: relative; z-index: 1; display: flex; align-items: center; justify-content: space-between; }
.banner-content h1 { font-size: 30px; font-weight: 800; margin-bottom: 8px; letter-spacing: 0.5px; }
.banner-content p { font-size: 15px; opacity: 0.85; font-weight: 400; }
.banner-emoji { font-size: 56px; opacity: 0.4; }

.stats-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin-bottom: 32px; }

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px 22px;
  display: flex; align-items: center; gap: 18px;
  border: 1px solid #E2E8F0;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
  transition: all 0.25s ease;
  position: relative; overflow: hidden;
}
.stat-card::after {
  content: '';
  position: absolute;
  top: -20px; right: -20px;
  width: 80px; height: 80px;
  border-radius: 50%;
  background: rgba(79,70,229,0.04);
  opacity: 0;
  transition: all 0.25s ease;
}
.stat-card:hover { transform: translateY(-3px); box-shadow: 0 12px 28px rgba(0,0,0,0.08); border-color: transparent; }
.stat-card:hover::after { opacity: 1; }
.stat-icon { width: 52px; height: 52px; min-width: 52px; border-radius: 14px; display: flex; align-items: center; justify-content: center; position: relative; z-index: 1; }
.stat-info { display: flex; flex-direction: column; gap: 4px; position: relative; z-index: 1; }
.stat-label { font-size: 12px; color: #94A3B8; font-weight: 500; letter-spacing: 0.3px; text-transform: uppercase; }
.stat-value { font-size: 20px; font-weight: 700; color: #0F172A; }
.status-ok { color: #10B981; }

.quick-section {
  background: white;
  border-radius: 20px;
  padding: 30px 34px;
  border: 1px solid #E2E8F0;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
}
.section-title {
  font-size: 18px; font-weight: 700; color: #0F172A; margin-bottom: 22px;
  display: flex; align-items: center; gap: 10px;
}
.section-title::before {
  content: '';
  width: 4px; height: 22px;
  background: linear-gradient(180deg, #4F46E5, #8B5CF6);
  border-radius: 2px;
  display: inline-block;
}
.quick-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 16px; }
.quick-card {
  display: flex; flex-direction: column; align-items: center; gap: 14px;
  padding: 24px 14px;
  background: #F8FAFC;
  border: 1px solid #F1F5F9;
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.25s ease;
  font-size: 14px; font-weight: 600;
  color: #475569;
  position: relative; overflow: hidden;
}
.quick-card::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 3px;
  background: linear-gradient(90deg, #4F46E5, #8B5CF6);
  opacity: 0; transition: opacity 0.25s ease;
  border-radius: 0 0 2px 2px;
}
.quick-card:hover { background: white; border-color: #4F46E5; transform: translateY(-4px); box-shadow: 0 8px 24px rgba(79,70,229,0.12); color: #4F46E5; }
.quick-card:hover::before { opacity: 1; }
.quick-icon { width: 56px; height: 56px; border-radius: 14px; display: flex; align-items: center; justify-content: center; }

@media (max-width: 768px) {
  .stats-grid { grid-template-columns: 1fr; }
  .quick-grid { grid-template-columns: repeat(2, 1fr); }
  .banner-content { flex-direction: column; text-align: center; gap: 16px; }
}
</style>
