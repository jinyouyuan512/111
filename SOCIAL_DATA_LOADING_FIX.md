# Social Data Loading Fix

## Issue
发布成功后，动态保存到数据库了，但前端页面没有显示。

## Root Cause
Axios 响应拦截器已经自动提取了 `response.data.data`，但前端代码仍然在检查 `response.data`，导致数据格式判断错误。

### Axios 拦截器处理流程
```typescript
// request.ts 中的响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data
    if (res && typeof res === 'object' && 'data' in res) {
      return res.data  // 自动提取 data 字段
    }
    return res
  }
)
```

### 后端返回格式
```json
{
  "code": 200,
  "data": [
    {
      "id": 11,
      "userId": 5,
      "userNickname": "用户5",
      "content": "测试内容",
      "location": "西柏坡纪念馆",
      "likesCount": 0,
      "commentsCount": 0,
      "sharesCount": 0,
      "createdAt": "2026-01-03T17:34:22"
    }
  ],
  "message": "success"
}
```

### 拦截器处理后
```javascript
// response 直接就是数组
response = [
  {
    "id": 11,
    "userId": 5,
    ...
  }
]
```

### 错误的代码
```typescript
// ❌ 错误：response 已经是数组，没有 data 字段
if (response.data && Array.isArray(response.data)) {
  posts.value = response.data.map(...)
}
```

### 正确的代码
```typescript
// ✅ 正确：直接检查 response 是否为数组
if (response && Array.isArray(response)) {
  posts.value = response.map(...)
}
```

## Solution

修改 `loadPosts()` 函数中的数据检查逻辑：

```typescript
const loadPosts = async () => {
  loading.value = true
  try {
    const response = await socialApi.getPosts({
      category: activeCategory.value === 'all' ? undefined : activeCategory.value,
      page: 1,
      size: 20
    })
    
    console.log('加载动态成功:', response)
    
    // 注意：axios 拦截器已经提取了 response.data.data
    // 所以 response 直接就是数组
    if (response && Array.isArray(response)) {
      posts.value = response.map((post: any) => ({
        id: post.id,
        userName: post.userNickname || '用户' + post.userId,
        userAvatar: post.userAvatar || '',
        isVip: false,
        createTime: formatTime(post.createdAt),
        location: post.location || '未知地点',
        title: post.title || '',
        content: post.content,
        images: post.images || [],
        video: null,
        tags: [],
        category: post.category || 'travel',
        likes: post.likesCount || 0,
        comments: post.commentsCount || 0,
        shares: post.sharesCount || 0,
        isLiked: post.liked || false
      }))
    } else {
      console.warn('后端返回的数据格式不正确:', response)
      posts.value = generatePosts()
    }
  } catch (error) {
    console.error('加载动态失败:', error)
    posts.value = generatePosts()
  } finally {
    loading.value = false
  }
}
```

## Database Verification

数据库中已经有发布的动态：

```sql
SELECT id, user_id, content, location_name, created_at 
FROM post 
WHERE deleted = 0 
ORDER BY created_at DESC 
LIMIT 5;
```

结果：
```
id  | user_id | content | location_name  | created_at
----|---------|---------|----------------|-------------------
11  | 5       | ...     | 西柏坡纪念馆    | 2026-01-03 17:34:22
10  | 5       | ...     |                | 2026-01-03 17:34:09
9   | 5       | ...     |                | 2026-01-03 17:25:47
8   | 5       | ...     |                | 2026-01-03 17:25:33
```

数据确实保存成功了！

## Testing

### 1. 清除浏览器缓存
```
Ctrl + Shift + Delete
或
F12 → Network → Disable cache
```

### 2. 刷新页面
```
F5 或 Ctrl + R
```

### 3. 查看控制台输出
应该看到：
```
加载动态成功: [Array(9)]
```

### 4. 发布新动态
1. 点击"发布动态"
2. 填写内容
3. 点击"发布"
4. 应该看到新动态出现在列表顶部

### 5. 验证数据
打开 Network 标签，查看：
- POST /api/posts → 返回 200
- GET /api/posts → 返回包含新动态的数组

## Expected Behavior

### Before Fix ❌
```
发布动态 → 保存到数据库 ✓
         → 调用 loadPosts() ✓
         → 检查 response.data (undefined) ✗
         → 使用模拟数据 ✗
         → 新动态不显示 ✗
```

### After Fix ✅
```
发布动态 → 保存到数据库 ✓
         → 调用 loadPosts() ✓
         → 检查 response (数组) ✓
         → 转换数据格式 ✓
         → 新动态显示在列表顶部 ✓
```

## Console Output

### 成功加载时
```javascript
加载动态成功: [
  {
    id: 11,
    userId: 5,
    userNickname: "用户5",
    content: "测试内容",
    location: "西柏坡纪念馆",
    likesCount: 0,
    commentsCount: 0,
    sharesCount: 0,
    liked: false,
    createdAt: "2026-01-03T17:34:22"
  },
  ...
]
```

### 数据格式错误时
```javascript
后端返回的数据格式不正确: {...}
```

## Files Modified
- `frontend/src/views/Social.vue`
  - 修改 `loadPosts()` 函数
  - 将 `response.data` 改为 `response`
  - 添加数据格式警告日志

## Related Issues
- Axios 拦截器自动提取 `data` 字段
- 前端代码没有考虑拦截器的处理
- 导致数据格式判断错误，使用了降级的模拟数据

## Prevention
在使用 axios 时，要注意：
1. 检查是否有响应拦截器
2. 了解拦截器如何处理响应数据
3. 根据实际返回的数据结构编写代码
4. 添加日志输出帮助调试
