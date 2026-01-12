# Navicat 连接配置指南

## 问题
在 Navicat 中看不到 `jiyi_user` 数据库

## 解决方案

### 步骤 1: 检查 Navicat 连接配置

1. 打开 Navicat
2. 找到你的 MySQL 连接（通常在左侧）
3. 右键点击连接 → 选择"连接属性"
4. 确认配置：
   ```
   连接名: 任意名称（如 "Local MySQL"）
   主机: localhost 或 127.0.0.1
   端口: 3306
   用户名: root
   密码: root
   ```
5. 点击"测试连接"确保能连接成功

### 步骤 2: 刷新数据库列表

1. 右键点击你的 MySQL 连接
2. 选择"刷新"（Refresh）
3. 双击连接展开
4. 应该能看到以下数据库：
   - jiyi_user ✅
   - jiyi_experience ✅
   - jiyihongtu
   - 其他系统数据库

### 步骤 3: 如果还是看不到

#### 方法 A: 使用查询窗口
1. 双击打开你的 MySQL 连接
2. 点击"查询"按钮（或按 Ctrl+Q）
3. 在查询窗口中输入：
   ```sql
   SHOW DATABASES LIKE 'jiyi%';
   ```
4. 点击"运行"（或按 F5）
5. 你应该能看到所有 jiyi 开头的数据库

#### 方法 B: 执行 SQL 文件
1. 在 Navicat 中打开"查询"窗口
2. 点击"打开"按钮
3. 选择项目根目录下的 `CHECK_DATABASES.sql` 文件
4. 点击"运行"
5. 查看结果

### 步骤 4: 手动切换到数据库

如果数据库存在但不显示在列表中：

1. 在查询窗口中执行：
   ```sql
   USE jiyi_user;
   SHOW TABLES;
   ```
2. 应该能看到 `user` 表

### 步骤 5: 查看用户数据

在查询窗口中执行：
```sql
USE jiyi_user;

SELECT 
    id,
    username,
    email,
    role,
    nickname,
    level,
    points,
    created_at
FROM user
ORDER BY id;
```

你应该能看到 3 个用户：
- testuser456
- ruler (你的账号)
- newuser789

## 常见问题

### Q1: Navicat 连接失败
**原因**: 可能连接配置不正确

**解决方案**:
1. 确认 MySQL 正在运行：
   ```bash
   netstat -an | findstr "3306"
   ```
2. 确认用户名密码正确（root/root）
3. 尝试使用 127.0.0.1 而不是 localhost

### Q2: 能连接但看不到数据库
**原因**: 可能需要刷新或权限问题

**解决方案**:
1. 右键连接 → 刷新
2. 在查询窗口执行：
   ```sql
   SHOW DATABASES;
   ```
3. 检查是否有 jiyi_user

### Q3: 数据库列表是空的
**原因**: 可能连接到了错误的 MySQL 实例

**解决方案**:
1. 检查端口是否正确（应该是 3306）
2. 在查询窗口执行：
   ```sql
   SELECT @@hostname, @@port, @@version;
   ```
3. 确认版本是 5.7.39

## 验证数据库存在

在命令行中执行（已确认存在）：
```bash
mysql -u root -proot -e "SHOW DATABASES LIKE 'jiyi%';"
```

输出：
```
+------------------+
| Database (jiyi%) |
+------------------+
| jiyi_experience  |
| jiyi_user        |
| jiyihongtu       |
+------------------+
```

## 数据库详细信息

### jiyi_user 数据库
- **表**: user
- **记录数**: 3
- **用途**: 用户认证和管理
- **服务**: User Service (端口 8081)

### 表结构
```sql
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    avatar VARCHAR(255),
    role VARCHAR(20) DEFAULT 'user',
    nickname VARCHAR(50),
    phone VARCHAR(20),
    gender VARCHAR(10),
    birthdate DATETIME,
    interests TEXT,
    level INT DEFAULT 1,
    points INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at DATETIME,
    deleted TINYINT DEFAULT 0
);
```

## 如果还是有问题

1. 截图 Navicat 的连接配置
2. 截图 Navicat 的数据库列表
3. 在查询窗口执行 `SHOW DATABASES;` 并截图结果

这样我可以帮你进一步诊断问题。
