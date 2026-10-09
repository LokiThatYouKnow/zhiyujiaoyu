<template>
    <template v-if="allVisible().teacher_KnowledgeOrganization_home_visible">
        <div style="margin-top: -5%;">
            <KnowledgeOrganizationHome />
        </div>
    </template>
    <template v-if="allVisible().teacher_KnowledgeOrganization_loading_visible">
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
        <div style="margin-left: 35%;margin-top: 12%">
            <strong style="font-size: 300%;">正在总结相关知识点ing...</strong>
        </div>
    </template>
    <template
        v-if="allData().teacher_KnowledgeOrganization_knowledgePoint_data !== '' && allData().teacher_KnowledgeOrganization_data !== ''">
        <div style="overflow-y: auto;height: 100%;">
            <MK :markdownContent="concludeKnowledgePoint" title="总结:"></MK>
        </div>
        <a-button @click="reConclude" type="primary" style="margin-left: 80%;">返回</a-button>
    </template>
</template>
<script lang="ts" setup>
import { reactive, ref, watch } from 'vue';
import { allWait } from '../../usePinia/allWait';
import { allData } from '../../usePinia/allData';
import type { R } from '../../response/R';
import { allVisible } from '../../usePinia/allVisible';
import { getCozeKnowledgeOrganizationApi } from '../../api/teacher/getKnowledgeOrganizationApi';
import type { teacher } from '../../modules/teacher';
import router from '../../routers/appRouters';
import { message } from 'ant-design-vue';

const concludeKnowledgePoint = ref(allData().teacher_KnowledgeOrganization_data);
const request = reactive({
    data: allData().teacher_KnowledgeOrganization_data,
    teacher_username: '',
    resource_name: allData().teacher_KnowledgeOrganization_resourceName_data,
    input: allData().teacher_KnowledgeOrganization_request_data,
    start_time: allData().teacher_KnowledgeOrganization_createTime_data,
});

// 使用watch监听特定变化
watch(
    () => allWait().teacher_get_konwledgeOrganization_wait,
    async (newVal, oldVal) => {
        if (newVal > oldVal) {
            request.data = allData().teacher_KnowledgeOrganization_knowledgePoint_data
            request.resource_name = allData().teacher_KnowledgeOrganization_resourceName_data
            request.start_time = allData().teacher_KnowledgeOrganization_createTime_data
            request.input = allData().teacher_KnowledgeOrganization_request_data
            const t:teacher = JSON.parse((sessionStorage.getItem('teacherDetail') || localStorage.getItem('teacherDetail')) as string)
            request.teacher_username = t.teacher_account as string;
            const R: R = (await getCozeKnowledgeOrganizationApi(request)).data
            concludeKnowledgePoint.value = R.data[0].concludeData;
            allData().teacher_KnowledgeOrganization_data = R.data[0].concludeData;
            allVisible().teacher_KnowledgeOrganization_loading_visible = false;   
            allWait().teacher_get_konwledgeOrganization_wait = false;
            setTimeout(() => { 
                router.push({ name: 'TeacherHomeView' })
            }, 50);
            setTimeout(() => {
                router.push({ name: 'KnowledgeOrganization' })
                message.success("课件生成成功，已经保存到课件资源")
            }, 100);
        }
    },
    { flush: 'sync' } // 同步模式，修改值不会触发新的回调
);

const reConclude = () => {
    location.reload();
}
</script>

<style scoped>
@import '../../style/views/teacher/KnowledgeOrganization/loading.css';

.stage {
    margin-bottom: 25px;
    padding: 15px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.stage ul {
    list-style-type: none;
    /* 去掉默认列表符号 */
    padding: 0;
    /* 去掉左侧内边距 */
    margin: 0;
}

.stage li {
    margin-bottom: 15px;
    /* 加大知识点间间距 */
    line-height: 1.6;
    padding: 15px;
    /* 给每个知识点添加内边距 */
    border: 1px solid #e0e0e0;
    /* 添加边框 */
    border-radius: 8px;
    /* 边框圆角 */
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    /* 小阴影增强卡片感 */
}

.stage strong {
    color: #2c3e50;
    font-weight: 600;
}
</style>
