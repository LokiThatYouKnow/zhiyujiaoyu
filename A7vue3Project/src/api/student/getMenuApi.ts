import axios from "../../utils/axios";
export const getMenuApi = async() => {
    return (await axios.get("/student/getMenu"))
}