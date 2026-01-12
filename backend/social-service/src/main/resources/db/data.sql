-- 插入示例话题数据
INSERT INTO topic (name, description, cover_image, post_count, follow_count, view_count, status, created_at) VALUES
('建党百年', '庆祝中国共产党成立100周年', '', 1250, 8200, 125000, 'active', NOW()),
('西柏坡精神', '学习和传承西柏坡精神', '', 856, 5600, 82000, 'active', NOW()),
('红色旅游打卡', '分享红色旅游景点打卡', '', 2340, 12500, 56000, 'active', NOW()),
('重走长征路', '重温长征历史，传承长征精神', '', 432, 3400, 34000, 'active', NOW()),
('我的入党故事', '分享入党初心和故事', '', 678, 2100, 21000, 'active', NOW()),
('党史学习', '学习党的光辉历史', '', 1890, 9800, 98000, 'active', NOW()),
('红色摄影', '用镜头记录红色文化', '', 567, 4200, 42000, 'active', NOW()),
('志愿服务', '记录志愿服务活动', '', 1234, 6700, 67000, 'active', NOW());

-- 插入示例徽章数据
INSERT INTO badge (name, description, icon, type, condition_type, condition_value, points, rarity) VALUES
('初出茅庐', '发布第一条动态', '/badges/first-post.png', 'post', 'post_count', 1, 10, 'common'),
('笔耕不辍', '发布10条动态', '/badges/10-posts.png', 'post', 'post_count', 10, 50, 'common'),
('内容达人', '发布50条动态', '/badges/50-posts.png', 'post', 'post_count', 50, 200, 'rare'),
('红色作家', '发布100条动态', '/badges/100-posts.png', 'post', 'post_count', 100, 500, 'epic'),

('初次打卡', '完成第一次景点打卡', '/badges/first-checkin.png', 'checkin', 'checkin_count', 1, 10, 'common'),
('红色足迹', '打卡5个红色景点', '/badges/5-checkins.png', 'checkin', 'checkin_count', 5, 50, 'common'),
('红色旅行家', '打卡20个红色景点', '/badges/20-checkins.png', 'checkin', 'checkin_count', 20, 200, 'rare'),
('红色探索者', '打卡50个红色景点', '/badges/50-checkins.png', 'checkin', 'checkin_count', 50, 500, 'epic'),
('红色朝圣者', '打卡100个红色景点', '/badges/100-checkins.png', 'checkin', 'checkin_count', 100, 1000, 'legendary'),

('社交新人', '获得10个点赞', '/badges/10-likes.png', 'social', 'like_count', 10, 20, 'common'),
('人气之星', '获得100个点赞', '/badges/100-likes.png', 'social', 'like_count', 100, 100, 'rare'),
('社区明星', '获得1000个点赞', '/badges/1000-likes.png', 'social', 'like_count', 1000, 500, 'epic'),

('学习标兵', '完成5门课程', '/badges/5-courses.png', 'learning', 'course_count', 5, 100, 'rare'),
('红色学者', '完成20门课程', '/badges/20-courses.png', 'learning', 'course_count', 20, 500, 'epic');

-- 插入示例动态数据
INSERT INTO post (user_id, content, location_id, location_name, latitude, longitude, type, visibility, likes, comments, shares, views, status, created_at) VALUES
(1, '今天参观了西柏坡纪念馆，深刻感受到了"两个务必"的重要性。老一辈革命家在如此艰苦的条件下，依然保持着清醒的头脑和坚定的信念。我们要继续发扬艰苦奋斗的精神！', 1, '西柏坡纪念馆', 38.2345, 114.5678, 'checkin', 'public', 342, 56, 28, 1250, 'published', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(2, '周末带孩子来河北省博物馆参观《抗日烽火》展览，让下一代了解历史，铭记先烈。孩子们听得很认真，这种现场教育比书本更生动。#红色教育 #亲子活动', 2, '河北省博物馆', 38.0456, 114.5234, 'normal', 'public', 215, 32, 15, 890, 'published', DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(3, '参加了社区组织的红色故事分享会，听老党员讲述革命年代的故事，深受感动。建议社区多举办这样的活动，让红色基因代代相传。', 3, '社区党群服务中心', 38.1234, 114.6789, 'normal', 'public', 568, 124, 89, 2340, 'published', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, '重走长征路活动第三天，今天徒步了25公里。虽然很累，但想到当年红军战士的艰辛，这点苦算什么！坚持就是胜利！#重走长征路', NULL, NULL, NULL, NULL, 'normal', 'public', 423, 67, 34, 1560, 'published', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, '分享一组在狼牙山拍摄的照片，这里是五壮士英勇跳崖的地方。站在山顶，仿佛能听到当年的呐喊声。向英雄致敬！', 4, '狼牙山', 39.0123, 115.2345, 'checkin', 'public', 789, 145, 67, 3210, 'published', DATE_SUB(NOW(), INTERVAL 3 DAY));

-- 插入示例媒体文件
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

-- 插入示例打卡记录
INSERT INTO checkin (user_id, location_id, location_name, latitude, longitude, post_id, created_at) VALUES
(1, 1, '西柏坡纪念馆', 38.2345, 114.5678, 1, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(2, 2, '河北省博物馆', 38.0456, 114.5234, 2, DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(2, 4, '狼牙山', 39.0123, 115.2345, 5, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(1, 5, '李大钊纪念馆', 39.6234, 119.1234, NULL, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(3, 1, '西柏坡纪念馆', 38.2345, 114.5678, NULL, DATE_SUB(NOW(), INTERVAL 7 DAY));

-- 插入示例用户徽章
INSERT INTO user_badge (user_id, badge_id, obtained_at) VALUES
(1, 1, DATE_SUB(NOW(), INTERVAL 30 DAY)),
(1, 2, DATE_SUB(NOW(), INTERVAL 15 DAY)),
(1, 5, DATE_SUB(NOW(), INTERVAL 20 DAY)),
(1, 6, DATE_SUB(NOW(), INTERVAL 10 DAY)),
(2, 1, DATE_SUB(NOW(), INTERVAL 25 DAY)),
(2, 5, DATE_SUB(NOW(), INTERVAL 18 DAY)),
(3, 1, DATE_SUB(NOW(), INTERVAL 20 DAY)),
(3, 2, DATE_SUB(NOW(), INTERVAL 5 DAY));

-- 插入示例评论
INSERT INTO comment (post_id, user_id, parent_id, reply_to_user_id, content, likes, status, created_at) VALUES
(1, 2, NULL, NULL, '说得太好了！我也去过西柏坡，确实很震撼！', 23, 'published', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(1, 3, NULL, NULL, '艰苦奋斗的精神永不过时！', 15, 'published', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(1, 1, 1, 2, '是的，非常值得一去！', 8, 'published', DATE_SUB(NOW(), INTERVAL 50 MINUTE)),
(2, 1, NULL, NULL, '这样的亲子教育很有意义！', 12, 'published', DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(2, 3, NULL, NULL, '我也要带孩子去看看', 9, 'published', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(3, 1, NULL, NULL, '老党员的故事最感人', 34, 'published', DATE_SUB(NOW(), INTERVAL 20 HOUR)),
(3, 2, NULL, NULL, '希望多举办这样的活动', 28, 'published', DATE_SUB(NOW(), INTERVAL 18 HOUR));

-- 插入示例点赞记录
INSERT INTO like_record (user_id, target_type, target_id, created_at) VALUES
(1, 'post', 2, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(1, 'post', 3, DATE_SUB(NOW(), INTERVAL 20 HOUR)),
(2, 'post', 1, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(2, 'post', 3, DATE_SUB(NOW(), INTERVAL 19 HOUR)),
(3, 'post', 1, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(3, 'post', 2, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(1, 'comment', 1, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(2, 'comment', 4, DATE_SUB(NOW(), INTERVAL 2 HOUR));
