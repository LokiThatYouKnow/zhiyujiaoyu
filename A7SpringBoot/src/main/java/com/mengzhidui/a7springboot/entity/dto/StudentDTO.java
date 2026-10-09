package com.mengzhidui.a7springboot.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mengzhidui.a7springboot.entity.po.CoursePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class StudentDTO {
    private int student_id;
    private String student_account;
    private String student_password;
    private String student_name;
    private String student_gender;
    private String student_age;
    private List<CoursePO> student_classList;
    private String student_major;
    private String student_class_name;
    private String teacher_binding_code;
    private String course_name;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")//适配前端返回来的数据格式
    private Timestamp create_time;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")//适配前端返回来的数据格式
    private Timestamp update_time;
}
