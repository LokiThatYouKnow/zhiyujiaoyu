<template>
    <div class="list-page">
        <div class="page-toolbar">
            <h2 class="page-title">教师列表</h2>
            <div class="toolbar-actions">
                <a-input placeholder="输入关键字搜索..." v-model:value="keyword" class="search-input" />
                <a-button type="primary" @click="search">搜索</a-button>
                <a-button @click="resetSearch">重置</a-button>
                <a-button type="primary" @click="addTeacherTable">新增教师</a-button>
                <a-popconfirm title="确定要删除选中教师吗?" ok-text="确定" cancel-text="取消" @confirm="deleteTeacherBySelected"
                    ok-type="danger" :disabled="notSelected">
                    <a-button type="primary" danger :disabled="notSelected">删除选中</a-button>
                </a-popconfirm>
                <a-button type="primary" @click="backToHome" class="btn-back">返回首页</a-button>
            </div>
        </div>
        <a-table :columns="columns" :data-source="teacherList"
            :row-selection="{ selectedRowKeys: usernames, onChange: onSelectChange }" row-Key="teacher_account"
            :pagination="pagination" class="modern-table">
            <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'action'">
                    <span class="action-btns">
                        <a-button type="primary" size="small" @click="openUpdate(record)">修改</a-button>
                        <a-popconfirm title="确定要删除该教师吗?" ok-text="确定" cancel-text="取消"
                            @confirm="deleteByUsername(record.teacher_account)" ok-type="danger">
                            <a-button type="primary" danger size="small">删除</a-button>
                        </a-popconfirm>
                    </span>
                </template>
            </template>
        </a-table>

        <a-modal :open="addTableVisible" @ok="addTeacher" @cancel="addTeacherTableBackToTeacherList" okText="添加" cancelText="取消" style="top: 20%;">
            <a-form name="add" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }" autocomplete="off">
                <div class="modal-title">添加教师</div>
                <a-form-item label="账户" name="account" v-bind="validateInfos.account"><a-input v-model:value="addTeacherRequest.teacher_account" /></a-form-item>
                <a-form-item label="密码" name="password" v-bind="validateInfos.password"><a-input-password v-model:value="addTeacherRequest.teacher_password" /></a-form-item>
                <a-form-item label="姓名" name="name" v-bind="validateInfos.name"><a-input v-model:value="addTeacherRequest.teacher_name" /></a-form-item>
                <a-form-item label="年龄" name="age" v-bind="validateInfos.age"><a-input-number v-model:value="addTeacherRequest.teacher_age" :min="1" :max="100" /></a-form-item>
                <a-form-item label="性别" name="gender">
                    <a-radio-group v-model:value="addTeacherRequest.teacher_gender" v-bind="validateInfos.gender">
                        <a-radio-button value="男">男</a-radio-button>
                        <a-radio-button value="女">女</a-radio-button>
                    </a-radio-group>
                </a-form-item>
                <a-form-item label="职位" name="position" v-bind="validateInfos.position">
                    <a-select v-model:value="addTeacherRequest.teacher_position" placeholder="请选择职称/职位">
                        <a-select-option value="讲师">教师</a-select-option>
                        <a-select-option value="班主任">班主任</a-select-option>
                    </a-select>
                </a-form-item>
                <template v-if="addTeacherRequest.teacher_position === '班主任'">
                    <a-form-item label="专业" name="major" v-bind="validateInfos.major"><a-input v-model:value="addTeacherRequest.teacher_major" /></a-form-item>
                    <a-form-item label="班级" name="class_name" v-bind="validateInfos.class_name"><a-input v-model:value="addTeacherRequest.teacher_class_name" /></a-form-item>
                </template>
            </a-form>
        </a-modal>

        <a-modal :open="updateTableVisible" @ok="updateTeacher" @cancel="updateTeacherTableBackToTeacherList" okText="修改" cancelText="取消" style="top: 21%;">
            <a-form name="update" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }" autocomplete="off">
                <div class="modal-title">修改教师</div>
                <a-form-item label="账户" name="account" v-bind="validateInfos.account"><a-input v-model:value="selectUpdateTeacher.teacher_account" /></a-form-item>
                <a-form-item label="密码" name="password" v-bind="validateInfos.password"><a-input-password v-model:value="selectUpdateTeacher.teacher_password" /><strong>如果要重置密码就在此输入新密码，否则不用填写</strong></a-form-item>
                <a-form-item label="姓名" name="name" v-bind="validateInfos.name"><a-input v-model:value="selectUpdateTeacher.teacher_name" /></a-form-item>
                <a-form-item label="年龄" name="age" v-bind="validateInfos.age"><a-input-number v-model:value="selectUpdateTeacher.teacher_age" :min="1" :max="100" /></a-form-item>
                <a-form-item label="性别" name="gender">
                    <a-radio-group v-model:value="selectUpdateTeacher.teacher_gender" v-bind="validateInfos.gender">
                        <a-radio-button value="男">男</a-radio-button>
                        <a-radio-button value="女">女</a-radio-button>
                    </a-radio-group>
                </a-form-item>
                <a-form-item label="职位" name="position" v-bind="validateInfos.position">
                    <a-select v-model:value="selectUpdateTeacher.teacher_position" placeholder="请选择职称/职位">
                        <a-select-option value="讲师">教师</a-select-option>
                        <a-select-option value="班主任">班主任</a-select-option>
                    </a-select>
                </a-form-item>
                <template v-if="selectUpdateTeacher.teacher_position === '班主任'">
                    <a-form-item label="专业" name="major" v-bind="validateInfos.major"><a-input v-model:value="selectUpdateTeacher.teacher_major" /></a-form-item>
                    <a-form-item label="班级" name="class_name" v-bind="validateInfos.class_name"><a-input v-model:value="selectUpdateTeacher.teacher_class_name" /></a-form-item>
                </template>
            </a-form>
        </a-modal>
    </div>
</template>

<script lang="ts" setup>
import { ref, shallowRef } from 'vue'
import type { teacher } from '../../modules/teacher'
import type { R } from '../../response/R'
import { Form, message } from 'ant-design-vue'
import { getSelfAdminDetailApi } from '../../api/admin/getSelfAdminDetailApi'
import type { adminVO } from '../../modules/adminVO'
import { jwtDecode } from 'jwt-decode'
import type { admin } from '../../modules/admin'
import { addTeacherApi, deleteTeacherBySelectedApi, deleteTeacherByUsernameApi, updateTeacherApi } from '../../api/admin/TeacherListApi'
import type { student } from '../../modules/student'
import router from '../../routers/appRouters'

const keyword = ref('')
const teacherList = ref<teacher[]>([])
const usernames = ref<string[]>([])
const originalTeacherList = shallowRef<teacher[]>([]);
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
const setTeacherList = () => { teacherList.value = adminVODetail.value.all_teacher_list }
if (localStorage.getItem('adminDetail')) { setSelfAdminDetail(); setTeacherList() }
else { getSelfAdminDetail(); setSelfAdminDetail(); setTeacherList() }

const deleteByUsername = async (username: string) => {
    const R: R = (await deleteTeacherByUsernameApi(username)).data
    if (R.code === 200) { message.success("删除教师成功", 2); localStorage.removeItem("adminDetail"); getSelfAdminDetail(); setTimeout(() => { setSelfAdminDetail(); setTeacherList() }, 1000) }
    else { message.error("删除教师失败", 2) }
}

const selectUpdateTeacher = ref<teacher>({ teacher_id:0, teacher_account: "", teacher_password: "", teacher_name: "", teacher_gender: "", teacher_age: "", teacher_major: "", teacher_position: "", teacher_class_name: "", teacher_class_student_number:"", teacher_binding_code:"" })
const openUpdate = (rowData: teacher) => { selectUpdateTeacher.value = { ...rowData }; updateTableVisible.value = true }
const updateTeacher = async () => {
    if(selectUpdateTeacher.value.teacher_position === "班主任"){
        if (!selectUpdateTeacher.value.teacher_account) { message.error('请填写账号'); return }
        else if (!selectUpdateTeacher.value.teacher_name) { message.error('请填写姓名'); return }
        else if (!selectUpdateTeacher.value.teacher_age) { message.error('请填写年龄'); return }
        else if (!selectUpdateTeacher.value.teacher_major) { message.error("请填写专业"); return }
        else if (!selectUpdateTeacher.value.teacher_class_name) { message.error("请填写班级名称"); return }
    } else {
        if (!selectUpdateTeacher.value.teacher_account) { message.error('请填写账号'); return }
        else if (!selectUpdateTeacher.value.teacher_name) { message.error('请填写姓名'); return }
        else if (!selectUpdateTeacher.value.teacher_age) { message.error('请填写年龄'); return }
        else if (!selectUpdateTeacher.value.teacher_position) { message.error("请选择职位"); return }
    }
    const R: R = (await updateTeacherApi(selectUpdateTeacher.value)).data
    if (R.code === 200) { updateTableVisible.value = false; message.success("修改教师成功", 2); localStorage.removeItem("adminDetail"); getSelfAdminDetail(); setTimeout(() => { setSelfAdminDetail(); setTeacherList() }, 1000) }
    else { message.error("修改教师失败", 2) }
}
const updateTeacherTableBackToTeacherList = () => { updateTableVisible.value = false }

const search = () => {
    const searchTerm = keyword.value.trim().toLowerCase();
    if (originalTeacherList.value.length === 0 && teacherList.value.length > 0) { originalTeacherList.value = [...teacherList.value] }
    if (!searchTerm) { teacherList.value = [...originalTeacherList.value]; return }
    const regex = new RegExp(searchTerm.split('').map(char => char.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')).join('.*'), 'i');
    teacherList.value = originalTeacherList.value.filter(teacher => (
        teacher.teacher_account?.toLowerCase().match(regex) || teacher.teacher_name?.toLowerCase().match(regex) || teacher.teacher_gender?.toLowerCase().match(regex) || teacher.teacher_age?.toLowerCase().match(regex) || teacher.teacher_major?.toLowerCase().match(regex) || teacher.teacher_class_name?.toLowerCase().match(regex) || teacher.teacher_position?.toLowerCase().match(regex)
    ));
};
const resetSearch = () => { keyword.value = ''; if (originalTeacherList.value.length > 0) { teacherList.value = [...originalTeacherList.value] } }

const addTeacherTable = () => { addTableVisible.value = true }
const addTeacherRequest = ref<teacher>({ teacher_account: "", teacher_password: "", teacher_name: "", teacher_gender: "男", teacher_age: "", teacher_major: "", teacher_position:"", teacher_class_name:"" });
const addTeacher = async () => {
    if (addTeacherRequest.value.teacher_position === "班主任") {
        if (!addTeacherRequest.value.teacher_account) { message.error('请填写账号'); return }
        else if (!addTeacherRequest.value.teacher_name) { message.error('请填写姓名'); return }
        else if (!addTeacherRequest.value.teacher_age) { message.error('请填写年龄'); return }
        else if (!addTeacherRequest.value.teacher_major) { message.error("请填写专业"); return }
        else if (!addTeacherRequest.value.teacher_class_name) { message.error("请填写班级名称"); return }
    } else {
        if (!addTeacherRequest.value.teacher_account) { message.error('请填写账号'); return }
        else if (!addTeacherRequest.value.teacher_name) { message.error('请填写姓名'); return }
        else if (!addTeacherRequest.value.teacher_age) { message.error('请填写年龄'); return }
        else if (!addTeacherRequest.value.teacher_position) { message.error("请选择职位"); return }
    }
    const R: R = (await addTeacherApi(addTeacherRequest.value)).data
    if (R.code === 200) { addTableVisible.value = false; message.success(R.msg, 2); localStorage.removeItem("adminDetail"); getSelfAdminDetail(); setTimeout(() => { setSelfAdminDetail(); setTeacherList() }, 1000); addTeacherRequest.value.teacher_account = ""; addTeacherRequest.value.teacher_password = ""; addTeacherRequest.value.teacher_name = ""; addTeacherRequest.value.teacher_age = ""; addTeacherRequest.value.teacher_major = ""; addTeacherRequest.value.teacher_position = ""; addTeacherRequest.value.teacher_class_name = "" }
    else { message.error(R.msg, 2) }
}
const addTeacherTableBackToTeacherList = () => { addTableVisible.value = false }

const deleteTeacherBySelected = async () => {
    try {
        const R: R = (await deleteTeacherBySelectedApi(usernames.value)).data
        if (R.code === 200) { message.success("批量删除教师成功", 2); localStorage.removeItem("adminDetail"); getSelfAdminDetail(); setTimeout(() => { setSelfAdminDetail(); setTeacherList() }, 1000) }
        else { message.error("删除失败", 2) }
        usernames.value = []; notSelected.value = true
    } catch (err) { console.log(err) }
}
function onSelectChange(keys: string[]) { usernames.value = keys; notSelected.value = usernames.value.length > 0 ? false : true }

const columns = [
    { title: '用户名', dataIndex: 'teacher_account', key: 'username' },
    { title: '姓名', dataIndex: 'teacher_name', key: 'name' },
    { title: '性别', dataIndex: 'teacher_gender', key: 'gender' },
    { title: '年龄', dataIndex: 'teacher_age', key: 'age' },
    { title: '专业', key: 'major', dataIndex: 'teacher_major' },
    { title: '职位', key: 'position', dataIndex: 'teacher_position' },
    { title: '班级', key: 'class_name', dataIndex: 'teacher_class_name' },
    { title: '班级人数', key: 'teacher_class_student_number', dataIndex: 'teacher_class_student_number' },
    { title: '教师绑定码', key: 'teacher_binding_code', dataIndex: 'teacher_binding_code' },
    { title: '操作', key: 'action' },
];
const pagination = ref({ current: 1, pageSize: 6, total: teacherList.value.length, onChange: (page: number, _pageSize: number) => { pagination.value.current = page } });
const rules = ref({
    account: [{ required: true, message: '账户不能为空', trigger: 'blur' }],
    password: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
    name: [{ required: true, message: '姓名不能为空', trigger: 'blur' }],
    age: [{ required: true, message: '年龄不能为空', trigger: 'blur' }],
    gender: [{ required: true, message: '性别不能为空', trigger: 'blur' }],
    major: [{ required: true, message: '专业不能为空', trigger: 'blur' }],
    position: [{ required: true, message: '职位不能为空', trigger: 'blur' }],
    class_name: [{ required: true, message: '班级不能为空', trigger: 'blur' }],
})
const { validateInfos } = Form.useForm([addTeacherRequest, addTeacherRequest], rules)
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
