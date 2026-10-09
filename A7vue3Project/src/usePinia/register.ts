import { defineStore } from "pinia";

export const useRegister = defineStore('register',{
  state: () => {
    return {
      visible: false,
      chooseEnterVisible: false,
      loginObeject: null as any,
    }
  },
});