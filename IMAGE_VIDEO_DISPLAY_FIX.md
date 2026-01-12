# 图片视频显示问题修复

## 问题描述
用户反馈发布的动态"只有文字"，图片和视频没有显示。

## 问题分析

### 1. 后端API检查
通过查询 `GET /api/posts` 接口，发现后端返回的数据是正确的：
- ID 18 的动态包含图片URL
- ID 22 的动态包含视频URL

```json
{
  "id": 18,
  "content": "4她FWEDCVS ",
  "images": ["http://localhost:8083/uploads/images/2026/01/03/ae797b07-1088-4dc6-8b4e-c7dcbad4aecf.png"]
}
```

### 2. 文件存储检查
检查发现 **uploads 目录不存在**！
- 图片URL保存在数据库中
- 但实际文件没有保存到磁盘
- FileUploadController 尝试保存文件时，目录不存在导致失败

### 3. 根本原因
1. **uploads 目录未创建** - social-service 启动时没有自动创建上传目录
2. **文件上传失败但没有报错** - 上传接口返回了URL，但文件实际没有保存成功
3. **前端显示正常** - 前端代码没有问题，只是图片URL指向不存在的文件

## 解决方案

### 1. 创建 uploads 目录结构
```bash
backend/social-service/
  └── uploads/
      ├── images/
      ├── videos/
      └── thumbnails/
```

已执行：
```powershell
New-Item -ItemType Directory -Path "backend/social-service/uploads/images" -Force
New-Item -ItemType Directory -Path "backend/social-service/uploads/videos" -Force
New-Item -ItemType Directory -Path "backend/social-service/uploads/thumbnails" -Force
```

### 2. 改进 FileUploadController
需要在保存文件前确保目录存在（代码中已有 `Files.createDirectories(dirPath)`，但可能权限问题）

### 3. 配置静态资源访问
WebMvcConfig 已配置：
```java
registry.addResourceHandler("/uploads/**")
        .addResourceLocations("file:uploads/");
```

## 当前状态

### ✅ 已完成
1. 创建了 uploads 目录结构
2. 后端API正确返回图片/视频信息
3. 前端代码正确处理显示逻辑
4. 视频上传功能已修复（VideoDTO、VideoVO）

### ⚠️ 需要注意
1. **之前上传的文件已丢失** - 数据库中有URL记录，但文件不存在
2. **需要重新上传** - 用户需要重新上传图片和视频
3. **浏览器缓存** - 可能需要硬刷新（Ctrl+Shift+R）

## 测试步骤

### 1. 测试图片上传
1. 打开 `test_image_upload_simple.html`
2. 选择一张图片
3. 点击"上传图片"
4. 查看是否显示图片预览
5. 检查 `backend/social-service/uploads/images/YYYY/MM/DD/` 目录是否有文件

### 2. 测试视频上传
1. 打开 `test_video_upload.html`
2. 选择一个视频文件（<100MB）
3. 点击"上传视频"
4. 查看视频预览
5. 检查 `backend/social-service/uploads/videos/YYYY/MM/DD/` 目录是否有文件

### 3. 测试发布动态
1. 在前端社区页面点击发布
2. 上传图片或视频
3. 输入内容并发布
4. 刷新页面查看动态是否显示图片/视频

### 4. 验证文件访问
访问上传的文件URL，例如：
```
http://localhost:8083/uploads/images/2026/01/03/xxx.png
http://localhost:8083/uploads/videos/2026/01/03/xxx.mp4
```

## 数据库清理（可选）

如果想清理数据库中指向不存在文件的记录：

```sql
-- 查看有图片/视频的动态
SELECT p.id, p.content, m.type, m.url 
FROM post p 
LEFT JOIN media_file m ON p.id = m.post_id 
WHERE m.id IS NOT NULL;

-- 删除不存在的媒体文件记录（谨慎操作）
-- DELETE FROM media_file WHERE post_id IN (18, 22);
```

## 前端显示逻辑

前端 Social.vue 已正确实现：

```vue
<!-- 图片显示 -->
<div v-if="post.images && post.images.length > 0" class="post-images">
  <div v-for="(img, index) in post.images" :key="index" class="post-image"
       :style="{ backgroundImage: `url(${img})` }">
  </div>
</div>

<!-- 视频显示 -->
<div v-if="post.video" class="post-video">
  <video :src="post.video.url" :poster="post.video.thumbnail" controls></video>
</div>
```

## 为什么之前看起来"只有文字"

1. 后端返回了图片/视频URL
2. 前端尝试加载这些URL
3. 但文件不存在（404），所以图片/视频无法显示
4. 只剩下文字内容可见

## 下一步

1. ✅ uploads 目录已创建
2. 🔄 需要重新测试上传功能
3. 🔄 用户需要重新上传图片和视频
4. 🔄 验证新上传的文件能正确显示

## 预防措施

### 1. 启动时自动创建目录
可以在 Application 启动类中添加：

```java
@PostConstruct
public void init() {
    try {
        Files.createDirectories(Paths.get("uploads/images"));
        Files.createDirectories(Paths.get("uploads/videos"));
        Files.createDirectories(Paths.get("uploads/thumbnails"));
    } catch (IOException e) {
        log.error("创建上传目录失败", e);
    }
}
```

### 2. 文件上传错误处理
FileUploadController 已有异常处理，但可以添加更详细的日志。

### 3. 定期备份
建议定期备份 uploads 目录。

## 测试文件
- `test_image_upload_simple.html` - 简单图片上传测试
- `test_video_upload.html` - 视频上传和发布测试
- `test_post_with_tags.html` - 带标签的动态发布测试
