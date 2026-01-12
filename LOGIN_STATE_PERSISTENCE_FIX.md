# 登录状态刷新丢失问题修复

## 问题描述
刷新页面后，登录状态丢失，用户变成游客状态。

## 根本原因

1. **App.vue 的激进验证**：每次刷新页面时，App.vue 的 `onMounted` 都会调用 `getCurrentUser()` 验证 token
2. **验证失败即登出**：如果验证失败（网络问题、服务未启动、token 格式问题），立即清除登录状态
3. **用户体验差**：即使 token 实际有效，临时的网络问题也会导致用户被登出

## 解决方案

### 1. 优化 App.vue 的 Token 验证策略

修改为更宽松的验证策略：

```typescript
// 如果有 token 但没有 userInfo，尝试获取用户信息
if (userStore.token && !userStore.userInfo) {
  try {
    const userInfo = await getCurrentUser()
    userStore.setUserInfo(userInfo)
  } catch (error) {
    // 只有在没有 userInfo 时才清除登录状态
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
    // 静默失败，不清除登录状态
    // 让 request.ts 的 401 拦截器处理真正的认证失败
    console.log('Background token validation failed, will handle on next request')
  }
}
```

**优点**：
- 如果 localStorage 中有完整的用户信息，直接使用，不等待验证
- 后台静默验证，失败不影响当前显示
- 真正的认证失败由 API 请求的 401 拦截器处理

### 2. 改进响应拦截器

修改 `request.ts` 的响应拦截器，正确处理嵌套的响应数据：

```typescript
service.interceptors.response.use(
  (response: AxiosResponse) => {
    // 如果响应数据有 data 字段，返回 data，否则返回整个响应
    const res = response.data
    if (res && typeof res === 'object' && 'data' in res) {
      return res.data
    }
    return res
  },
  // ... error handler
)
```

### 3. User Store 的持久化逻辑

`stores/user.ts` 已经正确实现了从 localStorage 恢复状态：

```typescript
// 从 localStorage 读取 token
const storedToken = localStorage.getItem('token')
const token = ref<string>(storedToken || '')
const userInfo = ref<any>(null)

// 如果有 token，尝试从 localStorage 恢复用户信息
if (storedToken) {
  const storedUserInfo = localStorage.getItem('userInfo')
  if (storedUserInfo) {
    try {
      userInfo.value = JSON.parse(storedUserInfo)
    } catch (e) {
      // 解析失败才清除
      localStorage.removeItem('userInfo')
      localStorage.removeItem('token')
      token.value = ''
    }
  }
}
```

## 工作流程

### 登录时
1. 用户输入账号密码
2. 调用 `/api/auth/login`
3. 保存 `accessToken`、`refreshToken`、`userInfo` 到 localStorage
4. 更新 Pinia store 状态
5. 跳转到目标页面

### 刷新页面时
1. Pinia store 从 localStorage 恢复 token 和 userInfo
2. NavBar 立即显示登录状态（基于 store 数据）
3. App.vue 在后台验证 token（不阻塞 UI）
4. 如果验证成功，静默更新用户信息
5. 如果验证失败但有 userInfo，继续显示登录状态
6. 下次 API 请求时，如果 401，由拦截器处理登出

### API 请求时
1. request.ts 自动添加 `Authorization: Bearer <token>` 头
2. 如果返回 401，拦截器清除登录状态并跳转登录页
3. 这是真正的认证失败处理点

## 测试步骤

1. **正常登录**：
   ```
   - 登录成功
   - 刷新页面
   - 应该保持登录状态
   ```

2. **Token 过期**：
   ```
   - 登录后等待 token 过期
   - 刷新页面（应该仍显示登录状态）
   - 进行任何 API 操作
   - 应该被 401 拦截器登出
   ```

3. **服务未启动**：
   ```
   - 登录后停止 user-service
   - 刷新页面
   - 应该仍显示登录状态（基于 localStorage）
   - 进行需要认证的操作时才会失败
   ```

## 已修改文件

1. `frontend/src/App.vue` - 优化 token 验证策略
2. `frontend/src/api/request.ts` - 改进响应数据处理

## 注意事项

- 这种方案优先考虑用户体验，允许在网络不稳定时继续使用
- 真正的安全验证在每个 API 请求时进行
- 如果需要更严格的验证，可以在 App.vue 中恢复原来的逻辑
