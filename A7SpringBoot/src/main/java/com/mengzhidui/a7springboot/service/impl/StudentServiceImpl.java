package com.mengzhidui.a7springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.gson.JsonObject;
import com.mengzhidui.a7springboot.entity.dto.StudentDTO;
import com.mengzhidui.a7springboot.entity.po.*;
import com.mengzhidui.a7springboot.entity.dto.JwtDTO;

import com.mengzhidui.a7springboot.mapper.*;
import com.mengzhidui.a7springboot.response.R;
import com.mengzhidui.a7springboot.service.LangChainClientService;
import com.mengzhidui.a7springboot.service.StudentService;
import com.mengzhidui.a7springboot.utils.IdGenerator;
import com.mengzhidui.a7springboot.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String,Object> redisTemplate;
    private final MenuMapper menuMapper;
    private final StudentMapper studentMapper;
    private final PasswordEncoder passwordEncoder;
    private final QuestionDetailRecordMapper questionDetailRecordMapper;
    private final SelfPractiseQuestionDetailRecordMapper selfPractiseQuestionDetailRecordMapper;
    private final CourseMapper courseMapper;
    private final CourseDetailRecordMapper courseDetailRecordMapper;
    private final AllStudentClickRecordMapper allStudentClickRecordMapper;
    private final LangChainClientService langChainClient;
    private final HomeworkPublishedMapper homeworkPublishedMapper;
    private final SubmissionRecordMapper submissionRecordMapper;


    private final String Data = "data";
    private final String Input = "input";
    private final String StudentUsername = "student_username";
    private final String TeacherUsername = "teacher_username";
    private final String QuestionId = "question_id";
    private final String IssueName = "issue_name";
    private final TeacherMapper teacherMapper;


    @Value("${jwt.expiration}")
    private Long EXPIRATION_TIME;
    @Override
    public R login(StudentPO student) {
        try {
            LambdaQueryWrapper<StudentPO> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(StudentPO::getUsername, student.getUsername());
            StudentPO findStudent = studentMapper.selectOne(wrapper);
            if (findStudent == null) {
                return R.error("登录失败");
            }
            // 验证用户名和密码
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            student.getUsername(),
                            student.getPassword()
                    )
            );

            // 设置认证上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);

            Map<String, Object> claims = Map.of("role","STUDENT");
            String token = jwtUtils.generateToken(student.getUsername(),claims);

            // 存储到 Redis（键为 "jwt:token:{token}"，值为 username）
            String redisKey = "jwt:student:token:" + token;
            redisTemplate.opsForValue()
                    .set(redisKey, authentication.getName(), EXPIRATION_TIME, TimeUnit.MILLISECONDS); // 设置相同的过期时间

            log.info( "学生{}登录成功，Token: {}",student.getUsername(),token);
            return R.success("登录成功", token);
        }catch (BadCredentialsException e) {
            // 密码错误
            log.error("用户名密码错误");
            return R.error("用户名或密码错误");
        } catch (UsernameNotFoundException e) {
            // 用户名不存在
            log.error("用户不存在");
            return R.error("用户名或密码错误");

        } catch (DisabledException e) {
            // 用户被禁用
            log.error("用户已被禁用");
            return R.error("该账号已被封禁");

        } catch (AuthenticationException e) {
            // 其他认证异常
            log.error( "认证失败");
            return R.error("认证失败");
        }
    }

    @Override
    public R loginOut(JwtDTO jwt) {
        String pattern = "jwt:*:token:" + jwt.getToken();
        ScanOptions options = ScanOptions.scanOptions()
                .match(pattern)
                .count(100)
                .build();

        // 使用非Pipeline方式获取所有匹配的键
        Set<String> keysToDelete = new HashSet<>();
        Cursor<byte[]> cursor = null;

        try {
            cursor = redisTemplate.executeWithStickyConnection(
                    redisConnection -> redisConnection.scan(options));

            while (cursor.hasNext()) {
                keysToDelete.add(new String(cursor.next(), StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            log.error("Error scanning Redis keys", e);
            return R.error("登出失败");
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    log.warn("Error closing Redis cursor", e);
                }
            }
        }

        // 批量删除匹配的键
        if (!keysToDelete.isEmpty()) {
            redisTemplate.delete(keysToDelete);
            log.info("登出成功，已删除 " + keysToDelete.size() + " 个缓存");
            return R.success("登出成功");
        } else {
            return R.error("登录超时，请重新登录");
        }
    }

    @Override
    @Cacheable(value = "menus", key = "'student_menu'", unless = "#result == null || #result.code != 200")
    public R getMenu() {
        LambdaQueryWrapper<MenuPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MenuPO::getAuth_role, "STUDENT");
        List<MenuPO> menus = menuMapper.selectList(wrapper);
        if (menus == null || menus.size() == 0){
            return R.error("获取学生端菜单失败");
        }else {
            return R.success("获取学生端菜单成功",menus);
        }
    }

    @Override
    @Transactional
    public R register(StudentDTO student) {
        String blindingTeacherCode = student.getTeacher_binding_code();
        if (blindingTeacherCode.equals("")){
            StudentPO studentPO = new StudentPO();
            studentPO.setId(IdGenerator.generateUniqueId());
            studentPO.setUsername(student.getStudent_account());
            studentPO.setPassword(passwordEncoder.encode(student.getStudent_password()));
            studentPO.setName(student.getStudent_name());
            studentPO.setAge(student.getStudent_age());
            studentPO.setGender(student.getStudent_gender());
            if(studentMapper.checkUsernameExists(studentPO.getUsername())){
                return R.error("用户名已存在");
            }
            studentMapper.insert(studentPO);
            StudentPO isExist = studentMapper.selectById(studentPO.getId());
            if (isExist != null){
                return R.success("注册成功");
            }else{
                return R.error("注册失败");
            }
        }else{
            TeacherPO teacherPO = teacherMapper.selectOne(new LambdaQueryWrapper<TeacherPO>().eq(TeacherPO::getTeacher_binding_code, blindingTeacherCode));
            if (teacherPO == null){
                return R.error("教师绑定码错误");
            }
            StudentPO studentPO = new StudentPO();
            studentPO.setId(IdGenerator.generateUniqueId());
            studentPO.setUsername(student.getStudent_account());
            studentPO.setPassword(passwordEncoder.encode(student.getStudent_password()));
            studentPO.setName(student.getStudent_name());
            studentPO.setAge(student.getStudent_age());
            studentPO.setGender(student.getStudent_gender());
            studentPO.setMajor(teacherPO.getMajor());
            studentPO.setClass_name(teacherPO.getClass_name());
            studentPO.setTeacher_binding_code(teacherPO.getTeacher_binding_code());
            if(studentMapper.checkUsernameExists(studentPO.getUsername())){
                return R.error("用户名已存在");
            }
            studentMapper.insert(studentPO);
            StudentPO isExist = studentMapper.selectById(studentPO.getId());
            if (isExist != null){
                int updateNumber = Integer.parseInt(teacherPO.getClass_student_number());
                updateNumber++;
                teacherPO.setClass_student_number(String.valueOf(updateNumber));
                teacherMapper.updateById(teacherPO);
                return R.success("注册成功");
            }else{
                return R.error("注册失败");
            }
        }
    }

    @Override
    public R cozeAnswer(Map<String, String> params) {
        JsonObject output = langChainClient.studentAnswer(params);
        if (output.has("error")) {
            return R.error("AI服务调用失败: " + output.get("error").getAsString());
        }
        Map<String,String> map = new HashMap<>();
        map.put("answerQuestion", output.get("answerQuestion").getAsString());
        UpdateWrapper<AllStudentClickRecordPO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.apply("DATE(create_time) = CURDATE()")
                .setSql("answer = answer + 1");
        allStudentClickRecordMapper.update(null, updateWrapper);
        return R.success("获取成功",List.of(map));
    }

    @Override
    public R cozeSelfPractise(Map<String, String> params) {
        String studentUsername = params.get(StudentUsername);
        String courseName = params.get(Data);
        String teacherUsername = params.get(TeacherUsername);

        JsonObject output = langChainClient.selfPractise(params);
        if (output.has("error")) {
            return R.error("AI服务调用失败: " + output.get("error").getAsString());
        }

        SelfPractiseQuestionDetailRecordPO record = SelfPractiseQuestionDetailRecordPO.builder()
                .user_category("STUDENT")
                .user_username(studentUsername)
                .question_id(output.get("studentsReviewExercisesQuestionId").getAsString())
                .teacher_username(teacherUsername)
                .course_name(courseName)
                .build();
        selfPractiseQuestionDetailRecordMapper.insert(record);

        Map<String,String> map = new HashMap<>();
        map.put("studentsReviewExercises", output.get("studentsReviewExercises").getAsString());

        UpdateWrapper<AllStudentClickRecordPO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.apply("DATE(create_time) = CURDATE()")
                .setSql("self_practise = self_practise + 1");
        allStudentClickRecordMapper.update(null, updateWrapper);

        return R.success("获取成功",List.of(map));
    }

    @Override
    public R cozeSelfPractiseCorrect(Map<String, String> params) {
        String studentUsername = params.get(StudentUsername);
        String studentAnswer = params.get(Input);
        String courseName = params.get(Data);
        String teacherUsername = params.get(TeacherUsername);

        //获取最近一次的教师发题
        LambdaQueryWrapper<SelfPractiseQuestionDetailRecordPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SelfPractiseQuestionDetailRecordPO::getUser_category,"STUDENT")
                .eq(SelfPractiseQuestionDetailRecordPO::getUser_username,studentUsername)
                .eq(SelfPractiseQuestionDetailRecordPO::getCourse_name,courseName)
                .orderByDesc(SelfPractiseQuestionDetailRecordPO::getRecord_id) //由于是自增，所以取record_id最大的一条数据即为最新的对应数据
                .last("LIMIT 1");

        SelfPractiseQuestionDetailRecordPO selfPractiseQuestionDetailRecordPO = selfPractiseQuestionDetailRecordMapper.selectOne(wrapper);
        String questionId = selfPractiseQuestionDetailRecordPO.getQuestion_id();

        JsonObject output = langChainClient.selfPractiseCorrect(params);
        if (output.has("error")) {
            return R.error("AI服务调用失败: " + output.get("error").getAsString());
        }
        Map<String,String> map = new HashMap<>();
        map.put("studentCorrectErrors", output.get("studentCorrectErrors").getAsString());
        return R.success("获取成功",List.of(map));
    }

    @Override
    public R cozeCoursePractise(Map<String, String> params) {
        JsonObject output = langChainClient.coursePractise(params);
        if (output.has("error")) {
            return R.error("AI服务调用失败: " + output.get("error").getAsString());
        }
        Map<String,String> map = new HashMap<>();
        map.put("studentGetRecentQuestion", output.get("studentGetRecentQuestion").getAsString());
        UpdateWrapper<AllStudentClickRecordPO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.apply("DATE(create_time) = CURDATE()")
                .setSql("course_practise = course_practise + 1");
        allStudentClickRecordMapper.update(null, updateWrapper);
        return R.success("获取成功",List.of(map));
    }

    @Override
    public R cozeCoursePractiseCorrectAnswer(Map<String, String> params) {
        JsonObject output = langChainClient.coursePractiseCorrectAnswer(params);
        if (output.has("error")) {
            return R.error("AI服务调用失败: " + output.get("error").getAsString());
        }
        Map<String,String> map = new HashMap<>();
        map.put("compareAnswer", output.get("compareAnswer").getAsString());
        return R.success("获取成功",List.of(map));
    }

    @Override
    public R getSelfStudentDetail(String student_id) {
        LambdaQueryWrapper<StudentPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentPO::getUsername, student_id);
        StudentPO studentPO = studentMapper.selectOne(wrapper);
        if (studentPO == null){
            return R.error("没有该学生");
        }

        List<CoursePO> courses = studentMapper.getCoursesByStudentId(studentPO.getId());
        StudentDTO studentDTO =  StudentDTO.builder()
                .student_id(studentPO.getId().intValue())
                .student_account(studentPO.getUsername())
                .student_name(studentPO.getName())
                .student_gender(studentPO.getGender())
                .student_age(studentPO.getAge())
                .student_major(studentPO.getMajor())
                .student_class_name(studentPO.getClass_name())
                .teacher_binding_code(studentPO.getTeacher_binding_code())
                .student_classList(courses)
                .create_time(studentPO.getCreate_time())
                .update_time(studentPO.getUpdate_time())
                .build();
        return R.success("获取学生信息成功",studentDTO);
    }

    @Override
    @Transactional
    public R JoinCourse(Map<String, String> params) {
        String studentId = params.get("student_id"); //前端未做类型转换，不管了
        String inviteCode = params.get("course_invite_code");
        String studentUsername = params.get("student_username");
        log.info("JoinCourse 接收参数: student_id={}, student_username={}, course_invite_code={}", studentId, studentUsername, inviteCode);
        LambdaQueryWrapper<CoursePO> cWrapper = new LambdaQueryWrapper<>();
        cWrapper.eq(CoursePO::getCourse_invite_code,inviteCode);
        CoursePO coursePO = courseMapper.selectOne(cWrapper);
        if (coursePO == null){
            return R.error("没有该课程");
        }

        StudentPO studentPO = studentMapper.selectOne(
                new LambdaQueryWrapper<StudentPO>().eq(StudentPO::getUsername, studentUsername));
        if (studentPO == null) {
            log.warn("JoinCourse 未找到学生: username='{}'", studentUsername);
            return R.error("学生信息不存在，查询用户名: " + studentUsername);
        }

        LambdaQueryWrapper<CourseDetailRecordPO> cdrWrapper = new LambdaQueryWrapper<>();
        cdrWrapper.eq(CourseDetailRecordPO::getCourse_id,coursePO.getCourse_id())
                .eq(CourseDetailRecordPO::getUser_id,studentPO.getId())
                .eq(CourseDetailRecordPO::getUser_category,"STUDENT");
        CourseDetailRecordPO exist = courseDetailRecordMapper.selectOne(cdrWrapper);
        if (exist != null){
            return R.error("该学生已加入该课程");
        }
        CourseDetailRecordPO courseDetailRecordPO = CourseDetailRecordPO.builder()
                .course_id(coursePO.getCourse_id())
                .user_id(studentPO.getId())
                .user_category("STUDENT")
                .build();
        int row = courseDetailRecordMapper.insert(courseDetailRecordPO);
        if (row == 0){
            return R.error("加入课程失败");
        }else{
            CoursePO updateCourse = CoursePO.builder()
                    .course_student_number(coursePO.getCourse_student_number() + 1)
                    .build();
            courseMapper.update(updateCourse,new LambdaUpdateWrapper<CoursePO>().eq(CoursePO::getCourse_id,coursePO.getCourse_id()));
            return R.success("加入课程成功");
        }
    }

    @Override
    @Transactional
    public R joinClass(Map<String, String> params) {
        String teacherBindingCode = params.get("teacher_binding_code");
        String studentUsername = params.get("student_username");
        TeacherPO teacherPO = teacherMapper.selectOne(new LambdaQueryWrapper<TeacherPO>()
                .eq(TeacherPO::getTeacher_binding_code,teacherBindingCode));
        if (teacherPO == null){
            return R.error("该绑定码没有教师");
        }
        int updateNumber = Integer.parseInt(teacherPO.getClass_student_number());
        updateNumber++;
        teacherPO.setClass_student_number(String.valueOf(updateNumber));
        StudentPO studentPO = studentMapper.selectOne(new LambdaQueryWrapper<StudentPO>()
                .eq(StudentPO::getUsername,studentUsername));
        studentPO.setMajor(teacherPO.getMajor());
        studentPO.setClass_name(teacherPO.getClass_name());
        studentPO.setTeacher_binding_code(teacherPO.getTeacher_binding_code());
        studentMapper.updateById(studentPO);
        return R.success("加入班级成功");
    }

    @Override
    public R getHomeworkPublished(Map<String, String> params) {
        String courseName = params.get("course_name");

        LambdaQueryWrapper<HomeworkPublishedPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HomeworkPublishedPO::getStatus, "published");
        if (courseName != null && !courseName.isEmpty()) {
            wrapper.eq(HomeworkPublishedPO::getCourse_name, courseName);
        }
        wrapper.orderByDesc(HomeworkPublishedPO::getCreated_at);
        List<HomeworkPublishedPO> list = homeworkPublishedMapper.selectList(wrapper);

        return R.success("获取成功", list != null ? list : List.of());
    }

    @Override
    public R getMySubmissions(Map<String, String> params) {
        String studentUsername = params.get("student_username");
        if (studentUsername == null || studentUsername.isEmpty()) {
            return R.error("缺少学生用户名");
        }

        LambdaQueryWrapper<SubmissionRecordPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SubmissionRecordPO::getStudent_username, studentUsername)
               .orderByDesc(SubmissionRecordPO::getSubmitted_at);
        List<SubmissionRecordPO> list = submissionRecordMapper.selectList(wrapper);

        return R.success("获取成功", list != null ? list : List.of());
    }

    @Override
    public R deleteSubmission(Map<String, String> params) {
        String homeworkId = params.get("homework_id");
        String studentUsername = params.get("student_username");
        if (homeworkId == null || studentUsername == null) {
            return R.error("缺少必要参数");
        }
        LambdaQueryWrapper<SubmissionRecordPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SubmissionRecordPO::getHomework_id, homeworkId)
               .eq(SubmissionRecordPO::getStudent_username, studentUsername);
        int deleted = submissionRecordMapper.delete(wrapper);
        if (deleted > 0) {
            return R.success("撤回成功");
        } else {
            return R.error("未找到提交记录");
        }
    }

    // ===== AI 通用聊天 =====

    @Override
    public R chat(Map<String, Object> params) {
        String jsonBody = new com.google.gson.Gson().toJson(params);
        JsonObject output = langChainClient.chatRaw(jsonBody);
        if (output.has("error")) {
            return R.error(output.get("error").getAsString());
        }
        String content = output.has("content") ? output.get("content").getAsString() : "";
        String model = output.has("model") ? output.get("model").getAsString() : "";
        Map<String, String> result = new java.util.HashMap<>();
        result.put("content", content);
        result.put("model", model);
        return R.success("success", result);
    }

    @Override
    public R listModels() {
        JsonObject output = langChainClient.listModels();
        if (output.has("code") && output.get("code").getAsInt() == 200) {
            return R.success("success", new com.google.gson.Gson().fromJson(output.get("data"), Object.class));
        }
        return R.success("success", java.util.List.of());
    }

}
