# 景区智慧导览与LBS服务 (Guide Service)

## 概述

景区智慧导览与LBS服务是冀忆红途平台的核心模块之一，提供基于位置的智能导览功能，包括景区识别、景点讲解、AR场景、路线导航、游览轨迹记录和游记生成等功能。

## 功能特性

### 1. LBS定位与景区识别
- 自动检测用户位置
- 识别附近的红色景区
- 加载景区地图和景点信息
- 支持5公里范围内的景区识别

### 2. 景点智能讲解
- 基于距离的自动触发
- 多语言语音讲解（中文/英文）
- 景点详细信息展示
- 历史文物和人物故事

### 3. AR场景体验
- 二维码扫描触发
- AR历史场景重现
- 沉浸式互动体验
- 支持多个景点的AR内容

### 4. 导览路线规划
- 景区内路线推荐
- 最优路径规划
- 距离和时长估算
- 难度等级标注

### 5. 游览轨迹记录
- 实时位置追踪
- 轨迹点自动记录
- 游览时长统计
- 轨迹地图生成

### 6. 游记自动生成
- 基于游览数据生成
- 包含轨迹地图
- 支持图片上传
- 个人游记管理

## 技术架构

### 后端技术栈
- **框架**: Spring Boot 3.x
- **数据库**: MySQL 8.0
- **缓存**: Redis 7.0
- **ORM**: MyBatis Plus
- **地图服务**: 高德地图API（预留接口）

### 数据模型

#### 景区表 (scenic_area)
- 景区基本信息
- 地理坐标
- 边界数据
- 地图数据

#### 景点表 (poi)
- 景点信息
- 位置坐标
- 触发半径
- 二维码标识

#### 语音讲解表 (audio_guide)
- 讲解内容
- 音频文件
- 多语言支持

#### AR场景表 (ar_scene)
- 场景数据
- 预览图片
- 关联景点

#### 导览路线表 (guide_route)
- 路线信息
- 路径数据
- 难度等级

#### 游览记录表 (user_visit)
- 用户游览
- 时间记录
- 状态管理

#### 轨迹表 (visit_track)
- 位置点记录
- 时间戳

#### 游记表 (travel_log)
- 游记内容
- 图片集合
- 轨迹地图

## API接口

### 1. 位置检测
```
POST /api/guide/detect-location
请求体: { latitude, longitude }
返回: ScenicAreaVO（景区信息及景点列表）
```

### 2. 获取景点信息
```
GET /api/guide/poi/{poiId}
返回: PoiVO（景点详细信息）
```

### 3. 触发语音讲解
```
GET /api/guide/audio-guide/{poiId}?language=zh
返回: AudioGuideVO（语音讲解信息）
```

### 4. 加载AR场景
```
GET /api/guide/ar-scene/{qrCode}
返回: ArSceneVO（AR场景数据）
```

### 5. 获取导览路线
```
GET /api/guide/routes/{scenicAreaId}
返回: List<GuideRouteVO>（路线列表）
```

### 6. 规划导航
```
POST /api/guide/navigation
请求体: { startLatitude, startLongitude, targetPoiId }
返回: 导航路径JSON
```

### 7. 开始游览
```
POST /api/guide/visit/start?userId={userId}&scenicAreaId={scenicAreaId}
返回: visitId（游览记录ID）
```

### 8. 记录轨迹点
```
POST /api/guide/visit/track
请求体: { visitId, latitude, longitude }
```

### 9. 结束游览
```
POST /api/guide/visit/end/{visitId}
```

### 10. 生成游记
```
POST /api/guide/travel-log/generate?userId={userId}&visitId={visitId}
返回: TravelLogVO（游记信息）
```

### 11. 获取用户游记
```
GET /api/guide/travel-log/user/{userId}
返回: List<TravelLogVO>（游记列表）
```

## 核心算法

### 距离计算
使用Haversine公式计算两个地理坐标之间的距离：

```java
private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
               Math.sin(dLon / 2) * Math.sin(dLon / 2);
    
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    
    return EARTH_RADIUS * c; // 返回公里
}
```

### 景区识别逻辑
1. 查询所有开放的景区
2. 计算用户位置到各景区的距离
3. 选择距离最近且在5公里范围内的景区
4. 加载景区信息和景点列表

### 自动触发机制
- 当用户距离景点小于触发半径时自动触发
- 触发半径默认为50米，可配置
- 支持多个景点的连续触发

## 示例数据

系统预置了以下红色景区数据：

1. **西柏坡纪念馆** - 解放战争指挥中心
   - 中共中央旧址
   - 七届二中全会会址
   - 西柏坡纪念碑
   - 西柏坡陈列馆

2. **狼牙山风景区** - 五壮士英勇跳崖地
   - 狼牙山五壮士纪念塔
   - 五壮士陈列馆
   - 棋盘陀

3. **冉庄地道战遗址** - 地道战典型代表
   - 地道战遗址
   - 地道战纪念馆
   - 地道战体验区

4. **李大钊纪念馆** - 共产主义先驱纪念地
   - 李大钊纪念碑
   - 李大钊生平陈列馆
   - 李大钊故居

## 部署说明

### 数据库初始化
```bash
# 执行schema.sql创建表结构
mysql -u root -p jiyi_guide < src/main/resources/db/schema.sql

# 执行data.sql插入示例数据
mysql -u root -p jiyi_guide < src/main/resources/db/data.sql
```

### 配置文件
修改 `application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/jiyi_guide
    username: root
    password: your_password
```

### 启动服务
```bash
# 编译
mvn clean package

# 运行
java -jar target/guide-service-1.0.0.jar
```

服务将在 `http://localhost:8085` 启动

## 前端集成

### API调用示例
```typescript
import * as guideApi from '@/api/guide'

// 检测位置
const response = await guideApi.detectLocation({
  latitude: 38.3167,
  longitude: 114.1667
})

// 触发语音讲解
const audio = await guideApi.triggerAudioGuide(poiId, 'zh')

// 加载AR场景
const arScene = await guideApi.loadArScene(qrCode)
```

### 游览流程
1. 用户进入景区 → 调用 `detectLocation` 识别景区
2. 系统提示开始游览 → 调用 `startVisit` 创建游览记录
3. 用户移动 → 定期调用 `recordTrackPoint` 记录轨迹
4. 用户靠近景点 → 自动触发 `triggerAudioGuide` 播放讲解
5. 用户扫描二维码 → 调用 `loadArScene` 加载AR场景
6. 用户结束游览 → 调用 `endVisit` 结束记录
7. 生成游记 → 调用 `generateTravelLog` 生成游记

## 扩展功能

### 高德地图集成（待实现）
- 真实地图显示
- 路径规划
- 导航功能
- 实时路况

### 语音识别（待实现）
- 语音提问
- 智能问答
- 语音导览

### 社交分享（待实现）
- 游记分享
- 景点打卡
- 好友互动

## 性能优化

1. **缓存策略**
   - 景区信息缓存（Redis）
   - 景点数据缓存
   - 语音文件CDN加速

2. **数据库优化**
   - 地理位置索引
   - 查询优化
   - 分页加载

3. **并发处理**
   - 异步轨迹记录
   - 批量数据处理
   - 连接池优化

## 监控与日志

- 接口调用统计
- 响应时间监控
- 错误日志记录
- 用户行为分析

## 安全考虑

- 位置信息加密
- 用户隐私保护
- API访问限流
- 数据备份策略

## 联系方式

如有问题或建议，请联系开发团队。
