# 🚨 创意上传 400 错误 - 快速修复

## 当前情况

- 前端运行在端口 **3002** (不是 3001)
- 提交作品时收到 400 Bad Request
- 需要查看具体错误信息

## 🔍 立即诊断

### 步骤 1: 打开错误检查工具

```bash
start check_creative_error.html
```

1. 确认端口号是 **3002**
2. 点击"测试最小请求"
3. 查看响应详情中的错误信息

### 步骤 2: 查看浏览器控制台

在创意上传页面 (http://localhost:3002/creative/upload):

1. 按 F12 打开开发者工具
2. 切换到 **Network** 标签
3. 提交表单
4. 点击 `designs` 请求
5. 查看 **Response** 标签的错误详情

### 步骤 3: 检查请求数据

在 Console 标签中应该看到：
```
提交数据: {title: "...", categoryType: 1, ...}
```

检查：
- `files` 是否是数组
- `categoryType` 是否是数字
- 是否有空字符串字段

## 🔧 可能的问题和解决方案

### 问题 1: 前端代码未更新

**症状**: 仍然发送空字符串字段

**解决**: 强制刷新浏览器
```
Ctrl + F5  (Windows)
Cmd + Shift + R  (Mac)
```

### 问题 2: 后端验证规则

**症状**: 后端拒绝某些字段值

**解决**: 查看后端返回的具体错误信息

可能的错误：
- "title 不能为空"
- "description 长度不足"
- "files 不能为空"
- "categoryType 无效"

### 问题 3: 数据类型错误

**症状**: 类型转换失败

**检查**:
```javascript
// 在浏览器 Console 中检查
console.log(typeof uploadForm.categoryType)  // 应该是 "number"
console.log(Array.isArray(uploadForm.files))  // 应该是 true
```

### 问题 4: 用户认证问题

**症状**: 401 或 403 错误

**检查**: 
- 用户是否已登录
- X-User-Id header 是否正确

## 📝 测试数据模板

### 最小有效请求

```json
{
  "title": "测试作品",
  "categoryType": 1,
  "description": "这是一个测试作品描述，至少需要10个字符。",
  "files": ["http://localhost:8083/uploads/test.jpg"]
}
```

### 完整请求

```json
{
  "title": "测试作品 - 红色文化海报",
  "categoryType": 1,
  "description": "这是一个完整的测试作品描述，展示红色文化主题的海报设计。",
  "designConcept": "设计理念：通过现代简约的设计风格传达红色文化精神。",
  "coverImage": "http://localhost:8083/uploads/cover.jpg",
  "files": [
    "http://localhost:8083/uploads/file1.jpg",
    "http://localhost:8083/uploads/file2.jpg"
  ],
  "copyrightStatement": "本作品版权归作者所有，仅供学习交流使用。",
  "tags": "红色文化,海报设计"
}
```

## 🎯 快速测试步骤

### 1. 使用测试工具

```bash
start check_creative_error.html
```

- 端口改为 3002
- 点击"测试最小请求"
- 如果成功 → 前端代码有问题
- 如果失败 → 查看错误信息

### 2. 检查后端日志

创意服务应该输出：
```
提交设计作品 - 用户ID: 1, 请求数据: DesignSubmitRequest(...)
```

如果有错误：
```
业务异常: xxx
参数校验失败: {field: "message"}
```

### 3. 验证数据库字段

```sql
-- 检查 design 表结构
DESC jiyi_creative.design;

-- 检查必填字段
SHOW COLUMNS FROM jiyi_creative.design WHERE `Null` = 'NO';
```

## 🚀 立即修复

### 如果是前端问题

1. **强制刷新浏览器** (Ctrl+F5)
2. **清除缓存**
3. **重启前端服务**:
   ```bash
   cd frontend
   npm run dev
   ```

### 如果是后端问题

1. **查看后端日志**
2. **检查数据库连接**
3. **重启创意服务**:
   ```bash
   cd backend/creative-service
   mvn spring-boot:run
   ```

## 📊 调试检查清单

在提交前检查：

- [ ] 用户已登录 (userStore.isLoggedIn === true)
- [ ] 标题已填写 (2-100字符)
- [ ] 描述已填写 (10-1000字符)
- [ ] 分类已选择 (1-4)
- [ ] 至少上传一个文件
- [ ] files 是数组: `Array.isArray(uploadForm.files)`
- [ ] categoryType 是数字: `typeof uploadForm.categoryType === 'number'`
- [ ] 浏览器已强制刷新 (Ctrl+F5)

## 🔍 获取详细错误信息

### 方法 1: 浏览器 Network 标签

1. F12 → Network
2. 提交表单
3. 点击 `designs` 请求
4. 查看 Response 标签
5. 复制完整错误信息

### 方法 2: 修改前端代码临时添加详细日志

在 `CreativeUpload.vue` 的 catch 块中：

```javascript
catch (error: any) {
  console.error('完整错误对象:', error)
  console.error('错误响应:', error.response)
  console.error('错误数据:', error.response?.data)
  console.error('错误消息:', error.response?.data?.message)
  console.error('错误详情:', error.response?.data?.data)
  
  ElMessage.error(error.response?.data?.message || '提交失败，请稍后重试')
}
```

## 📞 下一步

1. **运行** `check_creative_error.html`
2. **查看**具体错误信息
3. **根据**错误信息进行针对性修复
4. **如果**仍有问题，提供完整的错误响应内容

---

**创建时间**: 2026-01-04  
**优先级**: 🔴 高  
**状态**: 等待错误详情

