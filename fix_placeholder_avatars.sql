-- 修复user表中的placeholder头像
UPDATE user 
SET avatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
WHERE avatar LIKE '%placeholder.com%';

-- 检查更新结果
SELECT id, username, avatar FROM user WHERE avatar LIKE '%elemecdn%';
