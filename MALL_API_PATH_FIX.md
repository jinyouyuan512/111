# 商品列表加载失败修复

## 问题
访问商城页面时，商品列表加载失败。

## 根本原因

### API 路径不匹配

**后端路径**：
```java
@RestController
@RequestMapping("/api/mall/products")
public class ProductController {
    @GetMapping
    public Result<Page<Product>> getProductList(...) {
        // ...
    }
}
```
实际端点：`http://localhost:8085/api/mall/products`

**前端原路径**：
```typescript
getProductList(params) {
  return request.get('/mall/products', { params })
}
```
请求地址：`/mall/products` → 代理到 `http://localhost:8085/mall/products`

**问题**：缺少 `/api` 前缀，导致 404 错误。

## 解决方案

### 修改前端 API 路径

在 `frontend/src/api/mall.ts` 中，为所有 mall 相关的 API 路径添加 `/api` 前缀：

**修改前**：
```typescript
getProductList(params) {
  return request.get('/mall/products', { params })
}
```

**修改后**：
```typescript
getProductList(params) {
  return request.get('/api/mall/products', { params })
}
```

### 请求流程

1. **前端发起请求**：
   ```typescript
   mallApi.getProductList({ page: 1, size: 12 })
   ```

2. **请求路径**：
   ```
   GET /api/mall/products?page=1&size=12
   ```

3. **Vite 代理匹配**：
   ```typescript
   '/api/mall': {
     target: 'http://localhost:8085',
     changeOrigin: true
   }
   ```

4. **代理转发**：
   ```
   GET http://localhost:8085/api/mall/products?page=1&size=12
   ```

5. **后端处理**：
   ```java
   @GetMapping // /api/mall/products
   public Result<Page<Product>> getProductList(...)
   ```

## 修改的 API 路径

所有 mall 相关的 API 都已添加 `/api` 前缀：

### 商品接口
- ✅ `/api/mall/products` - 商品列表
- ✅ `/api/mall/products/{id}` - 商品详情
- ✅ `/api/mall/products` (POST) - 创建商品
- ✅ `/api/mall/products/{id}` (PUT) - 更新商品
- ✅ `/api/mall/products/{id}` (DELETE) - 删除商品

### 订单接口
- ✅ `/api/mall/orders` - 订单列表
- ✅ `/api/mall/orders/{id}` - 订单详情
- ✅ `/api/mall/orders` (POST) - 创建订单
- ✅ `/api/mall/orders/{id}/status` - 更新订单状态
- ✅ `/api/mall/orders/{id}/pay` - 支付订单
- ✅ `/api/mall/orders/{id}/confirm` - 确认收货

### 购物车接口
- ✅ `/api/mall/cart` - 购物车列表
- ✅ `/api/mall/cart` (POST) - 添加到购物车
- ✅ `/api/mall/cart/{id}` - 更新购物车项
- ✅ `/api/mall/cart/{id}` (DELETE) - 删除购物车项
- ✅ `/api/mall/cart/batch` - 批量删除
- ✅ `/api/mall/cart/select-all` - 全选/取消全选
- ✅ `/api/mall/cart/clear` - 清空购物车

### 地址接口
- ✅ `/api/mall/addresses` - 地址列表
- ✅ `/api/mall/addresses` (POST) - 添加地址
- ✅ `/api/mall/addresses/{id}` - 地址详情
- ✅ `/api/mall/addresses/{id}` (PUT) - 更新地址
- ✅ `/api/mall/addresses/{id}` (DELETE) - 删除地址
- ✅ `/api/mall/addresses/{id}/default` - 设置默认地址
- ✅ `/api/mall/addresses/default` - 获取默认地址

## 服务配置

### mall-service 配置
- **端口**：8085
- **路径前缀**：`/api/mall`
- **状态**：✅ 运行中

### Vite 代理配置
```typescript
'/api/mall': {
  target: 'http://localhost:8085',
  changeOrigin: true
}
```

## 测试步骤

1. **访问商城页面**：
   ```
   http://localhost:3001/mall
   ```

2. **检查 Network**：
   - 应该看到 `GET /api/mall/products?page=1&size=12`
   - 状态码应该是 200
   - 响应应该包含商品列表数据

3. **验证功能**：
   - ✅ 商品列表正常显示
   - ✅ 分类筛选正常工作
   - ✅ 搜索功能正常
   - ✅ 价格筛选正常
   - ✅ 排序功能正常

## 已修改文件

1. ✅ `frontend/src/api/mall.ts` - 所有 API 路径添加 `/api` 前缀

## 相关文件

- `backend/mall-service/src/main/java/com/jiyi/mall/controller/ProductController.java` - 商品控制器
- `backend/mall-service/src/main/resources/application.yml` - 服务配置（端口 8085）
- `frontend/vite.config.ts` - 代理配置

## 当前状态

✅ mall-service 运行中（端口 8085）
✅ API 路径已修复
✅ 代理配置正确
✅ 商品列表应该可以正常加载

现在刷新商城页面，商品列表应该能正常显示了。
