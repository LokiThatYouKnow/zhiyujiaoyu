package com.mengzhidui.a7springboot.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充处理器 — 在 insert/update 时自动设置 create_time 和 update_time。
 * 解决了 Hibernate @CreationTimestamp/@UpdateTimestamp 在 MyBatis-Plus 操作下不生效的问题。
 *
 * 注意：不同 PO 的 create_time 类型不同 — AdminPO/StudentPO/TeacherPO/CoursePO 使用 Timestamp，
 * 而 AllStudentClickRecordPO 等记录表使用 LocalDateTime。通过尝试 Timestamp 后回退到 LocalDateTime 来处理。
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        // 尝试 Timestamp 类型（AdminPO/StudentPO/TeacherPO/CoursePO）
        tryFill(metaObject, "create_time", now, LocalDateTime.now());
        tryFill(metaObject, "update_time", now, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        tryFill(metaObject, "update_time", now, LocalDateTime.now());
    }

    /**
     * 尝试以 Timestamp 填充；若字段类型不匹配（如 LocalDateTime），则回退到 fallback 值。
     */
    private void tryFill(MetaObject metaObject, String fieldName, Timestamp tsValue, LocalDateTime ldtValue) {
        try {
            this.strictInsertFill(metaObject, fieldName, Timestamp.class, tsValue);
        } catch (IllegalArgumentException e) {
            // 类型不是 Timestamp，尝试 LocalDateTime
            try {
                this.strictInsertFill(metaObject, fieldName, LocalDateTime.class, ldtValue);
            } catch (IllegalArgumentException ignored) {
                // 字段不存在或类型不支持 — 安静跳过
            }
        }
    }
}
