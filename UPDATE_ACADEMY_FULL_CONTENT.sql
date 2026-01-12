-- 为红色新闻表添加 external_url 字段（如果不存在）
ALTER TABLE red_news ADD COLUMN IF NOT EXISTS external_url VARCHAR(500) COMMENT '外部链接URL' AFTER publish_time;

-- 为红色书籍表添加 external_url 字段（如果不存在）
ALTER TABLE red_book ADD COLUMN IF NOT EXISTS external_url VARCHAR(500) COMMENT '外部购买链接URL' AFTER tags;

-- 更新新闻数据，添加外部链接
UPDATE red_news SET external_url = 'https://www.hebnews.cn/search?keyword=' || REPLACE(title, ' ', '+') WHERE external_url IS NULL OR external_url = '';

-- 更新书籍数据，添加京东购买链接
UPDATE red_book SET external_url = CONCAT('https://search.jd.com/Search?keyword=', REPLACE(title, ' ', '+')) WHERE external_url IS NULL OR external_url = '';

-- 插入/更新红色新闻数据（带外部链接）
INSERT INTO red_news (title, summary, content, category, source, author, view_count, like_count, is_top, is_hot, publish_time, external_url, status) VALUES
('西柏坡：新中国从这里走来', '西柏坡是中国革命圣地之一，是全国爱国主义教育示范基地。1948年5月至1949年3月，中共中央曾在此办公。', '西柏坡位于河北省石家庄市平山县中部，是解放战争时期中央工委、中共中央和解放军总部的所在地...', '党建动态', '河北日报', '记者组', 125800, 8960, 1, 1, NOW(), 'https://www.hebnews.cn/search?keyword=西柏坡', 'published'),
('狼牙山五壮士纪念馆迎来参观高峰', '清明节期间，狼牙山五壮士纪念馆迎来参观高峰，众多游客前来缅怀革命先烈。', '狼牙山五壮士纪念馆位于河北省保定市易县狼牙山镇，是为纪念抗日战争时期狼牙山五壮士而建...', '纪念活动', '河北新闻网', '李明', 89500, 6780, 0, 1, NOW(), 'https://www.hebnews.cn/search?keyword=狼牙山五壮士', 'published'),
('塞罕坝精神：绿色发展的时代答卷', '塞罕坝机械林场三代人艰苦创业，创造了荒原变林海的人间奇迹，铸就了塞罕坝精神。', '塞罕坝位于河北省承德市围场满族蒙古族自治县境内，是世界上面积最大的人工林场...', '红色故事', '人民日报', '王强', 156200, 12500, 1, 1, NOW(), 'https://www.hebnews.cn/search?keyword=塞罕坝精神', 'published'),
('白洋淀：雁翎队的英雄传奇', '抗日战争时期，白洋淀地区的雁翎队利用水上优势，神出鬼没地打击日寇，创造了许多传奇故事。', '白洋淀位于河北省保定市安新县境内，是华北平原最大的淡水湖泊...', '红色故事', '燕赵都市报', '张华', 78900, 5680, 0, 1, NOW(), 'https://www.hebnews.cn/search?keyword=白洋淀雁翎队', 'published'),
('李大钊故居：播撒革命火种的地方', '李大钊故居位于河北省唐山市乐亭县，是中国共产主义运动先驱李大钊的出生地和童年生活的地方。', '李大钊（1889-1927），字守常，河北乐亭人，中国共产主义运动的先驱...', '教育实践', '河北日报', '刘芳', 67800, 4560, 0, 0, NOW(), 'https://www.hebnews.cn/search?keyword=李大钊故居', 'published')
ON DUPLICATE KEY UPDATE 
    external_url = VALUES(external_url),
    updated_at = NOW();

-- 插入/更新红色书籍数据（带京东购买链接）
INSERT INTO red_book (title, author, publisher, isbn, description, category, page_count, rating, rating_count, read_count, is_recommended, has_ebook, tags, external_url, status) VALUES
('西柏坡：新中国从这里走来', '中共河北省委党史研究室', '河北人民出版社', '978-7-202-12345-6', '本书全面记录了中共中央在西柏坡的光辉历程，深入解读了西柏坡精神的科学内涵和时代价值。', '党史著作', 380, 4.9, 2580, 125800, 1, 1, '西柏坡,党史,必读,新中国', 'https://search.jd.com/Search?keyword=西柏坡新中国从这里走来', 'active'),
('狼牙山五壮士', '沈重', '人民文学出版社', '978-7-020-12346-7', '本书讲述了1941年狼牙山战斗中，五位八路军战士为掩护群众和主力撤退，在弹尽粮绝后跳崖的壮烈故事。', '纪实文学', 256, 4.8, 3650, 98500, 1, 1, '抗战,英雄,经典,狼牙山', 'https://search.jd.com/Search?keyword=狼牙山五壮士', 'active'),
('李大钊传', '朱志敏', '红旗出版社', '978-7-505-12347-8', '本书全面记述了中国共产主义运动先驱李大钊的革命生涯，展现了他为传播马克思主义所作出的卓越贡献。', '人物传记', 420, 4.7, 1890, 67200, 1, 1, '李大钊,先驱,传记,马克思主义', 'https://search.jd.com/Search?keyword=李大钊传', 'active'),
('塞罕坝精神', '河北省林业厅', '中国林业出版社', '978-7-503-12348-9', '本书记录了三代塞罕坝人艰苦创业、绿色发展的感人事迹，深刻诠释了塞罕坝精神。', '纪实文学', 320, 4.8, 2120, 85600, 1, 1, '塞罕坝,生态,奋斗,绿色发展', 'https://search.jd.com/Search?keyword=塞罕坝精神', 'active'),
('白洋淀纪事', '孙犁', '人民文学出版社', '978-7-020-12349-0', '孙犁代表作，以白洋淀为背景，描绘了抗日战争时期冀中人民的斗争生活，是中国现代文学的经典之作。', '红色经典', 280, 4.9, 5680, 156800, 1, 1, '白洋淀,孙犁,文学经典,抗战', 'https://search.jd.com/Search?keyword=白洋淀纪事孙犁', 'active'),
('董存瑞', '赵寰', '解放军文艺出版社', '978-7-503-12352-3', '本书讲述了舍身炸碉堡的战斗英雄董存瑞的英勇事迹，展现了他短暂而光辉的一生。', '人物传记', 240, 4.8, 2890, 112500, 1, 1, '董存瑞,英雄,解放战争,隆化', 'https://search.jd.com/Search?keyword=董存瑞传', 'active'),
('雁翎队', '穆青', '河北人民出版社', '978-7-202-12354-5', '本书讲述了抗日战争时期活跃在白洋淀地区的水上游击队——雁翎队的英勇事迹。', '纪实文学', 290, 4.7, 1680, 56700, 1, 1, '雁翎队,白洋淀,抗战,游击队', 'https://search.jd.com/Search?keyword=雁翎队', 'active'),
('红色少年读本：河北英雄故事', '河北省教育厅', '河北少年儿童出版社', '978-7-537-12351-2', '精选河北革命英雄故事，用生动的语言和精美的插图，向青少年讲述革命先辈的英勇事迹。', '青少年读物', 180, 4.7, 1560, 78900, 1, 0, '青少年,英雄故事,教育,插图', 'https://search.jd.com/Search?keyword=河北英雄故事青少年', 'active')
ON DUPLICATE KEY UPDATE 
    external_url = VALUES(external_url),
    updated_at = NOW();

SELECT '数据更新完成！新闻和书籍都已添加外部链接。' AS message;
