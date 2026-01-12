-- 修复 Academy 数据库表结构
USE jiyi_academy;

-- 添加 user_enrollment 表缺失的字段
ALTER TABLE user_enrollment 
ADD COLUMN IF NOT EXISTS `progress` INT NOT NULL DEFAULT 0 COMMENT '学习进度百分比' AFTER `status`,
ADD COLUMN IF NOT EXISTS `completed_time` DATETIME DEFAULT NULL COMMENT '完成时间' AFTER `progress`;

-- 确保 news 表有 external_url 字段
ALTER TABLE news 
ADD COLUMN IF NOT EXISTS `external_url` VARCHAR(500) DEFAULT NULL COMMENT '外部链接' AFTER `source`;

-- 确保 book 表有 external_url 字段
ALTER TABLE book 
ADD COLUMN IF NOT EXISTS `external_url` VARCHAR(500) DEFAULT NULL COMMENT '外部链接(购买链接)' AFTER `tags`;

-- 显示表结构
SHOW COLUMNS FROM user_enrollment;
SHOW COLUMNS FROM news;
SHOW COLUMNS FROM book;
