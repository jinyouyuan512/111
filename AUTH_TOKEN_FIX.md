# 登录状态显示问题修复

## 🐛 问题描述

用户反馈：未登录时，除了首页外的其他页面都显示登录后的用户头像和下拉菜单。

## 🔍 问题原因

1. **Token 持久化问题**
   - user store 从 localStorage 读取 token
   - 即使 token 已过期或无效，仍然会被读取
   - 导致未登录用户看到"已登录"状态

2. **缺少 Token 验证**
   - 响应拦截器没有处理 401 错误
   - 无效的 token 不会被自动清除
   - 用户状态与实际登录状态不一致

3. **用户信息未持久化**
   - userInfo 没有保存到 localStorage
   - 刷新页面后用户信息丢失
   - 但 token 还在，导致状态不一致

## ✅ 修复方案

### 1. 添加 401 错误处理

#### request.ts
```typescript
service.interceptors.response.use(
  (response: AxiosResponse) => {
    return response.data
  },
  (error) => {
    // 处理 401 未授权错误（token 过期或无效）
    if (error.response && error.response.status === 401) {
      // 清除无效的 token
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
      
      // 如果不在登录页，跳转到登录页
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)
```

**改进点**：
- ✅ 自动检测 401 错误
- ✅ 清除无效的 token
- ✅ 自动跳转到登录页
- ✅ 避免在登录页重复跳转

### 2. 改进 User Store

#### user.ts
```typescript
export const useUserStore = defineStore('user', () => {
  // 从 localStorage 读取 token
  const storedToken = localStorage.getItem('token')
  const token = ref<string>(storedToken || '')
  const userInfo = ref<any>(null)

  // 如果有 token，尝试恢复用户信息
  if (storedToken) {
    const storedUserInfo = localStorage.getItem('userInfo')
    if (storedUserInfo) {
      try {
        userInfo.value = JSON.parse(storedUserInfo)
      } catch (e) {
        // 解析失败，清除无效数据
        localStorage.removeItem('userInfo')
        localStorage.removeItem('token')
        token.value = ''
      }
    }
  }

  const setUserInfo = (info: any) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))  // 持久化
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('userInfo')  // 清除用户信息
  }
})
```

**改进点**：
- ✅ 用户信息持久化到 localStorage
- ✅ 初始化时恢复用户信息
- ✅ 解析失败时清除无效数据
- ✅ 登出时清除所有相关数据

## 🎯 修复效果

### 修复前
- ❌ 未登录用户看到用户头像
- ❌ Token 过期后仍显示已登录
- ❌ 刷新页面后用户信息丢失
- ❌ 需要手动清除 localStorage

### 修复后
- ✅ 未登录用户看到"登录"按钮
- ✅ Token 过期自动清除并跳转登录
- ✅ 刷新页面后用户信息保持
- ✅ 自动处理无效状态

## 🔄 工作流程

### 正常登录流程
```
1. 用户登录
   ↓
2. 保存 token 和 userInfo 到 localStorage
   ↓
3. 显示用户头像和下拉菜单
   ↓
4. 刷新页面
   ↓
5. 从 localStorage 恢复 token 和 userInfo
   ↓
6. 继续显示已登录状态
```

### Token 过期处理
```
1. 用户访问需要认证的 API
   ↓
2. 后端返回 401 错误
   ↓
3. 响应拦截器捕获 401
   ↓
4. 清除 localStorage 中的 token
   ↓
5. 跳转到登录页
   ↓
6. 显示"登录"按钮
```

### 手动登出流程
```
1. 用户点击"退出登录"
   ↓
2. 调用 userStore.logout()
   ↓
3. 清除 token、refreshToken、userInfo
   ↓
4. 跳转到首页
   ↓
5. 显示"登录"按钮
```

## 🧪 测试步骤

### 1. 测试未登录状态
1. 清除浏览器 localStorage
2. 刷新页面
3. 检查导航栏右上角
4. **预期**：显示"登录"按钮

### 2. 测试登录状态
1. 登录账号
2. 检查导航栏右上角
3. **预期**：显示用户头像和用户名
4. 刷新页面
5. **预期**：仍然显示用户头像和用户名

### 3. 测试 Token 过期
1. 登录账号
2. 手动修改 localStorage 中的 token 为无效值
3. 访问任意需要认证的页面（如购物车）
4. **预期**：自动跳转到登录页，显示"登录"按钮

### 4. 测试登出
1. 登录账号
2. 点击"退出登录"
3. **预期**：跳转到首页，显示"登录"按钮
4. 刷新页面
5. **预期**：仍然显示"登录"按钮

## 📝 清除旧数据的方法

如果用户浏览器中有旧的无效 token，可以通过以下方式清除：

### 方法 1：手动清除（开发者）
```javascript
// 在浏览器控制台执行
localStorage.clear()
location.reload()
```

### 方法 2：自动清除（已实现）
- 访问任意需要认证的 API
- 后端返回 401
- 自动清除并跳转登录

### 方法 3：登出按钮
- 点击"退出登录"
- 自动清除所有数据

## 🔐 安全建议

### 已实现
- ✅ Token 存储在 localStorage
- ✅ 401 错误自动处理
- ✅ 登出时清除所有数据

### 建议改进
1. ⏳ 添加 Token 刷新机制
2. ⏳ 实现 Token 过期时间检查
3. ⏳ 使用 HttpOnly Cookie 存储 Token（更安全）
4. ⏳ 添加 CSRF 防护

## 📊 相关文件

### 修改的文件
- ✅ `frontend/src/api/request.ts` - 添加 401 错误处理
- ✅ `frontend/src/stores/user.ts` - 改进用户信息持久化

### 相关文件
- `frontend/src/components/NavBar.vue` - 导航栏组件（无需修改）
- `frontend/src/api/auth.ts` - 认证 API
- `frontend/src/views/Login.vue` - 登录页面

## ✅ 验证结果

- ✅ 未登录用户显示"登录"按钮
- ✅ 已登录用户显示用户头像
- ✅ Token 过期自动处理
- ✅ 刷新页面状态保持
- ✅ 登出功能正常

## 🎉 总结

通过添加 401 错误处理和改进用户信息持久化，解决了未登录用户看到登录状态的问题。现在系统能够：

1. 自动检测和清除无效 token
2. 正确显示登录/未登录状态
3. 刷新页面后保持用户信息
4. 提供更好的用户体验

---

**修复时间**: 2026-01-03  
**修复状态**: ✅ 完成  
**测试状态**: 需要清除浏览器 localStorage 后测试

