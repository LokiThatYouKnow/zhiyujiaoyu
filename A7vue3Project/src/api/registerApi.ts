import axios from "axios"
export const registerByStudentApi = async(student: any) => {
    return (await axios.post("/api/student/register",student))           
}

export const registerByTeacherApi = async(teacher: any) => {
    return (await axios.post("/api/teacher/register",teacher))           
}

export const registerByAdminApi = async(admin: any) => {
    return (await axios.post("/api/admin/register",admin))           
}