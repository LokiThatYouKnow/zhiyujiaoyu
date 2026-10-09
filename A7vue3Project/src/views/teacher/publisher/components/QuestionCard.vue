<template>
  <div class="q-card">
    <div class="q-left">
      <span class="q-number">{{ index + 1 }}</span>
      <div class="q-info">
        <span class="q-type-badge" :style="typeStyle">{{ typeLabel }}</span>
        <span v-if="!complete" class="q-incomplete-badge">未完成</span>
        <span class="q-title">{{ truncatedTitle }}</span>
        <span class="q-answer-hint">{{ answerHint }}</span>
        <span class="q-score">{{ question.score }} 分</span>
      </div>
    </div>
    <div class="q-actions">
      <button class="q-btn q-btn-move" title="上移" :disabled="index === 0" @click="emit('moveUp')">↑</button>
      <button class="q-btn q-btn-move" title="下移" :disabled="isLast" @click="emit('moveDown')">↓</button>
      <button class="q-btn q-btn-edit" title="编辑" @click="emit('edit')">编辑</button>
      <a-popconfirm title="确定删除这道题目？" ok-text="确定" cancel-text="取消" @confirm="emit('delete')">
        <button class="q-btn q-btn-del">删除</button>
      </a-popconfirm>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import type { Question } from '../../../../types/question'
import { QUESTION_TYPE_LABELS, isQuestionComplete } from '../../../../types/question'

const props = defineProps<{ question: Question; index: number; isLast: boolean }>()
const emit = defineEmits<{ edit: []; delete: []; moveUp: []; moveDown: [] }>()

const complete = computed(() => isQuestionComplete(props.question))

const typeLabel = computed(() => QUESTION_TYPE_LABELS[props.question.type] || props.question.type)

const typeStyle = computed(() => {
  const styles: Record<string, { background: string; color: string }> = {
    'choice': { background: '#EEF2FF', color: '#4F46E5' },
    'fill-blank': { background: '#D1FAE5', color: '#059669' },
    'true-false': { background: '#FEF3C7', color: '#D97706' },
    'essay': { background: '#F3E8FF', color: '#7C3AED' },
    'calculation': { background: '#FEE2E2', color: '#DC2626' },
  }
  return styles[props.question.type] || { background: '#F1F5F9', color: '#475569' }
})

const truncatedTitle = computed(() => {
  const t = props.question.title || '(无题干)'
  return t.length > 60 ? t.slice(0, 60) + '...' : t
})

const answerHint = computed(() => {
  const q = props.question
  switch (q.type) {
    case 'choice': return '答案: ' + q.correctAnswer
    case 'fill-blank': return '答案: ' + (q.correctAnswer || '(空)')
    case 'true-false': return '答案: ' + (q.correctAnswer ? '正确 ✅' : '错误 ❌')
    case 'essay': return q.referenceAnswer ? '参考: ' + q.referenceAnswer.slice(0, 30) + '...' : '(无参考答案)'
    case 'calculation': return '答案: ' + (q.correctAnswer || '(空)')
    default: return ''
  }
})
</script>

<style scoped>
.q-card {
  display: flex; align-items: center; justify-content: space-between; gap: 12px;
  padding: 12px 16px; background: #fff; border: 1px solid #E2E8F0; border-radius: 10px;
  transition: all 0.15s; margin-bottom: 8px;
}
.q-card:hover { border-color: #CBD5E1; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
.q-left { display: flex; align-items: center; gap: 12px; flex: 1; min-width: 0; }
.q-number {
  width: 28px; height: 28px; min-width: 28px; border-radius: 50%;
  background: #4F46E5; color: #fff; font-size: 12px; font-weight: 700;
  display: flex; align-items: center; justify-content: center;
}
.q-info { display: flex; align-items: center; gap: 8px; min-width: 0; flex-wrap: wrap; }
.q-type-badge {
  padding: 2px 8px; border-radius: 4px; font-size: 11px; font-weight: 600; white-space: nowrap;
}
.q-incomplete-badge {
  padding: 2px 8px; border-radius: 4px; font-size: 11px; font-weight: 600; white-space: nowrap;
  background: #FEF3C7; color: #D97706;
}
.q-title { font-size: 13px; color: #1E293B; font-weight: 500; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.q-answer-hint { font-size: 12px; color: #94A3B8; white-space: nowrap; }
.q-score { font-size: 12px; color: #4F46E5; font-weight: 600; white-space: nowrap; margin-left: 4px; }
.q-actions { display: flex; gap: 4px; flex-shrink: 0; }
.q-btn {
  padding: 5px 10px; border: 1px solid #E2E8F0; border-radius: 6px; background: #fff;
  font-size: 12px; cursor: pointer; color: #475569; transition: all 0.15s; font-family: inherit;
}
.q-btn:hover { border-color: #CBD5E1; background: #F8FAFC; }
.q-btn:disabled { opacity: 0.35; cursor: not-allowed; }
.q-btn-move { font-size: 13px; padding: 4px 8px; }
.q-btn-edit:hover { color: #4F46E5; border-color: #4F46E5; background: #EEF2FF; }
.q-btn-del:hover { color: #DC2626; border-color: #FECACA; background: #FEF2F2; }
</style>
