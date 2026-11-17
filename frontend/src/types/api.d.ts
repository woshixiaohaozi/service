// 后端统一返回结构与业务实体类型定义

/** 后端 Result<T> 结构（请求拦截器会自动解包，业务层直接拿到 data） */
export interface Result<T> {
  code: number
  message: string
  data: T
  traceId: string
  timestamp: number
  success?: boolean
}

/** 统一分页返回 */
export interface PageResult<T> {
  total: number
  size: number
  current: number
  records: T[]
}

/** 登录返回 */
export interface TokenResponse {
  token: string
  tokenType: string
  expiresIn: number
}

/** 当前登录用户信息（由 /auth/current-user 返回） */
export interface CurrentUser {
  username: string
  realName: string
  permissions: string[]
  roles: string[]
}

/** 系统用户 */
export interface SysUser {
  id?: number
  username: string
  password?: string
  realName?: string
  mobile?: string
  status?: number
  deptId?: number
  createTime?: string
}

/** 系统角色 */
export interface SysRole {
  id?: number
  roleName: string
  roleKey: string
  sort?: number
  status?: number
  remark?: string
}

/** 系统菜单/权限 */
export interface SysMenu {
  id?: number
  menuName: string
  parentId: number
  sort?: number
  path?: string
  component?: string
  icon?: string
  permission?: string
  menuType: string
  visible?: number
  status?: number
  children?: SysMenu[]
}

/** 系统部门 */
export interface SysDept {
  id?: number
  deptName: string
  parentId: number
  leaderId?: number
  sort?: number
  status?: number
  children?: SysDept[]
}
