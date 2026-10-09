import axios from "../../utils/axios";
export const getSelfAdminDetailApi = async(admin_username:string) => {
    return (await axios.get("/admin/getSelfAdminDetail",{params:{admin_username}}))
}