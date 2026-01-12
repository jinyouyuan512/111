# n8n 工作流设置指南

## 一、安装 n8n

### 方式1：Docker（推荐）

```bash
# 创建数据目录
mkdir -p ~/.n8n

# 启动 n8n
docker run -d \
  --name n8n \
  -p 5678:5678 \
  -v ~/.n8n:/home/node/.n8n \
  -e N8N_BASIC_AUTH_ACTIVE=true \
  -e N8N_BASIC_AUTH_USER=admin \
  -e N8N_BASIC_AUTH_PASSWORD=jiyi123456 \
  -e GENERIC_TIMEZONE=Asia/Shanghai \
  n8nio/n8n
```

### 方式2：npm 全局安装

```bash
npm install n8n -g
n8n start
```

### 方式3：Windows 一键启动（见下方脚本）

## 二、访问 n8n

- 地址：http://localhost:5678
- 用户名：admin
- 密码：jiyi123456

## 三、导入工作流

1. 打开 n8n 界面
2. 点击左侧 "Workflows"
3. 点击右上角 "..." → "Import from File"
4. 依次导入 `n8n-workflows/` 目录下的文件：
   - `tourism-weather.json`
   - `tourism-aggregated-data.json`
   - `tourism-personalized-recommend.json`
   - `tourism-ai-plan.json`
   - `tourism-dashboard.json`

5. 激活每个工作流（点击右上角开关变绿）

## 四、配置 API 密钥

在 n8n 中设置环境变量（Settings → Environment Variables）：

| 变量名 | 说明 | 获取方式 |
|--------|------|----------|
| WEATHER_API_KEY | 天气API密钥 | https://www.weatherapi.com 注册免费获取 |
| AMAP_API_KEY | 高德地图密钥 | https://lbs.amap.com 注册获取 |
| OPENAI_API_KEY | OpenAI密钥 | https://platform.openai.com（AI行程规划用） |

## 五、前端配置

在 `frontend/.env.local` 添加：

```env
VITE_N8N_URL=http://localhost:5678
```

## 六、工作流说明

### 1. 天气查询 (tourism-weather)
- **Webhook**: POST `/webhook/tourism/weather`
- **功能**: 批量查询景点天气
- **请求示例**:
```json
{
  "spots": ["西柏坡纪念馆", "狼牙山五壮士纪念地"]
}
```

### 2. 数据聚合 (tourism-aggregated-data)
- **Webhook**: POST `/webhook/tourism/aggregated-data`
- **功能**: 整合天气、交通、人流、票务数据
- **请求示例**:
```json
{
  "spotName": "西柏坡纪念馆"
}
```

### 3. 智能推荐 (tourism-personalized-recommend)
- **Webhook**: POST `/webhook/tourism/personalized-recommend`
- **功能**: 基于用户偏好推荐路线
- **请求示例**:
```json
{
  "preferences": {
    "interests": ["红色文化", "自然风光"],
    "travelStyle": "comfort",
    "groupType": "family"
  },
  "limit": 5
}
```

### 4. AI行程规划 (tourism-ai-plan)
- **Webhook**: POST `/webhook/tourism/ai-plan`
- **功能**: AI生成详细行程
- **请求示例**:
```json
{
  "spots": ["西柏坡纪念馆", "狼牙山"],
  "duration": 2,
  "preferences": {
    "pace": "moderate"
  }
}
```

### 5. 运营仪表盘 (tourism-dashboard)
- **Webhook**: POST `/webhook/tourism/dashboard`
- **功能**: 获取运营统计数据

## 七、测试工作流

### 使用 curl 测试

```bash
# 测试天气查询
curl -X POST http://localhost:5678/webhook/tourism/weather \
  -H "Content-Type: application/json" \
  -d '{"spots": ["西柏坡纪念馆"]}'

# 测试智能推荐
curl -X POST http://localhost:5678/webhook/tourism/personalized-recommend \
  -H "Content-Type: application/json" \
  -d '{"preferences": {"interests": ["红色文化"]}, "limit": 3}'
```

### 使用浏览器测试

打开 `test_n8n_api.html` 进行可视化测试。

## 八、常见问题

### Q: n8n 启动失败？
A: 检查端口 5678 是否被占用：`netstat -ano | findstr 5678`

### Q: Webhook 返回 404？
A: 确保工作流已激活（开关为绿色）

### Q: 天气API返回错误？
A: 检查 WEATHER_API_KEY 是否正确配置

### Q: 前端调用失败？
A: 检查 CORS 设置，n8n 默认允许跨域

## 九、扩展开发

如需添加新工作流：
1. 在 n8n 界面创建新工作流
2. 添加 Webhook 触发器节点
3. 添加处理逻辑节点
4. 导出 JSON 保存到 `n8n-workflows/` 目录
5. 在 `frontend/src/api/n8n.ts` 添加对应接口
