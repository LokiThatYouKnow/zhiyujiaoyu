import axios from "../../utils/axios";

export const getIssueListApi = async(request: any) => {
    return (await axios.post("/admin/ai/getIssueList",request,{timeout:10000}))
}

export const getResourceListApi = async(request: any) => {
    return (await axios.post("/admin/ai/getResourceList",request,{timeout:10000}))
}

export const deleteCourseApi = async(request: any) => {
    return (await axios.post('/admin/ai/deleteCourse',request,{timeout:60000}))
}

export const deleteIssueApi = async(request: any) => {
    return (await axios.post("/admin/ai/deleteIssue",request,{timeout:60000}))
}

export const deleteResourceApi = async(request: any) => {
    return (await axios.post("/admin/ai/deleteResource",request,{timeout:60000}))
}
