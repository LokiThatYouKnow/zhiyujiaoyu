<template>
    <div class="sp-root">
        <!-- Header -->
        <div class="sp-header">
            <button class="sp-back" @click="backToHome">
                <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M19 12H5M12 19l-7-7 7-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                返回
            </button>
            <div class="sp-header-center">
                <div class="sp-header-icon">
                    <svg viewBox="0 0 24 24" fill="none" width="22" height="22"><path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                </div>
                <h1 class="sp-header-title">针对性训练</h1>
            </div>
            <div class="sp-header-spacer"></div>
        </div>

        <!-- Body -->
        <div class="sp-body">
            <div class="sp-panel">
                <!-- Info strip -->
                <div class="sp-info">
                    <svg viewBox="0 0 24 24" fill="none" width="20" height="20"><circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/><path d="M12 16v-4M12 8h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
                    <span>AI 将结合您的往期错题数据，生成针对性强化练习题</span>
                </div>

                <!-- Form -->
                <div class="sp-form-area">
                    <!-- Course Select -->
                    <div class="sp-field">
                        <label class="sp-label">选择课程</label>
                        <a-select
                            v-model:value="selectedCourse"
                            placeholder="请选择要练习的课程"
                            size="large"
                            :class="['sp-select', { 'sp-select-filled': selectedCourse }]"
                        >
                            <a-select-option v-for="course in studentDetail.student_classList" :key="course.course_id"
                                :value="course.course_name">
                                <span style="display:flex;align-items:center;gap:8px;">
                                    <span style="font-size:18px;">📚</span> {{ course.course_name }}
                                </span>
                            </a-select-option>
                        </a-select>
                    </div>

                    <!-- Request Input -->
                    <div class="sp-field">
                        <label class="sp-label">出题需求</label>
                        <textarea
                            v-model="studentRequest"
                            class="sp-textarea"
                            placeholder="描述您希望重点训练的知识点方向，例如：三角函数综合应用、现在完成时时态辨析、牛顿第二定律经典题型..."
                            rows="4"
                        ></textarea>
                        <p class="sp-hint">描述越具体，生成的题目越精准匹配您的学习需求</p>
                    </div>

                    <!-- Generate -->
                    <button class="sp-btn" @click="generateQuestion" :disabled="!selectedCourse">
                        <svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                        <span>开始生成题目</span>
                    </button>
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
import { getSelfStudentDetailApi } from '../../api/student/getSelfStudentDetailApi';
import { jwtDecode } from 'jwt-decode';
import type { R } from '../../response/R';
import type { student } from '../../modules/student';
import { message } from 'ant-design-vue';
import router from '../../routers/appRouters';

const studentRequest = ref(allData().student_SelfPractise_request_data);
const selectedCourse = ref(allData().student_SelfPractise_course_data);

const generateQuestion = () => {
    if (studentRequest.value === '' || studentRequest.value === null) {
        message.error('请输入你的出题需求')
        return
    }
    allData().student_SelfPractise_course_data = selectedCourse.value;
    allData().student_SelfPractise_request_data = studentRequest.value;
    allVisible().student_selfPractise_home_visible = false;
    allVisible().student_selfPractise_loading_visible = true;
    allWait().student_get_SelfPractise_wait = true;
};

const token = ref((sessionStorage.getItem('token') || localStorage.getItem('token')))
const student_id = jwtDecode(token.value as string).sub as string
const studentDetail = ref<student>({
    student_id: 0, student_account: '', student_name: '', student_gender: '',
    student_age: '', student_classList: [], student_major: '', student_class_name: '',
})

if ((sessionStorage.getItem('studentDetail') || localStorage.getItem('studentDetail'))) {
    studentDetail.value = JSON.parse((sessionStorage.getItem('studentDetail') || localStorage.getItem('studentDetail')) as string)
} else {
    getSelfStudentDetailApi(student_id).then(res => {
        const R: R = res.data
        if (R.code === 200) {
            studentDetail.value = R.data
            sessionStorage.setItem('studentDetail', JSON.stringify(studentDetail.value))
        } else { message.error('获取学生信息失败') }
    }).catch(err => { message.error('获取学生信息失败'); console.log(err) })
}

const backToHome = () => { router.push({ name: 'StudentHomeView' }) }
</script>

<style scoped>
/* ===== Root Container ===== */
.sp-root {
    height: 100%;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    background: linear-gradient(180deg, #F0FDF9 0%, #F8FAFC 30%, #FFFFFF 100%);
}

/* ===== Header ===== */
.sp-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px 28px;
    background: linear-gradient(135deg, #0D9488 0%, #14B8A6 50%, #06B6D4 100%);
    box-shadow: 0 4px 20px rgba(13,148,136,0.12);
    flex-shrink: 0;
    z-index: 10;
}
.sp-back {
    display: inline-flex; align-items: center; gap: 6px;
    padding: 8px 16px;
    background: rgba(255,255,255,0.15);
    backdrop-filter: blur(6px);
    border: 1px solid rgba(255,255,255,0.2);
    color: white; border-radius: 10px;
    font-size: 13px; font-weight: 600; cursor: pointer;
    transition: all 0.25s;
}
.sp-back:hover { background: rgba(255,255,255,0.28); }
.sp-header-center {
    display: flex; align-items: center; gap: 12px;
}
.sp-header-icon {
    width: 40px; height: 40px;
    border-radius: 12px;
    background: rgba(255,255,255,0.2);
    display: flex; align-items: center; justify-content: center;
    color: white;
}
.sp-header-title {
    font-size: 20px; font-weight: 800; color: white;
    margin: 0; letter-spacing: 1px;
}
.sp-header-spacer { width: 80px; }

/* ===== Body ===== */
.sp-body {
    flex: 1;
    overflow-y: auto;
    padding: 28px 24px 40px;
}
.sp-body::-webkit-scrollbar { width: 6px; }
.sp-body::-webkit-scrollbar-track { background: transparent; }
.sp-body::-webkit-scrollbar-thumb { background: #CBD5E1; border-radius: 10px; }
.sp-body::-webkit-scrollbar-thumb:hover { background: #94A3B8; }

.sp-panel {
    max-width: 720px;
    margin: 0 auto;
}

/* ===== Info ===== */
.sp-info {
    display: flex; align-items: center; gap: 12px;
    padding: 16px 20px;
    background: linear-gradient(135deg, rgba(13,148,136,0.06), rgba(6,182,212,0.05));
    border: 1px solid rgba(13,148,136,0.1);
    border-radius: 14px;
    margin-bottom: 28px;
    font-size: 14px; color: #334155; line-height: 1.6;
}
.sp-info svg { color: #0D9488; flex-shrink: 0; }

/* ===== Form ===== */
.sp-form-area {
    background: white;
    border-radius: 20px;
    padding: 32px;
    box-shadow: 0 1px 6px rgba(0,0,0,0.04);
    border: 1px solid #F1F5F9;
}
.sp-field { margin-bottom: 24px; }
.sp-label {
    display: block;
    font-size: 14px; font-weight: 700; color: #0F172A;
    margin-bottom: 10px;
    letter-spacing: 0.3px;
}

/* Select */
.sp-select {
    width: 100%;
}
:deep(.sp-select .ant-select-selector) {
    border-radius: 12px !important;
    border: 2px solid #E2E8F0 !important;
    padding: 10px 16px !important;
    height: auto !important;
    min-height: 50px !important;
    transition: all 0.3s !important;
    background: #F8FAFC !important;
    display: flex !important;
    align-items: center !important;
}
:deep(.sp-select .ant-select-selector:hover) {
    border-color: #0D9488 !important;
    background: white !important;
}
:deep(.sp-select.ant-select-focused .ant-select-selector) {
    border-color: #0D9488 !important;
    box-shadow: 0 0 0 4px rgba(13,148,136,0.08) !important;
    background: white !important;
}
:deep(.sp-select-filled .ant-select-selector) {
    border-color: #0D9488 !important;
    background: rgba(13,148,136,0.02) !important;
}

/* Textarea */
.sp-textarea {
    width: 100%;
    padding: 16px 20px;
    border: 2px solid #E2E8F0;
    border-radius: 14px;
    font-size: 14px; line-height: 1.7;
    font-family: inherit;
    resize: vertical;
    outline: none;
    background: #F8FAFC;
    color: #0F172A;
    transition: all 0.3s;
}
.sp-textarea:focus {
    border-color: #0D9488;
    box-shadow: 0 0 0 4px rgba(13,148,136,0.08);
    background: white;
}
.sp-textarea::placeholder { color: #CBD5E1; }
.sp-hint {
    margin: 8px 0 0;
    font-size: 12px; color: #94A3B8;
}

/* Button */
.sp-btn {
    width: 100%;
    display: flex; align-items: center; justify-content: center; gap: 10px;
    padding: 16px;
    background: linear-gradient(135deg, #0D9488, #14B8A6);
    color: white;
    border: none;
    border-radius: 16px;
    font-size: 16px; font-weight: 700;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 6px 22px rgba(13,148,136,0.28);
    margin-top: 8px;
    letter-spacing: 0.5px;
}
.sp-btn:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 10px 30px rgba(13,148,136,0.38);
}
.sp-btn:active:not(:disabled) { transform: translateY(0); }
.sp-btn:disabled {
    background: #CBD5E1;
    cursor: not-allowed;
    box-shadow: none;
    color: #94A3B8;
}

@media (max-width: 768px) {
    .sp-header { padding: 12px 16px; }
    .sp-header-spacer { width: 0; }
    .sp-header-title { font-size: 17px; }
    .sp-body { padding: 16px 12px 32px; }
    .sp-form-area { padding: 20px; border-radius: 16px; }
}
</style>
