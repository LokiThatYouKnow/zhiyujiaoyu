import axios from "../../utils/axios";
export const searchCoursewareApi = async(request: any) => {
    return (await axios.post("/teacher/ai/searchCourseware", request, {timeout: 180000}))
}
