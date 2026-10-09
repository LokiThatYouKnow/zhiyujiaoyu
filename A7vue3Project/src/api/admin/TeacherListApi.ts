import type { teacher } from "../../modules/teacher";
import axios from "../../utils/axios";

export const addTeacherApi = async(newTeacher:teacher) => {
    return (await axios.post("/admin/addTeacher",newTeacher))           
}

export const deleteTeacherByUsernameApi = async(username:string) => {
    return (await axios.delete(`/admin/deleteTeacherByUsername/${username}`))           
}

export const deleteTeacherBySelectedApi = async(usernames: string[]) => {
  return await axios.delete('/admin/deleteTeacherBySelected', {
    data: usernames , // 通过data字段传递请求体,默认指定JSON格式,注意usernames不要用{}括起来，这样的话传过去的就是对象而不是数组了
  });
};

export const updateTeacherApi = async(newTeacher:teacher) => {
    return (await axios.put("/admin/updateTeacher",newTeacher))           
}