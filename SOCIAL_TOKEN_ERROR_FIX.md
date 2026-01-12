# 社交平台Token认证错误修复

## 问题描述

用户在社交平台点赞时出现Token认证错误：

```
点赞操作失败: Error: Token无效，请重新登录
at request.ts:46:31
```

## 问题原因

### 可能的原因

1. **Token已过期**
   - JWT Token有过期时间
   - 用户长时间未操作，Token失效

2. **Token格式错误**
   - localStorage中的token格式不正确
   - Token被意外修改或损坏

3. **后端验证失败**
   - 后端JWT验证失败
   - 后端返回401或特定错误码

4. **未登录状态**
   - 用户实际上没有登录
   - localStorage中没有有效的token

## 当前Token处理逻辑

### request.ts 拦截器

```typescript
// 请求拦截器 - 添加Token
service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
      
      // 添加用户ID
      const userInfoStr = localStorage.getItem('userInfo')
      if (userInfoStr) {
        const userInfo = JSON.parse(userInfoStr)
        const userId = userInfo?.data?.id || userInfo?.id
        if (userId) {
          config.headers['X-User-Id'] = userId
        }
      }
    }
    return config
  }
)

// 响应拦截器 - 处理错误
service.interceptors.response.use(
  (response) => {
    const res = response?.data
    
    // 检查code
    if (res && typeof res === 'object' && 'code' in res) {
      if (res.code !== 200) {
        return Promise.reject(new Error(res.message || '请求失败'))
      }
      return res.data
    }
    
    return res
  },
  (error) => {
    // 处理401错误
    if (error.response && error.response.status === 401) {
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

## 诊断步骤

### 1. 检查Token是否存在

打开浏览器控制台，执行：

```javascript
// 检查token
console.log('Token:', localStorage.getItem('token'))

// 检查userInfo
console.log('UserInfo:', localStorage.getItem('userInfo'))

// 检查是否登录
console.log('Is Logged In:', !!localStorage.getItem('token'))
```

### 2. 检查Token格式

```javascript
const token = localStorage.getItem('token')
if (token) {
  console.log('Token length:', token.length)
  console.log('Token starts with:', token.substring(0, 20))
  
  // 尝试解析JWT
  try {
    const parts = token.split('.')
    if (parts.length === 3) {
      const payload = JSON.parse(atob(parts[1]))
      console.log('Token payload:', payload)
      console.log('Token expires:', new Date(payload.exp * 1000))
      console.log('Is expired:', Date.now() > payload.exp * 1000)
    }
  } catch (e) {
    console.error('Invalid JWT format:', e)
  }
}
```

### 3. 检查后端响应

在控制台Network标签中：
1. 找到失败的点赞请求
2. 查看Request Headers中的Authorization
3. 查看Response中的错误信息

## 解决方案

### 方案1：重新登录

最简单的解决方案：

```bash
1. 点击右上角头像
2. 选择"退出登录"
3. 重新登录
```

### 方案2：清除缓存并重新登录

在控制台执行：

```javascript
// 清除所有认证信息
localStorage.removeItem('token')
localStorage.removeItem('refreshToken')
localStorage.removeItem('userInfo')

// 刷新页面
location.reload()
```

### 方案3：增强Token错误处理

修改 `request.ts`，添加更详细的错误信息：

```typescript
service.interceptors.response.use(
  (response) => {
    const res = response?.data
    
    if (res && typeof res === 'object' && 'code' in res) {
      if (res.code !== 200) {
        // 特殊处理Token相关错误
        if (res.code === 401 || res.message?.includes('Token') || res.message?.includes('token')) {
          console.error('Token错误:', res.message)
          
          // 清除Token
          localStorage.removeItem('token')
          localStorage.removeItem('refreshToken')
          localStorage.removeItem('userInfo')
          
          // 提示用户重新登录
          import('element-plus').then(({ ElMessage }) => {
            ElMessage.error('登录已过期，请重新登录')
          })
          
          // 延迟跳转，让用户看到提示
          setTimeout(() => {
            if (window.location.pathname !== '/login') {
              window.location.href = '/login'
            }
          }, 1500)
          
          return Promise.reject(new Error('登录已过期'))
        }
        
        return Promise.reject(new Error(res.message || '请求失败'))
      }
      return res.data
    }
    
    return res
  },
  (error) => {
    // 处理401错误
    if (error.response && error.response.status === 401) {
      console.error('401 Unauthorized:', error.response.data)
      
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('userInfo')
      
      import('element-plus').then(({ ElMessage }) => {
        ElMessage.error('登录已过期，请重新登录')
      })
      
      setTimeout(() => {
        if (window.location.pathname !== '/login') {
          window.location.href = '/login'
        }
      }, 1500)
    }
    
    return Promise.reject(error)
  }
)
```

### 方案4：添加Token刷新机制

如果后端支持refreshToken，可以添加自动刷新：

```typescript
let isRefreshing = false
let failedQueue: any[] = []

const processQueue = (error: any, token: string | null = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error)
    } else {
      prom.resolve(token)
    }
  })
  
  failedQueue = []
}

service.interceptors.response.use(
  (response) => {
    // ... 现有逻辑
  },
  async (error) => {
    const originalRequest = error.config
    
    // 如果是401错误且不是刷新token请求
    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        // 如果正在刷新，将请求加入队列
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject })
        }).then(token => {
          originalRequest.headers.Authorization = `Bearer ${token}`
          return service(originalRequest)
        })
      }
      
      originalRequest._retry = true
      isRefreshing = true
      
      const refreshToken = localStorage.getItem('refreshToken')
      
      if (!refreshToken) {
        // 没有refreshToken，直接跳转登录
        localStorage.clear()
        window.location.href = '/login'
        return Promise.reject(error)
      }
      
      try {
        // 调用刷新token接口
        const response = await axios.post('/api/auth/refresh', {
          refreshToken
        })
        
        const { token: newToken } = response.data
        
        // 保存新token
        localStorage.setItem('token', newToken)
        
        // 更新请求头
        originalRequest.headers.Authorization = `Bearer ${newToken}`
        service.defaults.headers.common.Authorization = `Bearer ${newToken}`
        
        // 处理队列中的请求
        processQueue(null, newToken)
        
        // 重试原始请求
        return service(originalRequest)
      } catch (refreshError) {
        // 刷新失败，清除所有信息并跳转登录
        processQueue(refreshError, null)
        localStorage.clear()
        window.location.href = '/login'
        return Promise.reject(refreshError)
      } finally {
        isRefreshing = false
      }
    }
    
    return Promise.reject(error)
  }
)
```

## 用户操作指南

### 快速修复步骤

1. **打开浏览器控制台**（F12）

2. **执行清理命令**
   ```javascript
   localStorage.clear()
   location.reload()
   ```

3. **重新登录**
   - 访问登录页面
   - 输入用户名和密码
   - 登录成功后再次尝试点赞

### 预防措施

1. **定期刷新页面**
   - 长时间使用后刷新页面
   - 避免Token过期

2. **及时重新登录**
   - 看到"登录已过期"提示时立即重新登录
   - 不要忽略认证错误

3. **检查网络连接**
   - 确保网络稳定
   - 避免频繁切换网络

## 后端检查

### 检查JWT配置

确认后端JWT配置正确：

```java
// application.yml
jwt:
  secret: your-secret-key
  expiration: 86400000  # 24小时
  refresh-expiration: 604800000  # 7天
```

### 检查Token验证

确认后端正确验证Token：

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) {
        try {
            String token = getTokenFromRequest(request);
            
            if (token != null && jwtUtil.validateToken(token)) {
                // Token有效
                Long userId = jwtUtil.getUserIdFromToken(token);
                // ... 设置认证信息
            } else {
                // Token无效
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"code\":401,\"message\":\"Token无效，请重新登录\"}");
                return;
            }
        } catch (Exception e) {
            // Token验证异常
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"Token验证失败\"}");
            return;
        }
        
        filterChain.doFilter(request, response);
    }
}
```

## 总结

### 问题
用户Token无效或已过期，导致点赞等需要认证的操作失败。

### 快速解决
```javascript
// 在控制台执行
localStorage.clear()
location.reload()
// 然后重新登录
```

### 长期方案
1. 增强Token错误处理
2. 添加Token自动刷新机制
3. 改善用户提示体验
4. 定期检查Token有效性

---

**创建时间：** 2025-01-04 17:00  
**状态：** 待用户操作  
**优先级：** 🟡 中等（影响已登录用户的交互功能）
