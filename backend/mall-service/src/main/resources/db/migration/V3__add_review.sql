-- 商品评论表
CREATE TABLE IF NOT EXISTS product_review (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    user_id BIGINT,
    username VARCHAR(50),
    avatar VARCHAR(255),
    rating INT DEFAULT 5,
    content TEXT,
    images VARCHAR(1000),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_product_id (product_id)
);

-- 插入示例评论数据
INSERT INTO product_review (product_id, user_id, username, rating, content, create_time) VALUES
(1, 1, '用户***123', 5, '质量很好，设计精美，很有纪念意义！包装也很精致，送人很合适。', NOW() - INTERVAL 2 DAY),
(1, 2, '红色***爱好者', 5, '做工精细，红色文化元素融入得很好，值得收藏！', NOW() - INTERVAL 4 DAY),
(1, 3, '文化***传承', 4, '整体不错，就是物流稍慢，但产品本身很满意。', NOW() - INTERVAL 7 DAY),
(1, 4, '历史***迷', 5, '非常有意义的文创产品，买来送给孩子学习历史。', NOW() - INTERVAL 10 DAY),
(1, 5, '收藏***家', 5, '品质上乘，设计独特，已经是第二次购买了！', NOW() - INTERVAL 15 DAY),
(2, 1, '游客***888', 5, '很有特色的产品，朋友都说好看！', NOW() - INTERVAL 1 DAY),
(2, 2, '文创***粉', 4, '设计很用心，就是价格稍贵。', NOW() - INTERVAL 3 DAY),
(2, 3, '红色***记忆', 5, '非常满意，包装精美，送礼首选！', NOW() - INTERVAL 5 DAY),
(3, 1, '学生***党', 5, '买来学习用的，很有教育意义。', NOW() - INTERVAL 2 DAY),
(3, 2, '教师***王', 5, '给学生们买的，孩子们都很喜欢！', NOW() - INTERVAL 6 DAY);
