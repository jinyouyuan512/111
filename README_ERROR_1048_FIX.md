# 🚀 Error 1048 修复指南

## 📌 快速概览

**问题：** 创意作品上传时报错 `SQLIntegrityConstraintViolationException: Unknown error 1048`

**原因：** 数据库表中的可选字段设置了 NOT NULL 约束

**解决：** 修改数据库表结构，允许可选字段为 NULL

**状态：**
- ✅ 前端代码已修复
- ✅ 后端代码已修复
- ✅ 创意服务正在运行（端口 8087）
- ❌ **数据库表结构需要修复** ← 只差这一步！

---

## 🎯 立即执行（3 步解决）

### 步骤 1：打开 MySQL 客户端

选择以下任一工具：
- **Navicat** (推荐，图形界面)
- MySQL Workbench
- 命令行：`mysql -u root -p`

### 步骤 2：执行修复 SQL

**方法 A：一键修复（推荐）**

1. 打开文件：`一键修复.sql`
2. 复制全部内容
3. 粘贴到 MySQL 查询窗口
4. 点击"执行"或按 F5
5. 查看结果，应该显示所有字段都是 "✓ 正确"

**方法 B：逐条执行（更安全）**

在 MySQL 中依次执行：

```sql
USE jiyi_creative;

ALTER TABLE design MODIFY COLUMN contest_id BIGINT NULL;
ALTER TABLE design MODIFY COLUMN call_id BIGINT NULL;
ALTER TABLE design MODIFY COLUMN category_type INT NULL;
ALTER TABLE design MODIFY COLUMN design_concept TEXT NULL;
ALTER TABLE design MODIFY COLUMN cover_image VARCHAR(500) NULL;
ALTER TABLE design MODIFY COLUMN copyright_statement VARCHAR(500) NULL;
ALTER TABLE design MODIFY COLUMN tags VARCHAR(500) NULL;
ALTER TABLE design MODIFY COLUMN reject_reason VARCHAR(500) NULL;
```

每条命令执行后应该看到：
```
Query OK, 0 rows affected (0.XX sec)
```

### 步骤 3：验证并测试

**验证数据库修复：**
```sql
DESC design;
```

检查以下字段的 `Null` 列是否为 `YES`：
- contest_id ✓
- call_id ✓
- category_type ✓
- design_concept ✓
- cover_image ✓
- copyright_statement ✓
- tags ✓
- reject_reason ✓

**测试上传功能：**
1. 打开浏览器
2. 按 `Ctrl + F5` 强制刷新
3. 访问：http://localhost:3002/creative/upload
4. 填写表单并上传文件
5. 点击"提交作品"
6. 应该成功！🎉

---

## 📁 相关文件说明

| 文件名 | 用途 | 推荐度 |
|--------|------|--------|
| `一键修复.sql` | 一次性执行所有修复 SQL | ⭐⭐⭐⭐⭐ |
| `现在就做这个.txt` | 最简化的步骤说明 | ⭐⭐⭐⭐⭐ |
| `CREATIVE_1048_完整解决方案.md` | 详细的故障排查指南 | ⭐⭐⭐⭐ |
| `执行这个修复_现在.md` | 详细步骤说明 | ⭐⭐⭐⭐ |
| `FIX_CREATIVE_1048_ERROR.sql` | 完整的修复 SQL（带注释） | ⭐⭐⭐ |
| `VERIFY_DATABASE_NOW.sql` | 验证数据库状态 | ⭐⭐⭐ |
| `CHECK_CREATIVE_SERVICE.bat` | 检查服务运行状态 | ⭐⭐⭐ |
| `CHECK_NOW.sql` | 快速检查脚本 | ⭐⭐ |

---

## 🔍 故障排查

### 问题 1：执行 SQL 后仍然报错

**检查清单：**

1. **确认在正确的数据库中执行**
   ```sql
   SELECT DATABASE();
   ```
   应该显示：`jiyi_creative`
   
   如果不是，执行：
   ```sql
   USE jiyi_creative;
   ```

2. **确认 SQL 执行成功**
   每条 ALTER TABLE 命令应该显示 `Query OK`
   
   如果有错误，记录错误信息

3. **验证字段约束**
   ```sql
   DESC design;
   ```
   检查 `Null` 列是否为 `YES`

### 问题 2：不确定服务是否运行

**检查方法：**

Windows CMD 或 PowerShell：
```bash
netstat -ano | findstr :8087
```

如果有输出，说明服务正在运行。

如果没有输出，启动服务：
```bash
cd backend\creative-service
mvn spring-boot:run
```

### 问题 3：浏览器仍然显示旧错误

**解决方法：**

1. 清除浏览器缓存：`Ctrl + Shift + Delete`
2. 强制刷新页面：`Ctrl + F5`
3. 或者使用无痕模式测试

### 问题 4：前端无法连接后端

**检查 Vite 代理配置：**

文件：`frontend/vite.config.ts`

应该包含：
```typescript
proxy: {
  '/api/creative': {
    target: 'http://localhost:8087',
    changeOrigin: true
  }
}
```

如果配置正确，重启前端：
```bash
cd frontend
npm run dev
```

---

## 📊 完整检查清单

在提交作品前，确保以下所有项目都是 ✓：

**数据库：**
- [ ] 数据库 `jiyi_creative` 存在
- [ ] 表 `design` 存在
- [ ] 可选字段允许 NULL（执行 `DESC design` 验证）
- [ ] SQL 执行时显示 `Query OK`

**服务：**
- [ ] 创意服务运行在端口 8087
- [ ] 前端运行在端口 3002
- [ ] Vite 代理配置正确

**浏览器：**
- [ ] 已清除缓存或强制刷新（Ctrl+F5）
- [ ] 控制台没有 CORS 错误
- [ ] 网络请求正常（F12 → Network 标签）

---

## 🎯 快速验证命令

### 验证数据库状态
```sql
USE jiyi_creative;

SELECT 
    COLUMN_NAME AS '字段名',
    IS_NULLABLE AS '允许NULL',
    CASE WHEN IS_NULLABLE = 'YES' THEN '✓' ELSE '✗' END AS '状态'
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'jiyi_creative'
  AND TABLE_NAME = 'design'
  AND COLUMN_NAME IN (
    'contest_id', 'call_id', 'category_type', 
    'design_concept', 'cover_image', 
    'copyright_statement', 'tags', 'reject_reason'
  );
```

所有字段都应该显示 ✓

### 验证服务状态
```bash
# Windows
netstat -ano | findstr :8087

# 应该看到类似输出：
# TCP    0.0.0.0:8087    0.0.0.0:0    LISTENING    15312
```

### 验证前端访问
浏览器访问：http://localhost:3002/creative/upload

---

## 🎉 成功标志

修复成功后，你会看到：

1. **浏览器提示：** "作品提交成功！"
2. **控制台：** 没有 Error 1048 错误
3. **数据库：** 有新记录
   ```sql
   SELECT * FROM design ORDER BY id DESC LIMIT 1;
   ```
4. **后端日志：** 显示 "作品保存成功"

---

## 📞 需要帮助？

如果按照以上步骤仍然无法解决，请提供以下信息：

1. **数据库状态截图**
   ```sql
   DESC design;
   ```

2. **SQL 执行结果**
   每条 ALTER TABLE 命令的输出

3. **浏览器错误**
   F12 → Console 标签页的完整错误信息

4. **服务状态**
   ```bash
   netstat -ano | findstr :8087
   ```

5. **当前数据库**
   ```sql
   SELECT DATABASE();
   ```

---

## 📚 技术细节

### 为什么会出现 Error 1048？

MySQL Error 1048 表示：`Column 'xxx' cannot be NULL`

在我们的场景中：
- 用户提交作品时，某些可选字段（如 contest_id、tags）为空
- 数据库表定义这些字段为 NOT NULL
- MySQL 拒绝插入，抛出 Error 1048

### 修复原理

1. **代码层面：** 使用 `@TableField(insertStrategy = FieldStrategy.IGNORED)` 告诉 MyBatis-Plus 在字段为 null 时不插入该字段

2. **数据库层面：** 修改表结构，将可选字段的约束从 NOT NULL 改为 NULL

两者结合，确保：
- 如果字段有值，正常插入
- 如果字段为空，不插入该字段（使用数据库默认值 NULL）

### 为什么需要修改数据库？

虽然代码已经配置了 `FieldStrategy.IGNORED`，但如果数据库表结构仍然是 NOT NULL，在某些情况下（如 MyBatis-Plus 的某些版本或配置）仍然会尝试插入 NULL 值，导致错误。

修改数据库表结构是最彻底的解决方案。

---

**最后更新：** 2026-01-04  
**版本：** 1.0  
**状态：** 等待用户执行数据库修复 SQL
