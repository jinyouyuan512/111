<template>
  <MainLayout>
    <div class="guide-page">
      <!-- 顶部大Banner -->
      <div class="hero-banner">
        <div class="hero-bg"></div>
        <div class="hero-content">
          <h1>🗺️ 智慧导览</h1>
          <p>实地游览助手 · 语音讲解 · AR扫码 · 轨迹记录</p>
          <div class="hero-actions">
            <button class="hero-btn primary" @click="locateUser">
              <span class="btn-icon">📍</span>
              <span>定位当前位置</span>
            </button>
            <button class="hero-btn" @click="openARScanner">
              <span class="btn-icon">📱</span>
              <span>AR扫码识别</span>
            </button>
          </div>
        </div>
      </div>

      <!-- 功能Tab切换 -->
      <div class="feature-tabs">
        <button :class="{ active: activeTab === 'sites' }" @click="activeTab = 'sites'">
          <span>🏛️</span> 景点导览
        </button>
        <button :class="{ active: activeTab === 'audio' }" @click="activeTab = 'audio'">
          <span>🎧</span> 语音讲解
        </button>
        <button :class="{ active: activeTab === 'track' }" @click="activeTab = 'track'">
          <span>📍</span> 轨迹记录
        </button>
      </div>

      <!-- 景点导览Tab -->
      <div v-if="activeTab === 'sites'" class="sites-section">
        <div class="section-header">
          <h2>河北红色景点</h2>
          <div class="language-selector">
            <span>🌐</span>
            <select v-model="currentLanguage">
              <option value="zh">中文</option>
              <option value="en">English</option>
              <option value="ja">日本語</option>
              <option value="ko">한국어</option>
            </select>
          </div>
        </div>
        
        <div class="sites-grid">
          <div 
            v-for="site in sites" 
            :key="site.id"
            class="site-card"
            :class="{ active: currentSite?.id === site.id }"
            @click="selectSite(site)"
          >
            <div class="card-cover" :style="{ background: site.gradient }">
              <span class="card-icon">{{ site.icon }}</span>
              <div class="card-badges">
                <span class="badge level">{{ site.level }}</span>
              </div>
              <div class="card-features">
                <span v-if="site.hasAudio" title="语音讲解">🎧</span>
                <span v-if="site.hasAR" title="AR体验">📱</span>
                <span v-if="site.hasVR" title="VR全景">🔮</span>
              </div>
            </div>
            <div class="card-body">
              <h3>{{ site.name }}</h3>
              <p class="card-address">📍 {{ site.address }}</p>
              <p class="card-desc">{{ site.description }}</p>
              <div class="card-footer">
                <span class="distance">{{ site.distance }}</span>
                <span class="arrow">查看详情 →</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 语音讲解Tab -->
      <div v-if="activeTab === 'audio'" class="audio-section">
        <div class="audio-header">
          <h2>🎧 语音讲解库</h2>
          <p>专业讲解员录制，多语言支持</p>
        </div>

        <div class="audio-list">
          <div v-for="audio in audioGuides" :key="audio.id" class="audio-item" :class="{ playing: currentAudio?.id === audio.id }">
            <div class="audio-cover" :style="{ background: audio.gradient }">
              <span>{{ audio.icon }}</span>
            </div>
            <div class="audio-info">
              <h4>{{ audio.title }}</h4>
              <p>{{ audio.description }}</p>
              <div class="audio-meta">
                <span>⏱️ {{ audio.duration }}</span>
                <span>🎙️ {{ audio.narrator }}</span>
              </div>
            </div>
            <div class="audio-controls">
              <button class="play-btn" @click="toggleAudio(audio)">
                {{ currentAudio?.id === audio.id && isPlaying ? '⏸️' : '▶️' }}
              </button>
            </div>
          </div>
        </div>

        <!-- 音频播放器 -->
        <div v-if="currentAudio" class="audio-player">
          <div class="player-info">
            <span class="player-icon">{{ currentAudio.icon }}</span>
            <div class="player-text">
              <h4>{{ currentAudio.title }}</h4>
              <p>{{ currentAudio.narrator }}</p>
            </div>
          </div>
          <div class="player-progress">
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: audioProgress + '%' }"></div>
            </div>
            <div class="progress-time">
              <span>{{ formatTime(audioCurrentTime) }}</span>
              <span>{{ currentAudio.duration }}</span>
            </div>
          </div>
          <div class="player-controls">
            <button @click="seekAudio(-15)">⏪ 15s</button>
            <button class="main-control" @click="togglePlay">{{ isPlaying ? '⏸️' : '▶️' }}</button>
            <button @click="seekAudio(15)">15s ⏩</button>
            <select v-model="playbackSpeed" class="speed-select">
              <option value="0.75">0.75x</option>
              <option value="1">1x</option>
              <option value="1.25">1.25x</option>
              <option value="1.5">1.5x</option>
            </select>
          </div>
        </div>
      </div>

      <!-- 轨迹记录Tab -->
      <div v-if="activeTab === 'track'" class="track-section">
        <div class="track-header">
          <h2>📍 我的游览轨迹</h2>
          <button v-if="!isTracking" class="start-track-btn" @click="startTracking">
            🚶 开始记录
          </button>
          <button v-else class="stop-track-btn" @click="stopTracking">
            ⏹️ 停止记录
          </button>
        </div>

        <!-- 实时轨迹统计 -->
        <div v-if="isTracking" class="tracking-stats">
          <div class="stat-card">
            <span class="stat-icon">⏱️</span>
            <div class="stat-info">
              <span class="stat-value">{{ formatDuration(trackingDuration) }}</span>
              <span class="stat-label">游览时长</span>
            </div>
          </div>
          <div class="stat-card">
            <span class="stat-icon">📏</span>
            <div class="stat-info">
              <span class="stat-value">{{ trackingDistance.toFixed(2) }} km</span>
              <span class="stat-label">行走距离</span>
            </div>
          </div>
          <div class="stat-card">
            <span class="stat-icon">👣</span>
            <div class="stat-info">
              <span class="stat-value">{{ trackingSteps }}</span>
              <span class="stat-label">步数</span>
            </div>
          </div>
          <div class="stat-card">
            <span class="stat-icon">📍</span>
            <div class="stat-info">
              <span class="stat-value">{{ visitedSpots.length }}</span>
              <span class="stat-label">打卡景点</span>
            </div>
          </div>
        </div>

        <!-- 轨迹地图占位 -->
        <div class="track-map">
          <div class="map-placeholder">
            <span class="map-icon">🗺️</span>
            <p>轨迹地图</p>
            <p class="map-hint">{{ isTracking ? '正在记录您的游览轨迹...' : '开始记录后将显示实时轨迹' }}</p>
          </div>
        </div>

        <!-- 历史轨迹 -->
        <div class="track-history">
          <h3>📜 历史轨迹</h3>
          <div class="history-list">
            <div v-for="record in trackHistory" :key="record.id" class="history-item">
              <div class="history-icon" :style="{ background: record.gradient }">
                <span>{{ record.icon }}</span>
              </div>
              <div class="history-info">
                <h4>{{ record.title }}</h4>
                <p>{{ record.date }} · {{ record.duration }} · {{ record.distance }}</p>
                <div class="history-spots">
                  <span v-for="spot in record.spots" :key="spot">{{ spot }}</span>
                </div>
              </div>
              <button class="view-btn" @click="viewTrackDetail(record)">查看</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 景点详情弹窗 -->
      <el-dialog v-model="dialogVisible" :title="currentSite?.name" width="650px" top="8vh">
        <div v-if="currentSite" class="site-detail">
          <div class="detail-cover" :style="{ background: currentSite.gradient }">
            <span class="detail-icon">{{ currentSite.icon }}</span>
          </div>
          <div class="detail-info">
            <div class="info-row">
              <span class="label">📍 地址</span>
              <span class="value">{{ currentSite.address }}</span>
            </div>
            <div class="info-row">
              <span class="label">🏷️ 等级</span>
              <span class="value">{{ currentSite.level }}</span>
            </div>
          </div>
          <p class="detail-desc">{{ currentSite.description }}</p>
          
          <!-- 景点特色功能 -->
          <div class="detail-features">
            <div class="feature-item" v-if="currentSite.hasAudio" @click="playGuideAudio">
              <span class="feature-icon">🎧</span>
              <span>语音讲解</span>
            </div>
            <div class="feature-item" v-if="currentSite.hasAR" @click="openARForSite">
              <span class="feature-icon">📱</span>
              <span>AR识别</span>
            </div>
            <div class="feature-item" v-if="currentSite.hasVR">
              <span class="feature-icon">🔮</span>
              <span>VR全景</span>
            </div>
          </div>

          <!-- 景点展品/看点 -->
          <div class="detail-highlights" v-if="currentSite.highlights">
            <h4>🌟 主要看点</h4>
            <div class="highlights-list">
              <div v-for="(item, idx) in currentSite.highlights" :key="idx" class="highlight-item">
                <span class="highlight-num">{{ idx + 1 }}</span>
                <span class="highlight-text">{{ item }}</span>
              </div>
            </div>
          </div>

          <div class="detail-actions">
            <button class="action-btn primary" @click="startGuide">🎯 开始导览</button>
            <button class="action-btn" @click="addToTrack">📍 添加到轨迹</button>
            <button class="action-btn" @click="openNavigation">🚗 导航前往</button>
          </div>
        </div>
      </el-dialog>

      <!-- AR扫码弹窗 -->
      <el-dialog v-model="arDialogVisible" title="📱 AR扫码识别" width="500px">
        <div class="ar-scanner">
          <div class="scanner-area">
            <div class="scanner-frame">
              <div class="corner tl"></div>
              <div class="corner tr"></div>
              <div class="corner bl"></div>
              <div class="corner br"></div>
              <div class="scan-line"></div>
            </div>
            <p class="scanner-hint">将摄像头对准展品或标识牌</p>
          </div>
          <div class="ar-tips">
            <h4>💡 使用提示</h4>
            <ul>
              <li>确保光线充足</li>
              <li>保持手机稳定</li>
              <li>对准展品标识牌上的二维码</li>
              <li>识别成功后自动播放讲解</li>
            </ul>
          </div>
        </div>
      </el-dialog>

      <!-- 游览状态浮窗 -->
      <div v-if="isGuiding" class="guide-float">
        <div class="float-header">
          <span class="float-icon">🚶</span>
          <span>正在游览：{{ currentSite?.name }}</span>
        </div>
        <div class="float-stats">
          <div class="stat"><span class="num">{{ guideDuration }}</span><span class="unit">分钟</span></div>
          <div class="stat"><span class="num">{{ guideSteps }}</span><span class="unit">步</span></div>
        </div>
        <div class="float-actions">
          <button class="float-btn audio" @click="playGuideAudio">🎧 讲解</button>
          <button class="float-btn end" @click="endGuide">结束游览</button>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import MainLayout from '@/layouts/MainLayout.vue'

interface Site {
  id: number
  name: string
  icon: string
  address: string
  level: string
  description: string
  distance: string
  gradient: string
  hasAudio: boolean
  hasAR: boolean
  hasVR: boolean
  highlights?: string[]
}

interface AudioGuide {
  id: number
  title: string
  icon: string
  description: string
  duration: string
  narrator: string
  gradient: string
}

interface TrackRecord {
  id: number
  title: string
  icon: string
  date: string
  duration: string
  distance: string
  gradient: string
  spots: string[]
}

const activeTab = ref<'sites' | 'audio' | 'track'>('sites')
const currentLanguage = ref('zh')
const currentSite = ref<Site | null>(null)
const dialogVisible = ref(false)
const arDialogVisible = ref(false)
const isGuiding = ref(false)
const guideDuration = ref(0)
const guideSteps = ref(0)
let guideTimer: number | null = null

// 语音讲解相关
const currentAudio = ref<AudioGuide | null>(null)
const isPlaying = ref(false)
const audioProgress = ref(0)
const audioCurrentTime = ref(0)
const playbackSpeed = ref('1')
let audioTimer: number | null = null

// 轨迹记录相关
const isTracking = ref(false)
const trackingDuration = ref(0)
const trackingDistance = ref(0)
const trackingSteps = ref(0)
const visitedSpots = ref<string[]>([])
let trackTimer: number | null = null


const sites = ref<Site[]>([
  {
    id: 1, name: '西柏坡纪念馆', icon: '🏛️',
    address: '石家庄市平山县西柏坡镇',
    level: '5A景区',
    description: '新中国从这里走来。解放战争后期中共中央和解放军总部的所在地，是"两个务必"的诞生地，三大战役的指挥中心。',
    distance: '2.5km',
    gradient: 'linear-gradient(135deg, #c41e3a 0%, #8b0000 100%)',
    hasAudio: true, hasAR: true, hasVR: true,
    highlights: ['七届二中全会会址', '毛泽东旧居', '中央军委作战室', '西柏坡纪念碑']
  },
  {
    id: 2, name: '狼牙山五壮士纪念地', icon: '⛰️',
    address: '保定市易县狼牙山镇',
    level: '4A景区',
    description: '纪念在抗日战争中为掩护主力部队转移而英勇跳崖的五位八路军战士，展现了中华民族不屈不挠的民族精神。',
    distance: '15km',
    gradient: 'linear-gradient(135deg, #2c5530 0%, #1a3a1c 100%)',
    hasAudio: true, hasAR: false, hasVR: true,
    highlights: ['五壮士跳崖处', '狼牙山纪念塔', '棋盘陀', '红玛瑙溶洞']
  },
  {
    id: 3, name: '冉庄地道战遗址', icon: '🚇',
    address: '保定市清苑区冉庄镇',
    level: '4A景区',
    description: '抗日战争时期冀中平原抗日地道战的典型代表，地道全长约16公里，被誉为"地下长城"。',
    distance: '8km',
    gradient: 'linear-gradient(135deg, #5d4e37 0%, #3d3225 100%)',
    hasAudio: true, hasAR: true, hasVR: false,
    highlights: ['地道入口', '地下指挥所', '武器库', '民兵训练场']
  },
  {
    id: 4, name: '李大钊纪念馆', icon: '📚',
    address: '唐山市乐亭县大钊路',
    level: '4A景区',
    description: '为纪念中国共产主义运动的先驱、中国共产党的主要创始人之一李大钊同志而建立的纪念馆。',
    distance: '120km',
    gradient: 'linear-gradient(135deg, #1e3a5f 0%, #0d1f33 100%)',
    hasAudio: true, hasAR: false, hasVR: true,
    highlights: ['李大钊故居', '生平事迹展', '革命文献馆', '纪念广场']
  },
  {
    id: 5, name: '白洋淀雁翎队纪念馆', icon: '🚤',
    address: '雄安新区安新县',
    level: '5A景区',
    description: '展示白洋淀雁翎队抗日斗争史实，弘扬雁翎精神，是进行爱国主义教育的重要基地。',
    distance: '45km',
    gradient: 'linear-gradient(135deg, #1890ff 0%, #096dd9 100%)',
    hasAudio: true, hasAR: true, hasVR: true,
    highlights: ['雁翎队纪念馆', '水上游击战展示', '荷花淀', '白洋淀湿地']
  },
  {
    id: 6, name: '塞罕坝展览馆', icon: '🌲',
    address: '承德市围场县',
    level: '4A景区',
    description: '展示三代塞罕坝人艰苦奋斗、创造荒原变林海奇迹的感人事迹，弘扬塞罕坝精神。',
    distance: '280km',
    gradient: 'linear-gradient(135deg, #228b22 0%, #006400 100%)',
    hasAudio: true, hasAR: false, hasVR: true,
    highlights: ['塞罕坝展览馆', '望海楼', '七星湖', '百万亩林海']
  }
])

const audioGuides = ref<AudioGuide[]>([
  { id: 1, title: '西柏坡历史概述', icon: '🏛️', description: '了解西柏坡的历史地位和重要意义', duration: '15:30', narrator: '专业讲解员', gradient: 'linear-gradient(135deg, #c41e3a, #8b0000)' },
  { id: 2, title: '七届二中全会', icon: '📜', description: '详解七届二中全会的历史背景和重要决议', duration: '12:45', narrator: '历史学教授', gradient: 'linear-gradient(135deg, #d4380d, #ad2102)' },
  { id: 3, title: '狼牙山五壮士', icon: '⛰️', description: '讲述五壮士英勇抗敌的感人故事', duration: '18:20', narrator: '专业讲解员', gradient: 'linear-gradient(135deg, #2c5530, #1a3a1c)' },
  { id: 4, title: '地道战的智慧', icon: '🚇', description: '揭秘冉庄地道战的战术和建造技术', duration: '14:10', narrator: '军事专家', gradient: 'linear-gradient(135deg, #5d4e37, #3d3225)' },
  { id: 5, title: '李大钊生平', icon: '📚', description: '追忆中国共产主义运动先驱的一生', duration: '20:00', narrator: '历史学教授', gradient: 'linear-gradient(135deg, #1e3a5f, #0d1f33)' },
  { id: 6, title: '塞罕坝精神', icon: '🌲', description: '三代人创造绿色奇迹的感人故事', duration: '16:40', narrator: '专业讲解员', gradient: 'linear-gradient(135deg, #228b22, #006400)' }
])

const trackHistory = ref<TrackRecord[]>([
  { id: 1, title: '西柏坡一日游', icon: '🏛️', date: '2025-01-05', duration: '3小时25分', distance: '5.2km', gradient: 'linear-gradient(135deg, #c41e3a, #8b0000)', spots: ['纪念馆', '毛泽东旧居', '作战室'] },
  { id: 2, title: '狼牙山徒步', icon: '⛰️', date: '2025-01-03', duration: '4小时10分', distance: '8.7km', gradient: 'linear-gradient(135deg, #2c5530, #1a3a1c)', spots: ['纪念塔', '棋盘陀', '跳崖处'] },
  { id: 3, title: '冉庄地道探秘', icon: '🚇', date: '2024-12-28', duration: '2小时15分', distance: '3.1km', gradient: 'linear-gradient(135deg, #5d4e37, #3d3225)', spots: ['地道入口', '指挥所', '武器库'] }
])

const selectSite = (site: Site) => {
  currentSite.value = site
  dialogVisible.value = true
}

const locateUser = () => {
  ElMessage.info('正在定位您的位置...')
  setTimeout(() => {
    ElMessage.success('定位成功！已按距离排序')
  }, 1500)
}

const openARScanner = () => {
  arDialogVisible.value = true
  ElMessage.info('AR扫码功能已启动')
}

const openARForSite = () => {
  dialogVisible.value = false
  arDialogVisible.value = true
}

const startGuide = () => {
  dialogVisible.value = false
  isGuiding.value = true
  guideDuration.value = 0
  guideSteps.value = 0
  ElMessage.success(`开始游览：${currentSite.value?.name}`)
  
  guideTimer = window.setInterval(() => {
    guideDuration.value++
    guideSteps.value += Math.floor(Math.random() * 100) + 50
  }, 1000)
}

const endGuide = () => {
  if (guideTimer) {
    clearInterval(guideTimer)
    guideTimer = null
  }
  
  ElMessageBox.confirm(
    `本次游览${guideDuration.value}分钟，共${guideSteps.value}步。是否生成游记？`,
    '游览结束',
    { confirmButtonText: '生成游记', cancelButtonText: '稍后', type: 'success' }
  ).then(() => {
    ElMessage.success('游记生成成功！')
  }).catch(() => {})
  
  isGuiding.value = false
}

const playGuideAudio = () => {
  if (currentSite.value?.hasAudio) {
    ElMessage.success(`正在播放：${currentSite.value.name} 语音讲解`)
  } else {
    ElMessage.warning('该景点暂无语音讲解')
  }
}

const openNavigation = () => {
  ElMessage.success('正在打开导航...')
}

const addToTrack = () => {
  if (currentSite.value) {
    visitedSpots.value.push(currentSite.value.name)
    ElMessage.success(`已添加 ${currentSite.value.name} 到轨迹`)
  }
}

// 语音讲解方法
const toggleAudio = (audio: AudioGuide) => {
  if (currentAudio.value?.id === audio.id) {
    togglePlay()
  } else {
    currentAudio.value = audio
    isPlaying.value = true
    audioProgress.value = 0
    audioCurrentTime.value = 0
    startAudioProgress()
    ElMessage.success(`正在播放：${audio.title}`)
  }
}

const togglePlay = () => {
  isPlaying.value = !isPlaying.value
  if (isPlaying.value) {
    startAudioProgress()
  } else {
    stopAudioProgress()
  }
}

const startAudioProgress = () => {
  if (audioTimer) clearInterval(audioTimer)
  audioTimer = window.setInterval(() => {
    if (audioProgress.value < 100) {
      audioProgress.value += 0.5
      audioCurrentTime.value += 0.5
    } else {
      stopAudioProgress()
      isPlaying.value = false
    }
  }, 500)
}

const stopAudioProgress = () => {
  if (audioTimer) {
    clearInterval(audioTimer)
    audioTimer = null
  }
}

const seekAudio = (seconds: number) => {
  audioCurrentTime.value = Math.max(0, audioCurrentTime.value + seconds)
  audioProgress.value = Math.min(100, Math.max(0, audioProgress.value + seconds / 2))
}

const formatTime = (seconds: number) => {
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

// 轨迹记录方法
const startTracking = () => {
  isTracking.value = true
  trackingDuration.value = 0
  trackingDistance.value = 0
  trackingSteps.value = 0
  visitedSpots.value = []
  ElMessage.success('开始记录游览轨迹')
  
  trackTimer = window.setInterval(() => {
    trackingDuration.value++
    trackingDistance.value += 0.01 + Math.random() * 0.02
    trackingSteps.value += Math.floor(Math.random() * 20) + 10
  }, 1000)
}

const stopTracking = () => {
  if (trackTimer) {
    clearInterval(trackTimer)
    trackTimer = null
  }
  
  ElMessageBox.confirm(
    `本次记录${formatDuration(trackingDuration.value)}，行走${trackingDistance.value.toFixed(2)}km。是否保存轨迹？`,
    '停止记录',
    { confirmButtonText: '保存', cancelButtonText: '放弃', type: 'info' }
  ).then(() => {
    ElMessage.success('轨迹已保存！')
  }).catch(() => {
    ElMessage.info('轨迹已放弃')
  })
  
  isTracking.value = false
}

const formatDuration = (seconds: number) => {
  const hours = Math.floor(seconds / 3600)
  const mins = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60
  if (hours > 0) {
    return `${hours}小时${mins}分${secs}秒`
  }
  return `${mins}分${secs}秒`
}

const viewTrackDetail = (record: TrackRecord) => {
  ElMessage.info(`查看轨迹：${record.title}`)
}

onUnmounted(() => {
  if (guideTimer) clearInterval(guideTimer)
  if (audioTimer) clearInterval(audioTimer)
  if (trackTimer) clearInterval(trackTimer)
})
</script>


<style scoped>
.guide-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f0f5ff 0%, #fff 100%);
}

/* Hero Banner */
.hero-banner {
  position: relative;
  height: 320px;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 50%, #0050b3 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: white;
  overflow: hidden;
}
.hero-bg {
  position: absolute;
  inset: 0;
  background-image: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.08'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
}
.hero-content { position: relative; z-index: 2; }
.hero-content h1 { font-size: 2.8rem; margin: 0 0 12px; font-weight: 700; text-shadow: 0 2px 10px rgba(0,0,0,0.2); }
.hero-content p { font-size: 1.1rem; margin: 0 0 25px; opacity: 0.9; }
.hero-actions { display: flex; gap: 15px; justify-content: center; flex-wrap: wrap; }
.hero-btn {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  background: rgba(255,255,255,0.15);
  color: white;
  border: 2px solid rgba(255,255,255,0.3);
  padding: 12px 28px;
  border-radius: 50px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}
.hero-btn:hover { background: rgba(255,255,255,0.25); border-color: rgba(255,255,255,0.5); }
.hero-btn.primary { background: white; color: #1890ff; border-color: white; }
.hero-btn.primary:hover { transform: translateY(-3px); box-shadow: 0 8px 30px rgba(0,0,0,0.3); }
.btn-icon { font-size: 1.2rem; }

/* Feature Tabs */
.feature-tabs {
  display: flex;
  justify-content: center;
  gap: 10px;
  padding: 20px;
  background: white;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  position: sticky;
  top: 0;
  z-index: 100;
}
.feature-tabs button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 28px;
  border: 2px solid #e0e0e0;
  border-radius: 25px;
  background: white;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.3s;
}
.feature-tabs button:hover { border-color: #1890ff; color: #1890ff; }
.feature-tabs button.active { background: #1890ff; color: white; border-color: #1890ff; }

/* Sites Section */
.sites-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px 60px;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  background: white;
  padding: 20px 25px;
  border-radius: 15px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
}
.section-header h2 { font-size: 1.5rem; margin: 0; color: #1a1a2e; }
.language-selector {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #f5f5f5;
  padding: 8px 15px;
  border-radius: 20px;
}
.language-selector select {
  border: none;
  background: transparent;
  font-size: 0.9rem;
  cursor: pointer;
  outline: none;
}

/* Sites Grid */
.sites-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 25px;
}
.site-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}
.site-card:hover { transform: translateY(-10px); box-shadow: 0 20px 50px rgba(24, 144, 255, 0.2); }
.site-card.active { box-shadow: 0 0 0 3px #1890ff, 0 20px 50px rgba(24, 144, 255, 0.3); }
.card-cover {
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}
.card-icon { font-size: 4rem; filter: drop-shadow(0 4px 10px rgba(0,0,0,0.3)); }
.card-badges { position: absolute; top: 15px; left: 15px; }
.badge { background: rgba(255,255,255,0.95); padding: 5px 12px; border-radius: 20px; font-size: 0.75rem; font-weight: 600; color: #1890ff; }
.card-features { position: absolute; bottom: 15px; right: 15px; display: flex; gap: 8px; }
.card-features span { width: 32px; height: 32px; background: rgba(255,255,255,0.9); border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 0.9rem; }
.card-body { padding: 20px; }
.card-body h3 { margin: 0 0 8px; font-size: 1.15rem; color: #1a1a2e; }
.card-address { font-size: 0.8rem; color: #888; margin: 0 0 10px; }
.card-desc { font-size: 0.9rem; color: #555; line-height: 1.6; margin: 0 0 15px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.card-footer { display: flex; justify-content: space-between; align-items: center; padding-top: 15px; border-top: 1px solid #f0f0f0; }
.distance { font-size: 0.85rem; color: #1890ff; font-weight: 600; }
.arrow { font-size: 0.85rem; color: #999; }
.site-card:hover .arrow { color: #1890ff; }

/* Audio Section */
.audio-section {
  max-width: 900px;
  margin: 0 auto;
  padding: 30px 20px 60px;
}
.audio-header { text-align: center; margin-bottom: 30px; }
.audio-header h2 { font-size: 1.5rem; margin: 0 0 8px; color: #1a1a2e; }
.audio-header p { margin: 0; color: #666; }

.audio-list { display: flex; flex-direction: column; gap: 15px; margin-bottom: 30px; }
.audio-item {
  display: flex;
  align-items: center;
  gap: 20px;
  background: white;
  padding: 20px;
  border-radius: 15px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05);
  transition: all 0.3s;
}
.audio-item:hover { transform: translateX(5px); box-shadow: 0 8px 25px rgba(0,0,0,0.1); }
.audio-item.playing { border-left: 4px solid #1890ff; background: #f0f7ff; }
.audio-cover { width: 60px; height: 60px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 1.8rem; flex-shrink: 0; }
.audio-info { flex: 1; }
.audio-info h4 { margin: 0 0 5px; font-size: 1rem; color: #333; }
.audio-info p { margin: 0 0 8px; font-size: 0.85rem; color: #888; }
.audio-meta { display: flex; gap: 15px; font-size: 0.8rem; color: #999; }
.play-btn { width: 50px; height: 50px; border-radius: 50%; border: none; background: linear-gradient(135deg, #1890ff, #096dd9); color: white; font-size: 1.2rem; cursor: pointer; transition: all 0.2s; }
.play-btn:hover { transform: scale(1.1); }

/* Audio Player */
.audio-player {
  background: linear-gradient(135deg, #1890ff, #096dd9);
  color: white;
  padding: 25px;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(24, 144, 255, 0.3);
}
.player-info { display: flex; align-items: center; gap: 15px; margin-bottom: 20px; }
.player-icon { font-size: 2.5rem; }
.player-text h4 { margin: 0 0 5px; font-size: 1.1rem; }
.player-text p { margin: 0; opacity: 0.8; font-size: 0.9rem; }
.player-progress { margin-bottom: 20px; }
.progress-bar { height: 6px; background: rgba(255,255,255,0.3); border-radius: 3px; overflow: hidden; }
.progress-fill { height: 100%; background: white; transition: width 0.3s; }
.progress-time { display: flex; justify-content: space-between; margin-top: 8px; font-size: 0.85rem; opacity: 0.8; }
.player-controls { display: flex; align-items: center; justify-content: center; gap: 15px; }
.player-controls button { background: rgba(255,255,255,0.2); border: none; color: white; padding: 10px 20px; border-radius: 20px; cursor: pointer; font-size: 0.9rem; transition: all 0.2s; }
.player-controls button:hover { background: rgba(255,255,255,0.3); }
.player-controls .main-control { width: 60px; height: 60px; border-radius: 50%; font-size: 1.5rem; padding: 0; }
.speed-select { background: rgba(255,255,255,0.2); border: none; color: white; padding: 8px 12px; border-radius: 15px; cursor: pointer; }

/* Track Section */
.track-section {
  max-width: 1000px;
  margin: 0 auto;
  padding: 30px 20px 60px;
}
.track-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px; }
.track-header h2 { margin: 0; font-size: 1.5rem; color: #1a1a2e; }
.start-track-btn, .stop-track-btn {
  padding: 12px 25px;
  border: none;
  border-radius: 25px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s;
}
.start-track-btn { background: linear-gradient(135deg, #52c41a, #389e0d); color: white; }
.stop-track-btn { background: linear-gradient(135deg, #ff4d4f, #cf1322); color: white; }

.tracking-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
  margin-bottom: 25px;
}
.stat-card {
  display: flex;
  align-items: center;
  gap: 15px;
  background: white;
  padding: 20px;
  border-radius: 15px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05);
}
.stat-icon { font-size: 2rem; }
.stat-info { flex: 1; }
.stat-value { display: block; font-size: 1.5rem; font-weight: 700; color: #1890ff; }
.stat-label { font-size: 0.85rem; color: #888; }

.track-map {
  background: white;
  border-radius: 20px;
  height: 300px;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
  overflow: hidden;
}
.map-placeholder {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f0f5ff, #e6f7ff);
}
.map-icon { font-size: 4rem; margin-bottom: 15px; }
.map-placeholder p { margin: 0; color: #666; }
.map-hint { font-size: 0.9rem; color: #999; margin-top: 8px !important; }

.track-history h3 { margin: 0 0 20px; font-size: 1.2rem; color: #333; }
.history-list { display: flex; flex-direction: column; gap: 15px; }
.history-item {
  display: flex;
  align-items: center;
  gap: 20px;
  background: white;
  padding: 20px;
  border-radius: 15px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05);
}
.history-icon { width: 50px; height: 50px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; flex-shrink: 0; }
.history-info { flex: 1; }
.history-info h4 { margin: 0 0 5px; font-size: 1rem; color: #333; }
.history-info p { margin: 0 0 8px; font-size: 0.85rem; color: #888; }
.history-spots { display: flex; gap: 8px; flex-wrap: wrap; }
.history-spots span { background: #f0f5ff; color: #1890ff; padding: 3px 10px; border-radius: 12px; font-size: 0.75rem; }
.view-btn { padding: 8px 20px; background: #f0f5ff; color: #1890ff; border: none; border-radius: 20px; cursor: pointer; font-size: 0.85rem; transition: all 0.2s; }
.view-btn:hover { background: #1890ff; color: white; }

/* Dialog Detail */
.site-detail { padding: 0; }
.detail-cover { height: 180px; display: flex; align-items: center; justify-content: center; border-radius: 12px; margin-bottom: 20px; }
.detail-icon { font-size: 5rem; filter: drop-shadow(0 4px 10px rgba(0,0,0,0.3)); }
.detail-info { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; margin-bottom: 20px; }
.info-row { background: #f8f9fa; padding: 12px 15px; border-radius: 10px; }
.info-row .label { display: block; font-size: 0.8rem; color: #888; margin-bottom: 4px; }
.info-row .value { font-weight: 600; color: #333; }
.detail-desc { color: #555; line-height: 1.8; margin: 0 0 20px; text-align: justify; }
.detail-features { display: flex; gap: 15px; margin-bottom: 20px; }
.feature-item { flex: 1; display: flex; align-items: center; justify-content: center; gap: 8px; padding: 12px; background: #f0f7ff; border-radius: 10px; font-size: 0.9rem; color: #1890ff; cursor: pointer; transition: all 0.2s; }
.feature-item:hover { background: #1890ff; color: white; }
.feature-icon { font-size: 1.2rem; }
.detail-highlights { margin-bottom: 20px; }
.detail-highlights h4 { margin: 0 0 12px; font-size: 1rem; color: #333; }
.highlights-list { display: flex; flex-direction: column; gap: 8px; }
.highlight-item { display: flex; align-items: center; gap: 10px; padding: 10px 15px; background: #fafafa; border-radius: 8px; }
.highlight-num { width: 24px; height: 24px; background: #1890ff; color: white; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 0.8rem; font-weight: 600; }
.highlight-text { font-size: 0.9rem; color: #555; }
.detail-actions { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }
.action-btn { padding: 12px; border: 1px solid #e0e0e0; border-radius: 10px; background: white; cursor: pointer; font-size: 0.9rem; transition: all 0.2s; }
.action-btn:hover { border-color: #1890ff; color: #1890ff; }
.action-btn.primary { background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%); color: white; border: none; }

/* AR Scanner */
.ar-scanner { text-align: center; }
.scanner-area { background: #1a1a2e; border-radius: 15px; padding: 40px; margin-bottom: 20px; }
.scanner-frame { width: 250px; height: 250px; margin: 0 auto 20px; position: relative; border: 2px solid rgba(255,255,255,0.3); }
.corner { position: absolute; width: 30px; height: 30px; border: 3px solid #1890ff; }
.corner.tl { top: -3px; left: -3px; border-right: none; border-bottom: none; }
.corner.tr { top: -3px; right: -3px; border-left: none; border-bottom: none; }
.corner.bl { bottom: -3px; left: -3px; border-right: none; border-top: none; }
.corner.br { bottom: -3px; right: -3px; border-left: none; border-top: none; }
.scan-line { position: absolute; top: 0; left: 0; right: 0; height: 2px; background: linear-gradient(90deg, transparent, #1890ff, transparent); animation: scan 2s linear infinite; }
@keyframes scan { 0% { top: 0; } 100% { top: 100%; } }
.scanner-hint { color: rgba(255,255,255,0.7); margin: 0; }
.ar-tips { text-align: left; background: #f5f5f5; padding: 20px; border-radius: 12px; }
.ar-tips h4 { margin: 0 0 12px; color: #333; }
.ar-tips ul { margin: 0; padding-left: 20px; color: #666; }
.ar-tips li { margin-bottom: 8px; }

/* Guide Float */
.guide-float {
  position: fixed;
  bottom: 30px;
  right: 30px;
  background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%);
  color: white;
  padding: 20px;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(82, 196, 26, 0.4);
  z-index: 1000;
  min-width: 220px;
}
.float-header { display: flex; align-items: center; gap: 10px; margin-bottom: 15px; font-weight: 600; font-size: 0.9rem; }
.float-icon { font-size: 1.5rem; }
.float-stats { display: flex; gap: 20px; margin-bottom: 15px; }
.float-stats .stat { text-align: center; }
.float-stats .num { display: block; font-size: 1.5rem; font-weight: 700; }
.float-stats .unit { font-size: 0.75rem; opacity: 0.8; }
.float-actions { display: flex; gap: 10px; }
.float-btn { flex: 1; padding: 10px; background: rgba(255,255,255,0.2); border: 1px solid rgba(255,255,255,0.4); color: white; border-radius: 10px; cursor: pointer; font-size: 0.85rem; transition: all 0.2s; }
.float-btn:hover { background: rgba(255,255,255,0.3); }
.float-btn.end { background: rgba(255,255,255,0.9); color: #52c41a; }

@media (max-width: 992px) { .sites-grid { grid-template-columns: repeat(2, 1fr); } .tracking-stats { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 600px) {
  .sites-grid { grid-template-columns: 1fr; }
  .hero-content h1 { font-size: 2rem; }
  .detail-actions { grid-template-columns: 1fr; }
  .tracking-stats { grid-template-columns: 1fr; }
  .feature-tabs { flex-wrap: wrap; }
  .hero-actions { flex-direction: column; }
}
</style>
