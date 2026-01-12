# 红色足迹社交平台服务

## 服务概述

红色足迹社交平台服务是冀忆红途平台的核心社交模块，提供完整的社交网络功能，包括动态发布、互动、打卡、徽章系统等。

## 核心功能

### 1. 社交动态
- 发布动态（支持图文、视频、位置、话题标签）
- 动态列表展示（按时间/热度排序）
- 点赞、评论、转发功能
- 动态删除和编辑

### 2. 景点打卡
- 创建打卡记录（包含位置信息）
- 查看用户打卡历史
- 查看景点打卡统计
- 打卡地图展示

### 3. 成就徽章
- 徽章定义和管理
- 自动徽章授予机制
- 用户徽章展示
- 徽章获取条件检查

### 4. 用户等级
- 积分累计机制
- 等级计算逻辑
- 用户统计数据

### 5. 私信功能
- 发送私信（文本、图片、视频）
- 会话消息列表
- 消息已读状态
- 未读消息统计

### 6. 内容审核
- 内容举报功能
- 举报状态管理
- 审核流程支持

## API接口

### 动态管理
```
POST   /api/posts              - 创建动态
GET    /api/posts              - 获取动态列表
GET    /api/posts/{id}         - 获取动态详情
DELETE /api/posts/{id}         - 删除动态
POST   /api/posts/{id}/like    - 点赞动态
DELETE /api/posts/{id}/like    - 取消点赞
POST   /api/posts/{id}/share   - 分享动态
GET    /api/posts/user/{id}    - 获取用户动态
```

### 评论管理
```
POST   /api/comments           - 创建评论
GET    /api/comments/post/{id} - 获取动态评论
DELETE /api/comments/{id}      - 删除评论
```

### 打卡管理
```
POST   /api/checkins                  - 创建打卡记录
GET    /api/checkins/user/{id}        - 获取用户打卡记录
GET    /api/checkins/location/{id}    - 获取景点打卡记录
```

### 徽章管理
```
GET    /api/badges              - 获取所有徽章
GET    /api/badges/user/{id}    - 获取用户徽章
```

### 私信管理
```
POST   /api/messages                      - 发送私信
GET    /api/messages/conversation/{id}    - 获取会话消息
PUT    /api/messages/{id}/read            - 标记已读
GET    /api/messages/unread-count         - 获取未读数
```

### 举报管理
```
POST   /api/reports             - 提交举报
```

## 数据库设计

### 核心表
- `post` - 动态表
- `media_file` - 媒体文件表
- `topic` - 话题表
- `post_topic` - 动态话题关联表
- `comment` - 评论表
- `like_record` - 点赞记录表
- `share_record` - 转发记录表
- `checkin` - 打卡记录表
- `badge` - 成就徽章表
- `user_badge` - 用户徽章表
- `private_message` - 私信表
- `report` - 内容举报表
- `follow` - 关注关系表

## 技术栈

- Spring Boot 3.2+
- MyBatis Plus 3.5+
- MySQL 8.0+
- Redis 7.0+
- JWT认证

## 配置说明

### 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/jiyi_social?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

### Redis配置
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: your_password
```

## 启动服务

### 开发环境
```bash
mvn spring-boot:run
```

### 生产环境
```bash
mvn clean package
java -jar target/social-service-1.0.0.jar
```

## 测试

### 运行单元测试
```bash
mvn test
```

### API测试
访问 Swagger UI: http://localhost:8084/doc.html

## 徽章系统说明

### 徽章类型
- `post` - 发帖相关徽章
- `checkin` - 打卡相关徽章
- `social` - 社交互动徽章
- `learning` - 学习相关徽章

### 徽章稀有度
- `common` - 普通（白色）
- `rare` - 稀有（蓝色）
- `epic` - 史诗（紫色）
- `legendary` - 传说（橙色）

### 自动授予机制
系统会在以下时机自动检查并授予徽章：
- 用户发布动态后
- 用户完成打卡后
- 用户获得点赞后
- 用户完成课程后

## 打卡系统说明

### 打卡流程
1. 用户到达景点
2. 获取当前位置（经纬度）
3. 创建打卡记录
4. 可选：关联动态发布

### 打卡统计
- 用户总打卡数
- 景点打卡人数
- 打卡地图展示
- 打卡排行榜

## 私信系统说明

### 消息类型
- `text` - 文本消息
- `image` - 图片消息
- `video` - 视频消息

### 已读状态
- `0` - 未读
- `1` - 已读

### 消息推送
建议集成WebSocket实现实时消息推送

## 举报审核说明

### 举报类型
- `post` - 动态举报
- `comment` - 评论举报
- `user` - 用户举报

### 举报状态
- `pending` - 待处理
- `processing` - 处理中
- `resolved` - 已处理
- `rejected` - 已驳回

### 审核流程
1. 用户提交举报
2. 管理员审核
3. 处理违规内容
4. 通知举报者和被举报者

## 性能优化建议

### 缓存策略
- 热门动态缓存（Redis）
- 用户信息缓存
- 徽章数据缓存
- 话题数据缓存

### 数据库优化
- 添加适当索引
- 分页查询优化
- 读写分离
- 定期清理过期数据

### 接口优化
- 批量查询用户信息
- 异步处理徽章授予
- 消息队列处理通知
- CDN加速媒体文件

## 安全建议

### 内容安全
- 敏感词过滤
- 图片内容审核
- 防刷机制
- 频率限制

### 数据安全
- SQL注入防护
- XSS攻击防护
- CSRF防护
- 数据加密存储

## 监控告警

### 关键指标
- API响应时间
- 错误率
- 并发用户数
- 数据库连接数
- Redis命中率

### 告警规则
- 响应时间 > 1s
- 错误率 > 1%
- 数据库连接数 > 80%
- Redis内存使用 > 80%

## 联系方式

如有问题，请联系开发团队。
