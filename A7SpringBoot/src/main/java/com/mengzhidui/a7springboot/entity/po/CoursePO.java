package com.mengzhidui.a7springboot.entity.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "course")//从数据库返回
@TableName("course")//返回给数据库
public class CoursePO {
    @Id
    @TableId(type = IdType.AUTO)
    private Integer course_id;
    private String course_name;
    private Long course_teacher_id;
    private String course_teacher_name;
    private String course_teacher_account;
    private int course_student_number;
    private String course_hours;
    private String course_score;
    private String course_invite_code;
    private String course_document_id;
    private String minio_id;

    @CreationTimestamp
    @TableField(fill = FieldFill.INSERT)
    private Timestamp create_time;

    @UpdateTimestamp
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Timestamp update_time;
}
