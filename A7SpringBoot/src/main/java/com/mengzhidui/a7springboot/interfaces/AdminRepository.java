package com.mengzhidui.a7springboot.interfaces;

import com.mengzhidui.a7springboot.entity.po.AdminPO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<AdminPO, Long> {
    Optional<AdminPO> findByUsernameEquals(String username);
}
