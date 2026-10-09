import axios from '../../utils/axios'

export const getMyAttendanceApi = async (student_username: string) =>
  (await axios.get('/student/attendance/myRecords', {
    params: { student_username },
    timeout: 15000
  }))
