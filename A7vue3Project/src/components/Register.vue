<template>
  <div class="register-page">
    <!-- Background -->
    <div class="bg-blob bg-blob-1"></div>
    <div class="bg-blob bg-blob-2"></div>
    <div class="bg-grid"></div>

    <button class="back-btn" @click="backToHomeView">
      <svg viewBox="0 0 24 24" fill="none" width="18" height="18">
        <path d="M19 12H5M12 19l-7-7 7-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      返回首页
    </button>

    <div class="register-center">
      <div class="register-brand animate-fade-in-up">
        <div class="brand-logo">
          <svg viewBox="0 0 24 24" fill="none" width="28" height="28">
            <path d="M12 2L2 7L12 12L22 7L12 2Z" fill="white" fill-opacity="0.9"/>
            <path d="M2 17L12 22L22 17M2 12L12 17L22 12" stroke="white" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </div>
        <h1>智语教学平台</h1>
        <p>账号注册 - 开启智能学习之旅</p>
      </div>

      <div class="register-card animate-fade-in-up" style="animation-delay: 0.1s">
        <div class="tab-nav">
          <button class="tab-btn" :class="{ active: useRegister().loginObeject === 'student' }"
            @click="() => useRegister().loginObeject = 'student'">
            <svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M4 20h16M4 20v-1a4 4 0 0 1 4-4h8a4 4 0 0 1 4 4v1M12 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
            学生注册
          </button>
          <button class="tab-btn" :class="{ active: useRegister().loginObeject === 'teacher' }"
            @click="() => useRegister().loginObeject = 'teacher'">
            <svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8zM23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
            教师注册
          </button>
        </div>

        <a-form name="register" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }" autocomplete="off" class="register-form">
          <!-- Student Form -->
          <template v-if="useRegister().loginObeject === 'student'">
            <div class="form-section-title">学生信息注册</div>
            <a-form-item label="用户名" name="account" v-bind="validateInfos.account" class="form-row">
              <a-input v-model:value="registerStudentRequest.student_account" placeholder="设置登录用户名" class="styled-input" />
              <span class="field-hint">用户名(账号)将用于登录系统</span>
            </a-form-item>
            <a-form-item label="密码" name="password" v-bind="validateInfos.password" class="form-row">
              <a-input-password v-model:value="registerStudentRequest.student_password" placeholder="设置登录密码" class="styled-input" />
              <span class="field-hint">请认真设置密码</span>
            </a-form-item>
            <a-form-item label="姓名" name="name" v-bind="validateInfos.name" class="form-row">
              <a-input v-model:value="registerStudentRequest.student_name" placeholder="请输入真实姓名" class="styled-input" />
            </a-form-item>
            <a-form-item label="年龄" name="age" v-bind="validateInfos.age" class="form-row">
              <a-input-number v-model:value="registerStudentRequest.student_age" :min="1" :max="100" placeholder="请输入年龄" class="styled-input" style="width: 30%;" />
            </a-form-item>
            <a-form-item label="性别" name="gender" v-bind="validateInfos.gender" class="form-row">
              <a-radio-group v-model:value="registerStudentRequest.student_gender">
                <a-radio-button value="男">男</a-radio-button>
                <a-radio-button value="女">女</a-radio-button>
              </a-radio-group>
            </a-form-item>
            <a-form-item label="教师码" name="class" class="form-row">
              <a-input v-model:value="registerStudentRequest.teacher_binding_code" placeholder="输入教师提供的绑定码" class="styled-input" />
              <span class="field-hint">可选，用于绑定班主任</span>
            </a-form-item>
          </template>

          <!-- Teacher Form -->
          <template v-if="useRegister().loginObeject === 'teacher'">
            <div class="form-section-title">教师信息注册</div>
            <a-form-item label="用户名" name="account" v-bind="validateInfos.account" class="form-row">
              <a-input v-model:value="registerTeacherRequest.teacher_account" placeholder="设置登录用户名" class="styled-input" />
              <span class="field-hint">用户名(账号)将用于登录系统</span>
            </a-form-item>
            <a-form-item label="密码" name="password" v-bind="validateInfos.password" class="form-row">
              <a-input-password v-model:value="registerTeacherRequest.teacher_password" placeholder="设置登录密码" class="styled-input" />
              <span class="field-hint">请认真设置密码</span>
            </a-form-item>
            <a-form-item label="姓名" name="name" v-bind="validateInfos.name" class="form-row">
              <a-input v-model:value="registerTeacherRequest.teacher_name" placeholder="请输入真实姓名" class="styled-input" />
            </a-form-item>
            <a-form-item label="年龄" name="age" v-bind="validateInfos.age" class="form-row">
              <a-input-number v-model:value="registerTeacherRequest.teacher_age" :min="1" :max="100" placeholder="请输入年龄" class="styled-input" style="width: 30%;" />
            </a-form-item>
            <a-form-item label="性别" name="gender" v-bind="validateInfos.gender" class="form-row">
              <a-radio-group v-model:value="registerTeacherRequest.teacher_gender">
                <a-radio-button value="男">男</a-radio-button>
                <a-radio-button value="女">女</a-radio-button>
              </a-radio-group>
            </a-form-item>
            <a-form-item label="职位" name="position" v-bind="validateInfos.position" class="form-row">
              <a-select v-model:value="registerTeacherRequest.teacher_position" placeholder="请选择职称/职位" class="styled-input">
                <a-select-option value="讲师">讲师</a-select-option>
                <a-select-option value="班主任">班主任</a-select-option>
              </a-select>
            </a-form-item>
            <template v-if="registerTeacherRequest.teacher_position === '班主任'">
              <a-form-item label="专业" name="major" v-bind="validateInfos.major" class="form-row">
                <a-input v-model:value="registerTeacherRequest.teacher_major" placeholder="请输入任教专业" class="styled-input" />
              </a-form-item>
              <a-form-item label="班级" name="class" v-bind="validateInfos.class" class="form-row">
                <a-input v-model:value="registerTeacherRequest.teacher_class_name" placeholder="请输入所带班级" class="styled-input" />
                <span class="field-hint">请正确填写班级信息</span>
              </a-form-item>
            </template>
          </template>

          <div class="form-actions">
            <button type="button" class="submit-btn" @click="handleOk">
              <svg viewBox="0 0 24 24" fill="none" width="18" height="18"><path d="M9 11l3 3L22 4M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              完成注册
            </button>
          </div>
        </a-form>
      </div>

      <div class="register-footer">
        <p>© {{ new Date().getFullYear() }} 智语教学平台 - 版权所有</p>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { useRegister } from '../usePinia/register';
import { Form, message } from 'ant-design-vue';
import type { R } from '../response/R';
import type { student } from '../modules/student';
import type { teacher } from '../modules/teacher';
import { registerByStudentApi, registerByTeacherApi } from '../api/registerApi';
import { useLogin } from '../usePinia/login';

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'switchToLogin'): void
}>()

const registerStudentRequest: student = reactive({
  student_account: '', student_password: '', student_name: '',
  student_age: '', student_gender: '男', teacher_binding_code: '',
});

const registerTeacherRequest: teacher = reactive({
  teacher_account: '', teacher_password: '', teacher_name: '',
  teacher_age: '', teacher_gender: '男', teacher_major: '',
  teacher_position: '', teacher_class_name: '',
});

const rules = ref({
  account: [{ required: true, message: '账户不能为空', trigger: 'blur' }],
  password: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
  name: [{ required: true, message: '姓名不能为空', trigger: 'blur' }],
  age: [{ required: true, message: '年龄不能为空', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'blur' }],
  major: [{ required: true, message: '专业不能为空', trigger: 'blur' }],
  class: [{ required: true, message: '班级不能为空', trigger: 'blur' }],
  position: [{ required: true, message: '职位不能为空', trigger: 'blur' }],
});

const { validateInfos, validate } = Form.useForm([registerStudentRequest, registerTeacherRequest], rules);

const handleOk = async () => {
  const role = useRegister().loginObeject;
  if (role === 'student') {
    try {
      await validate();
      if (!registerStudentRequest.student_account) { message.error('请填写账号', 2); return; }
      if (!registerStudentRequest.student_password) { message.error('请填写密码', 2); return; }
      if (!registerStudentRequest.student_name) { message.error('请填写姓名', 2); return; }
      if (!registerStudentRequest.student_age) { message.error('请填写年龄', 2); return; }
      const requestData = { ...registerStudentRequest };
      const R: R = (await registerByStudentApi(requestData)).data;
      if (R.code === 200) {
        message.success(R.msg, 2);
        clearTable();
        useLogin().loginObeject = 'student';
        emit('switchToLogin')
      } else { message.error(R.msg, 2); }
    } catch (error) { console.log(error); }
  } else if (role === 'teacher') {
    try {
      await validate();
      if (!registerTeacherRequest.teacher_account) { message.error('请输入账号', 2); return; }
      if (!registerTeacherRequest.teacher_password) { message.error('请输入密码', 2); return; }
      if (!registerTeacherRequest.teacher_name) { message.error('请输入姓名', 2); return; }
      if (!registerTeacherRequest.teacher_age) { message.error('请输入年龄', 2); return; }
      if (!registerTeacherRequest.teacher_position) { message.error('请选择职位', 2); return; }
      if (registerTeacherRequest.teacher_position === '班主任') {
        if (!registerTeacherRequest.teacher_major) { message.error('请输入专业', 2); return; }
        if (!registerTeacherRequest.teacher_class_name) { message.error('请输入班级名称', 2); return; }
      }
      const requestData = { ...registerTeacherRequest };
      const R: R = (await registerByTeacherApi(requestData)).data;
      if (R.code === 200) {
        message.success(R.msg, 2);
        clearTable();
        useLogin().loginObeject = 'teacher';
        emit('switchToLogin')
      } else { message.error(R.msg, 2); }
    } catch (error) { console.log(error); }
  }
};

const clearTable = () => {
  registerStudentRequest.student_account = "";
  registerStudentRequest.student_password = "";
  registerStudentRequest.student_name = "";
  registerStudentRequest.student_gender = "男";
  registerStudentRequest.student_age = '';
  registerStudentRequest.teacher_binding_code = "";
  registerTeacherRequest.teacher_account = "";
  registerTeacherRequest.teacher_password = "";
  registerTeacherRequest.teacher_name = "";
  registerTeacherRequest.teacher_age = '';
  registerTeacherRequest.teacher_gender = "男";
  registerTeacherRequest.teacher_major = "";
  registerTeacherRequest.teacher_position = "";
  registerTeacherRequest.teacher_class_name = "";
};

const backToHomeView = () => {
  emit('close')
};
</script>

<style scoped>
/* ===== Book Theme Variables ===== */
.register-page {
    --book-bg: #F5F0E8;
    --book-page: #FAF7F2;
    --book-page-dark: #EDE4D3;
    --book-ink: #3D3226;
    --book-ink-light: #5C4B3A;
    --book-ink-muted: #8B7355;
    --book-accent: #8B5E3C;
    --book-accent-deep: #6B3A2A;
    --book-gold: #C4A35A;
    --book-gold-light: #D4C5A0;
    --book-border: #D4C5B2;
    --book-border-light: #E0D6C5;
    --book-shadow: rgba(61, 50, 38, 0.08);
    --book-shadow-lg: rgba(61, 50, 38, 0.15);
}

/* ===== Page Container ===== */
.register-page {
    min-height: 100vh;
    background: linear-gradient(170deg, #F5F0E8 0%, #EDE4D3 35%, #E8DCC8 65%, #F0EAE0 100%);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 40px 20px;
    position: relative;
    overflow: hidden;
    font-family: var(--font-family);
}

/* Book spine lines */
.register-page::before,
.register-page::after {
    content: '';
    position: fixed;
    top: 10%;
    bottom: 10%;
    width: 2px;
    z-index: 2;
    opacity: 0.4;
    background: linear-gradient(180deg, transparent, var(--book-gold-light) 20%, var(--book-gold) 50%, var(--book-gold-light) 80%, transparent);
}
.register-page::before { left: 60px; }
.register-page::after { right: 60px; }

/* ===== Background ===== */
.bg-blob {
    position: absolute;
    border-radius: 50%;
    filter: blur(100px);
    opacity: 0.22;
    pointer-events: none;
}

.bg-blob-1 {
    width: 350px; height: 350px;
    background: radial-gradient(circle, rgba(196, 163, 90, 0.18), transparent);
    top: -80px; right: -60px;
    animation: float 8s ease-in-out infinite;
}
.bg-blob-2 {
    width: 300px; height: 300px;
    background: radial-gradient(circle, rgba(139, 94, 60, 0.1), transparent);
    bottom: -60px; left: -40px;
    animation: float 10s ease-in-out infinite reverse;
}

.bg-grid {
    position: fixed; inset: 0; pointer-events: none;
    background-image:
        linear-gradient(rgba(139, 115, 85, 0.05) 1px, transparent 1px),
        linear-gradient(90deg, rgba(139, 115, 85, 0.05) 1px, transparent 1px);
    background-size: 30px 30px;
}

/* ===== Back Button ===== */
.back-btn {
    position: absolute; top: 24px; left: 24px; z-index: 20;
    display: inline-flex; align-items: center; gap: 6px;
    padding: 8px 18px;
    border-radius: 4px;
    border: 1px solid var(--book-border);
    background: rgba(245, 240, 232, 0.6);
    color: var(--book-ink-light);
    font-size: 14px; font-weight: 500; cursor: pointer;
    transition: all 0.3s;
    backdrop-filter: blur(10px);
}
.back-btn:hover { background: rgba(245, 240, 232, 0.9); transform: translateX(-3px); }

/* ===== Center ===== */
.register-center { position: relative; z-index: 10; width: 100%; max-width: 700px; }

.register-brand { text-align: center; margin-bottom: 28px; color: var(--book-ink); }

.brand-logo {
    width: 48px; height: 48px; border-radius: 6px;
    background: linear-gradient(135deg, var(--book-accent), var(--book-accent-deep));
    display: flex; align-items: center; justify-content: center;
    margin: 0 auto 12px;
    border: 1.5px solid var(--book-gold);
    box-shadow: 0 4px 12px rgba(107, 58, 42, 0.25);
}

.register-brand h1 {
    font-size: 24px; font-weight: 700; margin-bottom: 4px;
    font-family: Georgia, 'Times New Roman', serif;
    letter-spacing: 0.02em;
}
.register-brand p { font-size: 14px; opacity: 0.7; }

/* ===== Card ===== */
.register-card {
    background: var(--book-page);
    border-radius: 4px;
    border: 1px solid var(--book-border);
    box-shadow: 2px 3px 16px var(--book-shadow);
    overflow: hidden;
}

/* ===== Tab ===== */
.tab-nav { display: flex; border-bottom: 2px solid var(--book-border-light); }
.tab-btn {
    flex: 1;
    display: inline-flex; align-items: center; justify-content: center; gap: 8px;
    padding: 16px;
    background: transparent; border: none;
    font-size: 15px; font-weight: 500;
    color: var(--book-ink-muted);
    cursor: pointer;
    transition: all 0.3s;
}
.tab-btn.active {
    color: var(--book-accent-deep);
    font-weight: 600;
    background: rgba(139, 94, 60, 0.06);
    box-shadow: inset 0 -2px 0 var(--book-gold);
}
.tab-btn:hover:not(.active) { color: var(--book-ink); background: rgba(139, 94, 60, 0.03); }

/* ===== Form ===== */
.register-form { padding: 28px 36px; }
.form-section-title {
    font-size: 16px; font-weight: 600; color: var(--book-ink);
    margin-bottom: 20px; padding-bottom: 12px;
    border-bottom: 2px solid var(--book-border-light);
    font-family: Georgia, 'Times New Roman', serif;
    letter-spacing: 0.03em;
}
.form-row { margin-bottom: 18px; }
.field-hint { display: block; margin-top: 4px; font-size: 12px; color: var(--book-ink-muted); }

.form-actions { display: flex; justify-content: center; margin-top: 24px; }

.submit-btn {
    display: inline-flex; align-items: center; gap: 8px;
    padding: 12px 48px;
    background: linear-gradient(135deg, var(--book-accent), var(--book-accent-deep));
    color: #FAF7F2;
    font-size: 15px; font-weight: 600; border: none;
    border-radius: 4px; cursor: pointer;
    transition: all 0.3s;
    letter-spacing: 0.03em;
    border: 1.5px solid var(--book-gold);
}
.submit-btn:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 20px rgba(107, 58, 42, 0.3);
}

/* ===== Ant Design overrides ===== */
:deep(.ant-form-item-label > label) { color: var(--book-ink-light); font-weight: 600; }
:deep(.ant-input), :deep(.ant-input-number), :deep(.ant-select-selector) {
    background: rgba(139, 94, 60, 0.03);
    border: 1px solid var(--book-border);
    border-radius: 4px;
    color: var(--book-ink);
}
:deep(.ant-input:focus), :deep(.ant-input-number:focus), :deep(.ant-input-number-focused),
:deep(.ant-select-focused .ant-select-selector) {
    border-color: var(--book-accent);
    box-shadow: 0 0 0 3px rgba(139, 94, 60, 0.08);
}
:deep(.ant-radio-button-wrapper) {
    border-color: var(--book-border);
    color: var(--book-ink-light);
}
:deep(.ant-radio-button-wrapper-checked) {
    background: var(--book-accent);
    border-color: var(--book-accent);
    color: #FAF7F2;
}
:deep(.ant-radio-button-wrapper:hover) { color: var(--book-accent); }
:deep(.ant-input-password) { background: rgba(139, 94, 60, 0.03); border-radius: 4px; }

/* ===== Footer ===== */
.register-footer { text-align: center; margin-top: 28px; color: var(--book-ink-muted); font-size: 13px; opacity: 0.7; }

@media (max-width: 768px) {
    .register-page::before, .register-page::after { display: none; }
    .register-form { padding: 20px 16px; }
    .register-center { padding: 20px 10px; }
}
</style>
