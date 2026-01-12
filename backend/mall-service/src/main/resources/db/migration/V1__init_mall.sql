-- 商城数据库初始化脚本

-- 创建商品表
CREATE TABLE IF NOT EXISTS `product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
  `category` VARCHAR(50) NOT NULL COMMENT '商品分类',
  `description` TEXT COMMENT '商品描述',
  `cultural_background` TEXT COMMENT '文化背景',
  `icon` VARCHAR(50) COMMENT '商品图标',
  `color` VARCHAR(100) COMMENT '商品颜色',
  `price` DECIMAL(10,2) NOT NULL COMMENT '商品价格',
  `stock` INT NOT NULL DEFAULT 0 COMMENT '库存数量',
  `sales` INT NOT NULL DEFAULT 0 COMMENT '销量',
  `designer` VARCHAR(100) COMMENT '设计师名称',
  `in_stock` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否有货',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_sales` (`sales`),
  KEY `idx_price` (`price`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- 创建订单表
CREATE TABLE IF NOT EXISTS `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_number` VARCHAR(50) NOT NULL COMMENT '订单号',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
  `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '订单状态',
  `payment_method` VARCHAR(50) COMMENT '支付方式',
  `shipping_address` TEXT COMMENT '收货地址',
  `tracking_number` VARCHAR(100) COMMENT '物流单号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_number` (`order_number`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 创建订单项表
CREATE TABLE IF NOT EXISTS `order_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  `product_name` VARCHAR(200) NOT NULL COMMENT '商品名称',
  `product_icon` VARCHAR(50) COMMENT '商品图标',
  `product_color` VARCHAR(100) COMMENT '商品颜色',
  `price` DECIMAL(10,2) NOT NULL COMMENT '商品单价',
  `quantity` INT NOT NULL COMMENT '购买数量',
  `subtotal` DECIMAL(10,2) NOT NULL COMMENT '小计金额',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单项表';

-- 插入测试商品数据
INSERT INTO `product` (`name`, `category`, `description`, `cultural_background`, `icon`, `color`, `price`, `stock`, `sales`, `designer`, `in_stock`) VALUES
('红色记忆帆布包', '创意生活', '经典红色设计，传承革命精神', '以红色为主题，融入革命元素', '🎒', 'linear-gradient(135deg, #c41e3a, #8b1e3f)', 128.00, 100, 2300, NULL, 1),
('西柏坡笔记本', '文化周边', '精美笔记本，记录红色历史', '西柏坡主题设计', '📖', 'linear-gradient(135deg, #d4956c, #c41e3a)', 68.00, 200, 1800, NULL, 1),
('革命海报装饰画', '文化周边', '经典海报复刻，装点红色空间', '复刻经典革命海报', '🖼️', 'linear-gradient(135deg, #8b1e3f, #d4956c)', 198.00, 50, 1500, NULL, 0),
('燕赵印象艺术画', '设计师推荐', '设计师原创作品', '河北燕赵文化主题', '🎨', 'linear-gradient(135deg, #c41e3a, #d4956c)', 388.00, 30, 980, '张艺', 1),
('红色文化陶瓷杯', '设计师推荐', '精美陶瓷杯，日常使用', '红色文化元素设计', '🏺', 'linear-gradient(135deg, #8b1e3f, #c41e3a)', 158.00, 150, 1260, '李明', 1),
('传承系列丝巾', '设计师推荐', '高档丝巾，传承文化', '传统图案现代演绎', '🧣', 'linear-gradient(135deg, #d4956c, #8b1e3f)', 268.00, 80, 1120, '王芳', 0),
('红色经典书籍', '文化周边', '红色经典文学作品', '精选红色经典', '📚', 'linear-gradient(135deg, #8b1e3f, #c41e3a)', 88.00, 300, 3200, NULL, 1),
('京剧脸谱摆件', '文化周边', '传统京剧脸谱工艺品', '京剧文化传承', '🎭', 'linear-gradient(135deg, #c41e3a, #d4956c)', 168.00, 60, 760, NULL, 1),
('书法套装礼盒', '文化周边', '书法用品套装', '传统书法文化', '🖋️', 'linear-gradient(135deg, #d4956c, #8b1e3f)', 298.00, 40, 540, NULL, 1),
('红色主题马克杯', '创意生活', '日常使用马克杯', '红色主题设计', '☕', 'linear-gradient(135deg, #c41e3a, #8b1e3f)', 78.00, 250, 2100, NULL, 1),
('香薰蜡烛礼盒', '创意生活', '香薰蜡烛套装', '放松身心', '🕯️', 'linear-gradient(135deg, #8b1e3f, #d4956c)', 138.00, 120, 990, NULL, 1),
('文创礼品套装', '创意生活', '精选文创商品组合', '多件套装优惠', '🎁', 'linear-gradient(135deg, #d4956c, #c41e3a)', 388.00, 50, 640, NULL, 0),
('红色主题红包', '创意生活', '节日红包套装', '传统节日用品', '🧧', 'linear-gradient(135deg, #a61e4d, #c41e3a)', 36.00, 500, 2800, NULL, 1),
('文化灯笼摆件', '创意生活', '传统灯笼装饰', '节日装饰品', '🏮', 'linear-gradient(135deg, #c41e3a, #ff9f43)', 156.00, 80, 450, NULL, 1),
('纪念钢笔', '文化周边', '高档钢笔礼盒', '书写工具', '🖊️', 'linear-gradient(135deg, #8b1e3f, #6c757d)', 216.00, 100, 780, NULL, 1),
('主题袜子礼盒', '创意生活', '创意袜子套装', '日常穿着', '🧦', 'linear-gradient(135deg, #c41e3a, #e74c3c)', 96.00, 200, 1340, NULL, 1),
('特制手账本', '文化周边', '精美手账本', '记录生活', '📔', 'linear-gradient(135deg, #d4956c, #c41e3a)', 128.00, 150, 990, NULL, 1),
('红色旅游地图', '文化周边', '河北红色景点地图', '旅游必备', '🗺️', 'linear-gradient(135deg, #c41e3a, #8b1e3f)', 56.00, 300, 1890, NULL, 1),
('刺绣徽章套装', '设计师推荐', '手工刺绣徽章', '传统工艺', '🧵', 'linear-gradient(135deg, #8b1e3f, #d4956c)', 146.00, 70, 610, '刘瑶', 0),
('礼盒包装服务', '创意生活', '精美礼盒包装', '送礼首选', '📦', 'linear-gradient(135deg, #c41e3a, #8b1e3f)', 28.00, 1000, 3400, NULL, 1);
