-- =====================================================
-- 河北红色新闻与读物数据库初始化脚本
-- 在 MySQL 中执行此脚本以创建表和初始数据
-- =====================================================

USE jiyi_academy;

-- 河北红色新闻表
CREATE TABLE IF NOT EXISTS red_news (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '新闻标题',
    summary VARCHAR(500) COMMENT '新闻摘要',
    content TEXT COMMENT '新闻内容',
    cover_image VARCHAR(500) COMMENT '封面图片',
    category VARCHAR(50) NOT NULL COMMENT '分类',
    source VARCHAR(100) COMMENT '新闻来源',
    author VARCHAR(50) COMMENT '作者',
    view_count INT DEFAULT 0 COMMENT '阅读量',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    is_top TINYINT(1) DEFAULT 0 COMMENT '是否置顶',
    is_hot TINYINT(1) DEFAULT 0 COMMENT '是否热门',
    publish_time DATETIME COMMENT '发布时间',
    status VARCHAR(20) DEFAULT 'published' COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_category (category),
    INDEX idx_publish_time (publish_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='河北红色新闻表';

-- 红色读物表
CREATE TABLE IF NOT EXISTS red_book (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '书名',
    author VARCHAR(100) NOT NULL COMMENT '作者',
    publisher VARCHAR(100) COMMENT '出版社',
    isbn VARCHAR(20) COMMENT 'ISBN',
    cover_image VARCHAR(500) COMMENT '封面图片',
    description TEXT COMMENT '书籍简介',
    category VARCHAR(50) NOT NULL COMMENT '分类',
    page_count INT COMMENT '页数',
    publish_date VARCHAR(20) COMMENT '出版日期',
    rating DECIMAL(2,1) DEFAULT 0 COMMENT '评分',
    rating_count INT DEFAULT 0 COMMENT '评分人数',
    read_count INT DEFAULT 0 COMMENT '阅读量',
    favorite_count INT DEFAULT 0 COMMENT '收藏数',
    is_recommended TINYINT(1) DEFAULT 0 COMMENT '是否推荐',
    has_ebook TINYINT(1) DEFAULT 0 COMMENT '是否有电子版',
    ebook_url VARCHAR(500) COMMENT '电子书链接',
    preview_chapters INT DEFAULT 0 COMMENT '试读章节数',
    tags VARCHAR(500) COMMENT '标签',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_category (category),
    INDEX idx_is_recommended (is_recommended)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='红色读物表';

-- 清空旧数据
TRUNCATE TABLE red_news;
TRUNCATE TABLE red_book;

-- 插入红色新闻数据
INSERT INTO red_news (title, summary, content, category, source, author, view_count, like_count, is_top, is_hot, publish_time) VALUES
('西柏坡纪念馆举办"新中国从这里走来"主题展览', 
 '展览全面展示了中共中央在西柏坡的光辉历程，通过珍贵文物、历史照片和多媒体展示，生动再现了那段波澜壮阔的革命岁月。',
 '2026年1月，西柏坡纪念馆隆重举办"新中国从这里走来"主题展览。展览分为"战略转移""土地改革""三大战役""七届二中全会"四个部分，通过500余件珍贵文物、300余幅历史照片，全面展示了中共中央在西柏坡的光辉历程。',
 '纪念活动', '河北日报', '张明', 125800, 8560, 1, 1, NOW()),

('河北省开展"传承红色基因"主题教育活动',
 '全省各地积极组织党员干部深入红色教育基地，接受革命传统教育，推动党史学习教育走深走实。',
 '为深入贯彻落实习近平总书记关于传承红色基因的重要指示精神，河北省在全省范围内开展"传承红色基因"主题教育活动。',
 '教育实践', '河北新闻网', '李华', 89200, 5620, 0, 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),

('狼牙山五壮士纪念地迎来参观高峰',
 '清明节期间，大批群众前往狼牙山缅怀革命先烈，传承英雄精神，接受爱国主义教育。',
 '狼牙山五壮士纪念地位于河北省保定市易县，是全国爱国主义教育示范基地。',
 '红色故事', '燕赵都市报', '王强', 67500, 4230, 0, 0, DATE_SUB(NOW(), INTERVAL 2 DAY)),

('河北省委召开党史学习教育推进会',
 '会议强调要深入学习贯彻习近平总书记重要讲话精神，推动党史学习教育走深走实。',
 '河北省委近日召开党史学习教育推进会，省委书记出席会议并讲话。',
 '党建动态', '河北日报', '赵刚', 156800, 9870, 0, 1, DATE_SUB(NOW(), INTERVAL 3 DAY)),

('白洋淀雁翎队纪念馆完成升级改造',
 '改造后的纪念馆运用现代科技手段，生动再现雁翎队抗日斗争历史，成为雄安新区重要的红色教育基地。',
 '位于雄安新区的白洋淀雁翎队纪念馆近日完成升级改造并重新开放。',
 '纪念活动', '长城网', '刘芳', 43200, 2890, 0, 0, DATE_SUB(NOW(), INTERVAL 4 DAY)),

('塞罕坝精神宣讲团走进高校',
 '宣讲团成员讲述三代务林人艰苦奋斗的感人故事，激励青年学子传承塞罕坝精神。',
 '塞罕坝精神宣讲团近日走进河北大学、河北师范大学等高校，为青年学子讲述塞罕坝的故事。',
 '教育实践', '河北新闻网', '陈静', 78900, 5120, 0, 0, DATE_SUB(NOW(), INTERVAL 5 DAY)),

('李大钊故居纪念馆举办专题展览',
 '展览以"播火者"为主题，全面展示李大钊同志传播马克思主义、创建中国共产党的光辉历程。',
 '位于河北省唐山市乐亭县的李大钊故居纪念馆近日举办专题展览。',
 '红色故事', '唐山晚报', '周明', 52300, 3450, 0, 0, DATE_SUB(NOW(), INTERVAL 6 DAY)),

('董存瑞烈士陵园举行公祭活动',
 '社会各界代表齐聚董存瑞烈士陵园，缅怀革命先烈，传承英雄精神。',
 '在董存瑞烈士牺牲纪念日，河北省承德市隆化县董存瑞烈士陵园举行公祭活动。',
 '纪念活动', '承德日报', '孙伟', 68700, 4560, 0, 1, DATE_SUB(NOW(), INTERVAL 7 DAY));

-- 插入红色读物数据
INSERT INTO red_book (title, author, publisher, isbn, description, category, page_count, publish_date, rating, rating_count, read_count, favorite_count, is_recommended, has_ebook, tags) VALUES
('西柏坡：新中国从这里走来', '中共河北省委党史研究室', '河北人民出版社', '978-7-202-12345-6',
 '本书全面记录了中共中央在西柏坡的光辉历程，深入解读了西柏坡精神的科学内涵和时代价值。',
 '党史著作', 380, '2023-07', 4.9, 2580, 125800, 8960, 1, 1, '西柏坡,党史,必读,新中国'),

('狼牙山五壮士', '沈重', '人民文学出版社', '978-7-020-12346-7',
 '本书讲述了1941年狼牙山战斗中，五位八路军战士为掩护群众和主力撤退，在弹尽粮绝后跳崖的壮烈故事。',
 '纪实文学', 256, '2021-09', 4.8, 3650, 98500, 6780, 1, 1, '抗战,英雄,经典,狼牙山'),

('李大钊传', '朱志敏', '红旗出版社', '978-7-505-12347-8',
 '本书全面记述了中国共产主义运动先驱李大钊的革命生涯，展现了他为传播马克思主义所作出的卓越贡献。',
 '人物传记', 420, '2022-04', 4.7, 1890, 67200, 4560, 1, 1, '李大钊,先驱,传记,马克思主义'),

('塞罕坝精神', '河北省林业厅', '中国林业出版社', '978-7-503-12348-9',
 '本书记录了三代塞罕坝人艰苦创业、绿色发展的感人事迹，深刻诠释了塞罕坝精神。',
 '纪实文学', 320, '2022-08', 4.8, 2120, 85600, 5890, 1, 1, '塞罕坝,生态,奋斗,绿色发展'),

('白洋淀纪事', '孙犁', '人民文学出版社', '978-7-020-12349-0',
 '孙犁代表作，以白洋淀为背景，描绘了抗日战争时期冀中人民的斗争生活。',
 '红色经典', 280, '2020-05', 4.9, 5680, 156800, 12350, 1, 1, '白洋淀,孙犁,文学经典,抗战'),

('晋察冀边区革命史', '中共河北省委党史研究室', '中共党史出版社', '978-7-509-12350-1',
 '本书系统介绍了晋察冀抗日根据地的创建、发展和历史贡献。',
 '党史著作', 560, '2021-12', 4.6, 980, 32500, 2340, 0, 1, '晋察冀,根据地,抗战,边区'),

('红色少年读本：河北英雄故事', '河北省教育厅', '河北少年儿童出版社', '978-7-537-12351-2',
 '精选河北革命英雄故事，用生动的语言和精美的插图，向青少年讲述革命先辈的英勇事迹。',
 '青少年读物', 180, '2023-03', 4.7, 1560, 78900, 5670, 1, 1, '青少年,英雄故事,教育,插图'),

('董存瑞', '赵寰', '解放军文艺出版社', '978-7-503-12352-3',
 '本书讲述了舍身炸碉堡的战斗英雄董存瑞的英勇事迹，展现了他短暂而光辉的一生。',
 '人物传记', 240, '2022-05', 4.8, 2890, 112500, 7890, 1, 1, '董存瑞,英雄,解放战争,隆化'),

('冀中一日', '孙犁等', '花山文艺出版社', '978-7-551-12353-4',
 '1941年5月27日，冀中区党委组织千余名作者，记录下这一天冀中平原上发生的事情。',
 '红色经典', 350, '2021-05', 4.6, 1230, 45600, 3210, 0, 1, '冀中,抗战,纪实,群众'),

('雁翎队', '穆青', '河北人民出版社', '978-7-202-12354-5',
 '本书讲述了抗日战争时期活跃在白洋淀地区的水上游击队——雁翎队的英勇事迹。',
 '纪实文学', 290, '2022-07', 4.7, 1680, 56700, 4120, 1, 1, '雁翎队,白洋淀,抗战,游击队'),

('西柏坡精神研究', '河北省社会科学院', '河北人民出版社', '978-7-202-12355-6',
 '本书从理论和实践两个层面深入研究西柏坡精神，阐述其形成背景、科学内涵和时代价值。',
 '党史著作', 420, '2023-01', 4.5, 860, 28900, 1980, 0, 1, '西柏坡,精神研究,理论,党建'),

('河北抗战史话', '河北省档案馆', '河北教育出版社', '978-7-543-12356-7',
 '本书以丰富的档案资料为基础，全面记述了河北人民在抗日战争中的英勇斗争。',
 '党史著作', 480, '2022-09', 4.6, 1120, 38700, 2670, 0, 1, '抗战,河北,档案,历史');

SELECT '数据初始化完成！' AS message;
SELECT CONCAT('红色新闻: ', COUNT(*), ' 条') AS news_count FROM red_news;
SELECT CONCAT('红色读物: ', COUNT(*), ' 本') AS book_count FROM red_book;
