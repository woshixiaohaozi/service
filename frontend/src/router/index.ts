import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

/**
 * 路由表。meta.hidden 控制是否显示在侧边菜单；
 * meta.permission 为按钮/页面级权限标识。
 */
const routes: RouteRecordRaw[] = [
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue'), meta: { hidden: true } },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/Layout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue'), meta: { title: '首页', icon: 'House' } },
      { path: 'system/user', name: 'UserManage', component: () => import('@/views/system/UserManage.vue'), meta: { title: '用户管理', icon: 'User' } },
      { path: 'system/role', name: 'RoleManage', component: () => import('@/views/system/RoleManage.vue'), meta: { title: '角色管理', icon: 'UserFilled' } },
      { path: 'system/menu', name: 'MenuManage', component: () => import('@/views/system/MenuManage.vue'), meta: { title: '菜单管理', icon: 'Menu' } },
      { path: 'system/dept', name: 'DeptManage', component: () => import('@/views/system/DeptManage.vue'), meta: { title: '部门管理', icon: 'OfficeBuilding' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

/** 全局前置守卫：未登录跳转登录页 */
router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.path !== '/login' && !auth.isLoggedIn) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.path === '/login' && auth.isLoggedIn) {
    return { path: '/' }
  }
  return true
})

export default router
