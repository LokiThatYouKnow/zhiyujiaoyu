<template>
    <div class="list-page">
        <div class="page-toolbar">
            <h2 class="page-title">学生列表</h2>
            <div class="toolbar-actions">
                <a-input placeholder="输入关键字搜索..." v-model:value="keyword" class="search-input" />
                <a-button type="primary" @click="search">搜索</a-button>
                <a-button @click="resetSearch">重置</a-button>
                <a-button type="primary" @click="addStudentTable">新增学生</a-button>
                <a-popconfirm title="确定要删除选中学生吗?" ok-text="确定" cancel-text="取消" @confirm="deleteStudentBySelected"
                    ok-type="danger" :disabled="notSelected">
                    <a-button type="primary" danger :disabled="notSelected">删除选中</a-button>
                </a-popconfirm>
                <a-button type="primary" @click="backToHome" class="btn-back">返回首页</a-button>
            </div>
        </div>
        <a-table :columns="columns" :data-source="studentList"
            :row-selection="{ selectedRowKeys: usernames, onChange: onSelectChange }" row-Key="student_account"
            :pagination="pagination" class="modern-table">
            <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'action'">
                    <span class="action-btns">
                        <a-button type="primary" size="small" @click="openUpdate(record)">修改</a-button>
                        <a-popconfirm title="确定要删除该学生吗?" ok-text="确定" cancel-text="取消"
                            @confirm="deleteByUsername(record.student_account)" ok-type="danger">
                            <a-button type="primary" danger size="small">删除</a-button>
                        </a-popconfirm>
                    </span>
                </template>
            </template>
        </a-table>

        <a-modal :open="addTableVisible" @ok="addStudent" @cancel="addStudentTableBackToStudentList" okText="添加" cancelText="取消" style="top: 20%;">
            <a-form name="add" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }" autocomplete="off">
                <div class="modal-title">添加学生</div>
                <a-form-item label="账户" name="account" v-bind="validateInfos.account"><a-input v-model:value="addStudentRequest.student_account" /></a-form-item>
                <a-form-item label="密码" name="password" v-bind="validateInfos.password"><a-input-password v-model:value="addStudentRequest.student_password" /></a-form-item>
                <a-form-item label="姓名" name="name" v-bind="validateInfos.name"><a-input v-model:value="addStudentRequest.student_name" /></a-form-item>
                <a-form-item label="年龄" name="age" v-bind="validateInfos.age"><a-input-number v-model:value="addStudentRequest.student_age" :min="1" :max="100" /></a-form-item>
                <a-form-item label="性别" name="gender">
                    <a-radio-group v-model:value="addStudentRequest.student_gender" v-bind="validateInfos.gender">
                        <a-radio-button value="男">男</a-radio-button>
                        <a-radio-button value="女">女</a-radio-button>
                    </a-radio-group>
                </a-form-item>
                <a-form-item label="教师绑定码" name="class"><a-input v-model:value="addStudentRequest.teacher_binding_code" /><strong>若没有则不用填</strong></a-form-item>
            </a-form>
        </a-modal>

        <a-modal :open="updateTableVisible" @ok="updateStudent" @cancel="updateStudentTableBackToStudentList" okText="修改" cancelText="取消" style="top: 21%;">
            <a-form name="update" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }" autocomplete="off">
                <div class="modal-title">修改学生</div>
                <a-form-item label="账户" name="account" v-bind="validateInfos.account"><a-input v-model:value="selectUpdateStudent.student_account" /></a-form-item>
                <a-form-item label="密码" name="password" v-bind="validateInfos.password"><a-input-password v-model:value="selectUpdateStudent.student_password" /><strong>如果要重置密码就在此输入新密码，否则不用填写</strong></a-form-item>
                <a-form-item label="姓名" name="name" v-bind="validateInfos.name"><a-input v-model:value="selectUpdateStudent.student_name" /></a-form-item>
                <a-form-item label="年龄" name="age" v-bind="validateInfos.age"><a-input-number v-model:value="selectUpdateStudent.student_age" :min="1" :max="100" /></a-form-item>
                <a-form-item label="性别" name="gender">
                    <a-radio-group v-model:value="selectUpdateStudent.student_gender" v-bind="validateInfos.gender">
                        <a-radio-button value="男">男</a-radio-button>
                        <a-radio-button value="女">女</a-radio-button>
                    </a-radio-group>
                </a-form-item>
                <a-form-item label="教师绑定码" name="class"><a-input v-model:value="selectUpdateStudent.teacher_binding_code" /><strong>若没有则不用填</strong></a-form-item>
            </a-form>
        </a-modal>
    </div>
</template>

<script lang="ts" setup>
import { ref, shallowRef } from 'vue'
import type { student } from '../../modules/student'
import type { R } from '../../response/R'
import { Form, message } from 'ant-design-vue'
import { getSelfAdminDetailApi } from '../../api/admin/getSelfAdminDetailApi'
import type { adminVO } from '../../modules/adminVO'
import { jwtDecode } from 'jwt-decode'
import type { admin } from '../../modules/admin'
import type { teacher } from '../../modules/teacher'
import { addStudentApi, deleteStudentBySelectedApi, deleteStudentByUsernameApi, updateStudentApi } from '../../api/admin/StudentListApi'
import router from '../../routers/appRouters'

const keyword = ref('')
const studentList = ref<student[]>([])
const usernames = ref<string[]>([])
const originalStudentList = shallowRef<student[]>([]);
const notSelected = ref<boolean>(true)
const addTableVisible = ref(false)
const updateTableVisible = ref(false)
const token = ref((sessionStorage.getItem('token') || localStorage.getItem('token')))
const admin_usernmae = jwtDecode(token.value as string).sub as string
const adminVODetail = ref<adminVO>({
    admin_id: 0, admin_account: '', admin_password: '', admin_name: '',
    all_admin_list: [] as admin[], all_teacher_list: [] as teacher[], all_student_list: [] as student[],
})

const getSelfAdminDetail = async () => {
    await getSelfAdminDetailApi(admin_usernmae).then(res => {
        const R: R = res.data
        if (R.code === 200) { adminVODetail.value = R.data; localStorage.setItem('adminDetail', JSON.stringify(adminVODetail.value)) }
        else { message.error('获取管理员信息失败') }
    }).catch(err => { message.error('获取管理员信息失败'); console.log(err) })
}
const setSelfAdminDetail = () => { adminVODetail.value = JSON.parse(localStorage.getItem('adminDetail') as string) }
const setStudentList = () => { studentList.value = adminVODetail.value.all_student_list }
if (localStorage.getItem('adminDetail')) { setSelfAdminDetail(); setStudentList() }
else { getSelfAdminDetail(); setSelfAdminDetail(); setStudentList() }

const deleteByUsername = async (username: string) => {
    const R: R = (await deleteStudentByUsernameApi(username)).data
    if (R.code === 200) { message.success("删除学生成功", 2); localStorage.removeItem("adminDetail"); getSelfAdminDetail(); setTimeout(() => { setSelfAdminDetail(); setStudentList() }, 1000) }
    else { message.error("删除学生失败", 2) }
}

const selectUpdateStudent = ref<student>({ student_account: "", student_password: "", student_name: "", student_gender: "", student_age: "", student_major: "", teacher_binding_code: "" })
const openUpdate = (rowData: student) => { selectUpdateStudent.value = { ...rowData }; updateTableVisible.value = true }
const updateStudent = async () => {
    const R: R = (await updateStudentApi(selectUpdateStudent.value)).data
    if (R.code === 200) { updateTableVisible.value = false; message.success("修改学生成功", 2); localStorage.removeItem("adminDetail"); getSelfAdminDetail(); setTimeout(() => { setSelfAdminDetail(); setStudentList() }, 1000) }
    else { message.error("修改学生失败", 2) }
}
const updateStudentTableBackToStudentList = () => { updateTableVisible.value = false }

const search = () => {
    const searchTerm = keyword.value.trim().toLowerCase();
    if (originalStudentList.value.length === 0 && studentList.value.length > 0) { originalStudentList.value = [...studentList.value] }
    if (!searchTerm) { studentList.value = [...originalStudentList.value]; return }
    const regex = new RegExp(searchTerm.split('').map(char => char.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')).join('.*'), 'i');
    studentList.value = originalStudentList.value.filter(student => (
        student.student_account?.toLowerCase().match(regex) || student.student_name?.toLowerCase().match(regex) || student.student_gender?.toLowerCase().match(regex) || student.student_age?.toLowerCase().match(regex) || student.student_major?.toLowerCase().match(regex) || student.student_class_name?.toLowerCase().match(regex) || student.teacher_binding_code?.toLowerCase().match(regex)
    ));
};
const resetSearch = () => { keyword.value = ''; if (originalStudentList.value.length > 0) { studentList.value = [...originalStudentList.value] } }

const addStudentTable = () => { addTableVisible.value = true }
const addStudentRequest = ref<student>({ student_account: "", student_password: "", student_name: "", student_gender: "男", student_age: "", teacher_binding_code: "" });
const addStudent = async () => {
    if (addStudentRequest.value.student_account === "") { message.error('请填写账号'); return }
    else if (addStudentRequest.value.student_password === "") { message.error('请填写密码'); return }
    else if (addStudentRequest.value.student_name === "") { message.error('请填写姓名'); return }
    else if (addStudentRequest.value.student_age === "") { message.error('请填写年龄'); return }
    const R: R = (await addStudentApi(addStudentRequest.value)).data
    if (R.code === 200) { addTableVisible.value = false; message.success(R.msg, 2); localStorage.removeItem("adminDetail"); getSelfAdminDetail(); setTimeout(() => { setSelfAdminDetail(); setStudentList() }, 1000); addStudentRequest.value.student_account = ""; addStudentRequest.value.student_password = ""; addStudentRequest.value.student_name = ""; addStudentRequest.value.student_age = ""; addStudentRequest.value.teacher_binding_code = "" }
    else { message.error(R.msg, 2) }
}
const addStudentTableBackToStudentList = () => { addTableVisible.value = false }

const deleteStudentBySelected = async () => {
    try {
        const R: R = (await deleteStudentBySelectedApi(usernames.value)).data
        if (R.code === 200) { message.success("批量删除学生成功", 2); localStorage.removeItem("adminDetail"); getSelfAdminDetail(); setTimeout(() => { setSelfAdminDetail(); setStudentList() }, 1000) }
        else { message.error("删除失败", 2) }
        usernames.value = []; notSelected.value = true
    } catch (err) { console.log(err) }
}
function onSelectChange(keys: string[]) { usernames.value = keys; notSelected.value = usernames.value.length > 0 ? false : true }

const columns = [
    { title: '用户名', dataIndex: 'student_account', key: 'username' },
    { title: '姓名', dataIndex: 'student_name', key: 'name' },
    { title: '性别', dataIndex: 'student_gender', key: 'gender' },
    { title: '年龄', dataIndex: 'student_age', key: 'age' },
    { title: '专业', key: 'major', dataIndex: 'student_major' },
    { title: '班级', key: 'class_name', dataIndex: 'student_class_name' },
    { title: '教师绑定码', key: 'teacher_binding_code', dataIndex: 'teacher_binding_code' },
    { title: '操作', key: 'action' },
];

const pagination = ref({ current: 1, pageSize: 6, total: studentList.value.length, onChange: (page: number, _pageSize: number) => { pagination.value.current = page } });

const rules = ref({
    account: [{ required: true, message: '账户不能为空', trigger: 'blur' }],
    password: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
    name: [{ required: true, message: '姓名不能为空', trigger: 'blur' }],
    age: [{ required: true, message: '年龄不能为空', trigger: 'blur' }],
    gender: [{ required: true, message: '性别不能为空', trigger: 'blur' }],
})
const { validateInfos } = Form.useForm([addStudentRequest, addStudentRequest], rules)
const backToHome = () => { router.push({ name: 'HomeView' }) }
</script>

<style scoped>
.list-page { padding: 8px 4px; }

.page-toolbar {
  background: white;
  border-radius: 16px;
  padding: 20px 24px;
  margin-bottom: 20px;
  border: 1px solid #E2E8F0;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
}
.page-title {
  font-size: 1.6rem;
  font-weight: 800;
  color: #0F172A;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 10px;
}
.page-title::before {
  content: '';
  width: 4px; height: 28px;
  background: linear-gradient(180deg, #4F46E5, #8B5CF6);
  border-radius: 2px;
  display: inline-block;
}
.toolbar-actions { display: flex; gap: 10px; flex-wrap: wrap; align-items: center; }
.search-input { max-width: 240px; }
.btn-back { background: linear-gradient(135deg, #F59E0B, #D97706) !important; border: none !important; font-weight: 700 !important; }
.action-btns { display: flex; gap: 8px; }

.modal-title { font-size: 20px; font-weight: 700; color: #0F172A; margin-bottom: 16px; text-align: center; }

.modern-table {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid #E2E8F0;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
  padding: 0 4px;
}
</style>
