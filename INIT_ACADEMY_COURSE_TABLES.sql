-- 传承学院课程相关表
USE jiyi_academy;

-- 课程表
CREATE TABLE IF NOT EXISTS course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '课程标题',
    category VARCHAR(50) NOT NULL COMMENT '分类',
    description TEXT COMMENT '课程描述',
    instructor VARCHAR(100) COMMENT '讲师',
    cover_image VARCHAR(500) COMMENT '封面图片',
    total_hours INT DEFAULT 0 COMMENT '总学时',
    level VARCHAR(20) DEFAULT 'beginner' COMMENT '难度级别',
    enrollment_count INT DEFAULT 0 COMMENT '报名人数',
    status VARCHAR(20) DEFAULT 'published' COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 章节表
CREATE TABLE IF NOT EXISTS chapter (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL COMMENT '课程ID',
    title VARCHAR(200) NOT NULL COMMENT '章节标题',
    video_url VARCHAR(500) COMMENT '视频URL',
    duration INT DEFAULT 0 COMMENT '时长(分钟)',
    order_num INT DEFAULT 0 COMMENT '排序',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_course_id (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='章节表';

-- 用户报名表
CREATE TABLE IF NOT EXISTS user_enrollment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    enroll_time DATETIME COMMENT '报名时间',
    progress INT DEFAULT 0 COMMENT '学习进度',
    completed_time DATETIME COMMENT '完成时间',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_course (user_id, course_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户报名表';

-- 学习进度表
CREATE TABLE IF NOT EXISTS learning_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    chapter_id BIGINT NOT NULL COMMENT '章节ID',
    completed TINYINT(1) DEFAULT 0 COMMENT '是否完成',
    completed_at DATETIME COMMENT '完成时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_chapter (user_id, chapter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习进度表';

-- 测验表
CREATE TABLE IF NOT EXISTS quiz (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chapter_id BIGINT NOT NULL COMMENT '章节ID',
    title VARCHAR(200) NOT NULL COMMENT '测验标题',
    pass_score INT DEFAULT 60 COMMENT '及格分数',
    time_limit INT DEFAULT 30 COMMENT '时间限制(分钟)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_chapter_id (chapter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测验表';

-- 测验题目表
CREATE TABLE IF NOT EXISTS quiz_question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT NOT NULL COMMENT '测验ID',
    question TEXT NOT NULL COMMENT '题目',
    type VARCHAR(20) DEFAULT 'single_choice' COMMENT '题型',
    options TEXT COMMENT '选项(JSON)',
    correct_answer VARCHAR(200) COMMENT '正确答案',
    score INT DEFAULT 10 COMMENT '分值',
    order_num INT DEFAULT 0 COMMENT '排序',
    INDEX idx_quiz_id (quiz_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测验题目表';

-- 测验记录表
CREATE TABLE IF NOT EXISTS quiz_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    quiz_id BIGINT NOT NULL COMMENT '测验ID',
    score INT DEFAULT 0 COMMENT '得分',
    passed TINYINT(1) DEFAULT 0 COMMENT '是否通过',
    submit_time DATETIME COMMENT '提交时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_quiz (user_id, quiz_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测验记录表';

-- 证书表
CREATE TABLE IF NOT EXISTS course_certificate (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    certificate_no VARCHAR(50) COMMENT '证书编号',
    issue_time DATETIME COMMENT '颁发时间',
    points INT DEFAULT 0 COMMENT '奖励积分',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_course (user_id, course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='证书表';

-- 学习资料表
CREATE TABLE IF NOT EXISTS learning_material (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chapter_id BIGINT NOT NULL COMMENT '章节ID',
    title VARCHAR(200) NOT NULL COMMENT '资料标题',
    type VARCHAR(20) DEFAULT 'document' COMMENT '类型',
    file_url VARCHAR(500) COMMENT '文件URL',
    file_size BIGINT DEFAULT 0 COMMENT '文件大小',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_chapter_id (chapter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习资料表';

SELECT '课程相关表创建完成！' AS message;
SHOW TABLES;
