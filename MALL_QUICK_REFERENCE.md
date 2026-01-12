# 商城功能快速参考

## 🚀 快速开始

### 1. 初始化数据库
```bash
INIT_CART_ADDRESS.bat
```

### 2. 启动服务
```bash
cd backend/mall-service
mvn spring-boot:run
```

服务地址: `http://localhost:8085`

---

## 📡 API 快速参考

### 购物车 API

| 功能 | 方法 | 路径 | 请求头 |
|------|------|------|--------|
| 添加到购物车 | POST | `/api/mall/cart` | X-User-Id |
| 获取购物车 | GET | `/api/mall/cart` | X-User-Id |
| 更新购物车项 | PUT | `/api/mall/cart/{id}` | X-User-Id |
| 删除购物车项 | DELETE | `/api/mall/cart/{id}` | X-User-Id |
| 批量删除 | DELETE | `/api/mall/cart/batch` | X-User-Id |
| 全选/取消全选 | PUT | `/api/mall/cart/select-all?selected=true` | X-User-Id |
| 清空购物车 | DELETE | `/api/mall/cart/clear` | X-User-Id |

### 地址 API

| 功能 | 方法 | 路径 | 请求头 |
|------|------|------|--------|
| 添加地址 | POST | `/api/mall/addresses` | X-User-Id |
| 获取地址列表 | GET | `/api/mall/addresses` | X-User-Id |
| 获取地址详情 | GET | `/api/mall/addresses/{id}` | X-User-Id |
| 更新地址 | PUT | `/api/mall/addresses/{id}` | X-User-Id |
| 删除地址 | DELETE | `/api/mall/addresses/{id}` | X-User-Id |
| 设置默认地址 | PUT | `/api/mall/addresses/{id}/default` | X-User-Id |
| 获取默认地址 | GET | `/api/mall/addresses/default` | X-User-Id |

---

## 💻 前端 API 使用

```typescript
import { mallApi } from '@/api/mall'

// 购物车
await mallApi.addToCart({ productId: 1, quantity: 2 })
await mallApi.getCartList()
await mallApi.updateCartItem(1, { quantity: 3 })
await mallApi.deleteCartItem(1)
await mallApi.selectAll(true)

// 地址
await mallApi.getAddressList()
await mallApi.addAddress({ receiverName: '张三', ... })
await mallApi.setDefaultAddress(1)
await mallApi.getDefaultAddress()
```

---

## 🗂️ 数据库表

### cart (购物车)
- id, user_id, product_id, quantity, selected
- 唯一索引: (user_id, product_id)

### address (收货地址)
- id, user_id, receiver_name, receiver_phone
- province, city, district, detail
- is_default, created_at, updated_at, deleted

---

## 📁 文件位置

### 后端
```
backend/mall-service/src/main/java/com/jiyi/mall/
├── entity/Cart.java, Address.java
├── mapper/CartMapper.java, AddressMapper.java
├── service/CartService.java, AddressService.java
├── service/impl/CartServiceImpl.java, AddressServiceImpl.java
├── controller/CartController.java, AddressController.java
└── dto/AddToCartRequest.java, UpdateCartRequest.java
```

### 前端
```
frontend/src/api/mall.ts (已更新)
```

---

## 🔍 测试命令

```bash
# 添加到购物车
curl -X POST http://localhost:8085/api/mall/cart \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 2" \
  -d '{"productId": 1, "quantity": 2}'

# 获取购物车
curl -X GET http://localhost:8085/api/mall/cart -H "X-User-Id: 2"

# 获取地址列表
curl -X GET http://localhost:8085/api/mall/addresses -H "X-User-Id: 2"
```

---

## 📚 完整文档

- `MALL_SERVICE_IMPLEMENTATION.md` - 详细实现文档
- `MALL_VERIFICATION_CHECKLIST.md` - 验证清单
- `MALL_CART_ADDRESS_COMPLETE.md` - 完成总结

---

**测试用户**: user_id = 2 (ruler)  
**服务端口**: 8085  
**数据库**: jiyi_mall

