<template>
  <div class="editor-form">
    <div class="form-field">
      <label>题干 <span style="color:red">*</span></label>
      <a-textarea v-model:value="local.title" :rows="3" placeholder="请输入选择题题" />
    </div>
    <div class="form-field">
      <label>选项 <span style="color:red">*</span></label>
      <div v-for="opt in local.options" :key="opt.key" class="option-row">
        <span class="option-key">{{ opt.key }}.</span>
        <a-input v-model:value="opt.text" :placeholder="`选项 ${opt.key}`" style="flex:1" />
      </div>
    </div>
    <div class="form-field">
      <label>正确答案 <span style="color:red">*</span></label>
      <a-select v-model:value="local.correctAnswer" placeholder="选择正确答案" :disabled="validOptions.length === 0">
        <a-select-option v-for="opt in validOptions" :key="opt.key" :value="opt.key">
          {{ opt.key }}. {{ opt.text }}
        </a-select-option>
      </a-select>
      <p v-if="validOptions.length < 2" class="field-hint">请至少填写两个选项后才能设置正确答案</p>
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
import { reactive, computed, watch } from 'vue'
import type { ChoiceQuestion } from '../../../../../types/question'

const props = defineProps<{ question: ChoiceQuestion }>()
const emit = defineEmits<{ 'update:question': [q: ChoiceQuestion] }>()

const local = reactive<ChoiceQuestion>(JSON.parse(JSON.stringify(props.question)))

/** 只返回已填写的选项供答案选择 */
const validOptions = computed(() =>
  local.options.filter(o => o.text.trim())
)

watch(() => props.question, (q) => {
  Object.assign(local, JSON.parse(JSON.stringify(q)))
}, { deep: true })

// 实时发射更新
watch(local, () => emit('update:question', { ...local }), { deep: true })
</script>

<style scoped>
.editor-form { display: flex; flex-direction: column; gap: 4px; }
.form-field { display: flex; flex-direction: column; gap: 6px; margin-bottom: 16px; }
.form-field label { font-size: 13px; font-weight: 600; color: #334155; }
.field-hint { margin: 2px 0 0; font-size: 12px; color: #94A3B8; }

.option-row { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
.option-key { font-size: 13px; font-weight: 600; color: #475569; min-width: 20px; }

.score-field { margin-bottom: 0; }
.score-input-row { display: flex; align-items: center; gap: 10px; }
.score-hint { font-size: 12px; color: #94A3B8; }
</style>
