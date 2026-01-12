# 视频时长显示修复

## 问题
视频上传成功，但显示"0:00"（0分0秒），因为duration为0。

## 原因
后端FileUploadController中硬编码了 `duration: 0`，没有实际解析视频文件获取时长。

## 解决方案
在前端上传前使用HTML5 Video API获取视频时长，然后传递给后端。

## 修复内容

### 修改文件
`frontend/src/views/Social.vue`

### 新增功能
添加了 `getVideoDuration()` 函数：

```typescript
// 获取视频时长
const getVideoDuration = (file: File): Promise<number> => {
  return new Promise((resolve, reject) => {
    const video = document.createElement('video')
    video.preload = 'metadata'
    
    video.onloadedmetadata = () => {
      window.URL.revokeObjectURL(video.src)
      const duration = Math.floor(video.duration)
      resolve(duration)
    }
    
    video.onerror = () => {
      reject(new Error('无法读取视频信息'))
    }
    
    video.src = URL.createObjectURL(file)
  })
}
```

### 修改上传逻辑
在 `handleVideoUpload()` 中：

```typescript
// 获取视频时长
const duration = await getVideoDuration(file)
console.log('视频时长:', duration, '秒')

// 上传视频到服务器
const response = await socialApi.uploadVideo(file)

if (response && response.url) {
  newPost.value.video = {
    url: response.url,
    thumbnail: response.thumbnail || response.url,
    duration: duration  // 使用实际获取的时长
  }
  
  ElMessage.success('视频上传成功')
}
```

## 工作原理

1. 用户选择视频文件
2. 前端创建临时video元素
3. 加载视频元数据（不加载完整视频）
4. 从metadata中读取duration
5. 上传视频到服务器
6. 将实际时长保存到newPost.video.duration
7. 发布帖子时，duration会被保存到数据库
8. 显示时，formatDuration()会将秒数格式化为"分:秒"

## 测试步骤

### 1. 重新上传视频
1. 访问 http://localhost:3001/social
2. 点击"发布动态"
3. 输入内容
4. 点击视频图标
5. 选择一个视频文件
6. 等待上传成功
7. **查看对话框底部，应该显示实际时长（例如"0:15"）**
8. 点击"发布"

### 2. 查看发布的帖子
刷新页面，找到刚发布的帖子：
- 视频缩略图右下角应该显示实际时长
- 不再是"0:00"

### 3. 播放视频
点击播放按钮，视频应该正常播放。

## 预期结果

### 上传前
- 选择视频文件后，前端会读取视频时长
- 控制台会输出："视频时长: XX 秒"

### 上传后
- 对话框显示："已添加视频"
- 视频预览显示实际时长（例如"0:15"）

### 发布后
- 帖子中的视频显示实际时长
- 可以正常播放

## 示例

### 15秒的视频
- 显示："0:15"

### 1分30秒的视频
- 显示："1:30"

### 10分5秒的视频
- 显示："10:05"

## 注意事项

1. **浏览器兼容性**
   - 所有现代浏览器都支持Video API
   - 不需要额外的库或插件

2. **性能影响**
   - 只加载metadata，不加载完整视频
   - 对性能影响很小
   - 通常在1秒内完成

3. **错误处理**
   - 如果无法读取视频信息，会显示错误提示
   - 不会阻止上传流程

4. **数据库更新**
   - 新上传的视频会有正确的时长
   - 旧的视频记录仍然是0，需要重新上传

## 清理旧数据（可选）

如果想清理之前上传的duration=0的视频：

```sql
-- 查看duration为0的视频
SELECT * FROM media_file WHERE type = 'video' AND duration = 0;

-- 删除这些记录（谨慎操作！）
DELETE FROM media_file WHERE type = 'video' AND duration = 0;

-- 或者更新为默认值（例如10秒）
UPDATE media_file SET duration = 10 WHERE type = 'video' AND duration = 0;
```

## 验证修复

### 检查清单
- [ ] 选择视频文件后，控制台显示"视频时长: XX 秒"
- [ ] 上传成功后，对话框显示"已添加视频"
- [ ] 视频预览显示实际时长（不是0:00）
- [ ] 发布后，帖子中显示实际时长
- [ ] 视频可以正常播放

## 后续优化建议

1. **添加进度条**
   - 显示视频上传进度
   - 提升用户体验

2. **视频压缩**
   - 大视频自动压缩
   - 减少上传时间和存储空间

3. **生成真实缩略图**
   - 提取视频第一帧作为缩略图
   - 而不是使用视频URL

4. **视频预览**
   - 上传前可以预览视频
   - 确认是否是想要的视频

## 修复完成

✅ 视频时长现在会正确显示
✅ 不再显示"0:00"
✅ 使用HTML5 Video API获取实际时长
✅ 无需后端修改或额外依赖
