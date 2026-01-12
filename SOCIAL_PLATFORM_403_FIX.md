# 社区平台 403 错误修复

## 问题描述
用户在社区平台发布动态时遇到 403 Forbidden 错误，无法成功发布内容。

## 错误信息
```
POST http://localhost:3000/api/posts 403 (Forbidden)
```

## 根本原因分析

### 1. CORS 配置不完整
SecurityConfig 的 CORS 配置只允许 `http://localhost:3000` 和 `http://localhost:5173`，但前端实际运行在 `http://localhost:3001`。

### 2. 缺少全局异常处理
social-service 没有 GlobalExceptionHandler，导致认证/授权异常返回空响应，前端无法获取详细错误信息。

### 3. 端口配置不一致
- vite.config.ts 配置端口为 3000
- 实际运行端口为 3001
- SecurityConfig CORS 未包含 3001 端口

## 解决方案

### 1. 更新 CORS 配置

**文件**: `backend/social-service/src/main/java/com/jiyi/social/config/SecurityConfig.java`

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(
        "http://localhost:3000", 
        "http://localhost:3001",  // 添加 3001 端口
        "http://localhost:5173"
    ));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);
    configuration.setExposedHeaders(Arrays.asList("Authorization", "X-User-Id"));  // 暴露自定义头
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

**改进点**：
- 添加 `http://localhost:3001` 到允许的源
- 添加 `setExposedHeaders` 暴露自定义响应头
- 确保所有开发端口都被覆盖

### 2. 创建全局异常处理器

**文件**: `backend/social-service/src/main/java/com/jiyi/social/exception/GlobalExceptionHandler.java`

```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleAuthenticationException(AuthenticationException e) {
        log.error("认证失败: {}", e.getMessage());
        return Result.error(401, "认证失败，请先登录");
    }

    /**
     * 处理授权异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.error("权限不足: {}", e.getMessage());
        return Result.error(403, "权限不足，请先登录");
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        log.error("业务异常: {}", e.getMessage(), e);
        return Result.error(400, e.getMessage());
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return Result.error(500, "系统异常，请稍后重试");
    }
}
```

**功能**：
- 捕获 `AuthenticationException` 返回 401
- 捕获 `AccessDeniedException` 返回 403 并提供友好提示
- 捕获其他异常并记录日志
- 返回统一的 Result 格式

### 3. 统一前端端口配置

**文件**: `frontend/vite.config.ts`

```typescript
server: {
  port: 3001,  // 从 3000 改为 3001
  proxy: {
    // ... proxy 配置保持不变
  }
}
```

## 架构说明

### 请求流程

1. **前端发起请求**：
   ```typescript
   // Social.vue
   await socialApi.createPost({
     title: newPost.value.title,
     content: newPost.value.content,
     // ...
   })
   ```

2. **API 层添加认证头**：
   ```typescript
   // request.ts 拦截器
   const token = localStorage.getItem('token')
   if (token) {
     config.headers.Authorization = `Bearer ${token}`
   }
   ```

3. **Vite 代理转发**：
   ```
   http://localhost:3001/api/posts 
   → http://localhost:8083/api/posts
   ```

4. **Spring Security 过滤链**：
   ```
   CORS Filter → JWT Filter → Authorization Filter
   ```

5. **JWT 认证过滤器**：
   ```java
   // SecurityConfig.jwtAuthenticationFilter()
   String token = authHeader.substring(7);
   if (JwtUtil.validateToken(token)) {
     String userId = JwtUtil.getUserIdFromToken(token);
     // 设置 SecurityContext
   }
   ```

6. **Controller 方法授权**：
   ```java
   @PreAuthorize("isAuthenticated()")
   public Result<PostVO> createPost(...)
   ```

7. **异常处理**：
   ```
   AccessDeniedException 
   → GlobalExceptionHandler 
   → Result.error(403, "权限不足，请先登录")
   ```

### 认证流程图

```
┌─────────────┐
│  前端请求    │
│ + Bearer Token │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ CORS Filter  │ ← 检查 Origin
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  JWT Filter  │ ← 验证 Token
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ @PreAuthorize│ ← 检查权限
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  Controller  │
└─────────────┘
```

## 常见问题排查

### 1. 仍然返回 403

**检查项**：
- ✅ 前端是否已登录（localStorage 有 token）
- ✅ Token 是否有效（未过期）
- ✅ Authorization 头是否正确发送
- ✅ CORS 配置是否包含当前端口
- ✅ SecurityConfig 是否正确配置

**调试命令**：
```javascript
// 浏览器控制台
console.log('Token:', localStorage.getItem('token'))
console.log('UserInfo:', localStorage.getItem('userInfo'))
```

### 2. Token 验证失败

**可能原因**：
- Token 格式错误（缺少 "Bearer " 前缀）
- Token 已过期
- JWT 密钥不一致
- Token 签名验证失败

**解决方法**：
```java
// 在 JwtAuthenticationFilter 中添加日志
log.debug("Token: {}", token);
log.debug("Token valid: {}", JwtUtil.validateToken(token));
log.debug("UserId: {}", JwtUtil.getUserIdFromToken(token));
```

### 3. CORS 预检请求失败

**现象**：
```
Access to XMLHttpRequest at 'http://localhost:8083/api/posts' 
from origin 'http://localhost:3001' has been blocked by CORS policy
```

**解决**：
- 确保 SecurityConfig 的 CORS 配置包含前端端口
- 确保 `setAllowCredentials(true)` 已设置
- 确保 `setAllowedHeaders(Arrays.asList("*"))` 允许所有头

### 4. 前端未发送 Authorization 头

**检查**：
```typescript
// request.ts
service.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  console.log('Sending token:', token)  // 添加日志
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})
```

## 测试验证

### 1. 测试登录状态
```javascript
// 浏览器控制台
localStorage.getItem('token')  // 应该返回 JWT token
localStorage.getItem('userInfo')  // 应该返回用户信息
```

### 2. 测试发布动态
1. 登录系统
2. 进入社区页面
3. 点击"发布"按钮
4. 填写内容
5. 点击"发布"
6. ✅ 应该成功发布，不再返回 403

### 3. 测试 CORS
```bash
# 使用 curl 测试
curl -X OPTIONS http://localhost:8083/api/posts \
  -H "Origin: http://localhost:3001" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Authorization" \
  -v
```

应该返回：
```
Access-Control-Allow-Origin: http://localhost:3001
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
Access-Control-Allow-Headers: *
```

## 修改文件清单

1. ✅ `backend/social-service/src/main/java/com/jiyi/social/config/SecurityConfig.java`
   - 更新 CORS 配置，添加 3001 端口
   - 添加 exposedHeaders 配置

2. ✅ `backend/social-service/src/main/java/com/jiyi/social/exception/GlobalExceptionHandler.java`
   - 新建全局异常处理器
   - 处理认证和授权异常

3. ✅ `frontend/vite.config.ts`
   - 统一端口配置为 3001

## 重启服务

修改后需要重启 social-service：

```bash
# Windows
cd backend/social-service
mvn spring-boot:run

# 或使用启动脚本
START_SOCIAL_SERVICE.bat
```

## 验证清单

- ✅ social-service 成功启动在 8083 端口
- ✅ 前端运行在 3001 端口
- ✅ 登录后可以获取 token
- ✅ 发布动态不再返回 403
- ✅ 发布成功后动态出现在列表中
- ✅ 刷新页面后动态仍然存在

## 后续优化建议

### 1. 统一认证方式
考虑在所有微服务中使用相同的认证机制：
- user-service: JWT
- mall-service: JWT
- social-service: JWT

### 2. 添加 Token 刷新机制
```typescript
// 在 request.ts 中添加
if (error.response?.status === 401) {
  // 尝试刷新 token
  const refreshToken = localStorage.getItem('refreshToken')
  if (refreshToken) {
    // 调用刷新接口
  }
}
```

### 3. 改进错误提示
```typescript
// Social.vue
catch (error: any) {
  if (error.response?.status === 403) {
    ElMessage.error('请先登录后再发布动态')
    router.push('/login')
  } else {
    ElMessage.error(error.response?.data?.message || '发布失败')
  }
}
```

### 4. 添加请求重试机制
```typescript
// request.ts
import axios from 'axios'
import axiosRetry from 'axios-retry'

axiosRetry(service, {
  retries: 3,
  retryDelay: axiosRetry.exponentialDelay
})
```

## 总结

社区平台 403 错误已修复，主要改进：
1. ✅ 更新 CORS 配置支持所有开发端口
2. ✅ 添加全局异常处理器提供友好错误信息
3. ✅ 统一前端端口配置
4. ✅ 确保 JWT 认证流程正常工作

现在用户可以正常登录并发布社区动态了！
