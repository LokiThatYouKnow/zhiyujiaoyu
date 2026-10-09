package com.mengzhidui.a7springboot.controller;

import com.mengzhidui.a7springboot.response.R;
import com.mengzhidui.a7springboot.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    /*
     * 教师保存考勤（新增或更新）
     */
    @PostMapping("/teacher/attendance/save")
    @PreAuthorize("hasRole('TEACHER')")
    public R saveAttendance(@RequestBody Map<String, Object> params) {
        return attendanceService.saveAttendance(params);
    }

    /*
     * 教师查询考勤列表
     */
    @PostMapping("/teacher/attendance/list")
    @PreAuthorize("hasRole('TEACHER')")
    public R listAttendance(@RequestBody Map<String, String> params) {
        return attendanceService.listAttendance(params);
    }

    /*
     * 教师删除单条考勤
     */
    @DeleteMapping("/teacher/attendance/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public R deleteAttendance(@PathVariable Long id) {
        return attendanceService.deleteAttendance(id);
    }

    /*
     * 教师批量删除考勤
     */
    @DeleteMapping("/teacher/attendance/batch")
    @PreAuthorize("hasRole('TEACHER')")
    public R batchDeleteAttendance(@RequestBody Map<String, Object> params) {
        return attendanceService.batchDeleteAttendance(params);
    }

    /*
     * 学生查询自己的考勤记录
     */
    @GetMapping("/student/attendance/myRecords")
    @PreAuthorize("hasRole('STUDENT')")
    public R getMyRecords(@RequestParam String student_username) {
        Map<String, String> params = Map.of("student_username", student_username);
        return attendanceService.getMyRecords(params);
    }
}
