<template>
    <template v-if="allVisible().teacher_designIssue_home_visible">
        <DesignIssueHome />
    </template>

    <template v-else-if="allVisible().teacher_designIssue_loading_visible">
        <div style="margin-left: 40%;margin-top: 10%;">
            <figure>
                <div style="--i: 1"></div>
                <div style="--i: 2"></div>
                <div style="--i: 3"></div>
                <div style="--i: 4"></div>
                <div style="--i: 5"></div>
                <div style="--i: 6"></div>
                <div style="--i: 7"></div>
                <div style="--i: 8"></div>
                <div style="--i: 9"></div>
                <div style="--i: 10"></div>
                <div style="--i: 11"></div>
                <div style="--i: 12"></div>
            </figure>
        </div>
        <div style="margin-left: 40%;margin-top: 12%">
            <strong style="font-size: 300%;">正在生成题目ing...</strong>
        </div>
    </template>

    <template v-else-if="allVisible().teacher_designIssue_resultMK_visible">
        <div style="overflow-y: auto;height: 100%;">
            <MK :markdownContent="content" title="题目列表:"></MK>
        </div>
        <a-button @click="re" type="primary" style="margin-left: 80%;">返回</a-button>
    </template>

</template>

<script lang="ts" setup>
import { reactive, ref, watch } from 'vue';
import { allData } from '../../usePinia/allData';
import { allVisible } from '../../usePinia/allVisible';
import { allWait } from '../../usePinia/allWait';
import type { R } from '../../response/R';
import { getCozeDesignIssueApi } from '../../api/teacher/getDesignIssueApi';
import { message } from 'ant-design-vue';
import type { teacher } from '../../modules/teacher';
import router from '../../routers/appRouters';


const content = ref(allData().teacher_designIssue_data);
const request = reactive({
    data:'', //这里的data是对应coze的data=知识点
    teacher_username: '',
    issue_name:'',
    input: '',
    start_time: '',
    end_time: '',
});

// 使用watch监听特定变化
watch(
    () => allWait().teacher_get_designIssue_wait,
    async (newVal, oldVal) => {
        if (newVal > oldVal) {
            request.data = allData().teacher_designIssue_knowledgePoint_data;
            request.issue_name = allData().teacher_designIssue_issueName_data;
            request.input = allData().teacher_designIssue_teacherRequest_data;
            request.start_time = allData().teacher_designIssue_stratTime_data;
            request.end_time = allData().teacher_designIssue_endTime_data;
            const t: teacher = JSON.parse((sessionStorage.getItem('teacherDetail') || localStorage.getItem('teacherDetail')) as string)
            request.teacher_username = t.teacher_account as string;
            const R: R = (await getCozeDesignIssueApi(request)).data
            if(R.code === 200) { 
                content.value = R.data[0].decideAndAnswer;
                allData().teacher_designIssue_data = R.data[0].decideAndAnswer;
                allVisible().teacher_designIssue_loading_visible = false;
                allVisible().teacher_designIssue_resultMK_visible = true;
                setTimeout(() => {
                    router.push({ name: 'TeacherHomeView' })
                }, 10);
                setTimeout(() => {
                    router.push({ name: 'DesignIssue' })
                    message.success('题目已同步推送到学生端,也已保存在对应课程的练习列表中', 8);
                }, 10);
            }else{
                message.error(R.msg);
                allVisible().teacher_designIssue_loading_visible = false;
                allVisible().teacher_designIssue_home_visible = true;
            }
            allWait().teacher_get_designIssue_wait = false;
        }
    },
    { flush: 'sync' } // 同步模式，修改值不会触发新的回调
);

const re = () => {
    location.reload();
}
</script>

<style scoped>
@import "../../style/views/teacher/ArrangeTime/loading.css";
</style>