<template>
  <div class="score-panel">
    <div class="score-panel-header">
      <h3>📊 分值分配</h3>
      <div class="total-score-field">
        <label>总分</label>
        <a-input-number
          :value="totalScore"
          :min="1"
          :max="999"
          style="width:100px"
          @change="handleTotalChange"
        />
      </div>
    </div>

    <div v-if="activeTypes.length === 0" class="score-empty">
      暂无题目，请先添加题目
    </div>

    <div v-else class="score-type-rows">
      <div
        v-for="t in activeTypes"
        :key="t.type"
        class="score-type-row"
        :class="{ 'has-manual': t.hasManual }"
      >
        <span class="type-icon">{{ t.icon }}</span>
        <span class="type-label" :style="{ color: t.color }">{{ t.label }}</span>
        <span class="type-count">× {{ t.count }} 题</span>
        <div class="type-score-field">
          <span class="field-label">总分</span>
          <a-input-number
            :value="typeScores[t.type]"
            :min="0"
            :max="999"
            style="width:80px"
            size="small"
            @change="(v: number | null) => handleTypeScoreChange(t.type, v ?? 0)"
          />
        </div>
        <span class="per-question">每题 ≈ {{ t.perQuestion }} 分</span>
        <span v-if="t.hasManual" class="manual-badge" title="该题型下有手动设分的题目">⚡</span>
      </div>
    </div>

    <div class="score-panel-footer">
      <button class="btn-auto" @click="emit('autoDistribute')">🔄 自动分配</button>
      <span class="sum-total">合计: <strong>{{ computedTotal }}</strong> 分</span>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import type { Question, QuestionType } from '../../../../types/question'
import { QUESTION_TYPE_LABELS } from '../../../../types/question'

const TYPE_ICONS: Record<string, string> = {
  choice: '🔤', 'fill-blank': '✏️', 'true-false': '✅',
  essay: '📝', calculation: '🔢',
}
const TYPE_COLORS: Record<string, string> = {
  choice: '#4F46E5', 'fill-blank': '#059669', 'true-false': '#D97706',
  essay: '#7C3AED', calculation: '#DC2626',
}

const props = defineProps<{
  questions: Question[]
  totalScore: number
  typeScores: Record<QuestionType, number>
}>()

const emit = defineEmits<{
  'update:totalScore': [v: number]
  'update:typeScores': [v: Record<QuestionType, number>]
  autoDistribute: []
}>()

/** 存在题目的题型列表 */
const activeTypes = computed(() => {
  const counts: Record<string, number> = {}
  const hasManual: Record<string, boolean> = {}
  props.questions.forEach(q => {
    counts[q.type] = (counts[q.type] || 0) + 1
    if (q.scoreManual) hasManual[q.type] = true
  })
  return Object.entries(counts).map(([type, count]) => {
    const typeScore = props.typeScores[type as QuestionType] || 0
    return {
      type: type as QuestionType,
      label: QUESTION_TYPE_LABELS[type as QuestionType] || type,
      icon: TYPE_ICONS[type] || '📋',
      color: TYPE_COLORS[type] || '#475569',
      count,
      perQuestion: count > 0 ? Math.round(typeScore / count) : 0,
      hasManual: !!hasManual[type],
    }
  })
})

/** 计算各题型分之和 */
const computedTotal = computed(() => {
  return activeTypes.value.reduce((sum, t) => sum + (props.typeScores[t.type] || 0), 0)
})

function handleTotalChange(v: number | null) {
  emit('update:totalScore', v ?? 100)
}

function handleTypeScoreChange(type: QuestionType, v: number) {
  const newTypeScores = { ...props.typeScores, [type]: v }
  emit('update:typeScores', newTypeScores)
}
</script>

<style scoped>
.score-panel {
  background: #F8FAFC;
  border: 1px solid #E2E8F0;
  border-radius: 10px;
  padding: 20px;
}
.score-panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.score-panel-header h3 {
  font-size: 14px;
  font-weight: 700;
  color: #0F172A;
  margin: 0;
}
.total-score-field {
  display: flex;
  align-items: center;
  gap: 8px;
}
.total-score-field label {
  font-size: 13px;
  font-weight: 600;
  color: #475569;
}

.score-empty {
  text-align: center;
  color: #94A3B8;
  font-size: 13px;
  padding: 16px 0;
}

.score-type-rows {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 14px;
}
.score-type-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  background: #fff;
  border: 1px solid #E2E8F0;
  border-radius: 8px;
  transition: border-color 0.15s;
}
.score-type-row:hover {
  border-color: #CBD5E1;
}
.score-type-row.has-manual {
  border-color: #FCD34D;
  background: #FFFBEB;
}
.type-icon { font-size: 14px; }
.type-label {
  font-size: 13px;
  font-weight: 600;
  min-width: 56px;
}
.type-count {
  font-size: 12px;
  color: #64748B;
  min-width: 48px;
}
.type-score-field {
  display: flex;
  align-items: center;
  gap: 4px;
}
.field-label {
  font-size: 11px;
  color: #94A3B8;
}
.per-question {
  font-size: 12px;
  color: #64748B;
  margin-left: auto;
}
.manual-badge {
  font-size: 13px;
  cursor: help;
}

.score-panel-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 12px;
  border-top: 1px solid #E2E8F0;
}
.btn-auto {
  padding: 6px 14px;
  border: 1px solid #CBD5E1;
  border-radius: 6px;
  background: #fff;
  color: #475569;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.15s;
}
.btn-auto:hover {
  border-color: #4F46E5;
  color: #4F46E5;
  background: #EEF2FF;
}
.sum-total {
  font-size: 13px;
  color: #475569;
}
.sum-total strong {
  color: #0F172A;
  font-size: 16px;
}
</style>
