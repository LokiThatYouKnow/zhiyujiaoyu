import { defineStore } from "pinia";

export const useLogin = defineStore('visible',{
  state: () => {
    return {
      homeView_visible: true,
      login_visible: false,
      chooseEnterVisible: false,
      loginObeject: 'student',
    }
  },
});