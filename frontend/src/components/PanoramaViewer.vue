<template>
  <div class="panorama-viewer" ref="containerRef">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-overlay">
      <div class="loading-spinner"></div>
      <p>全景加载中... {{ loadProgress }}%</p>
    </div>
    
    <!-- 控制面板 -->
    <div class="controls-panel">
      <button @click="toggleAutoRotate" :class="{ active: autoRotate }">
        {{ autoRotate ? '⏸️' : '▶️' }}
      </button>
      <button @click="resetView">🔄</button>
      <button @click="zoomIn">➕</button>
      <button @click="zoomOut">➖</button>
      <button @click="toggleFullscreen">⛶</button>
    </div>
    
    <!-- 热点标记 -->
    <div 
      v-for="hotspot in visibleHotspots" 
      :key="hotspot.id"
      class="hotspot-marker"
      :style="hotspot.screenPosition"
      @click="onHotspotClick(hotspot)"
    >
      <span class="hotspot-icon">📍</span>
      <span class="hotspot-label">{{ hotspot.title }}</span>
    </div>
    
    <!-- 热点信息弹窗 -->
    <div v-if="activeHotspot" class="hotspot-popup">
      <button class="close-btn" @click="activeHotspot = null">×</button>
      <h4>{{ activeHotspot.title }}</h4>
      <p>{{ activeHotspot.description }}</p>
    </div>
    
    <!-- 场景切换器 -->
    <div v-if="scenes.length > 1" class="scene-switcher">
      <div 
        v-for="scene in scenes" 
        :key="scene.id"
        class="scene-item"
        :class="{ active: currentSceneId === scene.id }"
        @click="switchScene(scene.id)"
      >
        <span class="scene-icon">{{ scene.icon }}</span>
        <span class="scene-name">{{ scene.name }}</span>
      </div>
    </div>
    
    <!-- 指南针 -->
    <div class="compass" :style="{ transform: `rotate(${compassAngle}deg)` }">
      <span class="compass-n">N</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, computed } from 'vue'
import * as THREE from 'three'

interface Hotspot {
  id: string
  title: string
  description: string
  position: { lat: number; lon: number }
  screenPosition?: { left: string; top: string; display: string }
}

interface Scene {
  id: string
  name: string
  icon: string
  image: string
  hotspots: Hotspot[]
}

const props = defineProps<{
  scenes: Scene[]
  initialSceneId?: string
}>()

const emit = defineEmits(['sceneChange', 'hotspotClick'])

const containerRef = ref<HTMLElement | null>(null)
const loading = ref(true)
const loadProgress = ref(0)
const autoRotate = ref(true)
const currentSceneId = ref('')
const activeHotspot = ref<Hotspot | null>(null)
const compassAngle = ref(0)
const visibleHotspots = ref<Hotspot[]>([])

// Three.js 变量
let scene: THREE.Scene
let camera: THREE.PerspectiveCamera
let renderer: THREE.WebGLRenderer
let sphere: THREE.Mesh
let animationId: number
let isUserInteracting = false
let onPointerDownMouseX = 0
let onPointerDownMouseY = 0
let lon = 0
let lat = 0
let onPointerDownLon = 0
let onPointerDownLat = 0
let phi = 0
let theta = 0

const currentScene = computed(() => 
  props.scenes.find(s => s.id === currentSceneId.value)
)

// 初始化 Three.js
const initThree = () => {
  if (!containerRef.value) return
  
  const container = containerRef.value
  const width = container.clientWidth
  const height = container.clientHeight
  
  // 创建场景
  scene = new THREE.Scene()
  
  // 创建相机
  camera = new THREE.PerspectiveCamera(75, width / height, 1, 1100)
  camera.target = new THREE.Vector3(0, 0, 0)
  
  // 创建球体几何体（内部贴图）
  const geometry = new THREE.SphereGeometry(500, 60, 40)
  geometry.scale(-1, 1, 1) // 翻转使纹理在内部可见
  
  // 创建材质（先用空白）
  const material = new THREE.MeshBasicMaterial()
  sphere = new THREE.Mesh(geometry, material)
  scene.add(sphere)
  
  // 创建渲染器
  renderer = new THREE.WebGLRenderer({ antialias: true })
  renderer.setPixelRatio(window.devicePixelRatio)
  renderer.setSize(width, height)
  container.appendChild(renderer.domElement)
  
  // 添加事件监听
  container.addEventListener('pointerdown', onPointerDown)
  container.addEventListener('pointermove', onPointerMove)
  container.addEventListener('pointerup', onPointerUp)
  container.addEventListener('wheel', onWheel)
  window.addEventListener('resize', onWindowResize)
  
  // 加载初始场景
  const initialId = props.initialSceneId || props.scenes[0]?.id
  if (initialId) {
    switchScene(initialId)
  }
  
  // 开始动画循环
  animate()
}

// 加载全景图
const loadPanorama = (imageUrl: string) => {
  loading.value = true
  loadProgress.value = 0
  
  const loader = new THREE.TextureLoader()
  loader.load(
    imageUrl,
    (texture) => {
      texture.colorSpace = THREE.SRGBColorSpace
      ;(sphere.material as THREE.MeshBasicMaterial).map = texture
      ;(sphere.material as THREE.MeshBasicMaterial).needsUpdate = true
      loading.value = false
      loadProgress.value = 100
    },
    (xhr) => {
      loadProgress.value = Math.round((xhr.loaded / xhr.total) * 100)
    },
    (error) => {
      console.error('全景图加载失败:', error)
      loading.value = false
    }
  )
}

// 切换场景
const switchScene = (sceneId: string) => {
  const targetScene = props.scenes.find(s => s.id === sceneId)
  if (!targetScene) return
  
  currentSceneId.value = sceneId
  loadPanorama(targetScene.image)
  updateHotspots()
  emit('sceneChange', sceneId)
}

// 更新热点位置
const updateHotspots = () => {
  if (!currentScene.value || !camera) return
  
  visibleHotspots.value = currentScene.value.hotspots.map(hotspot => {
    const pos = latLonToVector3(hotspot.position.lat, hotspot.position.lon)
    const screenPos = worldToScreen(pos)
    
    return {
      ...hotspot,
      screenPosition: {
        left: `${screenPos.x}px`,
        top: `${screenPos.y}px`,
        display: screenPos.visible ? 'block' : 'none'
      }
    }
  })
}

// 经纬度转3D坐标
const latLonToVector3 = (lat: number, lon: number): THREE.Vector3 => {
  const phi = THREE.MathUtils.degToRad(90 - lat)
  const theta = THREE.MathUtils.degToRad(lon)
  
  return new THREE.Vector3(
    500 * Math.sin(phi) * Math.cos(theta),
    500 * Math.cos(phi),
    500 * Math.sin(phi) * Math.sin(theta)
  )
}

// 3D坐标转屏幕坐标
const worldToScreen = (position: THREE.Vector3) => {
  if (!containerRef.value) return { x: 0, y: 0, visible: false }
  
  const vector = position.clone()
  vector.project(camera)
  
  const width = containerRef.value.clientWidth
  const height = containerRef.value.clientHeight
  
  const x = (vector.x * 0.5 + 0.5) * width
  const y = (-vector.y * 0.5 + 0.5) * height
  
  // 检查是否在视野内
  const visible = vector.z < 1 && x > 0 && x < width && y > 0 && y < height
  
  return { x, y, visible }
}

// 鼠标/触摸事件
const onPointerDown = (event: PointerEvent) => {
  isUserInteracting = true
  onPointerDownMouseX = event.clientX
  onPointerDownMouseY = event.clientY
  onPointerDownLon = lon
  onPointerDownLat = lat
}

const onPointerMove = (event: PointerEvent) => {
  if (!isUserInteracting) return
  
  lon = (onPointerDownMouseX - event.clientX) * 0.1 + onPointerDownLon
  lat = (event.clientY - onPointerDownMouseY) * 0.1 + onPointerDownLat
}

const onPointerUp = () => {
  isUserInteracting = false
}

const onWheel = (event: WheelEvent) => {
  event.preventDefault()
  const fov = camera.fov + event.deltaY * 0.05
  camera.fov = THREE.MathUtils.clamp(fov, 30, 90)
  camera.updateProjectionMatrix()
}

const onWindowResize = () => {
  if (!containerRef.value) return
  
  const width = containerRef.value.clientWidth
  const height = containerRef.value.clientHeight
  
  camera.aspect = width / height
  camera.updateProjectionMatrix()
  renderer.setSize(width, height)
}

// 动画循环
const animate = () => {
  animationId = requestAnimationFrame(animate)
  
  // 自动旋转
  if (autoRotate.value && !isUserInteracting) {
    lon += 0.05
  }
  
  // 限制纬度范围
  lat = Math.max(-85, Math.min(85, lat))
  
  // 计算相机朝向
  phi = THREE.MathUtils.degToRad(90 - lat)
  theta = THREE.MathUtils.degToRad(lon)
  
  camera.target.x = 500 * Math.sin(phi) * Math.cos(theta)
  camera.target.y = 500 * Math.cos(phi)
  camera.target.z = 500 * Math.sin(phi) * Math.sin(theta)
  
  camera.lookAt(camera.target)
  
  // 更新指南针
  compassAngle.value = -lon % 360
  
  // 更新热点位置
  updateHotspots()
  
  renderer.render(scene, camera)
}

// 控制函数
const toggleAutoRotate = () => {
  autoRotate.value = !autoRotate.value
}

const resetView = () => {
  lon = 0
  lat = 0
  camera.fov = 75
  camera.updateProjectionMatrix()
}

const zoomIn = () => {
  camera.fov = Math.max(30, camera.fov - 10)
  camera.updateProjectionMatrix()
}

const zoomOut = () => {
  camera.fov = Math.min(90, camera.fov + 10)
  camera.updateProjectionMatrix()
}

const toggleFullscreen = () => {
  if (!containerRef.value) return
  
  if (document.fullscreenElement) {
    document.exitFullscreen()
  } else {
    containerRef.value.requestFullscreen()
  }
}

const onHotspotClick = (hotspot: Hotspot) => {
  activeHotspot.value = hotspot
  emit('hotspotClick', hotspot)
}

// 清理
const cleanup = () => {
  if (animationId) {
    cancelAnimationFrame(animationId)
  }
  
  if (containerRef.value) {
    containerRef.value.removeEventListener('pointerdown', onPointerDown)
    containerRef.value.removeEventListener('pointermove', onPointerMove)
    containerRef.value.removeEventListener('pointerup', onPointerUp)
    containerRef.value.removeEventListener('wheel', onWheel)
  }
  
  window.removeEventListener('resize', onWindowResize)
  
  if (renderer) {
    renderer.dispose()
    containerRef.value?.removeChild(renderer.domElement)
  }
}

watch(() => props.scenes, () => {
  if (props.scenes.length > 0 && !currentSceneId.value) {
    switchScene(props.scenes[0].id)
  }
}, { deep: true })

onMounted(() => {
  initThree()
})

onUnmounted(() => {
  cleanup()
})

defineExpose({
  switchScene,
  resetView,
  toggleAutoRotate
})
</script>

<style scoped>
.panorama-viewer {
  position: relative;
  width: 100%;
  height: 100%;
  background: #000;
  overflow: hidden;
  cursor: grab;
}

.panorama-viewer:active {
  cursor: grabbing;
}

/* 加载状态 */
.loading-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  z-index: 100;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top-color: #722ed1;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 15px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 控制面板 */
.controls-panel {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
  background: rgba(0, 0, 0, 0.6);
  padding: 10px 15px;
  border-radius: 30px;
  backdrop-filter: blur(10px);
  z-index: 50;
}

.controls-panel button {
  width: 40px;
  height: 40px;
  border: none;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border-radius: 50%;
  cursor: pointer;
  font-size: 1.1rem;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.controls-panel button:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.1);
}

.controls-panel button.active {
  background: #722ed1;
}

/* 热点标记 */
.hotspot-marker {
  position: absolute;
  transform: translate(-50%, -50%);
  cursor: pointer;
  z-index: 40;
  display: flex;
  flex-direction: column;
  align-items: center;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: translate(-50%, -50%) scale(1); }
  50% { transform: translate(-50%, -50%) scale(1.1); }
}

.hotspot-icon {
  font-size: 2rem;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.5));
}

.hotspot-label {
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 0.75rem;
  white-space: nowrap;
  margin-top: 5px;
}

/* 热点弹窗 */
.hotspot-popup {
  position: absolute;
  top: 20px;
  right: 20px;
  background: rgba(0, 0, 0, 0.85);
  color: white;
  padding: 20px;
  border-radius: 16px;
  max-width: 300px;
  z-index: 60;
  backdrop-filter: blur(10px);
  animation: slideIn 0.3s ease;
}

@keyframes slideIn {
  from { opacity: 0; transform: translateX(20px); }
  to { opacity: 1; transform: translateX(0); }
}

.hotspot-popup .close-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 24px;
  height: 24px;
  border: none;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border-radius: 50%;
  cursor: pointer;
  font-size: 1rem;
}

.hotspot-popup h4 {
  margin: 0 0 10px;
  color: #722ed1;
  font-size: 1.1rem;
}

.hotspot-popup p {
  margin: 0;
  font-size: 0.9rem;
  line-height: 1.6;
  opacity: 0.9;
}

/* 场景切换器 */
.scene-switcher {
  position: absolute;
  top: 20px;
  left: 20px;
  display: flex;
  gap: 10px;
  z-index: 50;
}

.scene-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
  padding: 10px 15px;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  backdrop-filter: blur(10px);
}

.scene-item:hover {
  background: rgba(0, 0, 0, 0.8);
  transform: translateY(-2px);
}

.scene-item.active {
  background: #722ed1;
}

.scene-icon {
  font-size: 1.5rem;
}

.scene-name {
  color: white;
  font-size: 0.75rem;
}

/* 指南针 */
.compass {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 50px;
  height: 50px;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 50;
  backdrop-filter: blur(10px);
}

.compass-n {
  color: #ff4d4f;
  font-weight: bold;
  font-size: 1rem;
}

/* 响应式 */
@media (max-width: 768px) {
  .controls-panel {
    bottom: 10px;
    padding: 8px 12px;
  }
  
  .controls-panel button {
    width: 35px;
    height: 35px;
    font-size: 0.9rem;
  }
  
  .scene-switcher {
    top: 10px;
    left: 10px;
    flex-wrap: wrap;
    max-width: 200px;
  }
  
  .scene-item {
    padding: 8px 10px;
  }
  
  .hotspot-popup {
    top: auto;
    bottom: 80px;
    right: 10px;
    left: 10px;
    max-width: none;
  }
}
</style>
