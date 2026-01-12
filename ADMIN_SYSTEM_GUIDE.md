# 后台管理系统使用指南

## 管理员账号

### 默认管理员账号
- **用户名**: `admin`
- **密码**: `admin123`（默认密码，建议首次登录后修改）
- **邮箱**: `admin@jiyi.com`
- **角色**: `admin`

### 登录方式
1. 访问登录页面：http://localhost:3000/login
2. 输入管理员用户名和密码
3. 登录成功后，访问：http://localhost:3000/admin

## 后台管理系统功能

### 1. 管理后台总览 (`/admin`)
- **系统统计**
  - 总用户数
  - 总动态数
  - 总订单数
  - 待处理举报数
  
- **快捷操作**
  - 快速访问各个管理模块
  
- **最近活动**
  - 查看系统最近的活动记录

### 2. 用户管理 (`/admin/users`)
- 查看所有用户列表
- 搜索用户（用户名、邮箱）
- 按角色筛选（普通用户、设计师、管理员）
- 查看用户详情
- 编辑用户信息
- 删除用户账号
- 修改用户角色和权限

### 3. 内容管理 (`/admin/content`)
- 管理用户发布的动态
- 管理评论内容
- 删除违规内容
- 内容审核

### 4. 商品管理 (`/admin/products`)
- 查看所有商品
- 添加新商品
- 编辑商品信息
- 删除商品
- 管理商品库存
- 设置商品价格

### 5. 订单管理 (`/admin/orders`)
- 查看所有订单
- 订单状态管理
- 处理退款申请
- 订单统计分析

### 6. 举报审核 (`/admin/reports`)
- 查看所有举报
- 按状态筛选（待处理、处理中、已解决、已驳回）
- 按类型筛选（动态、评论、用户）
- 处理举报（通过/驳回）
- 查看举报详情
- 添加处理说明

### 7. 系统设置 (`/admin/settings`)
- 系统参数配置
- 权限管理
- 日志查看
- 数据备份

## 系统架构

### 前端路由结构
```
/admin (AdminLayout)
├── / (Dashboard) - 总览
├── /users (Users) - 用户管理
├── /content (Content) - 内容管理
├── /products (Products) - 商品管理
├── /orders (Orders) - 订单管理
├── /reports (Reports) - 举报审核
└── /settings (Settings) - 系统设置
```

### 后端 API 端点

#### 用户管理
- `GET /api/users` - 获取用户列表
- `GET /api/users/{id}` - 获取用户详情
- `PUT /api/users/{id}` - 更新用户信息
- `DELETE /api/users/{id}` - 删除用户

#### 举报审核
- `GET /api/social/reports` - 获取举报列表
- `GET /api/social/reports/{id}` - 获取举报详情
- `PUT /api/social/reports/{id}/handle` - 处理举报

#### 商品管理
- `GET /api/mall/products` - 获取商品列表
- `POST /api/mall/products` - 创建商品
- `PUT /api/mall/products/{id}` - 更新商品
- `DELETE /api/mall/products/{id}` - 删除商品

#### 订单管理
- `GET /api/mall/orders` - 获取订单列表
- `GET /api/mall/orders/{id}` - 获取订单详情
- `PUT /api/mall/orders/{id}/status` - 更新订单状态

## 权限控制

### 角色类型
1. **user** - 普通用户
   - 浏览内容
   - 发布动态
   - 购买商品
   - 提交举报

2. **designer** - 设计师
   - 普通用户权限
   - 上传创意作品
   - 参与设计活动

3. **admin** - 管理员
   - 所有用户权限
   - 访问后台管理系统
   - 管理用户、内容、商品
   - 处理举报
   - 系统配置

### 权限验证
- 前端路由守卫：检查 `meta.requiresAdmin`
- 后端接口：使用 `@PreAuthorize("hasRole('ADMIN')")`
- JWT Token：包含用户角色信息

## 创建新管理员

### 方法 1：通过数据库
```sql
-- 插入新管理员
INSERT INTO user (username, email, password_hash, nickname, role, level, points)
VALUES ('newadmin', 'newadmin@jiyi.com', '$2a$10$...', '新管理员', 'admin', 10, 10000);

-- 或更新现有用户为管理员
UPDATE user SET role = 'admin' WHERE username = 'existinguser';
```

### 方法 2：通过后台管理系统
1. 登录管理员账号
2. 进入用户管理
3. 找到目标用户
4. 编辑用户信息
5. 修改角色为 `admin`
6. 保存更改

## 安全建议

1. **修改默认密码**
   - 首次登录后立即修改管理员密码
   - 使用强密码（至少8位，包含大小写字母、数字、特殊字符）

2. **定期审查**
   - 定期检查管理员账号列表
   - 删除不再需要的管理员账号
   - 审查用户权限变更记录

3. **操作日志**
   - 记录所有管理操作
   - 定期审查操作日志
   - 发现异常及时处理

4. **访问控制**
   - 限制管理后台访问IP
   - 启用双因素认证（如需要）
   - 设置会话超时时间

## 常见问题

### Q: 忘记管理员密码怎么办？
A: 通过数据库直接重置密码：
```sql
UPDATE user 
SET password_hash = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH' 
WHERE username = 'admin';
-- 密码重置为: admin123
```

### Q: 如何查看当前有哪些管理员？
A: 
```sql
SELECT id, username, email, nickname, role, created_at 
FROM user 
WHERE role = 'admin';
```

### Q: 普通用户能访问后台吗？
A: 不能。后台路由有权限验证，只有 `role='admin'` 的用户才能访问。

### Q: 如何撤销管理员权限？
A:
```sql
UPDATE user SET role = 'user' WHERE username = 'username';
```

## 开发说明

### 添加新的管理模块
1. 在 `frontend/src/views/admin/` 创建新页面
2. 在 `frontend/src/router/index.ts` 添加路由
3. 在 `AdminLayout.vue` 侧边栏添加导航项
4. 实现相应的后端 API

### 自定义权限
可以在路由 meta 中添加更细粒度的权限控制：
```typescript
{
  path: '/admin/sensitive',
  meta: { 
    requiresAuth: true, 
    requiresAdmin: true,
    permission: 'sensitive.view' 
  }
}
```

## 启动后台系统

### 1. 确保所有服务运行
```bash
# User Service (8081)
cd backend/user-service
mvn spring-boot:run

# Mall Service (8085)
cd backend/mall-service
mvn spring-boot:run

# Social Service (8086)
cd backend/social-service
mvn spring-boot:run

# Frontend (3000)
cd frontend
npm run dev
```

### 2. 访问后台
1. 打开浏览器访问：http://localhost:3000/login
2. 使用管理员账号登录
3. 登录成功后访问：http://localhost:3000/admin

## 技术栈

### 前端
- Vue 3 + TypeScript
- Element Plus UI
- Vue Router
- Pinia (状态管理)
- Axios (HTTP 请求)

### 后端
- Spring Boot 3
- Spring Security
- JWT 认证
- MyBatis Plus
- MySQL 5.7

---

**创建时间**: 2026-01-02
**版本**: 1.0.0
**维护者**: 系统管理员
