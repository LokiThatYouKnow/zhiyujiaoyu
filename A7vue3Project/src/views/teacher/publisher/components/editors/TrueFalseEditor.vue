<template>
  <div class="editor-form">
    <div class="form-field">
      <label>题干 <span style="color:red">*</span></label>
      <a-textarea v-model:value="local.title" :rows="3" placeholder="请输入判断命题，例如：二叉树是一种线性数据结构。" />
    </div>
    <div class="form-field">
      <label>正确答案 <span style="color:red">*</span></label>
      <a-radio-group v-model:value="local.correctAnswer">
        <a-radio :value="true">✅ 正确</a-radio>
        <a-radio :value="false">❌ 错误</a-radio>
      </a-radio-group>
    </div>
    <div class="form-field score-field">
      <label>分值</label>
      <div class="score-input-row">
        <a-input-number v-model:value="local.score" :min="0" :max="100" style="width:100px" size="small" />
        <span class="score-hint">{{ local.scoreManual ? '已自定义' : '默认（自动分配）' }}</span>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, watch } from 'vue'
import type { TrueFalseQuestion } from '../../../../../types/question'

const props = defineProps<{ question: TrueFalseQuestion }>()
const emit = defineEmits<{ 'update:question': [q: TrueFalseQuestion] }>()

const local = reactive<TrueFalseQuestion>(JSON.parse(JSON.stringify(props.question)))

watch(() => props.question, (q) => {
  Object.assign(local, JSON.parse(JSON.stringify(q)))
}, { deep: true })

watch(local, () => emit('update:question', { ...local }), { deep: true })
</script>

<style scoped>
.editor-form { display: flex; flex-direction: column; gap: 4px; }
.form-field { display: flex; flex-direction: column; gap: 6px; margin-bottom: 16px; }
.form-field label { font-size: 13px; font-weight: 600; color: #334155; }
.field-hint { margin: 2px 0 0; font-size: 12px; color: #94A3B8; }
.score-field { margin-bottom: 0; }
.score-input-row { display: flex; align-items: center; gap: 10px; }
.score-hint { font-size: 12px; color: #94A3B8; }
</style>
