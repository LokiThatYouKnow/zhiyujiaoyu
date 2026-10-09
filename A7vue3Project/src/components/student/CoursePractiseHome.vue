<template>
    <!-- ===== Course List View ===== -->
    <template v-if="allVisible().student_CoursePractise_courseList_visible">
        <div class="cp-home">
            <div class="cp-toolbar">
                <button class="btn-back" @click="backToHome">
                    <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M19 12H5M12 19l-7-7 7-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                    返回首页
                </button>
                <h2 class="cp-title">课程列表</h2>
                <button class="btn-join" @click="joinCourseTable">
                    <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
                    加入课程
                </button>
            </div>

            <div class="courses-grid" v-if="courses.length > 0">
                <div v-for="course in courses" :key="course.course_id" class="course-card"
                    @click="toCoursePractiseList(course)">
                    <div class="course-card-bar"></div>
                    <div class="course-card-body">
                        <div class="course-card-head">
                            <span class="course-emoji">📚</span>
                            <span class="course-id-chip">课程</span>
                        </div>
                        <h3 class="course-name">{{ course.course_name || '未命名课程' }}</h3>
                        <div class="course-meta">
                            <div class="meta-row">
                                <svg viewBox="0 0 24 24" fill="none" width="14" height="14"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
                                <span>{{ course.course_teacher_name || '未知' }}</span>
                            </div>
                            <div class="meta-row">
                                <svg viewBox="0 0 24 24" fill="none" width="14" height="14"><circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/><path d="M12 6v6l4 2" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
                                <span>{{ course.course_hours || '-' }} 学时</span>
                            </div>
                            <div class="meta-row">
                                <svg viewBox="0 0 24 24" fill="none" width="14" height="14"><path d="M12 20h9M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                                <span>{{ course.course_score || '-' }} 学分</span>
                            </div>
                        </div>
                        <div class="course-students">
                            <svg viewBox="0 0 24 24" fill="none" width="14" height="14"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                            <span>{{ course.course_student_number || '0' }} 名学生</span>
                        </div>
                    </div>
                    <div class="course-card-arrow">
                        <svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M9 18l6-6-6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                    </div>
                </div>
            </div>
            <div class="empty-state" v-else>暂无课程数据</div>
        </div>

        <!-- Join Course Modal -->
        <a-modal :open="allVisible().student_CoursePractise_joinCourseTable_visible"
            @ok="joinCourseTableOk" @cancel="joinCourseTableCancel" okText="加入" cancelText="取消"
            centered class="join-modal">
            <template #title>
                <div class="modal-title-row">
                    <div class="modal-title-icon">
                        <svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M12 5v14M5 12h14" stroke="white" stroke-width="2" stroke-linecap="round"/></svg>
                    </div>
                    <span>加入课程</span>
                </div>
            </template>
            <a-form class="join-form">
                <a-form-item label="课程邀请码" name="inviteCode">
                    <a-input v-model:value="inviteCode" placeholder="请输入课程邀请码" size="large" />
                </a-form-item>
            </a-form>
        </a-modal>
    </template>

    <!-- ===== Issue List Loading ===== -->
    <template v-if="allVisible().student_CoursePractise_issueList_loading_visible">
        <div class="loading-page">
            <div class="loading-card">
                <div class="loading-anim">
                    <svg xmlns="http://www.w3.org/2000/svg" height="140px" width="140px" viewBox="0 0 200 200" class="pencil">
                        <defs><clipPath id="pencil-eraser-cp"><rect height="30" width="30" ry="5" rx="5"></rect></clipPath></defs>
                        <circle transform="rotate(-113,100,100)" stroke-linecap="round" stroke-dashoffset="439.82" stroke-dasharray="439.82 439.82" stroke-width="2" stroke="currentColor" fill="none" r="70" class="pencil__stroke"></circle>
                        <g transform="translate(100,100)" class="pencil__rotate">
                            <g fill="none">
                                <circle transform="rotate(-90)" stroke-dashoffset="402" stroke-dasharray="402.12 402.12" stroke-width="30" stroke="hsl(174,70%,45%)" r="64" class="pencil__body1"></circle>
                                <circle transform="rotate(-90)" stroke-dashoffset="465" stroke-dasharray="464.96 464.96" stroke-width="10" stroke="hsl(174,70%,55%)" r="74" class="pencil__body2"></circle>
                                <circle transform="rotate(-90)" stroke-dashoffset="339" stroke-dasharray="339.29 339.29" stroke-width="10" stroke="hsl(174,70%,35%)" r="54" class="pencil__body3"></circle>
                            </g>
                            <g transform="rotate(-90) translate(49,0)" class="pencil__eraser"><g class="pencil__eraser-skew"><rect height="30" width="30" ry="5" rx="5" fill="hsl(174,70%,60%)"></rect><rect clip-path="url(#pencil-eraser-cp)" height="30" width="5" fill="hsl(174,70%,50%)"></rect><rect height="20" width="30" fill="hsl(174,10%,90%)"></rect><rect height="20" width="15" fill="hsl(174,10%,70%)"></rect><rect height="20" width="5" fill="hsl(174,10%,80%)"></rect><rect height="2" width="30" y="6" fill="hsla(174,10%,10%,0.2)"></rect><rect height="2" width="30" y="13" fill="hsla(174,10%,10%,0.2)"></rect></g></g>
                            <g transform="rotate(-90) translate(49,-30)" class="pencil__point"><polygon points="15 0,30 30,0 30" fill="hsl(33,90%,70%)"></polygon><polygon points="15 0,6 30,0 30" fill="hsl(33,90%,50%)"></polygon><polygon points="15 0,20 10,10 10" fill="hsl(223,10%,10%)"></polygon></g>
                        </g>
                    </svg>
                </div>
                <h2 class="loading-title">正在获取课程练习...</h2>
                <p class="loading-sub">AI 正在加载该课程的练习列表，请稍候</p>
                <div class="loading-bar-wrap"><div class="loading-bar"></div></div>
            </div>
        </div>
    </template>

    <!-- ===== Issue List View ===== -->
    <template v-if="allVisible().student_CoursePractise_issueList_visible">
        <div class="cp-home">
            <div class="cp-toolbar">
                <button class="btn-back" @click="backToCoursePractiseHome">
                    <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M19 12H5M12 19l-7-7 7-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                    返回课程
                </button>
                <h2 class="cp-title">
                    课程练习
                    <span class="cp-title-sub">{{ selectedCourseName || '' }}</span>
                </h2>
                <div></div>
            </div>

            <div class="issues-grid" v-if="response.length > 0">
                <div v-for="(backData, index) in response" :key="backData.question_id" class="issue-card"
                    @click="toCoursePractiseDetail(backData)">
                    <!-- Status-based header -->
                    <template v-if="isActive(backData)">
                        <div class="issue-header issue-active">
                            <div class="issue-status-dot"></div>
                            <span class="issue-status">进行中</span>
                        </div>
                    </template>
                    <template v-else-if="isUpcoming(backData)">
                        <div class="issue-header issue-upcoming">
                            <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/><path d="M12 6v6l4 2" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
                            <span class="issue-status">未开始</span>
                        </div>
                    </template>
                    <template v-else>
                        <div class="issue-header issue-ended">
                            <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M9 11l3 3L22 4M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                            <span class="issue-status">已结束</span>
                        </div>
                    </template>
                    <div class="issue-body">
                        <h3 class="issue-name">{{ backData.issue_name || backData.course_name + '练习' + (index + 1) }}</h3>
                        <div class="issue-times">
                            <div class="issue-time">
                                <span class="time-label">开始</span>
                                <span class="time-val">{{ formatDate(backData.start_time) }}</span>
                            </div>
                            <div class="issue-divider"></div>
                            <div class="issue-time">
                                <span class="time-label">结束</span>
                                <span class="time-val">{{ formatDate(backData.end_time) }}</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="empty-state" v-else>暂无练习数据</div>
        </div>
    </template>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { getSelfStudentDetailApi } from '../../api/student/getSelfStudentDetailApi';
import { jwtDecode } from 'jwt-decode';
import type { R } from '../../response/R';
import type { student } from '../../modules/student';
import { message } from 'ant-design-vue';
import type { course } from '../../modules/course';
import { allData } from '../../usePinia/allData';
import { allVisible } from '../../usePinia/allVisible';
import { allWait } from '../../usePinia/allWait';
import { getCozeCoursePractiseApi, joinCourseApi } from '../../api/student/CoursePractiseApi';
import router from '../../routers/appRouters';

const token = ref(sessionStorage.getItem('token') || localStorage.getItem('token'))
const student_id = (() => {
    try { return token.value ? jwtDecode(token.value).sub as string : '' }
    catch { return '' }
})()
const studentDetail = ref<student>({
    student_id: 0, student_account: '', student_name: '', student_gender: '',
    student_age: '', student_classList: [], student_major: '', student_class_name: '',
})

const courses = ref<course[]>([])
const getStudentDetail = async () => {
    getSelfStudentDetailApi(student_id).then(res => {
        const R: R = res.data
        if (R.code === 200) {
            studentDetail.value = R.data
            sessionStorage.setItem('studentDetail', JSON.stringify(studentDetail.value))
        } else { message.error('获取学生信息失败') }
    }).catch(err => { message.error('获取学生信息失败'); console.log(err) })
}
const setCourseList = () => {
    studentDetail.value = JSON.parse((sessionStorage.getItem('studentDetail') || localStorage.getItem('studentDetail')) as string)
    studentDetail.value.student_classList?.forEach(course => { courses.value.push(course) })
}

if ((sessionStorage.getItem('studentDetail') || localStorage.getItem('studentDetail'))) { setCourseList() }
else { getStudentDetail(); setCourseList() }

interface backData {
    data: string; question_id: string; issue_name: string;
    course_name: string; start_time: string; end_time: string; teacher_username: string;
}

const request = reactive({ data: allData().student_CoursePractise_course_data, teacher_username: '' });
const response = ref<backData[]>(allData().student_coursePractise_coursePractiseList_data)
const selectedCourseName = ref(allData().student_coursePractiseHome_selectedCourseName_data)

// Safe JSON parse: try direct parse, then try to extract JSON array from markdown/AI response
const safeParseCozeJSON = (raw: string): any[] => {
    // 1) Direct JSON parse
    try { const v = JSON.parse(raw); if (Array.isArray(v)) return v; return []; } catch {}
    // 2) Try to find a JSON array block in the text (AI sometimes wraps JSON in markdown)
    const arrayMatch = raw.match(/\[[\s\S]*\]/);
    if (arrayMatch) {
        try { const v = JSON.parse(arrayMatch[0]); if (Array.isArray(v)) return v; } catch {}
    }
    // 3) Not recoverable
    return [];
};

const toCoursePractiseList = async (course: course) => {
    allVisible().student_CoursePractise_courseList_visible = false;
    allVisible().student_CoursePractise_issueList_loading_visible = true;
    request.data = course.course_name as string;
    request.teacher_username = course.course_teacher_account as string;
    try {
        const R: R = (await getCozeCoursePractiseApi(request)).data;
        const raw = R.data[0]?.studentGetRecentQuestion;
        if (!raw || typeof raw !== 'string') {
            message.error('获取练习列表失败：AI 返回数据为空');
            allVisible().student_CoursePractise_issueList_loading_visible = false;
            allVisible().student_CoursePractise_courseList_visible = true;
            return;
        }
        const parsed = safeParseCozeJSON(raw);
        if (parsed.length === 0) {
            message.error('AI 返回格式异常，请重试或联系管理员');
            console.warn('Coze API returned non-JSON:', raw.substring(0, 200));
            allVisible().student_CoursePractise_issueList_loading_visible = false;
            allVisible().student_CoursePractise_courseList_visible = true;
            return;
        }
        response.value = parsed;
        allData().student_coursePractise_coursePractiseList_data = parsed;
        selectedCourseName.value = course.course_name as string;
        response.value.forEach(item => { allData().setData(item.issue_name, Array(12) as any); });
        allData().student_coursePractiseHome_selectedCourseName_data = course.course_name as string;
        allVisible().student_CoursePractise_issueList_loading_visible = false;
        allVisible().student_CoursePractise_issueList_visible = true;
    } catch (err: any) {
        console.error('CoursePractise API error:', err);
        message.error('网络或服务异常，请稍后重试');
        allVisible().student_CoursePractise_issueList_loading_visible = false;
        allVisible().student_CoursePractise_courseList_visible = true;
    }
}

const backToCoursePractiseHome = () => {
    allVisible().student_CoursePractise_issueList_visible = false;
    allVisible().student_CoursePractise_courseList_visible = true;
}

const updateCurrentTime = () => {
    const now = new Date();
    now.setMinutes(now.getMinutes());
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day}T${hours}:${minutes}`;
};

const isActive = (d: backData) => new Date(d.start_time).getTime() < new Date(updateCurrentTime()).getTime() && new Date(updateCurrentTime()).getTime() < new Date(d.end_time).getTime();
const isUpcoming = (d: backData) => new Date(updateCurrentTime()).getTime() < new Date(d.start_time).getTime();
const formatDate = (s: string) => s ? s.replace('T', ' ') : '-';

const toCoursePractiseDetail = (backData: backData) => {
    if (new Date(updateCurrentTime()).getTime() > new Date(backData.end_time).getTime()) { message.error('该课程练习已结束'); return }
    else if (new Date(updateCurrentTime()).getTime() < new Date(backData.start_time).getTime()) { message.error('该课程练习未开始'); return }
    allData().student_CoursePractise_currentCoursePractiseName_data = backData.issue_name as string;
    allData().student_coursePractise_selectedIssueJSONstring_data = JSON.stringify(backData);
    allVisible().student_CoursePractise_issueList_visible = false;
    allVisible().student_CoursePractise_question_visible = true;
    allWait().student_get_CoursePractise_wait = true;
}

const inviteCode = ref('');
const joinCourseRequser = ref({ student_id: 0, student_username: "", course_invite_code: "" })
const joinCourseTable = () => { allVisible().student_CoursePractise_joinCourseTable_visible = true; }

const joinCourseTableOk = async () => {
    if (inviteCode.value === '' || inviteCode.value === null) { message.error("邀请码不能为空"); return; }
    // 优先从 sessionStorage 读取登录时持久化的 username（标签页隔离），JWT 解码作为备用
    const storedUsername = sessionStorage.getItem('username') || localStorage.getItem('username') || ''
    let currentUsername = storedUsername
    if (!currentUsername) {
        // 备用：实时从 JWT token 解码
        const token = sessionStorage.getItem('token') || localStorage.getItem('token')
        if (token) {
            try { currentUsername = (jwtDecode(token) as any)?.sub || '' } catch {}
        }
    }
    // 最后兜底：从已加载的 studentDetail 中获取
    if (!currentUsername && studentDetail.value?.student_account) {
        currentUsername = studentDetail.value.student_account
    }
    joinCourseRequser.value.course_invite_code = inviteCode.value;
    joinCourseRequser.value.student_id = studentDetail.value.student_id as number;
    joinCourseRequser.value.student_username = currentUsername;
    console.log('joinCourse 发送数据:', JSON.stringify(joinCourseRequser.value));
    const R: R = (await joinCourseApi(joinCourseRequser.value)).data
    console.log('joinCourse 返回结果:', R);
    if (R.code === 200) {
        allVisible().student_CoursePractise_joinCourseTable_visible = false
        message.success("加入课程成功")
        localStorage.removeItem('studentDetail')
        getStudentDetail()
        setTimeout(() => { setCourseList(); location.reload() }, 500)
    } else { message.error("加入课程失败") }
}

const joinCourseTableCancel = () => { allVisible().student_CoursePractise_joinCourseTable_visible = false; }
const backToHome = () => { router.push({ name: 'StudentHomeView' }) }
</script>

<style scoped>
@import '../../style/views/student/CoursePractiseHome/loading.css';

/* ===== Layout ===== */
.cp-home {
    height: 100%;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    max-width: 960px;
    margin: 0 auto;
    padding: 16px 20px 0;
    width: 100%;
}

/* ===== Toolbar ===== */
.cp-toolbar {
    display: flex; align-items: center; justify-content: space-between; gap: 16px;
    margin-bottom: 20px;
    flex-shrink: 0;
}
.cp-title {
    font-size: 1.25rem; font-weight: 800; color: #0F172A; margin: 0;
    display: flex; align-items: center; gap: 10px;
}
.cp-title::before {
    content: '';
    width: 4px; height: 24px;
    background: linear-gradient(180deg, #0D9488, #06B6D4);
    border-radius: 2px;
    display: inline-block;
}
.cp-title-sub {
    font-size: 13px; font-weight: 600; color: #0D9488;
    background: rgba(13,148,136,0.06);
    padding: 4px 14px; border-radius: 8px;
}
.btn-back, .btn-join {
    display: inline-flex; align-items: center; gap: 6px;
    padding: 10px 18px; border-radius: 10px;
    font-size: 13px; font-weight: 600; cursor: pointer;
    transition: all 0.25s; border: 1px solid #E2E8F0;
    background: white; color: #475569;
}
.btn-back:hover, .btn-join:hover { border-color: #0D9488; color: #0D9488; background: rgba(13,148,136,0.03); }
.btn-join { color: #0D9488; border-color: rgba(13,148,136,0.2); background: rgba(13,148,136,0.04); }
.btn-join:hover { background: rgba(13,148,136,0.08); }

/* ===== Course Grid ===== */
.courses-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
    gap: 16px;
    flex: 1;
    overflow-y: auto;
    align-content: start;
    padding-bottom: 20px;
}
.courses-grid::-webkit-scrollbar { width: 5px; }
.courses-grid::-webkit-scrollbar-track { background: transparent; }
.courses-grid::-webkit-scrollbar-thumb { background: #CBD5E1; border-radius: 10px; }
.course-card {
    background: white;
    border-radius: 18px;
    overflow: hidden;
    border: 1px solid #E2E8F0;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 1px 3px rgba(0,0,0,0.03);
    display: flex; flex-direction: column;
}
.course-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 28px rgba(0,0,0,0.08);
    border-color: rgba(13,148,136,0.2);
}
.course-card-bar {
    height: 4px;
    background: linear-gradient(90deg, #0D9488, #14B8A6, #06B6D4);
    transition: height 0.3s;
}
.course-card:hover .course-card-bar { height: 6px; }
.course-card-body { padding: 20px 22px 16px; flex: 1; }
.course-card-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 10px; }
.course-emoji { font-size: 28px; }
.course-id-chip {
    font-size: 10px; font-weight: 700; color: #94A3B8;
    background: #F1F5F9; padding: 3px 10px; border-radius: 8px;
    text-transform: uppercase; letter-spacing: 0.5px;
}
.course-name { font-size: 16px; font-weight: 700; color: #0F172A; margin: 0 0 14px; line-height: 1.3; }
.course-meta { display: flex; flex-direction: column; gap: 7px; margin-bottom: 12px; }
.meta-row { display: flex; align-items: center; gap: 8px; font-size: 13px; color: #64748B; }
.meta-row svg { color: #94A3B8; flex-shrink: 0; }
.course-students {
    display: flex; align-items: center; gap: 8px;
    font-size: 12px; color: #94A3B8;
    padding-top: 10px; border-top: 1px solid #F1F5F9;
}
.course-students svg { flex-shrink: 0; }
.course-card-arrow {
    display: flex; justify-content: flex-end; padding: 0 22px 18px;
    color: #CBD5E1; transition: all 0.3s;
}
.course-card:hover .course-card-arrow { color: #0D9488; transform: translateX(4px); }

/* ===== Issue Grid ===== */
.issues-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 16px;
    flex: 1;
    overflow-y: auto;
    align-content: start;
    padding-bottom: 20px;
}
.issues-grid::-webkit-scrollbar { width: 5px; }
.issues-grid::-webkit-scrollbar-track { background: transparent; }
.issues-grid::-webkit-scrollbar-thumb { background: #CBD5E1; border-radius: 10px; }
.issue-card {
    background: white;
    border-radius: 16px;
    overflow: hidden;
    border: 1px solid #E2E8F0;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 1px 3px rgba(0,0,0,0.03);
}
.issue-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 10px 24px rgba(0,0,0,0.07);
}
.issue-header {
    display: flex; align-items: center; gap: 10px;
    padding: 14px 20px; font-weight: 700; font-size: 13px;
}
.issue-active {
    background: linear-gradient(135deg, rgba(13,148,136,0.08), rgba(6,182,212,0.04));
    color: #0D9488;
}
.issue-upcoming { background: #F8FAFC; color: #64748B; }
.issue-ended { background: #F1F5F9; color: #94A3B8; }
.issue-status-dot {
    width: 8px; height: 8px; border-radius: 50%;
    background: #10B981; box-shadow: 0 0 6px rgba(16,185,129,0.5);
    animation: pulseDot 2s infinite;
}
@keyframes pulseDot {
    0%, 100% { box-shadow: 0 0 6px rgba(16,185,129,0.5); }
    50% { box-shadow: 0 0 12px rgba(16,185,129,0.8); }
}
.issue-body { padding: 20px; }
.issue-name { font-size: 15px; font-weight: 700; color: #0F172A; margin: 0 0 16px; }
.issue-times { display: flex; align-items: center; gap: 0; }
.issue-time { flex: 1; display: flex; flex-direction: column; gap: 2px; }
.time-label { font-size: 11px; color: #94A3B8; font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px; }
.time-val { font-size: 13px; font-weight: 600; color: #475569; }
.issue-divider { width: 1px; height: 32px; background: #E2E8F0; margin: 0 16px; }

/* ===== Join Modal ===== */
.join-form { padding: 8px 0; }
.modal-title-row { display: flex; align-items: center; gap: 12px; font-size: 18px; font-weight: 700; color: #0F172A; }
.modal-title-icon {
    width: 38px; height: 38px; border-radius: 10px;
    background: linear-gradient(135deg, #0D9488, #14B8A6);
    display: flex; align-items: center; justify-content: center;
    box-shadow: 0 3px 10px rgba(13,148,136,0.2);
}

/* ===== Loading Page ===== */
.loading-page {
    display: flex; flex-direction: column;
    align-items: center; justify-content: center;
    min-height: 60vh; padding: 40px 20px;
}
.loading-card { text-align: center; }
.loading-anim { margin-bottom: 12px; }
.loading-title { font-size: 22px; font-weight: 800; color: #0F172A; margin: 0 0 8px; }
.loading-sub { font-size: 14px; color: #94A3B8; margin: 0 0 22px; }
.loading-bar-wrap { width: 260px; height: 4px; background: #E2E8F0; border-radius: 4px; overflow: hidden; margin: 0 auto; }
.loading-bar {
    width: 40%; height: 100%;
    background: linear-gradient(90deg, #0D9488, #14B8A6, #06B6D4);
    border-radius: 4px;
    animation: loadSlide 1.2s ease-in-out infinite;
}
@keyframes loadSlide {
    0% { transform: translateX(-100%); }
    100% { transform: translateX(360%); }
}

/* ===== Empty State ===== */
.empty-state {
    padding: 48px 20px; text-align: center; color: #94A3B8; font-size: 1rem;
    background: white; border-radius: 18px; border: 1px dashed #E2E8F0;
    min-height: 180px; display: flex; align-items: center; justify-content: center;
}

@media (max-width: 768px) {
    .cp-toolbar { flex-wrap: wrap; }
    .courses-grid { grid-template-columns: 1fr; }
    .issues-grid { grid-template-columns: 1fr; }
}
</style>
