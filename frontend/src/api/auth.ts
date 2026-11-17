import { post } from '@/utils/request'
import type { CurrentUser } from '@/types/api'

/** 认证相关接口 */
export const authApi = {
  login: (username: string, password: string) => post('/auth/login', { username, password }),
  currentUser: () => post<CurrentUser>('/api/v1/users/current-user')
}
