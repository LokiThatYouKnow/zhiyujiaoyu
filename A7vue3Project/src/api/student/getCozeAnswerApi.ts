import axios from "../../utils/axios";
export const getCozeAnswerApi = async(request: any) => {
    return (await axios.post("/student/ai/answer",request,{timeout:180000}))
}
