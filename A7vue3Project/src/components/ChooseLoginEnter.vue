<template>
  <a-modal :open="useLogin().chooseEnterVisible" 
            title="请选择角色登录"
            @ok="handleOk" 
            @cancel="handleCancel"
            okText="确定"
            cancelText="取消"
            style="top: 40%;"
            >
            
    <a-form>
        <a-radio-group v-model:value = "chooseRole">
        <a-radio-button value="student">学生登录</a-radio-button>
        <a-radio-button value="teacher">教师登录</a-radio-button>
        <a-radio-button value="admin">管理员登录</a-radio-button>
      </a-radio-group>
    </a-form>

  </a-modal>
</template>
<script lang="ts" setup>
import { useLogin } from '../usePinia/login'
import { ref } from 'vue';

const chooseRole = ref('student')

const handleOk = async() => {
  useLogin().chooseEnterVisible = false;
  useLogin().loginObeject = chooseRole.value;
  useLogin().homeView_visible = false;
  useLogin().login_visible = true;

  
};
const handleCancel = () => {
  useLogin().chooseEnterVisible = false;
  clearChooseRole();
};

const clearChooseRole = () => {
  chooseRole.value = 'student'
}

</script>
<style scoped>
.login-title {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  transform: translateY(-10px);
}
</style>