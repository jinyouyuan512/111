-- 查看最近发布的动态
SELECT 
    id,
    user_id,
    LEFT(content, 50) as content_preview,
    location_name,
    likes,
    comments,
    shares,
    views,
    status,
    created_at
FROM post 
WHERE deleted = 0
ORDER BY created_at DESC 
LIMIT 10;

-- 查看今天发布的动态数量
SELECT 
    COUNT(*) as today_posts,
    DATE(created_at) as post_date
FROM post 
WHERE DATE(created_at) = CURDATE()
GROUP BY DATE(created_at);

-- 查看用户ID为5的所有动态
SELECT 
    id,
    LEFT(content, 50) as content_preview,
    location_name,
    created_at
FROM post 
WHERE user_id = 5 
  AND deleted = 0
ORDER BY created_at DESC;
