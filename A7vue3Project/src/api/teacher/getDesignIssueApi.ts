import axios from "../../utils/axios";
export const getCozeDesignIssueApi = async(request: any) => {
    return (await axios.post("/teacher/ai/designIssue",request,{timeout:180000}))
}