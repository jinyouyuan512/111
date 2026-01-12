-- 快速检查数据库状态
-- 复制这整个文件的内容，粘贴到 MySQL 中执行

USE jiyi_creative;

-- 显示当前数据库
SELECT DATABASE() AS current_database;

-- 检查字段约束
SELECT 
    COLUMN_NAME AS '字段名',
    COLUMN_TYPE AS '类型',
    IS_NULLABLE AS '允许NULL',
    CASE 
        WHEN IS_NULLABLE = 'YES' THEN '✓ 正确'
        ELSE '✗ 错误 - 需要修复'
    END AS '状态'
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'jiyi_creative'
  AND TABLE_NAME = 'design'
  AND COLUMN_NAME IN (
    'contest_id', 'call_id', 'category_type', 
    'design_concept', 'cover_image', 
    'copyright_statement', 'tags', 'reject_reason'
  )
ORDER BY ORDINAL_POSITION;

-- 如果上面显示有"✗ 错误"，执行下面的修复 SQL
-- 否则跳过，直接重启创意服务
