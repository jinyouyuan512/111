# 项目运行状态

## 🎉 所有服务已成功启动！

### ✅ Redis
- **容器名**: jiyi-redis
- **端口**: 6379
- **状态**: 运行中
- **测试**: `docker exec jiyi-redis redis-cli ping` → PONG

### ✅ User Service (用户服务)
- **端口**: 8081
- **状态**: 运行中
- **启动时间**: ~6.5秒
- **功能**: 用户注册、登录、认证、JWT token 管理

### ✅ Mall Service (商城服务)
- **端口**: 8085
- **状态**: 运行中
- **启动时间**: ~5.6秒
- **API 文档**: http://localhost:8085/doc.html
- **功能**: 商品管理、购物车、订单、地址管理

### ✅ Frontend (前端)
- **端口**: 3000
- **状态**: 运行中
- **访问地址**: http://localhost:3000/
- **框架**: Vue 3 + Vite + TypeScript + Element Plus

## 🌐 访问地址

### 前端页面
- **首页**: http://localhost:3000/
- **登录**: http://localhost:3000/login
- **商城**: http://localhost:3000/mall
- **购物车**: http://localhost:3000/cart
- **订单**: http://localhost:3000/orders
- **管理后台**: http://localhost:3000/admin (需要管理员权限)
- **数字体验**: http://localhost:3000/experience
- **传承学院**: http://localhost:3000/academy
- **智慧导览**: http://localhost:3000/guide
- **智慧旅游**: http://localhost:3000/tourism
- **众创空间**: http://localhost:3000/creative
- **红色社区**: http://localhost:3000/social

### 后端 API
- **User Service**: http://localhost:8081
- **Mall Service**: http://localhost:8085
- **Mall API 文档**: http://localhost:8085/doc.html

## 👤 测试账号

### 管理员账号
- **用户名**: ruler
- **角色**: admin
- **权限**: 可访问管理后台

### 普通用户
- 可以注册新账号进行测试

## 🔧 最近修复

### 1. 登录状态显示问题
- ✅ App.vue 添加 token 验证
- ✅ 401 错误自动清除无效 token
- ✅ 首页 header 动态显示登录状态
- ✅ NavBar 正确显示用户信息

### 2. Redis 连接问题
- ✅ Redis 容器已启动
- ✅ User Service 成功连接 Redis
- ✅ 注册功能正常工作

### 3. 商城功能
- ✅ 商品列表加载正常
- ✅ 价格筛选功能正常
- ✅ 购物车和地址管理完整

## 📝 功能清单

### 已实现功能
- ✅ 用户注册和登录
- ✅ JWT token 认证
- ✅ 商品浏览和搜索
- ✅ 价格区间筛选
- ✅ 购物车管理
- ✅ 收货地址管理
- ✅ 订单管理
- ✅ 管理员后台
- ✅ 用户管理
- ✅ 内容审核
- ✅ 首页动态展示
- ✅ 响应式设计

### 开发中功能
- 🚧 社交平台完整功能
- 🚧 众创空间商品化
- 🚧 支付功能
- 🚧 物流跟踪

## 🚀 快速启动命令

### 启动 Docker Redis
```bash
docker start jiyi-redis
```

### 启动后端服务（在 Kiro 中已自动启动）
```bash
# User Service
cd backend/user-service
mvn spring-boot:run

# Mall Service
cd backend/mall-service
mvn spring-boot:run
```

### 启动前端（在 Kiro 中已自动启动）
```bash
cd frontend
npm run dev
```

## 🛠️ 停止服务

在 Kiro 中，可以通过以下方式停止后台进程：
- 使用 `listProcesses` 查看运行中的进程
- 使用 `controlPwshProcess` 的 stop 动作停止指定进程

或者手动停止：
- 按 Ctrl+C 停止终端中的进程
- 使用任务管理器结束 Java 或 Node 进程

## 📊 性能指标

- **User Service 启动时间**: ~6.5秒
- **Mall Service 启动时间**: ~5.6秒
- **Frontend 启动时间**: ~1.5秒
- **总启动时间**: ~15秒

## 🔍 调试信息

### 查看日志
- User Service: 查看 processId 2 的输出
- Mall Service: 查看 processId 3 的输出
- Frontend: 查看 processId 4 的输出

### 常见问题
1. **端口被占用**: 使用 `netstat -ano | Select-String ":端口号"` 查找占用进程
2. **Redis 连接失败**: 确保 Docker Desktop 运行且 Redis 容器已启动
3. **数据库连接失败**: 确保 MySQL 运行在 localhost:3306

## 📅 更新时间
2026-01-03 09:56

---

**项目状态**: 🟢 运行中
**所有核心服务**: ✅ 正常
**可以开始测试**: ✅ 是
