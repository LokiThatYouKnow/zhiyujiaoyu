<template>
  <a-modal :open="useRegister().chooseEnterVisible" 
            title="请选择角色注册"
            @ok="handleOk" 
            @cancel="handleCancel"
            okText="确定"
            cancelText="取消"
            style="top: 40%;"
            >
            
    <a-form>
        <a-radio-group v-model:value = "chooseRole">
        <a-radio-button value="student">学生注册</a-radio-button>
        <a-radio-button value="teacher">教师注册</a-radio-button>
      </a-radio-group>
    </a-form>

  </a-modal>
</template>
<script lang="ts" setup>
import { useLogin } from '../usePinia/login';
import { useRegister} from '../usePinia/register';
import { ref } from 'vue';

const chooseRole = ref('student')

const handleOk = async() => {
  useRegister().chooseEnterVisible = false;
  useRegister().loginObeject = chooseRole.value;
  useLogin().homeView_visible = false
  useRegister().visible = true;
};
const handleCancel = () => {
  useRegister().chooseEnterVisible = false;
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