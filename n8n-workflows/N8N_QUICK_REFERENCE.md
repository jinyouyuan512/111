# n8n 工作流快速参考

## 🚀 快速启动

```bash
# 启动 n8n
npx n8n

# 访问地址
http://localhost:5678
```

---

## 📋 工作流 API 速查表

| 功能 | Webhook 路径 | 方法 |
|------|-------------|------|
| AI 智能问答 | `/webhook/tourism/ai-chat` | POST |
| 智能行程生成 | `/webhook/tourism/smart-itinerary` | POST |
| 语音导览 | `/webhook/tourism/audio-guide` | POST |
| 景点搜索 | `/webhook/tourism/spot-search` | POST |
| 用户画像 | `/webhook/tourism/user-profile` | POST |
| 智能推荐 | `/webhook/tourism/personalized-recommend` | POST |
| AI 行程规划 | `/webhook/tourism/ai-plan` | POST |
| 天气查询 | `/webhook/tourism/weather` | POST |
| 运营仪表盘 | `/webhook/tourism/dashboard` | POST |

---

## 📝 请求示例

### AI 问答
```json
POST /webhook/tourism/ai-chat
{
  "question": "西柏坡有什么必看景点？",
  "sessionId": "session-123"
}
```

### 智能行程
```json
POST /webhook/tourism/smart-itinerary
{
  "spots": ["西柏坡纪念馆", "狼牙山五壮士纪念地"],
  "startDate": "2026-02-01",
  "pace": "moderate",
  "budget": "medium"
}
```

### 景点搜索
```json
POST /webhook/tourism/spot-search
{
  "query": "红色旅游",
  "filters": {
    "category": "红色文化",
    "freeOnly": true
  }
}
```

### 智能推荐
```json
POST /webhook/tourism/personalized-recommend
{
  "preferences": {
    "interests": ["红色文化"],
    "groupType": "family"
  },
  "limit": 5
}
```

---

## 🔧 curl 测试命令

```bash
# AI 问答
curl -X POST http://localhost:5678/webhook/tourism/ai-chat \
  -H "Content-Type: application/json" \
  -d '{"question": "推荐一日游路线"}'

# 景点搜索
curl -X POST http://localhost:5678/webhook/tourism/spot-search \
  -H "Content-Type: application/json" \
  -d '{"query": "免费"}'

# 智能行程
curl -X POST http://localhost:5678/webhook/tourism/smart-itinerary \
  -H "Content-Type: application/json" \
  -d '{"spots": ["西柏坡纪念馆"]}'
```

---

## 📁 工作流文件

```
n8n-workflows/
├── tourism-ai-chat.json          # AI 问答
├── tourism-smart-itinerary.json  # 智能行程
├── tourism-audio-guide.json      # 语音导览
├── tourism-spot-search.json      # 景点搜索
├── tourism-user-profile.json     # 用户画像
├── tourism-personalized-recommend.json  # 智能推荐
├── tourism-ai-plan.json          # AI 规划
├── tourism-weather.json          # 天气查询
├── tourism-dashboard.json        # 仪表盘
└── README.md                     # 说明文档
```

---

## ⚙️ 凭证配置

### OpenAI（AI 功能需要）
1. Settings → Credentials → Add
2. 搜索 "OpenAI"
3. 填入 API Key

### 无 OpenAI 时
工作流会自动使用内置规则引擎降级处理

---

## 🐛 常见问题

| 问题 | 解决方案 |
|------|---------|
| 404 错误 | 激活工作流（点击开关） |
| 凭证错误 | 配置或删除 OpenAI 凭证 |
| CORS 错误 | Webhook 添加 CORS 头 |
| 超时 | 增加前端超时时间 |

---

## 🧪 测试页面

打开 `test_n8n_ai_workflows.html` 进行可视化测试

---

## 📚 详细文档

- 完整配置指南：`N8N_WORKFLOW_CONFIG_GUIDE.md`
- 项目说明：`README.md`
- 节点配置：`N8N_NODE_CONFIG_GUIDE.md`
