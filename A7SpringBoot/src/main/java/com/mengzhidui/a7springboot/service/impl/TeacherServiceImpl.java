package com.mengzhidui.a7springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.gson.JsonObject;
import com.mengzhidui.a7springboot.entity.dto.StudentDTO;
import com.mengzhidui.a7springboot.entity.dto.TeacherDTO;
import com.mengzhidui.a7springboot.entity.po.*;
import com.mengzhidui.a7springboot.entity.dto.JwtDTO;
import com.mengzhidui.a7springboot.mapper.*;
import com.mengzhidui.a7springboot.response.R;
import com.mengzhidui.a7springboot.service.LangChainClientService;
import com.mengzhidui.a7springboot.service.TeacherService;
import com.mengzhidui.a7springboot.utils.IdGenerator;
import com.mengzhidui.a7springboot.utils.JwtUtils;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String,Object> redisTemplate;
    private final MenuMapper menuMapper;
    private final TeacherMapper teacherMapper;
    private final PasswordEncoder passwordEncoder;
    private final MinioClient minioClient;
    private final QuestionDetailRecordMapper questionDetailRecordMapper;
    private final CourseMapper courseMapper;
    private final CourseDetailRecordMapper courseDetailRecordMapper;
    private final AllTeacherClickRecordMapper allTeacherClickRecordMapper;
    private final IssueCorrectAVGRecordMapper issueCorrectAVGRecordMapper;
    private final KnowledgePointCommendDegreeRecordMapper knowledgePointCommendDegreeRecordMapper;
    private final LangChainClientService langChainClient;
    private final SubmissionRecordMapper submissionRecordMapper;
    private final HomeworkPublishedMapper homeworkPublishedMapper;
    private final StudentMapper studentMapper;

    private final String Data = "data";
    private final String Input = "input";
    private final String StudentUsername = "student_username";
    private final String TeacherUsername = "teacher_username";
    private final String IssueName = "issue_name";
    private final String StartTime = "start_time";
    private final String EndTime = "end_time";
    private final String QuestionId = "question_id";
    private final String ResourceName = "resource_name";
    private final String ResourceId = "resource_id";

    @Value("${jwt.expiration}")
    private Long EXPIRATION_TIME;

    @Override
    public R login(TeacherPO teacher) {
        LambdaQueryWrapper<TeacherPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherPO::getUsername, teacher.getUsername());
        TeacherPO findTeacher = teacherMapper.selectOne(wrapper);
        if (findTeacher == null) {
            return R.error("登录失败");
        }
        try {
            // 验证用户名和密码
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            teacher.getUsername(),
                            teacher.getPassword()
                    )
            );

            // 设置认证上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);

            Map<String, Object> claims = Map.of("role","TEACHER");
            String token = jwtUtils.generateToken(teacher.getUsername(),claims);

            // 存储到 Redis（键为 "jwt:token:{token}"，值为 username）
            String redisKey = "jwt:teacher:token:" + token;
            redisTemplate.opsForValue()
                    .set(redisKey, authentication.getName(), EXPIRATION_TIME, TimeUnit.MILLISECONDS); // 设置相同的过期时间

            log.info( "教师{}登录成功，Token: {}",teacher.getUsername(),token);
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
    @Cacheable(value = "menus", key = "'teacher_menu'", unless = "#result == null || #result.code != 200")
    public R getMenu() {
        LambdaQueryWrapper<MenuPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MenuPO::getAuth_role, "TEACHER");
        List<MenuPO> menus = menuMapper.selectList(wrapper);
        if (menus == null || menus.size() == 0){
            return R.error("获取教师端菜单失败");
        }else {
            return R.success("获取教师端菜单成功",menus);
        }
    }

    @Override
    public R register(TeacherDTO teacher) {
        if (teacher.getTeacher_position().equals("班主任")){
            TeacherPO teacherPO = new TeacherPO();
            teacherPO.setId(IdGenerator.generateUniqueId());
            teacherPO.setUsername(teacher.getTeacher_account());
            teacherPO.setPassword(passwordEncoder.encode(teacher.getTeacher_password()));
            teacherPO.setName(teacher.getTeacher_name());
            teacherPO.setAge(teacher.getTeacher_age());
            teacherPO.setGender(teacher.getTeacher_gender());
            teacherPO.setMajor(teacher.getTeacher_major());
            teacherPO.setPosition(teacher.getTeacher_position());
            teacherPO.setClass_name(teacher.getTeacher_class_name());
            teacherPO.setClass_student_number("0");
            teacherPO.setTeacher_binding_code(UUID.randomUUID().toString());
            if(teacherMapper.checkUsernameExists(teacherPO.getUsername())){
                return R.error("用户名已存在");
            }
            teacherMapper.insert(teacherPO);
            TeacherPO isExist = teacherMapper.selectById(teacherPO.getId());
            if (isExist != null){
                return R.success("注册成功");
            }else{
                return R.error("注册失败");
            }
        }else{
            TeacherPO teacherPO = new TeacherPO();
            teacherPO.setId(IdGenerator.generateUniqueId());
            teacherPO.setUsername(teacher.getTeacher_account());
            teacherPO.setPassword(passwordEncoder.encode(teacher.getTeacher_password()));
            teacherPO.setName(teacher.getTeacher_name());
            teacherPO.setAge(teacher.getTeacher_age());
            teacherPO.setGender(teacher.getTeacher_gender());
            teacherPO.setPosition(teacher.getTeacher_position());
            if(teacherMapper.checkUsernameExists(teacherPO.getUsername())){
                return R.error("用户名已存在");
            }
            teacherMapper.insert(teacherPO);
            TeacherPO isExist = teacherMapper.selectById(teacherPO.getId());
            if (isExist != null){
                return R.success("注册成功");
            }else{
                return R.error("注册失败");
            }
        }

    }

    @Override
    public R knowledgeOrganization(Map<String, String> params) {
        String knowledgePoint = params.get(Data);
        String teacherUsername = params.get(TeacherUsername);
        String resourceName = params.get(ResourceName);
        String startTime = params.get(StartTime);
        String need = params.get(Input);

        // 调用 LangChain 服务
        JsonObject output = langChainClient.knowledgeOrganization(params);

        if (output.has("error")) {
            return R.error("AI服务调用失败: " + output.get("error").getAsString());
        }

        Map<String,String> map = new HashMap<>();
        map.put("concludeData", output.get("concludeData").getAsString());

        // 进行次数统计
        UpdateWrapper<AllTeacherClickRecordPO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.apply("DATE(create_time) = CURDATE()")
                .setSql("knowledge_organization = knowledge_organization + 1");
        allTeacherClickRecordMapper.update(null, updateWrapper);

        return R.success("获取成功", List.of(map));
    }

    @Override
    public R PractiseDesign(Map<String, String> params) {
        JsonObject output = langChainClient.practiseDesign(params);
        if (output.has("error")) {
            return R.error("AI服务调用失败: " + output.get("error").getAsString());
        }
        Map<String,String> map = new HashMap<>();
        map.put("practise", output.get("practise").getAsString());
        UpdateWrapper<AllTeacherClickRecordPO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.apply("DATE(create_time) = CURDATE()")
                .setSql("practise_design = practise_design + 1");
        allTeacherClickRecordMapper.update(null, updateWrapper);
        return R.success("获取成功",List.of(map));
    }

    @Override
    public R ArrangeTime(Map<String, String> params) {
        JsonObject output = langChainClient.arrangeTime(params);
        if (output.has("error")) {
            return R.error("AI服务调用失败: " + output.get("error").getAsString());
        }
        Map<String,String> map = new HashMap<>();
        map.put("arrangeTime", output.get("arrangeTime").getAsString());
        return R.success("获取成功",List.of(map));
    }

    @Override
    public R DesignIssue(Map<String, String> params) {
        String courseName = params.get(Data);
        String teacherUsername = params.get(TeacherUsername);
        String issueName = params.get(IssueName);
        String teacherRequest = params.get(Input);
        String startTime = params.get(StartTime);
        String endTime = params.get(EndTime);

        JsonObject output = langChainClient.designIssue(params);
        if (output.has("error")) {
            return R.error("AI服务调用失败: " + output.get("error").getAsString());
        }

        // 智能出题为模板工具，不阻止同名再生；已存在则更新，否则新增
        LambdaQueryWrapper<QuestionDetailRecordPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionDetailRecordPO::getIssue_name, issueName);
        QuestionDetailRecordPO existing = questionDetailRecordMapper.selectOne(wrapper);

        QuestionDetailRecordPO questionDetailRecordPO = QuestionDetailRecordPO.builder()
                .user_category("TEACHER")
                .user_username(teacherUsername)
                .question_id(output.get("decideAndAnswerQuestionId").getAsString())
                .course_name(courseName)
                .issue_name(issueName)
                .start_time(startTime)
                .end_time(endTime)
                .build();

        if (existing != null) {
            questionDetailRecordMapper.update(questionDetailRecordPO, wrapper);
        } else {
            questionDetailRecordMapper.insert(questionDetailRecordPO);
        }

        Map<String,String> map = new HashMap<>();
        map.put("decideAndAnswer", output.get("decideAndAnswer").getAsString());

        //进行次数统计
        UpdateWrapper<AllTeacherClickRecordPO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.apply("DATE(create_time) = CURDATE()")
                .setSql("design_issue = design_issue + 1");
        allTeacherClickRecordMapper.update(null, updateWrapper);

        return R.success("获取成功",List.of(map));
    }

    @Override
    public R getSelfTeacherDetail(String teacherId){
        LambdaQueryWrapper<TeacherPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherPO::getUsername, teacherId);
        TeacherPO teacherPO = teacherMapper.selectOne(wrapper);
        if (teacherPO == null){
            return R.error("没有该教师");
        }

        List<CoursePO> courses = teacherMapper.selectCoursesByTeacherId(teacherPO.getId());

        List<StudentDTO> students = teacherMapper.selectStudentsByTeacherId(teacherPO.getId())
                .stream()
                .map(studentPO -> StudentDTO.builder()
                        .student_id(studentPO.getId().intValue())
                        .student_account(studentPO.getUsername())
                        .student_name(studentPO.getName())
                        .student_gender(studentPO.getGender())
                        .student_age(studentPO.getAge())
                        .student_major(studentPO.getMajor())
                        .student_class_name(studentPO.getClass_name())
                        .build()
                ).toList();


        List<String> courseResources = teacherMapper.selectMinioObjectRecordByTeacherId(teacherPO.getId())
                .stream()
                .map(minioObjectRecord -> {
                    try {
                        return minioClient.getPresignedObjectUrl(
                                GetPresignedObjectUrlArgs.builder()
                                        .method(Method.GET)
                                        .bucket(minioObjectRecord.getBucket_name())
                                        .object(minioObjectRecord.getObject_name())
                                        .expiry(600)
                                        .build()
                        );
                    } catch (Exception e) {
                        // 处理异常，例如记录日志或返回默认值
                        throw new RuntimeException("生成预签名URL失败", e);
                    }
                })
                .toList();


        TeacherDTO teacherDTO = TeacherDTO.builder()
                .teacher_id(teacherPO.getId().intValue())
                .teacher_account(teacherPO.getUsername())
                .teacher_name(teacherPO.getName())
                .teacher_gender(teacherPO.getGender())
                .teacher_age(teacherPO.getAge())
                .teacher_major(teacherPO.getMajor())
                .teacher_position(teacherPO.getPosition())
                .teacher_class_name(teacherPO.getClass_name())
                .teacher_binding_code(teacherPO.getTeacher_binding_code())
                .teacher_class_student_number(teacherPO.getClass_student_number())
                .teacher_studentList(students)
                .teacher_courseList(courses)
                .teacher_courseResource(courseResources)
                .build();

        return R.success("获取教师信息成功",teacherDTO);
    }

    @Override
    public R addCourse(MultipartFile file, String course_name,String teacher_username,String course_hours,String course_score) throws IOException {
        if (course_name == null || course_name.equals("")){
            return R.error("缺少课程名称");
        }

        LambdaQueryWrapper<CoursePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CoursePO::getCourse_name, course_name)
                .eq(CoursePO::getCourse_teacher_account, teacher_username);
        List<CoursePO> existCourseList = courseMapper.selectList(wrapper);
        if (existCourseList.size() != 0){
            return R.error("课程名已经存在");
        }

        if (file == null){
            Map<String, String> params = new HashMap<>();
            params.put(Data, course_name);
            params.put(TeacherUsername, teacher_username);

            JsonObject output = langChainClient.addCourseKnowledge(params);
            if (output.has("error")) {
                return R.error("AI服务调用失败: " + output.get("error").getAsString());
            }

            if (Objects.equals(output.get("addCourseKnowledgeByAICode").getAsString(), "0")){
                LambdaQueryWrapper<TeacherPO> teacherWrapper = new LambdaQueryWrapper<>();
                teacherWrapper.eq(TeacherPO::getUsername, teacher_username);
                TeacherPO teacher = teacherMapper.selectOne(teacherWrapper);
                if (teacher == null){
                    return R.error("教师不存在");
                }
                String invite_code = UUID.randomUUID().toString();
                CoursePO course = CoursePO.builder()
                        .course_name(course_name)
                        .course_teacher_id(teacher.getId())
                        .course_teacher_name(teacher.getName())
                        .course_teacher_account(teacher_username)
                        .course_student_number(0)
                        .course_hours(course_hours)
                        .course_score(course_score)
                        .course_invite_code(invite_code)
                        .course_document_id(output.get("addCourseKnowledgeByAIDocumentId").getAsString())
                        .build();
                courseMapper.insert(course);

                CourseDetailRecordPO courseDetailRecord = CourseDetailRecordPO.builder()
                        .user_category("TEACHER")
                        .user_id(teacher.getId())
                        .course_id(course.getCourse_id())
                        .build();

                courseDetailRecordMapper.insert(courseDetailRecord);

                //进行次数统计
                UpdateWrapper<AllTeacherClickRecordPO> updateWrapper = new UpdateWrapper<>();
                updateWrapper.apply("DATE(create_time) = CURDATE()")
                        .setSql("build_knowledge_base = build_knowledge_base + 1");
                allTeacherClickRecordMapper.update(null, updateWrapper);

                return R.success("添加课程成功",invite_code);
            }else{
                String errMsg = "未知错误";
                if (output.has("message")) {
                    errMsg = output.get("message").getAsString();
                }
                log.error("addCourseKnowledge 返回失败: code={}, message={}",
                    output.has("addCourseKnowledgeByAICode") ? output.get("addCourseKnowledgeByAICode").getAsString() : "null",
                    errMsg);
                return R.error("添加课程失败: " + errMsg);
            }
        }else{ //用户上传了文件的情况
            // 获取文件的字节数组
            byte[] fileBytes = file.getBytes();
            // 使用 Java 内置的 Base64 编码器
            String fileBase64 = Base64.getEncoder().encodeToString(fileBytes);

            Map<String, String> params = new HashMap<>();
            params.put(Data, course_name);
            params.put(TeacherUsername, teacher_username);
            params.put("file_base64", fileBase64);
            params.put("file_name", file.getOriginalFilename());

            JsonObject output = langChainClient.addCourseKnowledge(params);
            if (output.has("error")) {
                return R.error("AI服务调用失败: " + output.get("error").getAsString());
            }

            if (Objects.equals(output.get("addCourseKnowledgeByAICode").getAsString(), "0")){
                LambdaQueryWrapper<TeacherPO> teacherWrapper = new LambdaQueryWrapper<>();
                teacherWrapper.eq(TeacherPO::getUsername, teacher_username);
                TeacherPO teacher = teacherMapper.selectOne(teacherWrapper);
                if (teacher == null){
                    return R.error("教师不存在");
                }

                String invite_code = UUID.randomUUID().toString();
                CoursePO course = CoursePO.builder()
                        .course_name(course_name)
                        .course_teacher_id(teacher.getId())
                        .course_teacher_name(teacher.getName())
                        .course_teacher_account(teacher_username)
                        .course_student_number(0)
                        .course_hours(course_hours)
                        .course_score(course_score)
                        .course_invite_code(invite_code)
                        .course_document_id(output.get("addCourseKnowledgeByAIDocumentId").getAsString())
                        .build();
                courseMapper.insert(course);

                CourseDetailRecordPO courseDetailRecord = CourseDetailRecordPO.builder()
                        .user_category("TEACHER")
                        .user_id(teacher.getId())
                        .course_id(course.getCourse_id())
                        .build();

                courseDetailRecordMapper.insert(courseDetailRecord);

                return R.success("添加课程成功",invite_code);
            }else{
                String errMsg = "未知错误";
                if (output.has("message")) {
                    errMsg = output.get("message").getAsString();
                }
                log.error("addCourseKnowledge 返回失败: code={}, message={}",
                    output.has("addCourseKnowledgeByAICode") ? output.get("addCourseKnowledgeByAICode").getAsString() : "null",
                    errMsg);
                return R.error("添加课程失败: " + errMsg);
            }
        }
    }

    @Override
    @Transactional
    public R deleteCourse(Map<String,String> params) {
        JsonObject output = langChainClient.deleteCourse(params);
        if (output.has("error")) {
            return R.error("AI服务调用失败: " + output.get("error").getAsString());
        }

        String document_id = params.get("document_id");
        String teacherUsername = params.get(TeacherUsername);
        String courseName = params.get(Data);

        LambdaQueryWrapper<CoursePO> courseWrapper = new LambdaQueryWrapper<>();
        courseWrapper.eq(CoursePO::getCourse_document_id, document_id);
        CoursePO course = courseMapper.selectOne(courseWrapper);
        if (course == null){
            return R.error("课程不存在");
        }
        LambdaQueryWrapper<CourseDetailRecordPO> courseDetailRecordWrapper = new LambdaQueryWrapper<>();
        courseDetailRecordWrapper.eq(CourseDetailRecordPO::getCourse_id, course.getCourse_id());
        int deleteCDRRow = courseDetailRecordMapper.delete(courseDetailRecordWrapper);
        if (deleteCDRRow == 0){
            return R.error("删除课程详情记录失败");
        }
        LambdaQueryWrapper<QuestionDetailRecordPO> questionDetailRecordWrapper = new LambdaQueryWrapper<>();
        questionDetailRecordWrapper.eq(QuestionDetailRecordPO::getUser_username, course.getCourse_teacher_account())
                .eq(QuestionDetailRecordPO::getCourse_name, course.getCourse_name());
        int deleteQDRRow = questionDetailRecordMapper.delete(questionDetailRecordWrapper);
        //最后删除课程
        int deleteCRow = courseMapper.delete(courseWrapper);
        if (deleteCRow == 0){
            return R.error("删除课程失败");
        }
        return R.success("删除成功");
    }

    @Override
    public R getIssueList(Map<String,String> params) {
        JsonObject output = langChainClient.getIssueList(params);
        if (output.has("error")) {
            return R.error("AI服务调用失败: " + output.get("error").getAsString());
        }
        Map<String,String> map = new HashMap<>();
        map.put("getTeacherIssueList", output.get("getTeacherIssueList").getAsString());
        return R.success("获取成功",List.of(map));
    }

    @Override
    public R getIssueAnswerDetail(Map<String, String> params) {
        JsonObject output = langChainClient.getIssueAnswerDetail(params);
        if (output.has("error")) {
            return R.error("AI服务调用失败: " + output.get("error").getAsString());
        }
        Map<String,String> map = new HashMap<>();
        map.put("suggestionsAfterAnalysis", output.get("suggestionsAfterAnalysis").getAsString());
        //进行次数统计
        UpdateWrapper<AllTeacherClickRecordPO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.apply("DATE(create_time) = CURDATE()")
                .setSql("issue_answer_analysis = issue_answer_analysis + 1");
        allTeacherClickRecordMapper.update(null, updateWrapper);
        return R.success("获取成功",List.of(map));
    }

    @Override
    public R getResourceList(Map<String, String> params) {
        JsonObject output = langChainClient.getResourceList(params);
        if (output.has("error")) {
            return R.error("AI服务调用失败: " + output.get("error").getAsString());
        }
        Map<String,String> map = new HashMap<>();
        map.put("teacherResourceToGetResourceList", output.get("teacherResourceToGetResourceList").getAsString());
        return R.success("获取成功",List.of(map));
    }

    @Override
    public R deleteIssue(Map<String, String> params) {
        JsonObject output = langChainClient.deleteIssue(params);
        if (output.has("error")) {
            return R.error("AI服务调用失败: " + output.get("error").getAsString());
        }
        String questionId = params.get(QuestionId);
        LambdaQueryWrapper<QuestionDetailRecordPO> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(QuestionDetailRecordPO::getQuestion_id,questionId);
        int deleteRow = questionDetailRecordMapper.delete(deleteWrapper);
        if (deleteRow > 0){
            return R.success("删除成功");
        }else{
            return R.error("删除失败");
        }
    }

    @Override
    public R searchCourseware(Map<String, String> params) {
        JsonObject output = langChainClient.searchCourseware(params);
        if (output.has("error")) {
            return R.error("AI服务调用失败: " + output.get("error").getAsString());
        }
        Map<String,String> map = new HashMap<>();
        map.put("coursewareContent", output.get("coursewareContent").getAsString());
        map.put("searchQuery", output.get("searchQuery").getAsString());
        map.put("resultCount", output.get("resultCount").getAsString());
        // 进行次数统计
        UpdateWrapper<AllTeacherClickRecordPO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.apply("DATE(create_time) = CURDATE()")
                .setSql("search_courseware = search_courseware + 1");
        allTeacherClickRecordMapper.update(null, updateWrapper);
        return R.success("获取成功", List.of(map));
    }

    @Override
    public R deleteResource(Map<String, String> params) {
        JsonObject output = langChainClient.deleteResource(params);
        if (output.has("error")) {
            return R.error("AI服务调用失败: " + output.get("error").getAsString());
        }
        return R.success("删除课件资源成功");
    }

    @Override
    public List<IssueCorrectAVGRecordPO> getIssueCorrectAVGRecordByDateRange(String teacher_username, String course_name, LocalDate start, LocalDate end) {
        // 参数校验
        if (teacher_username == null || teacher_username.isEmpty() || course_name == null || course_name.isEmpty()) {
            return Collections.emptyList();
        }

        // 构建查询条件
        QueryWrapper<IssueCorrectAVGRecordPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("create_time", start.atStartOfDay(), end.atTime(LocalTime.MAX));
        queryWrapper.eq("teacher_username", teacher_username);
        queryWrapper.eq("course_name", course_name);

        // 执行查询获取所有记录
        List<IssueCorrectAVGRecordPO> records = issueCorrectAVGRecordMapper.selectList(queryWrapper);

        // 按issue_name分组，再按日期分组，并保留每天的最新记录
        Map<String, Map<LocalDate, IssueCorrectAVGRecordPO>> resultMap = new HashMap<>();

        // 遍历所有记录，按issue_name和日期进行分组处理
        for (IssueCorrectAVGRecordPO record : records) {
            String issueName = record.getIssue_name();
            LocalDate recordDate = record.getCreate_time().toLocalDate();

            // 获取或创建issue_name对应的日期Map
            Map<LocalDate, IssueCorrectAVGRecordPO> dateToRecordMap =
                    resultMap.computeIfAbsent(issueName, k -> new HashMap<>());

            // 获取该日期已存在的最新记录
            IssueCorrectAVGRecordPO existingRecord = dateToRecordMap.get(recordDate);

            // 如果当前记录的create_time更晚，则更新Map中的记录
            if (existingRecord == null || record.getCreate_time().isAfter(existingRecord.getCreate_time())) {
                // 设置日期字段
                record.setDate(record.getCreate_time().toLocalDate().atStartOfDay());
                dateToRecordMap.put(recordDate, record);
            }
        }

        // 生成最终结果列表
        List<IssueCorrectAVGRecordPO> finalResult = new ArrayList<>();

        // 遍历每个issue_name
        for (Map.Entry<String, Map<LocalDate, IssueCorrectAVGRecordPO>> entry : resultMap.entrySet()) {
            String issueName = entry.getKey();
            Map<LocalDate, IssueCorrectAVGRecordPO> dateToRecordMap = entry.getValue();

            // 为指定日期范围内的每一天生成记录
            for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                // 如果该日期有记录，则添加到结果列表
                if (dateToRecordMap.containsKey(date)) {
                    finalResult.add(dateToRecordMap.get(date));
                } else {
                    // 如果该日期没有记录，可以创建一个空记录
                    IssueCorrectAVGRecordPO emptyRecord = IssueCorrectAVGRecordPO.builder()
                            .issue_name(issueName)
                            .teacher_username(teacher_username)
                            .course_name(course_name)
                            .date(date.atStartOfDay())
                            .build();
                    finalResult.add(emptyRecord);
                }
            }
        }

        return finalResult;
    }

    @Override
    public List<KnowledgePointCommendDegreeRecordPO> getKnowledgePointCommendDegreeRecord(
            String teacherUsername, String courseName, LocalDate start, LocalDate end) {
        // 参数校验
        if (teacherUsername == null || teacherUsername.isEmpty()
                || courseName == null || courseName.isEmpty()
                || start == null || end == null || start.isAfter(end)) {
            return Collections.emptyList();
        }

        // 构建查询条件，查询指定区间的所有记录
        QueryWrapper<KnowledgePointCommendDegreeRecordPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("create_time", start.atStartOfDay(), end.atTime(LocalTime.MAX))
                .eq("teacher_username", teacherUsername)
                .eq("course_name", courseName);

        List<KnowledgePointCommendDegreeRecordPO> records = knowledgePointCommendDegreeRecordMapper.selectList(queryWrapper);
        if (records.isEmpty()) {
            return Collections.emptyList();
        }

        // 情况1：时间区间为1天（start和end是同一天），直接返回原始记录
        if (start.equals(end)) {
            return records;
        }

        // 情况2：时间区间大于1天，按knowledge_point分组计算
        // 1. 按knowledge_point分组
        Map<String, List<KnowledgePointCommendDegreeRecordPO>> groupByKnowledgePoint =
                records.stream()
                        .collect(Collectors.groupingBy(KnowledgePointCommendDegreeRecordPO::getKnowledge_point));

        // 2. 遍历每个分组，计算指标并构建新对象
        List<KnowledgePointCommendDegreeRecordPO> result = new ArrayList<>();
        for (Map.Entry<String, List<KnowledgePointCommendDegreeRecordPO>> entry : groupByKnowledgePoint.entrySet()) {
            String knowledgePoint = entry.getKey();
            List<KnowledgePointCommendDegreeRecordPO> groupRecords = entry.getValue();

            // 计算分组内的指标（处理空值和转换异常）
            int totalCommendDegree = 0; // commend_degree总和
            int totalErrorTime = 0;     // error_time总和
            int totalErrorRate = 0;     // error_rate总和
            int count = 0;              // 有效记录数（排除转换失败的记录）

            for (KnowledgePointCommendDegreeRecordPO record : groupRecords) {
                // 转换commend_degree为int（处理空值/非数字）
                int commendDegree = parseStringToInt(record.getCommend_degree());
                // 转换error_time为int
                int errorTime = parseStringToInt(record.getError_time());
                // 转换error_rate为int
                int errorRate = parseStringToInt(record.getError_rate());

                totalCommendDegree += commendDegree;
                totalErrorTime += errorTime;
                totalErrorRate += errorRate;
                count++; // 即使字段无效，也计数（避免除0）
            }

            // 计算平均值（强转int）
            int avgCommendDegree = count > 0 ? (int) ((double) totalCommendDegree / count) : 0;
            int avgErrorRate = count > 0 ? (int) ((double) totalErrorRate / count) : 0;

            // 构建分组后的对象（其他字段取分组中第一条记录的值，因查询条件已限定teacher和course）
            KnowledgePointCommendDegreeRecordPO groupedRecord = KnowledgePointCommendDegreeRecordPO.builder()
                    .teacher_username(groupRecords.get(0).getTeacher_username())
                    .course_name(groupRecords.get(0).getCourse_name())
                    .knowledge_point(knowledgePoint)
                    .commend_degree(String.valueOf(avgCommendDegree)) // 转回String类型
                    .error_time(String.valueOf(totalErrorTime))
                    .error_rate(String.valueOf(avgErrorRate))
                    .create_time(null) // 分组后无具体创建时间，可设为null或区间内任意时间
                    .build();

            result.add(groupedRecord);
        }

        return result;
    }

    /**
     * 辅助方法：将String转换为int（处理空值和格式错误）
     */
    private int parseStringToInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0; // 空值默认0
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            // 非数字格式默认0
            return 0;
        }
    }

    // ===== 作业/考试提交与批改 =====

    @Override
    @Transactional
    public R submitAnswer(Map<String, String> params) {
        String homeworkId = params.get("homework_id");
        String type = params.get("type");
        String studentUsername = params.get("student_username");
        String studentName = params.get("student_name");
        String courseName = params.get("course_name");
        String issueName = params.get("issue_name");
        String answers = params.get("answers");
        String scores = params.get("scores");

        if (homeworkId == null || type == null || studentUsername == null) {
            return R.error("缺少必要参数");
        }

        // 检查是否已提交
        LambdaQueryWrapper<SubmissionRecordPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SubmissionRecordPO::getHomework_id, homeworkId)
               .eq(SubmissionRecordPO::getStudent_username, studentUsername);
        SubmissionRecordPO existing = submissionRecordMapper.selectOne(wrapper);

        SubmissionRecordPO record = SubmissionRecordPO.builder()
                .homework_id(homeworkId)
                .type(type)
                .student_username(studentUsername)
                .student_name(studentName != null ? studentName : "")
                .course_name(courseName != null ? courseName : "")
                .issue_name(issueName != null ? issueName : "")
                .answers(answers != null ? answers : "[]")
                .scores(scores != null ? scores : "[]")
                .total_score(0.0)
                .max_total_score(0.0)
                .status("submitted")
                .submitted_at(System.currentTimeMillis())
                .build();

        if (existing != null) {
            record.setId(existing.getId());
            submissionRecordMapper.update(record, wrapper);
            return R.success("提交已更新");
        } else {
            submissionRecordMapper.insert(record);
            return R.success("提交成功");
        }
    }

    @Override
    public R getSubmissions(Map<String, String> params) {
        String homeworkId = params.get("homework_id");
        if (homeworkId == null || homeworkId.isEmpty()) {
            return R.error("缺少作业ID");
        }

        LambdaQueryWrapper<SubmissionRecordPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SubmissionRecordPO::getHomework_id, homeworkId)
               .orderByDesc(SubmissionRecordPO::getSubmitted_at);
        List<SubmissionRecordPO> list = submissionRecordMapper.selectList(wrapper);

        return R.success("获取成功", list != null ? list : List.of());
    }

    @Override
    public R getSubmissionDetail(Map<String, String> params) {
        String submissionId = params.get("submission_id");
        if (submissionId == null || submissionId.isEmpty()) {
            return R.error("缺少提交ID");
        }

        LambdaQueryWrapper<SubmissionRecordPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SubmissionRecordPO::getId, Integer.parseInt(submissionId));
        SubmissionRecordPO record = submissionRecordMapper.selectOne(wrapper);

        if (record == null) {
            return R.error("未找到该提交记录");
        }
        return R.success("获取成功", record);
    }

    @Override
    @Transactional
    public R gradeSubmission(Map<String, String> params) {
        String submissionId = params.get("submission_id");
        String scores = params.get("scores");        // JSON: [{"questionId":"","score":0,"maxScore":0}]
        String totalScore = params.get("total_score");
        String maxTotalScore = params.get("max_total_score");

        if (submissionId == null || submissionId.isEmpty()) {
            return R.error("缺少提交ID");
        }

        LambdaQueryWrapper<SubmissionRecordPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SubmissionRecordPO::getId, Integer.parseInt(submissionId));
        SubmissionRecordPO record = submissionRecordMapper.selectOne(wrapper);

        if (record == null) {
            return R.error("未找到该提交记录");
        }

        if (scores != null) record.setScores(scores);
        if (totalScore != null) record.setTotal_score(Double.parseDouble(totalScore));
        if (maxTotalScore != null) record.setMax_total_score(Double.parseDouble(maxTotalScore));
        record.setStatus("graded");
        record.setGraded_at(System.currentTimeMillis());

        submissionRecordMapper.update(record, wrapper);
        return R.success("批改已保存");
    }

    @Override
    public R getGradeSummary(Map<String, String> params) {
        String courseName = params.get("course_name");

        LambdaQueryWrapper<SubmissionRecordPO> wrapper = new LambdaQueryWrapper<>();
        if (courseName != null && !courseName.isEmpty()) {
            wrapper.eq(SubmissionRecordPO::getCourse_name, courseName);
        }
        wrapper.orderByDesc(SubmissionRecordPO::getSubmitted_at);
        List<SubmissionRecordPO> list = submissionRecordMapper.selectList(wrapper);

        // Calculate summary statistics
        int totalSubmissions = list != null ? list.size() : 0;
        long gradedSubmissions = list != null
            ? list.stream().filter(s -> "graded".equals(s.getStatus())).count()
            : 0;
        double avgScore = list != null && !list.isEmpty()
            ? list.stream()
                .filter(s -> "graded".equals(s.getStatus()) && s.getTotal_score() != null)
                .mapToDouble(SubmissionRecordPO::getTotal_score)
                .average().orElse(0.0)
            : 0.0;
        double highestScore = list != null && !list.isEmpty()
            ? list.stream()
                .filter(s -> "graded".equals(s.getStatus()) && s.getTotal_score() != null)
                .mapToDouble(SubmissionRecordPO::getTotal_score)
                .max().orElse(0.0)
            : 0.0;

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalSubmissions", totalSubmissions);
        summary.put("gradedSubmissions", (int) gradedSubmissions);
        summary.put("averageScore", String.format("%.1f", avgScore));
        summary.put("highestScore", String.format("%.1f", highestScore));
        summary.put("items", list != null ? list : List.of());

        return R.success("获取成功", summary);
    }

    @Override
    @Transactional
    public R publishHomeworkToStudents(Map<String, String> params) {
        String homeworkId = params.get("homework_id");
        String type = params.get("type");
        String teacherUsername = params.get("teacher_username");
        String courseName = params.get("course_name");
        String title = params.get("title");
        String description = params.get("description");
        String questionsJson = params.get("questions_json");
        String deadline = params.get("deadline");
        String endTime = params.get("end_time");
        String duration = params.get("duration");

        if (homeworkId == null || type == null) {
            return R.error("缺少必要参数");
        }

        long now = System.currentTimeMillis();

        // 检查是否已发布
        LambdaQueryWrapper<HomeworkPublishedPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HomeworkPublishedPO::getHomework_id, homeworkId);
        HomeworkPublishedPO existing = homeworkPublishedMapper.selectOne(wrapper);

        HomeworkPublishedPO record = HomeworkPublishedPO.builder()
                .homework_id(homeworkId)
                .type(type)
                .teacher_username(teacherUsername != null ? teacherUsername : "")
                .course_name(courseName != null ? courseName : "")
                .title(title != null ? title : "")
                .description(description != null ? description : "")
                .questions_json(questionsJson != null ? questionsJson : "[]")
                .deadline(deadline != null ? deadline : "")
                .end_time(endTime != null ? endTime : "")
                .duration(duration != null ? Integer.parseInt(duration) : 60)
                .status("published")
                .updated_at(now)
                .build();

        if (existing != null) {
            record.setId(existing.getId());
            record.setCreated_at(existing.getCreated_at());
            homeworkPublishedMapper.update(record, wrapper);
            return R.success("已更新发布");
        } else {
            record.setCreated_at(now);
            homeworkPublishedMapper.insert(record);
            return R.success("发布成功");
        }
    }

    // ===== AI 自动批改 =====

    @Override
    @Transactional
    public R aiAutoGrade(Map<String, String> params) {
        String homeworkId = params.get("homework_id");
        String modelId = params.getOrDefault("model", "deepseek-v4-pro");

        if (homeworkId == null || homeworkId.isEmpty()) {
            return R.error("缺少作业ID");
        }

        // 1. 获取作业/考试题目
        LambdaQueryWrapper<HomeworkPublishedPO> hwWrapper = new LambdaQueryWrapper<>();
        hwWrapper.eq(HomeworkPublishedPO::getHomework_id, homeworkId);
        HomeworkPublishedPO published = homeworkPublishedMapper.selectOne(hwWrapper);
        if (published == null) {
            return R.error("未找到该作业/考试");
        }

        String questionsJson = published.getQuestions_json();
        if (questionsJson == null || questionsJson.isEmpty() || "[]".equals(questionsJson)) {
            return R.error("该作业没有题目数据");
        }

        // 2. 获取所有未批改的提交
        LambdaQueryWrapper<SubmissionRecordPO> subWrapper = new LambdaQueryWrapper<>();
        subWrapper.eq(SubmissionRecordPO::getHomework_id, homeworkId)
                  .ne(SubmissionRecordPO::getStatus, "graded");
        List<SubmissionRecordPO> ungraded = submissionRecordMapper.selectList(subWrapper);

        if (ungraded == null || ungraded.isEmpty()) {
            return R.success("没有需要批改的提交", Map.of("total", 0, "graded", 0));
        }

        // 3. 解析题目信息用于 prompt
        com.google.gson.Gson gson = new com.google.gson.Gson();
        com.google.gson.JsonArray questions;
        try {
            questions = gson.fromJson(questionsJson, com.google.gson.JsonArray.class);
        } catch (Exception e) {
            return R.error("题目数据解析失败: " + e.getMessage());
        }

        // 4. 构建每道题的参考信息
        StringBuilder questionInfo = new StringBuilder();
        for (int i = 0; i < questions.size(); i++) {
            com.google.gson.JsonObject q = questions.get(i).getAsJsonObject();
            String type = q.has("type") ? q.get("type").getAsString() : "";
            String title = q.has("title") ? q.get("title").getAsString() : "";
            String correct = q.has("correctAnswer") ? q.get("correctAnswer").getAsString() : "";
            int maxScore = q.has("score") ? q.get("score").getAsInt() : 10;
            questionInfo.append(String.format("第%d题 [%s] %s (满分%d分)\n", i + 1, type, title, maxScore));
            if (!correct.isEmpty()) {
                questionInfo.append("  参考答案: ").append(correct).append("\n");
            }
        }

        int gradedCount = 0;
        int failCount = 0;

        // 5. 逐个批改
        for (SubmissionRecordPO sub : ungraded) {
            try {
                String answersJson = sub.getAnswers();
                if (answersJson == null || answersJson.isEmpty()) {
                    answersJson = "[]";
                }

                // 构建批改 prompt
                StringBuilder prompt = new StringBuilder();
                prompt.append("你是一位老师，正在批改学生作业。请根据以下信息为每道题打分。\n\n");
                prompt.append("=== 题目信息 ===\n");
                prompt.append(questionInfo).append("\n");
                prompt.append("=== 学生: ").append(sub.getStudent_name()).append(" ===\n");
                prompt.append("学生答案 JSON: ").append(answersJson).append("\n\n");
                prompt.append("请以 JSON 数组格式返回每道题的评分，格式如下（不要包含其他内容）：\n");
                prompt.append("[{\"questionIndex\":0,\"score\":5,\"maxScore\":5,\"comment\":\"正确\"},...]\n");
                prompt.append("评分规则：客观题（选择题/判断题/填空题）参考答案完全匹配给满分，不匹配给0分；\n");
                prompt.append("主观题（应用题/计算题）根据答案完整性、正确性给予部分分数。");

                // 调用 AI
                Map<String, Object> chatBody = new HashMap<>();
                chatBody.put("model", modelId);
                chatBody.put("messages", List.of(
                    Map.of("role", "system", "content", "你是一位专业的作业批改老师，只返回 JSON，不包含任何其他文字。"),
                    Map.of("role", "user", "content", prompt.toString())
                ));

                String jsonBody = gson.toJson(chatBody);
                JsonObject output = langChainClient.chatRaw(jsonBody);

                if (output.has("error")) {
                    log.warn("AI 批改失败 submission={}: {}", sub.getId(), output.get("error").getAsString());
                    failCount++;
                    continue;
                }

                String content = output.has("content") ? output.get("content").getAsString() : "";
                // 提取 JSON 部分（AI 可能包裹在 ```json ... ``` 中）
                String jsonStr = content.trim();
                if (jsonStr.startsWith("```")) {
                    int start = jsonStr.indexOf("\n");
                    int end = jsonStr.lastIndexOf("```");
                    if (start >= 0 && end > start) {
                        jsonStr = jsonStr.substring(start + 1, end).trim();
                    }
                }

                // 解析评分
                com.google.gson.JsonArray scores;
                try {
                    scores = gson.fromJson(jsonStr, com.google.gson.JsonArray.class);
                } catch (Exception parseErr) {
                    log.warn("AI 返回格式解析失败 submission={}: {}", sub.getId(), jsonStr);
                    failCount++;
                    continue;
                }

                // 计算总分
                double totalScore = 0;
                double maxTotalScore = 0;
                for (int i = 0; i < scores.size(); i++) {
                    com.google.gson.JsonObject sc = scores.get(i).getAsJsonObject();
                    totalScore += sc.has("score") ? sc.get("score").getAsDouble() : 0;
                    maxTotalScore += sc.has("maxScore") ? sc.get("maxScore").getAsDouble() : 0;
                }

                // 保存批改结果
                sub.setScores(gson.toJson(scores));
                sub.setTotal_score(totalScore);
                sub.setMax_total_score(maxTotalScore);
                sub.setStatus("graded");
                sub.setGraded_at(System.currentTimeMillis());

                // 使用 update + wrapper 模式确保按主键精确更新
                LambdaQueryWrapper<SubmissionRecordPO> updateWrapper = new LambdaQueryWrapper<>();
                updateWrapper.eq(SubmissionRecordPO::getId, sub.getId());
                submissionRecordMapper.update(sub, updateWrapper);

                gradedCount++;
            } catch (Exception e) {
                log.error("AI 批改异常 submission={}: {}", sub.getId(), e.getMessage());
                failCount++;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", ungraded.size());
        result.put("graded", gradedCount);
        result.put("failed", failCount);
        return R.success("AI 批改完成", result);
    }

    @Override
    @Transactional
    public R retractGrade(Map<String, String> params) {
        String submissionId = params.get("submission_id");
        if (submissionId == null || submissionId.isEmpty()) {
            return R.error("缺少提交ID");
        }

        LambdaQueryWrapper<SubmissionRecordPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SubmissionRecordPO::getId, Integer.parseInt(submissionId));
        SubmissionRecordPO record = submissionRecordMapper.selectOne(wrapper);

        if (record == null) {
            return R.error("未找到该提交记录");
        }

        // 撤回批改：恢复到已提交状态，清空评分数据
        record.setStatus("submitted");
        record.setScores("[]");
        record.setTotal_score(0.0);
        record.setMax_total_score(0.0);
        record.setGraded_at(null);
        submissionRecordMapper.update(record, wrapper);

        return R.success("批改已撤回");
    }

    @Override
    @Transactional
    public R retractAllGrades(Map<String, String> params) {
        String homeworkId = params.get("homework_id");
        if (homeworkId == null || homeworkId.isEmpty()) {
            return R.error("缺少作业ID");
        }

        // 查找该作业所有已批改的提交
        LambdaQueryWrapper<SubmissionRecordPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SubmissionRecordPO::getHomework_id, homeworkId)
               .eq(SubmissionRecordPO::getStatus, "graded");
        List<SubmissionRecordPO> gradedList = submissionRecordMapper.selectList(wrapper);

        if (gradedList == null || gradedList.isEmpty()) {
            return R.success("没有需要撤回的批改", Map.of("count", 0));
        }

        int count = 0;
        for (SubmissionRecordPO record : gradedList) {
            record.setStatus("submitted");
            record.setScores("[]");
            record.setTotal_score(0.0);
            record.setMax_total_score(0.0);
            record.setGraded_at(null);
            LambdaQueryWrapper<SubmissionRecordPO> updateWrapper = new LambdaQueryWrapper<>();
            updateWrapper.eq(SubmissionRecordPO::getId, record.getId());
            submissionRecordMapper.update(record, updateWrapper);
            count++;
        }

        return R.success("已撤回 " + count + " 份批改", Map.of("count", count));
    }

    // ===== 已发布作业/考试查询 =====

    @Override
    public R getPublishedAssignments(Map<String, String> params) {
        String courseName = params.get("course_name");
        if (courseName == null || courseName.isEmpty()) {
            return R.error("缺少课程名称");
        }
        List<HomeworkPublishedPO> list = teacherMapper.selectPublishedAssignmentsByCourse(courseName);
        List<Map<String, Object>> result = new ArrayList<>();
        if (list != null) {
            for (HomeworkPublishedPO po : list) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", po.getHomework_id());
                item.put("homework_id", po.getHomework_id());
                item.put("type", po.getType());
                item.put("title", po.getTitle());
                item.put("description", po.getDescription() != null ? po.getDescription() : "");
                item.put("courseName", po.getCourse_name());
                item.put("deadline", po.getDeadline() != null ? po.getDeadline() : "");
                item.put("end_time", po.getEnd_time() != null ? po.getEnd_time() : "");
                item.put("duration", po.getDuration() != null ? po.getDuration() : 60);
                item.put("status", po.getStatus());
                // 包含题目数据，GradingPage 批改需要用到
                item.put("questions", parseQuestionsJson(po.getQuestions_json()));
                result.add(item);
            }
        }
        return R.success("获取成功", result);
    }

    /** 安全解析 questions_json 字符串为 List */
    private List<?> parseQuestionsJson(String json) {
        if (json == null || json.isEmpty() || "[]".equals(json)) return List.of();
        try {
            return new com.google.gson.Gson().fromJson(json, List.class);
        } catch (Exception e) {
            return List.of();
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
            // 直接返回 data 数组
            return R.success("success", new com.google.gson.Gson().fromJson(output.get("data"), Object.class));
        }
        return R.success("success", java.util.List.of());
    }

    // ===== 学生管理 =====

    @Override
    public R listEnrolledStudents(String teacherUsername, String courseName) {
        LambdaQueryWrapper<TeacherPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherPO::getUsername, teacherUsername);
        TeacherPO teacherPO = teacherMapper.selectOne(wrapper);
        if (teacherPO == null) {
            return R.error("教师不存在");
        }

        List<Map<String, Object>> rows;
        if (courseName != null && !courseName.isEmpty()) {
            rows = teacherMapper.selectEnrolledStudentsByTeacherIdAndCourse(teacherPO.getId(), courseName);
        } else {
            rows = teacherMapper.selectEnrolledStudentsByTeacherId(teacherPO.getId());
        }

        List<StudentDTO> students = rows.stream()
                .map(row -> {
                    Long id = toLong(row.get("id"));
                    return StudentDTO.builder()
                        .student_id(id != null ? id.intValue() : 0)
                        .student_account(toString(row.get("username")))
                        .student_name(toString(row.get("name")))
                        .student_gender(toString(row.get("gender")))
                        .student_age(toString(row.get("age")))
                        .student_major(toString(row.get("major")))
                        .student_class_name(toString(row.get("class_name")))
                        .course_name(toString(row.get("course_name")))
                        .create_time(toTimestamp(row.get("create_time")))
                        .update_time(toTimestamp(row.get("update_time")))
                        .build();
                }).toList();

        return R.success("获取成功", students);
    }

    private String toString(Object obj) {
        return obj != null ? obj.toString() : "";
    }

    private Long toLong(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number n) return n.longValue();
        try { return Long.parseLong(obj.toString()); } catch (NumberFormatException e) { return null; }
    }

    private Timestamp toTimestamp(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Timestamp ts) return ts;
        return null;
    }

    @Override
    @Transactional
    public R addStudentByTeacher(Map<String, Object> params) {
        String username = (String) params.get("student_username");
        String password = (String) params.get("password");
        String name = (String) params.get("student_name");
        String gender = (String) params.get("student_gender");
        String age = (String) params.get("student_age");
        String major = (String) params.get("student_major");
        String className = (String) params.get("student_class_name");
        String teacherUsername = (String) params.get("teacher_username");

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return R.error("学号和密码不能为空");
        }

        // 检查用户名唯一性
        Boolean exists = teacherMapper.checkUsernameExists(username);
        if (Boolean.TRUE.equals(exists)) {
            return R.error("该学号已被使用");
        }

        // 查询教师
        LambdaQueryWrapper<TeacherPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherPO::getUsername, teacherUsername);
        TeacherPO teacherPO = teacherMapper.selectOne(wrapper);
        if (teacherPO == null) {
            return R.error("教师不存在");
        }

        // 创建学生
        StudentPO studentPO = StudentPO.builder()
                .id(IdGenerator.generateUniqueId())
                .username(username)
                .password(passwordEncoder.encode(password))
                .name(name != null ? name : "")
                .gender(gender != null ? gender : "")
                .age(age != null ? age : "")
                .major(major != null ? major : teacherPO.getMajor())
                .class_name(className != null ? className : teacherPO.getClass_name())
                .teacher_binding_code(teacherPO.getTeacher_binding_code())
                .build();
        studentMapper.insert(studentPO);

        // 关联到教师的课程（如果提供了 course_names）
        @SuppressWarnings("unchecked")
        List<String> courseNames = (List<String>) params.get("course_names");
        if (courseNames != null && !courseNames.isEmpty()) {
            for (String courseName : courseNames) {
                LambdaQueryWrapper<CoursePO> courseWrapper = new LambdaQueryWrapper<>();
                courseWrapper.eq(CoursePO::getCourse_name, courseName)
                        .eq(CoursePO::getCourse_teacher_account, teacherUsername);
                CoursePO course = courseMapper.selectOne(courseWrapper);
                if (course != null) {
                    // 检查是否已存在选课记录
                    LambdaQueryWrapper<CourseDetailRecordPO> cdrWrapper = new LambdaQueryWrapper<>();
                    cdrWrapper.eq(CourseDetailRecordPO::getUser_category, "STUDENT")
                            .eq(CourseDetailRecordPO::getUser_id, studentPO.getId())
                            .eq(CourseDetailRecordPO::getCourse_id, course.getCourse_id());
                    if (courseDetailRecordMapper.selectCount(cdrWrapper) == 0) {
                        CourseDetailRecordPO cdr = CourseDetailRecordPO.builder()
                                .user_category("STUDENT")
                                .user_id(studentPO.getId())
                                .course_id(course.getCourse_id())
                                .build();
                        courseDetailRecordMapper.insert(cdr);
                        // 更新课程学生数量
                        course.setCourse_student_number(course.getCourse_student_number() + 1);
                        courseMapper.updateById(course);
                    }
                }
            }
        }

        StudentDTO result = StudentDTO.builder()
                .student_id(studentPO.getId().intValue())
                .student_account(studentPO.getUsername())
                .student_name(studentPO.getName())
                .student_gender(studentPO.getGender())
                .student_age(studentPO.getAge())
                .student_major(studentPO.getMajor())
                .student_class_name(studentPO.getClass_name())
                .create_time(studentPO.getCreate_time())
                .update_time(studentPO.getUpdate_time())
                .build();

        return R.success("添加成功", result);
    }

    @Override
    @Transactional
    public R updateStudentByTeacher(Map<String, Object> params) {
        String username = (String) params.get("student_username");
        if (username == null || username.isEmpty()) {
            return R.error("学号不能为空");
        }

        // 查找学生
        LambdaQueryWrapper<StudentPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentPO::getUsername, username);
        StudentPO studentPO = studentMapper.selectOne(wrapper);
        if (studentPO == null) {
            return R.error("学生不存在");
        }

        // 更新基本信息
        if (params.containsKey("student_name")) {
            studentPO.setName((String) params.get("student_name"));
        }
        if (params.containsKey("student_gender")) {
            studentPO.setGender((String) params.get("student_gender"));
        }
        if (params.containsKey("student_age")) {
            studentPO.setAge((String) params.get("student_age"));
        }
        if (params.containsKey("student_major")) {
            studentPO.setMajor((String) params.get("student_major"));
        }
        if (params.containsKey("student_class_name")) {
            studentPO.setClass_name((String) params.get("student_class_name"));
        }
        // 如果提供了新密码，更新密码
        if (params.containsKey("password") && params.get("password") != null
                && !((String) params.get("password")).isEmpty()) {
            studentPO.setPassword(passwordEncoder.encode((String) params.get("password")));
        }

        studentMapper.updateById(studentPO);

        StudentDTO result = StudentDTO.builder()
                .student_id(studentPO.getId().intValue())
                .student_account(studentPO.getUsername())
                .student_name(studentPO.getName())
                .student_gender(studentPO.getGender())
                .student_age(studentPO.getAge())
                .student_major(studentPO.getMajor())
                .student_class_name(studentPO.getClass_name())
                .create_time(studentPO.getCreate_time())
                .update_time(studentPO.getUpdate_time())
                .build();

        return R.success("更新成功", result);
    }

    @Override
    @Transactional
    public R deleteStudentFromTeacher(String studentUsername, String teacherUsername) {
        // 查找学生
        LambdaQueryWrapper<StudentPO> studentWrapper = new LambdaQueryWrapper<>();
        studentWrapper.eq(StudentPO::getUsername, studentUsername);
        StudentPO studentPO = studentMapper.selectOne(studentWrapper);
        if (studentPO == null) {
            return R.error("学生不存在");
        }

        // 查找教师
        LambdaQueryWrapper<TeacherPO> teacherWrapper = new LambdaQueryWrapper<>();
        teacherWrapper.eq(TeacherPO::getUsername, teacherUsername);
        TeacherPO teacherPO = teacherMapper.selectOne(teacherWrapper);
        if (teacherPO == null) {
            return R.error("教师不存在");
        }

        // 删除该学生在教师所有课程中的选课记录
        List<CoursePO> teacherCourses = teacherMapper.selectCoursesByTeacherId(teacherPO.getId());
        for (CoursePO course : teacherCourses) {
            LambdaQueryWrapper<CourseDetailRecordPO> cdrWrapper = new LambdaQueryWrapper<>();
            cdrWrapper.eq(CourseDetailRecordPO::getUser_category, "STUDENT")
                    .eq(CourseDetailRecordPO::getUser_id, studentPO.getId())
                    .eq(CourseDetailRecordPO::getCourse_id, course.getCourse_id());
            int deleted = courseDetailRecordMapper.delete(cdrWrapper);
            if (deleted > 0) {
                // 更新课程学生数量
                course.setCourse_student_number(Math.max(0, course.getCourse_student_number() - 1));
                courseMapper.updateById(course);
            }
        }

        // 清除学生与教师的 class_name 关联
        if (teacherPO.getClass_name() != null && teacherPO.getClass_name().equals(studentPO.getClass_name())) {
            studentPO.setClass_name("");
            studentPO.setTeacher_binding_code("");
            studentMapper.updateById(studentPO);
        }

        return R.success("删除成功");
    }

    @Override
    @Transactional
    public R batchDeleteStudents(Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<String> studentUsernames = (List<String>) params.get("student_usernames");
        String teacherUsername = (String) params.get("teacher_username");

        if (studentUsernames == null || studentUsernames.isEmpty()) {
            return R.error("请选择要删除的学生");
        }
        if (teacherUsername == null || teacherUsername.isEmpty()) {
            return R.error("教师信息缺失");
        }

        int successCount = 0;
        int failCount = 0;
        for (String studentUsername : studentUsernames) {
            R result = deleteStudentFromTeacher(studentUsername, teacherUsername);
            if (result.getCode() == 200) {
                successCount++;
            } else {
                failCount++;
            }
        }

        return R.success("批量删除完成：成功 " + successCount + " 人" + (failCount > 0 ? "，失败 " + failCount + " 人" : ""));
    }

}
