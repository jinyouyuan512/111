-- 检查视频数据

-- 1. 查看media_file表中的视频记录
SELECT * FROM media_file WHERE type = 'video';

-- 2. 查看最近的帖子及其媒体文件
SELECT 
    p.id as post_id,
    p.content,
    p.user_id,
    p.created_at,
    mf.id as media_id,
    mf.type,
    mf.url,
    mf.thumbnail,
    mf.duration
FROM post p
LEFT JOIN media_file mf ON p.id = mf.post_id
ORDER BY p.created_at DESC
LIMIT 10;

-- 3. 统计媒体文件类型
SELECT type, COUNT(*) as count
FROM media_file
GROUP BY type;

-- 4. 查看有视频的帖子
SELECT 
    p.id,
    p.content,
    p.user_id,
    COUNT(mf.id) as media_count
FROM post p
INNER JOIN media_file mf ON p.id = mf.post_id
WHERE mf.type = 'video'
GROUP BY p.id, p.content, p.user_id;
