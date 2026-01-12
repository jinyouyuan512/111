# 购物车前后端同步修复

## 问题
购物车的删除、修改数量等操作只在前端生效，刷新后又恢复了。

## 根本原因
Cart.vue 的所有操作（删除、修改数量、选中状态）都只修改了前端的 `cartItems` 数组，没有调用后端API同步到数据库。

## 解决方案

### 1. 删除商品 - 调用后端API

**修改前**：
```typescript
const removeItem = (item: CartItem) => {
  ElMessageBox.confirm(...).then(() => {
    const index = cartItems.value.findIndex(i => i.id === item.id)
    if (index > -1) {
      cartItems.value.splice(index, 1)  // 只修改前端
      saveCart()  // 保存到 localStorage
    }
  })
}
```

**修改后**：
```typescript
const removeItem = async (item: CartItem) => {
  ElMessageBox.confirm(...).then(async () => {
    try {
      // 调用后端API删除
      await mallApi.deleteCartItem(item.id)
      
      // 从前端数组中移除
      const index = cartItems.value.findIndex(i => i.id === item.id)
      if (index > -1) {
        cartItems.value.splice(index, 1)
      }
      
      ElMessage.success('已删除')
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  })
}
```

### 2. 修改数量 - 调用后端API

**修改前**：
```typescript
const handleQuantityChange = () => {
  saveCart()  // 只保存到 localStorage
}
```

**修改后**：
```typescript
const handleQuantityChange = async (item: CartItem, value: number) => {
  try {
    // 调用后端API更新数量
    await mallApi.updateCartItem(item.id, { quantity: value })
  } catch (error) {
    console.error('更新数量失败:', error)
    ElMessage.error('更新数量失败')
    // 重新加载购物车数据
    await loadCart()
  }
}
```

**模板修改**：
```vue
<el-input-number 
  v-model="item.quantity" 
  @change="(value) => handleQuantityChange(item, value)"
/>
```

### 3. 选中状态 - 调用后端API

**单个商品选中**：
```typescript
const handleItemSelect = async (item: CartItem) => {
  try {
    // 调用后端API更新选中状态
    await mallApi.updateCartItem(item.id, { selected: item.selected })
    
    // 更新全选状态
    const allSelected = cartItems.value.every(item => item.selected)
    if (allSelected) {
      selectAll.value = true
    }
  } catch (error) {
    console.error('更新选中状态失败:', error)
    await loadCart()
  }
}
```

**全选/取消全选**：
```typescript
const handleSelectAll = async (value: boolean) => {
  try {
    // 调用后端API全选/取消全选
    await mallApi.selectAll(value)
    
    // 更新前端状态
    cartItems.value.forEach(item => {
      item.selected = value
    })
  } catch (error) {
    console.error('全选操作失败:', error)
    await loadCart()
  }
}
```

### 4. 移除 localStorage 依赖

**修改前**：
```typescript
const saveCart = () => {
  localStorage.setItem('cart', JSON.stringify(cartItems.value))
}
```

**修改后**：
```typescript
const saveCart = () => {
  // 不再使用 localStorage，购物车数据由后端管理
}
```

## 数据流程

### 删除商品
1. 用户点击"删除"按钮
2. 弹出确认对话框
3. 用户确认后，调用 `mallApi.deleteCartItem(id)`
4. 请求发送到 `DELETE /api/mall/cart/{id}`
5. 后端从数据库删除记录
6. 前端从数组中移除该项
7. 界面更新

### 修改数量
1. 用户修改数量输入框
2. 触发 `@change` 事件
3. 调用 `mallApi.updateCartItem(id, { quantity })`
4. 请求发送到 `PUT /api/mall/cart/{id}`
5. 后端更新数据库
6. 前端的 `v-model` 自动更新界面

### 选中状态
1. 用户点击复选框
2. 触发 `@change` 事件
3. 调用 `mallApi.updateCartItem(id, { selected })`
4. 请求发送到 `PUT /api/mall/cart/{id}`
5. 后端更新数据库
6. 前端更新全选状态

## API 端点

### 删除购物车项
```
DELETE /api/mall/cart/{id}
Headers: X-User-Id: {userId}
```

### 更新购物车项
```
PUT /api/mall/cart/{id}
Headers: X-User-Id: {userId}
Body: {
  "quantity": 2,
  "selected": true
}
```

### 全选/取消全选
```
PUT /api/mall/cart/select-all?selected=true
Headers: X-User-Id: {userId}
```

## 已修改文件

1. ✅ `frontend/src/views/Cart.vue` - 所有操作都调用后端API

## 测试步骤

1. **删除商品**：
   - 点击删除按钮
   - 确认删除
   - 刷新页面
   - ✅ 商品应该不再出现

2. **修改数量**：
   - 修改商品数量
   - 刷新页面
   - ✅ 数量应该保持修改后的值

3. **选中状态**：
   - 选中/取消选中商品
   - 刷新页面
   - ✅ 选中状态应该保持

4. **全选**：
   - 点击全选复选框
   - 刷新页面
   - ✅ 所有商品应该保持选中状态

## 优化建议

### 1. 乐观更新
先更新UI，再调用API，失败时回滚：
```typescript
const removeItem = async (item: CartItem) => {
  // 先从UI移除
  const index = cartItems.value.findIndex(i => i.id === item.id)
  const backup = cartItems.value[index]
  cartItems.value.splice(index, 1)
  
  try {
    // 调用API
    await mallApi.deleteCartItem(item.id)
  } catch (error) {
    // 失败时恢复
    cartItems.value.splice(index, 0, backup)
    ElMessage.error('删除失败')
  }
}
```

### 2. 防抖处理
数量修改时使用防抖，避免频繁请求：
```typescript
import { debounce } from 'lodash-es'

const handleQuantityChange = debounce(async (item, value) => {
  await mallApi.updateCartItem(item.id, { quantity: value })
}, 500)
```

### 3. 加载状态
显示加载状态，提升用户体验：
```typescript
const updating = ref(false)

const handleQuantityChange = async (item, value) => {
  updating.value = true
  try {
    await mallApi.updateCartItem(item.id, { quantity: value })
  } finally {
    updating.value = false
  }
}
```

## 当前状态

✅ 删除商品调用后端API
✅ 修改数量调用后端API
✅ 选中状态调用后端API
✅ 全选操作调用后端API
✅ 数据持久化到数据库

现在购物车的所有操作都会同步到后端，刷新页面后数据不会丢失了！
