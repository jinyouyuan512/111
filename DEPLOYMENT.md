# 冀忆红途 - 部署文档

## 项目概述

冀忆红途是一个河北红色文化数字平台，采用微服务架构，包含前端Vue3应用和多个Spring Boot后端服务。

## 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                      前端 (Vue3 + Vite)                      │
│                      http://localhost:3001                   │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      Nginx 反向代理                          │
│                      /api/* → 后端服务                       │
└─────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        ▼                     ▼                     ▼
┌───────────────┐   ┌───────────────┐   ┌───────────────┐
│ user-service  │   │ social-service│   │ mall-service  │
│    :8080      │   │    :8081      │   │    :8083      │
└───────────────┘   └───────────────┘   └───────────────┘
        │                     │                     │
        ▼                     ▼                     ▼
┌───────────────┐   ┌───────────────┐   ┌───────────────┐
│creative-service│  │tourism-service│   │academy-service│
│    :8084      │   │    :8082      │   │    :8085      │
└───────────────┘   └───────────────┘   └───────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    MySQL 8.0 + Redis                         │
│                    localhost:3306 / :6379                    │
└─────────────────────────────────────────────────────────────┘
```

## 环境要求

### 必需软件

| 软件 | 版本 | 说明 |
|------|------|------|
| JDK | 17+ | 后端运行环境 |
| Node.js | 18+ | 前端构建环境 |
| MySQL | 8.0+ | 数据库 |
| Redis | 6.0+ | 缓存/会话存储 |
| Maven | 3.8+ | 后端构建工具 |

### 可选软件

| 软件 | 版本 | 说明 |
|------|------|------|
| n8n | latest | AI工作流（智能行程规划等） |
| Nginx | 1.20+ | 生产环境反向代理 |
| Docker | 20+ | 容器化部署 |

## 快速部署

### 1. 克隆项目

```bash
git clone git@github.com:jinyouyuan512/111.git
cd 111
```

### 2. 数据库初始化

```bash
# 方式一：导入所有数据库
mysql -u root -p < sql/all_databases.sql

# 方式二：分别导入
mysql -u root -p jiyi_user < sql/jiyi_user.sql
mysql -u root -p jiyi_social < sql/jiyi_social.sql
mysql -u root -p jiyi_mall < sql/jiyi_mall.sql
mysql -u root -p jiyi_creative < sql/jiyi_creative.sql
mysql -u root -p jiyi_tourism < sql/jiyi_tourism.sql
mysql -u root -p jiyi_academy < sql/jiyi_academy.sql
```

### 3. 启动 Redis

```bash
# Windows
redis-server

# Linux/Mac
sudo systemctl start redis
```

### 4. 启动后端服务

```bash
# 进入后端目录
cd backend

# 启动各服务（每个服务在单独终端）
cd user-service && mvn spring-boot:run
cd social-service && mvn spring-boot:run
cd mall-service && mvn spring-boot:run
cd creative-service && mvn spring-boot:run
cd tourism-service && mvn spring-boot:run
cd academy-service && mvn spring-boot:run
```

或使用一键启动脚本（Windows）：
```powershell
.\START_ALL_SERVICES.ps1
```

### 5. 启动前端

```bash
cd frontend
npm install
npm run dev
```

### 6. 访问应用

- 前端页面: http://localhost:3001
- 后台管理: http://localhost:3001/admin

## 服务端口配置

| 服务 | 端口 | 数据库 | 说明 |
|------|------|--------|------|
| user-service | 8080 | jiyi_user | 用户认证、注册登录 |
| social-service | 8081 | jiyi_social | 社交平台、帖子评论 |
| tourism-service | 8082 | jiyi_tourism | 智慧旅游、行程规划 |
| mall-service | 8083 | jiyi_mall | 红色商城、订单管理 |
| creative-service | 8084 | jiyi_creative | 众创空间、作品上传 |
| academy-service | 8085 | jiyi_academy | 红色学院、课程新闻 |
| frontend | 3001 | - | Vue3前端应用 |

## 数据库配置

默认配置（可在各服务的 `application.yml` 中修改）：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/jiyi_xxx?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
  redis:
    host: localhost
    port: 6379
```

## 生产环境部署

### Nginx 配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 前端静态文件
    location / {
        root /var/www/jiyi-frontend/dist;
        try_files $uri $uri/ /index.html;
    }

    # API 代理
    location /api/user/ {
        proxy_pass http://127.0.0.1:8080/api/;
    }
    
    location /api/social/ {
        proxy_pass http://127.0.0.1:8081/api/;
    }
    
    location /api/tourism/ {
        proxy_pass http://127.0.0.1:8082/api/;
    }
    
    location /api/mall/ {
        proxy_pass http://127.0.0.1:8083/api/;
    }
    
    location /api/creative/ {
        proxy_pass http://127.0.0.1:8084/api/;
    }
    
    location /api/academy/ {
        proxy_pass http://127.0.0.1:8085/api/;
    }

    # 上传文件访问
    location /uploads/ {
        alias /var/www/uploads/;
    }
}
```

### 前端构建

```bash
cd frontend
npm run build
# 产物在 dist/ 目录
```

### 后端打包

```bash
cd backend/user-service
mvn clean package -DskipTests
# JAR 文件在 target/ 目录

# 运行
java -jar target/user-service-1.0.0.jar
```

## Docker 部署（可选）

项目根目录已包含 `docker-compose.yml`：

```bash
docker-compose up -d
```

## n8n 工作流配置

项目使用 n8n 实现 AI 功能：

1. 安装 n8n: `npm install -g n8n`
2. 启动: `n8n start`
3. 访问: http://localhost:5678
4. 导入工作流: `n8n-workflows/` 目录下的 JSON 文件

### 工作流列表

| 文件 | 功能 |
|------|------|
| tourism-ai-plan.json | AI智能行程规划 |
| tourism-audio-guide.json | 景点语音讲解 |
| academy-news-simple.json | 学院新闻生成 |

## 常见问题

### Q: 端口被占用
```bash
# Windows 查看端口占用
netstat -ano | findstr :8080

# 杀死进程
taskkill /PID <进程ID> /F
```

### Q: MySQL 连接失败
1. 确认 MySQL 服务已启动
2. 检查用户名密码是否正确
3. 确认数据库已创建

### Q: Redis 连接失败
1. 确认 Redis 服务已启动
2. Windows 可使用便携版 Redis

### Q: 前端 API 请求 404
1. 检查后端服务是否启动
2. 检查 `vite.config.ts` 中的代理配置

## 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 普通用户 | test | 123456 |
| 管理员 | admin | admin123 |

## 技术栈

### 前端
- Vue 3 + TypeScript
- Vite 5
- Element Plus
- Pinia
- Vue Router

### 后端
- Spring Boot 3.1
- MyBatis Plus
- Spring Security
- JWT 认证
- MySQL 8.0
- Redis

## 联系方式

如有问题，请提交 Issue 或联系开发团队。
