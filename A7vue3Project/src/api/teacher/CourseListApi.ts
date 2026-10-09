import axios from "../../utils/axios";
export const deleteCourseApi = async(request: any) => {
    return (await axios.post('/teacher/ai/deleteCourse',request,{timeout:60000}))
}

export const getIssueListApi = async(request:any) => {
    return (await axios.post(`/teacher/ai/getIssueList`,request,{timeout:60000}))
}

export const getIssueAnswerDetailApi = async(request: any) => {
    return (await axios.post("/teacher/ai/getIssueAnswerDetail",request,{timeout:180000}))
}

export const getResourceListApi = async(request: any) => {
    return (await axios.post("/teacher/ai/getResourceList",request,{timeout:10000}))
}

export const deleteIssueApi = async(request: any) => {
    return (await axios.post("/teacher/ai/deleteIssue",request,{timeout:60000}))
}

export const deleteResourceApi = async(request: any) => {
    return (await axios.post("/teacher/ai/deleteResource",request,{timeout:60000}))
}

export const getIssueCorrectAVGRecordApi = (params: {teacher_username:string,course_name:string, startDate: string, endDate: string }) => {
    return axios.get('/teacher/getIssueCorrectAVGRecord', {
        params: {
            teacher_username: params.teacher_username,
            course_name: params.course_name,
            startDate: params.startDate,
            endDate: params.endDate,
        }
    });
};

export const getKnowledgePointCommendDegreeRecordApi = (params: {teacher_username:string,course_name:string, startDate: string, endDate: string }) => {
    return axios.get('/teacher/getKnowledgePointCommendDegreeRecord', {
        params: {
            teacher_username: params.teacher_username,
            course_name: params.course_name,
            startDate: params.startDate,
            endDate: params.endDate,
        }
    });
};


