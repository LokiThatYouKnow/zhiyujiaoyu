import axios from "../../utils/axios";
export const getCozeSelfPractiseApi = async(request: any) => {
    return (await axios.post("/student/ai/selfPractise",request,{timeout:180000}))
}

export const getCozeselfPractiseCorrectApi = async(request: any) => {
    return (await axios.post("/student/ai/selfPractiseCorrect",request,{timeout:180000}))
}