package com.mengzhidui.a7springboot.service;

import com.mengzhidui.a7springboot.entity.po.AdminPO;
import com.mengzhidui.a7springboot.entity.po.StudentPO;
import com.mengzhidui.a7springboot.entity.po.TeacherPO;
import com.mengzhidui.a7springboot.interfaces.AdminRepository;
import com.mengzhidui.a7springboot.interfaces.StudentRepository;
import com.mengzhidui.a7springboot.interfaces.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 先查学生表（使用Equals显式区分大小写）
        Optional<StudentPO> student = studentRepository.findByUsernameEquals(username);
        if (student.isPresent()) return student.get();

        // 再查教师表
        Optional<TeacherPO> teacher = teacherRepository.findByUsernameEquals(username);
        if (teacher.isPresent()) return teacher.get();

        // 最后查管理员表
        Optional<AdminPO> admin = adminRepository.findByUsernameEquals(username);
        if (admin.isPresent()) return admin.get();

        throw new UsernameNotFoundException("User not found: " + username);
    }
}
