import axios from "axios"

/** 统一登录（后端自动识别角色） */
export const loginApi = async (credentials: { username: string; password: string; captchaKey: string; captchaCode: string }) => {
    return (await axios.post("/api/auth/login", credentials))
}

/** 获取图形验证码 */
export const getCaptchaApi = async () => {
    return (await axios.get("/api/auth/captcha"))
}

// ===== 以下为旧接口，保留向后兼容 =====
export const loginByStudentApi = async (student: any) => {
    return (await axios.post("/api/student/login", student))
}

export const loginByTeacherApi = async (teacher: any) => {
    return (await axios.post("/api/teacher/login", teacher))
}

export const loginByAdminApi = async (admin: any) => {
    return (await axios.post("/api/admin/login", admin))
}
