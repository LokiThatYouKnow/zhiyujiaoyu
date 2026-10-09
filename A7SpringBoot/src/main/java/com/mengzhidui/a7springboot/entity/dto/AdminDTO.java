package com.mengzhidui.a7springboot.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mengzhidui.a7springboot.entity.po.AdminPO;
import com.mengzhidui.a7springboot.entity.po.CoursePO;
import com.mengzhidui.a7springboot.entity.vo.AdminVO;
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
public class AdminDTO {
    private int admin_id;
    private String admin_account;
    private String admin_password;
    private String admin_name;
    private List<AdminVO> all_admin_list;
    private List<TeacherDTO> all_teacher_list;
    private List<StudentDTO> all_student_list;

    private Boolean is_super_admin; // 当前登录管理员是否为超级管理员


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")//适配前端返回来的数据格式
    private Timestamp create_time;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")//适配前端返回来的数据格式
    private Timestamp update_time;
}
