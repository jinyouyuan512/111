-- 创建数据库
CREATE DATABASE IF NOT EXISTS jiyi_creative DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE jiyi_creative;

-- 设计大赛表
CREATE TABLE IF NOT EXISTS `contest` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '大赛ID',
    `title` VARCHAR(200) NOT NULL COMMENT '大赛标题',
    `description` TEXT COMMENT '大赛描述',
    `theme` VARCHAR(100) NOT NULL COMMENT '主题',
    `cover_image` VARCHAR(255) COMMENT '封面图片',
    `prize_pool` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '奖金池',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `voting_end_time` DATETIME COMMENT '投票截止时间',
    `status` VARCHAR(20) NOT NULL DEFAULT 'upcoming' COMMENT '状态: upcoming, ongoing, voting, completed',
    `participant_count` INT NOT NULL DEFAULT 0 COMMENT '参赛人数',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_time` (`start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设计大赛表';

-- 创意征集表
CREATE TABLE IF NOT EXISTS `creative_call` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '征集ID',
    `title` VARCHAR(200) NOT NULL COMMENT '征集标题',
    `description` TEXT COMMENT '征集描述',
    `requirements` TEXT COMMENT '要求说明',
    `budget` DECIMAL(10,2) COMMENT '预算',
    `deadline` DATETIME NOT NULL COMMENT '截止时间',
    `status` VARCHAR(20) NOT NULL DEFAULT 'open' COMMENT '状态: open, closed, completed',
    `publisher_id` BIGINT NOT NULL COMMENT '发布者ID',
    `publisher_type` VARCHAR(20) NOT NULL COMMENT '发布者类型: platform, enterprise',
    `submission_count` INT NOT NULL DEFAULT 0 COMMENT '投稿数量',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_publisher` (`publisher_id`, `publisher_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='创意征集表';

-- 设计作品表
CREATE TABLE IF NOT EXISTS `design` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '作品ID',
    `designer_id` BIGINT NOT NULL COMMENT '设计师ID',
    `contest_id` BIGINT COMMENT '大赛ID',
    `call_id` BIGINT COMMENT '征集ID',
    `title` VARCHAR(200) NOT NULL COMMENT '作品标题',
    `description` TEXT COMMENT '作品描述',
    `design_concept` TEXT COMMENT '设计理念',
    `files` TEXT NOT NULL COMMENT '文件URLs(JSON数组)',
    `cover_image` VARCHAR(255) COMMENT '封面图片',
    `copyright_statement` TEXT COMMENT '版权声明',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态: pending, approved, rejected, published',
    `reject_reason` TEXT COMMENT '拒绝原因',
    `votes` INT NOT NULL DEFAULT 0 COMMENT '票数',
    `views` INT NOT NULL DEFAULT 0 COMMENT '浏览量',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_designer_id` (`designer_id`),
    KEY `idx_contest_id` (`contest_id`),
    KEY `idx_call_id` (`call_id`),
    KEY `idx_status` (`status`),
    KEY `idx_votes` (`votes`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设计作品表';

-- 作品标签表
CREATE TABLE IF NOT EXISTS `design_tag` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '标签ID',
    `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
    `usage_count` INT NOT NULL DEFAULT 0 COMMENT '使用次数',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作品标签表';

-- 作品标签关联表
CREATE TABLE IF NOT EXISTS `design_tag_relation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `design_id` BIGINT NOT NULL COMMENT '作品ID',
    `tag_id` BIGINT NOT NULL COMMENT '标签ID',
    PRIMARY KEY (`id`),
    KEY `idx_design_id` (`design_id`),
    KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作品标签关联表';

-- 投票记录表
CREATE TABLE IF NOT EXISTS `vote_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `design_id` BIGINT NOT NULL COMMENT '作品ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '投票时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_design` (`user_id`, `design_id`),
    KEY `idx_design_id` (`design_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='投票记录表';

-- 奖励记录表
CREATE TABLE IF NOT EXISTS `reward_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `designer_id` BIGINT NOT NULL COMMENT '设计师ID',
    `design_id` BIGINT NOT NULL COMMENT '作品ID',
    `contest_id` BIGINT COMMENT '大赛ID',
    `call_id` BIGINT COMMENT '征集ID',
    `type` VARCHAR(20) NOT NULL COMMENT '类型: prize, royalty, bonus',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态: pending, paid, failed',
    `paid_at` DATETIME COMMENT '支付时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_designer_id` (`designer_id`),
    KEY `idx_design_id` (`design_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='奖励记录表';

-- 设计师资料表
CREATE TABLE IF NOT EXISTS `designer_profile` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `bio` TEXT COMMENT '个人简介',
    `skills` TEXT COMMENT '技能标签(JSON数组)',
    `portfolio_url` VARCHAR(255) COMMENT '作品集链接',
    `experience_years` INT COMMENT '从业年限',
    `rating` DECIMAL(3,2) NOT NULL DEFAULT 0 COMMENT '评分',
    `completed_projects` INT NOT NULL DEFAULT 0 COMMENT '完成项目数',
    `verified` TINYINT NOT NULL DEFAULT 0 COMMENT '是否认证',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设计师资料表';

-- 设计需求表
CREATE TABLE IF NOT EXISTS `design_requirement` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '需求ID',
    `enterprise_id` BIGINT NOT NULL COMMENT '企业ID',
    `title` VARCHAR(200) NOT NULL COMMENT '需求标题',
    `description` TEXT NOT NULL COMMENT '需求描述',
    `requirements` TEXT COMMENT '具体要求',
    `budget` DECIMAL(10,2) COMMENT '预算',
    `deadline` DATETIME NOT NULL COMMENT '截止时间',
    `status` VARCHAR(20) NOT NULL DEFAULT 'open' COMMENT '状态: open, matched, in_progress, completed, cancelled',
    `matched_designer_id` BIGINT COMMENT '匹配的设计师ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_enterprise_id` (`enterprise_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设计需求表';

-- 设计师匹配记录表
CREATE TABLE IF NOT EXISTS `designer_match` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `requirement_id` BIGINT NOT NULL COMMENT '需求ID',
    `designer_id` BIGINT NOT NULL COMMENT '设计师ID',
    `match_score` DECIMAL(5,2) NOT NULL COMMENT '匹配分数',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态: pending, accepted, rejected',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_requirement_id` (`requirement_id`),
    KEY `idx_designer_id` (`designer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设计师匹配记录表';
