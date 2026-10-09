
import { defineStore } from "pinia";

export const allVisible = defineStore('allVisible',{
  state: () => {
    return {

      student_choose_knowledgePoint_visible: false,

      student_SelfStudentDetail_visible: true,
      student_SelfStudentDetail_CourseDetail_visible: false,

      student_selfPractise_home_visible: true,
      student_selfPractise_loading_visible: false,
      student_selfPractise_question_visible:false,
      student_selfPractise_checkAnswer_visible: false,
      student_selfPractise_submitAnswer_loading_visible: false,
      student_selfPractise_backAnalyseAnswer_visible: false,

      teacher_KnowledgeOrganization_home_visible: true,
      teacher_KnowledgeOrganization_loading_visible: false,
      
      teacher_practiseDesign_home_visible: true,
      teacher_practiseDesign_loading_visible: false,
      teacher_practiseDesign_MK_visible: false,

      teacher_arrangeTime_home_visible:true,
      teacher_arrangeTime_loading_visible:false,

      teacher_designIssue_home_visible: true,
      teacher_designIssue_loading_visible: false,
      teacher_designIssue_resultMK_visible: false,

      teacher_AnalyzeAnswer_home_visible: true,
      teacher_AnalyzeAnswer_loading_visible: false,

      student_CoursePractise_home_visible: true,
      student_CoursePractise_courseList_visible: true,
      student_CoursePractise_issueList_loading_visible: false,
      student_CoursePractise_issueList_visible: false,
      student_CoursePractise_question_visible: false,
      student_CoursePractise_checkAnswer_visible: false,
      student_CoursePractise_submitAnswer_loading_visible: false,
      student_CoursePractise_backAnalyseAnswer_visible: false,
      student_CoursePractise_joinCourseTable_visible:false,

      teacher_CourseList_home_visible:true,
      teacher_CourseList_addCourse_visible: false,
      teacher_CourseList_toIssueList_loading_visible: false,
      teacher_CourseList_issueList_visible: false,
      teacher_CourseList_issueDetail_visible: false,
      teacher_CourseList_issueDetail_exportTable_visible: false,
      teacher_CourseList_studentAnswerDetail_visible:false,
      teacher_CourseList_studentAnswerDetail_loading_visible:false,
      teacher_CourseList_resourceList_loading_visible:false,
      teacher_CourseList_resourceList_visible:false,
      teacher_CourseList_resourceDetail_visible:false,
      teacher_CourseList_resourceDetail_exportTable_visible:false,
      teacher_CourseList_CourseData_visible:false,

      admin_ResourceManager_visible: true,
      admin_ResourceManager_courseList_visible: false,
      admin_ResourceManager_issueList_visible: false,
      admin_ResourceManager_issueList_loading_visible: false,
      admin_ResourceManager_issueDetail_visible: false,
      admin_ResourceManager_issueDetail_exportTable_visible: false,
      admin_ResourceManager_resourceList_visible: false,
      admin_ResourceManager_resourceList_loading_visible: false,
      admin_ResourceManager_resourceDetail_visible: false,
      admin_ResourceManager_resourceDetail_exportTable_visible: false,

      admin_TeacherList_visible: true,

    }
  },
});