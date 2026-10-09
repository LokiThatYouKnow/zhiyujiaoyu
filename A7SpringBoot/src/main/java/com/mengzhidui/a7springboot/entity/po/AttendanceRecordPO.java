package com.mengzhidui.a7springboot.entity.po;

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
@Table(name = "attendance_record")
@TableName("attendance_record")
public class AttendanceRecordPO {
    @Id
    private Long id;
    private Long attendance_id;
    private String student_name;
    private String student_username;
    private String status;  // present / absent / leave
}
