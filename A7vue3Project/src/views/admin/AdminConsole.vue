<template>
  <div class="workspace">
    <!-- Tabs -->
    <div class="tabs-bar">
      <button v-for="tab in tabs" :key="tab.key" class="tab-btn" :class="{ active: activeTab === tab.key }"
        @click="activeTab = tab.key">
        <span v-html="tab.icon"></span>{{ tab.label }}
      </button>
      <!-- 管理员管理 tab：仅超级管理员可见 -->
      <button v-if="isSuperAdmin" class="tab-btn" :class="{ active: activeTab === 'admin-management' }"
        @click="activeTab = 'admin-management'">
        <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M12 15v-3m0 0V9m0 3H9m3 0h3M5.5 4.5h13a1 1 0 0 1 1 1v13a1 1 0 0 1-1 1h-13a1 1 0 0 1-1-1v-13a1 1 0 0 1 1-1z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
        管理员管理
      </button>
    </div>

    <!-- Overview Dashboard -->
    <div v-if="activeTab === 'overview'" class="animate-fade-in">
      <!-- Mock Mode Toggle (Admin Only) -->
      <div class="mock-toggle-bar">
        <div class="mock-toggle-info">
          <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/><path d="M12 6v6l4 2" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
          <span>Mock 模式：当前为 <strong>{{ isMockEnabled() ? 'Mock' : 'Real' }}</strong> 模式</span>
        </div>
        <button class="mock-toggle-btn" @click="toggleMockMode" :class="{ active: isMockEnabled() }">
          <span class="mock-toggle-dot"></span>
          <span>{{ isMockEnabled() ? 'Mock 中' : 'Real' }}</span>
        </button>
      </div>
      <div class="stats-row">
        <StatCard icon='<svg viewBox="0 0 24 24" fill="none" width="22" height="22"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>'
          label="教师总数" :value="overview.teachers" bg="linear-gradient(135deg, #F59E0B, #D97706)" />
        <StatCard icon='<svg viewBox="0 0 24 24" fill="none" width="22" height="22"><path d="M12 14a4 4 0 1 0 0-8 4 4 0 0 0 0 8zM4 21v-1a6 6 0 0 1 6-6h4a6 6 0 0 1 6 6v1" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>'
          label="学生总数" :value="overview.students" bg="linear-gradient(135deg, #0D9488, #14B8A6)" />
        <StatCard icon='<svg viewBox="0 0 24 24" fill="none" width="22" height="22"><path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>'
          label="课程总数" :value="overview.courses" bg="linear-gradient(135deg, #4F46E5, #6366F1)" />
        <StatCard icon='<svg viewBox="0 0 24 24" fill="none" width="22" height="22"><path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>'
          label="今日活跃" :value="overview.activeToday" bg="linear-gradient(135deg, #7C3AED, #8B5CF6)" />
      </div>
      <!-- Charts Grid -->
      <div class="overview-charts">
        <div class="overview-card">
          <h3>平台活跃趋势</h3>
          <div ref="chartOverview1" style="width:100%;height:280px"></div>
        </div>
        <div class="overview-card">
          <h3>用户角色分布</h3>
          <div ref="chartOverview2" style="width:100%;height:280px"></div>
        </div>
        <div class="overview-card full-width">
          <h3>课程热度排行</h3>
          <div ref="chartOverview3" style="width:100%;height:280px"></div>
        </div>
      </div>
    </div>

    <!-- User Management -->
    <div v-if="activeTab === 'users'" class="panel animate-fade-in">
      <div class="panel-header">
        <h2>用户管理</h2>
        <div class="header-actions" style="display:flex;gap:8px;align-items:center">
          <a-input v-model:value="userKeyword" placeholder="输入关键字搜索..." style="width:200px" @pressEnter="searchUsers" />
          <a-button type="primary" @click="searchUsers">搜索</a-button>
          <a-button @click="resetUserSearch">重置</a-button>
          <button class="btn-primary" @click="openUserModal('add')">
            <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/></svg>
            添加用户
          </button>
        </div>
      </div>
      <!-- Role sub-tabs -->
      <div class="sub-tabs" style="margin-bottom:16px">
        <button class="sub-tab-btn" :class="{ active: userRole === 'student' }" @click="userRole = 'student'">学生</button>
        <button class="sub-tab-btn" :class="{ active: userRole === 'teacher' }" @click="userRole = 'teacher'">教师</button>
      </div>
      <a-table :columns="userColumns" :dataSource="userList" rowKey="id" :pagination="{ pageSize: 8 }">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'actions'">
            <button class="btn-sm" @click="openUserModal('edit', record)">编辑</button>
            <a-popconfirm title="确定删除?" ok-text="确定" cancel-text="取消" @confirm="handleDeleteUser(record)">
              <button class="btn-sm btn-sm-danger" style="margin-left:8px">删除</button>
            </a-popconfirm>
          </template>
        </template>
      </a-table>

      <!-- User Modal -->
      <a-modal v-model:open="userModalVisible" :title="userModalMode === 'add' ? '添加用户' : '编辑用户'" :footer="null" width="520px">
        <div class="user-form">
          <div class="form-field">
            <label>用户名</label>
            <a-input v-model:value="userForm.username" placeholder="登录账号" />
          </div>
          <div class="form-field">
            <label>{{ userModalMode === 'add' ? '密码' : '新密码 (留空不修改)' }}</label>
            <a-input-password v-model:value="userForm.password" placeholder="密码" />
          </div>
          <div class="form-row" v-if="userRole !== 'admin'">
            <div class="form-field">
              <label>姓名</label>
              <a-input v-model:value="userForm.name" placeholder="真实姓名" />
            </div>
            <div class="form-field">
              <label>{{ userRole === 'student' ? '班级' : '职位' }}</label>
              <a-input v-model:value="userForm.extra" placeholder="" />
            </div>
          </div>
          <button class="btn-primary" style="width:100%;margin-top:16px" @click="handleSaveUser" :disabled="userSaving">
            {{ userSaving ? '保存中...' : '保存' }}
          </button>
        </div>
      </a-modal>
    </div>

    <!-- Resource Management -->
    <div v-if="activeTab === 'resources'" class="panel animate-fade-in">
      <div class="panel-header">
        <h2>资源管理</h2>
        <div style="display:flex;gap:8px;align-items:center">
          <a-input v-model:value="resKeyword" placeholder="输入关键字搜索..." style="width:200px" @pressEnter="searchResources" />
          <a-button type="primary" @click="searchResources">搜索</a-button>
          <a-button @click="resetResSearch">重置</a-button>
        </div>
      </div>
      <div class="sub-tabs" style="margin-bottom:16px">
        <button class="sub-tab-btn" :class="{ active: resType === 'courses' }" @click="resType = 'courses'">课程</button>
        <button class="sub-tab-btn" :class="{ active: resType === 'issues' }" @click="resType = 'issues'">练习</button>
        <button class="sub-tab-btn" :class="{ active: resType === 'resources' }" @click="resType = 'resources'">课件</button>
      </div>
      <a-table :columns="resColumns" :dataSource="resList" rowKey="id" :pagination="{ pageSize: 8 }">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'actions'">
            <a-popconfirm title="确定删除?此操作不可撤销。" ok-text="确定" cancel-text="取消" @confirm="handleDeleteResource(record)">
              <button class="btn-sm btn-sm-danger">删除</button>
            </a-popconfirm>
          </template>
        </template>
      </a-table>
    </div>

    <!-- Analytics -->
    <div v-if="activeTab === 'analytics'" class="panel animate-fade-in">
      <div class="panel-header"><h2>系统分析</h2></div>
      <div class="analytics-grid">
        <div class="analytics-card">
          <h3>练习正确率趋势</h3>
          <div ref="chartA1" style="width:100%;height:300px"></div>
        </div>
        <div class="analytics-card">
          <h3>知识点掌握度分布</h3>
          <div ref="chartA2" style="width:100%;height:300px"></div>
        </div>
        <div class="analytics-card full-width">
          <h3>各课程平均耗时 (分钟)</h3>
          <div ref="chartA3" style="width:100%;height:300px"></div>
        </div>
      </div>
    </div>
    <!-- Admin Management (Super Admin Only) -->
    <div v-if="activeTab === 'admin-management'" class="panel animate-fade-in">
      <div class="panel-header">
        <h2>管理员管理</h2>
        <div style="display:flex;gap:8px;align-items:center">
          <a-input v-model:value="adminKeyword" placeholder="输入关键字搜索..." style="width:200px" @pressEnter="searchAdmins" />
          <a-button type="primary" @click="searchAdmins">搜索</a-button>
          <a-button @click="resetAdminSearch">重置</a-button>
          <button class="btn-primary" @click="openAdminModal('add')">
          <svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/></svg>
          添加管理员
        </button>
        </div>
      </div>
      <!-- Batch bar -->
      <div v-if="adminSelectedIds.size > 0" class="batch-bar">
        <input type="checkbox" :checked="adminAllSelected" @change="toggleAdminSelectAll" class="batch-check" />
        <span class="batch-count">已选 {{ adminSelectedIds.size }} 项</span>
        <a-popconfirm title="确定批量删除选中的管理员?" ok-text="确定" cancel-text="取消" @confirm="deleteSelectedAdmins">
          <button class="btn-sm btn-sm-danger">批量删除</button>
        </a-popconfirm>
      </div>
      <a-table :columns="adminMgmtColumns" :dataSource="adminMgmtList" rowKey="id" :pagination="{ pageSize: 8 }">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'checkbox'">
            <input type="checkbox" :checked="adminSelectedIds.has(record.username)" @click.stop="toggleAdminSelect(record.username)" class="card-checkbox" />
          </template>
          <template v-if="column.key === 'type'">
            <span class="admin-type-tag" :class="record.isSuperAdmin ? 'super' : 'regular'">
              {{ record.isSuperAdmin ? '超级管理员' : '普通管理员' }}
            </span>
          </template>
          <template v-if="column.key === 'actions'">
            <button class="btn-sm" @click="openAdminModal('edit', record)">编辑</button>
            <a-popconfirm title="确定删除该管理员?" ok-text="确定" cancel-text="取消"
              :disabled="record.username === currentAdminUsername"
              @confirm="handleDeleteAdmin(record)">
              <button class="btn-sm btn-sm-danger" style="margin-left:8px" :disabled="record.username === currentAdminUsername">
                {{ record.username === currentAdminUsername ? '当前用户' : '删除' }}
              </button>
            </a-popconfirm>
          </template>
        </template>
      </a-table>

      <!-- Admin Modal -->
      <a-modal v-model:open="adminModalVisible" :title="adminModalMode === 'add' ? '添加管理员' : '编辑管理员'" :footer="null" width="520px">
        <div class="user-form">
          <div class="form-field">
            <label>用户名</label>
            <a-input v-model:value="adminForm.username" placeholder="登录账号" />
          </div>
          <div class="form-field">
            <label>{{ adminModalMode === 'add' ? '密码' : '新密码 (留空不修改)' }}</label>
            <a-input-password v-model:value="adminForm.password" placeholder="密码" />
          </div>
          <div class="form-field">
            <label>姓名</label>
            <a-input v-model:value="adminForm.name" placeholder="真实姓名" />
          </div>
          <div class="form-field" style="display:flex;align-items:center;gap:12px">
            <label style="margin-bottom:0">设为超级管理员</label>
            <a-switch v-model:checked="adminForm.isSuperAdmin" />
            <span style="font-size:12px;color:#94A3B8">{{ adminForm.isSuperAdmin ? '拥有全部管理权限' : '仅基础管理权限' }}</span>
          </div>
          <button class="btn-primary" style="width:100%;margin-top:16px" @click="handleSaveAdmin" :disabled="adminSaving">
            {{ adminSaving ? '保存中...' : '保存' }}
          </button>
        </div>
      </a-modal>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { message } from 'ant-design-vue'
import { jwtDecode } from 'jwt-decode'
import * as echarts from 'echarts'
import StatCard from '../../components/common/StatCard.vue'
import { addStudentApi, deleteStudentByUsernameApi, updateStudentApi } from '../../api/admin/StudentListApi'
import { addTeacherApi, deleteTeacherByUsernameApi, updateTeacherApi } from '../../api/admin/TeacherListApi'
import { addAdminApi, deleteAdminByUsernameApi, deleteAdminBySelectedApi, updateAdminApi } from '../../api/admin/AdminListApi'
import { deleteCourseApi, deleteIssueApi, deleteResourceApi, getIssueListApi, getResourceListApi } from '../../api/admin/ResourceManagerApi'
import { getSelfAdminDetailApi } from '../../api/admin/getSelfAdminDetailApi'
import { isMockEnabled, enableMock, disableMock } from '../../mock/index'

// Navigation — persist active tab across refreshes
const ADMIN_TAB_KEY = 'admin_active_tab'
const tabs = [
  { key: 'overview', label: '数据大屏', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><rect x="3" y="3" width="7" height="7" rx="1" stroke="currentColor" stroke-width="2"/><rect x="14" y="3" width="7" height="7" rx="1" stroke="currentColor" stroke-width="2"/><rect x="3" y="14" width="7" height="7" rx="1" stroke="currentColor" stroke-width="2"/><rect x="14" y="14" width="7" height="7" rx="1" stroke="currentColor" stroke-width="2"/></svg>' },
  { key: 'users', label: '用户管理', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8zM23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>' },
  { key: 'resources', label: '资源管理', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>' },
  { key: 'analytics', label: '系统分析', icon: '<svg viewBox="0 0 24 24" fill="none" width="16" height="16"><path d="M18 20V10M12 20V4M6 20v-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>' },
]
function getSavedTab(): string {
  const saved = sessionStorage.getItem(ADMIN_TAB_KEY)
  if (saved && tabs.find(t => t.key === saved)) return saved
  if (saved === 'admin-management') return saved
  return 'overview'
}
const activeTab = ref(getSavedTab())
watch(activeTab, (tab) => { sessionStorage.setItem(ADMIN_TAB_KEY, tab) })

// Super admin state from JWT
const isSuperAdmin = ref(false)
const currentAdminUsername = ref('')

function readTokenInfo() {
  const token = (sessionStorage.getItem('token') || localStorage.getItem('token'))
  if (!token) return
  try {
    const decoded: any = jwtDecode(token)
    const isSA = decoded?.isSuperAdmin
    isSuperAdmin.value = isSA === true || isSA === 'true'
    currentAdminUsername.value = decoded?.sub || ''
  } catch { /* ignore */ }
}
readTokenInfo()

// Mock Mode Toggle
function toggleMockMode() {
  if (isMockEnabled()) {
    disableMock()
  } else {
    enableMock()
  }
}

// Overview
const overview = reactive({ teachers: 0, students: 0, courses: 0, activeToday: 0 })
const chartOverview1 = ref<HTMLElement | null>(null)
const chartOverview2 = ref<HTMLElement | null>(null)
const chartOverview3 = ref<HTMLElement | null>(null)
// Store ECharts instances for disposal
const chartInstances: any[] = []

function initOverviewCharts() {
  if (chartOverview1.value) {
    const existing = echarts.getInstanceByDom(chartOverview1.value)
    if (existing) existing.dispose()
    const c = echarts.init(chartOverview1.value)
    chartInstances.push(c)
    c.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { data: ['周一','周二','周三','周四','周五','周六','周日'] },
      yAxis: {},
      series: [
        { name: '教师', type: 'line', data: [12,15,18,14,20,8,5], smooth: true, color: '#F59E0B' },
        { name: '学生', type: 'line', data: [45,60,72,55,80,30,15], smooth: true, color: '#0D9488' },
      ],
    })
  }
  if (chartOverview2.value) {
    const existing = echarts.getInstanceByDom(chartOverview2.value)
    if (existing) existing.dispose()
    const c = echarts.init(chartOverview2.value)
    chartInstances.push(c)
    c.setOption({
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie', radius: ['55%', '78%'],
        data: [
          { name: '学生', value: overview.students || 300, itemStyle: { color: '#0D9488' } },
          { name: '教师', value: overview.teachers || 20, itemStyle: { color: '#F59E0B' } },
          { name: '管理员', value: 3, itemStyle: { color: '#7C3AED' } },
        ],
        label: { formatter: '{b}\n{d}%' },
      }],
    })
  }
  if (chartOverview3.value) {
    const existing = echarts.getInstanceByDom(chartOverview3.value)
    if (existing) existing.dispose()
    const c = echarts.init(chartOverview3.value)
    chartInstances.push(c)
    c.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: ['数据结构', '操作系统', '计组', 'AI导论', '软件工程'] },
      yAxis: { type: 'value' },
      series: [{ type: 'bar', data: [85,72,60,95,78], itemStyle: { borderRadius: [8,8,0,0], color: '#4F46E5' } }],
    })
  }
}

watch(activeTab, async (tab) => {
  if (tab !== 'overview') return
  await nextTick()
  initOverviewCharts()
}, { immediate: true })

// User Management
const userRole = ref<'student' | 'teacher' | 'admin'>('student')
const userList = ref<any[]>([])
const userKeyword = ref('')
const userListSource = ref<any[]>([])
const userColumns = [
  { title: '用户名', dataIndex: 'username', key: 'username' },
  { title: '姓名', dataIndex: 'name', key: 'name' },
  { title: '创建时间', dataIndex: 'create_time', key: 'create_time' },
  { title: '操作', key: 'actions', width: 180 },
]
const userModalVisible = ref(false)
const userModalMode = ref<'add' | 'edit'>('add')
const userSaving = ref(false)
const userForm = reactive({ username: '', password: '', name: '', extra: '', id: '' })

function openUserModal(mode: 'add' | 'edit', record?: any) {
  userModalMode.value = mode
  if (mode === 'edit' && record) {
    userForm.username = record.username; userForm.password = ''; userForm.name = record.name || ''; userForm.id = record.id
  } else {
    userForm.username = ''; userForm.password = ''; userForm.name = ''; userForm.extra = ''; userForm.id = ''
  }
  userModalVisible.value = true
}

async function handleSaveUser() {
  if (!userForm.username) { message.warning('请输入用户名'); return }
  if (userModalMode.value === 'add' && !userForm.password) { message.warning('请输入密码'); return }
  userSaving.value = true
  try {
    let res: any
    const data: any = {}
    if (userRole.value === 'student') {
      data.student_account = userForm.username; data.student_password = userForm.password; data.student_name = userForm.name || userForm.username; data.student_class_name = userForm.extra
      res = userModalMode.value === 'add' ? await addStudentApi(data) : await updateStudentApi(data)
    } else if (userRole.value === 'teacher') {
      data.teacher_account = userForm.username; data.teacher_password = userForm.password; data.teacher_name = userForm.name || userForm.username; data.teacher_position = userForm.extra
      res = userModalMode.value === 'add' ? await addTeacherApi(data) : await updateTeacherApi(data)
    } else {
      data.username = userForm.username; data.password = userForm.password
      if (userModalMode.value === 'edit') data.id = userForm.id
      res = userModalMode.value === 'add' ? await addAdminApi(data) : await updateAdminApi(data)
    }
    if (res.data?.code === 200) { message.success(userModalMode.value === 'add' ? '添加成功' : '更新成功'); userModalVisible.value = false; await fetchAllData(); loadUserList() }
    else { message.error(res.data?.msg || '操作失败') }
  } catch { message.error('操作失败') }
  userSaving.value = false
}

async function handleDeleteUser(record: any) {
  try {
    let res: any
    if (userRole.value === 'student') res = await deleteStudentByUsernameApi(record.username || record.student_account)
    else if (userRole.value === 'teacher') res = await deleteTeacherByUsernameApi(record.username || record.teacher_account)
    else res = await deleteAdminByUsernameApi(record.username)
    if (res.data?.code === 200) { message.success('已删除'); await fetchAllData(); loadUserList() }
  } catch { message.error('删除失败') }
}

// Admin Management (Super Admin Only)
const adminMgmtList = ref<any[]>([])
const adminMgmtListSource = ref<any[]>([])
const adminKeyword = ref('')
const adminMgmtColumns = [
  { title: '', key: 'checkbox', width: 40 },
  { title: '用户名', dataIndex: 'username', key: 'username' },
  { title: '姓名', dataIndex: 'name', key: 'name' },
  { title: '类型', key: 'type', width: 120 },
  { title: '创建时间', dataIndex: 'create_time', key: 'create_time' },
  { title: '操作', key: 'actions', width: 200 },
]
const adminModalVisible = ref(false)
const adminModalMode = ref<'add' | 'edit'>('add')
const adminSaving = ref(false)
const adminForm = reactive({ username: '', password: '', name: '', isSuperAdmin: false })
const adminSelectedIds = ref<Set<string>>(new Set())
const adminAllSelected = computed(() => {
  return adminMgmtList.value.length > 0 && adminMgmtList.value.every(a => adminSelectedIds.value.has(a.username))
})

function toggleAdminSelectAll() {
  if (adminAllSelected.value) {
    adminSelectedIds.value = new Set()
  } else {
    adminSelectedIds.value = new Set(adminMgmtList.value.map(a => a.username))
  }
}
function toggleAdminSelect(username: string) {
  const next = new Set(adminSelectedIds.value)
  if (next.has(username)) next.delete(username)
  else next.add(username)
  adminSelectedIds.value = next
}

function openAdminModal(mode: 'add' | 'edit', record?: any) {
  adminModalMode.value = mode
  if (mode === 'edit' && record) {
    adminForm.username = record.username || ''
    adminForm.password = ''
    adminForm.name = record.name || ''
    adminForm.isSuperAdmin = !!record.isSuperAdmin
  } else {
    adminForm.username = ''
    adminForm.password = ''
    adminForm.name = ''
    adminForm.isSuperAdmin = false
  }
  adminModalVisible.value = true
}

async function handleSaveAdmin() {
  if (!adminForm.username) { message.warning('请输入用户名'); return }
  if (adminModalMode.value === 'add' && !adminForm.password) { message.warning('请输入密码'); return }
  adminSaving.value = true
  try {
    const data: any = { username: adminForm.username, password: adminForm.password, name: adminForm.name, isSuperAdmin: adminForm.isSuperAdmin }
    let res: any
    if (adminModalMode.value === 'add') {
      res = await addAdminApi(data)
    } else {
      res = await updateAdminApi(data)
    }
    if (res.data?.code === 200) {
      message.success(adminModalMode.value === 'add' ? '添加管理员成功' : '更新管理员成功')
      adminModalVisible.value = false
      await fetchAllData()
    } else {
      message.error(res.data?.msg || '操作失败')
    }
  } catch { message.error('操作失败') }
  adminSaving.value = false
}

async function handleDeleteAdmin(record: any) {
  try {
    const res = await deleteAdminByUsernameApi(record.username)
    if (res.data?.code === 200) {
      message.success('已删除')
      await fetchAllData()
    } else {
      message.error(res.data?.msg || '删除失败')
    }
  } catch { message.error('删除失败') }
}

async function deleteSelectedAdmins() {
  try {
    const usernames = [...adminSelectedIds.value]
    const res = await deleteAdminBySelectedApi(usernames)
    if (res.data?.code === 200) {
      message.success('批量删除成功')
      adminSelectedIds.value = new Set()
      await fetchAllData()
    } else {
      message.error(res.data?.msg || '批量删除失败')
    }
  } catch { message.error('批量删除失败') }
}

function searchAdmins() { adminMgmtList.value = filterByKeyword(adminMgmtListSource.value, adminKeyword.value) }
function resetAdminSearch() { adminKeyword.value = ''; adminMgmtList.value = [...adminMgmtListSource.value] }

// Resource Management
const resType = ref('courses')
const resList = ref<any[]>([])
const resListSource = ref<any[]>([])
const resKeyword = ref('')
const resColumns = [
  { title: '名称', dataIndex: 'name', key: 'name' },
  { title: '关联', dataIndex: 'related', key: 'related' },
  { title: '创建时间', dataIndex: 'create_time', key: 'create_time' },
  { title: '操作', key: 'actions', width: 100 },
]

async function handleDeleteResource(record: any) {
  try {
    let res: any
    if (resType.value === 'courses') res = await deleteCourseApi({ data: record.name })
    else if (resType.value === 'issues') res = await deleteIssueApi({ question_id: record.id })
    else res = await deleteResourceApi({ resource_id: record.id })
    if (res.data?.code === 200) { message.success('已删除'); loadResList() }
  } catch { message.error('删除失败') }
}

// Analytics
const chartA1 = ref<HTMLElement | null>(null)
const chartA2 = ref<HTMLElement | null>(null)
const chartA3 = ref<HTMLElement | null>(null)

function initAnalyticsCharts() {
  if (chartA1.value) {
    const existing = echarts.getInstanceByDom(chartA1.value)
    if (existing) existing.dispose()
    const c = echarts.init(chartA1.value)
    chartInstances.push(c)
    c.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { data: ['1月','2月','3月','4月','5月','6月'] },
      yAxis: { max: 100 },
      series: [{ name: '平均正确率', type: 'line', data: [60,65,72,78,82,88], smooth: true, areaStyle: { opacity: 0.1 }, color: '#4F46E5' }],
    })
  }
  if (chartA2.value) {
    const existing = echarts.getInstanceByDom(chartA2.value)
    if (existing) existing.dispose()
    const c = echarts.init(chartA2.value)
    chartInstances.push(c)
    c.setOption({
      radar: {
        indicator: [
          { name: '基础知识', max: 100 }, { name: '进阶应用', max: 100 }, { name: '实践能力', max: 100 },
          { name: '综合分析', max: 100 }, { name: '创新能力', max: 100 },
        ],
      },
      series: [{ type: 'radar', data: [{ value: [85, 72, 68, 60, 45], name: '平均掌握度', areaStyle: { color: 'rgba(79,70,229,0.2)' } }], color: ['#4F46E5'] }],
    })
  }
  if (chartA3.value) {
    const existing = echarts.getInstanceByDom(chartA3.value)
    if (existing) existing.dispose()
    const c = echarts.init(chartA3.value)
    chartInstances.push(c)
    c.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: ['数据结构', '操作系统', '计组', 'AI导论', '软件工程'] },
      yAxis: { type: 'value', name: '分钟' },
      series: [{ type: 'bar', data: [45, 38, 52, 30, 42], itemStyle: { borderRadius: [8,8,0,0], color: '#0D9488' } }],
    })
  }
}

watch(() => activeTab.value === 'analytics', async (isActive) => {
  if (!isActive) return
  await nextTick()
  initAnalyticsCharts()
}, { immediate: true })

// Navigation event handler
const navHandler = (e: Event) => {
  const detail = (e as CustomEvent).detail
  if (detail && (tabs.find(t => t.key === detail) || detail === 'admin-management')) activeTab.value = detail
}

// All data from API
const allStudents = ref<any[]>([])
const allTeachers = ref<any[]>([])
const allAdmins = ref<any[]>([])

// Load real data from API
async function fetchAllData() {
  try {
    const token = (sessionStorage.getItem('token') || localStorage.getItem('token'))
    if (!token) return
    const decoded: any = jwtDecode(token)
    const username = decoded?.sub
    if (!username) return
    const res = await getSelfAdminDetailApi(username)
    if (res.data?.code === 200 && res.data.data) {
      const d = res.data.data
      allStudents.value = (d.all_student_list || []).map((s: any) => ({
        ...s,
        id: s.student_id,
        username: s.student_account,
        name: s.student_name,
      }))
      allTeachers.value = (d.all_teacher_list || []).map((t: any) => ({
        ...t,
        id: t.teacher_id,
        username: t.teacher_account,
        name: t.teacher_name,
      }))
      allAdmins.value = (d.all_admin_list || []).map((a: any) => ({
        ...a,
        id: a.id || a.username,
      }))
      // Update overview stats
      overview.teachers = allTeachers.value.length
      overview.students = allStudents.value.length
      overview.courses = (d.all_course_list || []).length || allTeachers.value.reduce((sum: number, t: any) => sum + (t.teacher_courseList?.length || 0), 0)
      overview.activeToday = 0 // Will be populated by click records if available
      // Refresh admin management list
      adminMgmtListSource.value = [...allAdmins.value]
      adminMgmtList.value = filterByKeyword(adminMgmtListSource.value, adminKeyword.value)
    }
  } catch (e) {
    console.error('Failed to fetch admin detail', e)
  }
}

// Reusable keyword filter: matches if any field value contains the keyword (fuzzy)
function filterByKeyword<T extends Record<string, any>>(list: T[], keyword: string): T[] {
  const term = keyword.trim().toLowerCase()
  if (!term) return [...list]
  const regex = new RegExp(term.split('').map(c => c.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')).join('.*'), 'i')
  return list.filter(item =>
    Object.values(item).some(v => v != null && String(v).toLowerCase().match(regex))
  )
}

// Load user list based on sub-tab
function loadUserList() {
  if (userRole.value === 'student') {
    userListSource.value = [...allStudents.value]
  } else if (userRole.value === 'teacher') {
    userListSource.value = [...allTeachers.value]
  } else {
    if (isSuperAdmin.value) {
      userListSource.value = [...allAdmins.value]
    } else {
      userListSource.value = []
    }
  }
  userList.value = filterByKeyword(userListSource.value, userKeyword.value)
}

function searchUsers() { userList.value = filterByKeyword(userListSource.value, userKeyword.value) }
function resetUserSearch() { userKeyword.value = ''; userList.value = [...userListSource.value] }

// Load resource list based on sub-tab
async function loadResList() {
  try {
    if (resType.value === 'courses') {
      // Get all courses from teacher data
      const courses: any[] = []
      allTeachers.value.forEach((t: any) => {
        (t.teacher_courseList || []).forEach((c: any) => {
          courses.push({
            id: c.course_id || c.id,
            name: c.course_name || c.name,
            related: t.teacher_name || t.teacher_account,
            create_time: c.create_time || '',
          })
        })
      })
      resListSource.value = courses
    } else if (resType.value === 'issues') {
      // Fetch issues from all teachers' courses
      const issues: any[] = []
      for (const t of allTeachers.value) {
        const courses = t.teacher_courseList || []
        for (const c of courses) {
          try {
            const r = await getIssueListApi({ data: c.course_name, teacher_username: t.teacher_account })
            if (r.data?.code === 200 && r.data.data) {
              const list = Array.isArray(r.data.data) ? r.data.data : []
              list.forEach((q: any) => issues.push({
                id: q.question_id,
                name: q.question_title || q.issue_name || '题目',
                related: c.course_name,
                create_time: q.create_time || '',
              }))
            }
          } catch { /* skip */ }
        }
      }
      resListSource.value = issues
    } else {
      // Fetch resources from all teachers' courses
      const resources: any[] = []
      for (const t of allTeachers.value) {
        const courses = t.teacher_courseList || []
        for (const c of courses) {
          try {
            const r = await getResourceListApi({ data: c.course_name, teacher_username: t.teacher_account })
            if (r.data?.code === 200 && r.data.data) {
              const list = Array.isArray(r.data.data) ? r.data.data : []
              list.forEach((res: any) => resources.push({
                id: res.resource_id || res.id,
                name: res.resource_name || res.name || '课件',
                related: c.course_name,
                create_time: res.create_time || '',
              }))
            }
          } catch { /* skip */ }
        }
      }
      resListSource.value = resources
    }
    resList.value = filterByKeyword(resListSource.value, resKeyword.value)
  } catch { /* keep current list */ }
}

function searchResources() { resList.value = filterByKeyword(resListSource.value, resKeyword.value) }
function resetResSearch() { resKeyword.value = ''; resList.value = [...resListSource.value] }

// 切换 tab 时同步到左侧侧边栏高亮
watch(activeTab, (tab) => {
  window.dispatchEvent(new CustomEvent('sync-sidebar', { detail: tab }))
})

// Watch role/resource type changes to reload data
watch(userRole, () => { loadUserList() })
watch(resType, () => { loadResList() })

// Load initial data
onMounted(async () => {
  window.addEventListener('workspace-nav', navHandler)
  await fetchAllData()
  loadUserList()
  loadResList()
  // Trigger overview chart render if on overview tab
  if (activeTab.value === 'overview') initOverviewCharts()
})

onUnmounted(() => {
  window.removeEventListener('workspace-nav', navHandler)
  // Dispose all ECharts instances
  chartInstances.forEach(c => { try { c.dispose() } catch { /* ignore */ } })
  chartInstances.length = 0
})
</script>

<style scoped>
.workspace { padding: 24px; max-width: 1400px; margin: 0 auto; }

/* Mock Toggle Bar */
.mock-toggle-bar {
  display: flex; align-items: center; justify-content: space-between;
  background: #fff; border: 1px solid #E2E8F0; border-radius: 12px;
  padding: 12px 20px; margin-bottom: 20px;
}
.mock-toggle-info {
  display: flex; align-items: center; gap: 8px; font-size: 14px; color: #475569;
}
.mock-toggle-info strong { color: #0F172A; }
.mock-toggle-btn {
  display: flex; align-items: center; gap: 8px;
  padding: 8px 16px; border: 2px solid #E2E8F0; background: #F8FAFC;
  border-radius: 20px; font-size: 13px; font-weight: 600; cursor: pointer;
  transition: all 0.3s; font-family: inherit; color: #64748B;
}
.mock-toggle-btn:hover { border-color: #CBD5E1; }
.mock-toggle-btn.active {
  background: linear-gradient(135deg, #FEF3C7, #FDE68A); border-color: #F59E0B; color: #92400E;
}
.mock-toggle-dot {
  width: 10px; height: 10px; border-radius: 50%;
  background: #10B981; transition: all 0.3s;
  box-shadow: 0 0 8px rgba(16, 185, 129, 0.5);
}
.mock-toggle-btn.active .mock-toggle-dot {
  background: #F59E0B;
  box-shadow: 0 0 8px rgba(245, 158, 11, 0.6);
  animation: pulse-dot 2s ease-in-out infinite;
}
@keyframes pulse-dot {
  0%, 100% { box-shadow: 0 0 8px rgba(245, 158, 11, 0.6); }
  50% { box-shadow: 0 0 16px rgba(245, 158, 11, 0.9); }
}

.stats-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 24px; }
@media (max-width: 1100px) { .stats-row { grid-template-columns: repeat(2, 1fr); } }

.tabs-bar { display: flex; gap: 4px; background: #fff; border-radius: 12px; padding: 6px; margin-bottom: 20px; border: 1px solid #E2E8F0; }
.tab-btn { display: flex; align-items: center; gap: 8px; padding: 10px 20px; border: none; background: transparent; border-radius: 8px; color: #64748B; font-size: 14px; font-weight: 500; cursor: pointer; transition: all 0.2s; font-family: inherit; }
.tab-btn:hover { color: #334155; background: #F1F5F9; }
.tab-btn.active { background: #7C3AED; color: #fff; box-shadow: 0 2px 8px rgba(124,58,237,0.3); }

.panel { background: #fff; border-radius: 16px; border: 1px solid #E2E8F0; padding: 24px; min-height: 400px; }
.panel-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; }
.panel-header h2 { font-size: 18px; font-weight: 700; color: #0F172A; }

.btn-primary { display: inline-flex; align-items: center; gap: 8px; padding: 10px 22px; border: none; background: linear-gradient(135deg, #7C3AED, #8B5CF6); color: #fff; border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.2s; font-family: inherit; box-shadow: 0 2px 8px rgba(124,58,237,0.25); }
.btn-primary:hover:not(:disabled) { transform: translateY(-1px); box-shadow: 0 6px 20px rgba(124,58,237,0.35); }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-sm { display: inline-flex; align-items: center; gap: 6px; padding: 6px 14px; border: 1px solid #E2E8F0; background: #fff; border-radius: 6px; color: #475569; font-size: 13px; font-weight: 500; cursor: pointer; transition: all 0.2s; font-family: inherit; }
.btn-sm:hover { border-color: #7C3AED; color: #7C3AED; }
.btn-sm-danger { color: #EF4444; }
.btn-sm-danger:hover { border-color: #EF4444; background: rgba(239,68,68,0.05); }

.sub-tabs { display: flex; gap: 2px; background: #F1F5F9; border-radius: 8px; padding: 4px; }
.sub-tab-btn { padding: 7px 16px; border: none; background: none; border-radius: 6px; font-size: 13px; font-weight: 500; cursor: pointer; transition: all 0.2s; font-family: inherit; color: #64748B; }
.sub-tab-btn.active { background: #fff; color: #0F172A; font-weight: 600; box-shadow: 0 1px 3px rgba(0,0,0,0.06); }

/* Overview */
.overview-charts { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.overview-card { background: #fff; border: 1px solid #E2E8F0; border-radius: 12px; padding: 20px; }
.overview-card h3 { font-size: 15px; font-weight: 600; color: #0F172A; margin-bottom: 16px; }
.overview-card.full-width { grid-column: 1 / -1; }

/* Forms */
.form-field { margin-bottom: 14px; }
.form-field label { display: block; margin-bottom: 6px; font-size: 13px; font-weight: 600; color: #475569; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; }
.user-form { padding: 8px 0; }

/* Analytics */
.analytics-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-top: 16px; }
.analytics-card { background: #fff; border: 1px solid #E2E8F0; border-radius: 12px; padding: 20px; }
.analytics-card h3 { font-size: 15px; font-weight: 600; color: #0F172A; margin-bottom: 16px; }
.analytics-card.full-width { grid-column: 1 / -1; }

.admin-type-tag {
  display: inline-block; padding: 2px 10px; border-radius: 10px; font-size: 12px; font-weight: 600;
}
.admin-type-tag.super { background: #FEF3C7; color: #92400E; }
.admin-type-tag.regular { background: #E0E7FF; color: #3730A3; }

.batch-bar {
  display: flex; align-items: center; gap: 12px;
  padding: 10px 16px; background: #FEF3C7; border: 1px solid #FDE68A;
  border-radius: 8px; margin-bottom: 16px;
}
.batch-check { width: 18px; height: 18px; cursor: pointer; accent-color: #7C3AED; }
.batch-count { font-size: 13px; font-weight: 600; color: #92400E; }
.card-checkbox { width: 16px; height: 16px; cursor: pointer; accent-color: #7C3AED; }

@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
.animate-fade-in { animation: fadeIn 0.3s ease; }
</style>
