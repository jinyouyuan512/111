-- 插入示例课程数据
INSERT INTO `course` (`title`, `category`, `description`, `instructor`, `cover_image`, `total_hours`, `level`, `status`, `enrollment_count`) VALUES
('河北党史：新中国从这里走来', '党史教育', '深入学习中共中央在西柏坡的伟大实践，了解新中国诞生的前夜，感悟"两个务必"的深远历史意义。', '张教授', 'https://example.com/course1.jpg', 15.00, 'beginner', 'published', 5280),
('西柏坡精神深度解读', '革命精神', '系统解析西柏坡精神的科学内涵，探讨其在当代的时代价值，弘扬谦虚谨慎、艰苦奋斗的优良作风。', '李教授', 'https://example.com/course2.jpg', 12.00, 'intermediate', 'published', 4120),
('李大钊与马克思主义在河北', '英雄人物', '追寻中国共产主义运动先驱李大钊的足迹，了解马克思主义在燕赵大地的早期传播与革命实践。', '王教授', 'https://example.com/course3.jpg', 10.00, 'beginner', 'published', 3560),
('狼牙山五壮士英雄事迹', '英雄人物', '重温狼牙山五壮士舍生取义、视死如归的壮举，学习伟大的抗战精神和民族气节。', '赵教授', 'https://example.com/course4.jpg', 8.00, 'beginner', 'published', 4890),
('塞罕坝精神：荒原变林海', '时代楷模', '学习塞罕坝务林人三代接力、艰苦奋斗，将荒原变成林海的人间奇迹，感悟"绿水青山就是金山银山"。', '刘教授', 'https://example.com/course5.jpg', 18.00, 'intermediate', 'published', 6120);

-- 插入章节数据（为第一门课程）
INSERT INTO `chapter` (`course_id`, `title`, `video_url`, `duration`, `order_num`) VALUES
(1, '第一章：课程导论与背景', 'https://example.com/video1.mp4', 2700, 1),
(1, '第二章：历史发展脉络', 'https://example.com/video2.mp4', 3600, 2),
(1, '第三章：核心精神内涵', 'https://example.com/video3.mp4', 3000, 3),
(1, '第四章：重要历史事件', 'https://example.com/video4.mp4', 3300, 4),
(1, '第五章：总结与展望', 'https://example.com/video5.mp4', 2400, 5);

-- 插入学习资料
INSERT INTO `learning_material` (`chapter_id`, `title`, `type`, `file_url`, `file_size`) VALUES
(1, '课程讲义', 'pdf', 'https://example.com/material1.pdf', 1024000),
(1, '参考资料', 'doc', 'https://example.com/material2.doc', 512000),
(2, '历史图片集', 'image', 'https://example.com/material3.jpg', 2048000);

-- 插入测验
INSERT INTO `quiz` (`chapter_id`, `title`, `pass_score`, `time_limit`) VALUES
(1, '第一章测验', 60, 30),
(2, '第二章测验', 60, 30);

-- 插入测验题目
INSERT INTO `quiz_question` (`quiz_id`, `question`, `type`, `options`, `correct_answer`, `score`, `order_num`) VALUES
(1, '西柏坡位于河北省哪个市？', 'single_choice', '["石家庄市", "保定市", "邯郸市", "唐山市"]', '石家庄市', 10, 1),
(1, '中共中央在西柏坡召开了哪些重要会议？（多选）', 'multiple_choice', '["七届二中全会", "土地会议", "九月会议", "遵义会议"]', '七届二中全会,土地会议,九月会议', 15, 2),
(1, '"两个务必"是指务必使同志们继续地保持谦虚、谨慎、不骄、不躁的作风，务必使同志们继续地保持艰苦奋斗的作风。', 'true_false', '["正确", "错误"]', '正确', 10, 3);
