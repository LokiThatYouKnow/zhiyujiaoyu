package com.mengzhidui.a7springboot.entity.dto;

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
public class AllCostTimeRecordDTO {
    @Id
    private int record_id;
    private String teacher_username;
    private String course_name;
    private int knowledgeOrganization_AVG_cost_second;
    private int practiseDesign_AVG_cost_second;
    private int designIssue_AVG_cost_second;
    private int addCourse_buildKnowledgeBase_AVG_cost_second;
    private int getIssueAnswerDetail_AVG_cost_second;
    private LocalDateTime create_time;

    //汇总字段
    @Transient // JPA注解：不映射到数据库
    @TableField(exist = false) // MyBatis-Plus注解：表中不存在该字段
    private LocalDate summaryStartDate;

    @Transient // JPA注解：不映射到数据库
    @TableField(exist = false) // MyBatis-Plus注解：表中不存在该字段
    private LocalDate summaryEndDate;
}
