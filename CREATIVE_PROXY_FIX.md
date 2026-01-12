# 创意服务代理配置修复

## 🐛 问题描述

访问创意上传页面时无法连接到后端服务。

**错误原因：**
- 前端 Vite 配置中缺少 `/api/creative` 的代理配置
- 请求被默认代理到 8080 端口，而不是创意服务的 8087 端口

## ✅ 修复方案

### 1. 更新 Vite 配置

在 `frontend/vite.config.ts` 中添加创意服务的代理配置：

```typescript
server: {
  port: 3001,
  proxy: {
    '/api/auth': {
      target: 'http://localhost:8081',
      changeOrigin: true
    },
    '/api/users': {
      target: 'http://localhost:8081',
      changeOrigin: true
    },
    '/api/mall': {
      target: 'http://localhost:8085',
      changeOrigin: true
    },
    '/api/creative': {              // ✅ 新增
      target: 'http://localhost:8087',
      changeOrigin: true
    },
    '/api/posts': {
      target: 'http://localhost:8083',
      changeOrigin: true
    },
    // ... 其他配置
  }
}
```

### 2. 重启前端服务

修改 Vite 配置后需要重启前端开发服务器：

**方法一：手动重启**
```bash
# 停止当前运行的前端服务（Ctrl+C）
# 然后重新启动
cd frontend
npm run dev
```

**方法二：使用脚本**
```bash
# 在项目根目录执行
.\RESTART_FRONTEND.bat
```

## 📋 服务端口映射

| 服务 | 端口 | 代理路径 |
|------|------|----------|
| 前端开发服务器 | 3001 | - |
| 用户服务 | 8081 | /api/auth, /api/users |
| 社交服务 | 8083 | /api/posts, /api/topics, /api/comments, /api/upload |
| 商城服务 | 8085 | /api/mall |
| **创意服务** | **8087** | **/api/creative** |
| 网关服务 | 8080 | /api (默认) |

## 🧪 验证步骤

### 1. 确认服务运行状态

```bash
# 检查创意服务（端口 8087）
netstat -ano | findstr :8087

# 检查前端服务（端口 3001）
netstat -ano | findstr :3001
```

### 2. 测试 API 访问

**通过前端代理访问：**
```bash
curl http://localhost:3001/api/creative/space
```

**直接访问后端：**
```bash
curl http://localhost:8087/api/creative/space
```

两者应该返回相同的结果。

### 3. 浏览器测试

1. 访问 `http://localhost:3001/creative/upload`
2. 打开浏览器开发者工具（F12）
3. 查看 Network 标签
4. 提交作品时检查请求：
   - 请求 URL：`http://localhost:3001/api/creative/designs`
   - 状态码：应该是 200（成功）或其他有意义的状态码
   - 响应：应该有正常的 JSON 数据

## 🔧 完整的重启流程

### 1. 停止所有服务

```bash
# 停止前端（在前端终端按 Ctrl+C）
# 停止创意服务（在创意服务终端按 Ctrl+C）
```

### 2. 启动创意服务

```bash
cd backend/creative-service
mvn spring-boot:run
```

等待看到：
```
Started CreativeServiceApplication in X.XXX seconds
```

### 3. 启动前端服务

```bash
cd frontend
npm run dev
```

等待看到：
```
VITE v5.x.x  ready in XXX ms

➜  Local:   http://localhost:3001/
➜  Network: use --host to expose
```

### 4. 测试访问

访问 `http://localhost:3001/creative/upload`

## 🚨 常见问题

### 问题 1：前端无法启动

**错误信息：**
```
Port 3001 is already in use
```

**解决方法：**
```bash
# 查找占用端口的进程
netstat -ano | findstr :3001

# 停止进程
taskkill /F /PID <PID>
```

### 问题 2：代理不生效

**症状：**
- 请求仍然返回 404 或连接错误

**解决方法：**
1. 确认 Vite 配置已保存
2. 完全停止前端服务（Ctrl+C）
3. 重新启动前端服务
4. 清除浏览器缓存（Ctrl+Shift+Delete）

### 问题 3：CORS 错误

**错误信息：**
```
Access to XMLHttpRequest has been blocked by CORS policy
```

**解决方法：**
- 确认后端服务已配置 CORS
- 检查 `changeOrigin: true` 是否设置
- 重启后端服务

### 问题 4：请求超时

**错误信息：**
```
timeout of 10000ms exceeded
```

**解决方法：**
1. 检查创意服务是否正在运行
2. 检查数据库连接是否正常
3. 查看后端日志获取详细错误信息

## 📊 请求流程图

```
浏览器
  ↓
http://localhost:3001/api/creative/designs
  ↓
Vite 开发服务器（端口 3001）
  ↓
代理配置匹配 /api/creative
  ↓
转发到 http://localhost:8087/api/creative/designs
  ↓
创意服务（端口 8087）
  ↓
处理请求并返回响应
  ↓
通过代理返回给浏览器
```

## ✨ 验证成功标志

当一切正常时，你应该看到：

1. **前端控制台**
   - 无 CORS 错误
   - 无 404 错误
   - 请求成功返回 200 状态码

2. **后端日志**
   ```
   提交设计作品 - 用户ID: 1, 请求数据: DesignSubmitRequest(...)
   文件列表JSON: ["http://...", "http://..."]
   作品插入成功 - ID: 123
   ```

3. **浏览器页面**
   - 显示"作品上传成功！"提示
   - 自动跳转到创意空间页面

## 📚 相关文档

- [创意服务重启](CREATIVE_SERVICE_RESTARTED.md)
- [创意 500 错误修复](CREATIVE_500_ERROR_FIX.md)
- [创意文件上传更新](CREATIVE_FILE_UPLOAD_UPDATE.md)

---

**现在需要重启前端服务才能使代理配置生效！** 🔄
