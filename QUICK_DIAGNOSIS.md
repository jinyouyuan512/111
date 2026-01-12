# 快速诊断指南

## 问题1：加入购物车失败

### 检查步骤

#### 1. 检查浏览器控制台
打开浏览器开发者工具（F12），查看：
- Console标签：查看JavaScript错误
- Network标签：查看API请求

#### 2. 检查登录状态
在浏览器控制台执行：
```javascript
console.log('Token:', localStorage.getItem('token'))
console.log('UserInfo:', localStorage.getItem('userInfo'))
```

#### 3. 检查后端服务
确认以下服务正在运行：
- user-service (端口 8081)
- mall-service (端口 8083)
- Redis (端口 6379)

检查命令：
```powershell
# 检查Java进程
Get-Process java

# 检查端口占用
netstat -ano | findstr "8081"
netstat -ano | findstr "8083"
netstat -ano | findstr "6379"
```

#### 4. 检查数据库
确认mall数据库已创建并初始化：
```sql
USE mall;
SHOW TABLES;
SELECT * FROM product LIMIT 5;
SELECT * FROM cart LIMIT 5;
```

#### 5. 测试API
在浏览器控制台执行：
```javascript
// 测试加入购物车API
fetch('/api/mall/cart', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + localStorage.getItem('token')
  },
  body: JSON.stringify({
    productId: 1,
    quantity: 1
  })
})
.then(res => res.json())
.then(data => console.log('Response:', data))
.catch(err => console.error('Error:', err))
```

### 常见问题及解决方案

#### 问题A：401 Unauthorized
**原因**：Token无效或过期
**解决**：
1. 退出登录
2. 重新登录
3. 检查token是否正确保存

#### 问题B：404 Not Found
**原因**：后端服务未启动或路由配置错误
**解决**：
1. 启动mall-service
2. 检查application.yml配置
3. 查看后端日志

#### 问题C：500 Internal Server Error
**原因**：后端代码错误或数据库问题
**解决**：
1. 查看后端日志
2. 检查数据库连接
3. 确认数据表结构正确

#### 问题D：Network Error
**原因**：前端无法连接后端
**解决**：
1. 检查后端服务是否启动
2. 检查端口是否正确
3. 检查防火墙设置

## 问题2：个人中心打不开

### 检查步骤

#### 1. 检查路由配置
确认路由已正确配置：
```typescript
// frontend/src/router/index.ts
{
  path: '/profile',
  name: 'Profile',
  component: () => import('@/views/Profile.vue'),
  meta: { requiresAuth: true }
}
```

#### 2. 检查Profile.vue文件
确认文件存在且没有语法错误：
```powershell
Get-Content frontend/src/views/Profile.vue | Select-Object -First 10
```

#### 3. 检查浏览器控制台
查看是否有编译错误或运行时错误

#### 4. 检查导航配置
确认NavBar.vue中的handleCommand函数：
```typescript
case 'profile':
  router.push('/profile')
  break
```

### 解决方案

#### 方案1：清除缓存
```powershell
# 删除node_modules和重新安装
cd frontend
Remove-Item -Recurse -Force node_modules
npm install
```

#### 方案2：重启开发服务器
```powershell
# 停止当前服务器（Ctrl+C）
# 重新启动
cd frontend
npm run dev
```

#### 方案3：检查文件完整性
确认Profile.vue文件完整且没有损坏

## 快速修复脚本

### 重启所有服务
```powershell
# 停止所有Java进程
Get-Process java | Stop-Process -Force

# 重启Redis
redis-server

# 启动后端服务
cd backend/user-service
mvn spring-boot:run

cd ../mall-service
mvn spring-boot:run

# 启动前端
cd ../../frontend
npm run dev
```

### 重置数据库
```powershell
# 运行初始化脚本
.\INIT_DATABASE.bat
.\INIT_MALL_SOCIAL.bat
```

### 清除浏览器数据
在浏览器控制台执行：
```javascript
localStorage.clear()
sessionStorage.clear()
location.reload()
```

## 调试技巧

### 1. 启用详细日志
在Mall.vue的addToCart函数中已添加详细日志：
```typescript
console.log('addToCart - userStore.token:', userStore.token)
console.log('addToCart - localStorage token:', localStorage.getItem('token'))
console.log('准备加入购物车:', { productId: product.id, quantity: qty })
```

### 2. 使用Network标签
1. 打开开发者工具
2. 切换到Network标签
3. 点击"加入购物车"
4. 查看请求详情：
   - Request Headers（特别是Authorization）
   - Request Payload
   - Response

### 3. 检查后端日志
```powershell
# 查看mall-service日志
cd backend/mall-service
Get-Content logs/spring.log -Tail 50
```

## 联系支持

如果以上步骤都无法解决问题，请提供：
1. 浏览器控制台的完整错误信息
2. Network标签中失败请求的详情
3. 后端服务的日志
4. 数据库的状态

## 相关文档
- CART_LOGIN_FIX.md - 购物车登录修复
- PROFILE_CENTER_COMPLETE.md - 个人中心功能说明
- MALL_QUICK_START.md - 商城快速启动指南
- DATABASE_QUICK_START.md - 数据库快速启动指南
