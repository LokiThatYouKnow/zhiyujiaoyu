<template>
  <div class="markdown-body" v-html="renderedHtml"></div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { marked } from 'marked'
import DOMPurify from 'dompurify'

const props = withDefaults(defineProps<{
  content: string
  streaming?: boolean
}>(), {
  streaming: false,
})

const renderedHtml = computed(() => {
  if (!props.content) return ''
  const raw = marked.parse(props.content) as string
  return DOMPurify.sanitize(raw)
})
</script>

<style scoped>
.markdown-body {
  color: #334155;
  font-size: 14px;
  line-height: 1.8;
  word-wrap: break-word;
}
.markdown-body :deep(h1) { font-size: 22px; font-weight: 700; color: #0F172A; margin: 24px 0 12px; padding-bottom: 8px; border-bottom: 2px solid #E2E8F0; }
.markdown-body :deep(h2) { font-size: 18px; font-weight: 700; color: #1E293B; margin: 20px 0 10px; }
.markdown-body :deep(h3) { font-size: 16px; font-weight: 600; color: #334155; margin: 16px 0 8px; }
.markdown-body :deep(p) { margin: 8px 0; }
.markdown-body :deep(ul), .markdown-body :deep(ol) { padding-left: 24px; margin: 8px 0; }
.markdown-body :deep(li) { margin: 4px 0; }
.markdown-body :deep(code) {
  background: #F1F5F9; color: #E11D48;
  padding: 2px 6px; border-radius: 4px;
  font-size: 13px; font-family: 'JetBrains Mono', 'Fira Code', monospace;
}
.markdown-body :deep(pre) {
  background: #1E293B; color: #E2E8F0;
  padding: 16px; border-radius: 8px;
  overflow-x: auto; margin: 12px 0;
}
.markdown-body :deep(pre code) { background: none; color: inherit; padding: 0; }
.markdown-body :deep(table) { width: 100%; border-collapse: collapse; margin: 12px 0; }
.markdown-body :deep(th), .markdown-body :deep(td) {
  border: 1px solid #E2E8F0; padding: 10px 14px; text-align: left;
}
.markdown-body :deep(th) { background: #F8FAFC; font-weight: 600; color: #475569; }
.markdown-body :deep(blockquote) {
  border-left: 3px solid #4F46E5; padding: 8px 16px; margin: 12px 0;
  background: rgba(79,70,229,0.04); color: #475569;
}
.markdown-body :deep(strong) { font-weight: 700; color: #0F172A; }
</style>
