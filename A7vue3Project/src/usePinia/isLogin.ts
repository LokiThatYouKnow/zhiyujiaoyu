import { defineStore } from "pinia";
import strictStrToBoolean from "../utils/strictStrToBoolean";

export const useIsLogin = defineStore('isLogin',{
  state: () => {
    return {
      // 使用 sessionStorage：关闭浏览器后自动清除登录状态
      isLogin: sessionStorage.getItem('isAuthenticated') !== null ? strictStrToBoolean(sessionStorage.getItem('isAuthenticated')) : false,
    }
  },
});