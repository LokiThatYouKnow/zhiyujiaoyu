package com.mengzhidui.a7springboot.enums;

import lombok.Getter;

@Getter
public enum SwitchClientAndService {

    STUDENT_ANSWER("学生端","根据知识点解答学生问题"),
    STUDENT_SELF_PRACTISE("学生端","根据学生历史答题情况和学生要求生成题目"),
    STUDENT_SELF_PRACTISE_CORRECT("学生端","对比问题答案并纠错"),
    STUDENT_COURSE_PRACTISE("学生端","获取最新题目"),
    STUDENT_COURSE_PRACTISE_CORRECT_ANSWER("学生端","对照设计题目答案"),
    TEACHER_CONCLUDE_DATA("教师端","知识总结"),
    TEACHER_PRACTISE_DESIGN("教师端","设计实训练习题目"),
    TEACHER_ARRANGE_TIME("教师端","教学环节的时间分布"),
    TEACHER_DESIGN_ISSUE("教师端","设计题目并给出答案"),
    TEACHER_ADD_COURSE("教师端","新增课程"),
    TEACHER_GET_ISSUE_LIST("教师端","获取课程练习"),
    TEACHER_GET_ISSUE_ANSWER_DETAIL("教师端","分析总体答题情况并给出建议"),
    TEACHER_GET_RESOURCE_LIST("教师端","获取课程课件资源"),
    TEACHER_DELETE_ISSUE("教师端","删除对应练习"),
    TEACHER_DELETE_RESOURCE("教师端","删除对应的课件资源"),
    TEACHER_DELETE_ERROR_THEN_RECOVER_ISSUE("教师端","删除对应练习失败后恢复数据"),
    TEACHER_DELETE_COURSE("教师端","删除对应课程相关的练习和资源"),
    TEACHER_DELETE_ERROR_THEN_RECOVER_COURSE("教师端","恢复课程相关的练习和资源"),
    ADMIN_GET_ISSUE_LIST("管理端","获取练习集合"),
    ADMIN_GET_RESOURCE_LIST("管理端","获取课件资源集合"),
    ADMIN_GET_ALL_COST_TIME_RECORD("管理端","获取所有耗时记录"),
    ADMIN_DELETE_ALL_COST_TIME_RECORD("管理端","删除所有耗时记录"),
    ADMIN_GET_ISSUE_CORRECT_AVG_RECORD("管理端","学生课程练习平均正确率"),
    ADMIN_GET_KNOWLEDGE_POINT_COMMEND_DEGREE_RECORD("管理端","知识点掌握情况"),
    ADMIN_DELETE_TEACHER_INFO("管理端","删除教师相关信息"),
    ADMIN_UPDATE_TEACHER_INFO("管理端","修改教师相关信息"),
    ADMIN_DELETE_COURSE("管理端","删除课程"),
    ADMIN_DELETE_ISSUE("管理端","删除练习"),
    ADMIN_DELETE_RESOURCE("管理端","删除资源");


    private final String Client;
    private final String Service;

    SwitchClientAndService(String Client, String Service) {
        this.Client = Client;
        this.Service = Service;
    }

}
