import axios from "../../utils/axios";
export const getCozeKnowledgeOrganizationApi = async(request: any) => {
    return (await axios.post("/teacher/ai/knowledgeOrganization",request,{timeout:180000}))
}