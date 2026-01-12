# 用户服务 (User Service)

## 功能概述

用户服务提供完整的用户认证和授权功能，包括：

- 用户注册、登录、登出
- JWT令牌生成和验证
- 访问令牌和刷新令牌机制
- 密码加密（bcrypt）
- 基于角色的权限管理（RBAC）
- 用户信息管理

## 技术栈

- Spring Boot 3.2.0
- Spring Security
- JWT (JSON Web Token)
- MyBatis Plus
- MySQL 8.0+
- Redis 7.0+
- BCrypt密码加密

## 数据库设置

1. 确保MySQL服务运行
2. 执行数据库初始化脚本：

```bash
mysql -u root -p < src/main/resources/db/schema.sql
```

或者手动创建数据库和表（参考 `src/main/resources/db/schema.sql`）

## Redis设置

确保Redis服务运行在 `localhost:6379`

## 配置说明

配置文件位于 `src/main/resources/application.yml`

主要配置项：
- 数据库连接信息
- Redis连接信息
- JWT密钥（在JwtUtil中配置）
- 令牌过期时间

## API端点

### 公开端点（无需认证）

- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/refresh` - 刷新令牌

### 受保护端点（需要认证）

- `POST /api/auth/logout` - 用户登出
- `GET /api/users/me` - 获取当前用户信息
- `GET /api/users/{userId}` - 获取指定用户信息
- `PUT /api/users/{userId}` - 更新用户信息

## 认证流程

1. **注册**: 用户提交注册信息，密码使用bcrypt加密存储
2. **登录**: 验证用户名和密码，返回访问令牌和刷新令牌
3. **访问资源**: 在请求头中携带访问令牌 `Authorization: Bearer <token>`
4. **刷新令牌**: 访问令牌过期后，使用刷新令牌获取新的访问令牌
5. **登出**: 删除服务器端存储的刷新令牌

## 令牌说明

- **访问令牌 (Access Token)**: 有效期2小时，用于API访问
- **刷新令牌 (Refresh Token)**: 有效期7天，用于获取新的访问令牌

## 角色权限

系统支持三种角色：
- `user`: 普通用户
- `designer`: 设计师
- `admin`: 管理员

## 运行服务

```bash
cd backend/user-service
mvn spring-boot:run
```

服务将在 `http://localhost:8081` 启动

## API文档

启动服务后访问：
- Knife4j文档: http://localhost:8081/doc.html
- Swagger UI: http://localhost:8081/swagger-ui.html

## 测试账号

数据库初始化脚本包含以下测试账号：

- 管理员: `admin` / `password`
- 测试用户: `testuser` / `password`

## 安全特性

- 密码使用BCrypt加密，强度为10
- JWT令牌使用HS256算法签名
- 刷新令牌存储在Redis中，支持撤销
- 支持基于角色的访问控制
- 防止SQL注入（MyBatis Plus参数化查询）
- 会话管理采用无状态设计
