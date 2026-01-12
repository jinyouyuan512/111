# 热门话题统计和排行功能实现

## 功能说明
实现了基于真实数据的热门话题统计和排行功能，根据话题在动态中的使用次数进行排序。

## 实现方案

### 1. 数据库表结构
使用三个表实现话题功能：

**topic表** - 存储话题信息
- id: 话题ID
- name: 话题名称
- post_count: 使用该话题的动态数量
- view_count: 话题浏览量

**post表** - 存储动态信息
- id: 动态ID
- user_id: 用户ID
- content: 动态内容

**post_topic表** - 动态和话题的关联表
- id: 关联ID
- post_id: 动态ID
- topic_id: 话题ID

### 2. 统计逻辑

#### TopicMapper.updatePostCounts()
```sql
UPDATE topic t 
SET t.post_count = (
    SELECT COUNT(*) 
    FROM post_topic pt 
    WHERE pt.topic_id = t.id
)
```
这个SQL会统计每个话题在`post_topic`表中出现的次数，更新到`topic`表的`post_count`字段。

#### TopicServiceImpl.getHotTopics()
1. 先调用`updatePostCounts()`更新所有话题的统计数据
2. 按`post_count`降序排序（使用次数多的排前面）
3. 如果`post_count`相同，再按`view_count`降序排序
4. 返回前N个热门话题

### 3. 热度显示格式
- 10000+: 显示为 "X.Xw"（万）
- 1000+: 显示为 "X.Xk"（千）
- <1000: 显示实际数字

优先使用`post_count`（实际使用次数），如果为0则使用`view_count`（浏览量）。

### 4. 默认话题
如果数据库中没有话题数据，返回默认话题列表：
1. 建党百年 - 12.5w
2. 西柏坡精神 - 8.2w
3. 红色旅游打卡 - 5.6w
4. 重走长征路 - 3.4w
5. 我的入党故事 - 2.1w

## 修改的文件

### backend/social-service/src/main/java/com/jiyi/social/mapper/TopicMapper.java
添加了`updatePostCounts()`方法，用于统计话题使用次数。

### backend/social-service/src/main/java/com/jiyi/social/service/impl/TopicServiceImpl.java
更新了`getHotTopics()`方法：
- 调用统计方法更新数据
- 按post_count排序
- 添加默认话题支持
- 优化热度显示逻辑

## 使用方式

### API调用
```
GET /api/topics/hot?limit=5
```

返回格式：
```json
[
  {
    "id": 1,
    "name": "建党百年",
    "postCount": 125,
    "viewCount": 125000,
    "hot": "12.5w"
  },
  ...
]
```

### 前端显示
热门话题会显示在社交平台右侧边栏，包括：
- 排名（前3名高亮显示）
- 话题名称（带#标签）
- 热度值

## 数据流程

1. **用户发布动态时添加话题**
   - 前端：用户在发布框中添加话题标签
   - 后端：创建动态时，将话题保存到`post_topic`表

2. **查询热门话题**
   - 统计：从`post_topic`表统计每个话题的使用次数
   - 更新：更新`topic`表的`post_count`字段
   - 排序：按`post_count`降序返回

3. **前端展示**
   - 显示话题名称和热度
   - 前3名用特殊样式高亮
   - 点击话题可以查看相关动态

## 性能优化建议

1. **定时任务**：可以使用定时任务（如每小时）更新话题统计，而不是每次查询都更新
2. **缓存**：可以将热门话题列表缓存到Redis，减少数据库查询
3. **索引**：确保`post_topic`表的`topic_id`字段有索引

## 测试步骤

1. **发布带话题的动态**
   - 登录系统
   - 发布动态时添加话题标签（如 #西柏坡精神）
   - 发布成功

2. **查看热门话题**
   - 刷新页面
   - 查看右侧边栏的热门话题列表
   - 应该能看到刚才添加的话题

3. **验证排序**
   - 多次使用同一个话题发布动态
   - 该话题的排名应该上升
   - 热度数字应该增加

## 服务状态
✅ social-service: http://localhost:8083 (已重启)
✅ user-service: http://localhost:8081
✅ mall-service: http://localhost:8085
✅ frontend: http://localhost:3001
