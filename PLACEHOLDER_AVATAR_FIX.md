# Placeholder头像错误修复

## 问题
浏览器控制台显示错误：
```
GET https://via.placeholder.com/150 net::ERR_CONNECTION_CLOSED
```

via.placeholder.com服务无法访问，导致用户头像无法加载。

## 根本原因
1. 数据库中的用户头像URL仍然使用`https://via.placeholder.com/150`
2. TopicServiceImpl中的活跃用户示例数据使用空字符串作为头像

## 解决方案

### 1. 更新数据库中的用户头像
**执行SQL**：
```sql
UPDATE user 
SET avatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
WHERE avatar LIKE '%placeholder.com%';
```

**结果**：所有使用placeholder的用户头像已更新为Element Plus CDN的默认头像。

### 2. 更新TopicServiceImpl
**文件**：`backend/social-service/src/main/java/com/jiyi/social/service/impl/TopicServiceImpl.java`

**修改内容**：
```java
// 从数据库获取的活跃用户
user.setAvatar("https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png");

// 示例用户数据
String defaultAvatar = "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png";
user1.setAvatar(defaultAvatar);
user2.setAvatar(defaultAvatar);
// ... 所有示例用户
```

## 使用的默认头像
Element Plus官方CDN头像：
```
https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png
```

这是一个稳定可靠的CDN服务，不会出现连接问题。

## 修改的文件
1. `backend/social-service/src/main/java/com/jiyi/social/service/impl/TopicServiceImpl.java`
2. 数据库：`jiyi_user.user` 表

## 服务状态
✅ social-service: http://localhost:8083 (已重启)
✅ user-service: http://localhost:8081
✅ mall-service: http://localhost:8085
✅ frontend: http://localhost:3001

## 测试验证
1. 刷新浏览器页面
2. 打开开发者工具 -> Console
3. 不应该再看到`via.placeholder.com`的错误
4. 所有用户头像应该正常显示

## 其他已修复的地方
之前已经修复的代码（不需要再改）：
- `PostService.java` - 创建帖子时的默认头像
- `CommentService.java` - 创建评论时的默认头像
- `data.sql` - 初始化数据中的头像URL

## 注意事项
如果将来需要更换默认头像，只需要：
1. 更新TopicServiceImpl中的`defaultAvatar`常量
2. 更新数据库中的头像URL
3. 重启social-service

建议将默认头像URL定义为配置项，放在`application.yml`中，方便统一管理。
