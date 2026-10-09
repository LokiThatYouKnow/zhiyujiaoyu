package com.mengzhidui.a7springboot.entity.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "attendance")
@TableName("attendance")
public class AttendancePO {
    @Id
    private Long id;
    private String course_name;
    private String teacher_username;
    private String date;
    private Integer period;
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private Timestamp create_time;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Timestamp update_time;
}
