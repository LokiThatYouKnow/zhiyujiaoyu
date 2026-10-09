package com.mengzhidui.a7springboot.entity.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
@Table(name = "student")//从数据库返回
@TableName("student")//返回给数据库
public class StudentPO implements AppUser {
    @Id
    private Long id;
    private String username;
    private String password;
    private String name;
    private String gender;
    private String age;
    private String major;
    private String class_name;
    private String teacher_binding_code;

    @TableField(exist = false)
    private String course_name;

    @CreationTimestamp
    @TableField(fill = FieldFill.INSERT)
    private Timestamp create_time;

    @UpdateTimestamp
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Timestamp update_time;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_STUDENT"));
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
        return "STUDENT";
    }

    // 实现UserDetails其他方法
}
