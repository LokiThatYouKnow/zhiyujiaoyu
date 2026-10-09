<template>

        <template v-if="allVisible().teacher_practiseDesign_home_visible">
            <div style="margin-top: -5%;">
                <PractiseDesignHome />
            </div>
        </template>

        <template v-if="allVisible().teacher_practiseDesign_loading_visible">
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
            <div style="margin-left: 37%;margin-top: 10%">
                <strong style="font-size: 300%;">正在生成实训题目ing...</strong>
            </div>
        </template>

        <template v-if="allVisible().teacher_practiseDesign_MK_visible">
            <div style="overflow-y: auto;height: 100%;">
                <MK :markdownContent="response" :title="'实训项目:'"></MK>
            </div>
            <a-button @click="reload" type="primary" style="margin-left: 80%;width: 70px;">返回</a-button>
        </template>

</template>
<script lang="ts" setup>
import {  reactive, ref, watch } from 'vue';
import { allWait } from '../../usePinia/allWait';
import { allData } from '../../usePinia/allData';
import type { R } from '../../response/R';
import { allVisible } from '../../usePinia/allVisible';
import {  getCozePractiseDesignApi } from '../../api/teacher/getPractiseDesignApi';
import { message } from 'ant-design-vue';
import router from '../../routers/appRouters';
import { jwtDecode } from 'jwt-decode';

const token = ref((sessionStorage.getItem('token') || localStorage.getItem('token')))
const teacher_username = jwtDecode(token.value as string).sub as string

const response = ref(allData().teacher_practiseDesign_data)
const request = reactive({
    data: allData().teacher_practiseDesign_knowledgePoint_data,
    teacher_username:'',
    resource_name: allData().teacher_practiseDesign_resourceName_data,
    input: allData().teacher_practiseDesign_request_data,
    start_time: allData().teacher_practiseDesign_createTime_data,
});

// 使用watch监听特定变化
watch(
    () => allWait().teacher_get_practiseDesign_wait,
    async (newVal, oldVal) => {
        if (newVal > oldVal) {
            request.data = allData().teacher_practiseDesign_knowledgePoint_data
            request.resource_name = allData().teacher_practiseDesign_resourceName_data
            request.input = allData().teacher_practiseDesign_request_data
            request.start_time = allData().teacher_practiseDesign_createTime_data
            request.teacher_username = teacher_username
            const R: R = (await getCozePractiseDesignApi(request)).data
            response.value = R.data[0].practise
            allData().teacher_practiseDesign_data = R.data[0].practise
            if (response.value) {
                allVisible().teacher_practiseDesign_loading_visible = false;
                allVisible().teacher_practiseDesign_MK_visible = true;
                allWait().teacher_get_practiseDesign_wait = false;
                setTimeout(() => {
                    router.push({ name: 'TeacherHomeView' })
                }, 10);
                setTimeout(() => {
                    router.push({ name: 'PractiseDesign' })
                    message.success('实训项目生成成功，已经保存到课件资源', 3);
                }, 10);
            }else{
                message.error("获取数据失败")
                router.push({ name:'TeacherHomeView'})
            }
        }
    },
    { flush: 'sync' } // 同步模式，修改值不会触发新的回调
);

const reload = () => {
    location.reload();
}
</script>

<style scoped>
@import '../../style/views/teacher/PractiseDesign/loading.css';
@import '../../style/views/teacher/PractiseDesign/questionCard.css';
</style>
