import type { jwtRes } from '../response/jwtRes.ts'
import axios from "../utils/axios.ts"
export const loginAdminOutApi = async(jwtRes:jwtRes) => {
    return (await axios.post("/admin/loginOut",jwtRes))           
}

export const loginStudentOutApi = async(jwtRes:jwtRes) => {
    return (await axios.post("/student/loginOut",jwtRes))           
}

export const loginTeacherOutApi = async(jwtRes:jwtRes) => {
    return (await axios.post("/teacher/loginOut",jwtRes))           
}