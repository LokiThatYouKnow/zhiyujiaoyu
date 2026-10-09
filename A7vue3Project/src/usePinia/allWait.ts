import { defineStore } from "pinia";

//用于等待延时改变
export const allWait = defineStore('wait',{
  state: () => {
    return {
      student_shift_knowledgePoint_wait: false,

      student_get_SelfPractise_wait: false,

      student_get_CoursePractise_wait: false,

      teacher_get_konwledgeOrganization_wait: false,
      
      teacher_get_practiseDesign_wait: false,

      teacher_get_arrangeTime_wait: false,

      teacher_get_designIssue_wait: false,

      teacher_get_AnalyzeAnswer_wait: false,

    }
  },
});