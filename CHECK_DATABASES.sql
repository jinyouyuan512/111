-- 查看所有数据库
SHOW DATABASES;

-- 查看 jiyi 相关的数据库
SHOW DATABASES LIKE 'jiyi%';

-- 切换到 jiyi_user 数据库
USE jiyi_user;

-- 查看 jiyi_user 数据库中的表
SHOW TABLES;

-- 查看 user 表结构
DESCRIBE user;

-- 查看所有用户数据
SELECT 
    id,
    username,
    email,
    role,
    nickname,
    level,
    points,
    created_at,
    last_login_at
FROM user
ORDER BY id;

-- 统计用户数量
SELECT COUNT(*) as total_users FROM user;
