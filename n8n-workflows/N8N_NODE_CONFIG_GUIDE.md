# n8n 节点配置详细指南

本指南详细说明每个工作流中各节点的配置方法。

---

## 工作流1：天气查询 (tourism-weather)

### 节点流程图
```
[Webhook] → [Code: 映射景点] → [HTTP Request: 天气API] → [Code: 格式化] → [Respond]
```

### 节点1：Webhook 天气查询

**节点类型**: Webhook

**配置项**:
| 参数 | 值 | 说明 |
|------|-----|------|
| HTTP Method | POST | 接收POST请求 |
| Path | `tourism/weather` | Webhook路径 |
| Response Mode | Response Node | 由后续节点返回响应 |

**完整URL**: `http://localhost:5678/webhook/tourism/weather`

---

### 节点2：映射景点城市

**节点类型**: Code (JavaScript)

**配置项**:
- Language: JavaScript
- Mode: Run Once for All Items

**代码内容**:
```javascript
// 获取请求的景点列表
const spots = $input.first().json.spots || [];
const date = $input.first().json.date || new Date().toISOString().split('T')[0];

// 景点对应的城市（用于天气API查询）
const spotCityMap = {
  '西柏坡纪念馆': '石家庄',
  '狼牙山五壮士纪念地': '保定',
  '冉庄地道战遗址': '保定',
  '李大钊纪念馆': '唐山',
  '白洋淀雁翎队纪念馆': '保定',
  '塞罕坝展览馆': '承德',
  '八路军一二九师司令部': '邯郸',
  '石家庄解放纪念馆': '石家庄'
};

return spots.map(spot => ({
  spotName: spot,
  city: spotCityMap[spot] || '石家庄',
  date
}));
```

---

### 节点3：调用天气API

**节点类型**: HTTP Request

**配置项**:
| 参数 | 值 |
|------|-----|
| Method | GET |
| URL | `https://api.weatherapi.com/v1/forecast.json` |

**Query Parameters** (点击 Add Parameter):
| Name | Value |
|------|-------|
| key | `{{ $env.WEATHER_API_KEY }}` 或直接填你的API密钥 |
| q | `{{ $json.city }}` |
| days | `3` |
| lang | `zh` |

**Options**:
- Continue On Fail: ✅ 开启（API失败时继续执行）

**获取免费API密钥**: https://www.weatherapi.com/signup.aspx

---

### 节点4：格式化天气数据

**节点类型**: Code (JavaScript)

**代码内容**:
```javascript
const items = $input.all();
const weather = [];

for (const item of items) {
  const spotName = item.json.spotName;
  const apiData = item.json;
  
  if (apiData.forecast) {
    const today = apiData.forecast.forecastday[0];
    weather.push({
      spotName,
      date: today.date,
      condition: today.day.condition.text,
      temperature: {
        min: Math.round(today.day.mintemp_c),
        max: Math.round(today.day.maxtemp_c)
      },
      humidity: today.day.avghumidity,
      suggestion: getSuggestion(today.day.condition.text, today.day.maxtemp_c)
    });
  } else {
    // 降级：返回模拟数据
    weather.push({
      spotName,
      date: new Date().toISOString().split('T')[0],
      condition: '晴',
      temperature: { min: 3, max: 12 },
      humidity: 45,
      suggestion: '天气适宜出行，建议携带外套'
    });
  }
}

function getSuggestion(condition, maxTemp) {
  if (condition.includes('雨')) return '建议携带雨具，注意防滑';
  if (condition.includes('雪')) return '道路可能湿滑，注意保暖和安全';
  if (maxTemp > 30) return '天气炎热，注意防暑降温';
  if (maxTemp < 5) return '天气寒冷，注意保暖';
  return '天气适宜出行，祝旅途愉快';
}

return { weather };
```

---

### 节点5：返回天气响应

**节点类型**: Respond to Webhook

**配置项**:
| 参数 | 值 |
|------|-----|
| Respond With | JSON |
| Response Body | `{{ $json }}` |

---

## 工作流2：数据聚合 (tourism-aggregated-data)

### 节点流程图
```
[Webhook] → [Code: 生成模拟数据] → [Respond]
```

### 节点1：Webhook

| 参数 | 值 |
|------|-----|
| HTTP Method | POST |
| Path | `tourism/aggregated-data` |
| Response Mode | Response Node |

### 节点2：生成聚合数据 (Code)

```javascript
const spotName = $input.first().json.spotName || '西柏坡纪念馆';

// 生成综合数据
const result = {
  spotName,
  weather: {
    condition: '晴',
    temperature: { min: 3, max: 12 },
    humidity: 45,
    aqi: 65,
    uvIndex: 3
  },
  traffic: {
    congestionLevel: 'low',
    estimatedDriveTime: '约1小时',
    parkingAvailable: true,
    publicTransport: ['公交专线', '旅游大巴']
  },
  crowd: {
    currentLevel: 'medium',
    waitTime: 15,
    bestVisitTime: ['9:00-10:00', '14:00-15:00'],
    peakHours: ['10:00-12:00', '14:00-16:00']
  },
  ticket: {
    price: 0,
    needReservation: true,
    availableSlots: 500,
    bookingUrl: 'https://example.com/booking'
  },
  alerts: ['当前人流适中，适合参观']
};

return result;
```

### 节点3：Respond to Webhook

| 参数 | 值 |
|------|-----|
| Respond With | JSON |
| Response Body | `{{ $json }}` |

---

## 工作流3：智能推荐 (tourism-personalized-recommend)

### 节点流程图
```
[Webhook] → [Code: 推荐算法] → [Respond]
```

### 节点2：推荐算法 (Code)

```javascript
const preferences = $input.first().json.preferences || {};
const limit = $input.first().json.limit || 5;

// 路线数据库
const routes = [
  {
    id: 'route-1',
    title: '西柏坡红色经典之旅',
    tags: ['红色文化', '历史遗迹'],
    style: 'comfort',
    groupType: ['family', 'group'],
    spots: ['西柏坡纪念馆', '中共中央旧址'],
    duration: '1天',
    distance: 120,
    difficulty: '轻松'
  },
  {
    id: 'route-2',
    title: '狼牙山英雄之路',
    tags: ['红色文化', '自然风光', '登山'],
    style: 'intensive',
    groupType: ['couple', 'solo'],
    spots: ['狼牙山五壮士纪念地', '狼牙山景区'],
    duration: '1天',
    distance: 150,
    difficulty: '中等'
  },
  {
    id: 'route-3',
    title: '白洋淀红色水乡游',
    tags: ['红色文化', '自然风光', '水乡'],
    style: 'relaxed',
    groupType: ['family', 'couple'],
    spots: ['白洋淀雁翎队纪念馆', '白洋淀景区'],
    duration: '2天',
    distance: 180,
    difficulty: '轻松'
  }
];

// 计算匹配度
function calculateScore(route, prefs) {
  let score = 50; // 基础分
  
  // 兴趣匹配
  if (prefs.interests) {
    for (const interest of prefs.interests) {
      if (route.tags.includes(interest)) score += 15;
    }
  }
  
  // 风格匹配
  if (prefs.travelStyle && route.style === prefs.travelStyle) {
    score += 10;
  }
  
  // 人群匹配
  if (prefs.groupType && route.groupType.includes(prefs.groupType)) {
    score += 10;
  }
  
  return Math.min(score, 100);
}

// 生成推荐
const recommendations = routes
  .map(route => ({
    id: route.id,
    title: route.title,
    matchScore: calculateScore(route, preferences),
    matchReasons: generateReasons(route, preferences),
    route: {
      spots: route.spots,
      duration: route.duration,
      distance: route.distance,
      difficulty: route.difficulty
    },
    highlights: route.tags,
    estimatedCost: {
      transportation: 100,
      tickets: 0,
      meals: 150,
      total: 250
    },
    tags: route.tags
  }))
  .sort((a, b) => b.matchScore - a.matchScore)
  .slice(0, limit);

function generateReasons(route, prefs) {
  const reasons = [];
  if (prefs.interests) {
    const matched = prefs.interests.filter(i => route.tags.includes(i));
    if (matched.length) reasons.push(`符合${matched.join('、')}兴趣`);
  }
  if (prefs.groupType && route.groupType.includes(prefs.groupType)) {
    reasons.push('适合您的出行方式');
  }
  return reasons.length ? reasons : ['热门推荐路线'];
}

return { recommendations };
```

---

## 工作流4：AI行程规划 (tourism-ai-plan)

### 节点流程图
```
[Webhook] → [Code: 生成行程] → [Respond]
```

### 节点2：生成行程 (Code)

```javascript
const spots = $input.first().json.spots || ['西柏坡纪念馆'];
const duration = $input.first().json.duration || 1;
const preferences = $input.first().json.preferences || {};

// 生成行程
const days = [];
for (let i = 0; i < duration; i++) {
  const daySpots = spots.slice(i * 2, (i + 1) * 2);
  if (daySpots.length === 0) break;
  
  days.push({
    day: i + 1,
    date: new Date(Date.now() + i * 86400000).toISOString().split('T')[0],
    spots: daySpots.map((spot, idx) => ({
      name: spot,
      duration: '2-3小时',
      tips: '建议上午参观，人流较少',
      order: idx + 1
    })),
    meals: ['当地特色早餐', '景区附近午餐', '市区晚餐'],
    accommodation: i < duration - 1 ? '推荐入住景区附近酒店' : null
  });
}

const plan = {
  title: `${spots[0]}${duration}日游`,
  description: `精心规划的${duration}天行程，涵盖${spots.length}个景点`,
  days,
  totalDistance: spots.length * 50,
  estimatedCost: spots.length * 200 + duration * 300,
  tips: [
    '建议提前预约景点门票',
    '携带身份证件',
    '穿着舒适的步行鞋'
  ]
};

return { success: true, plan };
```

---

## 工作流5：运营仪表盘 (tourism-dashboard)

### 节点2：生成仪表盘数据 (Code)

```javascript
const dashboard = {
  todayVisitors: Math.floor(Math.random() * 5000) + 3000,
  activeUsers: Math.floor(Math.random() * 500) + 200,
  totalBookings: Math.floor(Math.random() * 200) + 100,
  popularSpots: [
    { name: '西柏坡纪念馆', visits: 1250 },
    { name: '狼牙山五壮士纪念地', visits: 980 },
    { name: '白洋淀雁翎队纪念馆', visits: 756 },
    { name: '冉庄地道战遗址', visits: 623 }
  ],
  recentAlerts: [
    { type: 'info', message: '西柏坡纪念馆今日预约已满80%', time: '10:30' },
    { type: 'warning', message: '狼牙山景区下午可能有阵雨', time: '09:15' }
  ],
  systemHealth: {
    weatherApi: 'ok',
    trafficApi: 'ok',
    aiService: 'ok',
    database: 'ok'
  }
};

return dashboard;
```

---

## 手动创建工作流步骤

如果导入失败，可以手动创建：

### 步骤1：创建新工作流
1. 点击 **Workflows** → **Add Workflow**
2. 输入工作流名称

### 步骤2：添加 Webhook 节点
1. 点击 **+** 添加节点
2. 搜索 **Webhook**
3. 配置 HTTP Method 和 Path

### 步骤3：添加 Code 节点
1. 点击 Webhook 节点右侧的 **+**
2. 搜索 **Code**
3. 粘贴对应的 JavaScript 代码

### 步骤4：添加 Respond to Webhook 节点
1. 点击 Code 节点右侧的 **+**
2. 搜索 **Respond to Webhook**
3. 配置 Response Body 为 `{{ $json }}`

### 步骤5：连接节点
确保节点按顺序连接：Webhook → Code → Respond

### 步骤6：激活工作流
点击右上角开关，变成绿色

---

## 常见配置问题

### Q: Code 节点报错？
A: 检查 JavaScript 语法，确保使用 `$input.first().json` 获取输入

### Q: HTTP Request 节点失败？
A: 检查 URL 和参数，开启 "Continue On Fail" 选项

### Q: Webhook 返回空？
A: 确保 Respond to Webhook 节点的 Response Body 配置正确

### Q: 如何调试？
A: 点击节点，选择 "Execute Node" 单独测试
