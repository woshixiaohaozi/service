import { post } from '@/utils/request'

/** 权限分配接口 */
export const permissionApi = {
  userRoles: (userId: number) => post<number[]>('/api/v1/permissions/user-role-ids', null, { userId }),
  assignRoles: (userId: number, roleIds: number[]) =>
    post<void>('/api/v1/permissions/assign-roles', { userId, roleIds }),
  userPermissions: (userId: number) =>
    post<string[]>('/api/v1/permissions/user-permissions', null, { userId })
}
