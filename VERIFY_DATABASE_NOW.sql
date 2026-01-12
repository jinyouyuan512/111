-- ========================================
-- 验证数据库修复状态
-- ========================================
-- 在 MySQL 中执行这个文件来检查数据库是否已正确修复

USE jiyi_creative;

-- 显示当前使用的数据库
SELECT DATABASE() AS '当前数据库';

-- 检查 design 表的字段约束
SELECT 
    COLUMN_NAME AS '字段名',
    COLUMN_TYPE AS '数据类型',
    IS_NULLABLE AS '允许NULL',
    CASE 
        WHEN COLUMN_NAME IN ('contest_id', 'call_id', 'category_type', 'design_concept', 
                             'cover_image', 'copyright_statement', 'tags', 'reject_reason') 
             AND IS_NULLABLE = 'YES' THEN '✓ 正确'
        WHEN COLUMN_NAME IN ('contest_id', 'call_id', 'category_type', 'design_concept', 
                             'cover_image', 'copyright_statement', 'tags', 'reject_reason') 
             AND IS_NULLABLE = 'NO' THEN '✗ 需要修复'
        WHEN COLUMN_NAME IN ('designer_id', 'title', 'description', 'files', 'status') 
             AND IS_NULLABLE = 'NO' THEN '✓ 正确'
        ELSE '检查'
    END AS '状态'
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'jiyi_creative'
  AND TABLE_NAME = 'design'
  AND COLUMN_NAME IN (
    'designer_id', 'contest_id', 'call_id', 'title', 'category_type', 
    'description', 'design_concept', 'files', 'cover_image', 
    'copyright_statement', 'tags', 'reject_reason', 'status'
  )
ORDER BY ORDINAL_POSITION;

-- 如果看到任何 "✗ 需要修复"，说明 SQL 还没有正确执行
-- 请执行 FIX_CREATIVE_1048_ERROR.sql 中的命令
