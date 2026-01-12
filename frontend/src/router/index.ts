import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/academy',
    name: 'Academy',
    component: () => import('@/views/Academy.vue')
  },
  {
    path: '/tourism',
    name: 'Tourism',
    component: () => import('@/views/Tourism.vue')
  },
  {
    path: '/guide',
    name: 'Guide',
    component: () => import('@/views/Guide.vue')
  },
  {
    path: '/creative',
    name: 'Creative',
    component: () => import('@/views/Creative.vue')
  },
  {
    path: '/creative/upload',
    name: 'CreativeUpload',
    component: () => import('@/views/CreativeUpload.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/mall',
    name: 'Mall',
    component: () => import('@/views/Mall.vue')
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('@/views/Cart.vue')
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('@/views/Orders.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/social',
    name: 'Social',
    component: () => import('@/views/Social.vue')
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: '',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue')
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue')
      },
      {
        path: 'content',
        name: 'AdminContent',
        component: () => import('@/views/admin/Content.vue')
      },
      {
        path: 'products',
        name: 'AdminProducts',
        component: () => import('@/views/admin/Products.vue')
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('@/views/admin/Orders.vue')
      },
      {
        path: 'mall-applications',
        name: 'AdminMallApplications',
        component: () => import('@/views/admin/MallApplications.vue')
      },
      {
        path: 'reports',
        name: 'AdminReports',
        component: () => import('@/views/Admin.vue')
      },
      {
        path: 'settings',
        name: 'AdminSettings',
        component: () => import('@/views/admin/Settings.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 从 localStorage 获取 token 和 userInfo
  const token = localStorage.getItem('token')
  const userInfoStr = localStorage.getItem('userInfo')
  let userInfo = null
  
  if (userInfoStr) {
    try {
      userInfo = JSON.parse(userInfoStr)
    } catch (e) {
      console.error('Failed to parse userInfo', e)
    }
  }

  // 检查是否需要认证
  if (to.meta.requiresAuth) {
    if (!token) {
      // 未登录，跳转到登录页
      next({
        path: '/login',
        query: { redirect: to.fullPath } // 保存目标路由，登录后跳转
      })
      return
    }

    // 检查是否需要管理员权限
    if (to.meta.requiresAdmin) {
      if (!userInfo || userInfo.role !== 'admin') {
        // 不是管理员，跳转到首页
        next('/')
        return
      }
    }
  }

  // 如果已登录且访问登录页，跳转到首页
  if (to.path === '/login' && token) {
    next('/')
    return
  }

  next()
})

export default router
