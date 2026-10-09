export interface course{
    course_id?:number,
    course_name?:string,
    course_teacher_id?:bigint,
    course_teacher_name?:string,
    course_teacher_account?:string,
    course_hours?:string,
    course_score?:string,
    course_student_number?:number,
    course_invite_code?:string,
    course_document_id?:string,
    minio_id?:string,
    create_time?:Date,
    update_time?:Date
}