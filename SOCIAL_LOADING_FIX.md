# Social Loading Variable Duplicate Declaration Fix

## Issue
编译错误：`Identifier 'loading' has already been declared`

## Root Cause
`loading` 变量被声明了两次：
1. 第 449 行：`const loading = ref(true)` - 原有声明
2. 第 500 行：`const loading = ref(false)` - 新增时重复声明

## Solution
删除第 500 行的重复声明，保留第 449 行的原有声明。

### Before
```typescript
// 状态
const loading = ref(true)  // 第 449 行
...
// 模拟数据
const hotTopics = ref<any[]>([])
const activeUsers = ref<any[]>([])
const posts = ref<any[]>([])
const loading = ref(false)  // 第 500 行 - 重复声明 ❌
```

### After
```typescript
// 状态
const loading = ref(true)  // 第 449 行 - 保留 ✓
...
// 模拟数据
const hotTopics = ref<any[]>([])
const activeUsers = ref<any[]>([])
const posts = ref<any[]>([])
// loading 声明已删除
```

## Loading State Flow

### Initial State
```typescript
const loading = ref(true)  // 页面加载时显示骨架屏
```

### During Data Loading
```typescript
const loadPosts = async () => {
  loading.value = true  // 开始加载，显示骨架屏
  try {
    // 加载数据...
  } finally {
    loading.value = false  // 加载完成，隐藏骨架屏
  }
}
```

### In Template
```vue
<div v-if="loading" class="posts-loading">
  <!-- 骨架屏 -->
  <el-skeleton ... />
</div>

<div v-else class="posts-list">
  <!-- 动态列表 -->
</div>
```

## Benefits
- ✅ 编译错误已修复
- ✅ 保持原有的加载状态逻辑
- ✅ 初始值为 `true`，页面加载时显示骨架屏
- ✅ 数据加载完成后自动隐藏骨架屏

## Verification
```bash
# 检查编译错误
npm run dev

# 预期：无编译错误，前端正常启动
```

## Files Modified
- `frontend/src/views/Social.vue` - 删除重复的 loading 声明
