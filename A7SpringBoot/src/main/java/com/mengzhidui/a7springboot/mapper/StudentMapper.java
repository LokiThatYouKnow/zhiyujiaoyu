package com.mengzhidui.a7springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mengzhidui.a7springboot.entity.po.CoursePO;
import com.mengzhidui.a7springboot.entity.po.StudentPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

public interface StudentMapper extends BaseMapper<StudentPO> {

    Boolean checkUsernameExists(String username);

    List<CoursePO> getCoursesByStudentId(Long studentId);

}
