import { defineStore } from "pinia";

export const allStatus = defineStore('allStatus',{
  state: () => {
    return {

      student_AnswerQuestion_isSending: false,//AnswerQuestion.vue

      teacher_CourseList_addCourse_isSending: false,//CourseList.vue
      teacher_CourseList_addCourse_sendingOver: false,//CourseList.vue

      admin_AdminHome_collapse: true,//AdminHome.vue
    }
  },
});