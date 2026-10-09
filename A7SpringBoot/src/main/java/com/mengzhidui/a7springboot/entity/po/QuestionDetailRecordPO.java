package com.mengzhidui.a7springboot.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "question_detail_record")//从数据库返回
@TableName("question_detail_record")//返回给数据库
public class QuestionDetailRecordPO{
    @Id
    private Integer record_id;
    private String user_category;
    private String user_username;
    private String question_id;
    private String course_name;
    private String issue_name;
    private String start_time;
    private String end_time;
}