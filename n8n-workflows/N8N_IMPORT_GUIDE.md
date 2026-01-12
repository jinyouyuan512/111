# n8n 工作流导入指南（图文版）

## 第一步：打开 n8n

访问 http://localhost:5678

## 第二步：导入工作流

### 2.1 进入工作流列表
点击左侧菜单 **Workflows**

### 2.2 导入文件
1. 点击右上角 **⋮** 或 **Import** 按钮
2. 选择 **Import from File**
3. 选择 `n8n-workflows` 目录下的 JSON 文件

### 2.3 需要导入的5个工作流文件

| 文件名 | 功能 | Webhook 路径 |
|--------|------|-------------|
| `tourism-weather.json` | 天气查询 | `/webhook/tourism/weather` |
| `tourism-aggregated-data.json` | 多源数据聚合 | `/webhook/tourism/aggregated-data` |
| `tourism-personalized-recommend.json` | 智能推荐 | `/webhook/tourism/personalized-recommend` |
| `tourism-ai-plan.json` | AI行程规划 | `/webhook/tourism/ai-plan` |
| `tourism-dashboard.json` | 运营仪表盘 | `/webhook/tourism/dashboard` |

## 第三步：激活工作流

导入后，每个工作流默认是**未激活**状态。

1. 打开每个工作流
2. 点击右上角的**开关按钮**
3. 开关变成**绿色**表示已激活

⚠️ **重要**：只有激活的工作流才能响应 Webhook 请求！

## 第四步：测试工作流

### 方法1：使用测试页面
打开项目根目录的 `test_n8n_api.html`，点击各个测试按钮

### 方法2：使用 curl 命令
```bash
# 测试天气查询
curl -X POST http://localhost:5678/webhook/tourism/weather ^
  -H "Content-Type: application/json" ^
  -d "{\"spots\": [\"西柏坡纪念馆\"]}"

# 测试智能推荐
curl -X POST http://localhost:5678/webhook/tourism/personalized-recommend ^
  -H "Content-Type: application/json" ^
  -d "{\"preferences\": {\"interests\": [\"红色文化\"]}, \"limit\": 3}"
```

## API 密钥配置（可选）

工作流内置了模拟数据，**不配置API密钥也能正常测试**。

如需使用真实天气数据：

### 方法1：在节点中直接配置
1. 打开 `tourism-weather` 工作流
2. 双击 **HTTP Request** 节点
3. 在 URL 中将 `{{$env.WEATHER_API_KEY}}` 替换为你的实际密钥

### 方法2：使用 Credentials
1. 点击左侧 **Credentials**
2. 点击 **Add Credential**
3. 选择 **Header Auth**
4. 配置后在节点中引用

### 方法3：Docker 环境变量
```bash
docker run -d --name n8n -p 5678:5678 \
  -e WEATHER_API_KEY=你的密钥 \
  n8nio/n8n
```

## 常见问题

### Q: Webhook 返回 404？
A: 工作流未激活，请确保开关为绿色

### Q: 导入失败？
A: 检查 JSON 文件是否完整，尝试重新下载

### Q: 前端调用失败？
A: 检查 n8n 是否运行在 5678 端口

## 工作流功能说明

### 1. tourism-weather（天气查询）
- 输入：景点名称列表
- 输出：各景点天气预报、出行建议
- 数据源：WeatherAPI（或模拟数据）

### 2. tourism-aggregated-data（数据聚合）
- 输入：单个景点名称
- 输出：天气+交通+人流+门票综合信息
- 用途：景点详情页展示

### 3. tourism-personalized-recommend（智能推荐）
- 输入：用户偏好（兴趣、风格、人群类型）
- 输出：匹配度排序的推荐路线
- 算法：标签匹配 + 评分加权

### 4. tourism-ai-plan（AI行程规划）
- 输入：景点列表、天数、偏好
- 输出：详细每日行程安排
- 可选：接入 OpenAI 生成更智能的规划

### 5. tourism-dashboard（运营仪表盘）
- 输入：无
- 输出：访客统计、热门景点、系统状态
- 用途：管理后台数据展示

## 前端集成

工作流已与前端 `Tourism.vue` 集成，API 接口定义在：
- `frontend/src/api/n8n.ts`

前端配置（可选）：
```env
# frontend/.env.local
VITE_N8N_URL=http://localhost:5678
```
