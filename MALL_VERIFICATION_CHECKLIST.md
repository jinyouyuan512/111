# 商城功能验证清单

## 📋 验证步骤

### 1. 数据库验证

#### 1.1 运行数据库迁移
```bash
# 执行初始化脚本
INIT_CART_ADDRESS.bat
```

#### 1.2 验证表结构
```sql
USE jiyi_mall;

-- 查看所有表
SHOW TABLES;

-- 应该看到以下表：
-- product, orders, order_item, cart, address, favorite, review

-- 查看 cart 表结构
DESC cart;

-- 查看 address 表结构
DESC address;

-- 查看测试数据
SELECT * FROM address;
-- 应该有 2 条测试地址数据
```

**预期结果**:
- ✅ cart 表存在，包含 8 个字段
- ✅ address 表存在，包含 12 个字段
- ✅ favorite 表存在
- ✅ review 表存在
- ✅ address 表有 2 条测试数据

---

### 2. 后端服务验证

#### 2.1 编译项目
```bash
cd backend/mall-service
mvn clean install -DskipTests
```

**预期结果**:
- ✅ 编译成功，无错误
- ✅ 所有新增的类都能正常编译

#### 2.2 启动服务
```bash
mvn spring-boot:run
```

**预期结果**:
- ✅ 服务启动成功
- ✅ 端口 8085 监听
- ✅ 日志中无错误信息
- ✅ Swagger 文档可访问：http://localhost:8085/swagger-ui.html

#### 2.3 检查日志
查看启动日志，确认：
- ✅ CartMapper 加载成功
- ✅ AddressMapper 加载成功
- ✅ CartController 注册成功
- ✅ AddressController 注册成功

---

### 3. 购物车 API 验证

#### 3.1 添加到购物车
```bash
curl -X POST http://localhost:8085/api/mall/cart \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 2" \
  -d '{"productId": 1, "quantity": 2}'
```

**预期结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "userId": 2,
    "productId": 1,
    "quantity": 2,
    "selected": true,
    "product": {
      "id": 1,
      "name": "红色文化T恤",
      "price": 99.00,
      ...
    }
  }
}
```

#### 3.2 获取购物车列表
```bash
curl -X GET http://localhost:8085/api/mall/cart \
  -H "X-User-Id: 2"
```

**预期结果**:
- ✅ 返回购物车列表
- ✅ 包含商品详细信息
- ✅ 数据结构正确

#### 3.3 更新购物车项
```bash
curl -X PUT http://localhost:8085/api/mall/cart/1 \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 2" \
  -d '{"quantity": 3}'
```

**预期结果**:
- ✅ 数量更新成功
- ✅ 返回更新后的数据

#### 3.4 更新选中状态
```bash
curl -X PUT http://localhost:8085/api/mall/cart/1 \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 2" \
  -d '{"selected": false}'
```

**预期结果**:
- ✅ 选中状态更新成功

#### 3.5 全选/取消全选
```bash
curl -X PUT "http://localhost:8085/api/mall/cart/select-all?selected=true" \
  -H "X-User-Id: 2"
```

**预期结果**:
- ✅ 所有商品选中状态更新

#### 3.6 删除购物车项
```bash
curl -X DELETE http://localhost:8085/api/mall/cart/1 \
  -H "X-User-Id: 2"
```

**预期结果**:
- ✅ 删除成功
- ✅ 再次查询购物车，该商品不存在

#### 3.7 批量删除
```bash
curl -X DELETE http://localhost:8085/api/mall/cart/batch \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 2" \
  -d '[1, 2, 3]'
```

**预期结果**:
- ✅ 批量删除成功

#### 3.8 清空购物车
```bash
curl -X DELETE http://localhost:8085/api/mall/cart/clear \
  -H "X-User-Id: 2"
```

**预期结果**:
- ✅ 购物车清空
- ✅ 查询购物车返回空列表

---

### 4. 地址 API 验证

#### 4.1 获取地址列表
```bash
curl -X GET http://localhost:8085/api/mall/addresses \
  -H "X-User-Id: 2"
```

**预期结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "userId": 2,
      "receiverName": "张三",
      "receiverPhone": "13800138000",
      "province": "河北省",
      "city": "石家庄市",
      "district": "长安区",
      "detail": "中山路123号",
      "isDefault": true
    },
    {
      "id": 2,
      "userId": 2,
      "receiverName": "李四",
      "receiverPhone": "13900139000",
      "province": "河北省",
      "city": "保定市",
      "district": "莲池区",
      "detail": "五四路456号",
      "isDefault": false
    }
  ]
}
```

#### 4.2 添加地址
```bash
curl -X POST http://localhost:8085/api/mall/addresses \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 2" \
  -d '{
    "receiverName": "王五",
    "receiverPhone": "13700137000",
    "province": "河北省",
    "city": "石家庄市",
    "district": "桥西区",
    "detail": "裕华路789号",
    "isDefault": false
  }'
```

**预期结果**:
- ✅ 地址添加成功
- ✅ 返回新地址的 ID

#### 4.3 获取地址详情
```bash
curl -X GET http://localhost:8085/api/mall/addresses/1 \
  -H "X-User-Id: 2"
```

**预期结果**:
- ✅ 返回地址详细信息

#### 4.4 更新地址
```bash
curl -X PUT http://localhost:8085/api/mall/addresses/3 \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 2" \
  -d '{
    "receiverName": "王五",
    "receiverPhone": "13700137000",
    "province": "河北省",
    "city": "石家庄市",
    "district": "桥西区",
    "detail": "裕华路999号",
    "isDefault": false
  }'
```

**预期结果**:
- ✅ 地址更新成功

#### 4.5 设置默认地址
```bash
curl -X PUT http://localhost:8085/api/mall/addresses/3/default \
  -H "X-User-Id: 2"
```

**预期结果**:
- ✅ 地址 3 设为默认
- ✅ 其他地址的 isDefault 变为 false

#### 4.6 获取默认地址
```bash
curl -X GET http://localhost:8085/api/mall/addresses/default \
  -H "X-User-Id: 2"
```

**预期结果**:
- ✅ 返回默认地址信息

#### 4.7 删除地址
```bash
curl -X DELETE http://localhost:8085/api/mall/addresses/3 \
  -H "X-User-Id: 2"
```

**预期结果**:
- ✅ 地址删除成功
- ✅ 如果删除的是默认地址，自动设置新的默认地址

---

### 5. 业务逻辑验证

#### 5.1 购物车业务逻辑

**测试场景 1: 添加相同商品**
1. 添加商品 1，数量 2
2. 再次添加商品 1，数量 3
3. 查询购物车

**预期结果**:
- ✅ 购物车中只有一条记录
- ✅ 数量为 5（2+3）

**测试场景 2: 库存验证**
1. 查询商品 1 的库存（假设为 10）
2. 添加商品 1，数量 15

**预期结果**:
- ✅ 返回错误："库存不足"

**测试场景 3: 商品信息填充**
1. 添加商品到购物车
2. 查询购物车列表

**预期结果**:
- ✅ 返回的购物车项包含完整的商品信息
- ✅ product 字段不为空

#### 5.2 地址业务逻辑

**测试场景 1: 首个地址自动设为默认**
1. 删除用户的所有地址
2. 添加第一个地址（isDefault 不设置或设为 false）

**预期结果**:
- ✅ 该地址自动设为默认（isDefault = true）

**测试场景 2: 设置默认地址**
1. 用户有 3 个地址，地址 1 是默认
2. 设置地址 2 为默认

**预期结果**:
- ✅ 地址 2 变为默认
- ✅ 地址 1 的 isDefault 变为 false

**测试场景 3: 删除默认地址**
1. 用户有 3 个地址，地址 1 是默认
2. 删除地址 1

**预期结果**:
- ✅ 地址 1 被删除
- ✅ 自动将另一个地址设为默认

**测试场景 4: 地址排序**
1. 查询地址列表

**预期结果**:
- ✅ 默认地址排在最前面
- ✅ 其他地址按创建时间倒序排列

---

### 6. 前端 API 验证

#### 6.1 检查 API 文件
```bash
# 查看 mall.ts 文件
cat frontend/src/api/mall.ts
```

**预期结果**:
- ✅ 包含购物车相关方法（7个）
- ✅ 包含地址相关方法（7个）
- ✅ 方法签名正确

---

### 7. 集成测试

#### 7.1 完整购物流程
1. 浏览商品列表
2. 添加商品到购物车
3. 查看购物车
4. 修改商品数量
5. 选择商品
6. 查看地址列表
7. 选择/添加收货地址
8. 创建订单

**预期结果**:
- ✅ 整个流程顺畅
- ✅ 数据一致性正确

---

## ✅ 验证结果

### 数据库
- [ ] 表结构正确
- [ ] 测试数据存在
- [ ] 索引创建成功

### 后端服务
- [ ] 编译成功
- [ ] 启动成功
- [ ] 无错误日志

### 购物车 API
- [ ] 添加到购物车
- [ ] 获取购物车列表
- [ ] 更新购物车项
- [ ] 删除购物车项
- [ ] 批量删除
- [ ] 全选/取消全选
- [ ] 清空购物车

### 地址 API
- [ ] 获取地址列表
- [ ] 添加地址
- [ ] 获取地址详情
- [ ] 更新地址
- [ ] 删除地址
- [ ] 设置默认地址
- [ ] 获取默认地址

### 业务逻辑
- [ ] 购物车合并逻辑
- [ ] 库存验证
- [ ] 商品信息填充
- [ ] 首个地址自动默认
- [ ] 默认地址切换
- [ ] 删除默认地址处理
- [ ] 地址排序

### 前端 API
- [ ] API 方法完整
- [ ] 方法签名正确

---

## 🐛 问题记录

| 问题描述 | 严重程度 | 状态 | 解决方案 |
|---------|---------|------|---------|
| - | - | - | - |

---

## 📝 备注

- 测试用户 ID: 2 (ruler)
- 测试商品 ID: 1-20
- MySQL 端口: 3306
- Mall Service 端口: 8085

---

**验证时间**: ___________  
**验证人**: ___________  
**验证结果**: [ ] 通过 / [ ] 未通过

