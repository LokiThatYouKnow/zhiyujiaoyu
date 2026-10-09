import axios from 'axios'
import { message } from 'ant-design-vue'
import {
  ok, fail,
  mockStudents, mockTeachers, mockAdmins, mockAdminDTO,
  mockStudentMenus, mockTeacherMenus, mockAdminMenus,
  mockTeacherClickRecord, mockStudentClickRecord, mockCostTimeRecord,
  mockIssueCorrectRecords, mockKnowledgePointRecords,
  mockCozeResponses, mockIssueList, mockResourceList, mockTokens,
} from './data'
import type { R } from '../response/R'
// ===== Mock 状态管理 =====
const MOCK_KEY = 'useMock'

export function isMockEnabled(): boolean {
  const val = localStorage.getItem(MOCK_KEY)
  if (val === null) {
    // 默认使用 Real 模式，只有管理员在管理端手动开启 Mock
    localStorage.setItem(MOCK_KEY, 'false')
    return false
  }
  return val === 'true'
}

export function enableMock(): void {
  localStorage.setItem(MOCK_KEY, 'true')
  message.success('Mock 模式已启用', 1.5)
  setTimeout(() => location.reload(), 800)
}

export function disableMock(): void {
  localStorage.setItem(MOCK_KEY, 'false')
  message.info('Mock 模式已关闭', 1.5)
  setTimeout(() => location.reload(), 800)
}

export function toggleMock(): void {
  if (isMockEnabled()) { disableMock() } else { enableMock() }
}

function delay(ms = 300): Promise<void> {
  return new Promise(resolve => setTimeout(resolve, ms + Math.random() * 200))
}

// ===== URL 匹配 =====
interface MockRule {
  method: string
  pattern: string
  handler: (url: string, data?: unknown) => R | Promise<R>
}


function matchPattern(pattern: string, url: string): boolean {
  const cleanUrl = url.replace(/^\/api/, '') || '/'
  const regexStr = pattern.replace(/\//g, '\\/').replace(/\{[^}]+\}/g, '[^/]+')
  return new RegExp(`^${regexStr}(\\?.*)?$`).test(cleanUrl)
}

function getCleanUrl(url: string): string {
  return url.replace(/^\/api/, '') || '/'
}

// ===== Mock 规则表（匹配后端真实返回格式）=====
const mockRules: MockRule[] = [
  // ------ 统一登录 & 验证码 ------
  {
    method: 'GET', pattern: '/auth/captcha',
    handler: () => {
      // 生成简单 SVG 验证码图片的 base64
      const svgContent = `<svg xmlns="http://www.w3.org/2000/svg" width="200" height="80" viewBox="0 0 200 80">
  <rect width="200" height="80" fill="#FAF7F2" rx="4"/>
  <line x1="20" y1="15" x2="180" y2="25" stroke="#D4C5B2" stroke-width="1.5"/>
  <line x1="15" y1="40" x2="170" y2="35" stroke="#C4A35A" stroke-width="1"/>
  <line x1="30" y1="55" x2="185" y2="65" stroke="#D4C5B2" stroke-width="1.5"/>
  <line x1="10" y1="70" x2="160" y2="60" stroke="#8B5E3C" stroke-width="1"/>
  <circle cx="45" cy="18" r="2" fill="#8B7355"/>
  <circle cx="120" cy="50" r="1.5" fill="#8B7355"/>
  <circle cx="160" cy="22" r="2" fill="#8B7355"/>
  <circle cx="85" cy="70" r="1.5" fill="#8B7355"/>
  <text x="30" y="52" font-family="Georgia,serif" font-size="36" font-weight="bold" fill="#3D3226" transform="rotate(-8, 30, 52)">1</text>
  <text x="70" y="48" font-family="Georgia,serif" font-size="34" font-weight="bold" fill="#6B3A2A" transform="rotate(5, 70, 48)">2</text>
  <text x="110" y="55" font-family="Georgia,serif" font-size="36" font-weight="bold" fill="#8B5E3C" transform="rotate(-3, 110, 55)">3</text>
  <text x="150" y="46" font-family="Georgia,serif" font-size="34" font-weight="bold" fill="#3D3226" transform="rotate(7, 150, 46)">4</text>
</svg>`
      const base64 = btoa(unescape(encodeURIComponent(svgContent)))
      return ok({
        captchaKey: 'mock-captcha-key',
        captchaImage: 'data:image/svg+xml;base64,' + base64,
      })
    },
  },
  {
    method: 'POST', pattern: '/auth/login',
    handler: (_url, data) => {
      const d = data as { username?: string; password?: string; captchaKey?: string; captchaCode?: string } | undefined
      const username = d?.username || ''
      const password = d?.password || ''
      const captchaCode = d?.captchaCode || ''

      // 验证码检查（mock 模式默认答案为 1234）
      if (captchaCode !== '1234') {
        return fail('验证码错误')
      }

      // 依次查找学生、教师、管理员
      const student = mockStudents.find(s => s.student_account === username && s.student_password === password)
      if (student) return ok(mockTokens.student, '登录成功')

      const teacher = mockTeachers.find(t => t.teacher_account === username && t.teacher_password === password)
      if (teacher) return ok(mockTokens.teacher, '登录成功')

      const admin = mockAdmins.find(a => a.username === username && a.password === password)
      if (admin) return ok(mockTokens.admin, '登录成功')

      return fail('账号或密码错误')
    },
  },

  // ------ 登录（返回纯 token 字符串）------
  {
    method: 'POST', pattern: '/student/login',
    handler: (_url, data) => {
      const d = data as { student_account?: string; student_password?: string } | undefined
      const account = d?.student_account || 'student01'
      const pwd = d?.student_password || '123456'
      const found = mockStudents.find(s => s.student_account === account && s.student_password === pwd)
      if (found) return ok(mockTokens.student, '登录成功')
      return fail('账号或密码错误')
    },
  },
  {
    method: 'POST', pattern: '/teacher/login',
    handler: (_url, data) => {
      const d = data as { teacher_account?: string; teacher_password?: string } | undefined
      const account = d?.teacher_account || 'teacher01'
      const pwd = d?.teacher_password || '123456'
      const found = mockTeachers.find(t => t.teacher_account === account && t.teacher_password === pwd)
      if (found) return ok(mockTokens.teacher, '登录成功')
      return fail('账号或密码错误')
    },
  },
  {
    method: 'POST', pattern: '/admin/login',
    handler: (_url, data) => {
      const d = data as { username?: string; password?: string } | undefined
      const name = d?.username || 'admin'
      const pwd = d?.password || 'admin123'
      const found = mockAdmins.find(a => a.username === name && a.password === pwd)
      if (found) return ok(mockTokens.admin, '登录成功')
      return fail('账号或密码错误')
    },
  },

  // ------ 短信验证码 ------
  {
    method: 'POST', pattern: '/sms/send',
    handler: (_url, data) => {
      const d = data as { phone?: string } | undefined
      const phone = d?.phone || ''
      // 简单验证手机号格式（11位，1开头）
      if (!/^1\d{10}$/.test(phone)) {
        return fail('手机号格式不正确')
      }
      console.log(`🎭 [Mock] 验证码已发送到 ${phone}，验证码：123456`)
      return ok(null, '验证码已发送')
    },
  },
  {
    method: 'POST', pattern: '/sms/verify',
    handler: (_url, data) => {
      const d = data as { phone?: string; code?: string } | undefined
      const code = d?.code || ''
      // Mock 模式下任意 4-6 位验证码均可通过（方便测试）
      if (code.length >= 4 && code.length <= 6) {
        return ok(null, '验证成功')
      }
      return fail('验证码错误')
    },
  },

  // ------ 注册 ------
  { method: 'POST', pattern: '/student/register', handler: () => ok(null, '注册成功，请登录') },
  { method: 'POST', pattern: '/teacher/register', handler: () => ok(null, '注册成功，请登录') },
  { method: 'POST', pattern: '/admin/register', handler: () => ok(null, '注册成功，请登录') },

  // ------ 登出 ------
  { method: 'POST', pattern: '/admin/loginOut', handler: () => ok(null, '已退出登录') },
  { method: 'POST', pattern: '/student/loginOut', handler: () => ok(null, '已退出登录') },
  { method: 'POST', pattern: '/teacher/loginOut', handler: () => ok(null, '已退出登录') },

  // ------ 菜单（返回 List<MenuPO>）------
  { method: 'GET', pattern: '/student/getMenu', handler: () => ok(mockStudentMenus) },
  { method: 'GET', pattern: '/teacher/getMenu', handler: () => ok(mockTeacherMenus) },
  { method: 'GET', pattern: '/admin/getMenu', handler: () => ok(mockAdminMenus) },

  // ------ 个人信息（返回 DTO）------
  { method: 'GET', pattern: '/student/getSelfStudentDetail', handler: (_url, _data) => {
    // 通过 query params 获取 student_id
    const clean = getCleanUrl(_url)
    const urlObj = new URL(clean, 'http://localhost')
    const sid = urlObj.searchParams.get('student_id') || '2024001'
    const found = mockStudents.find(s => String(s.student_id) === sid) || mockStudents[0]
    return ok(found)
  }},
  { method: 'GET', pattern: '/teacher/getSelfTeacherDetail', handler: (_url, _data) => {
    const clean = getCleanUrl(_url)
    const urlObj = new URL(clean, 'http://localhost')
    const tid = urlObj.searchParams.get('teacher_id') || '1001'
    const found = mockTeachers.find(t => String(t.teacher_id) === tid) || mockTeachers[0]
    return ok(found)
  }},
  { method: 'GET', pattern: '/admin/getSelfAdminDetail', handler: () => ok(mockAdminDTO) },

  // ------ 学生：加入课程/班级 ------
  { method: 'POST', pattern: '/student/joinCourse', handler: (_url, _data) => {
    return ok(null, '加入课程成功')
  }},
  { method: 'POST', pattern: '/student/joinClass', handler: (_url, _data) => {
    return ok(null, '加入班级成功')
  }},

  // ------ Coze AI 接口（返回 List<Map<String,String>>）------
  { method: 'POST', pattern: '/student/ai/coursePractise', handler: () => ok(mockCozeResponses.coursePractise) },
  { method: 'POST', pattern: '/student/ai/coursePractiseCorrectAnswer', handler: () => ok(mockCozeResponses.coursePractiseCorrect) },
  { method: 'POST', pattern: '/student/ai/answer', handler: () => ok(mockCozeResponses.answer) },
  { method: 'POST', pattern: '/student/ai/selfPractise', handler: () => ok(mockCozeResponses.selfPractise) },
  { method: 'POST', pattern: '/student/ai/selfPractiseCorrect', handler: () => ok(mockCozeResponses.selfPractiseCorrect) },
  { method: 'POST', pattern: '/teacher/ai/knowledgeOrganization', handler: () => ok(mockCozeResponses.knowledgeOrganization) },
  { method: 'POST', pattern: '/teacher/ai/practiseDesign', handler: () => ok(mockCozeResponses.practiseDesign) },
  { method: 'POST', pattern: '/teacher/ai/designIssue', handler: () => ok(mockCozeResponses.designIssue) },
  { method: 'POST', pattern: '/teacher/ai/arrangeTime', handler: () => ok(mockCozeResponses.arrangeTime) },
  { method: 'POST', pattern: '/teacher/ai/searchCourseware', handler: (_url, data) => {
    const d = data as { course_name?: string; search_prompt?: string } | undefined
    const course = d?.course_name || '未命名课程'
    const prompt = d?.search_prompt || '综合搜索'
    return ok([{
      coursewareContent: `# ${course} - 教学课件

## 一、课程概述
本课程是${course}的系统教学方案，旨在帮助学生全面掌握${course}的核心概念和实践技能。课程结合理论讲解与实训操作，适合高职/本科层次学生。

> **教学目标**：掌握${course}的基础理论、核心方法和实践应用能力
> **适用对象**：计算机/信息技术相关专业学生
> **参考学时**：48学时（理论24 + 实践24）

## 二、知识体系
1. **基础概念模块**
   - 课程背景与发展历程
   - 核心术语与定义
   - 基本原理解析
2. **核心技术模块**
   - 关键算法/方法讲解
   - 技术架构与实现
   - 常见问题与解决方案
3. **实践应用模块**
   - 典型案例分析
   - 项目实战演练
   - 行业应用场景
4. **进阶拓展模块**
   - 前沿技术动态
   - 相关领域交叉
   - 深入研究方向

## 三、详细教学内容

### 模块一：基础概念（8学时）

#### 1.1 课程导论（2学时）
${course}作为信息技术领域的核心课程，涵盖了从基础理论到高级应用的完整知识链。本部分介绍课程背景、学习路线和考核方式。

**重点**：理解课程的知识体系结构
**难点**：建立整体学习框架

#### 1.2 核心概念讲解（4学时）
详细讲解${course}的核心概念，通过图示、类比等方式帮助学生建立直观理解。

**案例**：以实际场景为例，演示核心概念的应用方式

#### 1.3 基础原理（2学时）
深入分析${course}的基本工作原理，为后续学习奠定理论基础。

### 模块二：核心技术（16学时）

#### 2.1 核心方法（6学时）
系统讲解${course}中的关键方法和技术路线，包含详细的操作步骤和代码示例。

\`\`\`
步骤1：分析需求 → 步骤2：设计方案 → 步骤3：实现验证 → 步骤4：优化迭代
\`\`\`

**重点**：掌握方法的选择依据和使用场景
**难点**：复杂场景下的方法组合运用

#### 2.2 架构设计（6学时）
从系统层面讲解架构设计原则、设计模式及其在${course}中的应用。

#### 2.3 常见问题与优化（4学时）
总结实践中常见的问题、排查方法和性能优化策略。

### 模块三：实践应用（16学时）

#### 3.1 案例分析（4学时）
分析3-5个真实案例，展示${course}在不同场景下的应用方法。

#### 3.2 项目实训（8学时）
学生动手完成一个完整的项目，从需求分析到最终成果展示。

#### 3.3 行业应用（4学时）
介绍${course}在行业中的实际应用情况和就业前景。

### 模块四：进阶拓展（8学时）

#### 4.1 前沿动态（4学时）
介绍${course}相关的最新技术趋势和发展方向。

#### 4.2 综合复习（4学时）
全课程知识体系的回顾与梳理，答疑解惑。

## 四、课后习题

### 习题1：概念理解
简述${course}的核心概念及其在实际开发中的重要性。
> **参考答案**：${course}的核心概念包括...

### 习题2：方法比较
对比分析${course}中2-3种常用方法的优缺点及适用场景。
> **参考答案**：各方法的对比如下...

### 习题3：案例分析
选择一个你熟悉的场景，分析如何运用${course}的知识解决实际问题。
> **参考答案**：以XX场景为例...

### 习题4：实践设计
设计一个基于${course}的小型项目方案，包含技术选型和实施步骤。
> **参考答案**：建议采用以下方案...

### 习题5：综合思考
谈谈你对${course}未来发展的看法。
> **参考答案**：开放性问题，鼓励学生独立思考...

## 五、参考资料
1. 网络搜索关键词：**${prompt}**
2. 课程相关资源链接（由AI整合搜索结果提供）
3. 推荐教材和在线课程资源

---
> 本课件由 AI 根据网络搜索结果自动整合生成，教师可根据实际需要进一步调整和完善。搜索关键词：\`${course} ${prompt}\`，共整合了多个网络来源的内容。`,
      searchQuery: `${course} ${prompt} 教学课件`,
      resultCount: 5,
    }])
  }},

  // ------ 教师：课程/资源管理 ------
  { method: 'POST', pattern: '/teacher/ai/getIssueList', handler: () => ok(mockIssueList) },
  { method: 'POST', pattern: '/teacher/ai/getResourceList', handler: () => ok(mockResourceList) },
  { method: 'POST', pattern: '/teacher/ai/getIssueAnswerDetail', handler: () => ok(mockCozeResponses.answer) },
  { method: 'POST', pattern: '/teacher/ai/deleteCourse', handler: () => ok(null, '课程已删除') },
  { method: 'POST', pattern: '/teacher/ai/deleteIssue', handler: () => ok(null, '题目已删除') },
  { method: 'POST', pattern: '/teacher/ai/deleteResource', handler: () => ok(null, '资源已删除') },

  // ------ 教师：数据分析 ------
  { method: 'GET', pattern: '/teacher/getIssueCorrectAVGRecord', handler: () => ok(mockIssueCorrectRecords) },
  { method: 'GET', pattern: '/teacher/getKnowledgePointCommendDegreeRecord', handler: () => ok(mockKnowledgePointRecords) },

  // ------ 管理员：CRUD ------
  { method: 'POST', pattern: '/admin/addStudent', handler: () => ok(null, '学生添加成功') },
  { method: 'POST', pattern: '/admin/addTeacher', handler: () => ok(null, '教师添加成功') },
  { method: 'POST', pattern: '/admin/addAdmin', handler: () => ok(null, '管理员添加成功') },
  { method: 'PUT', pattern: '/admin/updateStudent', handler: () => ok(null, '学生信息已更新') },
  { method: 'PUT', pattern: '/admin/updateTeacher', handler: () => ok(null, '教师信息已更新') },
  { method: 'PUT', pattern: '/admin/updateAdmin', handler: () => ok(null, '管理员信息已更新') },
  { method: 'DELETE', pattern: '/admin/deleteStudentByUsername/', handler: () => ok(null, '学生已删除') },
  { method: 'DELETE', pattern: '/admin/deleteTeacherByUsername/', handler: () => ok(null, '教师已删除') },
  { method: 'DELETE', pattern: '/admin/deleteAdminByUsername/', handler: () => ok(null, '管理员已删除') },
  { method: 'DELETE', pattern: '/admin/deleteStudentBySelected', handler: () => ok(null, '批量删除完成') },
  { method: 'DELETE', pattern: '/admin/deleteTeacherBySelected', handler: () => ok(null, '批量删除完成') },
  { method: 'DELETE', pattern: '/admin/deleteAdminBySelected', handler: () => ok(null, '批量删除完成') },

  // ------ 管理员：资源管理 ------
  { method: 'POST', pattern: '/admin/ai/getIssueList', handler: () => ok(mockIssueList) },
  { method: 'POST', pattern: '/admin/ai/getResourceList', handler: () => ok(mockResourceList) },
  { method: 'POST', pattern: '/admin/ai/deleteCourse', handler: () => ok(null, '课程已删除') },
  { method: 'POST', pattern: '/admin/ai/deleteIssue', handler: () => ok(null, '题目已删除') },
  { method: 'POST', pattern: '/admin/ai/deleteResource', handler: () => ok(null, '资源已删除') },

  // ------ 管理员：数据分析（返回汇总 PO/DTO）------
  {
    method: 'GET', pattern: '/admin/getAllTeacherClickRecord',
    handler: () => ok(mockTeacherClickRecord),
  },
  {
    method: 'GET', pattern: '/admin/getAllStudentClickRecord',
    handler: () => ok(mockStudentClickRecord),
  },
  {
    method: 'GET', pattern: '/admin/getAllCostTimeRecord',
    handler: () => ok(mockCostTimeRecord),
  },
  {
    method: 'GET', pattern: '/admin/getIssueCorrectAVGRecord',
    handler: () => ok(mockIssueCorrectRecords),
  },
  {
    method: 'GET', pattern: '/admin/getKnowledgePointCommendDegreeRecord',
    handler: () => ok(mockKnowledgePointRecords),
  },

  // ------ 文件转换 ------
  {
    method: 'POST', pattern: '/convert/txt',
    handler: () => {
      const blob = new Blob(['Mock 转换结果：这是转换后的文本内容。'], { type: 'text/plain' })
      return { code: 200, msg: 'success', data: blob as unknown } as R
    },
  },
  {
    method: 'POST', pattern: '/convert/doc',
    handler: () => {
      const blob = new Blob(['Mock DOC 转换结果'], { type: 'application/msword' })
      return { code: 200, msg: 'success', data: blob as unknown } as R
    },
  },
  {
    method: 'POST', pattern: '/convert/docx',
    handler: () => {
      const blob = new Blob(['Mock DOCX 转换结果'], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' })
      return { code: 200, msg: 'success', data: blob as unknown } as R
    },
  },

  // ------ AI 通用聊天 ------
  { method: 'GET', pattern: '/teacher/models', handler: () => ok(mockModelList) },
  { method: 'GET', pattern: '/student/models', handler: () => ok(mockModelList) },
  {
    method: 'POST', pattern: '/teacher/chat',
    handler: (_url, data) => {
      const body = (data || {}) as Record<string, unknown>
      const messages = (body.messages || []) as Array<{ role: string; content: string }>
      const lastMsg = messages.length > 0 ? messages[messages.length - 1].content : '你好'
      return ok({ content: `[Mock] 这是对「${lastMsg.slice(0, 30)}」的模拟回复。在实际部署中，这里将由 DeepSeek 大模型生成回答。`, model: body.model || 'deepseek-v4-pro' })
    },
  },
  {
    method: 'POST', pattern: '/student/chat',
    handler: (_url, data) => {
      const body = (data || {}) as Record<string, unknown>
      const messages = (body.messages || []) as Array<{ role: string; content: string }>
      const lastMsg = messages.length > 0 ? messages[messages.length - 1].content : '你好'
      return ok({ content: `[Mock] 这是对「${lastMsg.slice(0, 30)}」的模拟回复。在实际部署中，这里将由 DeepSeek 大模型生成回答。`, model: body.model || 'deepseek-v4-pro' })
    },
  },
]

// ===== 查找匹配的 Mock 规则 =====
function findMockRule(method: string, url: string): MockRule | undefined {
  const cleanUrl = getCleanUrl(url)
  const upperMethod = method.toUpperCase()
  return mockRules.find(rule => rule.method.toUpperCase() === upperMethod && matchPattern(rule.pattern, cleanUrl))
}

// ===== 安装 Mock 拦截器 =====
export function setupMock(): void {
  if (!isMockEnabled()) return

  // 清理脏 token
  const token = (sessionStorage.getItem('token') || localStorage.getItem('token'))
  if (token && (token === '[object Object]' || token.split('.').length < 3)) {
    console.log('🎭 [Mock] 检测到无效 token，已自动清理')
    localStorage.removeItem('token')
    localStorage.removeItem('isAuthenticated')
    sessionStorage.removeItem('isAuthenticated')
  }

  console.log('🎭 [Mock] Mock 模式已激活 - 所有 API 调用将返回模拟数据')

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const originalAdapter: any = axios.defaults.adapter

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  axios.defaults.adapter = async function (config: any) {
    const url = config.url || ''
    const method = config.method || 'get'
    const rule = findMockRule(method, url)

    if (rule) {
      console.log(`🎭 [Mock] ${method.toUpperCase()} ${url} → mock`)
      await delay(300)

      try {
        let requestData: unknown = undefined
        if (typeof config.data === 'string') {
          try { requestData = JSON.parse(config.data) } catch { requestData = config.data }
        } else {
          requestData = config.data
        }

        const result = await rule.handler(url, requestData)

        return {
          data: result,
          status: 200,
          statusText: 'OK',
          headers: { 'content-type': 'application/json', 'x-mock': 'true' },
          config,
          request: {},
        }
      } catch (err) {
        console.error(`🎭 [Mock] Error handling ${method} ${url}:`, err)
        return {
          data: fail('Mock 处理出错'),
          status: 500, statusText: 'Mock Error',
          headers: { 'content-type': 'application/json' },
          config, request: {},
        }
      }
    }

    // 未匹配：真实网络请求
    console.log(`🌐 [Real] ${method.toUpperCase()} ${url}`)
    if (originalAdapter) return originalAdapter(config)
    throw new Error('No adapter available')
  }

  console.log('🎭 [Mock] 已拦截 ' + mockRules.length + ' 条 API 规则')
}
