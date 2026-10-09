package com.mengzhidui.a7springboot.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mengzhidui.a7springboot.entity.po.CoursePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TeacherDTO {
    private int teacher_id;
    private String teacher_account;
    private String teacher_password;
    private String teacher_name;
    private String teacher_gender;
    private String teacher_age;
    private String teacher_major;
    private String teacher_position;
    private String teacher_class_name;
    private String teacher_class_student_number;
    private String teacher_binding_code;
    private List<StudentDTO> teacher_studentList;
    private List<CoursePO> teacher_courseList;
    private List<String> teacher_courseResource;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")//适配前端返回来的数据格式
    private Timestamp create_time;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")//适配前端返回来的数据格式
    private Timestamp update_time;
}
