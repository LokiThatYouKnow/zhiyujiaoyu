<template>
  <div class="workspace">

    <!-- Panel: Profile -->
    <div v-if="activeTab === 'profile'" class="panel animate-fade-in">
      <div class="panel-header"><h2>个人中心</h2></div>
      <!-- Stats -->
      <div class="stats-row">
        <StatCard icon='<svg viewBox="0 0 24 24" fill="none" width="22" height="22"><path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>'
          label="已选课程" :value="stats.courses" bg="linear-gradient(135deg, #0D9488, #14B8A6)" />
        <StatCard icon='<svg viewBox="0 0 24 24" fill="none" width="22" height="22"><path d="M9 11l3 3L22 4M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>'
          label="完成练习" :value="stats.practices" bg="linear-gradient(135deg, #4F46E5, #6366F1)" />
        <StatCard icon='<svg viewBox="0 0 24 24" fill="none" width="22" height="22"><circle cx="12" cy="12" r="10" stroke="white" stroke-width="2"/><path d="M12 6v6l4 2" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>'
          label="学习时长" :value="stats.hours + 'h'" bg="linear-gradient(135deg, #F59E0B, #F97316)" />
        <StatCard icon='<svg viewBox="0 0 24 24" fill="none" width="22" height="22"><path d="M18 20V10M12 20V4M6 20v-6" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>'
          label="平均正确率" :value="stats.accuracy + '%'" bg="linear-gradient(135deg, #7C3AED, #8B5CF6)" />
      </div>
      <!-- Info Card -->
      <div class="profile-full-card">
        <div class="profile-top">
          <div class="profile-avatar student-avatar">
            <svg viewBox="0 0 24 24" fill="none" width="48" height="48"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
          </div>
          <div class="profile-title-area">
            <h3 class="profile-name">{{ studentInfo.student_name || studentInfo.name || '未知' }}</h3>
            <span class="profile-role-badge student-badge">学生</span>
          </div>
        </div>
        <div class="profile-detail-grid">
          <div class="detail-item"><span class="detail-label">用户名</span><span class="detail-value">{{ studentInfo.student_account || studentUsername || '-' }}</span></div>
          <div class="detail-item"><span class="detail-label">性别</span><span class="detail-value">{{ studentInfo.student_gender || studentInfo.gender || '-' }}</span></div>
          <div class="detail-item"><span class="detail-label">年龄</span><span class="detail-value">{{ studentInfo.student_age || studentInfo.age || '-' }}</span></div>
          <div class="detail-item"><span class="detail-label">专业</span><span class="detail-value">{{ studentInfo.student_major || studentInfo.major || '-' }}</span></div>
          <div class="detail-item"><span class="detail-label">班级</span><span class="detail-value">{{ studentInfo.student_class_name || studentInfo.class_name || '-' }}</span></div>
          <div class="detail-item"><span class="detail-label">创建时间</span><span class="detail-value">{{ studentInfo.create_time || '-' }}</span></div>
          <div class="detail-item full-width"><span class="detail-label">绑定码</span><span class="detail-value"><code>{{ studentBindingCode || '未绑定' }}</code><button v-if="!studentBindingCode" class="btn-bind-inline" @click="openBindTeacherModal">绑定班级</button></span></div>
        </div>
      </div>
    </div>

    <!-- My Courses -->
    <div v-if="activeTab === 'courses'" class="panel animate-fade-in">
      <div class="panel-header">
        <h2>我的课程</h2>
        <div style="display:flex;gap:10px">
          <div class="binding-status" v-if="studentBindingCode">
            🔗 已绑定班级：<code>{{ studentBindingCode.slice(0, 8) }}...</code>
          </div>
          <button class="btn-sm" v-if="!studentBindingCode" @click="openBindTeacherModal" title="通过教师绑定码关联班级">
            🔗 绑定班级
          </button>
          <button class="btn-primary" @click="showJoinCourse = true">
            <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/></svg>
            加入课程
          </button>
        </div>
      </div>
      <div v-if="myCourses.length > 0" class="course-grid">
        <div v-for="course in myCourses" :key="course.course_id" class="course-card">
          <div class="cc-header">
            <div class="cc-icon">
              <svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M4 19.5A2.5 2.5 0 0 0 6.5 22H20V2H6.5A2.5 2.5 0 0 0 4 4.5v15z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
            </div>
            <div class="cc-info">
              <h3>{{ course.course_name }}</h3>
              <span class="cc-meta-text">{{ course.course_teacher_name || '未知教师' }} · {{ course.course_hours }}课时 · {{ course.course_score }}学分</span>
            </div>
          </div>
          <div class="cc-actions">
            <button class="btn-sm" @click="selectCourse(course); activeTab = 'tutor'">🤖 AI 学习</button>
            <button class="btn-sm" @click="selectCourse(course); tutorSubTab = 'practice'; activeTab = 'tutor'">✏️ 练习</button>
          </div>
        </div>
      </div>
      <EmptyState v-else title="还没有加入课程" description="点击上方按钮，输入课程邀请码加入课程" />

    </div>

    <!-- AI 学习助手 (合并：聊天 + 练习) -->
    <div v-if="activeTab === 'tutor'" class="panel animate-fade-in">
      <div class="panel-header" style="justify-content:space-between">
        <h2>AI 学习助手</h2>
        <div class="tutor-subtab-switch">
          <button class="subtab-btn" :class="{ active: tutorSubTab === 'chat' }" @click="tutorSubTab = 'chat'">🤖 AI课程聊天助手</button>
          <button class="subtab-btn" :class="{ active: tutorSubTab === 'practice' }" @click="tutorSubTab = 'practice'">✏️ AI练习中心</button>
        </div>
      </div>

      <!-- Sub-tab: Chat -->
      <div v-show="tutorSubTab === 'chat'">
        <div class="chat-layout">
          <div class="chat-sidebar">
            <div class="chat-side-title">选择课程</div>
            <div v-if="myCourses.length > 0" class="course-select-list">
              <button v-for="c in myCourses" :key="c.course_id" class="course-select-item"
                :class="{ active: selectedCourseName === c.course_name }"
                @click="selectCourse(c)">
                <span class="csi-name">{{ c.course_name }}</span>
                <span class="csi-teacher">{{ c.course_teacher_name }}</span>
              </button>
            </div>
            <EmptyState v-else title="未加入课程" description="先去课程页面加入课程" />
          </div>
          <div class="chat-main">
            <div v-if="!selectedCourseName" class="chat-empty">
              <EmptyState title="请选择课程" description="选择一门课程后开始 AI 问答" />
            </div>
            <div v-else class="chat-container">
              <div class="chat-messages" ref="chatMsgs">
                <div v-for="(msg, i) in chatMessages" :key="i" class="chat-msg" :class="msg.role">
                  <div class="msg-avatar">
                    <svg v-if="msg.role === 'assistant'" viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                    <svg v-else viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                  </div>
                  <div class="msg-content">
                    <MarkdownRenderer :content="msg.content" />
                  </div>
                </div>
                <div v-if="chatLoading" class="chat-typing">AI 正在思考...</div>
              </div>
              <div class="chat-input-area">
                <a-textarea v-model:value="chatInput" placeholder="输入你的问题..." :rows="2" @keydown.enter.exact="(e: KeyboardEvent) => { e.preventDefault(); handleSendChat() }" />
                <button class="btn-primary" @click="handleSendChat" :disabled="chatLoading || !chatInput.trim()">
                  <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Sub-tab: Practice -->
      <div v-show="tutorSubTab === 'practice'">
        <div class="practice-mode-switch" style="margin-bottom:16px">
          <button class="mode-btn" :class="{ active: practiceMode === 'self' }" @click="practiceMode = 'self'">自主练习</button>
          <button class="mode-btn" :class="{ active: practiceMode === 'course' }" @click="practiceMode = 'course'">课程练习</button>
        </div>

        <div v-if="myCourses.length === 0" class="panel-body">
          <EmptyState title="请先加入课程" description="练习功能需要先加入至少一门课程" />
        </div>

        <!-- Self Practice -->
        <div v-else-if="practiceMode === 'self'">
          <div class="ai-input-area">
            <a-select v-model:value="selectedCourseName" placeholder="选择课程" style="width:240px">
              <a-select-option v-for="c in myCourses" :key="c.course_name" :value="c.course_name">{{ c.course_name }}</a-select-option>
            </a-select>
            <label>练习需求 (可选)</label>
            <a-textarea v-model:value="selfPracticeInput" placeholder="描述你想练习的知识点或题型偏好..." :rows="2" />
            <button class="btn-primary" @click="startSelfPractice" :disabled="practiceLoading || !selectedCourseName">
              {{ practiceLoading ? '生成中...' : '开始练习' }}
            </button>
          </div>
          <div v-if="practiceQuestions.length > 0" class="practice-questions">
            <div v-for="(q, qi) in practiceQuestions" :key="qi" class="pq-card">
              <div class="pq-header"><span class="pq-num">第 {{ qi + 1 }} 题</span><span class="pq-type">{{ q.type || '选择题' }}</span></div>
              <div class="pq-body"><MarkdownRenderer :content="q.question || q.questionInfo || q.content || JSON.stringify(q)" /></div>
              <div v-if="q.type === '选择题' || q.options || q.A" class="pq-options">
                <div v-for="opt in (q.options || ['A','B','C','D'])" :key="opt" class="pq-option"
                  :class="{ selected: studentAnswers[qi] === opt }" @click="studentAnswers[qi] = opt">
                  <span class="pq-opt-letter">{{ opt }}</span><span>{{ q[opt] || '' }}</span>
                </div>
              </div>
              <div v-else class="pq-answer-input">
                <label>你的答案</label>
                <a-textarea v-model:value="studentAnswers[qi]" placeholder="输入你的答案..." :rows="3" />
              </div>
            </div>
            <button v-if="practiceQuestions.length > 0" class="btn-primary" style="margin-top:16px" @click="submitSelfPractice" :disabled="practiceLoading">提交并批改</button>
          </div>
          <div v-if="correctionResult" class="ai-result-card" style="margin-top:20px">
            <div class="ai-result-header"><h3>📝 批改结果</h3></div>
            <MarkdownRenderer :content="correctionResult" />
          </div>
        </div>

        <!-- Course Practice -->
        <div v-else>
          <div class="ai-input-area">
            <a-select v-model:value="selectedCourseName" placeholder="选择课程" style="width:240px">
              <a-select-option v-for="c in myCourses" :key="c.course_name" :value="c.course_name">{{ c.course_name }}</a-select-option>
            </a-select>
            <button class="btn-primary" @click="startCoursePractice" :disabled="practiceLoading || !selectedCourseName">
              {{ practiceLoading ? '获取中...' : '获取课程练习' }}
            </button>
          </div>
          <div v-if="practiceQuestions.length > 0">
            <div v-for="(q, qi) in practiceQuestions" :key="qi" class="pq-card">
              <div class="pq-header"><span class="pq-num">第 {{ qi + 1 }} 题</span><span class="pq-type">{{ q.type || '选择题' }}</span></div>
              <div class="pq-body"><MarkdownRenderer :content="q.question || q.questionInfo || q.content || JSON.stringify(q)" /></div>
              <div v-if="q.options || q.A" class="pq-options">
                <div v-for="opt in (q.options || ['A','B','C','D'])" :key="opt" class="pq-option"
                  :class="{ selected: studentAnswers[qi] === opt }" @click="studentAnswers[qi] = opt">
                  <span class="pq-opt-letter">{{ opt }}</span><span>{{ q[opt] || '' }}</span>
                </div>
              </div>
              <div v-else class="pq-answer-input">
                <label>你的答案</label>
                <a-textarea v-model:value="studentAnswers[qi]" placeholder="输入你的答案..." :rows="3" />
              </div>
            </div>
            <button class="btn-primary" style="margin-top:16px" @click="submitCoursePractice" :disabled="practiceLoading">提交并对比答案</button>
          </div>
          <div v-if="correctionResult" class="ai-result-card" style="margin-top:20px">
            <div class="ai-result-header"><h3>📝 答案对比</h3></div>
            <MarkdownRenderer :content="correctionResult" />
          </div>
        </div>
      </div>
    </div>

    <!-- ===== Panel: 课程作业 ===== -->
      <div v-if="activeTab === 'homework'" class="panel animate-fade-in">
        <div class="panel-header">
          <h2>课程作业</h2>
          <div style="display:flex;gap:8px;align-items:center;flex-wrap:wrap">
            <a-input v-model:value="hwKeyword" placeholder="输入关键字搜索..." style="width:200px" size="small" @pressEnter="searchHomework" />
            <a-button type="primary" size="small" @click="searchHomework">搜索</a-button>
            <a-button size="small" @click="resetHwSearch">重置</a-button>
            <div class="hw-tab-switch" v-if="!hwAnsweringItem">
              <button class="hw-switch-btn" :class="{ active: hwSubTab === 'todo' }" @click="hwSubTab = 'todo'">待完成</button>
              <button class="hw-switch-btn" :class="{ active: hwSubTab === 'done' }" @click="hwSubTab = 'done'">已完成</button>
            </div>
          </div>
        </div>

        <template v-if="!hwAnsweringItem">
        <div v-if="homeworkLoading" class="panel-body" style="text-align:center;padding:40px">
          <span style="color:#94A3B8">加载中...</span>
        </div>

        <div v-else-if="publishedAssignments.length === 0 && !homeworkLoading" class="panel-body">
          <EmptyState title="暂无作业" description="教师发布的作业会显示在这里" />
        </div>

        <!-- Todo List -->
        <div v-if="hwSubTab === 'todo' && filteredTodoAssignments.length > 0" class="homework-grid">
          <div v-for="item in filteredTodoAssignments" :key="item.id" class="homework-card" :class="{ 'exam-card': item.type === 'exam' }">
            <div class="hw-header">
              <h3>{{ item.title }}</h3>
              <span class="hw-course">{{ item.courseName || item.course_name }}</span>
              <span class="hw-type-badge" :class="item.type === 'exam' ? 'hw-exam' : 'hw-hw'">{{ item.type === 'exam' ? '考试' : '作业' }}</span>
            </div>
            <p class="hw-desc">{{ item.description || '暂无描述' }}</p>
            <div class="hw-meta">
              <div class="hw-meta-row">
                <span v-if="item.deadline">截止：{{ item.deadline }}</span>
                <span v-if="item.endTime || item.end_time">结束：{{ item.endTime || item.end_time }}</span>
                <span v-if="item.duration">时长：{{ item.duration }} 分钟</span>
              </div>
              <div class="hw-meta-row hw-meta-sub">
                <span>发布：{{ formatTime(item.publishedAt || item.createdAt || item.created_at) }}</span>
                <span>{{ item.questionCount || 0 }} 题</span>
              </div>
            </div>
            <div class="hw-actions">
              <button class="btn-primary" @click="openAnswerSheet(item)">开始答题</button>
            </div>
          </div>
        </div>

        <div v-if="hwSubTab === 'todo' && filteredTodoAssignments.length === 0 && publishedAssignments.length > 0" class="panel-body">
          <EmptyState title="全部完成" description="所有作业都已提交，去「已完成」标签查看提交记录" />
        </div>

        <!-- Done List -->
        <div v-if="hwSubTab === 'done' && filteredDoneAssignments.length > 0" class="homework-grid">
          <div v-for="item in filteredDoneAssignments" :key="item.id" class="homework-card" :class="{ 'exam-card': item.type === 'exam' }">
            <div class="hw-header">
              <h3>{{ item.title }}</h3>
              <span class="hw-course">{{ item.courseName || item.course_name }}</span>
              <span class="hw-type-badge" :class="item.type === 'exam' ? 'hw-exam' : 'hw-hw'">{{ item.type === 'exam' ? '考试' : '作业' }}</span>
            </div>
            <p class="hw-desc">{{ item.description || '暂无描述' }}</p>
            <div class="hw-meta">
              <div class="hw-meta-row">
                <span class="hw-score" v-if="item.myScore !== undefined">得分：{{ item.myScore }}/{{ item.maxScore }}</span>
                <span class="hw-status-done" v-if="item.status === 'graded'">✅ 已批改</span>
                <span class="hw-status-done" v-else>📝 已提交</span>
              </div>
              <div class="hw-meta-row hw-meta-sub">
                <span>提交于：{{ formatTime(item.submittedAt) }}</span>
              </div>
            </div>
            <div class="hw-actions">
              <button class="btn-sm" @click="viewMySubmission(item)">查看详情</button>
              <button class="btn-sm btn-sm-danger" @click="retractSubmission(item)">↩ 撤回</button>
            </div>
          </div>
        </div>

        <div v-if="hwSubTab === 'done' && doneAssignments.length === 0 && publishedAssignments.length > 0" class="panel-body">
          <EmptyState title="暂无已完成作业" description="完成答题并提交后，记录会显示在这里" />
        </div>

        </template>

        <!-- Inline Answer Sheet (Homework) -->
        <div v-if="hwAnsweringItem" class="answer-sheet-panel animate-fade-in">
          <div class="as-panel-top">
            <button class="btn-sm" @click="closeAnswerSheet('hw')">
              <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M19 12H5M12 19l-7-7 7-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              返回列表
            </button>
            <div class="as-panel-info">
              <span class="as-panel-title">{{ hwAnsweringItem.title }}</span>
              <span class="as-course">{{ hwAnsweringItem.courseName || hwAnsweringItem.course_name }}</span>
              <span class="as-type-tag as-hw">作业</span>
              <span v-if="hwAnsweringItem.deadline" class="as-deadline">⏰ 截止：{{ hwAnsweringItem.deadline }}</span>
              <span v-if="hwAnsweringItem.endTime || hwAnsweringItem.end_time" class="as-deadline">🏁 结束：{{ hwAnsweringItem.endTime || hwAnsweringItem.end_time }}</span>
              <span v-if="hwAnsweringItem.duration" class="as-deadline">⏱ 时长：{{ hwAnsweringItem.duration }} 分钟</span>
              <span class="as-draft-indicator" v-if="hwDraftSaved">💾 草稿已自动保存</span>
            </div>
            <button class="btn-sm btn-sm-danger" @click="clearAnswerDraft" :disabled="submittingAnswer">🗑 清空</button>
          </div>
          <div class="answer-sheet">
            <div v-for="(q, qi) in hwAnsweringQuestions" :key="qi" class="as-question-card">
              <div class="as-q-header">
                <span class="as-q-num">第 {{ qi + 1 }} 题</span>
                <span class="as-q-type">{{ q.type === 'choice' ? '选择题' : q.type === 'fill-blank' ? '填空题' : q.type === 'true-false' ? '判断题' : q.type === 'essay' ? '应用题' : q.type === 'calculation' ? '计算题' : (q.type || '未知') }}</span>
                <span class="as-q-score">({{ q.score || 10 }} 分)</span>
              </div>
              <div class="as-q-body">
                <MarkdownRenderer :content="q.title || q.question || q.content || ''" />
              </div>
              <!-- Choice -->
              <div v-if="q.type === 'choice' && q.options && q.options.length" class="as-options">
                <template v-for="(opt, oi) in q.options" :key="oi">
                  <div v-if="opt.text" class="as-option"
                    :class="{ selected: hwAnswerSheet[qi] === (opt.key || opt) }" @click="hwAnswerSheet[qi] = (opt.key || opt)">
                    <span class="as-opt-letter">{{ opt.key || String.fromCharCode(65 + oi) }}</span>
                    <span>{{ opt.text }}</span>
                  </div>
                </template>
              </div>
              <!-- True/False -->
              <div v-else-if="q.type === 'true-false'" class="as-options">
                <div class="as-option" :class="{ selected: hwAnswerSheet[qi] === '正确' }" @click="hwAnswerSheet[qi] = '正确'">
                  <span class="as-opt-letter">✓</span><span>正确</span>
                </div>
                <div class="as-option" :class="{ selected: hwAnswerSheet[qi] === '错误' }" @click="hwAnswerSheet[qi] = '错误'">
                  <span class="as-opt-letter">✗</span><span>错误</span>
                </div>
              </div>
              <!-- Text input -->
              <div v-else class="as-text-input">
                <a-textarea v-model:value="hwAnswerSheet[qi]" placeholder="输入你的答案..." :rows="q.type === 'essay' ? 5 : 3" @change="onAnswerChange" />
              </div>
            </div>
            <div class="as-actions">
              <button class="btn-primary" @click="submitAnswer" :disabled="submittingAnswer">
                {{ submittingAnswer ? '提交中...' : '提交答案' }}
              </button>
            </div>
          </div>
        </div>

        <!-- View Submission Modal -->
        <a-modal v-model:open="showSubmissionDetail" title="提交详情" :footer="null" width="650px">
          <div v-if="viewingSubmission" class="submission-detail">
            <div class="sd-header">
              <span class="sd-title">{{ viewingSubmission.title }}</span>
              <span class="sd-score" v-if="viewingSubmission.status === 'graded'">得分：{{ viewingSubmission.myScore }}/{{ viewingSubmission.maxScore }}</span>
              <span class="sd-status" v-else>状态：已提交，等待批改</span>
            </div>
            <div v-for="(ans, ai) in viewingAnswers" :key="ai" class="sd-answer-card">
              <div class="sd-q-header">
                <span class="sd-q-num">第 {{ ai + 1 }} 题</span>
                <span class="sd-q-score" v-if="viewingScores[ai]">得分：{{ viewingScores[ai].score }}/{{ viewingScores[ai].maxScore }}</span>
              </div>
              <div class="sd-answer-text">{{ ans.answer || '(未作答)' }}</div>
            </div>
          </div>
        </a-modal>
      </div>

      <!-- ===== Panel: 课程考试 ===== -->
      <div v-if="activeTab === 'exam'" class="panel animate-fade-in">
        <div class="panel-header">
          <h2>课程考试</h2>
          <div style="display:flex;gap:8px;align-items:center;flex-wrap:wrap">
            <a-input v-model:value="examKeyword" placeholder="输入关键字搜索..." style="width:200px" size="small" @pressEnter="searchExam" />
            <a-button type="primary" size="small" @click="searchExam">搜索</a-button>
            <a-button size="small" @click="resetExamSearch">重置</a-button>
            <div class="hw-tab-switch" v-if="!exAnsweringItem">
              <button class="hw-switch-btn" :class="{ active: examSubTab === 'todo' }" @click="examSubTab = 'todo'">待完成</button>
              <button class="hw-switch-btn" :class="{ active: examSubTab === 'done' }" @click="examSubTab = 'done'">已完成</button>
            </div>
          </div>
        </div>

        <template v-if="!exAnsweringItem">
        <div v-if="homeworkLoading" class="panel-body" style="text-align:center;padding:40px">
          <span style="color:#94A3B8">加载中...</span>
        </div>

        <div v-else-if="examAssignments.length === 0 && !homeworkLoading" class="panel-body">
          <EmptyState title="暂无考试" description="教师发布的考试会显示在这里" />
        </div>

        <!-- 待完成的考试 -->
        <div v-if="examSubTab === 'todo' && filteredTodoExams.length > 0" class="homework-grid">
          <div v-for="e in filteredTodoExams" :key="e.id || e.homework_id" class="homework-card exam-card">
            <div class="hw-header">
              <h3>{{ e.title }}</h3>
              <span class="hw-course">{{ e.courseName || e.course_name }}</span>
              <span class="hw-type-badge hw-exam">考试</span>
            </div>
            <p class="hw-desc">{{ e.description || '暂无描述' }}</p>
            <div class="hw-meta">
              <div class="hw-meta-row">
                <span v-if="e.deadline">开始：{{ e.deadline }}</span>
                <span v-if="e.endTime || e.end_time">结束：{{ e.endTime || e.end_time }}</span>
                <span v-if="e.duration">时长：{{ e.duration }} 分钟</span>
              </div>
              <div class="hw-meta-row hw-meta-sub">
                <span>发布：{{ formatTime(e.publishedAt || e.createdAt || e.created_at) }}</span>
                <span>{{ e.questionCount || 0 }} 题</span>
              </div>
            </div>
            <div class="hw-actions">
              <button class="btn-primary" @click="openAnswerSheet(e)">参加考试</button>
            </div>
          </div>
        </div>

        <div v-if="examSubTab === 'todo' && filteredTodoExams.length === 0 && examAssignments.length > 0" class="panel-body">
          <EmptyState title="全部完成" description="所有考试都已提交，去「已完成」标签查看提交记录" />
        </div>

        <!-- 已完成的考试 -->
        <div v-if="examSubTab === 'done' && filteredDoneExams.length > 0" class="homework-grid">
          <div v-for="e in filteredDoneExams" :key="e.id || e.homework_id" class="homework-card exam-card">
            <div class="hw-header">
              <h3>{{ e.title }}</h3>
              <span class="hw-course">{{ e.courseName || e.course_name }}</span>
              <span class="hw-type-badge hw-exam">考试</span>
            </div>
            <p class="hw-desc">{{ e.description || '暂无描述' }}</p>
            <div class="hw-meta">
              <div class="hw-meta-row">
                <span class="hw-score" v-if="e.myScore !== undefined">得分：{{ e.myScore }}/{{ e.maxScore }}</span>
                <span class="hw-status-done" v-if="e.status === 'graded'">✅ 已批改</span>
                <span class="hw-status-done" v-else>📝 已提交</span>
              </div>
              <div class="hw-meta-row hw-meta-sub">
                <span>提交于：{{ formatTime(e.submittedAt) }}</span>
              </div>
            </div>
            <div class="hw-actions">
              <button class="btn-sm" @click="viewExamSubmission(e)">查看详情</button>
              <button class="btn-sm btn-sm-danger" @click="retractSubmission(e)">↩ 撤回</button>
            </div>
          </div>
        </div>

        <div v-if="examSubTab === 'done' && doneExams.length === 0 && examAssignments.length > 0" class="panel-body">
          <EmptyState title="暂无已完成考试" description="完成答题并提交后，记录会显示在这里" />
        </div>
        </template>

        <!-- Inline Answer Sheet (Exam) -->
        <div v-if="exAnsweringItem" class="answer-sheet-panel animate-fade-in">
          <div class="as-panel-top">
            <button class="btn-sm" @click="closeAnswerSheet('ex')">
              <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M19 12H5M12 19l-7-7 7-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              返回列表
            </button>
            <div class="as-panel-info">
              <span class="as-panel-title">{{ exAnsweringItem.title }}</span>
              <span class="as-course">{{ exAnsweringItem.courseName || exAnsweringItem.course_name }}</span>
              <span class="as-type-tag as-exam">考试</span>
              <span v-if="exAnsweringItem.deadline" class="as-deadline">⏰ 截止：{{ exAnsweringItem.deadline }}</span>
              <span v-if="exAnsweringItem.endTime || exAnsweringItem.end_time" class="as-deadline">🏁 结束：{{ exAnsweringItem.endTime || exAnsweringItem.end_time }}</span>
              <span v-if="exAnsweringItem.duration" class="as-deadline">⏱ 时长：{{ exAnsweringItem.duration }} 分钟</span>
              <span class="as-draft-indicator" v-if="exDraftSaved">💾 草稿已自动保存</span>
            </div>
            <button class="btn-sm btn-sm-danger" @click="clearAnswerDraft" :disabled="submittingAnswer">🗑 清空</button>
          </div>
          <div class="answer-sheet">
            <div v-for="(q, qi) in exAnsweringQuestions" :key="qi" class="as-question-card">
              <div class="as-q-header">
                <span class="as-q-num">第 {{ qi + 1 }} 题</span>
                <span class="as-q-type">{{ q.type === 'choice' ? '选择题' : q.type === 'fill-blank' ? '填空题' : q.type === 'true-false' ? '判断题' : q.type === 'essay' ? '应用题' : q.type === 'calculation' ? '计算题' : (q.type || '未知') }}</span>
                <span class="as-q-score">({{ q.score || 10 }} 分)</span>
              </div>
              <div class="as-q-body">
                <MarkdownRenderer :content="q.title || q.question || q.content || ''" />
              </div>
              <!-- Choice -->
              <div v-if="q.type === 'choice' && q.options && q.options.length" class="as-options">
                <template v-for="(opt, oi) in q.options" :key="oi">
                  <div v-if="opt.text" class="as-option"
                    :class="{ selected: exAnswerSheet[qi] === (opt.key || opt) }" @click="exAnswerSheet[qi] = (opt.key || opt)">
                    <span class="as-opt-letter">{{ opt.key || String.fromCharCode(65 + oi) }}</span>
                    <span>{{ opt.text }}</span>
                  </div>
                </template>
              </div>
              <!-- True/False -->
              <div v-else-if="q.type === 'true-false'" class="as-options">
                <div class="as-option" :class="{ selected: exAnswerSheet[qi] === '正确' }" @click="exAnswerSheet[qi] = '正确'">
                  <span class="as-opt-letter">✓</span><span>正确</span>
                </div>
                <div class="as-option" :class="{ selected: exAnswerSheet[qi] === '错误' }" @click="exAnswerSheet[qi] = '错误'">
                  <span class="as-opt-letter">✗</span><span>错误</span>
                </div>
              </div>
              <!-- Text input -->
              <div v-else class="as-text-input">
                <a-textarea v-model:value="exAnswerSheet[qi]" placeholder="输入你的答案..." :rows="q.type === 'essay' ? 5 : 3" @change="onAnswerChange" />
              </div>
            </div>
            <div class="as-actions">
              <button class="btn-primary" @click="submitAnswer" :disabled="submittingAnswer">
                {{ submittingAnswer ? '提交中...' : '提交答案' }}
              </button>
            </div>
          </div>
        </div>

        <!-- View Exam Submission Modal -->
        <a-modal v-model:open="showExamSubmissionDetail" title="考试提交详情" :footer="null" width="650px">
          <div v-if="examViewingSubmission" class="submission-detail">
            <div class="sd-header">
              <span class="sd-title">{{ examViewingSubmission.title }}</span>
              <span class="sd-score" v-if="examViewingSubmission.status === 'graded'">得分：{{ examViewingSubmission.myScore }}/{{ examViewingSubmission.maxScore }}</span>
              <span class="sd-status" v-else>状态：已提交，等待批改</span>
            </div>
            <div v-for="(ans, ai) in examViewingAnswers" :key="ai" class="sd-answer-card">
              <div class="sd-q-header">
                <span class="sd-q-num">第 {{ ai + 1 }} 题</span>
                <span class="sd-q-score" v-if="examViewingScores[ai]">得分：{{ examViewingScores[ai].score }}/{{ examViewingScores[ai].maxScore }}</span>
              </div>
              <div class="sd-answer-text">{{ ans.answer || '(未作答)' }}</div>
            </div>
          </div>
        </a-modal>
      </div>

      <!-- ===== Panel: 考勤记录 ===== -->
      <div v-if="activeTab === 'attendance'" class="panel animate-fade-in">
        <div class="panel-header">
          <h2>考勤记录</h2>
          <div style="display:flex;gap:8px;align-items:center">
            <a-input v-model:value="studentAttKeyword" placeholder="输入关键字搜索..." style="width:200px" size="small" @pressEnter="searchStudentAtt" />
            <a-button type="primary" size="small" @click="searchStudentAtt">搜索</a-button>
            <a-button size="small" @click="resetStudentAttSearch">重置</a-button>
          </div>
        </div>
        <div v-if="attendanceLoading" class="panel-body" style="text-align:center;padding:40px">
          <span style="color:#94A3B8">加载中...</span>
        </div>
        <div v-else-if="filteredMyAttendance.length > 0" class="attendance-table-wrap">
          <a-table :columns="attendanceColumns" :dataSource="filteredMyAttendance" rowKey="id" :pagination="{ pageSize: 10 }">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'status'">
                <span class="as-tag" :class="record.status">
                  {{ record.status === 'present' ? '出勤' : record.status === 'absent' ? '缺勤' : '请假' }}
                </span>
              </template>
            </template>
          </a-table>
        </div>
        <EmptyState v-else title="暂无考勤记录" description="教师记录的考勤数据会显示在这里" />
      </div>

      <!-- ===== Panel: 通知中心 ===== -->
      <div v-if="activeTab === 'notifications'" class="panel animate-fade-in">
        <div class="panel-header">
          <h2>通知中心</h2>
          <span class="notif-unread-badge" v-if="unreadNotifCount > 0">{{ unreadNotifCount }} 条未读</span>
        </div>

        <div v-if="studentNotifications.length > 0" class="notif-list">
          <div v-for="n in studentNotifications" :key="n.id" class="notif-card" :class="{ 'notif-unread': !n.read }">
            <div class="notif-header">
              <span class="notif-course">{{ n.courseName || '全站通知' }}</span>
              <span class="notif-type-badge">{{ n.type === 'announcement' ? '公告' : '作业通知' }}</span>
              <span class="notif-time">{{ formatTime(n.createdAt) }}</span>
            </div>
            <h4 class="notif-title">{{ n.title }}</h4>
            <p class="notif-content">{{ n.content }}</p>
            <div class="notif-actions">
              <button class="btn-sm" @click="markStudentNotifRead(n.id)" v-if="!n.read">标记已读</button>
            </div>
          </div>
        </div>
        <EmptyState v-else title="暂无通知" description="教师发布的课程通知会显示在这里" />
      </div>

      <!-- ===== Panel: 讨论区 ===== -->
      <div v-if="activeTab === 'discussions'" class="panel animate-fade-in">
        <div class="panel-header">
          <h2>讨论区</h2>
          <div style="display:flex;gap:10px">
            <a-select v-model:value="studentDiscCourseFilter" placeholder="选择课程" class="course-select" style="width:200px" v-if="myCourses.length > 0">
              <a-select-option v-for="c in myCourses" :key="c.course_name" :value="c.course_name">{{ c.course_name }}</a-select-option>
            </a-select>
            <button class="btn-primary" @click="showStudentNewTopicModal = true" :disabled="!studentDiscCourseFilter">
              <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/></svg>
              发布话题
            </button>
          </div>
        </div>

        <div v-if="!studentDiscCourseFilter" class="panel-body">
          <EmptyState title="选择课程" description="请先选择一门课程查看讨论话题" />
        </div>

        <div v-else>
          <div v-if="studentFilteredDiscussions.length > 0" class="disc-list">
            <div v-for="topic in studentFilteredDiscussions" :key="topic.id" class="disc-card" @click="openStudentTopicDetail(topic)">
              <div class="disc-card-header">
                <h3 class="disc-title">{{ topic.title }}</h3>
                <span class="disc-replies">{{ topic.replies?.length || 0 }} 回复</span>
              </div>
              <p class="disc-preview">{{ topic.content?.slice(0, 120) }}{{ topic.content?.length > 120 ? '...' : '' }}</p>
              <div class="disc-meta">
                <span>{{ topic.author }}</span>
                <span>{{ formatTime(topic.createdAt) }}</span>
              </div>
            </div>
          </div>
          <EmptyState v-else title="暂无话题" description="点击「发布话题」发起第一个课程讨论" />

          <!-- Topic Detail Modal -->
          <a-modal v-model:open="showStudentTopicDetail" :title="studentViewingTopic?.title" :footer="null" width="650px">
            <div v-if="studentViewingTopic" class="topic-detail">
              <div class="topic-body">
                <div class="topic-meta">
                  <span class="topic-meta-left">
                    <span class="topic-author">{{ studentViewingTopic.author }}</span>
                    <span class="topic-time">{{ formatTime(studentViewingTopic.createdAt) }}</span>
                  </span>
                  <span class="topic-meta-right" v-if="studentViewingTopic.author === (studentUsername || '学生')">
                    <button class="btn-sm" @click="startEditStudentTopic" v-if="studentEditingTopicContent === ''">编辑</button>
                    <a-popconfirm title="确定删除此话题?" ok-text="确认" cancel-text="取消" @confirm="deleteStudentTopic(studentViewingTopic.id)">
                      <button class="btn-sm btn-sm-danger">删除</button>
                    </a-popconfirm>
                  </span>
                </div>
                <div v-if="studentEditingTopicContent === ''" class="topic-content">{{ studentViewingTopic.content }}</div>
                <div v-else class="topic-edit-area">
                  <a-textarea v-model:value="studentEditingTopicContent" :rows="4" :maxlength="3000" />
                  <div style="display:flex;gap:8px;margin-top:8px">
                    <button class="btn-primary" @click="saveStudentTopicEdit" :disabled="!studentEditingTopicContent.trim()">保存</button>
                    <button class="btn-sm" @click="studentEditingTopicContent = ''">取消</button>
                  </div>
                </div>
              </div>
              <div class="topic-replies-section">
                <div class="section-label">回复 ({{ studentViewingTopic.replies?.length || 0 }})</div>
                <div v-for="(reply, ri) in studentViewingTopic.replies" :key="ri" class="reply-card">
                  <div class="reply-meta">
                    <span class="reply-author">{{ reply.author }}</span>
                    <span class="reply-time">{{ formatTime(reply.createdAt) }}</span>
                    <span class="reply-actions-right" v-if="reply.author === (studentUsername || '学生')">
                      <button class="btn-sm" @click="startEditStudentReply(ri)">编辑</button>
                      <a-popconfirm title="确定删除此回复?" ok-text="确认" cancel-text="取消" @confirm="deleteStudentReply(ri)">
                        <button class="btn-sm btn-sm-danger">删除</button>
                      </a-popconfirm>
                    </span>
                  </div>
                  <div v-if="studentEditingReplyIdx !== ri" class="reply-content">{{ reply.content }}</div>
                  <div v-else class="reply-edit-area">
                    <a-textarea v-model:value="studentEditingReplyContent" :rows="3" :maxlength="1000" />
                    <div style="display:flex;gap:8px;margin-top:6px">
                      <button class="btn-primary" @click="saveStudentReplyEdit(ri)" :disabled="!studentEditingReplyContent.trim()">保存</button>
                      <button class="btn-sm" @click="studentEditingReplyIdx = -1">取消</button>
                    </div>
                  </div>
                </div>
                <div v-if="!studentViewingTopic.replies || studentViewingTopic.replies.length === 0" class="no-replies">暂无回复</div>
              </div>
              <div class="reply-input-area">
                <a-textarea v-model:value="studentReplyContent" placeholder="输入回复内容..." :rows="3" :maxlength="1000" />
                <span class="field-hint" style="font-size:12px;color:#94A3B8">{{ studentReplyContent.length }}/1000</span>
                <button class="btn-primary" style="margin-top:8px" @click="submitStudentReply" :disabled="!studentReplyContent.trim()">发送回复</button>
              </div>
            </div>
          </a-modal>

          <!-- New Topic Modal -->
          <a-modal v-model:open="showStudentNewTopicModal" title="发布话题" :footer="null" width="520px">
            <div class="homework-form">
              <div class="form-field">
                <label>标题 <span style="color:red">*</span></label>
                <a-input v-model:value="studentNewTopicForm.title" placeholder="话题标题" />
              </div>
              <div class="form-field">
                <label>内容</label>
                <a-textarea v-model:value="studentNewTopicForm.content" placeholder="话题内容..." :rows="6" />
              </div>
              <button class="btn-primary" style="width:100%" @click="createStudentTopic" :disabled="!studentNewTopicForm.title.trim() || !studentNewTopicForm.content.trim()">
                发布话题
              </button>
            </div>
          </a-modal>
        </div>
      </div>
  </div>

  <!-- Join Course Modal (moved outside panels so it's always rendered) -->
  <a-modal :open="showJoinCourse" @cancel="showJoinCourse = false" title="加入课程" :footer="null" width="420px">
    <div class="join-form">
      <div class="form-field">
        <label>课程邀请码</label>
        <a-input v-model:value="inviteCode" placeholder="输入教师分享的课程邀请码（如：550e8400-e29b-...）" :maxlength="50" />
      </div>
      <button class="btn-primary" style="width:100%;margin-top:16px" @click="handleJoinCourse" :disabled="joining">
        {{ joining ? '加入中...' : '加入课程' }}
      </button>
    </div>
  </a-modal>

  <!-- Bind Teacher Modal (moved outside panels so it's always rendered) -->
  <a-modal :open="showBindTeacher" @cancel="showBindTeacher = false" title="绑定教师/班级" :footer="null" width="420px">
    <div class="join-form">
      <p class="field-hint" style="margin-bottom:12px">输入教师提供的<strong>班级绑定码</strong>，将你的账号关联到教师的班级下</p>
      <div class="form-field">
        <label>教师绑定码</label>
        <a-input v-model:value="teacherBindCode" placeholder="输入教师提供的班级绑定码" :maxlength="50" />
      </div>
      <button class="btn-primary" style="width:100%;margin-top:16px" @click="handleBindTeacher" :disabled="bindingTeacher">
        {{ bindingTeacher ? '绑定中...' : '确认绑定' }}
      </button>
    </div>
  </a-modal>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, onUnmounted, nextTick, watch, computed } from 'vue'
import { message } from 'ant-design-vue'
import { jwtDecode } from 'jwt-decode'
import StatCard from '../../components/common/StatCard.vue'
import EmptyState from '../../components/common/EmptyState.vue'
import MarkdownRenderer from '../../components/common/MarkdownRenderer.vue'
import { getCozeAnswerApi } from '../../api/student/getCozeAnswerApi'
import { getCozeSelfPractiseApi, getCozeselfPractiseCorrectApi } from '../../api/student/getSelfPractiseApi'
import { getCozeCoursePractiseApi, getCozeCoursePractiseCorrectAnswerApi, joinCourseApi } from '../../api/student/CoursePractiseApi'
import type { R } from '../../response/R'
import { submitAnswerApi, getHomeworkPublishedApi, getMySubmissionsApi, deleteSubmissionApi } from '../../api/submissionApi'
import { joinClassApi } from '../../api/student/joinClassApi'

// Navigation
const activeTab = ref<string>('profile')
const tabs = [
  { key: 'profile', label: '个人中心', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>' },
  { key: 'courses', label: '我的课程', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>' },
  { key: 'homework', label: '课程作业', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8zM14 2v6h6M16 13H8M16 17H8M10 9H8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>' },
  { key: 'exam', label: '课程考试', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M9 11l3 3L22 4M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>' },
  { key: 'attendance', label: '考勤记录', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2M9 5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v0a2 2 0 0 1-2 2h-2a2 2 0 0 1-2-2zM9 14l2 2 4-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>' },
  { key: 'notifications', label: '通知中心', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9M13.73 21a2 2 0 0 1-3.46 0" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>' },
  { key: 'tutor', label: 'AI 学习助手', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>' },
  { key: 'discussions', label: '讨论区', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>' },
]

// Stats
const stats = reactive({ courses: 0, practices: 0, hours: 0, accuracy: 85 })
const myCourses = ref<any[]>([])
const studentUsername = ref('')
const studentInfo = ref<any>({})
const selectedCourseName = ref('')
const selectedCourseTeacher = ref('')

function selectCourse(c: any) {
  const prevCourse = selectedCourseName.value
  selectedCourseName.value = c.course_name
  selectedCourseTeacher.value = c.course_teacher_account || ''
  // 切换课程时清空历史消息并发送问候语
  if (selectedCourseName.value !== prevCourse) {
    chatMessages.value = [{ role: 'assistant', content: `你好！我是你的AI学习助手，请随时向我提问关于**${c.course_name}**的任何问题。` }]
  }
}

// Navigation event handler (named for cleanup)
const studentNavHandler = (e: Event) => {
  const detail = (e as CustomEvent).detail
  if (detail && tabs.find(t => t.key === detail)) activeTab.value = detail
}

// 提取学生信息加载为独立函数，便于绑定后刷新
async function loadStudentDetail() {
  const getStudentIdFromToken = (): string => {
    try {
      const token = (sessionStorage.getItem('token') || localStorage.getItem('token'))
      if (!token) return ''
      const decoded: any = jwtDecode(token)
      return decoded?.sub || decoded?.username || ''
    } catch { return '' }
  }
  const sid = getStudentIdFromToken() || localStorage.getItem('student_id') || JSON.parse(localStorage.getItem('userInfo') || '{}').student_id || ''
  if (!sid) return
  const res = await import('../../api/student/getSelfStudentDetailApi').then(m => m.getSelfStudentDetailApi(sid))
  if (res.data?.code === 200) {
    const info = res.data.data
    studentUsername.value = info.student_account || info.username || ''
    studentBindingCode.value = info.teacher_binding_code || ''
    studentInfo.value = info
    if (info.student_classList) { myCourses.value = info.student_classList; stats.courses = info.student_classList.length }
    else if (info.courseList) { myCourses.value = info.courseList; stats.courses = info.courseList.length }
  }
}

onMounted(async () => {
  window.addEventListener('workspace-nav', studentNavHandler)
  // 恢复上次停留的页面
  const saved = sessionStorage.getItem('student_active_tab')
  if (saved && tabs.find(t => t.key === saved)) activeTab.value = saved
  try {
    await loadStudentDetail()
  } catch { message.error('加载课程信息失败，请刷新重试') }
})

onUnmounted(() => {
  window.removeEventListener('workspace-nav', studentNavHandler)
})

// Join Course
const showJoinCourse = ref(false)
const inviteCode = ref('')
const joining = ref(false)

async function handleJoinCourse() {
  if (!inviteCode.value.trim()) { message.warning('请输入邀请码'); return }
  joining.value = true
  try {
    const res = await joinCourseApi({ course_invite_code: inviteCode.value.trim(), student_username: studentUsername.value })
    if (res.data?.code === 200) { message.success('加入成功'); showJoinCourse.value = false; inviteCode.value = ''; location.reload() }
    else { message.error(res.data?.msg || '加入失败') }
  } catch { message.error('加入失败') }
  joining.value = false
}

// Bind Teacher
const showBindTeacher = ref(false)
const teacherBindCode = ref('')
const bindingTeacher = ref(false)
const studentBindingCode = ref('')

function openBindTeacherModal() {
  showBindTeacher.value = true
}

async function handleBindTeacher() {
  if (!teacherBindCode.value.trim()) { message.warning('请输入教师绑定码'); return }
  bindingTeacher.value = true
  try {
    const res = await joinClassApi({ teacher_binding_code: teacherBindCode.value.trim(), student_username: studentUsername.value })
    if (res.data?.code === 200) {
      message.success('绑定成功')
      showBindTeacher.value = false
      teacherBindCode.value = ''
      // 重新加载学生信息，刷新专业、班级等绑定后更新的字段
      try { await loadStudentDetail() } catch { /* ignore */ }
    } else {
      message.error(res.data?.msg || '绑定失败')
    }
  } catch { message.error('绑定失败，请检查网络') }
  bindingTeacher.value = false
}

// Chat
const chatMessages = ref<{ role: string; content: string }[]>([])
const chatInput = ref('')
const chatLoading = ref(false)
const chatMsgs = ref<HTMLElement | null>(null)

async function handleSendChat() {
  const q = chatInput.value.trim()
  if (!q || chatLoading.value || !selectedCourseName.value) return
  chatMessages.value.push({ role: 'user', content: q })
  chatInput.value = ''
  chatLoading.value = true
  await nextTick(); chatMsgs.value?.scrollTo?.({ top: chatMsgs.value.scrollHeight, behavior: 'smooth' })

  try {
    const res = await getCozeAnswerApi({ data: selectedCourseName.value, input: q })
    const R: R = res.data
    if (R.code === 200 && R.data?.[0]) {
      const answer = R.data[0].answerQuestion || R.data[0].content || JSON.stringify(R.data[0])
      chatMessages.value.push({ role: 'assistant', content: answer })
    } else { chatMessages.value.push({ role: 'assistant', content: '抱歉，未能获取回答，请稍后再试。' }) }
  } catch { chatMessages.value.push({ role: 'assistant', content: '请求失败，请检查网络连接。' }) }
  chatLoading.value = false
  await nextTick(); chatMsgs.value?.scrollTo?.({ top: chatMsgs.value.scrollHeight, behavior: 'smooth' })
}

// Practice
const practiceMode = ref<'self' | 'course'>('self')
const selfPracticeInput = ref('')
const practiceLoading = ref(false)
const practiceQuestions = ref<any[]>([])
const studentAnswers = ref<string[]>([])
const correctionResult = ref('')
const currentQuestionId = ref('')

async function startSelfPractice() {
  practiceLoading.value = true; correctionResult.value = ''; practiceQuestions.value = []; currentQuestionId.value = ''
  try {
    const res = await getCozeSelfPractiseApi({ data: selectedCourseName.value, student_username: studentUsername.value, input: selfPracticeInput.value })
    const R: R = res.data
    if (R.code === 200 && R.data?.[0]) {
      const d = R.data[0]
      currentQuestionId.value = d.studentsReviewExercisesQuestionId || ''
      try { const parsed = JSON.parse(d.studentsReviewExercises); practiceQuestions.value = parsed.questions || []; studentAnswers.value = new Array(practiceQuestions.value.length).fill('') }
      catch { practiceQuestions.value = [{ content: d.studentsReviewExercises, type: '综合练习' }]; studentAnswers.value = [''] }
    } else { message.error('生成练习失败') }
  } catch { message.error('请求超时') }
  practiceLoading.value = false
}

async function submitSelfPractice() {
  practiceLoading.value = true
  try {
    const answers = studentAnswers.value.map((a, i) => `第${i + 1}题: ${a || '(未作答)'}`).join('\n')
    const res = await getCozeselfPractiseCorrectApi({
      data: selectedCourseName.value, student_username: studentUsername.value, input: answers, question_id: currentQuestionId.value
    })
    const R: R = res.data
    if (R.code === 200 && R.data?.[0]) { correctionResult.value = R.data[0].studentCorrectErrors || JSON.stringify(R.data[0]) }
  } catch { message.error('批改请求失败') }
  practiceLoading.value = false
}

async function startCoursePractice() {
  practiceLoading.value = true; correctionResult.value = ''; practiceQuestions.value = []; currentQuestionId.value = ''
  try {
    const res = await getCozeCoursePractiseApi({ data: selectedCourseName.value, teacher_username: selectedCourseTeacher.value })
    const R: R = res.data
    if (R.code === 200 && R.data?.[0]) {
      const d = R.data[0]
      currentQuestionId.value = d.studentGetRecentQuestionId || d.studentsReviewExercisesQuestionId || ''
      try { const parsed = JSON.parse(d.studentGetRecentQuestion); practiceQuestions.value = parsed.questions || []; studentAnswers.value = new Array(practiceQuestions.value.length).fill('') }
      catch { practiceQuestions.value = [{ content: d.studentGetRecentQuestion, type: '课程练习' }]; studentAnswers.value = [''] }
    } else { message.error('获取练习失败') }
  } catch { message.error('请求超时') }
  practiceLoading.value = false
}

async function submitCoursePractice() {
  practiceLoading.value = true
  try {
    const answers = studentAnswers.value.map((a, i) => `第${i + 1}题: ${a || '(未作答)'}`).join('\n')
    const res = await getCozeCoursePractiseCorrectAnswerApi({
      data: selectedCourseName.value, student_username: studentUsername.value, input: answers, question_id: currentQuestionId.value || '0'
    })
    const R: R = res.data
    if (R.code === 200 && R.data?.[0]) { correctionResult.value = R.data[0].compareAnswer || JSON.stringify(R.data[0]) }
  } catch { message.error('对比请求失败') }
  practiceLoading.value = false
}

// ===== 课程作业 =====
const hwSubTab = ref<'todo' | 'done'>('todo')
const publishedAssignments = ref<any[]>([])
const mySubmissions = ref<any[]>([])
const homeworkLoading = ref(false)
// Answering state — separate per tab to avoid cross-contamination
const hwAnsweringItem = ref<any>(null)
const hwAnsweringQuestions = ref<any[]>([])
const hwAnswerSheet = ref<string[]>([])

const exAnsweringItem = ref<any>(null)
const exAnsweringQuestions = ref<any[]>([])
const exAnswerSheet = ref<string[]>([])

const submittingAnswer = ref(false)
const hwDraftSaved = ref(false)
const exDraftSaved = ref(false)
let hwDraftSaveTimer: ReturnType<typeof setTimeout> | null = null
let exDraftSaveTimer: ReturnType<typeof setTimeout> | null = null
// Homework submission detail
const showSubmissionDetail = ref(false)
const viewingSubmission = ref<any>(null)
const viewingAnswers = ref<any[]>([])
const viewingScores = ref<any[]>([])
// Exam submission detail (独立状态)
const showExamSubmissionDetail = ref(false)
const examViewingSubmission = ref<any>(null)
const examViewingAnswers = ref<any[]>([])
const examViewingScores = ref<any[]>([])

// Auto-save draft for homework
let hwAutoSaveWatchStop: (() => void) | null = null
watch(hwAnsweringItem, (val) => {
  if (hwAutoSaveWatchStop) { hwAutoSaveWatchStop(); hwAutoSaveWatchStop = null }
  if (val) {
    hwAutoSaveWatchStop = watch(hwAnswerSheet, () => {
      hwDraftSaved.value = false
      if (hwDraftSaveTimer) clearTimeout(hwDraftSaveTimer)
      hwDraftSaveTimer = setTimeout(() => {
        if (hwAnsweringItem.value && hwAnswerSheet.value.some(a => a && a.trim())) {
          const draftKey = `answer_draft_${hwAnsweringItem.value.id || hwAnsweringItem.value.homework_id}`
          localStorage.setItem(draftKey, JSON.stringify(hwAnswerSheet.value))
          hwDraftSaved.value = true
          setTimeout(() => { hwDraftSaved.value = false }, 3000)
        }
      }, 1500)
    }, { deep: true })
  }
})

// Auto-save draft for exam
let exAutoSaveWatchStop: (() => void) | null = null
watch(exAnsweringItem, (val) => {
  if (exAutoSaveWatchStop) { exAutoSaveWatchStop(); exAutoSaveWatchStop = null }
  if (val) {
    exAutoSaveWatchStop = watch(exAnswerSheet, () => {
      exDraftSaved.value = false
      if (exDraftSaveTimer) clearTimeout(exDraftSaveTimer)
      exDraftSaveTimer = setTimeout(() => {
        if (exAnsweringItem.value && exAnswerSheet.value.some(a => a && a.trim())) {
          const draftKey = `answer_draft_${exAnsweringItem.value.id || exAnsweringItem.value.homework_id}`
          localStorage.setItem(draftKey, JSON.stringify(exAnswerSheet.value))
          exDraftSaved.value = true
          setTimeout(() => { exDraftSaved.value = false }, 3000)
        }
      }, 1500)
    }, { deep: true })
  }
})

// Published homework (not exams) that haven't been submitted
const hwKeyword = ref('')
function filterByKeyword<T extends Record<string, any>>(list: T[], keyword: string): T[] {
  const term = keyword.trim().toLowerCase()
  if (!term) return [...list]
  const regex = new RegExp(term.split('').map(c => c.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')).join('.*'), 'i')
  return list.filter(item =>
    Object.values(item).some(v => v != null && String(v).toLowerCase().match(regex))
  )
}

const todoAssignments = computed(() => {
  const submittedIds = new Set(mySubmissions.value.map((s: any) => String(s.homework_id || s.id || '')))
  return publishedAssignments.value.filter((a: any) => a.type !== 'exam' && !submittedIds.has(String(a.id || a.homework_id || '')))
})

// Submitted homework items with status (exclude exams)
const doneAssignments = computed(() => {
  return mySubmissions.value
    .filter((s: any) => s.type !== 'exam')
    .map((s: any) => {
    const pub = publishedAssignments.value.find((a: any) => String(a.id || a.homework_id || '') === String(s.homework_id || s.id || ''))
    // Normalize backend snake_case fields to camelCase for template
    const myScore = s.total_score !== undefined ? s.total_score : (s.myScore !== undefined ? s.myScore : undefined)
    const maxScore = s.max_total_score !== undefined ? s.max_total_score : (s.maxScore !== undefined ? s.maxScore : undefined)
    const submittedAt = s.submitted_at !== undefined ? s.submitted_at : (s.submittedAt !== undefined ? s.submittedAt : undefined)
    return {
      ...(pub || {}),
      ...s,
      id: s.homework_id || s.id,
      title: s.issue_name || pub?.title || s.title || '',
      courseName: s.course_name || pub?.courseName || '',
      type: s.type || pub?.type || 'homework',
      myScore,
      maxScore,
      submittedAt,
    }
  })
})

// ===== 课程考试 =====
const examSubTab = ref<'todo' | 'done'>('todo')
const examAssignments = computed(() => publishedAssignments.value.filter((a: any) => a.type === 'exam'))
const todoExams = computed(() => {
  const submittedIds = new Set(mySubmissions.value.map((s: any) => String(s.homework_id || s.id || '')))
  return examAssignments.value.filter((a: any) => !submittedIds.has(String(a.id || a.homework_id || '')))
})
const doneExams = computed(() => {
  const submittedIds = new Set(mySubmissions.value.map((s: any) => String(s.homework_id || s.id || '')))
  return examAssignments.value
    .filter((a: any) => submittedIds.has(String(a.id || a.homework_id || '')))
    .map((a: any) => {
      const sub = mySubmissions.value.find((s: any) => String(s.homework_id || s.id || '') === String(a.id || a.homework_id || ''))
      const myScore = sub?.total_score !== undefined ? sub.total_score : undefined
      const maxScore = sub?.max_total_score !== undefined ? sub.max_total_score : undefined
      const submittedAt = sub?.submitted_at !== undefined ? sub.submitted_at : (sub?.submittedAt !== undefined ? sub.submittedAt : undefined)
      return {
        ...a,
        ...(sub || {}),
        id: a.id || a.homework_id,
        title: sub?.issue_name || a.title || '',
        courseName: a.courseName || a.course_name || '',
        myScore,
        maxScore,
        submittedAt,
      }
    })
})

const filteredTodoAssignments = computed(() => filterByKeyword(todoAssignments.value, hwKeyword.value))
const filteredDoneAssignments = computed(() => filterByKeyword(doneAssignments.value, hwKeyword.value))
function searchHomework() { /* computed reactive */ }
function resetHwSearch() { hwKeyword.value = '' }

const examKeyword = ref('')
const filteredTodoExams = computed(() => filterByKeyword(todoExams.value, examKeyword.value))
const filteredDoneExams = computed(() => filterByKeyword(doneExams.value, examKeyword.value))
function searchExam() { /* computed reactive */ }
function resetExamSearch() { examKeyword.value = '' }

function searchStudentAtt() { /* computed reactive */ }
function resetStudentAttSearch() { studentAttKeyword.value = '' }

// ===== 考勤记录 =====
const myAttendanceRecords = ref<any[]>([])
const studentAttKeyword = ref('')
const filteredMyAttendance = computed(() => {
  const term = studentAttKeyword.value.trim().toLowerCase()
  if (!term) return myAttendanceRecords.value
  const regex = new RegExp(term.split('').map(c => c.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')).join('.*'), 'i')
  return myAttendanceRecords.value.filter((item: any) =>
    Object.values(item).some(v => v != null && String(v).toLowerCase().match(regex))
  )
})
const attendanceLoading = ref(false)
const attendanceColumns = [
  { title: '课程', dataIndex: 'course_name', key: 'course_name' },
  { title: '日期', dataIndex: 'date', key: 'date' },
  { title: '节次', key: 'period', width: 80 },
  { title: '状态', key: 'status', width: 100 },
  { title: '备注', dataIndex: 'remark', key: 'remark' },
]
async function loadMyAttendance() {
  if (!studentUsername.value) return
  attendanceLoading.value = true
  try {
    const res = await import('../../api/student/attendanceApi').then(m => m.getMyAttendanceApi(studentUsername.value))
    if (res.data?.code === 200) {
      myAttendanceRecords.value = res.data.data || []
    }
  } catch { /* ignore */ }
  attendanceLoading.value = false
}

// ===== AI 学习助手子 Tab =====
const tutorSubTab = ref<'chat' | 'practice'>('chat')
// 恢复子 Tab 状态
const savedTutorSub = sessionStorage.getItem('tutor_subtab')
if (savedTutorSub === 'practice') tutorSubTab.value = 'practice'
watch(tutorSubTab, (v) => { sessionStorage.setItem('tutor_subtab', v) })

function formatTime(ts: number): string {
  if (!ts) return '—'
  // Normalize: treat timestamps before year 2100 in ms as seconds
  const ms = ts < 1e12 ? ts * 1000 : ts
  const d = new Date(ms)
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

async function loadPublishedAssignments() {
  homeworkLoading.value = true
  try {
    const res = await getHomeworkPublishedApi({})
    if (res.data?.code === 200 && res.data.data) {
      publishedAssignments.value = res.data.data.map((a: any) => ({
        ...a,
        id: a.homework_id || a.id,  // 优先用 homework_id (UUID 字符串)，保证前后端 ID 一致
        questionCount: (() => { try { return JSON.parse(a.questions_json || '[]').length } catch { return 0 } })(),
      }))
    }
  } catch { /* ignore */ }
  homeworkLoading.value = false
}

async function loadMySubmissions() {
  try {
    // Priority: fetch from backend (includes grading results)
    if (studentUsername.value) {
      const res = await getMySubmissionsApi({ student_username: studentUsername.value })
      if (res.data?.code === 200 && res.data.data) {
        mySubmissions.value = res.data.data
      }
    }
  } catch {
    // Fallback to localStorage
    try {
      const stored = JSON.parse(localStorage.getItem('student_my_submissions') || '[]')
      mySubmissions.value = stored.filter((s: any) => s.student_username === studentUsername.value)
    } catch { mySubmissions.value = [] }
  }
}

function openAnswerSheet(item: any) {
  const isExam = item.type === 'exam'
  if (isExam) {
    exAnsweringItem.value = item
    let questions: any[] = []
    try { questions = JSON.parse(item.questions_json || '[]') } catch { questions = [] }
    exAnsweringQuestions.value = questions
    exAnswerSheet.value = new Array(questions.length).fill('')
    // Restore draft
    try {
      const draftKey = `answer_draft_${item.id || item.homework_id}`
      const draft = localStorage.getItem(draftKey)
      if (draft) {
        const parsed = JSON.parse(draft)
        if (parsed.length === questions.length) exAnswerSheet.value = parsed
      }
    } catch { /* ignore */ }
  } else {
    hwAnsweringItem.value = item
    let questions: any[] = []
    try { questions = JSON.parse(item.questions_json || '[]') } catch { questions = [] }
    hwAnsweringQuestions.value = questions
    hwAnswerSheet.value = new Array(questions.length).fill('')
    // Restore draft
    try {
      const draftKey = `answer_draft_${item.id || item.homework_id}`
      const draft = localStorage.getItem(draftKey)
      if (draft) {
        const parsed = JSON.parse(draft)
        if (parsed.length === questions.length) hwAnswerSheet.value = parsed
      }
    } catch { /* ignore */ }
  }
}

function closeAnswerSheet(type: 'hw' | 'ex') {
  if (type === 'ex') {
    if (exAnsweringItem.value) {
      const draftKey = `answer_draft_${exAnsweringItem.value.id || exAnsweringItem.value.homework_id}`
      const hasContent = exAnswerSheet.value.some(a => a && a.trim())
      if (hasContent) {
        localStorage.setItem(draftKey, JSON.stringify(exAnswerSheet.value))
      }
    }
    exAnsweringItem.value = null
    exAnsweringQuestions.value = []
    exAnswerSheet.value = []
    exDraftSaved.value = false
  } else {
    if (hwAnsweringItem.value) {
      const draftKey = `answer_draft_${hwAnsweringItem.value.id || hwAnsweringItem.value.homework_id}`
      const hasContent = hwAnswerSheet.value.some(a => a && a.trim())
      if (hasContent) {
        localStorage.setItem(draftKey, JSON.stringify(hwAnswerSheet.value))
      }
    }
    hwAnsweringItem.value = null
    hwAnsweringQuestions.value = []
    hwAnswerSheet.value = []
    hwDraftSaved.value = false
  }
}

function onAnswerChange() {
  // Auto-save is handled by the watchers on hwAnswerSheet / exAnswerSheet
}

async function retractSubmission(item: any) {
  const hid = item.homework_id || item.id
  try {
    const res = await deleteSubmissionApi({ homework_id: hid, student_username: studentUsername.value })
    if (res.data?.code === 200) {
      // Remove from local ref and localStorage
      mySubmissions.value = mySubmissions.value.filter((s: any) => (s.homework_id || s.id) !== hid)
      try {
        const stored = JSON.parse(localStorage.getItem('student_my_submissions') || '[]')
        const filtered = stored.filter((s: any) => s.homework_id !== hid && s.id !== hid)
        localStorage.setItem('student_my_submissions', JSON.stringify(filtered))
      } catch { /* ignore */ }
      message.success('已撤回，可在待完成列表中查看')
    } else {
      message.error(res.data?.msg || '撤回失败')
    }
  } catch { message.error('撤回失败，请检查网络') }
}

function clearAnswerDraft() {
  const item = hwAnsweringItem.value || exAnsweringItem.value
  if (!item) return
  const isExam = !!exAnsweringItem.value
  if (isExam) { exAnswerSheet.value = []; exDraftSaved.value = false }
  else { hwAnswerSheet.value = []; hwDraftSaved.value = false }
  const draftKey = `answer_draft_${item.id || item.homework_id}`
  localStorage.removeItem(draftKey)
  message.success('已清空')
}

async function submitAnswer() {
  const item = hwAnsweringItem.value || exAnsweringItem.value
  if (!item) return
  const isExam = !!exAnsweringItem.value
  const questions = isExam ? exAnsweringQuestions.value : hwAnsweringQuestions.value
  const sheet = isExam ? exAnswerSheet.value : hwAnswerSheet.value

  // Warn if all answers are empty
  const allEmpty = sheet.every(a => !a || !a.trim())
  if (allEmpty) {
    message.warning('所有答案均为空，请至少回答一题后再提交')
    return
  }

  submittingAnswer.value = true
  try {
    const answers = questions.map((q: any, i: number) => ({
      questionId: q.questionId || q.id || `q_${i}`,
      answer: sheet[i] || '',
    }))
    const res = await submitAnswerApi({
      homework_id: item.id || item.homework_id,
      type: item.type || 'homework',
      student_username: studentUsername.value,
      student_name: studentUsername.value,
      course_name: item.courseName || item.course_name || '',
      issue_name: item.title || '',
      answers: JSON.stringify(answers),
      scores: JSON.stringify(questions.map((q: any, i: number) => ({ questionId: q.questionId || q.id || `q_${i}`, score: 0, maxScore: q.score || 10 }))),
    })
    if (res.data?.code === 200) {
      message.success('提交成功')
      // Clear draft
      try {
        localStorage.removeItem(`answer_draft_${item.id || item.homework_id}`)
      } catch { /* ignore */ }
      // Close answer sheet and return to list
      if (isExam) { exAnsweringItem.value = null; exAnsweringQuestions.value = []; exAnswerSheet.value = []; exDraftSaved.value = false }
      else { hwAnsweringItem.value = null; hwAnsweringQuestions.value = []; hwAnswerSheet.value = []; hwDraftSaved.value = false }
      // Reload from backend (source of truth) and wait for completion
      await loadMySubmissions()
      await loadPublishedAssignments()
    } else {
      message.error(res.data?.msg || '提交失败')
    }
  } catch { message.error('提交失败，请检查网络') }
  submittingAnswer.value = false
}

function viewMySubmission(item: any) {
  viewingSubmission.value = item
  try {
    viewingAnswers.value = JSON.parse(item.answers || '[]')
    viewingScores.value = JSON.parse(item.scores || '[]')
  } catch {
    viewingAnswers.value = []
    viewingScores.value = []
  }
  showSubmissionDetail.value = true
}

function viewExamSubmission(item: any) {
  examViewingSubmission.value = item
  try {
    examViewingAnswers.value = JSON.parse(item.answers || '[]')
    examViewingScores.value = JSON.parse(item.scores || '[]')
  } catch {
    examViewingAnswers.value = []
    examViewingScores.value = []
  }
  showExamSubmissionDetail.value = true
}

// Watch for homework tab activation to load data
watch(() => activeTab.value, (tab) => {
  sessionStorage.setItem('student_active_tab', tab)
  window.dispatchEvent(new CustomEvent('sync-sidebar', { detail: tab }))
  if (tab === 'homework' && studentUsername.value) {
    loadPublishedAssignments()
    loadMySubmissions()
  }
  if (tab === 'exam' && studentUsername.value) {
    loadPublishedAssignments()
    loadMySubmissions()
  }
  if (tab === 'attendance' && studentUsername.value) {
    loadMyAttendance()
  }
})
// Also trigger when username becomes available while on homework/exam tab
watch(studentUsername, (val) => {
  if (val && activeTab.value === 'homework') {
    loadMySubmissions()
  }
  if (val && activeTab.value === 'exam') {
    loadMySubmissions()
  }
})

// ===== 通知中心 =====
const studentNotifications = ref<any[]>([])
const unreadNotifCount = computed(() => studentNotifications.value.filter((n: any) => !n.read).length)

function loadStudentNotifications() {
  try {
    const all = JSON.parse(localStorage.getItem('student_notifications') || '[]')
    studentNotifications.value = all
  } catch { studentNotifications.value = [] }
}

function markStudentNotifRead(id: string) {
  const n = studentNotifications.value.find((x: any) => x.id === id)
  if (n) {
    n.read = true
    localStorage.setItem('student_notifications', JSON.stringify(studentNotifications.value))
  }
}

watch(() => activeTab.value, (tab) => {
  if (tab === 'notifications') loadStudentNotifications()
}, { immediate: true })

// ===== 讨论区 =====
const studentDiscCourseFilter = ref('')
const showStudentNewTopicModal = ref(false)
const showStudentTopicDetail = ref(false)
const studentViewingTopic = ref<any>(null)
const studentReplyContent = ref('')
const studentNewTopicForm = reactive({ title: '', content: '' })
const studentDiscussions = ref<any[]>([])
const studentEditingTopicContent = ref('')
const studentEditingReplyIdx = ref(-1)
const studentEditingReplyContent = ref('')

const studentFilteredDiscussions = computed(() => {
  return studentDiscussions.value.filter((t: any) => t.courseName === studentDiscCourseFilter.value)
})

function loadStudentDiscussions(): any[] {
  try {
    const data = JSON.parse(localStorage.getItem('app_course_discussions') || '[]')
    studentDiscussions.value = data
    return data
  } catch { studentDiscussions.value = []; return [] }
}

function createStudentTopic() {
  if (!studentNewTopicForm.title.trim() || !studentNewTopicForm.content.trim()) return
  const topic = {
    id: 'disc_' + Date.now() + '_' + Math.random().toString(36).slice(2, 6),
    courseName: studentDiscCourseFilter.value,
    title: studentNewTopicForm.title.trim(),
    content: studentNewTopicForm.content.trim(),
    author: studentUsername.value || '学生',
    replies: [],
    createdAt: Date.now(),
  }
  // Re-read to avoid overwriting concurrent changes from teacher
  const latest = loadStudentDiscussions()
  latest.unshift(topic)
  studentDiscussions.value = latest
  localStorage.setItem('app_course_discussions', JSON.stringify(latest))
  showStudentNewTopicModal.value = false
  studentNewTopicForm.title = ''; studentNewTopicForm.content = ''
  message.success('话题已发布')
}

function openStudentTopicDetail(topic: any) {
  // Refresh from storage before displaying to catch new replies
  loadStudentDiscussions()
  const fresh = studentDiscussions.value.find((t: any) => t.id === topic.id)
  studentViewingTopic.value = fresh || topic
  studentReplyContent.value = ''
  showStudentTopicDetail.value = true
}

function submitStudentReply() {
  if (!studentReplyContent.value.trim() || !studentViewingTopic.value) return
  const reply = {
    author: studentUsername.value || '学生',
    content: studentReplyContent.value.trim(),
    createdAt: Date.now(),
  }
  // Re-read to avoid overwriting concurrent changes from teacher
  const latest = loadStudentDiscussions()
  const topic = latest.find((t: any) => t.id === studentViewingTopic.value!.id)
  if (topic) {
    if (!topic.replies) topic.replies = []
    topic.replies.push(reply)
    studentDiscussions.value = latest
    studentViewingTopic.value = topic
    localStorage.setItem('app_course_discussions', JSON.stringify(latest))
  }
  studentReplyContent.value = ''
  message.success('回复已发送')
}

function startEditStudentTopic() {
  if (studentViewingTopic.value) studentEditingTopicContent.value = studentViewingTopic.value.content
}
function saveStudentTopicEdit() {
  if (!studentViewingTopic.value || !studentEditingTopicContent.value.trim()) return
  const latest = loadStudentDiscussions()
  const t = latest.find((t: any) => t.id === studentViewingTopic.value!.id)
  if (t) { t.content = studentEditingTopicContent.value.trim(); studentDiscussions.value = latest; studentViewingTopic.value = t; localStorage.setItem('app_course_discussions', JSON.stringify(latest)) }
  studentEditingTopicContent.value = ''
  message.success('话题已更新')
}
function deleteStudentTopic(id: string) {
  const latest = loadStudentDiscussions().filter((t: any) => t.id !== id)
  studentDiscussions.value = latest; localStorage.setItem('app_course_discussions', JSON.stringify(latest))
  showStudentTopicDetail.value = false
  message.success('话题已删除')
}
function startEditStudentReply(idx: number) {
  studentEditingReplyIdx.value = idx
  studentEditingReplyContent.value = studentViewingTopic.value?.replies?.[idx]?.content || ''
}
function saveStudentReplyEdit(idx: number) {
  if (!studentViewingTopic.value || !studentEditingReplyContent.value.trim()) return
  const latest = loadStudentDiscussions()
  const t = latest.find((t: any) => t.id === studentViewingTopic.value!.id)
  if (t?.replies?.[idx]) { t.replies[idx].content = studentEditingReplyContent.value.trim(); studentDiscussions.value = latest; studentViewingTopic.value = t; localStorage.setItem('app_course_discussions', JSON.stringify(latest)) }
  studentEditingReplyIdx.value = -1; studentEditingReplyContent.value = ''
  message.success('回复已更新')
}
function deleteStudentReply(idx: number) {
  if (!studentViewingTopic.value) return
  const latest = loadStudentDiscussions()
  const t = latest.find((t: any) => t.id === studentViewingTopic.value!.id)
  if (t?.replies) { t.replies.splice(idx, 1); studentDiscussions.value = latest; studentViewingTopic.value = t; localStorage.setItem('app_course_discussions', JSON.stringify(latest)) }
  message.success('回复已删除')
}

watch(() => activeTab.value, (tab) => {
  if (tab === 'discussions') loadStudentDiscussions()
})
</script>

<style scoped>
.workspace { padding: 24px; max-width: 1400px; margin: 0 auto; }
.stats-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 24px; }
@media (max-width: 1100px) { .stats-row { grid-template-columns: repeat(2, 1fr); } }

.tabs-bar { display: flex; gap: 4px; background: #fff; border-radius: 12px; padding: 6px; margin-bottom: 20px; border: 1px solid #E2E8F0; }
.tab-btn { display: flex; align-items: center; gap: 8px; padding: 10px 20px; border: none; background: transparent; border-radius: 8px; color: #64748B; font-size: 14px; font-weight: 500; cursor: pointer; transition: all 0.2s; font-family: inherit; }
.tab-btn:hover { color: #334155; background: #F1F5F9; }
.tab-btn.active { background: #0D9488; color: #fff; box-shadow: 0 2px 8px rgba(13,148,136,0.3); }

.panel { background: #fff; border-radius: 16px; border: 1px solid #E2E8F0; padding: 24px; min-height: 400px; }
.panel-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; }
.panel-header h2 { font-size: 18px; font-weight: 700; color: #0F172A; }
.panel-body { padding: 40px 0; }

.btn-primary { display: inline-flex; align-items: center; gap: 8px; padding: 10px 22px; border: none; background: linear-gradient(135deg, #0D9488, #14B8A6); color: #fff; border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.2s; font-family: inherit; box-shadow: 0 2px 8px rgba(13,148,136,0.25); }
.btn-primary:hover:not(:disabled) { transform: translateY(-1px); box-shadow: 0 6px 20px rgba(13,148,136,0.35); }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-sm { display: inline-flex; align-items: center; gap: 6px; padding: 6px 14px; border: 1px solid #E2E8F0; background: #fff; border-radius: 6px; color: #475569; font-size: 13px; font-weight: 500; cursor: pointer; transition: all 0.2s; font-family: inherit; }
.btn-sm:hover { border-color: #0D9488; color: #0D9488; }
.btn-sm-danger { border-color: #FECACA; color: #DC2626; background: #FEF2F2; }
.btn-sm-danger:hover { border-color: #DC2626; background: #FEE2E2; color: #B91C1C; }

/* Course Cards */
.course-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 16px; }
.course-card { border: 1px solid #E2E8F0; border-radius: 12px; padding: 20px; transition: all 0.2s; display: flex; flex-direction: column; }
.course-card:hover { border-color: #CBD5E1; box-shadow: 0 4px 16px rgba(0,0,0,0.04); }
.cc-header { display: flex; align-items: center; gap: 14px; margin-bottom: 12px; flex: 1; }
.cc-icon { width: 42px; height: 42px; border-radius: 10px; background: linear-gradient(135deg, #0D9488, #14B8A6); display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.cc-info h3 { font-size: 16px; font-weight: 600; color: #0F172A; }
.cc-meta-text { font-size: 13px; color: #94A3B8; }
.cc-actions { display: flex; gap: 8px; margin-top: 12px; }

/* Chat */
.chat-layout { display: flex; gap: 0; height: calc(100vh - 280px); min-height: 500px; border: 1px solid #E2E8F0; border-radius: 12px; overflow: hidden; }
.chat-sidebar { width: 200px; border-right: 1px solid #E2E8F0; background: #F8FAFC; padding: 16px; overflow-y: auto; flex-shrink: 0; }
.chat-side-title { font-size: 12px; font-weight: 700; color: #94A3B8; text-transform: uppercase; letter-spacing: 1px; margin-bottom: 10px; }
.course-select-item { display: flex; flex-direction: column; padding: 10px 12px; border: none; background: none; width: 100%; text-align: left; cursor: pointer; border-radius: 8px; transition: all 0.2s; font-family: inherit; margin-bottom: 4px; }
.course-select-item:hover { background: #E2E8F0; }
.course-select-item.active { background: rgba(13,148,136,0.1); }
.csi-name { font-size: 14px; font-weight: 600; color: #0F172A; }
.csi-teacher { font-size: 12px; color: #94A3B8; }
.chat-main { flex: 1; display: flex; flex-direction: column; }
.chat-empty { flex: 1; display: flex; align-items: center; justify-content: center; }
.chat-container { display: flex; flex-direction: column; height: 100%; }
.chat-messages { flex: 1; overflow-y: auto; padding: 16px; display: flex; flex-direction: column; gap: 12px; }
.chat-msg { display: flex; gap: 10px; max-width: 85%; }
.chat-msg.user { align-self: flex-end; flex-direction: row-reverse; }
.chat-msg.assistant { align-self: flex-start; }
.msg-avatar { width: 32px; height: 32px; border-radius: 50%; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.chat-msg.assistant .msg-avatar { background: linear-gradient(135deg, #0D9488, #14B8A6); }
.chat-msg.user .msg-avatar { background: linear-gradient(135deg, #4F46E5, #6366F1); }
.msg-content { padding: 10px 16px; border-radius: 12px; font-size: 14px; line-height: 1.7; }
.chat-msg.user .msg-content { background: #EEF2FF; color: #334155; border-bottom-right-radius: 4px; }
.chat-msg.assistant .msg-content { background: #F8FAFC; border: 1px solid #E2E8F0; border-bottom-left-radius: 4px; }
.chat-typing { padding: 8px 16px; font-size: 13px; color: #94A3B8; }
.chat-input-area { display: flex; gap: 10px; padding: 12px; border-top: 1px solid #E2E8F0; align-items: flex-end; }
.chat-input-area :deep(.ant-input) { flex: 1; }

/* Practice */
.practice-mode-switch { display: flex; gap: 2px; background: #F1F5F9; border-radius: 8px; padding: 4px; }
.mode-btn { padding: 7px 16px; border: none; background: none; border-radius: 6px; font-size: 13px; font-weight: 500; cursor: pointer; transition: all 0.2s; font-family: inherit; color: #64748B; }
.mode-btn.active { background: #fff; color: #0F172A; font-weight: 600; box-shadow: 0 1px 3px rgba(0,0,0,0.06); }

.ai-input-area { background: #F8FAFC; border-radius: 12px; padding: 20px; margin-bottom: 20px; display: flex; flex-direction: column; gap: 12px; }
.ai-input-area label { font-size: 13px; font-weight: 600; color: #475569; }

.pq-card { border: 1px solid #E2E8F0; border-radius: 12px; padding: 20px; margin-bottom: 12px; transition: all 0.2s; }
.pq-card:hover { border-color: #CBD5E1; }
.pq-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.pq-num { font-weight: 700; color: #0D9488; font-size: 14px; }
.pq-type { font-size: 12px; color: #94A3B8; background: #F1F5F9; padding: 2px 10px; border-radius: 999px; }
.pq-body { margin-bottom: 12px; }
.pq-options { display: flex; flex-direction: column; gap: 8px; }
.pq-option { display: flex; align-items: center; gap: 10px; padding: 10px 14px; border: 1px solid #E2E8F0; border-radius: 8px; cursor: pointer; transition: all 0.2s; }
.pq-option:hover { border-color: #0D9488; background: rgba(13,148,136,0.03); }
.pq-option.selected { border-color: #0D9488; background: rgba(13,148,136,0.08); font-weight: 500; }
.pq-opt-letter { width: 28px; height: 28px; border-radius: 50%; background: #F1F5F9; display: flex; align-items: center; justify-content: center; font-weight: 600; font-size: 13px; flex-shrink: 0; }
.pq-answer-input { margin-top: 12px; }
.pq-answer-input label { display: block; font-size: 13px; font-weight: 600; color: #475569; margin-bottom: 6px; }

.ai-result-card { border: 1px solid #E2E8F0; border-radius: 12px; padding: 20px; background: #fff; }
.ai-result-header { margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid #F1F5F9; }
.ai-result-header h3 { font-size: 16px; font-weight: 700; color: #0F172A; margin: 0; }

.form-field { margin-bottom: 14px; }
.form-field label { display: block; margin-bottom: 6px; font-size: 13px; font-weight: 600; color: #475569; }
.join-form { padding: 8px 0; }
.field-hint { font-size: 13px; color: #64748B; line-height: 1.5; }

/* Binding Status */
.binding-status { display: inline-flex; align-items: center; gap: 6px; background: #ECFDF5; border: 1px solid #A7F3D0; border-radius: 8px; padding: 6px 12px; font-size: 13px; color: #059669; white-space: nowrap; }
.binding-status code { background: #fff; padding: 1px 6px; border-radius: 4px; font-size: 12px; color: #0D9488; }

@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
.animate-fade-in { animation: fadeIn 0.3s ease; }

/* ===== 课程作业 ===== */
.hw-tab-switch { display: flex; gap: 2px; background: #F1F5F9; border-radius: 8px; padding: 4px; }
.hw-switch-btn { padding: 7px 16px; border: none; background: none; border-radius: 6px; font-size: 13px; font-weight: 500; cursor: pointer; transition: all 0.2s; font-family: inherit; color: #64748B; }
.hw-switch-btn.active { background: #fff; color: #0F172A; font-weight: 600; box-shadow: 0 1px 3px rgba(0,0,0,0.06); }

/* Tutor sub-tab switch */
.tutor-subtab-switch { display: flex; gap: 2px; background: #F1F5F9; border-radius: 8px; padding: 4px; }
.subtab-btn { padding: 7px 16px; border: none; background: none; border-radius: 6px; font-size: 13px; font-weight: 500; cursor: pointer; transition: all 0.2s; font-family: inherit; color: #64748B; }
.subtab-btn.active { background: #fff; color: #0F172A; font-weight: 600; box-shadow: 0 1px 3px rgba(0,0,0,0.06); }

/* Attendance badge tags */
.as-tag { display: inline-block; padding: 2px 10px; border-radius: 20px; font-size: 12px; font-weight: 600; }
.as-tag.present { background: #D1FAE5; color: #059669; }
.as-tag.absent { background: #FEE2E2; color: #DC2626; }
.as-tag.leave { background: #FEF3C7; color: #D97706; }
.as-tag.exam { background: #FFFBEB; color: #D97706; }

/* Attendance table */
.attendance-table-wrap { margin-top: 8px; }

.homework-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(340px, 1fr)); gap: 16px; }
.homework-card { border: 1px solid #E2E8F0; border-radius: 12px; padding: 20px; transition: all 0.2s; background: #fff; }
.homework-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.04); }
.exam-card { border-left: 4px solid #F59E0B; }
.hw-header { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
.hw-header h3 { font-size: 16px; font-weight: 600; color: #0F172A; margin: 0; }
.hw-course { font-size: 12px; background: #EEF2FF; color: #0D9488; padding: 2px 10px; border-radius: 20px; font-weight: 500; }
.hw-type-badge { font-size: 11px; padding: 2px 10px; border-radius: 20px; font-weight: 600; }
.hw-hw { background: #D1FAE5; color: #059669; }
.hw-exam { background: #FFFBEB; color: #D97706; }
.hw-desc { font-size: 13px; color: #64748B; margin: 0 0 12px; line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.hw-meta { display: flex; flex-direction: column; gap: 4px; margin-bottom: 12px; }
.hw-meta-row { display: flex; gap: 14px; font-size: 12px; color: #64748B; align-items: center; flex-wrap: wrap; }
.hw-meta-row span { white-space: nowrap; }
.hw-meta-sub { font-size: 11px; color: #94A3B8; }
.hw-meta-sub span { white-space: nowrap; }
.hw-actions { display: flex; gap: 8px; }
.hw-score { font-weight: 700; color: #0D9488; font-size: 13px; }
.hw-status-done { font-weight: 500; color: #64748B; font-size: 13px; }

/* Answer Sheet */
.answer-sheet { padding: 4px 0; }
.as-info { display: flex; gap: 10px; align-items: center; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid #F1F5F9; }
.as-course { font-size: 13px; font-weight: 600; color: #0D9488; background: #ECFDF5; padding: 2px 10px; border-radius: 8px; }
.as-type { font-size: 12px; color: #64748B; background: #F1F5F9; padding: 2px 10px; border-radius: 8px; }
.as-deadline { font-size: 12px; color: #EF4444; }
.as-question-card { border: 1px solid #E2E8F0; border-radius: 10px; padding: 16px; margin-bottom: 12px; }
.as-q-header { display: flex; gap: 10px; align-items: center; margin-bottom: 10px; }
.as-q-num { font-size: 13px; font-weight: 700; color: #0D9488; }
.as-q-type { font-size: 12px; background: #F1F5F9; padding: 2px 8px; border-radius: 4px; color: #64748B; }
.as-q-score { font-size: 12px; color: #94A3B8; }
.as-options { display: flex; flex-direction: column; gap: 8px; margin-top: 10px; }
.as-option { display: flex; align-items: center; gap: 10px; padding: 10px 14px; border: 1px solid #E2E8F0; border-radius: 8px; cursor: pointer; transition: all 0.2s; }
.as-option:hover { border-color: #0D9488; background: rgba(13,148,136,0.03); }
.as-option.selected { border-color: #0D9488; background: rgba(13,148,136,0.08); font-weight: 500; }
.as-opt-letter { width: 28px; height: 28px; border-radius: 50%; background: #F1F5F9; display: flex; align-items: center; justify-content: center; font-weight: 600; font-size: 13px; flex-shrink: 0; }
.as-text-input { margin-top: 10px; }
.as-actions { display: flex; gap: 10px; margin-top: 16px; padding-top: 12px; border-top: 1px solid #F1F5F9; justify-content: flex-end; }

/* Inline Answer Sheet Panel */
.answer-sheet-panel { padding: 4px 0; }
.as-panel-top { display: flex; align-items: flex-start; gap: 12px; margin-bottom: 20px; padding-bottom: 16px; border-bottom: 1px solid #E2E8F0; flex-wrap: wrap; }
.as-panel-top .btn-sm { flex-shrink: 0; }
.as-panel-info { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; flex: 1; }
.as-panel-title { font-size: 16px; font-weight: 700; color: #0F172A; }
.as-type-tag { font-size: 12px; padding: 2px 10px; border-radius: 20px; font-weight: 600; }
.as-type-tag.as-hw { background: #D1FAE5; color: #059669; }
.as-type-tag.as-exam { background: #FFFBEB; color: #D97706; }
.as-draft-indicator { font-size: 12px; color: #059669; background: #ECFDF5; padding: 2px 10px; border-radius: 8px; animation: fadeIn 0.3s ease; }

/* Submission Detail */
.submission-detail { padding: 4px 0; }
.sd-header { display: flex; gap: 14px; align-items: center; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid #F1F5F9; }
.sd-title { font-size: 15px; font-weight: 600; color: #0F172A; }
.sd-score { font-size: 14px; font-weight: 700; color: #0D9488; }
.sd-status { font-size: 13px; color: #64748B; }
.sd-answer-card { border: 1px solid #E2E8F0; border-radius: 8px; padding: 14px; margin-bottom: 10px; }
.sd-q-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.sd-q-num { font-size: 13px; font-weight: 700; color: #0D9488; }
.sd-q-score { font-size: 12px; color: #64748B; }
.sd-answer-text { background: #F8FAFC; padding: 10px; border-radius: 6px; font-size: 14px; color: #1E293B; min-height: 36px; line-height: 1.5; }

/* ===== 通知中心 ===== */
.notif-unread-badge { font-size: 12px; background: #EF4444; color: #fff; padding: 2px 10px; border-radius: 10px; font-weight: 600; }
.notif-list { display: flex; flex-direction: column; gap: 10px; }
.notif-card { border: 1px solid #E2E8F0; border-radius: 10px; padding: 16px; background: #fff; transition: box-shadow .15s; }
.notif-card:hover { box-shadow: 0 2px 8px rgba(0,0,0,.04); }
.notif-unread { border-left: 3px solid #0D9488; background: #FAFDFB; }
.notif-header { display: flex; gap: 10px; align-items: center; margin-bottom: 6px; }
.notif-course { font-size: 12px; background: #ECFDF5; color: #0D9488; padding: 2px 10px; border-radius: 10px; font-weight: 500; }
.notif-type-badge { font-size: 11px; background: #F1F5F9; color: #64748B; padding: 2px 10px; border-radius: 10px; }
.notif-time { font-size: 12px; color: #94A3B8; margin-left: auto; }
.notif-title { font-size: 15px; font-weight: 600; color: #0F172A; margin: 0 0 6px; }
.notif-content { font-size: 13px; color: #475569; margin: 0 0 10px; line-height: 1.5; }
.notif-actions { display: flex; gap: 8px; }

/* ===== 讨论区 ===== */
.course-select { border: 1px solid #CBD5E1 !important; border-radius: 8px; }
.disc-list { display: flex; flex-direction: column; gap: 10px; }
.disc-card { border: 1px solid #E2E8F0; border-radius: 10px; padding: 16px; background: #fff; cursor: pointer; transition: all .15s; }
.disc-card:hover { border-color: #CBD5E1; box-shadow: 0 2px 8px rgba(0,0,0,.04); }
.disc-card-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 6px; }
.disc-title { font-size: 15px; font-weight: 600; color: #0F172A; margin: 0; }
.disc-replies { font-size: 12px; background: #ECFDF5; color: #0D9488; padding: 2px 10px; border-radius: 10px; font-weight: 500; white-space: nowrap; }
.disc-preview { font-size: 13px; color: #64748B; margin: 0 0 8px; line-height: 1.5; }
.disc-meta { display: flex; gap: 14px; font-size: 12px; color: #94A3B8; }

/* Topic Detail */
.topic-detail { padding: 4px 0; }
.topic-body { margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid #F1F5F9; }
.topic-meta { display: flex; align-items: center; justify-content: space-between; font-size: 12px; color: #94A3B8; margin-bottom: 10px; flex-wrap: wrap; gap: 6px; }
.topic-meta-left { display: flex; align-items: center; gap: 10px; }
.topic-meta-right { display: flex; gap: 6px; }
.topic-author { font-weight: 600; color: #334155; }
.topic-time { color: #94A3B8; }
.topic-content { font-size: 14px; color: #1E293B; line-height: 1.7; white-space: pre-wrap; }
.topic-edit-area { margin-top: 8px; }
.no-replies { text-align: center; padding: 16px; color: #94A3B8; font-size: 13px; }
.topic-replies-section { margin-bottom: 16px; }
.section-label { font-size: 13px; font-weight: 700; color: #64748B; text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 12px; }
.reply-card { background: #F8FAFC; border-radius: 8px; padding: 12px 14px; margin-bottom: 8px; }
.reply-meta { display: flex; align-items: center; gap: 10px; margin-bottom: 4px; }
.reply-author { font-size: 13px; font-weight: 600; color: #334155; }
.reply-time { font-size: 12px; color: #94A3B8; }
.reply-actions-right { margin-left: auto; display: flex; gap: 6px; }
.reply-content { font-size: 13px; color: #475569; line-height: 1.5; }
.reply-edit-area { margin-top: 6px; }
.no-replies { text-align: center; color: #94A3B8; font-size: 13px; padding: 20px; }
.reply-input-area { margin-top: 12px; }

/* Homework Form (reused for topic modal) */
.homework-form { padding: 8px 0; }
.form-field { margin-bottom: 14px; }
.form-field label { display: block; margin-bottom: 6px; font-size: 13px; font-weight: 600; color: #475569; }

/* Profile Card - Full Width */
.profile-full-card { background: #fff; border-radius: 16px; padding: 28px 32px; border: 1px solid #E2E8F0; }
.profile-top { display: flex; align-items: center; gap: 20px; margin-bottom: 24px; padding-bottom: 20px; border-bottom: 1px solid #F1F5F9; }
.profile-avatar { width: 72px; height: 72px; min-width: 72px; border-radius: 50%; background: linear-gradient(135deg, #EEF2FF, #E0E7FF); display: flex; align-items: center; justify-content: center; color: #4F46E5; }
.profile-avatar.student-avatar { background: linear-gradient(135deg, #ECFDF5, #D1FAE5); color: #059669; }
.profile-title-area { display: flex; flex-direction: column; gap: 4px; }
.profile-name { font-size: 20px; font-weight: 700; color: #0F172A; margin: 0; }
.profile-role-badge { display: inline-block; padding: 3px 12px; border-radius: 999px; background: rgba(79,70,229,0.08); color: #4F46E5; font-size: 12px; font-weight: 600; width: fit-content; }
.profile-role-badge.student-badge { background: rgba(5,150,105,0.08); color: #059669; }
.profile-detail-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 0; }
.detail-item { padding: 14px 0; border-bottom: 1px solid #F8FAFC; display: flex; flex-direction: column; gap: 4px; }
.detail-item.full-width { grid-column: 1 / -1; }
.detail-label { font-size: 12px; color: #94A3B8; font-weight: 500; }
.detail-value { font-size: 14px; color: #334155; font-weight: 600; display: flex; align-items: center; gap: 6px; }
.detail-value code { background: #F1F5F9; padding: 2px 8px; border-radius: 4px; font-size: 12px; color: #059669; }
.btn-bind-inline { border: 1px solid #059669; background: none; color: #059669; cursor: pointer; font-size: 12px; padding: 2px 10px; border-radius: 4px; font-family: inherit; }
.btn-bind-inline:hover { background: #ECFDF5; }
.btn-copy-inline { border: none; background: none; cursor: pointer; font-size: 14px; padding: 0; opacity: 0.6; }
.btn-copy-inline:hover { opacity: 1; }
</style>
