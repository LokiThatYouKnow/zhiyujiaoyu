import type { course } from "./course";

export interface student {
    student_id?: number;
    student_account?: string;
    student_password?: string;
    student_name?: string;
    student_gender?: string;
    student_age?: string;
    student_classList?: course[];
    student_major?: string;
    student_class_name?: string;
    phone?: string;
    teacher_binding_code?: string;
    create_time?: Date;
    update_time?: Date;
  }
  