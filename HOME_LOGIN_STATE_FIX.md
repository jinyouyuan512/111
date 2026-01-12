# 首页登录状态显示修复

## 问题描述
首页右上角登录后不变化，始终显示"登录"按钮，不显示用户信息。

## 根本原因
首页 `Home.vue` 有自己独立的 header，没有使用 `NavBar.vue` 组件，登录按钮是硬编码的，没有根据 `userStore.token` 动态显示。

## 解决方案

### 1. 修改 header-actions 部分
将固定的登录按钮改为根据登录状态动态显示：

```vue
<div class="header-actions">
  <template v-if="userStore.token">
    <el-dropdown @command="handleCommand">
      <span class="user-info">
        <el-avatar :src="userStore.userInfo?.avatar" size="small" />
        <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item v-if="userStore.userInfo?.role === 'admin'" command="admin">
            <span style="color: #a0182f; font-weight: 600;">⚙️ 管理后台</span>
          </el-dropdown-item>
          <el-dropdown-item command="profile">个人中心</el-dropdown-item>
          <el-dropdown-item command="orders">我的订单</el-dropdown-item>
          <el-dropdown-item command="cart">购物车</el-dropdown-item>
          <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </template>
  <template v-else>
    <el-button
      type="danger"
      @click="handleLogin"
    >
      登录
    </el-button>
  </template>
</div>
```

### 2. 添加 handleCommand 方法
处理用户下拉菜单的各种命令：

```typescript
// 处理用户下拉菜单命令
const handleCommand = (command: string) => {
  switch (command) {
    case 'admin':
      router.push('/admin')
      break
    case 'profile':
      ElMessage.info('个人中心功能开发中')
      break
    case 'orders':
      router.push('/orders')
      break
    case 'cart':
      router.push('/cart')
      break
    case 'logout':
      userStore.logout()
      ElMessage.success('已退出登录')
      break
  }
}
```

### 3. 导入 ElMessage
```typescript
import { ElMessage } from 'element-plus'
```

### 4. 添加 CSS 样式
```css
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 20px;
  transition: background 0.3s ease;
  color: rgba(255, 255, 255, 0.9);
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.15);
}

.username {
  font-size: 0.9rem;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.9);
}
```

## 功能说明

### 未登录状态
- 显示"登录"按钮
- 点击跳转到登录页面

### 已登录状态
- 显示用户头像和昵称/用户名
- 点击显示下拉菜单：
  - ⚙️ 管理后台（仅管理员可见）
  - 个人中心
  - 我的订单
  - 购物车
  - 退出登录

### 与 NavBar 保持一致
现在首页的登录状态显示逻辑与其他页面的 NavBar 组件完全一致。

## 验证步骤

1. **未登录状态**：
   - 访问 http://localhost:3001/
   - 首页右上角应显示"登录"按钮

2. **登录后**：
   - 点击"登录"按钮
   - 输入账号密码登录
   - 登录成功后自动跳转回首页
   - 首页右上角应显示用户头像和昵称

3. **下拉菜单**：
   - 点击用户头像/昵称
   - 应显示下拉菜单
   - 管理员账号应看到"管理后台"选项

4. **退出登录**：
   - 点击"退出登录"
   - 应显示"已退出登录"提示
   - 右上角恢复为"登录"按钮

## 修改的文件
- `frontend/src/views/Home.vue` - 添加动态登录状态显示

## 相关修复
- `LOGIN_STATE_FIX.md` - App.vue 中的 token 验证
- `AUTH_TOKEN_FIX.md` - 401 错误处理
- `REDIS_FIX_SUMMARY.md` - Redis 连接修复

## 测试账号
- 普通用户: 注册的任意账号
- 管理员: `ruler` (role='admin')
