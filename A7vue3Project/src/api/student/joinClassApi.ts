import axios from "../../utils/axios";
export const joinClassApi = async(request: any) => {
    return (await axios.post("/student/joinClass",request,{timeout:10000}))
}