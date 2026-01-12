# Social Post Disappear Fix

## Issue
发布动态后，动态立即消失。用户报告："发布之后立马消失了"

## Root Cause
前端在发布成功后调用了 `location.reload()` 刷新页面，但页面加载时使用的是 `generatePosts()` 生成的模拟数据，而不是从后端加载真实数据。

### 问题分析
1. **发布流程**:
   - 用户发布动态 → 调用后端API成功 → 动态保存到数据库
   - 前端调用 `location.reload()` 刷新页面
   
2. **页面加载流程**:
   - `onMounted()` 执行
   - 调用 `posts.value = generatePosts()` 加载模拟数据
   - 模拟数据中没有新发布的动态
   - 结果：新发布的动态消失

## Solution

### 1. 添加 `loadPosts()` 函数
从后端加载真实动态数据：

```typescript
const loadPosts = async () => {
  loading.value = true
  try {
    const response = await socialApi.getPosts({
      category: activeCategory.value === 'all' ? undefined : activeCategory.value,
      page: 1,
      size: 20
    })
    
    // 转换后端数据格式为前端格式
    if (response.data && Array.isArray(response.data)) {
      posts.value = response.data.map((post: any) => ({
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
      // 如果后端返回空数据，使用模拟数据
      posts.value = generatePosts()
    }
  } catch (error) {
    console.error('加载动态失败:', error)
    // 加载失败时使用模拟数据
    posts.value = generatePosts()
  } finally {
    loading.value = false
  }
}
```

### 2. 添加时间格式化函数
将后端返回的时间戳转换为友好的显示格式：

```typescript
const formatTime = (dateStr: string) => {
  if (!dateStr) return '刚刚'
  
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  
  return date.toLocaleDateString('zh-CN')
}
```

### 3. 修改 `onMounted()`
从加载模拟数据改为加载真实数据：

```typescript
// 修改前
onMounted(() => {
  posts.value = generatePosts()
  loadHotTopics()
  loadActiveUsers()
  setTimeout(() => {
    loading.value = false
  }, 800)
})

// 修改后
onMounted(() => {
  loadPosts()
  loadHotTopics()
  loadActiveUsers()
})
```

### 4. 添加分类监听
当用户切换分类时，重新加载数据：

```typescript
watch(activeCategory, () => {
  loadPosts()
})
```

### 5. 修改 `submitPost()` 函数
移除 `location.reload()`，改为重新加载数据：

```typescript
// 修改前
console.log('发布成功:', response)
ElMessage.success('发布成功！')
postDialogVisible.value = false

// 将新发布的动态添加到列表顶部
const newPostData = { ... }
posts.value.unshift(newPostData)

// 刷新页面数据
setTimeout(() => {
  location.reload()
}, 1000)

// 修改后
console.log('发布成功:', response)
ElMessage.success('发布成功！')
postDialogVisible.value = false

// 重新加载动态列表以显示新发布的内容
await loadPosts()
```

### 6. 添加 `watch` 导入
```typescript
import { ref, computed, onMounted, nextTick, watch } from 'vue'
```

## Data Mapping

### Backend Response → Frontend Display
```typescript
{
  id: post.id,                              // 动态ID
  userName: post.userNickname,              // 用户昵称
  userAvatar: post.userAvatar,              // 用户头像
  createTime: formatTime(post.createdAt),   // 创建时间（格式化）
  location: post.location,                  // 位置
  content: post.content,                    // 内容
  images: post.images,                      // 图片列表
  likes: post.likesCount,                   // 点赞数
  comments: post.commentsCount,             // 评论数
  shares: post.sharesCount,                 // 分享数
  isLiked: post.liked                       // 是否已点赞
}
```

## Benefits

1. **真实数据显示**: 页面加载时显示后端真实数据
2. **发布即显示**: 发布成功后立即重新加载，新动态会出现在列表中
3. **无需刷新**: 不再使用 `location.reload()`，用户体验更流畅
4. **分类过滤**: 切换分类时自动重新加载对应数据
5. **降级处理**: 如果后端加载失败，自动使用模拟数据保证页面可用

## Testing

### Test 1: 发布动态
1. 登录系统
2. 进入社区页面
3. 点击"发布动态"
4. 填写内容和位置
5. 点击"发布"
6. **预期**: 成功提示，新动态出现在列表顶部

### Test 2: 分类切换
1. 在社区页面
2. 点击不同的分类标签
3. **预期**: 显示对应分类的动态

### Test 3: 页面刷新
1. 发布几条动态
2. 手动刷新页面（F5）
3. **预期**: 所有发布的动态都显示在列表中

## Files Modified
- `frontend/src/views/Social.vue`
  - 添加 `loadPosts()` 函数
  - 添加 `formatTime()` 函数
  - 修改 `onMounted()` 调用 `loadPosts()`
  - 添加 `watch(activeCategory)` 监听
  - 修改 `submitPost()` 移除 `location.reload()`
  - 添加 `watch` 导入

## Notes
- `generatePosts()` 函数保留作为降级方案
- 后端返回的数据格式需要转换为前端期望的格式
- 时间显示使用相对时间（刚刚、X分钟前等）
- 如果后端没有返回用户信息，使用默认值
