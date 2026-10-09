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
@Table(name = "all_cost_time_record")//从数据库返回
@TableName("all_cost_time_record")//返回给数据库
public class AllCostTimeRecordPO {
    @Id
    private Integer record_id;
    private String teacher_username;
    private String course_name;
    private String type;
    private String cost_second;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime create_time;

}
