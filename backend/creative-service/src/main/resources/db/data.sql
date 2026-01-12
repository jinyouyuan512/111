USE jiyi_creative;

-- 插入设计大赛数据
INSERT INTO `contest` (`title`, `description`, `theme`, `cover_image`, `prize_pool`, `start_time`, `end_time`, `voting_end_time`, `status`, `participant_count`) VALUES
('红色文化创意设计大赛', '以河北红色文化为主题，设计具有时代特色的文创产品', '红色文化', '/images/contest/red-culture.jpg', 50000.00, '2024-01-01 00:00:00', '2024-03-31 23:59:59', '2024-04-15 23:59:59', 'ongoing', 156),
('革命精神传承设计赛', '用设计语言诠释革命精神，传承红色基因', '革命精神', '/images/contest/revolution-spirit.jpg', 30000.00, '2024-02-01 00:00:00', '2024-04-30 23:59:59', '2024-05-15 23:59:59', 'ongoing', 89),
('英雄人物形象设计赛', '为河北英雄人物设计现代化视觉形象', '英雄人物', '/images/contest/hero-image.jpg', 40000.00, '2024-03-01 00:00:00', '2024-05-31 23:59:59', '2024-06-15 23:59:59', 'ongoing', 67);

-- 插入创意征集数据
INSERT INTO `creative_call` (`title`, `description`, `requirements`, `budget`, `deadline`, `status`, `publisher_id`, `publisher_type`, `submission_count`) VALUES
('西柏坡纪念馆文创产品设计', '为西柏坡纪念馆设计系列文创产品，包括文具、服饰、纪念品等', '1. 体现西柏坡精神\n2. 实用性强\n3. 适合批量生产', 20000.00, '2024-06-30 23:59:59', 'open', 1, 'platform', 34),
('狼牙山五壮士主题海报设计', '设计一组狼牙山五壮士主题海报，用于景区宣传', '1. 尺寸：A1\n2. 风格现代\n3. 突出英雄气概', 15000.00, '2024-05-31 23:59:59', 'open', 1, 'platform', 28),
('红色旅游路线地图设计', '设计河北红色旅游路线地图，包括纸质版和电子版', '1. 包含主要红色景点\n2. 标注交通信息\n3. 美观易读', 25000.00, '2024-07-31 23:59:59', 'open', 1, 'platform', 19);

-- 插入设计作品数据
INSERT INTO `design` (`designer_id`, `contest_id`, `title`, `description`, `design_concept`, `files`, `cover_image`, `copyright_statement`, `status`, `votes`, `views`) VALUES
(1, 1, '红色印记系列文具', '以河北红色历史为灵感的文具套装设计', '将革命历史元素与现代设计语言相结合，打造既有文化内涵又实用美观的文具产品', '["https://example.com/files/design1-1.jpg", "https://example.com/files/design1-2.jpg", "https://example.com/files/design1-3.pdf"]', 'https://example.com/covers/design1.jpg', '本作品版权归设计师所有，未经授权不得商用', 'published', 234, 1567),
(2, 1, '革命之路主题笔记本', '以长征精神为主题的笔记本设计', '用插画形式展现革命历程，封面采用烫金工艺，内页设计简洁实用', '["https://example.com/files/design2-1.jpg", "https://example.com/files/design2-2.jpg"]', 'https://example.com/covers/design2.jpg', '本作品版权归设计师所有，未经授权不得商用', 'published', 189, 1234),
(3, 2, '英雄精神T恤设计', '以河北英雄人物为主题的T恤图案设计', '采用简约线条勾勒英雄形象，配以激励性文字，适合年轻人穿着', '["https://example.com/files/design3-1.jpg", "https://example.com/files/design3-2.jpg"]', 'https://example.com/covers/design3.jpg', '本作品版权归设计师所有，未经授权不得商用', 'published', 156, 987),
(4, 1, '红色记忆书签套装', '以河北红色景点为主题的书签设计', '每个书签代表一个红色景点，背面印有景点简介，既实用又有教育意义', '["https://example.com/files/design4-1.jpg"]', 'https://example.com/covers/design4.jpg', '本作品版权归设计师所有，未经授权不得商用', 'published', 145, 876),
(5, 3, '革命先烈肖像插画', '河北革命先烈现代插画形象设计', '用现代插画风格重新演绎革命先烈形象，使其更贴近当代审美', '["https://example.com/files/design5-1.jpg", "https://example.com/files/design5-2.jpg", "https://example.com/files/design5-3.jpg"]', 'https://example.com/covers/design5.jpg', '本作品版权归设计师所有，未经授权不得商用', 'published', 201, 1345);

-- 插入设计师资料数据
INSERT INTO `designer_profile` (`user_id`, `real_name`, `bio`, `skills`, `portfolio_url`, `experience_years`, `rating`, `completed_projects`, `verified`) VALUES
(1, '张设计', '专注文创产品设计5年，擅长将传统文化与现代设计相结合', '["平面设计", "产品设计", "品牌设计"]', 'https://portfolio.example.com/zhang', 5, 4.8, 23, 1),
(2, '李创意', '插画师，热爱红色文化，致力于用艺术传承革命精神', '["插画设计", "UI设计", "动画设计"]', 'https://portfolio.example.com/li', 3, 4.6, 15, 1),
(3, '王艺术', '服装设计师，擅长将文化元素融入服饰设计', '["服装设计", "图案设计", "面料设计"]', 'https://portfolio.example.com/wang', 7, 4.9, 31, 1),
(4, '赵美工', '平面设计师，专注于文化创意产品开发', '["平面设计", "包装设计", "VI设计"]', 'https://portfolio.example.com/zhao', 4, 4.7, 19, 1),
(5, '刘画师', '自由插画师，擅长人物肖像和场景插画', '["插画设计", "漫画设计", "概念设计"]', 'https://portfolio.example.com/liu', 6, 4.8, 27, 1);

-- 插入投票记录数据（示例）
INSERT INTO `vote_record` (`user_id`, `design_id`) VALUES
(10, 1), (11, 1), (12, 1),
(10, 2), (13, 2),
(14, 3), (15, 3),
(16, 4), (17, 4),
(18, 5), (19, 5), (20, 5);

-- 插入设计需求数据
INSERT INTO `design_requirement` (`enterprise_id`, `title`, `description`, `requirements`, `budget`, `deadline`, `status`) VALUES
(100, '企业文化墙设计', '为企业设计红色文化主题的文化墙', '1. 面积约50平米\n2. 体现企业与红色文化的结合\n3. 提供3D效果图', 30000.00, '2024-08-31 23:59:59', 'open'),
(101, '红色主题展厅设计', '设计红色文化主题展厅，包括空间规划和展陈设计', '1. 展厅面积200平米\n2. 包含多媒体展示\n3. 提供完整施工图', 80000.00, '2024-09-30 23:59:59', 'open');

