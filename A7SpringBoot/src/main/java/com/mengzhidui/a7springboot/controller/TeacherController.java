package com.mengzhidui.a7springboot.controller;

import com.mengzhidui.a7springboot.entity.dto.TeacherDTO;
import com.mengzhidui.a7springboot.entity.po.IssueCorrectAVGRecordPO;
import com.mengzhidui.a7springboot.entity.po.KnowledgePointCommendDegreeRecordPO;
import com.mengzhidui.a7springboot.entity.po.TeacherPO;
import com.mengzhidui.a7springboot.entity.dto.JwtDTO;
import com.mengzhidui.a7springboot.response.R;
import com.mengzhidui.a7springboot.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    /*
     * 学生登录
     * */
    @PostMapping("/teacher/login")
    public R login(@RequestBody TeacherPO teacher){
        return teacherService.login(teacher);
    }

    /*
     * 教师注册
     * */
    @PostMapping("/teacher/register")
    public R register(@RequestBody TeacherDTO teacher){
        return teacherService.register(teacher);
    }

    /*
     * 教师登出
     * */
    @PostMapping("/teacher/loginOut")
    @PreAuthorize("hasRole('TEACHER')")
    public R loginOut(@RequestBody JwtDTO jwt){return teacherService.loginOut(jwt);}

    /*
     * 获取学生端菜单
     * */
    @GetMapping("/teacher/getMenu")
    @PreAuthorize("hasRole('TEACHER')")
    public R getMenu(){
        return teacherService.getMenu();
    }

    /*
     * 获取当前教师信息
     * */
    @GetMapping("/teacher/getSelfTeacherDetail")
    @PreAuthorize("hasRole('TEACHER')")
    public R getSelfTeacherDetail(@RequestParam String teacher_id){
        return teacherService.getSelfTeacherDetail(teacher_id);
    }

    /*
     * 针对性训练改错
     * */
    @PostMapping("/teacher/ai/knowledgeOrganization")
    @PreAuthorize("hasRole('TEACHER')")
    public R knowledgeOrganization(@RequestBody Map<String,String> params){
        return teacherService.knowledgeOrganization(params);
    }

    /*
     * 设计实训题目
     * */
    @PostMapping("/teacher/ai/practiseDesign")
    @PreAuthorize("hasRole('TEACHER')")
    public R PractiseDesign(@RequestBody Map<String,String> params){
        return teacherService.PractiseDesign(params);
    }

    /*
     * 教学时间规划
     * */
    @PostMapping("/teacher/ai/arrangeTime")
    @PreAuthorize("hasRole('TEACHER')")
    public R ArrangeTime(@RequestBody Map<String,String> params){
        return teacherService.ArrangeTime(params);
    }

    /*
     * 设计题目并给出答案
     * */
    @PostMapping("/teacher/ai/designIssue")
    @PreAuthorize("hasRole('TEACHER')")
    public R DesignIssue(@RequestBody Map<String,String> params){
        return teacherService.DesignIssue(params);
    }

    /*
     * 添加课程
     * */
    @PostMapping("/teacher/ai/addCourse")
    @PreAuthorize("hasRole('TEACHER')")
    public R AddCourse(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "course_name", required = false) String course_name,
            @RequestParam(value = "teacher_username", required = false) String teacher_username,
            @RequestParam(value = "course_hours", required = false) String course_hours,
            @RequestParam(value = "course_score", required = false) String course_score
    ) throws IOException {
        return teacherService.addCourse(file,course_name,teacher_username,course_hours,course_score);
    }

    /*
     * 删除课程
     * */
    @PostMapping("/teacher/ai/deleteCourse")
    @PreAuthorize("hasRole('TEACHER')")
    public R DeleteCourse (@RequestBody Map<String,String> params){
        return teacherService.deleteCourse(params);
    }

    /*
     * 查找课程对应练习集合
     * */
    @PostMapping("/teacher/ai/getIssueList")
    @PreAuthorize("hasRole('TEACHER')")
    public R GetIssueList (@RequestBody Map<String,String> params){
        return teacherService.getIssueList(params);
    }

    /*
     * 获取课程对应练习的答题详情分析
     * */
    @PostMapping("/teacher/ai/getIssueAnswerDetail")
    @PreAuthorize("hasRole('TEACHER')")
    public R GetIssueAnswerDetail (@RequestBody Map<String,String> params){
        return teacherService.getIssueAnswerDetail(params);
    }

    /*
     * 查找教师的课程对应的课件资源集合
     * */
    @PostMapping("/teacher/ai/getResourceList")
    @PreAuthorize("hasRole('TEACHER')")
    public R GetResourceList (@RequestBody Map<String,String> params){
        return teacherService.getResourceList(params);
    }

    /*
     * 删除教师的课程对应的课程练习
     * */
    @PostMapping("/teacher/ai/deleteIssue")
    @PreAuthorize("hasRole('TEACHER')")
    public R DeleteIssue (@RequestBody Map<String,String> params){
        return teacherService.deleteIssue(params);
    }

    /*
     * AI 网络搜索课件
     * */
    @PostMapping("/teacher/ai/searchCourseware")
    @PreAuthorize("hasRole('TEACHER')")
    public R SearchCourseware(@RequestBody Map<String,String> params){
        return teacherService.searchCourseware(params);
    }

    /*
     * 删除教师的课程对应的课件资源
     * */
    @PostMapping("/teacher/ai/deleteResource")
    @PreAuthorize("hasRole('TEACHER')")
    public R DeleteResource (@RequestBody Map<String,String> params){
        return teacherService.deleteResource(params);
    }

    /*
     * 获取指定日期范围内的练习平均正确率记录
     */
    @GetMapping("/teacher/getIssueCorrectAVGRecord")
    @PreAuthorize("hasRole('TEACHER')")
    public R getIssueCorrectAVGRecord(
            @RequestParam(required = false) String teacher_username,
            @RequestParam(required = false) String course_name,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate){
        // 处理日期参数
        LocalDate start = startDate != null
                ? LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE)
                : LocalDate.now();

        LocalDate end = endDate != null
                ? LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE)
                : LocalDate.now();

        // 调用服务层方法获取指定日期范围内的数据
        List<IssueCorrectAVGRecordPO> records = teacherService.getIssueCorrectAVGRecordByDateRange(teacher_username,course_name,start, end);

        return records != null
                ? R.success("获取成功", records)
                : R.error("未找到数据");
    }

    /*
     * 获取指定日期范围内的知识点掌握度，错误次数和错误率记录
     */
    @GetMapping("/teacher/getKnowledgePointCommendDegreeRecord")
    @PreAuthorize("hasRole('TEACHER')")
    public R getKnowledgePointCommendDegreeRecord(
            @RequestParam(required = false) String teacher_username,
            @RequestParam(required = false) String course_name,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate){
        // 处理日期参数
        LocalDate start = startDate != null
                ? LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE)
                : LocalDate.now();

        LocalDate end = endDate != null
                ? LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE)
                : LocalDate.now();

        // 调用服务层方法获取指定日期范围内的数据
        List<KnowledgePointCommendDegreeRecordPO> records = teacherService.getKnowledgePointCommendDegreeRecord(teacher_username,course_name,start, end);

        return records != null
                ? R.success("获取成功", records)
                : R.error("未找到数据");
    }

    /*
     * 学生提交作业/考试答案
     */
    @PostMapping("/teacher/submitAnswer")
    @PreAuthorize("hasRole('STUDENT')")
    public R SubmitAnswer(@RequestBody Map<String, String> params) {
        return teacherService.submitAnswer(params);
    }

    /*
     * 教师获取某次作业/考试的所有提交列表
     */
    @PostMapping("/teacher/getSubmissions")
    @PreAuthorize("hasRole('TEACHER')")
    public R GetSubmissions(@RequestBody Map<String, String> params) {
        return teacherService.getSubmissions(params);
    }

    /*
     * 教师获取单份提交的详情（用于批改）
     */
    @PostMapping("/teacher/getSubmissionDetail")
    @PreAuthorize("hasRole('TEACHER')")
    public R GetSubmissionDetail(@RequestBody Map<String, String> params) {
        return teacherService.getSubmissionDetail(params);
    }

    /*
     * 教师批改并保存分数
     */
    @PostMapping("/teacher/gradeSubmission")
    @PreAuthorize("hasRole('TEACHER')")
    public R GradeSubmission(@RequestBody Map<String, String> params) {
        return teacherService.gradeSubmission(params);
    }

    /*
     * 获取某门课程的成绩汇总
     */
    @PostMapping("/teacher/getGradeSummary")
    @PreAuthorize("hasRole('TEACHER')")
    public R GetGradeSummary(@RequestBody Map<String, String> params) {
        return teacherService.getGradeSummary(params);
    }

    /*
     * 将作业/考试发布到学生端（保存到数据库供学生查询）
     */
    @PostMapping("/teacher/publishHomeworkToStudents")
    @PreAuthorize("hasRole('TEACHER')")
    public R PublishHomeworkToStudents(@RequestBody Map<String, String> params) {
        return teacherService.publishHomeworkToStudents(params);
    }

    /*
     * AI 自动批改作业/考试
     */
    @PostMapping("/teacher/aiAutoGrade")
    @PreAuthorize("hasRole('TEACHER')")
    public R AiAutoGrade(@RequestBody Map<String, String> params) {
        return teacherService.aiAutoGrade(params);
    }

    /*
     * 撤回单份批改 — 将已批改的提交恢复到已提交状态
     */
    @PostMapping("/teacher/retractGrade")
    @PreAuthorize("hasRole('TEACHER')")
    public R RetractGrade(@RequestBody Map<String, String> params) {
        return teacherService.retractGrade(params);
    }

    /*
     * 撤回全部批改 — 将某作业所有已批改的提交恢复到已提交状态
     */
    @PostMapping("/teacher/retractAllGrades")
    @PreAuthorize("hasRole('TEACHER')")
    public R RetractAllGrades(@RequestBody Map<String, String> params) {
        return teacherService.retractAllGrades(params);
    }

    /*
     * 获取某门课程已发布的作业/考试列表
     */
    @PostMapping("/teacher/getPublishedAssignments")
    @PreAuthorize("hasRole('TEACHER')")
    public R GetPublishedAssignments(@RequestBody Map<String, String> params) {
        return teacherService.getPublishedAssignments(params);
    }

    /*
     * AI 通用聊天对话
     */
    @PostMapping("/teacher/chat")
    @PreAuthorize("hasRole('TEACHER')")
    public R Chat(@RequestBody Map<String, Object> params) {
        return teacherService.chat(params);
    }

    /*
     * 获取可用 AI 模型列表
     */
    @GetMapping("/teacher/models")
    @PreAuthorize("hasRole('TEACHER')")
    public R ListModels() {
        return teacherService.listModels();
    }

    // ===== 学生管理 =====

    /*
     * 列出选修了教师课程的学生（可选按课程筛选）
     */
    @GetMapping("/teacher/students/list")
    @PreAuthorize("hasRole('TEACHER')")
    public R listEnrolledStudents(
            @RequestParam(required = false) String course_name,
            @RequestParam String teacher_username) {
        return teacherService.listEnrolledStudents(teacher_username, course_name);
    }

    /*
     * 添加学生（创建账号并关联到课程）
     */
    @PostMapping("/teacher/students/add")
    @PreAuthorize("hasRole('TEACHER')")
    public R addStudentByTeacher(@RequestBody Map<String, Object> params) {
        return teacherService.addStudentByTeacher(params);
    }

    /*
     * 更新学生信息
     */
    @PutMapping("/teacher/students/update")
    @PreAuthorize("hasRole('TEACHER')")
    public R updateStudentByTeacher(@RequestBody Map<String, Object> params) {
        return teacherService.updateStudentByTeacher(params);
    }

    /*
     * 从教师名下删除学生（解除选课关联和班级关联）
     */
    @DeleteMapping("/teacher/students/{student_username}")
    @PreAuthorize("hasRole('TEACHER')")
    public R deleteStudentFromTeacher(
            @PathVariable String student_username,
            @RequestParam String teacher_username) {
        return teacherService.deleteStudentFromTeacher(student_username, teacher_username);
    }

    /*
     * 批量删除学生
     */
    @PostMapping("/teacher/students/batch-delete")
    @PreAuthorize("hasRole('TEACHER')")
    public R batchDeleteStudents(@RequestBody Map<String, Object> params) {
        return teacherService.batchDeleteStudents(params);
    }
}