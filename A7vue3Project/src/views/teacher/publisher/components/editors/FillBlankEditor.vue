<template>
  <div class="editor-form">
    <div class="form-field">
      <label>题干 <span style="color:red">*</span></label>
      <div class="title-with-blanks">
        <a-textarea
          v-model:value="local.title"
          :rows="4"
          placeholder="请输入题目正文。点击下方按钮在光标位置插入填空。"
          @keydown="handleTitleKeydown"
        />
        <button class="btn-add-blank" @click="insertBlank">➕ 添加填空</button>
      </div>
      <p class="field-hint">
        点击按钮在题干中插入 <code>___</code>，每个填空对应下方一个答案框。
        当前 <strong>{{ blankCount }}</strong> 个填空。
      </p>
    </div>

    <div class="form-field" v-if="blankCount > 0">
      <label>正确答案 <span style="color:red">*</span></label>
      <div class="answer-list">
        <div v-for="(_ans, i) in answers" :key="i" class="answer-row">
          <span class="answer-label">填空 {{ i + 1 }}</span>
          <a-input v-model:value="answers[i]" :placeholder="`请输入第 ${i + 1} 个空的答案`" style="flex:1" />
          <button class="btn-remove-blank" title="移除此填空" @click="removeBlank(i)">✕</button>
        </div>
      </div>
      <p class="field-hint">答案将按顺序与题干中的填空一一对应</p>
    </div>

    <div v-else class="form-field">
      <label>正确答案 <span style="color:red">*</span></label>
      <p class="no-blank-hint">请先在题干中添加填空</p>
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
import { reactive, ref, computed, watch } from 'vue'
import type { FillBlankQuestion } from '../../../../../types/question'

const BLANK_MARKER = '___'

const props = defineProps<{ question: FillBlankQuestion }>()
const emit = defineEmits<{ 'update:question': [q: FillBlankQuestion] }>()

const local = reactive<FillBlankQuestion>(JSON.parse(JSON.stringify(props.question)))

// Parse answers from the comma-separated string
const answers = ref<string[]>(parseAnswers(local.correctAnswer))

function parseAnswers(raw: string): string[] {
  if (!raw || !raw.trim()) return []
  return raw.split(',').map(s => s.trim()).filter(s => s.length > 0)
}

function syncCorrectAnswer() {
  local.correctAnswer = answers.value.join(',')
}

const blankCount = computed(() => {
  if (!local.title) return 0
  // Count occurrences of BLANK_MARKER
  const matches = local.title.match(/___/g)
  return matches ? matches.length : 0
})

/** Align answer count with blank count */
function alignAnswers() {
  const count = blankCount.value
  while (answers.value.length > count) {
    answers.value.pop()
  }
  while (answers.value.length < count) {
    answers.value.push('')
  }
  // If blanks were removed, clean up empty leading answers
  syncCorrectAnswer()
}

// Watch title changes to auto-sync answer count
watch(() => local.title, () => {
  alignAnswers()
})

/** Insert a blank at the end of the title text */
function insertBlank() {
  local.title = (local.title || '') + BLANK_MARKER
  answers.value.push('')
  syncCorrectAnswer()
}

/** Remove a specific blank and its answer */
function removeBlank(index: number) {
  // Remove the nth occurrence of ___ from title
  let idx = 0
  let count = 0
  const title = local.title || ''
  while (count < index && idx < title.length) {
    if (title.substring(idx, idx + 3) === BLANK_MARKER) {
      count++
      idx += 3
    } else {
      idx++
    }
  }
  // Now idx points to the start of the nth marker
  const before = title.substring(0, idx)
  const after = title.substring(idx + 3)
  local.title = before + after
  answers.value.splice(index, 1)
  syncCorrectAnswer()
}

/** Keyboard shortcut: Ctrl+Shift+B to insert blank */
function handleTitleKeydown(e: KeyboardEvent) {
  if (e.ctrlKey && e.shiftKey && e.key === 'B') {
    e.preventDefault()
    insertBlank()
  }
}

// Emit on any local change (title, score, etc.)
watch(local, () => {
  emit('update:question', { ...local })
}, { deep: true })

// Emit on answer changes — always sync to correctAnswer
watch(answers, () => {
  syncCorrectAnswer()
  emit('update:question', { ...local })
}, { deep: true })

// Watch prop changes: reload everything from the incoming question
watch(() => props.question, (q) => {
  Object.assign(local, JSON.parse(JSON.stringify(q)))
  answers.value = parseAnswers(local.correctAnswer)
  // Ensure answer slots match blank count
  alignAnswers()
}, { deep: true })

// Initialize answer slots on mount (component is destroyedOnClose, so this runs each time)
alignAnswers()
</script>

<style scoped>
.editor-form { display: flex; flex-direction: column; gap: 4px; }
.form-field { display: flex; flex-direction: column; gap: 6px; margin-bottom: 16px; }
.form-field label { font-size: 13px; font-weight: 600; color: #334155; }
.field-hint { margin: 2px 0 0; font-size: 12px; color: #94A3B8; }
.field-hint code { background: #EEF2FF; color: #4F46E5; padding: 1px 4px; border-radius: 3px; }
.field-hint strong { color: #4F46E5; }

.title-with-blanks { display: flex; flex-direction: column; gap: 8px; }

.btn-add-blank {
  align-self: flex-start;
  padding: 6px 14px;
  border: 1px dashed #4F46E5;
  border-radius: 6px;
  background: #EEF2FF;
  color: #4F46E5;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.15s;
}
.btn-add-blank:hover { background: #E0E7FF; border-style: solid; }

.answer-list { display: flex; flex-direction: column; gap: 8px; }
.answer-row { display: flex; align-items: center; gap: 8px; }
.answer-label {
  font-size: 12px; font-weight: 600; color: #475569;
  min-width: 52px; white-space: nowrap;
}

.btn-remove-blank {
  width: 24px; height: 24px;
  border: 1px solid #E2E8F0;
  border-radius: 6px;
  background: #fff;
  color: #94A3B8;
  font-size: 12px;
  cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  transition: all 0.15s;
  flex-shrink: 0;
}
.btn-remove-blank:hover { color: #DC2626; border-color: #FECACA; background: #FEF2F2; }

.no-blank-hint { font-size: 13px; color: #94A3B8; margin: 0; }

.score-field { margin-bottom: 0; }
.score-input-row { display: flex; align-items: center; gap: 10px; }
.score-hint { font-size: 12px; color: #94A3B8; }
</style>
