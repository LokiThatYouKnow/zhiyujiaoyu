import axios from "../../utils/axios";
export const getSelfTeacherDetailApi = async(teacher_id:string) => {
    return (await axios.get("/teacher/getSelfTeacherDetail",{params:{teacher_id}}))
}