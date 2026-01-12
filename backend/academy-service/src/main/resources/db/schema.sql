-- 创建数据库
CREATE DATABASE IF NOT EXISTS jiyi_academy DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE jiyi_academy;

-- 课程表
CREATE TABLE IF NOT EXISTS `course` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '课程ID',
    `title` VARCHAR(200) NOT NULL COMMENT '课程标题',
    `category` VARCHAR(50) NOT NULL COMMENT '分类: 党史教育, 革命精神, 英雄人物, 时代楷模',
    `description` TEXT COMMENT '课程描述',
    `instructor` VARCHAR(100) COMMENT '讲师',
    `cover_image` VARCHAR(255) COMMENT '封面图片URL',
    `total_hours` DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT '总课时',
    `level` VARCHAR(20) NOT NULL DEFAULT 'beginner' COMMENT '难度: beginner, intermediate, advanced',
    `status` VARCHAR(20) NOT NULL DEFAULT 'published' COMMENT '状态: draft, published, archived',
    `enrollment_count` INT NOT NULL DEFAULT 0 COMMENT '报名人数',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

-- 章节表
CREATE TABLE IF NOT EXISTS `chapter` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '章节ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `title` VARCHAR(200) NOT NULL COMMENT '章节标题',
    `video_url` VARCHAR(255) COMMENT '视频URL',
    `duration` INT NOT NULL DEFAULT 0 COMMENT '时长(秒)',
    `order_num` INT NOT NULL DEFAULT 0 COMMENT '排序号',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_order` (`order_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='章节表';

-- 学习资料表
CREATE TABLE IF NOT EXISTS `learning_material` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '资料ID',
    `chapter_id` BIGINT NOT NULL COMMENT '章节ID',
    `title` VARCHAR(200) NOT NULL COMMENT '资料标题',
    `type` VARCHAR(20) NOT NULL COMMENT '类型: pdf, doc, ppt, image',
    `file_url` VARCHAR(255) NOT NULL COMMENT '文件URL',
    `file_size` BIGINT NOT NULL DEFAULT 0 COMMENT '文件大小(字节)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_chapter_id` (`chapter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习资料表';

-- 测验表
CREATE TABLE IF NOT EXISTS `quiz` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '测验ID',
    `chapter_id` BIGINT NOT NULL COMMENT '章节ID',
    `title` VARCHAR(200) NOT NULL COMMENT '测验标题',
    `pass_score` INT NOT NULL DEFAULT 60 COMMENT '及格分数',
    `time_limit` INT NOT NULL DEFAULT 0 COMMENT '时间限制(分钟), 0表示不限时',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_chapter_id` (`chapter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测验表';

-- 测验题目表
CREATE TABLE IF NOT EXISTS `quiz_question` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '题目ID',
    `quiz_id` BIGINT NOT NULL COMMENT '测验ID',
    `question` TEXT NOT NULL COMMENT '题目内容',
    `type` VARCHAR(20) NOT NULL COMMENT '题型: single_choice, multiple_choice, true_false',
    `options` TEXT COMMENT '选项(JSON数组)',
    `correct_answer` VARCHAR(255) NOT NULL COMMENT '正确答案',
    `score` INT NOT NULL DEFAULT 1 COMMENT '分值',
    `order_num` INT NOT NULL DEFAULT 0 COMMENT '排序号',
    PRIMARY KEY (`id`),
    KEY `idx_quiz_id` (`quiz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测验题目表';

-- 用户课程报名表
CREATE TABLE IF NOT EXISTS `user_enrollment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '报名ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `enrolled_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
    `status` VARCHAR(20) NOT NULL DEFAULT 'learning' COMMENT '状态: learning, completed, dropped',
    `progress` INT NOT NULL DEFAULT 0 COMMENT '学习进度百分比',
    `completed_time` DATETIME DEFAULT NULL COMMENT '完成时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_course` (`user_id`, `course_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户课程报名表';

-- 学习进度表
CREATE TABLE IF NOT EXISTS `learning_progress` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '进度ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `chapter_id` BIGINT NOT NULL COMMENT '章节ID',
    `completed` TINYINT NOT NULL DEFAULT 0 COMMENT '是否完成',
    `completed_at` DATETIME DEFAULT NULL COMMENT '完成时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_course_chapter` (`user_id`, `course_id`, `chapter_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习进度表';

-- 测验记录表
CREATE TABLE IF NOT EXISTS `quiz_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `quiz_id` BIGINT NOT NULL COMMENT '测验ID',
    `score` INT NOT NULL DEFAULT 0 COMMENT '得分',
    `passed` TINYINT NOT NULL DEFAULT 0 COMMENT '是否通过',
    `answers` TEXT COMMENT '答案(JSON)',
    `submitted_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_quiz_id` (`quiz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测验记录表';

-- 结业证书表
CREATE TABLE IF NOT EXISTS `course_certificate` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '证书ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `certificate_no` VARCHAR(50) NOT NULL COMMENT '证书编号',
    `certificate_url` VARCHAR(255) COMMENT '证书文件URL',
    `points_awarded` INT NOT NULL DEFAULT 0 COMMENT '获得积分',
    `issued_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '颁发时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_certificate_no` (`certificate_no`),
    UNIQUE KEY `uk_user_course` (`user_id`, `course_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='结业证书表';
