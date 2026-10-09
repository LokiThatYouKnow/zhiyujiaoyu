import axios from '../../utils/axios'

export const saveAttendanceApi = async (params: any) =>
  (await axios.post('/teacher/attendance/save', params, { timeout: 15000 }))

export const listAttendanceApi = async (params: any) =>
  (await axios.post('/teacher/attendance/list', params, { timeout: 15000 }))

export const deleteAttendanceApi = async (id: number | string) =>
  (await axios.delete(`/teacher/attendance/${id}`, { timeout: 10000 }))

export const batchDeleteAttendanceApi = async (ids: (number | string)[]) =>
  (await axios.delete('/teacher/attendance/batch', { data: { ids }, timeout: 10000 }))
