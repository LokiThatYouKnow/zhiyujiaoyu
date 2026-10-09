package com.mengzhidui.a7springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mengzhidui.a7springboot.entity.po.AttendancePO;
import com.mengzhidui.a7springboot.entity.po.AttendanceRecordPO;
import com.mengzhidui.a7springboot.mapper.AttendanceMapper;
import com.mengzhidui.a7springboot.mapper.AttendanceRecordMapper;
import com.mengzhidui.a7springboot.response.R;
import com.mengzhidui.a7springboot.service.AttendanceService;
import com.mengzhidui.a7springboot.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceMapper attendanceMapper;
    private final AttendanceRecordMapper attendanceRecordMapper;

    @Override
    @Transactional
    public R saveAttendance(Map<String, Object> params) {
        try {
            String courseName = (String) params.get("courseName");
            String teacherUsername = (String) params.get("teacherUsername");
            String date = (String) params.get("date");
            Integer period = params.get("period") != null ? (Integer) params.get("period") : null;
            String remark = (String) params.getOrDefault("remark", "");
            @SuppressWarnings("unchecked")
            List<Map<String, String>> records = (List<Map<String, String>>) params.get("records");

            if (courseName == null || teacherUsername == null || date == null || records == null) {
                return R.error("参数不完整");
            }

            // 检查是否已存在同课程+同日期+同节次的考勤，存在则更新
            LambdaQueryWrapper<AttendancePO> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AttendancePO::getCourse_name, courseName)
                   .eq(AttendancePO::getTeacher_username, teacherUsername)
                   .eq(AttendancePO::getDate, date);
            if (period != null) {
                wrapper.eq(AttendancePO::getPeriod, period);
            }
            AttendancePO existing = attendanceMapper.selectOne(wrapper);

            Long attendanceId;
            if (existing != null) {
                // 更新现有记录
                existing.setRemark(remark);
                attendanceMapper.updateById(existing);
                attendanceId = existing.getId();
                // 删除旧明细，重新插入
                LambdaQueryWrapper<AttendanceRecordPO> recordWrapper = new LambdaQueryWrapper<>();
                recordWrapper.eq(AttendanceRecordPO::getAttendance_id, attendanceId);
                attendanceRecordMapper.delete(recordWrapper);
            } else {
                AttendancePO attendance = AttendancePO.builder()
                        .id(IdGenerator.generateUniqueId())
                        .course_name(courseName)
                        .teacher_username(teacherUsername)
                        .date(date)
                        .period(period)
                        .remark(remark)
                        .build();
                attendanceMapper.insert(attendance);
                attendanceId = attendance.getId();
            }

            // 插入明细
            for (Map<String, String> record : records) {
                AttendanceRecordPO r = AttendanceRecordPO.builder()
                        .id(IdGenerator.generateUniqueId())
                        .attendance_id(attendanceId)
                        .student_name(record.get("studentName"))
                        .student_username(record.get("studentUsername"))
                        .status(record.get("status"))
                        .build();
                attendanceRecordMapper.insert(r);
            }

            return R.success("保存成功");
        } catch (Exception e) {
            log.error("保存考勤失败", e);
            return R.error("保存失败：" + e.getMessage());
        }
    }

    @Override
    public R listAttendance(Map<String, String> params) {
        try {
            String teacherUsername = params.get("teacher_username");
            String courseName = params.get("course_name");

            LambdaQueryWrapper<AttendancePO> wrapper = new LambdaQueryWrapper<>();
            if (teacherUsername != null && !teacherUsername.isEmpty()) {
                wrapper.eq(AttendancePO::getTeacher_username, teacherUsername);
            }
            if (courseName != null && !courseName.isEmpty()) {
                wrapper.eq(AttendancePO::getCourse_name, courseName);
            }
            wrapper.orderByDesc(AttendancePO::getDate).orderByAsc(AttendancePO::getPeriod);

            List<AttendancePO> attendances = attendanceMapper.selectList(wrapper);

            // 组装返回数据：每条 attendance 附加 presentCount/totalCount
            List<Map<String, Object>> result = new ArrayList<>();
            for (AttendancePO a : attendances) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", a.getId());
                item.put("course_name", a.getCourse_name());
                item.put("teacher_username", a.getTeacher_username());
                item.put("date", a.getDate());
                item.put("period", a.getPeriod());
                item.put("remark", a.getRemark());
                item.put("create_time", a.getCreate_time());

                LambdaQueryWrapper<AttendanceRecordPO> recordWrapper = new LambdaQueryWrapper<>();
                recordWrapper.eq(AttendanceRecordPO::getAttendance_id, a.getId());
                List<AttendanceRecordPO> records = attendanceRecordMapper.selectList(recordWrapper);
                item.put("records", records);
                item.put("totalCount", records.size());
                item.put("presentCount", records.stream().filter(r -> "present".equals(r.getStatus())).count());

                result.add(item);
            }

            return R.success("查询成功", result);
        } catch (Exception e) {
            log.error("查询考勤列表失败", e);
            return R.error("查询失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public R deleteAttendance(Long id) {
        try {
            // 删除明细
            LambdaQueryWrapper<AttendanceRecordPO> recordWrapper = new LambdaQueryWrapper<>();
            recordWrapper.eq(AttendanceRecordPO::getAttendance_id, id);
            attendanceRecordMapper.delete(recordWrapper);
            // 删除主记录
            attendanceMapper.deleteById(id);
            return R.success("删除成功");
        } catch (Exception e) {
            log.error("删除考勤失败", e);
            return R.error("删除失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public R batchDeleteAttendance(Map<String, Object> params) {
        try {
            @SuppressWarnings("unchecked")
            List<Integer> ids = (List<Integer>) params.get("ids");
            if (ids == null || ids.isEmpty()) {
                return R.error("请选择要删除的记录");
            }
            for (Integer id : ids) {
                deleteAttendance(id.longValue());
            }
            return R.success("批量删除成功");
        } catch (Exception e) {
            log.error("批量删除考勤失败", e);
            return R.error("批量删除失败：" + e.getMessage());
        }
    }

    @Override
    public R getMyRecords(Map<String, String> params) {
        try {
            String studentUsername = params.get("student_username");
            if (studentUsername == null || studentUsername.isEmpty()) {
                return R.error("参数不完整");
            }

            LambdaQueryWrapper<AttendanceRecordPO> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AttendanceRecordPO::getStudent_username, studentUsername);
            List<AttendanceRecordPO> records = attendanceRecordMapper.selectList(wrapper);

            // 关联查询 attendance 主表获取 course_name/date/period/remark
            List<Map<String, Object>> result = new ArrayList<>();
            for (AttendanceRecordPO record : records) {
                AttendancePO attendance = attendanceMapper.selectById(record.getAttendance_id());
                if (attendance == null) continue;
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", record.getId());
                item.put("course_name", attendance.getCourse_name());
                item.put("date", attendance.getDate());
                item.put("period", attendance.getPeriod());
                item.put("status", record.getStatus());
                item.put("remark", attendance.getRemark());
                result.add(item);
            }
            // 按日期降序排列
            result.sort((a, b) -> String.valueOf(b.get("date")).compareTo(String.valueOf(a.get("date"))));

            return R.success("查询成功", result);
        } catch (Exception e) {
            log.error("查询考勤记录失败", e);
            return R.error("查询失败：" + e.getMessage());
        }
    }
}
