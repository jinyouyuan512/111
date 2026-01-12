# 🚀 快速启动指南

## 当前状态

✅ **所有数据库已完成初始化**
- jiyi_user (用户服务) - 3个用户
- jiyi_mall (商城服务) - 20个商品
- jiyi_social (社区服务) - 8个话题, 14个徽章

✅ **User Service 正在运行** (端口 8081)

⏸️ **需要启动的服务**
- Mall Service (端口 8085)
- Social Service (端口 8086)

## 立即启动

### 步骤 1: 启动 Mall Service

打开新的命令行窗口，执行：

```bash
cd backend/mall-service
mvn spring-boot:run
```

等待看到：
```
Started MallServiceApplication in X.XXX seconds
```

### 步骤 2: 启动 Social Service

打开另一个新的命令行窗口，执行：

```bash
cd backend/social-service
mvn spring-boot:run
```

等待看到：
```
Started SocialServiceApplication in X.XXX seconds
```

### 步骤 3: 访问应用

打开浏览器访问：

- **首页**: http://localhost:3000
- **商城**: http://localhost:3000/mall
- **社区**: http://localhost:3000/social

## 测试账号

使用之前注册的账号登录：
- 用户名: ruler
- 密码: (你注册时设置的密码)

或者注册新账号。

## 功能测试

### 测试商城功能
1. 访问商城页面
2. 浏览商品
3. 搜索商品
4. 加入购物车
5. 查看购物车
6. 创建订单
7. 支付订单

### 测试社区功能
1. 访问社区页面
2. 发布动态
3. 点赞动态
4. 评论动态
5. 查看话题
6. 查看徽章

## API 文档

- User Service: http://localhost:8081/doc.html
- Mall Service: http://localhost:8085/doc.html
- Social Service: http://localhost:8086/doc.html

## 故障排除

### 服务启动失败

**检查端口占用**:
```bash
netstat -ano | findstr "8085"
netstat -ano | findstr "8086"
```

**检查 MySQL**:
```bash
netstat -an | findstr "3306"
```

**检查 Redis**:
```bash
docker ps | findstr redis
```

如果 Redis 未运行：
```bash
docker start jiyi-redis
```

### 前端无法访问后端

1. 确认后端服务已启动
2. 检查浏览器控制台错误
3. 清除浏览器缓存
4. 刷新页面 (Ctrl+F5)

## 完整服务列表

| 服务 | 端口 | 状态 | 访问地址 |
|------|------|------|----------|
| User Service | 8081 | ✅ 运行中 | http://localhost:8081 |
| Mall Service | 8085 | ⏸️ 待启动 | http://localhost:8085 |
| Social Service | 8086 | ⏸️ 待启动 | http://localhost:8086 |
| Frontend | 3000 | ✅ 运行中 | http://localhost:3000 |
| MySQL | 3306 | ✅ 运行中 | - |
| Redis | 6379 | ✅ 运行中 | - |

## 下一步

启动服务后，你可以：

1. ✅ 浏览和购买商品
2. ✅ 管理购物车和订单
3. ✅ 发布和浏览动态
4. ✅ 打卡红色景点
5. ✅ 获得成就徽章
6. ✅ 与其他用户互动

---

**祝你使用愉快！** 🎉
