# 智慧旅游 AI + 数据库 工作流配置指南

## 工作流说明

这些工作流集成了 **MySQL 数据库** 和 **OpenAI** 来实现：
1. 从数据库获取景点真实数据
2. 使用 AI 生成个性化的语音讲解和行程规划
3. AI 智能聊天助手（冀小游）

## 工作流文件

| 文件 | Webhook路径 | 功能 |
|-----|------------|------|
| `tourism-ai-chat-http.json` | `/webhook/tourism/ai-chat` | AI聊天(冀小游) - HTTP版 ⭐推荐 |
| `tourism-ai-chat-openai.json` | `/webhook/tourism/ai-chat` | AI聊天(冀小游) - LangChain版 |
| `tourism-audio-guide-ai.json` | `/webhook/tourism/audio-guide` | AI生成语音讲解 |
| `tourism-ai-route-plan-db.json` | `/webhook/tourism/ai-plan` | AI行程规划 |
| `tourism-routes-db.json` | `/webhook/tourism/routes` | 热门路线 |

## 配置步骤

### 1. 配置 MySQL 凭证

在 n8n 中创建 MySQL 凭证：
1. 点击左侧 "Credentials"
2. 点击 "Add Credential"
3. 搜索 "MySQL"
4. 填写：
   - Credential Name: `MySQL jiyi_tourism`
   - Host: `localhost`
   - Port: `3306`
   - Database: `jiyi_tourism`
   - User: `root`
   - Password: 你的密码

### 2. 配置 OpenAI 凭证

**方式A：HTTP Header Auth（推荐，用于 HTTP 版工作流）**
1. 点击 "Add Credential"
2. 搜索 "Header Auth"
3. 填写：
   - Credential Name: `OpenAI API Key`
   - Name: `Authorization`
   - Value: `Bearer sk-你的OpenAI密钥`

**方式B：OpenAI 凭证（用于 LangChain 版工作流）**
1. 点击 "Add Credential"
2. 搜索 "OpenAI"
3. 填写：
   - Credential Name: `OpenAI API`
   - API Key: 你的 OpenAI API Key

### 3. 导入工作流

**推荐导入顺序：**
1. `tourism-ai-chat-http.json` - AI聊天（HTTP版，兼容性最好）
2. `tourism-audio-guide-ai.json` - AI语音讲解
3. `tourism-ai-route-plan-db.json` - AI行程规划

**导入步骤：**
1. 打开 n8n (http://localhost:5678)
2. 点击 "Workflows" → "Import from File"
3. 导入工作流文件
4. 打开每个工作流，配置凭证：
   - 点击 MySQL 节点 → 选择 MySQL 凭证
   - 点击 HTTP Request/OpenAI 节点 → 选择对应凭证
5. 保存并激活工作流

### 4. 测试

```bash
# 测试 AI 聊天（冀小游）
curl -X POST http://localhost:5678/webhook/tourism/ai-chat \
  -H "Content-Type: application/json" \
  -d '{"question":"西柏坡有什么好玩的？"}'

# 测试 AI 语音讲解
curl -X POST http://localhost:5678/webhook/tourism/audio-guide \
  -H "Content-Type: application/json" \
  -d '{"spotName":"西柏坡"}'

# 测试 AI 行程规划
curl -X POST http://localhost:5678/webhook/tourism/ai-plan \
  -H "Content-Type: application/json" \
  -d '{"spots":["西柏坡","狼牙山"],"duration":2,"startDate":"2026-01-15"}'
```

## 数据库表结构

工作流需要 `attraction` 表包含以下字段：
- `id` - 景点ID
- `name` - 景点名称
- `description` - 描述
- `address` - 地址
- `ticket_price` - 门票价格
- `opening_hours` - 开放时间
- `visit_duration` - 建议游览时长(分钟)
- `rating` - 评分
- `category` - 分类
- `history` - 历史背景
- `highlights` - 亮点
- `tips` - 游览贴士
- `status` - 状态

## 无 OpenAI 时的降级

如果没有配置 OpenAI，可以使用简化版工作流：
- `tourism-audio-guide-db.json` - 使用预设内容
- `tourism-ai-route-plan.json` - 使用规则生成

## 注意事项

1. OpenAI API 需要付费，请确保账户有余额
2. 首次调用可能较慢（冷启动）
3. 如果 AI 返回格式不正确，会自动降级到默认内容
