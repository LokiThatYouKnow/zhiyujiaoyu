<template>
    <div class="page-container">
        <div class="content-wrapper">
            <div class="header-section">
                <h2 class="header-title">教学时间安排</h2>
                <p class="header-subtitle">对指定课程最近一次知识梳理进行合理的教学时间安排</p>
            </div>

            <div class="info-message">
                <p><i class="fa fa-info-circle mr-2"></i>系统已根据知识内容复杂度、重要程度和学习曲线，自动计算出最优教学时间分配方案。</p>
            </div>

            <div class="form-group">
                <label for="courseSelect" class="form-label">选择课程</label>
                <select id="courseSelect" class="form-input" v-model="selectedCourse" required>
                    <option value="" disabled>请选择课程</option>
                    <option v-for="course in courses" :key="course.course_id" :value="course.course_name">
                        {{ course.course_name }}
                    </option>
                </select>
                <div id="courseError" class="error-message"></div>
            </div>

            <button id="generateBtn" class="action-button" @click="generateTimeSchedule" :disabled="!selectedCourse">
                <i class="fa fa-check-circle mr-2"></i>
                确定生成时间安排
            </button>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { allVisible } from '../../usePinia/allVisible';
import { allWait } from '../../usePinia/allWait';
import { ref } from 'vue';
import type { teacher } from '../../modules/teacher';
import { jwtDecode } from 'jwt-decode';
import { getSelfTeacherDetailApi } from '../../api/teacher/getSelfTeacherDetailApi';
import type { R } from '../../response/R';
import { message } from 'ant-design-vue';
import type { course } from '../../modules/course';
import { allData } from '../../usePinia/allData';

// 添加选中课程状态
const selectedCourse = ref(allData().teacher_arrangeTime_course_data);

const generateTimeSchedule = () => {
    if (!selectedCourse.value) return;
    allData().teacher_arrangeTime_course_data = selectedCourse.value;
    allVisible().teacher_arrangeTime_home_visible = false;
    allVisible().teacher_arrangeTime_loading_visible = true;
    allWait().teacher_get_arrangeTime_wait = true;
}

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
    teacher_class_name:'',
    teacher_courseList: [],
    teacher_courseResource: [],
})

const courses = ref<course[]>([])

if ((sessionStorage.getItem('teacherDetail') || localStorage.getItem('teacherDetail'))) {
    teacherDetail.value = JSON.parse((sessionStorage.getItem('teacherDetail') || localStorage.getItem('teacherDetail')) as string)
    teacherDetail.value.teacher_courseList?.forEach(course => {
        courses.value.push(course)
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
</script>

<style scoped>
 .page-container {
     font-family: Arial, sans-serif;
     min-height: 20vh;
     margin-top: 8%;
     padding: 20px;
     background-color: white;
     display: flex;
     justify-content: center;
     align-items: center;
 }

 .content-wrapper {
     background-color: white;
     border-radius: 8px;
     box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
     padding: 20px;
     max-width: 600px;
     width: 100%;
 }

 .header-section {
     text-align: center;
     margin-bottom: 20px;
 }

 .icon-container {
     width: 60px;
     height: 60px;
     background-color: #e6f7ff;
     border-radius: 50%;
     display: flex;
     align-items: center;
     justify-content: center;
     margin: 0 auto 15px;
 }

 .icon-container i {
     color: #165DFF;
     font-size: 24px;
 }

 .header-title {
     color: #333;
     margin: 0 0 8px;
     font-size: 20px;
 }

 .header-subtitle {
     color: #666;
     margin: 0;
     font-size: 14px;
 }

 .info-message {
     background-color: #e6f7ff;
     border-left: 4px solid #165DFF;
     padding: 12px;
     margin-bottom: 20px;
     border-radius: 0 4px 4px 0;
     font-size: 14px;
 }

 .action-button {
     width: 100%;
     background-color: #165DFF;
     color: white;
     border: none;
     padding: 12px;
     border-radius: 4px;
     cursor: pointer;
     font-size: 16px;
     margin-top: 10px;
 }

 .action-button:hover {
     background-color: #0047cc;
 }

 .action-button:disabled {
     background-color: #7aa3ff;
     cursor: not-allowed;
 }

 /* 新增下拉框样式 */
 .form-group {
    margin-bottom: 20px;
 }

 .form-label {
    display: block;
    margin-bottom: 8px;
    color: #555;
    font-weight: 500;
 }

 .form-input {
    width: 100%;
    padding: 12px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 16px;
    appearance: none;
    background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 20 20'%3e%3cpath stroke='%236b7280' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.5' d='M6 8l4 4 4-4'/%3e%3c/svg%3e");
    background-repeat: no-repeat;
    background-position: right 0.75rem center;
    background-size: 1.5em 1.5em;
 }

 .form-input:focus {
    outline: none;
    border-color: #165DFF;
    box-shadow: 0 0 0 2px rgba(22, 93, 255, 0.2);
 }

 .error-message {
    color: #ff4d4f;
    font-size: 14px;
    margin-top: 5px;
 }
</style>