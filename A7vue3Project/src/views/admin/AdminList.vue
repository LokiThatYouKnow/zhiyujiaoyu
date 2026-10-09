<template>
    <div class="list-page">
        <div class="page-toolbar">
            <h2 class="page-title">管理员列表</h2>
            <div class="toolbar-actions">
                <a-input placeholder="输入关键字搜索..." v-model:value="keyword" class="search-input" />
                <a-button type="primary" @click="search">搜索</a-button>
                <a-button @click="resetSearch">重置</a-button>
                <a-button type="primary" @click="addAdminTable">新增管理员</a-button>
                <a-popconfirm title="确定要删除选中管理员吗?" ok-text="确定" cancel-text="取消" @confirm="deleteAdminBySelected"
                    ok-type="danger" :disabled="notSelected">
                    <a-button type="primary" danger :disabled="notSelected">删除选中</a-button>
                </a-popconfirm>
                <a-button type="primary" @click="backToHome" class="btn-back">返回首页</a-button>
            </div>
        </div>
        <a-table :columns="columns" :data-source="adminList"
            :row-selection="{ selectedRowKeys: usernames, onChange: onSelectChange }" row-Key="username"
            :pagination="pagination" class="modern-table">
            <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'action'">
                    <span class="action-btns">
                        <a-button type="primary" size="small" @click="openUpdate(record)">修改</a-button>
                        <a-popconfirm title="确定要删除该管理员吗?" ok-text="确定" cancel-text="取消"
                            @confirm="deleteByUsername(record.username)" ok-type="danger">
                            <a-button type="primary" danger size="small">删除</a-button>
                        </a-popconfirm>
                    </span>
                </template>
            </template>
        </a-table>

        <a-modal :open="addTableVisible" @ok="addAdmin" @cancel="addAdminTableBackToAdminList" okText="添加" cancelText="取消" style="top: 20%;">
            <a-form name="add" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }" autocomplete="off">
                <div class="modal-title">添加管理员</div>
                <a-form-item label="账户" name="account" v-bind="validateInfos.account"><a-input v-model:value="addAdminRequest.username" /></a-form-item>
                <a-form-item label="密码" name="password" v-bind="validateInfos.password"><a-input-password v-model:value="addAdminRequest.password" /></a-form-item>
                <a-form-item label="姓名" name="name" v-bind="validateInfos.name"><a-input v-model:value="addAdminRequest.name" /></a-form-item>
            </a-form>
        </a-modal>

        <a-modal :open="updateTableVisible" @ok="updateAdmin" @cancel="updateAdminTableBackToAdminList" okText="修改" cancelText="取消" style="top: 21%;">
            <a-form name="update" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }" autocomplete="off">
                <div class="modal-title">修改管理员</div>
                <a-form-item label="账户" name="account" v-bind="validateInfos.account"><a-input v-model:value="selectUpdateAdmin.username" /></a-form-item>
                <a-form-item label="密码" name="password" v-bind="validateInfos.password"><a-input-password v-model:value="selectUpdateAdmin.password" /><strong>如果忘记原密码就在此输入新密码，否则不用填写</strong></a-form-item>
                <a-form-item label="姓名" name="name" v-bind="validateInfos.name"><a-input v-model:value="selectUpdateAdmin.name" /></a-form-item>
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
import { addAdminApi, deleteAdminBySelectedApi, deleteAdminByUsernameApi, updateAdminApi } from '../../api/admin/AdminListApi'
import router from '../../routers/appRouters'

const keyword = ref('')
const adminList = ref<admin[]>([])
const usernames = ref<string[]>([])
const originalAdminList = shallowRef<admin[]>([]);
const notSelected = ref<boolean>(true)
const addTableVisible = ref(false)
const updateTableVisible = ref(false)
const token = ref((sessionStorage.getItem('token') || localStorage.getItem('token')))
const admin_usernmae = jwtDecode(token.value as string).sub as string
const adminVODetail = ref<adminVO>({
    admin_id: 0, admin_account: '', admin_password: '', admin_name: '',
    all_student_list: [] as student[], all_teacher_list: [] as teacher[], all_admin_list: [] as admin[],
})

const getSelfAdminDetail = async () => {
    await getSelfAdminDetailApi(admin_usernmae).then(res => {
        const R: R = res.data
        if (R.code === 200) { adminVODetail.value = R.data; localStorage.setItem('adminDetail', JSON.stringify(adminVODetail.value)) }
        else { message.error('获取管理员信息失败') }
    }).catch(err => { message.error('获取管理员信息失败'); console.log(err) })
}
const setSelfAdminDetail = () => { adminVODetail.value = JSON.parse(localStorage.getItem('adminDetail') as string) }
const setAdminList = () => { adminList.value = adminVODetail.value.all_admin_list }
if (localStorage.getItem('adminDetail')) { setSelfAdminDetail(); setAdminList() }
else { getSelfAdminDetail(); setSelfAdminDetail(); setAdminList() }

const deleteByUsername = async (username: string) => {
    const R: R = (await deleteAdminByUsernameApi(username)).data
    if (R.code === 200) { message.success("删除管理员成功", 2); localStorage.removeItem("adminDetail"); getSelfAdminDetail(); setTimeout(() => { setSelfAdminDetail(); setAdminList() }, 1000) }
    else { message.error("删除管理员失败", 2) }
}

const selectUpdateAdmin = ref<admin>({ username: "", password: "", name: "" })
const openUpdate = (rowData: admin) => { selectUpdateAdmin.value = { ...rowData }; updateTableVisible.value = true }
const updateAdmin = async () => {
    const R: R = (await updateAdminApi(selectUpdateAdmin.value)).data
    if (R.code === 200) { updateTableVisible.value = false; message.success("修改管理员成功", 2); localStorage.removeItem("adminDetail"); getSelfAdminDetail(); setTimeout(() => { setSelfAdminDetail(); setAdminList() }, 1000) }
    else { message.error("修改管理员失败", 2) }
}
const updateAdminTableBackToAdminList = () => { updateTableVisible.value = false }

const search = () => {
    const searchTerm = keyword.value.trim().toLowerCase();
    if (originalAdminList.value.length === 0 && adminList.value.length > 0) { originalAdminList.value = [...adminList.value] }
    if (!searchTerm) { adminList.value = [...originalAdminList.value]; return }
    const regex = new RegExp(searchTerm.split('').map(char => char.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')).join('.*'), 'i');
    adminList.value = originalAdminList.value.filter(admin => (admin.username?.toLowerCase().match(regex) || admin.name?.toLowerCase().match(regex)));
};
const resetSearch = () => { keyword.value = ''; if (originalAdminList.value.length > 0) { adminList.value = [...originalAdminList.value] } }

const addAdminTable = () => { addTableVisible.value = true }
const addAdminRequest = ref<admin>({ username: "", password: "", name: "" });
const addAdmin = async () => {
    if (addAdminRequest.value.username === "") { message.error('请填写账号'); return }
    else if (addAdminRequest.value.password === "") { message.error('请填写密码'); return }
    else if (addAdminRequest.value.name === "") { message.error('请填写姓名'); return }
    const R: R = (await addAdminApi(addAdminRequest.value)).data
    if (R.code === 200) { addTableVisible.value = false; message.success(R.msg, 2); localStorage.removeItem("adminDetail"); getSelfAdminDetail(); setTimeout(() => { setSelfAdminDetail(); setAdminList() }, 1000); addAdminRequest.value.username = ""; addAdminRequest.value.password = ""; addAdminRequest.value.name = "" }
    else { message.error(R.msg, 2) }
}
const addAdminTableBackToAdminList = () => { addTableVisible.value = false }

const deleteAdminBySelected = async () => {
    try {
        const R: R = (await deleteAdminBySelectedApi(usernames.value)).data
        if (R.code === 200) { message.success("批量删除管理员成功", 2); localStorage.removeItem("adminDetail"); getSelfAdminDetail(); setTimeout(() => { setSelfAdminDetail(); setAdminList() }, 1000) }
        else { message.error("删除失败", 2) }
        usernames.value = []; notSelected.value = true
    } catch (err) { console.log(err) }
}
function onSelectChange(keys: string[]) { usernames.value = keys; notSelected.value = usernames.value.length > 0 ? false : true }

const columns = [
    { title: '用户名', dataIndex: 'username', key: 'username', width: 400 },
    { title: '姓名', dataIndex: 'name', key: 'name', width: 700 },
    { title: '操作', key: 'action' },
];
const pagination = ref({ current: 1, pageSize: 6, total: adminList.value.length, onChange: (page: number, _pageSize: number) => { pagination.value.current = page } });
const rules = ref({
    account: [{ required: true, message: '账户不能为空', trigger: 'blur' }],
    password: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
    name: [{ required: true, message: '姓名不能为空', trigger: 'blur' }],
})
const { validateInfos } = Form.useForm([addAdminRequest, addAdminRequest], rules)
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
