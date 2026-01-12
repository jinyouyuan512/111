# 商城服务 (Mall Service)

## 服务概述

商城服务提供红色文创商品的展示、购买、订单管理等功能。

## 技术栈

- Spring Boot 3.x
- MyBatis Plus
- MySQL 8.0
- Redis
- Nacos (服务注册与发现)
- Knife4j (API文档)

## 端口配置

- 服务端口: 8085
- API文档: http://localhost:8085/doc.html

## 数据库

- 数据库名: `jiyi_mall`
- Redis Database: 5

## 核心功能

### 1. 商品管理
- 商品列表查询（分页、搜索、筛选、排序）
- 商品详情查询
- 商品创建/更新/删除
- 库存管理
- 销量统计

### 2. 订单管理
- 订单创建
- 订单列表查询
- 订单详情查询
- 订单状态更新
- 订单取消

### 3. 购物车
- 添加商品到购物车
- 购物车列表查询
- 更新购物车商品数量
- 删除购物车商品
- 清空购物车

## API接口

### 商品接口

```
GET    /api/mall/products          - 获取商品列表
GET    /api/mall/products/{id}     - 获取商品详情
POST   /api/mall/products          - 创建商品
PUT    /api/mall/products/{id}     - 更新商品
DELETE /api/mall/products/{id}     - 删除商品
```

### 订单接口

```
GET    /api/mall/orders            - 获取订单列表
GET    /api/mall/orders/{id}       - 获取订单详情
POST   /api/mall/orders            - 创建订单
PUT    /api/mall/orders/{id}       - 更新订单状态
DELETE /api/mall/orders/{id}       - 取消订单
```

### 购物车接口

```
GET    /api/mall/cart              - 获取购物车
POST   /api/mall/cart              - 添加商品到购物车
PUT    /api/mall/cart/{id}         - 更新购物车商品
DELETE /api/mall/cart/{id}         - 删除购物车商品
DELETE /api/mall/cart              - 清空购物车
```

## 数据模型

### Product (商品)
- id: 商品ID
- name: 商品名称
- category: 商品分类
- description: 商品描述
- culturalBackground: 文化背景
- icon: 商品图标
- color: 商品颜色
- price: 商品价格
- stock: 库存数量
- sales: 销量
- designer: 设计师
- inStock: 是否有货

### Order (订单)
- id: 订单ID
- orderNumber: 订单号
- userId: 用户ID
- totalAmount: 订单总金额
- status: 订单状态
- paymentMethod: 支付方式
- shippingAddress: 收货地址
- trackingNumber: 物流单号

### OrderItem (订单项)
- id: 订单项ID
- orderId: 订单ID
- productId: 商品ID
- productName: 商品名称
- price: 商品单价
- quantity: 购买数量
- subtotal: 小计金额

## 项目结构

```
mall-service/
├── src/main/java/com/jiyi/mall/
│   ├── MallServiceApplication.java    # 启动类
│   ├── controller/                     # 控制器层
│   │   ├── ProductController.java
│   │   ├── OrderController.java
│   │   └── CartController.java
│   ├── service/                        # 服务层
│   │   ├── ProductService.java
│   │   ├── OrderService.java
│   │   └── CartService.java
│   ├── mapper/                         # 数据访问层
│   │   ├── ProductMapper.java
│   │   ├── OrderMapper.java
│   │   └── OrderItemMapper.java
│   ├── entity/                         # 实体类
│   │   ├── Product.java
│   │   ├── Order.java
│   │   └── OrderItem.java
│   ├── dto/                            # 数据传输对象
│   └── config/                         # 配置类
├── src/main/resources/
│   ├── application.yml                 # 配置文件
│   ├── mapper/                         # MyBatis XML
│   └── db/                             # 数据库脚本
│       └── migration/
│           └── V1__init_mall.sql
└── pom.xml                             # Maven配置
```

## 启动步骤

### 1. 数据库准备
```sql
CREATE DATABASE IF NOT EXISTS jiyi_mall CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 执行数据库脚本
```bash
mysql -u root -p jiyi_mall < src/main/resources/db/migration/V1__init_mall.sql
```

### 3. 启动Nacos
```bash
# 确保Nacos在 localhost:8848 运行
```

### 4. 启动Redis
```bash
# 确保Redis在 localhost:6379 运行
```

### 5. 启动服务
```bash
mvn spring-boot:run
```

或者在IDE中直接运行 `MallServiceApplication.java`

## 测试

访问 API 文档: http://localhost:8085/doc.html

## 配置说明

### application.yml 主要配置

```yaml
server:
  port: 8085

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/jiyi_mall
    username: root
    password: root
  
  data:
    redis:
      database: 5
  
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
```

## 开发规范

1. 所有API返回统一的Result对象
2. 使用MyBatis Plus进行数据库操作
3. 使用Redis缓存热点数据
4. 使用Knife4j生成API文档
5. 遵循RESTful API设计规范

## 注意事项

1. 商品库存需要考虑并发问题
2. 订单创建需要事务保证
3. 购物车数据存储在Redis中
4. 价格使用BigDecimal避免精度问题

## 后续优化

- [ ] 添加商品评价功能
- [ ] 添加优惠券系统
- [ ] 添加积分系统
- [ ] 添加秒杀功能
- [ ] 添加订单超时自动取消
- [ ] 添加库存预警
- [ ] 添加销量排行榜
- [ ] 添加推荐算法

---

**开发团队**: 冀忆红途项目组
**最后更新**: 2024-12-23
