<template>
    <!-- ===== Home (Course/Issue Lists) ===== -->
    <template v-if="allVisible().student_CoursePractise_home_visible">
        <CoursePractiseHome></CoursePractiseHome>
    </template>

    <!-- ===== Question Card View ===== -->
    <template v-if="allVisible().student_CoursePractise_question_visible">
        <div class="programming-card">
            <div class="card-header">
                <div class="question-number">
                    <button class="btn-back-list" @click="backToCoursePractiseList" title="返回练习列表">
                        <svg viewBox="0 0 24 24" fill="none" width="22" height="22"><path d="M19 12H5M12 19l-7-7 7-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                    </button>
                    <span>{{ currentQuestionIndex + 1 }}</span>
                    {{ questionList[currentQuestionIndex]?.questionDetail.type }}
                </div>
                <div class="question-meta">第 {{ currentQuestionIndex + 1 }} 题 / 共 {{ questionList.length }} 题</div>
            </div>

            <template v-if="questionList[currentQuestionIndex]?.questionDetail.type === '选择题'">
                <div class="question-content">
                    <div class="question-title">{{ questionList[currentQuestionIndex]?.questionDetail.questionInfo }}</div>
                    <div class="question-options">
                        <div class="option" :class="{ 'option-selected': selectedOption === 'A' }" @click="selectOption('A')">
                            <label class="option-label">
                                <span class="option-letter">A</span>
                                <span class="option-text">{{ questionList[currentQuestionIndex]?.questionDetail.A }}</span>
                            </label>
                        </div>
                        <div class="option" :class="{ 'option-selected': selectedOption === 'B' }" @click="selectOption('B')">
                            <label class="option-label">
                                <span class="option-letter">B</span>
                                <span class="option-text">{{ questionList[currentQuestionIndex]?.questionDetail.B }}</span>
                            </label>
                        </div>
                        <div class="option" :class="{ 'option-selected': selectedOption === 'C' }" @click="selectOption('C')">
                            <label class="option-label">
                                <span class="option-letter">C</span>
                                <span class="option-text">{{ questionList[currentQuestionIndex]?.questionDetail.C }}</span>
                            </label>
                        </div>
                        <div class="option" :class="{ 'option-selected': selectedOption === 'D' }" @click="selectOption('D')">
                            <label class="option-label">
                                <span class="option-letter">D</span>
                                <span class="option-text">{{ questionList[currentQuestionIndex]?.questionDetail.D }}</span>
                            </label>
                        </div>
                    </div>
                </div>
            </template>

            <template v-else>
                <div class="question-content">
                    <div class="question-title">{{ questionList[currentQuestionIndex]?.questionDetail.questionInfo }}</div>
                    <div class="code-input-area">
                        <span class="code-label">答题区域</span>
                        <textarea v-model="blankAnswer" class="code-input" placeholder="在此输入您的答案..."></textarea>
                    </div>
                </div>
            </template>

            <div class="navigation-buttons">
                <button class="nav-button" @click="up">上一题</button>
                <template v-if="currentQuestionIndex === questionList.length - 1">
                    <button class="nav-button nav-submit" @click="submitCheckAnswer" style="background: linear-gradient(135deg, #F59E0B, #D97706); box-shadow: 0 4px 14px rgba(245,158,11,0.3);">提交</button>
                </template>
                <template v-else>
                    <button class="nav-button" @click="down">下一题</button>
                </template>
            </div>
        </div>
    </template>

    <!-- ===== Submit Confirmation Modal ===== -->
    <template v-if="allVisible().student_CoursePractise_checkAnswer_visible">
        <a-modal :open="true" @ok="handleOk" @cancel="handleCancel" okText="确定提交" cancelText="继续答题"
            centered class="cp-submit-modal">
            <template #title>
                <div class="cp-modal-head">
                    <div class="cp-modal-icon">
                        <svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M9 11l3 3L22 4M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                    </div>
                    <span>{{ submitCheckAnswerTitle }}</span>
                </div>
            </template>
            <p class="cp-modal-desc">提交后将由 AI 自动批改并给出详细对比解析</p>
        </a-modal>
    </template>

    <!-- ===== Submitting Loading ===== -->
    <template v-if="allVisible().student_CoursePractise_submitAnswer_loading_visible">
        <div class="cp-loading-full">
            <div class="cp-load-innner">
                <h2 class="cp-load-title">正在自动解析答案...</h2>
                <p class="cp-load-sub">AI 正在批改您的答题并与标准答案进行对比分析</p>
                <div class="cp-load-bar-wrap"><div class="cp-load-bar"></div></div>
            </div>
            <div class="scene" style="margin-top: 20px;">
                <div class="forest">
                    <div class="tree tree1"><div class="branch branch-top"></div><div class="branch branch-middle"></div></div>
                    <div class="tree tree2"><div class="branch branch-top"></div><div class="branch branch-middle"></div><div class="branch branch-bottom"></div></div>
                    <div class="tree tree3"><div class="branch branch-top"></div><div class="branch branch-middle"></div><div class="branch branch-bottom"></div></div>
                    <div class="tree tree4"><div class="branch branch-top"></div><div class="branch branch-middle"></div><div class="branch branch-bottom"></div></div>
                    <div class="tree tree5"><div class="branch branch-top"></div><div class="branch branch-middle"></div><div class="branch branch-bottom"></div></div>
                    <div class="tree tree6"><div class="branch branch-top"></div><div class="branch branch-middle"></div><div class="branch branch-bottom"></div></div>
                    <div class="tree tree7"><div class="branch branch-top"></div><div class="branch branch-middle"></div><div class="branch branch-bottom"></div></div>
                </div>
                <div class="tent"><div class="roof"></div><div class="roof-border-left"><div class="roof-border roof-border1"></div><div class="roof-border roof-border2"></div><div class="roof-border roof-border3"></div></div><div class="entrance"><div class="door left-door"><div class="left-door-inner"></div></div><div class="door right-door"><div class="right-door-inner"></div></div></div></div>
                <div class="floor"><div class="ground ground1"></div><div class="ground ground2"></div></div>
                <div class="fireplace"><div class="support"></div><div class="support"></div><div class="bar"></div><div class="hanger"></div><div class="smoke"></div><div class="pan"></div><div class="fire"><div class="line line1"><div class="particle particle1"></div><div class="particle particle2"></div><div class="particle particle3"></div><div class="particle particle4"></div></div><div class="line line2"><div class="particle particle1"></div><div class="particle particle2"></div><div class="particle particle3"></div><div class="particle particle4"></div></div><div class="line line3"><div class="particle particle1"></div><div class="particle particle2"></div><div class="particle particle3"></div><div class="particle particle4"></div></div></div></div>
                <div class="time-wrapper"><div class="time"><div class="day"></div><div class="night"><div class="moon"></div><div class="star star1 star-big"></div><div class="star star2 star-big"></div><div class="star star3 star-big"></div><div class="star star4"></div><div class="star star5"></div><div class="star star6"></div><div class="star star7"></div></div></div></div>
            </div>
        </div>
    </template>

    <!-- ===== Answer Analysis View ===== -->
    <template v-if="allVisible().student_CoursePractise_backAnalyseAnswer_visible">
        <div class="cp-analyse-full">
            <div class="cp-analyse-top">
                <h2 class="cp-analyse-title">题目解析</h2>
                <button class="cp-analyse-back" @click="re">
                    <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M1 4v6h6M23 20v-6h-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/><path d="M20.49 9A9 9 0 0 0 5.64 5.64L1 10m22 4l-4.64 4.36A9 9 0 0 1 3.51 15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                    重新开始
                </button>
            </div>
            <div class="cp-analyse-body">
                <MK :markdownContent="backAnalyseAnswer" title="题目解析:"></MK>
            </div>
        </div>
    </template>
</template>

<script lang="ts" setup>
import { reactive, ref, watch } from 'vue';
import { allData } from '../../usePinia/allData';
import type { R } from '../../response/R';
import { allVisible } from '../../usePinia/allVisible';
import { message } from 'ant-design-vue';
import router from '../../routers/appRouters';
import { getCozeCoursePractiseCorrectAnswerApi } from '../../api/student/CoursePractiseApi';
import MK from '../../components/MK.vue';
import { jwtDecode } from 'jwt-decode';
import { allWait } from '../../usePinia/allWait';

interface backData {
    data: string; question_id: string; issue_name: string;
    course_name: string; start_time: string; end_time: string; teacher_username: string;
}

interface ProgrammingData { questions: question[]; answers: answer[]; }

interface question { number: number; questionDetail: questionDetail; }
interface questionDetail { type: string; questionInfo: string; A?: string; B?: string; C?: string; D?: string; }
interface answer { number: number; answer: string; }

const currentQuestionIndex = ref(allData().student_CoursePractise_question_index_data);
const questionList = ref<question[]>(allData().student_CoursePractise_questions_data);
const answerList = ref<answer[]>(allData().student_CoursePractise_answers_data);
const programmingData = ref<ProgrammingData>();
const all = ref<backData>();

const studentAllCourseAnswer = ref(allData().student_CoursePractise_student_coursePractiseList_data)
const studentOneCourseAnswer = ref<answer[]>(Array(12) as any);
const selectedOption = ref('');
const blankAnswer = ref('');

const submitCheckAnswerTitle = ref('')
const backAnalyseAnswer = ref(allData().student_CoursePractise_student_backAnalyseAnswer_data)
const token = ref((sessionStorage.getItem('token') || localStorage.getItem('token')))
const submitAnalyseAnswer = reactive({
    input: '', data: allData().student_CoursePractise_course_data,
    student_username: jwtDecode(token.value as string).sub,
    teacher_username: '', question_id: '',
})

watch(
    () => allWait().student_get_CoursePractise_wait,
    async (newVal, oldVal) => {
        if (newVal > oldVal) {
            all.value = JSON.parse(allData().student_coursePractise_selectedIssueJSONstring_data)
            programmingData.value = JSON.parse(all.value?.data as string)
            answerList.value = programmingData.value?.answers as answer[];
            allData().student_CoursePractise_answers_data = answerList.value;
            questionList.value = programmingData.value?.questions as question[];
            allData().student_CoursePractise_questions_data = questionList.value;
            allData().student_CoursePractise_questionId_data = all.value?.question_id as string;
            allData().student_CoursePractise_course_data = all.value?.course_name as string;
            allData().student_CoursePractise_teacherUsername_data = all.value?.teacher_username as string;
            studentOneCourseAnswer.value = allData().getData(allData().student_CoursePractise_currentCoursePractiseName_data as string)
            currentQuestionIndex.value = 0;
            if (questionList.value[currentQuestionIndex.value].questionDetail.type === '选择题') {
                selectOption(studentOneCourseAnswer.value[currentQuestionIndex.value]?.answer ? studentOneCourseAnswer.value[currentQuestionIndex.value]?.answer : '')
            } else { blankAnswer.value = studentOneCourseAnswer.value[currentQuestionIndex.value]?.answer ? studentOneCourseAnswer.value[currentQuestionIndex.value]?.answer : '' }
            allWait().student_get_CoursePractise_wait = false;
        }
    }, { flush: 'sync' }
);

const backToCoursePractiseList = () => {
    allVisible().student_CoursePractise_question_visible = false;
    allVisible().student_CoursePractise_issueList_visible = true;
}

const up = () => {
    if (currentQuestionIndex.value === 0) { message.error('没有上一题了哦', 2); return }
    currentQuestionIndex.value--;
    allData().student_CoursePractise_question_index_data--
    if (questionList.value[currentQuestionIndex.value].questionDetail.type === '选择题') {
        selectOption(studentOneCourseAnswer.value[currentQuestionIndex.value]?.answer ? studentOneCourseAnswer.value[currentQuestionIndex.value]?.answer : '')
    } else { blankAnswer.value = studentOneCourseAnswer.value[currentQuestionIndex.value]?.answer ? studentOneCourseAnswer.value[currentQuestionIndex.value]?.answer : '' }
}

const down = () => {
    if (currentQuestionIndex.value === questionList.value.length - 1) { message.error('没有下一题了哦', 2); return }
    currentQuestionIndex.value++;
    allData().student_CoursePractise_question_index_data++
    if (questionList.value[currentQuestionIndex.value].questionDetail.type === '选择题') {
        selectOption(studentOneCourseAnswer.value[currentQuestionIndex.value]?.answer ? studentOneCourseAnswer.value[currentQuestionIndex.value]?.answer : '')
    } else { blankAnswer.value = studentOneCourseAnswer.value[currentQuestionIndex.value]?.answer ? studentOneCourseAnswer.value[currentQuestionIndex.value]?.answer : '' }
}

const submitCheckAnswer = () => {
    for (let i = 0; i < studentOneCourseAnswer.value.length; i++) {
        try {
            if (studentOneCourseAnswer.value[i].answer === '' || studentOneCourseAnswer.value[i].answer === null) {
                submitCheckAnswerTitle.value = '还有尚未答完的题目，是否提交?'
                allVisible().student_CoursePractise_checkAnswer_visible = true; return
            }
        } catch (e) {
            submitCheckAnswerTitle.value = '还有尚未答完的题目，是否提交?'
            allVisible().student_CoursePractise_checkAnswer_visible = true; return
        }
    }
    submitCheckAnswerTitle.value = '已经完成答题，是否提交?'
    allVisible().student_CoursePractise_checkAnswer_visible = true;
}

const handleOk = async () => {
    allVisible().student_CoursePractise_checkAnswer_visible = false;
    allVisible().student_CoursePractise_question_visible = false;
    allVisible().student_CoursePractise_submitAnswer_loading_visible = true;
    submitAnalyseAnswer.input = JSON.stringify(studentOneCourseAnswer.value);
    submitAnalyseAnswer.data = allData().student_CoursePractise_course_data
    submitAnalyseAnswer.question_id = allData().student_CoursePractise_questionId_data;
    submitAnalyseAnswer.teacher_username = allData().student_CoursePractise_teacherUsername_data;
    const R: R = (await getCozeCoursePractiseCorrectAnswerApi(submitAnalyseAnswer)).data
    if (R.code === 200) {
        backAnalyseAnswer.value = R.data[0].compareAnswer;
        allData().student_CoursePractise_student_backAnalyseAnswer_data = R.data[0].compareAnswer;
        allVisible().student_CoursePractise_submitAnswer_loading_visible = false;
        allVisible().student_CoursePractise_backAnalyseAnswer_visible = true;
        setTimeout(() => { router.push({ name: 'StudentHomeView' }) }, 100)
        setTimeout(() => { router.push({ name: 'CoursePractise' }); message.success('获取成功') }, 100)
    } else { message.error("获取失败"); router.push('/student/SelfStudentDetail') }
}

const handleCancel = () => { allVisible().student_CoursePractise_checkAnswer_visible = false; }

const selectOption = (option: string) => {
    if (option === '') { selectedOption.value = option; return }
    selectedOption.value = option;
    studentOneCourseAnswer.value.splice(currentQuestionIndex.value, 1, { number: currentQuestionIndex.value + 1, answer: selectedOption.value })
    studentAllCourseAnswer.value.set(allData().student_CoursePractise_currentCoursePractiseName_data, studentOneCourseAnswer.value)
    allData().setData(allData().student_CoursePractise_currentCoursePractiseName_data, studentOneCourseAnswer.value)
}

watch(blankAnswer, (newValue, oldValue) => {
    if (newValue !== oldValue) {
        studentOneCourseAnswer.value.splice(currentQuestionIndex.value, 1, { number: currentQuestionIndex.value + 1, answer: newValue })
        studentAllCourseAnswer.value.set(allData().student_CoursePractise_currentCoursePractiseName_data, studentOneCourseAnswer.value)
        allData().setData(allData().student_CoursePractise_currentCoursePractiseName_data, studentOneCourseAnswer.value)
    }
});

const rememberWhichQuestion = () => {
    if (allVisible().student_CoursePractise_question_visible) {
        studentOneCourseAnswer.value = allData().getData(allData().student_CoursePractise_currentCoursePractiseName_data as string);
        currentQuestionIndex.value = allData().student_CoursePractise_question_index_data;
        if (questionList.value[currentQuestionIndex.value].questionDetail.type === '选择题') {
            selectOption(studentOneCourseAnswer.value[currentQuestionIndex.value]?.answer ? studentOneCourseAnswer.value[currentQuestionIndex.value]?.answer : '')
        } else { blankAnswer.value = studentOneCourseAnswer.value[currentQuestionIndex.value]?.answer ? studentOneCourseAnswer.value[currentQuestionIndex.value]?.answer : '' }
    }
}
rememberWhichQuestion();

const re = () => { location.reload(); }
</script>

<style scoped>
@import '../../style/views/student/CoursePractise/loading.css';
@import '../../style/views/student/CoursePractise/questionCard.css';
@import '../../style/views/student/CoursePractise/getAnswerLoading.css';

/* ===== Back Button in Question Header ===== */
.btn-back-list {
    display: inline-flex; align-items: center; justify-content: center;
    width: 38px; height: 38px;
    background: rgba(255,255,255,0.2);
    border: 1px solid rgba(255,255,255,0.3);
    color: white;
    border-radius: 10px;
    cursor: pointer;
    transition: all 0.25s;
    margin-right: 16px;
}
.btn-back-list:hover { background: rgba(255,255,255,0.35); }

/* ===== Full-Screen Loading ===== */
.cp-loading-full {
    height: 100%;
    display: flex; flex-direction: column;
    align-items: center; justify-content: center;
    padding: 40px 20px;
    background: linear-gradient(180deg, #F0FDF9 0%, #FFFFFF 100%);
    overflow: hidden;
}
.cp-load-innner { text-align: center; }
.cp-load-title {
    font-size: 24px; font-weight: 800; color: #0F172A;
    margin: 0 0 10px;
    background: linear-gradient(135deg, #0D9488, #14B8A6);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
}
.cp-load-sub { font-size: 14px; color: #94A3B8; margin: 0 0 24px; }
.cp-load-bar-wrap {
    width: 280px; height: 5px;
    background: #E2E8F0;
    border-radius: 5px;
    overflow: hidden;
    margin: 0 auto;
}
.cp-load-bar {
    width: 40%; height: 100%;
    background: linear-gradient(90deg, #0D9488, #14B8A6, #06B6D4);
    border-radius: 5px;
    animation: loadSlide 1.2s ease-in-out infinite;
}
@keyframes loadSlide {
    0% { transform: translateX(-100%); }
    100% { transform: translateX(360%); }
}

/* ===== Full-Screen Analysis ===== */
.cp-analyse-full {
    height: 100%;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    background: linear-gradient(180deg, #F8FAFC 0%, #F1F5F9 100%);
}
.cp-analyse-top {
    background: linear-gradient(135deg, #0D9488 0%, #14B8A6 50%, #06B6D4 100%);
    padding: 18px 28px;
    display: flex; align-items: center; justify-content: space-between;
    box-shadow: 0 4px 20px rgba(13,148,136,0.12);
    flex-shrink: 0;
    z-index: 10;
}
.cp-analyse-title {
    margin: 0; color: white; font-size: 22px; font-weight: 800;
    letter-spacing: 0.5px;
}
.cp-analyse-back {
    display: inline-flex; align-items: center; gap: 8px;
    padding: 10px 22px;
    background: rgba(255,255,255,0.2); backdrop-filter: blur(6px);
    border: 1px solid rgba(255,255,255,0.25); color: white;
    border-radius: 12px; font-size: 14px; font-weight: 700;
    cursor: pointer; transition: all 0.25s;
}
.cp-analyse-back:hover { background: rgba(255,255,255,0.3); }
.cp-analyse-body {
    flex: 1;
    overflow-y: auto;
    max-width: 1000px;
    width: 100%;
    margin: 0 auto;
    padding: 28px 32px 40px;
}
.cp-analyse-body::-webkit-scrollbar { width: 6px; }
.cp-analyse-body::-webkit-scrollbar-track { background: transparent; }
.cp-analyse-body::-webkit-scrollbar-thumb { background: #CBD5E1; border-radius: 10px; }

/* ===== Submit Modal ===== */
.cp-modal-head {
    display: flex; align-items: center; gap: 12px;
    font-size: 18px; font-weight: 700; color: #0F172A;
}
.cp-modal-icon {
    width: 42px; height: 42px;
    border-radius: 12px;
    background: linear-gradient(135deg, #F59E0B, #D97706);
    display: flex; align-items: center; justify-content: center;
    box-shadow: 0 4px 14px rgba(245,158,11,0.3);
}
.cp-modal-desc { margin: 8px 0 0; font-size: 14px; color: #94A3B8; }

/* Submit button hover */
.nav-submit:hover {
    background: linear-gradient(135deg, #D97706, #C57002) !important;
    box-shadow: 0 6px 20px rgba(245,158,11,0.4) !important;
}

@media (max-width: 768px) {
    .cp-analyse-top { padding: 16px 20px; }
    .cp-analyse-body { padding: 20px 16px 40px; }
    .cp-analyse-title { font-size: 18px; }
}
</style>
