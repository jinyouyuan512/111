# 视频上传完整修复方案

## 当前状态
- ✅ 视频上传API正常
- ✅ 后端保存逻辑正常  
- ✅ 前端显示模板正常
- ❓ 视频上传后不显示

## 问题诊断

### 步骤1: 使用测试页面诊断
```bash
start test_video_display.html
```

查看结果：
- 如果显示"包含视频的帖子: 0" → 说明没有帖子包含视频数据
- 如果有数字 → 说明有视频数据，但可能显示有问题

### 步骤2: 检查数据库
在Navicat中执行：
```sql
-- 查看所有视频记录
SELECT * FROM media_file WHERE type = 'video';

-- 如果没有记录，说明视频没有保存到数据库
-- 如果有记录，记下post_id

-- 查看对应的帖子
SELECT * FROM post WHERE id IN (
    SELECT DISTINCT post_id FROM media_file WHERE type = 'video'
);
```

### 步骤3: 测试完整流程

#### 3.1 上传视频
1. 访问 http://localhost:3001/social
2. 点击"发布动态"
3. 输入内容："测试视频上传"
4. 点击视频图标（摄像机）
5. 选择一个小视频文件（<100MB）
6. 等待上传成功提示
7. 点击"发布"

#### 3.2 检查结果
刷新页面，应该能看到刚发布的帖子，并且包含视频。

## 可能的问题和解决方案

### 问题A: 视频上传成功，但发布帖子时没有包含video数据

**症状：**
- 上传视频显示"上传成功"
- 但发布后帖子没有视频
- media_file表没有video记录

**原因：**
前端在提交帖子时，newPost.video 可能为空或格式不对

**解决方案：**
检查 Social.vue 的 submitPost 方法：

```typescript
const submitPost = async () => {
  // ... 验证逻辑 ...
  
  const postData = {
    content: newPost.value.content,
    images: newPost.value.images,
    video: newPost.value.video,  // 确保这里有值
    tags: newPost.value.tags,
    location: newPost.value.location,
    category: newPost.value.category
  }
  
  console.log('提交的帖子数据:', postData)  // 添加日志
  
  const response = await socialApi.createPost(postData)
  // ...
}
```

### 问题B: 后端没有正确接收video数据

**症状：**
- 前端发送了video数据
- 但后端没有保存到数据库

**原因：**
PostCreateRequest 可能没有正确接收video字段

**解决方案：**
检查 PostCreateRequest.java：

```java
@Data
public class PostCreateRequest {
    private String content;
    private List<String> images;
    private VideoDTO video;  // 确保有这个字段
    private List<String> tags;
    private String location;
    private String category;
}
```

### 问题C: 视频数据在数据库，但API不返回

**症状：**
- media_file表有video记录
- 但GET /api/posts 不返回video字段

**原因：**
convertToVO 方法可能有问题

**解决方案：**
已经检查过，代码是对的。可能是缓存问题，重启social-service。

## 完整测试流程

### 1. 准备测试视频
准备一个小视频文件（<10MB），例如：
- test.mp4
- sample.mp4

### 2. 清空旧数据（可选）
```sql
-- 删除所有视频记录（谨慎操作！）
DELETE FROM media_file WHERE type = 'video';
```

### 3. 上传测试

#### 方法A: 使用应用界面
1. 登录系统
2. 进入社交平台
3. 点击"发布动态"
4. 输入内容
5. 点击视频图标
6. 选择视频文件
7. 等待上传（会显示"正在上传视频，请稍候..."）
8. 看到"视频上传成功"提示
9. 确认对话框中显示"已添加视频"
10. 点击"发布"

#### 方法B: 使用测试页面
打开 `test_video_upload.html`：
1. 选择视频文件
2. 点击"上传视频"
3. 查看返回的URL
4. 复制URL用于手动创建帖子

### 4. 验证结果

#### 4.1 检查文件
```bash
dir backend\social-service\uploads\videos\2026\01\03
```
应该看到上传的视频文件。

#### 4.2 检查数据库
```sql
SELECT * FROM media_file WHERE type = 'video' ORDER BY id DESC LIMIT 1;
```
应该看到最新的视频记录。

#### 4.3 检查API
访问：http://localhost:8083/api/posts?page=1&size=20

返回的数据中应该有：
```json
{
  "id": 123,
  "content": "测试视频上传",
  "video": {
    "url": "http://localhost:8083/uploads/videos/2026/01/03/xxx.mp4",
    "thumbnail": "http://localhost:8083/uploads/videos/2026/01/03/xxx.mp4",
    "duration": 10
  }
}
```

#### 4.4 检查前端显示
刷新 http://localhost:3001/social

应该看到：
- 帖子内容
- 视频播放器
- 可以点击播放

## 调试技巧

### 1. 查看上传响应
在浏览器控制台（F12）的Network标签中：
- 找到 `/api/upload/video` 请求
- 查看Response，应该返回：
```json
{
  "code": 200,
  "data": {
    "url": "http://localhost:8083/uploads/videos/...",
    "thumbnail": "...",
    "duration": 10
  }
}
```

### 2. 查看创建帖子请求
在Network标签中：
- 找到 `POST /api/posts` 请求
- 查看Request Payload，应该包含：
```json
{
  "content": "...",
  "video": {
    "url": "...",
    "thumbnail": "...",
    "duration": 10
  }
}
```

### 3. 查看帖子列表响应
在Network标签中：
- 找到 `GET /api/posts` 请求
- 查看Response，检查是否有video字段

### 4. 查看控制台日志
在浏览器控制台查看：
- 是否有错误信息
- 视频上传的日志
- 帖子创建的日志

## 常见错误

### 错误1: "视频上传失败"
**原因：** 
- 文件太大（>100MB）
- 文件格式不支持
- 网络问题

**解决：**
- 使用更小的文件
- 确保是视频格式（.mp4, .avi等）
- 检查网络连接

### 错误2: 上传成功但发布失败
**原因：**
- 内容为空
- 后端验证失败

**解决：**
- 确保输入了内容
- 查看后端日志

### 错误3: 发布成功但不显示视频
**原因：**
- video数据没有正确传递
- 前端条件判断问题

**解决：**
- 在submitPost中添加console.log
- 检查newPost.value.video是否有值

## 最终检查清单

发布带视频的帖子后，检查：

- [ ] uploads目录有视频文件
- [ ] media_file表有对应记录
- [ ] 记录的post_id正确
- [ ] GET /api/posts返回包含video
- [ ] 前端页面显示video标签
- [ ] 视频可以播放
- [ ] 视频URL可以直接访问

如果所有都✅，说明功能正常。
如果有❌，根据具体情况进行修复。

## 快速修复命令

如果需要重启服务：
```bash
# 停止social-service (Ctrl+C)
# 重新启动
cd backend/social-service
mvn spring-boot:run
```

如果需要清理缓存：
```bash
# 清理浏览器缓存
Ctrl + Shift + Delete

# 或者硬刷新
Ctrl + Shift + R
```
