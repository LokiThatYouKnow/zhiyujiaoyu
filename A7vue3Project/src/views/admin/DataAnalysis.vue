<template>
    <div class="app-container" style="overflow-y: auto;height: 100%;" ref="scrollContainer">

        <div class="date-selector" id="firstTarget"
            style="display: flex;flex-direction: row;justify-content: space-between;">
   
                <a-button type="primary" @click="backToHome"
                    style="background-color: goldenrod; font-weight: bolder;width: 70px;">返回首页</a-button>
                <strong>当前日期：{{ stringStartDate }} 到 {{ stringEndDate }}</strong>
                <label for="startDate">开始日期：</label>
                <input type="date" id="startDate" class="form-input time-input" v-model="stringStartDate" required>
                <div id="startDateError" class="error-message"></div>
                <label for="endDate">结束日期：</label>
                <input type="date" id="endDate" class="form-input time-input" v-model="stringEndDate" required>
                <div id="endDateError" class="error-message"></div>
                <a-button type="primary" @click="handleDateQuery" style="margin-left: 16px;width: 7%;">
                    查询
                </a-button>
                <a-button @click="resetDate" style="margin-left: 8px;width: 7%;">
                    重置
                </a-button>
       
        </div>

        <!-- 教师活跃板块分布 -->
        <div class="card">
            <h3 class="card-title">全体教师活跃板块分布</h3>
            <div class="chart-wrapper">
                <div ref="teacherActiveChartRef" class="chart-container"></div>
            </div>
        </div>

        <!-- 学生活跃板块分布 -->
        <div class="card">
            <h3 class="card-title">全体学生活跃板块分布</h3>
            <div class="chart-wrapper">
                <div ref="studentActiveChartRef" class="chart-container"></div>
            </div>
        </div>

        <!-- 教师和课程选择器 -->
        <div class="card">
            <h2 style="margin-left: 40%;">教师课程效率和效果的详情分析</h2>
            <div class="selector-container">
                <select v-model="selectedTeacher">
                    <option value="">请选择教师</option>
                    <option v-for="teacher in adminVODetail.all_teacher_list" :value="teacher.teacher_account">{{
                        teacher.teacher_name }}</option>
                </select>

                <select v-model="selectedCourse">
                    <option value="">请选择课程</option>
                    <option v-for="course in coursesList" :value="course.course_name">{{ course.course_name }}</option>
                </select>
                <button @click="showCharts('secondTarget')">查看数据</button>
            </div>
        </div>

        <!-- 教师和课程标题 -->
        <div v-if="showDetailCharts" class="card" style="display: flex;flex-direction: vertical;" id="secondTarget">
            <strong>当前日期：{{ stringStartDate }} 到 {{ stringEndDate }}</strong>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <h2 class="teacher-course-title">{{ `${selectedTeacherName} - ${selectedCourse}` }}</h2>
        </div>

        <!-- 教学耗时分析 -->
        <div v-if="showDetailCharts" class="card">
            <h3 class="card-title">课程教学平均耗时分析</h3>
            <div class="chart-wrapper">
                <div ref="teachingTimeChartRef" class="chart-container"></div>
            </div>
        </div>

        <!-- 学生平均正确率趋势 -->
        <div v-if="showDetailCharts" class="card">
            <h3 class="card-title">学生课程练习平均正确率趋势</h3>
            <h5>如果数据为-1则代表那个日期还暂无数据</h5>
            <div class="chart-wrapper">
                <div ref="correctRateChartRef" class="chart-container"></div>
            </div>
        </div>

        <!-- 知识点掌握情况 -->
        <div v-if="showDetailCharts" class="card">
            <h3 class="card-title">知识点掌握情况</h3>
            <div class="chart-wrapper">
                <div ref="knowledgePointChartRef" class="chart-container"></div>
            </div>
            <h4 style="margin-left: 45%;margin-top: -2%;">{{ stringStartDate }} 到 {{ stringEndDate }}</h4>
        </div>

        <!-- 表格示例 -->
        <div v-if="showDetailCharts" class="card">
            <h3 class="card-title">高频错误知识点</h3>
            <h4>{{ stringStartDate }} 到 {{ stringEndDate }}</h4>
            <table class="table">
                <thead>
                    <tr>
                        <th>排名</th>
                        <th>知识点</th>
                        <th>学科</th>
                        <th>错误次数</th>
                        <th>错误率</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="(item, index) in errorTop5" :key="index">
                        <td><span class="inline-flex items-center justify-center w-6 h-6">{{
                                index + 1 }}</span></td>
                        <td class="font-medium">{{ item.knowledge_point }}</td>
                        <td class="text-gray-600">{{ item.course_name }}</td>
                        <td>{{ item.error_time }}</td>
                        <td><span>{{ item.error_rate }}%</span></td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue';
import * as echarts from 'echarts';
import type { R } from '../../response/R';
import { getAllCostTimeRecordApi, getAllStudentClickRecordApi, getAllTeacherClickRecordApi, getIssueCorrectAVGRecordApi, getKnowledgePointCommendDegreeRecordApi } from '../../api/admin/DataAnalysisApi';
import { message } from 'ant-design-vue';
import { getSelfAdminDetailApi } from '../../api/admin/getSelfAdminDetailApi';
import type { teacher } from '../../modules/teacher';
import type { student } from '../../modules/student';
import type { admin } from '../../modules/admin';
import type { adminVO } from '../../modules/adminVO';
import { jwtDecode } from 'jwt-decode';
import type { course } from '../../modules/course';
import router from '../../routers/appRouters';

const token = ref((sessionStorage.getItem('token') || localStorage.getItem('token')))
const admin_usernmae = jwtDecode(token.value as string).sub as string
const adminVODetail = ref<adminVO>({
    admin_id: 0,
    admin_account: '',
    admin_password: '',
    admin_name: '',
    all_admin_list: [] as admin[],
    all_teacher_list: [] as teacher[],
    all_student_list: [] as student[],
})

const coursesList = ref<course[]>([])
const selectedTeacher = ref('');
const selectedCourse = ref('');
const selectedTeacherName = ref('');


const getSelfAdminDetail = async () => {
    await getSelfAdminDetailApi(admin_usernmae).then(res => {
        const R: R = res.data
        if (R.code === 200) {
            adminVODetail.value = R.data
            localStorage.setItem('adminDetail', JSON.stringify(adminVODetail.value))
        } else {
            message.error('获取管理员信息失败')
        }
    }).catch(err => {
        message.error('获取管理员信息失败')
        console.log(err)
    })
}

const setSelfAdminDetail = () => {
    adminVODetail.value = JSON.parse(localStorage.getItem('adminDetail') as string)
}


if (localStorage.getItem('adminDetail')) {
    setSelfAdminDetail()
} else {
    getSelfAdminDetail()
}

// 监听教师选择变化
watch(selectedTeacher, () => {
    coursesList.value = []
    adminVODetail.value?.all_teacher_list?.forEach(teacher => {
        if (teacher.teacher_account === selectedTeacher.value) {
            selectedTeacherName.value = teacher.teacher_name as string
            teacher.teacher_courseList?.forEach(course => {
                coursesList.value.push(course)
            })
        }
    })
    selectedCourse.value = '';
});

const formatDate = (date: Date) => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
};

const startDate = ref<Date>(new Date());
const endDate = ref<Date>(new Date());
const stringStartDate = ref<string>(startDate ? formatDate(startDate.value) : '');
const stringEndDate = ref<string>(endDate ? formatDate(endDate.value) : '');




// 状态管理

const showDetailCharts = ref(false);

// 图表引用
const teacherActiveChartRef = ref<HTMLDivElement | null>(null);
const studentActiveChartRef = ref<HTMLDivElement | null>(null);
const teachingTimeChartRef = ref<HTMLDivElement | null>(null);
const correctRateChartRef = ref<HTMLDivElement | null>(null);
const knowledgePointChartRef = ref<HTMLDivElement | null>(null);

// 图表实例
let teacherActiveChart: echarts.ECharts | null = null;
let studentActiveChart: echarts.ECharts | null = null;
let teachingTimeChart: echarts.ECharts | null = null;
let correctRateChart: echarts.ECharts | null = null;
let knowledgePointChart: echarts.ECharts | null = null;

interface allTeacherClickRecord{
    knowledge_organization: number,
    practise_design: number,
    design_issue: number,
    issue_answer_analysis: number,
    build_knowledge_base: number,
    create_time:string
    summaryStartDate:string
    summaryEndDate:string
}

const teacherClick = ref<allTeacherClickRecord>()

interface allStudentClickRecord{
    answer: number,
    self_practise: number,
    course_practise: number,
    create_time:string
    summaryStartDate: string
    summaryEndDate: string
}

const studentClick = ref<allStudentClickRecord>()

interface AllCostTimeRecord{
    record_id:number,
    teacher_username:string,
    course_name:string,
    knowledgeOrganization_AVG_cost_second:string,
    practiseDesign_AVG_cost_second:string,
    designIssue_AVG_cost_second:string,
    addCourse_buildKnowledgeBase_AVG_cost_second:string,
    getIssueAnswerDetail_AVG_cost_second:number,
    create_time:string,
    summaryStartDate:string,
    summaryEndDate:string
}

const allCostTime = ref<AllCostTimeRecord>()

/* 图表内第四图的方法，由于第五图也要用到，且第五图不在初始化图表里，所以把方法写在外边 */
interface knowledge_point_commend_degree_record{
    teacher_username:string,
    course_name:number,
    knowledge_point:string,
    commend_degree:string,
    error_time:string
    error_rate:string
    create_time:string
}
const R5 = ref<R>()
const knowledgePointCommendDegreeList = ref<knowledge_point_commend_degree_record[]>()

/* 第五图数据处理处 */
const errorTop5 = ref<knowledge_point_commend_degree_record[]>()

// 初始化图表，修改为接收日期范围
const initCharts = async (teacher_username:string,course_name:string,start: string, end: string) => {
    const R1: R = (await getAllTeacherClickRecordApi({ startDate: start, endDate: end })).data;
    teacherClick.value = R1.data;

    // 1. 教师活跃板块分布（饼图）
    if (teacherActiveChartRef.value) {
        // 先销毁旧实例，避免重复渲染
        if (teacherActiveChart) teacherActiveChart.dispose();
        teacherActiveChart = echarts.init(teacherActiveChartRef.value);
        teacherActiveChart.setOption({
            tooltip: { trigger: 'item' },
            legend: { orient: 'vertical', right: 10 },
            series: [{
                name: '使用次数',
                type: 'pie',
                radius: ['40%', '70%'],
                data: [
                    { value: teacherClick.value?.knowledge_organization, name: '备课与设计' },
                    { value: teacherClick.value?.practise_design, name: '实训设计' },
                    { value: teacherClick.value?.design_issue, name: '智能出题' },
                    { value: teacherClick.value?.issue_answer_analysis, name: '自动化分析学生答题' },
                    { value: teacherClick.value?.build_knowledge_base, name: '自动化构建知识库' }
                ],
                color: ['#165DFF', '#00C1D4', '#00B42A', '#FF7D00', '#F53F3F']
            }]
        });
    }

    const R2: R = (await getAllStudentClickRecordApi({ startDate: start, endDate: end })).data;
    studentClick.value = R2.data;

    // 2. 学生活跃板块分布（饼图）
    if (studentActiveChartRef.value) {
        if (studentActiveChart) studentActiveChart.dispose();
        studentActiveChart = echarts.init(studentActiveChartRef.value);
        studentActiveChart.setOption({
            tooltip: { trigger: 'item' },
            legend: { orient: 'vertical', right: 10 },
            series: [{
                name: '使用次数',
                type: 'pie',
                radius: ['40%', '70%'],
                data: [
                    { value: studentClick.value?.answer, name: '在线疑惑答解' },
                    { value: studentClick.value?.course_practise, name: '课程练习并自动批改和解析' },
                    { value: studentClick.value?.self_practise, name: '针对性训练并自动批改和解析' },
                ],
                color: ['#165DFF', '#00C1D4', '#00B42A']
            }]
        });0
    }
    const R3: R = (await getAllCostTimeRecordApi({teacher_username:teacher_username,course_name:course_name, startDate: start, endDate: end })).data
    allCostTime.value = R3.data

    // 3. 教学耗时分析（柱状图）
    if (teachingTimeChartRef.value) {
        teachingTimeChart = echarts.init(teachingTimeChartRef.value);
        teachingTimeChart.setOption({
            tooltip: { trigger: 'axis' },
            legend: { data: ['自动备课耗时', '自动设计练习耗时', '自动生成知识库耗时', '自动设计实训耗时', '自动分析学生答题'], bottom: 0 },
            xAxis: { type: 'category', data: [start + ' 到 ' + end] },
            yAxis: { type: 'value', name: '秒' },
            series: [
                { name: '自动备课耗时', type: 'bar', data: [allCostTime.value?.knowledgeOrganization_AVG_cost_second], color: '#165DFF' },
                { name: '自动设计练习耗时', type: 'bar', data: [allCostTime.value?.designIssue_AVG_cost_second], color: 'black' },
                { name: '自动生成知识库耗时', type: 'bar', data: [allCostTime.value?.addCourse_buildKnowledgeBase_AVG_cost_second], color: '#00B42A' },
                { name: '自动设计实训耗时', type: 'bar', data: [allCostTime.value?.practiseDesign_AVG_cost_second], color: 'pink' },
                { name: '自动分析学生答题', type: 'bar', data: [allCostTime.value?.getIssueAnswerDetail_AVG_cost_second], color: '#00C1D4' }
            ]
        });
    }
    

    // 4. 学生平均正确率趋势（折线图）
    interface correct_avg {
        record_id: number;
        teacher_username: string;
        course_name: string;
        issue_name: string;
        question_id: string;
        avg_correct: string;
        create_time: string,
        date: string,
    }
    interface series{
        name:string,
        type:'line'
        data:number[]
        color:string
    }
    const R4: R = (await getIssueCorrectAVGRecordApi({ teacher_username: teacher_username, course_name: course_name, startDate: start, endDate: end })).data
    const issue_correct_avg = ref<correct_avg[]>()
    issue_correct_avg.value = R4.data
    const currentIssueNameList = ref<string[]>([])
    currentIssueNameList.value = [...new Set(issue_correct_avg.value?.map(item => item.issue_name))];
    const seriesList = ref<series[]>([])
    // 生成随机颜色的工具函数
    const getRandomColor = (): string => {
        const letters = '0123456789ABCDEF';
        let color = '#';
        for (let i = 0; i < 6; i++) {
            color += letters[Math.floor(Math.random() * 16)];
        }
        return color;
    };

    // 1. 先对 issue_correct_avg 按 date 升序排序
    const sortedData = issue_correct_avg.value
        ? [...issue_correct_avg.value].sort((a, b) => {
            // 处理 date 为 null 的情况（放在最后）
            if (!a.date) return 1;
            if (!b.date) return -1;
            // 正常日期比较（ISO格式字符串可直接比较）
            return new Date(a.date).getTime() - new Date(b.date).getTime();
        })
        : [];

    // 2. 按排序后的顺序处理 seriesList
    seriesList.value = []; // 先清空原有数据
    sortedData.forEach(item => {
        const existingSeries = seriesList.value.find(s => s.name === item.issue_name);

        if (existingSeries) {
            // 处理 avg_correct 为 null 的情况，可替换为 0 或保留 null（根据图表需求）
            existingSeries.data.push(item.avg_correct ? Number(item.avg_correct) : -1);

        } else {
            seriesList.value.push({
                name: item.issue_name,
                type: 'line',
                data: [item.avg_correct ? Number(item.avg_correct) : 0],
                color: getRandomColor()
            });
        }
    });

    if (correctRateChartRef.value) {
        correctRateChart = echarts.init(correctRateChartRef.value);
        correctRateChart.setOption({
            tooltip: { trigger: 'axis' },
            legend: { data: currentIssueNameList.value, bottom: 0 },
            xAxis: { type: 'category', data: generateDateArray(start, end) },
            yAxis: { type: 'value', axisLabel: { formatter: '{value}%' } },
            series: seriesList.value
        });
    }

    // 5. 知识点掌握情况（横向柱状图）
    const yAxisData = ref<string[]>([]);
    const seriesData = ref<any[]>([]);
    knowledgePointCommendDegreeList.value?.forEach(item => { 
        yAxisData.value.push(item.knowledge_point);
        seriesData.value.push(item.commend_degree)
    });
    if (knowledgePointChartRef.value) {
        knowledgePointChart = echarts.init(knowledgePointChartRef.value);
        knowledgePointChart.setOption({
            tooltip: { trigger: 'axis' },
            xAxis: { type: 'value' },
            yAxis: {
                type: 'category',
                data: yAxisData.value
            },
            series: [{
                name: '掌握程度',
                type: 'bar',
                data: seriesData.value,
                color: '#165DFF'
            }]
        });
    }
};

function generateDateArray(startDate: string, endDate: string): string[] {
    // 解析日期为Date对象
    const start = new Date(startDate);
    const end = new Date(endDate);

    // 验证日期有效性
    if (isNaN(start.getTime()) || isNaN(end.getTime())) {
        throw new Error('无效的日期格式，请使用 YYYY-MM-DD 格式');
    }

    // 确保开始日期不晚于结束日期
    if (start > end) {
        throw new Error('开始日期不能晚于结束日期');
    }

    const dateArray: string[] = [];
    const current = new Date(start);

    // 循环生成每一天
    while (current <= end) {
        // 格式化日期为 YYYY-MM-DD
        const year = current.getFullYear();
        const month = String(current.getMonth() + 1).padStart(2, '0'); // 月份从0开始，需+1并补0
        const day = String(current.getDate()).padStart(2, '0'); // 补0确保两位数

        dateArray.push(`${year}-${month}-${day}`);

        // 日期加1天
        current.setDate(current.getDate() + 1);
    }

    return dateArray;
}

// 处理日期查询，修改为处理两个日期
const handleDateQuery = async() => {
    if (stringStartDate.value === '' || stringEndDate.value === '') {
        message.warning('请选择开始日期和结束日期');
        return;
    }

    // 开始时间和结束时间的判断
    const start = new Date(stringStartDate.value);
    const end = new Date(stringEndDate.value);
    if (start > end) {
        message.warning('开始时间不能大于结束时间');
        return;
    }
    if (end < start) {
        message.warning('结束时间不能小于开始时间');
        return;
    }
    R5.value = (await getKnowledgePointCommendDegreeRecordApi({ teacher_username: selectedTeacher.value, course_name: selectedCourse.value, startDate: stringStartDate.value, endDate: stringEndDate.value })).data
    knowledgePointCommendDegreeList.value = R5.value?.data

    if (knowledgePointCommendDegreeList.value) {
        // 1. 复制数组并按 error_rate 从高到低排序
        const sortedList = [...knowledgePointCommendDegreeList.value].sort((a, b) => {
            // 将 error_rate 转换为数字（处理空值或非数字情况，默认为0）
            const rateA = a.error_rate ? parseInt(a.error_rate, 10) : 0;
            const rateB = b.error_rate ? parseInt(b.error_rate, 10) : 0;
            // 降序排序（后 - 前）
            return rateB - rateA;
        });
        errorTop5.value = sortedList.slice(0, 5);
    }

    // 重新初始化所有图表
    initCharts(selectedTeacher.value,selectedCourse.value,stringStartDate.value, stringEndDate.value);
    // 如果已显示详细图表，也重新加载详细数据
    if (showDetailCharts.value) {
        showCharts("firstTarget"); // 调用现有方法，内部会触发initCharts
    }
};

// 重置日期为今天，修改为重置两个日期
const resetDate = () => {
    startDate.value = new Date();
    endDate.value = new Date();
    stringStartDate.value = formatDate(startDate.value);
    stringEndDate.value = formatDate(endDate.value);
    handleDateQuery(); // 自动查询今天的数据
};


// 获取滚动容器的DOM引用
const scrollContainer = ref<HTMLDivElement | null>(null);

const scrollToTarget = (targetId: string) => {
    if (!scrollContainer.value) return;

    // 获取目标标签的DOM元素
    const targetElement = document.getElementById(targetId);
    if (!targetElement) {
        console.warn(`未找到id为${targetId}的标签`);
        return;
    }

    // 计算目标标签相对于滚动容器的偏移量
    // 容器的顶部位置（相对于视口）
    const containerRect = scrollContainer.value.getBoundingClientRect();
    // 目标标签的顶部位置（相对于视口）
    const targetRect = targetElement.getBoundingClientRect();

    // 目标标签相对于容器的偏移量（需要滚动的距离）
    const scrollTop = targetRect.top - containerRect.top + scrollContainer.value.scrollTop;

    // 平滑滚动到目标位置
    scrollContainer.value.scrollTo({
        top: scrollTop,
        behavior: 'smooth' // 平滑滚动
    });
};
// 显示详细图表方法，添加日期范围参数
const showCharts = async(target:string) => {
    if (!selectedTeacher.value || !selectedCourse.value) {
        message.warning('请选择教师和课程');
        return;
    }

    showDetailCharts.value = true;

    R5.value = (await getKnowledgePointCommendDegreeRecordApi({ teacher_username: selectedTeacher.value, course_name: selectedCourse.value, startDate: stringStartDate.value, endDate: stringEndDate.value })).data
    knowledgePointCommendDegreeList.value = R5.value?.data

    if (knowledgePointCommendDegreeList.value) {
        // 1. 复制数组并按 error_rate 从高到低排序
        const sortedList = [...knowledgePointCommendDegreeList.value].sort((a, b) => {
            // 将 error_rate 转换为数字（处理空值或非数字情况，默认为0）
            const rateA = a.error_rate ? parseInt(a.error_rate, 10) : 0;
            const rateB = b.error_rate ? parseInt(b.error_rate, 10) : 0;
            // 降序排序（后 - 前）
            return rateB - rateA;
        });
        errorTop5.value = sortedList.slice(0, 5);
    }

    // 加载详细图表数据时传递日期范围
    await nextTick(() => {
        initCharts(selectedTeacher.value, selectedCourse.value, stringStartDate.value, stringEndDate.value);
        resizeCharts();
    });

    scrollToTarget(target);
    
};

// 调整图表大小
const resizeCharts = () => {
    teacherActiveChart?.resize();
    studentActiveChart?.resize();
    teachingTimeChart?.resize();
    correctRateChart?.resize();
    knowledgePointChart?.resize();
};



// 监听窗口大小变化
const handleResize = () => {
    resizeCharts();
};


// 生命周期钩子修改：初始加载今天的数据
onMounted(async() => {
    R5.value = (await getKnowledgePointCommendDegreeRecordApi({ teacher_username: selectedTeacher.value, course_name: selectedCourse.value, startDate: stringStartDate.value, endDate: stringEndDate.value })).data
    knowledgePointCommendDegreeList.value = R5.value?.data

    if (knowledgePointCommendDegreeList.value) {
        // 1. 复制数组并按 error_rate 从高到低排序
        const sortedList = [...knowledgePointCommendDegreeList.value].sort((a, b) => {
            // 将 error_rate 转换为数字（处理空值或非数字情况，默认为0）
            const rateA = a.error_rate ? parseInt(a.error_rate, 10) : 0;
            const rateB = b.error_rate ? parseInt(b.error_rate, 10) : 0;
            // 降序排序（后 - 前）
            return rateB - rateA;
        });
        errorTop5.value = sortedList.slice(0, 5);
    }

    initCharts(selectedTeacher.value, selectedCourse.value, stringStartDate.value, stringEndDate.value); // 传入日期范围
    window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
    // 销毁图表实例
    teacherActiveChart?.dispose();
    studentActiveChart?.dispose();
    teachingTimeChart?.dispose();
    correctRateChart?.dispose();
    knowledgePointChart?.dispose();

    // 移除窗口大小变化监听
    window.removeEventListener('resize', handleResize);
});

const backToHome = () => {
    router.push({ name: 'HomeView' })
}

</script>

<style scoped>
/* 全局样式重置 */
* {
    box-sizing: border-box;
    margin: 0;
    padding: 0;
}

/* 新增：日期选择栏样式 */
.date-selector {
    padding: 1rem;
    background-color: white;
    border-radius: 0.375rem;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
    margin: 1rem;
    display: flex;
    align-items: center;
    gap: 1rem;
    white-space: nowrap;
}

/* 主容器样式 */
.app-container {
    background-color: #F9FAFB;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
    line-height: 1.5;
}

/* 卡片样式 */
.card {
    background-color: white;
    border-radius: 0.375rem;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
    padding: 1.5rem;
    margin: 1rem;
}

.card-title {
    font-size: 1.125rem;
    font-weight: 600;
    margin-bottom: 1rem;
}

/* 图表容器 */
.chart-container {
    width: 100%;
    height: 100%;
}

.chart-wrapper {
    height: 400px;
    /* 增加图表高度 */
}

/* 表格样式 */
.table {
    width: 100%;
    border-collapse: collapse;
}

.table th,
.table td {
    padding: 0.75rem;
    text-align: left;
    border-bottom: 1px solid #E5E7EB;
}

.table th {
    font-size: 0.75rem;
    font-weight: 500;
    color: #6B7280;
    text-transform: uppercase;
}

/* 选择器样式 */
.selector-container {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 2rem;
    gap: 1rem;
}

.selector-container select {
    padding: 0.5rem 1rem;
    border: 1px solid #E5E7EB;
    border-radius: 0.375rem;
    background-color: white;
    font-size: 1rem;
}

.selector-container button {
    padding: 0.5rem 1rem;
    border: none;
    border-radius: 0.375rem;
    background-color: #165DFF;
    color: white;
    font-size: 1rem;
    cursor: pointer;
}

.selector-container button:hover {
    background-color: #0D47A1;
}

/* 标题样式 */
.teacher-course-title {
    font-size: 1.5rem;
    font-weight: bold;
    margin-bottom: 1rem;
    text-align: center;
    color: #165DFF;
}

/* 响应式布局 */
@media (max-width: 768px) {
    .chart-wrapper {
        height: 300px;
        /* 在小屏幕上减小图表高度 */
    }

    .selector-container {
        flex-direction: column;
    }

    .selector-container select,
    .selector-container button {
        width: 100%;
    }
}

/* 颜色类 */
.bg-danger {
    background-color: #F53F3F;
}

.bg-warning {
    background-color: #FF7D00;
}

.text-danger {
    color: #F53F3F;
}

.text-warning {
    color: #FF7D00;
}

.font-medium {
    font-weight: 500;
}


</style>