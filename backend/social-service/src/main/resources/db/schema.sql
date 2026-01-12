-- 创建数据库
CREATE DATABASE IF NOT EXISTS jiyi_social DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE jiyi_social;

-- 动态表
CREATE TABLE IF NOT EXISTS `post` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '动态ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `content` TEXT NOT NULL COMMENT '内容',
    `location_id` BIGINT COMMENT '位置ID',
    `location_name` VARCHAR(100) COMMENT '位置名称',
    `latitude` DECIMAL(10,7) COMMENT '纬度',
    `longitude` DECIMAL(10,7) COMMENT '经度',
    `type` VARCHAR(20) NOT NULL DEFAULT 'normal' COMMENT '类型: normal, checkin, share',
    `visibility` VARCHAR(20) NOT NULL DEFAULT 'public' COMMENT '可见性: public, friends, private',
    `likes` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    `comments` INT NOT NULL DEFAULT 0 COMMENT '评论数',
    `shares` INT NOT NULL DEFAULT 0 COMMENT '转发数',
    `views` INT NOT NULL DEFAULT 0 COMMENT '浏览数',
    `status` VARCHAR(20) NOT NULL DEFAULT 'published' COMMENT '状态: published, deleted, hidden',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_location_id` (`location_id`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='动态表';

-- 媒体文件表
CREATE TABLE IF NOT EXISTS `media_file` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '文件ID',
    `post_id` BIGINT NOT NULL COMMENT '动态ID',
    `type` VARCHAR(20) NOT NULL COMMENT '类型: image, video',
    `url` VARCHAR(255) NOT NULL COMMENT '文件URL',
    `thumbnail` VARCHAR(255) COMMENT '缩略图URL',
    `width` INT COMMENT '宽度',
    `height` INT COMMENT '高度',
    `duration` INT COMMENT '时长(秒)',
    `file_size` BIGINT COMMENT '文件大小(字节)',
    `order_num` INT NOT NULL DEFAULT 0 COMMENT '排序',
    PRIMARY KEY (`id`),
    KEY `idx_post_id` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='媒体文件表';

-- 话题表
CREATE TABLE IF NOT EXISTS `topic` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '话题ID',
    `name` VARCHAR(100) NOT NULL COMMENT '话题名称',
    `description` TEXT COMMENT '话题描述',
    `cover_image` VARCHAR(255) COMMENT '封面图片',
    `post_count` INT NOT NULL DEFAULT 0 COMMENT '动态数量',
    `follow_count` INT NOT NULL DEFAULT 0 COMMENT '关注数',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览数/热度',
    `status` VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态: active, inactive',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`),
    KEY `idx_post_count` (`post_count`),
    KEY `idx_view_count` (`view_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='话题表';

-- 动态话题关联表
CREATE TABLE IF NOT EXISTS `post_topic` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `post_id` BIGINT NOT NULL COMMENT '动态ID',
    `topic_id` BIGINT NOT NULL COMMENT '话题ID',
    PRIMARY KEY (`id`),
    KEY `idx_post_id` (`post_id`),
    KEY `idx_topic_id` (`topic_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='动态话题关联表';

-- 点赞记录表
CREATE TABLE IF NOT EXISTS `like_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `target_type` VARCHAR(20) NOT NULL COMMENT '目标类型: post, comment',
    `target_id` BIGINT NOT NULL COMMENT '目标ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_target` (`user_id`, `target_type`, `target_id`),
    KEY `idx_target` (`target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞记录表';

-- 评论表
CREATE TABLE IF NOT EXISTS `comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `post_id` BIGINT NOT NULL COMMENT '动态ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `parent_id` BIGINT COMMENT '父评论ID',
    `reply_to_user_id` BIGINT COMMENT '回复的用户ID',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `likes` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    `status` VARCHAR(20) NOT NULL DEFAULT 'published' COMMENT '状态: published, deleted, hidden',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_post_id` (`post_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 转发记录表
CREATE TABLE IF NOT EXISTS `share_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `post_id` BIGINT NOT NULL COMMENT '动态ID',
    `comment` TEXT COMMENT '转发评论',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_post_id` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='转发记录表';

-- 私信表
CREATE TABLE IF NOT EXISTS `private_message` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    `from_user_id` BIGINT NOT NULL COMMENT '发送者ID',
    `to_user_id` BIGINT NOT NULL COMMENT '接收者ID',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `type` VARCHAR(20) NOT NULL DEFAULT 'text' COMMENT '类型: text, image, video',
    `media_url` VARCHAR(255) COMMENT '媒体URL',
    `read_status` TINYINT NOT NULL DEFAULT 0 COMMENT '已读状态: 0-未读, 1-已读',
    `read_at` DATETIME COMMENT '阅读时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_from_user` (`from_user_id`),
    KEY `idx_to_user` (`to_user_id`),
    KEY `idx_read_status` (`read_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='私信表';

-- 关注关系表
CREATE TABLE IF NOT EXISTS `follow` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `follower_id` BIGINT NOT NULL COMMENT '关注者ID',
    `followee_id` BIGINT NOT NULL COMMENT '被关注者ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_follower_followee` (`follower_id`, `followee_id`),
    KEY `idx_follower` (`follower_id`),
    KEY `idx_followee` (`followee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关注关系表';

-- 打卡记录表
CREATE TABLE IF NOT EXISTS `checkin` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '打卡ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `location_id` BIGINT NOT NULL COMMENT '位置ID',
    `location_name` VARCHAR(100) NOT NULL COMMENT '位置名称',
    `latitude` DECIMAL(10,7) NOT NULL COMMENT '纬度',
    `longitude` DECIMAL(10,7) NOT NULL COMMENT '经度',
    `post_id` BIGINT COMMENT '关联动态ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '打卡时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_location_id` (`location_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='打卡记录表';

-- 成就徽章表
CREATE TABLE IF NOT EXISTS `badge` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '徽章ID',
    `name` VARCHAR(50) NOT NULL COMMENT '徽章名称',
    `description` TEXT COMMENT '徽章描述',
    `icon` VARCHAR(255) NOT NULL COMMENT '图标URL',
    `type` VARCHAR(20) NOT NULL COMMENT '类型: checkin, post, social, learning',
    `condition_type` VARCHAR(50) NOT NULL COMMENT '获得条件类型',
    `condition_value` INT NOT NULL COMMENT '条件值',
    `points` INT NOT NULL DEFAULT 0 COMMENT '获得积分',
    `rarity` VARCHAR(20) NOT NULL DEFAULT 'common' COMMENT '稀有度: common, rare, epic, legendary',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成就徽章表';

-- 用户徽章表
CREATE TABLE IF NOT EXISTS `user_badge` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `badge_id` BIGINT NOT NULL COMMENT '徽章ID',
    `obtained_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '获得时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_badge` (`user_id`, `badge_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_badge_id` (`badge_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户徽章表';

-- 内容举报表
CREATE TABLE IF NOT EXISTS `report` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '举报ID',
    `reporter_id` BIGINT NOT NULL COMMENT '举报者ID',
    `target_type` VARCHAR(20) NOT NULL COMMENT '目标类型: post, comment, user',
    `target_id` BIGINT NOT NULL COMMENT '目标ID',
    `reason` VARCHAR(50) NOT NULL COMMENT '举报原因',
    `description` TEXT COMMENT '详细说明',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态: pending, processing, resolved, rejected',
    `handler_id` BIGINT COMMENT '处理人ID',
    `handle_result` TEXT COMMENT '处理结果',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '举报时间',
    `handled_at` DATETIME COMMENT '处理时间',
    PRIMARY KEY (`id`),
    KEY `idx_reporter` (`reporter_id`),
    KEY `idx_target` (`target_type`, `target_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容举报表';
