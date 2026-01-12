-- 体验馆数据库表结构 V2 - 增强版（沉浸式3D + 游戏化）

-- =====================================================
-- 原有表结构保持不变
-- =====================================================

-- 场景表增强字段
ALTER TABLE `scene` ADD COLUMN IF NOT EXISTS `difficulty` TINYINT DEFAULT 1 COMMENT '难度等级 1-5';
ALTER TABLE `scene` ADD COLUMN IF NOT EXISTS `reward_points` INT DEFAULT 100 COMMENT '完成奖励积分';
ALTER TABLE `scene` ADD COLUMN IF NOT EXISTS `panorama_url` VARCHAR(255) COMMENT '360全景图URL';
ALTER TABLE `scene` ADD COLUMN IF NOT EXISTS `audio_url` VARCHAR(255) COMMENT '背景音频URL';
ALTER TABLE `scene` ADD COLUMN IF NOT EXISTS `unlock_condition` VARCHAR(100) COMMENT '解锁条件';
ALTER TABLE `scene` ADD COLUMN IF NOT EXISTS `sort_order` INT DEFAULT 0 COMMENT '排序顺序';

-- 交互点表增强字段
ALTER TABLE `interaction_point` ADD COLUMN IF NOT EXISTS `points` INT DEFAULT 10 COMMENT '完成获得积分';
ALTER TABLE `interaction_point` ADD COLUMN IF NOT EXISTS `quiz_id` BIGINT COMMENT '关联的答题ID';

-- =====================================================
-- 新增表：用户成就系统
-- =====================================================

-- 成就定义表
CREATE TABLE IF NOT EXISTS `achievement` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code` VARCHAR(50) NOT NULL COMMENT '成就代码',
  `name` VARCHAR(100) NOT NULL COMMENT '成就名称',
  `description` VARCHAR(255) COMMENT '成就描述',
  `icon` VARCHAR(100) COMMENT '成就图标',
  `category` VARCHAR(50) COMMENT '成就分类',
  `condition_type` VARCHAR(50) NOT NULL COMMENT '条件类型',
  `condition_value` INT NOT NULL COMMENT '条件值',
  `reward_points` INT DEFAULT 0 COMMENT '奖励积分',
  `rarity` TINYINT DEFAULT 1 COMMENT '稀有度 1普通 2稀有 3史诗 4传说',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `deleted` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成就定义表';

-- 用户成就记录表
CREATE TABLE IF NOT EXISTS `user_achievement` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `achievement_id` BIGINT NOT NULL COMMENT '成就ID',
  `unlock_time` DATETIME NOT NULL COMMENT '解锁时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_achievement` (`user_id`, `achievement_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户成就记录表';

-- =====================================================
-- 新增表：任务系统
-- =====================================================

-- 任务定义表
CREATE TABLE IF NOT EXISTS `task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `scene_id` BIGINT NOT NULL COMMENT '所属场景ID',
  `name` VARCHAR(100) NOT NULL COMMENT '任务名称',
  `description` TEXT COMMENT '任务描述',
  `type` VARCHAR(30) NOT NULL COMMENT '任务类型: quiz/explore/collect/challenge',
  `target_value` INT DEFAULT 1 COMMENT '目标值',
  `reward_points` INT DEFAULT 50 COMMENT '奖励积分',
  `time_limit` INT COMMENT '时间限制（秒）',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `deleted` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_scene_id` (`scene_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务定义表';

-- 用户任务进度表
CREATE TABLE IF NOT EXISTS `user_task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `task_id` BIGINT NOT NULL COMMENT '任务ID',
  `current_value` INT DEFAULT 0 COMMENT '当前进度值',
  `status` TINYINT DEFAULT 0 COMMENT '状态 0进行中 1已完成 2已领取奖励',
  `start_time` DATETIME COMMENT '开始时间',
  `complete_time` DATETIME COMMENT '完成时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_task` (`user_id`, `task_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户任务进度表';

-- =====================================================
-- 新增表：答题系统
-- =====================================================

-- 题目表
CREATE TABLE IF NOT EXISTS `quiz` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `scene_id` BIGINT NOT NULL COMMENT '所属场景ID',
  `interaction_point_id` BIGINT COMMENT '关联交互点ID',
  `question` TEXT NOT NULL COMMENT '题目内容',
  `type` VARCHAR(20) DEFAULT 'single' COMMENT '题目类型: single/multiple/truefalse',
  `options` JSON COMMENT '选项JSON数组',
  `answer` VARCHAR(50) NOT NULL COMMENT '正确答案',
  `explanation` TEXT COMMENT '答案解析',
  `points` INT DEFAULT 10 COMMENT '答对获得积分',
  `difficulty` TINYINT DEFAULT 1 COMMENT '难度 1-3',
  `sort_order` INT DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `deleted` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_scene_id` (`scene_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';

-- 用户答题记录表
CREATE TABLE IF NOT EXISTS `user_quiz_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `quiz_id` BIGINT NOT NULL COMMENT '题目ID',
  `user_answer` VARCHAR(50) COMMENT '用户答案',
  `is_correct` TINYINT DEFAULT 0 COMMENT '是否正确',
  `points_earned` INT DEFAULT 0 COMMENT '获得积分',
  `answer_time` DATETIME NOT NULL COMMENT '答题时间',
  `time_spent` INT COMMENT '用时（秒）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_quiz_id` (`quiz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户答题记录表';

-- =====================================================
-- 新增表：用户积分与等级
-- =====================================================

-- 用户体验馆数据表
CREATE TABLE IF NOT EXISTS `user_experience_profile` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `total_points` INT DEFAULT 0 COMMENT '总积分',
  `level` INT DEFAULT 1 COMMENT '等级',
  `exp` INT DEFAULT 0 COMMENT '当前经验值',
  `scenes_completed` INT DEFAULT 0 COMMENT '完成场景数',
  `tasks_completed` INT DEFAULT 0 COMMENT '完成任务数',
  `quizzes_correct` INT DEFAULT 0 COMMENT '答对题目数',
  `total_time_spent` INT DEFAULT 0 COMMENT '总体验时长（分钟）',
  `streak_days` INT DEFAULT 0 COMMENT '连续打卡天数',
  `last_checkin_date` DATE COMMENT '最后打卡日期',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户体验馆数据表';

-- 积分记录表
CREATE TABLE IF NOT EXISTS `points_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `points` INT NOT NULL COMMENT '积分变化',
  `type` VARCHAR(30) NOT NULL COMMENT '类型: scene/task/quiz/achievement/checkin',
  `source_id` BIGINT COMMENT '来源ID',
  `description` VARCHAR(255) COMMENT '描述',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分记录表';

-- =====================================================
-- 新增表：排行榜
-- =====================================================

-- 排行榜表（定期更新）
CREATE TABLE IF NOT EXISTS `leaderboard` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `username` VARCHAR(50) COMMENT '用户名',
  `avatar` VARCHAR(255) COMMENT '头像',
  `total_points` INT DEFAULT 0 COMMENT '总积分',
  `level` INT DEFAULT 1 COMMENT '等级',
  `rank_position` INT COMMENT '排名',
  `period` VARCHAR(20) NOT NULL COMMENT '周期: daily/weekly/monthly/all',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_period_rank` (`period`, `rank_position`),
  KEY `idx_user_period` (`user_id`, `period`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排行榜表';
