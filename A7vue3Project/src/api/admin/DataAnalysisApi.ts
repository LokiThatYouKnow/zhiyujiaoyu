import axios from "../../utils/axios";

// 获取教师点击记录 API
export const getAllTeacherClickRecordApi = (params: { startDate: string, endDate: string }) => {
    return axios.get('/admin/getAllTeacherClickRecord', {
        params: {
            startDate: params.startDate,
            endDate: params.endDate,
        }
    });
};

// 获取学生点击记录 API
export const getAllStudentClickRecordApi = (params: { startDate: string, endDate: string }) => {
    return axios.get('/admin/getAllStudentClickRecord', {
        params: {
            startDate: params.startDate,
            endDate: params.endDate,
        }
    });
};

export const getAllCostTimeRecordApi = (params: {teacher_username:string,course_name:string, startDate: string, endDate: string }) => {
    return axios.get('/admin/getAllCostTimeRecord', {
        params: {
            teacher_username: params.teacher_username,
            course_name: params.course_name,
            startDate: params.startDate,
            endDate: params.endDate,
        }
    });
};

export const getIssueCorrectAVGRecordApi = (params: {teacher_username:string,course_name:string, startDate: string, endDate: string }) => {
    return axios.get('/admin/getIssueCorrectAVGRecord', {
        params: {
            teacher_username: params.teacher_username,
            course_name: params.course_name,
            startDate: params.startDate,
            endDate: params.endDate,
        }
    });
};

export const getKnowledgePointCommendDegreeRecordApi = (params: {teacher_username:string,course_name:string, startDate: string, endDate: string }) => {
    return axios.get('/admin/getKnowledgePointCommendDegreeRecord', {
        params: {
            teacher_username: params.teacher_username,
            course_name: params.course_name,
            startDate: params.startDate,
            endDate: params.endDate,
        }
    });
};