# 购物车400错误修复

## 问题描述
加入购物车时返回400 Bad Request错误。

## 问题原因
后端CartController期望从请求头中获取`X-User-Id`，但前端的request拦截器只设置了`Authorization`头，没有设置`X-User-Id`。

## 后端期望的请求格式
```java
@PostMapping
public Result<Cart> addToCart(
    @RequestHeader("X-User-Id") Long userId,  // 需要这个请求头
    @RequestBody AddToCartRequest request
)
```

## 解决方案

### 修改前端request拦截器
在`frontend/src/api/request.ts`中添加`X-User-Id`请求头：

```typescript
service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
      
      // 从localStorage获取userInfo并添加X-User-Id头
      const userInfoStr = localStorage.getItem('userInfo')
      if (userInfoStr) {
        try {
          const userInfo = JSON.parse(userInfoStr)
          if (userInfo.id) {
            config.headers['X-User-Id'] = userInfo.id
          }
        } catch (e) {
          console.error('Failed to parse userInfo:', e)
        }
      }
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)
```

## 验证步骤

### 1. 检查userInfo中是否有id
在浏览器控制台执行：
```javascript
const userInfo = JSON.parse(localStorage.getItem('userInfo'))
console.log('User ID:', userInfo.id)
```

### 2. 检查请求头
1. 打开开发者工具 Network标签
2. 点击"加入购物车"
3. 查看请求头，确认包含：
   - `Authorization: Bearer xxx`
   - `X-User-Id: 数字`

### 3. 测试加入购物车
1. 登录系统
2. 进入商城
3. 点击商品详情
4. 点击"加入购物车"
5. 查看控制台日志

## 可能的其他问题

### 问题1：userInfo中没有id字段
**原因**：登录时后端返回的用户信息不包含id
**解决**：检查登录接口返回的数据结构

### 问题2：id为null或undefined
**原因**：用户信息未正确保存
**解决**：重新登录

### 问题3：后端仍然返回400
**原因**：可能是其他参数问题
**检查**：
- productId是否有效
- quantity是否为正整数
- 商品是否存在
- 商品库存是否充足

## 调试日志

修改后的addToCart函数会输出详细日志：
```
addToCart - userStore.token: xxx
addToCart - localStorage token: xxx
addToCart - localStorage userInfo: {"id":1,"username":"test",...}
准备加入购物车: {productId: 1, quantity: 1}
```

如果出现400错误，会额外输出：
```
400错误 - 请求数据: {productId: 1, quantity: 1}
400错误 - 用户信息: {"id":1,...}
```

## 后端日志检查

查看mall-service的日志：
```powershell
cd backend/mall-service
Get-Content logs/spring.log -Tail 50
```

查找类似的日志：
```
添加到购物车: userId=1, productId=1, quantity=1
```

如果看到错误日志，根据错误信息进行相应处理。

## 完整的请求示例

### 请求
```http
POST /api/mall/cart HTTP/1.1
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
X-User-Id: 1

{
  "productId": 1,
  "quantity": 1
}
```

### 成功响应
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "userId": 1,
    "productId": 1,
    "quantity": 1,
    "selected": true,
    "createTime": "2024-01-01T00:00:00",
    "updateTime": "2024-01-01T00:00:00"
  }
}
```

## 相关文件
- `frontend/src/api/request.ts` - HTTP请求拦截器
- `frontend/src/views/Mall.vue` - 商城页面
- `backend/mall-service/src/main/java/com/jiyi/mall/controller/CartController.java` - 购物车控制器

## 完成状态
✅ 修改request拦截器添加X-User-Id
✅ 增强错误日志输出
✅ 添加400错误特殊处理

现在应该可以正常加入购物车了！如果还有问题，请查看控制台的详细日志。
