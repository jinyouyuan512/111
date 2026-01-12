-- 查看 design 表中的所有记录
SELECT 
    id,
    designer_id,
    title,
    category_type,
    LEFT(description, 50) as description,
    LEFT(design_concept, 50) as design_concept,
    LEFT(files, 100) as files,
    LEFT(cover_image, 100) as cover_image,
    status,
    votes,
    views,
    created_at
FROM design
WHERE deleted = 0
ORDER BY created_at DESC;
