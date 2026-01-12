# 🔍 创意上传 400 错误诊断

## 问题描述

前端提交作品时收到 400 Bad Request 错误。

## 已修复

### 1. TypeScript 类型定义错误 ✅

**问题**: `frontend/src/api/creative.ts` 中 `files` 字段类型错误
```typescript
// 错误
files?: string

// 正确
files: string[]
```

**修复**: 已将 `files` 改为必填的数组类型

## 诊断步骤

### 1. 使用调试工具测试

打开 `debug_creative_request.html` 进行测试：

```bash
start debug_creative_request.html
```

这个工具可以：
- 自定义请求参数
- 查看请求数据格式
- 测试最小请求（仅必填字段）
- 查看详细的错误响应

### 2. 检查必填字段

根据后端 DTO，以下字段是必填的：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| title | String | ✅ | 作品标题 |
| description | String | ✅ | 作品描述 |
| files | List<String> | ✅ | 文件URL数组 |

可选字段：
- contestId
- callId
- categoryType
- designConcept
- coverImage
- copyrightStatement
- tags

### 3. 检查数据格式

**正确的请求格式：**
```json
{
  "title": "测试作品",
  "categoryType": 1,
  "description": "作品描述内容",
  "designConcept": "设计理念",
  "coverImage": "http://localhost:8083/uploads/cover.jpg",
  "files": [
    "http://localhost:8083/uploads/file1.jpg",
    "http://localhost:8083/uploads/file2.jpg"
  ],
  "copyrightStatement": "版权声明",
  "tags": "标签1,标签2"
}
```

**注意：**
- `files` 必须是数组，不能是字符串
- `categoryType` 必须是数字，不能是字符串
- 所有 URL 必须是完整的 HTTP URL

### 4. 检查用户认证

请求必须包含 `X-User-Id` header：

```javascript
headers: {
  'Content-Type': 'application/json',
  'X-User-Id': '1'  // 必须是有效的用户ID
}
```

### 5. 查看后端日志

创意服务会记录详细日志：

```
提交设计作品 - 用户ID: 1, 请求数据: DesignSubmitRequest(...)
文件列表JSON: ["http://...", "http://..."]
作品插入成功 - ID: 123
```

如果有错误，会显示：
```
业务异常: 参数错误
参数校验失败: {field: "message"}
```

## 可能的错误原因

### 1. 字段类型错误
- `categoryType` 发送了字符串而不是数字
- `files` 发送了字符串而不是数组

### 2. 必填字段缺失
- `title` 为空或未提供
- `description` 为空或未提供
- `files` 为空数组或未提供

### 3. 数据格式错误
- JSON 格式不正确
- 字段名拼写错误
- 嵌套结构错误

### 4. 用户认证问题
- `X-User-Id` header 缺失
- 用户ID 无效
- 用户未登录

## 测试步骤

### 步骤 1: 测试最小请求

使用 `debug_creative_request.html`，点击"发送最小请求"按钮：

```json
{
  "title": "测试作品",
  "categoryType": 1,
  "description": "测试描述",
  "files": ["http://localhost:8083/uploads/test.jpg"]
}
```

如果成功，说明基本功能正常。

### 步骤 2: 逐步添加字段

依次添加可选字段，找出导致错误的字段：

1. 添加 `designConcept`
2. 添加 `coverImage`
3. 添加 `copyrightStatement`
4. 添加 `tags`

### 步骤 3: 检查实际上传

在浏览器中：
1. 打开 http://localhost:3001/creative/upload
2. 打开开发者工具 (F12)
3. 切换到 Network 标签
4. 填写表单并提交
5. 查看请求详情：
   - Request Headers
   - Request Payload
   - Response

### 步骤 4: 验证文件上传

确保文件已成功上传：

1. 上传封面图片
2. 检查返回的 URL
3. 在浏览器中访问 URL，确认文件可访问
4. 上传作品文件
5. 检查返回的 URL 数组

## 快速修复

### 修复 1: 确保 files 是数组

在 `CreativeUpload.vue` 中：

```javascript
// 确保 files 是数组
const designData = {
  title: uploadForm.title,
  categoryType: uploadForm.categoryType,
  description: uploadForm.description,
  designConcept: uploadForm.designConcept,
  coverImage: uploadForm.coverImage,
  files: uploadForm.files,  // 已经是数组
  copyrightStatement: uploadForm.copyrightStatement,
  tags: uploadForm.tags
}
```

### 修复 2: 验证数据类型

```javascript
// 提交前验证
console.log('提交数据:', {
  ...designData,
  filesType: Array.isArray(designData.files),
  filesLength: designData.files.length,
  categoryTypeType: typeof designData.categoryType
})
```

### 修复 3: 处理空值

```javascript
// 移除空值字段
const designData = {
  title: uploadForm.title,
  categoryType: uploadForm.categoryType,
  description: uploadForm.description,
  files: uploadForm.files
}

// 只添加非空的可选字段
if (uploadForm.designConcept) {
  designData.designConcept = uploadForm.designConcept
}
if (uploadForm.coverImage) {
  designData.coverImage = uploadForm.coverImage
}
if (uploadForm.copyrightStatement) {
  designData.copyrightStatement = uploadForm.copyrightStatement
}
if (uploadForm.tags) {
  designData.tags = uploadForm.tags
}
```

## 验证修复

### 1. 使用测试页面

```bash
start debug_creative_request.html
```

点击"发送测试请求"，应该看到：
```
HTTP 状态码: 200
✓ 请求成功
```

### 2. 使用实际页面

1. 访问 http://localhost:3001/creative/upload
2. 填写所有必填字段
3. 上传文件
4. 点击提交
5. 应该看到"作品上传成功！"

### 3. 检查数据库

```sql
SELECT * FROM jiyi_creative.design 
ORDER BY id DESC 
LIMIT 1;
```

应该看到新插入的记录。

## 总结

主要问题是 TypeScript 类型定义错误，`files` 字段应该是 `string[]` 而不是 `string`。

已修复：
- ✅ TypeScript 接口定义
- ✅ 创建调试工具
- ✅ 提供详细诊断步骤

下一步：
1. 使用 `debug_creative_request.html` 测试
2. 如果仍有问题，查看具体错误信息
3. 根据错误信息进一步调试

---

**创建时间**: 2026-01-04
**状态**: 类型定义已修复，等待测试验证
