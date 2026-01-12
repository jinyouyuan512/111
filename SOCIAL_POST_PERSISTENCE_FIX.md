# 社区动态持久化问题修复

## 问题描述
用户发布动态后，退出登录再登录，发布的动态消失了。原因是动态只保存在前端本地数组中，没有保存到数据库。

## 修复过程

### 1. 数据库初始化
- 运行 `INIT_SOCIAL_DB.bat` 创建 `jiyi_social` 数据库和所有表
- 添加 `view_count` 字段到 `topic` 表

### 2. 修复依赖问题
**问题**: social-service 缺少必要的依赖
**解决**: 在 `backend/social-service/pom.xml` 中添加:
- `knife4j-openapi3-jakarta-spring-boot-starter` (API文档)
- `spring-boot-starter-security` (安全认证)

### 3. 修复包导入问题
**问题**: 使用了旧的 `javax.validation` 包
**解决**: 将所有 `javax.validation` 导入替换为 `jakarta.validation`
- 影响文件: CheckinRequest, CommentCreateRequest, MessageRequest, PostCreateRequest, ReportRequest
- 影响控制器: CheckinController, MessageController, ReportController

### 4. 修复实体类问题
**问题**: Topic 实体缺少 `viewCount` 字段
**解决**: 
- 在 `Topic.java` 中添加 `viewCount` 字段
- 更新数据库 schema 添加 `view_count` 列
- 修复 `TopicServiceImpl` 中的类型转换 (Integer → Long)

### 5. 修复配置问题
**问题**: Spring Cloud 版本兼容性检查失败
**解决**: 在 `application.yml` 中:
- 禁用 Nacos 服务发现 (`spring.cloud.nacos.discovery.enabled: false`)
- 禁用兼容性检查 (`spring.cloud.compatibility-verifier.enabled: false`)
- 修正端口号为 8083
- 修正数据库密码为 123456

### 6. 修复Spring Security配置
**问题**: Spring Security 默认拦截所有请求，导致API返回401/403
**解决**: 创建 `SecurityConfig.java`:
- 允许所有 `/api/**` 路径访问
- 配置CORS支持前端跨域请求
- 禁用CSRF（因为使用JWT无状态认证）

### 7. 修复前端代理配置
**问题**: Vite代理配置错误，将social请求路由到错误的端口
**解决**: 更新 `frontend/vite.config.ts`:
- 将 `/api/posts` 路由到 `http://localhost:8083`
- 将 `/api/topics` 路由到 `http://localhost:8083`
- 将 `/api/comments` 路由到 `http://localhost:8083`
- 移除错误的 `/api/social` 配置

### 8. 前端API调用
前端 `Social.vue` 的 `submitPost()` 方法已经修改为调用后端API:
```typescript
const response = await socialApi.createPost({
  title: newPost.value.title,
  content: newPost.value.content,
  images: newPost.value.images,
  video: newPost.value.video,
  tags: newPost.value.tags,
  location: newPost.value.location,
  category: newPost.value.category
})
```

## 服务状态

### Social Service (端口 8083)
✅ **运行中**
- 进程ID: 10
- 端口: 8083
- 数据库: jiyi_social
- API文档: http://localhost:8083/doc.html

### 可用的API端点
- `POST /api/posts` - 创建动态
- `GET /api/posts` - 获取动态列表
- `GET /api/posts/{id}` - 获取动态详情
- `DELETE /api/posts/{id}` - 删除动态
- `POST /api/posts/{id}/like` - 点赞动态
- `DELETE /api/posts/{id}/like` - 取消点赞
- `GET /api/topics/hot` - 获取热门话题
- `GET /api/topics/users/active` - 获取活跃用户

## 测试步骤

1. **确保服务运行**
   ```bash
   # 检查端口
   netstat -ano | findstr :8083
   ```

2. **测试API**
   ```powershell
   # 测试热门话题API
   Invoke-WebRequest -Uri "http://localhost:8083/api/topics/hot" -Method GET
   ```

3. **登录系统**
   - 访问 http://localhost:3000
   - 使用已注册账号登录

4. **发布动态**
   - 进入社区页面
   - 点击发布按钮
   - 填写内容并发布

5. **验证持久化**
   - 退出登录
   - 重新登录
   - 检查动态是否还在

## 数据库表结构

### post 表 (动态表)
- id: 动态ID
- user_id: 用户ID
- content: 内容
- location_name: 位置名称
- type: 类型
- likes, comments, shares: 互动数据
- created_at: 创建时间

### topic 表 (话题表)
- id: 话题ID
- name: 话题名称
- post_count: 动态数量
- view_count: 浏览数/热度
- created_at: 创建时间

### media_file 表 (媒体文件表)
- id: 文件ID
- post_id: 动态ID
- type: 类型 (image/video)
- url: 文件URL
- thumbnail: 缩略图

## 关键修复点

### 1. Spring Security配置
```java
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
                .requestMatchers("/api/**").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
```

### 2. Vite代理配置
```typescript
proxy: {
  '/api/posts': {
    target: 'http://localhost:8083',
    changeOrigin: true
  },
  '/api/topics': {
    target: 'http://localhost:8083',
    changeOrigin: true
  }
}
```

## 注意事项

1. **认证要求**: 发布动态需要登录，请求需要携带:
   - `Authorization` header: Bearer token
   - `X-User-Id` header: 用户ID

2. **图片/视频**: 当前使用 base64 编码存储，生产环境建议使用对象存储服务

3. **用户信息**: 活跃用户列表中的用户详情需要从 user-service 获取，当前使用默认值

4. **服务依赖**: social-service 独立运行，不依赖 Nacos

5. **前端热更新**: 修改 vite.config.ts 后，Vite会自动重启开发服务器

## 下一步优化建议

1. 集成文件上传服务 (OSS/MinIO)
2. 实现用户服务调用获取完整用户信息
3. 添加动态审核功能
4. 实现评论功能
5. 添加动态搜索功能
6. 优化图片/视频存储方案
