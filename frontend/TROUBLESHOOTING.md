# 页面空白问题排查指南

## 问题描述
页面显示空白

## 可能的原因和解决方案

### 1. 路由配置问题 ✅ 已修复
- **问题**: Experience 路由未在 router/index.ts 中注册
- **解决**: 已添加 `/experience` 路由配置

### 2. 开发服务器未启动
**检查步骤**:
```bash
cd frontend
npm run dev
```

**预期输出**:
```
VITE v5.x.x  ready in xxx ms

➜  Local:   http://localhost:5173/
➜  Network: use --host to expose
```

### 3. 浏览器控制台错误
**检查步骤**:
1. 打开浏览器开发者工具 (F12)
2. 查看 Console 标签页
3. 查看是否有红色错误信息

**常见错误**:
- `Failed to fetch dynamically imported module` - 需要重启开发服务器
- `404 Not Found` - 路由配置问题或文件不存在
- `Unexpected token` - 语法错误

### 4. 访问路径错误
**正确的访问路径**:
- 首页: `http://localhost:5173/`
- 体验馆: `http://localhost:5173/experience`
- 商城: `http://localhost:5173/mall`
- 购物车: `http://localhost:5173/cart`
- 订单: `http://localhost:5173/orders`

### 5. 依赖未安装
**检查步骤**:
```bash
cd frontend
npm install
```

### 6. 端口被占用
**检查步骤**:
```bash
# Windows
netstat -ano | findstr :5173

# 如果端口被占用，可以修改 vite.config.ts 中的端口
```

## 快速诊断步骤

### Step 1: 检查开发服务器
```bash
cd frontend
npm run dev
```

### Step 2: 访问首页
打开浏览器访问: `http://localhost:5173/`

### Step 3: 检查控制台
按 F12 打开开发者工具，查看是否有错误

### Step 4: 测试路由
依次访问以下页面：
- `/` - 首页
- `/experience` - 体验馆
- `/mall` - 商城
- `/cart` - 购物车
- `/orders` - 订单

## 具体页面问题

### 体验馆页面 (/experience)
**已完成的功能**:
- ✅ 场景列表展示
- ✅ 场景卡片点击
- ✅ 交互点系统
- ✅ 进度跟踪
- ✅ 证书生成

**可能的问题**:
1. 图片路径不存在（使用了占位符路径）
2. localStorage 权限问题

**解决方案**:
- 图片会显示为背景色，这是正常的
- 确保浏览器允许 localStorage

### 商城页面 (/mall)
**已完成的功能**:
- ✅ 商品列表
- ✅ 搜索筛选
- ✅ 加入购物车
- ✅ 商品详情

**可能的问题**:
1. Element Plus 样式未加载

**解决方案**:
- 检查 main.ts 中是否导入了 Element Plus CSS

### 购物车页面 (/cart)
**已完成的功能**:
- ✅ 购物车列表
- ✅ 数量调整
- ✅ 结算功能

**可能的问题**:
1. localStorage 数据格式错误

**解决方案**:
```javascript
// 在浏览器控制台执行
localStorage.clear()
location.reload()
```

## 重置所有数据

如果遇到数据相关问题，可以清除所有本地数据：

```javascript
// 在浏览器控制台执行
localStorage.clear()
sessionStorage.clear()
location.reload()
```

## 完全重启项目

```bash
# 1. 停止开发服务器 (Ctrl+C)

# 2. 清除依赖
cd frontend
rm -rf node_modules
rm package-lock.json

# 3. 重新安装
npm install

# 4. 启动开发服务器
npm run dev
```

## 检查文件完整性

确保以下文件存在且完整：
- ✅ `frontend/src/views/Experience.vue`
- ✅ `frontend/src/views/Mall.vue`
- ✅ `frontend/src/views/Cart.vue`
- ✅ `frontend/src/views/Orders.vue`
- ✅ `frontend/src/router/index.ts`
- ✅ `frontend/src/main.ts`
- ✅ `frontend/src/App.vue`

## 浏览器兼容性

**推荐浏览器**:
- Chrome 90+
- Firefox 88+
- Edge 90+
- Safari 14+

**不支持**:
- IE 11 及以下

## 开发环境要求

- Node.js 18+
- npm 9+
- 现代浏览器

## 获取帮助

如果以上步骤都无法解决问题，请提供：
1. 浏览器控制台的完整错误信息
2. 访问的具体 URL
3. 开发服务器的输出信息
4. 浏览器版本和操作系统

## 常见问题 FAQ

### Q: 页面完全空白，没有任何内容
A: 
1. 检查开发服务器是否正常运行
2. 检查浏览器控制台是否有错误
3. 尝试访问 `/` 首页

### Q: 某些页面空白，其他页面正常
A:
1. 检查该页面的路由配置
2. 检查该页面的 Vue 文件是否有语法错误
3. 查看浏览器控制台错误

### Q: 样式丢失，只有文字
A:
1. 检查 Element Plus CSS 是否正确导入
2. 检查 Vite 开发服务器是否正常
3. 清除浏览器缓存

### Q: 数据不显示
A:
1. 检查 localStorage 是否有数据
2. 检查浏览器是否允许 localStorage
3. 尝试清除 localStorage 重新开始

## 成功标志

当一切正常时，你应该看到：
- ✅ 首页显示6个全屏滚动区块
- ✅ 体验馆显示4个场景卡片
- ✅ 商城显示商品网格
- ✅ 购物车显示空状态或商品列表
- ✅ 订单页显示空状态或订单列表

---

**最后更新**: 2024-12-23
