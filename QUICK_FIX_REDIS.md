# Redis连接问题快速修复

## 问题
登录时出现错误：`Unable to connect to Redis`

## 解决方案

### 方案1：下载并启动Redis（推荐）

#### Windows下载Redis
1. 下载Redis for Windows:
   - GitHub: https://github.com/tporadowski/redis/releases
   - 下载 `Redis-x64-5.0.14.1.zip`

2. 解压到任意目录，例如 `C:\Redis`

3. 启动Redis:
   ```cmd
   cd C:\Redis
   redis-server.exe
   ```

4. 或者作为Windows服务安装:
   ```cmd
   cd C:\Redis
   redis-server.exe --service-install
   redis-server.exe --service-start
   ```

### 方案2：使用Docker启动Redis（如果Docker可用）

```bash
docker run -d --name jiyi-redis -p 6379:6379 redis:latest
```

### 方案3：临时禁用Redis（不推荐，会影响功能）

修改 `backend/user-service/src/main/resources/application.yml`:

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
      # 添加以下配置
      timeout: 1000ms
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
  # 添加自动配置排除
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
      - org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration
```

## 当前状态

- ❌ Redis未安装/未启动
- ✅ MySQL正在运行
- ✅ User Service正在运行（但登录失败）
- ✅ Mall Service正在运行
- ✅ Social Service正在运行
- ✅ Frontend正在运行

## 推荐操作

1. **立即下载Redis**（5分钟）
   - 访问: https://github.com/tporadowski/redis/releases
   - 下载并解压
   - 运行 `redis-server.exe`

2. **重启User Service**
   - 停止当前的User Service进程
   - 重新启动

3. **测试登录**
   - 访问 http://localhost:3001/login
   - 使用测试账号登录

## Redis的作用

在本项目中，Redis用于：
1. **Session管理** - 存储用户登录状态
2. **Token缓存** - 缓存JWT token
3. **验证码存储** - 存储图形验证码
4. **限流控制** - API访问频率限制

## 临时解决方案（无需Redis）

如果暂时无法安装Redis，可以修改代码使用内存存储：

### 修改 AuthServiceImpl.java

找到Redis相关代码，注释掉或改为内存Map存储。

但这样会导致：
- 登录状态在服务重启后丢失
- 多实例部署时session不共享
- 无法实现分布式锁

## 验证Redis是否运行

```bash
# 检查Redis端口
netstat -ano | findstr "6379"

# 或使用redis-cli测试
redis-cli ping
# 应该返回: PONG
```

## 下一步

1. 下载并启动Redis
2. 重启User Service
3. 测试登录功能
4. 如果仍有问题，查看User Service日志
