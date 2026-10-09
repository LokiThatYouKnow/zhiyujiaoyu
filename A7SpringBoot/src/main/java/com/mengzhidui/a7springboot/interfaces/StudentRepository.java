package com.mengzhidui.a7springboot.interfaces;

import com.mengzhidui.a7springboot.entity.po.StudentPO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<StudentPO, Long> {
    Optional<StudentPO> findByUsernameEquals(String username);
}
