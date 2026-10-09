<template>
  <div class="workspace">

    <!-- Panel: Profile -->
    <div v-if="activeTab === 'profile'" class="panel animate-fade-in">
      <div class="panel-header"><h2>个人中心</h2></div>
      <!-- Stats -->
      <div class="stats-row">
        <StatCard icon='<svg viewBox="0 0 24 24" fill="none" width="22" height="22"><path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/><path d="M2 17L12 22L22 17M2 12L12 17L22 12" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>'
          label="我的课程" :value="stats.courses" bg="linear-gradient(135deg, #4F46E5, #6366F1)" />
        <StatCard icon='<svg viewBox="0 0 24 24" fill="none" width="22" height="22"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8zM23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>'
          label="学生人数" :value="stats.students" bg="linear-gradient(135deg, #0D9488, #14B8A6)" />
        <StatCard icon='<svg viewBox="0 0 24 24" fill="none" width="22" height="22"><path d="M9 11l3 3L22 4M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>'
          label="练习题目" :value="stats.issues" bg="linear-gradient(135deg, #F59E0B, #F97316)" />
        <StatCard icon='<svg viewBox="0 0 24 24" fill="none" width="22" height="22"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>'
          label="课件资源" :value="stats.resources" bg="linear-gradient(135deg, #7C3AED, #8B5CF6)" />
      </div>
      <!-- Info Card -->
      <div class="profile-full-card">
        <div class="profile-top">
          <div class="profile-avatar">
            <svg viewBox="0 0 24 24" fill="none" width="48" height="48"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
          </div>
          <div class="profile-title-area">
            <h3 class="profile-name">{{ teacherInfo.teacher_name || teacherInfo.name || '未知' }}</h3>
            <span class="profile-role-badge">教师 · {{ teacherInfo.teacher_position || '未知职位' }}</span>
          </div>
        </div>
        <div class="profile-detail-grid">
          <div class="detail-item"><span class="detail-label">用户名</span><span class="detail-value">{{ teacherInfo.teacher_account || teacherUsername || '-' }}</span></div>
          <div class="detail-item"><span class="detail-label">性别</span><span class="detail-value">{{ teacherInfo.teacher_gender || teacherInfo.gender || '-' }}</span></div>
          <div class="detail-item"><span class="detail-label">年龄</span><span class="detail-value">{{ teacherInfo.teacher_age || teacherInfo.age || '-' }}</span></div>
          <div class="detail-item"><span class="detail-label">专业</span><span class="detail-value">{{ teacherInfo.teacher_major || teacherInfo.major || '-' }}</span></div>
          <div class="detail-item"><span class="detail-label">班级</span><span class="detail-value">{{ teacherInfo.teacher_class_name || teacherInfo.class_name || '-' }}</span></div>
          <div class="detail-item"><span class="detail-label">班级学生数</span><span class="detail-value">{{ teacherInfo.teacher_class_student_number || teacherInfo.class_student_number || '0' }}</span></div>
          <div class="detail-item full-width" v-if="teacherInfo.teacher_position === '班主任'"><span class="detail-label">绑定码</span><span class="detail-value"><code>{{ teacherBindingCode || '未生成' }}</code><button v-if="teacherBindingCode" class="btn-copy-inline" @click="copyBindingCode" title="复制">📋</button></span></div>
        </div>
      </div>
    </div>

    <!-- Panel: Course Management -->
    <div v-if="activeTab === 'courses'" class="panel animate-fade-in">
      <div class="panel-header">
        <h2>课程管理</h2>
        <div style="display:flex;gap:10px;align-items:center">
          <button class="btn-primary" @click="openNewCourseModal">
            <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/></svg>
            创建课程
          </button>
        </div>
      </div>

      <!-- ===== AI 搜索中的任务卡片 ===== -->
      <div v-if="aiStore.tasks.length > 0" class="ai-tasks-section">
        <div class="section-label">AI 搜索任务</div>
        <div class="course-grid">
          <div v-for="task in aiStore.tasks" :key="task.taskId"
            class="course-card ai-task-card"
            :class="{
              'task-searching': task.status === 'searching',
              'task-completed': task.status === 'completed',
              'task-failed': task.status === 'failed',
              'task-satisfied': task.status === 'satisfied',
            }"
            @click="openTaskModal(task.taskId)"
          >
            <!-- 删除按钮 -->
            <button class="task-delete-btn" @click.stop="handleDeleteTask(task.taskId)" title="删除此搜索任务">
              <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M18 6L6 18M6 6l12 12" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/></svg>
            </button>
            <!-- 搜索中状态 -->
            <template v-if="task.status === 'searching'">
              <div class="cc-header">
                <div class="cc-icon ai-searching-icon">
                  <svg viewBox="0 0 24 24" fill="none" width="20" height="20"><circle cx="12" cy="12" r="10" stroke="white" stroke-width="2"/><path d="M12 6v6l4 2" stroke="white" stroke-width="2" stroke-linecap="round"/></svg>
                </div>
                <div class="cc-info">
                  <h3>{{ task.courseName }}</h3>
                  <span class="cc-code task-status-text">AI 正在搜索相关知识...</span>
                </div>
              </div>
              <div class="task-progress-bar">
                <div class="task-progress-fill searching-animation"></div>
              </div>
              <div class="cc-meta">
                <span>{{ task.courseHours }} 课时</span>
                <span>{{ task.courseScore }} 学分</span>
              </div>
              <div class="task-hint">点击查看搜索进度</div>
            </template>

            <!-- 搜索完成状态 -->
            <template v-if="task.status === 'completed'">
              <div class="cc-header">
                <div class="cc-icon ai-done-icon">
                  <svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14M22 4L12 14.01l-3-3" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                </div>
                <div class="cc-info">
                  <h3>{{ task.courseName }}</h3>
                  <span class="cc-code task-status-text done">AI 搜索完成 ✓</span>
                </div>
              </div>
              <div class="task-progress-bar">
                <div class="task-progress-fill complete-bar"></div>
              </div>
              <div class="cc-meta">
                <span>{{ task.courseHours }} 课时</span>
                <span>{{ task.courseScore }} 学分</span>
              </div>
              <div class="task-hint done-hint">点击查看搜索结果 →</div>
            </template>

            <!-- 搜索失败状态 -->
            <template v-if="task.status === 'failed'">
              <div class="cc-header">
                <div class="cc-icon ai-fail-icon">
                  <svg viewBox="0 0 24 24" fill="none" width="20" height="20"><circle cx="12" cy="12" r="10" stroke="white" stroke-width="2"/><path d="M15 9l-6 6M9 9l6 6" stroke="white" stroke-width="2" stroke-linecap="round"/></svg>
                </div>
                <div class="cc-info">
                  <h3>{{ task.courseName }}</h3>
                  <span class="cc-code task-status-text fail">搜索失败</span>
                </div>
              </div>
              <div class="task-error-msg">{{ task.error }}</div>
              <div class="cc-meta">
                <span>{{ task.courseHours }} 课时</span>
                <span>{{ task.courseScore }} 学分</span>
              </div>
              <div class="task-hint fail-hint">点击重新搜索</div>
            </template>

            <!-- 已满意/待创建状态 -->
            <template v-if="task.status === 'satisfied'">
              <div class="cc-header">
                <div class="cc-icon ai-satisfied-icon">
                  <svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M9 11l3 3L22 4M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                </div>
                <div class="cc-info">
                  <h3>{{ task.courseName }}</h3>
                  <span class="cc-code task-status-text satisfied">课件已确认，点击创建 →</span>
                </div>
              </div>
              <div class="task-progress-bar">
                <div class="task-progress-fill complete-bar"></div>
              </div>
              <div class="cc-meta">
                <span>{{ task.courseHours }} 课时</span>
                <span>{{ task.courseScore }} 学分</span>
              </div>
              <div class="task-hint satisfied-hint">点击完成课程创建</div>
            </template>
          </div>
        </div>
      </div>

      <!-- ===== 已创建的课程卡片 ===== -->
      <div v-if="courses.length > 0 || aiStore.tasks.length === 0">
        <div v-if="courses.length > 0" class="course-grid">
          <div v-for="course in courses" :key="course.course_id" class="course-card">
            <div class="cc-header">
              <div class="cc-icon">
                <svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M4 19.5A2.5 2.5 0 0 0 6.5 22H20V2H6.5A2.5 2.5 0 0 0 4 4.5v15z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              </div>
              <div class="cc-info">
                <h3>{{ course.course_name }}</h3>
                <span class="cc-code">邀请码: <code>{{ course.course_invite_code }}</code>
                  <button class="cc-copy-btn" @click.stop="copyInviteCode(course.course_invite_code)" title="复制邀请码">
                    <svg viewBox="0 0 24 24" fill="none" width="12" height="12"><rect x="9" y="9" width="13" height="13" rx="2" stroke="currentColor" stroke-width="2"/><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1" stroke="currentColor" stroke-width="2"/></svg>
                  </button>
                </span>
              </div>
            </div>
            <div class="cc-meta">
              <span>{{ course.course_hours }} 课时</span>
              <span>{{ course.course_score }} 学分</span>
            </div>
            <div class="cc-actions">
              <button class="btn-sm" @click="selectCourse(course); activeTab = 'ai-tools'">
                <svg viewBox="0 0 24 24" fill="none" width="14" height="14"><path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                AI 工具
              </button>
              <a-popconfirm title="确定删除此课程?" ok-text="确定" cancel-text="取消" @confirm="handleDeleteCourse(course)">
                <button class="btn-sm btn-sm-danger">删除</button>
              </a-popconfirm>
            </div>
          </div>
        </div>
        <EmptyState v-else-if="aiStore.tasks.length === 0" title="暂无课程" description="创建你的第一门课程，开始 AI 智能教学" />
      </div>

      <!-- ===== 创建课程 Modal ===== -->
      <a-modal v-model:open="showCreateCourse" :title="modalTitle" :footer="null" width="680px" @cancel="handleModalCancel">
        <div class="create-form">
          <!-- 如果是查看已有任务，显示搜索提示词回显 -->
          <div v-if="viewingTask" class="task-context-banner">
            <span class="task-context-label">🔍 搜索提示词：</span>
            <span class="task-context-text">{{ viewingTask.searchPrompt }}</span>
          </div>

          <div class="form-field">
            <label>课程名称</label>
            <a-input v-model:value="newCourse.name" placeholder="例如：数据结构与算法" :disabled="!!viewingTask" />
          </div>
          <div class="form-row">
            <div class="form-field">
              <label>课时数</label>
              <a-input-number v-model:value="newCourse.hours" :min="1" :max="200" style="width:100%" :disabled="!!viewingTask" />
            </div>
            <div class="form-field">
              <label>学分</label>
              <a-input-number v-model:value="newCourse.score" :min="0.5" :max="20" :step="0.5" style="width:100%" :disabled="!!viewingTask" />
            </div>
          </div>

          <!-- 如果不是查看任务，显示课件来源切换 -->
          <template v-if="!viewingTask">
            <div class="form-field">
              <label>课件来源</label>
              <div class="source-toggle">
                <button type="button" class="source-btn" :class="{ active: coursewareSource === 'upload' }" @click="coursewareSource = 'upload'">
                  <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4M17 8l-5-5-5 5M12 3v12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                  上传文件
                </button>
                <button type="button" class="source-btn" :class="{ active: coursewareSource === 'ai-search' }" @click="coursewareSource = 'ai-search'">
                  <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><circle cx="11" cy="11" r="8" stroke="currentColor" stroke-width="2"/><path d="M21 21l-4.35-4.35" stroke="currentColor" stroke-width="2" stroke-linecap="round"/><path d="M8 11h6M11 8v6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
                  AI 智能搜索
                </button>
              </div>
            </div>

            <!-- Upload Tab -->
            <div v-if="coursewareSource === 'upload'" class="form-field">
              <label>上传课件 (可选，支持 PDF/DOCX/TXT/MD)</label>
              <input type="file" @change="handleFileSelect" accept=".pdf,.docx,.txt,.md" ref="fileInput" style="display:none" />
              <div class="upload-zone" @click="($refs.fileInput as HTMLInputElement).click()">
                <svg viewBox="0 0 24 24" fill="none" width="24" height="24"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4M17 8l-5-5-5 5M12 3v12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                <span v-if="!newCourse.file">点击上传课件文件</span>
                <span v-else class="file-name">{{ newCourse.file.name }}</span>
              </div>
            </div>

            <!-- AI Search Tab (new search) -->
            <div v-if="coursewareSource === 'ai-search'" class="ai-search-section">
              <div class="form-field">
                <label>搜索提示词 <span class="label-badge">AI</span></label>
                <a-textarea
                  v-model:value="searchPrompt"
                  placeholder="描述你需要的课件内容方向，例如：数据结构入门教学PPT、重点包含树和图的核心算法、适合大二学生的难度..."
                  :rows="3"
                />
                <span class="field-hint">越详细的描述，AI 搜索到的课件内容越精准</span>
              </div>

              <button type="button" class="btn-search-ai" @click="handleSearchAndClose" :disabled="!searchPrompt.trim() || !newCourse.name || searchingCourseware">
                {{ searchingCourseware ? '正在提交搜索...' : 'AI 搜索相关知识' }}
              </button>
            </div>
          </template>

          <!-- 查看任务时：搜索中 -->
          <div v-if="viewingTask && viewingTask.status === 'searching'" class="searching-state">
            <div class="searching-spinner"></div>
            <p>正在搜索网络资源并整合课件内容...</p>
            <span class="searching-hint">AI 正在从互联网搜索相关教学资料，请耐心等待约 30-60 秒</span>
            <div class="task-progress-bar" style="margin-top:12px;width:100%">
              <div class="task-progress-fill searching-animation"></div>
            </div>
          </div>

          <!-- 查看任务时：搜索完成 -->
          <div v-if="viewingTask && viewingTask.status === 'completed'" class="search-result-card">
            <div class="search-result-header">
              <div class="sr-header-left">
                <svg viewBox="0 0 24 24" fill="none" width="18" height="18"><circle cx="12" cy="12" r="10" stroke="#10B981" stroke-width="2"/><path d="M8 12l2 2 5-5" stroke="#10B981" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                <span>AI 已为你整合网络搜索结果</span>
              </div>
              <button type="button" class="btn-sm" @click="copyResult(viewingTask.result)">复制全文</button>
            </div>
            <div class="search-result-body">
              <MarkdownRenderer :content="viewingTask.result" />
            </div>
            <div class="search-result-actions">
              <button type="button" class="btn-satisfied" @click="handleSatisfiedTask">
                <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M9 11l3 3L22 4M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                满意，使用此课件
              </button>
              <button type="button" class="btn-retry" @click="handleRetrySearchTask">
                <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M23 4v6h-6M1 20v-6h6M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                不满意，重新搜索
              </button>
            </div>
          </div>

          <!-- 查看任务时：搜索失败 -->
          <div v-if="viewingTask && viewingTask.status === 'failed'" class="search-failed-state">
            <div class="failed-icon">
              <svg viewBox="0 0 24 24" fill="none" width="40" height="40"><circle cx="12" cy="12" r="10" stroke="#EF4444" stroke-width="2"/><path d="M15 9l-6 6M9 9l6 6" stroke="#EF4444" stroke-width="2" stroke-linecap="round"/></svg>
            </div>
            <p class="failed-title">AI 搜索失败</p>
            <p class="failed-detail">{{ viewingTask.error }}</p>
            <div class="failed-actions">
              <button type="button" class="btn-retry" @click="handleRetrySearchTask">
                <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M23 4v6h-6M1 20v-6h6M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                重新搜索
              </button>
              <button type="button" class="btn-sm" @click="handleCancelTask">取消任务</button>
            </div>
          </div>

          <!-- 查看任务时：已满意状态 -->
          <div v-if="viewingTask && viewingTask.status === 'satisfied'" class="search-result-card">
            <div class="search-result-header">
              <div class="sr-header-left">
                <svg viewBox="0 0 24 24" fill="none" width="18" height="18"><circle cx="12" cy="12" r="10" stroke="#10B981" stroke-width="2"/><path d="M8 12l2 2 5-5" stroke="#10B981" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                <span>已选定此课件 — 点击下方按钮完成创建</span>
              </div>
              <button type="button" class="btn-sm" @click="copyResult(viewingTask.result)">复制全文</button>
            </div>
            <div class="search-result-body">
              <MarkdownRenderer :content="viewingTask.result" />
            </div>
            <div class="search-result-actions">
              <div class="satisfied-badge">
                <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M9 11l3 3L22 4M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" stroke="#10B981" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                课件已确认
              </div>
              <button type="button" class="btn-retry" @click="handleRetrySearchTask">重新选择</button>
            </div>
          </div>

          <!-- 底部操作按钮 -->
          <div class="modal-bottom-actions">
            <!-- 查看任务且已完成 → 提示先点满意 -->
            <template v-if="viewingTask && viewingTask.status === 'completed'">
              <button class="btn-primary" style="width:100%;margin-top:20px"
                @click="handleCreateFromTask"
                :disabled="creating">
                {{ creating ? '创建中...' : '请先点击上方"满意，使用此课件"后再创建' }}
              </button>
            </template>

            <!-- 查看任务且已满意 → 创建课程 -->
            <template v-if="viewingTask && viewingTask.status === 'satisfied'">
              <button class="btn-primary" style="width:100%;margin-top:20px"
                @click="handleCreateFromTask"
                :disabled="creating">
                {{ creating ? '创建中...' : '创建课程' }}
              </button>
            </template>

            <!-- 查看任务且失败 → 重试 -->
            <template v-if="viewingTask && viewingTask.status === 'failed'">
              <button class="btn-primary" style="width:100%;margin-top:20px;background:linear-gradient(135deg, #F59E0B, #D97706)"
                @click="handleRetrySearchTask">
                重新搜索
              </button>
            </template>

            <!-- 普通模式 → 创建课程按钮 -->
            <button v-if="!viewingTask"
              class="btn-primary" style="width:100%;margin-top:20px"
              @click="handleCreateCourse"
              :disabled="creating || !canCreateCourse">
              {{ creating ? '创建中...' : canCreateCourse ? '创建课程' : (coursewareSource === 'ai-search' ? '请先点击上方"AI 搜索相关知识"按钮' : '请先上传课件文件') }}
            </button>
          </div>
        </div>
      </a-modal>
    </div>

    <!-- Panel: AI Teaching Tools -->
    <div v-if="activeTab === 'ai-tools'" class="panel animate-fade-in">
      <div class="panel-header">
        <h2>AI 教学工具</h2>
        <a-select v-model:value="selectedCourseName" placeholder="选择课程" class="course-select" style="width:240px" v-if="courses.length > 0">
          <a-select-option v-for="c in courses" :key="c.course_name" :value="c.course_name">{{ c.course_name }}</a-select-option>
        </a-select>
      </div>

      <div v-if="!selectedCourseName" class="panel-body">
        <div v-if="showCoursePicker !== 'ai-tools'" class="course-select-prompt" @click="showCoursePicker = 'ai-tools'">
          <div class="prompt-icon">
            <svg viewBox="0 0 24 24" fill="none" width="56" height="56">
              <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M4 19.5A2.5 2.5 0 0 0 6.5 22H20V2H6.5A2.5 2.5 0 0 0 4 4.5v15z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3 class="prompt-title">选择一门课程开始</h3>
          <p class="prompt-desc">点击此处选择课程，即可使用 AI 教学工具</p>
          <div class="prompt-arrow">
            <svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M6 9l6 6 6-6" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
          </div>
        </div>
        <div v-else class="course-picker-popup">
          <div class="picker-header">
            <h3>选择课程</h3>
            <button class="picker-close" @click="showCoursePicker = ''">✕</button>
          </div>
          <div class="picker-list">
            <div v-for="c in courses" :key="c.course_name" class="picker-item" @click="selectedCourseName = c.course_name; showCoursePicker = ''">
              <span class="picker-item-icon"><svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M4 19.5A2.5 2.5 0 0 0 6.5 22H20V2H6.5A2.5 2.5 0 0 0 4 4.5v15z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg></span>
              <span class="picker-item-name">{{ c.course_name }}</span>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="ai-tools-content">
        <div class="ai-warning-banner">
          <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M12 22c5.523 0 10-4.477 10-10S17.523 2 12 2 2 6.477 2 12s4.477 10 10 10zM12 8v4M12 16h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
          <span>AI 生成内容和导入可能有误，请仔细检查。</span>
        </div>
        <div class="sub-tabs">
          <button v-for="st in aiSubTabs" :key="st.key" class="sub-tab-btn" :class="{ active: aiActiveTab === st.key }"
            @click="aiActiveTab = st.key">
            {{ st.label }}
          </button>
        </div>

        <!-- Knowledge Organization -->
        <div v-if="aiActiveTab === 'knowledge'" class="ai-panel">
          <div class="ai-input-area">
            <label>教学需求 (可选)</label>
            <a-textarea v-model:value="aiInputs.knowledge" placeholder="描述你的教学需求，例如：重点讲解二叉树部分、需要包含课堂互动环节..." :rows="3" />
            <button class="btn-primary" @click="runAI('knowledge')" :disabled="aiLoading.knowledge">
              <svg v-if="!aiLoading.knowledge" viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              {{ aiLoading.knowledge ? '生成中...' : (aiResults.knowledge ? '重新生成教学方案' : '生成教学方案') }}
            </button>
          </div>
          <div v-if="aiLoading.knowledge" class="ai-progress-bar">
            <div class="ai-progress-fill"></div>
          </div>
          <div v-if="aiResults.knowledge" class="ai-result-card">
            <div class="ai-result-header">
              <h3>📋 知识梳理结果</h3>
              <div class="ai-result-actions">
                <button class="btn-sm" @click="copyResult(aiResults.knowledge)">复制</button>
                <a-dropdown>
                  <button class="btn-sm">导出 ▾</button>
                  <template #overlay>
                    <a-menu>
                      <a-menu-item v-for="opt in EXPORT_OPTIONS" :key="opt.key" @click="exportContent(aiResults.knowledge, opt.key, '知识梳理_' + selectedCourseName)">{{ opt.icon }} {{ opt.label }}</a-menu-item>
                    </a-menu>
                  </template>
                </a-dropdown>
                <button class="btn-sm btn-courseware" @click="saveToCourseware('knowledge')" title="保存为课件">
                  <svg viewBox="0 0 24 24" fill="none" width="14" height="14"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8zM14 2v6h6M12 18v-6M9 15l3-3 3 3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                  添加到课件
                </button>
              </div>
            </div>
            <MarkdownRenderer :content="aiResults.knowledge" />
          </div>
        </div>

        <!-- Practice Design -->
        <div v-if="aiActiveTab === 'practice'" class="ai-panel">
          <div class="ai-input-area">
            <label>实训需求 (可选)</label>
            <a-textarea v-model:value="aiInputs.practice" placeholder="描述实训需求，例如：设计一个完整的Web开发实训项目..." :rows="3" />
            <button class="btn-primary" @click="runAI('practice')" :disabled="aiLoading.practice">
              {{ aiLoading.practice ? '生成中...' : (aiResults.practice ? '重新生成实训方案' : '生成实训方案') }}
            </button>
          </div>
          <div v-if="aiLoading.practice" class="ai-progress-bar">
            <div class="ai-progress-fill"></div>
          </div>
          <div v-if="aiResults.practice" class="ai-result-card">
            <div class="ai-result-header">
              <h3>🔬 实训设计方案</h3>
              <div class="ai-result-actions">
                <button class="btn-sm" @click="copyResult(aiResults.practice)">复制</button>
                <a-dropdown>
                  <button class="btn-sm">导出 ▾</button>
                  <template #overlay>
                    <a-menu>
                      <a-menu-item v-for="opt in EXPORT_OPTIONS" :key="opt.key" @click="exportContent(aiResults.practice, opt.key, '实训设计_' + selectedCourseName)">{{ opt.icon }} {{ opt.label }}</a-menu-item>
                    </a-menu>
                  </template>
                </a-dropdown>
                <button class="btn-sm btn-import-training" @click="importFromPracticeAI" title="导入到实训计划">
                  <svg viewBox="0 0 24 24" fill="none" width="14" height="14"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8zM14 2v6h6M12 18v-6M9 15l3-3 3 3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                  导入到实训计划
                </button>
              </div>
            </div>
            <MarkdownRenderer :content="aiResults.practice" />
          </div>
        </div>

        <!-- Question Generation -->
        <div v-if="aiActiveTab === 'questions'" class="ai-panel">
          <div class="ai-input-area">
            <div class="form-row">
              <div class="form-field">
                <label>练习名称</label>
                <a-input v-model:value="aiInputs.issueName" placeholder="例如：数据结构期中测试" />
              </div>
            </div>
            <div class="form-row">
              <div class="form-field">
                <label>开始时间</label>
                <a-date-picker v-model:value="aiInputs.startTime" placeholder="选择开始日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width:100%" />
              </div>
              <div class="form-field">
                <label>结束时间</label>
                <a-date-picker v-model:value="aiInputs.endTime" placeholder="选择结束日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width:100%" />
              </div>
            </div>
            <div class="form-field">
              <label>
                出题数量分配
                <span class="qc-total-badge" :class="{ 'qc-exceeded': questionCountExceeded }">
                  总计 {{ totalQuestionCount }} / 100
                  <span v-if="questionCountExceeded" class="qc-warn">⚠️ 已超出</span>
                </span>
              </label>
              <div class="question-count-grid">
                <div class="qc-item">
                  <span class="qc-label">🔤 选择题</span>
                  <a-input-number v-model:value="aiInputs.choiceCount" :min="0" :max="100" style="width:100%" />
                </div>
                <div class="qc-item">
                  <span class="qc-label">✏️ 填空题</span>
                  <a-input-number v-model:value="aiInputs.fillBlankCount" :min="0" :max="100" style="width:100%" />
                </div>
                <div class="qc-item">
                  <span class="qc-label">✅ 判断题</span>
                  <a-input-number v-model:value="aiInputs.trueFalseCount" :min="0" :max="100" style="width:100%" />
                </div>
                <div class="qc-item">
                  <span class="qc-label">📝 应用题</span>
                  <a-input-number v-model:value="aiInputs.essayCount" :min="0" :max="20" style="width:100%" />
                  <span v-if="essayCountExceeded" class="qc-field-warn">最多 20 题</span>
                </div>
                <div class="qc-item">
                  <span class="qc-label">🔢 计算题</span>
                  <a-input-number v-model:value="aiInputs.calculationCount" :min="0" :max="20" style="width:100%" />
                  <span v-if="calculationCountExceeded" class="qc-field-warn">最多 20 题</span>
                </div>
              </div>
            </div>
            <label>出题偏重 (可选)</label>
            <a-textarea v-model:value="aiInputs.questions" placeholder="例如：重点考察二叉树和图论、难度中等偏上、适合大二学生..." :rows="3" />
            <button class="btn-primary" @click="runAI('questions')" :disabled="aiLoading.questions">
              {{ aiLoading.questions ? '生成中...' : (aiResults.questions ? '重新生成考核题目' : '生成考核题目') }}
            </button>
          </div>
          <div v-if="aiLoading.questions" class="ai-progress-bar">
            <div class="ai-progress-fill"></div>
          </div>
          <div v-if="aiResults.questions" class="ai-result-card">
            <div class="ai-result-header">
              <h3>📝 智能出题结果</h3>
              <div class="ai-result-actions">
                <button class="btn-sm" @click="copyResult(aiResults.questions)">复制</button>
                <a-dropdown>
                  <button class="btn-sm">导出 ▾</button>
                  <template #overlay>
                    <a-menu>
                      <a-menu-item v-for="opt in EXPORT_OPTIONS" :key="opt.key" @click="exportContent(aiResults.questions, opt.key, '智能出题_' + selectedCourseName)">{{ opt.icon }} {{ opt.label }}</a-menu-item>
                    </a-menu>
                  </template>
                </a-dropdown>
                <button class="btn-sm btn-import" @click="importQuestionsTo('homework')" title="将题目导入发布作业">
                  <svg viewBox="0 0 24 24" fill="none" width="14" height="14"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8zM14 2v6h6M16 13H8M16 17H8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                  导入作业
                </button>
                <button class="btn-sm btn-import-exam" @click="importQuestionsTo('exam')" title="将题目导入发布考试">
                  <svg viewBox="0 0 24 24" fill="none" width="14" height="14"><path d="M9 11l3 3L22 4M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                  导入考试
                </button>
                <button class="btn-sm btn-import-bank" @click="importQuestionsToBank" title="将题目导入题库积累">
                  <svg viewBox="0 0 24 24" fill="none" width="14" height="14"><path d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                  导入题库
                </button>
              </div>
            </div>
            <MarkdownRenderer :content="aiResults.questions" />
          </div>
        </div>
      </div>
    </div>

    <!-- ===== Panel: 课件管理 ===== -->
    <div v-if="activeTab === 'courseware'" class="panel animate-fade-in">
      <div class="panel-header">
        <h2>课件管理</h2>
        <div style="display:flex;gap:10px;align-items:center">
          <a-select v-model:value="coursewareCourse" placeholder="选择课程" class="course-select" style="width:240px" v-if="courses.length > 0">
            <a-select-option v-for="c in courses" :key="c.course_name" :value="c.course_name">{{ c.course_name }}</a-select-option>
          </a-select>
          <button class="btn-primary" @click="uploadCoursewareForSelected" :disabled="!coursewareCourse">
            <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4M17 8l-5-5-5 5M12 3v12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
            上传课件
          </button>
        </div>
      </div>
      <div v-if="!coursewareCourse" class="panel-body">
        <div v-if="showCoursePicker !== 'courseware'" class="course-select-prompt" @click="showCoursePicker = 'courseware'">
          <div class="prompt-icon">
            <svg viewBox="0 0 24 24" fill="none" width="56" height="56">
              <path d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z" stroke="currentColor" stroke-width="1.5"/>
            </svg>
          </div>
          <h3 class="prompt-title">选择一门课程查看课件</h3>
          <p class="prompt-desc">点击此处选择课程，管理和查看课程课件资源</p>
          <div class="prompt-arrow">
            <svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M6 9l6 6 6-6" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
          </div>
        </div>
        <div v-else class="course-picker-popup">
          <div class="picker-header">
            <h3>选择课程</h3>
            <button class="picker-close" @click="showCoursePicker = ''">✕</button>
          </div>
          <div class="picker-list">
            <div v-for="c in courses" :key="c.course_name" class="picker-item" @click="coursewareCourse = c.course_name; showCoursePicker = ''">
              <span class="picker-item-icon"><svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M4 19.5A2.5 2.5 0 0 0 6.5 22H20V2H6.5A2.5 2.5 0 0 0 4 4.5v15z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg></span>
              <span class="picker-item-name">{{ c.course_name }}</span>
            </div>
          </div>
        </div>
      </div>
      <div v-else>
        <!-- 批量操作栏 -->
        <div v-if="filteredCourseware.length > 0" class="batch-bar">
          <label class="batch-check">
            <input type="checkbox" :checked="cwAllSelected" @change="toggleCwSelectAll" />
            <span>全选</span>
          </label>
          <span class="batch-count" v-if="cwSelectedIds.size > 0">已选 {{ cwSelectedIds.size }} 项</span>
          <a-popconfirm v-if="cwSelectedIds.size > 0" title="确定批量删除所选课件？" ok-text="确定删除" cancel-text="取消" @confirm="deleteSelectedCw">
            <button class="btn-sm btn-sm-danger">🗑 批量删除</button>
          </a-popconfirm>
        </div>
        <!-- 课件列表 -->
        <div v-if="filteredCourseware.length > 0" class="courseware-grid">
          <div v-for="cw in filteredCourseware" :key="cw.id" class="courseware-card">
            <div class="cw-icon">
              <input type="checkbox" class="card-checkbox" style="position:absolute;top:8px;left:8px;z-index:2" :checked="cwSelectedIds.has(cw.id)" @change="toggleCwSelect(cw.id)" @click.stop />
              <svg viewBox="0 0 24 24" fill="none" width="32" height="32"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8zM14 2v6h6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
            </div>
            <div class="cw-info">
              <h4 class="cw-title">{{ cw.title }}</h4>
              <p class="cw-desc">{{ cw.description || '无描述' }}</p>
              <div class="cw-meta">
                <span>{{ cw.fileName }}</span>
                <span>{{ formatFileSize(cw.fileSize) }}</span>
                <span>{{ formatTime(cw.createdAt) }}</span>
              </div>
            </div>
            <div class="cw-actions">
              <button class="btn-sm" @click="viewCourseware(cw)">查看</button>
              <button class="btn-sm" @click="openExportModal(cw)">导出</button>
              <a-popconfirm title="确定删除?" ok-text="确定" cancel-text="取消" @confirm="deleteCourseware(cw.id)">
                <button class="btn-sm btn-sm-danger">删除</button>
              </a-popconfirm>
            </div>
          </div>
        </div>
        <EmptyState v-else title="暂无课件" :description="'该课程下还没有课件，点击上方「上传课件」按钮或通过 AI 教学工具添加'" />
      </div>

      <!-- 课件命名弹窗 -->
      <a-modal v-model:open="showCwNameModal" title="保存为课件" :footer="null" width="420px">
        <div class="cw-name-form">
          <div class="form-field">
            <label>课件名称</label>
            <a-input v-model:value="cwNameInput" placeholder="输入课件名称，留空则使用默认名" @pressEnter="confirmSaveToCourseware" />
            <span class="field-hint">不填写则自动使用默认命名格式</span>
          </div>
          <button class="btn-primary" style="width:100%;margin-top:12px" @click="confirmSaveToCourseware">确认保存</button>
        </div>
      </a-modal>

      <!-- 课件查看弹窗 -->
      <a-modal v-model:open="showCwViewModal" :title="viewingCoursewareItem?.title || '课件内容'" :footer="null" width="800px">
        <div v-if="viewingCoursewareItem" class="cw-view-content">
          <MarkdownRenderer :content="decodeCoursewareContent(viewingCoursewareItem)" />
        </div>
      </a-modal>

      <!-- 课件导出格式选择弹窗 -->
      <a-modal v-model:open="showCwExportModal" title="导出课件" :footer="null" width="420px">
        <div class="cw-export-form">
          <div class="form-field">
            <label>选择导出格式</label>
            <div class="export-format-list">
              <div v-for="fmt in cwExportFormats" :key="fmt.key" class="export-format-item" :class="{ active: cwExportFormat === fmt.key }" @click="cwExportFormat = fmt.key">
                <span class="fmt-radio">{{ cwExportFormat === fmt.key ? '●' : '○' }}</span>
                <span class="fmt-icon">{{ fmt.icon }}</span>
                <span class="fmt-label">{{ fmt.label }}</span>
              </div>
            </div>
          </div>
          <button class="btn-primary" style="width:100%;margin-top:12px" @click="confirmExportCourseware">
            <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4M17 8l-5-5-5 5M12 3v12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
            导出文件
          </button>
        </div>
      </a-modal>
    </div>

    <!-- ===== Panel: 发布作业 ===== -->
    <div v-if="activeTab === 'homework'" class="panel animate-fade-in">
      <div class="panel-header">
        <h2>发布作业</h2>
        <button class="btn-primary" @click="openHomeworkForm()">
          <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/></svg>
          发布作业
        </button>
      </div>

      <!-- 作业列表 -->
      <!-- 批量操作栏 -->
      <div v-if="homeworkList.length > 0" class="batch-bar">
        <label class="batch-check">
          <input type="checkbox" :checked="hwAllSelected" @change="toggleHwSelectAll" />
          <span>全选</span>
        </label>
        <span class="batch-count" v-if="hwSelectedIds.size > 0">已选 {{ hwSelectedIds.size }} 项</span>
        <a-popconfirm v-if="hwSelectedIds.size > 0" title="确定批量删除所选作业？" ok-text="确定删除" cancel-text="取消" @confirm="deleteSelectedHw">
          <button class="btn-sm btn-sm-danger">🗑 批量删除</button>
        </a-popconfirm>
      </div>
      <div v-if="homeworkList.length > 0" class="homework-grid">
        <div v-for="hw in homeworkList" :key="hw.id" class="homework-card">
          <div class="hw-header">
            <input type="checkbox" class="card-checkbox" :checked="hwSelectedIds.has(hw.id)" @change="toggleHwSelect(hw.id)" @click.stop />
            <h3>{{ hw.title }}</h3>
            <span class="hw-course">{{ hw.courseName }}</span>
            <span class="hw-status" :class="hw.status === 'published' ? 'status-pub' : 'status-draft'">
              {{ hw.status === 'published' ? '已发布' : '未发布' }}
            </span>
          </div>
          <p class="hw-desc">{{ hw.description || '暂无描述' }}</p>
          <div class="hw-meta">
            <div class="hw-meta-row">
              <span>截止：{{ hw.deadline || '未设截止' }}</span>
              <span>题目：{{ hw.questionCount || 0 }} 题</span>
            </div>
            <div class="hw-meta-row hw-meta-sub">
              <span>创建：{{ formatTime(hw.createdAt) }}</span>
              <span>更新：{{ formatTime(hw.updatedAt) }}</span>
            </div>
          </div>
          <div class="hw-actions">
            <button class="btn-sm" @click="openHomeworkForm(hw)">编辑</button>
            <button class="btn-sm btn-grade" @click="openGradingPage(hw, 'homework')">批改</button>
            <a-popconfirm title="确定删除此作业?" ok-text="确定" cancel-text="取消" @confirm="deleteHomework(hw.id)">
              <button class="btn-sm btn-sm-danger">删除</button>
            </a-popconfirm>
          </div>
        </div>
      </div>
      <EmptyState v-else title="暂无作业" :description="'点击【发布作业】布置新的课后作业'" />

    </div>

    <!-- ===== Panel: 发布考试 ===== -->
    <div v-if="activeTab === 'exam'" class="panel animate-fade-in">
      <div class="panel-header">
        <h2>发布考试</h2>
        <button class="btn-primary" @click="openExamForm()">
          <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/></svg>
          发布考试
        </button>
      </div>

      <!-- 批量操作栏 -->
      <div v-if="examList.length > 0" class="batch-bar">
        <label class="batch-check">
          <input type="checkbox" :checked="exAllSelected" @change="toggleExSelectAll" />
          <span>全选</span>
        </label>
        <span class="batch-count" v-if="exSelectedIds.size > 0">已选 {{ exSelectedIds.size }} 项</span>
        <a-popconfirm v-if="exSelectedIds.size > 0" title="确定批量删除所选考试？" ok-text="确定删除" cancel-text="取消" @confirm="deleteSelectedEx">
          <button class="btn-sm btn-sm-danger">🗑 批量删除</button>
        </a-popconfirm>
      </div>
      <div v-if="examList.length > 0" class="homework-grid">
        <div v-for="ex in examList" :key="ex.id" class="homework-card exam-card">
          <div class="hw-header">
            <input type="checkbox" class="card-checkbox" :checked="exSelectedIds.has(ex.id)" @change="toggleExSelect(ex.id)" @click.stop />
            <h3>{{ ex.title }}</h3>
            <span class="hw-course exam-course">{{ ex.courseName }}</span>
            <span class="hw-status" :class="ex.status === 'published' ? 'status-pub' : 'status-draft'">
              {{ ex.status === 'published' ? '已发布' : '未发布' }}
            </span>
          </div>
          <p class="hw-desc">{{ ex.description || '暂无描述' }}</p>
          <div class="hw-meta">
            <div class="hw-meta-row">
              <span>开始：{{ ex.startTime || '未设开始' }}</span>
              <span>结束：{{ ex.endTime || '未设结束' }}</span>
              <span>时长：{{ ex.duration }} 分钟</span>
            </div>
            <div class="hw-meta-row hw-meta-sub">
              <span>创建：{{ formatTime(ex.createdAt) }}</span>
              <span>更新：{{ formatTime(ex.updatedAt) }}</span>
            </div>
          </div>
          <div class="hw-actions">
            <button class="btn-sm" @click="openExamForm(ex)">编辑</button>
            <button class="btn-sm btn-grade" @click="openGradingPage(ex, 'exam')">批改</button>
            <a-popconfirm title="确定删除此考试?" ok-text="确定" cancel-text="取消" @confirm="deleteExam(ex.id)">
              <button class="btn-sm btn-sm-danger">删除</button>
            </a-popconfirm>
          </div>
        </div>
      </div>
      <EmptyState v-else title="暂无考试" :description="'点击【发布考试】创建新的在线考试'" />

    </div>

    <!-- ===== Panel: 实训计划 ===== -->
    <div v-if="activeTab === 'practical-training'" class="panel animate-fade-in">
      <!-- ===== 列表视图 ===== -->
      <template v-if="activeTpView === 'list'">
        <div class="panel-header">
          <h2>实训计划</h2>
          <button class="btn-primary" @click="openTrainingPlanForm()">
            <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/></svg>
            新建实训计划
          </button>
        </div>

        <!-- 批量操作栏 -->
        <div v-if="trainingPlanList.length > 0" class="batch-bar">
          <label class="batch-check">
            <input type="checkbox" :checked="tpAllSelected" @change="toggleTpSelectAll" />
            <span>全选</span>
          </label>
          <span class="batch-count" v-if="tpSelectedIds.size > 0">已选 {{ tpSelectedIds.size }} 项</span>
          <a-popconfirm v-if="tpSelectedIds.size > 0" title="确定批量删除所选实训计划？" ok-text="确定删除" cancel-text="取消" @confirm="deleteSelectedTp">
            <button class="btn-sm btn-sm-danger">🗑 批量删除</button>
          </a-popconfirm>
        </div>
        <div v-if="trainingPlanList.length > 0" class="homework-grid">
          <div v-for="tp in trainingPlanList" :key="tp.id" class="homework-card">
            <div class="hw-header">
              <input type="checkbox" class="card-checkbox" :checked="tpSelectedIds.has(tp.id)" @change="toggleTpSelect(tp.id)" @click.stop />
              <h3>{{ tp.title }}</h3>
              <span class="hw-course">{{ tp.courseName }}</span>
              <span class="tp-template-badge">{{ getTemplateLabel(tp.templateKey) }}</span>
            </div>
            <p class="hw-desc">{{ tp.description || '暂无描述' }}</p>
            <div class="hw-meta">
              <span>创建: {{ formatTime(tp.createdAt) }}</span>
              <span>更新: {{ formatTime(tp.updatedAt) }}</span>
              <span>{{ tp.sections.length }} 个板块</span>
            </div>
            <div class="hw-actions">
              <button class="btn-sm" @click="viewTrainingPlan(tp)">查看</button>
              <button class="btn-sm" @click="openTrainingPlanForm(tp)">编辑</button>
              <button class="btn-sm" @click="openTpExportModal(tp)">导出</button>
              <a-popconfirm title="确定删除此实训计划?" ok-text="确定" cancel-text="取消" @confirm="deleteTrainingPlan(tp.id)">
                <button class="btn-sm btn-sm-danger">删除</button>
              </a-popconfirm>
            </div>
          </div>
        </div>
        <EmptyState v-else title="暂无实训计划" :description="'点击「新建实训计划」使用模板创建，或从「AI 教学工具 → 实训设计」生成后导入'" />
      </template>

      <!-- ===== 表单视图 ===== -->
      <template v-else-if="activeTpView === 'form'">
        <div class="panel-header">
          <button class="btn-back" @click="resetTpView">← 返回列表</button>
          <h2>{{ editingTrainingPlan ? '编辑实训计划' : '新建实训计划' }}</h2>
          <button class="btn-clear" @click="clearTpForm" title="清空表单内容">清空</button>
        </div>
        <div class="tp-form">
          <div class="form-row">
            <div class="form-field">
              <label>课程 <span style="color:red">*</span></label>
              <a-select v-model:value="tpForm.courseName" placeholder="选择课程" style="width:100%" :disabled="!!editingTrainingPlan">
                <a-select-option v-for="c in courses" :key="c.course_name" :value="c.course_name">{{ c.course_name }}</a-select-option>
              </a-select>
            </div>
            <div class="form-field">
              <label>模板 <span style="color:red">*</span></label>
              <a-select v-model:value="tpForm.templateKey" placeholder="选择模板" style="width:100%" @change="onTemplateChange" :disabled="!!editingTrainingPlan">
                <a-select-option v-for="tpl in TRAINING_PLAN_TEMPLATES" :key="tpl.key" :value="tpl.key">{{ tpl.label }}</a-select-option>
              </a-select>
            </div>
          </div>
          <div class="form-field">
            <label>计划标题 <span style="color:red">*</span></label>
            <a-input v-model:value="tpForm.title" placeholder="输入实训计划标题" :maxlength="100" />
          </div>
          <div class="form-field">
            <label>简要描述</label>
            <a-textarea v-model:value="tpForm.description" placeholder="简要描述实训计划..." :rows="2" :maxlength="200" />
          </div>

          <!-- 各板块编辑器 -->
          <div class="section-label" style="margin:16px 0 10px">{{ getTemplateLabel(tpForm.templateKey) }} — 板块内容</div>
          <div v-for="(sec, si) in tpForm.sections" :key="sec.name" class="tp-section-editor">
            <div class="tp-section-header">
              <span class="tp-section-num">{{ si + 1 }}</span>
              <span class="tp-section-name">{{ sec.name }}</span>
            </div>
            <a-textarea v-model:value="tpForm.sections[si].content" :placeholder="getSectionPlaceholder(sec.name)" :rows="4" />
          </div>

          <button class="btn-primary" style="width:100%;margin-top:20px" @click="confirmTrainingPlan" :disabled="!tpForm.courseName || !tpForm.title || !tpForm.templateKey">
            {{ editingTrainingPlan ? '保存修改' : '创建实训计划' }}
          </button>
        </div>
      </template>

      <!-- ===== 详情视图 ===== -->
      <template v-else-if="activeTpView === 'detail'">
        <div class="panel-header">
          <button class="btn-back" @click="resetTpView">← 返回列表</button>
          <h2>{{ viewingTrainingPlan?.title || '实训计划详情' }}</h2>
        </div>
        <div v-if="viewingTrainingPlan" class="tp-detail-card">
          <div class="tp-detail-meta">
            <span class="hw-course" style="margin-right:12px">{{ viewingTrainingPlan.courseName }}</span>
            <span class="tp-template-badge">{{ getTemplateLabel(viewingTrainingPlan.templateKey) }}</span>
            <span style="margin-left:auto;color:#94A3B8;font-size:12px">更新于 {{ formatTime(viewingTrainingPlan.updatedAt) }}</span>
          </div>
          <p v-if="viewingTrainingPlan.description" class="tp-detail-desc">{{ viewingTrainingPlan.description }}</p>
          <div class="cw-view-content" style="margin-top:16px">
            <MarkdownRenderer :content="formatTrainingPlanToMarkdown(viewingTrainingPlan)" />
          </div>
          <div class="tp-detail-actions">
            <button class="btn-sm" @click="openTrainingPlanForm(viewingTrainingPlan)">编辑</button>
            <button class="btn-sm" @click="openTpExportModal(viewingTrainingPlan!)">导出</button>
          </div>
        </div>
      </template>

      <!-- 导出实训计划格式选择 Modal -->
      <a-modal v-model:open="showTpExportModal" title="导出实训计划" :footer="null" width="420px">
        <div class="cw-export-form">
          <div class="form-field">
            <label>选择导出格式</label>
            <div class="export-format-list">
              <div v-for="fmt in cwExportFormats" :key="fmt.key" class="export-format-item" :class="{ active: tpExportFormat === fmt.key }" @click="tpExportFormat = fmt.key">
                <span class="fmt-radio">{{ tpExportFormat === fmt.key ? '●' : '○' }}</span>
                <span class="fmt-icon">{{ fmt.icon }}</span>
                <span class="fmt-label">{{ fmt.label }}</span>
              </div>
            </div>
          </div>
          <button class="btn-primary" style="width:100%;margin-top:12px" @click="confirmTpExport">
            <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4M17 8l-5-5-5 5M12 3v12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
            导出文件
          </button>
        </div>
      </a-modal>
    </div>

    <!-- ===== Panel: 考勤记录 ===== -->
    <div v-if="activeTab === 'attendance'" class="panel animate-fade-in">
      <!-- ===== 列表视图 ===== -->
      <template v-if="activeAttendanceView === 'list'">
        <div class="panel-header">
          <h2>考勤记录</h2>
          <div style="display:flex;gap:8px;align-items:center">
            <a-input v-model:value="attKeyword" placeholder="输入关键字搜索..." style="width:200px" @pressEnter="searchAttendance" />
            <a-button type="primary" @click="searchAttendance">搜索</a-button>
            <a-button @click="resetAttSearch">重置</a-button>
            <button class="btn-primary" @click="openAttendanceForm()">
              <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/></svg>
              记录考勤
            </button>
          </div>
        </div>

        <!-- 批量操作栏 -->
        <div v-if="filteredAttendanceList.length > 0" class="batch-bar">
          <label class="batch-check">
            <input type="checkbox" :checked="attAllSelected" @change="toggleAttSelectAll" />
            <span>全选</span>
          </label>
          <span class="batch-count" v-if="attSelectedIds.size > 0">已选 {{ attSelectedIds.size }} 项</span>
          <a-popconfirm v-if="attSelectedIds.size > 0" title="确定批量删除所选考勤记录？" ok-text="确定删除" cancel-text="取消" @confirm="deleteSelectedAtt">
            <button class="btn-sm btn-sm-danger">🗑 批量删除</button>
          </a-popconfirm>
        </div>
        <div v-if="filteredAttendanceList.length > 0" class="attendance-table-wrap">
          <a-table :columns="attendanceColumns" :dataSource="filteredAttendanceList" rowKey="id" :pagination="{ pageSize: 10 }">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'presentCount'">
                <span class="attendance-present">{{ record.presentCount }}</span>
                <span class="attendance-sep">/</span>
                <span class="attendance-total">{{ record.totalCount }}</span>
              </template>
              <template v-if="column.key === 'actions'">
                <button class="btn-sm" @click="openAttendanceDetail(record)">详情</button>
                <a-dropdown>
                  <button class="btn-sm" style="margin-left:6px">导出 ▾</button>
                  <template #overlay>
                    <a-menu>
                      <a-menu-item v-for="opt in EXPORT_OPTIONS" :key="opt.key" @click="exportAttendance(record, opt.key)">{{ opt.icon }} {{ opt.label }}</a-menu-item>
                    </a-menu>
                  </template>
                </a-dropdown>
                <a-popconfirm title="确定删除?" ok-text="确定" cancel-text="取消" @confirm="deleteAttendance(record.id)">
                  <button class="btn-sm btn-sm-danger" style="margin-left:6px">删除</button>
                </a-popconfirm>
              </template>
            </template>
          </a-table>
        </div>
        <EmptyState v-else title="暂无考勤记录" :description="'点击【记录考勤】开始记录学生出勤情况'" />
      </template>

      <!-- ===== 表单视图 ===== -->
      <template v-else-if="activeAttendanceView === 'form'">
        <div class="panel-header">
          <button class="btn-back" @click="resetAttendanceView">← 返回列表</button>
          <h2>记录考勤</h2>
          <button class="btn-clear" @click="clearAttendanceForm" title="清空表单内容">清空</button>
        </div>
        <div class="homework-form">
          <div class="form-row">
            <div class="form-field">
              <label>课程 <span style="color:red">*</span></label>
              <a-select v-model:value="attendanceForm.courseName" placeholder="选择课程" style="width:100%" @change="onAttendanceCourseChange">
                <a-select-option v-for="c in courses" :key="c.course_name" :value="c.course_name">{{ c.course_name }}</a-select-option>
              </a-select>
            </div>
            <div class="form-field">
              <label>日期 <span style="color:red">*</span></label>
              <a-date-picker v-model:value="attendanceForm.date" placeholder="选择日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width:100%" />
            </div>
            <div class="form-field">
              <label>节次</label>
              <a-select v-model:value="attendanceForm.period" placeholder="选择节次" style="width:100%" allowClear>
                <a-select-option :value="1">第 1-2 节</a-select-option>
                <a-select-option :value="3">第 3-4 节</a-select-option>
                <a-select-option :value="5">第 5-6 节</a-select-option>
                <a-select-option :value="7">第 7-8 节</a-select-option>
                <a-select-option :value="9">第 9-10 节</a-select-option>
              </a-select>
            </div>
          </div>
          <div class="form-field">
            <label>备注</label>
            <a-input v-model:value="attendanceForm.remark" placeholder="可填写备注信息（可选）" style="width:100%" />
          </div>

          <div class="form-field" v-if="attendanceStudents.length > 0">
            <div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:8px">
              <label style="margin:0">学生考勤 <span style="color:red">*</span></label>
              <div class="as-batch-actions">
                <button class="btn-sm" @click="batchSetStatus('present')">全部出勤</button>
                <button class="btn-sm" @click="batchSetStatus('absent')">全部缺勤</button>
                <button class="btn-sm" @click="batchSetStatus('leave')">全部请假</button>
              </div>
            </div>
            <div class="attendance-student-list">
              <div v-for="s in attendanceStudents" :key="s.id" class="attendance-student-row">
                <span class="as-name">{{ s.name }}</span>
                <span class="as-class" v-if="s.className">{{ s.className }}</span>
                <div class="as-status-group">
                  <button class="as-status-btn present"
                    :class="{ active: s.status === 'present' }"
                    @click="s.status = 'present'">✓ 出勤</button>
                  <button class="as-status-btn absent"
                    :class="{ active: s.status === 'absent' }"
                    @click="s.status = 'absent'">✗ 缺勤</button>
                  <button class="as-status-btn leave"
                    :class="{ active: s.status === 'leave' }"
                    @click="s.status = 'leave'">— 请假</button>
                </div>
              </div>
            </div>
          </div>
          <div v-else-if="attendanceForm.courseName" class="form-field">
            <p class="field-hint">该课程暂无学生数据</p>
          </div>

          <div class="attendance-summary" v-if="attendanceStudents.length > 0">
            <span class="as-sum-item present">✓ 出勤 {{ attendanceStudents.filter(s => s.status === 'present').length }}</span>
            <span class="as-sum-item absent">✗ 缺勤 {{ attendanceStudents.filter(s => s.status === 'absent').length }}</span>
            <span class="as-sum-item leave">— 请假 {{ attendanceStudents.filter(s => s.status === 'leave').length }}</span>
          </div>

          <button class="btn-primary" style="width:100%;margin-top:12px" @click="saveAttendance" :disabled="!attendanceForm.courseName || !attendanceForm.date || attendanceStudents.length === 0">
            保存考勤记录
          </button>
        </div>
      </template>

      <!-- ===== 详情视图 ===== -->
      <template v-else-if="activeAttendanceView === 'detail'">
        <div class="panel-header">
          <button class="btn-back" @click="resetAttendanceView">← 返回列表</button>
          <h2>考勤详情</h2>
        </div>
        <div v-if="viewingAttendance" class="attendance-detail-card">
          <div class="attendance-detail-meta">
            <div class="detail-meta-row">
              <span class="detail-meta-label">课程</span>
              <span class="detail-meta-value">{{ viewingAttendance.courseName }}</span>
            </div>
            <div class="detail-meta-row">
              <span class="detail-meta-label">日期</span>
              <span class="detail-meta-value">{{ viewingAttendance.date }}</span>
            </div>
            <div class="detail-meta-row" v-if="viewingAttendance.period">
              <span class="detail-meta-label">节次</span>
              <span class="detail-meta-value">第 {{ viewingAttendance.period }}-{{ viewingAttendance.period + 1 }} 节</span>
            </div>
            <div class="detail-meta-row" v-if="viewingAttendance.remark">
              <span class="detail-meta-label">备注</span>
              <span class="detail-meta-value">{{ viewingAttendance.remark }}</span>
            </div>
            <div class="detail-meta-stats">
              <span class="as-sum-item present">✓ 出勤 {{ viewingAttendance.presentCount }}</span>
              <span class="as-sum-item absent">✗ 缺勤 {{ viewingAttendance.totalCount - viewingAttendance.presentCount - (viewingAttendance.records.filter(r => r.status === 'leave').length) }}</span>
              <span class="as-sum-item leave">— 请假 {{ viewingAttendance.records.filter(r => r.status === 'leave').length }}</span>
            </div>
          </div>
          <div class="attendance-detail-records">
            <div v-for="(r, i) in viewingAttendance.records" :key="i" class="attendance-record-row">
              <span class="att-name">{{ r.name }}</span>
              <span class="att-status" :class="'att-' + r.status">
                {{ r.status === 'present' ? '✓ 出勤' : r.status === 'leave' ? '— 请假' : '✗ 缺勤' }}
              </span>
            </div>
          </div>
        </div>
      </template>
    </div>

    <!-- Panel: Student Management -->
    <div v-if="activeTab === 'students'" class="panel animate-fade-in">
      <div class="panel-header">
        <h2>学生管理</h2>
        <div class="header-actions">
          <a-select v-model:value="studentFilterCourse" placeholder="全部课程" allowClear style="width:200px" @change="loadStudentList">
            <a-select-option v-for="c in courses" :key="c.course_name" :value="c.course_name">{{ c.course_name }}</a-select-option>
          </a-select>
          <a-input-search v-model:value="studentSearch" placeholder="搜索姓名/用户名" style="width:220px" @search="onStudentSearch" />
          <button class="btn-primary" @click="openAddStudentModal">+ 添加学生</button>
        </div>
        <div v-if="teacherBindingCode && teacherInfo.teacher_position === '班主任'" class="binding-code-banner" :title="teacherBindingCode" style="margin-left:auto">
          🔗 班级绑定码：<code>{{ teacherBindingCode.slice(0, 8) }}...</code>
          <button class="btn-copy-mini" @click="copyBindingCode" title="复制完整绑定码">📋</button>
        </div>
      </div>
      <!-- 批量操作栏 -->
      <div v-if="filteredEnrolledStudents.length > 0" class="batch-bar">
        <label class="batch-check">
          <input type="checkbox" :checked="studentAllSelected" @change="toggleStudentSelectAll" />
          <span>全选</span>
        </label>
        <span class="batch-count" v-if="studentSelectedIds.size > 0">已选 {{ studentSelectedIds.size }} 项</span>
        <a-popconfirm v-if="studentSelectedIds.size > 0" title="确定批量删除所选学生吗？这将解除学生与教师所有课程的关联" ok-text="确定删除" cancel-text="取消" @confirm="deleteSelectedStudents">
          <button class="btn-sm btn-sm-danger">🗑 批量删除</button>
        </a-popconfirm>
      </div>
      <div v-if="filteredEnrolledStudents.length > 0">
        <a-table :columns="enhancedStudentColumns" :dataSource="filteredEnrolledStudents" rowKey="student_id" :pagination="{ pageSize: 10 }">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'checkbox'">
              <input type="checkbox" :checked="studentSelectedIds.has(record.student_account)" @change="toggleStudentSelect(record.student_account)" />
            </template>
            <template v-if="column.key === 'action'">
              <a-button type="link" size="small" @click="openEditStudentModal(record)">编辑</a-button>
              <a-popconfirm title="确定删除该学生吗？这将解除学生与教师所有课程的关联" ok-text="确定" cancel-text="取消" @confirm="deleteStudent(record.student_account)">
                <a-button type="link" size="small" danger>删除</a-button>
              </a-popconfirm>
            </template>
          </template>
        </a-table>
      </div>
      <EmptyState v-else title="暂无学生" description="还没有学生选修您的课程，或尝试切换课程筛选" />
    </div>

    <!-- Modal: 添加/编辑学生 -->
    <a-modal v-model:open="showStudentModal" :title="editingStudent ? '编辑学生' : '添加学生'" :footer="null" width="560px" destroyOnClose>
      <div class="student-form">
        <div class="form-row">
          <label>用户名 <span class="required">*</span></label>
          <a-input v-model:value="studentForm.student_username" placeholder="请输入用户名" :disabled="!!editingStudent" />
        </div>
        <div class="form-row">
          <label>姓名 <span class="required">*</span></label>
          <a-input v-model:value="studentForm.student_name" placeholder="请输入姓名" />
        </div>
        <div class="form-row">
          <label>密码 <span class="required" v-if="!editingStudent">*</span></label>
          <a-input-password v-model:value="studentForm.password" :placeholder="editingStudent ? '留空则不修改密码' : '请输入密码'" />
        </div>
        <div class="form-row form-row-3col">
          <div class="form-col">
            <label>性别</label>
            <a-select v-model:value="studentForm.student_gender" placeholder="选择性别">
              <a-select-option value="男">男</a-select-option>
              <a-select-option value="女">女</a-select-option>
            </a-select>
          </div>
          <div class="form-col">
            <label>年龄</label>
            <a-input v-model:value="studentForm.student_age" placeholder="年龄" />
          </div>
          <div class="form-col">
            <label>专业</label>
            <a-input v-model:value="studentForm.student_major" placeholder="专业" />
          </div>
        </div>
        <div class="form-row">
          <label>班级</label>
          <a-input v-model:value="studentForm.student_class_name" :placeholder="teacherInfo.teacher_class_name ? '默认：' + teacherInfo.teacher_class_name : '请输入班级'" />
        </div>
        <div class="form-row" v-if="!editingStudent">
          <label>关联课程</label>
          <a-select v-model:value="studentForm.course_names" mode="multiple" placeholder="选择课程（可多选）" :options="courses.map((c: any) => ({ value: c.course_name, label: c.course_name }))" />
        </div>
        <div class="form-actions">
          <a-button @click="showStudentModal = false">取消</a-button>
          <a-button type="primary" @click="saveStudent" :loading="savingStudent">{{ editingStudent ? '保存修改' : '添加学生' }}</a-button>
        </div>
      </div>
    </a-modal>

    <!-- Panel: 班级管理（班主任专属） -->
    <div v-if="activeTab === 'class-management'" class="panel animate-fade-in">
      <div class="panel-header">
        <h2>班级管理</h2>
        <span class="binding-code-badge">🔗 班级绑定码：<code>{{ teacherBindingCode }}</code></span>
      </div>
      <div class="class-mgmt-grid">
        <!-- 班级信息卡片 -->
        <div class="class-mgmt-card">
          <div class="cm-card-icon" style="background:linear-gradient(135deg,#EEF2FF,#E0E7FF);color:#4F46E5;">
            <svg viewBox="0 0 24 24" fill="none" width="24" height="24"><path d="M3 21h18M3 10h18M5 6l7-3 7 3M4 10v11M20 10v11M8 14v4M12 14v4M16 14v4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
          </div>
          <div class="cm-card-body">
            <span class="cm-card-label">班级名称</span>
            <span class="cm-card-value">{{ teacherInfo.teacher_class_name || '未分配' }}</span>
          </div>
        </div>
        <div class="class-mgmt-card">
          <div class="cm-card-icon" style="background:linear-gradient(135deg,#ECFDF5,#D1FAE5);color:#059669;">
            <svg viewBox="0 0 24 24" fill="none" width="24" height="24"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8zM23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
          </div>
          <div class="cm-card-body">
            <span class="cm-card-label">班级人数</span>
            <span class="cm-card-value">{{ teacherInfo.teacher_class_student_number || 0 }} 人</span>
          </div>
        </div>
        <div class="class-mgmt-card">
          <div class="cm-card-icon" style="background:linear-gradient(135deg,#FDF2F8,#FCE7F3);color:#EC4899;">
            <svg viewBox="0 0 24 24" fill="none" width="24" height="24"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
          </div>
          <div class="cm-card-body">
            <span class="cm-card-label">班主任</span>
            <span class="cm-card-value">{{ teacherInfo.teacher_name }}</span>
          </div>
        </div>
        <div class="class-mgmt-card">
          <div class="cm-card-icon" style="background:linear-gradient(135deg,#FFF7ED,#FFEDD5);color:#F97316;">
            <svg viewBox="0 0 24 24" fill="none" width="24" height="24"><path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/><path d="M2 17l10 5 10-5M2 12l10 5 10-5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
          </div>
          <div class="cm-card-body">
            <span class="cm-card-label">专业方向</span>
            <span class="cm-card-value">{{ teacherInfo.teacher_major || '未设置' }}</span>
          </div>
        </div>
      </div>
      <!-- 班级学生列表 -->
      <div class="class-mgmt-section" style="margin-top:20px">
        <div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:12px">
          <h3 style="margin:0;font-size:15px;font-weight:700;color:#0F172A;">班级学生名单</h3>
          <div style="display:flex;gap:8px;align-items:center">
            <a-input v-model:value="classKeyword" placeholder="输入关键字搜索..." style="width:200px" size="small" @pressEnter="searchClassStudents" />
            <a-button type="primary" size="small" @click="searchClassStudents">搜索</a-button>
            <a-button size="small" @click="resetClassSearch">重置</a-button>
          </div>
        </div>
        <a-table v-if="filteredClassStudents.length > 0"
          :columns="classStudentColumns"
          :dataSource="filteredClassStudents"
          rowKey="student_id"
          :pagination="{ pageSize: 10 }"
        />
        <EmptyState v-else title="暂无学生" description="将班级绑定码分享给学生后，绑定的学生将显示在这里" />
      </div>
    </div>

    <!-- Panel: Analytics -->
    <div v-if="activeTab === 'analytics'" class="panel animate-fade-in">
      <div class="panel-header">
        <h2>教学分析</h2>
        <a-select v-model:value="analyticsCourse" placeholder="选择课程" class="course-select" style="width:240px" v-if="courses.length > 0">
          <a-select-option v-for="c in courses" :key="c.course_name" :value="c.course_name">{{ c.course_name }}</a-select-option>
        </a-select>
      </div>
      <div v-if="!analyticsCourse" class="panel-body">
        <div v-if="showCoursePicker !== 'analytics'" class="course-select-prompt" @click="showCoursePicker = 'analytics'">
          <div class="prompt-icon">
            <svg viewBox="0 0 24 24" fill="none" width="56" height="56">
              <path d="M18 20V10M12 20V4M6 20v-6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3 class="prompt-title">选择一门课程开始分析</h3>
          <p class="prompt-desc">点击此处选择课程，查看教学数据分析</p>
          <div class="prompt-arrow">
            <svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M6 9l6 6 6-6" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
          </div>
        </div>
        <div v-else class="course-picker-popup">
          <div class="picker-header">
            <h3>选择课程</h3>
            <button class="picker-close" @click="showCoursePicker = ''">✕</button>
          </div>
          <div class="picker-list">
            <div v-for="c in courses" :key="c.course_name" class="picker-item" @click="analyticsCourse = c.course_name; showCoursePicker = ''">
              <span class="picker-item-icon"><svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M4 19.5A2.5 2.5 0 0 0 6.5 22H20V2H6.5A2.5 2.5 0 0 0 4 4.5v15z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg></span>
              <span class="picker-item-name">{{ c.course_name }}</span>
            </div>
          </div>
        </div>
      </div>
      <!-- 选了课程但无提交数据 -->
      <div v-else-if="analyticsSubmissions.length === 0" class="panel-body">
        <EmptyState title="暂无数据" description="该课程暂无学生提交记录，无法生成教学分析" />
      </div>
      <!-- 有提交但无已批改数据 -->
      <div v-else-if="gradedSubmissionsCount === 0" class="panel-body">
        <EmptyState title="暂无批改数据" description="该课程有提交但尚未批改，请先批改学生作业或考试" />
      </div>
      <div v-else class="analytics-grid">
        <div class="analytics-card">
          <h3>练习正确率趋势</h3>
          <div ref="chartRef1" style="width:100%;height:300px"></div>
        </div>
        <div class="analytics-card">
          <h3>知识点掌握度</h3>
          <div ref="chartRef2" style="width:100%;height:300px"></div>
        </div>
      </div>
    </div>

    <!-- ===== Panel: 成绩管理 ===== -->
    <div v-if="activeTab === 'grading'" class="panel animate-fade-in">
      <div class="panel-header">
        <h2>成绩管理</h2>
        <a-select v-model:value="gradingCourse" @change="loadGradeSummary" placeholder="选择课程" class="course-select" style="width:240px" v-if="courses.length > 0">
          <a-select-option v-for="c in courses" :key="c.course_name" :value="c.course_name">{{ c.course_name }}</a-select-option>
        </a-select>
      </div>

      <div v-if="!gradingCourse" class="panel-body">
        <div v-if="showCoursePicker !== 'grading'" class="course-select-prompt" @click="showCoursePicker = 'grading'">
          <div class="prompt-icon">
            <svg viewBox="0 0 24 24" fill="none" width="56" height="56">
              <path d="M9 11l3 3L22 4M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3 class="prompt-title">选择一门课程查看成绩</h3>
          <p class="prompt-desc">点击此处选择课程，查看学生成绩与提交情况</p>
          <div class="prompt-arrow">
            <svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M6 9l6 6 6-6" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
          </div>
        </div>
        <div v-else class="course-picker-popup">
          <div class="picker-header">
            <h3>选择课程</h3>
            <button class="picker-close" @click="showCoursePicker = ''">✕</button>
          </div>
          <div class="picker-list">
            <div v-for="c in courses" :key="c.course_name" class="picker-item" @click="gradingCourse = c.course_name; showCoursePicker = ''; loadGradeSummary()">
              <span class="picker-item-icon"><svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M4 19.5A2.5 2.5 0 0 0 6.5 22H20V2H6.5A2.5 2.5 0 0 0 4 4.5v15z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg></span>
              <span class="picker-item-name">{{ c.course_name }}</span>
            </div>
          </div>
        </div>
      </div>

      <div v-else>
        <!-- Grade Summary Cards -->
        <div class="grade-summary-cards" v-if="gradeSummary.totalSubmissions !== undefined">
          <div class="grade-stat-card">
            <span class="gsc-value">{{ gradeSummary.totalSubmissions }}</span>
            <span class="gsc-label">总提交</span>
          </div>
          <div class="grade-stat-card">
            <span class="gsc-value">{{ gradeSummary.gradedSubmissions }}</span>
            <span class="gsc-label">已批改</span>
          </div>
          <div class="grade-stat-card">
            <span class="gsc-value">{{ gradeSummary.averageScore }}</span>
            <span class="gsc-label">平均分</span>
          </div>
          <div class="grade-stat-card">
            <span class="gsc-value">{{ gradeSummary.highestScore }}</span>
            <span class="gsc-label">最高分</span>
          </div>
        </div>

        <!-- Published Homework/Exam List -->
        <div v-if="gradingAssignments.length > 0" class="grading-assignment-list">
          <div class="section-label" style="margin-top:12px">已发布的作业/考试</div>
          <div v-for="item in gradingAssignments" :key="item.id" class="grading-assignment-row">
            <div class="ga-info">
              <span class="ga-type-badge" :class="item.type === 'exam' ? 'ga-exam' : 'ga-hw'">{{ item.type === 'exam' ? '考试' : '作业' }}</span>
              <span class="ga-title">{{ item.title }}</span>
            </div>
            <div class="ga-meta">
              <span>提交: {{ item.submissionCount || 0 }} 份</span>
              <span>已批: {{ item.gradedCount || 0 }} 份</span>
              <span v-if="item.avgScore">均分: {{ item.avgScore }}</span>
            </div>
            <button class="btn-sm btn-sm-primary" @click="aiAutoGradeItem(item)" :disabled="aiGradingItems.has(item.id)">
              {{ aiGradingItems.has(item.id) ? '⏳ 批改中...' : '智能批改' }}
            </button>
            <button class="btn-sm" @click="openGradingFromTab(item)" :disabled="aiGradingItems.has(item.id)">查看批改</button>
            <span v-if="aiGradingItems.has(item.id)" class="ga-grading-status">正在智能批改中...</span>
          </div>
        </div>
        <EmptyState v-else title="暂无发布记录" description="请先在「发布作业」或「发布考试」中推送内容给学生" />
      </div>
    </div>

    <!-- ===== Panel: 题库积累 ===== -->
    <div v-if="activeTab === 'question-bank'" class="panel animate-fade-in">
      <!-- ===== 列表视图 ===== -->
      <template v-if="activeQbView === 'list'">
      <div class="panel-header">
        <h2>题库积累</h2>
        <div style="display:flex;gap:10px;align-items:center;flex-wrap:wrap">
          <a-input v-model:value="qbKeyword" placeholder="输入关键字搜索..." style="width:200px" size="small" @pressEnter="searchQuestionBank" />
          <a-button type="primary" size="small" @click="searchQuestionBank">搜索</a-button>
          <a-button size="small" @click="resetQbSearch">重置</a-button>
          <a-select v-model:value="qbFilterType" placeholder="题型筛选" style="width:130px" allowClear>
            <a-select-option value="">全部题型</a-select-option>
            <a-select-option value="choice">选择题</a-select-option>
            <a-select-option value="fill-blank">填空题</a-select-option>
            <a-select-option value="true-false">判断题</a-select-option>
            <a-select-option value="essay">应用题</a-select-option>
            <a-select-option value="calculation">计算题</a-select-option>
          </a-select>
          <a-select v-model:value="qbFilterCourse" placeholder="课程筛选" style="width:180px" allowClear>
            <a-select-option value="">全部课程</a-select-option>
            <a-select-option v-for="c in courses" :key="c.course_name" :value="c.course_name">{{ c.course_name }}</a-select-option>
          </a-select>
          <button class="btn-primary" @click="openQbForm()">
            <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/></svg>
            添加题目
          </button>
        </div>
      </div>

      <!-- 批量操作栏 -->
      <div v-if="filteredQuestionBank.length > 0" class="batch-bar">
        <label class="batch-check">
          <input type="checkbox" :checked="qbAllSelected" @change="toggleQbSelectAll" />
          <span>全选</span>
        </label>
        <span class="batch-count" v-if="qbSelectedIds.size > 0">已选 {{ qbSelectedIds.size }} 项</span>
        <a-popconfirm
          v-if="qbSelectedIds.size > 0"
          title="确定批量删除所选题目？此操作不可恢复。"
          ok-text="确定删除" cancel-text="取消"
          @confirm="deleteSelectedQb"
        >
          <button class="btn-sm btn-sm-danger">🗑 批量删除</button>
        </a-popconfirm>
      </div>

      <div v-if="filteredQuestionBank.length > 0" class="qb-list">
        <div v-for="q in filteredQuestionBank" :key="q.id" class="qb-card">
          <div class="qb-header">
            <input type="checkbox" class="qb-checkbox" :checked="qbSelectedIds.has(q.id)" @change="toggleQbSelect(q.id)" @click.stop />
            <span class="qb-type-tag">{{ qbTypeLabel(q.type) }}</span>
            <span class="qb-course-tag" v-if="q.courseName">{{ q.courseName }}</span>
            <span class="qb-difficulty" v-if="q.difficulty">{{ '⭐'.repeat(q.difficulty) }}</span>
          </div>
          <div class="qb-body">
            <div class="qb-title">{{ q.title }}</div>
            <div class="qb-content" v-if="q.content">{{ q.content }}</div>
            <div class="qb-options" v-if="q.options && q.options.length > 0">
              <span v-for="(opt, oi) in q.options" :key="oi" class="qb-opt">{{ String.fromCharCode(65 + oi) }}. {{ opt }}</span>
            </div>
            <div class="qb-answer" v-if="q.correctAnswer">
              <template v-if="q.type === 'fill-blank' && q.correctAnswer.includes('|||')">
                答案: <span v-for="(part, pi) in q.correctAnswer.split('|||')" :key="pi" class="qb-blank-part">{{ part }}</span>
              </template>
              <template v-else>答案: {{ q.correctAnswer }}</template>
            </div>
          </div>
          <div class="qb-actions">
            <button class="btn-sm" @click="editQuestionBankItem(q)">编辑</button>
            <a-popconfirm title="确定删除?" ok-text="确定" cancel-text="取消" @confirm="deleteQuestionBankItem(q.id)">
              <button class="btn-sm btn-sm-danger">删除</button>
            </a-popconfirm>
          </div>
        </div>
      </div>
      <EmptyState v-else title="题库为空" description="点击「添加题目」手动录入，或在 AI 出题结果中导入题目" />
      </template>

      <!-- ===== 表单视图 ===== -->
      <template v-else-if="activeQbView === 'form'">
        <div class="panel-header">
          <button class="btn-back" @click="resetQbView">← 返回列表</button>
          <h2>{{ editingQbItem ? '编辑题目' : '添加题目' }}</h2>
          <button class="btn-clear" @click="clearQbForm" title="清空表单内容">清空</button>
        </div>
        <div class="qb-form-inline">
          <!-- 题型选择（公共） -->
          <div class="form-field">
            <label>题型 <span style="color:red">*</span></label>
            <a-select v-model:value="qbForm.type" placeholder="选择题型" style="width:100%" @change="qbForm.correctAnswer = ''">
              <a-select-option value="choice">🔤 选择题</a-select-option>
              <a-select-option value="fill-blank">✏️ 填空题</a-select-option>
              <a-select-option value="true-false">✅ 判断题</a-select-option>
              <a-select-option value="essay">📝 应用题</a-select-option>
              <a-select-option value="calculation">🔢 计算题</a-select-option>
            </a-select>
          </div>

          <!-- 题目内容（公共） -->
          <div class="form-field">
            <label>题目内容 <span style="color:red">*</span></label>
            <a-textarea v-model:value="qbForm.title" placeholder="输入题目内容..." :rows="3" />
          </div>

          <!-- ===== 选择题专属 ===== -->
          <template v-if="qbForm.type === 'choice'">
            <div class="form-field">
              <label>选项列表 <span style="color:red">*</span></label>
              <div class="qb-option-list">
                <div v-for="(_opt, oi) in qbForm.qbOptions" :key="oi" class="qb-option-row">
                  <span class="qb-opt-letter">{{ getQbOptionLetter(oi) }}</span>
                  <a-input v-model:value="qbForm.qbOptions[oi]" :placeholder="'选项 ' + getQbOptionLetter(oi) + ' 内容'" style="flex:1" />
                  <button class="qb-opt-remove" @click="removeQbOption(oi)" :disabled="qbForm.qbOptions.length <= 1" title="删除此选项">✕</button>
                </div>
              </div>
              <button class="qb-opt-add" @click="addQbOption">
                <svg viewBox="0 0 24 24" fill="none" width="14" height="14"><path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/></svg>
                添加选项
              </button>
            </div>
            <div class="form-row">
              <div class="form-field">
                <label>正确答案 <span style="color:red">*</span></label>
                <a-select v-model:value="qbForm.correctAnswer" placeholder="选择正确答案" style="width:100%">
                  <a-select-option v-for="(opt, oi) in qbChoiceOptions" :key="oi" :value="opt">{{ getQbOptionLetter(qbForm.qbOptions.indexOf(opt)) }}. {{ opt }}</a-select-option>
                </a-select>
              </div>
              <div class="form-field">
                <label>难度</label>
                <a-select v-model:value="qbForm.difficulty" placeholder="选择难度" style="width:100%">
                  <a-select-option :value="1">⭐ 简单</a-select-option>
                  <a-select-option :value="2">⭐⭐ 中等</a-select-option>
                  <a-select-option :value="3">⭐⭐⭐ 困难</a-select-option>
                </a-select>
              </div>
            </div>
          </template>

          <!-- ===== 判断题专属 ===== -->
          <template v-if="qbForm.type === 'true-false'">
            <div class="form-row">
              <div class="form-field">
                <label>正确答案 <span style="color:red">*</span></label>
                <div class="qb-tf-toggle">
                  <button class="qb-tf-btn tf-correct" :class="{ active: qbForm.correctAnswer === '正确' }" @click="qbForm.correctAnswer = '正确'">
                    <svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M9 11l3 3L22 4" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
                    正确
                  </button>
                  <button class="qb-tf-btn tf-wrong" :class="{ active: qbForm.correctAnswer === '错误' }" @click="qbForm.correctAnswer = '错误'">
                    <svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M18 6L6 18M6 6l12 12" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/></svg>
                    错误
                  </button>
                </div>
              </div>
              <div class="form-field">
                <label>难度</label>
                <a-select v-model:value="qbForm.difficulty" placeholder="选择难度" style="width:100%">
                  <a-select-option :value="1">⭐ 简单</a-select-option>
                  <a-select-option :value="2">⭐⭐ 中等</a-select-option>
                  <a-select-option :value="3">⭐⭐⭐ 困难</a-select-option>
                </a-select>
              </div>
            </div>
          </template>

          <!-- ===== 填空题专属 ===== -->
          <template v-if="qbForm.type === 'fill-blank'">
            <div class="form-field">
              <label>填空答案 <span style="color:red">*</span></label>
              <span class="field-hint" style="margin-bottom:6px">按题目中空位顺序依次填写，支持多个空</span>
              <div class="qb-blank-list">
                <div v-for="(_blank, bi) in qbForm.qbFillBlanks" :key="bi" class="qb-blank-row">
                  <span class="qb-blank-num">第{{ bi + 1 }}空</span>
                  <a-input v-model:value="qbForm.qbFillBlanks[bi]" :placeholder="'第 ' + (bi + 1) + ' 个空的答案'" style="flex:1" />
                  <button class="qb-blank-remove" @click="removeFillBlank(bi)" :disabled="qbForm.qbFillBlanks.length <= 1" title="删除此空">✕</button>
                </div>
              </div>
              <button class="qb-blank-add" @click="addFillBlank">
                <svg viewBox="0 0 24 24" fill="none" width="14" height="14"><path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/></svg>
                添加空位
              </button>
            </div>
            <div class="form-field">
              <label>难度</label>
              <a-select v-model:value="qbForm.difficulty" placeholder="选择难度" style="width:100%">
                <a-select-option :value="1">⭐ 简单</a-select-option>
                <a-select-option :value="2">⭐⭐ 中等</a-select-option>
                <a-select-option :value="3">⭐⭐⭐ 困难</a-select-option>
              </a-select>
            </div>
          </template>

          <!-- ===== 应用题专属 ===== -->
          <template v-if="qbForm.type === 'essay'">
            <div class="form-field">
              <label>参考答案或评分要点</label>
              <a-textarea v-model:value="qbForm.referenceAnswer" placeholder="参考答案或评分要点（可选）...&#10;用于教师批改时参考，不会自动判分" :rows="4" />
            </div>
            <div class="form-field">
              <label>难度</label>
              <a-select v-model:value="qbForm.difficulty" placeholder="选择难度" style="width:100%">
                <a-select-option :value="1">⭐ 简单</a-select-option>
                <a-select-option :value="2">⭐⭐ 中等</a-select-option>
                <a-select-option :value="3">⭐⭐⭐ 困难</a-select-option>
              </a-select>
            </div>
          </template>

          <!-- ===== 计算题专属 ===== -->
          <template v-if="qbForm.type === 'calculation'">
            <div class="form-row">
              <div class="form-field">
                <label>正确答案 <span style="color:red">*</span></label>
                <a-input v-model:value="qbForm.correctAnswer" placeholder="如: 42 或 x=3" />
              </div>
              <div class="form-field">
                <label>难度</label>
                <a-select v-model:value="qbForm.difficulty" placeholder="选择难度" style="width:100%">
                  <a-select-option :value="1">⭐ 简单</a-select-option>
                  <a-select-option :value="2">⭐⭐ 中等</a-select-option>
                  <a-select-option :value="3">⭐⭐⭐ 困难</a-select-option>
                </a-select>
              </div>
            </div>
            <div class="form-field">
              <label>解题步骤或公式</label>
              <a-textarea v-model:value="qbForm.referenceAnswer" placeholder="解题步骤或关键公式（可选）..." :rows="3" />
            </div>
          </template>

          <!-- 公共字段：课程 + 标签 -->
          <div class="form-field">
            <label>所属课程</label>
            <a-select v-model:value="qbForm.courseName" placeholder="选择课程" style="width:100%" allowClear>
              <a-select-option v-for="c in courses" :key="c.course_name" :value="c.course_name">{{ c.course_name }}</a-select-option>
            </a-select>
          </div>
          <div class="form-field">
            <label>标签 (用逗号分隔)</label>
            <a-input v-model:value="qbForm.tagsText" placeholder="如: 排序算法, 数据结构" />
          </div>

          <button class="btn-primary" style="width:100%;margin-top:16px" @click="saveQuestionBankItem">
            {{ editingQbItem ? '保存修改' : '添加到题库' }}
          </button>
        </div>
      </template>
    </div>

    <!-- ===== Panel: 通知中心 ===== -->
    <div v-if="activeTab === 'notifications'" class="panel animate-fade-in">
      <div class="panel-header">
        <h2>通知中心</h2>
        <button class="btn-primary" @click="showNotifyModal = true">
          <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/></svg>
          发送通知
        </button>
      </div>

      <!-- 批量操作栏 -->
      <div v-if="notificationList.length > 0" class="batch-bar">
        <label class="batch-check">
          <input type="checkbox" :checked="notifAllSelected" @change="toggleNotifSelectAll" />
          <span>全选</span>
        </label>
        <span class="batch-count" v-if="notifSelectedIds.size > 0">已选 {{ notifSelectedIds.size }} 项</span>
        <a-popconfirm v-if="notifSelectedIds.size > 0" title="确定批量删除所选通知？" ok-text="确定删除" cancel-text="取消" @confirm="deleteSelectedNotif">
          <button class="btn-sm btn-sm-danger">🗑 批量删除</button>
        </a-popconfirm>
      </div>
      <div v-if="notificationList.length > 0" class="notif-list">
        <div v-for="n in notificationList" :key="n.id" class="notif-card" :class="{ 'notif-unread': !n.read }">
          <div class="notif-header">
            <input type="checkbox" class="card-checkbox" style="margin-right:8px" :checked="notifSelectedIds.has(n.id)" @change="toggleNotifSelect(n.id)" @click.stop />
            <span class="notif-course">{{ n.courseName || '全站通知' }}</span>
            <span class="notif-priority-badge" :class="'np-' + (n.priority || 'normal')">{{ n.priority === 'urgent' ? '🚨 紧急' : n.priority === 'important' ? '📌 重要' : '普通' }}</span>
            <span class="notif-type-badge">{{ n.type === 'announcement' ? '公告' : n.type === 'exam' ? '考试' : '作业' }}</span>
            <span class="notif-time">{{ formatTime(n.createdAt) }}</span>
          </div>
          <h4 class="notif-title">{{ n.title }}</h4>
          <p class="notif-content">{{ n.content }}</p>
          <div class="notif-actions">
            <button class="btn-sm" @click="markNotifRead(n.id)" v-if="!n.read">标记已读</button>
            <a-popconfirm title="确定删除?" ok-text="确定" cancel-text="取消" @confirm="deleteNotification(n.id)">
              <button class="btn-sm btn-sm-danger">删除</button>
            </a-popconfirm>
          </div>
        </div>
      </div>
      <EmptyState v-else title="暂无通知" description="点击「发送通知」向学生发布课程公告或提醒" />

      <!-- Send Notification Modal -->
      <a-modal v-model:open="showNotifyModal" title="发送通知" :footer="null" width="560px">
        <div class="homework-form">
          <div class="form-field">
            <label>课程 <span style="color:red">*</span></label>
            <a-select v-model:value="notifyForm.courseName" placeholder="选择课程" style="width:100%">
              <a-select-option v-for="c in courses" :key="c.course_name" :value="c.course_name">{{ c.course_name }}</a-select-option>
            </a-select>
          </div>
          <div class="form-row">
            <div class="form-field">
              <label>通知类型</label>
              <a-select v-model:value="notifyForm.type" style="width:100%">
                <a-select-option value="announcement">📢 课程公告</a-select-option>
                <a-select-option value="homework">📝 作业提醒</a-select-option>
                <a-select-option value="exam">📋 考试通知</a-select-option>
              </a-select>
            </div>
            <div class="form-field">
              <label>优先级</label>
              <div class="notif-priority-toggle">
                <button class="np-btn np-normal" :class="{ active: notifyForm.priority === 'normal' }" @click="notifyForm.priority = 'normal'">
                  普通
                </button>
                <button class="np-btn np-important" :class="{ active: notifyForm.priority === 'important' }" @click="notifyForm.priority = 'important'">
                  重要
                </button>
                <button class="np-btn np-urgent" :class="{ active: notifyForm.priority === 'urgent' }" @click="notifyForm.priority = 'urgent'">
                  紧急
                </button>
              </div>
            </div>
          </div>
          <div class="form-field">
            <label>标题 <span style="color:red">*</span></label>
            <a-input v-model:value="notifyForm.title" placeholder="通知标题" :maxlength="100" />
            <span class="field-hint">{{ notifyForm.title.length }}/100</span>
          </div>
          <div class="form-field">
            <label>内容</label>
            <a-textarea v-model:value="notifyForm.content" placeholder="通知内容..." :rows="5" :maxlength="2000" />
            <span class="field-hint" style="text-align:right">{{ notifyForm.content.length }}/2000</span>
          </div>
          <button class="btn-primary" style="width:100%" @click="sendNotification" :disabled="!notifyForm.courseName || !notifyForm.title">
            {{ notifyForm.priority === 'urgent' ? '🚨 发送紧急通知' : notifyForm.priority === 'important' ? '📌 发送重要通知' : '发送通知' }}
          </button>
        </div>
      </a-modal>
    </div>

    <!-- ===== Panel: 模板复用 ===== -->
    <div v-if="activeTab === 'templates'" class="panel animate-fade-in">
      <div class="panel-header">
        <h2>模板复用</h2>
        <div style="display:flex;gap:10px">
          <a-select v-model:value="templateFilterType" placeholder="类型筛选" style="width:130px" allowClear>
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="homework">作业模板</a-select-option>
            <a-select-option value="exam">考试模板</a-select-option>
          </a-select>
        </div>
      </div>

      <div v-if="filteredTemplates.length > 0" class="template-grid">
        <div v-for="tpl in filteredTemplates" :key="tpl.id" class="template-card">
          <div class="tpl-header">
            <span class="tpl-type-badge" :class="tpl.type === 'exam' ? 'tpl-exam' : 'tpl-hw'">{{ tpl.type === 'exam' ? '考试' : '作业' }}</span>
            <h4 class="tpl-title">{{ tpl.name }}</h4>
          </div>
          <div class="tpl-meta">
            <span>{{ tpl.questionCount || 0 }} 题</span>
            <span v-if="tpl.courseName">{{ tpl.courseName }}</span>
            <span class="tpl-time">更新: {{ formatTime(tpl.updatedAt) }}</span>
          </div>
          <!-- Question type breakdown -->
          <div class="tpl-types" v-if="tpl.questions && tpl.questions.length > 0">
            <span v-for="(cnt, type) in getTemplateTypeCounts(tpl)" :key="type" class="tpl-type-chip">
              {{ getTypeEmoji(type) }} {{ getTypeLabel(type) }}×{{ cnt }}
            </span>
          </div>
          <p class="tpl-desc" v-if="tpl.description">{{ tpl.description }}</p>
          <div class="tpl-actions">
            <button class="btn-sm" @click="previewTemplate(tpl)">预览</button>
            <button class="btn-sm" @click="applyTemplate(tpl)">使用</button>
            <a-popconfirm title="确定删除?" ok-text="确定" cancel-text="取消" @confirm="deleteTemplate(tpl.id)">
              <button class="btn-sm btn-sm-danger">删除</button>
            </a-popconfirm>
          </div>
        </div>
      </div>
      <EmptyState v-else title="暂无模板" description="在「发布作业」或「发布考试」中编辑内容后，可点击「另存为模板」保存" />

      <!-- Template Preview Modal -->
      <a-modal v-model:open="showTemplatePreview" :title="previewingTemplate?.name" :footer="null" width="680px">
        <div v-if="previewingTemplate" class="tpl-preview">
          <div class="tpl-preview-meta">
            <span class="tpl-type-badge" :class="previewingTemplate.type === 'exam' ? 'tpl-exam' : 'tpl-hw'">{{ previewingTemplate.type === 'exam' ? '考试模板' : '作业模板' }}</span>
            <span v-if="previewingTemplate.courseName">{{ previewingTemplate.courseName }}</span>
            <span>{{ previewingTemplate.questionCount || 0 }} 题</span>
          </div>
          <p v-if="previewingTemplate.description" class="tpl-preview-desc">{{ previewingTemplate.description }}</p>
          <div class="section-label" style="margin:12px 0 8px">题目列表</div>
          <div v-if="previewingTemplate.questions && previewingTemplate.questions.length > 0" class="tpl-preview-questions">
            <div v-for="(q, qi) in previewingTemplate.questions" :key="qi" class="tpl-preview-q">
              <div class="tpq-header">
                <span class="tpq-idx">{{ qi + 1 }}</span>
                <span class="tpq-type">{{ getTypeEmoji(q.type) }} {{ getTypeLabel(q.type) }}</span>
                <span class="tpq-score" v-if="q.score">{{ q.score }} 分</span>
              </div>
              <div class="tpq-title">{{ q.title }}</div>
              <div v-if="q.options && q.options.length > 0" class="tpq-options">
                <span v-for="(opt, oi) in q.options" :key="oi" class="tpq-opt">{{ String.fromCharCode(65 + oi) }}. {{ opt }}</span>
              </div>
              <div v-if="q.correctAnswer" class="tpq-answer">答案: {{ q.correctAnswer }}</div>
            </div>
          </div>
          <div class="tpl-preview-actions" style="margin-top:16px;display:flex;gap:10px;justify-content:flex-end">
            <button class="btn-sm" @click="showTemplatePreview = false">关闭</button>
            <button class="btn-primary" @click="applyTemplate(previewingTemplate!); showTemplatePreview = false">使用此模板</button>
          </div>
        </div>
      </a-modal>
    </div>

    <!-- ===== Panel: 讨论区 ===== -->
    <div v-if="activeTab === 'discussions'" class="panel animate-fade-in">
      <div class="panel-header">
        <h2>讨论区</h2>
        <div style="display:flex;gap:10px">
          <a-select v-model:value="discCourseFilter" placeholder="选择课程" class="course-select" style="width:200px" v-if="courses.length > 0">
            <a-select-option v-for="c in courses" :key="c.course_name" :value="c.course_name">{{ c.course_name }}</a-select-option>
          </a-select>
          <button class="btn-primary" @click="showNewTopicModal = true" :disabled="!discCourseFilter">
            <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/></svg>
            发布话题
          </button>
        </div>
      </div>

      <div v-if="!discCourseFilter" class="panel-body">
        <div v-if="showCoursePicker !== 'discussions'" class="course-select-prompt" @click="showCoursePicker = 'discussions'">
          <div class="prompt-icon">
            <svg viewBox="0 0 24 24" fill="none" width="56" height="56">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3 class="prompt-title">选择一门课程开始讨论</h3>
          <p class="prompt-desc">点击此处选择课程，查看和发布课程讨论话题</p>
          <div class="prompt-arrow">
            <svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M6 9l6 6 6-6" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
          </div>
        </div>
        <div v-else class="course-picker-popup">
          <div class="picker-header">
            <h3>选择课程</h3>
            <button class="picker-close" @click="showCoursePicker = ''">✕</button>
          </div>
          <div class="picker-list">
            <div v-for="c in courses" :key="c.course_name" class="picker-item" @click="discCourseFilter = c.course_name; showCoursePicker = ''">
              <span class="picker-item-icon"><svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M4 19.5A2.5 2.5 0 0 0 6.5 22H20V2H6.5A2.5 2.5 0 0 0 4 4.5v15z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg></span>
              <span class="picker-item-name">{{ c.course_name }}</span>
            </div>
          </div>
        </div>
      </div>

      <div v-else>
        <!-- Topic List -->
        <div v-if="filteredDiscussions.length > 0" class="disc-list">
          <div v-for="topic in filteredDiscussions" :key="topic.id" class="disc-card" @click="openTopicDetail(topic)">
            <div class="disc-card-header">
              <span v-if="topic.pinned" class="disc-pinned-badge">📌 置顶</span>
              <span v-if="topic.category" class="disc-cat-badge">{{ topic.category }}</span>
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
        <a-modal v-model:open="showTopicDetail" :title="viewingTopic?.title" :footer="null" width="700px" @cancel="editingReplyIdx = -1; editingTopicContent = ''">
          <div v-if="viewingTopic" class="topic-detail">
            <div class="topic-body">
              <div class="topic-meta">
                <span class="topic-meta-left">
                  <span class="topic-author">{{ viewingTopic.author }}</span>
                  <span v-if="viewingTopic.category" class="disc-cat-badge">{{ viewingTopic.category }}</span>
                  <span>{{ formatTime(viewingTopic.createdAt) }}</span>
                </span>
                <span class="topic-meta-right">
                  <button class="btn-sm" @click="startEditTopic" v-if="viewingTopic.author === (teacherUsername || '教师')">编辑</button>
                  <a-popconfirm title="确定删除此话题?" ok-text="确认" cancel-text="取消" @confirm="deleteTopic(viewingTopic.id)">
                    <button class="btn-sm btn-sm-danger" v-if="viewingTopic.author === (teacherUsername || '教师')">删除</button>
                  </a-popconfirm>
                </span>
              </div>
              <!-- Topic content (view mode) -->
              <div v-if="editingTopicContent === ''" class="topic-content">{{ viewingTopic.content }}</div>
              <!-- Topic content (edit mode) -->
              <div v-else class="topic-edit-area">
                <a-textarea v-model:value="editingTopicContent" :rows="4" />
                <div style="display:flex;gap:8px;margin-top:8px">
                  <button class="btn-sm" @click="saveTopicEdit">保存</button>
                  <button class="btn-sm" @click="editingTopicContent = ''">取消</button>
                </div>
              </div>
            </div>
            <div class="topic-replies-section">
              <div class="section-label">回复 ({{ viewingTopic.replies?.length || 0 }})</div>
              <div v-for="(reply, ri) in viewingTopic.replies" :key="ri" class="reply-card">
                <div class="reply-meta">
                  <span class="reply-author">{{ reply.author }}</span>
                  <span class="reply-time">{{ formatTime(reply.createdAt) }}</span>
                  <span class="reply-actions-right">
                    <button class="btn-sm" @click="startEditReply(ri)" v-if="reply.author === (teacherUsername || '教师')">编辑</button>
                    <a-popconfirm title="确定删除?" ok-text="确认" cancel-text="取消" @confirm="deleteReply(ri)">
                      <button class="btn-sm btn-sm-danger" v-if="reply.author === (teacherUsername || '教师')">删除</button>
                    </a-popconfirm>
                  </span>
                </div>
                <div v-if="editingReplyIdx !== ri" class="reply-content">{{ reply.content }}</div>
                <div v-else class="reply-edit-area">
                  <a-textarea v-model:value="editingReplyContent" :rows="3" />
                  <div style="display:flex;gap:8px;margin-top:8px">
                    <button class="btn-sm" @click="saveReplyEdit(ri)">保存</button>
                    <button class="btn-sm" @click="editingReplyIdx = -1">取消</button>
                  </div>
                </div>
              </div>
              <div v-if="!viewingTopic.replies || viewingTopic.replies.length === 0" class="no-replies">暂无回复</div>
            </div>
            <div class="reply-input-area">
              <a-textarea v-model:value="replyContent" placeholder="输入回复内容..." :rows="3" :maxlength="1000" />
              <div style="display:flex;justify-content:space-between;align-items:center">
                <span class="field-hint">{{ replyContent.length }}/1000</span>
                <button class="btn-primary" style="margin-top:8px" @click="submitReply" :disabled="!replyContent.trim()">发送回复</button>
              </div>
            </div>
          </div>
        </a-modal>

        <!-- New Topic Modal -->
        <a-modal v-model:open="showNewTopicModal" title="发布话题" :footer="null" width="560px">
          <div class="homework-form">
            <div class="form-field">
              <label>标题 <span style="color:red">*</span></label>
              <a-input v-model:value="newTopicForm.title" placeholder="话题标题" :maxlength="100" />
              <span class="field-hint">{{ newTopicForm.title.length }}/100</span>
            </div>
            <div class="form-row">
              <div class="form-field">
                <label>分类标签</label>
                <a-input v-model:value="newTopicForm.category" placeholder="如：知识点讨论、作业答疑、课程建议" />
              </div>
              <div class="form-field">
                <label>置顶</label>
                <div class="qb-tf-toggle" style="margin-top:4px">
                  <button class="qb-tf-btn tf-correct" :class="{ active: newTopicForm.pinned }" @click="newTopicForm.pinned = !newTopicForm.pinned" type="button">
                    📌 {{ newTopicForm.pinned ? '已置顶' : '不置顶' }}
                  </button>
                </div>
              </div>
            </div>
            <div class="form-field">
              <label>内容 <span style="color:red">*</span></label>
              <a-textarea v-model:value="newTopicForm.content" placeholder="话题内容..." :rows="6" :maxlength="3000" />
              <span class="field-hint" style="text-align:right">{{ newTopicForm.content.length }}/3000</span>
            </div>
            <button class="btn-primary" style="width:100%" @click="createTopic" :disabled="!newTopicForm.title.trim() || !newTopicForm.content.trim()">
              发布话题
            </button>
          </div>
        </a-modal>
      </div>
    </div>

    <!-- Token 过期提醒弹窗 -->
    <a-modal
      v-model:open="showTokenExpiredModal"
      title="登录已过期"
      :footer="null"
      :maskClosable="true"
      :closable="true"
      @cancel="handleTokenExpiredRedirect"
      width="400px"
    >
      <div class="token-expired-content">
        <div class="token-expired-icon">
          <svg viewBox="0 0 24 24" fill="none" width="48" height="48">
            <circle cx="12" cy="12" r="10" stroke="#F59E0B" stroke-width="2"/>
            <path d="M12 8v4M12 16h.01" stroke="#F59E0B" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </div>
        <p class="token-expired-title">您的登录凭证已过期</p>
        <p class="token-expired-desc">为确保账户安全，请重新登录</p>
        <button class="btn-primary" style="width:100%" @click="handleTokenExpiredRedirect">
          返回登录
        </button>
      </div>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, onUnmounted, watch, nextTick, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import * as echarts from 'echarts'
import StatCard from '../../components/common/StatCard.vue'
import EmptyState from '../../components/common/EmptyState.vue'
import MarkdownRenderer from '../../components/common/MarkdownRenderer.vue'
import { getCozeKnowledgeOrganizationApi } from '../../api/teacher/getKnowledgeOrganizationApi'
import { getCozePractiseDesignApi } from '../../api/teacher/getPractiseDesignApi'
import { getCozeDesignIssueApi } from '../../api/teacher/getDesignIssueApi'
import { getCozeArrangeTimeApi } from '../../api/teacher/getArrangeTimeApi'
import { deleteCourseApi } from '../../api/teacher/CourseListApi'
import { getSelfTeacherDetailApi } from '../../api/teacher/getSelfTeacherDetailApi'
import { useAiSearchStore } from '../../usePinia/aiSearchStore'
import { useIsLogin } from '../../usePinia/isLogin'
import { jwtDecode } from 'jwt-decode'
import { exportContent, type ExportFormat, EXPORT_OPTIONS } from '../../composables/useExport'
import { parseQuestionsFromMarkdown } from '../../composables/useQuestionParser'
import { loadHomeworkList, saveHomeworkList, loadExamList, saveExamList } from '../../composables/usePublisherPersistence'

import type { PendingImportData, PendingEditData, HomeworkItem, ExamItem } from '../../types/question'
import type { R } from '../../response/R'

const router = useRouter()
const route = useRoute()

// ===== AI Search Store =====
const aiStore = useAiSearchStore()

/** 用户手动关闭过的任务 ID —— watcher 不再为其自动弹窗 */
const manuallyClosedTaskIds = new Set<string>()

/** 从 JWT 中提取教师标识（sub 字段即 username，用于 API 查询） */
function getTeacherIdFromToken(): string {
  try {
    const token = (sessionStorage.getItem('token') || localStorage.getItem('token'))
    if (!token) return ''
    const decoded: any = jwtDecode(token)
    return decoded?.sub || decoded?.username || ''
  } catch {
    return ''
  }
}

/** 从 JWT 中提取教师用户名（作为后端查询时的兜底） */
function getTeacherUsernameFromToken(): string {
  return getTeacherIdFromToken()
}

/** 获取教师用户名（供 API 调用用），确保不为空 */
function resolveTeacherUsername(): string {
  return teacherUsername.value || getTeacherUsernameFromToken()
}

function copyBindingCode() {
  if (!teacherBindingCode.value) return
  navigator.clipboard.writeText(teacherBindingCode.value).then(() => {
    message.success('班级绑定码已复制到剪贴板')
  }).catch(() => {
    message.error('复制失败，请手动复制')
  })
}

// ===== Navigation =====
const activeTab = ref('profile')

const tabsBase = [
  { key: 'profile', label: '个人中心', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>' },
  { key: 'courses', label: '课程管理', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M4 19.5A2.5 2.5 0 0 0 6.5 22H20V2H6.5A2.5 2.5 0 0 0 4 4.5v15z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>' },
  { key: 'courseware', label: '课件管理', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/><path d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z" stroke="currentColor" stroke-width="2"/></svg>' },
  { key: 'homework', label: '发布作业', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8zM14 2v6h6M16 13H8M16 17H8M10 9H8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>' },
  { key: 'exam', label: '发布考试', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M9 11l3 3L22 4M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>' },
  { key: 'practical-training', label: '实训计划', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2M9 5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2M9 15l2 2 4-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>' },
  { key: 'attendance', label: '考勤记录', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2M9 5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v0a2 2 0 0 1-2 2h-2a2 2 0 0 1-2-2zM9 14l2 2 4-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>' },
  { key: 'students', label: '学生管理', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>' },
  { key: 'analytics', label: '教学分析', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M18 20V10M12 20V4M6 20v-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>' },
  { key: 'grading', label: '成绩管理', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M9 11l3 3L22 4M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>' },
  { key: 'question-bank', label: '题库积累', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>' },
  { key: 'notifications', label: '通知中心', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9M13.73 21a2 2 0 0 1-3.46 0" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>' },
  { key: 'templates', label: '模板复用', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8zM14 2v6h6M12 11v6M9 14h6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>' },
  { key: 'ai-tools', label: 'AI 教学工具', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>' },
  { key: 'discussions', label: '讨论区', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>' },
]
const classManagementTab = { key: 'class-management', label: '班级管理', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M3 21h18M3 10h18M5 6l7-3 7 3M4 10v11M20 10v11M8 14v4M12 14v4M16 14v4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>' }

const tabs = computed(() => {
  if (teacherInfo.value.teacher_position === '班主任') {
    const idx = tabsBase.findIndex(t => t.key === 'students')
    const items = [...tabsBase]
    items.splice(idx + 1, 0, classManagementTab)
    return items
  }
  return tabsBase
})

// ===== Stats =====
const stats = reactive({ courses: 0, students: 0, issues: 0, resources: 0 })
const courses = ref<any[]>([])
const students = ref<any[]>([])
const classKeyword = ref('')
const filteredClassStudents = computed(() => {
  const term = classKeyword.value.trim().toLowerCase()
  if (!term) return students.value
  const regex = new RegExp(term.split('').map(c => c.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')).join('.*'), 'i')
  return students.value.filter(item =>
    Object.values(item).some(v => v != null && String(v).toLowerCase().match(regex))
  )
})
const teacherUsername = ref('')
const teacherBindingCode = ref('')
const teacherInfo = ref<any>({})

// ===== 学生管理 =====
const enrolledStudents = ref<any[]>([])
const studentFilterCourse = ref('')
const studentSearch = ref('')
const showStudentModal = ref(false)
const editingStudent = ref<any>(null)
const savingStudent = ref(false)
const studentForm = reactive({
  student_username: '',
  student_name: '',
  student_gender: '' as string,
  student_age: '',
  student_major: '',
  student_class_name: '',
  password: '',
  course_names: [] as string[],
})

const filteredEnrolledStudents = computed(() => {
  let list = enrolledStudents.value
  if (studentSearch.value.trim()) {
    const kw = studentSearch.value.trim().toLowerCase()
    list = list.filter((s: any) =>
      (s.student_account || '').toLowerCase().includes(kw) ||
      (s.student_name || '').toLowerCase().includes(kw)
    )
  }
  return list
})

function onStudentSearch() {
  // filteredEnrolledStudents is computed, no need to do anything extra
}

async function loadStudentList() {
  if (!teacherUsername.value) return
  try {
    const { listStudentsApi } = await import('../../api/teacher/studentManageApi')
    const res = await listStudentsApi({
      teacher_username: teacherUsername.value,
      course_name: studentFilterCourse.value || undefined,
    })
    if (res.data?.code === 200) {
      enrolledStudents.value = res.data.data || []
    }
  } catch { /* ignore */ }
}

function openAddStudentModal() {
  editingStudent.value = null
  studentForm.student_username = ''
  studentForm.student_name = ''
  studentForm.student_gender = ''
  studentForm.student_age = ''
  studentForm.student_major = ''
  studentForm.student_class_name = teacherInfo.value.teacher_class_name || ''
  studentForm.password = ''
  studentForm.course_names = []
  showStudentModal.value = true
}

function openEditStudentModal(student: any) {
  editingStudent.value = student
  studentForm.student_username = student.student_account || ''
  studentForm.student_name = student.student_name || ''
  studentForm.student_gender = student.student_gender || ''
  studentForm.student_age = student.student_age || ''
  studentForm.student_major = student.student_major || ''
  studentForm.student_class_name = student.student_class_name || ''
  studentForm.password = ''
  studentForm.course_names = []
  showStudentModal.value = true
}

async function saveStudent() {
  if (!studentForm.student_username || !studentForm.student_name) {
    message.warning('请填写学号和姓名')
    return
  }
  if (!editingStudent.value && !studentForm.password) {
    message.warning('请填写密码')
    return
  }
  savingStudent.value = true
  try {
    const { addStudentApi, updateStudentApi } = await import('../../api/teacher/studentManageApi')
    if (editingStudent.value) {
      const payload: any = {
        student_username: studentForm.student_username,
        student_name: studentForm.student_name,
        student_gender: studentForm.student_gender,
        student_age: studentForm.student_age,
        student_major: studentForm.student_major,
        student_class_name: studentForm.student_class_name,
      }
      if (studentForm.password) {
        payload.password = studentForm.password
      }
      const res = await updateStudentApi(payload)
      if (res.data?.code === 200) {
        message.success('学生信息已更新')
        showStudentModal.value = false
        await loadStudentList()
      } else {
        message.error(res.data?.msg || '更新失败')
      }
    } else {
      const payload: any = {
        student_username: studentForm.student_username,
        student_name: studentForm.student_name,
        student_gender: studentForm.student_gender,
        student_age: studentForm.student_age,
        student_major: studentForm.student_major,
        student_class_name: studentForm.student_class_name || teacherInfo.value.teacher_class_name || '',
        password: studentForm.password,
        teacher_username: teacherUsername.value,
        course_names: studentForm.course_names,
      }
      const res = await addStudentApi(payload)
      if (res.data?.code === 200) {
        message.success('学生添加成功')
        showStudentModal.value = false
        await loadStudentList()
        // 同时刷新教师数据以更新统计
        await refreshTeacherData()
      } else {
        message.error(res.data?.msg || '添加失败')
      }
    }
  } catch {
    message.error('操作失败，请重试')
  } finally {
    savingStudent.value = false
  }
}

async function deleteStudent(studentUsername: string) {
  if (!teacherUsername.value) return
  try {
    const { deleteStudentApi } = await import('../../api/teacher/studentManageApi')
    const res = await deleteStudentApi(studentUsername, teacherUsername.value)
    if (res.data?.code === 200) {
      message.success('学生已删除')
      studentSelectedIds.value.delete(studentUsername)
      await loadStudentList()
      await refreshTeacherData()
    } else {
      message.error(res.data?.msg || '删除失败')
    }
  } catch {
    message.error('删除失败，请重试')
  }
}

// ===== 学生批量选择 =====
const studentSelectedIds = ref<Set<string>>(new Set())
const studentAllSelected = computed(() =>
  filteredEnrolledStudents.value.length > 0 &&
  filteredEnrolledStudents.value.every((s: any) => studentSelectedIds.value.has(s.student_account))
)
function toggleStudentSelectAll() {
  studentSelectedIds.value = studentAllSelected.value
    ? new Set()
    : new Set(filteredEnrolledStudents.value.map((s: any) => s.student_account))
}
function toggleStudentSelect(studentAccount: string) {
  const n = new Set(studentSelectedIds.value)
  n.has(studentAccount) ? n.delete(studentAccount) : n.add(studentAccount)
  studentSelectedIds.value = n
}
async function deleteSelectedStudents() {
  if (studentSelectedIds.value.size === 0) { message.warning('请先选择要删除的学生'); return }
  if (!teacherUsername.value) return
  try {
    const { batchDeleteStudentsApi } = await import('../../api/teacher/studentManageApi')
    const res = await batchDeleteStudentsApi({
      student_usernames: [...studentSelectedIds.value],
      teacher_username: teacherUsername.value,
    })
    if (res.data?.code === 200) {
      message.success(res.data?.msg || '批量删除成功')
      studentSelectedIds.value = new Set()
      await loadStudentList()
      await refreshTeacherData()
    } else {
      message.error(res.data?.msg || '批量删除失败')
    }
  } catch {
    message.error('批量删除失败，请重试')
  }
}

// ===== Token 过期呼吸检查 =====
const showTokenExpiredModal = ref(false)
let tokenCheckInterval: ReturnType<typeof setInterval> | null = null
const TOKEN_CHECK_INTERVAL_MS = 5 * 60 * 1000 // 5分钟

/** 检查 JWT token 是否过期 */
function checkTokenExpiry() {
  // 已有过期弹窗在显示，不重复检查
  if (showTokenExpiredModal.value) return

  const token = (sessionStorage.getItem('token') || localStorage.getItem('token'))
  if (!token) {
    // 没有 token 说明未登录，不需要弹窗
    return
  }

  try {
    const decoded: any = jwtDecode(token)
    const now = Math.floor(Date.now() / 1000)
    if (decoded.exp && decoded.exp <= now) {
      // Token 已过期，检查是否有活跃的 AI 搜索任务
      const hasActiveTasks = aiStore.tasks.some(t => t.status === 'searching')
      if (!hasActiveTasks) {
        showTokenExpiredModal.value = true
      }
    }
  } catch {
    // JWT 解析失败（损坏的 token），也视为过期
    const hasActiveTasks = aiStore.tasks.some(t => t.status === 'searching')
    if (!hasActiveTasks) {
      showTokenExpiredModal.value = true
    }
  }
}

/** Token 过期后跳转到登录页 */
function handleTokenExpiredRedirect() {
  showTokenExpiredModal.value = false
  localStorage.clear()
  useIsLogin().isLogin = false
  message.info('登录已过期，请重新登录', 2)
  // 跳转到登录首页
  window.location.hash = '#/'
  window.location.reload()
}

/** 恢复行内里界面的子视图状态（考勤/实训计划） */
function restoreInlineSubviews() {
  const attView = sessionStorage.getItem('attendance_subview')
  if (attView === 'form' || attView === 'detail') {
    activeAttendanceView.value = attView
    // 恢复表单草稿
    const attDraft = sessionStorage.getItem('attendance_form_draft')
    if (attDraft && attView === 'form') {
      try {
        const d = JSON.parse(attDraft)
        if (d.courseName) attendanceForm.courseName = d.courseName
        if (d.date) attendanceForm.date = d.date
        if (d.period !== undefined) attendanceForm.period = d.period
        if (d.remark) attendanceForm.remark = d.remark
      } catch { /* ignore */ }
    }
    // 恢复详情视图的记录ID
    if (attView === 'detail') {
      const attDetailId = sessionStorage.getItem('attendance_detail_id')
      if (attDetailId) {
        const found = attendanceList.value.find(a => a.id === attDetailId)
        if (found) viewingAttendance.value = found
        else activeAttendanceView.value = 'list' // 记录已删除，回列表
      }
    }
  }

  const tpView = sessionStorage.getItem('tp_subview')
  if (tpView === 'form' || tpView === 'detail') {
    activeTpView.value = tpView
    // 恢复表单草稿
    const tpDraft = sessionStorage.getItem('tp_form_draft')
    if (tpDraft && tpView === 'form') {
      try {
        const d = JSON.parse(tpDraft)
        if (d.courseName) tpForm.courseName = d.courseName
        if (d.title) tpForm.title = d.title
        if (d.description) tpForm.description = d.description
        if (d.templateKey) tpForm.templateKey = d.templateKey
        if (d.sections && d.sections.length > 0) tpForm.sections = d.sections
        if (d.editingId) {
          const found = trainingPlanList.value.find(t => t.id === d.editingId)
          if (found) editingTrainingPlan.value = found
        }
      } catch { /* ignore */ }
    }
    // 恢复详情视图的记录ID
    if (tpView === 'detail') {
      const tpDetailId = sessionStorage.getItem('tp_detail_id')
      if (tpDetailId) {
        const found = trainingPlanList.value.find(t => t.id === tpDetailId)
        if (found) viewingTrainingPlan.value = found
        else activeTpView.value = 'list'
      }
    }
  }

  // 恢复题库里界面
  const qbView = sessionStorage.getItem('qb_subview')
  if (qbView === 'form') {
    activeQbView.value = 'form'
    const qbDraft = sessionStorage.getItem(QB_DRAFT_KEY)
    if (qbDraft) {
      try {
        const d = JSON.parse(qbDraft)
        if (d.type) qbForm.type = d.type
        if (d.title) qbForm.title = d.title
        if (d.qbOptions && d.qbOptions.length > 0) qbForm.qbOptions = d.qbOptions
        if (d.qbFillBlanks && d.qbFillBlanks.length > 0) qbForm.qbFillBlanks = d.qbFillBlanks
        if (d.correctAnswer) qbForm.correctAnswer = d.correctAnswer
        if (d.referenceAnswer) qbForm.referenceAnswer = d.referenceAnswer
        if (d.difficulty) qbForm.difficulty = d.difficulty
        if (d.courseName) qbForm.courseName = d.courseName
        if (d.tagsText) qbForm.tagsText = d.tagsText
        if (d.editingId) {
          const found = questionBank.value.find(q => q.id === d.editingId)
          if (found) editingQbItem.value = found
        }
      } catch { /* ignore */ }
    }
  }
}

/** 检查是否应自动回到发布里界面（依据 active 标记而非草稿内容） */
function tryRestorePublisher(tab: string): boolean {
  const activeKeys: Record<string, string> = {
    homework: 'homework_publisher_active',
    exam: 'exam_publisher_active',
    courseware: 'courseware_publisher_active',
  }
  const key = activeKeys[tab]
  if (!key) return false
  if (sessionStorage.getItem(key) !== '1') return false

  const routes: Record<string, string> = {
    homework: '/teacher/homework-publisher',
    exam: '/teacher/exam-publisher',
    courseware: '/teacher/courseware-publisher',
  }
  router.push(routes[tab])
  return true
}

// Navigation event handler
const navHandler = (e: Event) => {
  const detail = (e as CustomEvent).detail
  if (detail && tabs.value.find(t => t.key === detail)) {
    activeTab.value = detail
    // 如果该板块有未完成的发布草稿，直接回到里界面
    if (tryRestorePublisher(detail)) return
    // 恢复行内里界面（考勤/实训计划）的子视图
    restoreInlineSubviews()
  }
}

/** 从 query param 或 sessionStorage 恢复 tab 状态 */
function restoreTabState() {
  const tabParam = route.query.tab as string | undefined
  const courseParam = route.query.course as string | undefined
  if (tabParam && tabs.value.find(t => t.key === tabParam)) {
    activeTab.value = tabParam
    if (tabParam === 'grading' && courseParam) {
      gradingCourse.value = courseParam
    }
    router.replace({ query: {} })
    // 如果该板块有未完成的发布草稿，直接回到里界面
    if (tryRestorePublisher(tabParam)) return
  } else {
    // 恢复上次停留的页面
    const saved = sessionStorage.getItem('teacher_active_tab')
    if (saved && tabs.value.find(t => t.key === saved)) {
      activeTab.value = saved
      // 如果该板块有未完成的发布草稿，直接回到里界面
      if (tryRestorePublisher(saved)) return
    }
  }

  // 恢复 AI 出题状态（从发布页面返回时）
  const savedAiState = sessionStorage.getItem('ai_questions_state')
  if (savedAiState) {
    try {
      const state = JSON.parse(savedAiState)
      if (state.result) {
        aiResults.questions = state.result
        Object.assign(aiInputs, state.inputs || {})
        if (state.courseName) selectedCourseName.value = state.courseName
        if (state.activeTab) aiActiveTab.value = state.activeTab
      }
      sessionStorage.removeItem('ai_questions_state')
    } catch { /* ignore */ }
  }

  // 恢复行内里界面（考勤/实训计划）的子视图
  restoreInlineSubviews()
}

onMounted(async () => {
  window.addEventListener('workspace-nav', navHandler)

  // 恢复 tab 状态
  restoreTabState()

  try {
    // 从 JWT sub 获取 teacher_id（与旧组件一致，后端用此值查 username）
    const tid = getTeacherIdFromToken() || localStorage.getItem('teacher_id') || JSON.parse(localStorage.getItem('userInfo') || '{}').teacher_id || ''
    if (tid) {
      const res = await getSelfTeacherDetailApi(tid)
      if (res.data?.code === 200) {
        const info = res.data.data
        teacherUsername.value = info.teacher_username || info.username || info.teacher_account || ''
        if (!teacherUsername.value) {
          teacherUsername.value = getTeacherUsernameFromToken()
        }
        teacherBindingCode.value = info.teacher_binding_code || ''
        teacherInfo.value = info
        // 存入 sessionStorage，供 AppRoot 侧边栏判断是否为班主任
        sessionStorage.setItem('teacherDetail', JSON.stringify(info))
        window.dispatchEvent(new CustomEvent('teacher-detail-updated'))
        const courseList = info.courseList || info.teacher_courseList
        if (courseList) {
          courses.value = courseList
          stats.courses = courseList.length
        }
        const studentList = info.studentList || info.teacher_studentList
        if (studentList) {
          students.value = studentList
          stats.students = studentList.length
        }
      }
    }
  } catch {
    teacherUsername.value = getTeacherUsernameFromToken()
  }

  // 如果在考勤 tab，从 API 加载最新数据
  if (activeTab.value === 'attendance' && teacherUsername.value) {
    loadAttendanceListFromApi()
    loadStudentList()
  }

  // 如果在学生管理 tab，从 API 加载选课学生
  if (activeTab.value === 'students' && teacherUsername.value) {
    loadStudentList()
  }

  // 如果在成绩管理 tab 且课程已恢复，加载成绩数据
  if (activeTab.value === 'grading' && gradingCourse.value) {
    loadGradeSummary()
  }

  // 启动 Token 过期呼吸检查（每 5 分钟）
  checkTokenExpiry() // 首次立即检查
  tokenCheckInterval = setInterval(checkTokenExpiry, TOKEN_CHECK_INTERVAL_MS)
})

/** 重新拉取教师数据（课程创建成功后刷新列表，避免整页 reload） */
async function refreshTeacherData() {
  try {
    const tid = getTeacherIdFromToken() || localStorage.getItem('teacher_id') || JSON.parse(localStorage.getItem('userInfo') || '{}').teacher_id || ''
    if (tid) {
      const res = await getSelfTeacherDetailApi(tid)
      if (res.data?.code === 200) {
        const info = res.data.data
        // 更新 sessionStorage 和侧边栏
        sessionStorage.setItem('teacherDetail', JSON.stringify(info))
        window.dispatchEvent(new CustomEvent('teacher-detail-updated'))
        const courseList = info.courseList || info.teacher_courseList
        if (courseList) {
          courses.value = courseList
          stats.courses = courseList.length
        }
        const studentList = info.studentList || info.teacher_studentList
        if (studentList) {
          students.value = studentList
          stats.students = studentList.length
        }
      }
    }
  } catch { /* ignore */ }
}

// 监听 AI 搜索任务完成事件（自动弹窗）
// 放在组件顶层以确保正确清理
const stopTaskWatcher = watch(
  () => aiStore.tasks.map(t => ({ id: t.taskId, status: t.status, viewing: t.isViewing })),
  (newVal) => {
    // 只在课程管理 tab 且没有已打开的弹窗时才自动弹出
    if (activeTab.value !== 'courses' || showCreateCourse.value) return
    // 清理：status 不再是 completed 的任务，允许下次完成后再弹窗
    for (const snap of newVal) {
      if (snap.status !== 'completed') {
        manuallyClosedTaskIds.delete(snap.id)
      }
    }
    for (const snap of newVal) {
      if (snap.status === 'completed' && !snap.viewing && !manuallyClosedTaskIds.has(snap.id)) {
        openTaskModal(snap.id)
        break
      }
    }
  },
  { deep: true }
)

onUnmounted(() => {
  stopTaskWatcher()
  window.removeEventListener('workspace-nav', navHandler)
  if (tokenCheckInterval) {
    clearInterval(tokenCheckInterval)
    tokenCheckInterval = null
  }
  if (chartInstances.length > 0) {
    chartInstances.forEach(c => { try { c.dispose() } catch { /* ignore */ } })
    chartInstances.length = 0
  }
})

// ===== Course Management =====
const showCreateCourse = ref(false)
const creating = ref(false)
const newCourse = reactive({ name: '', hours: 6, score: 3, file: null as File | null })
const viewingTaskId = ref<string | null>(null)
const viewingTask = computed(() => {
  if (!viewingTaskId.value) return null
  return aiStore.getTask(viewingTaskId.value) || null
})

// AI Web Search state (for new search)
const coursewareSource = ref<'upload' | 'ai-search'>('upload')
const searchPrompt = ref('')
const searchingCourseware = ref(false)

/** 是否允许创建课程：AI 搜索模式必须走"搜索→确认→创建"流程，不能直接创建 */
const canCreateCourse = computed(() => {
  // AI 搜索模式：不能直接点创建，必须通过"AI 搜索相关知识"按钮触发搜索
  if (coursewareSource.value === 'ai-search') {
    return false
  }
  // 上传模式：必须选择文件
  return !!newCourse.file
})

// Modal title
const modalTitle = computed(() => {
  if (viewingTask.value) {
    if (viewingTask.value.status === 'searching') return 'AI 正在搜索课件...'
    if (viewingTask.value.status === 'completed') return 'AI 搜索结果'
    if (viewingTask.value.status === 'failed') return 'AI 搜索失败'
    if (viewingTask.value.status === 'satisfied') return '确认课件 - 准备创建课程'
  }
  return '创建新课程'
})

/** 打开新课程创建弹窗 */
function openNewCourseModal() {
  viewingTaskId.value = null
  coursewareSource.value = 'upload'
  searchPrompt.value = ''
  newCourse.name = ''
  newCourse.hours = 6
  newCourse.score = 3
  newCourse.file = null
  showCreateCourse.value = true
}

/** 打开已有任务的弹窗 */
function openTaskModal(taskId: string) {
  const task = aiStore.getTask(taskId)
  if (!task) return

  viewingTaskId.value = taskId
  aiStore.setViewing(taskId, true)

  // 预填课程信息
  newCourse.name = task.courseName
  newCourse.hours = task.courseHours
  newCourse.score = task.courseScore
  newCourse.file = null
  coursewareSource.value = 'ai-search'
  searchPrompt.value = task.searchPrompt

  showCreateCourse.value = true
}

/** 关闭弹窗 */
function handleModalCancel() {
  if (viewingTaskId.value) {
    aiStore.setViewing(viewingTaskId.value, false)
    // 记录为手动关闭，防止 watcher 立即重新弹窗
    manuallyClosedTaskIds.add(viewingTaskId.value)
  }
  showCreateCourse.value = false
}

/** AI 搜索并关闭弹窗 */
async function handleSearchAndClose() {
  if (!searchPrompt.value.trim()) { message.warning('请输入搜索提示词'); return }
  if (!newCourse.name) { message.warning('请先输入课程名称'); return }

  searchingCourseware.value = true
  try {
    await aiStore.startSearch({
      courseName: newCourse.name,
      courseHours: newCourse.hours,
      courseScore: newCourse.score,
      searchPrompt: searchPrompt.value,
      teacherUsername: resolveTeacherUsername(),
    })

    message.success('已开始后台 AI 搜索，课程已添加到列表，搜索完成后将自动通知你')

    // 关闭弹窗
    showCreateCourse.value = false
    // 重置表单
    newCourse.name = ''
    newCourse.hours = 6
    newCourse.score = 3
    newCourse.file = null
    coursewareSource.value = 'upload'
    searchPrompt.value = ''
  } catch (e: any) {
    message.error('启动 AI 搜索失败: ' + (e.message || '未知错误'))
  }
  searchingCourseware.value = false
}

/** 满意当前搜索结果 */
function handleSatisfiedTask() {
  if (!viewingTaskId.value) return
  aiStore.markSatisfied(viewingTaskId.value)
  message.success('课件已确认！请点击下方按钮完成课程创建')
}

/** 重新搜索 */
function handleRetrySearchTask() {
  if (!viewingTaskId.value) return
  aiStore.retrySearch(viewingTaskId.value)
  message.info('正在重新搜索，请稍候...')
}

/** 取消任务 */
function handleCancelTask() {
  if (!viewingTaskId.value) return
  aiStore.removeTask(viewingTaskId.value)
  viewingTaskId.value = null
  showCreateCourse.value = false
}

/** 从卡片删除任务 */
function handleDeleteTask(taskId: string) {
  aiStore.removeTask(taskId)
  manuallyClosedTaskIds.delete(taskId)
  if (viewingTaskId.value === taskId) {
    viewingTaskId.value = null
    showCreateCourse.value = false
  }
  message.info('已删除搜索任务')
}

/** 从任务创建课程 */
async function handleCreateFromTask() {
  const task = viewingTask.value
  if (!task) return
  if (task.status !== 'satisfied') {
    message.warning('请先确认 AI 生成的课件内容是否满意')
    return
  }

  creating.value = true
  try {
    const formData = new FormData()
    formData.append('course_name', task.courseName)
    formData.append('teacher_username', task.teacherUsername)
    formData.append('course_hours', String(task.courseHours))
    formData.append('course_score', String(task.courseScore))

    const blob = new Blob([task.result], { type: 'text/markdown' })
    const safeName = task.courseName.replace(/[\\/:*?"<>|]/g, '_')
    formData.append('file', blob, safeName + '_AI课件.md')

    const axios = (await import('../../utils/axios')).default
    const res = await axios.post('/teacher/ai/addCourse', formData, { timeout: 180000 })
    if (res.data?.code === 200) {
      message.success('课程创建成功')
      // 手动添加到列表（即时显示，不依赖接口刷新）
      courses.value.unshift({
        course_id: Date.now(), // 临时 id，后台静默刷新后会替换为真实数据
        course_name: task.courseName,
        course_invite_code: res.data.data || '',
        course_hours: String(task.courseHours),
        course_score: String(task.courseScore),
      })
      stats.courses++
      aiStore.removeTask(task.taskId)
      viewingTaskId.value = null
      showCreateCourse.value = false
      newCourse.name = ''; newCourse.hours = 6; newCourse.score = 3; newCourse.file = null
      // 后台静默刷新保证数据一致
      refreshTeacherData()
    } else {
      message.error(res.data?.msg || '创建失败')
    }
  } catch { message.error('创建失败，请重试') }
  creating.value = false
}

function handleFileSelect(e: Event) {
  const target = e.target as HTMLInputElement
  newCourse.file = target.files?.[0] || null
}

function selectCourse(course: any) {
  selectedCourseName.value = course.course_name
}

/** 课件管理面板中上传课件 */
function uploadCoursewareForSelected() {
  if (!coursewareCourse.value) return
  sessionStorage.setItem('courseware_preset_course', JSON.stringify({ courseName: coursewareCourse.value }))
  router.push('/teacher/courseware-publisher')
}

/** 普通创建课程（上传文件模式） */
async function handleCreateCourse() {
  if (!newCourse.name) { message.warning('请输入课程名称'); return }
  creating.value = true
  try {
    const formData = new FormData()
    formData.append('course_name', newCourse.name)
    const username = resolveTeacherUsername()
    if (!username) { message.error('无法获取教师身份信息，请重新登录'); return }
    formData.append('teacher_username', username)
    formData.append('course_hours', String(newCourse.hours))
    formData.append('course_score', String(newCourse.score))
    if (newCourse.file) {
      formData.append('file', newCourse.file)
    }
    const axios = (await import('../../utils/axios')).default
    const res = await axios.post('/teacher/ai/addCourse', formData, { timeout: 180000 })
    if (res.data?.code === 200) {
      message.success('课程创建成功')
      // 手动添加到列表（即时显示，不依赖接口刷新）
      courses.value.unshift({
        course_id: Date.now(),
        course_name: newCourse.name,
        course_invite_code: res.data.data || '',
        course_hours: String(newCourse.hours),
        course_score: String(newCourse.score),
      })
      stats.courses++
      showCreateCourse.value = false
      newCourse.name = ''; newCourse.hours = 6; newCourse.score = 3; newCourse.file = null
      // 后台静默刷新保证数据一致
      refreshTeacherData()
    }
  } catch { message.error('创建失败') }
  creating.value = false
}

function copyInviteCode(code: string) {
  navigator.clipboard.writeText(code).then(() => {
    message.success('邀请码已复制')
  }).catch(() => {
    message.error('复制失败')
  })
}

async function handleDeleteCourse(course: any) {
  try {
    const res = await deleteCourseApi({ data: course.course_name, teacher_username: teacherUsername.value })
    if (res.data?.code === 200) { message.success('已删除'); courses.value = courses.value.filter(c => c.course_id !== course.course_id); stats.courses-- }
  } catch { message.error('删除失败') }
}

// ===== AI Tools =====
const aiActiveTab = ref('knowledge')
const aiSubTabs = [
  { key: 'knowledge', label: '知识梳理' },
  { key: 'practice', label: '实训设计' },
  { key: 'questions', label: '智能出题' },
]
const selectedCourseName = ref('')

/** 课程选择弹窗状态：'' = 隐藏, 'ai-tools' | 'courseware' | 'analytics' = 对应面板显示中 */
const showCoursePicker = ref('')

// ===== 课件管理 =====
interface CoursewareItem {
  id: string; courseName: string; title: string; description: string
  fileName: string; fileSize: number; fileType: string; fileDataUrl: string; createdAt: number
}

// ===== 实训计划 =====
interface TrainingPlanSection {
  name: string; content: string
}
interface TrainingPlanItem {
  id: string; courseName: string; title: string; description: string
  templateKey: string; sections: TrainingPlanSection[]
  createdAt: number; updatedAt: number
}
interface TrainingPlanTemplate {
  key: string; label: string
  sections: { name: string; placeholder: string }[]
}
const TRAINING_PLAN_TEMPLATES: TrainingPlanTemplate[] = [
  {
    key: 'standard', label: '标准实训方案',
    sections: [
      { name: '实训目标', placeholder: '描述本次实训要达成的教学目标...' },
      { name: '实训内容', placeholder: '详细描述实训的具体内容和步骤...' },
      { name: '所需资源', placeholder: '列出需要的硬件、软件、资料等...' },
      { name: '时间安排', placeholder: '各阶段的时间分配...' },
      { name: '考核方式', placeholder: '实训的考核标准和评分方式...' },
      { name: '注意事项', placeholder: '安全和操作注意事项...' },
    ],
  },
  {
    key: 'project', label: '项目式实训方案',
    sections: [
      { name: '项目背景', placeholder: '描述项目的背景和实际应用场景...' },
      { name: '项目需求', placeholder: '明确项目的功能和技术需求...' },
      { name: '技术栈', placeholder: '列出项目使用的技术栈和工具...' },
      { name: '实施步骤', placeholder: '分阶段描述实施步骤...' },
      { name: '交付物', placeholder: '明确学生需要提交的成果...' },
      { name: '评分标准', placeholder: '各项交付物的评分权重和标准...' },
    ],
  },
  {
    key: 'week', label: '周实训计划',
    sections: [
      { name: '周一安排', placeholder: '周一的实训内容和目标...' },
      { name: '周二安排', placeholder: '周二的实训内容和目标...' },
      { name: '周三安排', placeholder: '周三的实训内容和目标...' },
      { name: '周四安排', placeholder: '周四的实训内容和目标...' },
      { name: '周五安排', placeholder: '周五的实训内容和目标...' },
      { name: '周末任务', placeholder: '周末的自主练习任务...' },
    ],
  },
]
const TRAINING_PLAN_STORAGE_KEY = 'teacher_training_plans'

const coursewareCourse = ref('')
const coursewareList = ref<CoursewareItem[]>([])

// 课件命名弹窗
const showCwNameModal = ref(false)
const cwNameInput = ref('')
const cwNamePendingType = ref('')

// 课件查看弹窗
const showCwViewModal = ref(false)
const viewingCoursewareItem = ref<CoursewareItem | null>(null)

// 课件导出格式选择
const showCwExportModal = ref(false)
const exportingCoursewareItem = ref<CoursewareItem | null>(null)
const cwExportFormat = ref('md')
const cwExportFormats = [
  { key: 'doc', label: 'Word (.doc)', icon: '📄' },
  { key: 'docx', label: 'Word (.docx)', icon: '📝' },
  { key: 'txt', label: '纯文本 (.txt)', icon: '📃' },
  { key: 'md', label: 'Markdown (.md)', icon: '📋' },
]

// 实训计划状态
const trainingPlanList = ref<TrainingPlanItem[]>([])
const activeTpView = ref<'list' | 'form' | 'detail'>('list')
const editingTrainingPlan = ref<TrainingPlanItem | null>(null)
const tpForm = reactive({
  courseName: '',
  title: '',
  description: '',
  templateKey: 'standard',
  sections: [] as TrainingPlanSection[],
})
const viewingTrainingPlan = ref<TrainingPlanItem | null>(null)
const showTpExportModal = ref(false)
const exportingTrainingPlan = ref<TrainingPlanItem | null>(null)
const tpExportFormat = ref('md')

function loadCoursewareList() {
  try { coursewareList.value = JSON.parse(localStorage.getItem('teacher_courseware_list') || '[]') } catch { coursewareList.value = [] }
}

const filteredCourseware = computed(() =>
  coursewareList.value.filter(cw => cw.courseName === coursewareCourse.value)
)

function formatFileSize(bytes: number): string {
  if (!bytes) return '0 B'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

/** 在线查看课件内容 */
function viewCourseware(cw: CoursewareItem) {
  viewingCoursewareItem.value = cw
  showCwViewModal.value = true
}

/** 将 markdown 转为简单 HTML */
function markdownToSimpleHtml(md: string): string {
  let html = md
    // Headers
    .replace(/^### (.+)$/gm, '<h3>$1</h3>')
    .replace(/^## (.+)$/gm, '<h2>$1</h2>')
    .replace(/^# (.+)$/gm, '<h1>$1</h1>')
    // Bold & italic
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.+?)\*/g, '<em>$1</em>')
    // Inline code
    .replace(/`(.+?)`/g, '<code>$1</code>')
    // Links
    .replace(/\[(.+?)\]\((.+?)\)/g, '<a href="$2">$1</a>')
    // Unordered lists
    .replace(/^- (.+)$/gm, '<li>$1</li>')
    // Paragraphs (double newlines)
    .replace(/\n\n/g, '</p><p>')
    // Single newlines to <br>
    .replace(/\n/g, '<br/>')
  return `<html><head><meta charset="utf-8"><style>body{font-family:'Segoe UI',sans-serif;line-height:1.8;max-width:800px;margin:40px auto;padding:0 20px;color:#1a1a2e}h1{font-size:24px;border-bottom:2px solid #4F46E5;padding-bottom:8px}h2{font-size:20px}h3{font-size:16px}code{background:#f1f5f9;padding:2px 6px;border-radius:4px;font-size:14px}li{margin:4px 0}</style></head><body><p>${html}</p></body></html>`
}

/** 导出课件 — 弹出格式选择 */
function openExportModal(cw: CoursewareItem) {
  exportingCoursewareItem.value = cw
  cwExportFormat.value = 'md'
  showCwExportModal.value = true
}

/** 确认导出课件 */
function confirmExportCourseware() {
  const cw = exportingCoursewareItem.value
  if (!cw) return

  // Decode content from data URL
  const base64 = cw.fileDataUrl.split(',')[1]
  const content = decodeURIComponent(escape(atob(base64)))

  const format = cwExportFormat.value
  let blob: Blob
  let fileName: string

  switch (format) {
    case 'doc':
      blob = new Blob([markdownToSimpleHtml(content)], { type: 'application/msword' })
      fileName = `${cw.title}.doc`
      break
    case 'docx':
      blob = new Blob([markdownToSimpleHtml(content)], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' })
      fileName = `${cw.title}.docx`
      break
    case 'txt':
      // Strip markdown syntax for plain text
      blob = new Blob([content.replace(/[#*`\-\[\]()]/g, '')], { type: 'text/plain;charset=utf-8' })
      fileName = `${cw.title}.txt`
      break
    case 'md':
    default:
      blob = new Blob([content], { type: 'text/markdown;charset=utf-8' })
      fileName = `${cw.title}.md`
      break
  }

  const a = document.createElement('a')
  a.href = URL.createObjectURL(blob)
  a.download = fileName
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(a.href)

  showCwExportModal.value = false
  message.success(`已导出：${fileName}`)
}

/** 解码课件的 base64 data URL 为文本内容 */
function decodeCoursewareContent(cw: CoursewareItem): string {
  try {
    const base64 = cw.fileDataUrl.split(',')[1]
    return decodeURIComponent(escape(atob(base64)))
  } catch { return '' }
}

function deleteCourseware(id: string) {
  coursewareList.value = coursewareList.value.filter(cw => cw.id !== id)
  try {
    localStorage.setItem('teacher_courseware_list', JSON.stringify(coursewareList.value))
  } catch { /* ignore */ }
  message.success('已删除')
}
const cwSelectedIds = ref<Set<string>>(new Set())
const cwAllSelected = computed(() => filteredCourseware.value.length > 0 && filteredCourseware.value.every((c: any) => cwSelectedIds.value.has(c.id)))
function toggleCwSelectAll() { cwSelectedIds.value = cwAllSelected.value ? new Set() : new Set(filteredCourseware.value.map((c: any) => c.id)) }
function toggleCwSelect(id: string) { const n = new Set(cwSelectedIds.value); n.has(id) ? n.delete(id) : n.add(id); cwSelectedIds.value = n }
function deleteSelectedCw() {
  if (cwSelectedIds.value.size === 0) { message.warning('请先选择要删除的课件'); return }
  const count = cwSelectedIds.value.size
  coursewareList.value = coursewareList.value.filter((cw: any) => !cwSelectedIds.value.has(cw.id))
  try { localStorage.setItem('teacher_courseware_list', JSON.stringify(coursewareList.value)) } catch {}
  cwSelectedIds.value = new Set()
  message.success(`已删除 ${count} 个课件`)
}

// ===== 实训计划函数 =====

function loadTrainingPlans() {
  try { trainingPlanList.value = JSON.parse(localStorage.getItem(TRAINING_PLAN_STORAGE_KEY) || '[]') } catch { trainingPlanList.value = [] }
}

function saveTrainingPlans() {
  try { localStorage.setItem(TRAINING_PLAN_STORAGE_KEY, JSON.stringify(trainingPlanList.value)) } catch { /* ignore */ }
}

function getTemplateLabel(key: string): string {
  return TRAINING_PLAN_TEMPLATES.find(t => t.key === key)?.label || key
}

function getSectionPlaceholder(name: string): string {
  for (const tpl of TRAINING_PLAN_TEMPLATES) {
    const s = tpl.sections.find(s => s.name === name)
    if (s) return s.placeholder
  }
  return '请输入内容...'
}

function onTemplateChange(key: string) {
  const tpl = TRAINING_PLAN_TEMPLATES.find(t => t.key === key)
  if (tpl) {
    tpForm.sections = tpl.sections.map(s => ({ name: s.name, content: '' }))
  }
}

function resetTpView() {
  // 离开表单前保存草稿（下次进入可恢复）
  if (activeTpView.value === 'form' && (tpForm.courseName || tpForm.title)) {
    sessionStorage.setItem('tp_form_draft', JSON.stringify({
      courseName: tpForm.courseName,
      title: tpForm.title,
      description: tpForm.description,
      templateKey: tpForm.templateKey,
      sections: tpForm.sections,
      editingId: editingTrainingPlan.value?.id || null,
    }))
  }
  activeTpView.value = 'list'
  editingTrainingPlan.value = null
  viewingTrainingPlan.value = null
  tpForm.courseName = ''
  tpForm.title = ''
  tpForm.description = ''
  tpForm.templateKey = 'standard'
  tpForm.sections = []
  sessionStorage.removeItem('tp_subview')
  sessionStorage.removeItem('tp_detail_id')
}

function openTrainingPlanForm(tp?: TrainingPlanItem) {
  if (tp) {
    // 编辑已有计划
    editingTrainingPlan.value = tp
    tpForm.courseName = tp.courseName
    tpForm.title = tp.title
    tpForm.description = tp.description
    tpForm.templateKey = tp.templateKey
    tpForm.sections = tp.sections.map(s => ({ ...s }))
  } else {
    // 新建：尝试恢复草稿
    const draft = sessionStorage.getItem('tp_form_draft')
    if (draft) {
      try {
        const d = JSON.parse(draft)
        if (d.courseName || d.title) {
          editingTrainingPlan.value = null
          tpForm.courseName = d.courseName || ''
          tpForm.title = d.title || ''
          tpForm.description = d.description || ''
          tpForm.templateKey = d.templateKey || 'standard'
          tpForm.sections = (d.sections && d.sections.length > 0) ? d.sections : []
          if (tpForm.sections.length === 0) onTemplateChange(tpForm.templateKey)
          if (d.editingId) {
            const found = trainingPlanList.value.find(t => t.id === d.editingId)
            if (found) editingTrainingPlan.value = found
          }
          activeTpView.value = 'form'
          return
        }
      } catch { /* ignore */ }
    }
    editingTrainingPlan.value = null
    tpForm.courseName = ''
    tpForm.title = ''
    tpForm.description = ''
    tpForm.templateKey = 'standard'
    onTemplateChange('standard')
  }
  activeTpView.value = 'form'
}

function clearTpForm() {
  editingTrainingPlan.value = null
  tpForm.courseName = ''
  tpForm.title = ''
  tpForm.description = ''
  tpForm.templateKey = 'standard'
  tpForm.sections = []
  sessionStorage.removeItem('tp_form_draft')
  message.success('已清空表单内容')
}

function confirmTrainingPlan() {
  if (!tpForm.courseName || !tpForm.title || !tpForm.templateKey) {
    message.warning('请填写课程、标题和模板')
    return
  }

  if (editingTrainingPlan.value) {
    const idx = trainingPlanList.value.findIndex(tp => tp.id === editingTrainingPlan.value!.id)
    if (idx !== -1) {
      trainingPlanList.value[idx] = {
        ...trainingPlanList.value[idx],
        title: tpForm.title,
        description: tpForm.description,
        templateKey: tpForm.templateKey,
        sections: tpForm.sections.filter(s => s.content.trim()),
        updatedAt: Date.now(),
      }
    }
    message.success('实训计划已更新')
  } else {
    const item: TrainingPlanItem = {
      id: 'tp_' + Date.now(),
      courseName: tpForm.courseName,
      title: tpForm.title,
      description: tpForm.description,
      templateKey: tpForm.templateKey,
      sections: tpForm.sections.filter(s => s.content.trim()),
      createdAt: Date.now(),
      updatedAt: Date.now(),
    }
    trainingPlanList.value.unshift(item)
    message.success('实训计划已创建')
  }

  saveTrainingPlans()
  // 保存成功，先清空表单再清除草稿，防止 resetTpView 重新保存
  editingTrainingPlan.value = null
  tpForm.courseName = ''
  tpForm.title = ''
  tpForm.description = ''
  tpForm.templateKey = 'standard'
  tpForm.sections = []
  sessionStorage.removeItem('tp_form_draft')
  resetTpView()
}

function viewTrainingPlan(tp: TrainingPlanItem) {
  viewingTrainingPlan.value = tp
  activeTpView.value = 'detail'
}

function deleteTrainingPlan(id: string) {
  trainingPlanList.value = trainingPlanList.value.filter(tp => tp.id !== id)
  saveTrainingPlans()
  message.success('已删除')
}
const tpSelectedIds = ref<Set<string>>(new Set())
const tpAllSelected = computed(() => trainingPlanList.value.length > 0 && trainingPlanList.value.every(tp => tpSelectedIds.value.has(tp.id)))
function toggleTpSelectAll() { tpSelectedIds.value = tpAllSelected.value ? new Set() : new Set(trainingPlanList.value.map(tp => tp.id)) }
function toggleTpSelect(id: string) { const n = new Set(tpSelectedIds.value); n.has(id) ? n.delete(id) : n.add(id); tpSelectedIds.value = n }
function deleteSelectedTp() {
  if (tpSelectedIds.value.size === 0) { message.warning('请先选择要删除的实训计划'); return }
  const count = tpSelectedIds.value.size
  trainingPlanList.value = trainingPlanList.value.filter(tp => !tpSelectedIds.value.has(tp.id))
  saveTrainingPlans()
  tpSelectedIds.value = new Set()
  message.success(`已删除 ${count} 项实训计划`)
}

/** 格式化实训计划为 Markdown */
function formatTrainingPlanToMarkdown(tp: TrainingPlanItem | null): string {
  if (!tp) return ''
  let md = `# ${tp.title}\n\n`
  if (tp.description) md += `> ${tp.description}\n\n`
  md += `**课程**: ${tp.courseName} | **模板**: ${getTemplateLabel(tp.templateKey)} | **更新**: ${formatTime(tp.updatedAt)}\n\n---\n\n`
  for (const sec of tp.sections) {
    if (sec.content.trim()) {
      md += `## ${sec.name}\n\n${sec.content}\n\n`
    }
  }
  return md
}

/** 导出实训计划 — 打开格式选择 */
function openTpExportModal(tp: TrainingPlanItem) {
  exportingTrainingPlan.value = tp
  tpExportFormat.value = 'md'
  showTpExportModal.value = true
}

/** 确认导出实训计划 */
function confirmTpExport() {
  const tp = exportingTrainingPlan.value
  if (!tp) return

  const content = formatTrainingPlanToMarkdown(tp)
  const format = tpExportFormat.value
  let blob: Blob
  let fileName: string

  switch (format) {
    case 'doc':
      blob = new Blob([markdownToSimpleHtml(content)], { type: 'application/msword' })
      fileName = `${tp.title}.doc`
      break
    case 'docx':
      blob = new Blob([markdownToSimpleHtml(content)], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' })
      fileName = `${tp.title}.docx`
      break
    case 'txt':
      blob = new Blob([content.replace(/[#*`\-\[\]()>]/g, '')], { type: 'text/plain;charset=utf-8' })
      fileName = `${tp.title}.txt`
      break
    case 'md':
    default:
      blob = new Blob([content], { type: 'text/markdown;charset=utf-8' })
      fileName = `${tp.title}.md`
      break
  }

  const a = document.createElement('a')
  a.href = URL.createObjectURL(blob)
  a.download = fileName
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(a.href)

  showTpExportModal.value = false
  message.success(`已导出：${fileName}`)
}

// 初始加载
loadTrainingPlans()

// 监听课件管理 tab 激活时刷新列表
watch(() => activeTab.value, (tab) => {
  sessionStorage.setItem('teacher_active_tab', tab)
  window.dispatchEvent(new CustomEvent('sync-sidebar', { detail: tab }))
  if (tab === 'courseware') loadCoursewareList()
  if (tab === 'practical-training') loadTrainingPlans()
  if (tab === 'attendance') { loadAttendanceListFromApi(); loadStudentList() }
  if (tab === 'students') loadStudentList()
  if (tab === 'grading' && gradingCourse.value) loadGradeSummary()
  if (tab === 'discussions') discussionTopics.value = loadDiscussions()
})
const aiInputs = reactive({ knowledge: '', practice: '', questions: '', issueName: '', startTime: '', endTime: '', choiceCount: 5, fillBlankCount: 3, trueFalseCount: 5, essayCount: 2, calculationCount: 2 })
const totalQuestionCount = computed(() => aiInputs.choiceCount + aiInputs.fillBlankCount + aiInputs.trueFalseCount + aiInputs.essayCount + aiInputs.calculationCount)
const questionCountExceeded = computed(() => totalQuestionCount.value > 100)
const essayCountExceeded = computed(() => aiInputs.essayCount > 20)
const calculationCountExceeded = computed(() => aiInputs.calculationCount > 20)
const aiLoading = reactive({ knowledge: false, practice: false, questions: false })
const aiResults = reactive({ knowledge: '', practice: '', questions: '' })

/** 从 AI 实训设计导入到实训计划 */
function importFromPracticeAI() {
  const content = aiResults.practice
  if (!content) { message.warning('请先生成实训方案'); return }
  if (!selectedCourseName.value) { message.warning('请先选择课程'); return }

  // 按 ## 标题拆分为各板块
  const sections: TrainingPlanSection[] = []
  const lines = content.split('\n')
  let currentName = ''
  let currentContent: string[] = []

  for (const line of lines) {
    if (/^##\s+(.+)/.test(line)) {
      if (currentName) {
        sections.push({ name: currentName, content: currentContent.join('\n').trim() })
      }
      currentName = line.replace(/^##\s+/, '')
      currentContent = []
    } else if (currentName) {
      currentContent.push(line)
    }
  }
  if (currentName) {
    sections.push({ name: currentName, content: currentContent.join('\n').trim() })
  }

  // 如果没有解析出板块，整段作为一个板块
  if (sections.length === 0) {
    sections.push({ name: '实训内容', content })
  }

  // 打开新建表单并预填
  editingTrainingPlan.value = null
  tpForm.courseName = selectedCourseName.value
  tpForm.title = `${selectedCourseName.value}_实训计划_${new Date().toLocaleDateString('zh-CN')}`
  tpForm.description = 'AI 实训设计自动生成'
  tpForm.templateKey = 'standard'
  tpForm.sections = sections
  activeTpView.value = 'form'

  // 自动跳转到实训计划面板
  activeTab.value = 'practical-training'
  window.dispatchEvent(new CustomEvent('sync-sidebar', { detail: 'practical-training' }))
}

async function runAI(type: string) {
  const courseName = selectedCourseName.value
  const username = teacherUsername.value
  if (!courseName) { message.warning('请先选择课程'); return }
  if (type === 'questions') {
    if (totalQuestionCount.value > 100) { message.warning('出题总数不能超过 100 道'); return }
    if (totalQuestionCount.value === 0) { message.warning('请至少设置 1 道题目'); return }
    if (aiInputs.essayCount > 20) { message.warning('应用题最多 20 道'); return }
    if (aiInputs.calculationCount > 20) { message.warning('计算题最多 20 道'); return }
  }

  const loadKey = type as keyof typeof aiLoading
  const resultKey = type as keyof typeof aiResults
  aiLoading[loadKey] = true

  try {
    const params: any = { data: courseName, teacher_username: username }
    let apiFn: any

    switch (type) {
      case 'knowledge':
        params.input = aiInputs.knowledge
        apiFn = getCozeKnowledgeOrganizationApi
        break
      case 'practice':
        params.input = aiInputs.practice
        apiFn = getCozePractiseDesignApi
        break
      case 'questions':
        // 构建严格出题数量指令
        const countParts: string[] = []
        if (aiInputs.choiceCount > 0) countParts.push(`选择题 ${aiInputs.choiceCount} 道`)
        if (aiInputs.fillBlankCount > 0) countParts.push(`填空题 ${aiInputs.fillBlankCount} 道`)
        if (aiInputs.trueFalseCount > 0) countParts.push(`判断题 ${aiInputs.trueFalseCount} 道`)
        if (aiInputs.essayCount > 0) countParts.push(`应用题 ${aiInputs.essayCount} 道`)
        if (aiInputs.calculationCount > 0) countParts.push(`计算题 ${aiInputs.calculationCount} 道`)
        const countInstruction = countParts.length > 0
          ? `\n\n【出题数量严格要求 — 必须严格遵守】\n请严格按照以下数量和题型出题，每种题型只能出指定数量，不能多出也不能少出：${countParts.join('、')}。\n总计 ${countParts.reduce((s, p) => s + parseInt(p.match(/\d+/)?.[0] || '0'), 0)} 道题。请确保题目数量精确匹配以上要求。\n\n【题型说明 — 请仔细区分】\n- 选择题：提供4个选项（A/B/C/D）的单选题\n- 填空题：在题目中留空（用____表示），要求填写关键词或数值\n- 判断题：判断陈述是否正确（正确/错误）\n- 应用题：需要文字分析、论述、解释或方案设计的主观题，考察理解与应用能力（如：请分析某算法的优缺点、请设计一个系统架构、请解释某原理）\n- 计算题：需要进行数学计算、公式推导、数值求解的题目，有明确的数值答案（如：求导、积分、解方程、计算复杂度）\n\n重要：应用题和计算题是两种完全不同的题型。应用题侧重文字论述和分析，不需要数值计算。计算题侧重数学运算和公式推导，必须给出数值结果。请严格区分，不要混用。\n\n【输出格式严格要求 — 必须严格遵守以下 Markdown 格式】\n请严格按照以下格式输出，系统将自动解析你的输出提取题目：\n\n## 选择题\n\n**1.** <题干内容>\nA. <选项A>\nB. <选项B>\nC. <选项C>\nD. <选项D>\n答案：A\n\n**2.** <题干内容>\n...（每题之间空一行）\n\n## 填空题\n\n**1.** <题干内容，留空处用____表示>\n答案：<正确答案>\n\n## 判断题\n\n**1.** <陈述内容>\n答案：正确\n\n## 应用题\n\n**1.** <题目内容>\n答案：<参考答案>\n\n## 计算题\n\n**1.** <题目内容>\n答案：<数值答案>\n\n格式要点：\n- 每个题型的标题必须用「## 题型名」开头（如 ## 选择题、## 填空题、## 判断题、## 应用题、## 计算题）\n- 每道题用「**序号.**」开头（如 **1.** 这是题目）\n- 选择题每个选项单独一行，格式为「A. 选项内容」\n- 每道题的答案紧跟在题目选项之后，格式为「答案：<答案内容>」\n- 如果不需要出某种题型，就省略该题型标题及其下所有题目\n- 不要输出多余的解释、总结或分析段落，只输出上述结构化的题目内容`
          : ''
        params.input = (aiInputs.questions || '无特殊偏重要求') + countInstruction
        params.issue_name = aiInputs.issueName || '综合练习'
        params.start_time = aiInputs.startTime
        params.end_time = aiInputs.endTime
        apiFn = getCozeDesignIssueApi
        break
      case 'time':
        apiFn = getCozeArrangeTimeApi
        break
    }

    const res = await apiFn(params)
    const R: R = res.data
    if (R.code === 200 && R.data?.[0]) {
      const d = R.data[0]
      const content = d.concludeData || d.practise || d.decideAndAnswer || d.arrangeTime || d.arrangeTimeSuggestion || JSON.stringify(d)
      aiResults[resultKey] = content
    } else {
      message.error(R.msg || 'AI 生成失败')
    }
  } catch { message.error('请求超时或失败') }
  aiLoading[loadKey] = false
}

function copyResult(text: string) {
  navigator.clipboard.writeText(text).then(() => message.success('已复制')).catch(() => message.error('复制失败'))
}

const COURSEWARE_LABELS: Record<string, string> = {
  knowledge: '知识梳理',
  practice: '实训设计',
}

/** 将 AI 生成内容保存为课件 — 先弹窗让用户命名 */
function saveToCourseware(type: string) {
  const content = aiResults[type as keyof typeof aiResults]
  if (!content) { message.warning('请先生成内容'); return }
  if (!selectedCourseName.value) { message.warning('请先选择课程'); return }

  const label = COURSEWARE_LABELS[type] || type
  cwNameInput.value = `${selectedCourseName.value}_${label}_${new Date().toLocaleDateString('zh-CN')}`
  cwNamePendingType.value = type
  showCwNameModal.value = true
}

/** 确认保存课件（用户已命名或使用默认名） */
function confirmSaveToCourseware() {
  const type = cwNamePendingType.value
  const content = aiResults[type as keyof typeof aiResults]
  if (!content) return

  const title = cwNameInput.value.trim() || `${selectedCourseName.value}_${COURSEWARE_LABELS[type] || type}_${new Date().toLocaleDateString('zh-CN')}`
  const blob = new Blob([content], { type: 'text/markdown;charset=utf-8' })
  const reader = new FileReader()
  reader.onload = () => {
    const item = {
      id: 'cw_' + Date.now(),
      courseName: selectedCourseName.value,
      title,
      description: `AI 生成的${COURSEWARE_LABELS[type] || type}`,
      fileName: `${title}.md`,
      fileSize: blob.size,
      fileType: 'md',
      fileDataUrl: reader.result as string,
      createdAt: Date.now(),
    }
    try {
      const list = JSON.parse(localStorage.getItem('teacher_courseware_list') || '[]')
      list.unshift(item)
      localStorage.setItem('teacher_courseware_list', JSON.stringify(list))
      // 刷新课件列表（如果当前在课件管理页面）
      loadCoursewareList()
      showCwNameModal.value = false
      message.success(`已添加到课件：${title}`)
    } catch { message.error('保存课件失败') }
  }
  reader.readAsDataURL(blob)
}

/** 格式化时间戳为可读字符串 */
function formatTime(ts: number): string {
  if (!ts) return '—'
  const d = new Date(ts)
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

/** 从智能出题导入题目到作业/考试（跳转到发布页面） */
function importQuestionsTo(target: 'homework' | 'exam') {
  if (!aiResults.questions) { message.warning('请先生成题目'); return }
  if (!selectedCourseName.value) { message.warning('请先选择课程'); return }

  const parsedQuestions = parseQuestionsFromMarkdown(aiResults.questions)

  const importData: PendingImportData = {
    target,
    courseName: selectedCourseName.value,
    title: aiInputs.issueName || (selectedCourseName.value + (target === 'homework' ? ' 课后作业' : ' 考试')),
    rawMarkdown: aiResults.questions,
    parsedQuestions,
    ...(target === 'exam' ? {
      startTime: aiInputs.startTime || '',
      endTime: aiInputs.endTime || '',
    } : {}),
  }

  sessionStorage.setItem('pending_import', JSON.stringify(importData))

  // 保存 AI 出题状态，返回时可恢复
  sessionStorage.setItem('ai_questions_state', JSON.stringify({
    result: aiResults.questions,
    inputs: { ...aiInputs },
    courseName: selectedCourseName.value,
    activeTab: aiActiveTab.value,
  }))

  // 同步侧边栏高亮到目标页面
  window.dispatchEvent(new CustomEvent('sync-sidebar', { detail: target }))

  router.push(target === 'homework'
    ? '/teacher/homework-publisher'
    : '/teacher/exam-publisher')

  message.success(`题目已导入（${parsedQuestions.length} 题），请在发布页面中确认和完善`)
}

/** 从智能出题导入题目到题库积累 */
function importQuestionsToBank() {
  if (!aiResults.questions) { message.warning('请先生成题目'); return }
  if (!selectedCourseName.value) { message.warning('请先选择课程'); return }

  const parsedQuestions = parseQuestionsFromMarkdown(aiResults.questions)
  if (parsedQuestions.length === 0) {
    message.warning('未能解析出有效题目，请检查 AI 输出格式')
    return
  }

  const bankItems: QuestionBankItem[] = parsedQuestions.map(q => {
    let correctAnswer = ''
    const options: string[] = []

    if (q.type === 'choice') {
      q.options.forEach(o => options.push(`${o.key}. ${o.text}`))
      correctAnswer = q.correctAnswer
    } else if (q.type === 'true-false') {
      correctAnswer = q.correctAnswer ? '正确' : '错误'
    } else if (q.type === 'essay') {
      correctAnswer = q.referenceAnswer || ''
    } else {
      correctAnswer = q.correctAnswer || ''
    }

    // 提取题目描述内容（title 中包含的代码块等附加内容）
    const titleLines = q.title.split('\n')
    const mainTitle = titleLines[0] || q.title
    const extraContent = titleLines.length > 1 ? titleLines.slice(1).join('\n') : ''

    return {
      id: 'qb_' + Date.now() + '_' + Math.random().toString(36).slice(2, 6),
      type: q.type,
      title: mainTitle,
      options,
      correctAnswer,
      difficulty: 3,
      tags: [],
      courseName: selectedCourseName.value,
      createdAt: Date.now(),
      content: extraContent || undefined,
    }
  })

  questionBank.value = [...bankItems, ...questionBank.value]
  saveQuestionBank(questionBank.value)
  message.success(`已导入题库 ${bankItems.length} 道题目`)
}

// ===== 发布作业 =====
const homeworkList = ref<HomeworkItem[]>(loadHomeworkList())

function openHomeworkForm(hw?: HomeworkItem) {
  if (hw) {
    const editData: PendingEditData = { target: 'homework', itemId: hw.id, item: hw }
    sessionStorage.setItem('pending_edit', JSON.stringify(editData))
  }
  router.push('/teacher/homework-publisher')
}

function deleteHomework(id: string) {
  homeworkList.value = homeworkList.value.filter(h => h.id !== id)
  saveHomeworkList(homeworkList.value)
  message.success('已删除')
}
// 批量选择
const hwSelectedIds = ref<Set<string>>(new Set())
const hwAllSelected = computed(() => homeworkList.value.length > 0 && homeworkList.value.every(h => hwSelectedIds.value.has(h.id)))
function toggleHwSelectAll() { hwSelectedIds.value = hwAllSelected.value ? new Set() : new Set(homeworkList.value.map(h => h.id)) }
function toggleHwSelect(id: string) { const n = new Set(hwSelectedIds.value); n.has(id) ? n.delete(id) : n.add(id); hwSelectedIds.value = n }
function deleteSelectedHw() {
  if (hwSelectedIds.value.size === 0) { message.warning('请先选择要删除的作业'); return }
  const count = hwSelectedIds.value.size
  homeworkList.value = homeworkList.value.filter(h => !hwSelectedIds.value.has(h.id))
  saveHomeworkList(homeworkList.value)
  hwSelectedIds.value = new Set()
  message.success(`已删除 ${count} 项作业`)
}

// ===== 发布考试 =====
const examList = ref<ExamItem[]>(loadExamList())

function openExamForm(ex?: ExamItem) {
  if (ex) {
    const editData: PendingEditData = { target: 'exam', itemId: ex.id, item: ex }
    sessionStorage.setItem('pending_edit', JSON.stringify(editData))
  }
  router.push('/teacher/exam-publisher')
}

function deleteExam(id: string) {
  examList.value = examList.value.filter(e => e.id !== id)
  saveExamList(examList.value)
  message.success('已删除')
}
const exSelectedIds = ref<Set<string>>(new Set())
const exAllSelected = computed(() => examList.value.length > 0 && examList.value.every(e => exSelectedIds.value.has(e.id)))
function toggleExSelectAll() { exSelectedIds.value = exAllSelected.value ? new Set() : new Set(examList.value.map(e => e.id)) }
function toggleExSelect(id: string) { const n = new Set(exSelectedIds.value); n.has(id) ? n.delete(id) : n.add(id); exSelectedIds.value = n }
function deleteSelectedEx() {
  if (exSelectedIds.value.size === 0) { message.warning('请先选择要删除的考试'); return }
  const count = exSelectedIds.value.size
  examList.value = examList.value.filter(e => !exSelectedIds.value.has(e.id))
  saveExamList(examList.value)
  exSelectedIds.value = new Set()
  message.success(`已删除 ${count} 项考试`)
}

// ===== 批改入口 =====

function openGradingPage(item: any, type: 'homework' | 'exam') {
  sessionStorage.setItem('grading_homework', JSON.stringify({ ...item, type }))
  router.push('/teacher/grading')
}

// ===== 考勤记录 =====
interface AttendanceRecord { name: string; status: 'present' | 'absent' | 'leave' }
interface AttendanceItem {
  id: string | number; courseName: string; date: string; period?: number; remark: string
  records: AttendanceRecord[]; presentCount: number; totalCount: number; createdAt?: number
}

/** 考勤表单中的学生条目 */
interface AttendanceStudentEntry {
  id: string; name: string; className: string; status: 'present' | 'absent' | 'leave'
}

// 从后端API加载考勤列表
async function loadAttendanceListFromApi() {
  try {
    const username = teacherUsername.value
    if (!username) return
    const res = await import('../../api/teacher/attendanceApi').then(m => m.listAttendanceApi({ teacher_username: username }))
    if (res.data?.code === 200) {
      const list = (res.data.data || []).map((a: any) => ({
        id: a.id,
        courseName: a.course_name,
        date: a.date,
        period: a.period,
        remark: a.remark || '',
        records: (a.records || []).map((r: any) => ({ name: r.student_name, status: r.status })),
        presentCount: a.presentCount || 0,
        totalCount: a.totalCount || 0,
      }))
      attendanceList.value = list
    }
  } catch { /* 静默处理 */ }
}
// 兼容旧 localStorage 数据的迁移
function loadAttendanceListLegacy(): AttendanceItem[] {
  try { return JSON.parse(localStorage.getItem('teacher_attendance_list') || '[]') } catch { return [] }
}

const attendanceList = ref<AttendanceItem[]>(loadAttendanceListLegacy())
const attKeyword = ref('')
const filteredAttendanceList = computed(() => {
  const term = attKeyword.value.trim().toLowerCase()
  if (!term) return attendanceList.value
  const regex = new RegExp(term.split('').map(c => c.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')).join('.*'), 'i')
  return attendanceList.value.filter(item =>
    Object.values(item).some(v => v != null && String(v).toLowerCase().match(regex))
  )
})
const activeAttendanceView = ref<'list' | 'form' | 'detail'>('list')
const viewingAttendance = ref<AttendanceItem | null>(null)
const attendanceForm = reactive({ courseName: '', date: '', period: undefined as number | undefined, remark: '' })
const attendanceStudents = ref<AttendanceStudentEntry[]>([])

const attendanceColumns = [
  { title: '课程', dataIndex: 'courseName', key: 'courseName' },
  { title: '日期', dataIndex: 'date', key: 'date' },
  { title: '备注', dataIndex: 'remark', key: 'remark' },
  { title: '出勤率', key: 'presentCount' },
  { title: '操作', key: 'actions' },
]

function searchAttendance() { /* filteredAttendanceList is reactive — just trigger a no-op to force re-render */ }
function resetAttSearch() { attKeyword.value = '' }

function resetAttendanceView() {
  // 离开表单前保存草稿（下次进入可恢复）
  if (activeAttendanceView.value === 'form' && (attendanceForm.courseName || attendanceForm.date)) {
    sessionStorage.setItem('attendance_form_draft', JSON.stringify({
      courseName: attendanceForm.courseName,
      date: attendanceForm.date,
      period: attendanceForm.period,
      remark: attendanceForm.remark,
    }))
  }
  activeAttendanceView.value = 'list'
  viewingAttendance.value = null
  sessionStorage.removeItem('attendance_subview')
  sessionStorage.removeItem('attendance_detail_id')
}

function openAttendanceForm() {
  // 尝试恢复草稿
  const draft = sessionStorage.getItem('attendance_form_draft')
  if (draft) {
    try {
      const d = JSON.parse(draft)
      if (d.courseName || d.date) {
        attendanceForm.courseName = d.courseName || ''
        attendanceForm.date = d.date || new Date().toISOString().slice(0, 10)
        attendanceForm.period = d.period
        attendanceForm.remark = d.remark || ''
        if (d.courseName) onAttendanceCourseChange()
        activeAttendanceView.value = 'form'
        return
      }
    } catch { /* ignore */ }
  }
  // 全新表单
  attendanceForm.courseName = ''; attendanceForm.date = new Date().toISOString().slice(0, 10)
  attendanceForm.period = undefined; attendanceForm.remark = ''
  attendanceStudents.value = []
  activeAttendanceView.value = 'form'
}

function clearAttendanceForm() {
  attendanceForm.courseName = ''
  attendanceForm.date = ''
  attendanceForm.period = undefined
  attendanceForm.remark = ''
  attendanceStudents.value = []
  sessionStorage.removeItem('attendance_form_draft')
  message.success('已清空表单内容')
}

/** 切换课程时加载该课程对应的学生 */
async function onAttendanceCourseChange() {
  // 使用 enrolledStudents（选课学生），并按所选课程筛选
  const courseName = attendanceForm.courseName
  // 如果 enrolledStudents 还没加载（比如直接进入考勤 tab），先加载
  if (enrolledStudents.value.length === 0 && teacherUsername.value) {
    await loadStudentList()
  }
  let studentList = enrolledStudents.value || []
  if (courseName) {
    studentList = studentList.filter((s: any) => s.course_name === courseName || s.courseName === courseName)
  }
  attendanceStudents.value = studentList.map((s: any) => ({
    id: s.student_account || s.student_id || s.id || '',
    name: s.student_name || s.name || '',
    className: s.student_class_name || s.class_name || '',
    status: 'present' as const, // 默认出勤
  }))
}

function batchSetStatus(status: 'present' | 'absent' | 'leave') {
  attendanceStudents.value.forEach(s => { s.status = status })
}

async function saveAttendance() {
  if (!attendanceForm.courseName || !attendanceForm.date) { message.warning('请填写课程和日期'); return }
  if (attendanceStudents.value.length === 0) { message.warning('请先选择课程以加载学生'); return }

  const records = attendanceStudents.value.map(s => ({
    studentName: s.name,
    studentUsername: s.id,
    status: s.status,
  }))
  try {
    const username = teacherUsername.value
    const res = await import('../../api/teacher/attendanceApi').then(m => m.saveAttendanceApi({
      courseName: attendanceForm.courseName,
      teacherUsername: username,
      date: attendanceForm.date,
      period: attendanceForm.period,
      remark: attendanceForm.remark,
      records,
    }))
    if (res.data?.code === 200) {
      // 刷新列表
      await loadAttendanceListFromApi()
      attendanceForm.courseName = ''
      attendanceForm.date = ''
      attendanceForm.period = undefined
      attendanceForm.remark = ''
      attendanceStudents.value = []
      sessionStorage.removeItem('attendance_form_draft')
      resetAttendanceView()
      message.success('考勤记录已保存')
    } else {
      message.error(res.data?.msg || '保存失败')
    }
  } catch { message.error('保存考勤记录失败') }
}

function openAttendanceDetail(item: AttendanceItem) {
  viewingAttendance.value = item
  activeAttendanceView.value = 'detail'
}

async function deleteAttendance(id: string | number) {
  try {
    const res = await import('../../api/teacher/attendanceApi').then(m => m.deleteAttendanceApi(id))
    if (res.data?.code === 200) {
      await loadAttendanceListFromApi()
      message.success('已删除')
    } else { message.error(res.data?.msg || '删除失败') }
  } catch { message.error('删除失败') }
}
const attSelectedIds = ref<Set<string | number>>(new Set())
const attAllSelected = computed(() => attendanceList.value.length > 0 && attendanceList.value.every(a => attSelectedIds.value.has(a.id)))
function toggleAttSelectAll() { attSelectedIds.value = attAllSelected.value ? new Set() : new Set(attendanceList.value.map(a => a.id)) }
function toggleAttSelect(id: string | number) { const n = new Set(attSelectedIds.value); n.has(id) ? n.delete(id) : n.add(id); attSelectedIds.value = n }
async function deleteSelectedAtt() {
  if (attSelectedIds.value.size === 0) { message.warning('请先选择要删除的考勤记录'); return }
  const ids = Array.from(attSelectedIds.value)
  try {
    const res = await import('../../api/teacher/attendanceApi').then(m => m.batchDeleteAttendanceApi(ids))
    if (res.data?.code === 200) {
      await loadAttendanceListFromApi()
      attSelectedIds.value = new Set()
      message.success(`已删除 ${ids.length} 条考勤记录`)
    } else { message.error(res.data?.msg || '批量删除失败') }
  } catch { message.error('批量删除失败') }
}

/** 导出考勤记录 */
function exportAttendance(item: AttendanceItem, format: ExportFormat) {
  const header = `考勤记录\n课程：${item.courseName}\n日期：${item.date}${item.remark ? '\n备注：' + item.remark : ''}\n出勤率：${item.presentCount}/${item.totalCount}\n\n`
  const rows = item.records.map(r => {
    const statusLabel = r.status === 'present' ? '出勤' : r.status === 'leave' ? '请假' : '缺勤'
    return `${r.name}\t${statusLabel}`
  }).join('\n')
  const content = header + '姓名\t状态\n' + rows
  exportContent(content, format, `考勤_${item.courseName}_${item.date}`)
}

// ===== 考勤 & 实训计划 子视图状态持久化 =====
watch(activeAttendanceView, (v) => {
  sessionStorage.setItem('attendance_subview', v)
  // 不在离开 form 时删除草稿 — 草稿持久保留直到用户清空或保存
  if (v !== 'detail') sessionStorage.removeItem('attendance_detail_id')
  else if (viewingAttendance.value) {
    sessionStorage.setItem('attendance_detail_id', viewingAttendance.value.id)
  }
})

watch(activeTpView, (v) => {
  sessionStorage.setItem('tp_subview', v)
  // 不在离开 form 时删除草稿 — 草稿持久保留直到用户清空或保存
  if (v !== 'detail') sessionStorage.removeItem('tp_detail_id')
  else if (viewingTrainingPlan.value) {
    sessionStorage.setItem('tp_detail_id', viewingTrainingPlan.value.id)
  }
})

// 考勤表单草稿自动保存
watch([() => attendanceForm.courseName, () => attendanceForm.date, () => attendanceForm.period, () => attendanceForm.remark], () => {
  if (activeAttendanceView.value === 'form' && (attendanceForm.courseName || attendanceForm.date)) {
    sessionStorage.setItem('attendance_form_draft', JSON.stringify({
      courseName: attendanceForm.courseName,
      date: attendanceForm.date,
      period: attendanceForm.period,
      remark: attendanceForm.remark,
    }))
  }
})

// 实训计划表单草稿自动保存
watch([() => tpForm.courseName, () => tpForm.title, () => tpForm.description, () => tpForm.templateKey], () => {
  if (activeTpView.value === 'form' && (tpForm.courseName || tpForm.title)) {
    sessionStorage.setItem('tp_form_draft', JSON.stringify({
      courseName: tpForm.courseName,
      title: tpForm.title,
      description: tpForm.description,
      templateKey: tpForm.templateKey,
      sections: tpForm.sections,
      editingId: editingTrainingPlan.value?.id || null,
    }))
  }
}, { deep: true })

// ===== Students =====
const studentColumns = [
  { title: '学号', dataIndex: 'student_id', key: 'student_id' },
  { title: '姓名', dataIndex: 'student_name', key: 'student_name' },
  { title: '班级', dataIndex: 'student_class_name', key: 'student_class_name' },
  { title: '加入时间', dataIndex: 'create_time', key: 'create_time' },
]

const enhancedStudentColumns = [
  { title: '', dataIndex: 'checkbox', key: 'checkbox', width: 40 },
  { title: '用户名', dataIndex: 'student_account', key: 'student_account', width: 120 },
  { title: '姓名', dataIndex: 'student_name', key: 'student_name', width: 100 },
  { title: '性别', dataIndex: 'student_gender', key: 'student_gender', width: 70 },
  { title: '年龄', dataIndex: 'student_age', key: 'student_age', width: 70 },
  { title: '课程', dataIndex: 'course_name', key: 'course_name', width: 140 },
  { title: '专业', dataIndex: 'student_major', key: 'student_major', width: 120 },
  { title: '班级', dataIndex: 'student_class_name', key: 'student_class_name', width: 120 },
  { title: '加入时间', dataIndex: 'create_time', key: 'create_time', width: 160 },
  { title: '操作', key: 'action', width: 140, fixed: 'right' as const },
]

const classStudentColumns = [
  { title: '用户名', dataIndex: 'student_id', key: 'student_id', width: 100 },
  { title: '姓名', dataIndex: 'student_name', key: 'student_name', width: 120 },
  { title: '性别', dataIndex: 'student_gender', key: 'student_gender', width: 80 },
  { title: '年龄', dataIndex: 'student_age', key: 'student_age', width: 80 },
  { title: '专业', dataIndex: 'student_major', key: 'student_major' },
  { title: '加入时间', dataIndex: 'create_time', key: 'create_time', width: 180 },
]

function searchClassStudents() { /* filteredClassStudents is computed — reactive */ }
function resetClassSearch() { classKeyword.value = '' }

// ===== Analytics =====
const analyticsCourse = ref('')
/** 存放后端返回的提交记录原始列表（用于计算图表数据） */
const analyticsSubmissions = ref<any[]>([])
const gradedSubmissionsCount = computed(() =>
  analyticsSubmissions.value.filter((s: any) =>
    s.status === 'graded' && s.total_score != null && s.max_total_score > 0
  ).length
)

// ===== 成绩管理 =====
const gradingCourse = ref('')
const gradingAssignments = ref<any[]>([])
const gradeSummary = reactive({ totalSubmissions: undefined as number | undefined, gradedSubmissions: 0, averageScore: '—', highestScore: '—' })
const aiGradingItems = reactive(new Set<string>())

/** 从后端加载成绩汇总（发布列表 + 提交统计，均来自数据库） */
async function loadGradeSummary() {
  if (!gradingCourse.value) return
  try {
    const { getGradeSummaryApi, getPublishedAssignmentsApi } = await import('../../api/submissionApi')

    // 同时查询已发布列表和提交数据
    const [pubRes, summaryRes] = await Promise.all([
      getPublishedAssignmentsApi({ course_name: gradingCourse.value }),
      getGradeSummaryApi({ course_name: gradingCourse.value }),
    ])

    // 处理已发布列表
    let publishedList: any[] = []
    if (pubRes.data?.code === 200 && pubRes.data.data) {
      publishedList = pubRes.data.data
    }

    // 处理提交统计
    const items: any[] = []
    if (summaryRes.data?.code === 200 && summaryRes.data.data) {
      const d = summaryRes.data.data
      gradeSummary.totalSubmissions = d.totalSubmissions || 0
      gradeSummary.gradedSubmissions = d.gradedSubmissions || 0
      gradeSummary.averageScore = d.averageScore || '—'
      gradeSummary.highestScore = d.highestScore || '—'
      items.push(...(d.items || []))
    } else {
      gradeSummary.totalSubmissions = 0
      gradeSummary.gradedSubmissions = 0
      gradeSummary.averageScore = '—'
      gradeSummary.highestScore = '—'
    }

    // 按 homework_id 分组统计
    const subByHw = new Map<string, { total: number; graded: number; scores: number[] }>()
    items.forEach((s: any) => {
      const hid = String(s.homework_id || '')
      if (!hid) return
      if (!subByHw.has(hid)) subByHw.set(hid, { total: 0, graded: 0, scores: [] })
      const g = subByHw.get(hid)!
      g.total++
      if (s.status === 'graded' && s.total_score != null) {
        g.graded++
        g.scores.push(Number(s.total_score))
      }
    })

    // 合并：已发布列表 + 提交数量
    gradingAssignments.value = publishedList.map((a: any) => {
      const hid = String(a.homework_id || a.id || '')
      const counts = subByHw.get(hid) || { total: 0, graded: 0, scores: [] as number[] }
      return {
        ...a,
        id: a.homework_id || a.id,
        submissionCount: counts.total,
        gradedCount: counts.graded,
        avgScore: counts.scores.length > 0
          ? (counts.scores.reduce((x, y) => x + y, 0) / counts.scores.length).toFixed(1)
          : undefined,
      }
    })
  } catch {
    gradingAssignments.value = []
    gradeSummary.totalSubmissions = 0
    gradeSummary.gradedSubmissions = 0
  }
}

async function openGradingFromTab(item: any) {
  // Data already comes from backend with questions, type, etc.
  sessionStorage.setItem('grading_homework', JSON.stringify(item))
  router.push('/teacher/grading')
}

async function aiAutoGradeItem(item: any) {
  if (aiGradingItems.has(item.id)) return
  aiGradingItems.add(item.id)
  try {
    const { aiAutoGradeApi } = await import('../../api/submissionApi')
    const res = await aiAutoGradeApi({ homework_id: item.id })
    if (res.data?.code === 200) {
      const r = res.data.data
      const graded = r.graded || 0
      const failed = r.failed || 0
      const total = r.total || 0
      if (graded > 0 && failed === 0) {
        message.success(`AI 批改完成：成功批改 ${graded} 份`)
      } else if (graded > 0) {
        message.warning(`AI 批改部分完成：成功 ${graded} 份，失败 ${failed} 份`)
      } else if (failed > 0) {
        message.error(`AI 批改失败：${failed} 份提交均未能批改。请检查 LangChain 服务是否启动（端口 9084）`)
      } else {
        message.info('没有需要批改的提交')
      }
      // 重新加载成绩数据以更新统计
      await loadGradeSummary()
    } else {
      message.error(res.data?.msg || 'AI 批改失败')
    }
  } catch { message.error('AI 批改请求失败，请检查网络连接') }
  aiGradingItems.delete(item.id)
}

// ===== 题库积累 =====
interface QuestionBankItem {
  id: string; type: string; title: string; options: string[]; correctAnswer: string
  difficulty: number; tags: string[]; courseName: string; createdAt: number
  /** 题目描述内容（代码块、程序片段等附加在题干后的内容） */
  content?: string
}

const QB_STORAGE_KEY = 'teacher_question_bank'
const QB_DRAFT_KEY = 'question_bank_form_draft'
const QB_VIEW_KEY = 'qb_subview'
const activeQbView = ref<'list' | 'form'>('list')
const editingQbItem = ref<QuestionBankItem | null>(null)
const qbFilterType = ref('')
const qbFilterCourse = ref('')
const qbKeyword = ref('')
// 批量选择
const qbSelectedIds = ref<Set<string>>(new Set())
const qbAllSelected = computed(() =>
  filteredQuestionBank.value.length > 0 &&
  filteredQuestionBank.value.every(q => qbSelectedIds.value.has(q.id))
)
function toggleQbSelectAll() {
  if (qbAllSelected.value) {
    qbSelectedIds.value = new Set()
  } else {
    qbSelectedIds.value = new Set(filteredQuestionBank.value.map(q => q.id))
  }
}
function toggleQbSelect(id: string) {
  const next = new Set(qbSelectedIds.value)
  if (next.has(id)) next.delete(id)
  else next.add(id)
  qbSelectedIds.value = next
}
function deleteSelectedQb() {
  if (qbSelectedIds.value.size === 0) { message.warning('请先选择要删除的题目'); return }
  const count = qbSelectedIds.value.size
  questionBank.value = questionBank.value.filter(q => !qbSelectedIds.value.has(q.id))
  saveQuestionBank(questionBank.value)
  qbSelectedIds.value = new Set()
  message.success(`已删除 ${count} 道题目`)
}

const qbForm = reactive({
  type: 'choice', title: '', qbOptions: [''] as string[],
  qbFillBlanks: [''] as string[], // 填空题多空答案
  correctAnswer: '',
  referenceAnswer: '', // 应用题参考答案（可选）
  difficulty: 2, courseName: '', tagsText: '',
})

function loadQuestionBank(): QuestionBankItem[] {
  try { return JSON.parse(localStorage.getItem(QB_STORAGE_KEY) || '[]') } catch { return [] }
}
function saveQuestionBank(list: QuestionBankItem[]) {
  try {
    localStorage.setItem(QB_STORAGE_KEY, JSON.stringify(list))
  } catch { message.error('保存题库失败，请检查存储空间') }
}

const questionBank = ref<QuestionBankItem[]>(loadQuestionBank())

const filteredQuestionBank = computed(() => {
  let list = questionBank.value
  if (qbFilterType.value) list = list.filter(q => q.type === qbFilterType.value)
  if (qbFilterCourse.value) list = list.filter(q => q.courseName === qbFilterCourse.value)
  const term = qbKeyword.value.trim().toLowerCase()
  if (term) {
    const regex = new RegExp(term.split('').map(c => c.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')).join('.*'), 'i')
    list = list.filter(q => Object.values(q).some(v => v != null && String(v).toLowerCase().match(regex)))
  }
  return list
})

function searchQuestionBank() { /* filteredQuestionBank is computed — reactive */ }
function resetQbSearch() { qbKeyword.value = '' }

function qbTypeLabel(type: string): string {
  const labels: Record<string, string> = { choice: '选择题', 'fill-blank': '填空题', 'true-false': '判断题', essay: '应用题', calculation: '计算题' }
  return labels[type] || type
}

function addQbOption() {
  qbForm.qbOptions.push('')
}
function removeQbOption(index: number) {
  if (qbForm.qbOptions.length <= 1) return
  qbForm.qbOptions.splice(index, 1)
}
function getQbOptionLetter(index: number): string {
  return String.fromCharCode(65 + index) // A, B, C, D...
}
/** 选择题可选的正确答案列表（非空选项） */
const qbChoiceOptions = computed(() => {
  return qbForm.qbOptions.filter(o => o.trim())
})

function addFillBlank() {
  qbForm.qbFillBlanks.push('')
}
function removeFillBlank(index: number) {
  if (qbForm.qbFillBlanks.length <= 1) return
  qbForm.qbFillBlanks.splice(index, 1)
}

function saveQuestionBankItem() {
  if (!qbForm.title.trim()) { message.warning('请输入题目内容'); return }
  if (!qbForm.type) { message.warning('请选择题型'); return }

  // Type-specific validation
  if (qbForm.type === 'choice') {
    const validOptions = qbForm.qbOptions.filter(o => o.trim())
    if (validOptions.length < 2) { message.warning('选择题至少需要 2 个选项'); return }
    if (!qbForm.correctAnswer.trim()) { message.warning('请选择正确答案'); return }
  } else if (qbForm.type === 'true-false') {
    if (!qbForm.correctAnswer) { message.warning('请选择正确答案（正确/错误）'); return }
  } else if (qbForm.type === 'fill-blank') {
    const validBlanks = qbForm.qbFillBlanks.filter(b => b.trim())
    if (validBlanks.length === 0) { message.warning('请至少填写一个空的答案'); return }
  } else if (qbForm.type === 'calculation') {
    if (!qbForm.correctAnswer.trim()) { message.warning('请填写正确答案'); return }
  }

  const options = qbForm.type === 'choice'
    ? qbForm.qbOptions.filter(o => o.trim())
    : []
  const tags = qbForm.tagsText.trim() ? qbForm.tagsText.split(/[,，]/).map(t => t.trim()).filter(Boolean) : []
  // For fill-blank, join blanks with ||| separator; for essay, store referenceAnswer as correctAnswer
  let correctAnswer: string
  if (qbForm.type === 'fill-blank') {
    correctAnswer = qbForm.qbFillBlanks.map(b => b.trim()).join('|||')
  } else if (qbForm.type === 'essay') {
    correctAnswer = qbForm.referenceAnswer.trim() || ''
  } else {
    correctAnswer = qbForm.correctAnswer.trim()
  }

  const item: QuestionBankItem = {
    id: editingQbItem.value?.id || 'qb_' + Date.now(),
    type: qbForm.type,
    title: qbForm.title.trim(),
    options,
    correctAnswer,
    difficulty: qbForm.difficulty,
    tags,
    courseName: qbForm.courseName,
    createdAt: editingQbItem.value?.createdAt || Date.now(),
    // 编辑模式下保留原有的 content（AI 导入的代码块等描述内容）
    content: editingQbItem.value?.content,
  }

  if (editingQbItem.value) {
    const idx = questionBank.value.findIndex(q => q.id === editingQbItem.value!.id)
    if (idx >= 0) questionBank.value[idx] = item
  } else {
    questionBank.value.unshift(item)
  }
  saveQuestionBank(questionBank.value)
  // 保存成功，清除草稿（finalizeLeave 模式）
  sessionStorage.removeItem(QB_DRAFT_KEY)
  sessionStorage.removeItem(QB_VIEW_KEY)
  const wasEditing = !!editingQbItem.value
  resetQbForm()
  activeQbView.value = 'list'
  message.success(wasEditing ? '已更新' : '已添加到题库')
}

function editQuestionBankItem(q: QuestionBankItem) {
  editingQbItem.value = q
  qbForm.type = q.type
  qbForm.title = q.title
  qbForm.qbOptions = (q.options && q.options.length > 0) ? [...q.options] : ['']
  // fill-blank: parse blanks from correctAnswer (stored as "A|||B|||C")
  if (q.type === 'fill-blank' && q.correctAnswer) {
    qbForm.qbFillBlanks = q.correctAnswer.split('|||').map(b => b.trim())
  } else {
    qbForm.qbFillBlanks = ['']
  }
  qbForm.correctAnswer = (q.type !== 'fill-blank') ? (q.correctAnswer || '') : ''
  qbForm.referenceAnswer = (q.type === 'essay') ? (q.correctAnswer || '') : ''
  qbForm.difficulty = q.difficulty
  qbForm.courseName = q.courseName
  qbForm.tagsText = (q.tags || []).join(', ')
  activeQbView.value = 'form'
}

function deleteQuestionBankItem(id: string) {
  questionBank.value = questionBank.value.filter(q => q.id !== id)
  saveQuestionBank(questionBank.value)
  message.success('已删除')
}

function saveQbDraft() {
  try {
    if (qbForm.title || qbForm.type) {
      sessionStorage.setItem(QB_DRAFT_KEY, JSON.stringify({
        type: qbForm.type,
        title: qbForm.title,
        qbOptions: qbForm.qbOptions,
        qbFillBlanks: qbForm.qbFillBlanks,
        correctAnswer: qbForm.correctAnswer,
        referenceAnswer: qbForm.referenceAnswer,
        difficulty: qbForm.difficulty,
        courseName: qbForm.courseName,
        tagsText: qbForm.tagsText,
        editingId: editingQbItem.value?.id || null,
      }))
    }
  } catch { /* ignore */ }
}

function clearQbDraft() {
  sessionStorage.removeItem(QB_DRAFT_KEY)
}

/** 打开题库表单（新建） */
function openQbForm() {
  // 尝试恢复草稿
  const draft = sessionStorage.getItem(QB_DRAFT_KEY)
  if (draft) {
    try {
      const d = JSON.parse(draft)
      if (d.title || d.type) {
        qbForm.type = d.type || 'choice'
        qbForm.title = d.title || ''
        qbForm.qbOptions = (d.qbOptions && d.qbOptions.length > 0) ? d.qbOptions : ['']
        qbForm.qbFillBlanks = (d.qbFillBlanks && d.qbFillBlanks.length > 0) ? d.qbFillBlanks : ['']
        qbForm.correctAnswer = d.correctAnswer || ''
        qbForm.referenceAnswer = d.referenceAnswer || ''
        qbForm.difficulty = d.difficulty || 2
        qbForm.courseName = d.courseName || ''
        qbForm.tagsText = d.tagsText || ''
        if (d.editingId) {
          const found = questionBank.value.find(q => q.id === d.editingId)
          if (found) editingQbItem.value = found
        }
        activeQbView.value = 'form'
        return
      }
    } catch { /* ignore */ }
  }
  // 全新表单
  editingQbItem.value = null
  resetQbForm()
  activeQbView.value = 'form'
}

/** 返回题库列表（保存草稿） */
function resetQbView() {
  // 离开表单前保存草稿
  if (activeQbView.value === 'form' && (qbForm.title || qbForm.type)) {
    saveQbDraft()
  }
  activeQbView.value = 'list'
  editingQbItem.value = null
  resetQbForm()
  sessionStorage.removeItem(QB_VIEW_KEY)
}

/** 清空题库表单 */
function clearQbForm() {
  editingQbItem.value = null
  resetQbForm()
  clearQbDraft()
  sessionStorage.removeItem(QB_VIEW_KEY)
  message.success('已清空表单内容')
}

function resetQbForm() {
  editingQbItem.value = null
  qbForm.type = 'choice'; qbForm.title = ''; qbForm.qbOptions = ['']
  qbForm.qbFillBlanks = ['']
  qbForm.correctAnswer = ''; qbForm.referenceAnswer = ''
  qbForm.difficulty = 2; qbForm.courseName = ''; qbForm.tagsText = ''
}

// 题库里界面状态持久化
watch(activeQbView, (v) => {
  if (v === 'form') {
    sessionStorage.setItem('qb_subview', v)
  } else {
    sessionStorage.removeItem('qb_subview')
  }
})

// 题库表单草稿自动保存
watch([() => qbForm.type, () => qbForm.title, () => qbForm.correctAnswer, () => qbForm.referenceAnswer, () => qbForm.courseName, () => qbForm.tagsText, () => qbForm.difficulty], () => {
  if (activeQbView.value === 'form' && (qbForm.title || qbForm.type)) {
    saveQbDraft()
  }
}, { deep: true })

// ===== 通知中心 =====
interface NotificationItem {
  id: string; courseName: string; type: 'announcement' | 'homework' | 'exam'; title: string
  content: string; priority: 'normal' | 'important' | 'urgent'; read: boolean; createdAt: number
}

const NOTIF_STORAGE_KEY = 'teacher_notifications'
const showNotifyModal = ref(false)

const notifyForm = reactive({
  courseName: '', type: 'announcement' as 'announcement' | 'homework' | 'exam', title: '', content: '',
  priority: 'normal' as 'normal' | 'important' | 'urgent',
})

const notificationList = ref<NotificationItem[]>(loadNotifications())

function loadNotifications(): NotificationItem[] {
  try { return JSON.parse(localStorage.getItem(NOTIF_STORAGE_KEY) || '[]') } catch { return [] }
}
function saveNotifications(list: NotificationItem[]) {
  try {
    localStorage.setItem(NOTIF_STORAGE_KEY, JSON.stringify(list))
  } catch { message.error('保存通知失败，请检查存储空间') }
}

function sendNotification() {
  if (!notifyForm.courseName || !notifyForm.title) { message.warning('请填写课程和标题'); return }
  const item: NotificationItem = {
    id: 'notif_' + Date.now() + '_' + Math.random().toString(36).slice(2, 6),
    courseName: notifyForm.courseName,
    type: notifyForm.type,
    title: notifyForm.title,
    content: notifyForm.content,
    priority: notifyForm.priority,
    read: false,
    createdAt: Date.now(),
  }
  notificationList.value.unshift(item)
  saveNotifications(notificationList.value)
  // Also push to student-accessible storage so students can see it
  try {
    const studentNotifs = JSON.parse(localStorage.getItem('student_notifications') || '[]')
    studentNotifs.unshift(item)
    localStorage.setItem('student_notifications', JSON.stringify(studentNotifs))
  } catch { /* ignore */ }
  showNotifyModal.value = false
  notifyForm.courseName = ''; notifyForm.type = 'announcement'; notifyForm.title = ''; notifyForm.content = ''; notifyForm.priority = 'normal'
  message.success('通知已发送')
}

function markNotifRead(id: string) {
  const n = notificationList.value.find(x => x.id === id)
  if (n) {
    n.read = true
    saveNotifications(notificationList.value)
    // Also sync read status to student side
    try {
      const studentNotifs = JSON.parse(localStorage.getItem('student_notifications') || '[]')
      const sn = studentNotifs.find((x: any) => x.id === id)
      if (sn) { sn.read = true; localStorage.setItem('student_notifications', JSON.stringify(studentNotifs)) }
    } catch { /* ignore */ }
  }
}

function deleteNotification(id: string) {
  notificationList.value = notificationList.value.filter(n => n.id !== id)
  saveNotifications(notificationList.value)
  // Also remove from student-accessible storage
  try {
    const studentNotifs = JSON.parse(localStorage.getItem('student_notifications') || '[]')
    const filtered = studentNotifs.filter((n: any) => n.id !== id)
    localStorage.setItem('student_notifications', JSON.stringify(filtered))
  } catch { /* ignore */ }
  message.success('已删除')
}
const notifSelectedIds = ref<Set<string>>(new Set())
const notifAllSelected = computed(() => notificationList.value.length > 0 && notificationList.value.every(n => notifSelectedIds.value.has(n.id)))
function toggleNotifSelectAll() { notifSelectedIds.value = notifAllSelected.value ? new Set() : new Set(notificationList.value.map(n => n.id)) }
function toggleNotifSelect(id: string) { const n = new Set(notifSelectedIds.value); n.has(id) ? n.delete(id) : n.add(id); notifSelectedIds.value = n }
function deleteSelectedNotif() {
  if (notifSelectedIds.value.size === 0) { message.warning('请先选择要删除的通知'); return }
  const count = notifSelectedIds.value.size
  const ids = notifSelectedIds.value
  notificationList.value = notificationList.value.filter(n => !ids.has(n.id))
  saveNotifications(notificationList.value)
  try {
    const studentNotifs = JSON.parse(localStorage.getItem('student_notifications') || '[]')
    localStorage.setItem('student_notifications', JSON.stringify(studentNotifs.filter((n: any) => !ids.has(n.id))))
  } catch {}
  notifSelectedIds.value = new Set()
  message.success(`已删除 ${count} 条通知`)
}

// ===== 模板复用 =====
interface TemplateItem {
  id: string; name: string; type: 'homework' | 'exam'; description: string
  courseName: string; questionCount: number; questions: any[]; createdAt: number; updatedAt: number
}

const TEMPLATE_STORAGE_KEY = 'teacher_templates'
const templateFilterType = ref('')

const templateList = ref<TemplateItem[]>(loadTemplates())

function loadTemplates(): TemplateItem[] {
  try { return JSON.parse(localStorage.getItem(TEMPLATE_STORAGE_KEY) || '[]') } catch { return [] }
}
function saveTemplates(list: TemplateItem[]) {
  try {
    localStorage.setItem(TEMPLATE_STORAGE_KEY, JSON.stringify(list))
  } catch { message.error('保存模板失败，请检查存储空间') }
}

const showTemplatePreview = ref(false)
const previewingTemplate = ref<TemplateItem | null>(null)

const filteredTemplates = computed(() => {
  if (!templateFilterType.value) return templateList.value
  return templateList.value.filter(t => t.type === templateFilterType.value)
})

function getTypeEmoji(type: string): string {
  const m: Record<string, string> = { choice: '🔤', 'fill-blank': '✏️', 'true-false': '✅', essay: '📝', calculation: '🔢' }
  return m[type] || '❓'
}
function getTypeLabel(type: string): string {
  const m: Record<string, string> = { choice: '选择', 'fill-blank': '填空', 'true-false': '判断', essay: '应用', calculation: '计算' }
  return m[type] || type
}
function getTemplateTypeCounts(tpl: TemplateItem): Record<string, number> {
  const counts: Record<string, number> = {}
  if (tpl.questions) {
    for (const q of tpl.questions) { counts[q.type] = (counts[q.type] || 0) + 1 }
  }
  return counts
}
function previewTemplate(tpl: TemplateItem) {
  previewingTemplate.value = tpl
  showTemplatePreview.value = true
}

function applyTemplate(tpl: TemplateItem) {
  sessionStorage.setItem('apply_template', JSON.stringify(tpl))
  if (tpl.type === 'homework') {
    router.push('/teacher/homework-publisher?fromTemplate=1')
  } else {
    router.push('/teacher/exam-publisher?fromTemplate=1')
  }
}

function deleteTemplate(id: string) {
  templateList.value = templateList.value.filter(t => t.id !== id)
  saveTemplates(templateList.value)
  message.success('已删除')
}

/** 从当前作业/考试另存为模板 — 由 publish page 通过 sessionStorage 传数据后调用 */
function saveAsTemplate(data: { name: string; type: 'homework' | 'exam'; description: string; courseName: string; questions: any[] }) {
  const tpl: TemplateItem = {
    id: 'tpl_' + Date.now(),
    name: data.name,
    type: data.type,
    description: data.description,
    courseName: data.courseName,
    questionCount: data.questions?.length || 0,
    questions: data.questions || [],
    createdAt: Date.now(),
    updatedAt: Date.now(),
  }
  templateList.value.unshift(tpl)
  saveTemplates(templateList.value)
  message.success('模板已保存')
}
// Expose for publisher pages
;(window as any).__saveAsTemplate = saveAsTemplate

// ===== 讨论区 =====
interface DiscussionReply { author: string; content: string; createdAt: number }
interface DiscussionTopic {
  id: string; courseName: string; title: string; content: string; author: string
  category: string; pinned: boolean; replies: DiscussionReply[]; createdAt: number
}

const DISC_STORAGE_KEY = 'app_course_discussions'
const discCourseFilter = ref('')
const showNewTopicModal = ref(false)
const showTopicDetail = ref(false)
const viewingTopic = ref<DiscussionTopic | null>(null)
const replyContent = ref('')

const newTopicForm = reactive({ title: '', content: '', category: '', pinned: false })
const editingTopicContent = ref('')
const editingReplyIdx = ref(-1)
const editingReplyContent = ref('')

const discussionTopics = ref<DiscussionTopic[]>(loadDiscussions())

function loadDiscussions(): DiscussionTopic[] {
  try { return JSON.parse(localStorage.getItem(DISC_STORAGE_KEY) || '[]') } catch { return [] }
}
function saveDiscussions(list: DiscussionTopic[]) {
  try {
    localStorage.setItem(DISC_STORAGE_KEY, JSON.stringify(list))
  } catch { message.error('保存讨论失败，请检查存储空间') }
}

const filteredDiscussions = computed(() => {
  return discussionTopics.value
    .filter(t => t.courseName === discCourseFilter.value)
    .sort((a, b) => (b.pinned ? 1 : 0) - (a.pinned ? 1 : 0) || b.createdAt - a.createdAt)
})

function createTopic() {
  if (!newTopicForm.title.trim() || !newTopicForm.content.trim()) return
  const topic: DiscussionTopic = {
    id: 'disc_' + Date.now() + '_' + Math.random().toString(36).slice(2, 6),
    courseName: discCourseFilter.value,
    title: newTopicForm.title.trim(),
    content: newTopicForm.content.trim(),
    author: teacherUsername.value || '教师',
    category: newTopicForm.category.trim(),
    pinned: newTopicForm.pinned,
    replies: [],
    createdAt: Date.now(),
  }
  // Re-read from storage to avoid overwriting concurrent changes
  const latest = loadDiscussions()
  latest.unshift(topic)
  discussionTopics.value = latest
  saveDiscussions(latest)
  showNewTopicModal.value = false
  newTopicForm.title = ''; newTopicForm.content = ''; newTopicForm.category = ''; newTopicForm.pinned = false
  message.success('话题已发布')
}

function openTopicDetail(topic: DiscussionTopic) {
  viewingTopic.value = topic
  replyContent.value = ''
  showTopicDetail.value = true
}

function submitReply() {
  if (!replyContent.value.trim() || !viewingTopic.value) return
  const reply: DiscussionReply = {
    author: teacherUsername.value || '教师',
    content: replyContent.value.trim(),
    createdAt: Date.now(),
  }
  // Re-read from storage to avoid overwriting concurrent changes
  const latest = loadDiscussions()
  const topic = latest.find(t => t.id === viewingTopic.value!.id)
  if (topic) {
    if (!topic.replies) topic.replies = []
    topic.replies.push(reply)
    discussionTopics.value = latest
    viewingTopic.value = topic
    saveDiscussions(latest)
  }
  replyContent.value = ''
  message.success('回复已发送')
}

function startEditTopic() {
  if (viewingTopic.value) editingTopicContent.value = viewingTopic.value.content
}
function saveTopicEdit() {
  if (!viewingTopic.value || !editingTopicContent.value.trim()) return
  const latest = loadDiscussions()
  const t = latest.find(t => t.id === viewingTopic.value!.id)
  if (t) { t.content = editingTopicContent.value.trim(); discussionTopics.value = latest; viewingTopic.value = t; saveDiscussions(latest) }
  editingTopicContent.value = ''
  message.success('话题已更新')
}
function deleteTopic(id: string) {
  const latest = loadDiscussions().filter(t => t.id !== id)
  discussionTopics.value = latest; saveDiscussions(latest)
  showTopicDetail.value = false
  message.success('话题已删除')
}
function startEditReply(idx: number) {
  editingReplyIdx.value = idx
  editingReplyContent.value = viewingTopic.value?.replies?.[idx]?.content || ''
}
function saveReplyEdit(idx: number) {
  if (!viewingTopic.value || !editingReplyContent.value.trim()) return
  const latest = loadDiscussions()
  const t = latest.find(t => t.id === viewingTopic.value!.id)
  if (t?.replies?.[idx]) { t.replies[idx].content = editingReplyContent.value.trim(); discussionTopics.value = latest; viewingTopic.value = t; saveDiscussions(latest) }
  editingReplyIdx.value = -1; editingReplyContent.value = ''
  message.success('回复已更新')
}
function deleteReply(idx: number) {
  if (!viewingTopic.value) return
  const latest = loadDiscussions()
  const t = latest.find(t => t.id === viewingTopic.value!.id)
  if (t?.replies) { t.replies.splice(idx, 1); discussionTopics.value = latest; viewingTopic.value = t; saveDiscussions(latest) }
  message.success('回复已删除')
}

const chartRef1 = ref<HTMLElement | null>(null)
const chartRef2 = ref<HTMLElement | null>(null)
const chartInstances: any[] = []

/** 从已批改提交中计算正确率趋势（按周分组） */
function computeAccuracyTrend(submissions: any[]) {
  const graded = submissions.filter((s: any) =>
    s.status === 'graded' && s.total_score != null && s.max_total_score != null && s.max_total_score > 0
  )
  if (graded.length === 0) return { weeks: [] as string[], rates: [] as number[] }

  const weekMap = new Map<string, { total: number; count: number }>()
  graded.forEach((s: any) => {
    const date = new Date(s.submitted_at)
    const day = date.getDay()
    const monday = new Date(date)
    monday.setDate(date.getDate() - (day === 0 ? 6 : day - 1))
    const key = `${monday.getFullYear()}/${monday.getMonth() + 1}/${monday.getDate()}`
    const acc = (s.total_score / s.max_total_score) * 100
    const existing = weekMap.get(key) || { total: 0, count: 0 }
    existing.total += acc
    existing.count++
    weekMap.set(key, existing)
  })

  const sorted = [...weekMap.entries()].sort((a, b) => a[0].localeCompare(b[0]))
  return {
    weeks: sorted.map(([k]) => {
      const parts = k.split('/')
      return `${parts[1]}/${parts[2]}` // M/D format for display
    }),
    rates: sorted.map(([, v]) => Math.round(v.total / v.count)),
  }
}

/** 从已批改提交中计算学生掌握度分布（按学生平均正确率分桶） */
function computeMasteryDistribution(submissions: any[]) {
  const graded = submissions.filter((s: any) =>
    s.status === 'graded' && s.total_score != null && s.max_total_score != null && s.max_total_score > 0
  )
  if (graded.length === 0) return []

  const studentMap = new Map<string, { total: number; count: number }>()
  graded.forEach((s: any) => {
    const key = s.student_username || s.student_name || 'unknown'
    const existing = studentMap.get(key) || { total: 0, count: 0 }
    existing.total += (s.total_score / s.max_total_score) * 100
    existing.count++
    studentMap.set(key, existing)
  })

  let excellent = 0, good = 0, average = 0, weak = 0
  studentMap.forEach(v => {
    const avg = v.total / v.count
    if (avg >= 90) excellent++
    else if (avg >= 75) good++
    else if (avg >= 60) average++
    else weak++
  })

  return [
    { name: '优秀 (≥90%)', value: excellent, itemStyle: { color: '#10B981' } },
    { name: '良好 (75-89%)', value: good, itemStyle: { color: '#4F46E5' } },
    { name: '一般 (60-74%)', value: average, itemStyle: { color: '#F59E0B' } },
    { name: '薄弱 (<60%)', value: weak, itemStyle: { color: '#EF4444' } },
  ].filter((d: any) => d.value > 0)
}

watch(analyticsCourse, async (val) => {
  if (!val) return

  // 1. 从后端加载该课程的提交记录
  analyticsSubmissions.value = []
  try {
    const { getGradeSummaryApi } = await import('../../api/submissionApi')
    const res = await getGradeSummaryApi({ course_name: val })
    if (res.data?.code === 200 && res.data.data) {
      const d = res.data.data
      if (d.items && Array.isArray(d.items)) {
        analyticsSubmissions.value = d.items
      }
    }
  } catch {
    // 后端不可用时尝试从 localStorage 读取
    const allAssignments = loadGradingAssignments().filter((a: any) => a.courseName === val)
    analyticsSubmissions.value = allAssignments
  }

  await nextTick()

  const trend = computeAccuracyTrend(analyticsSubmissions.value)
  const mastery = computeMasteryDistribution(analyticsSubmissions.value)

  // Chart 1: 正确率趋势
  if (chartRef1.value) {
    const existing = echarts.getInstanceByDom(chartRef1.value)
    if (existing) existing.dispose()
    const c1 = echarts.init(chartRef1.value)
    chartInstances.push(c1)
    c1.setOption({
      tooltip: {
        trigger: 'axis',
        formatter: (p: any) => {
          if (!p || p.length === 0) return ''
          return `${p[0].name}<br/>正确率: ${p[0].value}%`
        },
      },
      xAxis: { data: trend.weeks.length > 0 ? trend.weeks : ['暂无数据'] },
      yAxis: { max: 100, axisLabel: { formatter: '{value}%' } },
      series: [{
        name: '正确率', type: 'line',
        data: trend.rates,
        smooth: true, areaStyle: { opacity: 0.1 },
      }],
      color: ['#4F46E5'],
    })
  }

  // Chart 2: 知识点掌握度
  if (chartRef2.value) {
    const existing = echarts.getInstanceByDom(chartRef2.value)
    if (existing) existing.dispose()
    const c2 = echarts.init(chartRef2.value)
    chartInstances.push(c2)
    if (mastery.length > 0) {
      c2.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c} 人 ({d}%)' },
        series: [{
          type: 'pie', radius: ['50%', '75%'],
          data: mastery,
          label: { show: true, formatter: '{b}\n{d}%' },
        }],
      })
    } else {
      c2.setOption({
        title: {
          text: '暂无数据', left: 'center', top: 'center',
          textStyle: { color: '#94A3B8', fontSize: 14, fontWeight: 400 },
        },
        series: [],
      })
    }
  }
})
</script>

<style scoped>
.workspace { padding: 24px; max-width: 1400px; margin: 0 auto; }
.stats-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 24px; }
@media (max-width: 1100px) { .stats-row { grid-template-columns: repeat(2, 1fr); } }

/* Tabs */
.tabs-bar { display: flex; gap: 4px; background: #fff; border-radius: 12px; padding: 6px; margin-bottom: 20px; border: 1px solid #E2E8F0; }
.tab-btn { display: flex; align-items: center; gap: 8px; padding: 10px 20px; border: none; background: transparent; border-radius: 8px; color: #64748B; font-size: 14px; font-weight: 500; cursor: pointer; transition: all 0.2s; font-family: inherit; }
.tab-btn:hover { color: #334155; background: #F1F5F9; }
.tab-btn.active { background: #4F46E5; color: #fff; box-shadow: 0 2px 8px rgba(79,70,229,0.3); }

/* Panel */
.panel { background: #fff; border-radius: 16px; border: 1px solid #E2E8F0; padding: 24px; min-height: 400px; }
.panel-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; }
.panel-header h2 { font-size: 18px; font-weight: 700; color: #0F172A; }

/* Course select border — visible against panel background */
.course-select { border: 1px solid #CBD5E1 !important; border-radius: 8px; }
.course-select:hover { border-color: #4F46E5 !important; }
.course-select:focus-within { border-color: #4F46E5 !important; box-shadow: 0 0 0 2px rgba(79,70,229,0.15) !important; }
.panel-body { padding: 40px 0; }

/* Clickable course select prompt */
.course-select-prompt {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 64px 32px; margin: 0 auto; max-width: 420px;
  border: 2px dashed #E2E8F0; border-radius: 16px;
  cursor: pointer; transition: all 0.3s ease; user-select: none;
  background: #FAFBFC;
}
.course-select-prompt:hover {
  border-color: #4F46E5; background: #EEF2FF;
  transform: translateY(-3px);
  box-shadow: 0 8px 28px rgba(79,70,229,0.12);
}
.course-select-prompt:hover .prompt-icon { color: #4F46E5; transform: scale(1.05); }
.course-select-prompt:hover .prompt-arrow { color: #4F46E5; animation: bounceDown 0.6s infinite; }
.course-select-prompt:active { transform: translateY(-1px); box-shadow: 0 4px 12px rgba(79,70,229,0.08); }

/* Course Picker Popup (inline, replaces prompt) */
.course-picker-popup {
  max-width: 460px; margin: 0 auto;
  background: #fff; border: 1px solid #E2E8F0; border-radius: 14px;
  box-shadow: 0 12px 40px rgba(0,0,0,0.1); overflow: hidden;
}
.picker-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 16px 20px; border-bottom: 1px solid #F1F5F9;
}
.picker-header h3 { font-size: 15px; font-weight: 700; color: #0F172A; margin: 0; }
.picker-close {
  width: 28px; height: 28px; border: none; background: #F1F5F9; border-radius: 6px;
  cursor: pointer; font-size: 14px; color: #64748B; display: flex; align-items: center; justify-content: center;
  transition: all .15s; font-family: inherit; line-height: 1;
}
.picker-close:hover { background: #E2E8F0; color: #334155; }
.picker-list { padding: 8px; max-height: 320px; overflow-y: auto; }
.picker-item {
  display: flex; align-items: center; gap: 12px;
  padding: 12px 14px; border-radius: 8px; cursor: pointer;
  transition: all .15s; color: #475569;
}
.picker-item:hover { background: #EEF2FF; color: #4F46E5; }
.picker-item-icon { flex-shrink: 0; display: flex; align-items: center; }
.picker-item-name { font-size: 14px; font-weight: 500; }

.prompt-icon { color: #CBD5E1; margin-bottom: 16px; transition: all 0.3s ease; }
.prompt-title { font-size: 17px; font-weight: 700; color: #334155; margin: 0 0 8px; transition: color 0.3s; }
.course-select-prompt:hover .prompt-title { color: #4F46E5; }
.prompt-desc { font-size: 14px; color: #94A3B8; margin: 0 0 16px; transition: color 0.3s; }
.course-select-prompt:hover .prompt-desc { color: #6366F1; }
.prompt-arrow { color: #CBD5E1; transition: all 0.3s ease; }
@keyframes bounceDown {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(4px); }
}

/* Section Label */
.section-label { font-size: 13px; font-weight: 700; color: #64748B; text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 12px; }
.ai-tasks-section { margin-bottom: 24px; padding-bottom: 20px; border-bottom: 1px solid #F1F5F9; }

/* Buttons */
.btn-primary { display: inline-flex; align-items: center; gap: 8px; padding: 10px 22px; border: none; background: linear-gradient(135deg, #4F46E5, #6366F1); color: #fff; border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.2s; font-family: inherit; box-shadow: 0 2px 8px rgba(79,70,229,0.25); }

/* Teacher Binding Code Banner */
.binding-code-banner { display: inline-flex; align-items: center; gap: 6px; background: #EEF2FF; border: 1px solid #C7D2FE; border-radius: 8px; padding: 6px 12px; font-size: 13px; color: #4338CA; white-space: nowrap; }
.binding-code-banner code { background: #fff; padding: 1px 6px; border-radius: 4px; font-size: 12px; color: #4F46E5; }
.btn-copy-mini { border: none; background: none; cursor: pointer; font-size: 14px; padding: 0 2px; opacity: 0.7; }
.btn-copy-mini:hover { opacity: 1; }

/* ===== 班级管理 ===== */
.binding-code-badge {
  display: inline-flex; align-items: center; gap: 6px;
  background: #EEF2FF; border: 1px solid #C7D2FE; border-radius: 8px;
  padding: 6px 14px; font-size: 13px; color: #4338CA;
}
.binding-code-badge code {
  background: #fff; padding: 2px 8px; border-radius: 4px;
  font-size: 13px; color: #4F46E5; font-weight: 600;
}
.class-mgmt-grid {
  display: grid; grid-template-columns: repeat(4, 1fr); gap: 14px;
}
.class-mgmt-card {
  background: #fff; border-radius: 14px; padding: 18px;
  border: 1px solid #F1F5F9;
  display: flex; align-items: center; gap: 14px;
  transition: all 0.3s;
}
.class-mgmt-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0,0,0,0.06);
  border-color: rgba(79,70,229,0.1);
}
.cm-card-icon {
  width: 48px; height: 48px; min-width: 48px;
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
}
.cm-card-body { display: flex; flex-direction: column; gap: 2px; }
.cm-card-label { font-size: 11px; font-weight: 600; color: #94A3B8; text-transform: uppercase; letter-spacing: 0.5px; }
.cm-card-value { font-size: 16px; font-weight: 700; color: #0F172A; }
.class-mgmt-section { background: #fff; border-radius: 14px; padding: 20px; border: 1px solid #F1F5F9; }

@media (max-width: 900px) {
  .class-mgmt-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 500px) {
  .class-mgmt-grid { grid-template-columns: 1fr; }
}
.btn-primary:hover:not(:disabled) { transform: translateY(-1px); box-shadow: 0 6px 20px rgba(79,70,229,0.35); }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-sm { display: inline-flex; align-items: center; gap: 6px; padding: 6px 14px; border: 1px solid #E2E8F0; background: #fff; border-radius: 6px; color: #475569; font-size: 13px; font-weight: 500; cursor: pointer; transition: all 0.2s; font-family: inherit; }
.btn-sm:hover { border-color: #4F46E5; color: #4F46E5; }
.btn-sm-primary { background: linear-gradient(135deg, #EEF2FF 0%, #E0E7FF 100%); border-color: #818CF8; color: #4F46E5; }
.btn-sm-primary:hover:not(:disabled) { background: linear-gradient(135deg, #E0E7FF 0%, #C7D2FE 100%); border-color: #6366F1; }
.btn-sm-primary:disabled { opacity: 0.6; cursor: not-allowed; }

/* 成绩管理 — 智能批改状态指示 */
.ga-grading-status { font-size: 12px; color: #6366F1; font-weight: 500; white-space: nowrap; animation: pulse-text 1.5s ease-in-out infinite; }
@keyframes pulse-text { 0%, 100% { opacity: 1; } 50% { opacity: 0.5; } }
.btn-sm-danger { color: #EF4444; }
.btn-sm-danger:hover { border-color: #EF4444; background: rgba(239,68,68,0.05); }

/* ===== 学生管理表单 ===== */
.student-form { display: flex; flex-direction: column; gap: 16px; }
.student-form .form-row { display: flex; flex-direction: column; gap: 4px; }
.student-form .form-row label { font-size: 13px; font-weight: 600; color: #475569; }
.student-form .form-row label .required { color: #EF4444; }
.student-form .form-row-3col { flex-direction: row; gap: 12px; }
.student-form .form-col { flex: 1; display: flex; flex-direction: column; gap: 4px; }
.student-form .form-col label { font-size: 13px; font-weight: 600; color: #475569; }
.student-form .form-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 8px; }
.header-actions { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; }

/* 里界面返回按钮 */
.btn-back {
  padding: 8px 16px; border: 1px solid #E2E8F0; border-radius: 8px;
  background: #fff; color: #475569; font-size: 13px; font-weight: 500;
  cursor: pointer; font-family: inherit; transition: all 0.2s;
}
.btn-back:hover { background: #F8FAFC; border-color: #CBD5E1; }
.btn-clear {
  padding: 8px 16px; border: 1px solid #FECACA; border-radius: 8px; background: #FEF2F2;
  color: #DC2626; font-size: 13px; font-weight: 500; cursor: pointer; font-family: inherit;
  transition: all 0.2s;
}
.btn-clear:hover { background: #FEE2E2; border-color: #FCA5A5; }

/* 考勤详情卡片 */
.attendance-detail-card { max-width: 640px; }
.attendance-detail-meta {
  background: #F8FAFC; border: 1px solid #E2E8F0; border-radius: 10px; padding: 16px; margin-bottom: 16px;
}
.detail-meta-row {
  display: flex; align-items: center; padding: 6px 0;
}
.detail-meta-label {
  width: 60px; font-size: 13px; color: #94A3B8; font-weight: 500; flex-shrink: 0;
}
.detail-meta-value {
  font-size: 14px; color: #1E293B; font-weight: 500;
}
.detail-meta-stats {
  display: flex; gap: 16px; margin-top: 10px; padding-top: 10px; border-top: 1px solid #E2E8F0;
}
.attendance-detail-records {
  display: flex; flex-direction: column; gap: 4px;
}

/* 实训计划详情卡片 */
.tp-detail-card {
  background: #fff; border: 1px solid #E2E8F0; border-radius: 12px; padding: 24px;
}
.tp-detail-meta {
  display: flex; align-items: center; flex-wrap: wrap; gap: 8px;
  margin-bottom: 12px;
}
.tp-detail-desc {
  color: #64748B; font-size: 14px; line-height: 1.6; margin-bottom: 12px;
  padding-bottom: 12px; border-bottom: 1px solid #F1F5F9;
}
.tp-detail-actions {
  display: flex; gap: 8px; margin-top: 20px; padding-top: 16px; border-top: 1px solid #F1F5F9;
}

/* Course Cards */
.course-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 16px; }
.course-card { border: 1px solid #E2E8F0; border-radius: 12px; padding: 20px; transition: all 0.2s; }
.course-card:hover { border-color: #CBD5E1; box-shadow: 0 4px 16px rgba(0,0,0,0.04); }
.cc-header { display: flex; align-items: center; gap: 14px; margin-bottom: 12px; }
.cc-icon { width: 42px; height: 42px; border-radius: 10px; background: linear-gradient(135deg, #4F46E5, #6366F1); display: flex; align-items: center; justify-content: center; }
.cc-info h3 { font-size: 16px; font-weight: 600; color: #0F172A; }
.cc-code { font-size: 12px; color: #94A3B8; }
.cc-code code { background: #F1F5F9; padding: 2px 8px; border-radius: 4px; color: #4F46E5; font-weight: 600; }
.cc-copy-btn { display: inline-flex; align-items: center; justify-content: center; width: 22px; height: 22px; border: 1px solid #C4B5FD; border-radius: 4px; background: #F5F3FF; color: #7C3AED; cursor: pointer; vertical-align: middle; margin-left: 6px; flex-shrink: 0; transition: all 0.2s; }
.cc-copy-btn:hover { background: #EDE9FE; border-color: #7C3AED; }
.cc-meta { display: flex; gap: 16px; margin-bottom: 14px; font-size: 13px; color: #64748B; }
.cc-actions { display: flex; gap: 8px; }

/* Task card delete button */
.task-delete-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 28px;
  height: 28px;
  border: none;
  background: rgba(0,0,0,0.05);
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94A3B8;
  transition: all 0.2s;
  z-index: 2;
  opacity: 0;
}
.ai-task-card:hover .task-delete-btn { opacity: 1; }
.task-delete-btn:hover { background: rgba(239,68,68,0.12); color: #EF4444; }

/* Close modal button */
.btn-close-modal {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 8px 20px;
  border: 2px solid #E2E8F0;
  background: #fff;
  color: #64748B;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
}
.btn-close-modal:hover { border-color: #94A3B8; color: #475569; background: #F8FAFC; }

/* AI Task Cards */
.ai-task-card { cursor: pointer; position: relative; overflow: hidden; transition: all 0.3s ease; }
.ai-task-card:hover { transform: translateY(-3px); box-shadow: 0 8px 24px rgba(0,0,0,0.08); }

/* Color variations for task cards */
.task-searching { border-color: #A5B4FC; background: linear-gradient(135deg, #EEF2FF 0%, #fff 100%); }
.task-searching:hover { border-color: #6366F1; }
.task-completed { border-color: #A7F3D0; background: linear-gradient(135deg, #F0FDF4 0%, #fff 100%); }
.task-completed:hover { border-color: #10B981; }
.task-failed { border-color: #FECACA; background: linear-gradient(135deg, #FFF5F5 0%, #fff 100%); }
.task-failed:hover { border-color: #EF4444; }
.task-satisfied { border-color: #A7F3D0; background: linear-gradient(135deg, #ECFDF5 0%, #fff 100%); }
.task-satisfied:hover { border-color: #059669; }

/* Task card icons */
.ai-searching-icon { background: linear-gradient(135deg, #6366F1, #818CF8) !important; animation: pulse-icon 2s infinite; }
.ai-done-icon { background: linear-gradient(135deg, #10B981, #34D399) !important; }
.ai-fail-icon { background: linear-gradient(135deg, #EF4444, #F87171) !important; }
.ai-satisfied-icon { background: linear-gradient(135deg, #059669, #10B981) !important; }

@keyframes pulse-icon {
  0%, 100% { box-shadow: 0 0 0 0 rgba(99,102,241,0.4); }
  50% { box-shadow: 0 0 0 12px rgba(99,102,241,0); }
}

.task-status-text { font-size: 12px; font-weight: 600; color: #6366F1; }
.task-status-text.done { color: #059669; }
.task-status-text.fail { color: #EF4444; }
.task-status-text.satisfied { color: #059669; }

/* Progress Bar */
.task-progress-bar { width: 100%; height: 6px; background: #E2E8F0; border-radius: 3px; overflow: hidden; margin: 8px 0 4px; }
.task-progress-fill { height: 100%; border-radius: 3px; transition: width 0.6s ease; }
.searching-animation { width: 40%; background: linear-gradient(90deg, #6366F1, #818CF8, #6366F1); background-size: 200% 100%; animation: progressSlide 1.5s infinite ease-in-out; }
.complete-bar { width: 100%; background: linear-gradient(90deg, #10B981, #34D399); }

@keyframes progressSlide {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

.task-error-msg { font-size: 12px; color: #EF4444; margin: 4px 0; line-height: 1.4; max-height: 36px; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }

.task-hint { font-size: 12px; color: #94A3B8; margin-top: 4px; font-style: italic; }
.task-hint.done-hint { color: #10B981; }
.task-hint.fail-hint { color: #EF4444; }
.task-hint.satisfied-hint { color: #059669; }

/* Task Context Banner */
.task-context-banner { background: #F0F4FF; border: 1px solid #C7D2FE; border-radius: 8px; padding: 10px 14px; margin-bottom: 16px; display: flex; gap: 8px; align-items: flex-start; }
.task-context-label { font-size: 12px; font-weight: 700; color: #4338CA; white-space: nowrap; }
.task-context-text { font-size: 13px; color: #475569; line-height: 1.4; }

/* Create form */
.create-form { padding: 8px 0; }
.form-field { margin-bottom: 14px; }
.form-field label { display: block; margin-bottom: 6px; font-size: 13px; font-weight: 600; color: #475569; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; }
.upload-zone { border: 2px dashed #E2E8F0; border-radius: 10px; padding: 28px; text-align: center; cursor: pointer; color: #94A3B8; font-size: 14px; transition: all 0.2s; display: flex; flex-direction: column; align-items: center; gap: 8px; }
.upload-zone:hover { border-color: #4F46E5; color: #4F46E5; background: rgba(79,70,229,0.03); }
.file-name { color: #4F46E5; font-weight: 600; }

/* Source Toggle */
.source-toggle { display: flex; gap: 8px; }
.source-btn { display: flex; align-items: center; gap: 8px; padding: 10px 18px; border: 2px solid #E2E8F0; background: #fff; border-radius: 10px; color: #64748B; font-size: 14px; font-weight: 500; cursor: pointer; transition: all 0.2s; font-family: inherit; flex: 1; justify-content: center; }
.source-btn:hover { border-color: #CBD5E1; color: #475569; }
.source-btn.active { border-color: #4F46E5; color: #4F46E5; background: rgba(79,70,229,0.04); box-shadow: 0 0 0 3px rgba(79,70,229,0.1); }

/* AI Search Section */
.ai-search-section { margin-top: 4px; }
.label-badge { display: inline-block; padding: 1px 7px; background: linear-gradient(135deg, #4F46E5, #6366F1); color: #fff; border-radius: 4px; font-size: 10px; font-weight: 700; vertical-align: middle; }
.field-hint { display: block; margin-top: 4px; font-size: 12px; color: #94A3B8; }

.btn-search-ai { display: inline-flex; align-items: center; gap: 8px; padding: 10px 24px; border: none; background: linear-gradient(135deg, #10B981, #059669); color: #fff; border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.2s; font-family: inherit; box-shadow: 0 2px 8px rgba(16,185,129,0.3); }
.btn-search-ai:hover:not(:disabled) { transform: translateY(-1px); box-shadow: 0 6px 20px rgba(16,185,129,0.4); }
.btn-search-ai:disabled { opacity: 0.5; cursor: not-allowed; }

/* Searching State */
.searching-state { display: flex; flex-direction: column; align-items: center; padding: 32px 20px; gap: 12px; }
.searching-spinner { width: 40px; height: 40px; border: 3px solid #E2E8F0; border-top-color: #4F46E5; border-radius: 50%; animation: spin 0.8s linear infinite; }
.searching-state p { font-size: 15px; font-weight: 600; color: #334155; margin: 0; }
.searching-hint { font-size: 13px; color: #94A3B8; }
@keyframes spin { to { transform: rotate(360deg); } }

/* Search Result */
.search-result-card { margin-top: 16px; border: 1px solid #D1FAE5; border-radius: 12px; overflow: hidden; background: #fff; }
.search-result-header { display: flex; align-items: center; justify-content: space-between; padding: 12px 16px; background: #F0FDF4; border-bottom: 1px solid #D1FAE5; }
.sr-header-left { display: flex; align-items: center; gap: 8px; font-size: 13px; font-weight: 600; color: #059669; }
.search-result-body { padding: 16px; max-height: 360px; overflow-y: auto; font-size: 13px; }
.search-result-body :deep(h1) { font-size: 18px; }
.search-result-body :deep(h2) { font-size: 15px; }
.search-result-body :deep(h3) { font-size: 14px; }
.search-result-body :deep(p), .search-result-body :deep(li) { font-size: 13px; line-height: 1.7; }

.search-result-actions { display: flex; gap: 10px; padding: 12px 16px; background: #F8FAFC; border-top: 1px solid #E2E8F0; align-items: center; }
.btn-satisfied { display: inline-flex; align-items: center; gap: 6px; padding: 8px 20px; border: none; background: #10B981; color: #fff; border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.2s; font-family: inherit; }
.btn-satisfied:hover { background: #059669; transform: translateY(-1px); }
.btn-retry { display: inline-flex; align-items: center; gap: 6px; padding: 8px 20px; border: 2px solid #E2E8F0; background: #fff; color: #64748B; border-radius: 8px; font-size: 14px; font-weight: 500; cursor: pointer; transition: all 0.2s; font-family: inherit; }
.btn-retry:hover { border-color: #F59E0B; color: #F59E0B; }
.satisfied-badge { display: inline-flex; align-items: center; gap: 6px; padding: 8px 20px; color: #059669; font-weight: 600; font-size: 14px; }

/* Search Failed State */
.search-failed-state { display: flex; flex-direction: column; align-items: center; padding: 32px 20px; gap: 12px; }
.failed-title { font-size: 16px; font-weight: 700; color: #1F2937; margin: 0; }
.failed-detail { font-size: 13px; color: #EF4444; text-align: center; margin: 0; max-width: 400px; }
.failed-actions { display: flex; gap: 10px; margin-top: 8px; }

/* Modal Bottom */
.modal-bottom-actions { margin-top: 8px; }

/* AI Tools */
.ai-tools-content { animation: fadeIn 0.3s; }
.ai-warning-banner {
  display: flex; align-items: center; gap: 8px;
  padding: 10px 16px; margin-bottom: 12px;
  background: #FFFBEB; border: 1px solid #FCD34D; border-radius: 8px;
  color: #92400E; font-size: 13px; font-weight: 500;
}
.sub-tabs { display: flex; gap: 2px; background: #F1F5F9; border-radius: 8px; padding: 4px; margin-bottom: 20px; }
.sub-tab-btn { padding: 8px 18px; border: none; background: transparent; border-radius: 6px; color: #64748B; font-size: 13px; font-weight: 500; cursor: pointer; transition: all 0.2s; font-family: inherit; }
.sub-tab-btn.active { background: #fff; color: #0F172A; font-weight: 600; box-shadow: 0 1px 3px rgba(0,0,0,0.06); }

.ai-panel { animation: fadeIn 0.3s; }
.ai-input-area { background: #F8FAFC; border-radius: 12px; padding: 20px; margin-bottom: 20px; display: flex; flex-direction: column; gap: 12px; }
.ai-input-area label { font-size: 13px; font-weight: 600; color: #475569; }
.ai-hint { font-size: 14px; color: #94A3B8; margin: 0; }

/* AI 生成进度条 */
.ai-progress-bar {
  width: 100%; height: 4px; background: #E2E8F0; border-radius: 2px;
  overflow: hidden; margin: 8px 0 16px;
}
.ai-progress-fill {
  height: 100%; width: 40%;
  background: linear-gradient(90deg, #4F46E5, #818CF8, #4F46E5);
  background-size: 200% 100%;
  border-radius: 2px;
  animation: aiProgressShimmer 1.5s ease-in-out infinite;
}
@keyframes aiProgressShimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(350%); }
}

/* Question Count Grid */
.question-count-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 12px; }
@media (max-width: 900px) { .question-count-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 500px) { .question-count-grid { grid-template-columns: repeat(2, 1fr); } }
.qc-item { display: flex; flex-direction: column; gap: 4px; }
.qc-label { font-size: 12px; font-weight: 500; color: #64748B; white-space: nowrap; }
.qc-total-badge {
  display: inline-flex; align-items: center; gap: 6px;
  margin-left: 12px; padding: 2px 10px;
  font-size: 12px; font-weight: 600; color: #059669;
  background: rgba(16,185,129,0.08); border-radius: 9999px;
  transition: all 0.2s;
}
.qc-total-badge.qc-exceeded {
  color: #DC2626; background: rgba(239,68,68,0.1);
}
.qc-warn { font-weight: 700; animation: pulse-warn 0.8s ease-in-out infinite alternate; }
@keyframes pulse-warn { from { opacity: 1; } to { opacity: 0.5; } }
.qc-field-warn { font-size: 11px; color: #DC2626; font-weight: 500; }
.ai-result-card { border: 1px solid #E2E8F0; border-radius: 12px; padding: 20px; background: #fff; }
.ai-result-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid #F1F5F9; }
.ai-result-header h3 { font-size: 16px; font-weight: 700; color: #0F172A; margin: 0; }

/* Analytics */
.analytics-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-top: 20px; }
.analytics-card { background: #fff; border: 1px solid #E2E8F0; border-radius: 12px; padding: 20px; }
.analytics-card h3 { font-size: 15px; font-weight: 600; color: #0F172A; margin-bottom: 16px; }

@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
.animate-fade-in { animation: fadeIn 0.3s ease; }

/* Token expired modal */
.token-expired-content { text-align: center; padding: 20px 10px; }
.token-expired-icon { margin-bottom: 16px; display: flex; justify-content: center; }
.token-expired-title { font-size: 15px; font-weight: 600; color: #334155; margin: 0 0 8px; }
.token-expired-desc { font-size: 13px; color: #94A3B8; margin: 0 0 24px; }

/* ===== AI Result Actions (export buttons) ===== */
.ai-result-actions { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.btn-import { color: #4F46E5; border-color: #C7D2FE; }
.btn-import:hover { background: #EEF2FF; border-color: #4F46E5; }
.btn-import-exam { color: #059669; border-color: #A7F3D0; }
.btn-import-exam:hover { background: #ECFDF5; border-color: #059669; }
.btn-courseware { color: #7C3AED; border-color: #DDD6FE; }
.btn-courseware:hover { background: #F5F3FF; border-color: #7C3AED; }

.btn-grade { color: #D97706; border-color: #FDE68A; }
.btn-grade:hover { background: #FFFBEB; border-color: #D97706; }

/* ===== Homework / Exam Cards ===== */
.homework-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(340px, 1fr)); gap: 16px; }
.homework-card { border: 1px solid #E2E8F0; border-radius: 12px; padding: 20px; transition: all 0.2s; background: #fff; }
.homework-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.04); }
.exam-card { border-left: 4px solid #F59E0B; }
.hw-header { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
.hw-header h3 { font-size: 16px; font-weight: 600; color: #0F172A; margin: 0; }
.hw-course { font-size: 12px; background: #EEF2FF; color: #4F46E5; padding: 2px 10px; border-radius: 20px; font-weight: 500; }
.exam-course { background: #FFFBEB; color: #D97706; }
.hw-status { font-size: 11px; padding: 2px 10px; border-radius: 20px; font-weight: 600; }
.status-pub { background: #D1FAE5; color: #059669; }
.status-draft { background: #F1F5F9; color: #64748B; }
.hw-desc { font-size: 13px; color: #64748B; margin: 0 0 12px; line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.hw-meta { display: flex; flex-direction: column; gap: 4px; margin-bottom: 12px; }
.hw-meta-row { display: flex; gap: 14px; font-size: 12px; color: #64748B; align-items: center; flex-wrap: wrap; }
.hw-meta-row span { white-space: nowrap; }
.hw-meta-sub { font-size: 11px; color: #94A3B8; }
.hw-meta-sub span { white-space: nowrap; }
.hw-actions { display: flex; gap: 8px; }

/* ===== Courseware Cards ===== */
.courseware-grid { display: flex; flex-direction: column; gap: 12px; }
.courseware-card {
  display: flex; align-items: center; gap: 16px;
  background: #fff; border: 1px solid #E2E8F0; border-radius: 10px; padding: 16px 20px;
  transition: box-shadow .2s;
}
.courseware-card:hover { box-shadow: 0 2px 8px rgba(0,0,0,.06); }
.cw-icon { flex-shrink: 0; color: #4F46E5; }
.cw-info { flex: 1; min-width: 0; }
.cw-title { font-size: 15px; font-weight: 600; color: #0F172A; margin: 0 0 4px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.cw-desc { font-size: 13px; color: #64748B; margin: 0 0 6px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.cw-meta { display: flex; gap: 14px; font-size: 11px; color: #94A3B8; flex-wrap: wrap; }
.cw-actions { flex-shrink: 0; display: flex; gap: 8px; }

/* 课件查看 */
.cw-view-content { max-height: 65vh; overflow-y: auto; padding: 12px 0; }

/* 课件命名弹窗 */
.cw-name-form { padding: 8px 0; }

/* 导出格式选择 */
.export-format-list { display: flex; flex-direction: column; gap: 6px; }
.export-format-item {
  display: flex; align-items: center; gap: 10px;
  padding: 10px 14px; border-radius: 8px;
  border: 1px solid #E2E8F0; cursor: pointer;
  transition: all 0.2s;
}
.export-format-item:hover { border-color: #4F46E5; background: #EEF2FF; }
.export-format-item.active { border-color: #4F46E5; background: #EEF2FF; }
.fmt-radio { font-size: 14px; color: #4F46E5; width: 20px; }
.fmt-icon { font-size: 18px; }
.fmt-label { font-size: 14px; color: #334155; font-weight: 500; }

/* 实训计划 */
.tp-template-badge {
  display: inline-block; padding: 2px 8px; border-radius: 4px;
  font-size: 11px; font-weight: 600; background: #EEF2FF; color: #4F46E5;
}
.tp-form { padding: 8px 0; }
.tp-section-editor {
  margin-bottom: 14px; padding: 12px; border: 1px solid #E2E8F0; border-radius: 8px;
  background: #FAFBFC;
}
.tp-section-header { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
.tp-section-num {
  display: inline-flex; align-items: center; justify-content: center;
  width: 22px; height: 22px; border-radius: 50%;
  background: #4F46E5; color: #fff; font-size: 12px; font-weight: 700;
}
.tp-section-name { font-size: 13px; font-weight: 600; color: #334155; }
.btn-import-training { border-color: #8B5CF6 !important; color: #7C3AED !important; }
.btn-import-training:hover { border-color: #7C3AED !important; background: #F5F3FF !important; }

/* Homework / Exam Form */
.homework-form { padding: 8px 0; }

/* ===== Attendance ===== */
.attendance-table-wrap { margin-top: 8px; }
.attendance-present { color: #10B981; font-weight: 700; font-size: 15px; }
.attendance-sep { color: #CBD5E1; margin: 0 2px; }
.attendance-total { color: #64748B; }
.attendance-record-row { display: flex; align-items: center; justify-content: space-between; padding: 10px 12px; border-bottom: 1px solid #F1F5F9; }
.att-name { font-size: 14px; font-weight: 500; color: #334155; }
.att-status { font-size: 13px; font-weight: 600; padding: 3px 12px; border-radius: 20px; }
.att-present { background: #D1FAE5; color: #059669; }
.att-absent { background: #FEE2E2; color: #DC2626; }
.att-leave { background: #F1F5F9; color: #64748B; }

/* Attendance Student List */
.as-batch-actions { display: flex; gap: 6px; }
.attendance-student-list {
  max-height: 320px; overflow-y: auto; border: 1px solid #E2E8F0; border-radius: 8px;
}
.attendance-student-row {
  display: flex; align-items: center; gap: 12px; padding: 10px 14px;
  border-bottom: 1px solid #F1F5F9; transition: background 0.15s;
}
.attendance-student-row:last-child { border-bottom: none; }
.attendance-student-row:hover { background: #F8FAFC; }
.as-name { font-size: 14px; font-weight: 500; color: #1E293B; min-width: 80px; }
.as-class { font-size: 12px; color: #94A3B8; min-width: 60px; }
.as-status-group { display: flex; gap: 6px; margin-left: auto; }
.as-status-btn {
  padding: 4px 12px; border: 1px solid #E2E8F0; border-radius: 6px;
  font-size: 12px; font-weight: 500; cursor: pointer; background: #fff;
  color: #64748B; transition: all 0.15s; font-family: inherit; white-space: nowrap;
}
.as-status-btn:hover { border-color: #CBD5E1; }
.as-status-btn.present.active { background: #D1FAE5; color: #059669; border-color: #A7F3D0; font-weight: 700; }
.as-status-btn.absent.active { background: #FEE2E2; color: #DC2626; border-color: #FECACA; font-weight: 700; }
.as-status-btn.leave.active { background: #F1F5F9; color: #475569; border-color: #CBD5E1; font-weight: 700; }

.attendance-summary {
  display: flex; gap: 16px; padding: 10px 0; margin-top: 8px; flex-wrap: wrap;
}
.as-sum-item { font-size: 13px; font-weight: 600; padding: 2px 10px; border-radius: 6px; }
.as-sum-item.present { background: #D1FAE5; color: #059669; }
.as-sum-item.absent { background: #FEE2E2; color: #DC2626; }
.as-sum-item.leave { background: #F1F5F9; color: #475569; }

/* ===== 成绩管理 ===== */
.grade-summary-cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 14px; margin-bottom: 20px; }
.grade-stat-card { background: #F8FAFC; border: 1px solid #E2E8F0; border-radius: 10px; padding: 18px; text-align: center; }
.gsc-value { display: block; font-size: 26px; font-weight: 800; color: #0F172A; }
.gsc-label { display: block; font-size: 12px; color: #94A3B8; margin-top: 4px; }

.grading-assignment-list { display: flex; flex-direction: column; gap: 10px; }
.grading-assignment-row {
  display: flex; align-items: center; gap: 16px; padding: 14px 18px;
  background: #fff; border: 1px solid #E2E8F0; border-radius: 10px; transition: box-shadow .15s;
}
.grading-assignment-row:hover { box-shadow: 0 2px 8px rgba(0,0,0,.04); }
.ga-info { display: flex; align-items: center; gap: 10px; flex: 1; min-width: 0; }
.ga-type-badge { font-size: 11px; padding: 2px 10px; border-radius: 12px; font-weight: 600; white-space: nowrap; }
.ga-hw { background: #EEF2FF; color: #4F46E5; }
.ga-exam { background: #FFFBEB; color: #D97706; }
.ga-title { font-size: 14px; font-weight: 600; color: #1E293B; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.ga-meta { display: flex; gap: 14px; font-size: 12px; color: #64748B; white-space: nowrap; }

/* ===== 题库积累 ===== */
.qb-list { display: flex; flex-direction: column; gap: 10px; }
.qb-card { border: 1px solid #E2E8F0; border-radius: 10px; padding: 16px; background: #fff; transition: box-shadow .15s; position: relative; }
.qb-card:hover { box-shadow: 0 2px 8px rgba(0,0,0,.04); }
.qb-checkbox { position: absolute; top: 16px; left: 16px; width: 16px; height: 16px; cursor: pointer; accent-color: #4F46E5; z-index: 2; }
.qb-header { display: flex; gap: 8px; margin-bottom: 8px; align-items: center; padding-left: 28px; }
/* 批量操作栏 */
.batch-bar {
  display: flex; align-items: center; gap: 14px;
  padding: 10px 16px; margin-bottom: 12px;
  background: #F8FAFC; border: 1px solid #E2E8F0; border-radius: 8px;
}
.batch-check { display: flex; align-items: center; gap: 6px; font-size: 13px; color: #475569; cursor: pointer; user-select: none; }
.batch-check input { width: 16px; height: 16px; cursor: pointer; accent-color: #4F46E5; }
.batch-count { font-size: 12px; color: #6366F1; font-weight: 600; }
.card-checkbox { width: 16px; height: 16px; cursor: pointer; accent-color: #4F46E5; flex-shrink: 0; }
.qb-type-tag { font-size: 11px; padding: 2px 10px; border-radius: 10px; background: #EEF2FF; color: #4F46E5; font-weight: 600; }
.qb-course-tag { font-size: 11px; padding: 2px 10px; border-radius: 10px; background: #F1F5F9; color: #64748B; font-weight: 500; }
.qb-difficulty { font-size: 12px; }
.qb-body { margin-bottom: 10px; }
.qb-title { font-size: 14px; font-weight: 600; color: #1E293B; margin-bottom: 6px; line-height: 1.5; }
.qb-content {
  font-size: 13px; color: #334155; line-height: 1.6; white-space: pre-wrap;
  font-family: 'SF Mono', 'Fira Code', 'Cascadia Code', Consolas, monospace;
  background: #F1F5F9; padding: 8px 12px; border-radius: 6px;
  border-left: 3px solid #6366F1; margin-bottom: 6px;
  overflow-x: auto;
}
.qb-options { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 6px; }
.qb-opt { font-size: 13px; color: #475569; background: #F8FAFC; padding: 2px 8px; border-radius: 4px; }
.qb-answer { font-size: 13px; color: #059669; font-weight: 500; }
.qb-actions { display: flex; gap: 8px; }

/* ===== 题库表单（题型专属模板） ===== */
.qb-form { padding: 8px 0; }
.qb-option-list { display: flex; flex-direction: column; gap: 8px; margin-bottom: 8px; }
.qb-option-row { display: flex; align-items: center; gap: 8px; }
.qb-option-row .qb-opt-letter {
  width: 28px; height: 28px; border-radius: 50%; background: #EEF2FF;
  display: flex; align-items: center; justify-content: center;
  font-weight: 700; font-size: 13px; color: #4F46E5; flex-shrink: 0;
}
.qb-opt-remove {
  width: 28px; height: 28px; border: 1px solid #E2E8F0; border-radius: 6px;
  background: #fff; color: #94A3B8; cursor: pointer; font-size: 12px;
  display: flex; align-items: center; justify-content: center;
  transition: all .15s; flex-shrink: 0; font-family: inherit; line-height: 1;
}
.qb-opt-remove:hover:not(:disabled) { border-color: #EF4444; color: #EF4444; background: #FFF5F5; }
.qb-opt-remove:disabled { opacity: 0.3; cursor: not-allowed; }
.qb-opt-add {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 8px 16px; border: 1px dashed #CBD5E1; border-radius: 8px;
  background: #F8FAFC; color: #4F46E5; font-size: 13px; font-weight: 500;
  cursor: pointer; transition: all .15s; font-family: inherit;
  width: 100%; justify-content: center;
}
.qb-opt-add:hover { border-color: #4F46E5; background: #EEF2FF; }

/* 填空题多空答案 */
.qb-blank-list { display: flex; flex-direction: column; gap: 8px; margin-bottom: 8px; }
.qb-blank-row { display: flex; align-items: center; gap: 8px; }
.qb-blank-num {
  font-size: 12px; font-weight: 600; color: #0D9488; background: #ECFDF5;
  padding: 4px 10px; border-radius: 6px; flex-shrink: 0; white-space: nowrap;
}
.qb-blank-remove {
  width: 28px; height: 28px; border: 1px solid #E2E8F0; border-radius: 6px;
  background: #fff; color: #94A3B8; cursor: pointer; font-size: 12px;
  display: flex; align-items: center; justify-content: center;
  transition: all .15s; flex-shrink: 0; font-family: inherit; line-height: 1;
}
.qb-blank-remove:hover:not(:disabled) { border-color: #EF4444; color: #EF4444; background: #FFF5F5; }
.qb-blank-remove:disabled { opacity: 0.3; cursor: not-allowed; }
.qb-blank-add {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 8px 16px; border: 1px dashed #CBD5E1; border-radius: 8px;
  background: #F8FAFC; color: #0D9488; font-size: 13px; font-weight: 500;
  cursor: pointer; transition: all .15s; font-family: inherit;
  width: 100%; justify-content: center;
}
.qb-blank-add:hover { border-color: #0D9488; background: #ECFDF5; }
.qb-blank-part {
  display: inline-block; background: #ECFDF5; color: #0D9488; padding: 1px 8px;
  border-radius: 4px; margin-right: 6px; font-size: 12px; font-weight: 500;
}

/* 判断题切换按钮 */
.qb-tf-toggle { display: flex; gap: 10px; }
.qb-tf-btn {
  flex: 1; display: flex; align-items: center; justify-content: center; gap: 8px;
  padding: 14px 20px; border: 2px solid #E2E8F0; border-radius: 10px;
  background: #fff; color: #64748B; font-size: 15px; font-weight: 600;
  cursor: pointer; transition: all .2s; font-family: inherit;
}
.qb-tf-btn:hover { border-color: #CBD5E1; }
.qb-tf-btn.tf-correct.active { border-color: #10B981; background: #ECFDF5; color: #059669; box-shadow: 0 0 0 3px rgba(16,185,129,0.1); }
.qb-tf-btn.tf-wrong.active { border-color: #EF4444; background: #FFF5F5; color: #DC2626; box-shadow: 0 0 0 3px rgba(239,68,68,0.1); }

/* ===== 通知中心 ===== */
.notif-list { display: flex; flex-direction: column; gap: 10px; }
.notif-card { border: 1px solid #E2E8F0; border-radius: 10px; padding: 16px; background: #fff; transition: box-shadow .15s; }
.notif-card:hover { box-shadow: 0 2px 8px rgba(0,0,0,.04); }
.notif-unread { border-left: 3px solid #4F46E5; background: #FAFBFF; }
.notif-header { display: flex; gap: 10px; align-items: center; margin-bottom: 6px; }
.notif-course { font-size: 12px; background: #EEF2FF; color: #4F46E5; padding: 2px 10px; border-radius: 10px; font-weight: 500; }
.notif-type-badge { font-size: 11px; background: #F1F5F9; color: #64748B; padding: 2px 10px; border-radius: 10px; }
.notif-time { font-size: 12px; color: #94A3B8; margin-left: auto; }
.notif-title { font-size: 15px; font-weight: 600; color: #0F172A; margin: 0 0 6px; }
.notif-content { font-size: 13px; color: #475569; margin: 0 0 10px; line-height: 1.5; }
.notif-actions { display: flex; gap: 8px; }

.notif-priority-badge { font-size: 11px; padding: 2px 10px; border-radius: 10px; font-weight: 600; }
.notif-priority-badge.np-normal { background: #F1F5F9; color: #64748B; }
.notif-priority-badge.np-important { background: #FEF3C7; color: #D97706; }
.notif-priority-badge.np-urgent { background: #FEE2E2; color: #DC2626; }

/* 通知优先级切换按钮 */
.notif-priority-toggle { display: flex; gap: 8px; }
.np-btn {
  flex: 1; padding: 8px 0; border: 2px solid #E2E8F0; border-radius: 8px;
  background: #fff; color: #64748B; font-size: 13px; font-weight: 500;
  cursor: pointer; transition: all .15s; font-family: inherit; text-align: center;
}
.np-btn:hover { border-color: #CBD5E1; }
.np-btn.np-normal.active { border-color: #64748B; background: #F8FAFC; color: #334155; }
.np-btn.np-important.active { border-color: #F59E0B; background: #FFFBEB; color: #D97706; box-shadow: 0 0 0 3px rgba(245,158,11,0.1); }
.np-btn.np-urgent.active { border-color: #EF4444; background: #FFF5F5; color: #DC2626; box-shadow: 0 0 0 3px rgba(239,68,68,0.1); }

/* ===== 模板复用 ===== */
.template-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 14px; }
.template-card { border: 1px solid #E2E8F0; border-radius: 10px; padding: 18px; background: #fff; transition: box-shadow .15s; }
.template-card:hover { box-shadow: 0 2px 8px rgba(0,0,0,.04); }
.tpl-header { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; }
.tpl-type-badge { font-size: 11px; padding: 2px 10px; border-radius: 10px; font-weight: 600; white-space: nowrap; }
.tpl-hw { background: #EEF2FF; color: #4F46E5; }
.tpl-exam { background: #FFFBEB; color: #D97706; }
.tpl-title { font-size: 15px; font-weight: 600; color: #0F172A; margin: 0; }
.tpl-meta { display: flex; gap: 14px; font-size: 12px; color: #64748B; margin-bottom: 8px; }
.tpl-time { color: #94A3B8; }
.tpl-desc { font-size: 13px; color: #64748B; margin: 0 0 12px; line-height: 1.4; }
.tpl-types { display: flex; flex-wrap: wrap; gap: 6px; margin-bottom: 8px; }
.tpl-type-chip { font-size: 11px; background: #F8FAFC; border: 1px solid #E2E8F0; padding: 2px 8px; border-radius: 10px; color: #475569; }
.tpl-actions { display: flex; gap: 8px; }

/* Template Preview */
.tpl-preview { padding: 4px 0; }
.tpl-preview-meta { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; font-size: 13px; color: #64748B; margin-bottom: 8px; }
.tpl-preview-desc { font-size: 13px; color: #475569; margin: 0 0 4px; }
.tpl-preview-questions { display: flex; flex-direction: column; gap: 10px; max-height: 420px; overflow-y: auto; }
.tpl-preview-q { border: 1px solid #E2E8F0; border-radius: 8px; padding: 12px 14px; }
.tpq-header { display: flex; align-items: center; gap: 8px; margin-bottom: 4px; }
.tpq-idx { width: 22px; height: 22px; border-radius: 50%; background: #EEF2FF; color: #4F46E5; font-size: 12px; font-weight: 700; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.tpq-type { font-size: 12px; background: #F1F5F9; padding: 2px 8px; border-radius: 4px; color: #64748B; }
.tpq-score { font-size: 12px; color: #94A3B8; margin-left: auto; }
.tpq-title { font-size: 14px; font-weight: 500; color: #1E293B; margin-bottom: 4px; }
.tpq-options { display: flex; flex-wrap: wrap; gap: 6px; margin-top: 4px; }
.tpq-opt { font-size: 12px; color: #475569; background: #F8FAFC; padding: 1px 8px; border-radius: 4px; }
.tpq-answer { font-size: 12px; color: #059669; font-weight: 500; margin-top: 4px; }

/* ===== 讨论区 ===== */
.disc-list { display: flex; flex-direction: column; gap: 10px; }
.disc-card { border: 1px solid #E2E8F0; border-radius: 10px; padding: 16px; background: #fff; cursor: pointer; transition: all .15s; }
.disc-card:hover { border-color: #CBD5E1; box-shadow: 0 2px 8px rgba(0,0,0,.04); }
.disc-card-header { display: flex; align-items: center; gap: 8px; margin-bottom: 6px; flex-wrap: wrap; }
.disc-title { font-size: 15px; font-weight: 600; color: #0F172A; margin: 0; flex: 1; }
.disc-replies { font-size: 12px; background: #EEF2FF; color: #4F46E5; padding: 2px 10px; border-radius: 10px; font-weight: 500; white-space: nowrap; }
.disc-pinned-badge { font-size: 11px; background: #FEF3C7; color: #D97706; padding: 2px 8px; border-radius: 4px; font-weight: 600; }
.disc-cat-badge { font-size: 11px; background: #F1F5F9; color: #64748B; padding: 2px 8px; border-radius: 4px; }
.disc-preview { font-size: 13px; color: #64748B; margin: 0 0 8px; line-height: 1.5; }
.disc-meta { display: flex; gap: 14px; font-size: 12px; color: #94A3B8; }

/* Topic Detail */
.topic-detail { padding: 4px 0; }
.topic-body { margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid #F1F5F9; }
.topic-meta { display: flex; align-items: center; justify-content: space-between; font-size: 12px; color: #94A3B8; margin-bottom: 10px; flex-wrap: wrap; gap: 6px; }
.topic-meta-left { display: flex; align-items: center; gap: 10px; }
.topic-meta-right { display: flex; gap: 6px; }
.topic-author { font-weight: 600; color: #334155; }
.topic-content { font-size: 14px; color: #1E293B; line-height: 1.7; white-space: pre-wrap; }
.topic-edit-area { margin-top: 8px; }

.topic-replies-section { margin-bottom: 16px; }
.reply-card { background: #F8FAFC; border-radius: 8px; padding: 12px 14px; margin-bottom: 8px; }
.reply-meta { display: flex; align-items: center; gap: 10px; margin-bottom: 4px; }
.reply-author { font-size: 13px; font-weight: 600; color: #334155; }
.reply-time { font-size: 12px; color: #94A3B8; }
.reply-actions-right { margin-left: auto; display: flex; gap: 6px; }
.reply-content { font-size: 13px; color: #475569; line-height: 1.5; }
.reply-edit-area { margin-top: 6px; }
.no-replies { text-align: center; color: #94A3B8; font-size: 13px; padding: 20px; }

.reply-input-area { margin-top: 12px; }

/* Profile Card - Full Width */
.profile-full-card { background: #fff; border-radius: 16px; padding: 28px 32px; border: 1px solid #E2E8F0; }
.profile-top { display: flex; align-items: center; gap: 20px; margin-bottom: 24px; padding-bottom: 20px; border-bottom: 1px solid #F1F5F9; }
.profile-avatar { width: 72px; height: 72px; min-width: 72px; border-radius: 50%; background: linear-gradient(135deg, #EEF2FF, #E0E7FF); display: flex; align-items: center; justify-content: center; color: #4F46E5; }
.profile-title-area { display: flex; flex-direction: column; gap: 4px; }
.profile-name { font-size: 20px; font-weight: 700; color: #0F172A; margin: 0; }
.profile-role-badge { display: inline-block; padding: 3px 12px; border-radius: 999px; background: rgba(79,70,229,0.08); color: #4F46E5; font-size: 12px; font-weight: 600; width: fit-content; }
.profile-detail-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 0; }
.detail-item { padding: 14px 0; border-bottom: 1px solid #F8FAFC; display: flex; flex-direction: column; gap: 4px; }
.detail-item.full-width { grid-column: 1 / -1; }
.detail-label { font-size: 12px; color: #94A3B8; font-weight: 500; }
.detail-value { font-size: 14px; color: #334155; font-weight: 600; display: flex; align-items: center; gap: 6px; }
.detail-value code { background: #F1F5F9; padding: 2px 8px; border-radius: 4px; font-size: 12px; color: #4F46E5; }
.btn-copy-inline { border: none; background: none; cursor: pointer; font-size: 14px; padding: 0; opacity: 0.6; }
.btn-copy-inline:hover { opacity: 1; }
</style>
