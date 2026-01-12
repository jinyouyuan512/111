# 快速修复：刷新后登录状态丢失

## 问题
刷新页面后，用户从登录状态变成游客状态。

## 快速诊断

### 1. 检查浏览器 localStorage
打开浏览器控制台（F12），在 Console 中输入：
```javascript
console.log('token:', localStorage.getItem('token'))
console.log('userInfo:', localStorage.getItem('userInfo'))
```

**预期结果**：
- 如果登录过，应该能看到 token 和 userInfo
- 如果刷新后消失，说明被清除了

### 2. 检查浏览器控制台错误
刷新页面后，查看 Console 是否有错误信息：
- `Token validation failed` - token 验证失败
- `401` 错误 - 认证失败
- 网络错误 - 服务未启动或无法连接

### 3. 检查 Network 请求
在 Network 标签中查看是否有 `/api/users/me` 请求：
- 如果有且返回 200 - 正常
- 如果有且返回 401 - token 无效或过期
- 如果没有请求 - 可能是前端逻辑问题

## 已应用的修复

### ✅ 修复 1：优化 App.vue 验证策略
- 不再在刷新时强制验证 token
- 如果 localStorage 有用户信息，直接使用
- 后台静默验证，失败不影响显示

### ✅ 修复 2：改进响应拦截器
- 正确处理嵌套的响应数据结构
- 只在真正的 401 错误时清除登录状态

## 测试步骤

1. **清除旧数据**：
   ```javascript
   localStorage.clear()
   ```

2. **重新登录**：
   - 访问 http://localhost:5173/login
   - 输入账号密码登录
   - 确认登录成功

3. **测试刷新**：
   - 按 F5 刷新页面
   - 检查右上角是否仍显示用户名
   - 如果显示"登录"按钮，说明问题仍存在

4. **检查 localStorage**：
   ```javascript
   console.log('token:', localStorage.getItem('token'))
   console.log('userInfo:', localStorage.getItem('userInfo'))
   ```

## 如果问题仍存在

### 方案 A：完全禁用刷新验证（最宽松）

修改 `frontend/src/App.vue`：
```typescript
onMounted(async () => {
  // 完全不验证，直接使用 localStorage 的数据
  // 让 API 请求时的 401 拦截器处理认证失败
  console.log('App mounted, using cached auth state')
})
```

### 方案 B：延迟验证（推荐）

修改 `frontend/src/App.vue`：
```typescript
onMounted(async () => {
  // 延迟 2 秒后再验证，确保页面已完全加载
  setTimeout(async () => {
    if (userStore.token && userStore.userInfo) {
      try {
        const userInfo = await getCurrentUser()
        userStore.setUserInfo(userInfo)
      } catch (error) {
        // 静默失败
        console.log('Background validation failed')
      }
    }
  }, 2000)
})
```

### 方案 C：只在特定情况下验证

修改 `frontend/src/App.vue`：
```typescript
onMounted(async () => {
  // 只在没有 userInfo 时验证
  if (userStore.token && !userStore.userInfo) {
    try {
      const userInfo = await getCurrentUser()
      userStore.setUserInfo(userInfo)
    } catch (error) {
      userStore.logout()
    }
  }
  // 有 userInfo 就直接使用，不验证
})
```

## 测试用的账号

如果需要重新测试，可以使用：
- 用户名：`testuser` 或 `admin`
- 密码：`password123`

或者重新注册一个新账号。

## 使用测试工具

打开 `test_user_me.html` 文件测试 `/api/users/me` 接口：
1. 在浏览器中打开 `test_user_me.html`
2. Token 会自动从 localStorage 读取
3. 点击"测试"按钮查看接口响应

## 相关文件

- `frontend/src/App.vue` - 应用入口，token 验证逻辑
- `frontend/src/stores/user.ts` - 用户状态管理
- `frontend/src/api/request.ts` - HTTP 请求拦截器
- `frontend/src/components/NavBar.vue` - 导航栏，显示登录状态
- `backend/user-service/.../UserController.java` - 用户信息接口

## 当前状态

✅ 已修改 App.vue - 使用宽松的验证策略
✅ 已修改 request.ts - 改进响应处理
✅ user-service 正在运行（端口 8081）
✅ social-service 正在运行（端口 8083）

现在刷新页面应该能保持登录状态了。
