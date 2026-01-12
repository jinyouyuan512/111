-- 检查 Creative Service 数据库中的作品数据
USE jiyi_creative;

-- 查看最近上传的作品
SELECT 
    id,
    title,
    designer_id,
    cover_image,
    files,
    created_at
FROM design
ORDER BY created_at DESC
LIMIT 10;

-- 检查 files 字段的格式
SELECT 
    id,
    title,
    SUBSTRING(files, 1, 200) as files_preview,
    SUBSTRING(cover_image, 1, 200) as cover_preview
FROM design
WHERE created_at >= '2026-01-03'
ORDER BY created_at DESC;
