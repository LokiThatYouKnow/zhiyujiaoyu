<template>
    <!-- ===== 课程列表主页 ===== -->
    <template v-if="allVisible().teacher_CourseList_home_visible">
        <div class="course-card-container" :key="allData().teacher_CourseList_CourseListKey_data">
            <div class="course-page-header">
                <div class="left-actions">
                    <a-button class="back-btn" @click="backToHome">
                        <span style="margin-right:4px">←</span> 返回首页
                    </a-button>
                    <h2 class="section-title">课程列表</h2>
                </div>
                <div class="right-actions">
                    <a-button type="primary" @click="addCourse">
                        <span style="margin-right:4px">＋</span> 新增课程
                    </a-button>
                </div>
            </div>

            <div class="course-scroll-wrapper">
                <div class="course-cards" v-if="courses.length > 0">
                    <div v-for="(course, index) in courses" :key="course.course_id" class="course-card"
                        :class="{ 'margin-bottom': index < courses.length - 1 }">
                        <div class="card-header">
                            <h2 class="course-name">{{ course.course_name || '未命名课程' }}</h2>
                            <div class="card-header-actions">
                                <a-button size="small" @click="toIssueList(course)">📋 查看练习</a-button>
                                <a-button size="small" @click="toResourceList(course)">📁 课件资源</a-button>
                                <a-button size="small" @click="toCourseData(course)">📊 课程数据</a-button>
                                <a-popconfirm title="确定要删除课程吗" ok-text="确定" cancel-text="取消"
                                    @confirm="deleteCourse(course)" ok-type="danger" placement="top">
                                    <a-button size="small" danger>🗑 删除</a-button>
                                </a-popconfirm>
                            </div>
                        </div>
                        <div class="card-body">
                            <div class="course-info">
                                <div class="info-item">
                                    <div class="info-icon teacher">👨‍🏫</div>
                                    <div class="info-content">
                                        <span class="info-label">授课教师</span>
                                        <span class="info-value">{{ course.course_teacher_name || '未知' }}</span>
                                    </div>
                                </div>
                                <div class="info-item">
                                    <div class="info-icon hours">⏱</div>
                                    <div class="info-content">
                                        <span class="info-label">课程学时</span>
                                        <span class="info-value">{{ course.course_hours || '0' }} 学时</span>
                                    </div>
                                </div>
                                <div class="info-item">
                                    <div class="info-icon score">⭐</div>
                                    <div class="info-content">
                                        <span class="info-label">课程学分</span>
                                        <span class="info-value">{{ course.course_score || '暂无数据' }} 分</span>
                                    </div>
                                </div>
                                <div class="info-item">
                                    <div class="info-icon students">👥</div>
                                    <div class="info-content">
                                        <span class="info-label">学生人数</span>
                                        <span class="info-value">{{ course.course_student_number || '0' }} 人</span>
                                    </div>
                                </div>
                                <div class="info-item">
                                    <div class="info-icon code">🔑</div>
                                    <div class="info-content">
                                        <span class="info-label">邀请码</span>
                                        <span class="info-value" style="display:inline-flex;align-items:center;gap:8px">
                                          {{ course.course_invite_code || '暂无数据' }}
                                          <button
                                            style="display:inline-flex;align-items:center;gap:4px;padding:3px 10px;border:1px solid #7C3AED;border-radius:5px;background:#F5F3FF;color:#7C3AED;cursor:pointer;flex-shrink:0;font-family:inherit;font-size:12px;font-weight:500"
                                            :style="{ opacity: !course.course_invite_code ? 0.35 : 1 }"
                                            :disabled="!course.course_invite_code"
                                            @click.stop="copyInviteCode(course.course_invite_code)"
                                            :title="course.course_invite_code ? '复制邀请码' : ''"
                                          >
                                            <svg viewBox="0 0 24 24" fill="none" width="13" height="13"><rect x="9" y="9" width="13" height="13" rx="2" stroke="currentColor" stroke-width="2"/><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1" stroke="currentColor" stroke-width="2"/></svg>
                                            复制
                                          </button>
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div v-else class="empty-state">
                    <span class="empty-icon">📭</span>
                    <span class="empty-text">暂无课程数据</span>
                    <span class="empty-sub">点击"新增课程"创建你的第一门课程</span>
                </div>
            </div>
        </div>

        <a-modal :open="allVisible().teacher_CourseList_addCourse_visible" title="新增课程" @ok="handleOk"
            @cancel="handleCancel" okText="确定" cancelText="取消" style="top: 20%;" class="custom-course-modal">
            <a-form class="course-form">
                <a-form-item label="课程名称：" name="CourseName" class="form-item taller-form-item">
                    <a-input v-model:value="courseName" placeholder="请输入课程名称" />
                </a-form-item>
                <a-form-item label="课程学时：" name="CourseHours" class="form-item taller-form-item">
                    <a-input-number v-model:value="courseHours" placeholder="请输入课程学时(0-100)(时)" style="width: 100%;"
                        :min="0" :max="100" />
                </a-form-item>
                <a-form-item label="课程学分：" name="CourseScore" class="form-item taller-form-item">
                    <a-input-number v-model:value="courseScore" placeholder="请输入课程学分(0.0-10.0)" style="width: 100%;"
                        :min="0" :max="10" :step="0.1" :precision="1" />
                </a-form-item>
                <CourseListUploadFile :key="allData().teacher_CourseList_addCourse_childKey_data" ref="childRef" />
            </a-form>
        </a-modal>
    </template>

    <template v-if="allVisible().teacher_CourseList_toIssueList_loading_visible">
        <div style="margin-left: 37%;margin-top: 5%;">
            <strong style="font-size: 300%; ">正在获取课程练习ing...</strong>
        </div>
        <div style="margin-left: 37%;margin-top: 3%;">
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

    <template v-if="allVisible().teacher_CourseList_issueList_visible">
        <div class="course-card-container">
            <div class="issue-page-header">
                <div class="left-section">
                    <span class="back-arrow" @click="backToCourseList" title="返回课程列表">&#8249;</span>
                    <h2 class="section-title-2">课程练习 · {{ selectedCourseName || '未知' }}</h2>
                </div>
                <a-button type="primary" @click="addAIIssue">🤖 智能设计练习</a-button>
            </div>
            <div class="course-scroll-wrapper">
                <div class="course-cards" v-if="response.length > 0">
                    <div v-for="(backData, index) in response" :key="backData.question_id" class="course-card-2"
                        :class="{ 'margin-bottom': index < courses.length - 1 }">

                        <template
                            v-if="new Date(backData.start_time).getTime() < new Date(updateCurrentTime()).getTime() && new Date(updateCurrentTime()).getTime() < new Date(backData.end_time).getTime()">
                            <div class='card-header-2'>
                                <h3 class="course-name">{{ backData.issue_name || backData.course_name + '练习' + (index +
                                    1) }}</h3>
                                <span class="time-on">进行中</span>
                            </div>
                        </template>
                        <template
                            v-else-if="new Date(updateCurrentTime()).getTime() < new Date(backData.start_time).getTime()">
                            <div class='card-header-no-start'>
                                <h3 class="course-name">{{ backData.issue_name || backData.course_name + '练习' + (index +
                                    1) }}</h3>
                                <span class="time-no-start">未开始</span>
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
                            <div class="card-footer-actions">
                                <a-button type="primary" size="small" @click="toIssueDetail(backData)">📝 练习内容</a-button>
                                <a-button size="small" @click="exportIssue(backData)">📥 导出</a-button>
                                <a-button type="primary" size="small" @click="toAnswerDetail(backData)" style="background: linear-gradient(135deg, #10B981, #059669) !important; border: none !important;">📈 自动分析答题状况</a-button>
                                <a-popconfirm title="确定要删除该练习吗" ok-text="确定" cancel-text="取消"
                                    @confirm="deleteIssue(backData)" ok-type="danger" placement="top">
                                    <a-button type="primary" danger size="small">🗑 删除</a-button>
                                </a-popconfirm>
                            </div>
                        </div>
                    </div>
                </div>
                <div v-else class="empty-state">暂无练习数据</div>
            </div>
        </div>
    </template>

    <template v-if="allVisible().teacher_CourseList_studentAnswerDetail_loading_visible">
        <div style="margin-left: 34%;margin-top: 5%;">
            <strong style="font-size: 300%; ">正在自动解析答题情况ing...</strong>
        </div>
        <div style="margin-left: 37%;margin-top: 3%;">
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

    <template v-if="allVisible().teacher_CourseList_studentAnswerDetail_visible">
        <div class="detail-page">
            <div class="detail-content">
                <MK :markdownContent="issueAnswerDetail" :title="issueName + '答题详情分析:'"></MK>
            </div>
            <div class="detail-back-bar">
                <a-button @click="SADbackToIssueList" type="primary">← 返回练习列表</a-button>
            </div>
        </div>
    </template>

    <template v-if="allVisible().teacher_CourseList_issueDetail_visible">
        <div class="detail-page">
            <div class="detail-content">
                <MK :markdownContent="issueDetail" :title="issueName+':'"></MK>
            </div>
            <div class="detail-back-bar">
                <a-button @click="backToIssueList" type="primary">← 返回练习列表</a-button>
            </div>
        </div>
    </template>

    <a-modal :open="allVisible().teacher_CourseList_issueDetail_exportTable_visible" title="请选择要导出的格式"
        @ok="issueDetailExport" @cancel="issueDetailExportCancel" okText="确定" cancelText="取消" style="top: 40%;">
        <a-form>
            <a-radio-group v-model:value="issueDetailExportType">
                <a-radio-button value="txt">&nbsp;&nbsp;txt格式&nbsp;&nbsp;</a-radio-button>
                <a-radio-button value="doc">&nbsp;&nbsp;doc格式&nbsp;&nbsp;</a-radio-button>
                <a-radio-button value="docx">&nbsp;&nbsp;docx格式&nbsp;&nbsp;</a-radio-button>
            </a-radio-group>
        </a-form>
    </a-modal>

    <template v-if="allVisible().teacher_CourseList_resourceList_loading_visible">
        <div style="margin-left: 30%;margin-top: 5%;">
            <strong style="font-size: 300%; ">正在获取对应的课件资源列表ing...</strong>
        </div>
        <div style="margin-left: 35%;margin-top: 3%;">
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

    <template v-if="allVisible().teacher_CourseList_resourceList_visible">
        <div class="course-card-container">
            <div class="issue-page-header">
                <div class="left-section">
                    <span class="back-arrow resource" @click="backToCourseListFromResourceList" title="返回课程列表">&#8249;</span>
                    <h2 class="section-title-3">课件资源 · {{ selectedCourseName || '未知' }}</h2>
                </div>
                <div style="display: flex; gap: 12px;">
                    <a-button type="primary" @click="addAIResource">🤖 智能生成总结课件</a-button>
                    <a-button type="primary" @click="addPractiseResource" style="background: linear-gradient(135deg, #EC4899, #8B5CF6) !important; border: none !important;">🔬 智能生成实训课件</a-button>
                </div>
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
                                    <div class="info-content">
                                        <span class="info-label">课件编号</span>
                                        <span class="info-value">{{ backData.data_id || '未知' }}</span>
                                    </div>
                                </div>
                                <div class="info-item">
                                    <div class="info-content">
                                        <span class="info-label">创建时间</span>
                                        <span class="info-value">{{ backData.start_time.replace('T', '  ') || '未知' }}</span>
                                    </div>
                                </div>
                            </div>
                            <div class="card-footer-actions">
                                <a-button type="primary" size="small" @click="toResourceDetail(backData)">📄 课件内容</a-button>
                                <a-button size="small" @click="exportResource(backData)">📥 导出</a-button>
                                <a-popconfirm title="确定要删除该资源吗" ok-text="确定" cancel-text="取消"
                                    @confirm="deleteResource(backData)" ok-type="danger" placement="top">
                                    <a-button type="primary" danger size="small">🗑 删除</a-button>
                                </a-popconfirm>
                            </div>                            </div>
                        </div>
                    </div>
                <div v-else class="empty-state">
                    <span class="empty-icon">📭</span>
                    <span class="empty-text">暂无课件资源数据</span>
                    <span class="empty-sub">点击上方按钮智能生成课件资源</span>
                </div>
            </div>
        </div>
    </template>

    <template v-if="allVisible().teacher_CourseList_resourceDetail_visible">
        <div class="detail-page">
            <div class="detail-content">
                <MK :markdownContent="resourceDetail" :title="''"></MK>
            </div>
            <div class="detail-back-bar">
                <a-button @click="backToResourceList" type="primary">← 返回课件列表</a-button>
            </div>
        </div>
    </template>

    <a-modal :open="allVisible().teacher_CourseList_resourceDetail_exportTable_visible" title="请选择要导出的格式"
        @ok="resourceDetailExport" @cancel="resourceDetailExportCancel" okText="确定" cancelText="取消" style="top: 40%;">
        <a-form>
            <a-radio-group v-model:value="resourceDetailExportType">
                <a-radio-button value="txt">&nbsp;&nbsp;txt格式&nbsp;&nbsp;</a-radio-button>
                <a-radio-button value="doc">&nbsp;&nbsp;doc格式&nbsp;&nbsp;</a-radio-button>
                <a-radio-button value="docx">&nbsp;&nbsp;docx格式&nbsp;&nbsp;</a-radio-button>
            </a-radio-group>
        </a-form>
    </a-modal>

    <template v-if="allVisible().teacher_CourseList_CourseData_visible">
        <div class="app-container" style="overflow-y: auto;height: 100%;" ref="scrollContainer">

            <div class="date-selector" id="firstTarget"
                style="display: flex;flex-direction: row;justify-content: space-between;">

                <a-button class="back-btn" @click="backToCourseListFormCourseData">← 返回</a-button>
                <span class="date-display">📅 {{ stringStartDate }} → {{ stringEndDate }}</span>
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

            <!-- 教师和课程标题 -->
            <div class="card" style="text-align: center;" id="secondTarget">
                <h2 class="teacher-course-title">{{ `${teacherDetail.teacher_name} - ${selectedCourse}` }}</h2>
            </div>

            <!-- 学生平均正确率趋势 -->
            <div class="card">
                <h3 class="card-title">学生课程练习平均正确率趋势</h3>
                <h5>如果数据为-1则代表那个日期还暂无数据</h5>
                <div class="chart-wrapper">
                    <div ref="correctRateChartRef" class="chart-container"></div>
                </div>
            </div>

            <!-- 知识点掌握情况 -->
            <div class="card">
                <h3 class="card-title">知识点掌握情况</h3>
                <div class="chart-wrapper">
                    <div ref="knowledgePointChartRef" class="chart-container"></div>
                </div>
                <h4 style="margin-left: 45%;margin-top: -2%;">{{ stringStartDate }} 到 {{ stringEndDate }}</h4>
            </div>

            <!-- 表格示例 -->
            <div class="card">
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
</template>

<script lang="ts" setup>
import { onUnmounted, reactive, ref } from 'vue';
import type { teacher } from '../../modules/teacher';
import { jwtDecode } from 'jwt-decode';
import { getSelfTeacherDetailApi } from '../../api/teacher/getSelfTeacherDetailApi';
import type { R } from '../../response/R';
import { message } from 'ant-design-vue';
import type { course } from '../../modules/course';
import CourseListUploadFile from '../../components/teacher/CourseListUploadFile.vue';
import { allVisible } from '../../usePinia/allVisible';
import { allStatus } from '../../usePinia/allStatus';
import { deleteCourseApi, deleteIssueApi, deleteResourceApi, getIssueAnswerDetailApi, getIssueCorrectAVGRecordApi, getIssueListApi, getKnowledgePointCommendDegreeRecordApi, getResourceListApi } from '../../api/teacher/CourseListApi';
import { allData } from '../../usePinia/allData';
import { convertToDoc, convertToDocx, convertToTxt } from '../../api/convertApi';
import { v4 as uuidv4 } from 'uuid'

function copyInviteCode(code: string) {
  navigator.clipboard.writeText(code).then(() => {
    message.success('邀请码已复制')
  }).catch(() => {
    message.error('复制失败')
  })
}
import router from '../../routers/appRouters';
import * as echarts from 'echarts';


const token = ref((sessionStorage.getItem('token') || localStorage.getItem('token')))
const teacher_id = jwtDecode(token.value as string).sub as string
const teacherDetail = ref<teacher>({
    teacher_id: 0,
    teacher_account: '',
    teacher_password: '',
    teacher_name: '',
    teacher_gender: '',
    teacher_age: '',
    teacher_major: '',
    teacher_position: '',
    teacher_class_name:'',
    teacher_courseList: [],
    teacher_courseResource: [],
})

const courses = ref<course[]>([])

const courseName = ref('')
const courseHours = ref('0')
const courseScore = ref('0.0')

// 定义子组件的类型
interface ChildComponentInstance {
    uploadFile: (courseName: string, teacherUsername: string, courseHours: string, courseScore: string) => void
}
const childRef = ref<ChildComponentInstance | null>(null)

const getSelfTeacherDetail = async () => { 
    getSelfTeacherDetailApi(teacher_id).then(res => {
        const R: R = res.data
        if (R.code === 200) {
            teacherDetail.value = R.data
            sessionStorage.setItem('teacherDetail', JSON.stringify(teacherDetail.value))
        } else {
            message.error('获取教师信息失败')
        }
    }).catch(err => {
        message.error('获取教师信息失败')
        console.log(err)
    })
}

const setCourses = () => { 
    teacherDetail.value = JSON.parse((sessionStorage.getItem('teacherDetail') || localStorage.getItem('teacherDetail')) as string)
    teacherDetail.value?.teacher_courseList?.forEach(course => {
        courses.value.push(course)
    })
}

if ((sessionStorage.getItem('teacherDetail') || localStorage.getItem('teacherDetail'))) {
    setCourses()
} else {    
    getSelfTeacherDetail()
}

const addCourse = () => { 
    allVisible().teacher_CourseList_addCourse_visible = true
}

const handleOk = () => { 
    if (allStatus().teacher_CourseList_addCourse_isSending){
        message.error('课程正在创建中，请耐心等待')
        return
    }

    if (courseName.value === '' || courseName.value === null) {
        message.error('课程名不能为空')
        return;
    }else if(courseHours.value === '' || courseHours.value === null || courseHours.value === '0'){
        message.error('课程学时不合法')
        return;
    }else if(courseScore.value === '' || courseScore.value === null || courseScore.value === '0.0'){
        message.error('课程学分不合法')
        return;
    }

    if (childRef.value) {
        childRef.value.uploadFile(courseName.value,teacherDetail.value.teacher_account as string,courseHours.value,courseScore.value)
    }
}

const handleCancel = async() => { 
    if (allStatus().teacher_CourseList_addCourse_isSending) {
        allVisible().teacher_CourseList_addCourse_visible = false
        return
    }
    allVisible().teacher_CourseList_addCourse_visible = false
    localStorage.removeItem('teacherDetail')
    getSelfTeacherDetail()
    setTimeout(() => {
        location.reload()
    }, 500)
}

interface backData {
    data: string;
    question_id: string;
    issue_name: string;
    course_name: string;
    start_time: string;
    end_time: string;
    teacher_username: string,
}

const request = reactive({
    data: '',
    teacher_username: '',
});

const response = ref<backData[]>(allData().teacher_CourseList_issueList_data)
const selectedCourseName = ref(allData().teacher_CourseList_selectedCourseName_data)

const toIssueList = async(course:course) => { 
    if (allStatus().teacher_CourseList_addCourse_isSending) {
        message.error('课程正在创建中，请耐心等待')
        return
    }
    allVisible().teacher_CourseList_home_visible = false;
    allVisible().teacher_CourseList_toIssueList_loading_visible = true;
    request.data = course.course_name as string;
    request.teacher_username = course.course_teacher_account as string;
    const R: R = (await getIssueListApi(request)).data
    response.value = JSON.parse(R.data[0].getTeacherIssueList);
    allData().teacher_CourseList_issueList_data = JSON.parse(R.data[0].getTeacherIssueList);
    selectedCourseName.value = course.course_name as string;
    allData().teacher_CourseList_selectedCourseName_data = course.course_name as string;
    allVisible().teacher_CourseList_toIssueList_loading_visible = false;
    allVisible().teacher_CourseList_issueList_visible = true;
}

const backToCourseList = () => { 
    allVisible().teacher_CourseList_issueList_visible = false;
    allVisible().teacher_CourseList_home_visible = true;
}

const issueDetail = ref('')
const issueName = ref('')
const toIssueDetail = (backData:backData) => { 
    issueDetail.value = backData.data;
    issueName.value = backData.issue_name;
    allVisible().teacher_CourseList_issueList_visible = false;
    allVisible().teacher_CourseList_issueDetail_visible = true;
}

const backToIssueList = () => { 
    allVisible().teacher_CourseList_issueDetail_visible = false;
    allVisible().teacher_CourseList_issueList_visible = true;
}

const deleteIssueRequest = ref({
    question_id: allData().teacher_CourseList_deleteQuestionId_data
})

const deleteIssue = async (backData:backData) => { 
    deleteIssueRequest.value.question_id = backData.question_id as string
    allData().teacher_CourseList_deleteQuestionId_data = backData.question_id as string
    const R:R = (await deleteIssueApi(deleteIssueRequest.value)).data
    if(R.code === 200){
        message.success("删除成功")
        localStorage.removeItem("teacherDetail")
        getSelfTeacherDetail()
        setTimeout(() => { 
            location.reload()
        }, 700);
    }else{
        message.error("删除失败")
    }
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

const deleteCourseRequest = ref({
    document_id: allData().teacher_CourseList_deleteCourse_documentId_data,
    teacher_username: allData().teacher_CourseList_deleteCourse_teacherUsername_data,
    data: allData().teacher_CourseList_deleteCourse_courseName_data,
})

const deleteCourse = async(course:course) => { 
    if (allStatus().teacher_CourseList_addCourse_isSending) {
        message.error('课程正在创建中，请耐心等待')
        return
    }
    deleteCourseRequest.value.document_id = course.course_document_id as string
    allData().teacher_CourseList_deleteCourse_documentId_data = course.course_document_id as string
    deleteCourseRequest.value.teacher_username = course.course_teacher_account as string
    allData().teacher_CourseList_deleteCourse_teacherUsername_data = course.course_teacher_account as string
    deleteCourseRequest.value.data = course.course_name as string
    allData().teacher_CourseList_deleteCourse_courseName_data = course.course_name as string
    const R:R = (await deleteCourseApi(deleteCourseRequest.value)).data
    if (R.code === 200) {
        message.success("删除课程成功")
        localStorage.removeItem('teacherDetail')
        getSelfTeacherDetail()
        setCourses()
        setTimeout(() => {
            location.reload()
        }, 500) 
    } else {
        message.error(R.msg)
        localStorage.removeItem('teacherDetail')
        getSelfTeacherDetail()
        setCourses()
        setTimeout(() => {
            location.reload()
        }, 500) 
    }
}

const issueAnswerDetail = ref(allData().teacher_CourseList_issueAnswerDetail_data)
const getAnswerDetailRequest = ref(
    {
        question_id: '',
        data:'',
        teacher_username: '',
    }
)
const toAnswerDetail = async (backData: backData) => {
    if (new Date(updateCurrentTime()).getTime() < new Date(backData.start_time).getTime()){
        message.warning('该练习尚未开始')
        return
    }
    allVisible().teacher_CourseList_issueList_visible = false;
    allVisible().teacher_CourseList_studentAnswerDetail_loading_visible = true
    getAnswerDetailRequest.value.question_id = backData.question_id
    getAnswerDetailRequest.value.data = backData.course_name
    getAnswerDetailRequest.value.teacher_username = backData.teacher_username
    const R: R = (await getIssueAnswerDetailApi(getAnswerDetailRequest.value)).data
    if (R.code === 200) {
        issueAnswerDetail.value = R.data[0].suggestionsAfterAnalysis
        allData().teacher_CourseList_issueAnswerDetail_data = R.data[0].suggestionsAfterAnalysis
        allVisible().teacher_CourseList_studentAnswerDetail_loading_visible = false
        allVisible().teacher_CourseList_studentAnswerDetail_visible = true;
        router.push({ name:'TeacherHomeView'})
        setTimeout(() => {
            router.push({ name: 'CourseList' })
            message.success("获取学生答题详情分析成功")
        }, 50);
    } else {
        message.error('获取学生答题详情分析失败')
    }
}

const SADbackToIssueList = () => { 
    allVisible().teacher_CourseList_studentAnswerDetail_visible = false;
    allVisible().teacher_CourseList_issueList_visible = true;
}

/* 从这开始改 */

const issueDetailExportType = ref('txt')
const currentIssueDetailMK = ref(allData().teacher_CourseList_currentIssueDetailMK_data)
const exportIssue = (backData: backData) => {
    currentIssueDetailMK.value = backData.data;
    allData().teacher_CourseList_currentIssueDetailMK_data = currentIssueDetailMK.value
    allVisible().teacher_CourseList_issueDetail_exportTable_visible = true;
}

const markdownConvertRequest = ref({
    markdown: '',
})

const issueDetailExport = async () => {
    if (issueDetailExportType.value === 'txt') {
        markdownConvertRequest.value.markdown = allData().teacher_CourseList_currentIssueDetailMK_data;
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
        message.success('导出成功', 2);
        allVisible().teacher_CourseList_issueDetail_exportTable_visible = false;
    } else if (issueDetailExportType.value === 'doc') {
        markdownConvertRequest.value.markdown = allData().teacher_CourseList_currentIssueDetailMK_data;
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
        allVisible().teacher_CourseList_issueDetail_exportTable_visible = false;
    } else if (issueDetailExportType.value === 'docx') {
        markdownConvertRequest.value.markdown = allData().teacher_CourseList_currentIssueDetailMK_data;
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
        allVisible().teacher_CourseList_issueDetail_exportTable_visible = false;
    }
}

const issueDetailExportCancel = () => {
    allVisible().teacher_CourseList_issueDetail_exportTable_visible = false;
}


export interface backData2 {
    data: string;
    course_name: string;
    teacher_username: string,
    data_id: string,
    resource_name: string
    start_time: string;
}

const resourceListRequest = ref({
    data: '',
    teacher_username: '',
});

const resourceList = ref<backData2[]>(allData().teacher_CourseList_resourceList_data);

const toResourceList = async (course: course) => {
    if (allStatus().teacher_CourseList_addCourse_isSending) {
        message.error('课程正在创建中，请耐心等待')
        return
    }
    allVisible().teacher_CourseList_home_visible = false
    allVisible().teacher_CourseList_resourceList_loading_visible = true
    resourceListRequest.value.data = course.course_name as string;
    resourceListRequest.value.teacher_username = course.course_teacher_account as string;
    const R: R = (await getResourceListApi(resourceListRequest.value)).data
    resourceList.value = JSON.parse(R.data[0].teacherResourceToGetResourceList)
    allData().teacher_CourseList_resourceList_data = resourceList.value
    selectedCourseName.value = course.course_name as string
    allData().teacher_CourseList_selectedCourseName_data = selectedCourseName.value
    allVisible().teacher_CourseList_resourceList_loading_visible = false
    allVisible().teacher_CourseList_resourceList_visible = true
}

const backToCourseListFromResourceList = () => {
    allVisible().teacher_CourseList_resourceList_visible = false
    allVisible().teacher_CourseList_home_visible = true
}

const resourceDetail = ref(allData().teacher_CourseList_resourceDetail_data)
const resourceName = ref(allData().teacher_CourseList_resourceName_data)
const resourceId = ref(allData().teacher_CourseList_resourceId_data)
const createTime = ref(allData().teacher_CourseList_createTime_data)

const toResourceDetail = (backData: backData2) => {
    resourceDetail.value = backData.data;
    allData().teacher_CourseList_resourceDetail_data = resourceDetail.value
    resourceName.value = backData.resource_name;
    allData().admin_ResourceManager_issueName_data = resourceName.value
    resourceId.value = backData.data_id;
    allData().teacher_CourseList_resourceId_data = resourceId.value
    createTime.value = backData.start_time;
    allData().teacher_CourseList_createTime_data = createTime.value
    allVisible().teacher_CourseList_resourceList_visible = false;
    allVisible().teacher_CourseList_resourceDetail_visible = true;
}

const backToResourceList = () => {
    allVisible().teacher_CourseList_resourceDetail_visible = false;
    allVisible().teacher_CourseList_resourceList_visible = true;
}

const resourceDetailExportType = ref('txt')
const currentResourceDetailMK = ref(allData().teacher_CourseList_currentResourceDetailMK_data)

const exportResource = (backData: backData2) => {
    currentResourceDetailMK.value = backData.data;
    allData().teacher_CourseList_currentResourceDetailMK_data = currentResourceDetailMK.value
    allVisible().teacher_CourseList_resourceDetail_exportTable_visible = true;
}

const markdownConvertRequestByResource = ref({
    markdown: '',
})

const resourceDetailExport = async () => {
    if (resourceDetailExportType.value === 'txt') {
        markdownConvertRequestByResource.value.markdown = allData().teacher_CourseList_currentResourceDetailMK_data;
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
        allVisible().teacher_CourseList_resourceDetail_exportTable_visible = false;
    } else if (resourceDetailExportType.value === 'doc') {
        markdownConvertRequestByResource.value.markdown = allData().teacher_CourseList_currentResourceDetailMK_data;
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
        allVisible().teacher_CourseList_resourceDetail_exportTable_visible = false;
    } else if (resourceDetailExportType.value === 'docx') {
        markdownConvertRequestByResource.value.markdown = allData().teacher_CourseList_currentResourceDetailMK_data;
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
        allVisible().teacher_CourseList_resourceDetail_exportTable_visible = false;
    }
}

const resourceDetailExportCancel = () => {
    allVisible().teacher_CourseList_resourceDetail_exportTable_visible = false;
}

const deleteResourceRequest = ref({
    resource_id: allData().teacher_CourseList_deleteResourceId_data
})

const deleteResource = async(backData2: backData2) => { 
    deleteResourceRequest.value.resource_id = backData2.data_id
    allData().teacher_CourseList_deleteResourceId_data = deleteResourceRequest.value.resource_id
    const R:R = (await deleteResourceApi(deleteResourceRequest.value)).data
    if(R.code === 200) { 
        message.success("删除课件资源成功")
        localStorage.removeItem("teacherDetail")
        getSelfTeacherDetail()
        setTimeout(() => { 
            location.reload()
        }, 700)
    }else{
        message.error("删除课件资源失败")
    }
}

const addAIResource = () =>{ 
    router.push({ name: 'KnowledgeOrganization' })
}

const addPractiseResource = () =>{
    router.push({ name:'PractiseDesign'})
}

const addAIIssue = () =>{ 
    router.push({ name: 'DesignIssue' })
}

const backToHome = () => {
    router.push({ name: 'TeacherHomeView' })
}

const selectedTeacher = ref('')
const selectedCourse = ref('')

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

// 图表引用
const correctRateChartRef = ref<HTMLDivElement | null>(null);
const knowledgePointChartRef = ref<HTMLDivElement | null>(null);

// 图表实例
let correctRateChart: echarts.ECharts | null = null;
let knowledgePointChart: echarts.ECharts | null = null;

/* 图表内第四图的方法，由于第五图也要用到，且第五图不在初始化图表里，所以把方法写在外边 */
interface knowledge_point_commend_degree_record {
    teacher_username: string,
    course_name: number,
    knowledge_point: string,
    commend_degree: string,
    error_time: string
    error_rate: string
    create_time: string
}
const R5 = ref<R>()
const knowledgePointCommendDegreeList = ref<knowledge_point_commend_degree_record[]>()

/* 第五图数据处理处 */
const errorTop5 = ref<knowledge_point_commend_degree_record[]>()

// 初始化图表，修改为接收日期范围
const initCharts = async (teacher_username: string, course_name: string, start: string, end: string) => {
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
    interface series {
        name: string,
        type: 'line'
        data: number[]
        color: string
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
const handleDateQuery = async () => {
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
    initCharts(selectedTeacher.value, selectedCourse.value, stringStartDate.value, stringEndDate.value);
};

// 重置日期为今天，修改为重置两个日期
const resetDate = () => {
    startDate.value = new Date();
    endDate.value = new Date();
    stringStartDate.value = formatDate(startDate.value);
    stringEndDate.value = formatDate(endDate.value);
    handleDateQuery(); // 自动查询今天的数据
};

// 调整图表大小
const resizeCharts = () => {
    correctRateChart?.resize();
    knowledgePointChart?.resize();
};

// 监听窗口大小变化
const handleResize = () => {
    resizeCharts();
};


onUnmounted(() => {
    // 销毁图表实例
    correctRateChart?.dispose();
    knowledgePointChart?.dispose();

    // 移除窗口大小变化监听
    window.removeEventListener('resize', handleResize);
});

const toCourseData = (course:course) =>{
    if (allStatus().teacher_CourseList_addCourse_isSending) {
        message.error('课程正在创建中，请耐心等待')
        return
    }
    selectedTeacher.value = course.course_teacher_account as string;
    selectedCourse.value = course.course_name as string;
    handleDateQuery()
    allVisible().teacher_CourseList_home_visible = false
    allVisible().teacher_CourseList_CourseData_visible = true
}

const backToCourseListFormCourseData = () => {
    allVisible().teacher_CourseList_CourseData_visible = false
    allVisible().teacher_CourseList_home_visible = true
}
</script>

<style scoped>
@import '../../style/views/teacher/CourseList/CourseList.css';
@import '../../style/views/teacher/CourseList/loading.css';
@import '../../style/views/teacher/CourseList/issueList.css';
</style>