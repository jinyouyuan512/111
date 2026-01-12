# 社区真实数据集成

## 完成内容

### 1. 前端API集成
创建了 `frontend/src/api/social.ts`，包含以下API：
- ✅ 获取动态列表
- ✅ 发布动态
- ✅ 点赞/取消点赞
- ✅ 获取/发表评论
- ✅ **获取热门话题**
- ✅ **获取活跃用户（红色达人）**
- ✅ 关注/取消关注用户

### 2. 后端API实现

#### TopicController
创建了 `backend/social-service/src/main/java/com/jiyi/social/controller/TopicController.java`

**API端点：**
- `GET /api/topics/hot` - 获取热门话题
- `GET /api/topics/users/active` - 获取活跃用户
- `GET /api/topics/search` - 搜索话题

#### TopicService
创建了服务层实现：
- `TopicService.java` - 接口定义
- `TopicServiceImpl.java` - 实现类

**功能：**
- 从数据库查询热门话题（按浏览量排序）
- 返回活跃用户列表（模拟数据，实际应从user-service获取）
- 话题搜索功能
- 热度格式化（12.5w、5.6k等）

#### DTO对象
- `TopicVO.java` - 话题视图对象
- 更新 `UserStatsVO.java` - 添加用户展示所需字段

### 3. 前端数据加载

修改 `frontend/src/views/Social.vue`：
- 使用 `socialApi.getHotTopics()` 加载热门话题
- 使用 `socialApi.getActiveUsers()` 加载活跃用户
- 添加错误处理，失败时使用默认数据
- 实现关注用户功能

## 数据结构

### TopicVO
```java
{
  id: Long,           // 话题ID
  name: String,       // 话题名称
  hot: String,        // 热度显示（如"12.5w"）
  postCount: Long,    // 帖子数量
  viewCount: Long     // 浏览量
}
```

### UserStatsVO
```java
{
  id: Long,              // 用户ID
  name: String,          // 用户名
  desc: String,          // 用户描述
  avatar: String,        // 头像URL
  postCount: Long,       // 动态数
  followerCount: Long    // 粉丝数
}
```

## 热度格式化规则

```java
if (views >= 10000) {
    hot = views / 10000.0 + "w"  // 12.5w
} else if (views >= 1000) {
    hot = views / 1000.0 + "k"   // 5.6k
} else {
    hot = String.valueOf(views)   // 234
}
```

## 活跃用户数据

**现在从真实数据库查询：**
- 从post表统计每个用户的发帖数
- 按发帖数降序排序
- 返回最活跃的用户列表
- 用户基本信息（用户名、头像等）暂时使用默认值
- 实际应该通过Feign客户端从user-service获取完整用户信息

**数据来源：**
- 用户ID：来自post表的user_id
- 发帖数：COUNT(*)统计
- 用户名：默认"用户{ID}"（待集成user-service）
- 描述：显示发帖数量
- 头像：空（待集成user-service）
- 粉丝数：0（待集成user-service）

## 后续优化

### 1. 活跃用户真实数据 ✅ 已实现
- ✅ 从post表统计用户发帖数
- ✅ 按发帖数排序返回最活跃用户
- ⚠️ 用户详细信息（用户名、头像、昵称）需要从user-service获取
- 建议：实现Feign客户端调用user-service获取完整用户信息
- 建议：支持缓存提高性能

### 2. 话题数据初始化
需要在数据库中初始化话题数据：
```sql
INSERT INTO topic (name, post_count, view_count) VALUES
('建党百年', 1250, 125000),
('西柏坡精神', 820, 82000),
('红色旅游打卡', 560, 56000),
('重走长征路', 340, 34000),
('我的入党故事', 210, 21000);
```

### 3. 实时更新
- 发布动态时自动更新话题的post_count
- 浏览话题时更新view_count
- 定时任务更新热度排名

### 4. 用户关注功能
- 实现真实的关注/取消关注API
- 更新用户粉丝数统计
- 支持查看关注列表

### 5. 话题详情页
- 点击话题查看相关动态
- 话题趋势图表
- 话题参与用户排行

## 文件清单

### 前端
- `frontend/src/api/social.ts` - 新建
- `frontend/src/views/Social.vue` - 修改

### 后端
- `backend/social-service/src/main/java/com/jiyi/social/controller/TopicController.java` - 新建
- `backend/social-service/src/main/java/com/jiyi/social/service/TopicService.java` - 新建
- `backend/social-service/src/main/java/com/jiyi/social/service/impl/TopicServiceImpl.java` - 新建（使用真实数据库查询）
- `backend/social-service/src/main/java/com/jiyi/social/mapper/PostMapper.java` - 修改（添加活跃用户查询方法）
- `backend/social-service/src/main/java/com/jiyi/social/dto/TopicVO.java` - 新建
- `backend/social-service/src/main/java/com/jiyi/social/dto/UserStatsVO.java` - 修改

## 测试要点

- [ ] 热门话题列表正常显示
- [ ] 话题热度格式化正确（w/k）
- [ ] 活跃用户列表正常显示
- [ ] 用户头像、名称、描述显示正确
- [ ] 关注按钮点击有响应
- [ ] API失败时使用默认数据
- [ ] 页面加载性能良好
- [ ] 数据刷新机制正常

## API测试

### 获取热门话题
```bash
curl http://localhost:8083/api/topics/hot
```

### 获取活跃用户
```bash
curl http://localhost:8083/api/topics/users/active
```

### 搜索话题
```bash
curl http://localhost:8083/api/topics/search?keyword=西柏坡
```

## 注意事项

1. **跨服务调用**：活跃用户数据理想情况下应该从user-service获取，需要实现服务间调用
2. **数据一致性**：话题统计数据需要定期同步更新
3. **缓存策略**：热门话题和活跃用户可以缓存，减少数据库查询
4. **权限控制**：关注功能需要登录认证
5. **性能优化**：大量用户访问时需要考虑缓存和CDN
