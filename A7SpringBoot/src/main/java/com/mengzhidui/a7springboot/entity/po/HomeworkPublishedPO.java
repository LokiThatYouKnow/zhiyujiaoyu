package com.mengzhidui.a7springboot.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
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
@Table(name = "homework_published")
@TableName("homework_published")
public class HomeworkPublishedPO {
    @Id
    private Integer id;
    private String homework_id;
    private String type;              // 'homework' | 'exam'
    private String teacher_username;
    private String course_name;
    private String title;
    private String description;
    @Lob
    private String questions_json;    // full Question[] as JSON
    private String deadline;          // homework deadline, or exam start_time
    private String end_time;          // exam end_time (for exam type)
    private Integer duration;         // exam duration in minutes
    private String status;            // 'published'
    private Long created_at;
    private Long updated_at;
}
