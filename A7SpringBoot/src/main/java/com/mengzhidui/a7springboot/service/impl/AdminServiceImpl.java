package com.mengzhidui.a7springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.mengzhidui.a7springboot.entity.dto.*;
import com.mengzhidui.a7springboot.entity.po.*;
import com.mengzhidui.a7springboot.entity.vo.AdminVO;
import com.mengzhidui.a7springboot.mapper.*;
import com.mengzhidui.a7springboot.response.R;
import com.mengzhidui.a7springboot.service.AdminService;
import com.mengzhidui.a7springboot.service.LangChainClientService;
import com.mengzhidui.a7springboot.utils.IdGenerator;
import com.mengzhidui.a7springboot.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String,Object> redisTemplate;
    private final MenuMapper menuMapper;
    private final AdminMapper adminMapper;
    private final TeacherMapper teacherMapper;
    private final StudentMapper studentMapper;
    private final CourseMapper courseMapper;
    private final AllTeacherClickRecordMapper allTeacherClickRecordMapper;
    private final AllStudentClickRecordMapper allStudentClickRecordMapper;
    private final AllCostTimeRecordMapper allCostTimeRecordMapper;
    private final IssueCorrectAVGRecordMapper issueCorrectAVGRecordMapper;
    private final KnowledgePointCommendDegreeRecordMapper knowledgePointCommendDegreeRecordMapper;
    private final PasswordEncoder passwordEncoder;
    private final CourseDetailRecordMapper courseDetailRecordMapper;
    private final QuestionDetailRecordMapper questionDetailRecordMapper;
    private final SelfPractiseQuestionDetailRecordMapper selfPractiseQuestionDetailRecordMapper;


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

    private final LangChainClientService langChainClient;

    @Value("${jwt.expiration}")
    private Long EXPIRATION_TIME;

    @Override
    public R login(AdminPO admin) {
        LambdaQueryWrapper<AdminPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminPO::getUsername, admin.getUsername());
        AdminPO findAdmin = adminMapper.selectOne(wrapper);
        if (findAdmin == null) {
            return R.error("登录失败");
        }
        try {
            // 验证用户名和密码
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            admin.getUsername(),
                            admin.getPassword()
                    )
            );

            // 设置认证上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);

            Map<String, Object> claims = Map.of("role","ADMIN");
            String token = jwtUtils.generateToken(admin.getUsername(),claims);

            // 存储到 Redis（键为 "jwt:token:{token}"，值为 username）
            String redisKey = "jwt:admin:token:" + token;
            redisTemplate.opsForValue()
                    .set(redisKey, authentication.getName(), EXPIRATION_TIME, TimeUnit.MILLISECONDS); // 设置相同的过期时间

            log.info( "管理员{}登录成功，Token: {}", admin.getUsername(),token);
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
    @Cacheable(value = "menus", key = "'admin_menu'", unless = "#result == null || #result.code != 200")
    public R getMenu() {
        LambdaQueryWrapper<MenuPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MenuPO::getAuth_role, "ADMIN");
        List<MenuPO> menus = menuMapper.selectList(wrapper);
        if (menus == null || menus.size() == 0){
            return R.error("获取管理端菜单失败");
        }else {
            return R.success("获取管理端菜单成功",menus);
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
    public R getSelfAdminDetail(String admin_username) {
        LambdaQueryWrapper<AdminPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminPO::getUsername, admin_username);
        AdminPO adminPO = adminMapper.selectOne(wrapper);
        if (adminPO == null){
            return R.error("管理员不存在");
        }
        List<AdminPO> adminPOs = adminMapper.selectList(null);
        List<AdminVO> AdminVOs = adminPOs.stream().map(admin -> AdminVO.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .name(admin.getName())
                .create_time(admin.getCreate_time())
                .update_time(admin.getUpdate_time())
                .isSuperAdmin(admin.getIsSuperAdmin())
                .build()).toList();
        if (adminPOs.size() == 0){
            return R.error("管理员集合获取失败");
        }
        List<TeacherPO> teacherPOs = teacherMapper.selectList(null);
        if (teacherPOs.size() == 0){
            return R.error("教师集合获取失败");
        }
        List<TeacherDTO> teacherList = teacherPOs.stream()
                .map(teacherPO -> TeacherDTO.builder()
                .teacher_id(teacherPO.getId().intValue())
                        .teacher_account(teacherPO.getUsername())
                        .teacher_name(teacherPO.getName())
                                .teacher_gender(teacherPO.getGender())
                                .teacher_age(teacherPO.getAge())
                                .teacher_major(teacherPO.getMajor())
                        .teacher_position(teacherPO.getPosition())
                        .teacher_class_name(teacherPO.getClass_name())
                                .teacher_class_student_number(teacherPO.getClass_student_number())
                                .teacher_binding_code(teacherPO.getTeacher_binding_code())
                                .teacher_courseList(teacherMapper.selectCoursesByTeacherId(teacherPO.getId()))
                                .build()
                ).toList();

        List<StudentPO> studentPOs = studentMapper.selectList(null);
        if (studentPOs.size() == 0){
            return R.error("学生集合获取失败");
        }
        List<StudentDTO> studentList = studentPOs.stream()
                .map(studentPO -> StudentDTO.builder()
                .student_id(studentPO.getId().intValue())
                        .student_account(studentPO.getUsername())
                        .student_name(studentPO.getName())
                        .student_gender(studentPO.getGender())
                        .student_age(studentPO.getAge())
                        .student_major(studentPO.getMajor())
                        .student_class_name(studentPO.getClass_name())
                                .teacher_binding_code(studentPO.getTeacher_binding_code())
                        .build()
                ).toList();

        AdminDTO adminDTO = AdminDTO.builder()
                .admin_id(adminPO.getId().intValue())
                .admin_account(adminPO.getUsername())
                .admin_name(adminPO.getName())
                .all_admin_list(AdminVOs)
                .all_teacher_list(teacherList)
                .all_student_list(studentList)
                .is_super_admin(adminPO.getIsSuperAdmin())
                .build();
        return R.success("获取管理员详情成功",adminDTO);
    }

    @Override
    public R getIssueList(Map<String, String> params) {
        String CourseName = params.get(Data);
        String TeacherName = params.get(TeacherUsername);

        LambdaQueryWrapper<QuestionDetailRecordPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionDetailRecordPO::getUser_username, TeacherName)
               .eq(QuestionDetailRecordPO::getCourse_name, CourseName)
               .eq(QuestionDetailRecordPO::getUser_category, "TEACHER");
        List<QuestionDetailRecordPO> records = questionDetailRecordMapper.selectList(wrapper);

        return R.success("获取成功", records);
    }

    @Override
    public R getResourceList(Map<String, String> params) {
        String CourseName = params.get(Data);
        String TeacherName = params.get(TeacherUsername);

        LambdaQueryWrapper<CoursePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CoursePO::getCourse_teacher_account, TeacherName);
        if (CourseName != null && !CourseName.isEmpty()) {
            wrapper.eq(CoursePO::getCourse_name, CourseName);
        }
        List<CoursePO> records = courseMapper.selectList(wrapper);

        return R.success("获取成功", records);
    }

    /**
     * 查询指定日期范围内的数据并汇总
     */
    @Override
    public AllTeacherClickRecordPO getByDateRange(LocalDate startDate, LocalDate endDate) {
        // 查询指定日期范围内的所有记录
        QueryWrapper<AllTeacherClickRecordPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("create_time", startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
        List<AllTeacherClickRecordPO> records = allTeacherClickRecordMapper.selectList(queryWrapper);

        // 汇总数据
        return summarizeRecords(records, startDate, endDate);
    }

    /**
     * 查询指定日期范围内的学生点击记录并汇总
     */
    @Override
    public AllStudentClickRecordPO getStudentRecordByDateRange(LocalDate startDate, LocalDate endDate) {
        // 查询指定日期范围内的所有记录
        QueryWrapper<AllStudentClickRecordPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("create_time", startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
        List<AllStudentClickRecordPO> records = allStudentClickRecordMapper.selectList(queryWrapper);

        // 汇总数据
        return summarizeStudentRecords(records, startDate, endDate);
    }

    @Override
    public AllCostTimeRecordDTO getCostTimeRecordByDateRange(String teacher_username, String course_name, LocalDate start, LocalDate end) {
        // 查询指定日期范围内的所有记录
        QueryWrapper<AllCostTimeRecordPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("create_time", start, end.plusDays(1));
        if (teacher_username != null && !teacher_username.isEmpty()) {
            queryWrapper.eq("teacher_username", teacher_username);
        }else {
            return null;
        }
        if (course_name != null && !course_name.isEmpty()) {
            queryWrapper.eq("course_name", course_name);
        }else {
            return null;
        }

        List<AllCostTimeRecordPO> records = allCostTimeRecordMapper.selectList(queryWrapper);
        double result1 = records.stream()
                .filter(record -> "自动备课耗时".equals(record.getType()))
                .collect(Collectors.averagingInt(record -> Integer.parseInt(record.getCost_second())));
        int knowledgeOrganization_AVG_cost_second = (int) result1;

        double result2 = records.stream()
                .filter(record -> "自动设计实训耗时".equals(record.getType()))
                .collect(Collectors.averagingInt(record -> Integer.parseInt(record.getCost_second())));
        int practiseDesign_AVG_cost_second = (int) result2;

        double result3 = records.stream()
                .filter(record -> "自动设计练习耗时".equals(record.getType()))
                .collect(Collectors.averagingInt(record -> Integer.parseInt(record.getCost_second())));
        int designIssue_AVG_cost_second = (int) result3;

        double result4 = records.stream()
                .filter(record -> "自动生成知识库耗时".equals(record.getType()))
                .collect(Collectors.averagingInt(record -> Integer.parseInt(record.getCost_second())));
        int addCourse_buildKnowledgeBase_AVG_cost_second = (int) result4;

        double result5 = records.stream()
                .filter(record -> "自动分析学生答题".equals(record.getType()))
                .collect(Collectors.averagingInt(record -> Integer.parseInt(record.getCost_second())));
        int getIssueAnswerDetail_AVG_cost_second = (int) result5;

        return AllCostTimeRecordDTO.builder()
                .teacher_username(teacher_username)
                .course_name(course_name)
                .knowledgeOrganization_AVG_cost_second(knowledgeOrganization_AVG_cost_second)
                .practiseDesign_AVG_cost_second(practiseDesign_AVG_cost_second)
                .designIssue_AVG_cost_second(designIssue_AVG_cost_second)
                .addCourse_buildKnowledgeBase_AVG_cost_second(addCourse_buildKnowledgeBase_AVG_cost_second)
                .getIssueAnswerDetail_AVG_cost_second(getIssueAnswerDetail_AVG_cost_second)
                .summaryStartDate(start)
                .summaryEndDate(end)
                .build();
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

    @Override
    @Transactional
    public R addStudent(StudentDTO studentDTO) {
        String bindingCode = studentDTO.getTeacher_binding_code();
        boolean hasBindingCode = bindingCode != null && !bindingCode.isEmpty();
        TeacherPO teacherPO = null;
        if (hasBindingCode) {
            LambdaQueryWrapper<TeacherPO> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TeacherPO::getTeacher_binding_code, bindingCode);
            teacherPO = teacherMapper.selectOne(wrapper);
        }
        if (hasBindingCode){
            if (teacherPO == null){
                return R.error("没有该教师");
            }
            if(studentMapper.checkUsernameExists(studentDTO.getStudent_account())){
                return R.error("用户名已存在");
            }
            StudentPO studentPO = StudentPO.builder()
                    .id(IdGenerator.generateUniqueId())
                    .username(studentDTO.getStudent_account())
                    .password(passwordEncoder.encode(studentDTO.getStudent_password()))
                    .name(studentDTO.getStudent_name())
                    .gender(studentDTO.getStudent_gender())
                    .age(studentDTO.getStudent_age())
                    .major(teacherPO.getMajor())
                    .class_name(teacherPO.getClass_name())
                    .teacher_binding_code(studentDTO.getTeacher_binding_code())
                    .build();
            int row = studentMapper.insert(studentPO);
            if (row == 0){
                return R.error("添加学生失败");
            }else {
                UpdateWrapper<TeacherPO> updateWrapper = new UpdateWrapper<>();
                updateWrapper
                        .eq("teacher_binding_code", studentPO.getTeacher_binding_code())
                        .setSql("class_student_number = CAST(class_student_number AS SIGNED) + 1");
                int row1 = teacherMapper.update(teacherPO, updateWrapper);
                if (row1 == 0){
                    log.error("更新教师班级人数出错");
                    return R.error("添加学生失败");
                }
                return R.success("添加学生成功");
            }
        }else {
            if(studentMapper.checkUsernameExists(studentDTO.getStudent_account())){
                return R.error("用户名已存在");
            }
            StudentPO studentPO = StudentPO.builder()
                    .id(IdGenerator.generateUniqueId())
                    .username(studentDTO.getStudent_account())
                    .password(passwordEncoder.encode(studentDTO.getStudent_password()))
                    .name(studentDTO.getStudent_name())
                    .gender(studentDTO.getStudent_gender())
                    .age(studentDTO.getStudent_age())
                    .teacher_binding_code(studentDTO.getTeacher_binding_code())
                    .build();
            int row = studentMapper.insert(studentPO);
            if (row > 0){
                return R.success("添加学生成功");
            }else {
                return R.error("添加学生失败");
            }
        }
    }

    @Override
    @Transactional
    public R deleteStudentByUsername(String username) {
        StudentPO studentPO = studentMapper.selectOne(new LambdaQueryWrapper<StudentPO>().eq(StudentPO::getUsername, username));
        if (studentPO == null){
            return R.error("没有该学生");
        }
        CourseDetailRecordPO courseDetailRecordPO = courseDetailRecordMapper.selectOne(new LambdaQueryWrapper<CourseDetailRecordPO>().eq(CourseDetailRecordPO::getUser_category, "STUDENT")
                .eq(CourseDetailRecordPO::getUser_id, studentPO.getId()));
        if (courseDetailRecordPO == null){
            studentMapper.delete(new LambdaQueryWrapper<StudentPO>().eq(StudentPO::getUsername, username));
            return R.success("删除学生成功");
        }
        UpdateWrapper<CoursePO> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                .eq("course_id", courseDetailRecordPO.getCourse_id())
                .setSql("course_student_number = course_student_number - 1");
        int updateRow1 = courseMapper.update(null, updateWrapper);
        if (updateRow1 > 0){
            int deleteRow = courseDetailRecordMapper.delete(new LambdaQueryWrapper<CourseDetailRecordPO>().eq(CourseDetailRecordPO::getUser_category, "STUDENT")
                    .eq(CourseDetailRecordPO::getUser_id, studentPO.getId()));
            if (deleteRow > 0){
                int deleteRow2 = studentMapper.delete(new LambdaQueryWrapper<StudentPO>().eq(StudentPO::getUsername, username));
                if (deleteRow2 == 0){
                    return R.error("删除学生失败");
                }
                return R.success("删除学生成功");
            }else {
                return R.error("删除学生失败");
            }
        }else{
            return R.error("删除学生失败");
        }
    }

    @Override
    @Transactional
    public R deleteStudentBySelected(List<String> usernames) {
        for (String username : usernames) {
            deleteStudentByUsername(username);
        }
        return R.success("批量删除学生成功");
    }

    @Override
    public R updateStudent(StudentDTO studentDTO) {
        String bindingCode = studentDTO.getTeacher_binding_code();
        boolean hasBindingCode = bindingCode != null && !bindingCode.isEmpty();
        if (!hasBindingCode){
            if (studentDTO.getStudent_password() == null){
                StudentPO studentPO = StudentPO.builder()
                        .username(studentDTO.getStudent_account())
                        .name(studentDTO.getStudent_name())
                        .gender(studentDTO.getStudent_gender())
                        .age(studentDTO.getStudent_age())
                        .major("")
                        .class_name("")
                        .teacher_binding_code("")
                        .build();
                studentMapper.update(studentPO, new LambdaQueryWrapper<StudentPO>().eq(StudentPO::getUsername, studentDTO.getStudent_account()));
                return R.success("修改学生信息成功");
            }else{
                StudentPO studentPO = StudentPO.builder()
                        .username(studentDTO.getStudent_account())
                        .password(passwordEncoder.encode(studentDTO.getStudent_password()))
                        .name(studentDTO.getStudent_name())
                        .gender(studentDTO.getStudent_gender())
                        .age(studentDTO.getStudent_age())
                        .major("")
                        .class_name("")
                        .teacher_binding_code("")
                        .build();
                studentMapper.update(studentPO, new LambdaQueryWrapper<StudentPO>().eq(StudentPO::getUsername, studentDTO.getStudent_account()));
                return R.success("修改学生信息成功");
            }
        }else {
            TeacherPO teacherPO = teacherMapper.selectOne(new LambdaQueryWrapper<TeacherPO>().eq(TeacherPO::getTeacher_binding_code, studentDTO.getTeacher_binding_code()));
            if (teacherPO == null){
                return R.error("没有该教师");
            }
            if (studentDTO.getStudent_password() == null){
                StudentPO studentPO = StudentPO.builder()
                        .username(studentDTO.getStudent_account())
                        .name(studentDTO.getStudent_name())
                        .gender(studentDTO.getStudent_gender())
                        .age(studentDTO.getStudent_age())
                        .major(teacherPO.getMajor())
                        .class_name(teacherPO.getClass_name())
                        .teacher_binding_code(studentDTO.getTeacher_binding_code())
                        .build();
                studentMapper.update(studentPO, new LambdaQueryWrapper<StudentPO>().eq(StudentPO::getUsername, studentDTO.getStudent_account()));
                return R.success("修改学生信息成功");
            }else{
                StudentPO studentPO = StudentPO.builder()
                        .username(studentDTO.getStudent_account())
                        .password(passwordEncoder.encode(studentDTO.getStudent_password()))
                        .name(studentDTO.getStudent_name())
                        .gender(studentDTO.getStudent_gender())
                        .age(studentDTO.getStudent_age())
                        .major(teacherPO.getMajor())
                        .class_name(teacherPO.getClass_name())
                        .teacher_binding_code(studentDTO.getTeacher_binding_code())
                        .build();
                studentMapper.update(studentPO, new LambdaQueryWrapper<StudentPO>().eq(StudentPO::getUsername, studentDTO.getStudent_account()));
                return R.success("修改学生信息成功");
            }

        }
    }

    @Override
    public R addAdmin(AdminVO adminVO) {
        if(adminMapper.checkUsernameExists(adminVO.getUsername())){
            return R.error("用户名已存在");
        }
        // 新管理员默认 isSuperAdmin = false，除非调用者明确设为 true
        Boolean isSuper = adminVO.getIsSuperAdmin() != null && adminVO.getIsSuperAdmin();
        AdminPO admin = AdminPO.builder()
                .id(IdGenerator.generateUniqueId())
                .username(adminVO.getUsername())
                .password(passwordEncoder.encode(adminVO.getPassword()))
                .name(adminVO.getName())
                .isSuperAdmin(isSuper)
                .build();
        int insert = adminMapper.insert(admin);
        if (insert > 0){
            return R.success("添加管理员成功");
        }else{
            return R.error("添加管理员失败");
        }
    }

    @Override
    public R deleteAdminByUsername(String username) {
        // 禁止删除自己
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName().equals(username)) {
            return R.error("不能删除自己的账号");
        }
        // 禁止删除最后一个超级管理员
        AdminPO target = adminMapper.selectOne(
            new LambdaQueryWrapper<AdminPO>().eq(AdminPO::getUsername, username));
        if (target != null && Boolean.TRUE.equals(target.getIsSuperAdmin())) {
            Long superAdminCount = adminMapper.selectCount(
                new LambdaQueryWrapper<AdminPO>().eq(AdminPO::getIsSuperAdmin, true));
            if (superAdminCount <= 1) {
                return R.error("不能删除最后一个超级管理员");
            }
        }
        int delete = adminMapper.delete(new LambdaQueryWrapper<AdminPO>().eq(AdminPO::getUsername, username));
        if (delete > 0){
            return R.success("删除管理员成功");
        }else{
            return R.error("删除管理员失败");
        }
    }

    @Override
    public R deleteAdminBySelected(List<String> usernames) {
        for (String username : usernames) {
            deleteAdminByUsername(username);
        }
        return R.success("批量删除管理员成功");
    }

    @Override
    public R updateAdmin(AdminVO adminVO) {
        AdminPO.AdminPOBuilder builder = AdminPO.builder()
                .username(adminVO.getUsername())
                .name(adminVO.getName());
        if (adminVO.getPassword() != null) {
            builder.password(passwordEncoder.encode(adminVO.getPassword()));
        }
        // 支持更新超级管理员标识
        if (adminVO.getIsSuperAdmin() != null) {
            // 如果要取消某人的超级管理员身份，检查是否是最后一个
            if (!adminVO.getIsSuperAdmin()) {
                AdminPO target = adminMapper.selectOne(
                    new LambdaQueryWrapper<AdminPO>().eq(AdminPO::getUsername, adminVO.getUsername()));
                if (target != null && Boolean.TRUE.equals(target.getIsSuperAdmin())) {
                    Long superAdminCount = adminMapper.selectCount(
                        new LambdaQueryWrapper<AdminPO>().eq(AdminPO::getIsSuperAdmin, true));
                    if (superAdminCount <= 1) {
                        return R.error("不能取消最后一个超级管理员的身份");
                    }
                }
            }
            builder.isSuperAdmin(adminVO.getIsSuperAdmin());
        }
        AdminPO admin = builder.build();
        int update = adminMapper.update(admin, new LambdaQueryWrapper<AdminPO>().eq(AdminPO::getUsername, adminVO.getUsername()));
        if (update > 0){
            return R.success("修改管理员信息成功");
        }else{
            return R.error("修改管理员信息失败");
        }
    }

    @Override
    public R addTeacher(TeacherDTO teacherDTO) {
        if(teacherMapper.checkUsernameExists(teacherDTO.getTeacher_account())){
            return R.error("用户名已存在");
        }
        if ("班主任".equals(teacherDTO.getTeacher_position())){
            TeacherPO teacherPO = TeacherPO.builder()
                    .id(IdGenerator.generateUniqueId())
                    .username(teacherDTO.getTeacher_account())
                    .password(passwordEncoder.encode(teacherDTO.getTeacher_password()))
                    .name(teacherDTO.getTeacher_name())
                    .gender(teacherDTO.getTeacher_gender())
                    .age(teacherDTO.getTeacher_age())
                    .major(teacherDTO.getTeacher_major())
                    .position(teacherDTO.getTeacher_position())
                    .class_name(teacherDTO.getTeacher_class_name())
                    .class_student_number("0")
                    .teacher_binding_code(UUID.randomUUID().toString())
                    .build();
            int insert = teacherMapper.insert(teacherPO);
            if (insert > 0){
                return R.success("添加教师成功");
            }else{
                return R.error("添加教师失败");
            }
        }else{
            TeacherPO teacherPO = TeacherPO.builder()
                    .id(IdGenerator.generateUniqueId())
                    .username(teacherDTO.getTeacher_account())
                    .password(passwordEncoder.encode(teacherDTO.getTeacher_password()))
                    .name(teacherDTO.getTeacher_name())
                    .gender(teacherDTO.getTeacher_gender())
                    .age(teacherDTO.getTeacher_age())
                    .position(teacherDTO.getTeacher_position())
                    .build();
            int insert = teacherMapper.insert(teacherPO);
            if (insert > 0){
                return R.success("添加教师成功");
            }else{
                return R.error("添加教师失败");
            }
        }

    }

    @Override
    @Transactional
    public R deleteTeacherByUsername(String username) {
        if (courseDetailRecordMapper.selectList(new LambdaQueryWrapper<CourseDetailRecordPO>().eq(CourseDetailRecordPO::getUser_category, "TEACHER")
                .eq(CourseDetailRecordPO::getUser_id, teacherMapper.selectOne(new LambdaQueryWrapper<TeacherPO>().eq(TeacherPO::getUsername, username)).getId())).size() != 0){
            teacherMapper.delete(new LambdaQueryWrapper<TeacherPO>().eq(TeacherPO::getUsername, username));
            return R.success("删除教师成功");
        }
        List<CourseDetailRecordPO> CDRPOs = courseDetailRecordMapper.selectList(new LambdaQueryWrapper<CourseDetailRecordPO>().eq(CourseDetailRecordPO::getUser_category, "TEACHER")
                .eq(CourseDetailRecordPO::getUser_id, teacherMapper.selectOne(new LambdaQueryWrapper<TeacherPO>().eq(TeacherPO::getUsername, username)).getId()));

        //从练习开始删除
        questionDetailRecordMapper.delete(new LambdaQueryWrapper<QuestionDetailRecordPO>().eq(QuestionDetailRecordPO::getUser_username, username).eq(QuestionDetailRecordPO::getUser_category, "TEACHER"));

        courseMapper.delete(new LambdaQueryWrapper<CoursePO>().eq(CoursePO::getCourse_teacher_account, username));
        for (CourseDetailRecordPO cdrpo : CDRPOs) {
            courseDetailRecordMapper.delete(new LambdaQueryWrapper<CourseDetailRecordPO>().eq(CourseDetailRecordPO::getRecord_id, cdrpo.getRecord_id()));
        }

        selfPractiseQuestionDetailRecordMapper.delete(new LambdaQueryWrapper<SelfPractiseQuestionDetailRecordPO>().eq(SelfPractiseQuestionDetailRecordPO::getTeacher_username, username));

        allCostTimeRecordMapper.delete(new LambdaQueryWrapper<AllCostTimeRecordPO>().eq(AllCostTimeRecordPO::getTeacher_username, username));

        issueCorrectAVGRecordMapper.delete(new LambdaQueryWrapper<IssueCorrectAVGRecordPO>().eq(IssueCorrectAVGRecordPO::getTeacher_username, username));

        knowledgePointCommendDegreeRecordMapper.delete(new LambdaQueryWrapper<KnowledgePointCommendDegreeRecordPO>().eq(KnowledgePointCommendDegreeRecordPO::getTeacher_username, username));

        teacherMapper.delete(new LambdaQueryWrapper<TeacherPO>().eq(TeacherPO::getUsername, username));

        return R.success("删除教师成功");
    }

    @Override
    @Transactional
    public R deleteTeacherBySelected(List<String> usernames) {
        for (String username : usernames) {
            deleteTeacherByUsername(username);
        }
        return R.success("批量删除教师成功");
    }

    @Override
    @Transactional
    public R updateTeacher(TeacherDTO teacherDTO) {
        // 1. 查询数据库中的原始教师记录
        TeacherPO originalTeacher = teacherMapper.selectOne(new LambdaQueryWrapper<TeacherPO>().eq(TeacherPO::getUsername, teacherDTO.getTeacher_account()));
        if (originalTeacher == null) {
            return R.error("教师不存在");
        }

        // 2. 查询该教师是否有关联的课程记录
        List<CourseDetailRecordPO> courseRecords = courseDetailRecordMapper.selectList(
                new LambdaQueryWrapper<CourseDetailRecordPO>()
                        .eq(CourseDetailRecordPO::getUser_category, "TEACHER")
                        .eq(CourseDetailRecordPO::getUser_id, originalTeacher.getId())
        );
        boolean hasCourseRecords = !courseRecords.isEmpty();

        // 4. 如果有关联课程记录：执行完整更新逻辑
        // 4.1 保存原始账号，用于后续关联表更新
        String oldUsername = originalTeacher.getUsername();
        String newUsername = teacherDTO.getTeacher_account();

        // 4.2 检查专业或班级名称是否变化，若变化则重置学生数量和绑定码
        boolean majorChanged = !Objects.equals(originalTeacher.getMajor(), teacherDTO.getTeacher_major());
        boolean classNameChanged = !Objects.equals(originalTeacher.getClass_name(), teacherDTO.getTeacher_class_name());
        if (majorChanged || classNameChanged) {
            List<StudentPO> needToChangge = studentMapper.selectList(new LambdaQueryWrapper<StudentPO>().eq(StudentPO::getTeacher_binding_code, originalTeacher.getTeacher_binding_code()));

            originalTeacher.setClass_student_number("0");
            String bindingCode = UUID.randomUUID().toString();
            originalTeacher.setTeacher_binding_code(bindingCode);
            originalTeacher.setClass_name(teacherDTO.getTeacher_class_name());
            originalTeacher.setMajor(teacherDTO.getTeacher_major());

            for (StudentPO studentPO : needToChangge) {
                studentPO.setMajor(teacherDTO.getTeacher_major());
                studentPO.setClass_name(teacherDTO.getTeacher_class_name());
                studentPO.setTeacher_binding_code(bindingCode);
                studentMapper.updateById(studentPO);
            }
        }

        // 3. 如果无关联课程记录：仅更新teacherPO
        if (!hasCourseRecords) {
            mapDtoToPo(teacherDTO, originalTeacher);
            teacherMapper.updateById(originalTeacher);
            return R.success("教师信息更新成功");
        }

        // 4.3 手动映射DTO属性到PO
        mapDtoToPo(teacherDTO, originalTeacher);

        // 4.4 更新教师主表
        teacherMapper.updateById(originalTeacher);

        // 4.5 如果账号变化，同步更新所有关联表
        if (!Objects.equals(oldUsername, newUsername)) {
            updateRelatedTables(oldUsername, newUsername);
        }

        return R.success("教师信息更新成功");
    }

    @Override
    @Transactional
    public R deleteCourse(Map<String, String> params) {
        String teacherUsername = params.get(TeacherUsername);
        String CourseName = params.get(Data);

        List<CoursePO> coursePOS = courseMapper.selectList(new LambdaQueryWrapper<CoursePO>().eq(CoursePO::getCourse_name, CourseName).eq(CoursePO::getCourse_teacher_account, teacherUsername));
        if (coursePOS.size() == 0){
            return R.error("课程不存在");
        }

        selfPractiseQuestionDetailRecordMapper.delete(new LambdaQueryWrapper<SelfPractiseQuestionDetailRecordPO>().eq(SelfPractiseQuestionDetailRecordPO::getCourse_name, CourseName).eq(SelfPractiseQuestionDetailRecordPO::getTeacher_username, teacherUsername));

        questionDetailRecordMapper.delete(new LambdaQueryWrapper<QuestionDetailRecordPO>().eq(QuestionDetailRecordPO::getCourse_name, CourseName).eq(QuestionDetailRecordPO::getUser_username, teacherUsername).eq(QuestionDetailRecordPO::getUser_category, "TEACHER"));

        knowledgePointCommendDegreeRecordMapper.delete(new LambdaQueryWrapper<KnowledgePointCommendDegreeRecordPO>().eq(KnowledgePointCommendDegreeRecordPO::getCourse_name, CourseName).eq(KnowledgePointCommendDegreeRecordPO::getTeacher_username, teacherUsername));

        issueCorrectAVGRecordMapper.delete(new LambdaQueryWrapper<IssueCorrectAVGRecordPO>().eq(IssueCorrectAVGRecordPO::getCourse_name, CourseName).eq(IssueCorrectAVGRecordPO::getTeacher_username, teacherUsername));

        for (CoursePO coursePO : coursePOS) {
            courseDetailRecordMapper.delete(new LambdaQueryWrapper<CourseDetailRecordPO>().eq(CourseDetailRecordPO::getCourse_id, coursePO.getCourse_id()));
            courseMapper.deleteById(coursePO);
        }

        return R.success("课程删除成功");
    }

    @Override
    public R deleteIssue(Map<String, String> params) {
        String questionId = params.get(QuestionId);

        questionDetailRecordMapper.delete(new LambdaQueryWrapper<QuestionDetailRecordPO>().eq(QuestionDetailRecordPO::getQuestion_id, questionId));
        selfPractiseQuestionDetailRecordMapper.delete(new LambdaQueryWrapper<SelfPractiseQuestionDetailRecordPO>().eq(SelfPractiseQuestionDetailRecordPO::getQuestion_id, questionId));
        issueCorrectAVGRecordMapper.delete(new LambdaQueryWrapper<IssueCorrectAVGRecordPO>().eq(IssueCorrectAVGRecordPO::getQuestion_id, questionId));

        return R.success("练习删除成功");
    }

    @Override
    public R deleteResource(Map<String, String> params) {
        String resourceId = params.get(ResourceId);

        return R.success("练习删除成功");
    }

    /**
     * 手动映射 TeacherDTO 到 TeacherPO
     */
    private void mapDtoToPo(TeacherDTO dto, TeacherPO po) {
        po.setUsername(dto.getTeacher_account());
        if (dto.getTeacher_password() != null){
            po.setPassword(passwordEncoder.encode(dto.getTeacher_password()));
        }
        po.setName(dto.getTeacher_name());
        po.setGender(dto.getTeacher_gender());
        po.setAge(dto.getTeacher_age());
        po.setPosition(dto.getTeacher_position());
        if ("讲师".equals(dto.getTeacher_position())){
            po.setMajor("");
            po.setClass_name("");
            po.setClass_student_number("");
            po.setTeacher_binding_code("");
        }
    }

    /**
     * 同步更新关联表中的教师账号
     */
    private void updateRelatedTables(String oldUsername, String newUsername) {
        // 更新练习记录中的教师账号
        questionDetailRecordMapper.update(
                null,
                new LambdaUpdateWrapper<QuestionDetailRecordPO>()
                        .eq(QuestionDetailRecordPO::getUser_username, oldUsername)
                        .eq(QuestionDetailRecordPO::getUser_category, "TEACHER")
                        .set(QuestionDetailRecordPO::getUser_username, newUsername)
        );

        // 更新课程记录中的教师账号
        courseMapper.update(
                null,
                new LambdaUpdateWrapper<CoursePO>()
                        .eq(CoursePO::getCourse_teacher_account, oldUsername)
                        .set(CoursePO::getCourse_teacher_account, newUsername)
        );

        // 更新自主练习记录中的教师账号
        selfPractiseQuestionDetailRecordMapper.update(
                null,
                new LambdaUpdateWrapper<SelfPractiseQuestionDetailRecordPO>()
                        .eq(SelfPractiseQuestionDetailRecordPO::getTeacher_username, oldUsername)
                        .set(SelfPractiseQuestionDetailRecordPO::getTeacher_username, newUsername)
        );

        // 更新耗时记录中的教师账号
        allCostTimeRecordMapper.update(
                null,
                new LambdaUpdateWrapper<AllCostTimeRecordPO>()
                        .eq(AllCostTimeRecordPO::getTeacher_username, oldUsername)
                        .set(AllCostTimeRecordPO::getTeacher_username, newUsername)
        );

        // 更新正确率记录中的教师账号
        issueCorrectAVGRecordMapper.update(
                null,
                new LambdaUpdateWrapper<IssueCorrectAVGRecordPO>()
                        .eq(IssueCorrectAVGRecordPO::getTeacher_username, oldUsername)
                        .set(IssueCorrectAVGRecordPO::getTeacher_username, newUsername)
        );

        knowledgePointCommendDegreeRecordMapper.update(null,
                new LambdaUpdateWrapper<KnowledgePointCommendDegreeRecordPO>()
                        .eq(KnowledgePointCommendDegreeRecordPO::getTeacher_username, oldUsername)
                        .set(KnowledgePointCommendDegreeRecordPO::getTeacher_username, newUsername)
        );
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

    /**
     * 汇总记录数据
     */
    private AllTeacherClickRecordPO summarizeRecords(List<AllTeacherClickRecordPO> records,
                                                     LocalDate startDate,
                                                     LocalDate endDate) {
        if (records.isEmpty()) {
            return null;
        }

        // 创建汇总记录
        return AllTeacherClickRecordPO.builder()
                // 汇总各字段（直接对整数类型求和）
                .knowledge_organization(records.stream()
                        .mapToInt(AllTeacherClickRecordPO::getKnowledge_organization)
                        .sum())
                .practise_design(records.stream()
                        .mapToInt(AllTeacherClickRecordPO::getPractise_design)
                        .sum())
                .design_issue(records.stream()
                        .mapToInt(AllTeacherClickRecordPO::getDesign_issue)
                        .sum())
                .issue_answer_analysis(records.stream()
                        .mapToInt(AllTeacherClickRecordPO::getIssue_answer_analysis)
                        .sum())
                .build_knowledge_base(records.stream()
                        .mapToInt(AllTeacherClickRecordPO::getBuild_knowledge_base)
                        .sum())
                // 设置汇总日期范围
                .summaryStartDate(startDate)
                .summaryEndDate(endDate)
                .build();
    }

    /**
     * 汇总学生记录数据
     */
    private AllStudentClickRecordPO summarizeStudentRecords(List<AllStudentClickRecordPO> records,
                                                            LocalDate startDate,
                                                            LocalDate endDate) {
        if (records.isEmpty()) {
            return null;
        }

        return AllStudentClickRecordPO.builder()
                .answer(records.stream().mapToInt(AllStudentClickRecordPO::getAnswer).sum())
                .self_practise(records.stream().mapToInt(AllStudentClickRecordPO::getSelf_practise).sum())
                .course_practise(records.stream().mapToInt(AllStudentClickRecordPO::getCourse_practise).sum())
                .summaryStartDate(startDate)
                .summaryEndDate(endDate)
                .build();
    }

}
