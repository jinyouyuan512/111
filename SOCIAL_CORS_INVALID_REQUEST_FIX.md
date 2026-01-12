# 社区平台 "Invalid CORS request" 错误修复

## 问题描述
用户在社区平台发布动态时遇到 403 错误，错误信息为 "Invalid CORS request"。

## 错误信息
```
Failed to load resource: the server responded with a status of 403 (Forbidden)
发布失败: AxiosError
错误详情: Object
错误数据: Invalid CORS request
```

## 根本原因

### 1. CORS 配置使用 setAllowedOrigins 的限制
Spring Security 的 `setAllowedOrigins()` 方法在与 `setAllowCredentials(true)` 一起使用时有严格的限制：
- 不能使用通配符 `*`
- 必须精确匹配每个允许的源
- 如果前端端口变化，需要手动更新配置

### 2. 端口不匹配问题
- 前端可能运行在不同端口（3000, 3001, 5173等）
- 每次端口变化都需要更新后端 CORS 配置
- 开发环境中端口经常变化

### 3. Spring Security CORS 处理顺序
Spring Security 在处理请求时的顺序：
1. CORS 预检请求（OPTIONS）
2. 认证过滤器
3. 授权过滤器

如果 CORS 配置不正确，请求会在第一步就被拒绝。

## 解决方案

### 使用 setAllowedOriginPatterns 替代 setAllowedOrigins

**修改文件**: `backend/social-service/src/main/java/com/jiyi/social/config/SecurityConfig.java`

**修改前**:
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(
        "http://localhost:3000", 
        "http://localhost:3001",
        "http://localhost:5173"
    ));
    // ...
}
```

**问题**:
- 需要列举所有可能的端口
- 端口变化时需要修改代码
- 不够灵活

**修改后**:
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    
    // 使用 pattern 匹配所有 localhost 端口
    configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*"));
    
    // 允许所有常用 HTTP 方法
    configuration.setAllowedMethods(Arrays.asList(
        "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
    ));
    
    // 允许所有请求头
    configuration.setAllowedHeaders(Arrays.asList("*"));
    
    // 允许携带凭证（cookies, authorization headers）
    configuration.setAllowCredentials(true);
    
    // 预检请求缓存时间（秒）
    configuration.setMaxAge(3600L);
    
    // 暴露自定义响应头
    configuration.setExposedHeaders(Arrays.asList("Authorization", "X-User-Id"));
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

**优势**:
- ✅ 支持任意 localhost 端口
- ✅ 无需修改代码即可适应端口变化
- ✅ 开发环境更灵活
- ✅ 与 `setAllowCredentials(true)` 兼容

## 技术细节

### setAllowedOrigins vs setAllowedOriginPatterns

| 特性 | setAllowedOrigins | setAllowedOriginPatterns |
|------|-------------------|-------------------------|
| 精确匹配 | ✅ | ✅ |
| 通配符支持 | ❌ | ✅ |
| 与 credentials 兼容 | 有限制 | ✅ |
| 灵活性 | 低 | 高 |
| 安全性 | 高（生产环境） | 中（需谨慎配置） |

### Pattern 语法

```java
// 匹配所有 localhost 端口
"http://localhost:*"

// 匹配特定域名的所有子域
"https://*.example.com"

// 匹配多个 pattern
Arrays.asList(
    "http://localhost:*",
    "https://*.example.com",
    "https://example.com"
)
```

### CORS 预检请求流程

```
┌─────────────────────────────────────────────────────────────┐
│                    浏览器                                    │
│  ┌───────────────────────────────────────────────────────┐  │
│  │ 1. 检测到跨域请求                                      │  │
│  │ 2. 发送 OPTIONS 预检请求                               │  │
│  │    Origin: http://localhost:3001                       │  │
│  │    Access-Control-Request-Method: POST                 │  │
│  │    Access-Control-Request-Headers: Authorization       │  │
│  └───────────────────────────────────────────────────────┘  │
└──────────────────────┬──────────────────────────────────────┘
                       │ OPTIONS Request
                       ▼
┌─────────────────────────────────────────────────────────────┐
│              Spring Security CORS Filter                     │
│  ┌───────────────────────────────────────────────────────┐  │
│  │ 1. 检查 Origin 是否匹配 pattern                        │  │
│  │    "http://localhost:*" matches "http://localhost:3001"│  │
│  │                                                         │  │
│  │ 2. 检查请求方法是否允许                                │  │
│  │    POST in [GET, POST, PUT, DELETE, OPTIONS, PATCH]    │  │
│  │                                                         │  │
│  │ 3. 检查请求头是否允许                                  │  │
│  │    Authorization in ["*"]                              │  │
│  │                                                         │  │
│  │ 4. 返回 CORS 响应头                                    │  │
│  │    Access-Control-Allow-Origin: http://localhost:3001  │  │
│  │    Access-Control-Allow-Methods: GET, POST, ...        │  │
│  │    Access-Control-Allow-Headers: *                     │  │
│  │    Access-Control-Allow-Credentials: true              │  │
│  │    Access-Control-Max-Age: 3600                        │  │
│  └───────────────────────────────────────────────────────┘  │
└──────────────────────┬──────────────────────────────────────┘
                       │ 200 OK (预检通过)
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                    浏览器                                    │
│  ┌───────────────────────────────────────────────────────┐  │
│  │ 3. 预检通过，发送实际请求                              │  │
│  │    POST /api/posts                                     │  │
│  │    Origin: http://localhost:3001                       │  │
│  │    Authorization: Bearer xxx                           │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## 完整配置

### SecurityConfig.java

```java
package com.jiyi.social.config;

import com.jiyi.common.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/posts").permitAll()
                .requestMatchers("/api/posts/**").permitAll()
                .requestMatchers("/api/topics/**").permitAll()
                .requestMatchers("/doc.html", "/webjars/**", "/swagger-resources/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    public OncePerRequestFilter jwtAuthenticationFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                String authHeader = request.getHeader("Authorization");
                
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    try {
                        if (JwtUtil.validateToken(token)) {
                            String userId = JwtUtil.getUserIdFromToken(token);
                            UsernamePasswordAuthenticationToken authentication = 
                                new UsernamePasswordAuthenticationToken(
                                    userId, 
                                    null, 
                                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                                );
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    } catch (Exception e) {
                        // Invalid token, continue without authentication
                    }
                }
                
                filterChain.doFilter(request, response);
            }
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 使用 pattern 匹配所有 localhost 端口
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "X-User-Id"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

## 测试验证

### 1. 测试 CORS 预检请求

```bash
curl -X OPTIONS http://localhost:8083/api/posts \
  -H "Origin: http://localhost:3001" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Authorization" \
  -v
```

**期望响应**:
```
HTTP/1.1 200 OK
Access-Control-Allow-Origin: http://localhost:3001
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS, PATCH
Access-Control-Allow-Headers: *
Access-Control-Allow-Credentials: true
Access-Control-Max-Age: 3600
```

### 2. 测试不同端口

```bash
# 测试 3000 端口
curl -X OPTIONS http://localhost:8083/api/posts \
  -H "Origin: http://localhost:3000" \
  -v

# 测试 3001 端口
curl -X OPTIONS http://localhost:8083/api/posts \
  -H "Origin: http://localhost:3001" \
  -v

# 测试 5173 端口
curl -X OPTIONS http://localhost:8083/api/posts \
  -H "Origin: http://localhost:5173" \
  -v
```

所有端口都应该返回成功的 CORS 响应。

### 3. 浏览器测试

1. 打开浏览器开发者工具（F12）
2. 切换到 Network 标签
3. 访问社区页面并尝试发布动态
4. 查看网络请求：
   - 应该看到一个 OPTIONS 请求（预检）
   - 然后是 POST 请求（实际请求）
   - 两个请求都应该返回 200 状态码

## 生产环境配置

### 安全建议

在生产环境中，不应该使用通配符 pattern，而应该明确指定允许的域名：

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    
    // 生产环境：明确指定允许的域名
    if (isProd()) {
        configuration.setAllowedOrigins(Arrays.asList(
            "https://www.example.com",
            "https://app.example.com"
        ));
    } else {
        // 开发环境：使用 pattern
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*"));
    }
    
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}

private boolean isProd() {
    return "prod".equals(System.getProperty("spring.profiles.active"));
}
```

### 使用配置文件

```yaml
# application.yml
cors:
  allowed-origins:
    - https://www.example.com
    - https://app.example.com
  allowed-methods:
    - GET
    - POST
    - PUT
    - DELETE
    - OPTIONS
  allowed-headers: "*"
  allow-credentials: true
  max-age: 3600
```

```java
@Configuration
public class CorsConfig {
    
    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        // ...
    }
}
```

## 常见问题

### Q1: 为什么使用 pattern 而不是 origins？
**A**: 
- `setAllowedOrigins` 与 `setAllowCredentials(true)` 一起使用时不能用通配符
- `setAllowedOriginPatterns` 支持通配符且与 credentials 兼容
- 开发环境中端口经常变化，pattern 更灵活

### Q2: pattern 安全吗？
**A**: 
- 开发环境：安全，只匹配 localhost
- 生产环境：应该使用精确的域名列表，不要用通配符

### Q3: 为什么还是返回 403？
**A**: 检查以下几点：
1. 服务是否重启（必须重启才能生效）
2. 浏览器是否缓存了旧的 CORS 响应（清除缓存）
3. 是否已登录（检查 localStorage 的 token）
4. Token 是否有效（未过期）

### Q4: OPTIONS 请求成功但 POST 失败
**A**: 
- OPTIONS 是预检请求，只检查 CORS
- POST 失败可能是认证问题，检查 Authorization 头
- 查看后端日志确认具体错误

## 修改文件清单

1. ✅ `backend/social-service/src/main/java/com/jiyi/social/config/SecurityConfig.java`
   - 使用 `setAllowedOriginPatterns` 替代 `setAllowedOrigins`
   - 添加 PATCH 方法支持
   - 优化 CORS 配置

## 重启服务

修改后必须重启 social-service：

```bash
# 1. 停止旧进程
taskkill /F /PID <PID>

# 2. 启动新进程
cd backend/social-service
mvn spring-boot:run
```

## 验证清单

- ✅ social-service 成功启动在 8083 端口
- ✅ CORS 配置使用 `setAllowedOriginPatterns`
- ✅ 支持所有 localhost 端口
- ✅ OPTIONS 预检请求返回 200
- ✅ POST 请求不再返回 "Invalid CORS request"
- ✅ 可以成功发布社区动态

## 总结

"Invalid CORS request" 错误已修复，主要改进：

1. **使用 setAllowedOriginPatterns**:
   - 支持通配符 pattern
   - 与 credentials 兼容
   - 更灵活的开发体验

2. **Pattern 配置**:
   - `http://localhost:*` 匹配所有 localhost 端口
   - 无需手动列举每个端口
   - 端口变化时无需修改代码

3. **完整的 CORS 支持**:
   - 所有 HTTP 方法
   - 所有请求头
   - 凭证支持
   - 自定义响应头暴露

现在社区平台可以在任意 localhost 端口正常工作，用户可以成功发布动态了！
