-- 插入景点数据
INSERT INTO `attraction` (`name`, `category`, `description`, `address`, `latitude`, `longitude`, `visit_duration`, `ticket_price`, `opening_hours`, `images`, `rating`, `status`) VALUES
('西柏坡纪念馆', '革命遗址', '中国革命圣地，解放战争时期中共中央所在地', '河北省石家庄市平山县西柏坡镇', 38.3500, 114.1500, 120, 0.00, '09:00-17:00', '["https://example.com/xibaipo1.jpg","https://example.com/xibaipo2.jpg"]', 4.8, 'open'),
('狼牙山', '革命遗址', '狼牙山五壮士英勇跳崖之地', '河北省保定市易县狼牙山镇', 39.0500, 115.2000, 180, 80.00, '08:00-18:00', '["https://example.com/langyashan1.jpg","https://example.com/langyashan2.jpg"]', 4.7, 'open'),
('冉庄地道战遗址', '革命遗址', '抗日战争时期地道战遗址', '河北省保定市清苑区冉庄镇', 38.7500, 115.5000, 90, 30.00, '08:30-17:30', '["https://example.com/ranzhuang1.jpg","https://example.com/ranzhuang2.jpg"]', 4.6, 'open'),
('李大钊纪念馆', '革命遗址', '中国共产主义运动先驱李大钊纪念馆', '河北省唐山市乐亭县', 39.4200, 118.9000, 90, 0.00, '09:00-17:00', '["https://example.com/lidazhao1.jpg","https://example.com/lidazhao2.jpg"]', 4.7, 'open'),
('白求恩柯棣华纪念馆', '革命遗址', '纪念国际主义战士白求恩和柯棣华', '河北省石家庄市唐县', 38.7500, 114.9800, 60, 0.00, '09:00-17:00', '["https://example.com/baiqiuen1.jpg","https://example.com/baiqiuen2.jpg"]', 4.5, 'open'),
('华北军区烈士陵园', '革命遗址', '华北地区最大的烈士陵园', '河北省石家庄市中山西路', 38.0400, 114.4800, 90, 0.00, '08:00-18:00', '["https://example.com/huabei1.jpg","https://example.com/huabei2.jpg"]', 4.6, 'open');

-- 插入旅游路线数据
INSERT INTO `route` (`name`, `description`, `days`, `estimated_cost`, `difficulty`, `season`, `tags`, `cover_image`, `status`, `view_count`, `booking_count`) VALUES
('红色经典三日游', '探访西柏坡、狼牙山等革命圣地，重温红色历史', 3, 1200.00, 'easy', '春季,秋季', '["红色教育","历史文化","爱国主义"]', 'https://example.com/route1.jpg', 'active', 1250, 89),
('抗战英雄两日游', '参观狼牙山、冉庄地道战遗址，缅怀抗战英雄', 2, 800.00, 'easy', '全年', '["抗战历史","英雄事迹","爱国教育"]', 'https://example.com/route2.jpg', 'active', 980, 67),
('革命先驱寻访之旅', '走访李大钊纪念馆等地，学习革命先驱精神', 2, 900.00, 'easy', '春季,夏季,秋季', '["革命先驱","党史教育","文化传承"]', 'https://example.com/route3.jpg', 'active', 756, 45),
('河北红色全景五日游', '全面游览河北主要红色景点，深度体验红色文化', 5, 2500.00, 'medium', '春季,秋季', '["深度游","红色文化","历史教育"]', 'https://example.com/route4.jpg', 'active', 543, 32);

-- 插入路线景点关联数据
-- 红色经典三日游
INSERT INTO `route_attraction` (`route_id`, `attraction_id`, `day_num`, `order_num`, `visit_time`, `notes`) VALUES
(1, 1, 1, 1, '09:00-12:00', '上午参观西柏坡纪念馆主展区'),
(1, 1, 1, 2, '14:00-17:00', '下午参观七届二中全会旧址'),
(1, 2, 2, 1, '09:00-12:00', '上午登狼牙山，参观五勇士纪念塔'),
(1, 2, 2, 2, '14:00-17:00', '下午参观狼牙山陈列馆'),
(1, 3, 3, 1, '09:00-11:00', '参观冉庄地道战遗址'),
(1, 6, 3, 2, '14:00-16:00', '参观华北军区烈士陵园');

-- 抗战英雄两日游
INSERT INTO `route_attraction` (`route_id`, `attraction_id`, `day_num`, `order_num`, `visit_time`, `notes`) VALUES
(2, 2, 1, 1, '09:00-12:00', '上午游览狼牙山'),
(2, 2, 1, 2, '14:00-17:00', '下午参观纪念馆'),
(2, 3, 2, 1, '09:00-12:00', '参观地道战遗址'),
(2, 3, 2, 2, '14:00-16:00', '体验地道战场景');

-- 革命先驱寻访之旅
INSERT INTO `route_attraction` (`route_id`, `attraction_id`, `day_num`, `order_num`, `visit_time`, `notes`) VALUES
(3, 4, 1, 1, '09:00-12:00', '参观李大钊纪念馆'),
(3, 4, 1, 2, '14:00-17:00', '参观李大钊故居'),
(3, 5, 2, 1, '09:00-11:00', '参观白求恩柯棣华纪念馆'),
(3, 6, 2, 2, '14:00-16:00', '参观华北军区烈士陵园');

-- 河北红色全景五日游
INSERT INTO `route_attraction` (`route_id`, `attraction_id`, `day_num`, `order_num`, `visit_time`, `notes`) VALUES
(4, 1, 1, 1, '09:00-17:00', '全天游览西柏坡'),
(4, 2, 2, 1, '09:00-17:00', '全天游览狼牙山'),
(4, 3, 3, 1, '09:00-12:00', '上午参观冉庄地道战遗址'),
(4, 4, 3, 2, '14:00-17:00', '下午参观李大钊纪念馆'),
(4, 5, 4, 1, '09:00-12:00', '上午参观白求恩柯棣华纪念馆'),
(4, 6, 4, 2, '14:00-17:00', '下午参观华北军区烈士陵园');

-- 插入交通建议数据
INSERT INTO `transportation` (`route_id`, `from_attraction_id`, `to_attraction_id`, `type`, `duration`, `cost`, `description`) VALUES
(1, NULL, 1, 'bus', 120, 50.00, '从石家庄市区乘坐旅游专线到西柏坡'),
(1, 1, 2, 'bus', 180, 80.00, '从西柏坡到狼牙山，建议包车或乘坐旅游大巴'),
(1, 2, 3, 'bus', 90, 40.00, '从狼牙山到冉庄，乘坐长途汽车'),
(1, 3, 6, 'taxi', 60, 100.00, '从冉庄返回石家庄市区，建议打车'),
(2, NULL, 2, 'bus', 150, 60.00, '从保定市区到狼牙山'),
(2, 2, 3, 'bus', 90, 40.00, '从狼牙山到冉庄'),
(3, NULL, 4, 'bus', 180, 70.00, '从唐山市区到李大钊纪念馆'),
(3, 4, 5, 'bus', 120, 50.00, '从乐亭到唐县'),
(3, 5, 6, 'bus', 150, 60.00, '从唐县到石家庄');

-- 插入住宿建议数据
INSERT INTO `accommodation` (`route_id`, `day_num`, `name`, `type`, `address`, `price_range`, `rating`, `contact`) VALUES
(1, 1, '西柏坡红色文化酒店', 'hotel', '河北省石家庄市平山县西柏坡镇', '300-500元', 4.5, '0311-82851234'),
(1, 2, '易县宾馆', 'hotel', '河北省保定市易县', '200-400元', 4.3, '0312-8212345'),
(2, 1, '狼牙山度假村', 'hotel', '河北省保定市易县狼牙山镇', '250-450元', 4.4, '0312-8223456'),
(3, 1, '乐亭海景酒店', 'hotel', '河北省唐山市乐亭县', '280-480元', 4.6, '0315-4612345'),
(4, 1, '西柏坡红色文化酒店', 'hotel', '河北省石家庄市平山县西柏坡镇', '300-500元', 4.5, '0311-82851234'),
(4, 2, '易县宾馆', 'hotel', '河北省保定市易县', '200-400元', 4.3, '0312-8212345'),
(4, 3, '乐亭海景酒店', 'hotel', '河北省唐山市乐亭县', '280-480元', 4.6, '0315-4612345');

-- 插入天气预警示例数据
INSERT INTO `weather_alert` (`attraction_id`, `alert_type`, `severity`, `title`, `content`, `start_time`, `end_time`) VALUES
(2, 'weather', 'medium', '大风预警', '狼牙山景区未来三天将有6-7级大风，请游客注意安全，做好防风措施。', NOW(), DATE_ADD(NOW(), INTERVAL 3 DAY)),
(1, 'event', 'low', '参观人数较多', '西柏坡纪念馆近期参观人数较多，建议提前预约或错峰参观。', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY));
