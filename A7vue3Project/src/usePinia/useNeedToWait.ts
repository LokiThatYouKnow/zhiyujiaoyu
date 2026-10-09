import { defineStore } from 'pinia'

//可以用于不同组件的等待
export const useNeedToWait = defineStore('useNeedToWait', {
  // 定义状态
  state: () => ({
    useNeedToWait: false,
    timeoutId: null as null | ReturnType<typeof setTimeout>
  }),
  // 定义行为
  actions: {
    // 启动定时器
    setWaitTimerBySecond(second:number) {
      this.useNeedToWait = true
      this.timeoutId = setTimeout(() => {
      this.useNeedToWait = false
      }, second * 1000)
    },
    // 清除定时器(用于手动清除计时器)
    clearTimer() {
      if (this.timeoutId) {
        clearTimeout(this.timeoutId)
        this.timeoutId = null
      }
    }
  }
})