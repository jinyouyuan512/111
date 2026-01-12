# 注册功能测试指南

## 问题诊断步骤

### 1. 检查后端服务是否运行

```bash
# 在backend目录下运行
cd backend
mvn spring-boot:run -pl user-service
```

确保看到类似以下输出：
```
Started UserServiceApplication in X.XXX seconds
```

### 2. 检查数据库连接

确保MySQL已启动，并且数据库 `jiyi_user` 已创建。

```sql
-- 在MySQL中执行
SHOW DATABASES LIKE 'jiyi_user';
USE jiyi_user;
SHOW TABLES;
```

### 3. 使用curl测试注册API

```bash
# 测试注册接口
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser123",
    "email": "test123@example.com",
    "password": "password123",
    "nickname": "测试用户",
    "phone": "13800138000"
  }'
```

预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "testuser123",
    "email": "test123@example.com",
    ...
  }
}
```

### 4. 检查前端控制台

打开浏览器开发者工具（F12），查看：

1. **Console标签**：查看是否有JavaScript错误
2. **Network标签**：
   - 找到 `/api/auth/register` 请求
   - 查看请求的 Request Payload
   - 查看响应的 Response
   - 查看状态码（Status Code）

### 5. 常见问题和解决方案

#### 问题1：404 Not Found
**原因**：后端服务未启动或端口不对
**解决**：
- 确保user-service正在运行
- 检查vite.config.ts中的代理配置是否指向正确的端口（默认8080）

#### 问题2：CORS错误
**原因**：跨域配置问题
**解决**：检查后端是否配置了CORS

#### 问题3：409 Conflict - 用户名已存在
**原因**：用户名或邮箱已被注册
**解决**：使用不同的用户名和邮箱

#### 问题4：500 Internal Server Error
**原因**：后端服务错误
**解决**：
- 查看后端控制台的错误日志
- 检查数据库连接是否正常
- 确保Redis服务已启动（用于存储刷新令牌）

#### 问题5：Network Error
**原因**：无法连接到后端
**解决**：
- 确保后端服务在运行
- 检查防火墙设置
- 确认端口8080未被占用

### 6. 启动Redis（如果需要）

注册功能本身不需要Redis，但登录功能需要Redis来存储刷新令牌。

```bash
# Windows (使用WSL或下载Windows版本)
redis-server

# Linux/Mac
redis-server
```

### 7. 查看后端日志

在运行user-service的终端中查看日志输出，特别注意：
- SQL执行日志
- 异常堆栈信息
- 业务逻辑错误

### 8. 测试数据

可以使用以下测试数据进行注册：

```json
{
  "username": "zhangsan",
  "email": "zhangsan@example.com",
  "password": "123456",
  "nickname": "张三",
  "phone": "13912345678"
}
```

### 9. 直接在浏览器测试

访问 Swagger UI 进行测试：
```
http://localhost:8080/doc.html
```

找到"认证管理"分组，测试注册接口。

## 快速修复建议

### 修复1：确保后端正确返回数据

检查 `AuthController.java` 是否正确返回 `Result` 对象：

```java
@PostMapping("/register")
public Result<User> register(@RequestBody RegisterRequest request) {
    User user = authService.register(request);
    return Result.success(user);  // 确保返回格式正确
}
```

### 修复2：检查前端API baseURL

确保 `frontend/src/api/request.ts` 中的 baseURL 正确：

```typescript
const service: AxiosInstance = axios.create({
  baseURL: '/api',  // 通过代理转发到后端
  timeout: 10000
})
```

### 修复3：添加全局异常处理

如果后端抛出异常但没有被捕获，可能导致500错误。

## 完整测试流程

1. **启动MySQL**
2. **启动Redis**（可选，登录时需要）
3. **启动后端服务**
   ```bash
   cd backend
   mvn spring-boot:run -pl user-service
   ```
4. **启动前端服务**
   ```bash
   cd frontend
   npm run dev
   ```
5. **访问登录页面**
   ```
   http://localhost:3000/login
   ```
6. **填写注册表单并提交**
7. **查看浏览器控制台和网络请求**

## 获取详细错误信息

现在前端已经添加了详细的错误日志，注册时请：

1. 打开浏览器开发者工具（F12）
2. 切换到 Console 标签
3. 尝试注册
4. 查看控制台输出的详细错误信息：
   - `发送注册请求:` - 查看发送的数据
   - `注册响应:` - 查看服务器响应
   - `注册失败详情:` - 查看错误详情

将这些信息提供给我，我可以帮你进一步诊断问题。
