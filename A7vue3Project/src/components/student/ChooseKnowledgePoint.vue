<template>
  <a-modal
    :open="allVisible().student_choose_knowledgePoint_visible"
    @ok="handleOk"
    @cancel="handleCancel"
    okText="确认选择"
    cancelText="暂不选择"
    centered
    :bodyStyle="{ padding: '24px 28px', overflow: 'hidden' }"
    class="course-picker-modal"
  >
    <template #title>
      <div class="modal-header-custom">
        <div class="modal-icon-wrap">
          <svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M4 19.5A2.5 2.5 0 0 0 6.5 22H20V2H6.5A2.5 2.5 0 0 0 4 4.5v15z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
        </div>
        <span>选择咨询课程</span>
      </div>
    </template>

    <div class="modal-body-custom">
      <p class="modal-desc">选择一门课程，AI 将基于该课程的知识范围为您答疑解惑。也可以稍后再选。</p>

      <div class="course-list-picker">
        <div
          v-for="course in courseOptions"
          :key="course.course_id"
          :class="['course-radio-card', { selected: knowledgePoint === (course.course_name as string) }]"
          @click="knowledgePoint = course.course_name as string"
        >
          <div class="radio-circle">
            <svg v-if="knowledgePoint === course.course_name" viewBox="0 0 24 24" fill="none" width="16" height="16"><circle cx="12" cy="12" r="10" fill="#0D9488"/><path d="M8 12l3 3 5-5" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
          </div>
          <div class="course-card-content">
            <span class="course-card-emoji">📚</span>
            <div>
              <div class="course-card-name">{{ course.course_name }}</div>
              <div class="course-card-teacher" v-if="course.course_teacher_name">{{ course.course_teacher_name }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="modal-hint" v-if="!knowledgePoint">
        <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/><path d="M12 16v-4M12 8h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
        暂不选择也可以关闭弹窗，需要时点击「切换课程」即可
      </div>
    </div>
  </a-modal>
</template>

<script lang="ts" setup>
import { allVisible } from '../../usePinia/allVisible'
import { ref } from 'vue';
import { message } from 'ant-design-vue';
import { allWait } from '../../usePinia/allWait';
import { jwtDecode } from 'jwt-decode';
import type { student } from '../../modules/student';
import type { course } from '../../modules/course';
import { getSelfStudentDetailApi } from '../../api/student/getSelfStudentDetailApi';
import type { R } from '../../response/R';

const knowledgePoint = ref('');
const courseOptions = ref<course[]>([]);

const token = ref((sessionStorage.getItem('token') || localStorage.getItem('token')))
const student_id = jwtDecode(token.value as string).sub as string
const studentDetail = ref<student>({
  student_id: 0, student_account: '', student_name: '', student_gender: '',
  student_age: '', student_classList: [], student_major: '', student_class_name: '',
});

// Load student data and course list
const loadStudentData = async () => {
  if ((sessionStorage.getItem('studentDetail') || localStorage.getItem('studentDetail'))) {
    studentDetail.value = JSON.parse((sessionStorage.getItem('studentDetail') || localStorage.getItem('studentDetail')) as string);
    courseOptions.value = studentDetail.value.student_classList || [];
  } else {
    try {
      const res = await getSelfStudentDetailApi(student_id);
      const R: R = res.data;
      if (R.code === 200) {
        studentDetail.value = R.data;
        sessionStorage.setItem('studentDetail', JSON.stringify(studentDetail.value));
        courseOptions.value = studentDetail.value.student_classList || [];
      }
    } catch (err) {
      console.log(err);
    }
  }
};

loadStudentData();

// Pre-select previously saved course
const savedCourse = localStorage.getItem('knowledgePoint');
if (savedCourse) {
  knowledgePoint.value = savedCourse;
}

const handleOk = () => {
  if (knowledgePoint.value) {
    localStorage.setItem('knowledgePoint', knowledgePoint.value);
    allVisible().student_choose_knowledgePoint_visible = false;
    allWait().student_shift_knowledgePoint_wait = true;
    message.success('当前课程：' + knowledgePoint.value, 2);
  } else {
    // Allow closing without selection — just close
    allVisible().student_choose_knowledgePoint_visible = false;
  }
};

const handleCancel = () => {
  // Always allow closing the modal, regardless of whether a course is selected
  allVisible().student_choose_knowledgePoint_visible = false;
  if (!localStorage.getItem('knowledgePoint')) {
    message.info('您可以稍后点击「切换课程」来选择课程', 2);
  }
};
</script>

<style scoped>
/* ===== Modal Centering ===== */
:deep(.ant-modal) {
  top: 0 !important;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding-bottom: 0;
}
:deep(.ant-modal-content) {
  max-height: 85vh;
  display: flex;
  flex-direction: column;
}
:deep(.ant-modal-body) {
  overflow: hidden;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

/* Modal header */
.modal-header-custom {
  display: flex; align-items: center; gap: 12px;
  font-size: 18px; font-weight: 700; color: #0F172A;
}
.modal-icon-wrap {
  width: 38px; height: 38px;
  border-radius: 10px;
  background: linear-gradient(135deg, #0D9488, #14B8A6);
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 3px 10px rgba(13,148,136,0.25);
}

/* Body */
.modal-body-custom {
  display: flex;
  flex-direction: column;
  min-height: 0;
  flex: 1;
}
.modal-desc {
  margin: 0 0 16px;
  font-size: 14px; color: #475569; line-height: 1.6;
  flex-shrink: 0;
}

/* Scrollable course list */
.course-list-picker {
  display: flex; flex-direction: column; gap: 10px;
  margin-bottom: 16px;
  flex: 1;
  overflow-y: auto;
  padding-right: 6px;
  border: 1px solid #E2E8F0;
  border-radius: 14px;
  padding: 12px;
  background: #F8FAFC;
  min-height: 140px;
  max-height: 360px;
}
.course-list-picker::-webkit-scrollbar { width: 5px; }
.course-list-picker::-webkit-scrollbar-track {
  background: transparent;
  margin: 6px 0;
}
.course-list-picker::-webkit-scrollbar-thumb {
  background: #CBD5E1;
  border-radius: 10px;
}
.course-list-picker::-webkit-scrollbar-thumb:hover {
  background: #94A3B8;
}

.course-radio-card {
  display: flex; align-items: center; gap: 14px;
  padding: 14px 18px;
  border: 1px solid #E2E8F0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  background: white;
  flex-shrink: 0;
}
.course-radio-card:hover {
  border-color: #0D9488;
  background: rgba(13,148,136,0.03);
}
.course-radio-card.selected {
  border-color: #0D9488;
  background: linear-gradient(135deg, rgba(13,148,136,0.06), rgba(6,182,212,0.04));
  box-shadow: 0 2px 12px rgba(13,148,136,0.1);
}
.radio-circle {
  width: 24px; height: 24px;
  border-radius: 50%;
  border: 2px solid #CBD5E1;
  flex-shrink: 0;
  display: flex; align-items: center; justify-content: center;
  transition: all 0.25s;
}
.course-radio-card.selected .radio-circle {
  border-color: #0D9488;
}
.course-card-content {
  display: flex; align-items: center; gap: 12px;
  flex: 1; min-width: 0;
}
.course-card-emoji { font-size: 24px; flex-shrink: 0; }
.course-card-name { font-size: 15px; font-weight: 700; color: #0F172A; }
.course-card-teacher { font-size: 12px; color: #94A3B8; margin-top: 2px; }

/* Hint */
.modal-hint {
  display: flex; align-items: center; gap: 8px;
  font-size: 12px; color: #94A3B8;
  padding: 10px 14px;
  background: #F8FAFC;
  border-radius: 10px;
  flex-shrink: 0;
}
.modal-hint svg { flex-shrink: 0; color: #94A3B8; }
</style>
