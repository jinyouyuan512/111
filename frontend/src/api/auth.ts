import request from './request'

export interface RegisterRequest {
  username: string
  email: string
  password: string
  nickname?: string
  phone?: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  accessToken: string
  refreshToken: string
  userInfo: {
    id: number
    username: string
    email: string
    nickname: string
    avatar: string
    role: string
  }
}

/**
 * 用户注册
 */
export const register = (data: RegisterRequest) => {
  return request.post('/auth/register', data)
}

/**
 * 用户登录
 */
export const login = (data: LoginRequest) => {
  return request.post<LoginResponse>('/auth/login', data)
}

/**
 * 刷新令牌
 */
export const refreshToken = (refreshToken: string) => {
  return request.post('/auth/refresh', { refreshToken })
}

/**
 * 获取当前用户信息
 */
export const getCurrentUser = () => {
  return request.get('/users/me')
}
