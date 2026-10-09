import type { course } from "./course";
import type { student } from "./student";

export interface teacher{
    teacher_id?:number,
    teacher_account?:string,
    teacher_password?:string,
    teacher_name?:string,
    teacher_gender?:string,
    teacher_age?:string,
    teacher_major?:string,
    teacher_position?:string,
    teacher_class_name?:string,
    teacher_class_student_number?:string,
    phone?:string,
    teacher_binding_code?:string,
    teacher_studentList?:student[],
    teacher_courseList?:course[],
    teacher_courseResource?:string[],
    create_time?:Date,
    update_time?:Date
}