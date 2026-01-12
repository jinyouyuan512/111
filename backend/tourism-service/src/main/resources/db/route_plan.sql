-- 用户保存的行程规划表
USE jiyi_tourism;

-- 添加字段到 user_itinerary 表
ALTER TABLE `user_itinerary` 
ADD COLUMN IF NOT EXISTS `plan_data` JSON COMMENT '行程规划详情(JSON)',
ADD COLUMN IF NOT EXISTS `spot_names` VARCHAR(500) COMMENT '景点名称列表',
ADD COLUMN IF NOT EXISTS `estimated_cost` DECIMAL(10,2) DEFAULT 0 COMMENT '预估费用',
ADD COLUMN IF NOT EXISTS `save_count` INT DEFAULT 0 COMMENT '被收藏次数',
ADD COLUMN IF NOT EXISTS `is_public` TINYINT DEFAULT 0 COMMENT '是否公开';

-- 更新 route 表，添加更多统计字段
ALTER TABLE `route`
ADD COLUMN IF NOT EXISTS `creator_id` BIGINT COMMENT '创建者ID',
ADD COLUMN IF NOT EXISTS `spot_names` VARCHAR(500) COMMENT '景点名称列表',
ADD COLUMN IF NOT EXISTS `plan_data` JSON COMMENT '行程规划详情(JSON)',
ADD COLUMN IF NOT EXISTS `rating` DECIMAL(3,2) DEFAULT 4.5 COMMENT '评分';

-- 插入一些热门路线数据
INSERT INTO `route` (`name`, `description`, `days`, `estimated_cost`, `difficulty`, `status`, `view_count`, `booking_count`, `spot_names`, `rating`) VALUES
('西柏坡红色经典一日游', '探访新中国从这里走来的革命圣地', 1, 200, 'easy', 'active', 12580, 3560, '西柏坡纪念馆', 4.9),
('白洋淀+冉庄抗战寻迹', '重温华北平原抗战历史', 1, 300, 'easy', 'active', 9870, 2890, '白洋淀雁翎队纪念馆,冉庄地道战遗址', 4.8),
('太行山红色生态游', '狼牙山英雄壮举与塞罕坝绿色奇迹', 2, 800, 'medium', 'active', 8560, 2150, '狼牙山五壮士纪念地,塞罕坝展览馆', 4.8),
('河北红色全景游', '河北红色景点深度游览', 3, 1500, 'medium', 'active', 6780, 1680, '西柏坡纪念馆,狼牙山五壮士纪念地,白洋淀雁翎队纪念馆,李大钊纪念馆', 4.9),
('革命先驱纪念之旅', '缅怀李大钊等革命先驱', 1, 150, 'easy', 'active', 4560, 1230, '李大钊纪念馆', 4.8),
('地道战体验游', '亲身体验抗战时期的地道战', 1, 180, 'easy', 'active', 5430, 1560, '冉庄地道战遗址', 4.7)
ON DUPLICATE KEY UPDATE `view_count` = VALUES(`view_count`);
