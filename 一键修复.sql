-- ========================================
-- 一键修复 Error 1048
-- ========================================
-- 复制这个文件的全部内容，粘贴到 MySQL 中执行

-- 1. 选择数据库
USE jiyi_creative;

-- 2. 显示当前数据库（确认）
SELECT DATABASE() AS '当前数据库';

-- 3. 修改字段约束（允许 NULL）
ALTER TABLE design MODIFY COLUMN contest_id BIGINT NULL COMMENT '大赛ID（可选）';
ALTER TABLE design MODIFY COLUMN call_id BIGINT NULL COMMENT '征集ID（可选）';
ALTER TABLE design MODIFY COLUMN category_type INT NULL COMMENT '作品分类（可选）';
ALTER TABLE design MODIFY COLUMN design_concept TEXT NULL COMMENT '设计理念（可选）';
ALTER TABLE design MODIFY COLUMN cover_image VARCHAR(500) NULL COMMENT '封面图片（可选）';
ALTER TABLE design MODIFY COLUMN copyright_statement VARCHAR(500) NULL COMMENT '版权声明（可选）';
ALTER TABLE design MODIFY COLUMN tags VARCHAR(500) NULL COMMENT '标签（可选）';
ALTER TABLE design MODIFY COLUMN reject_reason VARCHAR(500) NULL COMMENT '拒绝原因（可选）';

-- 4. 验证修复结果
SELECT '========================================' AS '';
SELECT '修复完成！验证结果：' AS '';
SELECT '========================================' AS '';

SELECT 
    COLUMN_NAME AS '字段名',
    COLUMN_TYPE AS '类型',
    IS_NULLABLE AS '允许NULL',
    CASE 
        WHEN IS_NULLABLE = 'YES' THEN '✓ 正确'
        ELSE '✗ 错误'
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

SELECT '========================================' AS '';
SELECT '如果所有字段都显示 ✓ 正确，修复成功！' AS '';
SELECT '现在可以刷新浏览器测试上传功能了' AS '';
SELECT '========================================' AS '';
