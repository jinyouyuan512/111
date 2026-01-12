import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  // 从 localStorage 读取 token，但不立即显示为已登录
  // 需要等待 App.vue 中的 token 验证完成
  const storedToken = localStorage.getItem('token')
  const token = ref<string>(storedToken || '')
  const userInfo = ref<any>(null)

  // 如果有 token，尝试从 localStorage 恢复用户信息
  // 但这只是临时的，App.vue 会验证 token 有效性
  if (storedToken) {
    const storedUserInfo = localStorage.getItem('userInfo')
    if (storedUserInfo) {
      try {
        userInfo.value = JSON.parse(storedUserInfo)
      } catch (e) {
        console.error('Failed to parse userInfo from localStorage', e)
        // 如果解析失败，清除无效数据
        localStorage.removeItem('userInfo')
        localStorage.removeItem('token')
        token.value = ''
      }
    }
  }

  // 计算属性：是否已登录
  const isLoggedIn = computed(() => {
    return !!token.value && !!userInfo.value
  })

  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUserInfo = (info: any) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('userInfo')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    setToken,
    setUserInfo,
    logout
  }
})
