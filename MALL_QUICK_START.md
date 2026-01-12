# 商城模块快速启动指南

## 🚀 快速启动步骤

### 1. 准备环境

#### 启动MySQL
```bash
# 确保MySQL正在运行
# Windows: 检查服务
services.msc

# 或使用命令行
net start MySQL80
```

#### 创建数据库
```sql
CREATE DATABASE IF NOT EXISTS jiyi_mall CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 导入数据
```bash
# 进入项目目录
cd backend/mall-service

# 导入数据库脚本
mysql -u root -p jiyi_mall < src/main/resources/db/migration/V1__init_mall.sql
```

#### 启动Redis
```bash
# Windows: 启动Redis服务
redis-server

# 或者使用Windows服务
net start Redis
```

#### 启动Nacos（可选）
```bash
# 如果使用Nacos服务注册
cd nacos/bin
startup.cmd -m standalone
```

### 2. 启动后端服务

#### 方式一：使用Maven
```bash
cd backend/mall-service
mvn spring-boot:run
```

#### 方式二：使用IDE
1. 打开 `MallServiceApplication.java`
2. 点击运行按钮
3. 等待服务启动完成

#### 验证启动成功
```bash
# 访问API文档
http://localhost:8085/doc.html

# 测试健康检查
curl http://localhost:8085/actuator/health
```

### 3. 启动前端服务

```bash
cd frontend

# 安装依赖（首次运行）
npm install

# 启动开发服务器
npm run dev
```

#### 访问前端页面
```
商城页面: http://localhost:5173/mall
购物车页面: http://localhost:5173/cart
订单页面: http://localhost:5173/orders
```

## 🧪 快速测试

### 1. 测试商品列表API

```bash
# 获取商品列表
curl http://localhost:8085/api/mall/products?page=1&size=12

# 搜索商品
curl "http://localhost:8085/api/mall/products?keyword=西柏坡"

# 按分类筛选
curl "http://localhost:8085/api/mall/products?category=创意生活"

# 按价格排序
curl "http://localhost:8085/api/mall/products?sort=price_asc"
```

### 2. 测试商品详情API

```bash
# 获取商品详情
curl http://localhost:8085/api/mall/products/1
```

### 3. 测试创建订单API

```bash
# 创建订单
curl -X POST http://localhost:8085/api/mall/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "items": [
      {
        "productId": 1,
        "quantity": 2
      }
    ],
    "shippingAddress": "河北省石家庄市长安区XX路XX号",
    "paymentMethod": "微信支付"
  }'
```

### 4. 测试订单列表API

```bash
# 获取订单列表
curl "http://localhost:8085/api/mall/orders?page=1&size=10&userId=1"

# 按状态筛选
curl "http://localhost:8085/api/mall/orders?status=待支付"
```

### 5. 测试支付订单API

```bash
# 支付订单（假设订单ID为1）
curl -X POST "http://localhost:8085/api/mall/orders/1/pay?paymentMethod=微信支付"
```

## 🎯 功能演示流程

### 完整购物流程演示

#### 步骤1: 浏览商品
1. 访问 http://localhost:5173/mall
2. 查看商品列表
3. 使用搜索框搜索"西柏坡"
4. 点击分类筛选"创意生活"
5. 调整价格区间
6. 选择排序方式

#### 步骤2: 查看商品详情
1. 点击任意商品卡片
2. 查看商品详情弹窗
3. 选择规格和数量
4. 点击"加入购物车"

#### 步骤3: 管理购物车
1. 访问 http://localhost:5173/cart
2. 查看购物车商品
3. 调整商品数量
4. 选择要结算的商品
5. 查看价格计算（商品总价、运费、应付总额）

#### 步骤4: 创建订单
1. 点击"去结算"按钮
2. 确认订单信息
3. 订单创建成功
4. 自动跳转到订单页面

#### 步骤5: 管理订单
1. 访问 http://localhost:5173/orders
2. 查看订单列表
3. 点击"支付"按钮
4. 选择支付方式
5. 支付成功后状态变为"待发货"

#### 步骤6: 确认收货
1. 等待商家发货（状态变为"已发货"）
2. 点击"确认收货"按钮
3. 订单状态变为"已完成"

## 🔍 常见问题排查

### 问题1: 后端服务启动失败

**可能原因**:
- MySQL未启动或连接失败
- Redis未启动
- 端口8085被占用

**解决方案**:
```bash
# 检查MySQL
mysql -u root -p

# 检查Redis
redis-cli ping

# 检查端口占用
netstat -ano | findstr :8085
```

### 问题2: 前端无法连接后端

**可能原因**:
- 后端服务未启动
- CORS配置问题
- API路径错误

**解决方案**:
1. 确认后端服务已启动
2. 检查 `frontend/src/api/request.ts` 中的baseURL配置
3. 查看浏览器控制台错误信息

### 问题3: 商品列表为空

**可能原因**:
- 数据库脚本未执行
- 数据被逻辑删除

**解决方案**:
```sql
-- 检查商品数据
SELECT COUNT(*) FROM product WHERE deleted = 0;

-- 如果为0，重新导入数据
SOURCE src/main/resources/db/migration/V1__init_mall.sql;
```

### 问题4: 创建订单失败

**可能原因**:
- 库存不足
- 商品不存在
- 数据验证失败

**解决方案**:
1. 检查商品库存
2. 验证商品ID是否存在
3. 查看后端日志错误信息

### 问题5: 缓存未生效

**可能原因**:
- Redis未启动
- Redis配置错误
- 缓存键不匹配

**解决方案**:
```bash
# 连接Redis
redis-cli -n 5

# 查看所有键
KEYS *

# 清空缓存重试
FLUSHDB
```

## 📊 性能测试

### 使用JMeter进行压力测试

#### 测试场景1: 商品列表查询
```
并发用户: 100
循环次数: 10
预期TPS: > 500
预期响应时间: < 200ms
```

#### 测试场景2: 订单创建
```
并发用户: 50
循环次数: 5
预期TPS: > 100
预期响应时间: < 500ms
```

#### 测试场景3: 库存并发扣减
```
并发用户: 100
同一商品: ID=1
初始库存: 100
预期结果: 库存正确扣减，无超卖
```

## 🎓 学习资源

### API文档
- Swagger UI: http://localhost:8085/doc.html
- Knife4j文档: http://localhost:8085/doc.html

### 数据库设计
- 查看: `backend/mall-service/src/main/resources/db/migration/V1__init_mall.sql`

### 代码示例
- 商品服务: `backend/mall-service/src/main/java/com/jiyi/mall/service/impl/ProductServiceImpl.java`
- 订单服务: `backend/mall-service/src/main/java/com/jiyi/mall/service/impl/OrderServiceImpl.java`

### 前端组件
- 商城页面: `frontend/src/views/Mall.vue`
- 购物车页面: `frontend/src/views/Cart.vue`
- 订单页面: `frontend/src/views/Orders.vue`

## 📞 技术支持

如遇到问题，请：
1. 查看日志文件
2. 检查配置文件
3. 参考本文档的常见问题部分
4. 查看项目README文档

---

**祝您使用愉快！** 🎉
