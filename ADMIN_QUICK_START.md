# 后台管理系统快速启动

## 管理员账号信息

```
用户名: admin
密码: admin123
邮箱: admin@jiyi.com
角色: admin
```

## 快速访问

1. **登录页面**: http://localhost:3000/login
2. **管理后台**: http://localhost:3000/admin

## 一键启动所有服务

### Windows (PowerShell)
```powershell
# 启动所有后端服务
.\START_ALL_SERVICES.ps1
```

### 手动启动

#### 1. 启动 User Service
```bash
cd backend/user-service
mvn spring-boot:run
```

#### 2. 启动 Mall Service
```bash
cd backend/mall-service
mvn spring-boot:run
```

#### 3. 启动 Social Service
```bash
cd backend/social-service
mvn spring-boot:run
```

#### 4. 启动前端
```bash
cd frontend
npm run dev
```

## 服务端口

| 服务 | 端口 | 状态 |
|------|------|------|
| User Service | 8081 | ✅ 必需 |
| Mall Service | 8085 | ✅ 必需 |
| Social Service | 8086 | ✅ 必需 |
| Frontend | 3000 | ✅ 必需 |
| MySQL | 3306 | ✅ 必需 |
| Redis | 6379 | ✅ 必需 |

## 后台功能模块

### 📊 总览 (`/admin`)
- 系统统计数据
- 快捷操作入口
- 最近活动记录

### 👥 用户管理 (`/admin/users`)
- 用户列表查看
- 用户搜索和筛选
- 用户信息编辑
- 角色权限管理

### 📝 内容管理 (`/admin/content`)
- 动态内容管理
- 评论审核
- 违规内容处理

### 🎁 商品管理 (`/admin/products`)
- 商品列表
- 商品编辑
- 库存管理

### 🛒 订单管理 (`/admin/orders`)
- 订单查看
- 订单处理
- 退款管理

### ⚠️ 举报审核 (`/admin/reports`)
- 举报列表
- 举报处理
- 审核记录

### ⚙️ 系统设置 (`/admin/settings`)
- 系统配置
- 参数设置

## 首次使用步骤

1. **启动所有服务**（见上方）

2. **访问登录页面**
   ```
   http://localhost:3000/login
   ```

3. **使用管理员账号登录**
   - 用户名: `admin`
   - 密码: `admin123`

4. **访问管理后台**
   ```
   http://localhost:3000/admin
   ```

5. **修改默认密码**（推荐）
   - 点击右上角用户头像
   - 选择"个人资料"
   - 修改密码

## 常用操作

### 查看待处理举报
1. 登录管理后台
2. 点击左侧"举报审核"
3. 查看待处理列表
4. 点击"查看"查看详情
5. 选择"通过"或"驳回"

### 管理用户
1. 点击左侧"用户管理"
2. 搜索或筛选用户
3. 点击"编辑"修改用户信息
4. 可以修改用户角色（user/designer/admin）

### 管理商品
1. 点击左侧"商品管理"
2. 查看商品列表
3. 编辑商品信息
4. 管理库存和价格

## 故障排除

### 无法访问后台
- 检查是否使用管理员账号登录
- 确认 User Service 正在运行
- 清除浏览器缓存后重试

### 举报列表为空
- 确认 Social Service 正在运行（端口 8086）
- 检查数据库 `jiyi_social.report` 表

### 用户列表加载失败
- 确认 User Service 正在运行（端口 8081）
- 检查数据库 `jiyi_user.user` 表

## 技术支持

详细文档请查看：
- `ADMIN_SYSTEM_GUIDE.md` - 完整使用指南
- `ADMIN_MODERATION_GUIDE.md` - 举报审核指南

---

**快速启动完成！** 🎉
