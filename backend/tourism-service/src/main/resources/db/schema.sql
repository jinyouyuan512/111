-- 创建数据库
CREATE DATABASE IF NOT EXISTS jiyi_tourism DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE jiyi_tourism;

-- 景点表
CREATE TABLE IF NOT EXISTS `attraction` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '景点ID',
    `name` VARCHAR(100) NOT NULL COMMENT '景点名称',
    `category` VARCHAR(50) COMMENT '分类',
    `description` TEXT COMMENT '景点描述',
    `address` VARCHAR(255) COMMENT '地址',
    `latitude` DECIMAL(10,7) COMMENT '纬度',
    `longitude` DECIMAL(10,7) COMMENT '经度',
    `visit_duration` INT NOT NULL DEFAULT 60 COMMENT '建议游览时长(分钟)',
    `ticket_price` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '门票价格',
    `opening_hours` VARCHAR(100) COMMENT '开放时间',
    `images` TEXT COMMENT '图片URLs(JSON数组)',
    `rating` DECIMAL(3,2) NOT NULL DEFAULT 0 COMMENT '评分(0-5)',
    `status` VARCHAR(20) NOT NULL DEFAULT 'open' COMMENT '状态: open, closed, maintenance',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`),
    KEY `idx_location` (`latitude`, `longitude`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='景点表';

-- 旅游路线表
CREATE TABLE IF NOT EXISTS `route` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '路线ID',
    `name` VARCHAR(100) NOT NULL COMMENT '路线名称',
    `description` TEXT COMMENT '路线描述',
    `days` INT NOT NULL DEFAULT 1 COMMENT '天数',
    `estimated_cost` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '预估费用',
    `difficulty` VARCHAR(20) NOT NULL DEFAULT 'easy' COMMENT '难度: easy, medium, hard',
    `season` VARCHAR(50) COMMENT '适合季节',
    `tags` TEXT COMMENT '标签(JSON数组)',
    `cover_image` VARCHAR(255) COMMENT '封面图片',
    `status` VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态: active, inactive',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `booking_count` INT NOT NULL DEFAULT 0 COMMENT '预订次数',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_days` (`days`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='旅游路线表';

-- 路线景点关联表
CREATE TABLE IF NOT EXISTS `route_attraction` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `route_id` BIGINT NOT NULL COMMENT '路线ID',
    `attraction_id` BIGINT NOT NULL COMMENT '景点ID',
    `day_num` INT NOT NULL DEFAULT 1 COMMENT '第几天',
    `order_num` INT NOT NULL DEFAULT 0 COMMENT '当天顺序',
    `visit_time` VARCHAR(50) COMMENT '游览时间段',
    `notes` TEXT COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_route_id` (`route_id`),
    KEY `idx_attraction_id` (`attraction_id`),
    KEY `idx_day_order` (`day_num`, `order_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='路线景点关联表';

-- 交通建议表
CREATE TABLE IF NOT EXISTS `transportation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `route_id` BIGINT NOT NULL COMMENT '路线ID',
    `from_attraction_id` BIGINT COMMENT '起点景点ID',
    `to_attraction_id` BIGINT NOT NULL COMMENT '终点景点ID',
    `type` VARCHAR(20) NOT NULL COMMENT '交通方式: bus, subway, taxi, walk',
    `duration` INT NOT NULL DEFAULT 0 COMMENT '耗时(分钟)',
    `cost` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '费用',
    `description` TEXT COMMENT '说明',
    PRIMARY KEY (`id`),
    KEY `idx_route_id` (`route_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='交通建议表';

-- 住宿建议表
CREATE TABLE IF NOT EXISTS `accommodation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `route_id` BIGINT NOT NULL COMMENT '路线ID',
    `day_num` INT NOT NULL COMMENT '第几天',
    `name` VARCHAR(100) NOT NULL COMMENT '住宿名称',
    `type` VARCHAR(20) NOT NULL COMMENT '类型: hotel, hostel, guesthouse',
    `address` VARCHAR(255) COMMENT '地址',
    `price_range` VARCHAR(50) COMMENT '价格区间',
    `rating` DECIMAL(3,2) COMMENT '评分',
    `contact` VARCHAR(100) COMMENT '联系方式',
    PRIMARY KEY (`id`),
    KEY `idx_route_id` (`route_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='住宿建议表';

-- 用户行程表
CREATE TABLE IF NOT EXISTS `user_itinerary` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '行程ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `route_id` BIGINT NOT NULL COMMENT '路线ID',
    `title` VARCHAR(100) NOT NULL COMMENT '行程标题',
    `start_date` DATE NOT NULL COMMENT '开始日期',
    `end_date` DATE NOT NULL COMMENT '结束日期',
    `status` VARCHAR(20) NOT NULL DEFAULT 'planned' COMMENT '状态: planned, ongoing, completed, cancelled',
    `notes` TEXT COMMENT '备注',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_route_id` (`route_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户行程表';

-- 门票预订表
CREATE TABLE IF NOT EXISTS `ticket_booking` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '预订ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `itinerary_id` BIGINT COMMENT '行程ID',
    `attraction_id` BIGINT NOT NULL COMMENT '景点ID',
    `visit_date` DATE NOT NULL COMMENT '游览日期',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
    `total_price` DECIMAL(10,2) NOT NULL COMMENT '总价',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态: pending, confirmed, cancelled',
    `booking_no` VARCHAR(50) NOT NULL COMMENT '预订编号',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_booking_no` (`booking_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_attraction_id` (`attraction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='门票预订表';

-- 天气预警表
CREATE TABLE IF NOT EXISTS `weather_alert` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '预警ID',
    `attraction_id` BIGINT NOT NULL COMMENT '景点ID',
    `alert_type` VARCHAR(20) NOT NULL COMMENT '预警类型: weather, closure, event',
    `severity` VARCHAR(20) NOT NULL COMMENT '严重程度: low, medium, high',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `content` TEXT NOT NULL COMMENT '内容',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME COMMENT '结束时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_attraction_id` (`attraction_id`),
    KEY `idx_time` (`start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='天气预警表';
