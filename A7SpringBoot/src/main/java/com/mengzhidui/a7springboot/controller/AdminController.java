package com.mengzhidui.a7springboot.controller;

import com.mengzhidui.a7springboot.entity.dto.AllCostTimeRecordDTO;
import com.mengzhidui.a7springboot.entity.dto.StudentDTO;
import com.mengzhidui.a7springboot.entity.dto.TeacherDTO;
import com.mengzhidui.a7springboot.entity.po.*;
import com.mengzhidui.a7springboot.entity.dto.JwtDTO;
import com.mengzhidui.a7springboot.entity.vo.AdminVO;
import com.mengzhidui.a7springboot.response.R;
import com.mengzhidui.a7springboot.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /*
    * 管理员登录
    * */
    @PostMapping("/admin/login")
    public R login(@RequestBody AdminPO admin){
        return adminService.login(admin);
    }

    /*
    * 管理员登出
    * */
    @PostMapping("/admin/loginOut")
    @PreAuthorize("hasRole('ADMIN')")
    public R loginOut(@RequestBody JwtDTO jwt){return adminService.loginOut(jwt);}

    /*
    * 获取管理端菜单
    * */
    @GetMapping("/admin/getMenu")
    @PreAuthorize("hasRole('ADMIN')")
    public R getMenu(){
        return adminService.getMenu();
    }

    /*
     * 获取当前管理员的详情信息
     * */
    @GetMapping("/admin/getSelfAdminDetail")
    @PreAuthorize("hasRole('ADMIN')")
    public R getSelfAdminDetail(@RequestParam String admin_username){
        return adminService.getSelfAdminDetail(admin_username);
    }

    /*
     * 查找教师的课程对应的练习集合
     * */
    @PostMapping("/admin/ai/getIssueList")
    @PreAuthorize("hasRole('ADMIN')")
    public R GetIssueList (@RequestBody Map<String,String> params){
        return adminService.getIssueList(params);
    }

    /*
     * 查找教师的课程对应的课件资源集合
     * */
    @PostMapping("/admin/ai/getResourceList")
    @PreAuthorize("hasRole('ADMIN')")
    public R GetResourceList (@RequestBody Map<String,String> params){
        return adminService.getResourceList(params);
    }

    /*
     * 获取指定日期范围内的教师点击记录
     */
    @GetMapping("/admin/getAllTeacherClickRecord")
    @PreAuthorize("hasRole('ADMIN')")
    public R getAllTeacherClickRecord(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        // 处理日期参数
        LocalDate start = startDate != null
                ? LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE)
                : LocalDate.now();

        LocalDate end = endDate != null
                ? LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE)
                : LocalDate.now();

        // 调用服务层方法获取指定日期范围内的数据
        AllTeacherClickRecordPO record = adminService.getByDateRange(start, end);

        return record != null
                ? R.success("获取成功", record)
                : R.error("未找到数据");
    }

    /*
     * 获取指定日期范围内的学生点击记录
     */
    @GetMapping("/admin/getAllStudentClickRecord")
    @PreAuthorize("hasRole('ADMIN')")
    public R getAllStudentClickRecord(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        // 处理日期参数
        LocalDate start = startDate != null
                ? LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE)
                : LocalDate.now();

        LocalDate end = endDate != null
                ? LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE)
                : LocalDate.now();

        // 调用服务层方法获取指定日期范围内的数据
        AllStudentClickRecordPO record = adminService.getStudentRecordByDateRange(start, end);

        return record != null
                ? R.success("获取成功", record)
                : R.error("未找到数据");
    }

    /*
     * 获取指定日期范围内的耗时记录
     */
    @GetMapping("/admin/getAllCostTimeRecord")
    @PreAuthorize("hasRole('ADMIN')")
    public R getAllCostTimeRecord(
            @RequestParam(required = false) String teacher_username,
            @RequestParam(required = false) String course_name,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        // 处理日期参数
        LocalDate start = startDate != null
                ? LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE)
                : LocalDate.now();

        LocalDate end = endDate != null
                ? LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE)
                : LocalDate.now();

        // 调用服务层方法获取指定日期范围内的数据
        AllCostTimeRecordDTO record = adminService.getCostTimeRecordByDateRange(teacher_username,course_name,start, end);

        return record != null
                ? R.success("获取成功", record)
                : R.error("未找到数据");
    }

    /*
     * 获取指定日期范围内的练习平均正确率记录
     */
    @GetMapping("/admin/getIssueCorrectAVGRecord")
    @PreAuthorize("hasRole('ADMIN')")
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
        List<IssueCorrectAVGRecordPO> records = adminService.getIssueCorrectAVGRecordByDateRange(teacher_username,course_name,start, end);

        return records != null
                ? R.success("获取成功", records)
                : R.error("未找到数据");
    }

    /*
     * 获取指定日期范围内的知识点掌握度，错误次数和错误率记录
     */
    @GetMapping("/admin/getKnowledgePointCommendDegreeRecord")
    @PreAuthorize("hasRole('ADMIN')")
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
        List<KnowledgePointCommendDegreeRecordPO> records = adminService.getKnowledgePointCommendDegreeRecord(teacher_username,course_name,start, end);

        return records != null
                ? R.success("获取成功", records)
                : R.error("未找到数据");
    }

    /*
    * 学生列表添加学生
    * */
    @PostMapping("/admin/addStudent")
    @PreAuthorize("hasRole('ADMIN')")
    public R addStudent(@RequestBody StudentDTO studentDTO) {
        return adminService.addStudent(studentDTO);
    }

    /*
     * 学生列表删除学生
     * */
    @DeleteMapping("/admin/deleteStudentByUsername/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public R deleteStudentByUsername(@PathVariable String username) {
        return adminService.deleteStudentByUsername(username);
    }

    /*
     * 学生列表批量删除学生
     * */
    @DeleteMapping("/admin/deleteStudentBySelected")
    @PreAuthorize("hasRole('ADMIN')")
    public R deleteStudentBySelected(@RequestBody List<String> usernames) {
        return adminService.deleteStudentBySelected(usernames);
    }

    /*
     * 学生列表修改学生
     * */
    @PutMapping("/admin/updateStudent")
    @PreAuthorize("hasRole('ADMIN')")
    public R updateStudent(@RequestBody StudentDTO studentDTO) {
        return adminService.updateStudent(studentDTO);
    }

    /*
     * 管理员列表添加管理员（仅超级管理员）
     * */
    @PostMapping("/admin/addAdmin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public R addAdmin(@RequestBody AdminVO adminVO) {
        return adminService.addAdmin(adminVO);
    }

    /*
     * 管理员列表删除管理员（仅超级管理员）
     * */
    @DeleteMapping("/admin/deleteAdminByUsername/{username}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public R deleteAdminByUsername(@PathVariable String username) {
        return adminService.deleteAdminByUsername(username);
    }

    /*
     * 管理员列表批量删除管理员（仅超级管理员）
     * */
    @DeleteMapping("/admin/deleteAdminBySelected")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public R deleteAdminBySelected(@RequestBody List<String> usernames) {
        return adminService.deleteAdminBySelected(usernames);
    }

    /*
     * 管理员列表修改管理员（仅超级管理员）
     * */
    @PutMapping("/admin/updateAdmin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public R updateAdmin(@RequestBody AdminVO adminVO) {
        return adminService.updateAdmin(adminVO);
    }

    /*
     * 教师列表添加教师
     * */
    @PostMapping("/admin/addTeacher")
    @PreAuthorize("hasRole('ADMIN')")
    public R addTeacher(@RequestBody TeacherDTO teacherDTO) {
        return adminService.addTeacher(teacherDTO);
    }

    /*
     * 教师列表删除教师
     * */
    @DeleteMapping("/admin/deleteTeacherByUsername/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public R deleteTeacherByUsername(@PathVariable String username) {
        return adminService.deleteTeacherByUsername(username);
    }

    /*
     * 教师列表批量删除教师
     * */
    @DeleteMapping("/admin/deleteTeacherBySelected")
    @PreAuthorize("hasRole('ADMIN')")
    public R deleteTeacherBySelected(@RequestBody List<String> usernames) {
        return adminService.deleteTeacherBySelected(usernames);
    }

    /*
     * 教师列表修改教师
     * */
    @PutMapping("/admin/updateTeacher")
    @PreAuthorize("hasRole('ADMIN')")
    public R updateTeacher(@RequestBody TeacherDTO teacherDTO) {
        return adminService.updateTeacher(teacherDTO);
    }

    /*
    * 删除课程
    * */
    @PostMapping("/admin/ai/deleteCourse")
    @PreAuthorize("hasRole('ADMIN')")
    public R deleteCourse(@RequestBody Map<String,String> params) {
        return adminService.deleteCourse(params);
    }

    /*
     * 删除课程
     * */
    @PostMapping("/admin/ai/deleteIssue")
    @PreAuthorize("hasRole('ADMIN')")
    public R deleteIssue(@RequestBody Map<String,String> params) {
        return adminService.deleteIssue(params);
    }

    /*
     * 删除课程
     * */
    @PostMapping("/admin/ai/deleteResource")
    @PreAuthorize("hasRole('ADMIN')")
    public R deleteResource(@RequestBody Map<String,String> params) {
        return adminService.deleteResource(params);
    }
}
