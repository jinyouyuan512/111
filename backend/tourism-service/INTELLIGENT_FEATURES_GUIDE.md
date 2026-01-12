# 旅游服务智能功能使用指南

## 功能概述

本次更新为旅游服务添加了以下智能功能：

### 1. 智能路线推荐算法
基于用户偏好的个性化路线推荐，综合考虑：
- 兴趣匹配度 (40%)
- 预算匹配度 (30%)
- 路线热度 (20%)
- 天数匹配度 (10%)

### 2. 路径优化和费用计算
支持多种优化策略：
- **最短距离策略**: 使用贪心算法优化景点游览顺序，减少路程
- **最低费用策略**: 按门票价格排序，优先游览低价景点
- 自动计算总距离、总时长和总费用

### 3. 电子行程单生成
自动生成详细的电子行程单，包含：
- 每日详细计划（景点、用餐、交通）
- 住宿安排
- 费用明细
- 重要提示
- 二维码（用于快速查看）

### 4. 天气预报集成
- 获取景点未来多天天气预报
- 提供游览建议
- 支持自定义查询天数

### 5. 行程调整推送
- 根据天气变化推送调整建议
- 景区状态变化通知
- 自动创建预警记录

## API 接口说明

### 1. 智能推荐路线
```http
POST /api/tourism/routes/recommend
Content-Type: application/json

{
  "startLocation": "石家庄",
  "days": 3,
  "budget": 1500,
  "interests": ["红色教育", "历史文化"],
  "difficulty": "easy",
  "season": "春季"
}
```

**响应**: 返回按推荐分数排序的路线列表，每条路线包含 `recommendationScore` 字段

### 2. 优化路线
```http
POST /api/tourism/routes/{routeId}/optimize?strategy=shortest_distance
```

**策略选项**:
- `shortest_distance`: 最短距离
- `lowest_cost`: 最低费用

**响应**:
```json
{
  "code": 200,
  "data": {
    "optimizedAttractionOrder": [1, 3, 2, 4],
    "totalDistance": 125.50,
    "totalDuration": 480,
    "totalCost": 280.00,
    "optimizationStrategy": "shortest_distance"
  }
}
```

### 3. 生成电子行程单
```http
GET /api/tourism/itineraries/{itineraryId}/electronic
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "itineraryNo": "IT12345...",
    "title": "我的红色之旅",
    "startDate": "2026-01-10",
    "endDate": "2026-01-12",
    "totalDays": 3,
    "totalCost": 1200.00,
    "dailyPlans": [
      {
        "dayNum": 1,
        "date": "2026-01-10",
        "activities": [
          {
            "time": "09:00-12:00",
            "type": "attraction",
            "name": "西柏坡纪念馆",
            "location": "河北省石家庄市平山县西柏坡镇",
            "cost": 0.00,
            "notes": "上午参观西柏坡纪念馆主展区"
          },
          {
            "time": "12:00-13:00",
            "type": "meal",
            "name": "午餐",
            "location": "景区附近餐厅",
            "cost": 50.00,
            "notes": "建议品尝当地特色美食"
          }
        ],
        "accommodation": "西柏坡红色文化酒店 - 300-500元",
        "dailyCost": 350.00
      }
    ],
    "importantNotes": [
      "请提前预约景区门票，避免排队等候",
      "建议携带身份证件，部分景区需要实名登记"
    ],
    "qrCode": "QR_IT12345..."
  }
}
```

### 4. 获取天气预报
```http
GET /api/tourism/attractions/{attractionId}/weather?days=5
```

**响应**:
```json
{
  "code": 200,
  "data": [
    {
      "date": "2026-01-02",
      "weather": "晴",
      "temperature": "18-25℃",
      "windDirection": "东南风",
      "windPower": "3-4级",
      "humidity": "60%",
      "suggestion": "天气适宜游览，注意防晒"
    }
  ]
}
```

### 5. 推送行程调整建议
```http
POST /api/tourism/itineraries/{itineraryId}/adjustment
  ?reason=明天有大雨预警
  &suggestion=建议将行程推迟一天，或调整为室内景点
```

## 使用示例

### 场景1: 用户规划旅游
1. 用户填写旅游偏好（天数、预算、兴趣）
2. 调用智能推荐接口获取个性化路线
3. 用户选择心仪路线
4. 调用路线优化接口，选择最短距离策略
5. 保存行程
6. 生成电子行程单

### 场景2: 查看天气并调整行程
1. 用户查看已保存的行程
2. 调用天气预报接口查看未来天气
3. 如果天气不佳，系统自动推送调整建议
4. 用户根据建议修改行程

## 核心算法说明

### 推荐分数计算
```java
score = 兴趣匹配度 * 0.4 
      + 预算匹配度 * 0.3 
      + 热度分数 * 0.2 
      + 天数匹配度 * 0.1
```

### 最短距离优化（贪心算法）
1. 从第一个景点开始
2. 每次选择距离当前景点最近的未访问景点
3. 重复直到所有景点都被访问
4. 使用 Haversine 公式计算地理距离

### 距离计算公式
```
R = 6371 km (地球半径)
dLat = lat2 - lat1
dLon = lon2 - lon1
a = sin²(dLat/2) + cos(lat1) * cos(lat2) * sin²(dLon/2)
c = 2 * atan2(√a, √(1-a))
distance = R * c
```

## 前端集成

前端 API 已更新，新增以下方法：

```typescript
import { 
  optimizeRoute, 
  generateElectronicItinerary,
  getWeatherForecast,
  pushItineraryAdjustment 
} from '@/api/tourism'

// 优化路线
const result = await optimizeRoute(routeId, 'shortest_distance')

// 生成电子行程单
const itinerary = await generateElectronicItinerary(itineraryId)

// 获取天气预报
const weather = await getWeatherForecast(attractionId, 5)

// 推送调整建议
await pushItineraryAdjustment(itineraryId, reason, suggestion)
```

## 测试建议

### 手动测试步骤

1. **启动服务**
```bash
cd backend/tourism-service
mvn spring-boot:run
```

2. **测试智能推荐**
```bash
curl -X POST http://localhost:8084/api/tourism/routes/recommend \
  -H "Content-Type: application/json" \
  -d '{
    "days": 3,
    "budget": 1500,
    "interests": ["红色教育", "历史文化"],
    "difficulty": "easy",
    "season": "春季"
  }'
```

3. **测试路线优化**
```bash
curl -X POST "http://localhost:8084/api/tourism/routes/1/optimize?strategy=shortest_distance"
```

4. **测试电子行程单**
```bash
# 先创建行程
curl -X POST http://localhost:8084/api/tourism/itineraries \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1" \
  -d '{
    "routeId": 1,
    "title": "我的红色之旅",
    "startDate": "2026-01-10",
    "endDate": "2026-01-12"
  }'

# 生成电子行程单（假设返回的行程ID为1）
curl http://localhost:8084/api/tourism/itineraries/1/electronic
```

5. **测试天气预报**
```bash
curl "http://localhost:8084/api/tourism/attractions/1/weather?days=5"
```

## 性能优化建议

1. **缓存推荐结果**: 对于相同的偏好，可以缓存推荐结果
2. **异步处理**: 电子行程单生成可以异步处理
3. **天气API限流**: 实际集成天气API时需要注意调用频率限制
4. **路线优化**: 对于大量景点，可以使用更高效的算法（如动态规划）

## 未来扩展

1. **机器学习推荐**: 基于用户历史行为训练推荐模型
2. **实时天气集成**: 接入高德/百度天气API
3. **智能调度**: 考虑景区实时人流量动态调整行程
4. **社交推荐**: 基于好友的旅游记录推荐路线
5. **AR导航**: 集成AR技术提供实景导航

## 注意事项

1. 当前天气数据为模拟数据，生产环境需要接入真实天气API
2. 推荐算法可以根据实际数据调整权重
3. 路线优化算法适用于中小规模景点（<20个），大规模需要优化
4. 电子行程单的二维码生成需要集成二维码库
5. 推送功能需要集成消息推送服务（如极光推送）
