export const API_STATUS_KEY = 'campus_api_status'

export const PENDING_API_STATUS = { kind: 'pending', message: '正在检测后端接口...' }
export const DEMO_API_STATUS = { kind: 'mock', message: '演示模式：数据保存在当前浏览器' }

export function toChineseErrorMessage(error) {
  const raw = typeof error === 'string' ? error : error?.message || ''
  if (!raw) return '请求失败，请稍后重试。'
  if (/Cannot read properties of null.*liked/.test(raw)) return '点赞结果为空，请检查后端返回数据。'
  if (/Expected one result.*selectOne|found:\s*\d+/i.test(raw)) return '服务器查询结果异常，请检查后端点赞查询。'
  if (/Network Error|Failed to fetch|ERR_NETWORK/i.test(raw)) return '后端服务未连接，请启动后端后重试。'
  if (/timeout|超时/i.test(raw)) return '请求超时，请稍后重试。'
  if (/Request failed with status code 5\d\d/i.test(raw)) return '服务器暂时不可用，请稍后重试。'
  if (/Request failed with status code 4\d\d/i.test(raw)) return '请求参数或登录状态有误，请检查后重试。'
  return raw
}

export function describeApiError(error) {
  const status = error?.response?.status
  if (!status && (error?.code === 'ERR_NETWORK' || error?.message === 'Network Error')) return { kind: 'offline', message: '后端服务未连接，请启动 Spring Boot 后端后重试。' }
  if (status === 501) return { kind: 'unavailable', message: '该功能暂未开放，请先完成对应后端接口。' }
  if (status === 401) return { kind: 'auth', message: '登录状态已失效，请重新登录。' }
  if (status === 403) return { kind: 'forbidden', message: '没有权限执行此操作。' }
  if (status === 404) return { kind: 'not-found', message: '请求的资源不存在。' }
  if (status === 409) return { kind: 'conflict', message: toChineseErrorMessage(error?.response?.data?.message || '请求与现有数据冲突。') }
  return { kind: 'error', message: toChineseErrorMessage(error?.response?.data?.message || error?.message) }
}

export function publishApiStatus(status) {
  localStorage.setItem(API_STATUS_KEY, JSON.stringify(status))
  window.dispatchEvent(new CustomEvent('campus:api-status', { detail: status }))
}
