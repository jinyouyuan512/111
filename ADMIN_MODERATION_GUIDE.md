# 社区审核管理功能

## 功能概述

社区审核管理功能允许管理员查看和处理用户举报的内容，包括不当动态、评论和用户行为。

## 访问方式

### 前端页面
访问地址：http://localhost:3000/admin

**注意**：需要管理员权限才能访问此页面。

### 后端 API

所有 API 都需要管理员权限（`ROLE_ADMIN`）。

#### 1. 获取举报列表
```
GET /api/social/reports
```

参数：
- `page`: 页码（默认 1）
- `size`: 每页数量（默认 20）
- `status`: 状态筛选（pending, processing, resolved, rejected）
- `targetType`: 类型筛选（post, comment, user）

响应：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "reporterId": 1,
        "targetType": "post",
        "targetId": 123,
        "reason": "违规内容",
        "description": "包含不当言论",
        "status": "pending",
        "createdAt": "2026-01-02T18:00:00"
      }
    ],
    "total": 10,
    "current": 1,
    "pages": 1
  }
}
```

#### 2. 获取举报详情
```
GET /api/social/reports/{id}
```

#### 3. 处理举报
```
PUT /api/social/reports/{id}/handle
```

参数：
- `result`: 处理结果（resolved, rejected）
- `description`: 处理说明（可选）

#### 4. 提交举报（用户）
```
POST /api/social/reports
```

请求体：
```json
{
  "targetType": "post",
  "targetId": 123,
  "reason": "违规内容",
  "description": "详细说明"
}
```

## 功能特性

### 1. 统计面板
- 待处理举报数量
- 处理中举报数量
- 已解决举报数量
- 已驳回举报数量

### 2. 举报列表
- 分页显示所有举报
- 按状态筛选
- 按类型筛选（动态/评论/用户）
- 查看详情
- 快速处理（通过/驳回）

### 3. 举报详情
- 举报ID
- 举报类型
- 目标ID
- 举报原因
- 详细说明
- 举报时间
- 当前状态
- 处理结果（如已处理）

### 4. 处理操作
- 通过：确认举报有效，采取相应措施
- 驳回：举报无效或误报
- 添加处理说明

## 举报状态

- `pending`: 待处理 - 新提交的举报
- `processing`: 处理中 - 正在调查处理
- `resolved`: 已解决 - 举报有效，已处理
- `rejected`: 已驳回 - 举报无效

## 举报类型

- `post`: 动态举报
- `comment`: 评论举报
- `user`: 用户举报

## 举报原因

常见举报原因包括：
- 违规内容
- 垃圾信息
- 骚扰辱骂
- 虚假信息
- 侵权内容
- 其他

## 使用流程

### 用户举报流程
1. 用户在社区页面发现不当内容
2. 点击举报按钮
3. 选择举报原因
4. 填写详细说明
5. 提交举报

### 管理员审核流程
1. 登录管理员账号
2. 访问审核管理页面
3. 查看待处理举报列表
4. 点击"查看"查看详情
5. 根据情况选择"通过"或"驳回"
6. 填写处理说明
7. 提交处理结果

## 数据库表结构

```sql
CREATE TABLE `report` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '举报ID',
    `reporter_id` BIGINT NOT NULL COMMENT '举报者ID',
    `target_type` VARCHAR(20) NOT NULL COMMENT '目标类型',
    `target_id` BIGINT NOT NULL COMMENT '目标ID',
    `reason` VARCHAR(50) NOT NULL COMMENT '举报原因',
    `description` TEXT COMMENT '详细说明',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态',
    `handler_id` BIGINT COMMENT '处理人ID',
    `handle_result` TEXT COMMENT '处理结果',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '举报时间',
    `handled_at` DATETIME COMMENT '处理时间',
    PRIMARY KEY (`id`)
);
```

## 启动社区服务

如果社区服务未运行，需要先启动：

```bash
cd backend/social-service
mvn spring-boot:run
```

服务将在端口 8086 启动。

## 前端代理配置

确保 `frontend/vite.config.ts` 中已配置社区服务代理：

```typescript
'/api/social': {
  target: 'http://localhost:8086',
  changeOrigin: true
}
```

## 注意事项

1. **权限控制**：审核功能仅限管理员访问
2. **数据安全**：处理举报时要谨慎，避免误操作
3. **及时处理**：定期查看待处理举报，及时响应
4. **记录完整**：处理时填写详细的处理说明
5. **用户隐私**：保护举报者和被举报者的隐私

## 后续优化建议

1. 添加举报内容预览功能
2. 支持批量处理举报
3. 添加举报统计图表
4. 实现自动审核规则
5. 添加举报通知功能
6. 支持举报历史记录查询

---

**创建时间**: 2026-01-02
**版本**: 1.0.0
