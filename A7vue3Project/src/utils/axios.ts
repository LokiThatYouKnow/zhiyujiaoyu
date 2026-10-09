import axios from 'axios';
import router from '../routers/appRouters';
import { message } from 'ant-design-vue';
import { useIsLogin } from '../usePinia/isLogin';
import { throttle } from 'lodash-es';

const service = axios.create({
/*   baseURL: 'http://localhost:8080/api', // 后端API地址(开发环境) */
  baseURL: '/api',//生产环境
  timeout: 5000,
  headers: {
    'Cache-Control': 'no-cache',
    'Pragma': 'no-cache',
  }
});

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 优先从 sessionStorage 获取 Token（标签页隔离），兼容旧版 localStorage
    const token = sessionStorage.getItem('token') || localStorage.getItem('token');
    if (token) {
      // 在请求头中添加Token
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    console.log(error);
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  response => {
    return response;//还是使用response，而不是response.data,不然在传输数据时虽然不影响效果，但是会报类型错误
  },
  error => {
    // 处理401未认证或403 Token失效错误
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      // 清除所有本地缓存
      localStorage.clear();
      sessionStorage.clear();
      useIsLogin().isLogin = false
      router.push('/');
      messageLodash()
    }
    return Promise.reject(error);
  }
);

// 使用 lodash 的节流函数(自定义在几秒内只能触发一次)
const messageLodash = throttle(() => {
  message.error('登录已过期，请重新登录', 2);
  
}, 2000, { trailing: false }); // 关键配置(trailing: false)：冷却期内的调用会被忽略

export default service;