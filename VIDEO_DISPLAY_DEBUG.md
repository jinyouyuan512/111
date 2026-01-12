# 视频显示问题调试

## 问题
视频上传成功，但在帖子列表中不显示

## 调试步骤

### 1. 打开测试页面
```bash
start test_video_display.html
```

这个页面会：
- 加载帖子列表
- 检查哪些帖子包含视频
- 显示视频数据结构
- 尝试播放视频

### 2. 检查数据库
在Navicat中执行 `check_video_data.sql`：

```sql
-- 查看视频记录
SELECT * FROM media_file WHERE type = 'video';

-- 查看最近的帖子及其媒体
SELECT 
    p.id, p.content, p.user_id,
    mf.type, mf.url, mf.thumbnail
FROM post p
LEFT JOIN media_file mf ON p.id = mf.post_id
ORDER BY p.created_at DESC
LIMIT 10;
```

### 3. 检查点

#### A. 视频是否上传成功？
- 检查 `backend/social-service/uploads/videos/` 目录
- 应该有视频文件（.mp4等）

#### B. 数据库是否有记录？
- `media_file` 表应该有 type='video' 的记录
- 记录应该关联到某个 post_id

#### C. API返回的数据是否包含video？
- 在测试页面查看帖子数据
- 每个帖子应该有 `video` 字段（如果包含视频）
- video 对象应该有 `url`, `thumbnail`, `duration`

#### D. 前端是否正确显示？
- 检查浏览器控制台是否有错误
- 检查 video 标签的 src 是否正确
- 检查视频URL是否可访问

## 可能的问题

### 问题1: 视频上传后没有保存到数据库
**症状：** uploads目录有文件，但media_file表没有记录

**原因：** PostService.createPost() 中保存video的逻辑没有执行

**解决：** 检查 PostCreateRequest 是否包含 video 字段

### 问题2: 数据库有记录，但API不返回
**症状：** media_file表有记录，但API返回的post没有video字段

**原因：** PostService.convertToVO() 中没有正确加载video

**解决：** 检查 convertToVO 方法的 video 处理逻辑

### 问题3: API返回了video，但前端不显示
**症状：** API返回包含video，但页面上看不到

**原因：** 
- 前端模板条件判断问题
- video URL 不正确
- CORS 问题

**解决：** 
- 检查 `v-if="post.video"` 条件
- 检查 video.url 是否正确
- 检查浏览器控制台错误

### 问题4: 视频URL 404
**症状：** video标签显示，但无法播放

**原因：** 
- 文件路径不正确
- 静态资源配置问题
- 文件不存在

**解决：** 
- 检查 FileUploadController 的 URL 生成逻辑
- 检查 WebMvcConfig 的静态资源映射
- 确认文件确实存在

## 快速诊断命令

### 检查uploads目录
```bash
dir backend\social-service\uploads\videos /s
```

### 检查最新上传的文件
```bash
dir backend\social-service\uploads\videos\2026\01\03 /o-d
```

### 测试视频URL
在浏览器直接访问：
```
http://localhost:8083/uploads/videos/2026/01/03/[filename].mp4
```

## 预期结果

### 正常情况下：
1. ✅ uploads目录有视频文件
2. ✅ media_file表有对应记录
3. ✅ API返回包含video字段
4. ✅ 前端显示video标签
5. ✅ 视频可以播放

### 当前状态检查：
- [ ] 视频文件存在
- [ ] 数据库有记录
- [ ] API返回video
- [ ] 前端显示video
- [ ] 视频可播放

## 下一步

1. 打开 `test_video_display.html`
2. 点击"加载帖子"按钮
3. 查看结果：
   - 如果"包含视频的帖子: 0"，说明API没有返回video数据
   - 如果有数字但不显示，说明前端渲染有问题
   - 如果显示但无法播放，说明URL或文件有问题

4. 根据结果进行针对性修复
