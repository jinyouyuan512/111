# 智慧旅游后端完善总结

## 新增 API 接口

### 1. 景点导览 API (`/api/tourism/spots`)
- `GET /api/tourism/spots/red` - 获取所有红色景点
- `GET /api/tourism/spots/{spotId}` - 获取景点详情
- `GET /api/tourism/spots/{spotId}/audio-guides` - 获取语音讲解列表
- `GET /api/tourism/spots/search` - 搜索景点

### 2. 智能路线 API (`/api/tourism/smart-route`)
- `GET /api/tourism/smart-route/hot` - 获取热门路线
- `POST /api/tourism/smart-route/generate` - AI生成行程规划
- `POST /api/tourism/smart-route/save` - 保存行程
- `GET /api/tourism/smart-route/my-plans` - 获取用户行程
- `POST /api/tourism/smart-route/optimize` - 路线优化

### 3. 实时信息 API (`/api/tourism/realtime`)
- `GET /api/tourism/realtime/weather` - 获取景点天气
- `GET /api/tourism/realtime/crowd` - 获取人流量
- `GET /api/tourism/realtime/tips` - 获取出行建议
- `GET /api/tourism/realtime/status/{spotId}` - 获取景区状态
- `GET /api/tourism/realtime/dashboard` - 获取仪表盘数据

### 4. 门票预订 API (`/api/tourism/tickets`)
- `GET /api/tourism/tickets` - 获取门票列表
- `GET /api/tourism/tickets/{ticketId}` - 获取门票详情
- `POST /api/tourism/tickets/book` - 预订门票
- `GET /api/tourism/tickets/orders` - 获取用户订单
- `POST /api/tourism/tickets/orders/{orderId}/cancel` - 取消订单
- `GET /api/tourism/tickets/{ticketId}/availability` - 检查可用性

### 5. AI助手 API (`/api/tourism/ai`)
- `POST /api/tourism/ai/chat` - AI智能问答

## 新增文件

### 后端
- `controller/SpotGuideController.java` - 景点导览控制器
- `controller/SmartRouteController.java` - 智能路线控制器
- `controller/RealtimeInfoController.java` - 实时信息控制器
- `controller/TicketController.java` - 门票预订控制器
- `service/SpotGuideService.java` - 景点导览服务
- `service/SmartRouteService.java` - 智能路线服务
- `service/RealtimeInfoService.java` - 实时信息服务
- `service/TicketService.java` - 门票服务
- `service/impl/*ServiceImpl.java` - 服务实现类
- `dto/*.java` - 数据传输对象
- `entity/AudioGuide.java` - 语音讲解实体
- `mapper/AudioGuideMapper.java` - 语音讲解Mapper
- `resources/db/schema_v2.sql` - 扩展表结构
- `resources/db/data_v2.sql` - 扩展数据

### 前端
- `api/tourism.ts` - 完整的旅游API接口
- `views/Tourism.vue` - 更新为使用后端API

## 数据库扩展

新增表：
- `audio_guide` - 语音讲解表
- `user_trip_plan` - 用户行程保存表
- `spot_realtime_status` - 景点实时状态表

## 启动方式

1. 启动后端服务：
```bash
cd backend/tourism-service
mvn spring-boot:run -DskipTests
```

2. 或使用批处理：
```
双击 RESTART_TOURISM_SERVICE.bat
```

3. 访问页面：
```
http://localhost:3001/tourism
```

## 架构说明

```
前端 Tourism.vue
    ↓
前端 API (tourism.ts)
    ↓
后端 tourism-service (端口 8086)
    ↓
MySQL 数据库 (jiyi_tourism)
```

前端会优先调用后端API，如果后端不可用则使用内置的默认数据。
