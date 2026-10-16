import axios from 'axios'
import { ElMessage } from 'element-plus'
import { describeApiError, publishApiStatus, toChineseErrorMessage } from './status'
import { formatTokenHeader } from './auth'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/campus-api',
  headers: { 'Content-Type': 'application/json' }
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('campus_token')
  if (token) {
    config.headers.Authorization = formatTokenHeader(token)
  }
  if (typeof FormData !== 'undefined' && config.data instanceof FormData) {
    delete config.headers['Content-Type']
    delete config.headers['content-type']
  }
  return config
})

http.interceptors.response.use((response) => {
  const body = response.data
  const isApiResponse = body && typeof body === 'object' && Number.isInteger(body.code) && typeof body.message === 'string'
  if (isApiResponse) publishApiStatus({ kind: 'connected', message: '后端接口已连接' })
  if (isApiResponse && body.code !== 200) {
    const authError = /没有登[录陆]|登录已失效|登录状态已失效/.test(body.message)
    const businessError = new Error(toChineseErrorMessage(body.message))
    businessError.response = { status: authError ? 401 : body.code, data: body }
    return Promise.reject(businessError)
  }
  return response
}, (error) => {
  const status = describeApiError(error)
  publishApiStatus(status)
  if (error.response?.status === 401) {
    localStorage.removeItem('campus_token')
    localStorage.removeItem('campus_user')
    if (typeof window !== 'undefined' && !window.location.hash.startsWith('#/login')) {
      const currentRoute = window.location.hash.replace(/^#/, '') || '/'
      window.location.assign(`/#/login?redirect=${encodeURIComponent(currentRoute)}`)
    }
  } else if (typeof window !== 'undefined') {
    ElMessage({
      message: status.message,
      type: 'error',
      showClose: true,
      duration: 4200,
      customClass: 'campus-message campus-message-error'
    })
  }
  const wrapped = new Error(status.message)
  wrapped.kind = status.kind
  return Promise.reject(wrapped)
})

export default http
