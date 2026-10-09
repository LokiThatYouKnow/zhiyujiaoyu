<template>
    <template v-if="allVisible().teacher_arrangeTime_home_visible">
        <ArrangeTimeHome />
    </template>

    <template v-else-if="allVisible().teacher_arrangeTime_loading_visible">
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
        <div style="margin-left: 34%;margin-top: 12%">
            <strong style="font-size: 300%;">正在生成教学时间安排ing...</strong>
        </div>
    </template>

    <template v-else-if="allData().teacher_arrangeTime_data !== ''">
        <div style="overflow-y: auto;height: 100%;">
            <MK :markdownContent="content" title="教学时间安排:"></MK>
        </div>
        <a-button @click="re" type="primary" style="margin-left: 80%;">返回</a-button>
    </template>

</template>

<script lang="ts" setup>
import { reactive, ref, watch } from 'vue';
import { allData } from '../../usePinia/allData';
import { allVisible } from '../../usePinia/allVisible';
import { allWait } from '../../usePinia/allWait';
import { getCozeArrangeTimeApi } from '../../api/teacher/getArrangeTimeApi';
import type { R } from '../../response/R';
import type { teacher } from '../../modules/teacher';


const content = ref(allData().teacher_arrangeTime_data);
const request = reactive({
    data: allData().teacher_arrangeTime_course_data,
    teacher_username: '',
});

if (content.value === '') {
    allVisible().teacher_arrangeTime_home_visible = true;
}

// 使用watch监听特定变化
watch(
    () => allWait().teacher_get_arrangeTime_wait,
    async (newVal, oldVal) => {
        if (newVal > oldVal) {
            request.data = allData().teacher_arrangeTime_course_data;
            const t: teacher = JSON.parse((sessionStorage.getItem('teacherDetail') || localStorage.getItem('teacherDetail')) as string)
            request.teacher_username = t.teacher_account as string;
            const R:R = (await getCozeArrangeTimeApi(request)).data
            content.value = R.data[0].arrangeTime;
            allData().teacher_arrangeTime_data = R.data[0].arrangeTime;
            allVisible().teacher_arrangeTime_loading_visible = false;
            allWait().teacher_get_arrangeTime_wait = false;
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
