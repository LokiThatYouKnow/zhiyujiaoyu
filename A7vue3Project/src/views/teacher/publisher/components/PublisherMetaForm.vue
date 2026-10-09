<template>
  <div class="meta-form">
    <div class="form-row">
      <div class="form-field">
        <label>所属课程 <span style="color:red">*</span></label>
        <a-select :value="courseName" placeholder="选择课程" @update:value="$emit('update:courseName', $event)">
          <a-select-option v-for="c in courses" :key="c.course_name" :value="c.course_name">{{ c.course_name }}</a-select-option>
        </a-select>
      </div>
      <div class="form-field">
        <label>标题 <span style="color:red">*</span></label>
        <a-input :value="title" placeholder="请输入标题" @update:value="$emit('update:title', $event)" />
      </div>
    </div>
    <div class="form-field">
      <label>描述说明</label>
      <a-textarea :value="description" :rows="2" placeholder="描述本次作业/考试的要求或说明。" @update:value="$emit('update:description', $event)" />
    </div>
    <slot name="timing" />
  </div>
</template>

<script lang="ts" setup>
defineProps<{
  courseName: string
  title: string
  description: string
  courses: Array<{ course_name: string }>
}>()

defineEmits<{
  'update:courseName': [val: string]
  'update:title': [val: string]
  'update:description': [val: string]
}>()
</script>

<style scoped>
.meta-form { display: flex; flex-direction: column; gap: 0; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.form-field { display: flex; flex-direction: column; gap: 6px; margin-bottom: 16px; }
.form-field label { font-size: 13px; font-weight: 600; color: #334155; }
</style>
