import axios from "../../utils/axios";
export const getCozePractiseDesignApi = async(request: any) => {
    return (await axios.post("/teacher/ai/practiseDesign",request,{timeout:180000}))
}