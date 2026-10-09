import axios from "../../utils/axios";
export const getCozeArrangeTimeApi = async(request: any) => {
    return (await axios.post("/teacher/ai/arrangeTime",request,{timeout:180000}))
}