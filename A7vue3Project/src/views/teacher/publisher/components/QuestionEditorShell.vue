<template>
  <a-modal
    :open="visible"
    :title="modalTitle"
    :footer="null"
    width="640px"
    @cancel="emit('close')"
    destroyOnClose
  >
    <div v-if="question" class="editor-shell">
      <ChoiceEditor
        v-if="question.type === 'choice'"
        :question="question as any"
        @update:question="handleUpdate"
      />
      <FillBlankEditor
        v-else-if="question.type === 'fill-blank'"
        :question="question as any"
        @update:question="handleUpdate"
      />
      <TrueFalseEditor
        v-else-if="question.type === 'true-false'"
        :question="question as any"
        @update:question="handleUpdate"
      />
      <EssayEditor
        v-else-if="question.type === 'essay'"
        :question="question as any"
        @update:question="handleUpdate"
      />
      <CalculationEditor
        v-else-if="question.type === 'calculation'"
        :question="question as any"
        @update:question="handleUpdate"
      />

      <div class="editor-footer">
        <button class="btn-ghost" @click="emit('close')">取消</button>
        <button class="btn-primary" @click="handleConfirm">确认</button>
      </div>
    </div>
  </a-modal>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import type { Question } from '../../../../types/question'
import { QUESTION_TYPE_LABELS } from '../../../../types/question'
import ChoiceEditor from './editors/ChoiceEditor.vue'
import FillBlankEditor from './editors/FillBlankEditor.vue'
import TrueFalseEditor from './editors/TrueFalseEditor.vue'
import EssayEditor from './editors/EssayEditor.vue'
import CalculationEditor from './editors/CalculationEditor.vue'

const props = defineProps<{
  question: Question | null
  visible: boolean
  isNew?: boolean
}>()

const emit = defineEmits<{
  'update:question': [q: Question]
  close: []
}>()

const modalTitle = computed(() => {
  const prefix = props.isNew ? '添加' : '编辑'
  const typeLabel = props.question ? QUESTION_TYPE_LABELS[props.question.type] : '题目'
  return prefix + typeLabel
})

let pendingQuestion: Question | null = null

function handleUpdate(q: Question) {
  pendingQuestion = q
}

function handleConfirm() {
  if (pendingQuestion) {
    emit('update:question', pendingQuestion)
  }
  emit('close')
}
</script>

<style scoped>
.editor-shell { padding: 4px 0; }
.editor-footer {
  display: flex; justify-content: flex-end; gap: 8px;
  margin-top: 20px; padding-top: 16px; border-top: 1px solid #F1F5F9;
}
.btn-primary, .btn-ghost {
  padding: 8px 20px; border-radius: 8px; font-size: 14px; font-weight: 500; cursor: pointer; font-family: inherit; border: none;
}
.btn-primary { background: #4F46E5; color: #fff; }
.btn-primary:hover { background: #4338CA; }
.btn-ghost { background: #F1F5F9; color: #475569; }
.btn-ghost:hover { background: #E2E8F0; }
</style>
