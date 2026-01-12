# 登录响应格式错误修复

## 问题
登录失败，提示"响应格式错误"。

## 根本原因

### 响应数据流转

1. **后端返回**：
   ```java
   Result.success(loginResponse)
   ```
   实际返回：
   ```json
   {
     "code": 200,
     "message": "success",
     "data": {
       "accessToken": "...",
       "refreshToken": "...",
       "userInfo": {...}
     }
   }
   ```

2. **request.ts 拦截器处理**：
   ```typescript
   service.interceptors.response.use(
     (response: AxiosResponse) => {
       const res = response.data
       if (res && typeof res === 'object' && 'data' in res) {
         return res.data  // 自动提取 data 字段
       }
       return res
     }
   )
   ```
   
3. **前端收到的数据**：
   ```json
   {
     "accessToken": "...",
     "refreshToken": "...",
     "userInfo": {...}
   }
   ```

4. **前端原来的判断**：
   ```typescript
   if (response && response.code === 200 && response.data) {
     const { accessToken, refreshToken, userInfo } = response.data
     // ❌ 错误：response 已经是 data，没有 code 字段
   }
   ```

## 解决方案

### 修复登录逻辑

**修改前**：
```typescript
if (response && response.code === 200 && response.data) {
  const { accessToken, refreshToken, userInfo } = response.data
  // ...
}
```

**修改后**：
```typescript
// request.ts 拦截器已经自动提取了 data 字段
if (response && response.accessToken && response.userInfo) {
  const { accessToken, refreshToken, userInfo } = response
  // ...
}
```

### 修复注册逻辑

**修改前**：
```typescript
if (response && response.code === 200) {
  ElMessage.success('注册成功，请登录')
  // ...
}
```

**修改后**：
```typescript
// 注册成功后返回的是用户对象
if (response && (response.id || response.username)) {
  ElMessage.success('注册成功，请登录')
  // ...
}
```

## 响应拦截器的设计

`request.ts` 的响应拦截器设计为自动提取 `data` 字段，简化前端代码：

```typescript
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data
    // 如果响应有 data 字段，自动提取
    if (res && typeof res === 'object' && 'data' in res) {
      return res.data
    }
    return res
  },
  (error) => {
    // 错误处理
    if (error.response && error.response.status === 401) {
      // 清除登录状态
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('userInfo')
      
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)
```

**优点**：
- 前端代码更简洁，不需要每次都写 `response.data`
- 统一处理响应格式
- 自动处理 401 认证失败

**注意事项**：
- 前端代码不应该再检查 `response.code`
- 直接使用返回的数据对象
- 错误通过 catch 块处理

## 正确的使用方式

### API 调用
```typescript
// ✅ 正确
const loginData = await login({ username, password })
// loginData 直接就是 { accessToken, refreshToken, userInfo }

// ❌ 错误
const response = await login({ username, password })
if (response.code === 200) {
  const loginData = response.data
}
```

### 错误处理
```typescript
try {
  const data = await someApi()
  // 使用 data
} catch (error) {
  // 处理错误
  if (error.response) {
    // 服务器返回了错误响应
    const errorMsg = error.response.data?.message || '操作失败'
    ElMessage.error(errorMsg)
  } else {
    // 网络错误或其他错误
    ElMessage.error('网络错误，请稍后重试')
  }
}
```

## 测试步骤

1. **清除旧数据**：
   ```javascript
   localStorage.clear()
   ```

2. **测试注册**：
   - 访问 http://localhost:3001/login
   - 切换到"注册"标签
   - 填写注册信息
   - 点击注册
   - ✅ 应该显示"注册成功，请登录"

3. **测试登录**：
   - 输入刚注册的账号密码
   - 点击登录
   - ✅ 应该显示"登录成功"并跳转

4. **测试刷新**：
   - 刷新页面
   - ✅ 应该保持登录状态

## 已修改文件

1. ✅ `frontend/src/views/Login.vue` - 修复登录和注册的响应处理
2. ✅ `frontend/src/api/request.ts` - 响应拦截器自动提取 data

## 相关文件

- `frontend/src/api/auth.ts` - 认证 API 定义
- `backend/user-service/.../AuthController.java` - 认证接口实现
- `frontend/src/stores/user.ts` - 用户状态管理

## 当前状态

✅ 已修复登录响应格式处理
✅ 已修复注册响应格式处理
✅ 响应拦截器正确提取 data 字段
✅ 前端服务运行中（端口 3001）

现在可以正常登录和注册了。
