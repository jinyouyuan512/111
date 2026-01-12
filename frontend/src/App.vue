<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getCurrentUser } from '@/api/auth'

const userStore = useUserStore()

// 验证 token 有效性（延迟验证，避免影响页面加载）
onMounted(async () => {
  // 如果有 token 但没有 userInfo，尝试获取用户信息
  if (userStore.token && !userStore.userInfo) {
    try {
      const userInfo = await getCurrentUser()
      // Token 有效，更新用户信息
      userStore.setUserInfo(userInfo)
    } catch (error) {
      // Token 无效或过期，清除登录状态
      console.log('Token validation failed, clearing auth state')
      userStore.logout()
    }
  }
  
  // 如果有 token 和 userInfo，进行后台验证（不影响显示）
  if (userStore.token && userStore.userInfo) {
    try {
      const userInfo = await getCurrentUser()
      // 静默更新用户信息
      userStore.setUserInfo(userInfo)
    } catch (error) {
      // 静默失败，不清除登录状态，让用户继续使用
      // 只有在实际请求时才会因为 401 被拦截器处理
      console.log('Background token validation failed, will handle on next request')
    }
  }
})
</script>

<style>
#app {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei',
    '微软雅黑', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
</style>
