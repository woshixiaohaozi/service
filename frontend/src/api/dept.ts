import { post } from '@/utils/request'
import type { SysDept } from '@/types/api'

/** 部门管理接口 */
export const deptApi = {
  tree: () => post<SysDept[]>('/api/v1/depts/tree'),
  list: () => post<SysDept[]>('/api/v1/depts/list'),
  save: (data: SysDept) => post<void>('/api/v1/depts/save', data),
  remove: (id: number) => post<void>('/api/v1/depts/delete', null, { id })
}
