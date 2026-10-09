import {  message } from 'ant-design-vue';
import {createRouter,createWebHashHistory} from 'vue-router'
import { useIsLogin } from '../usePinia/isLogin';
import {jwtDecode} from 'jwt-decode'

const routes = [
    {
        path:'/',
        name:'home',
        component:() => import('../Home.vue'),
    },
    {
        path:'/student/home',
        name:'studentHome',
        component:() => import('../views/StudentHome.vue'),
        meta: { requiresAuth: true }
    },
    {
        path:'/teacher/home',
        name:'teacherHome',
        component:() => import('../views/TeacherHome.vue'),
        meta: { requiresAuth: true }
    },
    {
        path:'/admin',
        name:'adminHome',
        component:() => import('../views/AdminHome.vue'),
        meta: { requiresAuth: true },
        children:[
            {
                path:'HomeView',
                name:'HomeView',
                component:() => import('../views/admin/HomeView.vue'),
                meta: { requiresAuth: true }
            },
            {
                path:'DataAnalysis',
                name:'DataAnalysis',
                component:() => import('../views/admin/DataAnalysis.vue'),
                meta: { requiresAuth: true }
            },
            {
                path:'ResourceManager',
                name:'ResourceManager',
                component:() => import('../views/admin/ResourceManager.vue'),
                meta: { requiresAuth: true }
            },
            {
                path:'UserManager',
                name:'UserManager',
                children:[
                    {
                        path:'StudentList',
                        name:'StudentList',
                        component:() => import('../views/admin/StudentList.vue'),
                        meta: { requiresAuth: true }
                    },
                    {
                        path:'TeacherList',
                        name:'TeacherList',
                        component:() => import('../views/admin/TeacherList.vue'),
                        meta: { requiresAuth: true }
                    },
                    {
                        path:'AdminList',
                        name:'AdminList',
                        component:() => import('../views/admin/AdminList.vue'),
                        meta: { requiresAuth: true }
                    },
                ]
            },
        ]
    },
    {
        path:'/student',
        name:'studentnHome',
        component:() => import('../views/StudentHome.vue'),
        meta: { requiresAuth: true },
        children:[
            {
                path:'StudentHome',
                name:'StudentHomeView',
                component:() => import('../views/student/StudentHome.vue'),
                meta: { requiresAuth: true }
            },
            {
                path:'SelfStudentDetail',
                name:'SelfStudentDetail',
                component:() => import('../views/student/SelfStudentDetail.vue'),
                meta: { requiresAuth: true }
            },
            {
                path:'CoursePractise',
                name:'CoursePractise',
                component:() => import('../views/student/CoursePractise.vue'),
                meta: { requiresAuth: true }
            },
            {
                path:'AIHelper',
                name:'AIHelper',
                children:[
                    {
                        path:'AnswerQuestion',
                        name:'AnswerQuestion',
                        component:() => import('../views/student/AnswerQuestion.vue'),
                        meta: { requiresAuth: true }
                    },
                    {
                        path:'SelfPractise',
                        name:'SelfPractise',
                        component:() => import('../views/student/SelfPractise.vue'),
                        meta: { requiresAuth: true }
                    },
                ]
            },
        ]
    },
    {
        path:'/teacher',
        name:'teacherHome',
        component:() => import('../views/TeacherHome.vue'),
        meta: { requiresAuth: true },
        children:[
            {
                path:'TeacherHome',
                name:'TeacherHomeView',
                component:() => import('../views/teacher/TeacherHome.vue'),
                meta: { requiresAuth: true }
            },
            {
                path:'SelfTeacherDetail',
                name:'SelfTeacherDetail',
                component:() => import('../views/teacher/SelfTeacherDetail.vue'),
                meta: { requiresAuth: true }
            },
            {
                path:'CourseList',
                name:'CourseList',
                component:() => import('../views/teacher/CourseList.vue'),
                meta: { requiresAuth: true }
            },
            {
                path:'StudentList',
                name:'StudentListT',
                component:() => import('../views/teacher/StudentList.vue'),
                meta: { requiresAuth: true }
            },
            {
                path:'AIUse',
                name:'AIUse',
                children:[
                    {
                        path:'KnowledgeOrganization',
                        name:'KnowledgeOrganization',
                        component:() => import('../views/teacher/KnowledgeOrganization.vue'),
                        meta: { requiresAuth: true }
                    },
                    {
                        path:'PractiseDesign',
                        name:'PractiseDesign',
                        component:() => import('../views/teacher/PractiseDesign.vue'),
                        meta: { requiresAuth: true }
                    },
                    {
                        path:'DesignIssue',
                        name:'DesignIssue',
                        component:() => import('../views/teacher/DesignIssue.vue'),
                        meta: { requiresAuth: true }
                    },
                ]
            },
        ]
    },
/*     {
        path:'/loadingData',
        name:'loadingData',
        component:() => import('../views/LoadingData.vue'),
        meta: { requiresAuth: true }
    }, */
    {
        path:'/:pathMatch(.*)*',
        name:'notFound',
        redirect:'/notFound',
    },
    {
    path: '/notFound',
    component: () => import('../views/notFound.vue'),
  },
]

const router = createRouter({
    history:createWebHashHistory(),
    routes:routes
})
 
// 全局前置守卫
router.beforeEach((to, _from, next) => {
  console.log('[Router] beforeEach:', to.path, to.name);

  interface CustomJwtPayload {
    role?: string;
    exp?: number;
    sub?: string;
  }

  // 优先从 sessionStorage 读取 token（标签页隔离），兼容 localStorage 旧数据
  const token = sessionStorage.getItem('token') || localStorage.getItem('token');
  const isAuthenticated = sessionStorage.getItem('isAuthenticated') === 'true';
  console.log('[Router] token exists:', !!token, 'isAuthenticated:', isAuthenticated);

  // 检查令牌是否存在且未过期
  let tokenValid = false;
  let role: string | undefined;

  if (token && isAuthenticated) {
    try {
      const decodeJwt = jwtDecode(token) as CustomJwtPayload;
      const now = Math.floor(Date.now() / 1000);
      console.log('[Router] JWT exp:', decodeJwt.exp, 'now:', now);
      if (decodeJwt.exp && decodeJwt.exp > now) {
        tokenValid = true;
        role = decodeJwt.role;
        // 每次导航时同步用户名到 sessionStorage（标签页隔离）
        if (decodeJwt.sub && !sessionStorage.getItem('username')) {
          sessionStorage.setItem('username', decodeJwt.sub);
        }
        console.log('[Router] token valid, role:', role);
      } else {
        console.log('[Router] token expired, clearing');
        // 令牌过期，立即清除，防止 isAuthenticated 标志与令牌不一致
        localStorage.clear();
        sessionStorage.clear();
        useIsLogin().isLogin = false;
      }
    } catch (e) {
      console.log('[Router] token parse error, clearing:', e);
      // 令牌格式错误/伪造，立即清除
      localStorage.clear();
      sessionStorage.clear();
      useIsLogin().isLogin = false;
    }
  } else if (isAuthenticated && !token) {
    // isAuthenticated 为 true 但没有令牌，数据不一致，清除
    console.log('[Router] isAuthenticated but no token, clearing');
    localStorage.clear();
    sessionStorage.clear();
    useIsLogin().isLogin = false;
  }

  // 如果需要认证但令牌无效 -> 返回登录页
  if (to.meta.requiresAuth && !tokenValid) {
    console.log('[Router] auth required but token invalid, redirecting to /');
    message.error('登录已过期，请重新登录', 3);
    next('/');
    return;
  }

  // 已登录访问首页 -> 按角色跳转到个人中心
  if (tokenValid && to.name === 'home') {
    console.log('[Router] logged in, redirecting by role:', role);
    if (role === 'STUDENT') {
      next('/student/SelfStudentDetail');
      return;
    } else if (role === 'TEACHER') {
      next('/teacher/SelfTeacherDetail');
      return;
    } else if (role === 'ADMIN') {
      next('/admin/HomeView');
      return;
    }
  }

  // 已登录访问 /student 或 /teacher 父路由（无子路由） -> 重定向到个人中心
  if (tokenValid && (to.path === '/student' || to.path === '/teacher') && !to.query.tab) {
    console.log('[Router] base path without child, redirecting to profile');
    if (role === 'STUDENT') {
      next('/student/SelfStudentDetail');
      return;
    } else if (role === 'TEACHER') {
      next('/teacher/SelfTeacherDetail');
      return;
    }
  }

  console.log('[Router] next() - proceeding');
  next();
});

export default router