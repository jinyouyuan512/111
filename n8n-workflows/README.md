# 智慧旅游 n8n 工作流 - 方案B：深度集成

本目录包含智慧旅游模块与 n8n 深度集成的工作流配置文件。

## 快速开始

### 1. 启动 n8n
```bash
# Windows: 双击运行
START_N8N.bat

# 或使用 Docker
docker run -d --name n8n -p 5678:5678 n8nio/n8n
```

### 2. 导入工作流
1. 打开 http://localhost:5678
2. 登录（admin / jiyi123456）
3. Workflows → Import from File
4. 导入本目录下所有 .json 文件
5. 激活每个工作流

### 3. 测试
打开 `test_n8n_api.html` 进行可视化测试

## 架构概览

```
┌─────────────────────────────────────────────────────────────────────┐
│                    n8n 作为数据处理中枢                              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐     │
│  │ 天气API  │    │ 交通API  │    │ 人流API  │    │ AI服务   │     │
│  │ Weather  │    │ 高德地图 │    │ 景区系统 │    │ OpenAI   │     │
│  └────┬─────┘    └────┬─────┘    └────┬─────┘    └────┬─────┘     │
│       │               │               │               │           │
│       └───────────────┴───────┬───────┴───────────────┘           │
│                               ▼                                    │
│                    ┌──────────────────┐                           │
│                    │   n8n 工作流引擎  │                           │
│                    │  数据聚合 & AI处理 │                           │
│                    └────────┬─────────┘                           │
│                             │                                      │
│       ┌─────────────────────┼─────────────────────┐               │
│       ▼                     ▼                     ▼               │
│  ┌─────────┐         ┌─────────┐          ┌─────────┐            │
│  │AI智能   │         │智能行程 │          │用户画像 │            │
│  │ 问答   │         │ 规划   │          │ 分析   │            │
│  └─────────┘         └─────────┘          └─────────┘            │
│       │                     │                     │               │
│       └─────────────────────┼─────────────────────┘               │
│                             ▼                                      │
│                    ┌──────────────────┐                           │
│                    │   前端 Vue 应用   │                           │
│                    │   Tourism.vue    │                           │
│                    └──────────────────┘                           │
└─────────────────────────────────────────────────────────────────────┘
```

## 工作流列表

## 工作流列表

### 核心 AI 工作流（全部接入AI）

| 文件名 | 功能 | Webhook路径 | AI接入 |
|--------|------|-------------|--------|
| tourism-ai-chat.json | AI智能问答助手 | POST /webhook/tourism/ai-chat | ✅ Basic LLM Chain |
| tourism-ai-plan.json | AI行程规划 | POST /webhook/tourism/ai-plan | ✅ Code内置 |
| tourism-audio-guide.json | 语音导览生成 | POST /webhook/tourism/audio-guide | ✅ Basic LLM Chain |
| tourism-smart-itinerary.json | 智能行程生成器 | POST /webhook/tourism/smart-itinerary | ✅ Basic LLM Chain |
| tourism-personalized-recommend.json | 智能推荐引擎 | POST /webhook/tourism/personalized-recommend | ✅ Basic LLM Chain |
| tourism-user-profile.json | 用户画像分析 | POST /webhook/tourism/user-profile | ✅ Basic LLM Chain |
| tourism-spot-search.json | 智能景点搜索 | POST /webhook/tourism/spot-search | ✅ Basic LLM Chain |

### 数据服务工作流（全部接入AI）

| 文件名 | 功能 | Webhook路径 | AI接入 |
|--------|------|-------------|--------|
| tourism-weather-enhanced.json | 天气查询(增强版) | POST /webhook/tourism/weather | ✅ Code内置 |
| tourism-aggregated-data.json | 多源数据聚合 | POST /webhook/tourism/aggregated-data | ✅ Basic LLM Chain |
| tourism-realtime-optimize.json | 实时路线优化 | POST /webhook/tourism/realtime-optimize | ✅ Basic LLM Chain |
| tourism-dashboard.json | 运营数据仪表盘 | POST /webhook/tourism/dashboard | ✅ Code内置 |

### 辅助服务工作流（全部接入AI）

| 文件名 | 功能 | Webhook路径 | AI接入 |
|--------|------|-------------|--------|
| tourism-trip-reminder.json | 行程提醒服务 | POST /webhook/tourism/trip-reminder | ✅ Basic LLM Chain |
| tourism-review-analysis.json | 评价分析服务 | POST /webhook/tourism/review-analysis | ✅ Basic LLM Chain |
| tourism-spot-info.json | 景点详情查询 | POST /webhook/tourism/spot-info | ✅ Basic LLM Chain |

### 红色新闻与读物工作流（新增）

| 文件名 | 功能 | Webhook路径 | AI接入 |
|--------|------|-------------|--------|
| academy-red-news.json | 河北红色新闻服务 | POST /webhook/academy/red-news | ✅ Basic LLM Chain |
| academy-red-books.json | 红色读物推荐服务 | POST /webhook/academy/red-books | ✅ Basic LLM Chain |
| academy-book-recommend.json | AI读书推荐助手 | POST /webhook/academy/book-recommend | ✅ Basic LLM Chain |

---

## 详细工作流说明

### 1. tourism-ai-chat.json - AI智能问答助手

**触发方式**: POST `/webhook/tourism/ai-chat`

**功能**: 
- 基于知识库的智能问答
- 支持景点查询、路线推荐、美食推荐、天气查询
- AI生成个性化回答
- 提供相关问题建议

**请求示例**:
```json
{
  "question": "西柏坡有什么必看景点？",
  "sessionId": "user-session-123",
  "userId": "user-001"
}
```

**响应示例**:
```json
{
  "answer": "🏛️ 西柏坡纪念馆\n\n西柏坡位于河北省石家庄市平山县...",
  "type": "spot_info",
  "sessionId": "user-session-123",
  "suggestions": ["西柏坡门票多少钱？", "西柏坡怎么去？", "西柏坡附近有什么美食？"],
  "relatedSpots": ["西柏坡纪念馆"],
  "timestamp": "2026-01-09T12:00:00.000Z"
}
```

### 2. tourism-audio-guide.json - 语音导览生成

**触发方式**: POST `/webhook/tourism/audio-guide`

**功能**:
- 生成景点语音导览内容
- 支持多种讲解风格（标准、故事、儿童）
- 分章节组织内容
- 提供音频URL（需配合TTS服务）

**请求示例**:
```json
{
  "spotName": "西柏坡纪念馆",
  "language": "zh",
  "style": "standard"
}
```

**响应示例**:
```json
{
  "success": true,
  "spotName": "西柏坡纪念馆",
  "guide": {
    "intro": "欢迎来到西柏坡纪念馆...",
    "chapters": [
      {
        "id": 1,
        "title": "历史背景",
        "content": "1948年5月，中共中央...",
        "duration": 120,
        "audioUrl": "/audio/guide/西柏坡纪念馆/standard/1.mp3"
      }
    ],
    "totalDuration": 450,
    "totalChapters": 4
  }
}
```

### 3. tourism-smart-itinerary.json - 智能行程生成器

**触发方式**: POST `/webhook/tourism/smart-itinerary`

**功能**:
- 根据选择的景点自动生成行程
- 智能分配每日游览安排
- 推荐餐厅和住宿
- 计算费用和距离

**请求示例**:
```json
{
  "spots": ["西柏坡纪念馆", "狼牙山五壮士纪念地", "白洋淀雁翎队纪念馆"],
  "startDate": "2026-02-01",
  "pace": "moderate",
  "includeFood": true,
  "includeHotel": true,
  "budget": "medium"
}
```

### 4. tourism-user-profile.json - 用户画像分析

**触发方式**: POST `/webhook/tourism/user-profile`

**功能**:
- 分析用户游览历史
- 生成用户标签
- 计算偏好类别
- 提供个性化推荐

**请求示例**:
```json
{
  "userId": "user-001"
}
```

### 5. tourism-spot-search.json - 智能景点搜索

**触发方式**: POST `/webhook/tourism/spot-search`

**功能**:
- 关键词搜索
- 多维度筛选（分类、城市、价格、标签）
- 排序功能
- 分页支持

**请求示例**:
```json
{
  "query": "红色旅游",
  "filters": {
    "category": "红色文化",
    "freeOnly": true,
    "sortBy": "rating"
  },
  "page": 1,
  "pageSize": 10
}
```

---

## 部署指南

### 1. 安装 n8n

```bash
# Docker 方式（推荐）
docker run -d \
  --name n8n \
  -p 5678:5678 \
  -v ~/.n8n:/home/node/.n8n \
  -e N8N_BASIC_AUTH_ACTIVE=true \
  -e N8N_BASIC_AUTH_USER=admin \
  -e N8N_BASIC_AUTH_PASSWORD=your_password \
  n8nio/n8n

# 或 npm 方式
npm install n8n -g
n8n start
```

### 2. 配置环境变量

在 n8n 中设置以下环境变量:

```env
WEATHER_API_KEY=your_weatherapi_key
AMAP_API_KEY=your_amap_key
OPENAI_API_KEY=your_openai_key
DATABASE_URL=mysql://user:pass@host:3306/tourism
```

### 3. 导入工作流

1. 打开 n8n 界面 (http://localhost:5678)
2. 点击 "Import from File"
3. 依次导入本目录下的 JSON 文件
4. 配置各节点的凭证（如OpenAI API Key）
5. 激活工作流

### 4. 前端配置

```env
# frontend/.env.local
VITE_N8N_URL=http://localhost:5678
```

---

## 前端 API 使用指南

前端已封装完整的 n8n 工作流 API 调用函数，位于 `frontend/src/api/n8n.ts`。

### 导入方式

```typescript
import * as n8nApi from '@/api/n8n'
// 或按需导入
import { 
  aiChat, 
  generateSmartItinerary, 
  getPersonalizedRecommendations,
  analyzeUserProfile,
  searchSpots,
  optimizeRoute,
  getTripReminder,
  analyzeReviews
} from '@/api/n8n'
```

### API 函数列表

| 函数名 | 功能 | 对应工作流 |
|--------|------|-----------|
| `aiChat(request)` | AI 智能问答 | tourism-ai-chat |
| `getAudioGuide(request)` | 语音导览生成 | tourism-audio-guide |
| `generateAITripPlan(request)` | AI 行程规划 | tourism-ai-plan |
| `generateSmartItinerary(request)` | 智能行程生成器 | tourism-smart-itinerary |
| `getPersonalizedRecommendations(request)` | 个性化推荐 | tourism-personalized-recommend |
| `analyzeUserProfile(request)` | 用户画像分析 | tourism-user-profile |
| `searchSpots(request)` | 智能景点搜索 | tourism-spot-search |
| `optimizeRoute(request)` | 实时路线优化 | tourism-realtime-optimize |
| `getTripReminder(request)` | 行程提醒服务 | tourism-trip-reminder |
| `analyzeReviews(request)` | 评价分析服务 | tourism-review-analysis |
| `getSpotInfo(request)` | 景点详情查询 | tourism-spot-info |
| `getSpotWeather(spotNames, date)` | 天气查询 | tourism-weather |
| `getHotRoutes()` | 热门路线 | tourism-routes |
| `getRedSpots()` | 红色景点 | tourism-spots |
| `getDashboardData()` | 仪表盘数据 | tourism-dashboard |

### 使用示例

```typescript
// 1. 智能行程生成
const itinerary = await n8nApi.generateSmartItinerary({
  spots: ['西柏坡纪念馆', '狼牙山五壮士纪念地'],
  startDate: '2026-02-01',
  pace: 'moderate',
  budget: 'medium'
})

// 2. 个性化推荐
const recommendations = await n8nApi.getPersonalizedRecommendations({
  interests: ['红色文化', '自然风光'],
  groupType: 'family',
  days: 2
})

// 3. 用户画像分析
const profile = await n8nApi.analyzeUserProfile({
  userId: 'user-001'
})

// 4. 智能景点搜索
const searchResult = await n8nApi.searchSpots({
  query: '红色旅游',
  filters: { freeOnly: true, city: '保定' }
})

// 5. 路线优化
const optimized = await n8nApi.optimizeRoute({
  spots: ['西柏坡纪念馆', '狼牙山五壮士纪念地', '白洋淀雁翎队纪念馆'],
  optimizeFor: 'distance'
})

// 6. 行程提醒
const reminder = await n8nApi.getTripReminder({
  spotName: '西柏坡纪念馆',
  startDate: '2026-02-01'
})

// 7. 评价分析
const analysis = await n8nApi.analyzeReviews({
  spotName: '西柏坡纪念馆'
})
```

### 测试页面

打开 `test_n8n_workflows.html` 可以可视化测试所有工作流 API。

---

## 降级策略

当 n8n 服务不可用时，前端会自动使用模拟数据，确保功能可用：

```typescript
// frontend/src/api/n8n.ts
async function triggerWebhook<T>(webhookPath: string, data: any): Promise<T> {
  try {
    const response = await n8nClient.post(`/webhook/${webhookPath}`, data)
    return response.data
  } catch (error: any) {
    console.warn('n8n 不可用，使用模拟数据:', webhookPath)
    return getMockData(webhookPath, data) as T
  }
}
```

---

## 扩展建议

### 1. 接入真实 AI 服务
- 配置 OpenAI API Key
- 或接入国内 AI 服务（通义千问、文心一言等）

### 2. 添加更多数据源
- 携程/美团景区数据
- 百度地图POI
- 实时天气API

### 3. 增强功能
- 语音合成（TTS）
- 图片识别
- 多语言支持

### 4. 自动化运营
- 定时数据同步
- 异常告警通知
- 用户行为分析报表
