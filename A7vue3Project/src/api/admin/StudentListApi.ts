import type { student } from "../../modules/student";
import axios from "../../utils/axios";

export const addStudentApi = async(newStudent:student) => {
    return (await axios.post("/admin/addStudent",newStudent))           
}

export const deleteStudentByUsernameApi = async(username:string) => {
    return (await axios.delete(`/admin/deleteStudentByUsername/${username}`))           
}

export const deleteStudentBySelectedApi = async(usernames: string[]) => {
  return await axios.delete('/admin/deleteStudentBySelected', {
    data: usernames , // 通过data字段传递请求体,默认指定JSON格式,注意usernames不要用{}括起来，这样的话传过去的就是对象而不是数组了
  });
};

export const updateStudentApi = async(newStudent:student) => {
    return (await axios.put("/admin/updateStudent",newStudent))           
}