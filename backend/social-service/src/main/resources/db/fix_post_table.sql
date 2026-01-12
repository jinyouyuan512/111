-- 修复 post 表结构，使其与 Post 实体匹配

USE jiyi_social;

-- 添加缺失的列（使用存储过程来检查列是否存在）
SET @dbname = 'jiyi_social';
SET @tablename = 'post';

-- 添加 images 列
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = @dbname 
AND TABLE_NAME = @tablename 
AND COLUMN_NAME = 'images';

SET @query = IF(@col_exists = 0, 
    'ALTER TABLE `post` ADD COLUMN `images` TEXT COMMENT ''图片列表（JSON数组）'' AFTER `content`', 
    'SELECT ''Column images already exists'' AS Info');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 location 列
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = @dbname 
AND TABLE_NAME = @tablename 
AND COLUMN_NAME = 'location';

SET @query = IF(@col_exists = 0, 
    'ALTER TABLE `post` ADD COLUMN `location` VARCHAR(100) COMMENT ''位置'' AFTER `images`', 
    'SELECT ''Column location already exists'' AS Info');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 category 列
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = @dbname 
AND TABLE_NAME = @tablename 
AND COLUMN_NAME = 'category';

SET @query = IF(@col_exists = 0, 
    'ALTER TABLE `post` ADD COLUMN `category` VARCHAR(50) COMMENT ''分类'' AFTER `location`', 
    'SELECT ''Column category already exists'' AS Info');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 likes_count 列
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = @dbname 
AND TABLE_NAME = @tablename 
AND COLUMN_NAME = 'likes_count';

SET @query = IF(@col_exists = 0, 
    'ALTER TABLE `post` ADD COLUMN `likes_count` INT NOT NULL DEFAULT 0 COMMENT ''点赞数'' AFTER `category`', 
    'SELECT ''Column likes_count already exists'' AS Info');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 comments_count 列
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = @dbname 
AND TABLE_NAME = @tablename 
AND COLUMN_NAME = 'comments_count';

SET @query = IF(@col_exists = 0, 
    'ALTER TABLE `post` ADD COLUMN `comments_count` INT NOT NULL DEFAULT 0 COMMENT ''评论数'' AFTER `likes_count`', 
    'SELECT ''Column comments_count already exists'' AS Info');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 shares_count 列
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = @dbname 
AND TABLE_NAME = @tablename 
AND COLUMN_NAME = 'shares_count';

SET @query = IF(@col_exists = 0, 
    'ALTER TABLE `post` ADD COLUMN `shares_count` INT NOT NULL DEFAULT 0 COMMENT ''分享数'' AFTER `comments_count`', 
    'SELECT ''Column shares_count already exists'' AS Info');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 pinned 列
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = @dbname 
AND TABLE_NAME = @tablename 
AND COLUMN_NAME = 'pinned';

SET @query = IF(@col_exists = 0, 
    'ALTER TABLE `post` ADD COLUMN `pinned` TINYINT NOT NULL DEFAULT 0 COMMENT ''是否置顶'' AFTER `shares_count`', 
    'SELECT ''Column pinned already exists'' AS Info');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 如果旧列存在，复制数据到新列
UPDATE `post` SET `likes_count` = `likes` WHERE `likes` IS NOT NULL AND `likes_count` = 0;
UPDATE `post` SET `comments_count` = `comments` WHERE `comments` IS NOT NULL AND `comments_count` = 0;
UPDATE `post` SET `shares_count` = `shares` WHERE `shares` IS NOT NULL AND `shares_count` = 0;
UPDATE `post` SET `location` = `location_name` WHERE `location_name` IS NOT NULL AND `location` IS NULL;
