-- 更新话题表的view_count字段
UPDATE topic SET view_count = 125000 WHERE name = '建党百年';
UPDATE topic SET view_count = 82000 WHERE name = '西柏坡精神';
UPDATE topic SET view_count = 56000 WHERE name = '红色旅游打卡';
UPDATE topic SET view_count = 34000 WHERE name = '重走长征路';
UPDATE topic SET view_count = 21000 WHERE name = '我的入党故事';
UPDATE topic SET view_count = 98000 WHERE name = '党史学习';
UPDATE topic SET view_count = 42000 WHERE name = '红色摄影';
UPDATE topic SET view_count = 67000 WHERE name = '志愿服务';

-- 插入媒体文件数据
INSERT INTO media_file (post_id, type, url, thumbnail, width, height, duration, file_size, order_num) VALUES
-- 第1条动态的图片
(1, 'image', 'https://images.unsplash.com/photo-1599526725208-a9c6833777d9?q=80&w=2670&auto=format&fit=crop', 'https://images.unsplash.com/photo-1599526725208-a9c6833777d9?q=80&w=400&auto=format&fit=crop', 2670, 1780, NULL, 2048000, 1),
(1, 'image', 'https://images.unsplash.com/photo-1599526724673-863f69b56350?q=80&w=2670&auto=format&fit=crop', 'https://images.unsplash.com/photo-1599526724673-863f69b56350?q=80&w=400&auto=format&fit=crop', 2670, 1780, NULL, 1856000, 2),
-- 第2条动态的图片
(2, 'image', 'https://images.unsplash.com/photo-1580130601254-05fa235bcabd?q=80&w=2669&auto=format&fit=crop', 'https://images.unsplash.com/photo-1580130601254-05fa235bcabd?q=80&w=400&auto=format&fit=crop', 2669, 1779, NULL, 1920000, 1),
-- 第5条动态的图片
(5, 'image', 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?q=80&w=2670&auto=format&fit=crop', 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?q=80&w=400&auto=format&fit=crop', 2670, 1780, NULL, 2150000, 1),
(5, 'image', 'https://images.unsplash.com/photo-1519681393784-d120267933ba?q=80&w=2670&auto=format&fit=crop', 'https://images.unsplash.com/photo-1519681393784-d120267933ba?q=80&w=400&auto=format&fit=crop', 2670, 1780, NULL, 2048000, 2),
(5, 'image', 'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?q=80&w=2670&auto=format&fit=crop', 'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?q=80&w=400&auto=format&fit=crop', 2670, 1780, NULL, 1980000, 3);
