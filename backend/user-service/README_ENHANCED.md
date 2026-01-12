# 用户服务 - 功能增强版

## 概述

用户服务提供完整的用户管理、认证、关注系统等功能，是整个红色旅游平台的核心服务之一。

---

## 快速开始

### 1. 启动服务

```bash
cd backend/user-service
mvn spring-boot:run
```

### 2. 访问API文档

服务启动后访问: `http://localhost:8081/swagger-ui.html`

---

## 核心功能

### 用户管理
- ✅ 用户注册/登录
- ✅ 用户信息查询和更新
- ✅ 密码修改
- ✅ 头像上传
- ✅ 用户统计信息

### 社交功能
- ✅ 关注/取消关注用户
- ✅ 查看关注列表和粉丝列表
- ✅ 关注状态检查
- ✅ 关注数统计

### 数据集成
- ⏳ 学习进度查询（待集成academy-service）
- ⏳ 收藏路线查询（待集成tourism-service）
- ⏳ 用户动态查询（待集成social-service）

---

## API接口列表

### 认证相关
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /api/auth/register | 用户注册 | ❌ |
| POST | /api/auth/login | 用户登录 | ❌ |
| POST | /api/auth/refresh | 刷新令牌 | ❌ |
| POST | /api/auth/logout | 用户登出 | ✅ |

### 用户信息
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /api/users/me | 获取当前用户 | ✅ |
| GET | /api/users/{userId} | 获取用户信息 | ✅ |
| PUT | /api/users/{userId} | 更新用户信息 | ✅ |
| GET | /api/users/{userId}/statistics | 用户统计 | ✅ |

### 账户安全
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /api/users/change-password | 修改密码 | ✅ |
| POST | /api/users/avatar | 上传头像 | ✅ |

### 用户数据
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /api/users/{userId}/learning-progress | 学习进度 | ✅ |
| GET | /api/users/{userId}/saved-routes | 收藏路线 | ✅ |
| GET | /api/users/{userId}/posts | 用户动态 | ❌ |

### 关注系统
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /api/users/{userId}/follow | 关注用户 | ✅ |
| DELETE | /api/users/{userId}/follow | 取消关注 | ✅ |
| GET | /api/users/{userId}/is-following | 检查关注 | ✅ |
| GET | /api/users/{userId}/following | 关注列表 | ❌ |
| GET | /api/users/{userId}/followers | 粉丝列表 | ❌ |
| GET | /api/users/{userId}/following-count | 关注数 | ❌ |
| GET | /api/users/{userId}/followers-count | 粉丝数 | ❌ |

---

## 使用示例

### 1. 用户注册

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "nickname": "测试用户"
  }'
```

### 2. 用户登录

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

响应示例:
```json
{
  "code": 200,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 3600,
    "userInfo": {
      "id": 1,
      "username": "testuser",
      "nickname": "测试用户",
      "email": "test@example.com"
    }
  }
}
```

### 3. 获取用户统计

```bash
curl -X GET http://localhost:8081/api/users/1/statistics \
  -H "Authorization: Bearer {accessToken}"
```

### 4. 关注用户

```bash
curl -X POST http://localhost:8081/api/users/2/follow \
  -H "Authorization: Bearer {accessToken}"
```

### 5. 修改密码

```bash
curl -X POST http://localhost:8081/api/users/change-password \
  -H "Authorization: Bearer {accessToken}" \
  -H "Content-Type: application/json" \
  -d '{
    "oldPassword": "password123",
    "newPassword": "newpassword456"
  }'
```

### 6. 上传头像

```bash
curl -X POST http://localhost:8081/api/users/avatar \
  -H "Authorization: Bearer {accessToken}" \
  -F "file=@/path/to/avatar.jpg"
```

---

## 数据库表结构

### user 表
```sql
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    avatar VARCHAR(255),
    role VARCHAR(20) DEFAULT 'USER',
    nickname VARCHAR(50),
    phone VARCHAR(20),
    gender VARCHAR(10),
    birthdate TIMESTAMP,
    interests TEXT,
    level INT DEFAULT 1,
    points INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP,
    deleted INT DEFAULT 0
);
```

### user_follow 表
```sql
CREATE TABLE user_follow (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    follower_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_follower_following (follower_id, following_id),
    KEY idx_follower (follower_id),
    KEY idx_following (following_id)
);
```

---

## 配置说明

### application.yml

```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/jiyi_user?useSSL=false&serverTimezone=UTC
    username: root
    password: your_password
  
jwt:
  secret: your-secret-key
  expiration: 3600000  # 1小时
  refresh-expiration: 604800000  # 7天
```

---

## 安全说明

### JWT认证
- 所有需要认证的接口必须在Header中携带JWT Token
- Token格式: `Authorization: Bearer {token}`
- Token过期后需要使用refresh token刷新

### 权限控制
- 用户只能修改自己的信息
- 管理员可以查看所有用户信息
- 使用Spring Security进行权限控制

### 密码安全
- 使用BCrypt加密存储密码
- 密码强度要求: 至少6位
- 支持密码修改功能

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未认证 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

---

## 开发计划

### 已完成 ✅
- [x] 用户注册登录
- [x] 用户信息管理
- [x] 密码修改
- [x] 用户统计
- [x] 关注系统
- [x] 头像上传接口

### 进行中 ⏳
- [ ] 文件上传实现
- [ ] 跨服务数据集成
- [ ] 消息通知系统

### 计划中 📋
- [ ] 用户等级系统
- [ ] 成就勋章系统
- [ ] 用户推荐算法
- [ ] 行为分析统计

---

## 技术支持

如有问题，请联系开发团队或查看项目文档。
