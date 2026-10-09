export function convertToHtml(text: string): string {
  let result = text;
  
  // 1. 先将所有空格转换为HTML实体，避免被浏览器合并
  result = result.replace(/ /g, '&nbsp;');
  
  // 2. 标题转换：#### → <h1>，##### → <h2>，- **内容** → <h3>
  result = result
    .replace(/^#### (.*)$/gm, '<h1>$1</h1>')
    .replace(/^##### (.*)$/gm, '<h2>$1</h2>')
    .replace(/^- \*\*(.*?)\*\*$/gm, '<h3>$1</h3>');
  
  // 3. 列表转换（先处理列表项再还原空格）
  result = wrapListItems(result, /^- (.*)$/gm, '<li>$1</li>');
  result = wrapListItems(result, /^(\d+)\. (.*)$/gm, '<li>$2</li>');
  
  // 4. 加粗转换
  result = result.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
  
  // 5. 换行符处理（先处理连续换行，再处理单个换行）
  result = result.replace(/\n{3,}/g, '</p>\n<p>\n'); // 3个以上换行符
  result = result.replace(/\n{2}/g, '</p>\n<p>');    // 2个换行符
  result = result.replace(/\n/g, '<br/>');           // 单个换行符
  
  // 6. 段落标签完整性
  if (!result.startsWith('<p>')) result = '<p>' + result;
  if (!result.endsWith('</p>')) result = result + '</p>';
  
  // 7. 优化标题与段落间距
  result = result.replace(/<h2>/g, '</p><h2>');
  result = result.replace(/<h3>/g, '</p><h3>');
  
  // 8. 处理列表与段落边界
  result = result.replace(/<\/p><(ul|ol)>/g, '<$1>');
  result = result.replace(/<(ul|ol)><p>/g, '<$1>');
  result = result.replace(/<\/(ul|ol)><p>/g, '</$1>');
  
  // 9. 还原列表项中的&nbsp;为空格（避免列表内容空格过多）
  result = result.replace(/<(li|h1|h2|h3)>(.*?)&nbsp;(.*?)<\/\1>/g, '<$1>$2 $3</$1>');
  
  return result;
}

// 辅助函数：包裹列表项（优化空格处理）
function wrapListItems(text: string, regex: RegExp, replacement: string): string {
  let result = text;
  const listItems = [];
  
  // 收集列表项
  let match;
  while ((match = regex.exec(text)) !== null) {
    listItems.push({
      text: match[0],
      index: regex.lastIndex - match[0].length,
      isOrdered: /^\d+\./.test(match[0])
    });
  }
  
  // 处理列表包裹
  for (let i = listItems.length - 1; i >= 0; i--) {
    const { index, isOrdered } = listItems[i];
    let listEnd = index;
    
    // 查找列表结束位置
    while (listEnd < text.length) {
      const lineEnd = text.indexOf('\n', listEnd);
      const line = lineEnd === -1 ? text.substring(listEnd) : text.substring(listEnd, lineEnd);
      
      if (!regex.test(line.trim().replace(/&nbsp;/g, ' '))) break;
      listEnd = lineEnd === -1 ? text.length : lineEnd + 1;
    }
    
    // 转换列表项并还原空格
    const listContent = text.substring(index, listEnd)
      .split('\n')
      .map(line => line.trim().replace(/&nbsp;/g, ' '))
      .filter(line => line !== '' && regex.test(line))
      .map(line => line.replace(regex, replacement.replace(/&nbsp;/g, ' ')))
      .join('');
    
    if (listContent === '') continue;
    
    // 包裹列表标签
    const listTag = isOrdered ? '<ol>' : '<ul>';
    const endTag = isOrdered ? '</ol>' : '</ul>';
    
    result = 
      result.substring(0, index) + 
      listTag + listContent + endTag + 
      result.substring(listEnd);
  }
  
  return result;
}