import { post } from '@/utils/request'
import type { PageResult, SysUser } from '@/types/api'

/** 用户管理接口（全站 POST） */
export const userApi = {
  page: (data: { pageNum: number; pageSize: number; username?: string; status?: number }) =>
    post<PageResult<SysUser>>('/api/v1/users/page', data),
  list: () => post<SysUser[]>('/api/v1/users/list'),
  save: (data: SysUser) => post<void>('/api/v1/users/save', data),
  remove: (id: number) => post<void>('/api/v1/users/delete', null, { id }),
  // 启用/禁用用户
  changeStatus: (id: number, status: number) =>
    post<void>('/api/v1/users/change-status', null, { id, status })
}
