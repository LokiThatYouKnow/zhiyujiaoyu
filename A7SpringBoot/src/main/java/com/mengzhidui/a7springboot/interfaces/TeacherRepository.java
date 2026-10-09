package com.mengzhidui.a7springboot.interfaces;

import com.mengzhidui.a7springboot.entity.po.TeacherPO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<TeacherPO, Long> {
    Optional<TeacherPO> findByUsernameEquals(String username);
}
