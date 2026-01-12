-- 修复创意作品表的 NULL 约束问题
-- Error 1048: Column cannot be NULL

USE jiyi_creative;

-- 1. 查看当前表结构
DESC design;

-- 2. 修改可能导致问题的字段为允许 NULL
-- 这些字段应该是可选的，不应该有 NOT NULL 约束

ALTER TABLE design 
MODIFY COLUMN contest_id BIGINT NULL COMMENT '大赛ID（可选）';

ALTER TABLE design 
MODIFY COLUMN call_id BIGINT NULL COMMENT '征集ID（可选）';

ALTER TABLE design 
MODIFY COLUMN category_type INT NULL COMMENT '作品分类（可选）';

ALTER TABLE design 
MODIFY COLUMN design_concept TEXT NULL COMMENT '设计理念（可选）';

ALTER TABLE design 
MODIFY COLUMN cover_image VARCHAR(500) NULL COMMENT '封面图片（可选）';

ALTER TABLE design 
MODIFY COLUMN copyright_statement VARCHAR(500) NULL COMMENT '版权声明（可选）';

ALTER TABLE design 
MODIFY COLUMN tags VARCHAR(500) NULL COMMENT '标签（可选）';

ALTER TABLE design 
MODIFY COLUMN reject_reason VARCHAR(500) NULL COMMENT '拒绝原因（可选）';

-- 3. 确保必填字段有 NOT NULL 约束
ALTER TABLE design 
MODIFY COLUMN designer_id BIGINT NOT NULL COMMENT '设计师ID（必填）';

ALTER TABLE design 
MODIFY COLUMN title VARCHAR(200) NOT NULL COMMENT '作品标题（必填）';

ALTER TABLE design 
MODIFY COLUMN description TEXT NOT NULL COMMENT '作品描述（必填）';

ALTER TABLE design 
MODIFY COLUMN files TEXT NOT NULL COMMENT '作品文件（必填）';

ALTER TABLE design 
MODIFY COLUMN status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态（必填）';

ALTER TABLE design 
MODIFY COLUMN votes INT NOT NULL DEFAULT 0 COMMENT '投票数（必填）';

ALTER TABLE design 
MODIFY COLUMN views INT NOT NULL DEFAULT 0 COMMENT '浏览量（必填）';

-- 4. 验证修改
DESC design;

-- 5. 测试插入（可选）
-- INSERT INTO design (designer_id, title, description, files, status, votes, views)
-- VALUES (1, '测试作品', '测试描述', '["http://test.jpg"]', 'pending', 0, 0);

SELECT '✓ 数据库字段约束已修复' AS status;
