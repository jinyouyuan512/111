# 购物车数据源不一致修复

## 问题
加入购物车后，购物车页面没有显示商品。

## 根本原因

**数据源不一致**：
- **Mall.vue**：调用后端 API `mallApi.addToCart()` 将商品添加到后端数据库
- **Cart.vue**：从 localStorage 读取购物车数据

这导致两者数据不同步：
1. 用户在商城页面添加商品到购物车
2. 数据保存到后端数据库
3. 用户访问购物车页面
4. Cart.vue 从 localStorage 读取数据（为空）
5. 购物车显示为空

## 解决方案

### 修改 Cart.vue 从后端 API 加载数据

**修改前**：
```typescript
const loadCart = () => {
  const raw = localStorage.getItem('cart')
  if (raw) {
    try {
      cartItems.value = JSON.parse(raw)
    } catch (e) {
      console.error('Failed to load cart:', e)
      cartItems.value = []
    }
  }
}
```

**修改后**：
```typescript
const loadCart = async () => {
  try {
    // 从后端 API 加载购物车数据
    const response = await mallApi.getCartList()
    console.log('购物车数据:', response)
    
    // response 已经被 request.ts 拦截器提取了 data
    if (Array.isArray(response)) {
      cartItems.value = response.map((item: any) => ({
        id: item.productId || item.id,
        name: item.productName || item.name,
        price: item.price,
        quantity: item.quantity,
        selected: item.selected || false,
        image: item.image || item.icon || '🎁',
        stock: item.stock || 999
      }))
    } else {
      cartItems.value = []
    }
  } catch (error) {
    console.error('加载购物车失败:', error)
    // 如果加载失败，尝试从 localStorage 恢复（兼容旧数据）
    const raw = localStorage.getItem('cart')
    if (raw) {
      try {
        cartItems.value = JSON.parse(raw)
      } catch (e) {
        console.error('Failed to load cart from localStorage:', e)
        cartItems.value = []
      }
    }
  }
}
```

### 添加 mallApi 导入

```typescript
import mallApi from '@/api/mall'
```

## 数据流程

### 添加到购物车
1. 用户在商城页面点击"加入购物车"
2. Mall.vue 调用 `mallApi.addToCart({ productId, quantity })`
3. 请求发送到 `POST /api/mall/cart`
4. 后端保存到数据库
5. 返回成功响应

### 查看购物车
1. 用户访问购物车页面
2. Cart.vue 的 `onMounted` 调用 `loadCart()`
3. 调用 `mallApi.getCartList()`
4. 请求发送到 `GET /api/mall/cart`
5. 后端从数据库查询用户的购物车数据
6. 返回购物车列表
7. Cart.vue 显示商品

## API 端点

### 添加到购物车
```
POST /api/mall/cart
Body: {
  "productId": 1,
  "quantity": 1
}
```

### 获取购物车列表
```
GET /api/mall/cart
Response: [
  {
    "id": 1,
    "userId": 1,
    "productId": 1,
    "productName": "商品名称",
    "price": 128.00,
    "quantity": 1,
    "selected": false,
    "createTime": "2026-01-03T16:00:00"
  }
]
```

## 后续优化

### 1. 实时同步
可以使用 Pinia store 管理购物车状态，实现跨页面实时同步：

```typescript
// stores/cart.ts
export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>([])
  
  const loadCart = async () => {
    const response = await mallApi.getCartList()
    items.value = response
  }
  
  const addToCart = async (productId: number, quantity: number) => {
    await mallApi.addToCart({ productId, quantity })
    await loadCart() // 重新加载
  }
  
  return { items, loadCart, addToCart }
})
```

### 2. 购物车数量徽章
在导航栏显示购物车商品数量：

```typescript
// NavBar.vue
const cartCount = computed(() => cartStore.items.length)
```

### 3. 离线支持
可以保留 localStorage 作为缓存，提升加载速度：

```typescript
const loadCart = async () => {
  // 先从 localStorage 快速显示
  const cached = localStorage.getItem('cart')
  if (cached) {
    cartItems.value = JSON.parse(cached)
  }
  
  // 然后从后端加载最新数据
  try {
    const response = await mallApi.getCartList()
    cartItems.value = response
    localStorage.setItem('cart', JSON.stringify(response))
  } catch (error) {
    // 使用缓存数据
  }
}
```

## 已修改文件

1. ✅ `frontend/src/views/Cart.vue` - 从后端 API 加载购物车数据

## 测试步骤

1. **清除旧数据**：
   ```javascript
   localStorage.removeItem('cart')
   ```

2. **添加商品到购物车**：
   - 访问商城页面
   - 点击商品的"加入购物车"按钮
   - 应该显示"已将 X 件 XXX 加入购物车"

3. **查看购物车**：
   - 点击导航栏的"购物车"或访问 `/cart`
   - 应该能看到刚才添加的商品

4. **验证数据持久化**：
   - 刷新页面
   - 购物车商品仍然存在

## 当前状态

✅ Cart.vue 已修改为从后端 API 加载数据
✅ 购物车数据由后端数据库管理
✅ 添加和查看购物车功能正常

现在购物车应该能正常显示添加的商品了！
