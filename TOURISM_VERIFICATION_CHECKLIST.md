# 旅游服务智能功能验证清单

## 快速验证步骤

### 前置条件
- [ ] MySQL 数据库已启动
- [ ] Redis 已启动
- [ ] 数据库已初始化（运行 schema.sql 和 data.sql）

### 1. 启动服务

```bash
cd backend/tourism-service
mvn spring-boot:run
```

等待服务启动完成，看到类似输出：
```
Started TourismServiceApplication in X.XXX seconds
```

### 2. 验证智能推荐功能

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

**预期结果**:
- 返回路线列表
- 每条路线包含 `recommendationScore` 字段
- 路线按推荐分数降序排列

### 3. 验证路线优化功能

```bash
# 最短距离策略
curl -X POST "http://localhost:8084/api/tourism/routes/1/optimize?strategy=shortest_distance"

# 最低费用策略
curl -X POST "http://localhost:8084/api/tourism/routes/1/optimize?strategy=lowest_cost"
```

**预期结果**:
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

### 4. 验证电子行程单生成

**步骤1**: 创建行程
```bash
curl -X POST http://localhost:8084/api/tourism/itineraries \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1" \
  -d '{
    "routeId": 1,
    "title": "我的红色之旅",
    "startDate": "2026-01-10",
    "endDate": "2026-01-12",
    "notes": "期待这次旅行"
  }'
```

记录返回的行程ID（假设为1）

**步骤2**: 生成电子行程单
```bash
curl http://localhost:8084/api/tourism/itineraries/1/electronic
```

**预期结果**:
- 包含行程编号
- 包含每日详细计划
- 每日计划包含景点、用餐、住宿
- 包含重要提示列表
- 包含二维码

### 5. 验证天气预报功能

```bash
curl "http://localhost:8084/api/tourism/attractions/1/weather?days=5"
```

**预期结果**:
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

### 6. 验证行程调整推送

```bash
curl -X POST "http://localhost:8084/api/tourism/itineraries/1/adjustment?reason=明天有大雨预警&suggestion=建议将行程推迟一天"
```

**预期结果**:
- 返回成功状态
- 在数据库 `weather_alert` 表中创建预警记录

### 7. 验证前端API

在前端项目中测试：

```typescript
import { 
  recommendRoutes,
  optimizeRoute,
  generateElectronicItinerary,
  getWeatherForecast,
  pushItineraryAdjustment
} from '@/api/tourism'

// 测试推荐
const routes = await recommendRoutes({
  days: 3,
  budget: 1500,
  interests: ['红色教育']
})
console.log('推荐路线:', routes)

// 测试优化
const optimization = await optimizeRoute(1, 'shortest_distance')
console.log('优化结果:', optimization)

// 测试电子行程单
const itinerary = await generateElectronicItinerary(1)
console.log('电子行程单:', itinerary)

// 测试天气
const weather = await getWeatherForecast(1, 5)
console.log('天气预报:', weather)
```

## 功能验证清单

### 智能推荐算法
- [ ] 能够根据用户偏好返回路线
- [ ] 返回的路线包含推荐分数
- [ ] 路线按推荐分数排序
- [ ] 兴趣匹配影响推荐结果
- [ ] 预算匹配影响推荐结果

### 路径优化
- [ ] 最短距离策略正常工作
- [ ] 最低费用策略正常工作
- [ ] 返回优化后的景点顺序
- [ ] 正确计算总距离
- [ ] 正确计算总时长
- [ ] 正确计算总费用

### 电子行程单
- [ ] 能够生成唯一的行程编号
- [ ] 包含完整的基本信息
- [ ] 每日计划包含景点活动
- [ ] 每日计划包含用餐安排
- [ ] 每日计划包含住宿信息
- [ ] 包含重要提示列表
- [ ] 生成二维码标识

### 天气预报
- [ ] 能够查询指定天数的天气
- [ ] 返回完整的天气信息
- [ ] 包含游览建议
- [ ] 支持自定义查询天数

### 行程调整推送
- [ ] 能够推送调整建议
- [ ] 创建预警记录
- [ ] 记录调整原因和建议

## 数据库验证

### 检查预警记录
```sql
SELECT * FROM weather_alert ORDER BY created_at DESC LIMIT 5;
```

### 检查行程记录
```sql
SELECT * FROM user_itinerary ORDER BY created_at DESC LIMIT 5;
```

### 检查路线数据
```sql
SELECT id, name, booking_count, view_count FROM route WHERE status = 'active';
```

## 性能测试

### 推荐算法性能
```bash
# 使用 Apache Bench 测试
ab -n 100 -c 10 -p recommend.json -T application/json \
  http://localhost:8084/api/tourism/routes/recommend
```

**预期**: 响应时间 < 200ms

### 路线优化性能
```bash
ab -n 100 -c 10 -m POST \
  "http://localhost:8084/api/tourism/routes/1/optimize?strategy=shortest_distance"
```

**预期**: 响应时间 < 500ms

## 常见问题排查

### 问题1: 服务启动失败
- 检查 MySQL 是否启动
- 检查 Redis 是否启动
- 检查数据库连接配置
- 检查端口 8084 是否被占用

### 问题2: 推荐结果为空
- 检查数据库中是否有路线数据
- 检查路线状态是否为 'active'
- 检查偏好条件是否过于严格

### 问题3: 优化失败
- 检查路线是否存在
- 检查路线是否包含景点
- 检查景点是否有经纬度数据

### 问题4: 电子行程单生成失败
- 检查行程是否存在
- 检查关联的路线是否存在
- 检查路线是否包含景点和住宿数据

## 验证完成标准

所有以下条件都满足时，验证通过：

✅ 服务正常启动，无错误日志
✅ 所有API接口返回正确的数据结构
✅ 智能推荐返回有效的推荐分数
✅ 路径优化计算出合理的结果
✅ 电子行程单包含完整信息
✅ 天气预报返回多天数据
✅ 行程调整推送创建预警记录
✅ 前端API调用成功
✅ 数据库记录正确创建

## 下一步

验证通过后，可以：
1. 集成到前端页面
2. 添加更多测试用例
3. 优化推荐算法权重
4. 接入真实天气API
5. 集成推送服务
