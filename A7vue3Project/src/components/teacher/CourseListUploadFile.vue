<template>
    <div class="file-upload-card">
        <div class="upload-header">
            <h3>上传课程知识库文件</h3>
            <p class="upload-tip">目前仅支持上传 txt，pdf，doc，docx 文件，确保你上传的文件是知识库内容文件且暂时不支持知识库内容文件内内包含视频，图片等元素,如若执意上传，可能会发生错误</p>
            <p class="upload-tip">如若不上传知识库文件，则由AI自动生成相关知识库</p>
        </div>

        <div class="upload-area">
            <label for="file-input" class="upload-btn">
                <i class="fas fa-cloud-upload-alt"></i> 选择文件
                <input type="file" @change="handleFileChange" ref="fileInput" id="file-input"
                    class="file-input-hidden" />
            </label>

            <div v-if="fileName" class="file-info">
                <span class="file-name">{{ fileName }}</span>
                <span class="file-size">{{ formatFileSize(fileSize) }}</span>
            </div>
        </div>

        <div v-if="uploading" class="upload-progress">
            <div class="progress-bar">
                <div class="progress-bar-fill" :style="{ width: `${uploadProgress}%` }"></div>
            </div>
            <div class="progress-text">
                <span v-if="uploadProgress >= 0">上传进度: {{ uploadProgress }}%</span>
                <span v-else>加载中...</span>
            </div>
        </div>

        <div v-if="errorMessage" class="upload-error">
            <i class="fas fa-exclamation-circle"></i>
            <span>{{ errorMessage }}</span>
        </div>

        <div v-if="successMessage" class="upload-success">
            <i class="fas fa-check-circle"></i>
            <span>{{ successMessage }}</span>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue';
import axios from "../../utils/axios";
import type { R } from '../../response/R';
import { allStatus } from '../../usePinia/allStatus';
import { message } from 'ant-design-vue';
import { allVisible } from '../../usePinia/allVisible';
import { allData } from '../../usePinia/allData';

const fileInput = ref<HTMLInputElement | null>(null);
const uploading = ref(false);
const errorMessage = ref('');
const successMessage = ref('');
const uploadProgress = ref(0);
const selectedFile = ref<File | null>(null);

// 计算属性：获取文件名和大小
const fileName = computed(() => selectedFile.value?.name || '');
const fileSize = computed(() => selectedFile.value?.size || 0);

// 处理文件选择
const handleFileChange = (e: Event) => {
    const target = e.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
        const file = target.files[0];
        selectedFile.value = file;

        // 检查文件类型
        const allowedTypes = ['text/plain', 'application/msword', 'application/pdf', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];
        if (!allowedTypes.includes(file.type)) {
            errorMessage.value = '只支持：txt，pdf，doc，docx类型的文件';
            selectedFile.value = null;
            return;
        }
        errorMessage.value = '';
    }
};

//打开组件时判断状态
if (allStatus().teacher_CourseList_addCourse_isSending){
    uploading.value = false;
    successMessage.value = '正在添加课程，如果你没上传知识库文件则AI会自动生成知识库文档，这个过程可能需要几分钟，请耐心等待...';
}

if (allStatus().teacher_CourseList_addCourse_sendingOver){
    uploading.value = false;
    if (allData().teacher_CourseList_addCourse_backResult_data.code === 200){
        successMessage.value = '添加课程成功，课程邀请码为：' + allData().teacher_CourseList_addCourse_backResult_data.data;
    } else if (allData().teacher_CourseList_addCourse_backResult_data.code && allData().teacher_CourseList_addCourse_backResult_data.code !== 200 ){
        errorMessage.value = '添加课程失败，原因：' + allData().teacher_CourseList_addCourse_backResult_data.msg;
    }
}

// 上传文件
const uploadFile = async (courseName:string,teacherUsername:string,courseHours:string,courseScore:string) => {
    allStatus().teacher_CourseList_addCourse_isSending = true
    const file = selectedFile.value;
    const formData = new FormData();
    formData.append('file', file as any);
    formData.append('course_name', courseName);
    formData.append('teacher_username', teacherUsername);
    formData.append('course_hours', courseHours);
    formData.append('course_score', courseScore);
    

    uploading.value = true;
    errorMessage.value = '';
    successMessage.value = '';
    uploadProgress.value = 0;

    try {
        const R:R = (await axios.post('/teacher/ai/addCourse', formData, {
            onUploadProgress: (progressEvent) => {
                let total = progressEvent.total;
                if (total === undefined) {
                    if(file){
                        total = file.size;
                        const percentCompleted = Math.round((progressEvent.loaded * 100) / total);
                        uploadProgress.value = percentCompleted;
                    }else{
                        uploading.value = false;
                        successMessage.value = 'AI正在自动生成知识库文档，这个过程可能需要几分钟，请耐心等待...';
                    }
                }else{
                    if(!file){
                        uploading.value = false;
                        successMessage.value = 'AI正在自动生成知识库文档，这个过程可能需要几分钟，请耐心等待...';
                    }
                    if (total <= 0) {
                        uploadProgress.value = -1;
                        return;
                    }
                }
            },
            timeout: 180000,
        })).data;

        if (R.code === 200) {
            successMessage.value = '添加课程成功，课程邀请码为：' + R.data;
            allData().teacher_CourseList_addCourse_backResult_data = R;
            message.success("添加课程成功")
            allVisible().teacher_CourseList_addCourse_visible = true
            allStatus().teacher_CourseList_addCourse_sendingOver = true
            selectedFile.value = null; // 上传成功后清空文件
        }else{
            successMessage.value = '';
            errorMessage.value = '添加课程失败，原因：'+ R.msg;
            message.error(R.msg)
            allVisible().teacher_CourseList_addCourse_visible = true
            allStatus().teacher_CourseList_addCourse_sendingOver = true
            selectedFile.value = null;//清空文件
        }
    } catch (error) {
        errorMessage.value = '上传失败: 未知错误';
        console.error('未知错误:', error);
    } finally {
        uploading.value = false;
        allStatus().teacher_CourseList_addCourse_isSending = false
        allData().teacher_CourseList_addCourse_childKey_data++
    }
};

// 暴露方法给父组件
defineExpose({
    uploadFile
});

// 格式化文件大小
const formatFileSize = (bytes: number): string => {
    if (bytes < 1024) return bytes + ' B';
    else if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB';
    else return (bytes / 1048576).toFixed(1) + ' MB';
};
</script>

<style scoped>
/* 基础样式重置 */
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

.file-upload-card {
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    padding: 24px;
    max-width: 500px;
    width: 100%;
    margin: 0 auto;
    transition: all 0.3s ease;
}

.file-upload-card:hover {
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

/* 头部样式 */
.upload-header {
    margin-bottom: 24px;
}

.upload-header h3 {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin-bottom: 8px;
}

.upload-tip {
    color: #8c8c8c;
    font-size: 14px;
}

/* 上传区域样式 */
.upload-area {
    border: 2px dashed #d9d9d9;
    border-radius: 8px;
    padding: 32px 24px;
    text-align: center;
    margin-bottom: 20px;
    transition: all 0.3s ease;
}

.upload-area:hover {
    border-color: #40a9ff;
    background-color: #f0f9ff;
}

.upload-btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    background: #40a9ff;
    color: #fff;
    border: none;
    border-radius: 6px;
    padding: 10px 20px;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.2s ease;
}

.upload-btn:hover {
    background: #3182ce;
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(64, 169, 255, 0.3);
}

.upload-btn i {
    margin-right: 8px;
    font-size: 16px;
}

.file-input-hidden {
    display: none;
    opacity: 0;
}

.file-info {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid #f0f0f0;
}

.file-name {
    color: #333;
    font-size: 14px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 70%;
}

.file-size {
    color: #8c8c8c;
    font-size: 12px;
}

/* 进度条样式 */
.upload-progress {
    margin-top: 20px;
}

.progress-bar {
    height: 6px;
    background: #e8e8e8;
    border-radius: 3px;
    overflow: hidden;
    margin-bottom: 8px;
}

.progress-bar-fill {
    height: 100%;
    background: #40a9ff;
    width: 0;
    border-radius: 3px;
    transition: width 0.2s ease;
}

.progress-text {
    font-size: 14px;
    color: #333;
    text-align: center;
}

/* 状态提示样式 */
.upload-error,
.upload-success {
    display: flex;
    align-items: center;
    padding: 12px;
    border-radius: 6px;
    margin-top: 16px;
    font-size: 14px;
}

.upload-error {
    background: #fffbe6;
    color: #d4380d;
    border-left: 4px solid #f5222d;
}

.upload-success {
    background: #e6ffed;
    color: #0f5132;
    border-left: 4px solid #52c41a;
}

.upload-error i,
.upload-success i {
    margin-right: 8px;
    font-size: 16px;
}
</style>