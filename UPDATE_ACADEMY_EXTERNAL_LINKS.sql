-- 更新红色新闻外部链接
-- 先添加 external_url 字段（如果不存在）
ALTER TABLE red_news ADD COLUMN IF NOT EXISTS external_url VARCHAR(500) COMMENT '外部链接';

-- 更新新闻外部链接
UPDATE red_news SET external_url = 'https://hebei.hebnews.cn/node_50.htm' WHERE source LIKE '%河北日报%';
UPDATE red_news SET external_url = 'https://www.hebnews.cn/' WHERE source LIKE '%河北新闻网%';
UPDATE red_news SET external_url = 'https://yzdsb.hebnews.cn/' WHERE source LIKE '%燕赵都市报%';
UPDATE red_news SET external_url = 'https://www.hebnews.cn/' WHERE source LIKE '%长城网%';
UPDATE red_news SET external_url = 'https://www.tswb.com.cn/' WHERE source LIKE '%唐山晚报%';
UPDATE red_news SET external_url = 'https://www.cdnews.cn/' WHERE source LIKE '%承德日报%';

-- 更新红色读物外部链接
-- 先添加 external_url 字段（如果不存在）
ALTER TABLE red_book ADD COLUMN IF NOT EXISTS external_url VARCHAR(500) COMMENT '外部链接（购买/阅读）';

-- 更新书籍外部链接（京东搜索）
UPDATE red_book SET external_url = CONCAT('https://search.jd.com/Search?keyword=', REPLACE(title, ' ', '')) WHERE external_url IS NULL OR external_url = '';

SELECT '外部链接更新完成' AS message;
