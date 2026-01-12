<template>
  <div class="home-page">
    <!-- 顶部导航 -->
    <header
      class="header"
      :class="{ scrolled: currentSection > 0 }"
    >
      <div class="header-container">
        <div
          class="logo-area"
          @click="scrollToSection(0)"
        >
          <Logo
            size="medium"
            width="45px"
            height="45px"
          />
          <div class="logo-text">
            <h1>冀忆红途</h1>
            <p>河北红色文化数字平台</p>
          </div>
        </div>

        <nav class="main-nav">
          <a
            class="nav-link"
            :class="{ active: currentSection === 0 }"
            @click="scrollToSection(0)"
          >首页</a>
          <a
            class="nav-link"
            @click="navigateTo('/academy')"
          >传承学院</a>
          <a
            class="nav-link"
            @click="navigateTo('/tourism')"
          >智慧旅游</a>
          <a
            class="nav-link"
            @click="navigateTo('/creative')"
          >众创空间</a>
          <a
            class="nav-link"
            @click="navigateTo('/mall')"
          >文创商城</a>
          <a
            class="nav-link"
            @click="navigateTo('/social')"
          >红色社区</a>
        </nav>

        <div class="header-actions">
          <template v-if="userStore.token">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-avatar :src="userStore.userInfo?.avatar" size="small" />
                <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-if="userStore.userInfo?.role === 'admin'" command="admin">
                    <span style="color: #a0182f; font-weight: 600;">⚙️ 管理后台</span>
                  </el-dropdown-item>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                  <el-dropdown-item command="cart">购物车</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button
              type="danger"
              @click="handleLogin"
            >
              登录
            </el-button>
          </template>
        </div>
      </div>
    </header>

    <!-- 全屏滚动容器 -->
    <div
      ref="container"
      class="fullpage-container"
      @wheel="handleWheel"
    >
      <!-- 第1屏：首屏品牌展示 -->
      <section
        class="fullpage-section section-hero"
        :class="{ active: currentSection === 0 }"
      >
        <!-- 背景轮播 -->
        <div class="hero-carousel">
          <div 
            v-for="(slide, index) in heroSlides" 
            :key="index"
            class="carousel-slide"
            :class="{ active: currentSlide === index }"
            :style="{ backgroundImage: `linear-gradient(rgba(139, 30, 63, 0.7), rgba(196, 30, 58, 0.7)), url(${slide.image})` }"
          >
            <div class="slide-caption">
              <h3>{{ slide.title }}</h3>
              <p>{{ slide.subtitle }}</p>
            </div>
          </div>
          
          <!-- 轮播指示器 -->
          <div class="carousel-indicators">
            <div 
              v-for="(slide, index) in heroSlides" 
              :key="index"
              class="indicator-dot"
              :class="{ active: currentSlide === index }"
              @click="changeSlide(index)"
            />
          </div>
        </div>
        
        <div class="hero-bg">
          <div class="bg-pattern" />
          <div class="hero-overlay" />
        </div>
        
        <!-- 中心内容 -->
        <div class="section-content">
          <div class="hero-center">
            <!-- Logo -->
            <div class="hero-logo-large">
              <Logo
                size="large"
                width="100px"
                height="100px"
              />
            </div>

            <!-- 主标题 -->
            <h1 class="hero-title">
              冀忆红途
            </h1>
            <p class="hero-subtitle">
              河北红色文化数字平台
            </p>

            <!-- 口号 -->
            <div class="hero-slogan">
              <span>传承红色基因</span>
              <span class="slogan-dot">·</span>
              <span>赓续燕赵血脉</span>
            </div>

            <!-- 行动按钮 -->
            <div class="hero-actions">
              <el-button
                type="danger"
                size="large"
                class="btn-explore"
                @click="scrollToSection(1)"
              >
                开始探索
              </el-button>
            </div>
          </div>
        </div>

        <!-- 滚动提示 -->
        <div
          class="scroll-indicator"
          @click="scrollToSection(1)"
        >
          <div class="scroll-icon">
            <span>↓</span>
          </div>
          <p class="scroll-text">
            向下滚动探索
          </p>
        </div>
      </section>

      <!-- 第2屏：沉浸式故事墙 -->
      <section
        class="fullpage-section section-story"
        :class="{ active: currentSection === 1 }"
      >
        <div class="section-content story-layout">
          <!-- 左侧：视频背景 -->
          <div class="story-video">
            <div class="video-container">
              <!-- 视频播放器 -->
              <video 
                ref="storyVideo"
                class="story-video-player"
                :src="currentStoryVideo"
                autoplay
                loop
                muted
                playsinline
              />
              
              <!-- 视频遮罩层 -->
              <div class="video-overlay">
                <div class="video-info">
                  <div class="video-badge">
                    <span class="badge-icon">🎬</span>
                    <span class="badge-text">历史影像</span>
                  </div>
                  <h3 class="video-title">
                    {{ currentStoryTitle }}
                  </h3>
                  <p class="video-subtitle">
                    {{ currentStorySubtitle }}
                  </p>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 右侧：时间轴叙事 -->
          <div
            ref="timelineRef"
            class="story-timeline"
            @wheel.stop.prevent="handleTimelineWheel"
          >
            <h2 class="timeline-title">
              <span class="title-icon">📜</span>
              红色燕赵 · 历史时刻
            </h2>
            <div class="timeline-container">
              <div 
                v-for="(event, index) in historyEvents" 
                :key="index" 
                class="timeline-item"
                :class="{ 
                  active: activeTimelineIndex === index,
                  passed: activeTimelineIndex > index 
                }"
                @mouseenter="activeTimelineIndex = index"
                @mouseleave="activeTimelineIndex = -1"
                @click="changeStoryVideo(index)"
              >
                <div class="timeline-dot">
                  <div class="dot-inner" />
                  <div class="dot-ring" />
                </div>
                <div class="timeline-content">
                  <div class="timeline-year">
                    {{ event.year }}
                  </div>
                  <h3 class="timeline-event">
                    {{ event.title }}
                  </h3>
                  <p class="timeline-desc">
                    {{ event.description }}
                  </p>
                  <div class="timeline-tags">
                    <span
                      v-for="tag in event.tags"
                      :key="tag"
                      class="timeline-tag"
                    >
                      {{ tag }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 进入体验按钮 -->
            <el-button 
              type="danger" 
              size="large" 
              class="btn-enter-experience" 
              @click="navigateTo('/experience')"
            >
              <span class="btn-icon">🥽</span>
              进入沉浸式体验
              <span class="btn-arrow">→</span>
            </el-button>
          </div>
        </div>
      </section>

      <!-- 第3屏：交互式河北地图 -->
      <section
        class="fullpage-section section-map"
        :class="{ active: currentSection === 2 }"
      >
        <div class="section-content map-layout">
          <!-- 地图主体 -->
          <div class="map-container">
            <!-- Leaflet 地图容器 -->
            <div 
              ref="mapContainer" 
              class="map-placeholder"
              style="width: 100%; height: 100%; border-radius: 20px; overflow: hidden;"
            />
            
            <!-- 景点卡片悬浮显示 -->
            <transition name="card-fade">
              <div
                v-if="hoveredSite !== null"
                class="site-card"
                :style="getSiteCardPosition(hoveredSite)"
              >
                <div class="site-card-image">
                  <div class="site-card-icon">
                    {{ redSites[hoveredSite].icon }}
                  </div>
                </div>
                <div class="site-card-content">
                  <h3>{{ redSites[hoveredSite].name }}</h3>
                  <p>{{ redSites[hoveredSite].description }}</p>
                  <div class="site-card-meta">
                    <span>📍 {{ redSites[hoveredSite].location }}</span>
                    <span>⭐ {{ redSites[hoveredSite].rating }}</span>
                  </div>
                </div>
              </div>
            </transition>
          </div>
          
          <!-- 右侧推荐路线 -->
          <div
            class="routes-panel"
            @wheel.stop.prevent="handleRoutesPanelWheel"
          >
            <h2 class="routes-title">
              推荐红色路线
            </h2>
            <div class="routes-list">
              <div
                v-for="(route, index) in recommendedRoutes"
                :key="index" 
                class="route-item"
                @click="showRoute(route)"
              >
                <div class="route-icon">
                  {{ route.icon }}
                </div>
                <div class="route-info">
                  <h3>{{ route.name }}</h3>
                  <p>{{ route.sites }} · {{ route.duration }}</p>
                </div>
                <div class="route-arrow">
                  →
                </div>
              </div>
            </div>
            
            <!-- 热门景点排行 -->
            <div class="hot-sites">
              <h3 class="hot-sites-title">
                🔥 本月热门景点
              </h3>
              <div class="hot-sites-list">
                <div
                  v-for="(site, index) in hotSites"
                  :key="index"
                  class="hot-site-item"
                >
                  <span class="hot-rank">{{ index + 1 }}</span>
                  <span class="hot-name">{{ site }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 第4屏：文创市集 -->
      <section
        class="fullpage-section section-market"
        :class="{ active: currentSection === 3 }"
      >
        <div class="section-content market-layout">
          <h2 class="market-title">
            红色文创市集
          </h2>
          <p class="market-subtitle">
            传统集市风格 · 文创精品荟萃
          </p>
          
          <!-- 横向滚动市集 -->
          <div class="market-scroll-container">
            <div class="market-stalls">
              <!-- 本周爆款区 -->
              <div class="market-stall featured">
                <div class="stall-header">
                  <div class="stall-icon">
                    🔥
                  </div>
                  <h3 class="stall-title">
                    本周爆款
                  </h3>
                </div>
                <div class="stall-products">
                  <div
                    v-for="(product, index) in hotProducts"
                    :key="index"
                    class="product-card"
                  >
                    <div
                      class="product-image"
                      :style="{ background: product.color }"
                    >
                      <div class="product-icon">
                        {{ product.icon }}
                      </div>
                    </div>
                    <div class="product-info">
                      <h4>{{ product.name }}</h4>
                      <p class="product-price">
                        ¥{{ product.price }}
                      </p>
                      <div class="product-sales">
                        已售{{ product.sales }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- 设计师推荐区 -->
              <div class="market-stall designer">
                <div class="stall-header">
                  <div class="stall-icon">
                    🎨
                  </div>
                  <h3 class="stall-title">
                    设计师推荐
                  </h3>
                </div>
                <div class="stall-products">
                  <div
                    v-for="(product, index) in designerProducts"
                    :key="index"
                    class="product-card"
                  >
                    <div
                      class="product-image"
                      :style="{ background: product.color }"
                    >
                      <div class="product-icon">
                        {{ product.icon }}
                      </div>
                    </div>
                    <div class="product-info">
                      <h4>{{ product.name }}</h4>
                      <p class="product-price">
                        ¥{{ product.price }}
                      </p>
                      <div class="product-designer">
                        by {{ product.designer }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- 文化周边区 -->
              <div class="market-stall cultural">
                <div class="stall-header">
                  <div class="stall-icon">
                    📚
                  </div>
                  <h3 class="stall-title">
                    文化周边
                  </h3>
                </div>
                <div class="stall-products">
                  <div
                    v-for="(product, index) in culturalProducts"
                    :key="index"
                    class="product-card"
                  >
                    <div
                      class="product-image"
                      :style="{ background: product.color }"
                    >
                      <div class="product-icon">
                        {{ product.icon }}
                      </div>
                    </div>
                    <div class="product-info">
                      <h4>{{ product.name }}</h4>
                      <p class="product-price">
                        ¥{{ product.price }}
                      </p>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- 创意生活区 -->
              <div class="market-stall lifestyle">
                <div class="stall-header">
                  <div class="stall-icon">
                    🏮
                  </div>
                  <h3 class="stall-title">
                    创意生活
                  </h3>
                </div>
                <div class="stall-products">
                  <div
                    v-for="(product, index) in lifestyleProducts"
                    :key="index"
                    class="product-card"
                  >
                    <div
                      class="product-image"
                      :style="{ background: product.color }"
                    >
                      <div class="product-icon">
                        {{ product.icon }}
                      </div>
                    </div>
                    <div class="product-info">
                      <h4>{{ product.name }}</h4>
                      <p class="product-price">
                        ¥{{ product.price }}
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 文创展示区域 -->
          <div class="market-action">
            <p style="text-align: center; color: #666; font-size: 1.1rem;">
              更多精彩文创商品即将上线
            </p>
          </div>
        </div>
      </section>

      <!-- 第5屏：社交广场 -->
      <section
        class="fullpage-section section-social-square"
        :class="{ active: currentSection === 4 }"
      >
        <div class="section-content square-layout">
          <h2 class="square-title">
            红色足迹社交广场
          </h2>
          
          <!-- 中心大屏 -->
          <div class="central-screen">
            <div class="screen-frame">
              <div class="screen-content">
                <div class="video-player">
                  <div class="video-icon">
                    ▶️
                  </div>
                  <p class="video-title">
                    用户打卡精彩瞬间
                  </p>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 用户头像墙 -->
          <div class="avatar-walls">
            <div class="avatar-wall left">
              <div
                v-for="i in 12"
                :key="`left-${i}`"
                class="avatar-item"
                :style="{ animationDelay: `${i * 0.1}s` }"
              >
                <div
                  class="avatar-circle"
                  :style="{ background: getRandomGradient() }"
                >
                  {{ getRandomEmoji() }}
                </div>
              </div>
            </div>
            <div class="avatar-wall right">
              <div
                v-for="i in 12"
                :key="`right-${i}`"
                class="avatar-item"
                :style="{ animationDelay: `${i * 0.1}s` }"
              >
                <div
                  class="avatar-circle"
                  :style="{ background: getRandomGradient() }"
                >
                  {{ getRandomEmoji() }}
                </div>
              </div>
            </div>
          </div>
          
          <!-- 滚动弹幕 -->
          <div class="danmaku-container">
            <div
              v-for="(line, index) in danmakuLines"
              :key="index"
              class="danmaku-line"
            >
              <div class="danmaku-content">
                <span
                  v-for="(msg, i) in line"
                  :key="i"
                  class="danmaku-item"
                >
                  {{ msg }}
                </span>
              </div>
            </div>
          </div>
          
          <!-- 右侧成就榜 -->
          <div class="achievement-panel">
            <div class="achievement-header">
              <h3>🏆 本月之星</h3>
            </div>
            <div class="achievement-list">
              <div
                v-for="(user, index) in topUsers"
                :key="index"
                class="achievement-item"
              >
                <div
                  class="achievement-rank"
                  :class="`rank-${index + 1}`"
                >
                  {{ index + 1 }}
                </div>
                <div class="achievement-avatar">
                  {{ user.avatar }}
                </div>
                <div class="achievement-info">
                  <div class="achievement-name">
                    {{ user.name }}
                  </div>
                  <div class="achievement-score">
                    {{ user.score }}积分
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 进入社交平台按钮 -->
          <div class="square-action">
            <el-button
              type="danger"
              size="large"
              @click="navigateTo('/social')"
            >
              加入社交广场 →
            </el-button>
          </div>
        </div>
      </section>

      <!-- 第6屏：数据可视化大屏 -->
      <section
        class="fullpage-section section-dashboard"
        :class="{ active: currentSection === 5 }"
      >
        <div class="section-content dashboard-layout">
          <!-- 顶部标题 -->
          <div class="dashboard-header">
            <h1 class="dashboard-title">
              冀忆红途 · 平台数据总览
            </h1>
            <div class="dashboard-time">
              {{ currentTime }}
            </div>
          </div>
          
          <!-- 主体内容 -->
          <div class="dashboard-body">
            <!-- 左侧：地图热力图 -->
            <div class="dashboard-left">
              <div class="dashboard-panel">
                <h3 class="panel-title">
                  📍 地区访问热力图
                </h3>
                <div class="heatmap-container">
                  <svg
                    viewBox="0 0 400 300"
                    class="heatmap-svg"
                  >
                    <path
                      d="M 100 50 L 150 40 L 200 50 L 250 60 L 300 75 L 325 100 L 340 140 L 335 175 L 320 210 L 290 240 L 250 260 L 200 270 L 150 265 L 110 250 L 80 225 L 65 190 L 60 150 L 70 110 L 90 75 Z" 
                      fill="url(#heatGradient)" 
                      stroke="#ffd700" 
                      stroke-width="2"
                    />
                    <defs>
                      <radialGradient id="heatGradient">
                        <stop
                          offset="0%"
                          style="stop-color:#ff0000;stop-opacity:0.8"
                        />
                        <stop
                          offset="50%"
                          style="stop-color:#ff6b00;stop-opacity:0.6"
                        />
                        <stop
                          offset="100%"
                          style="stop-color:#8b1e3f;stop-opacity:0.3"
                        />
                      </radialGradient>
                    </defs>
                    <!-- 热点标记 -->
                    <circle
                      cx="175"
                      cy="140"
                      r="8"
                      fill="#ffd700"
                      class="heat-point"
                    >
                      <animate
                        attributeName="r"
                        values="8;12;8"
                        dur="2s"
                        repeatCount="indefinite"
                      />
                    </circle>
                    <circle
                      cx="210"
                      cy="160"
                      r="6"
                      fill="#ffd700"
                      class="heat-point"
                    >
                      <animate
                        attributeName="r"
                        values="6;10;6"
                        dur="2s"
                        repeatCount="indefinite"
                      />
                    </circle>
                    <circle
                      cx="140"
                      cy="175"
                      r="5"
                      fill="#ffd700"
                      class="heat-point"
                    >
                      <animate
                        attributeName="r"
                        values="5;9;5"
                        dur="2s"
                        repeatCount="indefinite"
                      />
                    </circle>
                  </svg>
                </div>
                <div class="heatmap-legend">
                  <div class="legend-item">
                    <span class="legend-color hot" />
                    <span>高访问量</span>
                  </div>
                  <div class="legend-item">
                    <span class="legend-color medium" />
                    <span>中访问量</span>
                  </div>
                  <div class="legend-item">
                    <span class="legend-color low" />
                    <span>低访问量</span>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 中间：核心数据大屏 -->
            <div class="dashboard-center">
              <div class="data-grid">
                <div
                  v-for="(data, index) in dashboardData"
                  :key="index"
                  class="data-card"
                >
                  <div class="data-icon">
                    {{ data.icon }}
                  </div>
                  <div class="data-value">
                    {{ data.value }}
                  </div>
                  <div class="data-label">
                    {{ data.label }}
                  </div>
                  <div
                    class="data-trend"
                    :class="data.trend"
                  >
                    {{ data.trend === 'up' ? '↑' : '↓' }} {{ data.change }}
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 右侧：实时动态流 -->
            <div class="dashboard-right">
              <div class="dashboard-panel">
                <h3 class="panel-title">
                  📊 实时动态
                </h3>
                <div class="activity-stream">
                  <div
                    v-for="(activity, index) in realtimeActivities"
                    :key="index" 
                    class="activity-item"
                    :style="{ animationDelay: `${index * 0.2}s` }"
                  >
                    <div class="activity-time">
                      {{ activity.time }}
                    </div>
                    <div class="activity-content">
                      <span class="activity-icon">{{ activity.icon }}</span>
                      <span class="activity-text">{{ activity.text }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 底部：合作伙伴 -->
          <div class="dashboard-footer">
            <div class="partners-scroll">
              <div class="partners-content">
                <div
                  v-for="i in 10"
                  :key="i"
                  class="partner-item"
                >
                  合作伙伴 {{ i }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>

    <!-- 侧边导航指示器 -->
    <div class="section-indicators">
      <div
        v-for="i in 6"
        :key="i"
        class="indicator"
        :class="{ active: currentSection === i - 1 }"
        @click="scrollToSection(i - 1)"
      >
        <span class="indicator-dot" />
        <span class="indicator-label">{{ getSectionName(i - 1) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import Logo from '@/components/Logo.vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const currentSection = ref(0)
const isScrolling = ref(false)
const container = ref<HTMLElement>()
const mapContainer = ref<HTMLElement>()
let map: L.Map | null = null
 

// 故事视频数据
const storyVideo = ref<HTMLVideoElement>()
const currentStoryIndex = ref(0)
const activeTimelineIndex = ref(-1)

type Site = {
  name: string
  lat: number
  lng: number
  icon: string
  type: string
  color: string
  location: string
  rating: string
  description: string
  x: number
  y: number
  hasImage: boolean
}
type RouteInfo = {
  icon: string
  name: string
  sites: string
  duration: string
}

const storyVideos = [
  {
    label: '西柏坡',
    title: '新中国从这里走来',
    subtitle: '1948年，中共中央在西柏坡指挥三大战役',
    // 使用占位视频，实际使用时替换为真实视频URL
    url: 'https://www.w3schools.com/html/mov_bbb.mp4'
  },
  {
    label: '狼牙山',
    title: '英雄气概永垂不朽',
    subtitle: '1941年，五壮士英勇跳崖，谱写抗战壮歌',
    url: 'https://www.w3schools.com/html/movie.mp4'
  },
  {
    label: '冉庄',
    title: '地道战的传奇',
    subtitle: '人民战争的伟大创举',
    url: 'https://www.w3schools.com/html/mov_bbb.mp4'
  },
  {
    label: '李大钊',
    title: '革命先驱的足迹',
    subtitle: '传播马克思主义的火种',
    url: 'https://www.w3schools.com/html/movie.mp4'
  }
]

const currentStoryVideo = computed(() => storyVideos[currentStoryIndex.value].url)
const currentStoryTitle = computed(() => storyVideos[currentStoryIndex.value].title)
const currentStorySubtitle = computed(() => storyVideos[currentStoryIndex.value].subtitle)

// 切换故事视频
const changeStoryVideo = (index: number) => {
  if (index >= 0 && index < storyVideos.length) {
    currentStoryIndex.value = index
    activeTimelineIndex.value = index
    if (storyVideo.value) {
      storyVideo.value.load()
      storyVideo.value.play()
    }
  }
}

// 处理时间轴区域的滚轮事件
const timelineRef = ref<HTMLElement>()
const handleTimelineWheel = (event: WheelEvent) => {
  const timeline = timelineRef.value || (event.currentTarget as HTMLElement)
  if (!timeline) return
  
  // 手动处理滚动
  timeline.scrollTop += event.deltaY
}

// 处理右侧路线面板的滚轮事件
const handleRoutesPanelWheel = (event: WheelEvent) => {
  const panel = event.currentTarget as HTMLElement
  if (!panel) return
  
  // 手动处理滚动
  panel.scrollTop += event.deltaY
}



// 历史事件时间轴
const historyEvents = [
  {
    year: '1921',
    title: '李大钊传播马克思主义',
    description: '李大钊在河北乐亭传播马克思主义思想，为中国共产党的成立奠定思想基础',
    tags: ['思想启蒙', '革命先驱']
  },
  {
    year: '1937',
    title: '狼牙山五壮士英勇跳崖',
    description: '八路军五位战士在狼牙山与日军激战，弹尽粮绝后英勇跳崖，展现了中华民族不屈的抗战精神',
    tags: ['英雄壮举', '抗日战争']
  },
  {
    year: '1942',
    title: '冉庄地道战创举',
    description: '冉庄人民创造性地开展地道战，成为人民战争的光辉典范',
    tags: ['人民战争', '智慧抗战']
  },
  {
    year: '1948',
    title: '西柏坡指挥三大战役',
    description: '中共中央在西柏坡指挥了辽沈、淮海、平津三大战役，为新中国的诞生奠定基础',
    tags: ['解放战争', '战略决战']
  }
]

// 河北红色景点数据 - 真实经纬度坐标，添加类型和颜色
const redSites: Site[] = [
  { name: '西柏坡', lat: 38.4667, lng: 113.9167, icon: '🏛️', type: '革命圣地', color: '#e74c3c', location: '石家庄市平山县', rating: '5.0', description: '新中国从这里走来，中共中央最后一个农村指挥所', x: 280, y: 450, hasImage: true },
  { name: '狼牙山', lat: 39.0333, lng: 115.2833, icon: '⛰️', type: '抗战遗址', color: '#f39c12', location: '保定市易县', rating: '4.9', description: '狼牙山五壮士英勇跳崖之地，抗战精神永存', x: 420, y: 380, hasImage: true },
  { name: '冉庄', lat: 38.7667, lng: 115.6333, icon: '🏘️', type: '抗战遗址', color: '#f39c12', location: '保定市清苑区', rating: '4.8', description: '地道战遗址，人民战争的伟大创举', x: 480, y: 420, hasImage: false },
  { name: '李大钊故居', lat: 39.4167, lng: 118.9000, icon: '🏠', type: '名人故居', color: '#3498db', location: '唐山市乐亭县', rating: '4.9', description: '中国共产主义运动先驱李大钊的故乡', x: 720, y: 320, hasImage: true },
  { name: '129师司令部', lat: 36.5833, lng: 113.6833, icon: '🎖️', type: '革命圣地', color: '#e74c3c', location: '邯郸市涉县', rating: '4.8', description: '刘伯承、邓小平领导的129师司令部旧址', x: 200, y: 580, hasImage: true },
  { name: '白求恩纪念馆', lat: 38.7333, lng: 114.9833, icon: '⚕️', type: '纪念馆', color: '#1abc9c', location: '石家庄市唐县', rating: '4.7', description: '纪念国际主义战士白求恩的英雄事迹', x: 380, y: 360, hasImage: false }
]

const hoveredSite = ref<number | null>(null)

// 推荐路线
const recommendedRoutes: RouteInfo[] = [
  { icon: '🎖️', name: '革命圣地之旅', sites: '西柏坡 → 狼牙山 → 冉庄', duration: '3天2晚' },
  { icon: '📚', name: '红色教育专线', sites: '李大钊故居 → 白求恩纪念馆', duration: '2天1晚' },
  { icon: '⛰️', name: '抗战英雄路线', sites: '狼牙山 → 129师司令部', duration: '2天1晚' },
  { icon: '🏛️', name: '经典红色游', sites: '西柏坡 → 李大钊故居 → 冉庄', duration: '4天3晚' }
]

// 热门景点
const hotSites = ['西柏坡', '狼牙山', '李大钊故居', '冉庄地道战', '白求恩纪念馆']

// 文创商品数据
const hotProducts = [
  { icon: '🎒', name: '红色记忆帆布包', price: 128, sales: '2.3k', color: 'linear-gradient(135deg, #c41e3a, #8b1e3f)' },
  { icon: '📖', name: '西柏坡笔记本', price: 68, sales: '1.8k', color: 'linear-gradient(135deg, #d4956c, #c41e3a)' },
  { icon: '🖼️', name: '革命海报装饰画', price: 198, sales: '1.5k', color: 'linear-gradient(135deg, #8b1e3f, #d4956c)' }
]

const designerProducts = [
  { icon: '🎨', name: '燕赵印象艺术画', price: 388, designer: '张艺', color: 'linear-gradient(135deg, #c41e3a, #d4956c)' },
  { icon: '🏺', name: '红色文化陶瓷杯', price: 158, designer: '李明', color: 'linear-gradient(135deg, #8b1e3f, #c41e3a)' },
  { icon: '🧣', name: '传承系列丝巾', price: 268, designer: '王芳', color: 'linear-gradient(135deg, #d4956c, #8b1e3f)' }
]

const culturalProducts = [
  { icon: '📚', name: '红色经典书籍', price: 88, color: 'linear-gradient(135deg, #8b1e3f, #c41e3a)' },
  { icon: '🎭', name: '京剧脸谱摆件', price: 168, color: 'linear-gradient(135deg, #c41e3a, #d4956c)' },
  { icon: '🖋️', name: '书法套装礼盒', price: 298, color: 'linear-gradient(135deg, #d4956c, #8b1e3f)' }
]

const lifestyleProducts = [
  { icon: '☕', name: '红色主题马克杯', price: 78, color: 'linear-gradient(135deg, #c41e3a, #8b1e3f)' },
  { icon: '🕯️', name: '香薰蜡烛礼盒', price: 138, color: 'linear-gradient(135deg, #8b1e3f, #d4956c)' },
  { icon: '🎁', name: '文创礼品套装', price: 388, color: 'linear-gradient(135deg, #d4956c, #c41e3a)' }
]

 

// 首屏轮播内容 - 河北红色景点真实图片
// 使用方法：将真实图片放入 frontend/public/images/hero/ 目录
// 图片命名：xibaipo.jpg, langyashan.jpg, ranzhuang.jpg, lidazhao.jpg
const USE_LOCAL_IMAGES = true // 设置为 true 使用本地图片，false 使用占位图片

const heroSlides = [
  {
    title: '西柏坡革命圣地',
    subtitle: '新中国从这里走来',
    image: USE_LOCAL_IMAGES 
      ? '/images/hero/xibaipo.jpg' 
      : 'https://images.unsplash.com/photo-1508804185872-d7badad00f7d?w=1920&q=80' // 中国红色建筑
  },
  {
    title: '狼牙山五壮士',
    subtitle: '英雄气概永垂不朽',
    image: USE_LOCAL_IMAGES 
      ? '/images/hero/langyashan.jpg' 
      : 'https://images.unsplash.com/photo-1519677100203-a0e668c92439?w=1920&q=80' // 壮丽山峰
  },
  {
    title: '冉庄地道战遗址',
    subtitle: '人民战争的伟大创举',
    image: USE_LOCAL_IMAGES 
      ? '/images/hero/ranzhuang.jpg' 
      : 'https://images.unsplash.com/photo-1548013146-72479768bada?w=1920&q=80' // 中国乡村历史建筑
  },
  {
    title: '李大钊故居',
    subtitle: '中国共产主义运动的先驱',
    image: USE_LOCAL_IMAGES 
      ? '/images/hero/lidazhao.jpg' 
      : 'https://images.unsplash.com/photo-1528127269322-539801943592?w=1920&q=80' // 中国传统庭院
  }
]

const currentSlide = ref(0)
let slideInterval: number | null = null

// 切换轮播
const changeSlide = (index: number) => {
  currentSlide.value = index
  resetSlideInterval()
}

// 自动轮播
const startSlideShow = () => {
  slideInterval = window.setInterval(() => {
    currentSlide.value = (currentSlide.value + 1) % heroSlides.length
  }, 5000)
}

const resetSlideInterval = () => {
  if (slideInterval) {
    clearInterval(slideInterval)
  }
  startSlideShow()
}

// 获取区块名称
const getSectionName = (index: number) => {
  const names = ['首页', '故事', '地图', '市集', '广场', '数据']
  return names[index] || ''
}

// 滚动到指定区块
const scrollToSection = (index: number) => {
  if (isScrolling.value || index < 0 || index > 5) return
  
  isScrolling.value = true
  currentSection.value = index
  
  // 直接控制fullpage-container的滚动位置
  if (container.value) {
    container.value.scrollTo({
      top: index * window.innerHeight,
      behavior: 'smooth'
    })
  }
  
  setTimeout(() => {
    isScrolling.value = false
  }, 1000)
}

// 处理鼠标滚轮
const handleWheel = (event: WheelEvent) => {
  if (isScrolling.value) {
    event.preventDefault()
    return
  }
  
  event.preventDefault()
  
  // 增加滚动阈值，避免过于敏感
  const threshold = 50
  
  if (Math.abs(event.deltaY) < threshold) {
    return
  }
  
  if (event.deltaY > 0) {
    // 向下滚动
    if (currentSection.value < 5) {
      scrollToSection(currentSection.value + 1)
    }
  } else {
    // 向上滚动
    if (currentSection.value > 0) {
      scrollToSection(currentSection.value - 1)
    }
  }
}

// 触摸事件处理
let touchStartY = 0
let touchEndY = 0

const handleTouchStart = (event: TouchEvent) => {
  touchStartY = event.touches[0].clientY
}

const handleTouchMove = (event: TouchEvent) => {
  touchEndY = event.touches[0].clientY
}

const handleTouchEnd = () => {
  if (isScrolling.value) return
  
  const swipeThreshold = 50
  const diff = touchStartY - touchEndY
  
  if (Math.abs(diff) > swipeThreshold) {
    if (diff > 0 && currentSection.value < 5) {
      // 向上滑动，显示下一屏
      scrollToSection(currentSection.value + 1)
    } else if (diff < 0 && currentSection.value > 0) {
      // 向下滑动，显示上一屏
      scrollToSection(currentSection.value - 1)
    }
  }
  
  touchStartY = 0
  touchEndY = 0
}

// 导航
const navigateTo = (path: string) => {
  router.push(path)
}

// 登录
const handleLogin = () => {
  router.push('/login')
}

// 处理用户下拉菜单命令
const handleCommand = (command: string) => {
  switch (command) {
    case 'admin':
      router.push('/admin')
      break
    case 'profile':
      ElMessage.info('个人中心功能开发中')
      break
    case 'orders':
      router.push('/orders')
      break
    case 'cart':
      router.push('/cart')
      break
    case 'logout':
      userStore.logout()
      ElMessage.success('已退出登录')
      break
  }
}

// 地图相关方法
const getSiteCardPosition = (index: number) => {
  const site = redSites[index]
  return {
    left: `${site.x + 50}px`,
    top: `${site.y - 50}px`
  }
}

const selectSite = (site: Site) => {
  console.log('选择景点:', site.name)
  navigateTo('/tourism')
}

const showRoute = (route: RouteInfo) => {
  console.log('显示路线:', route.name)
  navigateTo('/tourism')
}

// 社交广场数据
const danmakuLines = [
  ['今天打卡西柏坡！', '狼牙山风景太美了', '红色文化值得传承', '感受革命精神', '推荐大家来参观'],
  ['李大钊故居很有意义', '冉庄地道战震撼', '学到了很多历史知识', '带孩子来教育', '五星推荐'],
  ['红色旅游真不错', '文创商品很精美', '买了好多纪念品', '下次还要来', '已经推荐给朋友了']
]

const topUsers = [
  { avatar: '👨', name: '红色追梦人', score: 8520 },
  { avatar: '👩', name: '燕赵行者', score: 7680 },
  { avatar: '👦', name: '历史探索者', score: 6890 },
  { avatar: '👧', name: '文化传承者', score: 6120 },
  { avatar: '🧑', name: '红途先锋', score: 5540 }
]

const getRandomGradient = () => {
  const gradients = [
    'linear-gradient(135deg, #8b1e3f, #c41e3a)',
    'linear-gradient(135deg, #c41e3a, #d4956c)',
    'linear-gradient(135deg, #d4956c, #8b1e3f)',
    'linear-gradient(135deg, #8b1e3f, #d4956c)'
  ]
  return gradients[Math.floor(Math.random() * gradients.length)]
}

const getRandomEmoji = () => {
  const emojis = ['👨', '👩', '👦', '👧', '🧑', '👴', '👵', '🧒']
  return emojis[Math.floor(Math.random() * emojis.length)]
}

// 数据大屏数据
const currentTime = ref('')
const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit', 
    hour: '2-digit', 
    minute: '2-digit', 
    second: '2-digit' 
  })
}

const dashboardData = [
  { icon: '👥', label: '总用户数', value: '128.5万', change: '12.3%', trend: 'up' },
  { icon: '📱', label: '今日访问', value: '45.2万', change: '8.7%', trend: 'up' },
  { icon: '🏛️', label: '景点数量', value: '500+', change: '5.2%', trend: 'up' },
  { icon: '📚', label: '内容总量', value: '12万+', change: '15.6%', trend: 'up' },
  { icon: '🎨', label: '文创商品', value: '3200+', change: '18.9%', trend: 'up' },
  { icon: '💬', label: '社交互动', value: '89.6万', change: '22.4%', trend: 'up' }
]

const realtimeActivities = [
  { time: '14:32:15', icon: '👤', text: '用户"红色追梦人"打卡西柏坡' },
  { time: '14:31:48', icon: '🛍️', text: '用户购买了"红色记忆帆布包"' },
  { time: '14:31:22', icon: '📝', text: '新增评论："狼牙山风景太美了"' },
  { time: '14:30:55', icon: '⭐', text: '用户"燕赵行者"完成成就' },
  { time: '14:30:31', icon: '🎨', text: '设计师上传新作品' },
  { time: '14:30:08', icon: '📍', text: '用户开启智慧导览服务' },
  { time: '14:29:42', icon: '🎓', text: '新用户注册加入平台' },
  { time: '14:29:15', icon: '💬', text: '社交平台新增动态' }
]

// 键盘导航
const handleKeydown = (event: KeyboardEvent) => {
  if (event.key === 'ArrowDown' || event.key === 'PageDown') {
    event.preventDefault()
    if (currentSection.value < 5) {
      scrollToSection(currentSection.value + 1)
    }
  } else if (event.key === 'ArrowUp' || event.key === 'PageUp') {
    event.preventDefault()
    if (currentSection.value > 0) {
      scrollToSection(currentSection.value - 1)
    }
  } else if (event.key === 'Home') {
    event.preventDefault()
    scrollToSection(0)
  } else if (event.key === 'End') {
    event.preventDefault()
    scrollToSection(5)
  }
}

onMounted(() => {
  window.addEventListener('keydown', handleKeydown)
  
  // 添加触摸事件监听
  if (container.value) {
    container.value.addEventListener('touchstart', handleTouchStart, { passive: true })
    container.value.addEventListener('touchmove', handleTouchMove, { passive: true })
    container.value.addEventListener('touchend', handleTouchEnd)
  }
  
  startSlideShow()
  updateTime()
  setInterval(updateTime, 1000)
  
  // 初始化 Leaflet 地图
  setTimeout(() => {
    if (mapContainer.value) {
      // 河北省的边界范围
      const hebeiSouthWest = L.latLng(36.0, 113.5) // 西南角
      const hebeiNorthEast = L.latLng(42.5, 119.8) // 东北角
      const hebeiBounds = L.latLngBounds(hebeiSouthWest, hebeiNorthEast)
      
      // 创建地图，中心设置为河北省中心，限制在河北省范围内
      map = L.map(mapContainer.value, {
        center: [38.8, 115.5], // 河北省中心坐标
        zoom: 7,
        minZoom: 7,
        maxZoom: 10,
        zoomControl: true,
        scrollWheelZoom: false,
        maxBounds: hebeiBounds, // 限制地图边界为河北省
        maxBoundsViscosity: 1.0 // 边界完全限制，不能拖出范围
      })
      
      // 添加 CartoDB 深色瓦片层（更美观）
      L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
        attribution: '© OpenStreetMap © CartoDB',
        maxZoom: 18,
        subdomains: 'abcd'
      }).addTo(map)
      
      // 让地图适应河北省边界
      map.fitBounds(hebeiBounds)
      
      // 绘制河北省边界（更精确的多边形）
      L.polygon([
        // 从西北角开始，顺时针绘制
        [42.6, 114.5],  // 最西北（张家口西北）
        [42.4, 115.2],  // 张家口北部
        [42.2, 115.8],  // 张家口东北
        [41.9, 116.7],  // 承德西部
        [42.0, 117.5],  // 承德北部
        [41.5, 118.2],  // 承德东北
        [40.9, 118.8],  // 承德东部
        [40.5, 119.3],  // 秦皇岛西部
        [40.0, 119.6],  // 秦皇岛（最东北角）
        [39.7, 119.5],  // 秦皇岛南部
        [39.5, 119.2],  // 唐山东北
        [39.3, 118.8],  // 唐山东部
        [39.1, 118.3],  // 唐山中部
        [39.0, 117.8],  // 唐山西部（天津边界）
        [38.8, 117.5],  // 廊坊东部
        [38.6, 117.2],  // 廊坊南部
        [38.3, 116.8],  // 沧州北部
        [38.0, 116.9],  // 沧州东部
        [37.7, 116.7],  // 沧州中部
        [37.4, 116.5],  // 沧州南部
        [37.2, 116.2],  // 衡水东部
        [37.0, 115.8],  // 衡水南部
        [36.8, 115.5],  // 邢台东部
        [36.5, 115.2],  // 邢台南部
        [36.4, 114.8],  // 邯郸东部
        [36.3, 114.5],  // 邯郸南部（最南端）
        [36.5, 114.2],  // 邯郸西南
        [36.8, 114.0],  // 邯郸西部
        [37.2, 113.8],  // 邢台西部
        [37.6, 113.7],  // 石家庄西南
        [38.0, 113.6],  // 石家庄西部
        [38.4, 113.7],  // 西柏坡区域
        [38.8, 113.9],  // 保定西部
        [39.2, 114.2],  // 保定西北
        [39.6, 114.4],  // 保定北部
        [40.0, 114.6],  // 张家口南部
        [40.5, 114.5],  // 张家口中部
        [41.0, 114.3],  // 张家口西部
        [41.5, 114.2],  // 张家口西北
        [42.0, 114.3]   // 回到起点附近
      ], {
        color: '#e74c3c',
        weight: 3,
        opacity: 0.9,
        fillColor: '#e74c3c',
        fillOpacity: 0.03
      }).addTo(map)
      
      // 添加河北省标签
      L.marker([38.8, 115.5], {
        icon: L.divIcon({
          className: 'province-label',
          html: '<div style="font-size: 24px; font-weight: bold; color: #c41e3a; text-shadow: 2px 2px 4px rgba(255,255,255,0.8);">河北省</div>',
          iconSize: [100, 40],
          iconAnchor: [50, 20]
        })
      }).addTo(map)
      
      // 添加景点标记 - 使用不同颜色
      redSites.forEach(site => {
        // 根据是否有图片决定标记样式
        const markerHtml = site.hasImage 
          ? `<div style="
              width: 60px;
              height: 70px;
              position: relative;
              cursor: pointer;
            ">
              <div style="
                width: 60px;
                height: 50px;
                background: white;
                border: 3px solid ${site.color};
                border-radius: 8px;
                box-shadow: 0 4px 12px rgba(0,0,0,0.3);
                overflow: hidden;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 24px;
              ">${site.icon}</div>
              <div style="
                position: absolute;
                bottom: 0;
                left: 50%;
                transform: translateX(-50%);
                width: 0;
                height: 0;
                border-left: 8px solid transparent;
                border-right: 8px solid transparent;
                border-top: 12px solid ${site.color};
              "></div>
            </div>`
          : `<div style="
              width: 40px;
              height: 40px;
              background: ${site.color};
              border: 3px solid white;
              border-radius: 50%;
              display: flex;
              align-items: center;
              justify-content: center;
              font-size: 18px;
              box-shadow: 0 3px 10px rgba(0,0,0,0.3);
              cursor: pointer;
            ">${site.icon}</div>`
        
        const icon = L.divIcon({
          className: 'custom-marker',
          html: markerHtml,
          iconSize: site.hasImage ? [60, 70] : [40, 40],
          iconAnchor: site.hasImage ? [30, 70] : [20, 20]
        })
        
        const marker = L.marker([site.lat, site.lng], { icon })
          .addTo(map!)
          .bindPopup(`
            <div style="padding: 12px; min-width: 220px;">
              <div style="display: flex; align-items: center; margin-bottom: 10px;">
                <div style="
                  width: 36px;
                  height: 36px;
                  background: ${site.color};
                  border-radius: 50%;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  font-size: 20px;
                  margin-right: 10px;
                ">${site.icon}</div>
                <div>
                  <h3 style="margin: 0; color: ${site.color}; font-size: 16px; font-weight: bold;">${site.name}</h3>
                  <span style="font-size: 11px; color: #999; background: #f0f0f0; padding: 2px 6px; border-radius: 3px;">${site.type}</span>
                </div>
              </div>
              <p style="margin: 0 0 10px 0; color: #666; font-size: 13px; line-height: 1.5;">${site.description}</p>
              <div style="display: flex; justify-content: space-between; font-size: 12px; color: #999; padding-top: 8px; border-top: 1px solid #eee;">
                <span>📍 ${site.location}</span>
                <span style="color: #f39c12;">⭐ ${site.rating}</span>
              </div>
            </div>
          `)
        
        // 添加悬停效果
        marker.on('mouseover', () => {
          marker.openPopup()
        })
        // 点击导航到智慧旅游
        marker.on('click', () => {
          selectSite(site)
        })
      })
    }
  }, 100)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
  
  // 移除触摸事件监听
  if (container.value) {
    container.value.removeEventListener('touchstart', handleTouchStart)
    container.value.removeEventListener('touchmove', handleTouchMove)
    container.value.removeEventListener('touchend', handleTouchEnd)
  }
  
  if (slideInterval) {
    clearInterval(slideInterval)
  }
  if (map) {
    map.remove()
    map = null
  }
})
</script>

<style scoped>
/* 全局样式 */
.home-page {
  width: 100%;
  height: 100vh;
  overflow: hidden;
  font-family: 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
}

/* 顶部导航 */
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background: rgba(139, 30, 63, 0.95);
  backdrop-filter: blur(10px);
  z-index: 9999;
  transition: all 0.3s;
  border-bottom: 2px solid rgba(212, 149, 108, 0.3);
}

.header.scrolled {
  background: rgba(139, 30, 63, 0.98);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.header-container {
  max-width: 1600px;
  margin: 0 auto;
  padding: 0 3rem;
  height: 75px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 1rem;
  cursor: pointer;
  transition: transform 0.3s;
}

.logo-area:hover {
  transform: scale(1.02);
}

.logo-text h1 {
  font-size: 1.4rem;
  font-weight: 700;
  color: #ffd700;
  margin: 0;
  letter-spacing: 3px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.logo-text p {
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
  letter-spacing: 2px;
}

.main-nav {
  display: flex;
  gap: 2rem;
  flex: 1;
  justify-content: center;
}

.nav-link {
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  font-size: 0.95rem;
  font-weight: 500;
  padding: 0.5rem 0;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
  letter-spacing: 1px;
  cursor: pointer;
  white-space: nowrap;
  /* 增加触摸区域 */
  -webkit-tap-highlight-color: transparent;
  touch-action: manipulation;
}

.nav-link:hover,
.nav-link.active {
  color: #ffd700;
  border-bottom-color: #ffd700;
}

/* 触摸设备优化 */
@media (hover: none) and (pointer: coarse) {
  .nav-link {
    padding: 0.7rem 0.5rem;
  }
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 20px;
  transition: background 0.3s ease;
  color: rgba(255, 255, 255, 0.9);
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.15);
}

.username {
  font-size: 0.9rem;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.9);
}

/* 移动端菜单按钮 */
.mobile-menu-btn {
  display: none;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  -webkit-tap-highlight-color: transparent;
}

.menu-icon {
  display: flex;
  flex-direction: column;
  gap: 5px;
  width: 28px;
  height: 24px;
  position: relative;
}

.menu-icon span {
  display: block;
  width: 100%;
  height: 3px;
  background: #ffd700;
  border-radius: 2px;
  transition: all 0.3s;
}

.menu-icon.open span:nth-child(1) {
  transform: rotate(45deg) translateY(10px);
}

.menu-icon.open span:nth-child(2) {
  opacity: 0;
}

.menu-icon.open span:nth-child(3) {
  transform: rotate(-45deg) translateY(-10px);
}

/* 移动端菜单 */
.mobile-menu {
  position: fixed;
  top: 75px;
  left: 0;
  right: 0;
  background: rgba(139, 30, 63, 0.98);
  backdrop-filter: blur(10px);
  z-index: 9998;
  max-height: calc(100vh - 75px);
  overflow-y: auto;
  border-top: 2px solid rgba(212, 149, 108, 0.3);
}

.mobile-nav {
  display: flex;
  flex-direction: column;
  padding: 1rem 0;
}

@media (max-width: 1200px) {
  .header-container {
    padding: 0 2rem;
  }
  .main-nav {
    gap: 1.5rem;
  }
  .nav-link {
    font-size: 0.9rem;
  }
}

@media (max-width: 992px) {
  .header-container {
    padding: 0 2rem;
    height: 68px;
  }
  .section-content {
    padding: 0 2rem;
    height: calc(100vh - 68px);
  }
  .fullpage-section {
    padding-top: 68px;
  }
  .main-nav {
    gap: 1rem;
  }
  .nav-link {
    font-size: 0.85rem;
    padding: 0.4rem 0;
  }
  .logo-text h1 {
    font-size: 1.25rem;
    letter-spacing: 2px;
  }
  .logo-text p {
    font-size: 0.7rem;
  }
}

@media (max-width: 768px) {
  .header-container {
    padding: 0 1.5rem;
    height: 64px;
  }
  .section-content {
    padding: 0 1.5rem;
    height: calc(100vh - 64px);
  }
  .fullpage-section {
    padding-top: 64px;
  }
  .main-nav {
    gap: 0.8rem;
  }
  .nav-link {
    font-size: 0.8rem;
  }
  .logo-text h1 {
    font-size: 1.15rem;
  }
}

@media (max-width: 640px) {
  .header-container {
    padding: 0 1rem;
    height: 60px;
  }
  .section-content {
    padding: 0 1rem;
    height: calc(100vh - 60px);
  }
  .fullpage-section {
    padding-top: 60px;
  }
  .main-nav {
    display: none;
  }
  .nav-link {
    font-size: 0.75rem;
  }
  .logo-text h1 {
    font-size: 1rem;
    letter-spacing: 1px;
  }
  .logo-text p {
    font-size: 0.65rem;
    letter-spacing: 1px;
  }
  .header-actions .el-button {
    padding: 0.5rem 1rem;
    font-size: 0.9rem;
  }
}



/* 全屏滚动容器 */
.fullpage-container {
  width: 100%;
  height: 100vh;
  overflow-y: auto;
  scroll-snap-type: y mandatory;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch; /* iOS 平滑滚动 */
}

.fullpage-container::-webkit-scrollbar {
  display: none;
}

.fullpage-section {
  width: 100%;
  height: 100vh;
  position: relative;
  scroll-snap-align: start;
  padding-top: 75px;
  box-sizing: border-box;
  /* 防止触摸时的选择 */
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
  /* 优化触摸响应 */
  touch-action: pan-y;
}

.section-content {
  width: 100%;
  height: calc(100vh - 75px);
  max-width: 1600px;
  margin: 0 auto;
  padding: 0 3rem;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
  z-index: 10;
}

/* 第1屏：首屏 */
.section-hero {
  background: linear-gradient(135deg, #8b1e3f 0%, #c41e3a 50%, #d4956c 100%);
  overflow: hidden;
  position: relative;
}

/* 背景轮播 */
.hero-carousel {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
}

.carousel-slide {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-size: cover;
  background-position: center;
  opacity: 0;
  transition: opacity 1.5s ease-in-out;
}

.carousel-slide.active {
  opacity: 1;
}

.slide-caption {
  position: absolute;
  bottom: 8rem;
  left: 3rem;
  color: #fff;
  text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.8);
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.8s ease-out 0.5s;
}

.carousel-slide.active .slide-caption {
  opacity: 1;
  transform: translateY(0);
}

.slide-caption h3 {
  font-size: 2rem;
  font-weight: 800;
  margin-bottom: 0.5rem;
  letter-spacing: 3px;
  color: #ffd700;
}

.slide-caption p {
  font-size: 1.2rem;
  letter-spacing: 2px;
  opacity: 0.95;
}

/* 轮播指示器 */
.carousel-indicators {
  position: absolute;
  bottom: 6rem;
  left: 3rem;
  display: flex;
  gap: 1rem;
  z-index: 10;
}

.indicator-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.4);
  border: 2px solid rgba(255, 255, 255, 0.6);
  cursor: pointer;
  transition: all 0.3s;
}

.indicator-dot:hover {
  background: rgba(255, 215, 0, 0.6);
  border-color: #ffd700;
  transform: scale(1.2);
}

.indicator-dot.active {
  background: #ffd700;
  border-color: #ffd700;
  width: 40px;
  border-radius: 6px;
}

.hero-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 2;
  pointer-events: none;
}

.bg-pattern {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    repeating-linear-gradient(90deg, rgba(0,0,0,0.03) 0px, transparent 1px, transparent 40px, rgba(0,0,0,0.03) 41px),
    repeating-linear-gradient(0deg, rgba(0,0,0,0.03) 0px, transparent 1px, transparent 40px, rgba(0,0,0,0.03) 41px);
  opacity: 0.2;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at center, transparent 0%, rgba(0, 0, 0, 0.3) 100%);
}

.section-hero .section-content {
  justify-content: center;
  position: relative;
  z-index: 3;
}

.hero-center {
  text-align: center;
  color: #fff;
  width: 100%;
  max-width: 900px;
}

/* Logo */
.hero-logo-large {
  margin-bottom: 3rem;
  animation: fadeInDown 1s ease-out;
}

/* 主标题 */
.hero-title {
  font-size: 6rem;
  font-weight: 900;
  color: #ffd700;
  text-shadow: 4px 4px 16px rgba(0, 0, 0, 0.7);
  letter-spacing: 20px;
  margin: 0 0 1.5rem 0;
  animation: fadeInUp 1s ease-out 0.2s both;
}

.hero-subtitle {
  font-size: 1.6rem;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.95);
  letter-spacing: 6px;
  margin: 0 0 4rem 0;
  animation: fadeInUp 1s ease-out 0.4s both;
}

/* 口号 */
.hero-slogan {
  font-size: 2rem;
  font-weight: 600;
  color: #fff;
  letter-spacing: 8px;
  margin-bottom: 4rem;
  text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.6);
  animation: fadeInUp 1s ease-out 0.6s both;
}

.slogan-dot {
  margin: 0 1.5rem;
  color: #ffd700;
}

/* 行动按钮 */
.hero-actions {
  animation: fadeInUp 1s ease-out 0.8s both;
}

.btn-explore {
  padding: 1.2rem 4rem;
  font-size: 1.3rem;
  font-weight: 700;
  letter-spacing: 4px;
  border-radius: 50px;
  box-shadow: 0 10px 30px rgba(196, 30, 58, 0.5);
  transition: all 0.4s;
  -webkit-tap-highlight-color: transparent;
  touch-action: manipulation;
}

.btn-explore:hover {
  transform: translateY(-5px) scale(1.05);
  box-shadow: 0 15px 40px rgba(196, 30, 58, 0.7);
}

.btn-explore:active {
  transform: translateY(-2px) scale(1.02);
}

/* 滚动提示 */
.scroll-indicator {
  position: absolute;
  bottom: 3rem;
  left: 50%;
  transform: translateX(-50%);
  text-align: center;
  color: #fff;
  cursor: pointer;
  z-index: 10;
  animation: fadeIn 1s ease-out 1.2s both, bounce 2s ease-in-out 2s infinite;
}

.scroll-icon {
  width: 50px;
  height: 50px;
  margin: 0 auto 0.8rem;
  border: 2px solid rgba(255, 215, 0, 0.5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.scroll-indicator:hover .scroll-icon {
  border-color: #ffd700;
  background: rgba(255, 215, 0, 0.1);
}

.scroll-icon span {
  font-size: 2rem;
}

.scroll-text {
  font-size: 0.9rem;
  letter-spacing: 2px;
  opacity: 0.9;
}

/* 动画 */
@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes bounce {
  0%, 100% {
    transform: translateX(-50%) translateY(0);
  }
  50% {
    transform: translateX(-50%) translateY(15px);
  }
}

/* 第2屏：沉浸式故事墙 */
.section-story {
  background: #1a1a1a;
  overflow: hidden;
}

.story-layout {
  display: flex;
  flex-direction: row;
  gap: 0;
  width: 100%;
  max-width: none;
  padding: 0 !important;
  height: 100%;
  justify-content: stretch;
  align-items: stretch;
}

.story-video {
  flex: 0 0 55%;
  width: 55%;
  min-height: 100%;
  position: relative;
  overflow: hidden;
}

.video-container {
  width: 100%;
  height: 100%;
  position: relative;
}

.story-video-player {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: brightness(0.7);
  transition: filter 0.3s;
}

.video-container:hover .story-video-player {
  filter: brightness(0.85);
}

.video-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    to bottom,
    rgba(0, 0, 0, 0.3) 0%,
    rgba(0, 0, 0, 0.5) 50%,
    rgba(0, 0, 0, 0.8) 100%
  );
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 3rem;
  color: #fff;
}

.video-info {
  animation: fadeInUp 0.8s ease-out;
}

.video-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  background: rgba(196, 30, 58, 0.9);
  padding: 0.5rem 1.5rem;
  border-radius: 50px;
  margin-bottom: 2rem;
  backdrop-filter: blur(10px);
  animation: pulse 2s ease-in-out infinite;
}

.badge-icon {
  font-size: 1.2rem;
}

.badge-text {
  font-size: 0.9rem;
  font-weight: 600;
  letter-spacing: 1px;
}

.video-title {
  font-size: 3rem;
  font-weight: 900;
  margin-bottom: 1rem;
  text-shadow: 2px 2px 10px rgba(0, 0, 0, 0.8);
  line-height: 1.2;
}

.video-subtitle {
  font-size: 1.3rem;
  opacity: 0.9;
  text-shadow: 1px 1px 5px rgba(0, 0, 0, 0.8);
}



.story-timeline {
  flex: 0 0 45%;
  width: 45%;
  background: #fff;
  padding: 4rem 3rem;
  overflow-y: auto;
  overflow-x: hidden;
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 10;
}

.story-timeline::-webkit-scrollbar {
  width: 8px;
}

.story-timeline::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

.story-timeline::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #8b1e3f, #c41e3a);
  border-radius: 10px;
}

.story-timeline::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #c41e3a, #8b1e3f);
}

.timeline-title {
  font-size: 2.5rem;
  font-weight: 900;
  color: #8b1e3f;
  margin-bottom: 3rem;
  letter-spacing: 4px;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
}

.title-icon {
  font-size: 2rem;
  animation: swing 2s ease-in-out infinite;
}

.timeline-container {
  flex: 1;
  position: relative;
  padding-left: 3rem;
}

.timeline-container::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: linear-gradient(to bottom, #8b1e3f, #c41e3a, #d4956c);
  box-shadow: 0 0 10px rgba(139, 30, 63, 0.3);
}

.timeline-item {
  position: relative;
  margin-bottom: 3rem;
  animation: fadeInRight 0.6s ease-out;
  opacity: 0.7;
  transition: all 0.4s;
  cursor: pointer;
  user-select: none;
  -webkit-tap-highlight-color: transparent;
  touch-action: manipulation;
}

.timeline-item:nth-child(1) { animation-delay: 0.2s; }
.timeline-item:nth-child(2) { animation-delay: 0.4s; }
.timeline-item:nth-child(3) { animation-delay: 0.6s; }
.timeline-item:nth-child(4) { animation-delay: 0.8s; }

.timeline-item.active {
  opacity: 1;
  transform: scale(1.02);
}

.timeline-item.passed {
  opacity: 0.5;
}

/* 触摸设备优化 */
@media (hover: none) and (pointer: coarse) {
  .timeline-item {
    margin-bottom: 2.5rem;
  }
  
  .timeline-content {
    padding: 1.8rem;
  }
}

.timeline-dot {
  position: absolute;
  left: -3.75rem;
  top: 0.5rem;
  width: 20px;
  height: 20px;
  z-index: 10;
  transition: all 0.3s;
}

.dot-inner {
  width: 100%;
  height: 100%;
  background: #ffd700;
  border: 4px solid #8b1e3f;
  border-radius: 50%;
  transition: all 0.3s;
}

.dot-ring {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
  height: 100%;
  border: 2px solid #8b1e3f;
  border-radius: 50%;
  opacity: 0;
  transition: all 0.3s;
}

.timeline-item.active .dot-inner {
  background: #c41e3a;
  border-color: #ffd700;
  box-shadow: 0 0 20px rgba(196, 30, 58, 0.8);
  animation: pulse 1.5s ease-in-out infinite;
}

.timeline-item.active .dot-ring {
  opacity: 1;
  width: 200%;
  height: 200%;
  animation: ripple 1.5s ease-out infinite;
}

.timeline-content {
  background: #f8f9fa;
  padding: 2rem;
  border-radius: 12px;
  border-left: 4px solid #8b1e3f;
  transition: all 0.3s;
  cursor: pointer;
}

.timeline-item.active .timeline-content {
  background: #fff;
  box-shadow: 0 10px 30px rgba(139, 30, 63, 0.2);
  transform: translateX(10px);
  border-left-color: #c41e3a;
}

.timeline-content:hover {
  background: #fff;
  box-shadow: 0 8px 24px rgba(139, 30, 63, 0.15);
  transform: translateX(10px);
}

.timeline-year {
  display: inline-block;
  background: linear-gradient(135deg, #8b1e3f, #c41e3a);
  color: #ffd700;
  padding: 0.5rem 1.5rem;
  border-radius: 20px;
  font-size: 1.1rem;
  font-weight: 900;
  margin-bottom: 1rem;
  letter-spacing: 2px;
}

.timeline-event {
  font-size: 1.5rem;
  font-weight: 800;
  color: #333;
  margin-bottom: 0.8rem;
  letter-spacing: 1px;
}

.timeline-desc {
  font-size: 1rem;
  color: #666;
  line-height: 1.8;
  margin-bottom: 1rem;
}

.timeline-tags {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  margin-top: 1rem;
}

.timeline-tag {
  display: inline-block;
  background: rgba(139, 30, 63, 0.1);
  color: #8b1e3f;
  padding: 0.3rem 1rem;
  border-radius: 15px;
  font-size: 0.85rem;
  font-weight: 600;
  transition: all 0.3s;
}

.timeline-item.active .timeline-tag {
  background: rgba(196, 30, 58, 0.15);
  color: #c41e3a;
}

.btn-enter-experience {
  margin-top: 2rem;
  width: 100%;
  padding: 1.2rem;
  font-size: 1.2rem;
  font-weight: 700;
  letter-spacing: 3px;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(196, 30, 58, 0.3);
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.8rem;
}

.btn-enter-experience:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 32px rgba(196, 30, 58, 0.5);
}

.btn-icon {
  font-size: 1.3rem;
  animation: float 2s ease-in-out infinite;
}

.btn-arrow {
  font-size: 1.5rem;
  transition: transform 0.3s;
}

.btn-enter-experience:hover .btn-arrow {
  transform: translateX(5px);
}

@keyframes fadeInRight {
  from {
    opacity: 0;
    transform: translateX(-30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.8;
  }
}

@keyframes ripple {
  0% {
    opacity: 0.6;
  }
  100% {
    opacity: 0;
    width: 300%;
    height: 300%;
  }
}

@keyframes swing {
  0%, 100% {
    transform: rotate(0deg);
  }
  25% {
    transform: rotate(10deg);
  }
  75% {
    transform: rotate(-10deg);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5px);
  }
}

/* 第3屏：交互式河北地图 */
.section-map {
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
}

.map-layout {
  flex-direction: row;
  gap: 2rem;
  width: 100%;
  max-width: none;
  padding: 0 2rem;
  align-items: stretch;
}

.map-container {
  flex: 1.5;
  height: 100%;
  background: #fff;
  border-radius: 24px;
  padding: 2rem;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: visible;
  display: flex;
  align-items: center;
  justify-content: center;
}

.map-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Leaflet 地图样式 */
:deep(.leaflet-container) {
  width: 100%;
  height: 100%;
  border-radius: 20px;
  z-index: 1;
}

:deep(.leaflet-popup-content-wrapper) {
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

:deep(.leaflet-popup-content) {
  margin: 0;
  font-family: 'Microsoft YaHei', sans-serif;
}

:deep(.custom-marker) {
  background: transparent !important;
  border: none !important;
}

:deep(.custom-marker div:hover) {
  transform: scale(1.2);
  box-shadow: 0 4px 12px rgba(196, 30, 58, 0.6) !important;
}

.hebei-map {
  width: 100%;
  height: 100%;
  max-width: 800px;
  max-height: 600px;
}

.map-outline {
  transition: all 0.3s;
}

.map-outline:hover {
  fill: rgba(139, 30, 63, 0.15);
}

.site-marker {
  cursor: pointer;
  transition: all 0.3s;
}

.site-marker circle:first-child {
  transition: all 0.3s;
}

.site-marker:hover circle:first-child,
.site-marker.active circle:first-child {
  r: 12;
  fill: #ffd700;
}

.site-marker text {
  font-family: 'Microsoft YaHei', sans-serif;
  pointer-events: none;
}

.pulse-ring {
  animation: pulse 2s ease-out infinite;
}

@keyframes pulse {
  0% {
    r: 12;
    opacity: 0.5;
  }
  100% {
    r: 24;
    opacity: 0;
  }
}

.site-card {
  position: absolute;
  width: 320px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
  overflow: hidden;
  z-index: 100;
  pointer-events: none;
}

.site-card-image {
  height: 120px;
  background: linear-gradient(135deg, #8b1e3f, #c41e3a);
  display: flex;
  align-items: center;
  justify-content: center;
}

.site-card-icon {
  font-size: 4rem;
}

.site-card-content {
  padding: 1.5rem;
}

.site-card-content h3 {
  font-size: 1.3rem;
  font-weight: 800;
  color: #333;
  margin-bottom: 0.8rem;
}

.site-card-content p {
  font-size: 0.95rem;
  color: #666;
  line-height: 1.6;
  margin-bottom: 1rem;
}

.site-card-meta {
  display: flex;
  gap: 1.5rem;
  font-size: 0.9rem;
  color: #999;
}

.card-fade-enter-active,
.card-fade-leave-active {
  transition: all 0.3s;
}

.card-fade-enter-from,
.card-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.routes-panel {
  flex: 0 0 380px;
  width: 380px;
  display: flex;
  flex-direction: column;
  gap: 2rem;
  max-height: 100%;
  overflow-y: auto;
}

.routes-panel::-webkit-scrollbar {
  width: 6px;
}

.routes-panel::-webkit-scrollbar-track {
  background: transparent;
}

.routes-panel::-webkit-scrollbar-thumb {
  background: rgba(139, 30, 63, 0.3);
  border-radius: 3px;
}

.routes-panel::-webkit-scrollbar-thumb:hover {
  background: rgba(139, 30, 63, 0.5);
}

.routes-title {
  font-size: 2rem;
  font-weight: 900;
  color: #8b1e3f;
  letter-spacing: 2px;
}

.routes-list {
  background: #fff;
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.route-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.2rem;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 0.8rem;
  -webkit-tap-highlight-color: transparent;
  touch-action: manipulation;
}

.route-item:last-child {
  margin-bottom: 0;
}

.route-item:hover {
  background: linear-gradient(135deg, rgba(139, 30, 63, 0.05), rgba(196, 30, 58, 0.05));
  transform: translateX(8px);
}

.route-item:active {
  transform: translateX(4px);
  background: linear-gradient(135deg, rgba(139, 30, 63, 0.08), rgba(196, 30, 58, 0.08));
}

.route-icon {
  font-size: 2.5rem;
  flex-shrink: 0;
}

.route-info {
  flex: 1;
}

.route-info h3 {
  font-size: 1.1rem;
  font-weight: 700;
  color: #333;
  margin-bottom: 0.3rem;
}

.route-info p {
  font-size: 0.9rem;
  color: #999;
}

.route-arrow {
  font-size: 1.5rem;
  color: #c41e3a;
  font-weight: 700;
}

.hot-sites {
  background: #fff;
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.hot-sites-title {
  font-size: 1.3rem;
  font-weight: 800;
  color: #333;
  margin-bottom: 1rem;
}

.hot-sites-list {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.hot-site-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0.8rem;
  border-radius: 8px;
  background: #f8f9fa;
  transition: all 0.3s;
}

.hot-site-item:hover {
  background: rgba(139, 30, 63, 0.05);
}

.hot-rank {
  width: 28px;
  height: 28px;
  background: linear-gradient(135deg, #8b1e3f, #c41e3a);
  color: #ffd700;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 900;
  font-size: 0.9rem;
  flex-shrink: 0;
}

.hot-name {
  font-size: 1rem;
  color: #333;
  font-weight: 600;
}

/* 第4屏：文创市集 */
.section-market {
  background: linear-gradient(135deg, #f8f9fa, #fff5e6);
  position: relative;
  overflow: hidden;
}

.section-market::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    repeating-linear-gradient(90deg, rgba(139, 30, 63, 0.02) 0px, transparent 1px, transparent 60px, rgba(139, 30, 63, 0.02) 61px);
  pointer-events: none;
}

.market-layout {
  flex-direction: column;
  gap: 2rem;
  width: 100%;
  max-width: none;
  padding: 2rem 3rem;
}

.market-title {
  font-size: 3rem;
  font-weight: 900;
  color: #8b1e3f;
  text-align: center;
  letter-spacing: 6px;
  margin-bottom: 0.5rem;
}

.market-subtitle {
  font-size: 1.3rem;
  color: #666;
  text-align: center;
  letter-spacing: 3px;
  margin-bottom: 2rem;
}

.market-scroll-container {
  flex: 1;
  overflow-x: auto;
  overflow-y: hidden;
  padding: 1rem 0;
}

.market-scroll-container::-webkit-scrollbar {
  height: 8px;
}

.market-scroll-container::-webkit-scrollbar-track {
  background: rgba(139, 30, 63, 0.1);
  border-radius: 4px;
}

.market-scroll-container::-webkit-scrollbar-thumb {
  background: linear-gradient(90deg, #8b1e3f, #c41e3a);
  border-radius: 4px;
}

.market-stalls {
  display: flex;
  gap: 2rem;
  padding: 0 1rem 0 1rem;
  padding-right: 6rem;
}

.market-stall {
  min-width: 400px;
  background: #fff;
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 3px solid transparent;
  transition: all 0.3s;
  position: relative;
}

.market-stall::before {
  content: '';
  position: absolute;
  top: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 80%;
  height: 20px;
  background: linear-gradient(90deg, transparent, rgba(139, 30, 63, 0.2), transparent);
  border-radius: 50%;
}

.market-stall.featured {
  border-color: #ff6b6b;
  background: linear-gradient(135deg, #fff, #fff5f5);
}

.market-stall.designer {
  border-color: #d4956c;
  background: linear-gradient(135deg, #fff, #fffaf5);
}

.market-stall.cultural {
  border-color: #8b1e3f;
  background: linear-gradient(135deg, #fff, #fef5f8);
}

.market-stall.lifestyle {
  border-color: #c41e3a;
  background: linear-gradient(135deg, #fff, #fff8f5);
}

.market-stall:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.15);
}

.stall-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 2px dashed rgba(139, 30, 63, 0.2);
}

.stall-icon {
  font-size: 2.5rem;
}

.stall-title {
  font-size: 1.5rem;
  font-weight: 800;
  color: #333;
  letter-spacing: 2px;
}

.stall-products {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.product-card {
  display: flex;
  gap: 1rem;
  padding: 1rem;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
  -webkit-tap-highlight-color: transparent;
  touch-action: manipulation;
}

.product-card:hover {
  background: #fff;
  border-color: rgba(139, 30, 63, 0.2);
  transform: translateX(8px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.product-card:active {
  transform: translateX(4px);
}

.product-image {
  width: 80px;
  height: 80px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.product-icon {
  font-size: 2.5rem;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.product-info h4 {
  font-size: 1.1rem;
  font-weight: 700;
  color: #333;
  margin-bottom: 0.5rem;
}

.product-price {
  font-size: 1.3rem;
  font-weight: 900;
  color: #c41e3a;
  margin-bottom: 0.3rem;
}

.product-sales {
  font-size: 0.85rem;
  color: #999;
}

.product-designer {
  font-size: 0.85rem;
  color: #666;
  font-style: italic;
}

.market-action {
  text-align: center;
  padding-top: 1rem;
}

.market-action .el-button {
  padding: 1.2rem 4rem;
  font-size: 1.2rem;
  font-weight: 700;
  letter-spacing: 3px;
  border-radius: 50px;
  box-shadow: 0 8px 24px rgba(196, 30, 58, 0.3);
}

.market-action .el-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 32px rgba(196, 30, 58, 0.5);
}

/* 第3-4屏：双栏布局 */
.section-dual {
  background: #f8f9fa;
}

.dual-layout {
  flex-direction: row;
  gap: 3rem;
  width: 100%;
}

.dual-item {
  flex: 1;
  height: 600px;
  border-radius: 24px;
  position: relative;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s;
}

.dual-item:hover {
  transform: scale(1.05);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.dual-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.dual-content {
  position: relative;
  z-index: 10;
  height: 100%;
  padding: 3rem;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: #fff;
}

.dual-icon {
  font-size: 6rem;
  margin-bottom: 2rem;
}

.dual-title {
  font-size: 2.2rem;
  font-weight: 800;
  margin-bottom: 1.5rem;
  letter-spacing: 2px;
}

.dual-desc {
  font-size: 1.2rem;
  line-height: 1.8;
  margin-bottom: 2rem;
  opacity: 0.95;
}

.dual-features {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 2rem;
}

.feature-tag {
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  padding: 0.5rem 1.2rem;
  border-radius: 20px;
  font-size: 0.9rem;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.dual-action {
  font-size: 1.3rem;
  font-weight: 700;
  color: #ffd700;
}

/* 第5屏：社交广场 */
.section-social-square {
  background: linear-gradient(135deg, #1a1a1a, #2d2d2d);
  position: relative;
  overflow: hidden;
}

.square-layout {
  flex-direction: column;
  gap: 2rem;
  width: 100%;
  max-width: none;
  padding: 2rem 3rem;
  position: relative;
}

.square-title {
  font-size: 3rem;
  font-weight: 900;
  color: #ffd700;
  text-align: center;
  letter-spacing: 6px;
  margin-bottom: 2rem;
  text-shadow: 0 0 20px rgba(255, 215, 0, 0.5);
}

/* 中心大屏 */
.central-screen {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  position: relative;
  z-index: 10;
}

.screen-frame {
  background: linear-gradient(135deg, #8b1e3f, #c41e3a);
  padding: 1.5rem;
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
}

.screen-content {
  background: #000;
  border-radius: 16px;
  aspect-ratio: 16/9;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.video-player {
  text-align: center;
  color: #fff;
}

.video-icon {
  font-size: 5rem;
  margin-bottom: 1rem;
  opacity: 0.8;
}

.video-title {
  font-size: 1.5rem;
  font-weight: 700;
  letter-spacing: 2px;
}

/* 用户头像墙 */
.avatar-walls {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 1;
}

.avatar-wall {
  position: absolute;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  top: 50%;
  transform: translateY(-50%);
}

.avatar-wall.left {
  left: 2rem;
}

.avatar-wall.right {
  right: 2rem;
}

.avatar-item {
  animation: fadeInScale 0.6s ease-out both;
}

@keyframes fadeInScale {
  from {
    opacity: 0;
    transform: scale(0);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.avatar-circle {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  border: 3px solid #ffd700;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
  transition: all 0.3s;
}

.avatar-circle:hover {
  transform: scale(1.2);
  box-shadow: 0 8px 24px rgba(255, 215, 0, 0.5);
}

/* 滚动弹幕 */
.danmaku-container {
  position: absolute;
  top: 30%;
  left: 0;
  right: 0;
  z-index: 5;
  pointer-events: none;
}

.danmaku-line {
  height: 40px;
  overflow: hidden;
  margin-bottom: 1rem;
}

.danmaku-content {
  display: flex;
  gap: 3rem;
  animation: scrollDanmaku 30s linear infinite;
}

@keyframes scrollDanmaku {
  from {
    transform: translateX(100%);
  }
  to {
    transform: translateX(-100%);
  }
}

.danmaku-item {
  display: inline-block;
  padding: 0.5rem 1.5rem;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 20px;
  font-size: 1rem;
  font-weight: 600;
  color: #333;
  white-space: nowrap;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

/* 成就榜 */
.achievement-panel {
  position: absolute;
  right: 3rem;
  top: 50%;
  transform: translateY(-50%);
  width: 280px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 1.5rem;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.3);
  z-index: 10;
}

.achievement-header h3 {
  font-size: 1.3rem;
  font-weight: 800;
  color: #8b1e3f;
  text-align: center;
  margin-bottom: 1.5rem;
}

.achievement-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.achievement-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0.8rem;
  background: #f8f9fa;
  border-radius: 12px;
  transition: all 0.3s;
}

.achievement-item:hover {
  background: rgba(139, 30, 63, 0.05);
  transform: translateX(-5px);
}

.achievement-rank {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 900;
  font-size: 1rem;
  flex-shrink: 0;
  background: linear-gradient(135deg, #8b1e3f, #c41e3a);
  color: #fff;
}

.achievement-rank.rank-1 {
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  color: #8b1e3f;
  font-size: 1.2rem;
}

.achievement-rank.rank-2 {
  background: linear-gradient(135deg, #c0c0c0, #e8e8e8);
  color: #666;
}

.achievement-rank.rank-3 {
  background: linear-gradient(135deg, #cd7f32, #e8a87c);
  color: #fff;
}

.achievement-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #8b1e3f, #c41e3a);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  flex-shrink: 0;
}

.achievement-info {
  flex: 1;
}

.achievement-name {
  font-size: 1rem;
  font-weight: 700;
  color: #333;
  margin-bottom: 0.2rem;
}

.achievement-score {
  font-size: 0.85rem;
  color: #999;
}

.square-action {
  text-align: center;
  margin-top: 2rem;
  position: relative;
  z-index: 10;
}

.square-action .el-button {
  padding: 1.2rem 4rem;
  font-size: 1.2rem;
  font-weight: 700;
  letter-spacing: 3px;
  border-radius: 50px;
  box-shadow: 0 8px 24px rgba(196, 30, 58, 0.5);
}

.square-action .el-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 32px rgba(196, 30, 58, 0.7);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 2rem;
  width: 100%;
}

.stat-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 215, 0, 0.3);
  border-radius: 16px;
  padding: 2rem;
  text-align: center;
  color: #fff;
  transition: all 0.3s;
}

.stat-card:hover {
  background: rgba(255, 255, 255, 0.15);
  border-color: #ffd700;
  transform: translateY(-5px);
}

.stat-icon {
  font-size: 3.5rem;
  margin-bottom: 1rem;
}

.stat-number {
  font-size: 2.5rem;
  font-weight: 900;
  color: #ffd700;
  margin-bottom: 0.5rem;
}

.stat-label {
  font-size: 1.1rem;
  opacity: 0.9;
}

/* 第6屏：数据可视化大屏 */
.section-dashboard {
  background: linear-gradient(135deg, #0a0e27, #1a1f3a);
  position: relative;
  overflow: hidden;
}

.section-dashboard::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    repeating-linear-gradient(90deg, rgba(0, 255, 255, 0.03) 0px, transparent 1px, transparent 50px, rgba(0, 255, 255, 0.03) 51px),
    repeating-linear-gradient(0deg, rgba(0, 255, 255, 0.03) 0px, transparent 1px, transparent 50px, rgba(0, 255, 255, 0.03) 51px);
  pointer-events: none;
}

.dashboard-layout {
  flex-direction: column;
  gap: 1.5rem;
  width: 100%;
  max-width: none;
  padding: 1.5rem 2rem;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background: rgba(0, 255, 255, 0.05);
  border: 1px solid rgba(0, 255, 255, 0.2);
  border-radius: 12px;
}

.dashboard-title {
  font-size: 2.5rem;
  font-weight: 900;
  background: linear-gradient(90deg, #00ffff, #ffd700);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: 6px;
}

.dashboard-time {
  font-size: 1.3rem;
  color: #00ffff;
  font-weight: 700;
  font-family: 'Courier New', monospace;
}

.dashboard-body {
  display: grid;
  grid-template-columns: 1fr 2fr 1fr;
  gap: 1.5rem;
  flex: 1;
}

.dashboard-panel {
  background: rgba(0, 255, 255, 0.05);
  border: 1px solid rgba(0, 255, 255, 0.2);
  border-radius: 16px;
  padding: 1.5rem;
  height: 100%;
}

.panel-title {
  font-size: 1.3rem;
  font-weight: 800;
  color: #00ffff;
  margin-bottom: 1.5rem;
  text-align: center;
}

/* 热力图 */
.heatmap-container {
  width: 100%;
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 1rem;
}

.heatmap-svg {
  width: 100%;
  height: 100%;
}

.heat-point {
  filter: drop-shadow(0 0 8px #ffd700);
}

.heatmap-legend {
  display: flex;
  justify-content: center;
  gap: 2rem;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.8);
}

.legend-color {
  width: 20px;
  height: 20px;
  border-radius: 4px;
}

.legend-color.hot {
  background: #ff0000;
}

.legend-color.medium {
  background: #ff6b00;
}

.legend-color.low {
  background: #8b1e3f;
}

/* 核心数据 */
.dashboard-center {
  display: flex;
  align-items: center;
  justify-content: center;
}

.data-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.5rem;
  width: 100%;
}

.data-card {
  background: rgba(0, 255, 255, 0.05);
  border: 2px solid rgba(0, 255, 255, 0.3);
  border-radius: 16px;
  padding: 2rem 1.5rem;
  text-align: center;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}

.data-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(0, 255, 255, 0.1), transparent);
  animation: shimmer 3s infinite;
}

@keyframes shimmer {
  to {
    left: 100%;
  }
}

.data-card:hover {
  border-color: #00ffff;
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 255, 255, 0.3);
}

.data-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.data-value {
  font-size: 2.5rem;
  font-weight: 900;
  color: #ffd700;
  margin-bottom: 0.5rem;
  text-shadow: 0 0 10px rgba(255, 215, 0, 0.5);
}

.data-label {
  font-size: 1rem;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 0.8rem;
}

.data-trend {
  font-size: 1.1rem;
  font-weight: 700;
}

.data-trend.up {
  color: #00ff00;
}

.data-trend.down {
  color: #ff0000;
}

/* 实时动态 */
.activity-stream {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  max-height: 400px;
  overflow-y: auto;
}

.activity-stream::-webkit-scrollbar {
  width: 6px;
}

.activity-stream::-webkit-scrollbar-track {
  background: rgba(0, 255, 255, 0.1);
}

.activity-stream::-webkit-scrollbar-thumb {
  background: rgba(0, 255, 255, 0.3);
  border-radius: 3px;
}

.activity-item {
  padding: 1rem;
  background: rgba(0, 255, 255, 0.05);
  border-left: 3px solid #00ffff;
  border-radius: 8px;
  animation: slideInRight 0.5s ease-out both;
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.activity-time {
  font-size: 0.85rem;
  color: #00ffff;
  font-family: 'Courier New', monospace;
  margin-bottom: 0.5rem;
}

.activity-content {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  font-size: 0.95rem;
  color: rgba(255, 255, 255, 0.9);
}

.activity-icon {
  font-size: 1.3rem;
}

/* 合作伙伴滚动 */
.dashboard-footer {
  background: rgba(0, 255, 255, 0.05);
  border: 1px solid rgba(0, 255, 255, 0.2);
  border-radius: 12px;
  padding: 1rem;
  overflow: hidden;
}

.partners-scroll {
  overflow: hidden;
}

.partners-content {
  display: flex;
  gap: 3rem;
  animation: scrollPartners 30s linear infinite;
}

@keyframes scrollPartners {
  from {
    transform: translateX(0);
  }
  to {
    transform: translateX(-50%);
  }
}

.partner-item {
  flex-shrink: 0;
  padding: 0.8rem 2rem;
  background: rgba(0, 255, 255, 0.1);
  border: 1px solid rgba(0, 255, 255, 0.3);
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 1rem;
  font-weight: 600;
  white-space: nowrap;
}

/* 侧边导航指示器 */
.section-indicators {
  position: fixed;
  right: 3rem;
  top: 50%;
  transform: translateY(-50%);
  z-index: 999;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.indicator {
  display: flex;
  align-items: center;
  gap: 1rem;
  cursor: pointer;
  transition: all 0.3s;
}

.indicator-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  border: 2px solid rgba(139, 30, 63, 0.5);
  transition: all 0.3s;
}

.indicator.active .indicator-dot {
  background: #8b1e3f;
  border-color: #ffd700;
  transform: scale(1.3);
}

.indicator-label {
  font-size: 0.85rem;
  color: rgba(139, 30, 63, 0.7);
  opacity: 0;
  transform: translateX(-10px);
  transition: all 0.3s;
  white-space: nowrap;
}

.indicator:hover .indicator-label,
.indicator.active .indicator-label {
  opacity: 1;
  transform: translateX(0);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .map-layout {
    padding: 0 1.5rem;
  }
  
  .routes-panel {
    flex: 0 0 320px;
    width: 320px;
  }
  
  .market-layout {
    padding: 2rem 2rem;
  }
  
  .dashboard-body {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .data-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 1024px) {
  .hero-title {
    font-size: 4.5rem;
    letter-spacing: 15px;
  }

  .hero-subtitle {
    font-size: 1.4rem;
    letter-spacing: 5px;
  }

  .hero-slogan {
    font-size: 1.7rem;
    letter-spacing: 6px;
  }

  .story-layout {
    flex-direction: column;
  }

  .story-video {
    flex: 0 0 45%;
    width: 100%;
    height: 45%;
  }

  .story-timeline {
    flex: 0 0 55%;
    width: 100%;
    height: 55%;
    padding: 2rem 1.5rem;
  }

  .timeline-title {
    font-size: 2rem;
    margin-bottom: 2rem;
  }
  
  .map-layout {
    flex-direction: column;
    gap: 1.5rem;
  }
  
  .map-container {
    height: 50vh;
  }
  
  .routes-panel {
    flex: 1;
    width: 100%;
    max-height: 40vh;
  }
  
  .market-stall {
    min-width: 350px;
  }
  
  .achievement-panel {
    position: static;
    transform: none;
    width: 100%;
    max-width: 400px;
    margin: 0 auto;
  }
  
  .avatar-walls {
    display: none;
  }
  
  .section-indicators {
    right: 1.5rem;
  }
  
  .data-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 1rem;
  }
}

@media (max-width: 768px) {
  .slide-caption {
    left: 1.5rem;
    bottom: 6rem;
  }

  .slide-caption h3 {
    font-size: 1.5rem;
    letter-spacing: 2px;
  }

  .slide-caption p {
    font-size: 1rem;
    letter-spacing: 1px;
  }

  .carousel-indicators {
    left: 1.5rem;
    bottom: 4.5rem;
  }

  .hero-logo-large {
    margin-bottom: 2rem;
  }

  .hero-title {
    font-size: 3rem;
    letter-spacing: 8px;
    margin-bottom: 1rem;
  }

  .hero-subtitle {
    font-size: 1.1rem;
    letter-spacing: 3px;
    margin-bottom: 3rem;
  }

  .hero-slogan {
    font-size: 1.3rem;
    letter-spacing: 4px;
    margin-bottom: 3rem;
  }

  .slogan-dot {
    margin: 0 1rem;
  }

  .btn-explore {
    width: 90%;
    padding: 1rem 2rem;
    font-size: 1.1rem;
    letter-spacing: 3px;
  }
  
  .story-video {
    height: 40%;
  }
  
  .story-timeline {
    height: 60%;
    padding: 1.5rem 1rem;
  }
  
  .timeline-title {
    font-size: 1.6rem;
    margin-bottom: 1.5rem;
  }
  
  .timeline-container {
    padding-left: 2rem;
  }
  
  .timeline-year {
    font-size: 1rem;
    padding: 0.4rem 1rem;
  }
  
  .timeline-event {
    font-size: 1.2rem;
  }
  
  .timeline-desc {
    font-size: 0.9rem;
  }
  
  .btn-enter-experience {
    font-size: 1rem;
    padding: 1rem;
  }
  
  .map-container {
    padding: 1rem;
    height: 45vh;
  }
  
  .routes-title {
    font-size: 1.5rem;
  }
  
  .route-item {
    padding: 1rem;
  }
  
  .market-title {
    font-size: 2.2rem;
    letter-spacing: 4px;
  }
  
  .market-subtitle {
    font-size: 1.1rem;
    letter-spacing: 2px;
  }
  
  .market-stall {
    min-width: 300px;
    padding: 1.5rem;
  }
  
  .stall-title {
    font-size: 1.3rem;
  }
  
  .product-card {
    padding: 0.8rem;
  }
  
  .product-image {
    width: 70px;
    height: 70px;
  }
  
  .product-info h4 {
    font-size: 1rem;
  }
  
  .product-price {
    font-size: 1.1rem;
  }
  
  .square-title {
    font-size: 2.2rem;
    letter-spacing: 4px;
  }
  
  .central-screen {
    max-width: 100%;
  }
  
  .danmaku-container {
    top: 25%;
  }
  
  .danmaku-item {
    font-size: 0.9rem;
    padding: 0.4rem 1rem;
  }
  
  .dashboard-title {
    font-size: 1.8rem;
    letter-spacing: 3px;
  }
  
  .dashboard-time {
    font-size: 1rem;
  }
  
  .data-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .data-card {
    padding: 1.5rem 1rem;
  }
  
  .data-icon {
    font-size: 2.5rem;
  }
  
  .data-value {
    font-size: 2rem;
  }

  .section-indicators {
    display: none;
  }
}
</style>
