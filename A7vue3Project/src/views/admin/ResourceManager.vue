<template>
    <template v-if="allVisible().admin_ResourceManager_visible">
        <div class="course-card-container">
            <div style="display: flex; flex-direction: row;justify-content: space-between;">
                <h2 class="section-title">请选择一个教师</h2>
                <a-button type="primary" @click="backToHome"
                    style="background-color: goldenrod; font-weight: bolder;width: 70px;">返回首页</a-button>
            </div>
            <div class="course-scroll-wrapper">
                <div class="course-cards" v-if="adminVODetail.all_teacher_list.length > 0">
                    <div v-for="(teacher, index) in adminVODetail.all_teacher_list" :key="teacher.teacher_id"
                        class="course-card"
                        :class="{ 'margin-bottom': index < adminVODetail.all_teacher_list.length - 1 }"
                        @click="toCourseList(teacher)">
                        <div class="card-header">
                            <h2 class="course-name">{{ teacher.teacher_name || '未知姓名' }}</h2>
                            <div>
                            </div>
                        </div>
                        <div class="card-body">
                            <div class="course-info">
                                <div class="info-item">
                                    <span class="info-label">教师性别:</span>
                                    <span class="info-value">{{ teacher.teacher_gender || '未知' }}</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">教师年龄:</span>
                                    <span class="info-value">{{ teacher.teacher_age || '未知' }} 岁</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">教师专业:</span>
                                    <span class="info-value">{{ teacher.teacher_major || '暂无数据' }}</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">教师职位:</span>
                                    <span class="info-value">{{ teacher.teacher_position || '暂无数据' }}</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">教师班级:</span>
                                    <span class="info-value">{{ teacher.teacher_class_name || '暂无数据' }}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div v-else class="empty-state">暂无课程数据</div>
            </div>
        </div>
    </template>

    <template v-if="allVisible().admin_ResourceManager_courseList_visible">
        <div class="course-card-container">
            <div style="display: flex;flex-direction: vertical;">
                <h2 style="font-size: 400%;margin-top: -1.2%;color: deepskyblue;font-weight: bolder;cursor: pointer;"
                    @click="backToTeacherList">
                    < </h2>
                        &nbsp;&nbsp;&nbsp;&nbsp;
                        <h2 class="section-title-2">课程列表</h2>
            </div>
            <div class="course-scroll-wrapper">
                <div class="course-cards" v-if="courses.length > 0">
                    <div v-for="(course, index) in courses" :key="course.course_id" class="course-card-2"
                        :class="{ 'margin-bottom': index < courses.length - 1 }">
                        <div class="card-header-2">
                            <h2 class="course-name">{{ course.course_name || '未命名课程' }}</h2>
                            <div>
                                <a-button
                                    style="font-weight: bolder;width: 80px;background-color: goldenrod;color: white;"
                                    @click="toIssueList(course)">查看练习</a-button>
                                &nbsp;&nbsp;
                                <a-button style="font-weight: bolder;width: 100px;background-color: brown;color: white;"
                                    @click="toResourceList(course)">查看课件资源</a-button>
                                &nbsp;&nbsp;
                                <a-popconfirm title="确定要删除课程吗" ok-text="确定" cancel-text="取消"
                                    @confirm="deleteCourse(course)" ok-type="danger" placement="top">
                                    <a-button type="primary" danger
                                        style="font-weight: bolder;width: 80px;">删除课程</a-button>
                                </a-popconfirm>
                            </div>
                        </div>
                        <div class="card-body">
                            <div class="course-info">
                                <div class="info-item">
                                    <span class="info-label">授课教师:</span>
                                    <span class="info-value">{{ course.course_teacher_name || '未知' }}</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">课程学时:</span>
                                    <span class="info-value">{{ course.course_hours || '0' }} 时</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">课程学分:</span>
                                    <span class="info-value">{{ course.course_score || '暂无数据' }} 分</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">学生人数:</span>
                                    <span class="info-value">{{ course.course_student_number || '0' }} 人</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">课程邀请码:</span>
                                    <span class="info-value">{{ course.course_invite_code || '暂无数据' }}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div v-else class="empty-state">暂无课程数据</div>
            </div>
        </div>
    </template>

    <template v-if="allVisible().admin_ResourceManager_issueList_loading_visible">
        <div style="margin-left: 33%;margin-top: 5%;">
            <strong style="font-size: 300%; ">正在获取对应的练习列表ing...</strong>
        </div>
        <div style="margin-left: 36%;margin-top: 3%;">
            <svg xmlns="http://www.w3.org/2000/svg" height="200px" width="200px" viewBox="0 0 200 200" class="pencil">
                <defs>
                    <clipPath id="pencil-eraser">
                        <rect height="30" width="30" ry="5" rx="5"></rect>
                    </clipPath>
                </defs>
                <circle transform="rotate(-113,100,100)" stroke-linecap="round" stroke-dashoffset="439.82"
                    stroke-dasharray="439.82 439.82" stroke-width="2" stroke="currentColor" fill="none" r="70"
                    class="pencil__stroke"></circle>
                <g transform="translate(100,100)" class="pencil__rotate">
                    <g fill="none">
                        <circle transform="rotate(-90)" stroke-dashoffset="402" stroke-dasharray="402.12 402.12"
                            stroke-width="30" stroke="hsl(223,90%,50%)" r="64" class="pencil__body1"></circle>
                        <circle transform="rotate(-90)" stroke-dashoffset="465" stroke-dasharray="464.96 464.96"
                            stroke-width="10" stroke="hsl(223,90%,60%)" r="74" class="pencil__body2"></circle>
                        <circle transform="rotate(-90)" stroke-dashoffset="339" stroke-dasharray="339.29 339.29"
                            stroke-width="10" stroke="hsl(223,90%,40%)" r="54" class="pencil__body3"></circle>
                    </g>
                    <g transform="rotate(-90) translate(49,0)" class="pencil__eraser">
                        <g class="pencil__eraser-skew">
                            <rect height="30" width="30" ry="5" rx="5" fill="hsl(223,90%,70%)"></rect>
                            <rect clip-path="url(#pencil-eraser)" height="30" width="5" fill="hsl(223,90%,60%)">
                            </rect>
                            <rect height="20" width="30" fill="hsl(223,10%,90%)"></rect>
                            <rect height="20" width="15" fill="hsl(223,10%,70%)"></rect>
                            <rect height="20" width="5" fill="hsl(223,10%,80%)"></rect>
                            <rect height="2" width="30" y="6" fill="hsla(223,10%,10%,0.2)"></rect>
                            <rect height="2" width="30" y="13" fill="hsla(223,10%,10%,0.2)"></rect>
                        </g>
                    </g>
                    <g transform="rotate(-90) translate(49,-30)" class="pencil__point">
                        <polygon points="15 0,30 30,0 30" fill="hsl(33,90%,70%)"></polygon>
                        <polygon points="15 0,6 30,0 30" fill="hsl(33,90%,50%)"></polygon>
                        <polygon points="15 0,20 10,10 10" fill="hsl(223,10%,10%)"></polygon>
                    </g>
                </g>
            </svg>
        </div>
    </template>

    <template v-if="allVisible().admin_ResourceManager_issueList_visible">
        <div class="course-card-container">
            <div style="display: flex; flex-direction: vertical;">
                <h2 style="font-size: 400%;margin-top: -1.6%;color: deepskyblue;font-weight: bolder;cursor: pointer;"
                    @click="backToCourseList">
                    < </h2>
                        &nbsp;&nbsp;&nbsp;&nbsp;
                        <h2 class="section-title-3">课程练习&nbsp;&nbsp;({{ selectedCourseName || '未知' }})</h2>
            </div>
            <div class="course-scroll-wrapper">
                <div class="course-cards" v-if="issueList.length > 0">
                    <div v-for="(backData, index) in issueList" :key="backData.question_id" class="course-card-2"
                        :class="{ 'margin-bottom': index < courses.length - 1 }">

                        <template
                            v-if="new Date(updateCurrentTime()).getTime() < new Date(backData.end_time).getTime()">
                            <div class='card-header-3'>
                                <h3 class="course-name">{{ backData.issue_name || backData.course_name + '练习' + (index +
                                    1) }}</h3>
                                <span class="time-on">进行中</span>
                            </div>
                        </template>
                        <template v-else>
                            <div class='card-header-time-out'>
                                <h3 class="course-name">{{ backData.issue_name || backData.course_name + '练习' + (index +
                                    1) }}</h3>
                                <span class="time-out">已结束</span>
                            </div>
                        </template>
                        <div class="card-body">
                            <div class="course-info">
                                <div class="info-item">
                                    <span class="info-label">开始时间:</span>
                                    <span class="info-value">{{ backData.start_time.replace('T',
                                        '\u00A0\u00A0\u00A0') || '未知' }}</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">结束时间:</span>
                                    <span class="info-value">{{ backData.end_time.replace('T',
                                        '\u00A0\u00A0\u00A0') || '未知' }}</span>
                                </div>
                            </div>
                            <div style="display: flex;flex-direction: row;justify-content: end;">
                                <a-button type="primary" style="width: 80px;margin-left: 78%;"
                                    @click="toIssueDetail(backData)">练习内容</a-button>
                                <a-button type="primary" style="width: 80px;margin-left: 1%;"
                                    @click="exportIssue(backData)">导出</a-button>
                                <a-popconfirm title="确定要删除该练习吗" ok-text="确定" cancel-text="取消"
                                    @confirm="deleteIssue(backData)" ok-type="danger" placement="top">
                                    <a-button type="primary" danger
                                        style="font-weight: bolder;width: 80px;margin-left: 1%;">删除</a-button>
                                </a-popconfirm>
                            </div>
                        </div>
                    </div>
                </div>
                <div v-else class="empty-state">暂无练习数据</div>
            </div>
        </div>
    </template>

    <template v-if="allVisible().admin_ResourceManager_issueDetail_visible">
        <div style="overflow-y: auto;height: 100%;">
            <MK :markdownContent="issueDetail" :title="issueName + ':'"></MK>
        </div>
        <a-button @click="backToIssueList" type="primary" style="margin-left: 80%;width: 7%;">返回</a-button>
    </template>

    <a-modal :open="allVisible().admin_ResourceManager_issueDetail_exportTable_visible" title="请选择要导出的格式"
        @ok="issueDetailExport" @cancel="issueDetailExportCancel" okText="确定" cancelText="取消" style="top: 40%;">
        <a-form>
            <a-radio-group v-model:value="issueDetailExportType">
                <a-radio-button value="txt">&nbsp;&nbsp;txt格式&nbsp;&nbsp;</a-radio-button>
                <a-radio-button value="doc">&nbsp;&nbsp;doc格式&nbsp;&nbsp;</a-radio-button>
                <a-radio-button value="docx">&nbsp;&nbsp;docx格式&nbsp;&nbsp;</a-radio-button>
            </a-radio-group>
        </a-form>
    </a-modal>

    <template v-if="allVisible().admin_ResourceManager_resourceList_loading_visible">
        <div style="margin-left: 31%;margin-top: 5%;">
            <strong style="font-size: 300%; ">正在获取对应的课件资源列表ing...</strong>
        </div>
        <div style="margin-left: 36%;margin-top: 3%;">
            <svg xmlns="http://www.w3.org/2000/svg" height="200px" width="200px" viewBox="0 0 200 200" class="pencil">
                <defs>
                    <clipPath id="pencil-eraser">
                        <rect height="30" width="30" ry="5" rx="5"></rect>
                    </clipPath>
                </defs>
                <circle transform="rotate(-113,100,100)" stroke-linecap="round" stroke-dashoffset="439.82"
                    stroke-dasharray="439.82 439.82" stroke-width="2" stroke="currentColor" fill="none" r="70"
                    class="pencil__stroke"></circle>
                <g transform="translate(100,100)" class="pencil__rotate">
                    <g fill="none">
                        <circle transform="rotate(-90)" stroke-dashoffset="402" stroke-dasharray="402.12 402.12"
                            stroke-width="30" stroke="hsl(223,90%,50%)" r="64" class="pencil__body1"></circle>
                        <circle transform="rotate(-90)" stroke-dashoffset="465" stroke-dasharray="464.96 464.96"
                            stroke-width="10" stroke="hsl(223,90%,60%)" r="74" class="pencil__body2"></circle>
                        <circle transform="rotate(-90)" stroke-dashoffset="339" stroke-dasharray="339.29 339.29"
                            stroke-width="10" stroke="hsl(223,90%,40%)" r="54" class="pencil__body3"></circle>
                    </g>
                    <g transform="rotate(-90) translate(49,0)" class="pencil__eraser">
                        <g class="pencil__eraser-skew">
                            <rect height="30" width="30" ry="5" rx="5" fill="hsl(223,90%,70%)"></rect>
                            <rect clip-path="url(#pencil-eraser)" height="30" width="5" fill="hsl(223,90%,60%)">
                            </rect>
                            <rect height="20" width="30" fill="hsl(223,10%,90%)"></rect>
                            <rect height="20" width="15" fill="hsl(223,10%,70%)"></rect>
                            <rect height="20" width="5" fill="hsl(223,10%,80%)"></rect>
                            <rect height="2" width="30" y="6" fill="hsla(223,10%,10%,0.2)"></rect>
                            <rect height="2" width="30" y="13" fill="hsla(223,10%,10%,0.2)"></rect>
                        </g>
                    </g>
                    <g transform="rotate(-90) translate(49,-30)" class="pencil__point">
                        <polygon points="15 0,30 30,0 30" fill="hsl(33,90%,70%)"></polygon>
                        <polygon points="15 0,6 30,0 30" fill="hsl(33,90%,50%)"></polygon>
                        <polygon points="15 0,20 10,10 10" fill="hsl(223,10%,10%)"></polygon>
                    </g>
                </g>
            </svg>
        </div>
    </template>

    <template v-if="allVisible().admin_ResourceManager_resourceList_visible">
        <div class="course-card-container">
            <div style="display: flex; flex-direction: vertical;">
                <h2 style="font-size: 400%;margin-top: -1.6%;color: deepskyblue;font-weight: bolder;cursor: pointer;"
                    @click="backToCourseListFromResourceList">
                    < </h2>
                        &nbsp;&nbsp;&nbsp;&nbsp;
                        <h2 class="section-title-3">课件资源&nbsp;&nbsp;({{ selectedCourseName || '未知' }})</h2>
            </div>
            <div class="course-scroll-wrapper">
                <div class="course-cards" v-if="resourceList.length > 0">
                    <div v-for="(backData, index) in resourceList" :key="backData.data_id" class="course-card-2"
                        :class="{ 'margin-bottom': index < resourceList.length - 1 }">
                        <div class='card-header-3'>
                            <div
                                style="display: flex;flex-direction: vertical;flex-direction: row;align-items: center;">
                                <h3 class="course-name" style="white-space: nowrap;margin-right: auto">{{
                                    backData.resource_name ||
                                    backData.course_name + '资源' +
                                    (index +
                                    1) }}</h3>
                            </div>
                        </div>
                        <div class="card-body">
                            <div class="course-info">
                                <div class="info-item">
                                    <span class="info-label">课件编号:</span>
                                    <span class="info-value">{{ backData.data_id || '未知' }}</span>
                                </div>
                            </div>
                            &nbsp;
                            &nbsp;
                            <div class="course-info">
                                <div class="info-item">
                                    <span class="info-label">创建时间:</span>
                                    <span class="info-value">{{ backData.start_time.replace('T',
                                        '\u00A0\u00A0\u00A0') || '未知' }}</span>
                                </div>
                            </div>
                            <div style="display: flex;flex-direction: row; justify-content: end;">
                                <a-button type="primary" style="width: 80px;"
                                    @click="toResourceDetail(backData)">课件内容</a-button>
                                <a-button type="primary" style="width: 80px;margin-left: 1%;"
                                    @click="exportResource(backData)">导出</a-button>
                                <a-popconfirm title="确定要删除该资源吗" ok-text="确定" cancel-text="取消"
                                    @confirm="deleteResource(backData)" ok-type="danger" placement="top">
                                    <a-button type="primary" danger
                                        style="font-weight: bolder;width: 80px;margin-left: 1%;">删除</a-button>
                                </a-popconfirm>
                            </div>
                        </div>
                    </div>
                </div>
                <div v-else class="empty-state">暂无课件资源数据</div>
            </div>
        </div>
    </template>

    <template v-if="allVisible().admin_ResourceManager_resourceDetail_visible">
        <div style="overflow-y: auto;height: 100%;">
            <MK :markdownContent="resourceDetail" :title="''"></MK>
        </div>
        <a-button @click="backToResourceList" type="primary" style="margin-left: 80%;width: 7%;">返回</a-button>
    </template>

    <a-modal :open="allVisible().admin_ResourceManager_resourceDetail_exportTable_visible" title="请选择要导出的格式"
        @ok="resourceDetailExport" @cancel="resourceDetailExportCancel" okText="确定" cancelText="取消" style="top: 40%;">
        <a-form>
            <a-radio-group v-model:value="resourceDetailExportType">
                <a-radio-button value="txt">&nbsp;&nbsp;txt格式&nbsp;&nbsp;</a-radio-button>
                <a-radio-button value="doc">&nbsp;&nbsp;doc格式&nbsp;&nbsp;</a-radio-button>
                <a-radio-button value="docx">&nbsp;&nbsp;docx格式&nbsp;&nbsp;</a-radio-button>
            </a-radio-group>
        </a-form>
    </a-modal>

</template>

<script lang="ts" setup>
import { allVisible } from "../../usePinia/allVisible"
import { ref } from 'vue';
import type { teacher } from '../../modules/teacher';
import { jwtDecode } from 'jwt-decode';
import type { R } from '../../response/R';
import { message } from 'ant-design-vue';
import type { course } from '../../modules/course';
import type { adminVO } from "../../modules/adminVO";
import { getSelfAdminDetailApi } from "../../api/admin/getSelfAdminDetailApi";
import type { admin } from "../../modules/admin";
import type { student } from "../../modules/student";
import { allData } from "../../usePinia/allData";
import { deleteCourseApi, deleteIssueApi, deleteResourceApi, getIssueListApi, getResourceListApi } from "../../api/admin/ResourceManagerApi";
import { convertToDoc, convertToDocx, convertToTxt } from "../../api/convertApi";
import { v4 as uuidv4 } from 'uuid'
import router from "../../routers/appRouters";

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

const courses = ref<course[]>(allData().admin_ResourceManager_courseList_data)

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

const toCourseList = (selectedTeacher: teacher) => {
    adminVODetail.value.all_teacher_list.forEach(teacher => { 
        if (teacher.teacher_account === selectedTeacher.teacher_account){
            courses.value = teacher.teacher_courseList as course[]
            allData().admin_ResourceManager_courseList_data = courses.value
            allVisible().admin_ResourceManager_visible = false
            allVisible().admin_ResourceManager_courseList_visible = true
            return
        }
    })
}

const backToTeacherList = () => { 
    allVisible().admin_ResourceManager_courseList_visible = false
    allVisible().admin_ResourceManager_visible = true
}

export interface backData {
    data: string;
    question_id: string;
    issue_name: string;
    course_name: string;
    start_time: string;
    end_time: string;
    teacher_username: string,
}

const issueListRequest = ref({
    data: '',
    teacher_username: '',
});

const issueList = ref<backData[]>(allData().admin_ResourceManager_issueList_data);
const selectedCourseName = ref(allData().admin_ResourceManager_selectedCourseName_data)

const toIssueList = async(course: course) => {
    allVisible().admin_ResourceManager_courseList_visible = false
    allVisible().admin_ResourceManager_issueList_loading_visible = true
    issueListRequest.value.data = course.course_name as string;
    issueListRequest.value.teacher_username = course.course_teacher_account as string;
    const R: R = (await getIssueListApi(issueListRequest.value)).data
    issueList.value = JSON.parse(R.data[0].adminResourceToGetIssueList)
    allData().admin_ResourceManager_issueList_data = issueList.value
    selectedCourseName.value = course.course_name as string
    allData().admin_ResourceManager_selectedCourseName_data = selectedCourseName.value
    allVisible().admin_ResourceManager_issueList_loading_visible = false
    allVisible().admin_ResourceManager_issueList_visible = true
}

const backToCourseList = () => { 
    allVisible().admin_ResourceManager_issueList_visible = false
    allVisible().admin_ResourceManager_courseList_visible = true
}

const issueDetail = ref(allData().admin_ResourceManager_issueDetail_data)
const issueName = ref(allData().admin_ResourceManager_issueName_data)

const toIssueDetail = (backData: backData) => {
    issueDetail.value = backData.data;
    allData().admin_ResourceManager_issueDetail_data = issueDetail.value
    issueName.value = backData.issue_name;
    allData().admin_ResourceManager_issueName_data = issueName.value
    allVisible().admin_ResourceManager_issueList_visible = false;
    allVisible().admin_ResourceManager_issueDetail_visible = true;
}

const backToIssueList = () => { 
    allVisible().admin_ResourceManager_issueDetail_visible = false;
    allVisible().admin_ResourceManager_issueList_visible = true;
}

const issueDetailExportType = ref('txt')
const currentIssueDetailMK = ref(allData().admin_ResourceManager_currentIssueDetailMK_data)
const exportIssue = (backData: backData) => { 
    currentIssueDetailMK.value = backData.data;
    allData().admin_ResourceManager_currentIssueDetailMK_data = currentIssueDetailMK.value
    allVisible().admin_ResourceManager_issueDetail_exportTable_visible = true;
}

const markdownConvertRequest = ref({
    markdown: '',
})

const issueDetailExport = async() => { 
    if (issueDetailExportType.value === 'txt') { 
        markdownConvertRequest.value.markdown = allData().admin_ResourceManager_currentIssueDetailMK_data;
        const response = await convertToTxt(markdownConvertRequest.value);
        // 创建Blob对象
        const blob = new Blob([response.data], { type: 'application/octet-stream' });
        // 创建下载链接
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        const uuidFileName: string = uuidv4();
        a.download = uuidFileName + '.txt';
        a.style.display = 'none';
        document.body.appendChild(a);
        a.click();
        // 清理
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
        message.success('导出成功',2);
        allVisible().admin_ResourceManager_issueDetail_exportTable_visible = false;
    } else if (issueDetailExportType.value === 'doc'){
        markdownConvertRequest.value.markdown = allData().admin_ResourceManager_currentIssueDetailMK_data;
        const response = await convertToDoc(markdownConvertRequest.value);
        // 创建Blob对象
        const blob = new Blob([response.data], { type: 'application/octet-stream' });
        // 创建下载链接
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        const uuidFileName: string = uuidv4();
        a.download = uuidFileName + '.doc';
        a.style.display = 'none';
        document.body.appendChild(a);
        a.click();
        // 清理
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
        message.success('导出成功', 2);
        allVisible().admin_ResourceManager_issueDetail_exportTable_visible = false;
    } else if (issueDetailExportType.value === 'docx'){
        markdownConvertRequest.value.markdown = allData().admin_ResourceManager_currentIssueDetailMK_data;
        const response = await convertToDocx(markdownConvertRequest.value);
        // 创建Blob对象
        const blob = new Blob([response.data], { type: 'application/octet-stream' });
        // 创建下载链接
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        const uuidFileName: string = uuidv4();
        a.download = uuidFileName + '.docx';
        a.style.display = 'none';
        document.body.appendChild(a);
        a.click();
        // 清理
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
        message.success('导出成功', 2);
        allVisible().admin_ResourceManager_issueDetail_exportTable_visible = false;
    }
}

const issueDetailExportCancel = () => { 
    allVisible().admin_ResourceManager_issueDetail_exportTable_visible = false;
}


// 更新当前时间（解决时区问题）
const updateCurrentTime = () => {
    const now = new Date();

    now.setMinutes(now.getMinutes());

    // 格式化为本地时区的 YYYY-MM-DD HH:mm 格式
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');

    // 格式化为 ISO 格式（YYYY-MM-DDTHH:mm）
    return `${year}-${month}-${day}T${hours}:${minutes}`;
};

export interface backData2 {
    data: string;
    course_name: string;
    teacher_username: string,
    data_id:string,
    resource_name:string
    start_time: string;
}

const resourceListRequest = ref({
    data: '',
    teacher_username: '',
});

const resourceList = ref<backData2[]>(allData().admin_ResourceManager_resourceList_data);

const toResourceList = async(course: course) => {
    allVisible().admin_ResourceManager_courseList_visible = false
    allVisible().admin_ResourceManager_resourceList_loading_visible = true
    resourceListRequest.value.data = course.course_name as string;
    resourceListRequest.value.teacher_username = course.course_teacher_account as string;
    const R: R = (await getResourceListApi(resourceListRequest.value)).data
    resourceList.value = JSON.parse(R.data[0].adminResourceToGetResourceList)
    allData().admin_ResourceManager_resourceList_data = resourceList.value
    selectedCourseName.value = course.course_name as string
    allData().admin_ResourceManager_selectedCourseName_data = selectedCourseName.value
    allVisible().admin_ResourceManager_resourceList_loading_visible = false
    allVisible().admin_ResourceManager_resourceList_visible = true
}

const backToCourseListFromResourceList = () => { 
    allVisible().admin_ResourceManager_resourceList_visible = false
    allVisible().admin_ResourceManager_courseList_visible = true
}

const resourceDetail = ref(allData().admin_ResourceManager_resourceDetail_data)
const resourceName = ref(allData().admin_ResourceManager_resourceName_data)
const resourceId = ref(allData().admin_ResourceManager_resourceId_data)
const createTime = ref(allData().admin_ResourceManager_createTime_data)

const toResourceDetail = (backData: backData2) => { 
    resourceDetail.value = backData.data;
    allData().admin_ResourceManager_resourceDetail_data = resourceDetail.value
    resourceName.value = backData.resource_name;
    allData().admin_ResourceManager_issueName_data = resourceName.value
    resourceId.value = backData.data_id;
    allData().admin_ResourceManager_resourceId_data = resourceId.value
    createTime.value = backData.start_time;
    allData().admin_ResourceManager_createTime_data = createTime.value
    allVisible().admin_ResourceManager_resourceList_visible = false;
    allVisible().admin_ResourceManager_resourceDetail_visible = true;
}

const backToResourceList = () => { 
    allVisible().admin_ResourceManager_resourceDetail_visible = false;
    allVisible().admin_ResourceManager_resourceList_visible = true;
}

const resourceDetailExportType = ref('txt')
const currentResourceDetailMK = ref(allData().admin_ResourceManager_currentResourceDetailMK_data)

const exportResource = (backData: backData2) => {
    currentResourceDetailMK.value = backData.data;
    allData().admin_ResourceManager_currentResourceDetailMK_data = currentResourceDetailMK.value
    allVisible().admin_ResourceManager_resourceDetail_exportTable_visible = true;
}

const markdownConvertRequestByResource = ref({
    markdown: '',
})

const resourceDetailExport = async () => {
    if (resourceDetailExportType.value === 'txt') {
        markdownConvertRequestByResource.value.markdown = allData().admin_ResourceManager_currentResourceDetailMK_data;
        const response = await convertToTxt(markdownConvertRequestByResource.value);
        // 创建Blob对象
        const blob = new Blob([response.data], { type: 'application/octet-stream' });
        // 创建下载链接
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        const uuidFileName: string = uuidv4();
        a.download = uuidFileName + '.txt';
        a.style.display = 'none';
        document.body.appendChild(a);
        a.click();
        // 清理
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
        message.success('导出成功', 2);
        allVisible().admin_ResourceManager_resourceDetail_exportTable_visible = false;
    } else if (resourceDetailExportType.value === 'doc') {
        markdownConvertRequestByResource.value.markdown = allData().admin_ResourceManager_currentResourceDetailMK_data;
        const response = await convertToDoc(markdownConvertRequestByResource.value);
        // 创建Blob对象
        const blob = new Blob([response.data], { type: 'application/octet-stream' });
        // 创建下载链接
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        const uuidFileName: string = uuidv4();
        a.download = uuidFileName + '.doc';
        a.style.display = 'none';
        document.body.appendChild(a);
        a.click();
        // 清理
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
        message.success('导出成功', 2);
        allVisible().admin_ResourceManager_resourceDetail_exportTable_visible = false;
    } else if (resourceDetailExportType.value === 'docx') {
        markdownConvertRequestByResource.value.markdown = allData().admin_ResourceManager_currentResourceDetailMK_data;
        const response = await convertToDocx(markdownConvertRequestByResource.value);
        // 创建Blob对象
        const blob = new Blob([response.data], { type: 'application/octet-stream' });
        // 创建下载链接
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        const uuidFileName: string = uuidv4();
        a.download = uuidFileName + '.docx';
        a.style.display = 'none';
        document.body.appendChild(a);
        a.click();
        // 清理
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
        message.success('导出成功', 2);
        allVisible().admin_ResourceManager_resourceDetail_exportTable_visible = false;
    }
}

const resourceDetailExportCancel = () => {
    allVisible().admin_ResourceManager_resourceDetail_exportTable_visible = false;
}

const deleteCourseRequest = ref({
    teacher_username:"",
    data:"",
    document_id:""
})
const deleteCourse = async(course:course) => {
    deleteCourseRequest.value.teacher_username = course.course_teacher_account as string
    deleteCourseRequest.value.data = course.course_name as string
    deleteCourseRequest.value.document_id = course.course_document_id as string
    const R: R = (await deleteCourseApi(deleteCourseRequest.value)).data
    if(R.code === 200){
        localStorage.removeItem("adminDetail")
        getSelfAdminDetail()
        message.success(R.msg,2)
        setTimeout(() => {
            location.reload()
        }, 1000)
    }else{
        message.error("删除课程失败",2)
    }
}

const deleteIssueRequest = ref({
    question_id: "",
})
const deleteIssue = async(issue:backData) => {
    deleteIssueRequest.value.question_id = issue.question_id as string
    const R: R = (await deleteIssueApi(deleteIssueRequest.value)).data
    if(R.code === 200){
        message.success(R.msg,2)
        setTimeout(() => {
            location.reload()
        }, 1000)
    }else{
        message.error("删除练习失败",2)
    }
}

const deleteResourceRequest = ref({
    resource_id: "",
})
const deleteResource = async(resource:backData2) =>{
    deleteResourceRequest.value.resource_id = resource.data_id
    const R:R = (await deleteResourceApi(deleteResourceRequest.value)).data
    if(R.code === 200) { 
        message.success(R.msg,2)
        setTimeout(() => {
            location.reload()
        }, 1000)
    }else{
        message.error("删除课件资源失败",2)
    }
}

const backToHome = () => {
    router.push({ name: 'HomeView' })
}
</script>

<style scoped>
@import '../../style/views/admin/ResourceManager/loading.css';
/* 基础样式重置 */
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

/* 容器样式 */
.course-card-container {
    width: 100%;
    padding: 20px;
}

.section-title {
    font-size: 1.8rem;
    font-weight: 700;
    color: rgb(14, 132, 229);
    margin-bottom: 19px;
    position: relative;
    padding-bottom: 8px;
}

.section-title::after {
    content: '';
    position: absolute;
    left: 0;
    bottom: 0;
    width: 80px;
    height: 4px;
    background-color: rgb(7, 147, 179);
    border-radius: 2px;
}

.section-title-2 {
    font-size: 1.8rem;
    font-weight: 700;
    color: rgba(104, 104, 6, 0.667);
    margin-bottom: 19px;
    position: relative;
    padding-bottom: 8px;
}

.section-title-3 {
    font-size: 1.8rem;
    font-weight: 700;
    background: linear-gradient(135deg, #6366f1, #8b5cf6, #a855f7, #d434fe, #ec4899);
    background-clip: text;
    color: transparent;
    margin-bottom: 5px;
    position: relative;
    padding-bottom: 8px;
}

.section-title-2::after {
    content: '';
    position: absolute;
    left: 0;
    bottom: 0;
    width: 80px;
    height: 4px;
    background-color: rgb(31, 126, 12);
    border-radius: 2px;
}

/* 滚动容器样式 */
.course-scroll-wrapper {
    max-height: 500px;
    /* 最大高度，超出后显示滚动条 */
    overflow-y: auto;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    background-color: #fff;
    padding: 10px;
}

/* 卡片列表样式 */
.course-cards {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.margin-bottom {
    margin-bottom: 15px;
}

/* 卡片样式 */
.course-card {
    background-color: #fff;
    border-radius: 10px;
    overflow: hidden;
    box-shadow: 0 3px 10px rgba(0, 0, 0, 0.05);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    cursor: pointer;
}

.course-card-2 {
    background-color: #fff;
    border-radius: 10px;
    overflow: hidden;
    box-shadow: 0 3px 10px rgba(0, 0, 0, 0.05);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.course-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 20px rgba(67, 97, 238, 0.12);
}

.card-header {
    background: linear-gradient(to right, #4361ee, #3f37c9);
    padding: 15px 20px;
    border-bottom: 1px solid black;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.card-header-2 {
    background: linear-gradient(135deg, #9D50BB 0%, #6E48AA 100%);
    padding: 15px 20px;
    border-bottom: 1px solid black;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.card-header-3 {
    background: radial-gradient(circle at 20% 30%, #4f46e5, #8b5cf6, #ec4899, #f87171);
    padding: 15px 20px;
    border-bottom: 1px solid black;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.card-header-time-out {
    background: linear-gradient(135deg, #121212 0%, #1e1e1e 50%, #2a2a2a 100%);
    padding: 15px 20px;
    border-bottom: 1px solid black;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.course-name {
    font-size: 1.1rem;
    font-weight: 600;
    color: gold;
}

.course-id {
    font-size: 0.85rem;
    color: #ffffff;
    background-color: #d7bb2e;
    padding: 3px 8px;
    border-radius: 4px;
    font-weight: bolder;
}

.card-body {
    padding: 15px 20px;
}

.time-on {
    font-weight: bold;
    font-size: 0.85rem;
    color: #000000;
    background-color: #31ec69;
    padding: 3px 8px;
    border-radius: 4px;
}

.time-out {
    font-weight: bold;
    font-size: 0.85rem;
    color: #f04343;
    background-color: #000000;
    padding: 3px 8px;
    border-radius: 4px;
}

.course-info {
    display: grid;
    grid-template-columns: 1fr;
    gap: 12px;
}

.info-item {
    display: flex;
    align-items: center;
}

.info-label {
    font-size: 0.9rem;
    color: #6b7280;
    width: 80px;
    font-weight: 500;
}

.info-value {
    font-size: 0.9rem;
    color: #333;
    flex: 1;
}

/* 响应式设计 */
@media (min-width: 768px) {
    .course-card-container {
        padding: 30px;
    }

    .course-scroll-wrapper {
        max-height: 600px;
    }

    .course-info {
        grid-template-columns: repeat(2, 1fr);
    }
}

/* 空状态样式 */
.empty-state {
    padding: 40px 20px;
    text-align: center;
    color: #94a3b8;
    font-size: 1rem;
    background-color: #f8fafc;
    border-radius: 12px;
    min-height: 200px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
}
</style>