<template>
    <div class="teaching-schedule bg-white rounded-xl shadow-lg p-6 max-w-4xl mx-auto">
        <h1 class="text-[clamp(2rem,4vw,3rem)] font-bold text-gray-800 mb-6">{{ props.title }}</h1>

        <div class="content-grid">
            <div class="prose max-w-none" v-html="parsedContent"></div>
        </div>

        <button v-if="showMoreButton"
            class="mt-6 w-full py-3 px-4 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors text-lg font-medium"
            @click="toggleExpanded">
            {{ expanded ? '收起' : '展开更多' }}
            <i class="fa fa-chevron-down ml-2 transition-transform" :class="{ 'rotate-180': expanded }"></i>
        </button>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { marked } from 'marked';
import DOMPurify from 'dompurify';

// 接收 Markdown 内容作为 props
const props = defineProps({
    markdownContent: {
        type: String,
        required: true
    },
    maxVisibleStages: {
        type: Number,
        default: 2
    },
    title: {
        type: String,
        required: true
    }
});

// 状态管理
const parsedContent = ref('');
const expanded = ref(false);
const showMoreButton = ref(false);

// 解析 Markdown 内容
onMounted(async () => {
    // 配置 marked 解析器
    marked.setOptions({
        breaks: true, // 启用换行符转 <br>
        gfm: true,    // 启用 GitHub Flavored Markdown
    });

    // 解析 Markdown 并净化 HTML
    const markdownHTML = await marked.parse(props.markdownContent);
    parsedContent.value = DOMPurify.sanitize(markdownHTML as string);

    // 检查是否需要显示"展开更多"按钮
    const stageCount = (props.markdownContent.match(/#{3,4}\s+阶段/g) || []).length;
    showMoreButton.value = stageCount > props.maxVisibleStages;

    // 如果有超出限制的阶段，默认折叠
    if (showMoreButton.value && !expanded.value) {
        collapseContent();
    }
});

// 折叠内容到前几个阶段
const collapseContent = () => {
    const element = document.querySelector('.teaching-schedule .prose');
    if (!element) return;

    const headings = element.querySelectorAll('h3, h4');
    if (headings.length <= props.maxVisibleStages) return;

    // 隐藏超出限制的阶段
    for (let i = props.maxVisibleStages; i < headings.length; i++) {
        let currentElement: Element | null = headings[i];
        while (currentElement && !currentElement.nextElementSibling?.matches('h3, h4')) {
            if (currentElement instanceof HTMLElement) {
                currentElement.classList.add('hidden');
            }
            currentElement = currentElement.nextElementSibling;
        }
    }
};

// 切换展开/折叠状态
const toggleExpanded = () => {
    expanded.value = !expanded.value;

    if (!expanded.value) {
        collapseContent();
    } else {
        // 显示所有内容
        const hiddenElements = document.querySelectorAll('.teaching-schedule .prose .hidden');
        hiddenElements.forEach(el => el.classList.remove('hidden'));
    }
};
</script>

<style scoped>
/* 关键：用 Grid 布局让内容填充父容器 */
.content-grid {
    display: grid;
    grid-template-columns: 1fr;
    gap: 1rem;
}

.prose {
    @apply text-gray-700;
    word-break: break-word;
    overflow-x: hidden;
    margin: 0 auto;
    max-width: 100%;
    font-size: 1.25rem;
    /* 基础字体大小增加到20px */
    line-height: 1.8;
    /* 行高略微增加 */
}

/* 大幅增加各级标题字体 */
.prose h1 {
    font-size: 2.5rem;
}

.prose h2 {
    font-size: 2rem;
}

.prose h3 {
    font-size: 1.75rem;
}

.prose h4 {
    font-size: 1.5rem;
}

.prose p,
.prose li,
.prose td {
    word-wrap: break-word;
    white-space: normal;
    margin-bottom: 1.25rem;
    /* 段落间距增加 */
}

.prose pre {
    white-space: pre-wrap;
    overflow-x: auto;
    font-size: 1.1rem;
    /* 代码块字体同步放大 */
    padding: 1.25rem;
    margin-bottom: 1.5rem;
    border-radius: 0.5rem;
    background-color: #f8f9fa;
}

.prose code {
    font-size: 0.95em;
    /* 内联代码字体微调 */
    padding: 0.2rem 0.4rem;
    border-radius: 0.3rem;
    background-color: rgba(0, 0, 0, 0.05);
}

.prose table {
    @apply w-full border-collapse mb-6;
    table-layout: auto;
}

.prose th,
.prose td {
    @apply py-3 px-4 border border-gray-200;
    word-break: break-word;
    padding: 0.75rem 1rem;
    /* 表格单元格内边距增加 */
}

/* 折叠按钮样式 */
button {
    transition: all 0.3s ease;
}

button:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}
</style>