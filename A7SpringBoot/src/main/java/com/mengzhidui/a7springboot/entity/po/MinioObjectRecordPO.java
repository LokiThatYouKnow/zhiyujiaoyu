package com.mengzhidui.a7springboot.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "minio_object_record")//从数据库返回
@TableName("minio_object_record")//返回给数据库
public class MinioObjectRecordPO{
    @Id
    private Integer record_id;
    private int course_id;
    private String object_name;
    private String bucket_name;
}

