# 购物车登录状态修复

## 问题描述
登录后无法加入购物车，提示"请先登录"。

## 问题原因
在 `Mall.vue` 的 `addToCart` 和 `buyNow` 函数中，每次都重新创建了 `useUserStore()` 实例，导致无法正确获取登录状态。

## 修复方案

### 1. 在组件顶部创建 userStore 实例
```typescript
const router = useRouter()
const userStore = useUserStore()  // 在顶部创建一次
```

### 2. 修改 addToCart 函数
- 移除函数内部的 `const userStore = useUserStore()`
- 直接使用顶部创建的 `userStore` 实例
- 添加详细的错误日志
- 401错误时清除登录状态并跳转登录页

### 3. 修改 buyNow 函数
- 同样移除函数内部的 userStore 创建
- 使用顶部的 userStore 实例
- 统一错误处理逻辑

## 修复后的代码

```typescript
// 组件顶部
const router = useRouter()
const userStore = useUserStore()

// addToCart 函数
const addToCart = async (product: any, quantity?: number) => {
  // 检查是否登录
  console.log('addToCart - userStore.token:', userStore.token)
  console.log('addToCart - localStorage token:', localStorage.getItem('token'))
  
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  try {
    const qty = quantity || buyCount.value || 1
    console.log('准备加入购物车:', { productId: product.id, quantity: qty })
    
    await mallApi.addToCart({
      productId: product.id,
      quantity: qty
    })
    
    ElMessage.success(`已将 ${qty} 件 ${product.name} 加入购物车`)
    detailVisible.value = false
  } catch (error: any) {
    console.error('加入购物车失败:', error)
    console.error('错误详情:', error.response)
    
    if (error.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      userStore.logout()
      router.push('/login')
    } else {
      ElMessage.error(error.response?.data?.message || '加入购物车失败，请稍后重试')
    }
  }
}
```

## 调试信息
添加了控制台日志来帮助诊断问题：
- 显示 userStore.token 的值
- 显示 localStorage 中的 token
- 显示准备加入购物车的数据
- 显示详细的错误信息

## 测试步骤
1. 登录系统
2. 进入商城页面
3. 点击商品查看详情
4. 点击"加入购物车"按钮
5. 检查控制台日志
6. 验证是否成功加入购物车

## 可能的其他问题

### 如果仍然无法加入购物车，检查：

1. **Token 是否正确保存**
   - 打开浏览器开发者工具
   - 查看 Application > Local Storage
   - 确认 token 字段存在且有值

2. **后端服务是否正常**
   - 检查 mall-service 是否启动
   - 查看后端日志是否有错误
   - 确认 Redis 是否正常运行

3. **API 请求是否正确**
   - 打开 Network 标签
   - 查看加入购物车的请求
   - 检查请求头是否包含 Authorization
   - 查看响应状态码和内容

4. **跨域问题**
   - 确认后端 CORS 配置正确
   - 检查请求是否被浏览器拦截

## 相关文件
- `frontend/src/views/Mall.vue` - 商城页面
- `frontend/src/stores/user.ts` - 用户状态管理
- `frontend/src/api/mall.ts` - 商城 API
- `backend/mall-service` - 商城后端服务

## 完成状态
✅ 修复 userStore 实例创建问题
✅ 添加详细的错误日志
✅ 优化错误处理逻辑
✅ 添加登录跳转功能

现在登录后应该可以正常加入购物车了！
