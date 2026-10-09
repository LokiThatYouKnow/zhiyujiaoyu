
import { defineStore } from "pinia";
import type { course } from "../modules/course";
import type { R } from "../response/R";
import type { backData, backData2 } from "../views/admin/ResourceManager.vue";

export const allData = defineStore('data',{
  state: () => {
    return {
      teacher_KnowledgeOrganization_knowledgePoint_data: '',//KnowledgeOrganization.vue 
      teacher_KnowledgeOrganization_resourceName_data: '',//KnowledgeOrganization.vue 
      teacher_KnowledgeOrganization_createTime_data: '',//KnowledgeOrganization.vue 
      teacher_KnowledgeOrganization_data: '',//KnowledgeOrganization.vue 
      teacher_KnowledgeOrganization_request_data: '',//KnowledgeOrganization.vue 

      student_AnswerQuestion_messages_data: [] as any,//AnswerQuestion.vue
      student_AnswerQuestion_inputText_data: '',//AnswerQuestion.vue

      student_SelfPractise_course_data: '',//SelfPractise.vue
      student_SelfPractise_request_data: '',//SelfPractise.vue
      student_SelfPractise_questions_data: [] as any,//SelfPractise.vue
      student_SelfPractise_answers_data: [] as any,//SelfPractise.vue
      student_SelfPractise_question_index_data: 0,//SelfPractise.vue
      student_SelfPractise_student_answers_data: Array(12) as any,//SelfPractise.vue
      student_SelfPractise_student_backAnalyseAnswer_data: '',//SelfPractise.vue

      teacher_arrangeTime_course_data: '',//ArrangeTime.vue
      teacher_arrangeTime_data: '',//ArrangeTime.vue

      teacher_designIssue_teacherRequest_data: '',//DesignIssue.vue
      teacher_designIssue_issueName_data: '',//DesignIssue.vue
      teacher_designIssue_knowledgePoint_data: '',//DesignIssue.vue
      teacher_designIssue_data: '',//DesignIssue.vue
      teacher_designIssue_stratTime_data: '',//DesignIssue.vue
      teacher_designIssue_endTime_data: '',//DesignIssue.vue

      teacher_practiseDesign_knowledgePoint_data:'',//PractiseDesign.vue
      teacher_practiseDesign_resourceName_data:'',//PractiseDesign.vue
      teacher_practiseDesign_request_data:'',//PractiseDesign.vue
      teacher_practiseDesign_data:'',//PractiseDesign.vue
      teacher_practiseDesign_createTime_data: '',//KnowledgeOrganization.vue 

      teacher_AnalyzeAnswer_course_data: '',//AnalyzeAnswer.vue
      teacher_AnalyzeAnswer_data: '',//AnalyzeAnswer.vue

      student_coursePractise_coursePractiseList_data: [] as any,//CoursePractiseHome.vue
      student_coursePractiseHome_selectedCourseName_data: '',//CoursePractiseHome.vue
      student_coursePractise_selectedIssueJSONstring_data: '',//CoursePractise.vue 和 CoursePractiseHome.vue
      student_CoursePractise_course_data: '',//CoursePractise.vue
      student_CoursePractise_questions_data: [] as any,//CoursePractise.vue
      student_CoursePractise_answers_data: [] as any,//CoursePractise.vue
      student_CoursePractise_questionId_data: '',//CoursePractise.vue
      student_CoursePractise_teacherUsername_data: '',//CoursePractise.vue
      student_CoursePractise_currentCoursePractiseName_data: '',//CoursePractise.vue
      student_CoursePractise_question_index_data: 0,//CoursePractise.vue
      student_CoursePractise_student_coursePractiseList_data: new Map(),//CoursePractise.vue
      student_CoursePractise_student_backAnalyseAnswer_data: '',//CoursePractise.vue

      student_SelfStudentDetail_course_data: {} as course,//SelfStudentDetail.vue

      teacher_CourseList_addCourse_backResult_data: {} as R,//CourseList.vue:
      teacher_CourseList_addCourse_childKey_data: 0,//CourseList.vue:
      teacher_CourseList_CourseListKey_data: 0,//CourseList.vue:
      teacher_CourseList_issueList_data: [] as any,//CourseList.vue:
      teacher_CourseList_selectedCourseName_data: '',//CourseList.vue:
      teacher_CourseList_issueAnswerDetail_data: '',//CourseList.vue:
      teacher_CourseList_currentIssueDetailMK_data: '',//CourseList.vue:
      teacher_CourseList_resourceList_data: [] as backData2[],//CourseList.vue:
      teacher_CourseList_resourceDetail_data: '',//CourseList.vue:
      teacher_CourseList_resourceName_data: '',//CourseList.vue:
      teacher_CourseList_resourceId_data: '',//CourseList.vue:
      teacher_CourseList_createTime_data: '',//CourseList.vue:
      teacher_CourseList_currentResourceDetailMK_data: '',//CourseList.vue:
      teacher_CourseList_deleteQuestionId_data: '',//CourseList.vue:
      teacher_CourseList_deleteResourceId_data: '',//CourseList.vue:
      teacher_CourseList_deleteCourse_documentId_data: '',//CourseList.vue:
      teacher_CourseList_deleteCourse_teacherUsername_data: '',//CourseList.vue:
      teacher_CourseList_deleteCourse_courseName_data: '',//CourseList.vue:

      admin_ResourceManager_courseList_data: [] as course[],//ResourceManager.vue
      admin_ResourceManager_issueList_data:[] as backData[],//ResourceManager.vue
      admin_ResourceManager_selectedCourseName_data:'',//ResourceManager.vue
      admin_ResourceManager_issueDetail_data:'',//ResourceManager.vue
      admin_ResourceManager_issueName_data:'',//ResourceManager.vue
      admin_ResourceManager_currentIssueDetailMK_data:'',//ResourceManager.vue
      admin_ResourceManager_resourceList_data:[] as backData2[],//ResourceManager.vue
      admin_ResourceManager_resourceDetail_data:'',//ResourceManager.vue
      admin_ResourceManager_resourceName_data:'',//ResourceManager.vue
      admin_ResourceManager_currentResourceDetailMK_data:'',//ResourceManager.vue
      admin_ResourceManager_resourceId_data:'',//ResourceManager.vue
      admin_ResourceManager_createTime_data:'',//ResourceManager.vue
      
    }
  },
  actions: {
    setData(coursePractiseName:string, issue:Array<any>) {
      this.student_CoursePractise_student_coursePractiseList_data.set(coursePractiseName, issue);
    },

    getData(coursePractiseName:string) {
      return this.student_CoursePractise_student_coursePractiseList_data.get(coursePractiseName);
    },
  }
});