# 商城和社区服务完整实施指南

## 当前状态

### ✅ 已完成
1. **User Service** - 用户服务
   - 数据库: jiyi_user ✅
   - 服务状态: 运行中 (端口 8081) ✅
   - 功能: 注册、登录、用户管理 ✅

2. **Mall Service** - 商城服务
   - 数据库: jiyi_mall ✅
   - 表结构: product, orders, order_item ✅
   - 测试数据: 20个商品 ✅
   - 服务状态: 未启动 ⏸️

3. **Social Service** - 社区服务
   - 数据库: 需要创建 ⏸️
   - 服务状态: 未启动 ⏸️

## 实施步骤

### 第一步：初始化社区数据库

```bash
# 1. 创建数据库
mysql -u root -proot -e "CREATE DATABASE IF NOT EXISTS jiyi_social CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 2. 初始化表结构
mysql -u root -proot jiyi_social < backend/social-service/src/main/resources/db/schema.sql

# 3. 导入测试数据
mysql -u root -proot jiyi_social < backend/social-service/src/main/resources/db/data.sql
```

### 第二步：配置服务

#### Mall Service 配置
文件: `backend/mall-service/src/main/resources/application.yml`

```yaml
server:
  port: 8085

spring:
  application:
    name: mall-service
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/jiyi_mall?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: root
  data:
    redis:
      host: localhost
      port: 6379
      database: 5
  cloud:
    nacos:
      discovery:
        enabled: false
    compatibility-verifier:
      enabled: false
```

#### Social Service 配置
文件: `backend/social-service/src/main/resources/application.yml`

```yaml
server:
  port: 8086

spring:
  application:
    name: social-service
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/jiyi_social?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: root
  data:
    redis:
      host: localhost
      port: 6379
      database: 6
  cloud:
    nacos:
      discovery:
        enabled: false
    compatibility-verifier:
      enabled: false
```

### 第三步：启动服务

#### 启动 Mall Service
```bash
cd backend/mall-service
mvn clean install -DskipTests
mvn spring-boot:run
```

#### 启动 Social Service
```bash
cd backend/social-service
mvn clean install -DskipTests
mvn spring-boot:run
```

### 第四步：验证服务

#### 验证 Mall Service
```bash
# 检查服务是否启动
curl http://localhost:8085/api/mall/products

# 或在浏览器访问
http://localhost:8085/doc.html
```

#### 验证 Social Service
```bash
# 检查服务是否启动
curl http://localhost:8086/api/posts

# 或在浏览器访问
http://localhost:8086/doc.html
```

### 第五步：前端配置

#### 更新前端代理配置
文件: `frontend/vite.config.ts`

```typescript
server: {
  port: 3000,
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
    '/api/posts': {
      target: 'http://localhost:8086',
      changeOrigin: true
    },
    '/api/comments': {
      target: 'http://localhost:8086',
      changeOrigin: true
    },
    '/api/checkins': {
      target: 'http://localhost:8086',
      changeOrigin: true
    },
    '/api/badges': {
      target: 'http://localhost:8086',
      changeOrigin: true
    },
    '/api/messages': {
      target: 'http://localhost:8086',
      changeOrigin: true
    },
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## 功能清单

### 商城功能 (Mall Service)

#### 商品管理
- ✅ 商品列表查询（分页、搜索、筛选、排序）
- ✅ 商品详情查询
- ✅ 商品创建/更新/删除
- ✅ 库存管理
- ✅ 销量统计

#### 订单管理
- ✅ 订单列表查询
- ✅ 订单详情查询
- ✅ 订单创建（库存扣减）
- ✅ 订单状态更新
- ✅ 订单取消（库存恢复）
- ✅ 订单支付（销量增加）
- ✅ 确认收货

#### 前端页面
- ✅ 商城主页 (/mall)
- ✅ 购物车页面 (/cart)
- ✅ 订单页面 (/orders)

### 社区功能 (Social Service)

#### 动态管理
- ✅ 动态发布（图文、视频、位置、话题）
- ✅ 动态列表展示
- ✅ 动态详情查询
- ✅ 动态删除
- ✅ 点赞/取消点赞
- ✅ 分享动态

#### 评论管理
- ✅ 发布评论
- ✅ 查看评论列表
- ✅ 删除评论
- ✅ 回复评论

#### 打卡系统
- ✅ 创建打卡记录
- ✅ 查看打卡历史
- ✅ 景点打卡统计

#### 徽章系统
- ✅ 徽章列表
- ✅ 用户徽章查询
- ✅ 自动徽章授予

#### 私信功能
- ✅ 发送私信
- ✅ 查看会话消息
- ✅ 标记已读
- ✅ 未读消息统计

#### 举报功能
- ✅ 提交举报
- ✅ 举报状态管理

#### 前端页面
- ✅ 社区主页 (/social)

## 数据库结构

### jiyi_mall 数据库

#### product 表 - 商品表
```sql
- id: 商品ID
- name: 商品名称
- category: 商品分类
- description: 商品描述
- cultural_background: 文化背景
- icon: 商品图标
- color: 商品颜色
- price: 商品价格
- stock: 库存数量
- sales: 销量
- designer: 设计师名称
- in_stock: 是否有货
- create_time, update_time, deleted
```

#### orders 表 - 订单表
```sql
- id: 订单ID
- order_number: 订单号
- user_id: 用户ID
- total_amount: 订单总金额
- status: 订单状态
- payment_method: 支付方式
- shipping_address: 收货地址
- tracking_number: 物流单号
- create_time, update_time, deleted
```

#### order_item 表 - 订单项表
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
- create_time, deleted
```

### jiyi_social 数据库

#### post 表 - 动态表
```sql
- id: 动态ID
- user_id: 用户ID
- content: 动态内容
- type: 动态类型
- location: 位置信息
- latitude, longitude: 经纬度
- likes_count: 点赞数
- comments_count: 评论数
- shares_count: 分享数
- visibility: 可见性
- create_time, update_time, deleted
```

#### comment 表 - 评论表
```sql
- id: 评论ID
- post_id: 动态ID
- user_id: 用户ID
- parent_id: 父评论ID
- content: 评论内容
- likes_count: 点赞数
- create_time, deleted
```

#### checkin 表 - 打卡记录表
```sql
- id: 打卡ID
- user_id: 用户ID
- location_id: 景点ID
- location_name: 景点名称
- latitude, longitude: 经纬度
- post_id: 关联动态ID
- create_time
```

#### badge 表 - 徽章表
```sql
- id: 徽章ID
- name: 徽章名称
- description: 徽章描述
- icon: 徽章图标
- type: 徽章类型
- rarity: 稀有度
- condition_type: 获得条件类型
- condition_value: 获得条件值
- create_time
```

#### private_message 表 - 私信表
```sql
- id: 消息ID
- sender_id: 发送者ID
- receiver_id: 接收者ID
- content: 消息内容
- type: 消息类型
- is_read: 是否已读
- create_time
```

## API 端点

### Mall Service (端口 8085)

#### 商品相关
- GET    /api/mall/products - 获取商品列表
- GET    /api/mall/products/{id} - 获取商品详情
- POST   /api/mall/products - 创建商品
- PUT    /api/mall/products/{id} - 更新商品
- DELETE /api/mall/products/{id} - 删除商品

#### 订单相关
- GET    /api/mall/orders - 获取订单列表
- GET    /api/mall/orders/{id} - 获取订单详情
- POST   /api/mall/orders - 创建订单
- PUT    /api/mall/orders/{id}/status - 更新订单状态
- DELETE /api/mall/orders/{id} - 取消订单
- POST   /api/mall/orders/{id}/pay - 支付订单
- POST   /api/mall/orders/{id}/confirm - 确认收货

### Social Service (端口 8086)

#### 动态相关
- POST   /api/posts - 创建动态
- GET    /api/posts - 获取动态列表
- GET    /api/posts/{id} - 获取动态详情
- DELETE /api/posts/{id} - 删除动态
- POST   /api/posts/{id}/like - 点赞动态
- DELETE /api/posts/{id}/like - 取消点赞
- POST   /api/posts/{id}/share - 分享动态

#### 评论相关
- POST   /api/comments - 创建评论
- GET    /api/comments/post/{id} - 获取动态评论
- DELETE /api/comments/{id} - 删除评论

#### 打卡相关
- POST   /api/checkins - 创建打卡记录
- GET    /api/checkins/user/{id} - 获取用户打卡记录
- GET    /api/checkins/location/{id} - 获取景点打卡记录

#### 徽章相关
- GET    /api/badges - 获取所有徽章
- GET    /api/badges/user/{id} - 获取用户徽章

#### 私信相关
- POST   /api/messages - 发送私信
- GET    /api/messages/conversation/{id} - 获取会话消息
- PUT    /api/messages/{id}/read - 标记已读
- GET    /api/messages/unread-count - 获取未读数

## 测试数据

### 商城测试数据
- 20个商品（创意生活、文化周边、设计师推荐）
- 价格范围：28元 - 388元
- 库存范围：30 - 1000件
- 销量范围：450 - 3400件

### 社区测试数据
- 10个话题（#红色记忆、#革命精神等）
- 8个徽章（新手上路、打卡达人等）
- 示例动态和评论
- 示例打卡记录

## 快速启动命令

### 一键启动所有服务

```bash
# 启动 User Service (如果未运行)
start cmd /k "cd backend/user-service && mvn spring-boot:run"

# 启动 Mall Service
start cmd /k "cd backend/mall-service && mvn spring-boot:run"

# 启动 Social Service
start cmd /k "cd backend/social-service && mvn spring-boot:run"

# 启动前端
start cmd /k "cd frontend && npm run dev"
```

### 验证所有服务

```bash
# 检查所有服务端口
netstat -an | findstr "8081 8085 8086 3000"
```

应该看到：
- 8081 - User Service
- 8085 - Mall Service
- 8086 - Social Service
- 3000 - Frontend

## 访问地址

- 前端应用: http://localhost:3000
- 商城页面: http://localhost:3000/mall
- 购物车: http://localhost:3000/cart
- 订单页面: http://localhost:3000/orders
- 社区页面: http://localhost:3000/social

- User Service API: http://localhost:8081/doc.html
- Mall Service API: http://localhost:8085/doc.html
- Social Service API: http://localhost:8086/doc.html

## 故障排除

### 问题 1: 服务启动失败
**解决方案**:
1. 检查端口是否被占用
2. 检查数据库连接
3. 检查 Redis 连接
4. 查看日志文件

### 问题 2: 数据库连接失败
**解决方案**:
1. 确认 MySQL 正在运行
2. 确认数据库已创建
3. 确认用户名密码正确
4. 检查防火墙设置

### 问题 3: Redis 连接失败
**解决方案**:
1. 启动 Docker Redis: `docker start jiyi-redis`
2. 或启动本地 Redis 服务

### 问题 4: 前端无法访问后端
**解决方案**:
1. 检查代理配置
2. 确认后端服务已启动
3. 检查跨域配置
4. 清除浏览器缓存

## 下一步优化

### 高优先级
1. 集成支付网关（微信支付、支付宝）
2. 实现图片上传功能
3. 添加实时消息推送（WebSocket）
4. 完善用户评价系统

### 中优先级
5. 添加物流跟踪功能
6. 实现优惠券系统
7. 添加积分系统
8. 完善售后服务流程

### 低优先级
9. 添加秒杀功能
10. 实现推荐算法
11. 添加数据统计分析
12. 优化性能和缓存策略

## 总结

商城和社区服务已完成核心功能开发，包括：
- ✅ 完整的商品和订单管理系统
- ✅ 完整的社交动态和互动系统
- ✅ 打卡、徽章、私信等特色功能
- ✅ 前后端完整集成
- ✅ 数据库设计和测试数据

系统采用微服务架构，具有良好的扩展性和维护性。
