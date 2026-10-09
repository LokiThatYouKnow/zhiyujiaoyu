/**
 * 作业/考试提交与批改 API
 */
import axios from '../utils/axios'

/** 学生提交答案 */
export const submitAnswerApi = async (params: {
  homework_id: string
  type: 'homework' | 'exam'
  student_username: string
  student_name: string
  course_name: string
  issue_name: string
  answers: string       // JSON: [{"questionId":"","answer":""}]
  scores: string        // JSON: [{"questionId":"","score":0,"maxScore":0}]
}) => (await axios.post('/student/submitAnswer', params, { timeout: 30000 }))

/** 学生获取已发布的作业/考试列表 */
export const getHomeworkPublishedApi = async (params: { course_name?: string }) =>
  (await axios.post('/student/getHomeworkPublished', params, { timeout: 30000 }))

/** 教师将作业/考试发布到学生端 */
export const publishHomeworkApi = async (params: {
  homework_id: string
  type: 'homework' | 'exam'
  teacher_username: string
  course_name: string
  title: string
  description: string
  questions_json: string
  deadline?: string
  end_time?: string
  duration?: string
}) => (await axios.post('/teacher/publishHomeworkToStudents', params, { timeout: 30000 }))

/** 教师获取某次作业的所有提交 */
export const getSubmissionsApi = async (params: { homework_id: string }) =>
  (await axios.post('/teacher/getSubmissions', params, { timeout: 30000 }))

/** 教师获取单份提交详情 */
export const getSubmissionDetailApi = async (params: { submission_id: string }) =>
  (await axios.post('/teacher/getSubmissionDetail', params, { timeout: 30000 }))

/** 教师批改并保存分数 */
export const gradeSubmissionApi = async (params: {
  submission_id: string
  scores: string         // JSON
  total_score: string
  max_total_score: string
}) => (await axios.post('/teacher/gradeSubmission', params, { timeout: 30000 }))

/** AI 自动批改 */
export const aiAutoGradeApi = async (params: { homework_id: string; model?: string }) =>
  (await axios.post('/teacher/aiAutoGrade', params, { timeout: 120000 }))

/** 获取课程已发布的作业/考试列表 */
export const getPublishedAssignmentsApi = async (params: { course_name: string }) =>
  (await axios.post('/teacher/getPublishedAssignments', params, { timeout: 15000 }))

/** 获取成绩汇总 */
export const getGradeSummaryApi = async (params: { course_name?: string }) =>
  (await axios.post('/teacher/getGradeSummary', params, { timeout: 30000 }))

/** 学生获取自己的提交记录（含批改结果） */
export const getMySubmissionsApi = async (params: { student_username: string }) =>
  (await axios.post('/student/getMySubmissions', params, { timeout: 30000 }))

/** 教师撤回单份批改 — 恢复到已提交状态 */
export const retractGradeApi = async (params: { submission_id: string }) =>
  (await axios.post('/teacher/retractGrade', params, { timeout: 15000 }))

/** 教师撤回某作业的全部批改 */
export const retractAllGradesApi = async (params: { homework_id: string }) =>
  (await axios.post('/teacher/retractAllGrades', params, { timeout: 15000 }))

/** 学生撤回已提交的作业/考试 */
export const deleteSubmissionApi = async (params: { homework_id: string; student_username: string }) =>
  (await axios.post('/student/deleteSubmission', params, { timeout: 15000 }))
