# 启动 Redis 指南

## 问题

User Service 需要 Redis 来存储刷新令牌（refresh tokens）。

## 解决方案

### 方案 1: 使用 Docker（推荐）

1. **启动 Docker Desktop**
   - 在开始菜单中找到 "Docker Desktop" 并启动
   - 等待 Docker 完全启动（托盘图标变为绿色）

2. **启动 Redis 容器**
   ```bash
   docker run -d --name jiyi-redis -p 6379:6379 redis:latest
   ```

3. **验证 Redis 是否运行**
   ```bash
   docker ps | findstr redis
   ```

### 方案 2: 下载 Redis for Windows

1. **下载 Redis**
   - 访问: https://github.com/tporadowski/redis/releases
   - 下载最新的 `.zip` 文件

2. **解压并运行**
   ```bash
   # 解压到某个目录，例如 C:\Redis
   # 然后运行
   C:\Redis\redis-server.exe
   ```

3. **验证 Redis 是否运行**
   ```bash
   netstat -an | findstr "6379"
   ```

### 方案 3: 使用 WSL2 中的 Redis

如果你有 WSL2：

```bash
wsl
sudo apt-get update
sudo apt-get install redis-server
sudo service redis-server start
```

## 快速启动命令

如果 Docker Desktop 已经在运行：

```bash
# 检查是否已有 Redis 容器
docker ps -a | findstr redis

# 如果容器存在但未运行，启动它
docker start jiyi-redis

# 如果容器不存在，创建并启动
docker run -d --name jiyi-redis -p 6379:6379 redis:latest
```

## 验证 Redis 连接

```bash
# 使用 PowerShell
Test-NetConnection localhost -Port 6379

# 或使用 netstat
netstat -an | findstr "6379"
```

应该看到：
```
TCP    0.0.0.0:6379           0.0.0.0:0              LISTENING
```

## 重启 User Service

Redis 启动后，重启 user-service：

```bash
cd backend/user-service
mvn spring-boot:run
```

## 测试注册功能

Redis 启动后，注册功能应该可以正常工作：

1. 打开 http://localhost:3000
2. 点击"登录"
3. 切换到"注册"标签
4. 填写信息并注册

## 常见问题

### Q: Docker Desktop 启动很慢
**A**: 这是正常的，Docker Desktop 需要启动虚拟机。通常需要 1-2 分钟。

### Q: 端口 6379 被占用
**A**: 
```bash
# 查找占用端口的进程
netstat -ano | findstr "6379"
# 结束该进程或使用不同端口
```

### Q: 不想使用 Redis
**A**: Redis 是必需的，因为它用于存储刷新令牌。没有 Redis，登录功能无法正常工作。

## 当前状态

- ❌ Redis 未运行
- ✅ MySQL 正在运行
- ✅ User Service 已编译
- ⏸️ User Service 等待 Redis 启动

## 下一步

1. 启动 Docker Desktop
2. 运行 `docker run -d --name jiyi-redis -p 6379:6379 redis:latest`
3. 等待 Redis 启动（几秒钟）
4. User Service 应该能够连接到 Redis
5. 测试注册功能
