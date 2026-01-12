-- 验证数据库修复状态

-- 1. 确认使用正确的数据库
USE jiyi_creative;

-- 2. 查看 design 表结构
DESC design;

-- 3. 检查哪些字段允许 NULL
SELECT 
    COLUMN_NAME,
    COLUMN_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'jiyi_creative'
  AND TABLE_NAME = 'design'
  AND COLUMN_NAME IN (
    'contest_id', 'call_id', 'category_type', 
    'design_concept', 'cover_image', 
    'copyright_statement', 'tags', 'reject_reason'
  )
ORDER BY ORDINAL_POSITION;

-- 4. 如果上面显示 IS_NULLABLE = 'NO'，说明 SQL 没有执行成功
-- 需要重新执行修复 SQL

-- 5. 测试插入（不会真正插入，只是测试）
-- 如果这个查询不报错，说明修复成功
EXPLAIN INSERT INTO design (
    designer_id, title, description, files, status, votes, views
) VALUES (
    1, '测试', '测试描述', '["test.jpg"]', 'pending', 0, 0
);
