# 🔥 创意上传 Error 1048 修复 - 立即执行

## 🐛 错误详情

```
SQLIntegrityConstraintViolationException: Unknown error 1048
```

**原因**: 数据库表中某些字段设置为 NOT NULL，但代码尝试插入 NULL 值。

## ⚡ 立即修复（3步）

### 步骤 1: 修复数据库字段约束 ✅

在 MySQL 中执行 SQL 文件：

```bash
mysql -u root -p jiyi_creative < FIX_CREATIVE_1048_ERROR.sql
```

或者手动执行以下 SQL：

```sql
USE jiyi_creative;

-- 修改可选字段为允许 NULL
ALTER TABLE design MODIFY COLUMN contest_id BIGINT NULL;
ALTER TABLE design MODIFY COLUMN call_id BIGINT NULL;
ALTER TABLE design MODIFY COLUMN category_type INT NULL;
ALTER TABLE design MODIFY COLUMN design_concept TEXT NULL;
ALTER TABLE design MODIFY COLUMN cover_image VARCHAR(500) NULL;
ALTER TABLE design MODIFY COLUMN copyright_statement VARCHAR(500) NULL;
ALTER TABLE design MODIFY COLUMN tags VARCHAR(500) NULL;
ALTER TABLE design MODIFY COLUMN reject_reason VARCHAR(500) NULL;
```

### 步骤 2: 重启创意服务 ✅

```bash
# 停止当前服务 (Ctrl+C)
# 然后重新启动
cd backend/creative-service
mvn spring-boot:run
```

### 步骤 3: 测试修复 ✅

```bash
start check_creative_error.html
```

1. 确认端口是 **3002**
2. 点击"测试最小请求"
3. 应该看到 HTTP 200 成功

## 📋 问题分析

### 错误的 SQL INSERT 语句

```sql
INSERT INTO design (
  designer_id, contest_id, call_id, title, category_type,
  description, design_concept, files, cover_image,
  copyright_statement, tags, status, reject_reason,
  votes, views, created_at, updated_at
) VALUES (
  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
)
```

### 问题字段

某些可选字段（如 `contest_id`, `call_id`, `category_type` 等）被设置为 NOT NULL，但前端没有提供值时，MyBatis-Plus 尝试插入 NULL，导致错误。

### 解决方案

1. **数据库层面**: 将可选字段改为允许 NULL
2. **代码层面**: 已添加 `@TableField(insertStrategy = FieldStrategy.IGNORED)`
3. **两者结合**: 确保数据库和代码都正确处理 NULL 值

## 🔍 验证修复

### 1. 检查数据库字段

```sql
USE jiyi_creative;
DESC design;
```

应该看到：

| Field | Type | Null | Key | Default | Extra |
|-------|------|------|-----|---------|-------|
| contest_id | bigint | YES | | NULL | |
| call_id | bigint | YES | | NULL | |
| category_type | int | YES | | NULL | |
| design_concept | text | YES | | NULL | |
| cover_image | varchar(500) | YES | | NULL | |
| copyright_statement | varchar(500) | YES | | NULL | |
| tags | varchar(500) | YES | | NULL | |

### 2. 测试插入

```sql
INSERT INTO design (
  designer_id, title, description, files, status, votes, views
) VALUES (
  1, '测试作品', '测试描述', '["http://test.jpg"]', 'pending', 0, 0
);

SELECT * FROM design ORDER BY id DESC LIMIT 1;
```

应该成功插入，可选字段为 NULL。

### 3. 测试 API

使用 `check_creative_error.html`:

```json
{
  "title": "测试作品",
  "categoryType": 1,
  "description": "测试描述",
  "files": ["http://localhost:8083/uploads/test.jpg"]
}
```

应该返回 HTTP 200。

## 📊 字段约束规则

### 必填字段 (NOT NULL)

| 字段 | 类型 | 说明 |
|------|------|------|
| designer_id | BIGINT | 设计师ID |
| title | VARCHAR(200) | 作品标题 |
| description | TEXT | 作品描述 |
| files | TEXT | 作品文件（JSON数组） |
| status | VARCHAR(20) | 状态（默认 pending） |
| votes | INT | 投票数（默认 0） |
| views | INT | 浏览量（默认 0） |

### 可选字段 (NULL)

| 字段 | 类型 | 说明 |
|------|------|------|
| contest_id | BIGINT | 大赛ID |
| call_id | BIGINT | 征集ID |
| category_type | INT | 作品分类 |
| design_concept | TEXT | 设计理念 |
| cover_image | VARCHAR(500) | 封面图片 |
| copyright_statement | VARCHAR(500) | 版权声明 |
| tags | VARCHAR(500) | 标签 |
| reject_reason | VARCHAR(500) | 拒绝原因 |

## 🚨 常见问题

### Q1: 执行 SQL 后仍然报错

**A**: 确保创意服务已重启。数据库修改后必须重启服务。

### Q2: 不知道 MySQL 密码

**A**: 查看 `backend/creative-service/src/main/resources/application.yml` 中的数据库配置。

### Q3: 找不到 jiyi_creative 数据库

**A**: 先创建数据库：
```sql
CREATE DATABASE IF NOT EXISTS jiyi_creative CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Q4: 修改后其他功能出问题

**A**: 不会。我们只是将可选字段改为允许 NULL，不影响现有数据和功能。

## ✅ 修复验证清单

- [ ] 执行了 SQL 修复脚本
- [ ] 重启了创意服务
- [ ] 使用测试工具验证成功
- [ ] 在实际页面测试成功
- [ ] 数据成功保存到数据库

## 📚 相关文件

| 文件 | 用途 |
|------|------|
| `FIX_CREATIVE_1048_ERROR.sql` | SQL 修复脚本 |
| `FIX_CREATIVE_NOW.bat` | 自动修复脚本 |
| `check_creative_error.html` | 测试工具 |
| `CREATIVE_1048_ERROR_FIX.md` | 本文档 |

## 🎯 快速执行

```bash
# 方法 1: 使用自动脚本
FIX_CREATIVE_NOW.bat

# 方法 2: 手动执行
# 1. 执行 SQL
mysql -u root -p jiyi_creative < FIX_CREATIVE_1048_ERROR.sql

# 2. 重启服务
cd backend/creative-service
mvn spring-boot:run

# 3. 测试
start check_creative_error.html
```

## 🎉 预期结果

修复后，提交作品应该：

1. ✅ 返回 HTTP 200
2. ✅ 显示"作品上传成功！"
3. ✅ 数据保存到数据库
4. ✅ 可选字段为 NULL 时不报错

---

**创建时间**: 2026-01-04  
**优先级**: 🔴 紧急  
**状态**: 等待执行

