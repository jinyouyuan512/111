# 登录状态显示问题修复

## 问题描述
用户未登录，但除了首页外的所有页面都显示已登录状态（右上角显示用户头像和下拉菜单）。

## 根本原因
1. **Token 持久化但未验证**: `userStore` 在初始化时从 `localStorage` 读取 token 和 userInfo，但没有验证 token 是否有效
2. **延迟验证**: 只有在发起 API 请求时才会触发 401 错误处理，如果用户没有触发任何 API 调用，过期的 token 会一直存在
3. **NavBar 依赖 token**: `NavBar.vue` 通过 `v-if="userStore.token"` 判断是否显示登录状态，只要 token 存在就显示已登录

## 解决方案

### 1. App.vue - 添加启动时 Token 验证
在应用启动时立即验证 token 有效性：

```typescript
onMounted(async () => {
  // 如果有 token，验证其有效性
  if (userStore.token) {
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
})
```

### 2. request.ts - 增强 401 错误处理
在响应拦截器中同时清除 userInfo：

```typescript
if (error.response && error.response.status === 401) {
  // 清除无效的 token 和用户信息
  localStorage.removeItem('token')
  localStorage.removeItem('refreshToken')
  localStorage.removeItem('userInfo')  // 新增
  
  // 如果不在登录页，跳转到登录页
  if (window.location.pathname !== '/login') {
    window.location.href = '/login'
  }
}
```

### 3. user.ts - 改进注释说明
更新注释说明 token 验证流程，明确指出初始加载的数据需要等待 App.vue 验证。

## 验证步骤

1. **清除旧数据**: 
   - 打开浏览器开发者工具 (F12)
   - 进入 Application/应用 → Local Storage
   - 删除 `token`、`refreshToken`、`userInfo`

2. **刷新页面**: 按 F5 或 Ctrl+R 刷新页面

3. **检查状态**: 
   - 所有页面右上角应该显示"登录"按钮
   - 不应该显示用户头像和下拉菜单

4. **测试登录**: 
   - 点击登录按钮
   - 使用 `ruler` 账号登录
   - 登录后应该正确显示用户信息

## 技术细节

### Token 验证流程
1. 应用启动 → `App.vue` 的 `onMounted` 执行
2. 检查 `userStore.token` 是否存在
3. 如果存在，调用 `getCurrentUser()` API
4. 后端验证 token:
   - 有效 → 返回用户信息 → 更新 store
   - 无效 → 返回 401 → 清除所有认证数据
5. NavBar 根据最新的 store 状态显示

### 双重保护机制
1. **启动验证**: App.vue 在应用启动时主动验证
2. **请求验证**: request.ts 在每次 API 调用时被动验证

这确保了无论用户如何操作，过期的 token 都会被及时清除。

## 修改的文件
- `frontend/src/App.vue` - 添加 token 验证逻辑
- `frontend/src/api/request.ts` - 增强 401 错误处理
- `frontend/src/stores/user.ts` - 改进注释说明

## 测试结果
✅ 应用启动时自动验证 token
✅ 无效 token 被立即清除
✅ NavBar 正确显示登录/未登录状态
✅ 401 错误自动跳转到登录页
✅ 登录后正确保存和显示用户信息
