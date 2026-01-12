# 数据库设置指南

## 数据库技术栈

本项目使用以下数据库：

- **MySQL 8.0+** - 主数据库（关系型数据库）
- **Redis 7.0+** - 缓存和会话存储（可选，登录功能需要）
- **MongoDB 6.0+** - 文档存储（可选，用于3D模型元数据）

## MySQL 数据库设置

### 1. 安装MySQL

#### Windows
下载并安装 MySQL 8.0+：
https://dev.mysql.com/downloads/mysql/

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install mysql-server
sudo mysql_secure_installation
```

#### macOS
```bash
brew install mysql
brew services start mysql
```

### 2. 创建数据库

连接到MySQL：
```bash
mysql -u root -p
```

执行以下SQL创建所有需要的数据库：

```sql
-- 用户服务数据库
CREATE DATABASE IF NOT EXISTS jiyi_user DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 学院服务数据库
CREATE DATABASE IF NOT EXISTS jiyi_academy DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 旅游服务数据库
CREATE DATABASE IF NOT EXISTS jiyi_tourism DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 导览服务数据库
CREATE DATABASE IF NOT EXISTS jiyi_guide DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 商城服务数据库
CREATE DATABASE IF NOT EXISTS jiyi_mall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 众创服务数据库
CREATE DATABASE IF NOT EXISTS jiyi_creative DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 社交服务数据库
CREATE DATABASE IF NOT EXISTS jiyi_social DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

验证数据库创建成功：
```sql
SHOW DATABASES LIKE 'jiyi_%';
```

### 3. 初始化用户服务数据库

```sql
USE jiyi_user;

-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `email` VARCHAR(100) NOT NULL COMMENT '邮箱',
    `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `role` VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色: user, designer, admin',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `gender` VARCHAR(10) DEFAULT NULL COMMENT '性别',
    `birthdate` DATETIME DEFAULT NULL COMMENT '生日',
    `interests` TEXT DEFAULT NULL COMMENT '兴趣标签(JSON数组)',
    `level` INT NOT NULL DEFAULT 1 COMMENT '用户等级',
    `points` INT NOT NULL DEFAULT 0 COMMENT '积分',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `last_login_at` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    KEY `idx_role` (`role`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 插入测试用户（密码都是: password123）
INSERT INTO `user` (`username`, `email`, `password_hash`, `nickname`, `role`, `level`, `points`) 
VALUES 
('admin', 'admin@jiyi.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '管理员', 'admin', 10, 1000),
('testuser', 'test@jiyi.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '测试用户', 'user', 1, 0);

-- 验证数据
SELECT id, username, email, nickname, role FROM user;
```

### 4. 配置数据库连接

修改 `backend/user-service/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/jiyi_user?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false
    username: root
    password: your_mysql_password  # 修改为你的MySQL密码
```

## 快速初始化脚本

### 方式1：使用SQL文件

项目已经包含了初始化SQL文件：

```bash
# 初始化用户服务数据库
mysql -u root -p jiyi_user < backend/user-service/src/main/resources/db/schema.sql

# 初始化其他服务数据库
mysql -u root -p jiyi_academy < backend/academy-service/src/main/resources/db/schema.sql
mysql -u root -p jiyi_tourism < backend/tourism-service/src/main/resources/db/schema.sql
mysql -u root -p jiyi_guide < backend/guide-service/src/main/resources/db/schema.sql
mysql -u root -p jiyi_mall < backend/mall-service/src/main/resources/db/migration/V1__init_mall.sql
mysql -u root -p jiyi_creative < backend/creative-service/src/main/resources/db/schema.sql
mysql -u root -p jiyi_social < backend/social-service/src/main/resources/db/migration/V1__init_social.sql
```

### 方式2：使用统一初始化脚本

```bash
# 执行统一初始化脚本
mysql -u root -p < backend/init-all-databases.sql
```

## Redis 设置（可选）

### 安装Redis

#### Windows
下载Redis for Windows：
https://github.com/microsoftarchive/redis/releases

或使用WSL：
```bash
wsl --install
wsl
sudo apt update
sudo apt install redis-server
redis-server
```

#### Linux
```bash
sudo apt update
sudo apt install redis-server
sudo systemctl start redis
sudo systemctl enable redis
```

#### macOS
```bash
brew install redis
brew services start redis
```

### 测试Redis连接
```bash
redis-cli ping
# 应该返回: PONG
```

### 配置Redis连接

在 `application.yml` 中：
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
      password:  # 如果设置了密码，在这里填写
```

## 验证数据库设置

### 1. 检查MySQL连接
```bash
mysql -u root -p -e "SHOW DATABASES LIKE 'jiyi_%';"
```

应该看到所有7个数据库。

### 2. 检查用户表
```bash
mysql -u root -p jiyi_user -e "SELECT id, username, email, nickname FROM user;"
```

应该看到admin和testuser两个用户。

### 3. 测试登录
使用测试账号登录：
- 用户名: `testuser`
- 密码: `password123`

## 常见问题

### Q1: "Access denied for user 'root'@'localhost'"
**A**: MySQL密码错误，重置密码：
```bash
# Linux/Mac
sudo mysql
ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password';
FLUSH PRIVILEGES;

# Windows
# 以管理员身份运行MySQL，然后执行上述SQL
```

### Q2: "Unknown database 'jiyi_user'"
**A**: 数据库未创建，执行创建数据库的SQL语句

### Q3: "Table 'user' doesn't exist"
**A**: 表未创建，执行schema.sql文件

### Q4: Redis连接失败
**A**: 
- 检查Redis是否运行：`redis-cli ping`
- 注册功能不需要Redis，只有登录时才需要
- 可以暂时忽略Redis错误

### Q5: 字符集问题
**A**: 确保数据库使用utf8mb4字符集：
```sql
ALTER DATABASE jiyi_user CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 数据库管理工具推荐

- **MySQL Workbench** - 官方GUI工具
- **Navicat** - 功能强大的数据库管理工具
- **DBeaver** - 免费开源的数据库工具
- **phpMyAdmin** - Web界面管理工具

## 测试账号

项目已经预置了以下测试账号：

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | password123 | admin | 管理员账号 |
| testuser | password123 | user | 普通用户账号 |

## 下一步

数据库设置完成后：

1. ✅ 启动user-service
2. ✅ 测试注册功能
3. ✅ 使用testuser账号登录
4. ✅ 初始化其他服务的数据库

## 完整启动顺序

```bash
# 1. 启动MySQL
# 确保MySQL服务正在运行

# 2. 启动Redis（可选）
redis-server

# 3. 启动后端服务
cd backend
mvn spring-boot:run -pl user-service

# 4. 启动前端
cd frontend
npm run dev

# 5. 访问登录页面
# http://localhost:3000/login
```
