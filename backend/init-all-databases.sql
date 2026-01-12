-- 初始化所有数据库脚本
-- 此脚本用于一次性创建所有服务的数据库

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

-- Nacos配置中心数据库
CREATE DATABASE IF NOT EXISTS nacos DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 显示所有数据库
SHOW DATABASES;
