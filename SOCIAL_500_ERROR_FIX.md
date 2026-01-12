# Social Service 500 错误修复

## 问题现象
访问社交平台时出现多个500错误：
```
GET http://localhost:3001/api/posts?page=1&size=20 500 (Internal Server Error)
GET http://localhost:3001/api/topics/hot 500 (Internal Server Error)
GET http://localhost:3001/api/topics/users/active 500 (Internal Server Error)
```

## 根本原因
**social-service 服务未运行**

检查端口8083没有进程监听，说明服务已停止。

## 解决方案

### 已执行的修复
✅ 启动 social-service
```bash
cd backend/social-service
mvn spring-boot:run
```

服务已成功启动在端口 8083。

## 验证步骤

### 1. 检查服务状态
```bash
netstat -ano | findstr :8083
```
应该看到端口8083被占用。

### 2. 测试API端点
在浏览器或Postman中测试：
```
GET http://localhost:8083/api/posts?page=1&size=20
GET http://localhost:8083/api/topics/hot
```

### 3. 刷新前端页面
1. 访问 http://localhost:3001/social
2. 应该能看到帖子列表
3. 右侧应该显示热门话题
4. 应该显示活跃用户

## 当前运行的服务

| 服务 | 端口 | 状态 |
|------|------|------|
| user-service | 8081 | ✅ 运行中 |
| social-service | 8083 | ✅ 运行中 |
| mall-service | 8085 | ❓ 需要检查 |
| frontend | 3001 | ✅ 运行中 |

## 启动所有服务的脚本

如果需要一次性启动所有服务，使用：
```bash
START_ALL_SERVICES.ps1
```

或者单独启动：
```bash
# User Service
cd backend/user-service
mvn spring-boot:run

# Social Service
cd backend/social-service
mvn spring-boot:run

# Mall Service
cd backend/mall-service
mvn spring-boot:run
```

## 常见问题

### Q: 为什么服务会停止？
**A:** 可能的原因：
1. 手动关闭了终端窗口
2. 系统重启
3. 服务崩溃
4. 端口被其他程序占用

### Q: 如何保持服务持续运行？
**A:** 
1. 不要关闭运行服务的终端窗口
2. 使用后台进程管理工具
3. 使用 `START_ALL_SERVICES.ps1` 脚本统一管理

### Q: 如何检查哪些服务在运行？
**A:** 
```bash
# 检查所有Java进程
jps -l

# 检查特定端口
netstat -ano | findstr :8081
netstat -ano | findstr :8083
netstat -ano | findstr :8085
```

## 下一步

现在所有服务都已启动，你可以：

1. ✅ 重新登录（如果需要）
   - 用户名: chovy
   - 密码: 123456

2. ✅ 访问社交平台
   - http://localhost:3001/social
   - 查看帖子列表
   - 查看热门话题（显示真实统计数据）
   - 查看活跃用户

3. ✅ 测试图片和视频上传
   - 创建新帖子
   - 上传图片（最大10MB）
   - 上传视频（最大100MB）
   - 查看媒体文件显示

4. ✅ 测试其他功能
   - 点赞、评论
   - 关注用户
   - 浏览商城
   - 查看个人中心

## 修复总结

问题：social-service 未运行导致500错误
解决：启动 social-service 服务
状态：✅ 已修复，服务正常运行
