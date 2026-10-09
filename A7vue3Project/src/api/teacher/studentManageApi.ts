import axios from '../../utils/axios'

export const listStudentsApi = async (params: { course_name?: string; teacher_username: string }) =>
  (await axios.get('/teacher/students/list', { params, timeout: 15000 }))

export const addStudentApi = async (data: any) =>
  (await axios.post('/teacher/students/add', data, { timeout: 15000 }))

export const updateStudentApi = async (data: any) =>
  (await axios.put('/teacher/students/update', data, { timeout: 15000 }))

export const deleteStudentApi = async (student_username: string, teacher_username: string) =>
  (await axios.delete(`/teacher/students/${student_username}`, {
    params: { teacher_username },
    timeout: 10000,
  }))

export const batchDeleteStudentsApi = async (data: { student_usernames: string[]; teacher_username: string }) =>
  (await axios.post('/teacher/students/batch-delete', data, { timeout: 30000 }))
