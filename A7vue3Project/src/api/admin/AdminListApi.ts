import type { admin } from "../../modules/admin";
import axios from "../../utils/axios";

export const addAdminApi = async(newAdmin:admin) => {
    return (await axios.post("/admin/addAdmin",newAdmin))           
}

export const deleteAdminByUsernameApi = async(username:string) => {
    return (await axios.delete(`/admin/deleteAdminByUsername/${username}`))           
}

export const deleteAdminBySelectedApi = async(usernames: string[]) => {
  return await axios.delete('/admin/deleteAdminBySelected', {
    data: usernames , // 通过data字段传递请求体,默认指定JSON格式,注意usernames不要用{}括起来，这样的话传过去的就是对象而不是数组了
  });
};

export const updateAdminApi = async(newAdmin:admin) => {
    return (await axios.put("/admin/updateAdmin",newAdmin))           
}