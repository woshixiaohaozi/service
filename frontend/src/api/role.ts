import { post } from '@/utils/request'
import type { PageResult, SysRole } from '@/types/api'

/** 角色管理接口 */
export const roleApi = {
  page: (data: { pageNum: number; pageSize: number; roleName?: string; status?: number }) =>
    post<PageResult<SysRole>>('/api/v1/roles/page', data),
  list: () => post<SysRole[]>('/api/v1/roles/list'),
  save: (data: SysRole) => post<void>('/api/v1/roles/save', data),
  remove: (id: number) => post<void>('/api/v1/roles/delete', null, { id }),
  menuIds: (roleId: number) => post<number[]>('/api/v1/permissions/role-menus', null, { roleId }),
  assignMenus: (roleId: number, menuIds: number[]) =>
    post<void>('/api/v1/permissions/assign-menus', { roleId, menuIds })
}
