# 冀忆红途 - 数据库架构说明

## 数据库技术栈

本项目采用**微服务架构**，每个服务使用独立的数据库，主要使用以下数据库技术：

### 1. MySQL 8.0+ （主数据库）
- **用途**：关系型数据存储
- **端口**：3306
- **字符集**：utf8mb4
- **排序规则**：utf8mb4_unicode_ci

### 2. Redis 7.0+ （缓存）
- **用途**：缓存、会话存储、分布式锁
- **端口**：6379
- **使用场景**：
  - 用户登录会话
  - 热点数据缓存
  - 分布式锁

### 3. MongoDB 6.0+ （文档数据库）
- **用途**：文档存储（可选）
- **端口**：27017
- **使用场景**：
  - 3D模型元数据
  - 日志存储

## 数据库列表

项目共有 **7个独立的MySQL数据库**，每个微服务一个：

| 数据库名 | 服务名 | 端口 | 说明 |
|---------|--------|------|------|
| `jiyi_user` | user-service | 8081 | 用户服务：用户信息、认证、关注 |
| `jiyi_academy` | academy-service | 8082 | 学院服务：课程、学习记录 |
| `jiyi_tourism` | tourism-service | 8083 | 旅游服务：景点、路线 |
| `jiyi_guide` | guide-service | 8084 | 导览服务：导游、预约 |
| `jiyi_mall` | mall-service | 8085 | 商城服务：商品、订单 |
| `jiyi_creative` | creative-service | 8086 | 众创服务：作品、投稿 |
| `jiyi_social` | social-service | 8087 | 社交服务：帖子、评论、消息 |

## 数据库架构特点

### 微服务独立数据库
- ✅ 每个服务有独立的数据库
- ✅ 服务间数据隔离
- ✅ 可独立扩展和维护
- ✅ 避免单点故障

### 统一技术栈
- ✅ 所有服务使用 MySQL 8.0+
- ✅ 统一字符集 utf8mb4
- ✅ 统一使用 MyBatis-Plus ORM
- ✅ 统一的逻辑删除机制

## 核心数据库详解

### 1. jiyi_mall（商城数据库）

#### 表结构

**product（商品表）**
```sql
- id: 商品ID
- name: 商品名称
- category: 商品分类
- description: 商品描述
- cultural_background: 文化背景
- icon: 商品图标（emoji）
- color: 商品颜色（渐变色）
- price: 商品价格
- stock: 库存数量
- sales: 销量
- designer: 设计师名称
- in_stock: 是否有货
- create_time: 创建时间
- update_time: 更新时间
- deleted: 逻辑删除标记
```

**orders（订单表）**
```sql
- id: 订单ID
- order_number: 订单号
- user_id: 用户ID
- total_amount: 订单总金额
- status: 订单状态（pending/paid/shipped/completed/cancelled）
- payment_method: 支付方式
- shipping_address: 收货地址
- tracking_number: 物流单号
- create_time: 创建时间
- update_time: 更新时间
- deleted: 逻辑删除标记
```

**order_item（订单项表）**
```sql
- id: 订单项ID
- order_id: 订单ID
- product_id: 商品ID
- product_name: 商品名称
- product_icon: 商品图标
- product_color: 商品颜色
- price: 商品单价
- quantity: 购买数量
- subtotal: 小计金额
- create_time: 创建时间
- deleted: 逻辑删除标记
```

#### 初始数据
- 20+ 红色文创商品
- 涵盖创意生活、文化周边、设计师推荐三大分类
- 价格区间：¥28 - ¥388

### 2. jiyi_user（用户数据库）

**user（用户表）**
```sql
- id: 用户ID
- username: 用户名（唯一）
- email: 邮箱（唯一）
- password_hash: 密码哈希
- avatar: 头像URL
- role: 角色（user/designer/admin）
- nickname: 昵称
- phone: 手机号
- gender: 性别
- birthdate: 生日
- interests: 兴趣标签（JSON）
- level: 用户等级
- points: 积分
- created_at: 创建时间
- updated_at: 更新时间
- last_login_at: 最后登录时间
- deleted: 逻辑删除标记
```

**user_follow（关注表）**
```sql
- id: 关注ID
- follower_id: 关注者ID
- following_id: 被关注者ID
- created_at: 创建时间
```

### 3. jiyi_social（社交数据库）

**post（帖子表）**
```sql
- id: 帖子ID
- user_id: 用户ID
- content: 内容
- images: 图片（JSON数组）
- location: 位置
- likes: 点赞数
- comments: 评论数
- shares: 分享数
- create_time: 创建时间
- update_time: 更新时间
- deleted: 逻辑删除标记
```

**comment（评论表）**
```sql
- id: 评论ID
- post_id: 帖子ID
- user_id: 用户ID
- content: 内容
- parent_id: 父评论ID
- likes: 点赞数
- create_time: 创建时间
- deleted: 逻辑删除标记
```

**private_message（私信表）**
```sql
- id: 消息ID
- sender_id: 发送者ID
- receiver_id: 接收者ID
- content: 内容
- is_read: 是否已读
- create_time: 创建时间
- deleted: 逻辑删除标记
```

## 数据库连接配置

### 默认配置
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/jiyi_mall?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
```

### Redis配置
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      database: 0-7  # 不同服务使用不同的database
      timeout: 3000ms
```

## ORM框架：MyBatis-Plus

### 配置
```yaml
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true  # 下划线转驼峰
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # SQL日志
  global-config:
    db-config:
      id-type: auto  # 主键自增
      logic-delete-field: deleted  # 逻辑删除字段
      logic-delete-value: 1  # 删除值
      logic-not-delete-value: 0  # 未删除值
```

### 特性
- ✅ 自动CRUD操作
- ✅ 分页插件
- ✅ 逻辑删除
- ✅ 乐观锁
- ✅ 代码生成器

## 数据库初始化

### 方式1：Docker Compose（推荐）
```bash
docker-compose up -d
```
自动启动 MySQL、Redis、MongoDB

### 方式2：手动初始化
```bash
# 1. 创建所有数据库
mysql -u root -p < backend/init-all-databases.sql

# 2. 初始化各服务表结构和数据
mysql -u root -p jiyi_mall < backend/mall-service/src/main/resources/db/migration/V1__init_mall.sql
mysql -u root -p jiyi_user < backend/user-service/src/main/resources/db/schema.sql
# ... 其他服务类似
```

### 方式3：使用批处理脚本（Windows）
```bash
INIT_DATABASE.bat
```

## 数据迁移工具

### Flyway（推荐）
- 版本化数据库迁移
- 自动执行SQL脚本
- 迁移历史记录

### 配置示例
```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```

## 数据库监控

### 性能监控
- Druid 数据源监控
- 访问地址：http://localhost:8085/druid
- 用户名/密码：admin/admin

### 慢查询日志
```yaml
logging:
  level:
    com.jiyi.mall.mapper: debug
```

## 备份策略

### 定期备份
```bash
# 备份单个数据库
mysqldump -u root -p jiyi_mall > backup_mall_$(date +%Y%m%d).sql

# 备份所有数据库
mysqldump -u root -p --all-databases > backup_all_$(date +%Y%m%d).sql
```

### 恢复数据
```bash
mysql -u root -p jiyi_mall < backup_mall_20241231.sql
```

## 安全建议

1. **生产环境**
   - ❌ 不要使用 root/root
   - ✅ 创建专用数据库用户
   - ✅ 限制用户权限
   - ✅ 启用SSL连接

2. **密码管理**
   - ✅ 使用环境变量
   - ✅ 使用配置中心（Nacos）
   - ✅ 定期更换密码

3. **访问控制**
   - ✅ 限制远程访问
   - ✅ 使用防火墙规则
   - ✅ 启用审计日志

## 常见问题

### Q: 为什么使用多个数据库？
A: 微服务架构的最佳实践，每个服务独立数据库，实现数据隔离和服务解耦。

### Q: 数据库之间如何关联？
A: 通过服务间API调用，不直接跨库查询。例如订单服务需要用户信息时，调用用户服务API。

### Q: 如何保证数据一致性？
A: 使用分布式事务（Seata）或最终一致性方案（消息队列）。

### Q: Redis是必须的吗？
A: 不是必须的。注册功能不需要Redis，只有登录会话管理需要。可以先不启动Redis。

## 总结

冀忆红途项目采用**微服务 + 独立数据库**的架构：
- 🗄️ **MySQL 8.0** 作为主数据库
- 🚀 **Redis** 用于缓存和会话
- 📦 **MongoDB** 用于文档存储（可选）
- 🔧 **MyBatis-Plus** 作为ORM框架
- 🐳 **Docker Compose** 简化部署

这种架构提供了良好的扩展性、可维护性和性能。
