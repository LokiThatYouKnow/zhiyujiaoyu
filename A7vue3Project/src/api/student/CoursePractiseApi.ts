import axios from "../../utils/axios";

export const getCozeCoursePractiseApi = async(request: any) => {
    return (await axios.post("/student/ai/coursePractise",request,{timeout:180000}))
}

export const getCozeCoursePractiseCorrectAnswerApi = async(request: any) => {
    return (await axios.post("/student/ai/coursePractiseCorrectAnswer",request,{timeout:180000}))
}

export const joinCourseApi = async(request: any) => {
    return (await axios.post("/student/joinCourse",request,{timeout:10000}))
}
