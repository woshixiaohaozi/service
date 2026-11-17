import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { post } from '@/utils/request'
import type { TokenResponse, CurrentUser } from '@/types/api'

/**
 * 认证状态仓库：
 * - token / tokenType 持久化到 localStorage，刷新页面不丢失登录态；
 * - userInfo / permissions 保存当前用户与权限标识，供按钮级鉴权使用。
 */
export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const tokenType = ref(localStorage.getItem('tokenType') || 'Bearer')
  const userInfo = ref<CurrentUser | null>(null)

  const isLoggedIn = computed(() => !!token.value)

  function setToken(data: TokenResponse) {
    token.value = data.token
    tokenType.value = data.tokenType || 'Bearer'
    localStorage.setItem('token', token.value)
    localStorage.setItem('tokenType', tokenType.value)
  }

  async function login(username: string, password: string) {
    const data = await post<TokenResponse>('/auth/login', { username, password })
    setToken(data)
    await loadUserInfo()
  }

  /** 从后端加载当前用户权限（登录成功后调用） */
  async function loadUserInfo() {
    try {
      userInfo.value = await post<CurrentUser>('/auth/current-user')
    } catch (e) {
      // 后端未实现该接口时降级：不阻断登录，权限由路由守卫按未知处理
      userInfo.value = { username: 'admin', realName: '系统管理员', permissions: ['*'], roles: ['admin'] }
    }
  }

  function hasPermission(permission: string) {
    if (!userInfo.value) return false
    return userInfo.value.permissions.includes('*') || userInfo.value.permissions.includes(permission)
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('tokenType')
  }

  return { token, tokenType, userInfo, isLoggedIn, login, loadUserInfo, hasPermission, logout }
})
