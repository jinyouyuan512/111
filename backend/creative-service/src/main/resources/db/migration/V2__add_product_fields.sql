-- 为设计作品表添加商品化相关字段

ALTER TABLE `design` 
ADD COLUMN `product_status` VARCHAR(20) DEFAULT 'none' COMMENT '商品化状态: none, pending, approved, rejected, published' AFTER `status`,
ADD COLUMN `product_id` BIGINT COMMENT '关联的商品ID' AFTER `product_status`,
ADD COLUMN `product_price` DECIMAL(10,2) COMMENT '建议售价' AFTER `product_id`,
ADD COLUMN `product_description` TEXT COMMENT '商品描述' AFTER `product_price`,
ADD COLUMN `product_apply_time` DATETIME COMMENT '申请上架时间' AFTER `product_description`,
ADD COLUMN `product_approve_time` DATETIME COMMENT '审核通过时间' AFTER `product_apply_time`,
ADD COLUMN `product_reject_reason` TEXT COMMENT '拒绝上架原因' AFTER `product_approve_time`;

-- 添加索引
ALTER TABLE `design` ADD KEY `idx_product_status` (`product_status`);
ALTER TABLE `design` ADD KEY `idx_product_id` (`product_id`);
