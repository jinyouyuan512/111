-- 河北红色新闻表
CREATE TABLE IF NOT EXISTS red_news (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '新闻标题',
    summary VARCHAR(500) COMMENT '新闻摘要',
    content TEXT COMMENT '新闻内容',
    cover_image VARCHAR(500) COMMENT '封面图片',
    category VARCHAR(50) NOT NULL COMMENT '分类: party-news, red-story, memorial, education',
    source VARCHAR(100) COMMENT '新闻来源',
    author VARCHAR(50) COMMENT '作者',
    view_count INT DEFAULT 0 COMMENT '阅读量',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    is_top TINYINT(1) DEFAULT 0 COMMENT '是否置顶',
    is_hot TINYINT(1) DEFAULT 0 COMMENT '是否热门',
    publish_time DATETIME COMMENT '发布时间',
    external_url VARCHAR(500) COMMENT '外部链接URL',
    status VARCHAR(20) DEFAULT 'published' COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_category (category),
    INDEX idx_publish_time (publish_time),
    INDEX idx_is_top (is_top),
    INDEX idx_is_hot (is_hot)
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
    category VARCHAR(50) NOT NULL COMMENT '分类: classic, history, biography, documentary, youth',
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
    tags VARCHAR(500) COMMENT '标签，逗号分隔',
    external_url VARCHAR(500) COMMENT '外部购买链接URL',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_category (category),
    INDEX idx_is_recommended (is_recommended),
    INDEX idx_rating (rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='红色读物表';

-- 用户阅读记录表
CREATE TABLE IF NOT EXISTS user_reading_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    book_id BIGINT NOT NULL COMMENT '书籍ID',
    progress INT DEFAULT 0 COMMENT '阅读进度(百分比)',
    last_read_at DATETIME COMMENT '最后阅读时间',
    is_favorite TINYINT(1) DEFAULT 0 COMMENT '是否收藏',
    user_rating DECIMAL(2,1) COMMENT '用户评分',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_book (user_id, book_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户阅读记录表';
