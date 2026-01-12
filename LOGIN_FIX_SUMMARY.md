# 登录按钮跳转修复总结

## 问题描述
点击导航栏右上角的"登录"按钮没有跳转到登录页面。

## 问题原因
在 Vue 3 的 `<template>` 中直接使用 `@click="$router.push('/login')"` 可能不会正确执行，因为 `$router` 在某些情况下可能无法正确访问。

## 修复方案

### 修改文件：`frontend/src/components/NavBar.vue`

#### 1. 桌面端登录按钮
**修改前：**
```vue
<el-button type="danger" @click="$router.push('/login')">登录</el-button>
```

**修改后：**
```vue
<el-button type="danger" @click="goToLogin">登录</el-button>
```

#### 2. 移动端登录链接
**修改前：**
```vue
<router-link to="/login" class="mobile-link" @click="closeMobileMenu">登录 / 注册</router-link>
```

**修改后：**
```vue
<a class="mobile-link" @click="goToLogin">登录 / 注册</a>
```

#### 3. 添加 goToLogin 方法
在 `<script setup>` 中添加：
```typescript
const goToLogin = () => {
  router.push('/login')
}
```

## 测试步骤

1. **启动前端服务**
   ```bash
   cd frontend
   npm run dev
   ```

2. **访问首页**
   打开浏览器访问：`http://localhost:5173`

3. **测试桌面端**
   - 点击右上角红色"登录"按钮
   - 应该跳转到 `/login` 页面

4. **测试移动端**（缩小浏览器窗口或使用开发者工具的移动设备模式）
   - 点击右上角菜单图标
   - 点击"登录 / 注册"
   - 应该跳转到 `/login` 页面

## 登录功能说明

### 登录页面功能
- **路径**: `/login`
- **功能**:
  - 用户登录（用户名 + 密码）
  - 用户注册（用户名 + 邮箱 + 密码）
  - 表单验证
  - 错误提示

### 测试账号
根据数据库初始化脚本，可以使用：
- **用户名**: `testuser`
- **密码**: `password123`

### 登录后功能
登录成功后，导航栏会显示：
- 用户头像
- 用户昵称
- 下拉菜单：
  - 个人中心（开发中）
  - 我的订单
  - 购物车
  - 退出登录

## 相关文件
- `frontend/src/components/NavBar.vue` - 导航栏组件
- `frontend/src/views/Login.vue` - 登录页面
- `frontend/src/router/index.ts` - 路由配置
- `frontend/src/stores/user.ts` - 用户状态管理
- `frontend/src/api/auth.ts` - 认证API

## 注意事项
1. 确保前端服务正在运行
2. 确保后端 user-service 正在运行（端口 8081）
3. 确保数据库已初始化
4. 如果遇到跨域问题，检查 `frontend/vite.config.ts` 中的代理配置

## 修复完成
✅ 桌面端登录按钮跳转已修复
✅ 移动端登录链接跳转已修复
✅ 使用方法调用代替直接路由跳转
