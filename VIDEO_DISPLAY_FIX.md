# 视频显示修复

## 问题
视频上传成功并保存到数据库，但在帖子列表中不显示。

## 根本原因
前端 `Social.vue` 的 `loadPosts()` 函数在转换后端数据时，将 `video` 字段硬编码为 `null`，而不是从后端响应中映射视频数据。

## 修复内容

### 1. 前端修复 (frontend/src/views/Social.vue)
**文件**: `frontend/src/views/Social.vue`
**行号**: 651

**修改前**:
```typescript
video: null,
```

**修改后**:
```typescript
video: post.video || null,
```

这样前端就会正确映射后端返回的视频数据。

### 2. 后端验证
后端代码已经正确实现：
- `PostService.convertToVO()` 方法正确加载 `media_file` 表中的视频记录
- 创建 `VideoVO` 对象并设置 `url`, `thumbnail`, `duration` 字段
- 将 `VideoVO` 设置到 `PostVO.video` 字段

### 3. 数据库验证
使用以下 SQL 验证视频数据存在：
```sql
SELECT p.id, p.content, m.type, m.url, m.duration
FROM post p
LEFT JOIN media_file m ON p.id = m.post_id
WHERE p.id = 31;
```

预期结果：应该看到 post_id=31 有一条 type='video' 的记录。

## 测试步骤

### 1. 重启前端服务
```bash
cd frontend
npm run dev
```

### 2. 测试视频显示
1. 打开浏览器访问 http://localhost:3001
2. 登录账号 (username: chovy, password: 123456)
3. 进入社区页面
4. 查看帖子列表，应该能看到 post_id=31 的视频
5. 视频应该显示播放器控件，可以播放
6. 视频时长应该显示为 "2:01" (121秒)

### 3. 测试新视频上传
1. 点击"发布动态"按钮
2. 点击视频图标，上传新视频
3. 预览视频应该可以播放
4. 发布后，帖子列表应该立即显示新视频

## 预期结果
- ✅ 已有视频帖子在列表中正确显示
- ✅ 视频播放器有控件，可以播放
- ✅ 视频时长正确显示
- ✅ 新上传的视频立即在列表中显示

## 技术细节

### 前端视频显示组件
```vue
<div v-if="post.video" class="post-video">
  <video 
    :src="post.video.url" 
    :poster="post.video.thumbnail"
    controls
    class="video-player"
  ></video>
</div>
```

### 后端数据流
1. `PostService.getPosts()` 查询 post 表
2. `convertToVO()` 为每个 post 查询 media_file 表
3. 找到 type='video' 的记录，创建 VideoVO
4. 返回包含 video 字段的 PostVO

### 数据库结构
```sql
media_file 表:
- id: 主键
- post_id: 关联的帖子ID
- type: 'image' 或 'video'
- url: 文件URL
- thumbnail: 缩略图URL (视频用)
- duration: 时长秒数 (视频用)
- order_num: 排序
```

## 相关文件
- `frontend/src/views/Social.vue` - 前端社区页面
- `backend/social-service/src/main/java/com/jiyi/social/service/PostService.java` - 后端帖子服务
- `backend/social-service/src/main/java/com/jiyi/social/dto/VideoVO.java` - 视频VO
- `backend/social-service/src/main/java/com/jiyi/social/entity/MediaFile.java` - 媒体文件实体

## 修复时间
2025-01-03
