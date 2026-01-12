# 众创空间作品商品化功能

## 功能概述

设计师在众创空间发布的优秀作品，可以申请上架到文创商城进行销售，实现创作变现。

## 业务流程

```
设计师上传作品 → 申请商品化 → 管理员审核 → 审核通过 → 自动创建商品 → 商城上架
```

### 详细流程

1. **设计师上传作品**
   - 在众创空间上传设计作品
   - 填写作品信息（标题、描述、设计理念等）
   - 作品状态：`pending`（待审核）

2. **作品审核**
   - 管理员审核作品质量
   - 审核通过：`status = 'approved'`
   - 审核拒绝：`status = 'rejected'`

3. **申请商品化**
   - 设计师在已审核通过的作品上点击"申请上架"
   - 填写商品信息：
     - 建议售价
     - 商品描述
     - 商品分类
   - 提交申请：`product_status = 'pending'`

4. **商品化审核**
   - 管理员在后台审核商品化申请
   - 检查：
     - 价格是否合理
     - 描述是否完整
     - 是否适合商品化
   - 审核结果：
     - 通过：`product_status = 'approved'`
     - 拒绝：`product_status = 'rejected'`

5. **自动创建商品**
   - 审核通过后，系统自动：
     - 在 `jiyi_mall.product` 表创建商品记录
     - 关联作品ID：`product_id`
     - 设置商品信息：
       - 名称：作品标题
       - 描述：商品描述
       - 价格：建议售价
       - 分类：设计师推荐
       - 设计师：作品作者
     - 商品状态：`product_status = 'published'`

6. **商城展示**
   - 商品在文创商城展示
   - 标注"设计师原创"
   - 显示设计师信息
   - 销售分成给设计师

## 数据库设计

### design 表新增字段

```sql
ALTER TABLE `design` 
ADD COLUMN `product_status` VARCHAR(20) DEFAULT 'none' COMMENT '商品化状态',
ADD COLUMN `product_id` BIGINT COMMENT '关联的商品ID',
ADD COLUMN `product_price` DECIMAL(10,2) COMMENT '建议售价',
ADD COLUMN `product_description` TEXT COMMENT '商品描述',
ADD COLUMN `product_apply_time` DATETIME COMMENT '申请上架时间',
ADD COLUMN `product_approve_time` DATETIME COMMENT '审核通过时间',
ADD COLUMN `product_reject_reason` TEXT COMMENT '拒绝上架原因';
```

### 商品化状态

- `none`: 未申请商品化
- `pending`: 待审核
- `approved`: 审核通过
- `rejected`: 审核拒绝
- `published`: 已上架商城

## API 接口

### 1. 申请商品化
```
POST /api/creative/designs/{id}/apply-product
```

请求体：
```json
{
  "price": 128.00,
  "description": "这是一款精美的红色文创产品...",
  "category": "设计师推荐"
}
```

### 2. 获取商品化申请列表（管理员）
```
GET /api/creative/designs/product-applications
```

参数：
- `status`: pending, approved, rejected
- `page`: 页码
- `size`: 每页数量

### 3. 审核商品化申请（管理员）
```
PUT /api/creative/designs/{id}/approve-product
```

请求体：
```json
{
  "approved": true,
  "reason": "作品优秀，适合商品化"
}
```

### 4. 创建商品（自动调用）
```
POST /api/mall/products/from-design
```

请求体：
```json
{
  "designId": 123,
  "name": "作品标题",
  "description": "商品描述",
  "price": 128.00,
  "category": "设计师推荐",
  "designer": "设计师名称",
  "coverImage": "作品封面"
}
```

## 前端页面

### 1. 众创空间 - 作品详情页
添加"申请上架"按钮：
- 仅作品作者可见
- 仅 `status='approved'` 的作品可申请
- 点击后弹出申请表单

### 2. 管理后台 - 商品化审核
新增管理页面：`/admin/product-applications`
- 显示所有商品化申请
- 筛选：待审核、已通过、已拒绝
- 操作：查看详情、通过、拒绝

### 3. 商城 - 设计师作品专区
- 标注"设计师原创"标签
- 显示设计师信息
- 链接到原作品页面

## 收益分成

### 分成比例（建议）
- 设计师：70%
- 平台：30%

### 结算方式
- 每月结算一次
- 记录在 `reward_record` 表
- 类型：`royalty`（版税）

## 实现优先级

### 高优先级（MVP）
1. ✅ 数据库字段添加
2. ⏳ 申请商品化 API
3. ⏳ 管理员审核 API
4. ⏳ 自动创建商品逻辑
5. ⏳ 前端申请表单
6. ⏳ 管理后台审核页面

### 中优先级
1. 商品与作品关联展示
2. 设计师收益统计
3. 销售数据反馈
4. 自动分成结算

### 低优先级
1. 设计师作品专区
2. 热门设计师排行
3. 作品推荐算法
4. 批量上架功能

## 使用示例

### 设计师操作流程

1. **上传作品**
   ```
   访问：/creative
   点击：上传作品
   填写：作品信息
   提交：等待审核
   ```

2. **申请上架**
   ```
   作品审核通过后
   点击：申请上架
   填写：
     - 建议售价：128元
     - 商品描述：详细介绍
   提交申请
   ```

3. **等待审核**
   ```
   管理员审核商品化申请
   审核通过后自动上架
   ```

4. **查看销售**
   ```
   访问：个人中心
   查看：销售数据
   查看：收益统计
   ```

### 管理员操作流程

1. **审核作品**
   ```
   访问：/admin/content
   查看：待审核作品
   操作：通过/拒绝
   ```

2. **审核商品化申请**
   ```
   访问：/admin/product-applications
   查看：待审核申请
   检查：价格、描述、质量
   操作：通过/拒绝
   ```

3. **管理商品**
   ```
   访问：/admin/products
   查看：所有商品
   筛选：设计师作品
   操作：编辑、下架
   ```

## 注意事项

1. **版权保护**
   - 设计师需声明原创
   - 平台审核版权
   - 侵权责任追究

2. **质量控制**
   - 严格审核作品质量
   - 确保商品化可行性
   - 维护商城品质

3. **价格管理**
   - 建议合理定价
   - 平台可调整价格
   - 保护消费者权益

4. **收益结算**
   - 及时结算收益
   - 透明分成比例
   - 提供收益报表

## 技术实现

### 后端服务
- Creative Service：作品管理、申请处理
- Mall Service：商品创建、销售管理
- 服务间通信：REST API 或消息队列

### 数据同步
- 作品信息 → 商品信息
- 销售数据 → 收益统计
- 实时或定时同步

### 事务处理
- 审核通过 + 创建商品：原子操作
- 失败回滚机制
- 状态一致性保证

---

**创建时间**: 2026-01-02
**状态**: 📋 设计完成，待实现
**优先级**: ⭐⭐⭐ 高
