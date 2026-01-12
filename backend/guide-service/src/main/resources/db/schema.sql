-- 创建数据库
CREATE DATABASE IF NOT EXISTS jiyi_guide DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE jiyi_guide;

-- 景区表
CREATE TABLE IF NOT EXISTS `scenic_area` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '景区ID',
    `name` VARCHAR(100) NOT NULL COMMENT '景区名称',
    `description` TEXT COMMENT '景区描述',
    `address` VARCHAR(255) COMMENT '地址',
    `latitude` DECIMAL(10,7) NOT NULL COMMENT '纬度',
    `longitude` DECIMAL(10,7) NOT NULL COMMENT '经度',
    `boundary` TEXT COMMENT '边界坐标(GeoJSON)',
    `map_data` TEXT COMMENT '地图数据',
    `status` VARCHAR(20) NOT NULL DEFAULT 'open' COMMENT '状态: open, closed',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_location` (`latitude`, `longitude`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='景区表';

-- 景点表
CREATE TABLE IF NOT EXISTS `poi` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '景点ID',
    `scenic_area_id` BIGINT NOT NULL COMMENT '景区ID',
    `name` VARCHAR(100) NOT NULL COMMENT '景点名称',
    `description` TEXT COMMENT '景点描述',
    `latitude` DECIMAL(10,7) NOT NULL COMMENT '纬度',
    `longitude` DECIMAL(10,7) NOT NULL COMMENT '经度',
    `type` VARCHAR(20) NOT NULL COMMENT '类型: monument, museum, memorial, site',
    `trigger_radius` INT NOT NULL DEFAULT 50 COMMENT '触发半径(米)',
    `qr_code` VARCHAR(100) COMMENT '二维码标识',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_scenic_area_id` (`scenic_area_id`),
    KEY `idx_location` (`latitude`, `longitude`),
    KEY `idx_qr_code` (`qr_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='景点表';

-- 语音讲解表
CREATE TABLE IF NOT EXISTS `audio_guide` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '讲解ID',
    `poi_id` BIGINT NOT NULL COMMENT '景点ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `content` TEXT NOT NULL COMMENT '讲解文本',
    `audio_url` VARCHAR(255) NOT NULL COMMENT '音频文件URL',
    `duration` INT NOT NULL DEFAULT 0 COMMENT '时长(秒)',
    `language` VARCHAR(10) NOT NULL DEFAULT 'zh' COMMENT '语言: zh, en',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_poi_id` (`poi_id`),
    KEY `idx_language` (`language`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='语音讲解表';

-- AR场景表
CREATE TABLE IF NOT EXISTS `ar_scene` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'AR场景ID',
    `poi_id` BIGINT NOT NULL COMMENT '景点ID',
    `name` VARCHAR(100) NOT NULL COMMENT '场景名称',
    `description` TEXT COMMENT '场景描述',
    `scene_data_url` VARCHAR(255) NOT NULL COMMENT '场景数据URL',
    `preview_image` VARCHAR(255) COMMENT '预览图',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_poi_id` (`poi_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AR场景表';

-- 导览路线表
CREATE TABLE IF NOT EXISTS `guide_route` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '路线ID',
    `scenic_area_id` BIGINT NOT NULL COMMENT '景区ID',
    `name` VARCHAR(100) NOT NULL COMMENT '路线名称',
    `description` TEXT COMMENT '路线描述',
    `duration` INT NOT NULL DEFAULT 0 COMMENT '预计时长(分钟)',
    `distance` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '距离(公里)',
    `difficulty` VARCHAR(20) NOT NULL DEFAULT 'easy' COMMENT '难度: easy, medium, hard',
    `path_data` TEXT COMMENT '路径数据(GeoJSON)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_scenic_area_id` (`scenic_area_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='导览路线表';

-- 路线景点关联表
CREATE TABLE IF NOT EXISTS `route_poi` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `route_id` BIGINT NOT NULL COMMENT '路线ID',
    `poi_id` BIGINT NOT NULL COMMENT '景点ID',
    `order_num` INT NOT NULL DEFAULT 0 COMMENT '顺序',
    PRIMARY KEY (`id`),
    KEY `idx_route_id` (`route_id`),
    KEY `idx_poi_id` (`poi_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='路线景点关联表';

-- 用户游览记录表
CREATE TABLE IF NOT EXISTS `user_visit` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `scenic_area_id` BIGINT NOT NULL COMMENT '景区ID',
    `visit_date` DATE NOT NULL COMMENT '游览日期',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME COMMENT '结束时间',
    `status` VARCHAR(20) NOT NULL DEFAULT 'ongoing' COMMENT '状态: ongoing, completed',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_scenic_area_id` (`scenic_area_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户游览记录表';

-- 游览轨迹表
CREATE TABLE IF NOT EXISTS `visit_track` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '轨迹ID',
    `visit_id` BIGINT NOT NULL COMMENT '游览记录ID',
    `latitude` DECIMAL(10,7) NOT NULL COMMENT '纬度',
    `longitude` DECIMAL(10,7) NOT NULL COMMENT '经度',
    `recorded_at` DATETIME NOT NULL COMMENT '记录时间',
    PRIMARY KEY (`id`),
    KEY `idx_visit_id` (`visit_id`),
    KEY `idx_time` (`recorded_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='游览轨迹表';

-- 游记表
CREATE TABLE IF NOT EXISTS `travel_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '游记ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `visit_id` BIGINT NOT NULL COMMENT '游览记录ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `content` TEXT COMMENT '内容',
    `images` TEXT COMMENT '图片URLs(JSON数组)',
    `track_map_url` VARCHAR(255) COMMENT '轨迹地图URL',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_visit_id` (`visit_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='游记表';
