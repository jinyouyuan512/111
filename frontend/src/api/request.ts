import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'

const service: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000
})

service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
      
      // 从localStorage获取userInfo并添加X-User-Id头
      const userInfoStr = localStorage.getItem('userInfo')
      if (userInfoStr) {
        try {
          const userInfo = JSON.parse(userInfoStr)
          // userInfo可能是{code, message, data}格式，也可能直接是用户对象
          const userId = userInfo?.data?.id || userInfo?.id
          if (userId) {
            config.headers['X-User-Id'] = userId
          }
        } catch (e) {
          console.error('Failed to parse userInfo:', e)
        }
      }
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  (response: AxiosResponse) => {
    // 如果响应数据有 data 字段，返回 data，否则返回整个响应
    const res = response?.data
    
    // 检查是否是标准的 Result 格式 {code, message, data}
    if (res && typeof res === 'object' && 'code' in res) {
      // 如果code不是200，抛出错误
      if (res.code !== 200) {
        return Promise.reject(new Error(res.message || '请求失败'))
      }
      // 返回data字段
      return res.data
    }
    
    // 否则返回原始响应
    return res
  },
  (error) => {
    // 处理 401 未授权错误（token 过期或无效）
    if (error.response && error.response.status === 401) {
      // 清除无效的 token 和用户信息
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('userInfo')
      
      // 如果不在登录页，跳转到登录页
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

export default service
