<template>
  <a-layout class="app-layout">
    <a-layout-sider
      width="240"
      :collapsed-width="64"
      collapsible
      v-model:collapsed="allStatus().admin_AdminHome_collapse"
      class="app-sidebar"
      :trigger="null"
    >
      <div class="sidebar-logo" :class="{ collapsed: allStatus().admin_AdminHome_collapse }">
        <div class="logo-icon role-admin">
          <svg viewBox="0 0 24 24" fill="none" width="24" height="24">
            <path d="M12 2L2 7L12 12L22 7L12 2Z" fill="white" fill-opacity="0.9"/>
            <path d="M2 17L12 22L22 17" stroke="white" stroke-width="2" stroke-linecap="round"/>
            <path d="M2 12L12 17L22 12" stroke="white" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </div>
        <transition name="fade">
          <div v-if="!allStatus().admin_AdminHome_collapse" class="logo-text">
            <span class="logo-title">智语教学</span>
            <span class="logo-subtitle">管理端</span>
          </div>
        </transition>
      </div>

      <a-menu v-model:selectedKeys="selectedKeys" v-model:openKeys="openKeys" mode="inline" class="sidebar-menu">
        <template v-for="menu in topMenu" :key="menu.id">
          <template v-if="menu.childrenMenu && menu.childrenMenu.length > 0">
            <a-sub-menu :key="menu.id">
              <template #title>
                <span class="menu-icon-wrap"><a-icon :icon="menu.icon" /></span>
                <span>{{ menu.name }}</span>
              </template>
              <template v-for="child in menu.childrenMenu" :key="child.id">
                <router-link :to="child.path">
                  <a-menu-item :key="child.id">
                    <span class="menu-icon-wrap"><a-icon :icon="child.icon" /></span>
                    <span>{{ child.name }}</span>
                  </a-menu-item>
                </router-link>
              </template>
            </a-sub-menu>
          </template>
          <template v-else>
            <router-link :to="menu.path">
              <a-menu-item :key="menu.id">
                <span class="menu-icon-wrap"><a-icon :icon="menu.icon" /></span>
                <span>{{ menu.name }}</span>
              </a-menu-item>
            </router-link>
          </template>
        </template>
      </a-menu>

      <div class="sidebar-toggle toggle-admin" @click="allStatus().admin_AdminHome_collapse = !allStatus().admin_AdminHome_collapse" :title="allStatus().admin_AdminHome_collapse ? '展开菜单' : '收起菜单'">
        <svg v-if="!allStatus().admin_AdminHome_collapse" viewBox="0 0 24 24" fill="none" width="16" height="16">
          <path d="M15 18l-6-6 6-6" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <svg v-else viewBox="0 0 24 24" fill="none" width="16" height="16">
          <path d="M9 18l6-6-6-6" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
    </a-layout-sider>

    <a-layout class="layout-main">
      <a-layout-header class="app-header">
        <div class="header-left">
          <div class="header-greeting">
            <span class="greeting-emoji">⚙️</span>
            <span class="greeting-text">管理员工作台</span>
          </div>
        </div>
        <div class="header-right">
          <div class="header-role-badge admin">
            <span class="role-dot"></span>
            管理端
          </div>
          <a-popconfirm title="确定要登出吗?" ok-text="确定" cancel-text="取消" @confirm="loginOut()" ok-type="danger" placement="bottomRight">
            <button class="header-logout-btn">
              <svg viewBox="0 0 24 24" fill="none" width="16" height="16">
                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4M16 17l5-5-5-5M21 12H9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              登出
            </button>
          </a-popconfirm>
        </div>
      </a-layout-header>

      <a-layout-content class="app-content">
        <RouterView></RouterView>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue';
import type { menu } from '../modules/menu';
import { getMenuApi } from '../api/admin/getMenuApi';
import type { R } from '../response/R';
import { message } from 'ant-design-vue';
import { useIsLogin } from '../usePinia/isLogin';
import router from '../routers/appRouters';
import { loginAdminOutApi } from '../api/loginOutApi';
import type { jwtRes } from '../response/jwtRes';
import AIcon from '../utils/AIcon';
import { allStatus } from '../usePinia/allStatus';

const selectedKeys = ref<string[]>(['1']);
const openKeys = ref<string[]>(['sub1']);
const antdvMenu = ref<menu[]>([]);
const topMenu = ref<menu[]>([]);
const jwt = reactive<jwtRes>({
  token: (sessionStorage.getItem('token') || localStorage.getItem('token')) as string
});

getMenuApi().then(res => {
  const R: R = res.data;
  if (R.code === 200 && Array.isArray(R.data)) {
    antdvMenu.value = R.data;
    for (let menu of antdvMenu.value) {
      if (!menu.pid) {
        topMenu.value.push(menu);
      } else {
        for (let m of antdvMenu.value) {
          if (m.id === menu.pid) {
            m.childrenMenu ??= [];
            m.childrenMenu.push(menu);
          }
        }
      }
    }
  } else {
    localStorage.clear();
    useIsLogin().isLogin = false;
    message.error('登录已过期，请重新登录', 2);
    router.push('/');
  }
}).catch(err => {
  console.log(err);
  localStorage.clear();
  useIsLogin().isLogin = false;
  message.error('登录已过期，请重新登录', 2);
  router.push('/');
});

const loginOut = async () => {
  if (!jwt) return;
  const res = await loginAdminOutApi(jwt);
  const R: R = res.data;
  if (R.code === 200) {
    message.success(R.msg, 2);
    localStorage.removeItem('isAuthenticated');
    sessionStorage.removeItem('isAuthenticated');
    useIsLogin().isLogin = false;
    router.push('/');
    localStorage.clear();
    antdvMenu.value = [];
    topMenu.value = [];
  } else {
    message.error(R.msg, 2);
    antdvMenu.value = [];
    topMenu.value = [];
  }
};
</script>

<style scoped>
@import '../style/layout.css';
</style>
