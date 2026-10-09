package com.mengzhidui.a7springboot.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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
@Table(name = "submission_record")
@TableName("submission_record")
public class SubmissionRecordPO {
    @Id
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String homework_id;
    private String type;              // 'homework' | 'exam'
    private String student_username;
    private String student_name;
    private String course_name;
    private String issue_name;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String answers;           // JSON: [{"questionId":"","answer":""}]
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String scores;            // JSON: [{"questionId":"","score":0,"maxScore":0}]
    private Double total_score;
    private Double max_total_score;
    private String status;            // 'submitted' | 'graded'
    private Long submitted_at;
    private Long graded_at;
}
