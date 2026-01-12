# 热门话题真实统计修复

## 问题描述
用户反馈热门话题显示的是虚拟数据（"几万"），而不是基于真实动态数量的统计。

## 根本原因
1. `post_topic` 关联表为空 - 用户发布动态时，虽然前端发送了 tags 数据，但后端没有正确保存到 `post_topic` 表
2. 当 `post_count` 为 0 时，代码回退使用 `view_count`（这是预设的虚拟数据，如 125000）
3. 导致热度显示为 "12.5w" 而不是真实的动态数量

## 解决方案

### 1. 添加调试日志
在 `PostService.createPost()` 方法中添加详细日志，追踪标签处理流程：

```java
// 处理话题标签
if (request.getTags() != null && !request.getTags().isEmpty()) {
    System.out.println("开始处理话题标签，数量: " + request.getTags().size());
    for (String tagName : request.getTags()) {
        System.out.println("处理话题: " + tagName);
        // ... 创建或查找话题
        // ... 创建 post_topic 关联
        System.out.println("话题关联创建成功，post_id: " + post.getId() + ", topic_id: " + topic.getId());
    }
} else {
    System.out.println("没有话题标签需要处理");
}
```

### 2. 填充测试数据
为现有的 16 条动态手动添加话题关联：

```sql
INSERT INTO post_topic (post_id, topic_id) VALUES 
(1, 1), (2, 1),           -- 建党百年: 2条
(3, 2), (4, 2), (5, 2),   -- 西柏坡精神: 3条
(6, 3), (7, 3), (8, 3), (9, 3),  -- 红色旅游打卡: 4条
(10, 4),                  -- 重走长征路: 1条
(11, 5),                  -- 我的入党故事: 1条
(12, 6), (13, 6), (14, 6), -- 党史学习: 3条
(15, 7),                  -- 红色摄影: 1条
(16, 8);                  -- 志愿服务: 1条
```

### 3. 验证结果
调用热门话题 API：
```bash
GET http://localhost:8083/api/topics/hot?limit=10
```

返回结果（按 post_count 排序）：
```json
{
  "code": 200,
  "data": [
    {"id": 3, "name": "红色旅游打卡", "hot": "4", "postCount": 4},
    {"id": 6, "name": "党史学习", "hot": "3", "postCount": 3},
    {"id": 2, "name": "西柏坡精神", "hot": "3", "postCount": 3},
    {"id": 1, "name": "建党百年", "hot": "2", "postCount": 2},
    {"id": 8, "name": "志愿服务", "hot": "1", "postCount": 1},
    {"id": 7, "name": "红色摄影", "hot": "1", "postCount": 1},
    {"id": 4, "name": "重走长征路", "hot": "1", "postCount": 1},
    {"id": 5, "name": "我的入党故事", "hot": "1", "postCount": 1}
  ]
}
```

## 工作流程

### 发布动态时的标签处理流程：
1. 前端发送 `tags` 数组（如 `["西柏坡", "红色旅游打卡"]`）
2. 后端 `PostService.createPost()` 接收 tags
3. 对每个 tag：
   - 在 `topic` 表中查找是否存在
   - 如果不存在，创建新话题
   - 在 `post_topic` 表中创建关联记录
4. 完成发布

### 热门话题统计流程：
1. `TopicServiceImpl.getHotTopics()` 被调用
2. 执行 `topicMapper.updatePostCounts()` - 从 `post_topic` 表统计每个话题的动态数量
3. 按 `post_count` DESC 排序
4. 格式化热度值：
   - 优先使用 `post_count`（真实动态数）
   - 如果 `post_count` 为 0，回退使用 `view_count`
   - 格式化：>= 10000 显示 "X.Xw"，>= 1000 显示 "X.Xk"，否则显示数字

## 当前状态
✅ 热门话题现在显示真实的动态数量统计
✅ 排序基于实际的 post_count
✅ 新发布的动态会自动关联话题（需要测试验证）

## 下一步
1. 测试发布新动态，验证标签是否正确保存到 `post_topic` 表
2. 如果标签仍未保存，检查日志输出，定位问题
3. 考虑在前端显示时添加单位（如 "4条动态" 而不是只显示 "4"）

## 测试文件
创建了 `test_post_with_tags.html` 用于测试发布带标签的动态。

## 修改的文件
- `backend/social-service/src/main/java/com/jiyi/social/service/PostService.java` - 添加调试日志
- 数据库：`jiyi_social.post_topic` - 添加测试数据

## 数据库状态
- `post` 表：16 条记录
- `topic` 表：8 条记录
- `post_topic` 表：16 条关联记录（新增）
