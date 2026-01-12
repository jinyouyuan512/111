# Unreal Engine Pixel Streaming 完整实施指南

## 概述

Pixel Streaming是UE5的一项技术，允许在服务器上运行高质量的UE应用，通过WebRTC将渲染结果以视频流的形式传输到浏览器，用户可以实时交互。

## 核心原理

```
用户浏览器 (HTML5)
    ↕ WebRTC (视频流 + 输入指令)
Signaling Server (信令服务器)
    ↕
Pixel Streaming Server (GPU服务器)
    ↕
Unreal Engine 应用实例
    ↕
后端API (数据交互)
```

**工作流程**：
1. 用户打开浏览器访问Web页面
2. 信令服务器建立WebRTC连接
3. UE应用在GPU服务器上渲染场景
4. 渲染结果编码为H.264视频流
5. 通过WebRTC传输到用户浏览器
6. 用户的鼠标/键盘输入回传到UE应用
7. UE应用响应输入并更新画面

---

## 第一部分：环境准备

### 1.1 硬件要求

**GPU服务器配置**：
```
CPU: Intel Xeon / AMD EPYC (8核+)
GPU: NVIDIA RTX 3060+ / Tesla T4+ (推荐RTX 4090)
内存: 32GB+ (推荐64GB)
存储: SSD 500GB+
网络: 100Mbps+ 上行带宽
```

**云服务器推荐**：
- AWS: g4dn.xlarge / g5.xlarge
- 阿里云: ecs.gn6i-c4g1.xlarge
- 腾讯云: GN7.2XLARGE32
- Azure: NC6s_v3

**成本估算**：
- AWS g4dn.xlarge: ~$0.526/小时 (~$380/月)
- 阿里云GPU实例: ~¥3000-8000/月


### 1.2 软件要求

**开发环境**：
```
操作系统: Windows 10/11 或 Linux (Ubuntu 20.04+)
Unreal Engine: 5.3+ (推荐5.4)
Visual Studio: 2022 (Windows)
Node.js: 18+
Python: 3.9+
```

**服务器环境**：
```
操作系统: Windows Server 2019+ 或 Linux
NVIDIA驱动: 最新版本
CUDA: 11.8+
Docker: 可选，用于容器化部署
```

---

## 第二部分：UE项目配置

### 2.1 创建UE项目

1. **启动Unreal Engine 5**
2. **创建新项目**：
   - 模板：空白项目 或 建筑可视化
   - 项目设置：
     - Blueprint 或 C++
     - 目标平台：Desktop
     - 质量预设：最高
     - 启用光线追踪：是（可选）

3. **项目命名**：`JiyiRedRoute_Experience`

### 2.2 启用Pixel Streaming插件

**步骤**：
1. 编辑 → 插件 (Edit → Plugins)
2. 搜索 "Pixel Streaming"
3. 勾选启用
4. 重启编辑器

**验证插件**：
```
项目设置 → 插件 → Pixel Streaming
确认插件已启用
```


### 2.3 配置项目设置

**编辑 → 项目设置**：

**1. 渲染设置**：
```
引擎 → 渲染
- 默认RHI: DirectX 12 (Windows) / Vulkan (Linux)
- 抗锯齿方法: TSR (Temporal Super Resolution)
- 动态全局光照: Lumen
- 反射: Lumen
- 阴影: Virtual Shadow Maps
```

**2. Pixel Streaming设置**：
```
插件 → Pixel Streaming
- 编码器: NVIDIA NVENC (GPU编码)
- 分辨率: 1920x1080
- 帧率: 60 FPS
- 比特率: 20000 kbps
- 编码质量: 高
```

**3. 输入设置**：
```
引擎 → 输入
- 启用鼠标捕获: 是
- 启用触摸输入: 是
```

### 2.4 创建场景内容

**导入3D模型**：
```
1. 准备GLTF/FBX模型
2. 导入到Content Browser
3. 设置材质和纹理
4. 放置到关卡中
```

**示例：西柏坡纪念馆场景**：
```
场景结构：
├── 建筑主体 (Static Mesh)
├── 室内陈设 (Static Mesh)
├── 历史文物 (Static Mesh + 交互)
├── 光照系统 (Directional Light + Sky Light)
├── 后处理 (Post Process Volume)
└── 交互点 (Blueprint Actors)
```


### 2.5 创建交互蓝图

**交互点Actor蓝图**：

```blueprint
// BP_InteractionPoint.uasset

组件：
- Static Mesh (球体标记)
- Widget Component (信息面板)
- Sphere Collision (触发器)

事件图：
- Event BeginPlay
  → 设置初始状态
  
- OnComponentBeginOverlap
  → 显示信息面板
  → 发送事件到前端
  
- OnComponentEndOverlap
  → 隐藏信息面板
  
- OnClicked
  → 播放详细介绍
  → 记录用户交互
```

**与前端通信**：
```cpp
// C++ 代码示例
#include "PixelStreamingInputComponent.h"

void AInteractionPoint::SendToFrontend(FString EventName, FString Data)
{
    UPixelStreamingInputComponent* PSInput = 
        FindComponentByClass<UPixelStreamingInputComponent>();
    
    if (PSInput)
    {
        FString JsonData = FString::Printf(
            TEXT("{\"event\":\"%s\",\"data\":\"%s\"}"), 
            *EventName, *Data
        );
        PSInput->SendPixelStreamingResponse(JsonData);
    }
}
```

---

## 第三部分：打包和部署

### 3.1 打包UE应用

**打包步骤**：
1. 文件 → 打包项目 → Windows (64-bit) 或 Linux
2. 选择输出目录
3. 等待打包完成（可能需要30分钟-2小时）

**打包配置**：
```
项目设置 → 打包
- 构建配置: Shipping
- 包含调试文件: 否
- 压缩包: 是
- 包含先决条件: 是
```

**输出文件**：
```
PackagedProject/
├── WindowsNoEditor/
│   ├── JiyiRedRoute_Experience.exe
│   ├── Engine/
│   └── JiyiRedRoute_Experience/
└── LinuxNoEditor/
    ├── JiyiRedRoute_Experience
    └── ...
```


### 3.2 配置Pixel Streaming服务器

**下载Pixel Streaming基础设施**：
```bash
# UE5.3+ 自带Pixel Streaming服务器
# 位置：Engine/Source/Programs/PixelStreaming/WebServers/

# 或从GitHub下载最新版本
git clone https://github.com/EpicGames/PixelStreamingInfrastructure.git
cd PixelStreamingInfrastructure
```

**安装依赖**：
```bash
# 安装Node.js依赖
cd SignallingWebServer
npm install

cd ../Matchmaker
npm install

cd ../SFU
npm install
```

**配置文件**：

**SignallingWebServer/config.json**：
```json
{
  "UseFrontend": true,
  "UseMatchmaker": false,
  "UseHTTPS": false,
  "HTTPSCertFile": "",
  "HTTPSKeyFile": "",
  "LogToFile": true,
  "LogVerbose": true,
  "HomepageFile": "player.html",
  "AdditionalRoutes": {},
  "EnableWebserver": true,
  "MatchmakerAddress": "",
  "MatchmakerPort": "9999",
  "PublicIP": "localhost",
  "HttpPort": 80,
  "HttpsPort": 443,
  "StreamerPort": 8888,
  "SFUPort": 8889
}
```


### 3.3 启动服务

**Windows启动脚本** (start_servers.bat)：
```batch
@echo off

REM 启动信令服务器
start "Signalling Server" cmd /k "cd SignallingWebServer && node cirrus.js"

REM 等待2秒
timeout /t 2

REM 启动UE应用（Pixel Streaming模式）
start "UE Application" cmd /k "cd PackagedProject\WindowsNoEditor && JiyiRedRoute_Experience.exe -PixelStreamingIP=localhost -PixelStreamingPort=8888 -RenderOffScreen"

echo Servers started!
echo Open browser: http://localhost
pause
```

**Linux启动脚本** (start_servers.sh)：
```bash
#!/bin/bash

# 启动信令服务器
cd SignallingWebServer
node cirrus.js &
SIGNALLING_PID=$!

# 等待信令服务器启动
sleep 2

# 启动UE应用
cd ../PackagedProject/LinuxNoEditor
./JiyiRedRoute_Experience.sh -PixelStreamingIP=localhost -PixelStreamingPort=8888 -RenderOffScreen &
UE_PID=$!

echo "Servers started!"
echo "Signalling Server PID: $SIGNALLING_PID"
echo "UE Application PID: $UE_PID"
echo "Open browser: http://localhost"

# 等待用户输入停止
read -p "Press Enter to stop servers..."

# 停止服务
kill $SIGNALLING_PID
kill $UE_PID
```

**启动命令参数说明**：
```
-PixelStreamingIP=localhost    # 信令服务器地址
-PixelStreamingPort=8888       # 信令服务器端口
-RenderOffScreen               # 无头渲染（服务器模式）
-PixelStreamingEncoderCodec=H264  # 编码器（H264/H265）
-PixelStreamingEncoderTargetBitrate=20000000  # 目标比特率
-PixelStreamingEncoderMaxBitrate=100000000    # 最大比特率
```


---

## 第四部分：前端集成

### 4.1 自定义前端页面

**创建Vue组件** (UEPixelStreaming.vue)：
```vue
<template>
  <div class="ue-container">
    <div id="player-container" ref="playerContainer">
      <!-- Pixel Streaming播放器将在这里渲染 -->
    </div>
    
    <div class="controls">
      <el-button @click="toggleFullscreen">全屏</el-button>
      <el-button @click="toggleStats">统计信息</el-button>
      <el-slider v-model="quality" :min="1" :max="100" @change="setQuality" />
    </div>
    
    <div v-if="showStats" class="stats">
      <p>FPS: {{ stats.fps }}</p>
      <p>延迟: {{ stats.latency }}ms</p>
      <p>比特率: {{ stats.bitrate }} Mbps</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const playerContainer = ref<HTMLElement>()
const quality = ref(80)
const showStats = ref(false)
const stats = ref({
  fps: 0,
  latency: 0,
  bitrate: 0
})

let pixelStreaming: any = null

onMounted(() => {
  initPixelStreaming()
})

onUnmounted(() => {
  if (pixelStreaming) {
    pixelStreaming.disconnect()
  }
})

function initPixelStreaming() {
  // 加载Pixel Streaming库
  const script = document.createElement('script')
  script.src = '/lib/webRtcPlayer.js'
  script.onload = () => {
    setupPixelStreaming()
  }
  document.head.appendChild(script)
}

function setupPixelStreaming() {
  const config = {
    serverUrl: 'ws://localhost:80',
    autoConnect: true,
    autoPlayVideo: true,
    startVideoMuted: false,
    useMic: false
  }
  
  // 初始化Pixel Streaming
  pixelStreaming = new window.PixelStreaming(config)
  
  // 监听事件
  pixelStreaming.addEventListener('videoInitialized', () => {
    console.log('视频流已初始化')
  })
  
  pixelStreaming.addEventListener('statsReceived', (data: any) => {
    stats.value = {
      fps: data.fps,
      latency: data.latency,
      bitrate: (data.bitrate / 1000000).toFixed(2)
    }
  })
  
  // 监听来自UE的消息
  pixelStreaming.addEventListener('dataChannelMessage', (message: string) => {
    handleUEMessage(JSON.parse(message))
  })
  
  // 将播放器添加到容器
  if (playerContainer.value) {
    playerContainer.value.appendChild(pixelStreaming.video)
  }
}

function handleUEMessage(data: any) {
  console.log('收到UE消息:', data)
  
  // 处理交互点点击事件
  if (data.event === 'interactionPointClicked') {
    // 显示详情面板
    showInteractionDetails(data.data)
  }
}

function sendToUE(eventName: string, data: any) {
  if (pixelStreaming) {
    const message = JSON.stringify({
      event: eventName,
      data: data
    })
    pixelStreaming.emitUIInteraction(message)
  }
}

function toggleFullscreen() {
  if (playerContainer.value) {
    if (!document.fullscreenElement) {
      playerContainer.value.requestFullscreen()
    } else {
      document.exitFullscreen()
    }
  }
}

function toggleStats() {
  showStats.value = !showStats.value
}

function setQuality(value: number) {
  sendToUE('setQuality', { quality: value })
}

function showInteractionDetails(data: any) {
  // 实现详情展示逻辑
  console.log('显示交互点详情:', data)
}
</script>

<style scoped>
.ue-container {
  position: relative;
  width: 100%;
  height: 100vh;
  background: #000;
}

#player-container {
  width: 100%;
  height: 100%;
}

#player-container video {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.controls {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
  padding: 10px;
  background: rgba(0, 0, 0, 0.7);
  border-radius: 8px;
}

.stats {
  position: absolute;
  top: 20px;
  right: 20px;
  padding: 15px;
  background: rgba(0, 0, 0, 0.8);
  color: #fff;
  border-radius: 8px;
  font-family: monospace;
}
</style>
```


### 4.2 与后端API集成

**API服务** (uePixelStreaming.ts)：
```typescript
import axios from 'axios'

const API_BASE = '/api/ue-streaming'

export interface StreamSession {
  sessionId: string
  userId: string
  sceneId: string
  serverUrl: string
  status: 'pending' | 'active' | 'ended'
  startTime: Date
  endTime?: Date
}

export interface ServerInstance {
  instanceId: string
  serverUrl: string
  status: 'available' | 'busy' | 'offline'
  currentUsers: number
  maxUsers: number
  gpuUsage: number
  cpuUsage: number
}

// 请求流媒体会话
export async function requestStreamSession(
  userId: string, 
  sceneId: string
): Promise<StreamSession> {
  const response = await axios.post(`${API_BASE}/sessions`, {
    userId,
    sceneId
  })
  return response.data
}

// 结束流媒体会话
export async function endStreamSession(sessionId: string): Promise<void> {
  await axios.delete(`${API_BASE}/sessions/${sessionId}`)
}

// 获取可用服务器
export async function getAvailableServers(): Promise<ServerInstance[]> {
  const response = await axios.get(`${API_BASE}/servers`)
  return response.data
}

// 记录用户交互
export async function recordInteraction(
  sessionId: string,
  interactionData: any
): Promise<void> {
  await axios.post(`${API_BASE}/sessions/${sessionId}/interactions`, 
    interactionData
  )
}
```

**后端控制器** (UEStreamingController.java)：
```java
@RestController
@RequestMapping("/api/ue-streaming")
public class UEStreamingController {
    
    @Autowired
    private UEStreamingService streamingService;
    
    @PostMapping("/sessions")
    public Result<StreamSession> createSession(
        @RequestBody SessionRequest request
    ) {
        // 1. 查找可用的UE服务器实例
        ServerInstance server = streamingService.findAvailableServer();
        
        if (server == null) {
            return Result.error("暂无可用服务器");
        }
        
        // 2. 创建会话
        StreamSession session = streamingService.createSession(
            request.getUserId(),
            request.getSceneId(),
            server
        );
        
        // 3. 通知UE服务器加载场景
        streamingService.loadScene(server, request.getSceneId());
        
        return Result.success(session);
    }
    
    @DeleteMapping("/sessions/{sessionId}")
    public Result<Void> endSession(@PathVariable String sessionId) {
        streamingService.endSession(sessionId);
        return Result.success();
    }
    
    @GetMapping("/servers")
    public Result<List<ServerInstance>> getServers() {
        List<ServerInstance> servers = streamingService.getAllServers();
        return Result.success(servers);
    }
}
```


---

## 第五部分：生产环境部署

### 5.1 Docker容器化部署

**Dockerfile** (UE应用)：
```dockerfile
FROM nvidia/cuda:11.8.0-runtime-ubuntu22.04

# 安装依赖
RUN apt-get update && apt-get install -y \
    libvulkan1 \
    libgl1-mesa-glx \
    libglu1-mesa \
    xvfb \
    && rm -rf /var/lib/apt/lists/*

# 复制UE应用
COPY PackagedProject/LinuxNoEditor /app

# 设置工作目录
WORKDIR /app

# 启动脚本
COPY start.sh /app/start.sh
RUN chmod +x /app/start.sh

# 暴露端口
EXPOSE 8888

# 启动命令
CMD ["/app/start.sh"]
```

**start.sh**：
```bash
#!/bin/bash

# 启动虚拟显示
Xvfb :99 -screen 0 1920x1080x24 &
export DISPLAY=:99

# 启动UE应用
./JiyiRedRoute_Experience.sh \
    -PixelStreamingIP=${SIGNALLING_SERVER} \
    -PixelStreamingPort=8888 \
    -RenderOffScreen \
    -PixelStreamingEncoderCodec=H264 \
    -PixelStreamingEncoderTargetBitrate=20000000
```

**docker-compose.yml**：
```yaml
version: '3.8'

services:
  signalling-server:
    image: node:18
    working_dir: /app
    volumes:
      - ./SignallingWebServer:/app
    command: node cirrus.js
    ports:
      - "80:80"
      - "8888:8888"
    networks:
      - ue-network

  ue-instance-1:
    build:
      context: .
      dockerfile: Dockerfile
    runtime: nvidia
    environment:
      - SIGNALLING_SERVER=signalling-server
      - NVIDIA_VISIBLE_DEVICES=0
    depends_on:
      - signalling-server
    networks:
      - ue-network
    deploy:
      resources:
        reservations:
          devices:
            - driver: nvidia
              count: 1
              capabilities: [gpu]

networks:
  ue-network:
    driver: bridge
```

**启动容器**：
```bash
# 构建镜像
docker-compose build

# 启动服务
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```


### 5.2 Kubernetes部署（可选）

**deployment.yaml**：
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ue-pixel-streaming
spec:
  replicas: 3
  selector:
    matchLabels:
      app: ue-streaming
  template:
    metadata:
      labels:
        app: ue-streaming
    spec:
      containers:
      - name: ue-app
        image: your-registry/ue-jiyi:latest
        resources:
          limits:
            nvidia.com/gpu: 1
            memory: "16Gi"
            cpu: "4"
          requests:
            nvidia.com/gpu: 1
            memory: "8Gi"
            cpu: "2"
        ports:
        - containerPort: 8888
        env:
        - name: SIGNALLING_SERVER
          value: "signalling-service"
---
apiVersion: v1
kind: Service
metadata:
  name: ue-streaming-service
spec:
  selector:
    app: ue-streaming
  ports:
  - protocol: TCP
    port: 8888
    targetPort: 8888
  type: LoadBalancer
```

### 5.3 负载均衡和自动扩展

**Matchmaker服务配置**：

Matchmaker可以自动将用户分配到可用的UE实例。

**matchmaker.js** (简化版)：
```javascript
const express = require('express');
const app = express();

// UE实例列表
const ueInstances = [
  { id: 1, url: 'ws://ue-instance-1:8888', users: 0, maxUsers: 5 },
  { id: 2, url: 'ws://ue-instance-2:8888', users: 0, maxUsers: 5 },
  { id: 3, url: 'ws://ue-instance-3:8888', users: 0, maxUsers: 5 }
];

// 查找可用实例
app.get('/getServer', (req, res) => {
  const available = ueInstances.find(
    instance => instance.users < instance.maxUsers
  );
  
  if (available) {
    available.users++;
    res.json({ 
      success: true, 
      serverUrl: available.url 
    });
  } else {
    res.json({ 
      success: false, 
      message: '暂无可用服务器' 
    });
  }
});

// 释放实例
app.post('/releaseServer', (req, res) => {
  const { serverId } = req.body;
  const instance = ueInstances.find(i => i.id === serverId);
  
  if (instance && instance.users > 0) {
    instance.users--;
  }
  
  res.json({ success: true });
});

app.listen(9999, () => {
  console.log('Matchmaker running on port 9999');
});
```


---

## 第六部分：性能优化

### 6.1 UE应用优化

**1. 渲染优化**：
```
- 使用LOD (Level of Detail)
- 启用Nanite虚拟几何体（UE5）
- 使用Virtual Shadow Maps
- 合并静态网格体
- 优化材质复杂度
- 使用纹理流送
```

**2. 编码优化**：
```
启动参数：
-PixelStreamingEncoderCodec=H264
-PixelStreamingEncoderRateControl=CBR
-PixelStreamingEncoderTargetBitrate=20000000
-PixelStreamingEncoderMaxBitrate=100000000
-PixelStreamingEncoderMinQP=20
-PixelStreamingEncoderMaxQP=51
```

**3. 内存优化**：
```cpp
// 定期清理未使用的资源
void AMyGameMode::CleanupResources()
{
    // 强制垃圾回收
    GEngine->ForceGarbageCollection(true);
    
    // 清理纹理流送池
    IStreamingManager::Get().GetTextureStreamingManager()
        .StreamOutTextureData(0);
}
```

### 6.2 网络优化

**1. 使用CDN加速**：
```
将信令服务器和静态资源部署到CDN
- 阿里云CDN
- 腾讯云CDN
- Cloudflare
```

**2. WebRTC优化**：
```javascript
// 配置TURN服务器（用于NAT穿透）
const rtcConfig = {
  iceServers: [
    { urls: 'stun:stun.l.google.com:19302' },
    {
      urls: 'turn:your-turn-server.com:3478',
      username: 'user',
      credential: 'pass'
    }
  ]
};
```

**3. 带宽自适应**：
```javascript
// 根据网络状况调整比特率
function adjustBitrate(networkQuality) {
  let bitrate;
  
  switch(networkQuality) {
    case 'excellent':
      bitrate = 20000000; // 20 Mbps
      break;
    case 'good':
      bitrate = 10000000; // 10 Mbps
      break;
    case 'poor':
      bitrate = 5000000;  // 5 Mbps
      break;
    default:
      bitrate = 10000000;
  }
  
  sendToUE('setBitrate', { bitrate });
}
```

### 6.3 服务器优化

**1. GPU监控**：
```bash
# 安装nvidia-smi
nvidia-smi

# 监控GPU使用率
watch -n 1 nvidia-smi

# 查看GPU进程
nvidia-smi pmon
```

**2. 自动重启脚本**：
```bash
#!/bin/bash
# auto_restart.sh

while true; do
    # 检查UE进程是否运行
    if ! pgrep -f "JiyiRedRoute_Experience" > /dev/null; then
        echo "UE进程已停止，正在重启..."
        ./start.sh &
    fi
    
    # 检查GPU内存使用
    GPU_MEM=$(nvidia-smi --query-gpu=memory.used --format=csv,noheader,nounits)
    if [ $GPU_MEM -gt 15000 ]; then
        echo "GPU内存过高，重启进程..."
        pkill -f "JiyiRedRoute_Experience"
        sleep 5
        ./start.sh &
    fi
    
    sleep 60
done
```


---

## 第七部分：监控和运维

### 7.1 监控指标

**关键指标**：
```
1. 服务器指标
   - GPU使用率
   - GPU内存使用
   - CPU使用率
   - 系统内存使用
   - 网络带宽

2. 应用指标
   - 活跃会话数
   - 平均FPS
   - 平均延迟
   - 视频比特率
   - 丢包率

3. 业务指标
   - 用户并发数
   - 平均会话时长
   - 场景加载时间
   - 交互响应时间
```

**Prometheus监控配置**：
```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'ue-streaming'
    static_configs:
      - targets: ['localhost:9090']
    metrics_path: '/metrics'
```

**自定义指标导出**：
```javascript
// metrics.js
const express = require('express');
const promClient = require('prom-client');

const register = new promClient.Registry();

// 定义指标
const activeSessionsGauge = new promClient.Gauge({
  name: 'ue_active_sessions',
  help: '当前活跃会话数',
  registers: [register]
});

const avgLatencyGauge = new promClient.Gauge({
  name: 'ue_avg_latency_ms',
  help: '平均延迟（毫秒）',
  registers: [register]
});

// 导出指标
app.get('/metrics', async (req, res) => {
  res.set('Content-Type', register.contentType);
  res.end(await register.metrics());
});
```

### 7.2 日志管理

**日志配置** (UE应用)：
```ini
; Engine.ini
[Core.Log]
LogPixelStreaming=Verbose
LogPixelStreamingPlayer=Verbose
LogWebRTC=Verbose

[/Script/Engine.Engine]
bEnableOnScreenDebugMessages=True
```

**日志收集**：
```bash
# 使用ELK Stack收集日志

# Filebeat配置
filebeat.inputs:
- type: log
  enabled: true
  paths:
    - /app/Saved/Logs/*.log
  fields:
    service: ue-streaming
    
output.elasticsearch:
  hosts: ["elasticsearch:9200"]
```

### 7.3 告警配置

**Grafana告警规则**：
```yaml
# alert_rules.yml
groups:
  - name: ue_streaming_alerts
    interval: 30s
    rules:
      - alert: HighGPUUsage
        expr: gpu_usage_percent > 90
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "GPU使用率过高"
          
      - alert: HighLatency
        expr: ue_avg_latency_ms > 100
        for: 2m
        labels:
          severity: critical
        annotations:
          summary: "延迟过高"
          
      - alert: NoAvailableServers
        expr: ue_available_servers == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "无可用服务器"
```


---

## 第八部分：成本优化

### 8.1 按需启动策略

**自动扩缩容**：
```javascript
// auto_scaler.js
const AWS = require('aws-sdk');
const ec2 = new AWS.EC2();

class AutoScaler {
  async scaleUp() {
    // 启动新的GPU实例
    const params = {
      ImageId: 'ami-xxxxxx',
      InstanceType: 'g4dn.xlarge',
      MinCount: 1,
      MaxCount: 1
    };
    
    const result = await ec2.runInstances(params).promise();
    console.log('启动新实例:', result.Instances[0].InstanceId);
  }
  
  async scaleDown(instanceId) {
    // 停止空闲实例
    await ec2.terminateInstances({
      InstanceIds: [instanceId]
    }).promise();
    
    console.log('停止实例:', instanceId);
  }
  
  async checkAndScale() {
    const metrics = await this.getMetrics();
    
    // 如果所有实例都在使用，启动新实例
    if (metrics.utilizationRate > 0.8) {
      await this.scaleUp();
    }
    
    // 如果有空闲实例超过10分钟，停止它
    const idleInstances = metrics.instances.filter(
      i => i.idleTime > 600
    );
    
    for (const instance of idleInstances) {
      await this.scaleDown(instance.id);
    }
  }
}

// 每分钟检查一次
setInterval(() => {
  scaler.checkAndScale();
}, 60000);
```

### 8.2 Spot实例使用

**AWS Spot实例配置**：
```javascript
const params = {
  InstanceType: 'g4dn.xlarge',
  SpotPrice: '0.30',  // 最高出价
  LaunchSpecification: {
    ImageId: 'ami-xxxxxx',
    InstanceType: 'g4dn.xlarge',
    KeyName: 'your-key',
    SecurityGroups: ['sg-xxxxxx']
  }
};

ec2.requestSpotInstances(params, (err, data) => {
  if (err) console.error(err);
  else console.log('Spot实例请求成功:', data);
});
```

**成本节省**：
- 按需实例: $0.526/小时
- Spot实例: $0.15-0.25/小时
- 节省: 50-70%

### 8.3 混合部署策略

**策略**：
```
1. 核心时段（9:00-21:00）
   - 使用按需实例保证稳定性
   - 3-5个实例常驻

2. 非核心时段（21:00-9:00）
   - 使用Spot实例降低成本
   - 1-2个实例待命

3. 高峰时段
   - 自动扩展到10个实例
   - 混合使用按需+Spot

4. 深夜时段（0:00-6:00）
   - 只保留1个实例
   - 或完全关闭，按需启动
```

**月度成本估算**：
```
方案A: 全天候按需实例（3个）
3 × $0.526 × 24 × 30 = $1,136/月

方案B: 智能混合部署
- 核心时段按需: 2 × $0.526 × 12 × 30 = $378
- 非核心Spot: 1 × $0.20 × 12 × 30 = $72
- 高峰扩展: 5 × $0.20 × 2 × 30 = $60
总计: $510/月

节省: 55%
```


---

## 第九部分：常见问题和解决方案

### 9.1 连接问题

**问题1: 无法连接到信令服务器**
```
症状: 浏览器显示"连接失败"

解决方案:
1. 检查信令服务器是否运行
   ps aux | grep cirrus
   
2. 检查防火墙规则
   sudo ufw allow 80
   sudo ufw allow 8888
   
3. 检查WebSocket连接
   在浏览器控制台查看WebSocket状态
```

**问题2: 视频流黑屏**
```
症状: 连接成功但画面黑屏

解决方案:
1. 检查UE应用是否正常运行
   ps aux | grep JiyiRedRoute
   
2. 检查GPU是否被正确识别
   nvidia-smi
   
3. 查看UE日志
   tail -f Saved/Logs/JiyiRedRoute.log
   
4. 确认编码器设置
   -PixelStreamingEncoderCodec=H264
```

### 9.2 性能问题

**问题3: 延迟过高（>100ms）**
```
原因分析:
- 网络带宽不足
- 服务器负载过高
- 编码设置不当

解决方案:
1. 降低比特率
   -PixelStreamingEncoderTargetBitrate=10000000
   
2. 降低分辨率
   -ResX=1280 -ResY=720
   
3. 使用更快的编码预设
   -PixelStreamingEncoderPreset=lowlatency
   
4. 检查网络质量
   ping your-server.com
   traceroute your-server.com
```

**问题4: FPS低于30**
```
原因分析:
- GPU性能不足
- 场景过于复杂
- 编码负载过高

解决方案:
1. 优化场景复杂度
   - 减少多边形数量
   - 使用LOD
   - 降低材质复杂度
   
2. 调整渲染设置
   r.ScreenPercentage 80
   r.Shadow.MaxResolution 1024
   
3. 使用硬件编码
   确保使用NVENC而非软件编码
```

### 9.3 稳定性问题

**问题5: UE应用崩溃**
```
常见原因:
- 内存泄漏
- GPU内存不足
- 资源加载失败

解决方案:
1. 启用崩溃报告
   -CrashReporter
   
2. 增加内存限制
   -MaxGPUCount=1
   
3. 定期重启实例
   每4小时自动重启一次
   
4. 实现健康检查
   定期ping UE应用
```

**问题6: 会话无法释放**
```
症状: 用户断开后实例仍显示占用

解决方案:
1. 实现超时机制
   if (sessionIdleTime > 300) {
     forceDisconnect(sessionId);
   }
   
2. 监听断开事件
   pixelStreaming.addEventListener('disconnect', () => {
     releaseSession();
   });
   
3. 定期清理僵尸会话
   setInterval(cleanupZombieSessions, 60000);
```

### 9.4 安全问题

**问题7: 未授权访问**
```
解决方案:
1. 实现JWT认证
   const token = generateJWT(userId);
   ws.send({ type: 'auth', token });
   
2. IP白名单
   只允许特定IP访问
   
3. 限制会话时长
   最长会话时间: 30分钟
```

**问题8: DDoS攻击**
```
防护措施:
1. 使用CDN防护
   Cloudflare / 阿里云DDoS防护
   
2. 限制连接频率
   每IP每分钟最多3次连接
   
3. 实现排队系统
   高峰期用户排队等待
```


---

## 第十部分：实施路线图

### 10.1 Phase 1: 原型验证（2-3周）

**目标**: 验证技术可行性

**任务**:
- [ ] 安装UE5和配置开发环境
- [ ] 创建简单的测试场景
- [ ] 启用Pixel Streaming插件
- [ ] 本地测试视频流传输
- [ ] 测量延迟和画质

**交付物**:
- 可运行的UE Pixel Streaming原型
- 性能测试报告
- 技术可行性评估

### 10.2 Phase 2: 场景开发（4-6周）

**目标**: 开发高质量3D场景

**任务**:
- [ ] 导入西柏坡纪念馆3D模型
- [ ] 设置光照和材质
- [ ] 创建交互点系统
- [ ] 实现场景切换逻辑
- [ ] 优化性能和画质

**交付物**:
- 4个完整的红色场景
- 交互系统蓝图
- 场景优化文档

### 10.3 Phase 3: 前后端集成（3-4周）

**目标**: 完整的系统集成

**任务**:
- [ ] 开发Vue前端组件
- [ ] 实现后端API接口
- [ ] 集成会话管理
- [ ] 实现用户认证
- [ ] 测试端到端流程

**交付物**:
- 完整的Web应用
- API文档
- 集成测试报告

### 10.4 Phase 4: 部署和优化（2-3周）

**目标**: 生产环境部署

**任务**:
- [ ] 配置GPU服务器
- [ ] Docker容器化
- [ ] 部署到云平台
- [ ] 配置监控和告警
- [ ] 性能调优

**交付物**:
- 生产环境部署
- 运维文档
- 监控仪表板

### 10.5 Phase 5: 测试和上线（2周）

**目标**: 正式上线

**任务**:
- [ ] 压力测试
- [ ] 用户验收测试
- [ ] 修复问题
- [ ] 准备上线
- [ ] 培训运维团队

**交付物**:
- 测试报告
- 上线检查清单
- 运维手册

**总时间**: 13-18周（约3-4.5个月）

---

## 第十一部分：成本预算

### 11.1 开发成本

| 项目 | 数量 | 单价 | 小计 |
|------|------|------|------|
| UE开发工程师 | 2人×4个月 | ¥25k/月 | ¥200k |
| 3D美术师 | 1人×3个月 | ¥20k/月 | ¥60k |
| 前端工程师 | 1人×2个月 | ¥20k/月 | ¥40k |
| 后端工程师 | 1人×2个月 | ¥20k/月 | ¥40k |
| 测试工程师 | 1人×1个月 | ¥15k/月 | ¥15k |
| 项目管理 | 1人×4个月 | ¥15k/月 | ¥60k |
| **开发总成本** | | | **¥415k** |

### 11.2 基础设施成本（年度）

| 项目 | 配置 | 月成本 | 年成本 |
|------|------|--------|--------|
| GPU服务器×3 | g4dn.xlarge | ¥8k | ¥96k |
| 信令服务器 | 2核4G | ¥0.3k | ¥3.6k |
| 数据库服务器 | 4核8G | ¥0.8k | ¥9.6k |
| 带宽费用 | 100Mbps | ¥2k | ¥24k |
| CDN流量 | 10TB/月 | ¥1k | ¥12k |
| 存储 | 2TB SSD | ¥0.5k | ¥6k |
| 监控和日志 | - | ¥0.5k | ¥6k |
| **基础设施总成本** | | | **¥157.2k** |

### 11.3 运维成本（年度）

| 项目 | 说明 | 年成本 |
|------|------|--------|
| 运维工程师 | 1人 | ¥180k |
| 技术支持 | 7×12小时 | ¥60k |
| 应急响应 | 备用资源 | ¥30k |
| **运维总成本** | | **¥270k** |

### 11.4 总成本

| 类型 | 第一年 | 后续年度 |
|------|--------|----------|
| 开发成本 | ¥415k | ¥0 |
| 基础设施 | ¥157k | ¥157k |
| 运维成本 | ¥270k | ¥270k |
| **总计** | **¥842k** | **¥427k** |

**优化后成本**（使用Spot实例+自动扩缩容）:
- 第一年: ¥650k
- 后续年度: ¥300k

---

## 总结

### 优势
✅ **影视级画质** - 最佳视觉体验
✅ **无设备限制** - 任何设备都能流畅运行
✅ **复杂场景支持** - 不受客户端性能限制
✅ **实时光追** - 次世代渲染技术

### 挑战
❌ **成本较高** - 需要GPU服务器
❌ **运维复杂** - 需要专业团队
❌ **延迟敏感** - 网络质量要求高
❌ **并发限制** - 每用户需要独立实例

### 适用场景
- ✅ 展厅展示（高端体验）
- ✅ VIP用户（付费服务）
- ✅ 特殊活动（限时开放）
- ❌ 大规模公众访问（成本过高）

### 建议
对于冀忆红途项目，建议采用**混合方案**：
1. **Three.js** - 作为主要方案，覆盖90%用户
2. **UE Pixel Streaming** - 作为高端选项，用于：
   - 展厅大屏展示
   - VIP付费体验
   - 重要活动直播
   - 营销宣传视频

这样既能控制成本，又能提供顶级体验选项。

---

## 参考资源

**官方文档**:
- [UE5 Pixel Streaming文档](https://docs.unrealengine.com/5.3/en-US/pixel-streaming-in-unreal-engine/)
- [Pixel Streaming基础设施](https://github.com/EpicGames/PixelStreamingInfrastructure)

**教程视频**:
- [Pixel Streaming快速入门](https://www.youtube.com/watch?v=xxx)
- [UE5云渲染实战](https://www.bilibili.com/video/xxx)

**社区资源**:
- [Unreal Engine论坛](https://forums.unrealengine.com/)
- [UE中文社区](https://www.unrealchina.net/)

**云服务商**:
- [AWS GPU实例](https://aws.amazon.com/ec2/instance-types/g4/)
- [阿里云GPU云服务器](https://www.aliyun.com/product/ecs/gpu)
- [腾讯云GPU服务器](https://cloud.tencent.com/product/gpu)

---

**文档版本**: v1.0
**最后更新**: 2024年12月
**作者**: Kiro AI Assistant
