-- 添加 category_type 和 tags 字段到 design 表

USE jiyi_creative;

-- 添加分类类型字段
ALTER TABLE `design` 
ADD COLUMN `category_type` INT COMMENT '作品分类: 1-海报设计, 2-Logo设计, 3-文创产品, 4-视频动画' AFTER `title`;

-- 添加标签字段
ALTER TABLE `design` 
ADD COLUMN `tags` VARCHAR(500) COMMENT '作品标签，逗号分隔' AFTER `copyright_statement`;

-- 为现有数据设置默认值
UPDATE `design` SET `category_type` = 1 WHERE `category_type` IS NULL;
