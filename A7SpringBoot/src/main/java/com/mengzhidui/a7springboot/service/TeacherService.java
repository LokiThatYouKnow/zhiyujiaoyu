package com.mengzhidui.a7springboot.service;

import com.mengzhidui.a7springboot.entity.dto.TeacherDTO;
import com.mengzhidui.a7springboot.entity.po.IssueCorrectAVGRecordPO;
import com.mengzhidui.a7springboot.entity.po.KnowledgePointCommendDegreeRecordPO;
import com.mengzhidui.a7springboot.entity.po.TeacherPO;
import com.mengzhidui.a7springboot.entity.dto.JwtDTO;
import com.mengzhidui.a7springboot.response.R;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface TeacherService {
    R login(TeacherPO teacher);

    R loginOut(JwtDTO jwt);

    R getMenu();

    R register(TeacherDTO teacher);

    R knowledgeOrganization(Map<String, String> params);

    R PractiseDesign(Map<String, String> params);

    R ArrangeTime(Map<String, String> params);//暂时废弃

    R DesignIssue(Map<String, String> params);

    R getSelfTeacherDetail(String teacherId);

    R addCourse(MultipartFile file, String course_name,String teacher_username,String course_hours,String course_score) throws IOException;

    R deleteCourse(Map<String,String> params);

    R getIssueList(Map<String,String> params);

    R getIssueAnswerDetail(Map<String, String> params);

    R getResourceList(Map<String, String> params);

    R deleteIssue(Map<String, String> params);

    R deleteResource(Map<String, String> params);

    R searchCourseware(Map<String, String> params);

    List<IssueCorrectAVGRecordPO> getIssueCorrectAVGRecordByDateRange(String teacherUsername, String courseName, LocalDate start, LocalDate end);

    List<KnowledgePointCommendDegreeRecordPO> getKnowledgePointCommendDegreeRecord(String teacherUsername, String courseName, LocalDate start, LocalDate end);

    // ===== 作业/考试提交与批改 =====
    R submitAnswer(Map<String, String> params);

    R getSubmissions(Map<String, String> params);

    R getSubmissionDetail(Map<String, String> params);

    R gradeSubmission(Map<String, String> params);

    R getGradeSummary(Map<String, String> params);

    R publishHomeworkToStudents(Map<String, String> params);

    R aiAutoGrade(Map<String, String> params);

    R retractGrade(Map<String, String> params);

    R retractAllGrades(Map<String, String> params);

    R getPublishedAssignments(Map<String, String> params);

    // ===== AI 通用聊天 =====
    R chat(Map<String, Object> params);

    R listModels();

    // ===== 学生管理 =====
    R listEnrolledStudents(String teacherUsername, String courseName);
    R addStudentByTeacher(Map<String, Object> params);
    R updateStudentByTeacher(Map<String, Object> params);
    R deleteStudentFromTeacher(String studentUsername, String teacherUsername);
    R batchDeleteStudents(Map<String, Object> params);
}
