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
@Table(name = "all_student_click_record")//从数据库返回
@TableName("all_student_click_record")//返回给数据库
public class AllStudentClickRecordPO {
    @Id
    private Integer record_id;
    private int answer;
    private int self_practise;
    private int course_practise;
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
