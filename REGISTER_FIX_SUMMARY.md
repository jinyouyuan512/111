# 注册功能修复总结

## 问题原因

**500错误的根本原因**：前端代理配置问题

- user-service运行在端口 **8081**
- 网关运行在端口 **8080**
- 前端代理所有`/api`请求到8080（网关）
- 但网关配置中user-service的路径是`/api/user/**`
- 而AuthController的实际路径是`/api/auth/**`
- 导致请求无法正确路由到user-service

## 已完成的修复

### 1. 添加了全局异常处理器
文件：`backend/user-service/src/main/java/com/jiyi/user/exception/GlobalExceptionHandler.java`
- 捕获所有异常并返回详细错误信息
- 帮助诊断问题

### 2. 更新了Result类
文件：`backend/common/src/main/java/com/jiyi/common/result/Result.java`
- 添加了带data参数的error方法

### 3. 修复了前端代理配置
文件：`frontend/vite.config.ts`
- 将`/api/auth`和`/api/users`请求直接代理到8081端口
- 其他`/api`请求继续代理到8080（网关）

### 4. 修复了网关配置
文件：`backend/gateway/src/main/resources/application.yml`
- 添加了`/api/auth/**`路由规则
- 修改了user-service路由为`/api/users/**`

### 5. 创建了启动脚本
文件：`START_USER_SERVICE.bat`
- 一键编译和启动user-service

## 现在如何使用

### 方式1：直接启动user-service（推荐，简单快速）

```bash
# Windows
START_USER_SERVICE.bat

# 或手动执行
cd backend
mvn clean install -DskipTests
mvn spring-boot:run -pl user-service
```

然后启动前端：
```bash
cd frontend
npm run dev
```

访问：http://localhost:3000/login

### 方式2：通过网关（完整微服务架构）

1. 启动Nacos（如果有）
2. 启动user-service
3. 启动gateway
4. 启动前端

## 测试步骤

### 1. 确保MySQL正在运行

### 2. 启动user-service
```bash
cd backend
mvn spring-boot:run -pl user-service
```

等待看到：
```
Started UserServiceApplication in X.XXX seconds
```

### 3. 测试后端API
打开浏览器访问：
```
http://localhost:8081/doc.html
```

或使用curl：
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser999",
    "email": "test999@example.com",
    "password": "password123"
  }'
```

### 4. 启动前端
```bash
cd frontend
npm run dev
```

### 5. 访问登录页面
```
http://localhost:3000/login
```

### 6. 注册新用户
填写表单并提交，应该看到"注册成功，请登录"

## 验证成功

### 后端日志应该显示：
```
Hibernate: INSERT INTO user (...) VALUES (...)
```

### 前端控制台应该显示：
```
发送注册请求: {username: "...", email: "...", ...}
注册响应: {code: 200, message: "success", data: {...}}
```

### 数据库中应该有新记录：
```sql
SELECT * FROM jiyi_user.user ORDER BY id DESC LIMIT 1;
```

## 常见问题

### Q1: 仍然500错误
**A**: 检查后端控制台的详细错误日志，现在会显示完整的堆栈信息

### Q2: 数据库连接失败
**A**: 检查MySQL是否运行，密码是否正确（application.yml中配置）

### Q3: Redis连接失败
**A**: 注册功能不需要Redis，只有登录时才需要。可以暂时忽略Redis错误

### Q4: 端口被占用
**A**: 
- 检查8081端口：`netstat -ano | findstr :8081`
- 修改application.yml中的端口号

## 端口说明

- **3000**: 前端开发服务器
- **8080**: API网关（可选）
- **8081**: user-service
- **8082**: academy-service
- **8083**: tourism-service
- **8084**: guide-service
- **8085**: mall-service
- **8086**: creative-service
- **8087**: social-service
- **3306**: MySQL
- **6379**: Redis
- **8848**: Nacos（可选）

## 下一步

注册成功后，可以：
1. 使用注册的账号登录
2. 查看用户信息
3. 测试其他功能模块

## 需要帮助？

如果问题仍然存在，请提供：
1. 后端控制台的完整错误日志
2. 浏览器控制台的错误信息
3. Network标签中的请求详情
