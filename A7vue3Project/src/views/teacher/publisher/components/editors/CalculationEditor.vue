<template>
  <div class="editor-form">
    <div class="form-field">
      <label>题干 <span style="color:red">*</span></label>
      <a-textarea v-model:value="local.title" :rows="3" placeholder="请输入计算题题目描述。" />
    </div>
    <div class="form-field">
      <label>公式（可选）</label>
      <a-input v-model:value="local.formula" placeholder="例如：E = mc^2 或 F = ma" />
    </div>
    <div class="form-field">
      <label>正确答案 <span style="color:red">*</span></label>
      <a-input v-model:value="local.correctAnswer" placeholder="请输入期望的数值或表达式结果。" />
    </div>
    <div class="form-field">
      <label>解题步骤（可选）</label>
      <a-textarea v-model:value="local.steps" :rows="3" placeholder="请输入分步解题过程，作为评分参考。" />
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
import type { CalculationQuestion } from '../../../../../types/question'

const props = defineProps<{ question: CalculationQuestion }>()
const emit = defineEmits<{ 'update:question': [q: CalculationQuestion] }>()

const local = reactive<CalculationQuestion>(JSON.parse(JSON.stringify(props.question)))

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
