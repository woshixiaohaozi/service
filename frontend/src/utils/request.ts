import axios, { type InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

/**
 * Axios 封装，统一约定：
 * 1. 后端全站仅接受 POST，因此所有请求都用 post（参数放 body 或 query）；
 * 2. 自动注入 Authorization: Bearer <token>；
 * 3. 响应拦截器自动解包 Result，业务层直接拿到 data；
 * 4. 统一错误提示；401 时清理登录态并跳转登录页。
 */
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 15000
})

service.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const auth = useAuthStore()
  if (auth.token) {
    config.headers.Authorization = auth.tokenType + ' ' + auth.token
  }
  return config
})

service.interceptors.response.use(
  (response) => {
    const res = response.data
    // 文件下载等二进制响应直接返回
    if (!(res && typeof res === 'object' && 'code' in res)) return res
    if (res.code === 0) return res.data
    // 401 未登录：清理登录态并跳转
    if (res.code === 40100 || res.code === 401) {
      useAuthStore().logout()
      import('@/router').then(({ default: router }) => router.push('/login'))
    }
    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error) => {
    const msg = error.response?.data?.message || error.message || '网络异常'
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

/** 统一 POST 请求：data 放请求体，params 放 query（后端 @RequestParam 使用） */
export function post<T>(url: string, data?: unknown, params?: Record<string, unknown>): Promise<T> {
  return service.post(url, data, { params }) as unknown as Promise<T>
}

export default service
