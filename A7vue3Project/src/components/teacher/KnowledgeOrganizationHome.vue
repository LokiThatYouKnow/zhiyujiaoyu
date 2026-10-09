<template>
    <div class="ai-page">
        <div class="ai-container">
            <!-- Header -->
            <div class="ai-header">
                <div class="ai-header-icon">📚</div>
                <h1 class="ai-title">知识梳理</h1>
                <p class="ai-desc">选择课程，系统将基于课程内容自动设计教学方案，包括知识讲解、实训练习与时间分布</p>
            </div>

            <!-- Form Card -->
            <div class="ai-card">
                <div class="form-field">
                    <label class="field-label">
                        <span class="field-icon">📖</span> 选择课程
                    </label>
                    <select class="field-select" v-model="knowledgePoint" required>
                        <option value="" disabled>请选择课程</option>
                        <option v-for="course in courses" :key="course.course_id" :value="course.course_name">
                            {{ course.course_name }}
                        </option>
                    </select>
                    <span class="field-hint">只能选择你创建的课程</span>
                </div>

                <div class="form-field">
                    <label class="field-label">
                        <span class="field-icon">📝</span> 课件名称
                    </label>
                    <input class="field-input" placeholder="为本次课件取个名字..." v-model="resourceName" />
                </div>

                <div class="form-field">
                    <label class="field-label">
                        <span class="field-icon">💡</span> 详细需求
                    </label>
                    <textarea class="field-textarea" placeholder="请输入你的详细需求，如无特殊需求则按课程总体内容生成课件..."
                        v-model="request" rows="3"></textarea>
                </div>

                <div class="form-actions">
                    <a-button class="back-btn-ghost" @click="backToHome">
                        ← 返回首页
                    </a-button>
                    <button class="submit-btn" @click="getQuestion" :disabled="!knowledgePoint || !resourceName">
                        <span>✨</span> 开始智能总结
                    </button>
                </div>
            </div>

            <!-- Feature hints -->
            <div class="feature-hints">
                <div class="hint-item">
                    <span class="hint-icon">🧠</span>
                    <span>自动梳理核心知识点</span>
                </div>
                <div class="hint-item">
                    <span class="hint-icon">📊</span>
                    <span>结构化教学方案输出</span>
                </div>
                <div class="hint-item">
                    <span class="hint-icon">⏱</span>
                    <span>合理分配教学时间</span>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { allVisible } from '../../usePinia/allVisible';
import { allWait } from '../../usePinia/allWait';
import { allData } from '../../usePinia/allData';
import type { teacher } from '../../modules/teacher';
import { jwtDecode } from 'jwt-decode';
import { getSelfTeacherDetailApi } from '../../api/teacher/getSelfTeacherDetailApi';
import type { R } from '../../response/R';
import { message } from 'ant-design-vue';
import type { course } from '../../modules/course';
import router from '../../routers/appRouters';

const knowledgePoint = ref(allData().teacher_KnowledgeOrganization_knowledgePoint_data);
const resourceName = ref(allData().teacher_KnowledgeOrganization_resourceName_data);
const request = ref(allData().teacher_KnowledgeOrganization_request_data)

const currentTime = ref('');

const updateCurrentTime = () => {
    const now = new Date();
    now.setMinutes(now.getMinutes());
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    currentTime.value = `${year}-${month}-${day} ${hours}:${minutes}`;
};

const getQuestion = () => {
    if (knowledgePoint.value === '') return
    if (resourceName.value === '') return
    allData().teacher_KnowledgeOrganization_resourceName_data = resourceName.value;
    allData().teacher_KnowledgeOrganization_knowledgePoint_data = knowledgePoint.value;
    allData().teacher_KnowledgeOrganization_request_data = request.value;
    updateCurrentTime();
    allData().teacher_KnowledgeOrganization_createTime_data = currentTime.value;
    allVisible().teacher_KnowledgeOrganization_home_visible = false;
    allVisible().teacher_KnowledgeOrganization_loading_visible = true;
    allWait().teacher_get_konwledgeOrganization_wait = true;
};

const token = ref((sessionStorage.getItem('token') || localStorage.getItem('token')))
const teacher_id = jwtDecode(token.value as string).sub as string
const teacherDetail = ref<teacher>({
    teacher_id: 0, teacher_account: '', teacher_password: '',
    teacher_name: '', teacher_gender: '', teacher_age: '',
    teacher_major: '', teacher_position: '', teacher_class_name: '',
    teacher_courseList: [], teacher_courseResource: [],
})

const courses = ref<course[]>([])

if ((sessionStorage.getItem('teacherDetail') || localStorage.getItem('teacherDetail'))) {
    teacherDetail.value = JSON.parse((sessionStorage.getItem('teacherDetail') || localStorage.getItem('teacherDetail')) as string)
    teacherDetail.value.teacher_courseList?.forEach(course => { courses.value.push(course) })
} else {
    getSelfTeacherDetailApi(teacher_id).then(res => {
        const R: R = res.data
        if (R.code === 200) {
            teacherDetail.value = R.data
            sessionStorage.setItem('teacherDetail', JSON.stringify(teacherDetail.value))
        } else { message.error('获取教师信息失败') }
    }).catch(err => { message.error('获取教师信息失败'); console.log(err) })
}

const backToHome = () => { router.push({ name: 'TeacherHomeView' }) }
</script>

<style scoped>
/* ===== Page ===== */
.ai-page {
    height: 100%; overflow-y: auto; padding: 40px 32px;
    display: flex; justify-content: center;
    background: linear-gradient(180deg, #EEF2FF 0%, #F1F5F9 100%);
}
.ai-container { width: 100%; max-width: 640px; display: flex; flex-direction: column; gap: 24px; }

/* ===== Header ===== */
.ai-header { text-align: center; padding: 8px 0; animation: fadeInUp 0.5s ease forwards; }
.ai-header-icon { font-size: 52px; margin-bottom: 16px; display: block; line-height: 1.3; }
.ai-title { font-size: 28px; font-weight: 800; color: #0F172A; letter-spacing: -0.5px; margin: 0 0 10px; }
.ai-desc { font-size: 15px; color: #64748B; line-height: 1.6; max-width: 480px; margin: 0 auto; }

/* ===== Form Card ===== */
.ai-card {
    background: #FFFFFF; border-radius: 20px; padding: 32px;
    box-shadow: 0 4px 24px rgba(0,0,0,0.04), 0 1px 4px rgba(0,0,0,0.03);
    border: 1px solid #E2E8F0; display: flex; flex-direction: column; gap: 24px;
    animation: fadeInUp 0.5s ease 0.1s forwards; opacity: 0;
}

@keyframes fadeInUp {
    from { opacity: 0; transform: translateY(16px); }
    to { opacity: 1; transform: translateY(0); }
}

/* ===== Fields ===== */
.form-field { display: flex; flex-direction: column; gap: 10px; }
.field-label { font-size: 14px; font-weight: 700; color: #0F172A; display: flex; align-items: center; gap: 8px; }
.field-icon { font-size: 18px; }
.field-hint { font-size: 12px; color: #94A3B8; font-weight: 500; }

.field-select {
    width: 100%; padding: 14px 18px; border: 2px solid #E2E8F0; border-radius: 14px;
    font-size: 15px; font-family: var(--font-family); background: #F8FAFC; color: #0F172A;
    appearance: none;
    background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 20 20'%3e%3cpath stroke='%2394A3B8' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.5' d='M6 8l4 44-4'/%3e%3c/svg%3e");
    background-repeat: no-repeat; background-position: right 18px center; background-size: 1.4em 1.4em;
    transition: all 0.2s ease; cursor: pointer;
}
.field-select:focus {
    outline: none; border-color: #6366F1;
    box-shadow: 0 0 0 4px rgba(99,102,241,0.1); background: #FFFFFF;
}

.field-input, .field-textarea {
    width: 100%; padding: 14px 18px; border: 2px solid #E2E8F0; border-radius: 14px;
    font-size: 15px; font-family: var(--font-family); background: #F8FAFC; color: #0F172A;
    transition: all 0.2s ease;
}
.field-textarea { resize: vertical; min-height: 90px; }
.field-input:focus, .field-textarea:focus {
    outline: none; border-color: #6366F1;
    box-shadow: 0 0 0 4px rgba(99,102,241,0.1); background: #FFFFFF;
}
.field-input::placeholder, .field-textarea::placeholder { color: #94A3B8; }

/* ===== Actions ===== */
.form-actions { display: flex; justify-content: space-between; align-items: center; gap: 12px; padding-top: 4px; }

.back-btn-ghost {
    border-radius: 999px !important; font-weight: 600 !important; padding: 0 22px !important;
    height: 44px !important; color: #64748B !important; border: 2px solid #E2E8F0 !important;
    background: #FFFFFF !important; transition: all 0.25s ease !important;
}
.back-btn-ghost:hover { border-color: #6366F1 !important; color: #4F46E5 !important; }

.submit-btn {
    padding: 0 32px; height: 44px;
    background: linear-gradient(135deg, #6366F1, #4F46E5);
    color: white; border: none; border-radius: 999px; font-size: 15px; font-weight: 700;
    font-family: var(--font-family); cursor: pointer; transition: all 0.3s ease;
    box-shadow: 0 4px 16px rgba(99,102,241,0.35);
    display: flex; align-items: center; gap: 8px; white-space: nowrap;
}
.submit-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 28px rgba(99,102,241,0.45);
    background: linear-gradient(135deg, #4F46E5, #4338CA);
}
.submit-btn:disabled { background: #CBD5E1; box-shadow: none; cursor: not-allowed; transform: none; }

/* ===== Feature Hints ===== */
.feature-hints {
    display: flex; justify-content: center; gap: 20px; flex-wrap: wrap; padding: 8px 0;
    animation: fadeInUp 0.5s ease 0.2s forwards; opacity: 0;
}
.hint-item {
    display: flex; align-items: center; gap: 8px;
    font-size: 13px; font-weight: 500; color: #475569;
    padding: 12px 18px; background: #FFFFFF; border-radius: 999px;
    border: 1px solid #E2E8F0; transition: all 0.2s ease;
}
.hint-item:hover {
    border-color: #A5B4FC; box-shadow: 0 2px 8px rgba(99,102,241,0.1);
    transform: translateY(-1px);
}
.hint-icon { font-size: 18px; }

@media (max-width: 600px) {
    .ai-page { padding: 20px 16px; }
    .ai-card { padding: 24px 20px; }
    .feature-hints { flex-direction: column; align-items: center; }
}
</style>
