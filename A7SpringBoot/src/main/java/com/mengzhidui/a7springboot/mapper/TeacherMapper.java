package com.mengzhidui.a7springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mengzhidui.a7springboot.entity.po.CoursePO;
import com.mengzhidui.a7springboot.entity.po.HomeworkPublishedPO;
import com.mengzhidui.a7springboot.entity.po.MinioObjectRecordPO;
import com.mengzhidui.a7springboot.entity.po.StudentPO;
import com.mengzhidui.a7springboot.entity.po.TeacherPO;

import java.util.List;

public interface TeacherMapper extends BaseMapper<TeacherPO> {

    Boolean checkUsernameExists(String username);

    List<CoursePO> selectCoursesByTeacherId(Long id);

    List<MinioObjectRecordPO> selectMinioObjectRecordByTeacherId(Long id);

    List<StudentPO> selectStudentsByTeacherId(Long id);

    /** 通过选课关系查找学生（course_detail_record 中选了教师课程的学生），返回 Map 避免 @TableField(exist=false) 干扰 */
    List<java.util.Map<String, Object>> selectEnrolledStudentsByTeacherId(Long teacherId);

    /** 通过选课关系查找学生，按课程名筛选 */
    List<java.util.Map<String, Object>> selectEnrolledStudentsByTeacherIdAndCourse(
            @org.apache.ibatis.annotations.Param("teacherId") Long teacherId,
            @org.apache.ibatis.annotations.Param("courseName") String courseName);

    /** 获取某门课程已发布的作业/考试列表 */
    List<HomeworkPublishedPO> selectPublishedAssignmentsByCourse(
            @org.apache.ibatis.annotations.Param("courseName") String courseName);

}
