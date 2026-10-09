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

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "knowledge_point_commend_degree_record")//从数据库返回
@TableName("knowledge_point_commend_degree_record")//返回给数据库
public class KnowledgePointCommendDegreeRecordPO {
    @Id
    private Integer record_id;
    private String teacher_username;
    private String course_name;
    private String knowledge_point;
    private String commend_degree;
    private String error_time;
    private String error_rate;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime create_time;

}
