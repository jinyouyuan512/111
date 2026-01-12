-- 体验馆示例数据

-- 插入场景数据
INSERT INTO `scene` (`id`, `name`, `description`, `era`, `duration`, `thumbnail`, `model_url`, `interaction_count`) VALUES
(1, '西柏坡中共中央旧址', '1:1 还原西柏坡中央大院。进入当年指挥三大战役的简陋土屋，感受"新中国从这里走来"的震撼。', '解放战争', 15, '/images/scenes/xibaipo.jpg', '/models/xibaipo.glb', 8),
(2, '冉庄地道战全景体验', '通过数字孪生技术重现"地下长城"。多视角切换，深度解析冉庄地道巧妙的攻防结构。', '抗战时期', 20, '/images/scenes/ranzhuang.jpg', '/models/ranzhuang.glb', 12),
(3, '李大钊纪念馆数字展厅', '沉浸式参观李大钊纪念馆。高精度扫描文物，近距离感受中国共产主义先驱的伟大人格。', '建党时期', 12, '/images/scenes/lidazhao.jpg', '/models/lidazhao.glb', 6),
(4, '狼牙山五壮士纪念地', '数字化重现狼牙山五壮士英勇跳崖的历史场景。通过VR技术身临其境感受英雄气概。', '抗战时期', 18, '/images/scenes/langyashan.jpg', '/models/langyashan.glb', 5),
(5, '塞罕坝机械林场：绿色奇迹', 'VR 视角回顾三代塞罕坝人艰苦奋斗的历程。从荒原到林海，数字还原生态文明建设的辉煌成就。', '新中国建设', 25, '/images/scenes/saihanba.jpg', '/models/saihanba.glb', 10);

-- 插入西柏坡交互点
INSERT INTO `interaction_point` (`scene_id`, `title`, `type`, `position_x`, `position_y`, `position_z`, `content`, `media_url`, `sort_order`) VALUES
(1, '七届二中全会旧址', 'info', 20, 30, 50, '1949年3月5日至13日，中国共产党第七届中央委员会第二次全体会议在此召开。毛泽东在会上提出了"两个务必"的重要思想。', '/media/xibaipo/qjec.jpg', 1),
(1, '毛泽东旧居', 'story', 50, 40, 45, '毛泽东同志在此指挥了辽沈、淮海、平津三大战役，为新中国的诞生奠定了基础。', '/media/xibaipo/mzd.jpg', 2),
(1, '进京赶考出发地', 'video', 70, 60, 55, '1949年3月23日，中共中央离开西柏坡前往北平，毛泽东说："今天是进京赶考的日子。"', '/media/xibaipo/jingjing.mp4', 3),
(1, '中央军委作战室', 'artifact', 35, 50, 48, '这里是指挥三大战役的核心场所，保留着当年的作战地图和通讯设备。', '/media/xibaipo/zuozhan.jpg', 4),
(1, '周恩来旧居', 'story', 60, 35, 52, '周恩来同志在此处理大量军政事务，为新中国的建立做出了卓越贡献。', '/media/xibaipo/zel.jpg', 5),
(1, '朱德旧居', 'story', 45, 55, 47, '朱德同志在此协助毛泽东指挥作战，展现了卓越的军事才能。', '/media/xibaipo/zd.jpg', 6),
(1, '刘少奇旧居', 'story', 55, 45, 50, '刘少奇同志在此参与重大决策，为党的建设做出了重要贡献。', '/media/xibaipo/lsq.jpg', 7),
(1, '任弼时旧居', 'story', 40, 60, 46, '任弼时同志在此工作，为党的事业鞠躬尽瘁。', '/media/xibaipo/rbs.jpg', 8);

-- 插入冉庄地道战交互点
INSERT INTO `interaction_point` (`scene_id`, `title`, `type`, `position_x`, `position_y`, `position_z`, `content`, `media_url`, `sort_order`) VALUES
(2, '地道入口', 'info', 30, 20, 50, '冉庄地道战遗址保留了当年的地道入口，展示了抗日军民的智慧。', '/media/ranzhuang/entrance.jpg', 1),
(2, '地道内部结构', 'video', 50, 50, 45, '通过3D扫描技术还原的地道内部结构，展示了复杂的地下网络。', '/media/ranzhuang/tunnel.mp4', 2),
(2, '作战指挥所', 'artifact', 60, 40, 55, '地道内的作战指挥所，是当年指挥战斗的核心场所。', '/media/ranzhuang/command.jpg', 3),
(2, '陷阱机关', 'info', 40, 60, 48, '地道内设置的各种陷阱机关，用于打击敌人。', '/media/ranzhuang/trap.jpg', 4),
(2, '通风口', 'info', 70, 30, 52, '巧妙设计的通风口，保证了地道内的空气流通。', '/media/ranzhuang/vent.jpg', 5),
(2, '储藏室', 'artifact', 35, 55, 47, '地道内的储藏室，存放着粮食和武器弹药。', '/media/ranzhuang/storage.jpg', 6),
(2, '战斗场景还原', 'video', 55, 45, 50, '通过VR技术还原的地道战战斗场景，让您身临其境。', '/media/ranzhuang/battle.mp4', 7),
(2, '地道出口', 'info', 65, 55, 46, '地道的多个出口，为游击战提供了灵活的进出通道。', '/media/ranzhuang/exit.jpg', 8),
(2, '地道博物馆', 'artifact', 45, 35, 53, '展示地道战历史文物和资料的博物馆。', '/media/ranzhuang/museum.jpg', 9),
(2, '英雄事迹展', 'story', 50, 65, 49, '讲述冉庄人民英勇抗战的感人事迹。', '/media/ranzhuang/heroes.jpg', 10),
(2, '地道防御工事', 'info', 60, 50, 51, '地道内的防御工事，展示了军民的智慧。', '/media/ranzhuang/defense.jpg', 11),
(2, '纪念碑广场', 'photo', 55, 60, 48, '冉庄地道战纪念碑，缅怀抗日英烈。', '/media/ranzhuang/monument.jpg', 12);

-- 插入李大钊纪念馆交互点
INSERT INTO `interaction_point` (`scene_id`, `title`, `type`, `position_x`, `position_y`, `position_z`, `content`, `media_url`, `sort_order`) VALUES
(3, '李大钊生平展', 'info', 50, 50, 50, '详细介绍李大钊同志的生平事迹和革命历程。', '/media/lidazhao/life.jpg', 1),
(3, '《新青年》杂志', 'artifact', 40, 40, 48, '李大钊在《新青年》杂志上发表的重要文章。', '/media/lidazhao/magazine.jpg', 2),
(3, '马克思主义传播', 'video', 60, 60, 52, '李大钊传播马克思主义的历史影像资料。', '/media/lidazhao/marxism.mp4', 3),
(3, '北京大学图书馆', 'story', 35, 55, 47, '李大钊担任北京大学图书馆主任期间的故事。', '/media/lidazhao/library.jpg', 4),
(3, '就义场景', 'video', 65, 45, 53, '1927年4月28日，李大钊英勇就义的历史场景还原。', '/media/lidazhao/martyrdom.mp4', 5),
(3, '手稿文物', 'artifact', 45, 65, 49, '李大钊的珍贵手稿和文物展示。', '/media/lidazhao/manuscript.jpg', 6);

-- 插入狼牙山交互点
INSERT INTO `interaction_point` (`scene_id`, `title`, `type`, `position_x`, `position_y`, `position_z`, `content`, `media_url`, `sort_order`) VALUES
(4, '棋盘陀峰顶', 'info', 30, 20, 50, '狼牙山主峰棋盘陀，五壮士在此英勇跳崖。', '/media/langyashan/peak.jpg', 1),
(4, '五壮士纪念塔', 'photo', 60, 50, 55, '高耸的五壮士纪念塔，缅怀英雄。', '/media/langyashan/tower.jpg', 2),
(4, '战斗场景还原', 'video', 50, 60, 48, '通过VR技术还原五壮士与敌人战斗的场景。', '/media/langyashan/battle.mp4', 3),
(4, '英雄事迹展', 'story', 40, 40, 52, '详细讲述五壮士的英雄事迹。', '/media/langyashan/heroes.jpg', 4),
(4, '跳崖地点', 'info', 70, 30, 47, '五壮士跳崖的具体地点，让人肃然起敬。', '/media/langyashan/cliff.jpg', 5);

-- 插入塞罕坝交互点
INSERT INTO `interaction_point` (`scene_id`, `title`, `type`, `position_x`, `position_y`, `position_z`, `content`, `media_url`, `sort_order`) VALUES
(5, '荒原变林海', 'video', 50, 50, 50, '通过时间轴展示塞罕坝从荒原到林海的巨变。', '/media/saihanba/transformation.mp4', 1),
(5, '第一代建设者', 'story', 30, 40, 48, '讲述第一代塞罕坝建设者的艰苦奋斗故事。', '/media/saihanba/pioneers.jpg', 2),
(5, '机械林场', 'info', 60, 60, 52, '塞罕坝机械林场的建设历程和成就。', '/media/saihanba/forest.jpg', 3),
(5, '生态奇迹', 'photo', 40, 30, 55, '塞罕坝创造的生态奇迹，绿色发展的典范。', '/media/saihanba/ecology.jpg', 4),
(5, '防风固沙', 'info', 70, 50, 47, '塞罕坝在防风固沙方面的重要作用。', '/media/saihanba/windbreak.jpg', 5),
(5, '林场工人生活', 'story', 35, 60, 49, '林场工人的日常生活和工作场景。', '/media/saihanba/workers.jpg', 6),
(5, '四季美景', 'photo', 65, 40, 53, '塞罕坝四季的美丽景色。', '/media/saihanba/seasons.jpg', 7),
(5, '生态效益', 'info', 45, 55, 51, '塞罕坝带来的巨大生态效益和社会效益。', '/media/saihanba/benefits.jpg', 8),
(5, '精神传承', 'video', 55, 45, 48, '塞罕坝精神的传承和发扬。', '/media/saihanba/spirit.mp4', 9),
(5, '未来展望', 'info', 50, 35, 54, '塞罕坝未来的发展规划和展望。', '/media/saihanba/future.jpg', 10);
