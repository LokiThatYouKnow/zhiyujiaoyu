import type { admin } from "./admin";
import type { student } from "./student";
import type { teacher } from "./teacher";

export interface adminVO{
    admin_id:number,
    admin_account:string,
    admin_password:string,
    admin_name:string,
    all_admin_list:admin[],
    all_teacher_list:teacher[],
    all_student_list:student[],
    create_time?:Date,
    update_time?:Date
}