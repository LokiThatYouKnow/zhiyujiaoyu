import axios from "axios"

/**
 * 发送短信验证码
 * @param phone 手机号码
 */
export const sendSmsCodeApi = async (phone: string) => {
    return (await axios.post("/api/sms/send", { phone }))
}

/**
 * 验证短信验证码
 * @param phone 手机号码
 * @param code 验证码
 */
export const verifySmsCodeApi = async (phone: string, code: string) => {
    return (await axios.post("/api/sms/verify", { phone, code }))
}
