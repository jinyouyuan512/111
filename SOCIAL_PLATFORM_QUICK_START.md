# 红色足迹社交平台 - 快速开始指南

## 概述

本指南帮助您快速启动和测试红色足迹社交平台模块。

## 前置条件

### 环境要求
- JDK 17 或更高版本
- Maven 3.8+
- MySQL 8.0+
- Redis 7.0+
- Node.js 18+
- npm 或 yarn

### 检查环境
```bash
# 检查Java版本
java -version

# 检查Maven版本
mvn -version

# 检查MySQL
mysql --version

# 检查Redis
redis-cli --version

# 检查Node.js
node --version
```

## 数据库设置

### 1. 创建数据库
```sql
CREATE DATABASE IF NOT EXISTS jiyi_social 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
```

### 2. 执行数据库脚本
```bash
# 进入social-service目录
cd backend/social-service

# 执行schema.sql创建表结构
mysql -u root -p jiyi_social < src/main/resources/db/schema.sql

# 执行data.sql插入示例数据
mysql -u root -p jiyi_social < src/main/resources/db/data.sql
```

### 3. 验证数据库
```sql
USE jiyi_social;

-- 查看所有表
SHOW TABLES;

-- 查看示例数据
SELECT * FROM post;
SELECT * FROM topic;
SELECT * FROM badge;
SELECT * FROM checkin;
```

## 后端服务启动

### 1. 配置数据库连接
编辑 `backend/social-service/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/jiyi_social?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password  # 修改为你的密码
```

### 2. 配置Redis连接
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: your_password  # 如果有密码
```

### 3. 启动服务
```bash
# 方式1: 使用Maven
cd backend/social-service
mvn spring-boot:run

# 方式2: 打包后运行
mvn clean package
java -jar target/social-service-1.0.0.jar
```

### 4. 验证服务启动
```bash
# 检查服务健康状态
curl http://localhost:8084/actuator/health

# 访问API文档
# 浏览器打开: http://localhost:8084/doc.html
```

## 前端应用启动

### 1. 安装依赖
```bash
cd frontend
npm install
# 或
yarn install
```

### 2. 启动开发服务器
```bash
npm run dev
# 或
yarn dev
```

### 3. 访问应用
浏览器打开: http://localhost:5173

## 快速测试

### 测试1: 查看动态列表
```bash
# 获取动态列表
curl http://localhost:8084/api/posts?page=1&size=10
```

### 测试2: 查看话题列表
```bash
# 获取所有话题
curl http://localhost:8084/api/topics
```

### 测试3: 查看徽章列表
```bash
# 需要登录后获取token
curl -H "Authorization: Bearer YOUR_TOKEN" \
     http://localhost:8084/api/badges
```

### 测试4: 创建打卡记录
```bash
# 需要登录后获取token
curl -X POST http://localhost:8084/api/checkins \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "locationId": 1,
    "locationName": "西柏坡纪念馆",
    "latitude": 38.2345,
    "longitude": 114.5678,
    "content": "今天来到西柏坡"
  }'
```

## 使用Swagger测试API

### 1. 访问Swagger UI
浏览器打开: http://localhost:8084/doc.html

### 2. 测试步骤
1. 展开API分组
2. 选择要测试的接口
3. 点击"Try it out"
4. 填写参数
5. 点击"Execute"
6. 查看响应结果

### 3. 需要认证的接口
1. 先调用登录接口获取token
2. 点击页面右上角"Authorize"
3. 输入: Bearer YOUR_TOKEN
4. 点击"Authorize"
5. 现在可以测试需要认证的接口

## 前端功能测试

### 1. 访问社交平台页面
浏览器打开: http://localhost:5173/social

### 2. 测试功能点
- [ ] 查看动态列表
- [ ] 切换分类标签
- [ ] 点击发帖框
- [ ] 点赞动态
- [ ] 查看评论
- [ ] 分享动态
- [ ] 查看热门话题
- [ ] 查看活跃用户

## 常见问题

### Q1: 数据库连接失败
**解决方案:**
1. 检查MySQL服务是否启动
2. 检查数据库用户名密码
3. 检查数据库是否已创建
4. 检查防火墙设置

### Q2: Redis连接失败
**解决方案:**
1. 检查Redis服务是否启动
2. 检查Redis密码配置
3. 检查Redis端口是否正确

### Q3: 端口被占用
**解决方案:**
```bash
# Windows
netstat -ano | findstr :8084
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8084
kill -9 <PID>
```

### Q4: 前端无法连接后端
**解决方案:**
1. 检查后端服务是否启动
2. 检查CORS配置
3. 检查API地址配置
4. 查看浏览器控制台错误

### Q5: JWT认证失败
**解决方案:**
1. 检查token是否过期
2. 检查token格式（Bearer + 空格 + token）
3. 重新登录获取新token

## 示例数据说明

### 话题数据
- 建党百年
- 西柏坡精神
- 红色旅游打卡
- 重走长征路
- 我的入党故事
- 党史学习
- 红色摄影
- 志愿服务

### 徽章数据
- 发帖相关: 初出茅庐、笔耕不辍、内容达人、红色作家
- 打卡相关: 初次打卡、红色足迹、红色旅行家、红色探索者、红色朝圣者
- 社交相关: 社交新人、人气之星、社区明星
- 学习相关: 学习标兵、红色学者

### 动态数据
- 5条示例动态
- 包含图文、打卡等不同类型
- 包含点赞、评论等互动数据

## 下一步

### 功能扩展
1. 实现图片上传功能
2. 实现视频上传功能
3. 添加实时消息推送
4. 完善用户主页
5. 添加搜索功能

### 性能优化
1. 添加Redis缓存
2. 优化数据库查询
3. 实现CDN加速
4. 添加分页优化

### 安全加固
1. 添加敏感词过滤
2. 实现图片内容审核
3. 添加防刷机制
4. 实现频率限制

## 获取帮助

### 文档
- [实施文档](SOCIAL_PLATFORM_IMPLEMENTATION.md)
- [完成总结](SOCIAL_PLATFORM_COMPLETION.md)
- [验证清单](SOCIAL_PLATFORM_VERIFICATION.md)
- [服务README](backend/social-service/README.md)

### 问题反馈
如遇到问题，请查看:
1. 应用日志
2. 数据库日志
3. Redis日志
4. 浏览器控制台

## 总结

按照本指南，您应该能够:
1. ✅ 成功启动后端服务
2. ✅ 成功启动前端应用
3. ✅ 访问社交平台页面
4. ✅ 测试基本功能
5. ✅ 使用Swagger测试API

祝您使用愉快！🎉
