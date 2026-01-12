# 项目运行状态

## 启动时间
2026-01-04 08:10

## 服务状态

### ✅ 正在运行的服务

| 服务名称 | 端口 | 状态 | 进程ID | 说明 |
|---------|------|------|--------|------|
| **User Service** | 8081 | ✅ 运行中 | 2 | 用户服务 - 登录、注册、用户管理 |
| **Mall Service** | 8085 | ✅ 运行中 | 3 | 商城服务 - 商品、订单、购物车 |
| **Social Service** | 8083 | ✅ 运行中 | 4 | 社交服务 - 动态、评论、点赞 |
| **Frontend** | 3001 | ✅ 运行中 | 6 | 前端应用 - Vue3 + Vite |

### ❌ 未运行的服务

| 服务名称 | 状态 | 原因 |
|---------|------|------|
| **Creative Service** | ❌ 停止 | Spring Boot版本兼容性问题 |
| **Redis** | ❌ 未启动 | Docker未运行 |

### ⚠️ 其他服务

| 服务名称 | 状态 | 说明 |
|---------|------|------|
| **MySQL** | ✅ 运行中 | 端口 3306 |
| **Academy Service** | ⏸️ 未启动 | 可选服务 |
| **Tourism Service** | ⏸️ 未启动 | 可选服务 |
| **Guide Service** | ⏸️ 未启动 | 可选服务 |
| **Experience Service** | ⏸️ 未启动 | 可选服务 |

## 访问地址

### 🌐 前端应用
- **首页**: http://localhost:3001
- **登录页**: http://localhost:3001/login
- **注册页**: http://localhost:3001/register
- **商城**: http://localhost:3001/mall
- **社交平台**: http://localhost:3001/social
- **众创空间**: http://localhost:3001/creative
- **个人中心**: http://localhost:3001/profile
- **管理后台**: http://localhost:3001/admin

### 📡 后端API
- **User Service**: http://localhost:8081
  - API文档: http://localhost:8081/doc.html
- **Mall Service**: http://localhost:8085
  - API文档: http://localhost:8085/doc.html
- **Social Service**: http://localhost:8083
  - API文档: http://localhost:8083/doc.html

## 测试账号

### 普通用户
- 用户名: `testuser`
- 密码: `123456`

### 管理员
- 用户名: `admin`
- 密码: `admin123`

## 功能状态

### ✅ 可用功能
1. **用户系统**
   - ✅ 用户注册
   - ✅ 用户登录
   - ✅ 个人信息管理
   - ✅ 关注/粉丝功能

2. **商城系统**
   - ✅ 商品浏览
   - ✅ 商品详情
   - ✅ 购物车
   - ✅ 订单管理
   - ✅ 收货地址

3. **社交平台**
   - ✅ 发布动态
   - ✅ 图片上传
   - ✅ 视频上传
   - ✅ 点赞评论
   - ✅ 话题标签
   - ✅ 热门话题

4. **个人中心**
   - ✅ 个人资料
   - ✅ 我的动态
   - ✅ 我的订单
   - ✅ 收货地址

5. **管理后台**
   - ✅ 用户管理
   - ✅ 内容审核
   - ✅ 数据统计

### ⚠️ 部分可用功能
1. **众创空间**
   - ⚠️ 前端页面已优化（视觉效果完成）
   - ❌ 后端服务未启动（需要修复Spring Boot版本）
   - ⚠️ 使用模拟数据展示

### ❌ 不可用功能
1. **众创空间后端**
   - ❌ 作品上传
   - ❌ 作品投票
   - ❌ 我的作品

## Creative Service 问题

### 问题描述
```
Spring Boot [3.1.5] is not compatible with this Spring Cloud release train
```

### 解决方案
需要将 Spring Boot 版本升级到 3.2.x 或禁用兼容性检查：

**方法1：升级Spring Boot版本**
修改 `backend/creative-service/pom.xml`:
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
</parent>
```

**方法2：禁用兼容性检查**
在 `backend/creative-service/src/main/resources/application.yml` 添加:
```yaml
spring:
  cloud:
    compatibility-verifier:
      enabled: false
```

## 下一步操作

### 立即可以做的
1. ✅ 访问前端: http://localhost:3001
2. ✅ 注册新用户或使用测试账号登录
3. ✅ 浏览商城、发布动态、查看个人中心
4. ✅ 测试购物车和订单功能
5. ✅ 体验社交平台的图片/视频上传

### 需要修复的
1. ❌ 修复 Creative Service 的 Spring Boot 版本问题
2. ❌ 启动 Redis（如果需要缓存功能）
3. ⏸️ 根据需要启动其他可选服务

## 停止服务

如需停止服务，可以：
1. 关闭各个PowerShell窗口
2. 或使用 `Ctrl+C` 在各个终端中停止服务

## 日志查看

各服务的日志会实时显示在对应的PowerShell窗口中。

## 注意事项

1. **首次访问可能较慢**：Spring Boot服务启动需要时间
2. **数据库连接**：确保MySQL正在运行且配置正确
3. **端口占用**：如果端口被占用，需要修改配置或停止占用端口的程序
4. **跨域问题**：已配置CORS，前后端可以正常通信

## 性能优化建议

1. 启动Redis以提升性能
2. 配置数据库连接池
3. 启用Spring Boot的缓存功能
4. 前端使用生产构建（`npm run build`）

---

**项目状态**: 🟢 核心功能正常运行
**最后更新**: 2026-01-04 08:10
