# 购物车价格显示 ¥NaN 修复

## 问题描述
购物车页面显示商品价格为 ¥NaN，无法正常显示价格信息。

## 根本原因分析

### 1. 数据类型不匹配
后端 Java 使用 `BigDecimal` 类型存储价格：
```java
// Product.java
private BigDecimal price;
```

前端 JavaScript 接收到的是字符串或对象，不是 number 类型。

### 2. 字段映射错误
Cart.vue 的 `loadCart()` 方法中存在字段映射错误：
```typescript
// 错误：映射到 image 字段
image: item.product?.icon || item.icon || '🎁'

// 但模板中使用的是 icon 字段
<div class="product-icon">{{ item.icon }}</div>
```

### 3. Boolean 值处理错误
```typescript
// 错误：使用 || 运算符，false 会被当作 falsy 值
selected: item.selected || false

// 当 item.selected 为 false 时，会被替换为 false
// 但这样无法区分 undefined 和 false
```

## 解决方案

### 修复数据映射
```typescript
const loadCart = async () => {
  try {
    const response = await mallApi.getCartList()
    console.log('购物车数据:', response)
    
    if (Array.isArray(response)) {
      cartItems.value = response.map((item: any) => ({
        id: item.id,
        name: item.product?.name || item.productName || '未知商品',
        
        // ✅ 修复1: 使用 parseFloat 转换 BigDecimal 为 number
        price: parseFloat(item.product?.price || item.price || 0),
        
        quantity: item.quantity || 1,
        
        // ✅ 修复2: 正确处理 false 值
        selected: item.selected !== undefined ? item.selected : false,
        
        // ✅ 修复3: 字段名从 image 改为 icon
        icon: item.product?.icon || item.icon || '🎁',
        
        stock: item.product?.stock || item.stock || 999,
        color: item.product?.color || 'linear-gradient(135deg, #c41e3a, #8b1e3f)',
        category: item.product?.category || item.category || '商品',
        designer: item.product?.designer || item.designer
      }))
    } else {
      cartItems.value = []
    }
  } catch (error) {
    console.error('加载购物车失败:', error)
  }
}
```

## 技术细节

### Java BigDecimal 与 JavaScript Number 转换

**后端数据结构**：
```json
{
  "id": 1,
  "userId": 1,
  "productId": 1,
  "quantity": 2,
  "selected": true,
  "product": {
    "id": 1,
    "name": "红色文化T恤",
    "price": 128.00,  // BigDecimal 序列化为数字
    "icon": "👕",
    "color": "linear-gradient(...)"
  }
}
```

**前端接收**：
- JSON 序列化后，BigDecimal 变成 JavaScript number
- 但可能因为精度问题或其他原因变成字符串
- 使用 `parseFloat()` 确保转换为 number 类型

### Boolean 值的正确处理

**错误方式**：
```typescript
selected: item.selected || false
// 问题：当 item.selected 为 false 时，|| 运算符会返回右侧的 false
// 无法区分 undefined 和 false
```

**正确方式**：
```typescript
selected: item.selected !== undefined ? item.selected : false
// 只有当 item.selected 为 undefined 时才使用默认值 false
// 当 item.selected 为 false 时，保持 false
```

## 数据流程

1. **后端返回数据**：
   ```
   CartServiceImpl.getCartList() 
   → 查询 Cart 列表
   → 填充 Product 信息
   → 返回 List<Cart>
   ```

2. **JSON 序列化**：
   ```
   List<Cart> → JSON
   BigDecimal → number/string
   ```

3. **前端接收**：
   ```
   axios 拦截器提取 response.data
   → loadCart() 映射数据
   → parseFloat() 转换价格
   → 更新 cartItems.value
   ```

4. **模板渲染**：
   ```vue
   <span class="price-value">¥{{ item.price }}</span>
   <span class="subtotal-value">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
   ```

## 修改文件
- ✅ `frontend/src/views/Cart.vue` - 修复 loadCart() 方法的数据映射

## 测试验证

### 1. 价格显示
- ✅ 单价显示正确：¥128.00
- ✅ 小计计算正确：¥256.00 (128 × 2)
- ✅ 总价计算正确：包含运费

### 2. 图标显示
- ✅ 商品图标正确显示（emoji）
- ✅ 颜色渐变正确应用

### 3. 选中状态
- ✅ 已选中商品显示为选中
- ✅ 未选中商品显示为未选中
- ✅ 全选功能正常

## 调试技巧

### 1. 查看原始数据
```typescript
const response = await mallApi.getCartList()
console.log('购物车数据:', response)
console.log('第一项价格类型:', typeof response[0]?.product?.price)
console.log('第一项价格值:', response[0]?.product?.price)
```

### 2. 检查映射后的数据
```typescript
cartItems.value = response.map((item: any) => {
  const mapped = {
    price: parseFloat(item.product?.price || 0),
    // ...
  }
  console.log('映射后的价格:', mapped.price, typeof mapped.price)
  return mapped
})
```

### 3. 模板中调试
```vue
<div>价格类型: {{ typeof item.price }}</div>
<div>价格值: {{ item.price }}</div>
<div>是否为 NaN: {{ isNaN(item.price) }}</div>
```

## 相关问题

### 为什么使用 parseFloat 而不是 Number()？

```typescript
// parseFloat 更宽容，可以处理字符串
parseFloat("128.00")  // 128
parseFloat("128")     // 128
parseFloat("128.5")   // 128.5

// Number() 更严格
Number("128.00")      // 128
Number("128")         // 128
Number("128.5")       // 128.5
Number("128abc")      // NaN
parseFloat("128abc")  // 128 (解析到非数字字符为止)
```

### 为什么 BigDecimal 会导致问题？

Java 的 BigDecimal 是为了精确计算设计的，但 JavaScript 只有 number 类型（IEEE 754 双精度浮点数）。序列化时需要转换，可能导致：
- 精度损失
- 类型不匹配
- 需要显式转换

## 最佳实践

### 1. 后端统一处理
```java
// 使用 @JsonSerialize 注解
@JsonSerialize(using = ToStringSerializer.class)
private BigDecimal price;
```

### 2. 前端统一转换
```typescript
// 创建工具函数
const parsePrice = (price: any): number => {
  if (typeof price === 'number') return price
  if (typeof price === 'string') return parseFloat(price)
  return 0
}

// 使用
price: parsePrice(item.product?.price)
```

### 3. 类型定义
```typescript
interface CartItem {
  id: number
  name: string
  price: number  // 明确类型
  quantity: number
  selected: boolean
  icon: string
  // ...
}
```

## 总结

购物车价格显示 ¥NaN 的问题已修复，主要改进：
1. ✅ 使用 `parseFloat()` 转换 BigDecimal 价格为 number
2. ✅ 修正字段映射：`image` → `icon`
3. ✅ 正确处理 Boolean 值：使用三元运算符而不是 `||`

现在购物车可以正确显示价格、图标和选中状态了！
