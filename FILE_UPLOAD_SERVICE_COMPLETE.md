# 完整文件上传服务实现

## 功能概述
实现了完整的本地文件上传服务，支持图片和视频上传，文件保存到服务器本地目录，并返回可访问的URL。

## 架构设计

### 后端架构
```
backend/social-service/
├── controller/
│   └── FileUploadController.java    # 文件上传API
├── config/
│   └── WebMvcConfig.java            # 静态资源配置
└── uploads/                          # 文件存储目录
    ├── images/                       # 图片目录
    │   └── 2026/01/03/              # 按日期分类
    └── videos/                       # 视频目录
        └── 2026/01/03/
```

### 前端架构
```
frontend/src/
├── api/
│   └── social.ts                    # 上传API封装
└── views/
    └── Social.vue                   # 上传UI和逻辑
```

## 核心功能

### 1. 文件上传API

#### 单张图片上传
```
POST /api/upload/image
Content-Type: multipart/form-data
参数: file (图片文件)

响应:
{
  "code": 200,
  "data": {
    "url": "http://localhost:8083/uploads/images/2026/01/03/xxx.jpg",
    "filename": "photo.jpg",
    "size": "1024000"
  }
}
```

#### 批量图片上传
```
POST /api/upload/images
Content-Type: multipart/form-data
参数: files[] (多个图片文件)

响应:
{
  "code": 200,
  "data": [
    {
      "url": "http://localhost:8083/uploads/images/2026/01/03/xxx.jpg",
      "filename": "photo1.jpg",
      "size": "1024000"
    },
    ...
  ]
}
```

#### 视频上传
```
POST /api/upload/video
Content-Type: multipart/form-data
参数: file (视频文件)

响应:
{
  "code": 200,
  "data": {
    "url": "http://localhost:8083/uploads/videos/2026/01/03/xxx.mp4",
    "thumbnail": "http://localhost:8083/uploads/thumbnails/2026/01/03/xxx.mp4.jpg",
    "filename": "video.mp4",
    "size": 10240000,
    "duration": 120
  }
}
```

### 2. 文件限制

#### 图片限制
- 最大大小: 10MB
- 支持格式: JPG, PNG, GIF, WEBP
- 最多上传: 9张

#### 视频限制
- 最大大小: 100MB
- 支持格式: MP4, AVI, MOV, WMV
- 最多上传: 1个

### 3. 文件存储

#### 目录结构
```
uploads/
├── images/
│   └── 2026/
│       └── 01/
│           └── 03/
│               ├── uuid1.jpg
│               ├── uuid2.png
│               └── ...
└── videos/
    └── 2026/
        └── 01/
            └── 03/
                ├── uuid1.mp4
                └── ...
```

#### 文件命名
- 使用UUID生成唯一文件名
- 保留原始文件扩展名
- 按日期分类存储（年/月/日）

### 4. 静态资源访问

配置了静态资源映射，使上传的文件可以通过HTTP访问：
```
http://localhost:8083/uploads/images/2026/01/03/xxx.jpg
http://localhost:8083/uploads/videos/2026/01/03/xxx.mp4
```

## 实现细节

### 后端实现

#### FileUploadController.java
```java
@RestController
@RequestMapping("/api/upload")
public class FileUploadController {
    
    @Value("${file.upload.path:uploads}")
    private String uploadPath;
    
    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        // 1. 验证文件类型和大小
        // 2. 生成唯一文件名
        // 3. 创建日期目录
        // 4. 保存文件
        // 5. 返回访问URL
    }
    
    @PostMapping("/images")
    public Result<List<Map<String, String>>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        // 批量上传逻辑
    }
    
    @PostMapping("/video")
    public Result<Map<String, Object>> uploadVideo(@RequestParam("file") MultipartFile file) {
        // 视频上传逻辑
    }
}
```

#### WebMvcConfig.java
```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Value("${file.upload.path:uploads}")
    private String uploadPath;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = new File(uploadPath).getAbsolutePath() + File.separator;
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absolutePath);
    }
}
```

#### application.yml
```yaml
spring:
  servlet:
    multipart:
      enabled: true
      max-file-size: 100MB
      max-request-size: 100MB

file:
  upload:
    path: uploads
```

### 前端实现

#### social.ts API封装
```typescript
// 上传图片
export const uploadImage = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/upload/image',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 批量上传图片
export const uploadImages = (files: File[]) => {
  const formData = new FormData()
  files.forEach(file => {
    formData.append('files', file)
  })
  return request({
    url: '/upload/images',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 上传视频
export const uploadVideo = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/upload/video',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
```

#### Social.vue 上传逻辑
```typescript
// 图片上传
const handleImageUpload = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const files = target.files
  if (!files || files.length === 0) return
  
  const remainingSlots = 9 - newPost.value.images.length
  const filesToProcess = Array.from(files).slice(0, remainingSlots)
  
  ElMessage.info(`正在上传 ${filesToProcess.length} 张图片...`)
  
  try {
    const response = await socialApi.uploadImages(filesToProcess)
    
    if (response && Array.isArray(response)) {
      response.forEach((item: any) => {
        if (item.url) {
          newPost.value.images.push(item.url)
        }
      })
      
      ElMessage.success(`成功上传 ${response.length} 张图片`)
    }
  } catch (error) {
    console.error('图片上传失败:', error)
    ElMessage.error('图片上传失败，请重试')
  }
  
  target.value = ''
}

// 视频上传
const handleVideoUpload = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return
  
  if (!file.type.startsWith('video/')) {
    ElMessage.error('请选择视频文件')
    return
  }
  
  const maxSize = 100 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.error('视频文件不能超过100MB')
    return
  }
  
  ElMessage.info('正在上传视频，请稍候...')
  
  try {
    const response = await socialApi.uploadVideo(file)
    
    if (response && response.url) {
      newPost.value.video = {
        url: response.url,
        thumbnail: response.thumbnail || response.url,
        duration: response.duration || 0
      }
      
      ElMessage.success('视频上传成功')
    }
  } catch (error) {
    console.error('视频上传失败:', error)
    ElMessage.error('视频上传失败，请重试')
  }
  
  target.value = ''
}
```

## 使用流程

### 1. 上传图片
1. 点击发布框的"图片"按钮
2. 选择1-9张图片文件
3. 系统自动上传到服务器
4. 显示上传进度和结果
5. 图片URL保存到newPost.images数组
6. 发布时将URL保存到数据库

### 2. 上传视频
1. 点击发布框的"视频"按钮
2. 选择1个视频文件（最大100MB）
3. 系统自动上传到服务器
4. 显示上传进度
5. 视频URL和缩略图保存到newPost.video对象
6. 发布时将URL保存到数据库

### 3. 发布动态
1. 输入内容
2. 上传图片或视频
3. 添加话题标签（可选）
4. 选择位置（可选）
5. 点击发布
6. 系统将内容和媒体URL保存到数据库

## 测试步骤

### 1. 测试图片上传
```bash
# 访问前端
http://localhost:3002

# 操作步骤
1. 点击发布框
2. 点击"图片"按钮
3. 选择图片文件（JPG/PNG/GIF）
4. 等待上传完成
5. 查看图片预览
6. 输入内容后发布
7. 刷新页面查看动态
```

### 2. 测试视频上传
```bash
# 操作步骤
1. 点击发布框
2. 点击"视频"按钮
3. 选择视频文件（MP4/AVI/MOV）
4. 等待上传完成
5. 查看视频预览
6. 输入内容后发布
7. 刷新页面查看动态
```

### 3. 测试API
```bash
# 使用curl测试图片上传
curl -X POST http://localhost:8083/api/upload/image \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@/path/to/image.jpg"

# 使用curl测试视频上传
curl -X POST http://localhost:8083/api/upload/video \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@/path/to/video.mp4"
```

## 优化建议

### 当前实现（本地存储）
✅ 简单易用
✅ 无需额外服务
✅ 适合开发和演示
❌ 不适合生产环境
❌ 无法水平扩展
❌ 占用服务器磁盘空间

### 生产环境建议
1. **使用对象存储服务（OSS）**
   - 阿里云OSS
   - 腾讯云COS
   - 七牛云
   - AWS S3

2. **添加CDN加速**
   - 提高访问速度
   - 减轻服务器压力

3. **图片处理**
   - 自动压缩
   - 生成多种尺寸
   - 格式转换（WebP）

4. **视频处理**
   - 转码为多种格式
   - 生成缩略图
   - 提取视频时长

5. **安全增强**
   - 文件类型验证
   - 病毒扫描
   - 防盗链
   - 访问权限控制

## 服务状态
- ✅ social-service: 运行在端口8083
- ✅ frontend: 运行在端口3002
- ✅ 文件上传服务: 已启用
- ✅ 静态资源访问: 已配置

## 注意事项
1. 确保uploads目录有写入权限
2. 定期清理过期文件
3. 监控磁盘空间使用
4. 备份重要文件
5. 生产环境建议使用OSS

## 故障排查

### 上传失败
1. 检查文件大小是否超限
2. 检查文件格式是否支持
3. 检查uploads目录权限
4. 查看后端日志

### 文件无法访问
1. 检查静态资源配置
2. 检查文件路径是否正确
3. 检查文件是否存在
4. 检查防火墙设置

### 上传速度慢
1. 检查网络连接
2. 压缩文件大小
3. 考虑使用CDN
4. 优化服务器配置
