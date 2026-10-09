package com.mengzhidui.a7springboot.entity.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "all_teacher_click_record")//从数据库返回
@TableName("all_teacher_click_record")//返回给数据库
public class AllTeacherClickRecordPO {
    @Id
    private Integer record_id;
    private int knowledge_organization;
    private int practise_design;
    private int design_issue;
    private int issue_answer_analysis;
    private int build_knowledge_base;
    private int search_courseware;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime create_time;

    //汇总字段
    @Transient // JPA注解：不映射到数据库
    @TableField(exist = false) // MyBatis-Plus注解：表中不存在该字段
    private LocalDate summaryStartDate;

    @Transient // JPA注解：不映射到数据库
    @TableField(exist = false) // MyBatis-Plus注解：表中不存在该字段
    private LocalDate summaryEndDate;
}
