package com.mengzhidui.a7springboot.controller;

import com.mengzhidui.a7springboot.entity.dto.StudentDTO;
import com.mengzhidui.a7springboot.entity.po.StudentPO;
import com.mengzhidui.a7springboot.entity.dto.JwtDTO;
import com.mengzhidui.a7springboot.response.R;
import com.mengzhidui.a7springboot.service.StudentService;
import com.mengzhidui.a7springboot.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final TeacherService teacherService;

    /*
     * 学生登录
     * */
    @PostMapping("/student/login")
    public R login(@RequestBody StudentPO student){
        return studentService.login(student);
    }

    /*
     * 学生注册
     * */
    @PostMapping("/student/register")
    public R register(@RequestBody StudentDTO student){
        return studentService.register(student);
    }

    /*
     * 学生登出
     * */
    @PostMapping("/student/loginOut")
    @PreAuthorize("hasRole('STUDENT')")
    public R loginOut(@RequestBody JwtDTO jwt){return studentService.loginOut(jwt);}

    /*
     * 获取学生端菜单
     * */
    @GetMapping("/student/getMenu")
    @PreAuthorize("hasRole('STUDENT')")
    public R getMenu(){
        return studentService.getMenu();
    }

    /*
     * 获取当前学生信息
     * */
    @GetMapping("/student/getSelfStudentDetail")
    @PreAuthorize("hasRole('STUDENT')")
    public R getSelfStudentDetail(@RequestParam String student_id){
        return studentService.getSelfStudentDetail(student_id);
    }

    /*
    * 对学生提出的问题结合教学内容进行解答
    * */
    @PostMapping("/student/ai/answer")
    @PreAuthorize("hasRole('STUDENT')")
    public R cozeAnswer(@RequestBody Map<String,String> params){
        return studentService.cozeAnswer(params);
    }

    /*
    * 生成针对性训练
    * */
    @PostMapping("/student/ai/selfPractise")
    @PreAuthorize("hasRole('STUDENT')")
    public R cozeSelfPractise(@RequestBody Map<String,String> params){
        return studentService.cozeSelfPractise(params);
    }

    /*
     * 针对性训练改错
     * */
    @PostMapping("/student/ai/selfPractiseCorrect")
    @PreAuthorize("hasRole('STUDENT')")
    public R cozeSelfPractiseCorrect(@RequestBody Map<String,String> params){
        return studentService.cozeSelfPractiseCorrect(params);
    }

    /*
     * 课堂练习
     * */
    @PostMapping("/student/ai/coursePractise")
    @PreAuthorize("hasRole('STUDENT')")
    public R cozeCoursePractise(@RequestBody Map<String,String> params){
        return studentService.cozeCoursePractise(params);
    }

    /*
     * 对照设计题目答案
     * */
    @PostMapping("/student/ai/coursePractiseCorrectAnswer")
    @PreAuthorize("hasRole('STUDENT')")
    public R cozeCoursePractiseCorrectAnswer(@RequestBody Map<String,String> params){
        return studentService.cozeCoursePractiseCorrectAnswer(params);
    }

    /*
     * 加入课程
     * */
    @PostMapping("/student/joinCourse")
    @PreAuthorize("hasRole('STUDENT')")
    public R JoinCourse(@RequestBody Map<String,String> params){
        return studentService.JoinCourse(params);
    }

    /*
     * 加入班级
     * */
    @PostMapping("/student/joinClass")
    @PreAuthorize("hasRole('STUDENT')")
    public R joinClass(@RequestBody Map<String,String> params){
        return studentService.joinClass(params);
    }

    /*
     * 获取已发布的作业/考试列表
     */
    @PostMapping("/student/getHomeworkPublished")
    @PreAuthorize("hasRole('STUDENT')")
    public R getHomeworkPublished(@RequestBody Map<String, String> params) {
        return studentService.getHomeworkPublished(params);
    }

    /*
     * 学生提交作业/考试答案
     */
    @PostMapping("/student/submitAnswer")
    @PreAuthorize("hasRole('STUDENT')")
    public R submitAnswer(@RequestBody Map<String, String> params) {
        // Delegate to teacher service which has submission logic
        return teacherService.submitAnswer(params);
    }

    /*
     * 学生获取自己的提交记录（含批改结果）
     */
    @PostMapping("/student/getMySubmissions")
    @PreAuthorize("hasRole('STUDENT')")
    public R getMySubmissions(@RequestBody Map<String, String> params) {
        return studentService.getMySubmissions(params);
    }

    /*
     * 学生撤回已提交的作业/考试（删除提交记录，回到待完成状态）
     */
    @PostMapping("/student/deleteSubmission")
    @PreAuthorize("hasRole('STUDENT')")
    public R deleteSubmission(@RequestBody Map<String, String> params) {
        return studentService.deleteSubmission(params);
    }

    /*
     * AI 通用聊天对话
     */
    @PostMapping("/student/chat")
    @PreAuthorize("hasRole('STUDENT')")
    public R Chat(@RequestBody Map<String, Object> params) {
        return studentService.chat(params);
    }

    /*
     * 获取可用 AI 模型列表
     */
    @GetMapping("/student/models")
    @PreAuthorize("hasRole('STUDENT')")
    public R ListModels() {
        return studentService.listModels();
    }
}
