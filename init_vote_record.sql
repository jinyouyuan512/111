-- 初始化 vote_record 表
-- 在 MySQL 中执行此脚本

USE jiyi_creative;

-- 创建投票记录表（如果不存在）
CREATE TABLE IF NOT EXISTS `vote_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `design_id` BIGINT NOT NULL COMMENT '作品ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '投票时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_design` (`user_id`, `design_id`),
    KEY `idx_design_id` (`design_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='投票记录表';

-- 检查表是否创建成功
SHOW TABLES LIKE 'vote_record';

-- 查看表结构
DESCRIBE vote_record;

SELECT '投票记录表初始化完成！' AS message;
