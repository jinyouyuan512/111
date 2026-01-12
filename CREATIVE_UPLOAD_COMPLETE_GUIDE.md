# 🎨 创意作品上传功能 - 完整使用指南

## ✅ 功能状态：已完成并可用

所有服务正在运行，创意作品上传功能已完全实现。

## 🚀 快速开始

### 1. 确认服务状态

所有必需的服务都在运行：

```bash
# 检查服务端口
netstat -ano | findstr :3001   # 前端服务
netstat -ano | findstr :8087   # 创意服务
netstat -ano | findstr :8083   # 社交服务（文件上传）
```

**当前状态：**
- ✅ 前端服务：http://localhost:3001
- ✅ 创意服务：http://localhost:8087
- ✅ 社交服务：http://localhost:8083

### 2. 访问上传页面

打开浏览器访问：
```
http://localhost:3001/creative/upload
```

**注意：** 需要先登录才能上传作品。如果未登录，会自动跳转到登录页面。

## 📝 使用步骤

### 步骤 1: 填写基本信息

1. **作品标题**（必填）
   - 长度：2-100 个字符
   - 示例：西柏坡红色文化海报设计

2. **作品分类**（必填）
   - 海报设计
   - Logo设计
   - 文创产品
   - 视频动画

3. **作品描述**（必填）
   - 长度：10-1000 个字符
   - 示例：这是一幅以西柏坡为主题的红色文化海报设计，融合了革命历史元素和现代设计理念。

### 步骤 2: 填写设计理念（可选）

- 长度：最多 1000 个字符
- 示例：通过简洁的视觉语言，传达西柏坡精神的深刻内涵。使用红色作为主色调，象征革命精神。

### 步骤 3: 上传作品文件

#### 上传封面图片（必填）
- **支持格式**：JPG、PNG、GIF、WEBP
- **最大大小**：10MB
- **建议尺寸**：800x600 像素
- **操作方式**：点击上传区域或拖拽文件

#### 上传作品文件（必填）
- **支持格式**：图片、视频、PDF、PSD、AI、Sketch
- **最大大小**：单个文件 100MB
- **最多数量**：5 个文件
- **操作方式**：点击按钮或拖拽文件

### 步骤 4: 填写版权信息（可选）

1. **版权声明**
   - 长度：最多 500 个字符
   - 示例：本作品版权归作者所有，仅供学习交流使用

2. **作品标签**
   - 多个标签用逗号分隔
   - 示例：西柏坡,红色文化,海报设计

### 步骤 5: 提交作品

1. 点击"提交作品"按钮
2. 等待上传完成
3. 看到"作品上传成功！"提示
4. 自动跳转到创意空间页面

## 🧪 测试功能

### 使用测试页面

打开 `test_creative_submit.html` 进行快速测试：

```bash
# 在浏览器中打开
start test_creative_submit.html
```

这个测试页面会：
- 显示测试数据
- 发送 POST 请求到创意服务
- 显示响应结果

### 使用实际页面测试

1. 访问 http://localhost:3001/login
2. 登录账号（用户名：test1，密码：123456）
3. 访问 http://localhost:3001/creative/upload
4. 按照上述步骤上传作品

## 🔧 技术实现

### 前端实现

**文件：** `frontend/src/views/CreativeUpload.vue`

**关键功能：**
- 使用 `el-upload` 组件实现文件上传
- 表单验证使用 Element Plus 的验证规则
- 文件上传到社交服务 (8083)
- 作品数据提交到创意服务 (8087)

**上传配置：**
```javascript
const uploadUrl = 'http://localhost:8083/api/upload'
const uploadHeaders = {
  'X-User-Id': userStore.userId?.toString() || ''
}
```

### 后端实现

**文件：** `backend/creative-service/src/main/java/com/jiyi/creative/`

**关键组件：**

1. **Controller** - `controller/CreativeController.java`
   ```java
   @PostMapping("/designs")
   public Result<DesignVO> submitDesign(
       @RequestHeader("X-User-Id") Long userId,
       @RequestBody DesignSubmitRequest request)
   ```

2. **Service** - `service/impl/CreativeServiceImpl.java`
   - 接收请求数据
   - 转换为实体对象
   - 保存到数据库
   - 返回结果

3. **DTO** - `dto/DesignSubmitRequest.java`
   ```java
   @Data
   public class DesignSubmitRequest {
       private String title;
       private Integer categoryType;
       private String description;
       private String designConcept;
       private List<String> files;
       private String coverImage;
       private String copyrightStatement;
       private String tags;
   }
   ```

4. **Entity** - `entity/Design.java`
   - 使用 `@TableField(insertStrategy = FieldStrategy.IGNORED)` 处理可空字段
   - 文件列表存储为 JSON 字符串

### 数据库结构

**表名：** `jiyi_creative.design`

**关键字段：**
```sql
CREATE TABLE design (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    designer_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    category_type INT,
    description TEXT NOT NULL,
    design_concept TEXT,
    files TEXT NOT NULL,
    cover_image VARCHAR(500),
    copyright_statement VARCHAR(500),
    tags VARCHAR(500),
    status VARCHAR(20) DEFAULT 'pending',
    votes INT DEFAULT 0,
    views INT DEFAULT 0,
    created_at DATETIME,
    updated_at DATETIME,
    deleted INT DEFAULT 0
);
```

## 🐛 常见问题

### 问题 1: 提交时显示 400 错误

**可能原因：**
- 必填字段未填写
- 文件未上传
- 用户未登录

**解决方法：**
1. 检查所有必填字段是否已填写
2. 确保封面图片和作品文件都已上传
3. 确认已登录（检查浏览器控制台的 X-User-Id）

### 问题 2: 文件上传失败

**可能原因：**
- 文件大小超限
- 文件格式不支持
- 社交服务未运行

**解决方法：**
1. 检查文件大小（封面<10MB，作品<100MB）
2. 确认文件格式在支持列表中
3. 确认社交服务运行在 8083 端口

### 问题 3: 提交后无响应

**可能原因：**
- 创意服务未运行
- 代理配置错误
- 网络连接问题

**解决方法：**
1. 检查创意服务是否运行：`netstat -ano | findstr :8087`
2. 检查 Vite 代理配置：`frontend/vite.config.ts`
3. 查看浏览器控制台和网络请求

### 问题 4: 数据库插入失败

**可能原因：**
- 数据库连接问题
- 字段约束冲突
- NULL 值处理错误

**解决方法：**
1. 检查数据库连接配置
2. 确认所有字段都有 `@TableField(insertStrategy = FieldStrategy.IGNORED)` 注解
3. 查看后端日志获取详细错误信息

## 📊 数据流程

```
用户操作
    ↓
前端表单验证
    ↓
上传封面图片 → 社交服务(8083) → 返回 URL
    ↓
上传作品文件 → 社交服务(8083) → 返回 URL 数组
    ↓
提交表单数据
    ↓
Vite 代理 → 创意服务(8087)
    ↓
Controller 接收请求
    ↓
Service 处理业务逻辑
    ↓
MyBatis-Plus 插入数据库
    ↓
返回成功响应
    ↓
前端显示成功提示并跳转
```

## 🔍 调试技巧

### 1. 查看前端日志

打开浏览器开发者工具（F12）：
- **Console** 标签：查看 JavaScript 错误和日志
- **Network** 标签：查看 HTTP 请求和响应

### 2. 查看后端日志

创意服务日志会显示：
```
提交设计作品 - 用户ID: 1, 请求数据: DesignSubmitRequest(...)
文件列表JSON: ["http://...", "http://..."]
作品插入成功 - ID: 123
```

### 3. 查看数据库

```sql
-- 查看最新的作品
SELECT * FROM jiyi_creative.design 
ORDER BY id DESC 
LIMIT 1;

-- 查看作品详情
SELECT 
    id,
    title,
    category_type,
    description,
    files,
    cover_image,
    status,
    created_at
FROM jiyi_creative.design
WHERE designer_id = 1;
```

## 📚 相关文档

1. [创意文件上传更新](CREATIVE_FILE_UPLOAD_UPDATE.md) - 从 URL 改为文件上传
2. [创意上传错误修复](CREATIVE_UPLOAD_ERROR_FIX.md) - 表单验证错误处理
3. [创意代理配置修复](CREATIVE_PROXY_FIX.md) - Vite 代理配置
4. [创意字段缺失修复](CREATIVE_FIELDS_FIX.md) - 添加 categoryType 和 tags
5. [创意上传最终修复](CREATIVE_UPLOAD_FINAL_FIX.md) - NULL 值处理

## ✅ 功能清单

| 功能 | 状态 | 说明 |
|------|------|------|
| 表单验证 | ✅ | 必填字段验证 |
| 封面上传 | ✅ | 支持图片格式 |
| 文件上传 | ✅ | 支持多种格式 |
| 多文件上传 | ✅ | 最多 5 个文件 |
| 文件预览 | ✅ | 封面图片预览 |
| 文件列表 | ✅ | 显示已上传文件 |
| 文件删除 | ✅ | 移除已上传文件 |
| 数据提交 | ✅ | 保存到数据库 |
| 错误处理 | ✅ | 友好的错误提示 |
| 成功提示 | ✅ | 上传成功提示 |
| 自动跳转 | ✅ | 跳转到创意空间 |

## 🎉 总结

创意作品上传功能已完全实现并可用。用户可以：

1. ✅ 填写作品信息
2. ✅ 上传封面图片
3. ✅ 上传作品文件（支持多个）
4. ✅ 填写版权信息
5. ✅ 提交作品到数据库
6. ✅ 查看上传成功提示
7. ✅ 自动跳转到创意空间

所有服务正常运行，功能测试通过，可以正常使用！

---

**文档创建时间**: 2026-01-04
**功能状态**: ✅ 完全可用
**测试状态**: ✅ 已通过

