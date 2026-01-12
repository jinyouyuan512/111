# 智慧旅游 n8n 工作流配置指南

## 工作流列表

| 文件 | Webhook路径 | 功能 | 前端调用 |
|-----|------------|------|---------|
| `tourism-spots-db.json` | `/webhook/tourism/spots` | 从数据库获取红色景点列表 | `n8nApi.getRedSpots()` |
| `tourism-routes-db.json` | `/webhook/tourism/routes` | 从数据库获取热门路线 | `n8nApi.getHotRoutes()` |
| `tourism-route-detail-db.json` | `/webhook/tourism/route-detail` | 获取路线详情 | - |
| `tourism-ai-route-plan.json` | `/webhook/tourism/ai-plan` | AI智能行程规划 | `n8nApi.generateAITripPlan()` |
| `tourism-audio-guide-db.json` | `/webhook/tourism/audio-guide` | 获取语音讲解内容 | `n8nApi.getAudioGuide()` |

## 前端集成说明

前端 `Tourism.vue` 已更新，会优先调用 n8n 工作流获取数据：

1. **热门路线** - 调用 `n8nApi.getHotRoutes()` → `/webhook/tourism/routes`
2. **语音讲解** - 点击景点时调用 `n8nApi.getAudioGuide()` → `/webhook/tourism/audio-guide`
3. **AI行程规划** - 点击"AI生成行程"时调用 `n8nApi.generateAITripPlan()` → `/webhook/tourism/ai-plan`
4. **AI聊天** - 冀小游助手调用 `n8nApi.aiChat()` → `/webhook/tourism/ai-chat`

如果 n8n 不可用，会自动降级到后端 API 或本地模拟数据。

## 配置步骤

### 1. 启动 n8n

```bash
# 方式1：使用 npx
npx n8n

# 方式2：使用 Docker
docker run -it --rm -p 5678:5678 n8nio/n8n
```

### 2. 配置 MySQL 凭证

在 n8n 中创建 MySQL 凭证：
- 名称: `MySQL jiyi_tourism`
- Host: `localhost`
- Port: `3306`
- Database: `jiyi_tourism`
- User: `root`
- Password: 你的密码

### 3. 导入工作流

1. 打开 n8n (http://localhost:5678)
2. 点击 "Import from file"
3. 依次导入上述 JSON 文件
4. 为每个工作流配置 MySQL 凭证
5. 激活工作流

### 4. 测试接口

```bash
# 获取景点列表
curl -X POST http://localhost:5678/webhook/tourism/spots

# 获取热门路线
curl -X POST http://localhost:5678/webhook/tourism/routes

# AI行程规划
curl -X POST http://localhost:5678/webhook/tourism/ai-plan \
  -H "Content-Type: application/json" \
  -d '{"spots":["西柏坡","狼牙山"],"duration":2}'

# 获取语音讲解
curl -X POST http://localhost:5678/webhook/tourism/audio-guide \
  -H "Content-Type: application/json" \
  -d '{"spotName":"西柏坡"}'

# AI聊天
curl -X POST http://localhost:5678/webhook/tourism/ai-chat \
  -H "Content-Type: application/json" \
  -d '{"question":"西柏坡有什么必看景点？"}'
```

## 数据来源

工作流从以下数据库表读取数据：
- `attraction` - 景点信息
- `route` - 路线信息
- `route_attraction` - 路线景点关联
- `audio_guide` - 语音讲解

## 降级机制

前端通过 `api/n8n.ts` 调用这些工作流，如果 n8n 不可用会自动降级：

1. 首先尝试调用 n8n webhook
2. 如果失败，尝试调用后端 API
3. 最终降级到本地模拟数据

这确保了即使 n8n 未运行，页面也能正常显示。

## 访问页面

前端页面: http://localhost:3001/tourism
