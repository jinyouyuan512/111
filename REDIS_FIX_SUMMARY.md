# Redis 连接问题修复总结

## 问题
注册时报错：`数据库访问错误: Unable to connect to Redis`

## 原因
Redis Docker 容器已停止运行

## 解决方案

### 1. 启动 Redis 容器
```bash
docker start jiyi-redis
```

### 2. 验证 Redis 运行状态
```bash
# 查看容器状态
docker ps --filter "name=jiyi-redis"

# 测试连接
docker exec jiyi-redis redis-cli ping
# 应该返回: PONG
```

### 3. 重启相关服务
由于 User Service 在启动时无法连接 Redis，需要重启：
```bash
# 停止旧进程
taskkill /F /PID <PID>

# 重新启动
cd backend/user-service
mvn spring-boot:run
```

## 当前服务状态

### ✅ Redis
- 容器名: `jiyi-redis`
- 端口: `6379`
- 状态: 运行中
- 测试: `docker exec jiyi-redis redis-cli ping` → PONG

### ✅ User Service
- 端口: `8081`
- 状态: 运行中
- Redis 连接: 正常

### ✅ Mall Service
- 端口: `8085`
- 状态: 运行中
- API 文档: http://localhost:8085/doc.html

### ✅ Frontend
- 端口: `3001` (3000 被占用，自动切换)
- 状态: 运行中
- 访问: http://localhost:3001/

## 快速启动命令

### 启动 Redis
```bash
docker start jiyi-redis
```

### 启动所有后端服务
```powershell
# User Service
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd backend/user-service; mvn spring-boot:run"

# Mall Service
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd backend/mall-service; mvn spring-boot:run"
```

### 启动前端
```powershell
cd frontend
npm run dev
```

## 注册功能验证

现在可以正常注册和登录：

1. 访问 http://localhost:3001/
2. 点击"登录"按钮
3. 切换到"注册"标签
4. 填写注册信息
5. 提交注册

Redis 现在正常运行，注册功能应该可以正常工作。

## 常见问题

### Q: Redis 容器为什么会停止？
A: Docker 容器在系统重启或 Docker Desktop 重启后会停止，需要手动启动。

### Q: 如何设置 Redis 自动启动？
A: 创建容器时使用 `--restart=always` 参数：
```bash
docker run -d --name jiyi-redis -p 6379:6379 --restart=always redis:7-alpine
```

### Q: 如何查看 Redis 中的数据？
A: 使用 Redis CLI：
```bash
docker exec -it jiyi-redis redis-cli
> keys *
> get <key>
```

## 测试账号
- 用户名: `ruler`
- 角色: `admin`
- 密码: (你的注册密码)
