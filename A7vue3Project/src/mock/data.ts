import type { menu } from '../modules/menu'

// ===== 通用响应工具 =====
export interface R<T = unknown> {
  code: number
  msg: string
  data: T | null
}

export function ok<T>(data: T, msg = 'success'): R<T> {
  return { code: 200, msg, data }
}
export function fail(msg = 'error', code = 500): R<null> {
  return { code, msg, data: null }
}

// ===== Base64 工具 (Unicode 安全) =====
function toBase64(str: string): string {
  const bytes = new TextEncoder().encode(str)
  let binary = ''
  bytes.forEach(b => binary += String.fromCharCode(b))
  return btoa(binary)
}

function makeMockToken(payload: Record<string, unknown>): string {
  const header = toBase64(JSON.stringify({ alg: 'HS256', typ: 'JWT' }))
  const body = toBase64(JSON.stringify({ ...payload, iat: Math.floor(Date.now() / 1000), exp: Math.floor(Date.now() / 1000) + 86400 }))
  return `${header}.${body}.mock_signature`
}

// ===== Mock 课程数据 =====
export interface MockCoursePO {
  course_id: number
  course_name: string
  course_teacher_id: number
  course_teacher_name: string
  course_teacher_account: string
  course_student_number: number
  course_hours: string
  course_score: string
  course_invite_code: string
  course_document_id: string
  minio_id: string
  create_time: string
  update_time: string
}

export const mockCourses: MockCoursePO[] = [
  {
    course_id: 1, course_name: '数据结构与算法', course_teacher_id: 1001,
    course_teacher_name: '张明远', course_teacher_account: 'teacher01',
    course_student_number: 45, course_hours: '64', course_score: '4.0',
    course_invite_code: 'DST2024001', course_document_id: 'coze_doc_ds001', minio_id: 'minio_c1',
    create_time: '2024-09-01 08:00:00', update_time: '2024-09-01 08:00:00',
  },
  {
    course_id: 2, course_name: '操作系统原理', course_teacher_id: 1001,
    course_teacher_name: '张明远', course_teacher_account: 'teacher01',
    course_student_number: 38, course_hours: '72', course_score: '4.5',
    course_invite_code: 'OS2024002', course_document_id: 'coze_doc_os002', minio_id: 'minio_c2',
    create_time: '2024-09-01 08:00:00', update_time: '2024-09-01 08:00:00',
  },
  {
    course_id: 3, course_name: '计算机组成原理', course_teacher_id: 1001,
    course_teacher_name: '张明远', course_teacher_account: 'teacher01',
    course_student_number: 42, course_hours: '56', course_score: '3.5',
    course_invite_code: 'CO2024003', course_document_id: 'coze_doc_co003', minio_id: 'minio_c3',
    create_time: '2024-09-01 08:00:00', update_time: '2024-09-01 08:00:00',
  },
  {
    course_id: 4, course_name: '人工智能导论', course_teacher_id: 1002,
    course_teacher_name: '李慧芳', course_teacher_account: 'teacher02',
    course_student_number: 60, course_hours: '48', course_score: '3.0',
    course_invite_code: 'AI2024004', course_document_id: 'coze_doc_ai004', minio_id: 'minio_c4',
    create_time: '2024-09-01 08:00:00', update_time: '2024-09-01 08:00:00',
  },
  {
    course_id: 5, course_name: '软件工程', course_teacher_id: 1002,
    course_teacher_name: '李慧芳', course_teacher_account: 'teacher02',
    course_student_number: 55, course_hours: '48', course_score: '3.5',
    course_invite_code: 'SE2024005', course_document_id: 'coze_doc_se005', minio_id: 'minio_c5',
    create_time: '2024-09-01 08:00:00', update_time: '2024-09-01 08:00:00',
  },
]

// ===== Mock 学生数据 (StudentDTO 格式) =====
export interface MockStudentDTO {
  student_id: number
  student_account: string
  student_password: string
  student_name: string
  student_gender: string
  student_age: string
  student_major: string
  student_class_name: string
  teacher_binding_code: string
  student_classList: MockCoursePO[]
  create_time: string
  update_time: string
}

export const mockStudents: MockStudentDTO[] = [
  {
    student_id: 2024001, student_account: 'student01', student_password: '123456',
    student_name: '王小明', student_gender: '男', student_age: '20',
    student_major: '计算机科学与技术', student_class_name: '计科2401班',
    teacher_binding_code: 'TB2024001',
    student_classList: [mockCourses[0], mockCourses[1], mockCourses[3]],
    create_time: '2024-09-01 08:00:00', update_time: '2024-09-01 08:00:00',
  },
  {
    student_id: 2024002, student_account: 'student02', student_password: '123456',
    student_name: '李晓红', student_gender: '女', student_age: '21',
    student_major: '软件工程', student_class_name: '软工2402班',
    teacher_binding_code: 'TB2024002',
    student_classList: [mockCourses[2], mockCourses[3], mockCourses[4]],
    create_time: '2024-09-01 08:00:00', update_time: '2024-09-01 08:00:00',
  },
  {
    student_id: 2024003, student_account: 'student03', student_password: '123456',
    student_name: '赵大伟', student_gender: '男', student_age: '20',
    student_major: '数据科学与大数据技术', student_class_name: '计科2401班',
    teacher_binding_code: 'TB2024001',
    student_classList: [mockCourses[0], mockCourses[2], mockCourses[4]],
    create_time: '2024-09-01 08:00:00', update_time: '2024-09-01 08:00:00',
  },
  {
    student_id: 2024004, student_account: 'student04', student_password: '123456',
    student_name: '陈美丽', student_gender: '女', student_age: '22',
    student_major: '人工智能', student_class_name: '人工智能2401班',
    teacher_binding_code: 'TB2024002',
    student_classList: [mockCourses[1], mockCourses[3]],
    create_time: '2024-09-01 08:00:00', update_time: '2024-09-01 08:00:00',
  },
  {
    student_id: 2024005, student_account: 'student05', student_password: '123456',
    student_name: '刘浩然', student_gender: '男', student_age: '19',
    student_major: '计算机科学与技术', student_class_name: '计科2401班',
    teacher_binding_code: 'TB2024001',
    student_classList: [mockCourses[0], mockCourses[1]],
    create_time: '2024-09-01 08:00:00', update_time: '2024-09-01 08:00:00',
  },
  {
    student_id: 2024006, student_account: 'student06', student_password: '123456',
    student_name: '周思雨', student_gender: '女', student_age: '20',
    student_major: '软件工程', student_class_name: '软工2402班',
    teacher_binding_code: 'TB2024002',
    student_classList: [mockCourses[3], mockCourses[4]],
    create_time: '2024-09-01 08:00:00', update_time: '2024-09-01 08:00:00',
  },
]

// ===== Mock 教师数据 (TeacherDTO 格式) =====
export interface MockTeacherDTO {
  teacher_id: number
  teacher_account: string
  teacher_password: string
  teacher_name: string
  teacher_gender: string
  teacher_age: string
  teacher_major: string
  teacher_position: string
  teacher_class_name: string
  teacher_class_student_number: string
  teacher_binding_code: string
  teacher_studentList: MockStudentDTO[]
  teacher_courseList: MockCoursePO[]
  teacher_courseResource: string[]
  create_time: string
  update_time: string
}

export const mockTeachers: MockTeacherDTO[] = [
  {
    teacher_id: 1001, teacher_account: 'teacher01', teacher_password: '123456',
    teacher_name: '张明远', teacher_gender: '男', teacher_age: '45',
    teacher_major: '计算机科学与技术', teacher_position: '教授',
    teacher_class_name: '计科2401班', teacher_class_student_number: '45',
    teacher_binding_code: 'TB2024001',
    teacher_studentList: [mockStudents[0], mockStudents[2], mockStudents[4]],
    teacher_courseList: [mockCourses[0], mockCourses[1], mockCourses[2]],
    teacher_courseResource: [
      'http://192.168.240.131:9000/default-bucket/ds_ppt.pdf',
      'http://192.168.240.131:9000/default-bucket/os_lab_guide.pdf',
      'http://192.168.240.131:9000/default-bucket/final_exam_2024.pdf',
    ],
    create_time: '2023-03-01 08:00:00', update_time: '2024-09-01 08:00:00',
  },
  {
    teacher_id: 1002, teacher_account: 'teacher02', teacher_password: '123456',
    teacher_name: '李慧芳', teacher_gender: '女', teacher_age: '42',
    teacher_major: '软件工程', teacher_position: '副教授',
    teacher_class_name: '软工2402班', teacher_class_student_number: '52',
    teacher_binding_code: 'TB2024002',
    teacher_studentList: [mockStudents[1], mockStudents[3], mockStudents[5]],
    teacher_courseList: [mockCourses[3], mockCourses[4]],
    teacher_courseResource: [
      'http://192.168.240.131:9000/default-bucket/ai_intro.pptx',
    ],
    create_time: '2022-07-01 08:00:00', update_time: '2024-09-01 08:00:00',
  },
]

// ===== Mock 管理员数据 (AdminVO 格式) =====
export interface MockAdminVO {
  id: number
  username: string
  password: string
  name: string
  create_time: string
  update_time: string
}

export const mockAdmins: MockAdminVO[] = [
  { id: 1, username: 'admin', password: 'admin123', name: '系统管理员', create_time: '2024-01-01 00:00:00', update_time: '2024-01-01 00:00:00' },
  { id: 2, username: 'admin02', password: 'admin123', name: '教务管理员', create_time: '2024-03-01 00:00:00', update_time: '2024-03-01 00:00:00' },
]

// ===== AdminDTO (getSelfAdminDetail 返回) =====
export interface MockAdminDTO {
  admin_id: number
  admin_account: string
  admin_password: string
  admin_name: string
  all_admin_list: MockAdminVO[]
  all_teacher_list: MockTeacherDTO[]
  all_student_list: MockStudentDTO[]
  create_time: string
  update_time: string
}

export const mockAdminDTO: MockAdminDTO = {
  admin_id: 1, admin_account: 'admin', admin_password: 'admin123', admin_name: '系统管理员',
  all_admin_list: mockAdmins,
  all_teacher_list: mockTeachers,
  all_student_list: mockStudents,
  create_time: '2024-01-01 00:00:00', update_time: '2024-01-01 00:00:00',
}

// ===== Mock Menu =====
export const mockStudentMenus: menu[] = [
  { id: 1, name: '学生首页', icon: 'HomeOutlined', pid: 0, path: '/student/StudentHome', component: 'StudentHomeView' },
  { id: 2, name: '个人详情', icon: 'UserOutlined', pid: 0, path: '/student/SelfStudentDetail', component: 'SelfStudentDetail' },
  {
    id: 3, name: 'AI 助手', icon: 'RobotOutlined', pid: 0, path: '/student/AIHelper', component: '',
    childrenMenu: [
      { id: 31, name: '答疑解惑', icon: 'QuestionCircleOutlined', pid: 3, path: '/student/AIHelper/AnswerQuestion', component: 'AnswerQuestion' },
      { id: 32, name: '自主练习', icon: 'EditOutlined', pid: 3, path: '/student/AIHelper/SelfPractise', component: 'SelfPractise' },
    ],
  },
  { id: 4, name: '课程练习', icon: 'BookOutlined', pid: 0, path: '/student/CoursePractise', component: 'CoursePractise' },
]

export const mockTeacherMenus: menu[] = [
  { id: 1, name: '教师首页', icon: 'HomeOutlined', pid: 0, path: '/teacher/TeacherHome', component: 'TeacherHomeView' },
  { id: 2, name: '个人详情', icon: 'UserOutlined', pid: 0, path: '/teacher/SelfTeacherDetail', component: 'SelfTeacherDetail' },
  { id: 3, name: '课程列表', icon: 'BookOutlined', pid: 0, path: '/teacher/CourseList', component: 'CourseList' },
  { id: 4, name: '学生列表', icon: 'TeamOutlined', pid: 0, path: '/teacher/StudentList', component: 'StudentListT' },
  {
    id: 5, name: 'AI 工具', icon: 'ThunderboltOutlined', pid: 0, path: '/teacher/AIUse', component: '',
    childrenMenu: [
      { id: 51, name: '知识梳理', icon: 'FileTextOutlined', pid: 5, path: '/teacher/AIUse/KnowledgeOrganization', component: 'KnowledgeOrganization' },
      { id: 52, name: '实训设计', icon: 'ExperimentOutlined', pid: 5, path: '/teacher/AIUse/PractiseDesign', component: 'PractiseDesign' },
      { id: 53, name: '智能出题', icon: 'BulbOutlined', pid: 5, path: '/teacher/AIUse/DesignIssue', component: 'DesignIssue' },
    ],
  },
]

export const mockAdminMenus: menu[] = [
  { id: 1, name: '管理首页', icon: 'HomeOutlined', pid: 0, path: '/admin/HomeView', component: 'HomeView' },
  { id: 2, name: '数据分析', icon: 'BarChartOutlined', pid: 0, path: '/admin/DataAnalysis', component: 'DataAnalysis' },
  { id: 3, name: '资源管理', icon: 'FolderOutlined', pid: 0, path: '/admin/ResourceManager', component: 'ResourceManager' },
  {
    id: 4, name: '用户管理', icon: 'TeamOutlined', pid: 0, path: '/admin/UserManager', component: '',
    childrenMenu: [
      { id: 41, name: '学生管理', icon: 'UserOutlined', pid: 4, path: '/admin/UserManager/StudentList', component: 'StudentList' },
      { id: 42, name: '教师管理', icon: 'UserOutlined', pid: 4, path: '/admin/UserManager/TeacherList', component: 'TeacherList' },
      { id: 43, name: '管理员管理', icon: 'SafetyOutlined', pid: 4, path: '/admin/UserManager/AdminList', component: 'AdminList' },
    ],
  },
]

// ===== Mock JWT Tokens =====
export const mockTokens = {
  student: makeMockToken({ sub: '2024001', role: 'STUDENT', name: 'WangXiaoMing' }),
  teacher: makeMockToken({ sub: '1001', role: 'TEACHER', name: 'ZhangMingYuan' }),
  admin: makeMockToken({ sub: '1', role: 'ADMIN', name: 'Admin' }),
}

// ===== Coze AI 响应 (List<Map<String,String>> 格式) =====
export const mockCozeResponses = {
  knowledgeOrganization: [{ concludeData: `# 📚 数据结构与算法 - 知识体系梳理

## 一、线性结构
### 1.1 数组与链表
- **数组**：连续内存空间，支持 O(1) 随机访问，插入删除 O(n)
- **单链表**：节点包含数据域和指针域，插入删除 O(1)，查找 O(n)
- **双向链表**：增加前驱指针，支持双向遍历
- **循环链表**：尾节点指向头节点，适用于循环队列

### 1.2 栈与队列
- **栈 (Stack)**：LIFO 结构，应用场景包括函数调用、括号匹配、表达式求值
- **队列 (Queue)**：FIFO 结构，应用场景包括 BFS、任务调度、消息队列
- **双端队列 (Deque)**：两端均可入队出队

### 1.3 字符串
- **KMP 算法**：利用部分匹配表避免回溯，时间复杂度 O(n+m)
- **Trie 树**：高效字符串检索，应用于自动补全、拼写检查

## 二、树形结构
### 2.1 二叉树
- **遍历方式**：前序、中序、后序、层序
- **二叉搜索树 (BST)**：左 < 根 < 右，平均 O(log n)
- **平衡二叉树 (AVL)**：通过旋转保持平衡，|平衡因子| ≤ 1

### 2.2 堆与优先队列
- **大顶堆 / 小顶堆**：完全二叉树，堆顶为最大值/最小值
- **堆排序**：建堆 O(n) + 调整 O(n log n)
- **Top-K 问题**：小顶堆维护 K 个最大元素

## 三、图结构
### 3.1 图的表示
- **邻接矩阵**：O(V²) 空间，适合稠密图
- **邻接表**：O(V+E) 空间，适合稀疏图

### 3.2 图的遍历
- **DFS**：递归/栈实现，用于拓扑排序、连通分量
- **BFS**：队列实现，用于最短路径（无权图）

### 3.3 最短路径
- **Dijkstra**：贪心策略，O((V+E) log V)，不能处理负权边
- **Floyd-Warshall**：动态规划，O(V³)，可处理负权
- **Bellman-Ford**：O(VE)，可检测负权环

## 四、排序与查找
### 4.1 排序算法对比
| 算法 | 平均 | 最坏 | 空间 | 稳定 |
|------|------|------|------|------|
| 快速排序 | O(n log n) | O(n²) | O(log n) | ❌ |
| 归并排序 | O(n log n) | O(n log n) | O(n) | ✅ |
| 堆排序 | O(n log n) | O(n log n) | O(1) | ❌ |

### 4.2 查找算法
- **二分查找**：O(log n)，要求有序
- **哈希查找**：O(1) 平均，空间换时间
- **跳表**：O(log n)，概率性数据结构` }],

  practiseDesign: [{ practise: `# 📝 实训设计：链表反转

## 题目描述
给定一个单链表的头节点 \`head\`，请实现**原地反转**算法，并返回反转后的链表头节点。

## 难度等级
⭐⭐ 中等

## 输入输出示例
| 输入 | 输出 |
|------|------|
| 1→2→3→4→5→NULL | 5→4→3→2→1→NULL |
| 1→2→NULL | 2→1→NULL |
| NULL | NULL |

## 解题思路
### 方法一：迭代法（推荐）
使用三个指针 \`prev\`、\`curr\`、\`next\` 遍历链表：

\`\`\`java
public ListNode reverseList(ListNode head) {
    ListNode prev = null;
    ListNode curr = head;
    while (curr != null) {
        ListNode next = curr.next;  // 保存下一个节点
        curr.next = prev;           // 反转指针
        prev = curr;                // prev 前进
        curr = next;                // curr 前进
    }
    return prev;  // prev 即为新头节点
}
\`\`\`

**复杂度分析：**
- 时间复杂度：O(n)，遍历一次链表
- 空间复杂度：O(1)，仅使用常量额外空间

### 方法二：递归法
\`\`\`java
public ListNode reverseList(ListNode head) {
    if (head == null || head.next == null) return head;
    ListNode newHead = reverseList(head.next);
    head.next.next = head;  // 反转指向
    head.next = null;       // 断开原链接
    return newHead;
}
\`\`\`

## 评分标准
| 维度 | 分值 | 要求 |
|------|------|------|
| 正确性 | 40% | 所有测试用例通过 |
| 时间复杂度 | 25% | 达到 O(n) |
| 空间复杂度 | 15% | 达到 O(1) |
| 代码规范 | 20% | 命名清晰，注释完整 |

## 常见错误
1. ❌ 忘记保存 \`next\` 节点，导致链表断裂
2. ❌ 返回值错误（应返回 prev 而非 head）
3. ❌ 未处理空链表边界情况` }],

  designIssue: [{ decideAndAnswer: `# 📋 智能出题：二叉树遍历

## 一、选择题（每题 25 分）

### 第 1 题
对于如下二叉树，其中序遍历结果为：
\`\`\`
     A
    / \\
   B   C
  / \\
 D   E
\`\`\`
A. D B E A C ✅
B. A B D E C
C. D E B C A
D. B D E A C

### 第 2 题
具有 n 个节点的完全二叉树，其高度为：
A. n
B. log₂n
C. ⌊log₂n⌋ + 1 ✅
D. n/2

### 第 3 题
下列哪种数据结构最适合实现优先队列？
A. 栈
B. 二叉堆 ✅
C. 二叉搜索树
D. 队列

### 第 4 题
快速排序的最坏时间复杂度出现在：
A. 数组已经有序 ✅
B. 数组随机排列
C. 数组逆序
D. 所有元素相同

## 二、编程题（50 分）

### 题目：实现二叉树的层序遍历
给定一个二叉树，返回其按**层序遍历**得到的节点值（即逐层地，从左到右访问所有节点）。

**示例：**
\`\`\`
输入: [3,9,20,null,null,15,7]
    3
   / \\
  9  20
    /  \\
   15   7
输出: [[3],[9,20],[15,7]]
\`\`\`

## 答案与解析

### 选择题答案：A, C, B, A

### 编程题参考解答：
\`\`\`java
public List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        List<Integer> level = new ArrayList<>();
        for (int i = 0; i < levelSize; i++) {
            TreeNode node = queue.poll();
            level.add(node.val);
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        result.add(level);
    }
    return result;
}
\`\`\`
**复杂度：** 时间 O(n)，空间 O(n)` }],

  answer: [{ answerQuestion: `好的，我来为你详细解答这个问题。

## 问题分析
这是一个经典的**链表操作**问题，核心在于理解指针的重新指向过程。

## 核心思路
使用三个指针（\`prev\`、\`curr\`、\`next\`）遍历链表：
1. 保存当前节点的下一个节点（\`next\`）
2. 将当前节点的 \`next\` 指针指向 \`prev\`
3. \`prev\` 和 \`curr\` 都向前移动一步

## 图解过程
\`\`\`
初始:  NULL ← prev    curr → 1 → 2 → 3 → NULL
第1步:  NULL ← 1 ← prev    curr → 2 → 3 → NULL
第2步:  NULL ← 1 ← 2 ← prev    curr → 3 → NULL
第3步:  NULL ← 1 ← 2 ← 3 ← prev    curr = NULL
结束:  返回 prev (节点3)
\`\`\`

## 代码实现
\`\`\`java
public ListNode reverseList(ListNode head) {
    ListNode prev = null;
    ListNode curr = head;
    while (curr != null) {
        ListNode nextTemp = curr.next;
        curr.next = prev;
        prev = curr;
        curr = nextTemp;
    }
    return prev;
}
\`\`\`

## 关键要点
- 必须先保存 \`next\` 节点，否则修改 \`curr.next\` 后会丢失后续链表
- 循环结束后 \`prev\` 指向原链表的最后一个节点，即新链表的头节点
- 空链表和单节点链表都能正确处理

有其他问题欢迎继续提问！` }],

  coursePractise: [{ studentGetRecentQuestion: `# 📝 课程练习

## 题目：栈实现队列
请你仅使用两个栈实现一个**先进先出（FIFO）**的队列。

## 要求
实现 \`MyQueue\` 类：
- \`push(x)\` — 将元素 x 推到队列末尾
- \`pop()\` — 从队列开头移除并返回元素
- \`peek()\` — 返回队列开头的元素
- \`empty()\` — 若队列为空返回 \`true\`，否则返回 \`false\`

## 示例
\`\`\`
输入：
["MyQueue","push","push","peek","pop","empty"]
[[],[1],[2],[],[],[]]
输出：[null,null,null,1,1,false]
\`\`\`

**提示：** 你可以使用一个栈作为「输入栈」，另一个作为「输出栈」。` }],

  coursePractiseCorrect: [{ compareAnswer: `# 📊 答案分析报告

## 评分：85/100

## 代码评价
✅ **优点：**
- 代码结构清晰，变量命名规范
- 正确使用了两个栈（stackIn / stackOut）
- push 操作实现正确，O(1) 时间复杂度
- 处理了 stackOut 为空时的转移逻辑

⚠️ **改进建议：**
- \`pop()\` 和 \`peek()\` 有重复代码，建议抽取公共方法
- 未处理空队列 pop/peek 的异常情况
- 建议添加单元测试覆盖边界条件

## 时间复杂度分析
| 操作 | 均摊复杂度 |
|------|-----------|
| push | O(1) |
| pop  | O(1) 均摊 |
| peek | O(1) 均摊 |
| empty | O(1) |` }],

  selfPractise: [{ studentsReviewExercises: `# 🎯 自主练习

## 题目：回文串判断
给定一个字符串 \`s\`，验证它是否是回文串。只考虑**字母和数字**字符，忽略大小写。

## 示例
| 输入 | 输出 |
|------|------|
| "A man, a plan, a canal: Panama" | true |
| "race a car" | false |
| " " | true |

## 要求
- 时间复杂度：O(n)
- 空间复杂度：O(1)

请实现 \`isPalindrome(s: string): boolean\` 函数。` }],

  selfPractiseCorrect: [{ studentCorrectErrors: `# 📊 练习批改结果

## 评分：92/100

## 代码评价
✅ **表现优秀：**
- 双指针法实现，空间复杂度 O(1)
- 正确使用 \`Character.isLetterOrDigit()\` 过滤非字母数字
- \`Character.toLowerCase()\` 统一大小写比较

⚠️ **微小改进：**
- 建议使用 \`while (left < right)\` 代替 \`while (left <= right)\`，减少一次不必要的比较
- 可添加更多测试用例覆盖特殊 Unicode 字符场景` }],

  arrangeTime: [{ arrangeTimeSuggestion: `# 📅 智能排课建议

## 分析结果

基于以下数据综合分析：
- 课程：**数据结构与算法**（64学时）
- 学生人数：45人
- 教师：张明远教授

## 推荐方案

| 时间段 | 日期 | 说明 |
|--------|------|------|
| 08:00 - 10:00 | 周一 | 理论授课（学生注意力最集中时段） |
| 14:00 - 16:00 | 周三 | 实验/上机练习 |
| 10:00 - 12:00 | 周五 | 答疑 + 习题课 |

## 依据
- ✅ 周一上午学生出勤率和专注度最高
- ✅ 周三下午适合安排实践环节
- ✅ 避开其他核心课程的高峰时段
- ✅ 理论-实践-巩固的节奏符合认知规律` }],
}

// ===== 数据分析 Mock 数据 =====
// AllTeacherClickRecordPO
export const mockTeacherClickRecord = {
  record_id: 1,
  knowledge_organization: 45,
  practise_design: 32,
  design_issue: 28,
  issue_answer_analysis: 56,
  build_knowledge_base: 12,
  create_time: '2024-10-15 10:30:00',
  summaryStartDate: '2024-09-01',
  summaryEndDate: '2024-10-31',
}

// AllStudentClickRecordPO
export const mockStudentClickRecord = {
  record_id: 1,
  answer: 128,
  self_practise: 89,
  course_practise: 67,
  create_time: '2024-10-15 10:30:00',
  summaryStartDate: '2024-09-01',
  summaryEndDate: '2024-10-31',
}

// AllCostTimeRecordDTO
export const mockCostTimeRecord = {
  record_id: 1,
  teacher_username: 'teacher01',
  course_name: '数据结构与算法',
  knowledgeOrganization_AVG_cost_second: 45.5,
  practiseDesign_AVG_cost_second: 62.3,
  designIssue_AVG_cost_second: 38.2,
  addCourse_buildKnowledgeBase_AVG_cost_second: 120.0,
  getIssueAnswerDetail_AVG_cost_second: 25.8,
  create_time: '2024-10-15 10:30:00',
  summaryStartDate: '2024-09-01',
  summaryEndDate: '2024-10-31',
}

// IssueCorrectAVGRecordPO[]
export const mockIssueCorrectRecords = [
  { record_id: 1, teacher_username: 'teacher01', course_name: '数据结构与算法', issue_name: '二叉树遍历选择题', question_id: 'q001', avg_correct: '0.85', create_time: '2024-10-15 10:30:00' },
  { record_id: 2, teacher_username: 'teacher01', course_name: '数据结构与算法', issue_name: '链表反转编程题', question_id: 'q002', avg_correct: '0.72', create_time: '2024-10-15 10:30:00' },
  { record_id: 3, teacher_username: 'teacher01', course_name: '操作系统原理', issue_name: '进程调度分析', question_id: 'q003', avg_correct: '0.68', create_time: '2024-10-15 10:30:00' },
  { record_id: 4, teacher_username: 'teacher02', course_name: '人工智能导论', issue_name: '神经网络基础', question_id: 'q004', avg_correct: '0.79', create_time: '2024-10-15 10:30:00' },
  { record_id: 5, teacher_username: 'teacher02', course_name: '人工智能导论', issue_name: '决策树算法', question_id: 'q005', avg_correct: '0.81', create_time: '2024-10-15 10:30:00' },
]

// KnowledgePointCommendDegreeRecordPO[]
export const mockKnowledgePointRecords = [
  { record_id: 1, teacher_username: 'teacher01', course_name: '数据结构与算法', knowledge_point: '线性结构', commend_degree: '85', error_time: '12', error_rate: '15', create_time: '2024-10-15 10:30:00' },
  { record_id: 2, teacher_username: 'teacher01', course_name: '数据结构与算法', knowledge_point: '树结构', commend_degree: '78', error_time: '20', error_rate: '22', create_time: '2024-10-15 10:30:00' },
  { record_id: 3, teacher_username: 'teacher01', course_name: '数据结构与算法', knowledge_point: '图结构', commend_degree: '65', error_time: '28', error_rate: '35', create_time: '2024-10-15 10:30:00' },
  { record_id: 4, teacher_username: 'teacher01', course_name: '数据结构与算法', knowledge_point: '排序算法', commend_degree: '90', error_time: '8', error_rate: '10', create_time: '2024-10-15 10:30:00' },
  { record_id: 5, teacher_username: 'teacher01', course_name: '数据结构与算法', knowledge_point: '查找算法', commend_degree: '82', error_time: '15', error_rate: '18', create_time: '2024-10-15 10:30:00' },
  { record_id: 6, teacher_username: 'teacher02', course_name: '人工智能导论', knowledge_point: '机器学习基础', commend_degree: '75', error_time: '22', error_rate: '25', create_time: '2024-10-15 10:30:00' },
  { record_id: 7, teacher_username: 'teacher02', course_name: '人工智能导论', knowledge_point: '神经网络', commend_degree: '68', error_time: '26', error_rate: '32', create_time: '2024-10-15 10:30:00' },
]

// ===== 资源和题目列表 =====
export const mockIssueList = [
  { issue_id: 'q001', issue_name: '二叉树中序遍历选择题', course_name: '数据结构与算法', start_time: '2024-10-15 08:00:00', end_time: '2024-10-22 23:59:59', status: '进行中' },
  { issue_id: 'q002', issue_name: '链表反转编程题', course_name: '数据结构与算法', start_time: '2024-10-16 08:00:00', end_time: '2024-10-23 23:59:59', status: '进行中' },
  { issue_id: 'q003', issue_name: '进程调度分析题', course_name: '操作系统原理', start_time: '2024-10-10 08:00:00', end_time: '2024-11-10 23:59:59', status: '进行中' },
  { issue_id: 'q004', issue_name: '线性表操作实践', course_name: '数据结构与算法', start_time: '2024-09-01 08:00:00', end_time: '2024-10-01 23:59:59', status: '已结束' },
  { issue_id: 'q005', issue_name: '排序算法对比', course_name: '数据结构与算法', start_time: '2024-09-15 08:00:00', end_time: '2024-10-15 23:59:59', status: '已结束' },
  { issue_id: 'q006', issue_name: '期末综合测试', course_name: '计算机组成原理', start_time: '2024-12-01 08:00:00', end_time: '2024-12-20 23:59:59', status: '未开始' },
]

export const mockResourceList = [
  { resource_id: 'r001', resource_name: '数据结构课件PPT', course_name: '数据结构与算法', type: 'ppt', size: '12.5MB', create_time: '2024-09-15 10:30:00' },
  { resource_id: 'r002', resource_name: '算法实验指导书', course_name: '数据结构与算法', type: 'pdf', size: '3.2MB', create_time: '2024-09-20 14:00:00' },
  { resource_id: 'r003', resource_name: '操作系统期末试卷', course_name: '操作系统原理', type: 'docx', size: '1.1MB', create_time: '2024-10-01 09:00:00' },
  { resource_id: 'r004', resource_name: '计算机组成原理讲义', course_name: '计算机组成原理', type: 'pdf', size: '8.7MB', create_time: '2024-09-10 11:00:00' },
  { resource_id: 'r005', resource_name: 'AI导论-神经网络章节', course_name: '人工智能导论', type: 'pptx', size: '15.3MB', create_time: '2024-10-05 16:00:00' },
]

export const mockModelList = [
  { id: 'deepseek-v4-pro', name: 'DeepSeek V4 Pro', description: '最新旗舰模型，综合能力最强', provider: 'deepseek', online: true },
  { id: 'deepseek-chat', name: 'DeepSeek Chat', description: '通用对话模型，速度快', provider: 'deepseek', online: true },
]
