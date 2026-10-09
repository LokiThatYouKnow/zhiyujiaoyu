<template>
  <div class="q-list">
    <div class="q-list-header">
      <h3>
        题目列表
        <span class="q-count">{{ questions.length }} 题</span>
        <span v-if="incompleteCount > 0" class="q-incomplete-count">⚠️ {{ incompleteCount }} 题未完成</span>
      </h3>
      <div class="q-list-stats" v-if="typeCountLabels.length > 0">
        <span v-for="tl in typeCountLabels" :key="tl.type" class="q-stat-badge" :style="tl.style">{{ tl.label }} × {{ tl.count }}</span>
      </div>
    </div>

    <!-- Empty -->
    <div v-if="questions.length === 0" class="q-empty">
      <p>暂无题目，点击下方按钮添加</p>
    </div>

    <!-- Questions -->
    <QuestionCard
      v-for="(q, idx) in questions"
      :key="q.id"
      :question="q"
      :index="idx"
      :isLast="idx === questions.length - 1"
      @edit="emit('editQuestion', idx)"
      @delete="emit('deleteQuestion', idx)"
      @moveUp="emit('moveQuestion', idx, 'up')"
      @moveDown="emit('moveQuestion', idx, 'down')"
    />

    <!-- Add button -->
    <a-dropdown :trigger="['click']">
      <button class="btn-add-question">+ 添加题目</button>
      <template #overlay>
        <a-menu @click="handleAdd">
          <a-menu-item key="choice">🔤 选择题</a-menu-item>
          <a-menu-item key="fill-blank">✏️ 填空题</a-menu-item>
          <a-menu-item key="true-false">✅ 判断题</a-menu-item>
          <a-menu-item key="essay">📝 应用题</a-menu-item>
          <a-menu-item key="calculation">🔢 计算题</a-menu-item>
        </a-menu>
      </template>
    </a-dropdown>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import type { Question, QuestionType } from '../../../../types/question'
import { QUESTION_TYPE_LABELS, isQuestionComplete } from '../../../../types/question'
import QuestionCard from './QuestionCard.vue'

const props = defineProps<{ questions: Question[] }>()

const incompleteCount = computed(() => props.questions.filter(q => !isQuestionComplete(q)).length)
const emit = defineEmits<{
  addQuestion: [type: QuestionType]
  editQuestion: [index: number]
  deleteQuestion: [index: number]
  moveQuestion: [index: number, direction: 'up' | 'down']
}>()

const typeColorMap: Record<string, { background: string; color: string }> = {
  'choice': { background: '#EEF2FF', color: '#4F46E5' },
  'fill-blank': { background: '#D1FAE5', color: '#059669' },
  'true-false': { background: '#FEF3C7', color: '#D97706' },
  'essay': { background: '#F3E8FF', color: '#7C3AED' },
  'calculation': { background: '#FEE2E2', color: '#DC2626' },
}

const typeCountLabels = computed(() => {
  const counts: Record<string, number> = {}
  props.questions.forEach(q => { counts[q.type] = (counts[q.type] || 0) + 1 })
  return Object.entries(counts).map(([type, count]) => ({
    type,
    label: QUESTION_TYPE_LABELS[type as QuestionType] || type,
    count,
    style: typeColorMap[type] || {},
  }))
})

function handleAdd({ key }: { key: string }) {
  emit('addQuestion', key as QuestionType)
}
</script>

<style scoped>
.q-list { margin-top: 8px; }
.q-list-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; flex-wrap: wrap; gap: 8px; }
.q-list-header h3 { font-size: 15px; font-weight: 700; color: #0F172A; margin: 0; }
.q-count { font-weight: 400; color: #64748B; font-size: 13px; }
.q-incomplete-count { font-weight: 500; color: #D97706; font-size: 12px; margin-left: 8px; }
.q-list-stats { display: flex; gap: 6px; flex-wrap: wrap; }
.q-stat-badge { padding: 2px 8px; border-radius: 4px; font-size: 11px; font-weight: 500; }
.q-empty { padding: 40px 0; text-align: center; color: #94A3B8; font-size: 14px; }
.btn-add-question {
  width: 100%; padding: 10px; border: 2px dashed #CBD5E1; border-radius: 10px; background: transparent;
  color: #64748B; font-size: 14px; font-weight: 500; cursor: pointer; transition: all 0.2s; font-family: inherit; margin-top: 12px;
}
.btn-add-question:hover { border-color: #4F46E5; color: #4F46E5; background: #EEF2FF; }
</style>
