package com.mengzhidui.a7springboot.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "menu")
@TableName("menu")//返回给数据库
public class MenuPO {
    private Integer id;
    private String name;
    private String icon;
    private Integer pid;
    private String path;
    private String component;
    private String auth_role;
}
