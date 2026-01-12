-- 智慧旅游数据库扩展表结构

-- 语音讲解表
CREATE TABLE IF NOT EXISTS `audio_guide` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `spot_id` BIGINT NOT NULL COMMENT '景点ID',
    `title` VARCHAR(100) NOT NULL COMMENT '讲解标题',
    `duration` INT NOT NULL DEFAULT 180 COMMENT '时长(秒)',
    `transcript` TEXT COMMENT '文字稿',
    `audio_url` VARCHAR(500) COMMENT '音频URL',
    `order_num` INT NOT NULL DEFAULT 1 COMMENT '排序',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_spot_id` (`spot_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='语音讲解表';

-- 用户行程保存表（如果不存在）
CREATE TABLE IF NOT EXISTS `user_trip_plan` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(200) NOT NULL COMMENT '行程标题',
    `description` TEXT COMMENT '行程描述',
    `plan_data` JSON COMMENT '行程数据(JSON)',
    `start_date` DATE COMMENT '开始日期',
    `end_date` DATE COMMENT '结束日期',
    `total_distance` DECIMAL(10,2) COMMENT '总距离(km)',
    `estimated_cost` DECIMAL(10,2) COMMENT '预估费用',
    `status` VARCHAR(20) DEFAULT 'draft' COMMENT '状态',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行程保存表';

-- 景点实时状态表
CREATE TABLE IF NOT EXISTS `spot_realtime_status` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `spot_id` BIGINT NOT NULL COMMENT '景点ID',
    `current_visitors` INT DEFAULT 0 COMMENT '当前游客数',
    `max_capacity` INT DEFAULT 5000 COMMENT '最大容量',
    `crowd_level` VARCHAR(20) DEFAULT 'low' COMMENT '拥挤程度',
    `wait_time` INT DEFAULT 0 COMMENT '等待时间(分钟)',
    `is_open` TINYINT(1) DEFAULT 1 COMMENT '是否开放',
    `notices` TEXT COMMENT '公告(JSON数组)',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_spot_id` (`spot_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='景点实时状态表';
