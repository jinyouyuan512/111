<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h1>冀忆红途</h1>
        <p>红色文化数字生态平台</p>
      </div>

      <el-tabs v-model="activeTab" class="login-tabs">
        <!-- 登录表单 -->
        <el-tab-pane label="登录" name="login">
          <el-form
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            class="login-form"
            @submit.prevent="handleLogin"
          >
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                size="large"
                prefix-icon="User"
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                prefix-icon="Lock"
                show-password
                @keyup.enter="handleLogin"
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loginLoading"
                class="login-button"
                @click="handleLogin"
              >
                登录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 注册表单 -->
        <el-tab-pane label="注册" name="register">
          <el-form
            ref="registerFormRef"
            :model="registerForm"
            :rules="registerRules"
            class="login-form"
            @submit.prevent="handleRegister"
          >
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名"
                size="large"
                prefix-icon="User"
              />
            </el-form-item>

            <el-form-item prop="email">
              <el-input
                v-model="registerForm.email"
                placeholder="请输入邮箱"
                size="large"
                prefix-icon="Message"
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码（至少6位）"
                size="large"
                prefix-icon="Lock"
                show-password
              />
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请确认密码"
                size="large"
                prefix-icon="Lock"
                show-password
                @keyup.enter="handleRegister"
              />
            </el-form-item>

            <el-form-item prop="nickname">
              <el-input
                v-model="registerForm.nickname"
                placeholder="请输入昵称（可选）"
                size="large"
                prefix-icon="Avatar"
              />
            </el-form-item>

            <el-form-item prop="phone">
              <el-input
                v-model="registerForm.phone"
                placeholder="请输入手机号（可选）"
                size="large"
                prefix-icon="Phone"
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="registerLoading"
                class="login-button"
                @click="handleRegister"
              >
                注册
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { login, register } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeTab = ref('login')
const loginLoading = ref(false)
const registerLoading = ref(false)

const loginFormRef = ref<FormInstance>()
const registerFormRef = ref<FormInstance>()

// 登录表单
const loginForm = reactive({
  username: '',
  password: ''
})

// 注册表单
const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  phone: ''
})

// 登录表单验证规则
const loginRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少 6 个字符', trigger: 'blur' }
  ]
}

// 注册表单验证规则
const registerRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少 6 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return

    loginLoading.value = true
    try {
      console.log('发送登录请求:', loginForm)
      const response: any = await login(loginForm)
      console.log('登录响应:', response)
      
      // request.ts 拦截器已经自动提取了 data 字段
      // 所以 response 直接就是 { accessToken, refreshToken, userInfo }
      if (response && response.accessToken && response.userInfo) {
        const { accessToken, refreshToken, userInfo } = response
        
        // 保存token和用户信息
        userStore.setToken(accessToken)
        userStore.setUserInfo(userInfo)
        localStorage.setItem('refreshToken', refreshToken)
        
        ElMessage.success('登录成功')
        
        // 检查是否有重定向参数
        const redirect = route.query.redirect as string
        
        if (redirect) {
          // 如果有重定向参数，跳转到目标页面
          router.push(redirect)
        } else {
          // 否则根据用户角色跳转
          if (userInfo.role === 'admin') {
            // 管理员跳转到后台
            router.push('/admin')
          } else {
            // 普通用户跳转到首页
            router.push('/')
          }
        }
      } else {
        console.error('响应格式错误:', response)
        ElMessage.error('登录失败，响应格式错误')
      }
    } catch (error: any) {
      console.error('登录失败详情:', error)
      console.error('错误响应:', error.response)
      
      let errorMessage = '登录失败，请检查用户名和密码'
      
      if (error.response) {
        if (error.response.data) {
          if (error.response.data.message) {
            errorMessage = error.response.data.message
          } else if (error.response.data.msg) {
            errorMessage = error.response.data.msg
          } else if (typeof error.response.data === 'string') {
            errorMessage = error.response.data
          }
        }
        errorMessage += ` (状态码: ${error.response.status})`
      } else if (error.message) {
        errorMessage = error.message
      }
      
      ElMessage.error(errorMessage)
    } finally {
      loginLoading.value = false
    }
  })
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return

  await registerFormRef.value.validate(async (valid) => {
    if (!valid) return

    registerLoading.value = true
    try {
      const { confirmPassword, ...registerData } = registerForm
      console.log('发送注册请求:', registerData)
      const response: any = await register(registerData)
      console.log('注册响应:', response)
      
      // request.ts 拦截器已经自动提取了 data 字段
      // 注册成功后返回的是用户对象
      if (response && (response.id || response.username)) {
        ElMessage.success('注册成功，请登录')
        
        // 切换到登录标签
        activeTab.value = 'login'
        
        // 清空注册表单
        registerFormRef.value?.resetFields()
      } else {
        console.error('注册响应格式错误:', response)
        ElMessage.error('注册失败，响应格式错误')
      }
    } catch (error: any) {
      console.error('注册失败详情:', error)
      console.error('错误响应:', error.response)
      
      let errorMessage = '注册失败，请稍后重试'
      
      if (error.response) {
        // 服务器返回了错误响应
        if (error.response.data) {
          if (error.response.data.message) {
            errorMessage = error.response.data.message
          } else if (error.response.data.msg) {
            errorMessage = error.response.data.msg
          } else if (typeof error.response.data === 'string') {
            errorMessage = error.response.data
          }
        }
        errorMessage += ` (状态码: ${error.response.status})`
      } else if (error.message) {
        errorMessage = error.message
      }
      
      ElMessage.error(errorMessage)
    } finally {
      registerLoading.value = false
    }
  })
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-box {
  width: 100%;
  max-width: 450px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  padding: 40px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h1 {
  font-size: 32px;
  font-weight: bold;
  color: #333;
  margin: 0 0 10px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.login-header p {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.login-tabs {
  margin-top: 20px;
}

.login-form {
  margin-top: 20px;
}

.login-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.login-button:hover {
  opacity: 0.9;
}

:deep(.el-tabs__item) {
  font-size: 16px;
  font-weight: 500;
}

:deep(.el-tabs__item.is-active) {
  color: #667eea;
}

:deep(.el-tabs__active-bar) {
  background-color: #667eea;
}

:deep(.el-input__wrapper) {
  padding: 12px 15px;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}
</style>
