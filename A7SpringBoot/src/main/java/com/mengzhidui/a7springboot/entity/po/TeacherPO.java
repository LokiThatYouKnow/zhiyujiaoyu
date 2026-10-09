package com.mengzhidui.a7springboot.entity.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mengzhidui.a7springboot.interfaces.AppUser;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "teacher")
@TableName("teacher")
public class TeacherPO implements AppUser {
    @Id
    private Long id;
    private String username;
    private String password;
    private String name;
    private String gender;
    private String age;
    private String position;
    private String major;
    private String class_name;
    private String class_student_number;
    private String teacher_binding_code;

    @CreationTimestamp
    @TableField(fill = FieldFill.INSERT)
    private Timestamp create_time;

    @UpdateTimestamp
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Timestamp update_time;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_TEACHER"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return AppUser.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return AppUser.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return AppUser.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return AppUser.super.isEnabled();
    }


    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getType() {
        return "TEACHER";
    }
}
