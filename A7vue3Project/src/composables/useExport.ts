/**
 * AI 结果导出工具
 * 支持将 Markdown 内容导出为 .txt / .md / .doc / .pdf
 */
import { message } from 'ant-design-vue'
import { jsPDF } from 'jspdf'

export type ExportFormat = 'txt' | 'md' | 'doc' | 'pdf'

/** 将 Markdown 转为纯文本（去除标记符号） */
function markdownToPlainText(md: string): string {
  return md
    .replace(/^#{1,6}\s+/gm, '')       // 标题
    .replace(/\*\*(.+?)\*\*/g, '$1')    // 粗体
    .replace(/\*(.+?)\*/g, '$1')        // 斜体
    .replace(/`{1,3}[^`]*`{1,3}/g, '') // 代码
    .replace(/\[(.+?)\]\(.+?\)/g, '$1') // 链接
    .replace(/!\[.*?\]\(.+?\)/g, '')    // 图片
    .replace(/^[-*+]\s+/gm, '• ')       // 无序列表
    .replace(/^\d+\.\s+/gm, '')         // 有序列表
    .replace(/\n{3,}/g, '\n\n')         // 多余空行
    .trim()
}

/** HTML 模板：包裹 Markdown 渲染内容，Word 可直接打开 */
function wrapAsDocHTML(title: string, bodyHTML: string): string {
  return `<!DOCTYPE html>
<html xmlns:o="urn:schemas-microsoft-com:office:office"
      xmlns:w="urn:schemas-microsoft-com:office:word"
      xmlns="http://www.w3.org/TR/REC-html40">
<head>
<meta charset="utf-8">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<!--[if gte mso 9]><xml><w:WordDocument><w:View>Print</w:View></w:WordDocument></xml><![endif]-->
<style>
  body { font-family: 'Microsoft YaHei', sans-serif; font-size: 14px; line-height: 1.8; padding: 40px; color: #333; }
  h1 { font-size: 22px; border-bottom: 2px solid #4F46E5; padding-bottom: 8px; }
  h2 { font-size: 18px; color: #4F46E5; }
  h3 { font-size: 15px; }
  pre { background: #f5f5f5; padding: 12px; border-radius: 4px; white-space: pre-wrap; }
  table { border-collapse: collapse; width: 100%; }
  th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
  th { background: #4F46E5; color: #fff; }
</style>
</head>
<body>
<h1>${title}</h1>
${bodyHTML}
</body>
</html>`
}

/** 简单的 Markdown → HTML 转换 */
function simpleMarkdownToHTML(md: string): string {
  return md
    .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
    .replace(/^### (.+)$/gm, '<h3>$1</h3>')
    .replace(/^## (.+)$/gm, '<h2>$1</h2>')
    .replace(/^# (.+)$/gm, '<h1>$1</h1>')
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.+?)\*/g, '<em>$1</em>')
    .replace(/`(.+?)`/g, '<code>$1</code>')
    .replace(/^[-*+] (.+)$/gm, '<li>$1</li>')
    .replace(/^\d+\. (.+)$/gm, '<li>$1</li>')
    .replace(/(<li>.*<\/li>\n?)+/g, '<ul>$&</ul>')
    .replace(/<\/ul>\n<ul>/g, '')
    .replace(/\n\n/g, '</p><p>')
    .replace(/^(?!<[hul/])/gm, '<p>')
    .replace(/(?<![>])\n/g, '<br>')
}

function downloadBlob(content: string, filename: string, mime: string) {
  const blob = new Blob(['﻿' + content], { type: mime }) // BOM for Chinese
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(url)
}

/** 导出为 PDF（使用 jsPDF 直接下载） */
async function exportAsPDF(title: string, bodyHTML: string) {
  const safeName = title.replace(/[\\/:*?"<>|]/g, '_').substring(0, 60)

  // 构建用于 PDF 渲染的完整 HTML
  const html = `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  body { font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif; font-size: 14px; line-height: 1.8; padding: 20px; color: #333; }
  h1 { font-size: 20px; border-bottom: 2px solid #4F46E5; padding-bottom: 6px; }
  h2 { font-size: 16px; color: #4F46E5; }
  h3 { font-size: 14px; }
  pre { background: #f5f5f5; padding: 10px; border-radius: 4px; white-space: pre-wrap; font-size: 12px; }
  code { background: #f0f0f0; padding: 2px 4px; border-radius: 3px; font-size: 13px; }
  table { border-collapse: collapse; width: 100%; margin: 10px 0; }
  th, td { border: 1px solid #ddd; padding: 6px 10px; text-align: left; }
  th { background: #4F46E5; color: #fff; }
  ul, ol { padding-left: 20px; }
  li { margin: 4px 0; }
  p { margin: 6px 0; }
</style>
</head>
<body>
<h1>${title}</h1>
${bodyHTML}
</body>
</html>`

  try {
    const doc = new jsPDF({ orientation: 'p', unit: 'mm', format: 'a4' })

    // 使用 jspdf 的 html 方法渲染内容
    await doc.html(html, {
      callback: (doc) => {
        doc.save(`${safeName}.pdf`)
      },
      margin: [12, 12, 12, 12],
      x: 0,
      y: 0,
      width: 186, // A4 = 210mm, with 12mm margins each side
      windowWidth: 794, // ~A4 px width at 96dpi
      autoPaging: 'text',
    })
  } catch (e: any) {
    // 如果 jsPDF html 渲染失败（如某些浏览器不支持），回退到打印方式
    console.warn('jsPDF html render failed, falling back to print:', e)
    const printWindow = window.open('', '_blank', 'width=800,height=600')
    if (!printWindow) {
      message.error('弹窗被浏览器拦截，请允许弹窗后重试')
      return
    }
    printWindow.document.write(html)
    printWindow.document.close()
    printWindow.focus()
    printWindow.onload = () => {
      setTimeout(() => printWindow!.print(), 300)
    }
  }
}

/**
 * 导出内容到指定格式
 * @param content Markdown 格式内容
 * @param format 导出格式
 * @param title 文档标题（用于文件名和 .doc/.pdf 标题）
 */
export async function exportContent(content: string, format: ExportFormat, title: string = 'AI导出') {
  const safeName = title.replace(/[\\/:*?"<>|]/g, '_').substring(0, 60)

  try {
    switch (format) {
      case 'txt': {
        const text = markdownToPlainText(content)
        downloadBlob(text, `${safeName}.txt`, 'text/plain;charset=utf-8')
        break
      }
      case 'md': {
        downloadBlob(content, `${safeName}.md`, 'text/markdown;charset=utf-8')
        break
      }
      case 'doc': {
        const bodyHTML = simpleMarkdownToHTML(content)
        const docHTML = wrapAsDocHTML(title, bodyHTML)
        downloadBlob(docHTML, `${safeName}.doc`, 'application/msword;charset=utf-8')
        break
      }
      case 'pdf': {
        const bodyHTML = simpleMarkdownToHTML(content)
        await exportAsPDF(title, bodyHTML)
        break
      }
    }
    message.success(`已导出为 ${format.toUpperCase()} 格式`)
  } catch (e: any) {
    message.error('导出失败: ' + (e.message || '未知错误'))
  }
}

/** 导出格式选项配置 */
export const EXPORT_OPTIONS: { key: ExportFormat; label: string; icon: string }[] = [
  { key: 'txt', label: '纯文本 .txt', icon: '📄' },
  { key: 'md', label: 'Markdown .md', icon: '📝' },
  { key: 'doc', label: 'Word .doc', icon: '📘' },
  { key: 'pdf', label: 'PDF .pdf', icon: '📕' },
]
