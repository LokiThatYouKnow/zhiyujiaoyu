package com.mengzhidui.a7springboot.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mengzhidui.a7springboot.entity.po.AllStudentClickRecordPO;
import com.mengzhidui.a7springboot.entity.po.AllTeacherClickRecordPO;
import com.mengzhidui.a7springboot.mapper.AllStudentClickRecordMapper;
import com.mengzhidui.a7springboot.mapper.AllTeacherClickRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 定时任务 - 本地数据库管理
 * 原 COZE 数据同步任务已移除，数据由 LangChain 服务直接写入本地数据库
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledTask implements CommandLineRunner {

    private final AllTeacherClickRecordMapper allTeacherClickRecordMapper;
    private final AllStudentClickRecordMapper allStudentClickRecordMapper;

    // 应用启动时执行
    @Override
    public void run(String... args) {
        // 计算今天00:00:00和明天00:00:00
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime tomorrowStart = today.plusDays(1).atStartOfDay();

        // 处理教师记录
        QueryWrapper<AllTeacherClickRecordPO> teacherQuery = new QueryWrapper<>();
        teacherQuery.ge("create_time", todayStart)
                .lt("create_time", tomorrowStart);
        AllTeacherClickRecordPO teacherRecord = allTeacherClickRecordMapper.selectOne(teacherQuery);

        if (teacherRecord == null) {
            AllTeacherClickRecordPO newRecord = new AllTeacherClickRecordPO();
            newRecord.setCreate_time(LocalDateTime.now());
            allTeacherClickRecordMapper.insert(newRecord);
            log.info("插入教师今日记录：{}", newRecord.getCreate_time());
        } else {
            log.info("教师今日记录已存在：{}", teacherRecord.getCreate_time());
        }

        // 处理学生记录
        QueryWrapper<AllStudentClickRecordPO> studentQuery = new QueryWrapper<>();
        studentQuery.ge("create_time", todayStart)
                .lt("create_time", tomorrowStart);
        AllStudentClickRecordPO studentRecord = allStudentClickRecordMapper.selectOne(studentQuery);

        if (studentRecord == null) {
            AllStudentClickRecordPO newRecord = new AllStudentClickRecordPO();
            newRecord.setCreate_time(LocalDateTime.now());
            allStudentClickRecordMapper.insert(newRecord);
            log.info("插入学生今日记录：{}", newRecord.getCreate_time());
        } else {
            log.info("学生今日记录已存在：{}", studentRecord.getCreate_time());
        }

        log.info("A7 本地定时任务初始化完成（LangChain模式）");
    }

    // 每天凌晨0点创建点击记录
    @Scheduled(cron = "0 0 0 * * ?")
    public void createClickRecord() {
        log.info("开始创建今日点击记录");

        QueryWrapper<AllTeacherClickRecordPO> teacherWrapper = new QueryWrapper<>();
        teacherWrapper.apply("DATE(create_time) = CURDATE()");
        if (allTeacherClickRecordMapper.selectOne(teacherWrapper) == null) {
            allTeacherClickRecordMapper.insert(new AllTeacherClickRecordPO());
        }

        QueryWrapper<AllStudentClickRecordPO> studentWrapper = new QueryWrapper<>();
        studentWrapper.apply("DATE(create_time) = CURDATE()");
        if (allStudentClickRecordMapper.selectOne(studentWrapper) == null) {
            allStudentClickRecordMapper.insert(new AllStudentClickRecordPO());
        }

        log.info("今日点击记录创建完成");
    }

    /**
     * 注：以下定时任务已从 COZE 迁移为本地模式。
     * 耗时记录、练习正确率、知识点掌握度等数据
     * 现在由 LangChain 服务在每次调用后直接写入本地数据库。
     * 无需再通过定时任务从远程同步。
     */
}
