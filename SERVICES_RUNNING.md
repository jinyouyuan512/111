# 服务运行状态

## 当前运行的服务

### 后端服务

#### 1. user-service (用户服务)
- **端口**: 8081
- **状态**: ✅ 运行中
- **功能**: 用户注册、登录、个人信息管理
- **访问**: http://localhost:8081

#### 2. social-service (社交服务)
- **端口**: 8083
- **状态**: ✅ 运行中
- **功能**: 
  - 动态发布、点赞、评论
  - 热门话题、活跃用户
  - 文件上传（图片、视频）
- **访问**: http://localhost:8083
- **文件上传**: http://localhost:8083/api/upload
- **静态资源**: http://localhost:8083/uploads

#### 3. mall-service (商城服务)
- **端口**: 8085
- **状态**: ✅ 运行中
- **功能**: 商品浏览、购物车、订单管理
- **访问**: http://localhost:8085

### 前端服务

#### Vue3 + Vite
- **端口**: 3001
- **状态**: ✅ 运行中
- **访问**: http://localhost:3001
- **代理配置**: 已配置所有后端服务的代理

## API代理配置

前端通过Vite代理访问后端服务：

```
/api/auth       → http://localhost:8081 (user-service)
/api/users      → http://localhost:8081 (user-service)
/api/posts      → http://localhost:8083 (social-service)
/api/topics     → http://localhost:8083 (social-service)
/api/comments   → http://localhost:8083 (social-service)
/api/upload     → http://localhost:8083 (social-service)
/uploads        → http://localhost:8083 (social-service)
/api/mall       → http://localhost:8085 (mall-service)
```

## 快速访问

### 主要页面
- 首页: http://localhost:3001/
- 登录: http://localhost:3001/login
- 社交平台: http://localhost:3001/social
- 商城: http://localhost:3001/mall
- 个人中心: http://localhost:3001/profile

### API文档
- User Service: http://localhost:8081/swagger-ui.html
- Social Service: http://localhost:8083/swagger-ui.html
- Mall Service: http://localhost:8085/swagger-ui.html

## 测试账号

### 用户账号
```
用户名: testuser
密码: 123456
```

或注册新账号：http://localhost:3001/login

## 功能测试清单

### 1. 用户功能
- [ ] 注册新账号
- [ ] 登录
- [ ] 查看个人信息
- [ ] 修改个人信息

### 2. 社交功能
- [ ] 查看动态列表
- [ ] 发布文字动态
- [ ] 上传图片（最多9张）
- [ ] 上传视频（最大100MB）
- [ ] 点赞动态
- [ ] 评论动态
- [ ] 查看热门话题
- [ ] 查看活跃用户

### 3. 商城功能
- [ ] 浏览商品
- [ ] 添加购物车
- [ ] 查看购物车
- [ ] 提交订单
- [ ] 查看订单

## 文件上传测试

### 图片上传
1. 访问社交平台: http://localhost:3001/social
2. 点击发布框
3. 点击"图片"按钮
4. 选择图片文件（JPG/PNG/GIF，最大10MB）
5. 等待上传完成
6. 输入内容后发布
7. 刷新页面查看图片

### 视频上传
1. 访问社交平台: http://localhost:3001/social
2. 点击发布框
3. 点击"视频"按钮
4. 选择视频文件（MP4/AVI/MOV，最大100MB）
5. 等待上传完成
6. 输入内容后发布
7. 刷新页面查看视频

## 停止服务

### 停止所有后端服务
```powershell
# 查找Java进程
Get-Process -Name "java" | Stop-Process -Force
```

### 停止前端服务
```powershell
# 在前端终端按 Ctrl+C
```

## 重启服务

### 重启后端服务
```powershell
# user-service
cd backend/user-service
mvn spring-boot:run

# social-service
cd backend/social-service
mvn spring-boot:run

# mall-service
cd backend/mall-service
mvn spring-boot:run
```

### 重启前端
```powershell
cd frontend
npm run dev
```

## 故障排查

### 端口被占用
```powershell
# 查看端口占用
netstat -ano | findstr :8081
netstat -ano | findstr :8083
netstat -ano | findstr :8085
netstat -ano | findstr :3001

# 结束进程
taskkill /PID <进程ID> /F
```

### 数据库连接失败
1. 检查MySQL是否运行
2. 检查数据库配置（application.yml）
3. 检查数据库是否已创建

### 文件上传失败
1. 检查uploads目录是否存在
2. 检查目录权限
3. 检查文件大小是否超限
4. 查看后端日志

### 前端无法访问后端
1. 检查后端服务是否启动
2. 检查代理配置（vite.config.ts）
3. 清除浏览器缓存
4. 检查网络连接

## 日志位置

### 后端日志
- 控制台输出
- 或查看target/logs目录（如果配置了）

### 前端日志
- 浏览器控制台（F12）
- Network标签查看API请求

## 性能监控

### 后端
- JVM内存使用
- 数据库连接池
- API响应时间

### 前端
- 页面加载时间
- 资源加载大小
- 网络请求数量

## 注意事项

1. **首次启动**: 后端服务启动需要10-20秒
2. **数据库**: 确保MySQL已启动并创建了相应数据库
3. **文件上传**: uploads目录会自动创建
4. **浏览器缓存**: 修改代码后建议清除缓存
5. **端口冲突**: 确保端口未被其他程序占用

## 开发建议

1. **热重载**: 前端支持热重载，修改代码自动刷新
2. **API测试**: 使用Swagger UI测试API
3. **数据库工具**: 使用Navicat或MySQL Workbench管理数据库
4. **日志级别**: 开发环境建议使用DEBUG级别
5. **错误处理**: 查看控制台和浏览器开发者工具

## 下一步

所有服务已启动，可以开始测试：

1. 访问 http://localhost:3001
2. 注册/登录账号
3. 测试社交平台功能
4. 测试文件上传功能
5. 测试商城功能

祝开发顺利！🚀
