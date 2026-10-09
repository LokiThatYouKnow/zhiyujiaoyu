<template>
    <!-- ===== Home (Form Input) ===== -->
    <template v-if="allVisible().student_selfPractise_home_visible">
        <SelfPractiseHome></SelfPractiseHome>
    </template>

    <!-- ===== Generating Loading ===== -->
    <template v-if="allVisible().student_selfPractise_loading_visible">
        <div class="sp-loading-full">
            <div class="sp-load-innner">
                <div class="sp-load-anim">
                    <svg xmlns="http://www.w3.org/2000/svg" height="160px" width="160px" viewBox="0 0 200 200" class="pencil">
                        <defs><clipPath id="pencil-eraser-sp"><rect height="30" width="30" ry="5" rx="5"></rect></clipPath></defs>
                        <circle transform="rotate(-113,100,100)" stroke-linecap="round" stroke-dashoffset="439.82" stroke-dasharray="439.82 439.82" stroke-width="2" stroke="currentColor" fill="none" r="70" class="pencil__stroke"></circle>
                        <g transform="translate(100,100)" class="pencil__rotate">
                            <g fill="none">
                                <circle transform="rotate(-90)" stroke-dashoffset="402" stroke-dasharray="402.12 402.12" stroke-width="30" stroke="hsl(174,70%,45%)" r="64" class="pencil__body1"></circle>
                                <circle transform="rotate(-90)" stroke-dashoffset="465" stroke-dasharray="464.96 464.96" stroke-width="10" stroke="hsl(174,70%,55%)" r="74" class="pencil__body2"></circle>
                                <circle transform="rotate(-90)" stroke-dashoffset="339" stroke-dasharray="339.29 339.29" stroke-width="10" stroke="hsl(174,70%,35%)" r="54" class="pencil__body3"></circle>
                            </g>
                            <g transform="rotate(-90) translate(49,0)" class="pencil__eraser"><g class="pencil__eraser-skew"><rect height="30" width="30" ry="5" rx="5" fill="hsl(174,70%,60%)"></rect><rect clip-path="url(#pencil-eraser-sp)" height="30" width="5" fill="hsl(174,70%,50%)"></rect><rect height="20" width="30" fill="hsl(174,10%,90%)"></rect><rect height="20" width="15" fill="hsl(174,10%,70%)"></rect><rect height="20" width="5" fill="hsl(174,10%,80%)"></rect><rect height="2" width="30" y="6" fill="hsla(174,10%,10%,0.2)"></rect><rect height="2" width="30" y="13" fill="hsla(174,10%,10%,0.2)"></rect></g></g>
                            <g transform="rotate(-90) translate(49,-30)" class="pencil__point"><polygon points="15 0,30 30,0 30" fill="hsl(33,90%,70%)"></polygon><polygon points="15 0,6 30,0 30" fill="hsl(33,90%,50%)"></polygon><polygon points="15 0,20 10,10 10" fill="hsl(223,10%,10%)"></polygon></g>
                        </g>
                    </svg>
                </div>
                <h2 class="sp-load-title">正在生成针对性训练题目...</h2>
                <p class="sp-load-sub">AI 正在根据您的要求和往期错题数据精心出题，请稍候</p>
                <div class="sp-load-bar-wrap"><div class="sp-load-bar"></div></div>
            </div>
        </div>
    </template>

    <!-- ===== Questions View ===== -->
    <template v-if="allVisible().student_selfPractise_question_visible">
        <div class="programming-card">
            <div class="card-header">
                <div class="question-number">
                    <span>{{ currentQuestionIndex + 1 }}</span>
                    {{ questionList[currentQuestionIndex].questionDetail.type }}
                </div>
                <div class="question-meta">第 {{ currentQuestionIndex + 1 }} 题 / 共 {{ questionList.length }} 题</div>
            </div>

            <template v-if="questionList[currentQuestionIndex].questionDetail.type === '选择题'">
                <div class="question-content">
                    <div class="question-title">{{ questionList[currentQuestionIndex].questionDetail.questionInfo }}</div>
                    <div class="question-options">
                        <div class="option" :class="{ 'option-selected': selectedOption === 'A' }" @click="selectOption('A')">
                            <label class="option-label">
                                <span class="option-letter">A</span>
                                <span class="option-text">{{ questionList[currentQuestionIndex].questionDetail.A }}</span>
                            </label>
                        </div>
                        <div class="option" :class="{ 'option-selected': selectedOption === 'B' }" @click="selectOption('B')">
                            <label class="option-label">
                                <span class="option-letter">B</span>
                                <span class="option-text">{{ questionList[currentQuestionIndex].questionDetail.B }}</span>
                            </label>
                        </div>
                        <div class="option" :class="{ 'option-selected': selectedOption === 'C' }" @click="selectOption('C')">
                            <label class="option-label">
                                <span class="option-letter">C</span>
                                <span class="option-text">{{ questionList[currentQuestionIndex].questionDetail.C }}</span>
                            </label>
                        </div>
                        <div class="option" :class="{ 'option-selected': selectedOption === 'D' }" @click="selectOption('D')">
                            <label class="option-label">
                                <span class="option-letter">D</span>
                                <span class="option-text">{{ questionList[currentQuestionIndex].questionDetail.D }}</span>
                            </label>
                        </div>
                    </div>
                </div>
            </template>

            <template v-else>
                <div class="question-content">
                    <div class="question-title">{{ questionList[currentQuestionIndex].questionDetail.questionInfo }}</div>
                    <div class="code-input-area">
                        <span class="code-label">答题区域</span>
                        <textarea v-model="blankAnswer" class="code-input" placeholder="在此输入您的答案..."></textarea>
                    </div>
                </div>
            </template>

            <div class="navigation-buttons">
                <button class="nav-button" @click="up">上一题</button>
                <button class="nav-button nav-reload" @click="reload" style="background: linear-gradient(135deg, #64748B, #475569); box-shadow: 0 4px 14px rgba(100,116,139,0.25);">重新生成</button>
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
    <template v-if="allVisible().student_selfPractise_checkAnswer_visible">
        <a-modal :open="true" @ok="handleOk" @cancel="handleCancel" okText="确定提交" cancelText="继续答题"
            centered class="sp-submit-modal">
            <template #title>
                <div class="sp-modal-head">
                    <div class="sp-modal-icon">
                        <svg viewBox="0 0 24 24" fill="none" width="22" height="22"><path d="M9 11l3 3L22 4M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                    </div>
                    <span>{{ submitCheckAnswerTitle }}</span>
                </div>
            </template>
            <p class="sp-modal-desc">提交后将由 AI 自动批改并给出详细解析</p>
        </a-modal>
    </template>

    <!-- ===== Submitting Loading ===== -->
    <template v-if="allVisible().student_selfPractise_submitAnswer_loading_visible">
        <div class="sp-loading-full">
            <div class="sp-load-innner">
                <h2 class="sp-load-title">正在自动解析答案...</h2>
                <p class="sp-load-sub">AI 正在批改您的答题并生成详细解析，请稍候</p>
                <div class="sp-load-bar-wrap"><div class="sp-load-bar"></div></div>
            </div>
            <div class="scene" style="margin-top: 24px;">
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
    <template v-if="allVisible().student_selfPractise_backAnalyseAnswer_visible">
        <div class="sp-analyse-full">
            <div class="sp-analyse-top">
                <h2 class="sp-analyse-title">题目解析</h2>
                <button class="sp-analyse-back" @click="re">
                    <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M1 4v6h6M23 20v-6h-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/><path d="M20.49 9A9 9 0 0 0 5.64 5.64L1 10m22 4l-4.64 4.36A9 9 0 0 1 3.51 15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                    重新开始
                </button>
            </div>
            <div class="sp-analyse-body">
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
import MK from '../../components/MK.vue';
import { jwtDecode } from 'jwt-decode';
import { getCozeSelfPractiseApi, getCozeselfPractiseCorrectApi } from '../../api/student/getSelfPractiseApi';
import SelfPractiseHome from '../../components/student/SelfPractiseHome.vue';
import { allWait } from '../../usePinia/allWait';
import type { student } from '../../modules/student';

interface question { number: number; questionDetail: questionDetail; }
interface questionDetail { type: string; questionInfo: string; A?: string; B?: string; C?: string; D?: string; }
interface answer { number: number; answer: string; }
interface ProgrammingData { questions: question[]; answers: answer[]; }

const token = ref((sessionStorage.getItem('token') || localStorage.getItem('token')))

const currentQuestionIndex = ref(allData().student_SelfPractise_question_index_data);
const questionList = ref<question[]>(allData().student_SelfPractise_questions_data);
const answerList = ref<answer[]>(allData().student_SelfPractise_answers_data);
const response = ref<ProgrammingData>();
const request = reactive({
    input: allData().student_SelfPractise_request_data,
    data: allData().student_SelfPractise_course_data,
    student_username: jwtDecode(token.value as string).sub,
    teacher_username:'',
});

const studentAnswer = ref<answer[]>(allData().student_SelfPractise_student_answers_data);
const selectedOption = ref('');
const blankAnswer = ref('');

const submitCheckAnswerTitle = ref('')
const backAnalyseAnswer = ref(allData().student_SelfPractise_student_backAnalyseAnswer_data)
const submitAnalyseAnswer = reactive({
    input: '', data: allData().student_SelfPractise_course_data,
    student_username: jwtDecode(token.value as string).sub, teacher_username: '',
})

const rememberWhichQuestion = () => {
    if (allVisible().student_selfPractise_question_visible) {
        selectedOption.value = studentAnswer.value[currentQuestionIndex.value]?.answer ? studentAnswer.value[currentQuestionIndex.value]?.answer : '';
    }
}
rememberWhichQuestion();

const getQuesion = async () => {
    request.input = allData().student_SelfPractise_request_data
    request.data = allData().student_SelfPractise_course_data
    const s: student = JSON.parse((sessionStorage.getItem('studentDetail') || localStorage.getItem('studentDetail')) as string)
    if (s.student_classList)
        for (let i = 0; i < s.student_classList.length; i++) {
            if (s.student_classList[i].course_name === request.data) {
                request.teacher_username = s.student_classList[i].course_teacher_account as string
                continue
            }
        }
    const R: R = (await getCozeSelfPractiseApi(request)).data
    const word = ref(R.data[0].studentsReviewExercises)
    if (word.value === '没有相关的答题记录，请先去答题。') {
        message.error('没有相关的答题记录，请先去答题',4)
        allVisible().student_selfPractise_loading_visible = false;
        allVisible().student_selfPractise_home_visible = true;
        return
    }
    response.value = JSON.parse(R.data[0].studentsReviewExercises);
    if (response.value) {
        questionList.value = response.value.questions;
        answerList.value = response.value.answers;
        allData().student_SelfPractise_questions_data = response.value.questions;
        allData().student_SelfPractise_answers_data = response.value.answers;
        allVisible().student_selfPractise_loading_visible = false;
        allVisible().student_selfPractise_question_visible = true;
        setTimeout(() => { router.push({ name: 'StudentHomeView' }) }, 50);
        setTimeout(() => { router.push({ name: 'SelfPractise' }); message.success("获取问题成功") }, 50);
    } else {
        message.error("获取数据失败")
        router.push('/student/SelfStudentDetail')
    }
};

watch(
    () => allWait().student_get_SelfPractise_wait,
    async (newVal, oldVal) => {
        if (newVal > oldVal) {
            getQuesion();
            allWait().student_get_SelfPractise_wait = false;
        }
    }, { flush: 'sync' }
);

const up = () => {
    if (currentQuestionIndex.value === 0) { message.error('没有上一题了哦', 2); return }
    currentQuestionIndex.value--;
    allData().student_SelfPractise_question_index_data--
    if (questionList.value[currentQuestionIndex.value].questionDetail.type === '选择题') {
        selectOption(studentAnswer.value[currentQuestionIndex.value]?.answer ? studentAnswer.value[currentQuestionIndex.value]?.answer : '')
    } else { blankAnswer.value = studentAnswer.value[currentQuestionIndex.value]?.answer ? studentAnswer.value[currentQuestionIndex.value]?.answer : '' }
}

const down = () => {
    if (currentQuestionIndex.value === questionList.value.length - 1) { message.error('没有下一题了哦', 2); return }
    currentQuestionIndex.value++;
    allData().student_SelfPractise_question_index_data++
    if (questionList.value[currentQuestionIndex.value].questionDetail.type === '选择题') {
        selectOption(studentAnswer.value[currentQuestionIndex.value]?.answer ? studentAnswer.value[currentQuestionIndex.value]?.answer : '')
    } else { blankAnswer.value = studentAnswer.value[currentQuestionIndex.value]?.answer ? studentAnswer.value[currentQuestionIndex.value]?.answer : '' }
}

const reload = () => { location.reload() }

const submitCheckAnswer = () => {
    for (let i = 0; i < studentAnswer.value.length; i++) {
        try {
            if (studentAnswer.value[i].answer === '' || studentAnswer.value[i].answer === null) {
                submitCheckAnswerTitle.value = '还有尚未答完的题目，是否提交?'
                allVisible().student_selfPractise_checkAnswer_visible = true; return
            }
        } catch (e) {
            submitCheckAnswerTitle.value = '还有尚未答完的题目，是否提交?'
            allVisible().student_selfPractise_checkAnswer_visible = true; return
        }
    }
    submitCheckAnswerTitle.value = '已经完成答题，是否提交?'
    allVisible().student_selfPractise_checkAnswer_visible = true;
}

const handleOk = async () => {
    allVisible().student_selfPractise_checkAnswer_visible = false;
    allVisible().student_selfPractise_question_visible = false;
    allVisible().student_selfPractise_submitAnswer_loading_visible = true;
    submitAnalyseAnswer.input = JSON.stringify(studentAnswer.value);
    submitAnalyseAnswer.data = allData().student_SelfPractise_course_data
    const s: student = JSON.parse((sessionStorage.getItem('studentDetail') || localStorage.getItem('studentDetail')) as string)
    if (s.student_classList)
        for (let i = 0; i < s.student_classList.length; i++) {
            if (s.student_classList[i].course_name === submitAnalyseAnswer.data) {
                submitAnalyseAnswer.teacher_username = s.student_classList[i].course_teacher_account as string
                continue
            }
        }
    const R: R = (await getCozeselfPractiseCorrectApi(submitAnalyseAnswer)).data
    if (R.code === 200) {
        backAnalyseAnswer.value = R.data[0].studentCorrectErrors;
        allData().student_SelfPractise_student_backAnalyseAnswer_data = R.data[0].studentCorrectErrors;
        allVisible().student_selfPractise_submitAnswer_loading_visible = false;
        allVisible().student_selfPractise_backAnalyseAnswer_visible = true;
        setTimeout(() => { router.push({ name: 'StudentHomeView' }) }, 100);
        setTimeout(() => { router.push({ name: 'SelfPractise' }); message.success("获取成功") }, 100);
    } else {
        message.error("获取失败")
        router.push('/student/SelfStudentDetail')
    }
}

const handleCancel = () => { allVisible().student_selfPractise_checkAnswer_visible = false; }

const selectOption = (option: string) => {
    if (option === '') { selectedOption.value = option; return }
    selectedOption.value = option;
    studentAnswer.value.splice(currentQuestionIndex.value, 1, { number: currentQuestionIndex.value + 1, answer: selectedOption.value })
    allData().student_SelfPractise_student_answers_data = studentAnswer.value;
}

watch(blankAnswer, (newValue, oldValue) => {
    if (newValue !== oldValue) {
        studentAnswer.value.splice(currentQuestionIndex.value, 1, { number: currentQuestionIndex.value + 1, answer: newValue })
        allData().student_SelfPractise_student_answers_data = studentAnswer.value;
    }
});

const re = () => { location.reload(); }
</script>

<style scoped>
@import '../../style/views/student/SelfPractise/questionCard.css';
@import '../../style/views/student/SelfPractise/loading.css';
@import '../../style/views/student/SelfPractise/getAnswerLoading.css';

/* ===== Full-Screen Loading ===== */
.sp-loading-full {
    height: 100%;
    display: flex; flex-direction: column;
    align-items: center; justify-content: center;
    padding: 40px 20px;
    background: linear-gradient(180deg, #F0FDF9 0%, #FFFFFF 100%);
    overflow: hidden;
}
.sp-load-innner { text-align: center; }
.sp-load-anim { margin-bottom: 20px; }
.sp-load-title {
    font-size: 26px; font-weight: 800; color: #0F172A;
    margin: 0 0 12px;
    background: linear-gradient(135deg, #0D9488, #14B8A6);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
}
.sp-load-sub { font-size: 15px; color: #94A3B8; margin: 0 0 26px; }
.sp-load-bar-wrap {
    width: 300px; height: 5px;
    background: #E2E8F0;
    border-radius: 5px;
    overflow: hidden;
    margin: 0 auto;
}
.sp-load-bar {
    width: 50%; height: 100%;
    background: linear-gradient(90deg, #0D9488, #14B8A6, #06B6D4);
    border-radius: 5px;
    animation: loadSlide 1.2s ease-in-out infinite;
}
@keyframes loadSlide {
    0% { transform: translateX(-100%); }
    100% { transform: translateX(300%); }
}

/* ===== Full-Screen Analysis ===== */
.sp-analyse-full {
    height: 100%;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    background: linear-gradient(180deg, #F8FAFC 0%, #F1F5F9 100%);
}
.sp-analyse-top {
    background: linear-gradient(135deg, #0D9488 0%, #14B8A6 50%, #06B6D4 100%);
    padding: 18px 28px;
    display: flex; align-items: center; justify-content: space-between;
    box-shadow: 0 4px 20px rgba(13,148,136,0.12);
    flex-shrink: 0;
    z-index: 10;
}
.sp-analyse-title {
    margin: 0; color: white; font-size: 22px; font-weight: 800;
    letter-spacing: 0.5px;
}
.sp-analyse-back {
    display: inline-flex; align-items: center; gap: 8px;
    padding: 10px 22px;
    background: rgba(255,255,255,0.2); backdrop-filter: blur(6px);
    border: 1px solid rgba(255,255,255,0.25); color: white;
    border-radius: 12px; font-size: 14px; font-weight: 700;
    cursor: pointer; transition: all 0.25s;
}
.sp-analyse-back:hover { background: rgba(255,255,255,0.3); }
.sp-analyse-body {
    flex: 1;
    overflow-y: auto;
    max-width: 1000px;
    width: 100%;
    margin: 0 auto;
    padding: 28px 32px 40px;
}
.sp-analyse-body::-webkit-scrollbar { width: 6px; }
.sp-analyse-body::-webkit-scrollbar-track { background: transparent; }
.sp-analyse-body::-webkit-scrollbar-thumb { background: #CBD5E1; border-radius: 10px; }

/* ===== Submit Modal ===== */
.sp-modal-head {
    display: flex; align-items: center; gap: 12px;
    font-size: 18px; font-weight: 700; color: #0F172A;
}
.sp-modal-icon {
    width: 42px; height: 42px;
    border-radius: 12px;
    background: linear-gradient(135deg, #F59E0B, #D97706);
    display: flex; align-items: center; justify-content: center;
    box-shadow: 0 4px 14px rgba(245,158,11,0.3);
}
.sp-modal-desc { margin: 8px 0 0; font-size: 14px; color: #94A3B8; }

@media (max-width: 768px) {
    .sp-analyse-top { padding: 16px 20px; }
    .sp-analyse-body { padding: 20px 16px 40px; }
    .sp-analyse-title { font-size: 18px; }
}
</style>
