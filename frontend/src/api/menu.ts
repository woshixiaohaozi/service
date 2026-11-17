import { post } from '@/utils/request'
import type { SysMenu } from '@/types/api'

/** 菜单/权限管理接口 */
export const menuApi = {
  tree: () => post<SysMenu[]>('/api/v1/menus/tree'),
  list: () => post<SysMenu[]>('/api/v1/menus/list'),
  save: (data: SysMenu) => post<void>('/api/v1/menus/save', data),
  remove: (id: number) => post<void>('/api/v1/menus/delete', null, { id })
}
