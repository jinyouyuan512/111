# 商城功能完善计划

## 当前状态分析

### ✅ 已完成功能
1. 商品列表展示
2. 商品搜索和筛选
3. 商品详情查看
4. 购物车基础界面
5. 订单基础界面
6. 后端商品 CRUD API
7. 后端订单 CRUD API

### ⏳ 待完善功能

#### 1. 购物车功能
- [ ] 添加到购物车（后端API）
- [ ] 购物车数据持久化
- [ ] 购物车数量更新
- [ ] 购物车商品删除
- [ ] 购物车结算

#### 2. 订单功能
- [ ] 创建订单
- [ ] 订单支付
- [ ] 订单状态更新
- [ ] 订单详情查看
- [ ] 订单取消
- [ ] 订单评价

#### 3. 收藏功能
- [ ] 添加收藏
- [ ] 取消收藏
- [ ] 收藏列表

#### 4. 地址管理
- [ ] 添加收货地址
- [ ] 编辑收货地址
- [ ] 删除收货地址
- [ ] 设置默认地址

#### 5. 支付功能
- [ ] 支付方式选择
- [ ] 模拟支付流程
- [ ] 支付成功回调

#### 6. 评价功能
- [ ] 商品评价
- [ ] 评价列表
- [ ] 评价点赞

## 实现优先级

### P0 - 核心功能（必须完成）
1. **购物车持久化**
   - 数据库表：`cart`
   - API：添加、删除、更新数量
   - 前端：与后端集成

2. **订单创建流程**
   - 选择地址
   - 确认商品
   - 创建订单
   - 跳转支付

3. **订单支付**
   - 模拟支付
   - 更新订单状态
   - 减少库存

4. **订单管理**
   - 订单列表
   - 订单详情
   - 订单状态流转

### P1 - 重要功能（建议完成）
1. **地址管理**
   - 地址CRUD
   - 默认地址

2. **收藏功能**
   - 收藏商品
   - 收藏列表

3. **商品评价**
   - 发表评价
   - 查看评价

### P2 - 增强功能（可选）
1. 优惠券系统
2. 积分系统
3. 限时秒杀
4. 拼团功能

## 数据库设计

### 1. 购物车表
```sql
CREATE TABLE `cart` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `quantity` INT NOT NULL DEFAULT 1,
    `selected` TINYINT NOT NULL DEFAULT 1,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    KEY `idx_user_id` (`user_id`)
);
```

### 2. 收货地址表
```sql
CREATE TABLE `address` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `receiver_name` VARCHAR(50) NOT NULL,
    `receiver_phone` VARCHAR(20) NOT NULL,
    `province` VARCHAR(50) NOT NULL,
    `city` VARCHAR(50) NOT NULL,
    `district` VARCHAR(50) NOT NULL,
    `detail` VARCHAR(200) NOT NULL,
    `is_default` TINYINT NOT NULL DEFAULT 0,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
);
```

### 3. 收藏表
```sql
CREATE TABLE `favorite` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    KEY `idx_user_id` (`user_id`)
);
```

### 4. 商品评价表
```sql
CREATE TABLE `review` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `order_id` BIGINT NOT NULL,
    `rating` INT NOT NULL,
    `content` TEXT,
    `images` TEXT,
    `likes` INT NOT NULL DEFAULT 0,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_order_id` (`order_id`)
);
```

## API 接口设计

### 购物车 API
```
POST   /api/mall/cart              添加到购物车
GET    /api/mall/cart              获取购物车列表
PUT    /api/mall/cart/{id}         更新购物车项
DELETE /api/mall/cart/{id}         删除购物车项
DELETE /api/mall/cart/batch        批量删除
PUT    /api/mall/cart/select-all   全选/取消全选
```

### 地址 API
```
POST   /api/mall/addresses         添加地址
GET    /api/mall/addresses         获取地址列表
PUT    /api/mall/addresses/{id}    更新地址
DELETE /api/mall/addresses/{id}    删除地址
PUT    /api/mall/addresses/{id}/default  设置默认地址
```

### 收藏 API
```
POST   /api/mall/favorites         添加收藏
GET    /api/mall/favorites         获取收藏列表
DELETE /api/mall/favorites/{id}    取消收藏
```

### 评价 API
```
POST   /api/mall/reviews           发表评价
GET    /api/mall/reviews           获取评价列表
PUT    /api/mall/reviews/{id}/like 点赞评价
```

### 订单 API（补充）
```
POST   /api/mall/orders/{id}/pay      支付订单
POST   /api/mall/orders/{id}/cancel   取消订单
POST   /api/mall/orders/{id}/confirm  确认收货
POST   /api/mall/orders/{id}/refund   申请退款
```

## 实现步骤

### 第一阶段：核心购物流程
1. 创建数据库表
2. 实现购物车后端API
3. 实现地址管理后端API
4. 完善订单创建流程
5. 实现支付功能
6. 前端集成测试

### 第二阶段：增强功能
1. 实现收藏功能
2. 实现评价功能
3. 优化用户体验
4. 性能优化

### 第三阶段：高级功能
1. 优惠券系统
2. 积分系统
3. 营销活动
4. 数据统计

## 前端页面补充

### 1. 结算页面 (`/checkout`)
- 选择收货地址
- 确认商品信息
- 选择支付方式
- 使用优惠券
- 订单备注
- 提交订单

### 2. 支付页面 (`/payment`)
- 订单信息展示
- 支付方式选择
- 支付倒计时
- 支付结果

### 3. 订单详情页 (`/orders/:id`)
- 订单状态
- 商品信息
- 物流信息
- 操作按钮（取消、确认收货、评价）

### 4. 地址管理页 (`/addresses`)
- 地址列表
- 添加地址
- 编辑地址
- 删除地址
- 设置默认

### 5. 收藏列表页 (`/favorites`)
- 收藏商品列表
- 批量操作
- 加入购物车

## 测试用例

### 购物车测试
- [ ] 添加商品到购物车
- [ ] 更新商品数量
- [ ] 删除购物车商品
- [ ] 全选/取消全选
- [ ] 结算选中商品

### 订单测试
- [ ] 创建订单
- [ ] 支付订单
- [ ] 取消订单
- [ ] 确认收货
- [ ] 申请退款

### 地址测试
- [ ] 添加地址
- [ ] 编辑地址
- [ ] 删除地址
- [ ] 设置默认地址

## 性能优化

1. **缓存策略**
   - Redis 缓存商品信息
   - 购物车数据缓存
   - 热门商品缓存

2. **数据库优化**
   - 添加索引
   - 查询优化
   - 分页优化

3. **前端优化**
   - 图片懒加载
   - 虚拟滚动
   - 防抖节流

## 安全考虑

1. **库存控制**
   - 乐观锁防止超卖
   - 库存预扣
   - 定时释放

2. **订单安全**
   - 价格校验
   - 重复提交防护
   - 订单状态机

3. **支付安全**
   - 签名验证
   - 回调验证
   - 金额校验

---

**创建时间**: 2026-01-02
**预计完成时间**: 根据优先级分阶段实施
**负责人**: 开发团队
