package com.mengzhidui.a7springboot.service;

import com.mengzhidui.a7springboot.entity.dto.AllCostTimeRecordDTO;
import com.mengzhidui.a7springboot.entity.dto.StudentDTO;
import com.mengzhidui.a7springboot.entity.dto.TeacherDTO;
import com.mengzhidui.a7springboot.entity.po.*;
import com.mengzhidui.a7springboot.entity.dto.JwtDTO;
import com.mengzhidui.a7springboot.entity.vo.AdminVO;
import com.mengzhidui.a7springboot.response.R;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AdminService {
    R login(AdminPO admin);

    R getMenu();

    R loginOut(JwtDTO jwt);

    R getSelfAdminDetail(String admin_username);

    R getIssueList(Map<String, String> params);

    R getResourceList(Map<String, String> params);

    AllTeacherClickRecordPO getByDateRange(LocalDate start, LocalDate end);

    AllStudentClickRecordPO getStudentRecordByDateRange(LocalDate start, LocalDate end);

    AllCostTimeRecordDTO getCostTimeRecordByDateRange(String teacher_username, String course_name, LocalDate start, LocalDate end);

    List<IssueCorrectAVGRecordPO> getIssueCorrectAVGRecordByDateRange(String teacher_username,String course_name,LocalDate start, LocalDate end);

    List<KnowledgePointCommendDegreeRecordPO> getKnowledgePointCommendDegreeRecord(String teacherUsername, String courseName, LocalDate start, LocalDate end);

    R addStudent(StudentDTO studentDTO);

    R deleteStudentByUsername(String username);

    R deleteStudentBySelected(List<String> usernames);

    R updateStudent(StudentDTO studentDTO);

    R addAdmin(AdminVO adminVO);

    R deleteAdminByUsername(String username);

    R deleteAdminBySelected(List<String> usernames);

    R updateAdmin(AdminVO adminVO);

    R addTeacher(TeacherDTO teacherDTO);

    R deleteTeacherByUsername(String username);

    R deleteTeacherBySelected(List<String> usernames);

    R updateTeacher(TeacherDTO teacherDTO);

    R deleteCourse(Map<String, String> params);

    R deleteIssue(Map<String, String> params);

    R deleteResource(Map<String, String> params);
}
