package com.mengzhidui.a7springboot.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mengzhidui.a7springboot.entity.po.AdminPO;
import com.mengzhidui.a7springboot.mapper.AdminMapper;
import com.mengzhidui.a7springboot.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 应用启动数据初始化：确保默认管理员账号存在。
 * 幂等操作 — 仅当账号不存在时才创建。
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        initSuperAdmin();
        initRegularAdmin();
    }

    /**
     * 创建默认超级管理员 Admin / 123456
     */
    private void initSuperAdmin() {
        String username = "Admin";
        LambdaQueryWrapper<AdminPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminPO::getUsername, username);
        AdminPO existing = adminMapper.selectOne(wrapper);
        if (existing != null) {
            log.debug("超级管理员 {} 已存在，跳过创建", username);
            return;
        }
        AdminPO admin = AdminPO.builder()
                .id(IdGenerator.generateUniqueId())
                .username(username)
                .password(passwordEncoder.encode("123456"))
                .name("超级管理员")
                .isSuperAdmin(true)
                .build();
        adminMapper.insert(admin);
        log.debug("已创建默认超级管理员: {}", username);
    }

    /**
     * 创建默认普通管理员 lokiAdmin / 123456
     */
    private void initRegularAdmin() {
        String username = "lokiAdmin";
        LambdaQueryWrapper<AdminPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminPO::getUsername, username);
        AdminPO existing = adminMapper.selectOne(wrapper);
        if (existing != null) {
            log.debug("普通管理员 {} 已存在，跳过创建", username);
            return;
        }
        AdminPO admin = AdminPO.builder()
                .id(IdGenerator.generateUniqueId())
                .username(username)
                .password(passwordEncoder.encode("123456"))
                .name("普通管理员")
                .isSuperAdmin(false)
                .build();
        adminMapper.insert(admin);
        log.debug("已创建默认普通管理员: {}", username);
    }
}
