# 订单页面API调用错误修复

## 问题描述

订单页面出现多个API调用错误：

### 错误1：getOrders is not a function
```
TypeError: mallApi.getOrders is not a function
at loadOrders (Orders.vue:160:36)
```

### 错误2：取消订单500错误
```
AxiosError {message: 'Request failed with status code 500', ...}
at cancelOrder (Orders.vue:310)
```

### 错误3：确认收货500错误
```
AxiosError {message: 'Request failed with status code 500', ...}
at confirmReceipt (Orders.vue:349)
```

## 问题原因

### 1. API方法名不匹配
**Orders.vue 调用：**
```typescript
const response = await mallApi.getOrders(1, 100)
```

**mall.ts 实际定义：**
```typescript
getOrderList(params: {
  page?: number
  size?: number
  userId?: number
  status?: string
}) {
  return request.get('/mall/orders', { params })
}
```

**问题：** 方法名不一致（`getOrders` vs `getOrderList`）

### 2. 后端API可能未实现或返回500错误
- 取消订单API返回500
- 确认收货API返回500

## 解决方案

### 修复1：更正API方法名

**修改前：**
```typescript
const response = await mallApi.getOrders(1, 100)
```

**修改后：**
```typescript
const response = await mallApi.getOrderList({ page: 1, size: 100 })
```

### 修复2：添加错误处理和本地降级

Orders.vue已经有本地降级逻辑：
```typescript
try {
  loading.value = true
  const response = await mallApi.getOrderList({ page: 1, size: 100 })
  // ... 处理响应
} catch (error: any) {
  console.error('加载订单失败:', error)
  // 如果API失败，尝试从localStorage加载
  loadOrdersFromLocal()
} finally {
  loading.value = false
}
```

## API方法对照表

| Orders.vue 调用 | mall.ts 实际方法 | 状态 |
|----------------|-----------------|------|
| `mallApi.getOrders()` | `mallApi.getOrderList()` | ✅ 已修复 |
| `mallApi.cancelOrder()` | `mallApi.cancelOrder()` | ✅ 正确 |
| `mallApi.confirmOrder()` | `mallApi.confirmOrder()` | ✅ 正确 |
| `mallApi.updateOrderStatus()` | `mallApi.updateOrderStatus()` | ✅ 正确 |

## 后端API检查

### 需要确认的后端接口

#### 1. 获取订单列表
```
GET /mall/orders?page=1&size=100
```

#### 2. 取消订单
```
DELETE /mall/orders/{id}
```

#### 3. 确认收货
```
POST /mall/orders/{id}/confirm
```

#### 4. 更新订单状态
```
PUT /mall/orders/{id}/status?status=SHIPPED
```

### 检查后端服务是否运行

```bash
# 检查mall-service是否运行
curl http://localhost:8084/mall/orders

# 或者查看后端日志
cd backend/mall-service
# 查看日志文件
```

## 测试步骤

### 1. 测试订单列表加载

```bash
# 访问订单页面
http://localhost:5173/orders

# 检查控制台
# 应该看到：
# - 如果后端正常：显示订单列表
# - 如果后端异常：显示"您还没有订单"（从localStorage加载）
```

### 2. 测试取消订单

```bash
# 1. 创建一个待支付订单
# 2. 点击"取消订单"按钮
# 3. 确认取消

# 预期结果：
# - 如果后端正常：订单状态变为"已取消"
# - 如果后端异常：显示错误提示
```

### 3. 测试确认收货

```bash
# 1. 创建一个已发货订单
# 2. 点击"确认收货"按钮

# 预期结果：
# - 如果后端正常：订单状态变为"已完成"
# - 如果后端异常：显示错误提示
```

## 本地降级方案

Orders.vue已实现本地降级：

### 从localStorage加载订单
```typescript
const loadOrdersFromLocal = () => {
  const ordersRaw = localStorage.getItem('orders')
  if (ordersRaw) {
    try {
      orders.value = JSON.parse(ordersRaw)
    } catch (e) {
      console.error('Failed to parse orders', e)
      orders.value = []
    }
  }
}
```

### 保存订单到localStorage
```typescript
const saveOrdersToLocal = () => {
  localStorage.setItem('orders', JSON.stringify(orders.value))
}
```

## 后续优化建议

### 1. 统一API方法命名
建议在mall.ts中添加别名：
```typescript
export const mallApi = {
  // ... 其他方法
  
  // 添加别名以保持向后兼容
  getOrders: function(page: number, size: number) {
    return this.getOrderList({ page, size })
  }
}
```

### 2. 增强错误处理
```typescript
const loadOrders = async () => {
  try {
    loading.value = true
    const response = await mallApi.getOrderList({ page: 1, size: 100 })
    
    if (response && response.records) {
      orders.value = response.records.map(/* ... */)
    } else {
      throw new Error('Invalid response format')
    }
  } catch (error: any) {
    console.error('加载订单失败:', error)
    
    // 显示友好的错误提示
    if (error.response?.status === 500) {
      ElMessage.warning('订单服务暂时不可用，显示本地缓存数据')
    } else if (error.response?.status === 401) {
      ElMessage.error('请先登录')
      router.push('/login')
      return
    }
    
    // 降级到本地数据
    loadOrdersFromLocal()
  } finally {
    loading.value = false
  }
}
```

### 3. 添加重试机制
```typescript
const loadOrdersWithRetry = async (retries = 3) => {
  for (let i = 0; i < retries; i++) {
    try {
      return await mallApi.getOrderList({ page: 1, size: 100 })
    } catch (error) {
      if (i === retries - 1) throw error
      await new Promise(resolve => setTimeout(resolve, 1000 * (i + 1)))
    }
  }
}
```

### 4. 检查后端服务状态
```typescript
const checkMallService = async () => {
  try {
    await request.get('/mall/health')
    return true
  } catch {
    return false
  }
}

onMounted(async () => {
  const isServiceAvailable = await checkMallService()
  if (!isServiceAvailable) {
    ElMessage.warning('商城服务暂时不可用，部分功能可能受限')
  }
  loadOrders()
})
```

## 总结

### 已修复
✅ API方法名不匹配问题（`getOrders` → `getOrderList`）

### 需要检查
⚠️ 后端mall-service是否正常运行
⚠️ 后端API是否返回正确的数据格式
⚠️ 取消订单和确认收货接口是否实现

### 降级方案
✅ 已实现localStorage本地降级
✅ 已有错误处理和友好提示

---

**修复时间：** 2025-01-04 16:45  
**状态：** ✅ 前端已修复，需要检查后端服务  
**影响范围：** 订单页面（/orders）
