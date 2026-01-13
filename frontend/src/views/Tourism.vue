<template>
  <MainLayout>
    <div class="tourism-page">
      <!-- Hero Banner -->
      <div class="hero-banner">
        <div class="hero-content">
          <span class="hero-tag">🌟 河北红色旅游智慧平台</span>
          <h1>智慧旅游</h1>
          <p>景点导览 · 智能路线 · 实时信息 · 门票预订 · AI助手</p>
          <div class="hero-tabs">
            <button :class="{ active: activeTab === 'guide' }" @click="activeTab = 'guide'">🏛️ 景点导览</button>
            <button :class="{ active: activeTab === 'route' }" @click="activeTab = 'route'">🗺️ 智能路线</button>
            <button :class="{ active: activeTab === 'realtime' }" @click="activeTab = 'realtime'; loadRealtimeData()">📊 实时信息</button>
            <button :class="{ active: activeTab === 'ticket' }" @click="activeTab = 'ticket'">🎫 门票预订</button>
          </div>
        </div>
      </div>

      <div class="main-content">
        <!-- 景点导览 -->
        <div v-if="activeTab === 'guide'" class="tab-content">
          <div class="section-header">
            <h2>🏛️ 河北红色景点导览</h2>
            <p>6大红色景点，语音讲解带你重温革命历史</p>
          </div>
          <div class="spots-grid">
            <div v-for="spot in redSpots" :key="spot.id" class="spot-card" @click="selectSpot(spot)">
              <div class="spot-cover" :style="{ background: spot.gradient }">
                <span class="spot-icon">{{ spot.icon }}</span>
                <div class="spot-badges">
                  <span v-if="spot.isFree" class="badge-free">免费</span>
                  <span v-if="spot.needReserve" class="badge-reserve">需预约</span>
                </div>
                <button class="btn-favorite" @click.stop="toggleFavorite(spot.id)" :class="{ active: favoriteSpots.includes(spot.id) }">
                  {{ favoriteSpots.includes(spot.id) ? '❤️' : '🤍' }}
                </button>
              </div>
              <div class="spot-info">
                <h3>{{ spot.name }}</h3>
                <p class="spot-slogan">{{ spot.slogan }}</p>
                <div class="spot-meta">
                  <span>📍 {{ spot.location }}</span>
                  <span>⭐ {{ spot.rating }}</span>
                </div>
                <div class="spot-extra">
                  <span class="spot-price">{{ spot.isFree ? '免费' : '¥' + (spot.ticketPrice || 80) }}</span>
                  <span class="spot-time">{{ spot.openingHours || '08:00-17:30' }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 景点详情 -->
          <div v-if="currentSpot" class="spot-detail">
            <div class="detail-header">
              <div class="detail-title">
                <span class="detail-icon" :style="{ background: currentSpot.gradient }">{{ currentSpot.icon }}</span>
                <div>
                  <h3>{{ currentSpot.name }}</h3>
                  <p>{{ currentSpot.slogan }}</p>
                </div>
              </div>
              <div class="detail-actions">
                <button class="btn-favorite-lg" @click="toggleFavorite(currentSpot.id)" :class="{ active: favoriteSpots.includes(currentSpot.id) }">
                  {{ favoriteSpots.includes(currentSpot.id) ? '❤️ 已收藏' : '🤍 收藏' }}
                </button>
                <button class="btn-close" @click="currentSpot = null">×</button>
              </div>
            </div>
            
            <!-- 基本信息卡片 -->
            <div class="detail-info-cards">
              <div class="info-card">
                <span class="info-icon">🎫</span>
                <div class="info-content">
                  <span class="info-label">门票</span>
                  <span class="info-value">{{ currentSpot.isFree ? '免费' : '¥' + (currentSpot.ticketPrice || 80) }}</span>
                </div>
              </div>
              <div class="info-card">
                <span class="info-icon">🕐</span>
                <div class="info-content">
                  <span class="info-label">开放时间</span>
                  <span class="info-value">{{ currentSpot.openingHours || '08:00-17:30' }}</span>
                </div>
              </div>
              <div class="info-card">
                <span class="info-icon">⏱️</span>
                <div class="info-content">
                  <span class="info-label">建议游览</span>
                  <span class="info-value">{{ currentSpot.suggestedDuration || '2-3小时' }}</span>
                </div>
              </div>
              <div class="info-card">
                <span class="info-icon">📞</span>
                <div class="info-content">
                  <span class="info-label">咨询电话</span>
                  <span class="info-value">{{ currentSpot.phone || '400-xxx-xxxx' }}</span>
                </div>
              </div>
            </div>
            
            <div class="detail-body">
              <div class="detail-section">
                <h4>📖 景点简介</h4>
                <p>{{ currentSpot.introduction }}</p>
              </div>
              <div class="detail-section">
                <h4>📜 历史背景</h4>
                <p>{{ currentSpot.history }}</p>
              </div>
              
              <!-- 交通指南 -->
              <div class="detail-section">
                <h4>🚗 交通指南</h4>
                <div class="transport-info">
                  <div class="transport-item">
                    <span class="transport-icon">🚌</span>
                    <div>
                      <strong>公共交通</strong>
                      <p>{{ currentSpot.publicTransport || '可乘坐旅游专线大巴直达景区' }}</p>
                    </div>
                  </div>
                  <div class="transport-item">
                    <span class="transport-icon">🚗</span>
                    <div>
                      <strong>自驾路线</strong>
                      <p>{{ currentSpot.selfDrive || '导航搜索景区名称即可，景区设有免费停车场' }}</p>
                    </div>
                  </div>
                </div>
                <button class="btn-navigate" @click="openNavigation(currentSpot)">
                  📍 导航到这里
                </button>
              </div>
              
              <div class="detail-section">
                <h4>💡 游览贴士</h4>
                <ul>
                  <li v-for="tip in currentSpot.tips" :key="tip">{{ tip }}</li>
                </ul>
              </div>
              
              <!-- 用户评价 -->
              <div class="detail-section">
                <h4>💬 游客评价 <span class="review-count">({{ currentSpot.reviewCount || 128 }}条)</span></h4>
                <div class="reviews-summary">
                  <div class="rating-big">
                    <span class="rating-num">{{ currentSpot.rating }}</span>
                    <span class="rating-stars">⭐⭐⭐⭐⭐</span>
                    <span class="rating-text">非常好</span>
                  </div>
                  <div class="rating-tags">
                    <span class="tag">历史厚重 {{ Math.floor(Math.random() * 50 + 50) }}</span>
                    <span class="tag">讲解详细 {{ Math.floor(Math.random() * 40 + 30) }}</span>
                    <span class="tag">值得一去 {{ Math.floor(Math.random() * 60 + 40) }}</span>
                    <span class="tag">环境优美 {{ Math.floor(Math.random() * 30 + 20) }}</span>
                  </div>
                </div>
                <div class="reviews-list">
                  <div class="review-item" v-for="review in spotReviews" :key="review.id">
                    <div class="review-header">
                      <span class="review-avatar">{{ review.avatar }}</span>
                      <div class="review-user">
                        <span class="review-name">{{ review.name }}</span>
                        <span class="review-date">{{ review.date }}</span>
                      </div>
                      <span class="review-rating">{{ '⭐'.repeat(review.rating) }}</span>
                    </div>
                    <p class="review-content">{{ review.content }}</p>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 语音讲解 -->
            <div class="audio-section">
              <h4>🎧 语音讲解 ({{ currentSpot.audioGuides.length }}段)</h4>
              <div class="audio-list">
                <div v-for="(audio, idx) in currentSpot.audioGuides" :key="audio.id" class="audio-item" :class="{ playing: playingAudioId === audio.id }">
                  <span class="audio-num">{{ idx + 1 }}</span>
                  <span class="audio-title">{{ audio.title }}</span>
                  <span class="audio-duration">{{ formatDuration(audio.duration) }}</span>
                  <button class="btn-play" @click="toggleAudio(audio)">{{ playingAudioId === audio.id ? '⏸' : '▶' }}</button>
                </div>
              </div>
              <div v-if="playingAudio" class="audio-player">
                <div class="player-info">
                  <span>{{ playingAudio.title }}</span>
                  <span>{{ formatDuration(audioCurrentTime) }} / {{ formatDuration(playingAudio.duration) }}</span>
                </div>
                <div class="player-progress">
                  <div class="progress-bar" :style="{ width: audioProgress + '%' }"></div>
                </div>
                <p class="player-text">{{ playingAudio.transcript }}</p>
              </div>
            </div>
            
            <!-- 底部操作栏 -->
            <div class="detail-footer">
              <button class="btn-add-route" @click="addToRoute(currentSpot)">
                ➕ 加入行程
              </button>
              <button class="btn-book-ticket" @click="bookSpotTicket(currentSpot)">
                🎫 预约门票
              </button>
            </div>
          </div>
        </div>

        <!-- 智能路线 -->
        <div v-if="activeTab === 'route'" class="tab-content">
          <div class="route-layout">
            <div class="route-left">
              <div class="panel">
                <h3>📍 选择景点 (已选 {{ selectedSpots.length }}/6)</h3>
                <div class="spot-list">
                  <div v-for="spot in redSpots" :key="spot.id" class="spot-item" :class="{ selected: selectedSpots.includes(spot.id) }" @click="toggleSpotSelect(spot.id)">
                    <span class="item-icon">{{ spot.icon }}</span>
                    <span class="item-name">{{ spot.name }}</span>
                    <span class="item-check">{{ selectedSpots.includes(spot.id) ? '✓' : '' }}</span>
                  </div>
                </div>
              </div>
              <div class="panel">
                <h3>🔥 热门路线推荐</h3>
                <div class="hot-list">
                  <div v-for="route in hotRoutes" :key="route.id" class="hot-item" @click="useHotRoute(route)">
                    <span class="hot-rank" :class="{ top: route.rank <= 3 }">{{ route.rank }}</span>
                    <div class="hot-info">
                      <span class="hot-name">{{ route.name }}</span>
                      <span class="hot-meta">{{ route.spots.length }}景点 · {{ route.duration }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="route-right">
              <div class="panel">
                <h3>✏️ 行程规划</h3>
                <div v-if="selectedSpots.length === 0" class="empty-state">
                  <span>🗺️</span>
                  <p>请在左侧选择景点开始规划</p>
                </div>
                <div v-else>
                  <div class="selected-tags">
                    <span v-for="(id, idx) in selectedSpots" :key="id" class="tag">
                      {{ idx + 1 }}. {{ getSpotById(id)?.name }}
                      <button @click="removeSpot(id)">×</button>
                    </span>
                  </div>
                  <div class="plan-form">
                    <div class="form-row">
                      <label>出行日期</label>
                      <input type="date" v-model="planDate" />
                    </div>
                    <div class="form-row">
                      <label>行程天数</label>
                      <select v-model="planDays">
                        <option :value="1">1天</option>
                        <option :value="2">2天</option>
                        <option :value="3">3天</option>
                      </select>
                    </div>
                  </div>
                  <button class="btn-generate" @click="generatePlan" :disabled="planLoading">
                    {{ planLoading ? `🤖 AI规划中... ${formatWaitingTime(waitingTime)}` : '🚀 AI生成行程' }}
                  </button>
                  <p v-if="planLoading" class="loading-hint">
                    💡 可以切换到其他页面，规划完成后会通知您
                  </p>
                </div>
              </div>
              <div v-if="generatedPlan" class="panel plan-result">
                <h3>📋 {{ generatedPlan.title }}</h3>
                <p class="plan-desc">{{ generatedPlan.description }}</p>
                <div v-for="day in generatedPlan.days" :key="day.day" class="day-plan">
                  <div class="day-header">
                    <span class="day-num">{{ day.date }}</span>
                    <span v-if="day.theme" class="day-theme">{{ day.theme }}</span>
                  </div>
                  <div class="day-spots">
                    <div v-for="spot in day.spots" :key="spot.order" class="day-spot">
                      <span class="spot-order">{{ spot.order }}</span>
                      <div class="spot-content">
                        <div class="spot-name">{{ spot.name }}</div>
                        <div class="spot-time">⏱️ {{ spot.duration }}</div>
                        <pre v-if="spot.tips" class="spot-detail">{{ spot.tips }}</pre>
                      </div>
                    </div>
                  </div>
                </div>
                <!-- 预算参考 -->
                <div v-if="generatedPlan.budget" class="plan-budget">
                  <h4>💰 预算参考</h4>
                  <div class="budget-items">
                    <span v-if="generatedPlan.budget['门票费用']">🎫 门票：{{ generatedPlan.budget['门票费用'] }}</span>
                    <span v-if="generatedPlan.budget['交通费用']">🚗 交通：{{ generatedPlan.budget['交通费用'] }}</span>
                    <span v-if="generatedPlan.budget['餐饮费用']">🍽️ 餐饮：{{ generatedPlan.budget['餐饮费用'] }}</span>
                    <span v-if="generatedPlan.budget['人均总预算']" class="budget-total">📊 人均：{{ generatedPlan.budget['人均总预算'] }}</span>
                  </div>
                </div>
                <!-- 温馨提示 -->
                <div v-if="generatedPlan.tips && generatedPlan.tips.length" class="plan-tips">
                  <h4>💡 实用信息</h4>
                  <ul>
                    <li v-for="(tip, i) in generatedPlan.tips.slice(0, 6)" :key="i">{{ tip }}</li>
                  </ul>
                </div>
                <div class="plan-actions">
                  <button class="btn-save" @click="savePlan">⭐ 保存行程</button>
                  <button class="btn-share" @click="sharePlan">📤 分享</button>
                </div>
              </div>
              <!-- 历史行程记录 -->
              <div class="panel history-plans">
                <h3>📂 历史行程 ({{ myPlans.length }}) <button class="btn-refresh-small" @click="loadMyPlans">🔄</button></h3>
                <div v-if="myPlansLoading" class="loading-state">加载中...</div>
                <div v-else-if="!myPlans || myPlans.length === 0" class="empty-state-small">暂无保存的行程</div>
                <div v-else class="my-plans-list">
                  <div v-for="plan in myPlans" :key="plan.id" class="my-plan-item" @click="viewSavedPlan(plan)">
                    <div class="plan-item-header">
                      <span class="plan-item-title">{{ plan.title || 'AI生成行程' }}</span>
                      <span class="plan-item-date">{{ formatPlanDate(plan.createdAt) }}</span>
                    </div>
                    <div class="plan-item-meta">
                      <span>📅 {{ plan.days?.length || 1 }}天</span>
                      <span>💰 ¥{{ plan.estimatedCost || 200 }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 实时信息 -->
        <div v-if="activeTab === 'realtime'" class="tab-content">
          <div class="section-header">
            <h2>📊 实时信息中心</h2>
            <button class="btn-refresh" @click="loadRealtimeData" :disabled="realtimeLoading">
              {{ realtimeLoading ? '刷新中...' : '🔄 刷新' }}
            </button>
          </div>
          <div class="panel">
            <h3>🌤️ 景区天气预报</h3>
            <div class="weather-grid">
              <div v-for="w in weatherData" :key="w.spotName" class="weather-card">
                <div class="weather-name">{{ w.spotName }}</div>
                <div class="weather-main">
                  <span class="weather-icon">{{ getWeatherIcon(w.condition) }}</span>
                  <span class="weather-temp">{{ w.temperature.max }}°</span>
                </div>
                <div class="weather-condition">{{ w.condition }}</div>
                <div class="weather-range">{{ w.temperature.min }}° ~ {{ w.temperature.max }}°</div>
                <div class="weather-tip">💡 {{ w.suggestion }}</div>
              </div>
            </div>
          </div>
          <div class="panel">
            <h3>👥 景点人流量监控</h3>
            <div class="crowd-list">
              <div v-for="c in crowdData" :key="c.name" class="crowd-item">
                <div class="crowd-name">
                  <span>{{ c.icon }}</span>
                  <span>{{ c.name }}</span>
                </div>
                <div class="crowd-bar">
                  <div class="bar-fill" :class="c.level" :style="{ width: c.percent + '%' }"></div>
                </div>
                <div class="crowd-info">
                  <span :class="'level-' + c.level">{{ c.levelText }}</span>
                  <span>等待{{ c.waitTime }}分钟</span>
                </div>
                <div class="crowd-best">最佳: {{ c.bestTime }}</div>
              </div>
            </div>
          </div>
          <!-- AI出行建议 -->
          <div v-if="aiTravelSuggestion" class="panel ai-suggestion-panel">
            <h3>🤖 AI智能出行建议</h3>
            <div class="ai-suggestion-content">
              <p v-html="aiTravelSuggestion.replace(/\\n/g, '<br>')"></p>
            </div>
          </div>
          <div class="panel">
            <h3>💡 今日出行建议</h3>
            <div class="tips-grid">
              <div v-for="tip in travelTips" :key="tip.title" class="tip-card" :class="tip.type">
                <span class="tip-icon">{{ tip.icon }}</span>
                <div class="tip-content">
                  <strong>{{ tip.title }}</strong>
                  <p>{{ tip.content }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 门票预订 -->
        <div v-if="activeTab === 'ticket'" class="tab-content">
          <div class="section-header">
            <h2>🎫 景区门票预订</h2>
            <p>在线预订，便捷入园</p>
          </div>
          <div class="ticket-grid">
            <div v-for="t in ticketList" :key="t.id" class="ticket-card">
              <div class="ticket-cover" :style="{ background: t.gradient }">
                <span class="ticket-icon">{{ t.icon }}</span>
                <div class="ticket-badges">
                  <span v-if="t.discount" class="badge-discount">{{ t.discount }}</span>
                  <span v-if="t.needReserve" class="badge-reserve">需预约</span>
                </div>
              </div>
              <div class="ticket-body">
                <h3>{{ t.name }}</h3>
                <p class="ticket-addr">📍 {{ t.address }}</p>
                <div class="ticket-meta">
                  <span>🕐 {{ t.openTime }}</span>
                  <span>⭐ {{ t.rating }}</span>
                </div>
                <div class="ticket-price">
                  <span class="price" :class="{ free: t.price === 0 }">{{ t.price === 0 ? '免费' : '¥' + t.price }}</span>
                  <span v-if="t.originalPrice" class="original">¥{{ t.originalPrice }}</span>
                </div>
                <div class="ticket-sold">已售 {{ formatNumber(t.sold) }}</div>
                <div class="ticket-actions">
                  <button class="btn-book" @click="bookTicket(t)">{{ t.needReserve ? '立即预约' : '立即预订' }}</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- AI助手 -->
      <div class="ai-float" @click="showAI = true">🤖 冀小游</div>
      <div v-if="showAI" class="ai-dialog">
        <div class="ai-header">
          <span>🤖 冀小游 · 智能助手</span>
          <button @click="showAI = false">×</button>
        </div>
        <div class="ai-body" ref="aiChatRef">
          <div v-for="(msg, idx) in aiMessages" :key="idx" class="ai-msg" :class="msg.role">
            <span class="msg-avatar">{{ msg.role === 'user' ? '👤' : '🤖' }}</span>
            <div class="msg-text" v-html="formatMessage(msg.content)"></div>
          </div>
          <div v-if="aiTyping" class="ai-msg assistant">
            <span class="msg-avatar">🤖</span>
            <div class="msg-text typing"><span></span><span></span><span></span></div>
          </div>
        </div>
        <div class="ai-footer">
          <div class="quick-btns">
            <button @click="askAI('西柏坡有什么必看景点？')">西柏坡必看</button>
            <button @click="askAI('推荐一日游路线')">一日游推荐</button>
            <button @click="askAI('附近有什么美食？')">周边美食</button>
          </div>
          <div class="ai-input">
            <input v-model="aiInput" placeholder="问我任何问题..." @keyup.enter="sendAIMessage" />
            <button @click="sendAIMessage" :disabled="!aiInput.trim()">发送</button>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import MainLayout from '@/layouts/MainLayout.vue'
import * as tourismApi from '@/api/tourism'
import * as n8nApi from '@/api/n8n'

const activeTab = ref('guide')

// 景点数据
interface AudioGuide { id: number; title: string; duration: number; transcript: string }
interface RedSpot { id: number; name: string; icon: string; gradient: string; slogan: string; location: string; rating: number; isFree: boolean; needReserve: boolean; tags: string[]; introduction: string; history: string; tips: string[]; audioGuides: AudioGuide[] }

// 默认景点数据（后端不可用时使用）
const defaultSpots: RedSpot[] = [
  { id: 1, name: '西柏坡纪念馆', icon: '🏛️', gradient: 'linear-gradient(135deg, #c41e3a, #8b0000)', slogan: '新中国从这里走来', location: '石家庄市平山县', rating: 4.9, isFree: true, needReserve: true, tags: ['革命圣地'], introduction: '西柏坡位于河北省石家庄市平山县，是解放战争时期中央工委、中共中央和解放军总部的所在地。', history: '1948年5月至1949年3月，中共中央在西柏坡指挥了辽沈、淮海、平津三大战役，召开了七届二中全会。', tips: ['建议游览2-3小时', '免费参观需预约', '周一闭馆'], audioGuides: [{ id: 1, title: '西柏坡概述', duration: 180, transcript: '欢迎来到西柏坡纪念馆，这里被誉为新中国从这里走来...' }, { id: 2, title: '七届二中全会', duration: 240, transcript: '1949年3月，中国共产党第七届中央委员会第二次全体会议在此召开...' }] },
  { id: 2, name: '狼牙山五壮士纪念地', icon: '⛰️', gradient: 'linear-gradient(135deg, #2c5530, #1a3a1c)', slogan: '英雄壮举，气壮山河', location: '保定市易县', rating: 4.8, isFree: false, needReserve: false, tags: ['抗战遗址'], introduction: '狼牙山位于河北省保定市易县，因奇峰林立状若狼牙而得名。', history: '1941年9月25日，八路军五名战士为掩护群众撤退，在狼牙山顶峰与日军激战后跳崖。', tips: ['建议游览3-4小时', '门票65元', '山路较陡注意安全'], audioGuides: [{ id: 1, title: '狼牙山简介', duration: 150, transcript: '狼牙山五壮士的故事是中国抗日战争中最悲壮的篇章之一...' }] },
  { id: 3, name: '白洋淀雁翎队纪念馆', icon: '🚤', gradient: 'linear-gradient(135deg, #1890ff, #096dd9)', slogan: '华北明珠，水上传奇', location: '雄安新区安新县', rating: 4.7, isFree: false, needReserve: true, tags: ['水上游击'], introduction: '白洋淀是华北平原最大的淡水湖泊，抗战时期雁翎队在此神出鬼没打击日寇。', history: '雁翎队成立于1939年，因队员在枪上插雁翎作标志而得名，被誉为淀上神兵。', tips: ['建议游览半天', '门票40元', '夏季荷花最美'], audioGuides: [{ id: 1, title: '白洋淀概述', duration: 160, transcript: '白洋淀，华北平原上的一颗明珠...' }] },
  { id: 4, name: '塞罕坝展览馆', icon: '🌲', gradient: 'linear-gradient(135deg, #228b22, #006400)', slogan: '荒原变林海的绿色奇迹', location: '承德市围场县', rating: 4.9, isFree: false, needReserve: true, tags: ['生态文明'], introduction: '塞罕坝是世界上面积最大的人工林场，三代人用55年将荒原变成百万亩林海。', history: '1962年，369名创业者来到塞罕坝，开始了艰苦卓绝的造林事业。', tips: ['建议游览1-2天', '门票130元', '秋季色彩最美'], audioGuides: [{ id: 1, title: '塞罕坝精神', duration: 180, transcript: '塞罕坝，蒙古语意为美丽的高岭...' }] },
  { id: 5, name: '李大钊纪念馆', icon: '📚', gradient: 'linear-gradient(135deg, #1e3a5f, #0d1f33)', slogan: '铁肩担道义，妙手著文章', location: '唐山市乐亭县', rating: 4.8, isFree: true, needReserve: true, tags: ['革命先驱'], introduction: '李大钊纪念馆是为纪念中国共产主义运动先驱李大钊同志而建立的。', history: '李大钊是中国共产党的主要创始人之一，最早在中国传播马克思主义。', tips: ['建议游览1-2小时', '免费参观需预约', '周一闭馆'], audioGuides: [{ id: 1, title: '李大钊生平', duration: 200, transcript: '李大钊，字守常，河北乐亭人...' }] },
  { id: 6, name: '冉庄地道战遗址', icon: '🚇', gradient: 'linear-gradient(135deg, #5d4e37, #3d3225)', slogan: '地下长城，抗战奇迹', location: '保定市清苑区', rating: 4.7, isFree: false, needReserve: false, tags: ['地道战'], introduction: '冉庄地道战遗址是抗日战争时期冀中平原人民创造的地道战典型代表。', history: '抗战时期，冉庄人民挖掘了长达16公里的地道网，有力打击了日本侵略者。', tips: ['建议游览2小时', '门票30元', '可体验地道'], audioGuides: [{ id: 1, title: '地道战简介', duration: 170, transcript: '冉庄地道战是中国人民抗日战争中的伟大创举...' }] }
]

const redSpots = ref<RedSpot[]>(defaultSpots)

// 景点导览
const currentSpot = ref<RedSpot | null>(null)
const playingAudioId = ref<number | null>(null)
const playingAudio = ref<AudioGuide | null>(null)
const audioCurrentTime = ref(0)
const audioProgress = ref(0)
let audioTimer: any = null

// 收藏的景点
const favoriteSpots = ref<number[]>([])

// 模拟用户评价数据
const spotReviews = ref([
  { id: 1, avatar: '👨', name: '红色旅行者', date: '2026-01-10', rating: 5, content: '非常震撼的红色教育基地，讲解员讲得很详细，让人深刻感受到革命先辈的伟大精神。强烈推荐！' },
  { id: 2, avatar: '👩', name: '历史爱好者', date: '2026-01-08', rating: 5, content: '带孩子来接受爱国主义教育，孩子收获很大。景区环境优美，设施完善。' },
  { id: 3, avatar: '👴', name: '老党员', date: '2026-01-05', rating: 5, content: '重温革命历史，缅怀革命先烈。每次来都有新的感悟，值得多次参观。' }
])

// 收藏/取消收藏景点
const toggleFavorite = (spotId: number) => {
  const idx = favoriteSpots.value.indexOf(spotId)
  if (idx > -1) {
    favoriteSpots.value.splice(idx, 1)
    ElMessage.success('已取消收藏')
  } else {
    favoriteSpots.value.push(spotId)
    ElMessage.success('已添加到收藏')
  }
  // 保存到本地存储
  localStorage.setItem('favoriteSpots', JSON.stringify(favoriteSpots.value))
}

// 打开导航
const openNavigation = (spot: RedSpot) => {
  const url = `https://uri.amap.com/search?keyword=${encodeURIComponent(spot.name)}&city=${encodeURIComponent(spot.location)}`
  window.open(url, '_blank')
  ElMessage.success('正在打开高德地图导航...')
}

// 加入行程
const addToRoute = (spot: RedSpot) => {
  if (!selectedSpots.value.includes(spot.id)) {
    selectedSpots.value.push(spot.id)
    ElMessage.success(`已将"${spot.name}"加入行程规划`)
  } else {
    ElMessage.info('该景点已在行程中')
  }
  // 切换到智能路线标签
  activeTab.value = 'route'
}

// 预约门票
const bookSpotTicket = (spot: RedSpot) => {
  activeTab.value = 'ticket'
  ElMessage.info(`请在门票预订中选择"${spot.name}"进行预约`)
}

const selectSpot = async (spot: RedSpot) => { 
  if (currentSpot.value?.id === spot.id) {
    currentSpot.value = null
    return
  }
  currentSpot.value = spot
  
  // 尝试从 n8n 获取语音讲解内容
  try {
    const res = await n8nApi.getAudioGuide({ spotId: spot.id, spotName: spot.name })
    if (res && res.guide && res.guide.chapters && res.guide.chapters.length > 0) {
      // 更新当前景点的语音讲解
      currentSpot.value = {
        ...spot,
        audioGuides: res.guide.chapters.map((ch: any) => ({
          id: ch.id,
          title: ch.title,
          duration: ch.duration,
          transcript: ch.content,
          audioUrl: ch.audioUrl
        }))
      }
    }
  } catch (e) {
    console.log('使用默认语音讲解数据')
  }
}
const formatDuration = (s: number) => `${Math.floor(s / 60)}:${(s % 60).toString().padStart(2, '0')}`
const toggleAudio = (audio: AudioGuide) => {
  if (playingAudioId.value === audio.id) { playingAudioId.value = null; playingAudio.value = null; clearInterval(audioTimer); return }
  playingAudioId.value = audio.id; playingAudio.value = audio; audioCurrentTime.value = 0; audioProgress.value = 0
  clearInterval(audioTimer)
  audioTimer = setInterval(() => {
    if (audioCurrentTime.value >= audio.duration) { clearInterval(audioTimer); playingAudioId.value = null; playingAudio.value = null; return }
    audioCurrentTime.value++; audioProgress.value = (audioCurrentTime.value / audio.duration) * 100
  }, 1000)
}

// 智能路线
const selectedSpots = ref<number[]>([])
const planDate = ref(new Date().toISOString().split('T')[0])
const planDays = ref(1)
const planLoading = ref(false)
const generatedPlan = ref<any>(null)

const hotRoutes = ref([
  { id: '1', rank: 1, name: '西柏坡红色经典一日游', spots: [1], duration: '1天', views: 12580, rating: 4.9 },
  { id: '2', rank: 2, name: '白洋淀+冉庄抗战寻迹', spots: [3, 6], duration: '1天', views: 9870, rating: 4.8 },
  { id: '3', rank: 3, name: '太行山红色生态游', spots: [2, 4], duration: '2天', views: 8560, rating: 4.8 },
  { id: '4', rank: 4, name: '河北红色全景游', spots: [1, 2, 3, 5], duration: '3天', views: 6780, rating: 4.9 }
])

// 我的行程
const myPlans = ref<any[]>([])
const myPlansLoading = ref(false)

const loadMyPlans = async () => {
  console.log('[Tourism] loadMyPlans 开始')
  myPlansLoading.value = true
  try {
    // 直接使用 axios 发送请求，绕过可能的问题
    const axios = (await import('axios')).default
    const res = await axios.get('/api/tourism/smart-route/my-plans', {
      headers: { 'X-User-Id': '1' }
    })
    console.log('[Tourism] 直接axios响应:', res.data)
    
    const data = res.data?.data || res.data
    console.log('[Tourism] 解析后数据:', data)
    
    if (Array.isArray(data)) {
      myPlans.value = data
      console.log('[Tourism] 设置 myPlans:', myPlans.value.length, '条')
    } else if (data) {
      myPlans.value = [data]
    } else {
      myPlans.value = []
    }
  } catch (e) {
    console.error('[Tourism] 加载我的行程失败:', e)
    myPlans.value = []
  } finally {
    myPlansLoading.value = false
    console.log('[Tourism] loadMyPlans 结束, myPlans =', myPlans.value)
  }
}

const viewSavedPlan = (plan: any) => {
  console.log('[Tourism] 查看保存的行程:', plan)
  
  // 解析保存的行程数据
  let planData = plan.plan_data || plan.planData
  if (typeof planData === 'string') {
    try { 
      planData = JSON.parse(planData) 
      console.log('[Tourism] 解析后的 planData:', planData)
    } catch (e) { 
      console.error('[Tourism] JSON解析失败:', e)
      planData = null 
    }
  }
  
  if (planData && planData.plan) {
    // n8n 格式的数据，使用 parseN8nPlan 解析
    const parsed = parseN8nPlan(planData)
    if (parsed) {
      generatedPlan.value = parsed
      ElMessage.success(`已加载行程：${plan.title}`)
      return
    }
  }
  
  // 如果 planData 直接包含行程信息
  if (planData && (planData['详细行程'] || planData['行程概述'])) {
    const parsed = parseN8nPlan({ plan: planData })
    if (parsed) {
      generatedPlan.value = parsed
      ElMessage.success(`已加载行程：${plan.title}`)
      return
    }
  }
  
  // 直接使用 plan 对象的字段
  generatedPlan.value = {
    title: plan.title || 'AI生成行程',
    description: plan.description || 'AI智能规划的红色之旅',
    days: plan.days || [],
    estimatedCost: plan.estimated_cost || plan.estimatedCost || 200,
    tips: plan.tips || ['携带身份证', '穿舒适的鞋子', '提前预约免费景点']
  }
  ElMessage.success(`已加载行程：${plan.title}`)
}

const formatPlanDate = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}/${date.getDate()}`
}

const getSpotById = (id: number) => redSpots.value.find(s => s.id === id)
const toggleSpotSelect = (id: number) => { const idx = selectedSpots.value.indexOf(id); idx > -1 ? selectedSpots.value.splice(idx, 1) : selectedSpots.value.push(id); generatedPlan.value = null }
const removeSpot = (id: number) => { const idx = selectedSpots.value.indexOf(id); if (idx > -1) selectedSpots.value.splice(idx, 1); generatedPlan.value = null }
const useHotRoute = (route: any) => { selectedSpots.value = [...route.spots]; generatedPlan.value = null; ElMessage.success(`已选择"${route.name}"`) }
const formatNumber = (n: number) => n >= 10000 ? (n / 10000).toFixed(1) + 'w' : n.toString()

// 解析 n8n 返回的中文格式行程数据
const parseN8nPlan = (data: any) => {
  console.log('[parseN8nPlan] 输入数据:', data)
  
  // n8n 返回数组时取第一个
  const plan = Array.isArray(data) ? data[0]?.plan : data?.plan || data
  if (!plan) {
    console.log('[parseN8nPlan] plan 为空')
    return null
  }
  
  console.log('[parseN8nPlan] plan 对象:', plan)
  
  const overview = plan['行程概述'] || plan['行程概况'] || plan.overview || {}
  const details = plan['详细行程'] || plan['每日详细安排'] || plan.details || []
  const practicalInfo = plan['实用信息'] || plan['实用建议'] || {}
  const budgetRef = plan['预算参考'] || practicalInfo['预算参考'] || {}
  const specialTips = plan['特别提醒'] || ''
  
  console.log('[parseN8nPlan] overview:', overview)
  console.log('[parseN8nPlan] details:', details)
  
  // 解析预算
  let estimatedCost = 200
  if (budgetRef['人均总预算']) {
    const match = String(budgetRef['人均总预算']).match(/\d+/)
    if (match) estimatedCost = parseInt(match[0])
  }
  
  // 转换为前端期望的格式
  const result = {
    title: overview['主题特色'] || overview['行程主题'] || overview['主题'] || plan.title || `红色之旅`,
    description: `${overview['总天数'] || details.length || 1}天行程 · ${overview['出发日期'] || '明天'}出发`,
    days: details.map((day: any, idx: number) => {
      // 获取当天的行程安排
      const arrangements = day['行程安排'] || day['安排'] || day['行程详情'] || []
      
      console.log(`[parseN8nPlan] 第${idx+1}天安排:`, arrangements)
      
      return {
        day: idx + 1,
        date: day['日期'] || day['date'] || `第${idx + 1}天`,
        theme: day['主题'] || day['theme'] || '',
        spots: arrangements.map((item: any, i: number) => {
          // 解析景点详情
          const spotDetail = item['景点详情'] || item['景点信息'] || {}
          let detailText = ''
          
          // 构建详情文本
          if (item['内容'] && Array.isArray(item['内容'])) {
            detailText += item['内容'].join('\n')
          }
          if (item['交通方式']) detailText += `\n🚗 ${item['交通方式']}`
          if (item['距离']) detailText += ` · ${item['距离']}`
          if (item['预计用时']) detailText += ` · ${item['预计用时']}`
          if (item['门票'] || spotDetail['门票']) detailText += `\n🎫 ${item['门票'] || spotDetail['门票']}`
          if (spotDetail['建议游览时间']) detailText += ` · 建议${spotDetail['建议游览时间']}`
          if (item['注意事项']) {
            const notes = Array.isArray(item['注意事项']) ? item['注意事项'].join('；') : item['注意事项']
            detailText += `\n⚠️ ${notes}`
          }
          if (item['推荐餐厅'] || item['餐饮建议']) detailText += `\n🍽️ ${item['推荐餐厅'] || item['餐饮建议']}`
          if (item['特色菜'] || item['特色菜品']) detailText += ` · ${item['特色菜'] || item['特色菜品']}`
          if (item['人均消费']) detailText += ` · 人均${item['人均消费']}`
          if (item['住宿推荐']) {
            const hotel = typeof item['住宿推荐'] === 'object' 
              ? Object.entries(item['住宿推荐']).map(([k,v]) => `${k}: ${v}`).join('；')
              : item['住宿推荐']
            detailText += `\n🏨 ${hotel}`
          }
          
          // 核心参观点
          if (spotDetail['核心参观点'] && Array.isArray(spotDetail['核心参观点'])) {
            detailText += `\n📍 核心参观点：${spotDetail['核心参观点'].join('、')}`
          }
          
          return {
            order: i + 1,
            name: item['活动'] || item['景点'] || item.activity || `活动${i+1}`,
            duration: item['时间'] || item.time || '',
            tips: detailText.trim() || item['详情'] || item['备注'] || ''
          }
        }),
        meals: day['餐饮建议'] || day['餐饮安排'] || {},
        accommodation: day['住宿建议'] || '',
        notes: day['注意事项'] || []
      }
    }),
    totalDistance: 0,
    estimatedCost: estimatedCost,
    // 整合所有提示信息
    tips: [
      ...(practicalInfo['注意事项'] || []),
      ...(practicalInfo['推荐携带物品'] ? [`携带物品：${practicalInfo['推荐携带物品'].join('、')}`] : []),
      ...(practicalInfo['装备准备'] ? [`装备：${practicalInfo['装备准备'].join('、')}`] : []),
      ...(practicalInfo['交通提示'] || practicalInfo['交通建议'] || []),
      ...(specialTips ? [specialTips] : [])
    ].filter(Boolean),
    // 额外信息
    budget: budgetRef,
    accommodation: practicalInfo['住宿建议'] || [],
    transport: practicalInfo['交通提示'] || []
  }
  
  console.log('[parseN8nPlan] 解析结果:', result)
  return result
}

// 等待时间
const waitingTime = ref(0)
let waitingTimer: any = null
let planStartTime: number | null = null

const formatWaitingTime = (seconds: number) => {
  const min = Math.floor(seconds / 60)
  const sec = seconds % 60
  return min > 0 ? `${min}分${sec}秒` : `${sec}秒`
}

const generatePlan = async () => {
  if (selectedSpots.value.length === 0) { ElMessage.warning('请先选择景点'); return }
  
  const spotNames = selectedSpots.value.map(id => getSpotById(id)?.name || '')
  planLoading.value = true
  waitingTime.value = 0
  planStartTime = Date.now()
  generatedPlan.value = null // 清除之前的结果
  
  // 启动计时器
  clearInterval(waitingTimer)
  waitingTimer = setInterval(() => {
    if (planStartTime) {
      waitingTime.value = Math.floor((Date.now() - planStartTime) / 1000)
    }
  }, 1000)
  
  ElMessage.info('🤖 AI正在规划行程，最长需要5分钟，请耐心等待...')
  
  try {
    console.log('[Tourism] 开始调用 n8n AI 行程规划...')
    const res = await n8nApi.generateAITripPlan({ 
      spots: spotNames, 
      duration: planDays.value, 
      startDate: planDate.value 
    })
    console.log('[Tourism] n8n 响应:', res)
    
    const parsed = parseN8nPlan(res)
    console.log('[Tourism] 解析后的数据:', parsed)
    
    if (parsed && parsed.title) {
      // 只要有标题就认为是有效响应
      generatedPlan.value = parsed
      ElMessage.success(`✅ AI行程规划完成！用时 ${formatWaitingTime(waitingTime.value)}`) 
    } else {
      throw new Error('解析失败或响应为空')
    }
  } catch (e: any) {
    console.error('[Tourism] n8n 请求失败:', e)
    const elapsed = Date.now() - (planStartTime || 0)
    
    // 只有超过5分钟才认为是超时
    if (elapsed >= 300000) {
      ElMessage.error('请求超时（超过5分钟），请稍后重试')
      // 超时后生成本地数据
      generatedPlan.value = { 
        title: `${spotNames[0]}等${spotNames.length}景点游`, 
        description: '为您规划的红色之旅（AI超时，基础版）', 
        days: [{ day: 1, date: planDate.value, spots: spotNames.map((n, i) => ({ name: n, duration: '2-3小时', tips: '建议提前预约，携带身份证', order: i + 1 })) }], 
        totalDistance: spotNames.length * 30, 
        estimatedCost: spotNames.length * 150, 
        tips: ['携带身份证', '穿舒适的鞋子', '提前预约免费景点', '注意天气变化'] 
      }
    } else {
      // 未超时但失败，提示用户
      ElMessage.error(`AI规划失败: ${e.message || '请检查n8n工作流是否正常运行'}`)
      // 不自动生成本地数据，让用户决定是否重试
    }
  } finally { 
    planLoading.value = false
    clearInterval(waitingTimer)
    planStartTime = null
  }
}

// 保存行程到数据库
const savePlan = async () => {
  if (!generatedPlan.value) return
  try {
    // 转换为后端期望的格式
    const planToSave = {
      title: generatedPlan.value.title,
      description: generatedPlan.value.description,
      days: generatedPlan.value.days?.map((d: any) => ({
        day: d.day,
        date: d.date,
        spots: d.spots?.map((s: any) => ({
          order: s.order,
          name: s.name,
          duration: s.duration,
          tips: s.tips
        })) || [],
        meals: d.meals,
        accommodation: d.accommodation
      })) || [],
      totalDistance: generatedPlan.value.totalDistance || 0,
      estimatedCost: generatedPlan.value.estimatedCost || 0,
      tips: generatedPlan.value.tips || []
    }
    await tourismApi.saveTripPlan(planToSave)
    ElMessage.success('行程已保存到我的收藏')
    // 刷新热门路线
    loadHotRoutes()
  } catch (e) {
    console.error('保存失败:', e)
    ElMessage.error('保存失败，请稍后重试')
  }
}
const sharePlan = () => { navigator.clipboard.writeText(window.location.href); ElMessage.success('链接已复制') }

// 实时信息
const realtimeLoading = ref(false)
const weatherData = ref<tourismApi.SpotWeather[]>([])
const crowdData = ref<tourismApi.CrowdInfo[]>([
  { spotId: 1, name: '西柏坡纪念馆', icon: '🏛️', percent: 65, level: 'medium', levelText: '适中', waitTime: 15, bestTime: '14:00-16:00' },
  { spotId: 2, name: '狼牙山景区', icon: '⛰️', percent: 35, level: 'low', levelText: '人少', waitTime: 5, bestTime: '全天' },
  { spotId: 3, name: '白洋淀景区', icon: '🚤', percent: 80, level: 'high', levelText: '较多', waitTime: 30, bestTime: '8:00-10:00' },
  { spotId: 4, name: '塞罕坝森林公园', icon: '🌲', percent: 25, level: 'low', levelText: '人少', waitTime: 0, bestTime: '全天' },
  { spotId: 5, name: '李大钊纪念馆', icon: '📚', percent: 45, level: 'medium', levelText: '适中', waitTime: 10, bestTime: '下午' },
  { spotId: 6, name: '冉庄地道战遗址', icon: '🚇', percent: 55, level: 'medium', levelText: '适中', waitTime: 15, bestTime: '上午' }
])
const travelTips = ref([
  { icon: '👔', title: '穿衣建议', content: '今日气温3-12℃，建议穿保暖外套', type: 'info' },
  { icon: '🚗', title: '出行提示', content: '西柏坡高速畅通，建议上午出发', type: 'success' },
  { icon: '📸', title: '摄影推荐', content: '今日光线充足，适合拍摄', type: 'info' },
  { icon: '⚠️', title: '注意事项', content: '塞罕坝地区有降雪，谨慎前往', type: 'warning' }
])
const getWeatherIcon = (c: string) => ({ '晴': '☀️', '多云': '⛅', '阴': '☁️', '小雨': '🌧️', '雪': '❄️', '雨': '🌧️' }[c] || '🌤️')

// AI出行建议（从天气API获取）
const aiTravelSuggestion = ref('')

const loadRealtimeData = async () => {
  realtimeLoading.value = true
  try {
    // 优先使用 n8n 天气API（带AI出行建议）
    const spotNames = redSpots.value.map(s => s.name)
    const weatherRes = await n8nApi.getSpotWeather(spotNames).catch(() => null)
    
    if (weatherRes && Array.isArray(weatherRes) && weatherRes.length > 0) {
      weatherData.value = weatherRes
      // 获取AI建议
      if (weatherRes[0]?.suggestion) {
        aiTravelSuggestion.value = weatherRes[0].suggestion
      }
    } else {
      // 降级到后端API
      const weather = await tourismApi.getSpotWeather(spotNames).catch(() => null)
      if (weather) weatherData.value = weather
    }
    
    // 加载人流量和出行建议
    const [crowd, tips] = await Promise.all([
      tourismApi.getCrowdInfo().catch(() => null),
      tourismApi.getTravelTips().catch(() => null)
    ])
    if (crowd) crowdData.value = crowd
    if (tips) travelTips.value = tips
  } catch {
    // 使用默认数据
    weatherData.value = redSpots.value.map(s => ({ 
      spotName: s.name, 
      date: new Date().toISOString().split('T')[0], 
      condition: ['晴', '多云', '阴'][Math.floor(Math.random() * 3)], 
      temperature: { min: Math.floor(Math.random() * 5), max: Math.floor(Math.random() * 10) + 10 }, 
      humidity: 50, 
      suggestion: '天气适宜出行' 
    }))
  } finally { realtimeLoading.value = false }
}

// 门票
const ticketList = ref([
  { id: 1, name: '西柏坡纪念馆', icon: '🏛️', gradient: 'linear-gradient(135deg, #c41e3a, #8b0000)', address: '石家庄市平山县', openTime: '09:00-17:00', rating: 4.9, price: 0, needReserve: true, sold: 12580 },
  { id: 2, name: '狼牙山风景区', icon: '⛰️', gradient: 'linear-gradient(135deg, #2c5530, #1a3a1c)', address: '保定市易县', openTime: '08:00-18:00', rating: 4.8, price: 65, originalPrice: 80, discount: '8折', needReserve: false, sold: 8920 },
  { id: 3, name: '白洋淀景区', icon: '🚤', gradient: 'linear-gradient(135deg, #1890ff, #096dd9)', address: '雄安新区安新县', openTime: '08:00-18:00', rating: 4.7, price: 40, needReserve: true, sold: 15670 },
  { id: 4, name: '塞罕坝国家森林公园', icon: '🌲', gradient: 'linear-gradient(135deg, #228b22, #006400)', address: '承德市围场县', openTime: '全天开放', rating: 4.9, price: 130, originalPrice: 150, discount: '特惠', needReserve: true, sold: 7890 },
  { id: 5, name: '李大钊纪念馆', icon: '📚', gradient: 'linear-gradient(135deg, #1e3a5f, #0d1f33)', address: '唐山市乐亭县', openTime: '09:00-16:30', rating: 4.8, price: 0, needReserve: true, sold: 4560 },
  { id: 6, name: '冉庄地道战遗址', icon: '🚇', gradient: 'linear-gradient(135deg, #5d4e37, #3d3225)', address: '保定市清苑区', openTime: '08:30-17:30', rating: 4.7, price: 30, needReserve: false, sold: 6540 }
])
const bookTicket = async (t: any) => {
  try {
    const result = await tourismApi.bookTicket({
      ticketId: t.id,
      visitDate: new Date().toISOString().split('T')[0],
      quantity: 1
    })
    ElMessage.success(result.message || (t.needReserve ? `预约成功：${t.name}` : `预订成功：${t.name}`))
  } catch {
    ElMessage.success(t.needReserve ? `正在预约 ${t.name}` : `正在预订 ${t.name} 门票`)
  }
}

// AI助手
const showAI = ref(false)
const aiInput = ref('')
const aiTyping = ref(false)
const aiChatRef = ref<HTMLElement | null>(null)
const aiSessionId = ref('session-' + Date.now())
const aiMessages = ref([{ role: 'assistant' as const, content: '👋 您好！我是冀小游，河北红色旅游智能助手。\n\n我可以帮您：\n• 🏛️ 介绍红色景点\n• 🗺️ 推荐旅游路线\n• 🍜 推荐周边美食\n\n请问有什么可以帮您的？' }])
const formatMessage = (c: string) => (c || '').replace(/\n/g, '<br>')
const askAI = (q: string) => { aiInput.value = q; sendAIMessage() }
const sendAIMessage = async () => {
  if (!aiInput.value.trim() || aiTyping.value) return
  const q = aiInput.value.trim()
  aiMessages.value.push({ role: 'user', content: q })
  aiInput.value = ''
  aiTyping.value = true
  await nextTick(); if (aiChatRef.value) aiChatRef.value.scrollTop = aiChatRef.value.scrollHeight
  try {
    // 优先使用 n8n AI 聊天
    const res = await n8nApi.aiChat({ question: q, sessionId: aiSessionId.value })
    const answer = res?.answer || res?.message || res?.text || res?.content
    if (answer) {
      aiMessages.value.push({ role: 'assistant', content: answer })
    } else {
      throw new Error('No answer in response')
    }
  } catch (e) {
    console.log('n8n AI chat failed:', e)
    // 降级到后端 API
    try {
      const res = await tourismApi.aiChat({ question: q, sessionId: aiSessionId.value })
      const answer = res?.answer || res?.message || res?.text || res?.content
      if (answer) {
        aiMessages.value.push({ role: 'assistant', content: answer })
      } else {
        throw new Error('No answer in response')
      }
    } catch {
      // 最终降级到本地回复
      const ql = q.toLowerCase()
      let ans = '感谢您的提问！我可以为您介绍景点、推荐路线、推荐美食，请告诉我您想了解什么？'
      if (ql.includes('西柏坡')) ans = '🏛️ 西柏坡纪念馆\n\n位于石家庄市平山县，被誉为"新中国从这里走来"。\n\n✨ 必看：七届二中全会会址、毛泽东旧居\n🎫 门票：免费（需预约）\n⏰ 开放：9:00-17:00，周一闭馆'
      else if (ql.includes('路线') || ql.includes('推荐')) ans = '📍 推荐路线：西柏坡红色经典一日游\n\n🗺️ 行程：西柏坡纪念馆 → 七届二中全会会址 → 毛泽东旧居\n💰 费用：约200元/人'
      else if (ql.includes('美食') || ql.includes('吃')) ans = '🍜 特色美食推荐：\n\n📍 平山：缸炉烧饼、抿须面\n📍 保定：驴肉火烧\n📍 白洋淀：炖杂鱼、荷叶鸡\n\n💰 人均：30-80元'
      aiMessages.value.push({ role: 'assistant', content: ans })
    }
  } finally {
    aiTyping.value = false
    await nextTick(); if (aiChatRef.value) aiChatRef.value.scrollTop = aiChatRef.value.scrollHeight
  }
}

// 从数据库加载热门路线
const loadHotRoutes = async () => {
  try {
    // 优先从后端数据库加载（按 booking_count 排序）
    const routes = await tourismApi.getHotRoutes(10)
    if (routes && routes.length > 0) {
      hotRoutes.value = routes.map((r: any, idx: number) => ({
        id: r.id || String(idx + 1),
        rank: r.rank || idx + 1,
        name: r.name,
        spots: parseSpotIds(r.spots || r.spotNames),
        duration: r.duration || r.days + '天',
        views: r.views || r.viewCount || 0,
        rating: r.rating || 4.8
      }))
      return
    }
  } catch (e) {
    console.log('后端热门路线加载失败，使用默认数据')
  }
  
  // 不再调用 n8n，直接使用默认数据
  console.log('使用默认路线数据')
}

// 解析景点ID
const parseSpotIds = (spots: any): number[] => {
  if (!spots) return []
  if (Array.isArray(spots)) {
    return spots.map((s: any) => {
      if (typeof s === 'number') return s
      if (typeof s === 'string') {
        const idx = redSpots.value.findIndex(rs => rs.name.includes(s))
        return idx >= 0 ? idx + 1 : 1
      }
      return 1
    })
  }
  if (typeof spots === 'string') {
    // 逗号分隔的景点名称
    return spots.split(',').map((name: string) => {
      const idx = redSpots.value.findIndex(rs => rs.name.includes(name.trim()))
      return idx >= 0 ? idx + 1 : 1
    })
  }
  return []
}

// 加载数据
const loadData = async () => {
  console.log('[Tourism] loadData 开始执行')
  
  // 尝试从后端加载景点数据
  try {
    const spots = await tourismApi.getRedSpots()
    if (spots && spots.length > 0) {
      redSpots.value = spots as any
    }
  } catch (e) {
    console.log('使用默认景点数据')
  }
  
  // 加载热门路线（不阻塞后续加载）
  try {
    await loadHotRoutes()
  } catch (e) {
    console.log('热门路线加载失败:', e)
  }
  
  // 加载我的行程
  console.log('[Tourism] 准备调用 loadMyPlans')
  try {
    await loadMyPlans()
  } catch (e) {
    console.error('[Tourism] loadMyPlans 异常:', e)
  }
  console.log('[Tourism] loadMyPlans 完成, myPlans.length =', myPlans.value.length)
  
  // 加载门票
  try {
    const tickets = await tourismApi.getTickets()
    if (tickets && tickets.length > 0) {
      ticketList.value = tickets as any
    }
  } catch (e) {
    console.log('使用默认门票数据')
  }
}

onMounted(() => {
  console.log('=== Tourism onMounted 执行 ===')
  // 加载收藏的景点
  const savedFavorites = localStorage.getItem('favoriteSpots')
  if (savedFavorites) {
    try {
      favoriteSpots.value = JSON.parse(savedFavorites)
    } catch (e) {
      console.log('加载收藏失败')
    }
  }
  loadData()
  loadRealtimeData()
})
</script>

<style scoped>
.tourism-page {
  min-height: 100vh;
  background: #f5f5f5;
}

/* Hero */
.hero-banner {
  background: linear-gradient(135deg, #52c41a 0%, #237804 100%);
  padding: 60px 20px;
  text-align: center;
  color: white;
}
.hero-tag {
  display: inline-block;
  background: rgba(255,255,255,0.2);
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 14px;
  margin-bottom: 15px;
}
.hero-banner h1 {
  font-size: 36px;
  margin: 0 0 10px;
}
.hero-banner > .hero-content > p {
  font-size: 16px;
  margin: 0 0 25px;
  opacity: 0.9;
}
.hero-tabs {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
}
.hero-tabs button {
  background: rgba(255,255,255,0.15);
  border: 1px solid rgba(255,255,255,0.3);
  color: white;
  padding: 12px 24px;
  border-radius: 25px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}
.hero-tabs button:hover {
  background: rgba(255,255,255,0.25);
}
.hero-tabs button.active {
  background: white;
  color: #52c41a;
}

/* Main */
.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}
.tab-content {
  animation: fadeIn 0.3s;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
.section-header {
  text-align: center;
  margin-bottom: 30px;
}
.section-header h2 {
  font-size: 24px;
  margin: 0 0 8px;
  color: #333;
}
.section-header p {
  color: #666;
  margin: 0;
}
.panel {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.panel h3 {
  font-size: 16px;
  margin: 0 0 15px;
  color: #333;
}

/* 景点卡片 */
.spots-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}
.spot-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.spot-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.1);
}
.spot-cover {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}
.spot-icon {
  font-size: 48px;
}
.spot-badges {
  position: absolute;
  top: 10px;
  right: 10px;
  display: flex;
  gap: 5px;
}
.badge-free {
  background: #52c41a;
  color: white;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
}
.badge-reserve {
  background: #fa8c16;
  color: white;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
}
.badge-discount {
  background: #f5222d;
  color: white;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
}
.spot-info {
  padding: 15px;
}
.spot-info h3 {
  margin: 0 0 8px;
  font-size: 16px;
  color: #333;
}
.spot-slogan {
  color: #666;
  font-size: 13px;
  margin: 0 0 10px;
}
.spot-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
}

/* 景点详情 */
.spot-detail {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}
.detail-title {
  display: flex;
  align-items: center;
  gap: 15px;
}
.detail-icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}
.detail-title h3 {
  margin: 0 0 5px;
  font-size: 18px;
}
.detail-title p {
  margin: 0;
  color: #666;
  font-size: 14px;
}
.btn-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
}

/* 收藏按钮 */
.btn-favorite {
  position: absolute;
  top: 10px;
  left: 10px;
  background: rgba(255,255,255,0.9);
  border: none;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s;
}
.btn-favorite:hover, .btn-favorite.active {
  transform: scale(1.1);
}
.btn-favorite-lg {
  background: #fff5f5;
  border: 1px solid #ffccc7;
  padding: 8px 16px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}
.btn-favorite-lg.active {
  background: #fff1f0;
  border-color: #ff4d4f;
}

/* 景点额外信息 */
.spot-extra {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px dashed #eee;
}
.spot-price {
  color: #f5222d;
  font-weight: bold;
  font-size: 14px;
}
.spot-time {
  color: #999;
  font-size: 12px;
}

/* 详情操作区 */
.detail-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 信息卡片 */
.detail-info-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
  margin-bottom: 20px;
}
.info-card {
  background: linear-gradient(135deg, #f6ffed, #e6f7ff);
  padding: 15px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  gap: 12px;
}
.info-icon {
  font-size: 24px;
}
.info-content {
  display: flex;
  flex-direction: column;
}
.info-label {
  font-size: 12px;
  color: #666;
}
.info-value {
  font-size: 14px;
  font-weight: bold;
  color: #333;
}

/* 交通指南 */
.transport-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 15px;
}
.transport-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}
.transport-icon {
  font-size: 20px;
}
.transport-item strong {
  display: block;
  margin-bottom: 4px;
  color: #333;
}
.transport-item p {
  margin: 0;
  font-size: 13px;
  color: #666;
}
.btn-navigate {
  background: linear-gradient(135deg, #1890ff, #096dd9);
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}
.btn-navigate:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.4);
}

/* 用户评价 */
.review-count {
  font-size: 12px;
  color: #999;
  font-weight: normal;
}
.reviews-summary {
  display: flex;
  align-items: center;
  gap: 30px;
  margin-bottom: 15px;
  padding: 15px;
  background: #fffbe6;
  border-radius: 8px;
}
.rating-big {
  text-align: center;
}
.rating-num {
  font-size: 36px;
  font-weight: bold;
  color: #fa8c16;
}
.rating-stars {
  display: block;
  font-size: 14px;
}
.rating-text {
  font-size: 12px;
  color: #666;
}
.rating-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.rating-tags .tag {
  background: #fff;
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 12px;
  color: #666;
  border: 1px solid #f0f0f0;
}
.reviews-list {
  max-height: 300px;
  overflow-y: auto;
}
.review-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}
.review-item:last-child {
  border-bottom: none;
}
.review-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}
.review-avatar {
  font-size: 24px;
}
.review-user {
  flex: 1;
}
.review-name {
  display: block;
  font-weight: bold;
  font-size: 14px;
}
.review-date {
  font-size: 12px;
  color: #999;
}
.review-rating {
  font-size: 12px;
}
.review-content {
  margin: 0;
  font-size: 14px;
  color: #333;
  line-height: 1.6;
}

/* 底部操作栏 */
.detail-footer {
  display: flex;
  gap: 15px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}
.btn-add-route {
  flex: 1;
  background: linear-gradient(135deg, #52c41a, #389e0d);
  color: white;
  border: none;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 15px;
  transition: all 0.3s;
}
.btn-add-route:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(82, 196, 26, 0.4);
}
.btn-book-ticket {
  flex: 1;
  background: linear-gradient(135deg, #fa8c16, #d46b08);
  color: white;
  border: none;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 15px;
  transition: all 0.3s;
}
.btn-book-ticket:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(250, 140, 22, 0.4);
}

.detail-body {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}
.detail-section {
  background: #fafafa;
  padding: 15px;
  border-radius: 8px;
}
.detail-section:last-child {
  grid-column: span 2;
}
.detail-section h4 {
  margin: 0 0 10px;
  font-size: 14px;
  color: #333;
}
.detail-section p {
  margin: 0;
  color: #666;
  font-size: 14px;
  line-height: 1.6;
}
.detail-section ul {
  margin: 0;
  padding-left: 20px;
  color: #666;
  font-size: 14px;
}
.detail-section li {
  margin-bottom: 5px;
}

/* 语音讲解 */
.audio-section {
  background: #f6ffed;
  padding: 20px;
  border-radius: 8px;
}
.audio-section h4 {
  margin: 0 0 15px;
  font-size: 14px;
  color: #333;
}
.audio-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 15px;
}
.audio-item {
  display: flex;
  align-items: center;
  gap: 12px;
  background: white;
  padding: 12px 15px;
  border-radius: 8px;
}
.audio-item.playing {
  background: #e6fffb;
  border: 1px solid #52c41a;
}
.audio-num {
  width: 24px;
  height: 24px;
  background: #52c41a;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}
.audio-title {
  flex: 1;
  font-size: 14px;
  color: #333;
}
.audio-duration {
  font-size: 12px;
  color: #999;
}
.btn-play {
  width: 32px;
  height: 32px;
  background: #52c41a;
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  font-size: 14px;
}
.audio-player {
  background: white;
  padding: 15px;
  border-radius: 8px;
}
.player-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
}
.player-progress {
  height: 6px;
  background: #e8e8e8;
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 15px;
}
.progress-bar {
  height: 100%;
  background: #52c41a;
  transition: width 0.3s;
}
.player-text {
  margin: 0;
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  background: #fafafa;
  padding: 12px;
  border-radius: 6px;
}

/* 智能路线 */
.route-layout {
  display: grid;
  grid-template-columns: 350px 1fr;
  gap: 20px;
}
.route-left {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.spot-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.spot-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  border: 1px solid #f0f0f0;
  transition: all 0.2s;
}
.spot-item:hover {
  background: #f6ffed;
}
.spot-item.selected {
  background: #e6fffb;
  border-color: #52c41a;
}
.item-icon {
  font-size: 20px;
}
.item-name {
  flex: 1;
  font-size: 14px;
  color: #333;
}
.item-check {
  color: #52c41a;
  font-weight: bold;
}
.hot-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.hot-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #fafafa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}
.hot-item:hover {
  background: #f0f0f0;
}
.hot-rank {
  width: 24px;
  height: 24px;
  background: #d9d9d9;
  color: #666;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
}
.hot-rank.top {
  background: #faad14;
  color: white;
}
.hot-info {
  flex: 1;
}
.hot-name {
  display: block;
  font-size: 14px;
  color: #333;
}
.hot-meta {
  font-size: 12px;
  color: #999;
}

/* 我的行程样式 */
.btn-refresh-small {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 14px;
  padding: 2px 6px;
  border-radius: 4px;
}
.btn-refresh-small:hover {
  background: #f0f0f0;
}
.loading-state, .empty-state-small {
  text-align: center;
  padding: 20px;
  color: #999;
  font-size: 14px;
}
.my-plans-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-height: 200px;
  overflow-y: auto;
}
.my-plan-item {
  padding: 12px;
  background: linear-gradient(135deg, #fff5f5 0%, #fff 100%);
  border: 1px solid #ffe0e0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}
.my-plan-item:hover {
  border-color: #c41e3a;
  box-shadow: 0 2px 8px rgba(196, 30, 58, 0.1);
}
.plan-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}
.plan-item-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}
.plan-item-date {
  font-size: 12px;
  color: #999;
}
.plan-item-meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #666;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #999;
}
.empty-state span {
  font-size: 48px;
  display: block;
  margin-bottom: 10px;
}
.selected-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}
.tag {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #e6fffb;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 13px;
}
.tag button {
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  padding: 0;
  font-size: 14px;
}
.plan-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 20px;
}
.form-row {
  display: flex;
  align-items: center;
  gap: 10px;
}
.form-row label {
  width: 80px;
  font-size: 14px;
  color: #666;
}
.form-row input,
.form-row select {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  font-size: 14px;
}
.btn-generate {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #52c41a, #237804);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s;
}
.btn-generate:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(82, 196, 26, 0.4);
}
.btn-generate:disabled {
  background: linear-gradient(135deg, #52c41a, #237804);
  cursor: wait;
  transform: none;
  box-shadow: none;
  animation: pulse 2s infinite;
}
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}
.loading-hint {
  text-align: center;
  font-size: 12px;
  color: #52c41a;
  margin-top: 8px;
}
.plan-result {
  background: white;
}
.plan-desc {
  color: #666;
  font-size: 14px;
  margin: 0 0 20px;
}
.day-plan {
  background: #fafafa;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
}
.day-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
}
.day-num {
  background: #52c41a;
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 13px;
}
.day-date {
  color: #999;
  font-size: 13px;
}
.day-spots {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.day-spot {
  display: flex;
  gap: 12px;
}
.spot-order {
  width: 24px;
  height: 24px;
  background: #e6fffb;
  color: #52c41a;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  flex-shrink: 0;
}
.spot-content {
  flex: 1;
}
.spot-content .spot-name {
  display: block;
  font-size: 14px;
  color: #333;
  font-weight: 500;
}
.spot-tips {
  display: block;
  font-size: 12px;
  color: #999;
  margin-top: 3px;
}
.plan-summary {
  display: flex;
  gap: 30px;
  padding: 15px;
  background: #f6ffed;
  border-radius: 8px;
  margin-bottom: 15px;
  font-size: 14px;
  color: #52c41a;
}
.day-theme {
  color: #52c41a;
  font-size: 13px;
  font-weight: 500;
}
.spot-time {
  font-size: 12px;
  color: #52c41a;
  margin: 4px 0;
}
.spot-detail {
  font-size: 12px;
  color: #666;
  margin: 8px 0 0;
  line-height: 1.8;
  background: #f9f9f9;
  padding: 10px;
  border-radius: 6px;
  border-left: 3px solid #52c41a;
  white-space: pre-wrap;
  font-family: inherit;
}
.plan-budget {
  background: #fffbe6;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
}
.plan-budget h4 {
  margin: 0 0 10px;
  font-size: 14px;
  color: #d48806;
}
.budget-items {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  font-size: 13px;
  color: #666;
}
.budget-total {
  color: #d48806;
  font-weight: 600;
}
.plan-tips {
  margin-bottom: 15px;
  padding: 15px;
  background: #f6ffed;
  border-radius: 8px;
}
.plan-tips h4 {
  margin: 0 0 10px;
  font-size: 14px;
  color: #52c41a;
}
.plan-tips ul {
  margin: 0;
  padding-left: 20px;
}
.plan-tips li {
  margin: 6px 0;
  font-size: 13px;
  color: #666;
  line-height: 1.6;
}
.day-meals, .day-notes {
  margin-top: 12px;
  padding: 10px;
  background: #fafafa;
  border-radius: 6px;
  font-size: 13px;
  color: #666;
}
.day-meals strong, .day-notes strong {
  color: #333;
}
.old-plan-tips {
  margin-bottom: 15px;
  padding: 12px;
  background: #fffbe6;
  border-radius: 8px;
  font-size: 13px;
}
.plan-tips ul {
  margin: 8px 0 0 20px;
  padding: 0;
}
.plan-tips li {
  margin: 4px 0;
  color: #666;
}
.plan-actions {
  display: flex;
  gap: 15px;
}
.btn-save,
.btn-share {
  flex: 1;
  padding: 10px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}
.btn-save {
  background: #52c41a;
  color: white;
  border: none;
}
.btn-share {
  background: white;
  color: #52c41a;
  border: 1px solid #52c41a;
}

/* 实时信息 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.section-header h2 {
  margin: 0;
}
.btn-refresh {
  background: #52c41a;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 13px;
}
.btn-refresh:disabled {
  background: #d9d9d9;
}
.weather-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}
.weather-card {
  background: linear-gradient(135deg, #e6f7ff, #bae7ff);
  border-radius: 10px;
  padding: 15px;
  text-align: center;
}
.weather-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  margin-bottom: 10px;
}
.weather-main {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-bottom: 8px;
}
.weather-icon {
  font-size: 32px;
}
.weather-temp {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}
.weather-condition {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}
.weather-range {
  font-size: 12px;
  color: #999;
  margin-bottom: 10px;
}
.weather-tip {
  font-size: 12px;
  color: #52c41a;
  background: rgba(82, 196, 26, 0.1);
  padding: 6px 10px;
  border-radius: 6px;
}
.crowd-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.crowd-item {
  display: grid;
  grid-template-columns: 180px 1fr 100px 100px;
  gap: 15px;
  align-items: center;
  padding: 12px;
  background: #fafafa;
  border-radius: 8px;
}
.crowd-name {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: #333;
}
.crowd-bar {
  height: 8px;
  background: #e8e8e8;
  border-radius: 4px;
  overflow: hidden;
}
.bar-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.5s;
}
.bar-fill.low { background: #52c41a; }
.bar-fill.medium { background: #faad14; }
.bar-fill.high { background: #f5222d; }
.crowd-info {
  display: flex;
  flex-direction: column;
  gap: 3px;
  font-size: 13px;
}
.level-low { color: #52c41a; }
.level-medium { color: #faad14; }
.level-high { color: #f5222d; }
.crowd-best {
  font-size: 12px;
  color: #52c41a;
}
.ai-suggestion-panel {
  background: linear-gradient(135deg, #f6ffed, #e6fffb);
  border: 1px solid #b7eb8f;
}
.ai-suggestion-panel h3 {
  color: #52c41a;
}
.ai-suggestion-content {
  padding: 15px;
  background: white;
  border-radius: 8px;
  line-height: 1.8;
  font-size: 14px;
  color: #333;
}
.ai-suggestion-content p {
  margin: 0;
}
.tips-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}
.tip-card {
  display: flex;
  gap: 15px;
  padding: 15px;
  border-radius: 8px;
  background: #fafafa;
}
.tip-card.info { background: #e6f7ff; }
.tip-card.success { background: #f6ffed; }
.tip-card.warning { background: #fffbe6; }
.tip-icon {
  font-size: 24px;
}
.tip-content strong {
  display: block;
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
}
.tip-content p {
  margin: 0;
  font-size: 13px;
  color: #666;
}

/* 门票 */
.ticket-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}
.ticket-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  transition: all 0.3s;
}
.ticket-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.1);
}
.ticket-cover {
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}
.ticket-icon {
  font-size: 40px;
}
.ticket-badges {
  position: absolute;
  top: 10px;
  right: 10px;
  display: flex;
  gap: 5px;
}
.ticket-body {
  padding: 15px;
}
.ticket-body h3 {
  margin: 0 0 8px;
  font-size: 15px;
  color: #333;
}
.ticket-addr {
  color: #999;
  font-size: 12px;
  margin: 0 0 10px;
}
.ticket-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #666;
  margin-bottom: 12px;
}
.ticket-price {
  margin-bottom: 5px;
}
.price {
  font-size: 20px;
  font-weight: bold;
  color: #f5222d;
}
.price.free {
  color: #52c41a;
}
.original {
  font-size: 13px;
  color: #999;
  text-decoration: line-through;
  margin-left: 8px;
}
.ticket-sold {
  font-size: 12px;
  color: #999;
  margin-bottom: 15px;
}
.ticket-actions {
  display: flex;
}
.btn-book {
  flex: 1;
  padding: 10px;
  background: #52c41a;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}
.btn-book:hover {
  background: #389e0d;
}

/* AI助手 */
.ai-float {
  position: fixed;
  bottom: 30px;
  right: 30px;
  background: linear-gradient(135deg, #52c41a, #237804);
  color: white;
  padding: 12px 20px;
  border-radius: 30px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  box-shadow: 0 4px 15px rgba(82, 196, 26, 0.4);
  z-index: 100;
  transition: all 0.3s;
}
.ai-float:hover {
  transform: scale(1.05);
}
.ai-dialog {
  position: fixed;
  bottom: 100px;
  right: 30px;
  width: 380px;
  height: 500px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.15);
  z-index: 101;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.ai-header {
  background: linear-gradient(135deg, #52c41a, #237804);
  color: white;
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 500;
}
.ai-header button {
  background: none;
  border: none;
  color: white;
  font-size: 20px;
  cursor: pointer;
}
.ai-body {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.ai-msg {
  display: flex;
  gap: 10px;
}
.ai-msg.user {
  flex-direction: row-reverse;
}
.msg-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
}
.ai-msg.assistant .msg-avatar {
  background: #e6fffb;
}
.msg-text {
  max-width: 75%;
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.5;
}
.ai-msg.user .msg-text {
  background: #52c41a;
  color: white;
  border-bottom-right-radius: 4px;
}
.ai-msg.assistant .msg-text {
  background: #f5f5f5;
  color: #333;
  border-bottom-left-radius: 4px;
}
.msg-text.typing {
  display: flex;
  gap: 4px;
  padding: 15px;
}
.msg-text.typing span {
  width: 8px;
  height: 8px;
  background: #52c41a;
  border-radius: 50%;
  animation: typing 1.4s infinite;
}
.msg-text.typing span:nth-child(2) { animation-delay: 0.2s; }
.msg-text.typing span:nth-child(3) { animation-delay: 0.4s; }
@keyframes typing {
  0%, 60%, 100% { transform: translateY(0); }
  30% { transform: translateY(-8px); }
}
.ai-footer {
  padding: 15px;
  border-top: 1px solid #f0f0f0;
}
.quick-btns {
  display: flex;
  gap: 8px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}
.quick-btns button {
  background: #f5f5f5;
  border: none;
  padding: 6px 12px;
  border-radius: 15px;
  font-size: 12px;
  cursor: pointer;
  color: #666;
  transition: all 0.2s;
}
.quick-btns button:hover {
  background: #e6fffb;
  color: #52c41a;
}
.ai-input {
  display: flex;
  gap: 10px;
}
.ai-input input {
  flex: 1;
  padding: 10px 15px;
  border: 1px solid #d9d9d9;
  border-radius: 20px;
  font-size: 14px;
  outline: none;
}
.ai-input input:focus {
  border-color: #52c41a;
}
.ai-input button {
  background: #52c41a;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
}
.ai-input button:disabled {
  background: #d9d9d9;
  cursor: not-allowed;
}

/* 响应式 */
@media (max-width: 1024px) {
  .spots-grid,
  .ticket-grid,
  .weather-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .route-layout {
    grid-template-columns: 1fr;
  }
  .crowd-item {
    grid-template-columns: 1fr 1fr;
  }
}
@media (max-width: 768px) {
  .spots-grid,
  .ticket-grid,
  .weather-grid,
  .tips-grid {
    grid-template-columns: 1fr;
  }
  .crowd-item {
    grid-template-columns: 1fr;
    gap: 10px;
  }
  .ai-dialog {
    width: calc(100% - 40px);
    right: 20px;
    bottom: 80px;
  }
  .detail-body {
    grid-template-columns: 1fr;
  }
  .detail-section:last-child {
    grid-column: span 1;
  }
}
</style>
