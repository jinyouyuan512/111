# 登录状态持久化修复

## 问题描述
用户反复遇到登录状态丢失的问题，即使刚登录成功，刷新页面或切换路由后登录状态就消失了。

## 根本原因分析

### 1. 响应拦截器处理不当
`request.ts` 的响应拦截器只检查了 `'data' in res`，但没有验证 `code` 字段，导致：
- 即使请求失败（code !== 200），也会返回 data
- 可能返回 undefined 或 null 的 data

### 2. localStorage 存储时机问题
- 登录成功后，数据可能没有正确保存到 localStorage
- userInfo 可能被保存为 undefined 或 null

### 3. App.vue 验证逻辑问题
- 在页面加载时立即验证 token，可能导致竞态条件
- 如果验证失败，会清除登录状态，但用户刚刚登录成功

## 修复方案

### 1. 改进响应拦截器 ✅
```typescript
service.interceptors.response.use(
  (response: AxiosResponse) => {
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
  // ... error handler
)
```

**改进点：**
- 明确检查 `code === 200`
- 失败时抛出错误而不是返回 undefined
- 保证返回的数据是有效的

### 2. 登录流程验证
```typescript
// Login.vue 中的处理
if (response && response.accessToken && response.userInfo) {
  const { accessToken, refreshToken, userInfo } = response
  
  // 保存token和用户信息
  userStore.setToken(accessToken)
  userStore.setUserInfo(userInfo)
  localStorage.setItem('refreshToken', refreshToken)
  
  console.log('登录成功，已保存:', {
    token: accessToken.substring(0, 20) + '...',
    userInfo: userInfo
  })
}
```

### 3. User Store 持久化
```typescript
const setToken = (newToken: string) => {
  token.value = newToken
  localStorage.setItem('token', newToken)
}

const setUserInfo = (info: any) => {
  userInfo.value = info
  localStorage.setItem('userInfo', JSON.stringify(info))
}
```

## 测试步骤

### 使用测试页面
打开 `test_login_persistence.html` 进行测试：

1. **登录测试**
   - 输入用户名: chovy
   - 输入密码: 123456
   - 点击"登录"按钮
   - 检查是否显示"登录成功"

2. **检查存储**
   - 点击"检查存储"按钮
   - 确认 token、refreshToken、userInfo 都存在
   - 查看 userInfo 的内容是否正确

3. **验证 Token**
   - 点击"验证当前Token"按钮
   - 确认 token 有效，能获取用户信息

4. **刷新页面测试**
   - 刷新浏览器页面
   - 再次点击"检查存储"
   - 确认数据仍然存在

### 在实际应用中测试

1. **登录测试**
   ```
   访问: http://localhost:3001/login
   用户名: chovy
   密码: 123456
   ```

2. **检查浏览器控制台**
   - 打开 DevTools (F12)
   - 切换到 Application 标签
   - 查看 Local Storage
   - 确认有 token、refreshToken、userInfo

3. **刷新页面测试**
   - 登录成功后，按 F5 刷新页面
   - 检查右上角是否仍显示用户名
   - 检查 localStorage 是否仍有数据

4. **路由切换测试**
   - 登录后访问不同页面（首页、社交、商城等）
   - 确认每个页面都能保持登录状态

## 调试技巧

### 1. 检查 localStorage
在浏览器控制台执行：
```javascript
console.log('token:', localStorage.getItem('token'))
console.log('userInfo:', localStorage.getItem('userInfo'))
console.log('refreshToken:', localStorage.getItem('refreshToken'))
```

### 2. 监控登录流程
在 Login.vue 的 handleLogin 中添加更多日志：
```typescript
console.log('1. 发送登录请求')
console.log('2. 收到响应:', response)
console.log('3. 保存token:', accessToken)
console.log('4. 保存userInfo:', userInfo)
console.log('5. 验证localStorage:', localStorage.getItem('token'))
```

### 3. 检查请求拦截器
在 request.ts 的请求拦截器中添加日志：
```typescript
service.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  console.log('请求拦截器 - token存在:', !!token)
  // ...
})
```

## 常见问题

### Q1: 登录成功但立即丢失状态
**原因：** App.vue 的 token 验证失败，清除了登录状态
**解决：** 检查 user-service 是否正常运行，端口是否正确

### Q2: localStorage 有数据但页面显示未登录
**原因：** userInfo 格式不正确或解析失败
**解决：** 检查 userInfo 的 JSON 格式，确保可以正确解析

### Q3: 刷新页面后 token 消失
**原因：** 浏览器隐私模式或扩展程序阻止了 localStorage
**解决：** 使用普通模式，禁用可能干扰的浏览器扩展

### Q4: Token 验证返回 401
**原因：** Token 已过期或格式不正确
**解决：** 检查 JWT 配置，确保 token 有效期足够长

## 后续优化建议

1. **添加 Token 刷新机制**
   - 在 token 即将过期时自动刷新
   - 避免用户在使用过程中突然掉线

2. **添加心跳检测**
   - 定期验证 token 有效性
   - 提前发现 token 失效问题

3. **改进错误提示**
   - 当登录状态丢失时，给出明确的提示
   - 引导用户重新登录

4. **添加登录状态监控**
   - 记录登录状态变化
   - 帮助诊断问题

## 修复文件清单

- ✅ `frontend/src/api/request.ts` - 改进响应拦截器
- ✅ `test_login_persistence.html` - 添加测试页面
- ✅ `LOGIN_PERSISTENCE_FIX.md` - 本文档

## 验证清单

- [ ] 登录成功后 localStorage 有 token
- [ ] 登录成功后 localStorage 有 userInfo
- [ ] 刷新页面后登录状态保持
- [ ] 切换路由后登录状态保持
- [ ] Token 验证接口返回正确数据
- [ ] 控制台没有 "Failed to parse userInfo" 错误
