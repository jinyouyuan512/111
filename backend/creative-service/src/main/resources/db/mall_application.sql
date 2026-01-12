-- 商城上架申请表
CREATE TABLE IF NOT EXISTS `mall_application` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `design_id` BIGINT NOT NULL COMMENT '关联的设计作品ID',
    `user_id` BIGINT NOT NULL COMMENT '申请人ID',
    `product_name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `category` VARCHAR(100) COMMENT '商品分类',
    `description` TEXT COMMENT '商品描述',
    `suggested_price` DECIMAL(10,2) COMMENT '建议价格',
    `initial_stock` INT DEFAULT 100 COMMENT '初始库存',
    `icon` VARCHAR(50) DEFAULT '🎁' COMMENT '商品图标',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态: pending-待审核, approved-已通过, rejected-已拒绝',
    `reviewer_id` BIGINT COMMENT '审核人ID',
    `review_comment` TEXT COMMENT '审核意见',
    `reviewed_at` DATETIME COMMENT '审核时间',
    `product_id` BIGINT COMMENT '生成的商品ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` INT DEFAULT 0,
    INDEX `idx_design_id` (`design_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城上架申请表';
