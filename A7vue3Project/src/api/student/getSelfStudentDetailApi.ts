import axios from "../../utils/axios";
export const getSelfStudentDetailApi = async(student_id:string) => {
    return (await axios.get("/student/getSelfStudentDetail",{params:{student_id}}))
}