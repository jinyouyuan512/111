# 智慧旅游 n8n 工作流配置指南

## 目录
1. [环境准备](#1-环境准备)
2. [n8n 安装与启动](#2-n8n-安装与启动)
3. [工作流导入](#3-工作流导入)
4. [凭证配置](#4-凭证配置)
5. [工作流详细配置](#5-工作流详细配置)
6. [测试验证](#6-测试验证)
7. [常见问题](#7-常见问题)

---

## 1. 环境准备

### 1.1 系统要求
- Node.js 18+ 或 Docker
- 内存：至少 2GB
- 端口：5678（n8n 默认端口）

### 1.2 可选依赖
- OpenAI API Key（用于 AI 功能）
- MySQL 数据库（用于数据持久化）

---

## 2. n8n 安装与启动

### 方式一：NPM 安装（推荐开发环境）

```bash
# 全局安装 n8n
npm install n8n -g

# 启动 n8n
n8n start

# 或指定端口
n8n start --port 5678
```

### 方式二：Docker 安装（推荐生产环境）

```bash
# 基础启动
docker run -d \
  --name n8n \
  -p 5678:5678 \
  -v ~/.n8n:/home/node/.n8n \
  n8nio/n8n

# 带认证启动
docker run -d \
  --name n8n \
  -p 5678:5678 \
  -v ~/.n8n:/home/node/.n8n \
  -e N8N_BASIC_AUTH_ACTIVE=true \
  -e N8N_BASIC_AUTH_USER=admin \
  -e N8N_BASIC_AUTH_PASSWORD=jiyi123456 \
  n8nio/n8n
```

### 方式三：使用项目启动脚本

```bash
# Windows
双击 START_N8N.bat

# 或命令行
npx n8n
```

### 访问 n8n
启动后访问：http://localhost:5678

---

## 3. 工作流导入

### 3.1 导入步骤

1. 打开 n8n 界面 (http://localhost:5678)
2. 点击左侧菜单 **Workflows**
3. 点击右上角 **Import from File**
4. 选择 `n8n-workflows/` 目录下的 JSON 文件
5. 点击 **Import**

### 3.2 工作流文件列表

| 文件名 | 功能 | 优先级 |
|--------|------|--------|
| tourism-ai-chat.json | AI 智能问答 | ⭐⭐⭐ |
| tourism-smart-itinerary.json | 智能行程生成 | ⭐⭐⭐ |
| tourism-audio-guide.json | 语音导览 | ⭐⭐ |
| tourism-spot-search.json | 景点搜索 | ⭐⭐⭐ |
| tourism-user-profile.json | 用户画像 | ⭐⭐ |
| tourism-personalized-recommend.json | 智能推荐 | ⭐⭐⭐ |
| tourism-ai-plan.json | AI 行程规划 | ⭐⭐ |
| tourism-weather.json | 天气查询 | ⭐⭐ |
| tourism-aggregated-data.json | 数据聚合 | ⭐ |
| tourism-dashboard.json | 运营仪表盘 | ⭐ |

### 3.3 批量导入

可以一次性导入所有工作流，n8n 会自动处理。

---

## 4. 凭证配置

### 4.1 OpenAI API 配置（AI 功能必需）

1. 在 n8n 中点击 **Settings** → **Credentials**
2. 点击 **Add Credential**
3. 搜索 **OpenAI**
4. 填写配置：
   - **Credential Name**: `OpenAI API`
   - **API Key**: 你的 OpenAI API Key

```
获取 API Key: https://platform.openai.com/api-keys
```

### 4.2 MySQL 数据库配置（可选）

1. 添加 MySQL 凭证
2. 填写配置：
   - **Host**: localhost
   - **Port**: 3306
   - **Database**: jiyi_tourism
   - **User**: root
   - **Password**: 你的密码

### 4.3 HTTP 请求配置（可选）

如需调用外部 API（天气、地图等），添加相应凭证：

**天气 API (WeatherAPI)**
- 注册：https://www.weatherapi.com/
- 免费额度：100万次/月

**高德地图 API**
- 注册：https://lbs.amap.com/
- 免费额度：每日 5000 次

---

## 5. 工作流详细配置

### 5.1 tourism-ai-chat.json - AI 智能问答

**Webhook 路径**: `/webhook/tourism/ai-chat`

**节点配置**:

| 节点名称 | 类型 | 配置说明 |
|---------|------|---------|
| Webhook 问答请求 | Webhook | 接收 POST 请求 |
| 准备对话上下文 | Code | 构建知识库和上下文 |
| AI 生成回答 | OpenAI | 需要配置 OpenAI 凭证 |
| 处理AI响应 | Code | 解析响应或降级处理 |
| 返回回答 | Respond to Webhook | 返回 JSON 响应 |

**OpenAI 节点配置**:
```json
{
  "model": "gpt-4",
  "temperature": 0.7,
  "maxTokens": 1000
}
```

**如果没有 OpenAI API Key**:
工作流内置了规则引擎降级方案，会使用预设的知识库回答问题。

---

### 5.2 tourism-smart-itinerary.json - 智能行程生成

**Webhook 路径**: `/webhook/tourism/smart-itinerary`

**请求参数**:
```json
{
  "spots": ["西柏坡纪念馆", "狼牙山五壮士纪念地"],
  "startDate": "2026-02-01",
  "pace": "moderate",
  "includeFood": true,
  "includeHotel": true,
  "budget": "medium"
}
```

**节点配置**:

| 节点名称 | 类型 | 配置说明 |
|---------|------|---------|
| Webhook 行程请求 | Webhook | 接收景点列表和偏好 |
| 生成智能行程 | Code | 核心行程生成逻辑 |
| 返回行程 | Respond to Webhook | 返回完整行程 |

**内置数据**:
- 景点信息（开放时间、门票、游览时长）
- 餐厅推荐（按区域和价位）
- 酒店推荐（按星级和价位）

---

### 5.3 tourism-audio-guide.json - 语音导览

**Webhook 路径**: `/webhook/tourism/audio-guide`

**请求参数**:
```json
{
  "spotName": "西柏坡纪念馆",
  "style": "standard",
  "language": "zh"
}
```

**支持的讲解风格**:
- `standard` - 标准讲解
- `story` - 故事模式
- `kids` - 儿童版

**内置景点导览**:
- 西柏坡纪念馆
- 狼牙山
- 白洋淀
- 塞罕坝

---

### 5.4 tourism-spot-search.json - 景点搜索

**Webhook 路径**: `/webhook/tourism/spot-search`

**请求参数**:
```json
{
  "query": "红色旅游",
  "filters": {
    "category": "红色文化",
    "city": "石家庄",
    "freeOnly": true,
    "sortBy": "rating"
  },
  "page": 1,
  "pageSize": 10
}
```

**支持的筛选条件**:
- `category`: 红色文化、生态文明、历史遗迹
- `city`: 石家庄、保定、承德、邯郸
- `freeOnly`: 仅免费景点
- `priceRange`: [最低价, 最高价]
- `tags`: 标签数组
- `sortBy`: rating（评分）、reviewCount（评论数）、price（价格）

---

### 5.5 tourism-user-profile.json - 用户画像

**Webhook 路径**: `/webhook/tourism/user-profile`

**请求参数**:
```json
{
  "userId": "user-001"
}
```

**返回数据**:
- 基本信息（访问次数、收藏数等）
- 偏好分析（喜欢的景点类型）
- 用户标签
- 个性化推荐

---

### 5.6 tourism-personalized-recommend.json - 智能推荐

**Webhook 路径**: `/webhook/tourism/personalized-recommend`

**请求参数**:
```json
{
  "preferences": {
    "interests": ["红色文化", "自然风光"],
    "travelStyle": "comfort",
    "pace": "moderate",
    "groupType": "family",
    "avoidCrowds": true
  },
  "limit": 5
}
```

**推荐算法**:
1. 兴趣标签匹配 (+3分/匹配)
2. 出行风格匹配 (+5分)
3. 人群类型匹配 (+5分)
4. AI 语义优化（如配置了 OpenAI）

---

## 6. 测试验证

### 6.1 使用测试页面

打开项目根目录下的 `test_n8n_ai_workflows.html`，可以可视化测试所有工作流。

### 6.2 使用 curl 测试

**测试 AI 问答**:
```bash
curl -X POST http://localhost:5678/webhook/tourism/ai-chat \
  -H "Content-Type: application/json" \
  -d '{"question": "西柏坡有什么必看景点？"}'
```

**测试智能行程**:
```bash
curl -X POST http://localhost:5678/webhook/tourism/smart-itinerary \
  -H "Content-Type: application/json" \
  -d '{"spots": ["西柏坡纪念馆", "狼牙山五壮士纪念地"], "startDate": "2026-02-01"}'
```

**测试景点搜索**:
```bash
curl -X POST http://localhost:5678/webhook/tourism/spot-search \
  -H "Content-Type: application/json" \
  -d '{"query": "红色", "filters": {"freeOnly": true}}'
```

### 6.3 使用 Postman

导入以下 Collection：

```json
{
  "info": { "name": "智慧旅游 n8n API" },
  "item": [
    {
      "name": "AI 问答",
      "request": {
        "method": "POST",
        "url": "http://localhost:5678/webhook/tourism/ai-chat",
        "body": {
          "mode": "raw",
          "raw": "{\"question\": \"推荐一日游路线\"}"
        }
      }
    }
  ]
}
```

---

## 7. 常见问题

### Q1: 工作流导入后显示错误

**原因**: 缺少凭证配置

**解决**: 
1. 打开工作流
2. 点击报错的节点（通常是 OpenAI 节点）
3. 配置或移除凭证

### Q2: Webhook 返回 404

**原因**: 工作流未激活

**解决**:
1. 打开工作流
2. 点击右上角的开关，激活工作流

### Q3: AI 回答不准确

**原因**: 未配置 OpenAI，使用了降级的规则引擎

**解决**:
1. 配置 OpenAI API Key
2. 或者扩展 Code 节点中的知识库

### Q4: 请求超时

**原因**: OpenAI API 响应慢

**解决**:
1. 增加前端请求超时时间
2. 或使用国内 AI 服务替代

### Q5: CORS 错误

**原因**: 跨域请求被阻止

**解决**: 在 Webhook 节点中添加响应头：
```json
{
  "Access-Control-Allow-Origin": "*"
}
```

---

## 8. 工作流节点类型说明

### Webhook 节点
- **用途**: 接收 HTTP 请求
- **配置项**:
  - HTTP Method: POST
  - Path: 自定义路径
  - Response Mode: responseNode（使用 Respond to Webhook 返回）

### Code 节点
- **用途**: 执行 JavaScript 代码
- **版本**: 使用 typeVersion 2
- **访问数据**: `$input.first().json`
- **访问其他节点**: `$('节点名称').first().json`

### OpenAI 节点
- **用途**: 调用 OpenAI API
- **配置项**:
  - Model: gpt-4 / gpt-3.5-turbo
  - Temperature: 0-1（创造性程度）
  - Max Tokens: 最大输出长度

### Respond to Webhook 节点
- **用途**: 返回 HTTP 响应
- **配置项**:
  - Respond With: JSON
  - Response Body: `={{ $json }}`

---

## 9. 扩展开发

### 添加新的景点数据

在 Code 节点中扩展 `spotInfo` 对象：

```javascript
const spotInfo = {
  '新景点名称': {
    duration: 180,
    openTime: '09:00',
    closeTime: '17:00',
    ticketPrice: 50,
    location: { lat: 38.0, lng: 114.0 },
    category: '红色文化',
    highlights: ['亮点1', '亮点2'],
    tips: '游览提示'
  }
};
```

### 接入真实 API

替换 Code 节点中的模拟数据为 HTTP Request 节点：

1. 添加 HTTP Request 节点
2. 配置 API URL 和认证
3. 在后续 Code 节点中处理响应

### 添加数据库存储

1. 添加 MySQL 节点
2. 配置数据库凭证
3. 编写 SQL 查询

---

## 10. 生产环境部署

### Docker Compose 配置

```yaml
version: '3'
services:
  n8n:
    image: n8nio/n8n
    ports:
      - "5678:5678"
    environment:
      - N8N_BASIC_AUTH_ACTIVE=true
      - N8N_BASIC_AUTH_USER=admin
      - N8N_BASIC_AUTH_PASSWORD=${N8N_PASSWORD}
      - N8N_HOST=your-domain.com
      - N8N_PROTOCOL=https
      - WEBHOOK_URL=https://your-domain.com/
    volumes:
      - n8n_data:/home/node/.n8n

volumes:
  n8n_data:
```

### Nginx 反向代理

```nginx
server {
    listen 443 ssl;
    server_name n8n.your-domain.com;

    location / {
        proxy_pass http://localhost:5678;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
    }
}
```

---

## 联系支持

如有问题，请查看：
- n8n 官方文档：https://docs.n8n.io/
- 项目 README：`n8n-workflows/README.md`
- 测试页面：`test_n8n_ai_workflows.html`
