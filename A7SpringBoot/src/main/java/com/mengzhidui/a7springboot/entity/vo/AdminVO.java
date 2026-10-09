package com.mengzhidui.a7springboot.entity.vo;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AdminVO {

    private Long id;
    private String username;
    private String password;
    private String name;

    private Timestamp create_time;

    private Timestamp update_time;

    private Boolean isSuperAdmin; // true=超级管理员, false=普通管理员
}
