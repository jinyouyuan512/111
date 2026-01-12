# Three.js + UE Pixel Streaming 混合方案实施指南

## 概述

混合方案通过智能路由，让不同用户使用最适合的渲染技术，既保证体验又控制成本。

## 核心策略

```
用户访问
    ↓
智能检测（设备、网络、会员等级）
    ↓
    ├─ 90%用户 → Three.js (Web渲染)
    │   - 普通用户
    │   - 移动设备
    │   - 网络较差
    │   - 免费体验
    │
    └─ 10%用户 → UE Pixel Streaming (云渲染)
        - VIP会员
        - 高端设备
        - 网络良好
        - 付费体验
```

---

## 第一部分：架构设计

### 1.1 系统架构图

```
                    用户浏览器
                         ↓
                    前端路由器
                    (Vue Router)
                         ↓
              ┌──────────┴──────────┐
              ↓                     ↓
        体验选择器              直接访问
      (ExperienceSelector)         ↓
              ↓                 Three.js
    ┌─────────┴─────────┐       组件
    ↓                   ↓
Three.js组件      UE Streaming组件
    ↓                   ↓
本地WebGL渲染    云端GPU渲染
                        ↓
                  Pixel Streaming
                     服务器
```

### 1.2 决策流程

```javascript
// 决策逻辑
function selectExperienceMode(user, device, network) {
  // 1. VIP用户优先使用UE
  if (user.isVIP && ueServersAvailable()) {
    return 'UE_STREAMING';
  }
  
  // 2. 检查设备性能
  if (device.gpu === 'low' || device.isMobile) {
    return 'THREEJS';
  }
  
  // 3. 检查网络质量
  if (network.bandwidth < 10 || network.latency > 100) {
    return 'THREEJS';
  }
  
  // 4. 检查UE服务器可用性
  if (!ueServersAvailable()) {
    return 'THREEJS';
  }
  
  // 5. 用户选择（高级设置）
  if (user.preferredMode) {
    return user.preferredMode;
  }
  
  // 6. 默认使用Three.js
  return 'THREEJS';
}
```

---

## 第二部分：前端实现

### 2.1 体验选择器组件

**ExperienceSelector.vue**:

```vue
<template>
  <div class="experience-container">
    <!-- 加载中 -->
    <div v-if="loading" class="loading">
      <el-icon class="is-loading"><Loading /></el-icon>
      <p>正在为您选择最佳体验模式...</p>
    </div>
    
    <!-- 模式选择提示 -->
    <div v-else-if="showModeSelector" class="mode-selector">
      <h3>选择体验模式</h3>
      
      <el-card class="mode-card" @click="selectMode('THREEJS')">
        <h4>标准模式 (推荐)</h4>
        <p>✅ 快速加载</p>
        <p>✅ 流畅体验</p>
        <p>✅ 兼容性好</p>
        <el-tag type="success">免费</el-tag>
      </el-card>
      
      <el-card 
        class="mode-card premium" 
        @click="selectMode('UE_STREAMING')"
        :class="{ disabled: !ueAvailable }"
      >
        <h4>影院模式 <el-tag type="warning">VIP</el-tag></h4>
        <p>🎬 影视级画质</p>
        <p>🌟 光线追踪</p>
        <p>⚡ 实时渲染</p>
        <el-tag v-if="!ueAvailable" type="info">暂无可用</el-tag>
        <el-tag v-else-if="!user.isVIP" type="warning">需要VIP</el-tag>
      </el-card>
    </div>
    
    <!-- Three.js 渲染器 -->
    <ThreeJSExperience
      v-else-if="selectedMode === 'THREEJS'"
      :scene-id="sceneId"
      @upgrade-request="showUpgradeDialog"
    />
    
    <!-- UE Pixel Streaming 渲染器 -->
    <UEStreamingExperience
      v-else-if="selectedMode === 'UE_STREAMING'"
      :scene-id="sceneId"
      :session-id="sessionId"
      @fallback="fallbackToThreeJS"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { detectDevice, detectNetwork } from '@/utils/detection'
import { checkUEAvailability, requestUESession } from '@/api/ueStreaming'
import ThreeJSExperience from './ThreeJSExperience.vue'
import UEStreamingExperience from './UEStreamingExperience.vue'

const props = defineProps<{
  sceneId: string
}>()

const userStore = useUserStore()
const user = userStore.user

const loading = ref(true)
const showModeSelector = ref(false)
const selectedMode = ref<'THREEJS' | 'UE_STREAMING' | null>(null)
const ueAvailable = ref(false)
const sessionId = ref<string>()

onMounted(async () => {
  await initializeExperience()
})

async function initializeExperience() {
  loading.value = true
  
  // 1. 检测设备和网络
  const device = await detectDevice()
  const network = await detectNetwork()
  
  // 2. 检查UE服务器可用性
  ueAvailable.value = await checkUEAvailability()
  
  // 3. 自动选择模式
  const autoMode = selectExperienceMode(user, device, network)
  
  // 4. 如果是VIP且UE可用，显示选择器
  if (user.isVIP && ueAvailable.value) {
    showModeSelector.value = true
    loading.value = false
  } else {
    // 直接使用自动选择的模式
    await activateMode(autoMode)
  }
}

function selectExperienceMode(user: any, device: any, network: any) {
  // VIP用户优先
  if (user.isVIP && ueAvailable.value) {
    return 'UE_STREAMING'
  }
  
  // 移动设备或低端设备
  if (device.isMobile || device.gpuTier < 2) {
    return 'THREEJS'
  }
  
  // 网络质量差
  if (network.bandwidth < 10 || network.latency > 100) {
    return 'THREEJS'
  }
  
  // 默认Three.js
  return 'THREEJS'
}

async function selectMode(mode: 'THREEJS' | 'UE_STREAMING') {
  // 检查权限
  if (mode === 'UE_STREAMING' && !user.isVIP) {
    showUpgradeDialog()
    return
  }
  
  await activateMode(mode)
}

async function activateMode(mode: 'THREEJS' | 'UE_STREAMING') {
  loading.value = true
  
  try {
    if (mode === 'UE_STREAMING') {
      // 请求UE会话
      const session = await requestUESession(user.id, props.sceneId)
      sessionId.value = session.sessionId
    }
    
    selectedMode.value = mode
    showModeSelector.value = false
    
    // 记录用户选择
    localStorage.setItem('preferredMode', mode)
  } catch (error) {
    console.error('激活模式失败:', error)
    // 降级到Three.js
    selectedMode.value = 'THREEJS'
  } finally {
    loading.value = false
  }
}

function fallbackToThreeJS() {
  ElMessage.warning('云渲染服务暂时不可用，已切换到标准模式')
  selectedMode.value = 'THREEJS'
}

function showUpgradeDialog() {
  ElMessageBox.confirm(
    '影院模式需要VIP会员，是否立即升级？',
    '升级提示',
    {
      confirmButtonText: '立即升级',
      cancelButtonText: '继续使用标准模式',
      type: 'warning'
    }
  ).then(() => {
    // 跳转到会员页面
    router.push('/vip')
  }).catch(() => {
    // 继续使用Three.js
    selectedMode.value = 'THREEJS'
  })
}
</script>

<style scoped>
.experience-container {
  width: 100%;
  height: 100vh;
  position: relative;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.mode-selector {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 40px;
  background: #f5f5f5;
}

.mode-card {
  width: 300px;
  margin: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.mode-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0,0,0,0.2);
}

.mode-card.premium {
  border: 2px solid #ffd700;
}

.mode-card.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
```


### 2.2 设备和网络检测

**detection.ts**:
```typescript
// 设备检测
export async function detectDevice() {
  const canvas = document.createElement('canvas')
  const gl = canvas.getContext('webgl') || canvas.getContext('experimental-webgl')
  
  let gpuTier = 1 // 1=低端, 2=中端, 3=高端
  let gpuInfo = 'Unknown'
  
  if (gl) {
    const debugInfo = gl.getExtension('WEBGL_debug_renderer_info')
    if (debugInfo) {
      gpuInfo = gl.getParameter(debugInfo.UNMASKED_RENDERER_WEBGL)
      
      // 简单的GPU分级
      if (gpuInfo.includes('NVIDIA') || gpuInfo.includes('AMD')) {
        gpuTier = 3
      } else if (gpuInfo.includes('Intel')) {
        gpuTier = 2
      }
    }
  }
  
  return {
    isMobile: /Mobile|Android|iPhone|iPad/i.test(navigator.userAgent),
    gpuTier,
    gpuInfo,
    memory: (navigator as any).deviceMemory || 4,
    cores: navigator.hardwareConcurrency || 4,
    screen: {
      width: window.screen.width,
      height: window.screen.height,
      pixelRatio: window.devicePixelRatio
    }
  }
}

// 网络检测
export async function detectNetwork() {
  const connection = (navigator as any).connection || 
                     (navigator as any).mozConnection || 
                     (navigator as any).webkitConnection
  
  let bandwidth = 10 // Mbps
  let latency = 50 // ms
  
  if (connection) {
    // 获取网络类型
    const effectiveType = connection.effectiveType
    
    switch (effectiveType) {
      case '4g':
        bandwidth = 20
        latency = 30
        break
      case '3g':
        bandwidth = 5
        latency = 100
        break
      case '2g':
        bandwidth = 1
        latency = 300
        break
      default:
        bandwidth = 10
        latency = 50
    }
    
    // 如果有downlink信息，使用实际值
    if (connection.downlink) {
      bandwidth = connection.downlink
    }
    
    if (connection.rtt) {
      latency = connection.rtt
    }
  }
  
  // 测试实际延迟
  try {
    const start = Date.now()
    await fetch('/api/ping', { method: 'HEAD' })
    latency = Date.now() - start
  } catch (error) {
    console.warn('网络测试失败:', error)
  }
  
  return {
    bandwidth,
    latency,
    type: connection?.effectiveType || 'unknown',
    saveData: connection?.saveData || false
  }
}
```


---

## 第三部分：后端实现

### 3.1 会话管理服务

**UESessionManager.java**:
```java
@Service
public class UESessionManager {
    
    @Autowired
    private UEServerPool serverPool;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final int MAX_SESSION_DURATION = 1800; // 30分钟
    
    /**
     * 检查UE服务器可用性
     */
    public boolean checkAvailability() {
        List<UEServer> servers = serverPool.getAvailableServers();
        return !servers.isEmpty();
    }
    
    /**
     * 请求UE会话
     */
    public UESession requestSession(String userId, String sceneId) {
        // 1. 检查用户权限
        if (!hasUEPermission(userId)) {
            throw new BusinessException("需要VIP权限");
        }
        
        // 2. 查找可用服务器
        UEServer server = serverPool.allocateServer();
        if (server == null) {
            throw new BusinessException("暂无可用服务器");
        }
        
        // 3. 创建会话
        UESession session = new UESession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setUserId(userId);
        session.setSceneId(sceneId);
        session.setServerId(server.getId());
        session.setServerUrl(server.getUrl());
        session.setStartTime(new Date());
        session.setStatus("ACTIVE");
        
        // 4. 保存到Redis
        String key = "ue:session:" + session.getSessionId();
        redisTemplate.opsForValue().set(
            key, 
            session, 
            MAX_SESSION_DURATION, 
            TimeUnit.SECONDS
        );
        
        // 5. 通知UE服务器加载场景
        notifyUEServer(server, sceneId);
        
        // 6. 记录日志
        logSessionStart(session);
        
        return session;
    }
    
    /**
     * 结束会话
     */
    public void endSession(String sessionId) {
        String key = "ue:session:" + sessionId;
        UESession session = (UESession) redisTemplate.opsForValue().get(key);
        
        if (session != null) {
            // 释放服务器
            serverPool.releaseServer(session.getServerId());
            
            // 删除会话
            redisTemplate.delete(key);
            
            // 记录日志
            logSessionEnd(session);
        }
    }
    
    /**
     * 会话心跳
     */
    public void heartbeat(String sessionId) {
        String key = "ue:session:" + sessionId;
        // 续期
        redisTemplate.expire(key, MAX_SESSION_DURATION, TimeUnit.SECONDS);
    }
    
    private boolean hasUEPermission(String userId) {
        // 检查用户是否是VIP
        User user = userService.getUserById(userId);
        return user != null && user.isVIP();
    }
    
    private void notifyUEServer(UEServer server, String sceneId) {
        // 通过HTTP或WebSocket通知UE服务器
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> params = new HashMap<>();
        params.put("sceneId", sceneId);
        
        restTemplate.postForObject(
            server.getUrl() + "/loadScene",
            params,
            String.class
        );
    }
}
```

### 3.2 服务器池管理

**UEServerPool.java**:
```java
@Component
public class UEServerPool {
    
    private List<UEServer> servers = new CopyOnWriteArrayList<>();
    
    @PostConstruct
    public void init() {
        // 初始化服务器列表
        servers.add(new UEServer("ue-1", "ws://ue-server-1:8888", 5));
        servers.add(new UEServer("ue-2", "ws://ue-server-2:8888", 5));
        servers.add(new UEServer("ue-3", "ws://ue-server-3:8888", 5));
        
        // 启动健康检查
        startHealthCheck();
    }
    
    /**
     * 分配服务器
     */
    public UEServer allocateServer() {
        // 找到负载最低的服务器
        return servers.stream()
            .filter(s -> s.getStatus() == ServerStatus.ONLINE)
            .filter(s -> s.getCurrentUsers() < s.getMaxUsers())
            .min(Comparator.comparingInt(UEServer::getCurrentUsers))
            .map(server -> {
                server.incrementUsers();
                return server;
            })
            .orElse(null);
    }
    
    /**
     * 释放服务器
     */
    public void releaseServer(String serverId) {
        servers.stream()
            .filter(s -> s.getId().equals(serverId))
            .findFirst()
            .ifPresent(UEServer::decrementUsers);
    }
    
    /**
     * 获取可用服务器列表
     */
    public List<UEServer> getAvailableServers() {
        return servers.stream()
            .filter(s -> s.getStatus() == ServerStatus.ONLINE)
            .filter(s -> s.getCurrentUsers() < s.getMaxUsers())
            .collect(Collectors.toList());
    }
    
    /**
     * 健康检查
     */
    @Scheduled(fixedRate = 30000) // 每30秒
    public void startHealthCheck() {
        servers.forEach(server -> {
            try {
                // Ping服务器
                RestTemplate restTemplate = new RestTemplate();
                String response = restTemplate.getForObject(
                    server.getUrl() + "/health",
                    String.class
                );
                
                if ("OK".equals(response)) {
                    server.setStatus(ServerStatus.ONLINE);
                } else {
                    server.setStatus(ServerStatus.OFFLINE);
                }
            } catch (Exception e) {
                server.setStatus(ServerStatus.OFFLINE);
                log.error("服务器健康检查失败: {}", server.getId(), e);
            }
        });
    }
}
```


---

## 第四部分：智能降级策略

### 4.1 降级触发条件

```typescript
// 降级管理器
class FallbackManager {
  private fallbackReasons: string[] = []
  
  /**
   * 检查是否需要降级
   */
  shouldFallback(context: ExperienceContext): boolean {
    this.fallbackReasons = []
    
    // 1. UE服务器不可用
    if (!context.ueAvailable) {
      this.fallbackReasons.push('UE服务器不可用')
      return true
    }
    
    // 2. 网络质量差
    if (context.network.latency > 150) {
      this.fallbackReasons.push('网络延迟过高')
      return true
    }
    
    // 3. 连接失败次数过多
    if (context.connectionFailures > 3) {
      this.fallbackReasons.push('连接失败次数过多')
      return true
    }
    
    // 4. FPS过低
    if (context.fps < 20) {
      this.fallbackReasons.push('帧率过低')
      return true
    }
    
    // 5. 会话超时
    if (context.sessionDuration > 1800) {
      this.fallbackReasons.push('会话超时')
      return true
    }
    
    return false
  }
  
  /**
   * 执行降级
   */
  async executeFallback(
    from: 'UE_STREAMING',
    to: 'THREEJS',
    context: ExperienceContext
  ) {
    console.log('执行降级:', this.fallbackReasons)
    
    // 1. 保存当前进度
    const progress = await this.saveProgress(context)
    
    // 2. 结束UE会话
    if (context.sessionId) {
      await endUESession(context.sessionId)
    }
    
    // 3. 切换到Three.js
    await this.switchToThreeJS(context.sceneId, progress)
    
    // 4. 通知用户
    ElNotification({
      title: '已切换到标准模式',
      message: this.fallbackReasons.join(', '),
      type: 'warning'
    })
  }
}
```

### 4.2 无缝切换

**场景状态同步**:
```typescript
// 状态同步管理器
class StateSyncManager {
  /**
   * 从UE切换到Three.js时同步状态
   */
  async syncUEToThreeJS(ueState: UEState): Promise<ThreeJSState> {
    return {
      sceneId: ueState.sceneId,
      camera: {
        position: ueState.cameraPosition,
        rotation: ueState.cameraRotation,
        fov: ueState.cameraFOV
      },
      progress: ueState.explorationProgress,
      visitedPoints: ueState.visitedInteractionPoints,
      timestamp: Date.now()
    }
  }
  
  /**
   * 从Three.js切换到UE时同步状态
   */
  async syncThreeJSToUE(threeState: ThreeJSState): Promise<UEState> {
    return {
      sceneId: threeState.sceneId,
      cameraPosition: threeState.camera.position,
      cameraRotation: threeState.camera.rotation,
      cameraFOV: threeState.camera.fov,
      explorationProgress: threeState.progress,
      visitedInteractionPoints: threeState.visitedPoints
    }
  }
  
  /**
   * 保存状态到服务器
   */
  async saveState(userId: string, state: any) {
    await axios.post('/api/experience/state', {
      userId,
      state,
      timestamp: Date.now()
    })
  }
  
  /**
   * 从服务器恢复状态
   */
  async restoreState(userId: string, sceneId: string) {
    const response = await axios.get('/api/experience/state', {
      params: { userId, sceneId }
    })
    return response.data
  }
}
```


---

## 第五部分：场景内容管理

### 5.1 双版本场景管理

**场景配置**:
```typescript
interface SceneConfig {
  id: string
  name: string
  versions: {
    threejs: {
      modelUrl: string      // GLTF模型
      textureQuality: 'low' | 'medium' | 'high'
      polyCount: number
      fileSize: number      // MB
    }
    ue: {
      available: boolean
      requiredGPU: string
      features: string[]    // ['raytracing', 'lumen', 'nanite']
    }
  }
  interactionPoints: InteractionPoint[]
  metadata: {
    era: string
    duration: number
    difficulty: 'easy' | 'medium' | 'hard'
  }
}

// 场景配置示例
const scenes: SceneConfig[] = [
  {
    id: 'xibaipo',
    name: '西柏坡革命纪念馆',
    versions: {
      threejs: {
        modelUrl: '/models/xibaipo.glb',
        textureQuality: 'high',
        polyCount: 500000,
        fileSize: 25
      },
      ue: {
        available: true,
        requiredGPU: 'RTX 3060',
        features: ['raytracing', 'lumen']
      }
    },
    interactionPoints: [
      // ... 交互点配置
    ],
    metadata: {
      era: '解放战争',
      duration: 20,
      difficulty: 'medium'
    }
  }
]
```

### 5.2 内容同步策略

**场景内容必须保持一致**:
```
Three.js版本                UE版本
    ↓                         ↓
相同的交互点位置          相同的交互点位置
相同的交互点内容          相同的交互点内容
相同的场景布局            相同的场景布局
    ↓                         ↓
        统一的内容数据库
```

**内容管理API**:
```java
@RestController
@RequestMapping("/api/scenes")
public class SceneController {
    
    /**
     * 获取场景配置（包含两个版本）
     */
    @GetMapping("/{sceneId}/config")
    public Result<SceneConfig> getSceneConfig(
        @PathVariable String sceneId,
        @RequestParam String mode // 'threejs' or 'ue'
    ) {
        SceneConfig config = sceneService.getConfig(sceneId);
        
        // 根据模式返回对应的资源URL
        if ("ue".equals(mode)) {
            config.setResourceUrl(config.getUeResourceUrl());
        } else {
            config.setResourceUrl(config.getThreejsResourceUrl());
        }
        
        return Result.success(config);
    }
    
    /**
     * 获取交互点（两个版本共用）
     */
    @GetMapping("/{sceneId}/interactions")
    public Result<List<InteractionPoint>> getInteractions(
        @PathVariable String sceneId
    ) {
        List<InteractionPoint> points = 
            sceneService.getInteractionPoints(sceneId);
        return Result.success(points);
    }
}
```

---

## 第六部分：用户体验优化

### 6.1 预加载策略

```typescript
// 预加载管理器
class PreloadManager {
  /**
   * 智能预加载
   */
  async smartPreload(user: User, device: Device) {
    // 1. 预加载Three.js资源（所有用户）
    this.preloadThreeJSAssets()
    
    // 2. VIP用户预热UE连接
    if (user.isVIP) {
      this.warmupUEConnection()
    }
    
    // 3. 根据历史偏好预加载
    if (user.preferredMode === 'UE_STREAMING') {
      this.preloadUEAssets()
    }
  }
  
  /**
   * 预加载Three.js资源
   */
  private async preloadThreeJSAssets() {
    const loader = new GLTFLoader()
    
    // 预加载常用场景
    const commonScenes = ['xibaipo', 'langyashan']
    
    for (const sceneId of commonScenes) {
      loader.load(
        `/models/${sceneId}.glb`,
        (gltf) => {
          // 缓存到内存
          this.cache.set(sceneId, gltf)
        }
      )
    }
  }
  
  /**
   * 预热UE连接
   */
  private async warmupUEConnection() {
    try {
      // 建立WebSocket连接但不启动会话
      const ws = new WebSocket('ws://ue-server/warmup')
      ws.onopen = () => {
        console.log('UE连接已预热')
        ws.close()
      }
    } catch (error) {
      console.warn('UE连接预热失败:', error)
    }
  }
}
```

### 6.2 体验对比展示

**让用户看到差异**:
```vue
<template>
  <div class="comparison-view">
    <div class="split-screen">
      <!-- 左侧：Three.js -->
      <div class="screen-half">
        <h3>标准模式</h3>
        <ThreeJSPreview :scene-id="sceneId" />
        <div class="stats">
          <p>加载时间: 3秒</p>
          <p>帧率: 60 FPS</p>
          <p>画质: 高</p>
        </div>
      </div>
      
      <!-- 右侧：UE Streaming -->
      <div class="screen-half premium">
        <h3>影院模式 <el-tag type="warning">VIP</el-tag></h3>
        <UEPreview :scene-id="sceneId" />
        <div class="stats">
          <p>加载时间: 2秒</p>
          <p>帧率: 60 FPS</p>
          <p>画质: 影视级</p>
          <p>✨ 光线追踪</p>
          <p>✨ 全局光照</p>
        </div>
      </div>
    </div>
    
    <el-button type="primary" @click="upgradeToVIP">
      升级VIP，体验影院模式
    </el-button>
  </div>
</template>
```

### 6.3 用户引导

**首次访问引导**:
```typescript
// 新手引导
class OnboardingGuide {
  async showGuide(user: User) {
    if (user.isFirstVisit) {
      // 步骤1: 介绍两种模式
      await this.showStep1()
      
      // 步骤2: 演示标准模式
      await this.showStep2()
      
      // 步骤3: 如果是VIP，演示影院模式
      if (user.isVIP) {
        await this.showStep3()
      }
      
      // 步骤4: 让用户选择
      await this.showStep4()
    }
  }
  
  private async showStep1() {
    ElMessageBox.alert(
      '冀忆红途提供两种体验模式：\n\n' +
      '📱 标准模式 - 快速流畅，适合所有设备\n' +
      '🎬 影院模式 - 影视级画质，VIP专享',
      '欢迎使用',
      { type: 'info' }
    )
  }
}
```


---

## 第七部分：成本控制

### 7.1 分级定价策略

```typescript
// 会员等级配置
const membershipTiers = {
  free: {
    name: '免费用户',
    price: 0,
    features: {
      experienceMode: 'THREEJS',
      maxSessionTime: 0,
      ueAccess: false
    }
  },
  
  basic: {
    name: '基础会员',
    price: 19, // 元/月
    features: {
      experienceMode: 'THREEJS',
      maxSessionTime: 0,
      ueAccess: false,
      adFree: true
    }
  },
  
  vip: {
    name: 'VIP会员',
    price: 99, // 元/月
    features: {
      experienceMode: 'BOTH', // 可选择
      maxSessionTime: 30, // 分钟/月
      ueAccess: true,
      adFree: true,
      priority: true
    }
  },
  
  premium: {
    name: '尊享会员',
    price: 299, // 元/月
    features: {
      experienceMode: 'BOTH',
      maxSessionTime: 120, // 分钟/月
      ueAccess: true,
      adFree: true,
      priority: true,
      exclusiveScenes: true
    }
  }
}
```

### 7.2 使用时长限制

```java
@Service
public class UsageQuotaService {
    
    /**
     * 检查用户UE使用配额
     */
    public boolean checkUEQuota(String userId) {
        User user = userService.getUserById(userId);
        
        if (!user.isVIP()) {
            return false;
        }
        
        // 获取本月已使用时长
        int usedMinutes = getMonthlyUsage(userId);
        int quotaMinutes = user.getMembershipTier().getMaxSessionTime();
        
        return usedMinutes < quotaMinutes;
    }
    
    /**
     * 记录使用时长
     */
    public void recordUsage(String userId, int minutes) {
        String key = "ue:usage:" + userId + ":" + getCurrentMonth();
        redisTemplate.opsForValue().increment(key, minutes);
        
        // 设置过期时间为下月初
        redisTemplate.expireAt(key, getNextMonthStart());
    }
    
    /**
     * 获取剩余配额
     */
    public int getRemainingQuota(String userId) {
        User user = userService.getUserById(userId);
        int used = getMonthlyUsage(userId);
        int total = user.getMembershipTier().getMaxSessionTime();
        
        return Math.max(0, total - used);
    }
}
```

### 7.3 按需计费

**计费策略**:
```
1. 包月套餐
   - VIP: ¥99/月，30分钟UE时长
   - 尊享: ¥299/月，120分钟UE时长

2. 按次付费
   - ¥5/10分钟
   - ¥20/小时

3. 流量包
   - ¥50 = 100分钟
   - ¥150 = 360分钟（6小时）
   - ¥500 = 1500分钟（25小时）

4. 企业定制
   - 独立服务器
   - 无限时长
   - 定制场景
```

---

## 第八部分：监控和分析

### 8.1 使用数据统计

```typescript
// 数据统计服务
class AnalyticsService {
  /**
   * 记录模式选择
   */
  trackModeSelection(userId: string, mode: string, reason: string) {
    this.sendEvent({
      event: 'mode_selected',
      userId,
      mode,
      reason,
      timestamp: Date.now()
    })
  }
  
  /**
   * 记录模式切换
   */
  trackModeSwitc(
    userId: string,
    from: string,
    to: string,
    reason: string
  ) {
    this.sendEvent({
      event: 'mode_switched',
      userId,
      from,
      to,
      reason,
      timestamp: Date.now()
    })
  }
  
  /**
   * 记录性能指标
   */
  trackPerformance(metrics: {
    mode: string
    fps: number
    latency: number
    loadTime: number
  }) {
    this.sendEvent({
      event: 'performance_metrics',
      ...metrics,
      timestamp: Date.now()
    })
  }
}
```

### 8.2 A/B测试

```typescript
// A/B测试配置
const abTests = {
  'auto-mode-selection': {
    name: '自动模式选择',
    variants: {
      A: {
        name: '总是显示选择器',
        weight: 50,
        config: { alwaysShowSelector: true }
      },
      B: {
        name: '智能自动选择',
        weight: 50,
        config: { alwaysShowSelector: false }
      }
    }
  },
  
  'ue-pricing': {
    name: 'UE定价策略',
    variants: {
      A: {
        name: '包月制',
        weight: 50,
        config: { pricingModel: 'subscription' }
      },
      B: {
        name: '按次付费',
        weight: 50,
        config: { pricingModel: 'pay-per-use' }
      }
    }
  }
}

// 分配测试组
function assignABTest(userId: string, testName: string) {
  const test = abTests[testName]
  const hash = hashCode(userId + testName)
  const variant = hash % 100 < 50 ? 'A' : 'B'
  
  return test.variants[variant]
}
```

### 8.3 关键指标

**需要监控的指标**:
```
1. 模式分布
   - Three.js使用率: 目标90%
   - UE使用率: 目标10%

2. 转化率
   - 免费→VIP转化率: 目标5%
   - 体验对比页访问→升级: 目标15%

3. 用户满意度
   - Three.js满意度: 目标>4.0/5.0
   - UE满意度: 目标>4.5/5.0

4. 技术指标
   - Three.js平均FPS: 目标>50
   - UE平均延迟: 目标<80ms
   - 降级率: 目标<5%

5. 成本指标
   - 单用户UE成本: 目标<¥2/小时
   - 服务器利用率: 目标>70%
```


---

## 第九部分：实施路线图

### Phase 1: 基础混合架构（2周）

**目标**: 实现基本的模式切换

**任务**:
- [ ] 创建ExperienceSelector组件
- [ ] 实现设备和网络检测
- [ ] 开发后端会话管理API
- [ ] 实现简单的模式切换逻辑

**交付物**:
- 可工作的模式选择器
- 基础的切换功能

### Phase 2: UE集成（3-4周）

**目标**: 集成UE Pixel Streaming

**任务**:
- [ ] 部署UE Pixel Streaming服务器
- [ ] 开发UEStreamingExperience组件
- [ ] 实现WebRTC连接
- [ ] 测试UE场景加载

**交付物**:
- 可用的UE Streaming体验
- 完整的前后端集成

### Phase 3: 智能优化（2周）

**目标**: 优化用户体验

**任务**:
- [ ] 实现智能降级策略
- [ ] 开发状态同步机制
- [ ] 优化预加载策略
- [ ] 添加用户引导

**交付物**:
- 无缝切换体验
- 智能降级系统

### Phase 4: 商业化（2周）

**目标**: 实现会员体系

**任务**:
- [ ] 开发会员等级系统
- [ ] 实现使用配额管理
- [ ] 集成支付系统
- [ ] 创建体验对比页面

**交付物**:
- 完整的会员体系
- 支付和计费功能

### Phase 5: 监控和优化（1周）

**目标**: 完善监控和分析

**任务**:
- [ ] 部署监控系统
- [ ] 实现数据统计
- [ ] 配置告警规则
- [ ] 优化成本

**交付物**:
- 监控仪表板
- 数据分析报告

**总时间**: 10-12周（约2.5-3个月）

---

## 第十部分：成本效益分析

### 10.1 成本对比

**纯Three.js方案**:
```
开发成本: ¥300k
年度运营: ¥50k
总计第一年: ¥350k
```

**纯UE方案**:
```
开发成本: ¥800k
年度运营: ¥400k
总计第一年: ¥1,200k
```

**混合方案**:
```
开发成本: ¥450k
年度运营: ¥150k
总计第一年: ¥600k

节省vs纯UE: ¥600k (50%)
增加vs纯Three.js: ¥250k
```

### 10.2 收益预测

**用户规模假设**:
- 月活用户: 10,000
- VIP转化率: 5%
- VIP用户: 500

**收入预测**:
```
VIP会员费:
500用户 × ¥99/月 × 12月 = ¥594k/年

按次付费:
假设20%的免费用户偶尔付费
2000用户 × ¥10/次 × 2次/年 = ¥40k/年

总收入: ¥634k/年
```

**盈利分析**:
```
第一年:
收入: ¥634k
成本: ¥600k
利润: ¥34k

第二年:
收入: ¥634k
成本: ¥150k (无开发成本)
利润: ¥484k

投资回收期: 约1年
```

### 10.3 规模化效益

**用户增长到50,000时**:
```
VIP用户: 2,500
VIP收入: ¥2,970k/年

成本增加:
- 增加5台UE服务器: ¥300k/年
- 带宽增加: ¥100k/年
总成本: ¥550k/年

利润: ¥2,420k/年
利润率: 81%
```

---

## 总结

### ✅ 混合方案的优势

1. **成本可控**
   - 90%用户使用Three.js（低成本）
   - 10%用户使用UE（高价值）
   - 总成本比纯UE方案低50%

2. **体验分层**
   - 免费用户获得良好体验
   - VIP用户获得顶级体验
   - 清晰的价值区分

3. **灵活扩展**
   - 可以根据需求调整比例
   - 可以逐步增加UE服务器
   - 可以按需启停服务器

4. **风险分散**
   - UE服务故障不影响主要用户
   - Three.js作为可靠的后备方案
   - 降低技术风险

5. **商业价值**
   - 创造VIP升级动力
   - 提供差异化服务
   - 增加收入来源

### 🎯 关键成功因素

1. **无缝切换** - 用户感知不到技术差异
2. **智能分配** - 自动选择最佳模式
3. **稳定可靠** - 降级策略保证体验
4. **价值清晰** - 让用户看到差异
5. **成本优化** - 按需使用资源

### 📊 推荐配置

**初期（前3个月）**:
- Three.js: 100%用户
- UE: 仅用于演示和测试
- 成本: 最低

**成长期（3-12个月）**:
- Three.js: 95%用户
- UE: 5%VIP用户
- 2-3台UE服务器

**成熟期（12个月后）**:
- Three.js: 90%用户
- UE: 10%VIP用户
- 5-10台UE服务器
- 自动扩缩容

---

## 附录：快速开始

### 最小可行实现（MVP）

如果你想快速验证混合方案，可以先实现这个最小版本：

```vue
<!-- 简化版混合组件 -->
<template>
  <div>
    <!-- 模式选择 -->
    <el-radio-group v-model="mode" v-if="user.isVIP">
      <el-radio label="threejs">标准模式</el-radio>
      <el-radio label="ue">影院模式</el-radio>
    </el-radio-group>
    
    <!-- 渲染器 -->
    <ThreeJSExperience v-if="mode === 'threejs'" />
    <UEStreamingExperience v-else-if="mode === 'ue'" />
  </div>
</template>

<script setup>
const mode = ref('threejs')
const user = useUserStore().user
</script>
```

这个MVP只需要：
1. 一个简单的模式选择器
2. 两个独立的渲染组件
3. 基础的权限检查

然后逐步添加：
- 智能检测
- 自动降级
- 状态同步
- 监控统计

---

**文档版本**: v1.0
**最后更新**: 2024年12月
**作者**: Kiro AI Assistant
