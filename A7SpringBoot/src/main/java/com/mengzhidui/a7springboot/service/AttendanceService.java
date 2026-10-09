package com.mengzhidui.a7springboot.service;

import com.mengzhidui.a7springboot.response.R;

import java.util.Map;

public interface AttendanceService {
    R saveAttendance(Map<String, Object> params);
    R listAttendance(Map<String, String> params);
    R deleteAttendance(Long id);
    R batchDeleteAttendance(Map<String, Object> params);
    R getMyRecords(Map<String, String> params);
}
