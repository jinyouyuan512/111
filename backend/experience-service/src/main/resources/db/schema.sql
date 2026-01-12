-- 体验馆数据库表结构

-- 场景表
CREATE TABLE IF NOT EXISTS `scene` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(100) NOT NULL COMMENT '场景名称',
  `description` TEXT COMMENT '场景描述',
  `era` VARCHAR(50) COMMENT '时代背景',
  `duration` INT COMMENT '体验时长（分钟）',
  `thumbnail` VARCHAR(255) COMMENT '预览图URL',
  `model_url` VARCHAR(255) COMMENT '3D模型URL',
  `interaction_count` INT DEFAULT 0 COMMENT '交互点数量',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场景表';

-- 交互点表
CREATE TABLE IF NOT EXISTS `interaction_point` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `scene_id` BIGINT NOT NULL COMMENT '场景ID',
  `title` VARCHAR(100) NOT NULL COMMENT '交互点标题',
  `type` VARCHAR(20) NOT NULL COMMENT '交互点类型',
  `position_x` INT COMMENT 'X坐标位置（百分比）',
  `position_y` INT COMMENT 'Y坐标位置（百分比）',
  `position_z` INT COMMENT 'Z坐标位置（百分比）',
  `content` TEXT COMMENT '交互点内容描述',
  `media_url` VARCHAR(255) COMMENT '媒体资源URL',
  `sort_order` INT DEFAULT 0 COMMENT '排序顺序',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_scene_id` (`scene_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交互点表';

-- 用户进度表
CREATE TABLE IF NOT EXISTS `user_progress` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `scene_id` BIGINT NOT NULL COMMENT '场景ID',
  `progress` INT DEFAULT 0 COMMENT '完成进度（0-100）',
  `completed_interactions` TEXT COMMENT '已完成的交互点ID列表',
  `start_time` DATETIME COMMENT '体验开始时间',
  `last_update_time` DATETIME COMMENT '最后更新时间',
  `completed` TINYINT DEFAULT 0 COMMENT '是否已完成',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_scene` (`user_id`, `scene_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_scene_id` (`scene_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户进度表';

-- 证书表
CREATE TABLE IF NOT EXISTS `certificate` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `scene_id` BIGINT NOT NULL COMMENT '场景ID',
  `certificate_no` VARCHAR(100) NOT NULL COMMENT '证书编号',
  `issue_date` DATE NOT NULL COMMENT '颁发日期',
  `certificate_url` VARCHAR(255) COMMENT '证书图片URL',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_scene` (`user_id`, `scene_id`),
  UNIQUE KEY `uk_certificate_no` (`certificate_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_scene_id` (`scene_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='证书表';
