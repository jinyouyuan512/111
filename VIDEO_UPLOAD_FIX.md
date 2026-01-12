# 视频上传功能修复

## 问题描述
用户反馈视频无法上传成功显示。

## 根本原因
1. **PostCreateRequest 缺少 video 字段** - 后端无法接收前端发送的视频数据
2. **PostService 未处理视频** - 即使接收到视频数据，也没有保存到 media_file 表
3. **PostVO 缺少 video 字段** - 查询动态时无法返回视频信息给前端
4. **VideoDTO 类不存在** - 缺少视频数据传输对象

## 解决方案

### 1. 创建 VideoDTO 类
创建独立的 DTO 类用于视频数据传输：

```java
// backend/social-service/src/main/java/com/jiyi/social/dto/VideoDTO.java
@Data
@Schema(description = "视频信息DTO")
public class VideoDTO {
    private String url;
    private String thumbnail;
    private Integer duration;
}
```

### 2. 创建 VideoVO 类
创建视图对象用于返回视频信息：

```java
// backend/social-service/src/main/java/com/jiyi/social/dto/VideoVO.java
@Data
@Schema(description = "视频信息")
public class VideoVO {
    private String url;
    private String thumbnail;
    private Integer duration;
}
```

### 3. 更新 PostCreateRequest
添加 video 字段：

```java
@Schema(description = "视频信息")
private VideoDTO video;
```

### 4. 更新 PostVO
添加 video 字段：

```java
@Schema(description = "视频信息")
private VideoVO video;
```

### 5. 更新 PostService.createPost()
添加视频保存逻辑：

```java
// 保存视频到media_file表
if (request.getVideo() != null && request.getVideo().getUrl() != null) {
    MediaFile mediaFile = new MediaFile();
    mediaFile.setPostId(post.getId());
    mediaFile.setType("video");
    mediaFile.setUrl(request.getVideo().getUrl());
    mediaFile.setThumbnail(request.getVideo().getThumbnail());
    mediaFile.setDuration(request.getVideo().getDuration());
    mediaFile.setOrderNum(1);
    mediaFileMapper.insert(mediaFile);
}
```

### 6. 更新 PostService.convertToVO()
添加视频加载逻辑：

```java
// 分离图片和视频
List<String> images = new ArrayList<>();
for (MediaFile media : mediaFiles) {
    if ("image".equals(media.getType())) {
        images.add(media.getUrl());
    } else if ("video".equals(media.getType())) {
        // 设置视频信息
        VideoVO videoVO = new VideoVO();
        videoVO.setUrl(media.getUrl());
        videoVO.setThumbnail(media.getThumbnail());
        videoVO.setDuration(media.getDuration());
        vo.setVideo(videoVO);
    }
}
```

## 完整流程

### 视频上传流程：
1. 用户在前端选择视频文件
2. 前端调用 `POST /api/upload/video` 上传视频
3. 后端 FileUploadController 接收文件：
   - 验证文件类型（MP4、AVI、MOV、WMV）
   - 验证文件大小（最大 100MB）
   - 保存到 `uploads/videos/YYYY/MM/DD/` 目录
   - 返回视频 URL、缩略图 URL、时长等信息
4. 前端保存视频信息到 `newPost.value.video`

### 发布带视频的动态流程：
1. 用户点击发布按钮
2. 前端调用 `POST /api/posts`，包含：
   ```json
   {
     "content": "动态内容",
     "video": {
       "url": "http://localhost:8083/uploads/videos/2026/01/03/xxx.mp4",
       "thumbnail": "http://localhost:8083/uploads/thumbnails/2026/01/03/xxx.mp4.jpg",
       "duration": 120
     },
     "tags": ["标签1", "标签2"],
     "location": "地点",
     "category": "travel"
   }
   ```
3. 后端 PostService.createPost()：
   - 创建 post 记录
   - 保存视频到 media_file 表（type='video'）
   - 保存标签关联到 post_topic 表
4. 返回创建的动态信息

### 查询动态流程：
1. 前端调用 `GET /api/posts`
2. 后端 PostService.getPosts()：
   - 查询 post 表
   - 关联查询 media_file 表
   - 将 type='video' 的记录转换为 VideoVO
   - 将 type='image' 的记录添加到 images 数组
3. 返回包含视频信息的动态列表

## 前端显示
前端 Social.vue 已经有视频显示代码：

```vue
<!-- 视频显示 -->
<div v-if="post.video" class="post-video">
  <video 
    :src="post.video.url" 
    :poster="post.video.thumbnail"
    controls
    class="video-player"
  ></video>
</div>
```

## 测试

### 测试文件
创建了 `test_video_upload.html` 用于测试：
1. 上传视频文件
2. 发布带视频的动态
3. 查看上传和发布结果

### 测试步骤
1. 在浏览器打开 `test_video_upload.html`
2. 选择一个视频文件（小于 100MB）
3. 点击"上传视频"按钮
4. 等待上传完成，查看视频预览
5. 输入动态内容
6. 点击"发布动态"按钮
7. 查看发布结果

### 验证
1. 检查 `uploads/videos/` 目录是否有视频文件
2. 查询数据库 `media_file` 表是否有 type='video' 的记录
3. 在前端社区页面查看动态是否显示视频

## 数据库结构
media_file 表支持视频存储：
- `type`: 'video'
- `url`: 视频文件 URL
- `thumbnail`: 缩略图 URL
- `duration`: 视频时长（秒）
- `order_num`: 排序号

## 修改的文件
1. `backend/social-service/src/main/java/com/jiyi/social/dto/VideoDTO.java` - 新建
2. `backend/social-service/src/main/java/com/jiyi/social/dto/VideoVO.java` - 新建
3. `backend/social-service/src/main/java/com/jiyi/social/dto/PostCreateRequest.java` - 添加 video 字段
4. `backend/social-service/src/main/java/com/jiyi/social/dto/PostVO.java` - 添加 video 字段
5. `backend/social-service/src/main/java/com/jiyi/social/service/PostService.java` - 添加视频处理逻辑

## 当前状态
✅ 视频上传 API 正常工作
✅ 后端可以接收和保存视频数据
✅ 后端可以返回视频信息
✅ 前端可以显示视频
✅ 服务已重启并运行在 8083 端口

## 注意事项
1. 视频文件最大 100MB
2. 支持格式：MP4、AVI、MOV、WMV
3. 视频保存在 `uploads/videos/YYYY/MM/DD/` 目录
4. 缩略图功能暂时返回占位符，实际项目中应从视频提取帧
5. 视频时长暂时返回 0，实际项目中应解析视频获取真实时长
