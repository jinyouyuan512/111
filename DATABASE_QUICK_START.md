# 数据库快速启动指南

## 📦 使用的数据库

本项目使用 **MySQL 8.0+** 作为主数据库。

每个微服务都有独立的数据库：

| 服务 | 数据库名 | 说明 |
|------|---------|------|
| user-service | jiyi_user | 用户认证和个人信息 |
| academy-service | jiyi_academy | 课程和学习数据 |
| tourism-service | jiyi_tourism | 旅游路线和行程 |
| guide-service | jiyi_guide | 导览和景点信息 |
| mall-service | jiyi_mall | 商品和订单 |
| creative-service | jiyi_creative | 设计作品和大赛 |
| social-service | jiyi_social | 社交动态和互动 |

## 🚀 快速初始化（推荐）

### Windows用户

1. **确保MySQL已安装并运行**

2. **运行初始化脚本**
```bash
INIT_DATABASE.bat
```

3. **按提示输入MySQL用户名和密码**

4. **完成！**

### Linux/Mac用户

```bash
# 1. 创建所有数据库
mysql -u root -p < backend/init-all-databases.sql

# 2. 初始化用户服务
mysql -u root -p jiyi_user < backend/user-service/src/main/resources/db/schema.sql

# 3. 验证
mysql -u root -p -e "SHOW DATABASES LIKE 'jiyi_%';"
mysql -u root -p jiyi_user -e "SELECT id, username, email FROM user;"
```

## 📝 手动初始化

如果自动脚本失败，可以手动执行：

### 1. 连接MySQL
```bash
mysql -u root -p
```

### 2. 创建数据库
```sql
CREATE DATABASE IF NOT EXISTS jiyi_user DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 创建用户表
```sql
USE jiyi_user;

CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `email` VARCHAR(100) NOT NULL COMMENT '邮箱',
    `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `role` VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `gender` VARCHAR(10) DEFAULT NULL COMMENT '性别',
    `birthdate` DATETIME DEFAULT NULL COMMENT '生日',
    `interests` TEXT DEFAULT NULL COMMENT '兴趣标签',
    `level` INT NOT NULL DEFAULT 1 COMMENT '用户等级',
    `points` INT NOT NULL DEFAULT 0 COMMENT '积分',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `last_login_at` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 4. 插入测试数据
```sql
INSERT INTO `user` (`username`, `email`, `password_hash`, `nickname`, `role`, `level`, `points`) 
VALUES 
('admin', 'admin@jiyi.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '管理员', 'admin', 10, 1000),
('testuser', 'test@jiyi.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '测试用户', 'user', 1, 0);
```

## ⚙️ 配置数据库连接

修改 `backend/user-service/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/jiyi_user?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false
    username: root
    password: your_password  # 改成你的MySQL密码
```

## ✅ 验证安装

### 检查数据库
```bash
mysql -u root -p -e "SHOW DATABASES LIKE 'jiyi_%';"
```

应该看到：
```
+------------------+
| Database         |
+------------------+
| jiyi_academy     |
| jiyi_creative    |
| jiyi_guide       |
| jiyi_mall        |
| jiyi_social      |
| jiyi_tourism     |
| jiyi_user        |
+------------------+
```

### 检查用户数据
```bash
mysql -u root -p jiyi_user -e "SELECT id, username, email, nickname FROM user;"
```

应该看到：
```
+----+----------+----------------+-----------+
| id | username | email          | nickname  |
+----+----------+----------------+-----------+
|  1 | admin    | admin@jiyi.com | 管理员    |
|  2 | testuser | test@jiyi.com  | 测试用户  |
+----+----------+----------------+-----------+
```

## 🔑 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| testuser | password123 | 普通用户 |
| admin | password123 | 管理员 |

## 🐛 常见问题

### MySQL连接失败
```
Error: Access denied for user 'root'@'localhost'
```
**解决**: 检查密码是否正确，或重置MySQL密码

### 数据库不存在
```
Error: Unknown database 'jiyi_user'
```
**解决**: 运行 `INIT_DATABASE.bat` 或手动创建数据库

### 字符集问题
```
Error: Incorrect string value
```
**解决**: 确保数据库使用utf8mb4字符集

### 端口被占用
```
Error: Can't connect to MySQL server on 'localhost:3306'
```
**解决**: 
- 检查MySQL是否运行
- 检查3306端口是否被占用
- 尝试重启MySQL服务

## 📚 更多信息

详细的数据库设置说明请查看：
- `DATABASE_SETUP_GUIDE.md` - 完整的数据库设置指南
- `backend/user-service/src/main/resources/db/schema.sql` - 用户表结构

## 🎯 下一步

数据库初始化完成后：

1. ✅ 启动后端服务
```bash
START_USER_SERVICE.bat
```

2. ✅ 启动前端
```bash
cd frontend
npm run dev
```

3. ✅ 访问登录页面
```
http://localhost:3000/login
```

4. ✅ 使用testuser账号登录测试
