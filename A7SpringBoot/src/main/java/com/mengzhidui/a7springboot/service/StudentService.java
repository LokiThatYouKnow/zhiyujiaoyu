package com.mengzhidui.a7springboot.service;

import com.mengzhidui.a7springboot.entity.dto.StudentDTO;
import com.mengzhidui.a7springboot.entity.po.StudentPO;
import com.mengzhidui.a7springboot.entity.dto.JwtDTO;
import com.mengzhidui.a7springboot.response.R;

import java.util.Map;

public interface StudentService {
    R login(StudentPO student);

    R loginOut(JwtDTO jwt);

    R getMenu();

    R register(StudentDTO student);

    R cozeAnswer(Map<String, String> params);

    R cozeSelfPractise(Map<String, String> params);

    R cozeSelfPractiseCorrect(Map<String, String> params);

    R cozeCoursePractise(Map<String, String> params);

    R cozeCoursePractiseCorrectAnswer(Map<String, String> params);

    R getSelfStudentDetail(String student_id);

    R JoinCourse(Map<String, String> params);

    R joinClass(Map<String, String> params);

    R getHomeworkPublished(Map<String, String> params);

    R getMySubmissions(Map<String, String> params);

    R deleteSubmission(Map<String, String> params);

    // ===== AI 通用聊天 =====
    R chat(Map<String, Object> params);

    R listModels();
}
