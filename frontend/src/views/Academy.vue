<template>
  <MainLayout>
    <div class="academy-page">
      <!-- Hero Banner -->
      <div class="hero-banner">
        <div class="hero-bg"></div>
        <div class="hero-content">
          <span class="hero-tag">📚 河北红色文化传承</span>
          <h1>红色新闻 · 红色读物</h1>
          <p>传承红色基因 · 赓续精神血脉 · 汲取奋进力量</p>
          <div class="hero-tabs">
            <button :class="{ active: activeTab === 'news' }" @click="activeTab = 'news'">
              <span class="tab-icon">📰</span> 红色新闻
            </button>
            <button :class="{ active: activeTab === 'books' }" @click="activeTab = 'books'">
              <span class="tab-icon">📖</span> 红色读物
            </button>
            <button :class="{ active: activeTab === 'recommend' }" @click="activeTab = 'recommend'; loadRecommendations()">
              <span class="tab-icon">🤖</span> AI推荐
            </button>
          </div>
        </div>
      </div>

      <div class="main-content">
        <!-- 红色新闻 Tab -->
        <div v-if="activeTab === 'news'" class="news-section">
          <div class="section-header">
            <h2>📰 河北红色新闻</h2>
            <div class="search-box">
              <el-input v-model="newsKeyword" placeholder="搜索新闻..." prefix-icon="Search" @keyup.enter="searchNews" />
              <el-button type="primary" @click="searchNews">搜索</el-button>
            </div>
          </div>

          <!-- 新闻分类 -->
          <div class="category-tabs">
            <span 
              v-for="cat in newsCategories" 
              :key="cat.key"
              :class="{ active: newsCategory === cat.key }"
              @click="newsCategory = cat.key; loadNews()"
            >{{ cat.label }}</span>
          </div>

          <!-- 置顶新闻 -->
          <div v-if="topNews.length > 0" class="top-news">
            <div v-for="news in topNews" :key="news.id" class="top-news-card" @click="viewNewsDetail(news)">
              <div class="top-badge">📌 置顶</div>
              <h3>{{ news.title }}</h3>
              <p>{{ news.summary }}</p>
              <div class="news-meta">
                <span>{{ news.source }}</span>
                <span>{{ formatTime(news.publishTime) }}</span>
                <span>👁️ {{ formatNumber(news.viewCount) }}</span>
              </div>
            </div>
          </div>

          <!-- 新闻列表 -->
          <div v-if="newsLoading" class="loading-state">
            <el-skeleton :rows="5" animated />
          </div>
          <div v-else class="news-list">
            <div v-for="news in newsList" :key="news.id" class="news-item" @click="viewNewsDetail(news)">
              <div class="news-content">
                <div class="news-badges">
                  <span v-if="news.isHot" class="badge hot">🔥 热门</span>
                  <span class="badge category">{{ news.category }}</span>
                  <span v-if="news.externalUrl" class="badge external">🔗 外链</span>
                </div>
                <h3>{{ news.title }}</h3>
                <p>{{ news.summary }}</p>
                <div class="news-footer">
                  <span class="source">{{ news.source }}</span>
                  <span class="time">{{ formatTime(news.publishTime) }}</span>
                  <span class="views">👁️ {{ formatNumber(news.viewCount) }}</span>
                </div>
              </div>
            </div>
            <el-empty v-if="newsList.length === 0" description="暂无相关新闻" />
          </div>
        </div>

        <!-- 红色读物 Tab -->
        <div v-if="activeTab === 'books'" class="books-section">
          <div class="section-header">
            <h2>📖 河北红色读物</h2>
            <div class="search-box">
              <el-input v-model="bookKeyword" placeholder="搜索书籍..." prefix-icon="Search" @keyup.enter="searchBooks" />
              <el-button type="primary" @click="searchBooks">搜索</el-button>
            </div>
          </div>

          <!-- 书籍分类 -->
          <div class="category-tabs">
            <span 
              v-for="cat in bookCategories" 
              :key="cat.key"
              :class="{ active: bookCategory === cat.key }"
              @click="bookCategory = cat.key; loadBooks()"
            >{{ cat.label }}</span>
          </div>

          <!-- AI 推荐语 -->
          <div v-if="aiRecommendation" class="ai-tip">
            <span class="ai-icon">🤖</span>
            <span>{{ aiRecommendation }}</span>
          </div>

          <!-- 书籍列表 -->
          <div v-if="booksLoading" class="loading-state">
            <el-skeleton :rows="5" animated />
          </div>
          <div v-else class="books-grid">
            <div v-for="book in booksList" :key="book.id" class="book-card" @click="viewBookDetail(book)">
              <div class="book-cover" :style="{ background: getBookGradient(book.category) }">
                <span class="book-icon">📕</span>
                <div v-if="book.isRecommended" class="recommend-badge">⭐ 推荐</div>
                <div v-if="book.externalUrl" class="external-badge">🔗</div>
              </div>
              <div class="book-body">
                <h3>{{ book.title }}</h3>
                <p class="author">{{ book.author }}</p>
                <p class="desc">{{ book.description }}</p>
                <div class="book-tags">
                  <span v-for="tag in parseBookTags(book.tags).slice(0, 3)" :key="tag">{{ tag }}</span>
                </div>
                <div class="book-stats">
                  <span class="rating">⭐ {{ book.rating }}</span>
                  <span class="reads">📖 {{ formatNumber(book.readCount) }}人读过</span>
                </div>
                <div class="book-actions">
                  <el-button v-if="book.hasEbook" type="primary" size="small">在线阅读</el-button>
                  <el-button size="small">收藏</el-button>
                </div>
              </div>
            </div>
            <el-empty v-if="booksList.length === 0" description="暂无相关书籍" />
          </div>
        </div>

        <!-- AI推荐 Tab -->
        <div v-if="activeTab === 'recommend'" class="recommend-section">
          <div class="section-header">
            <h2>🤖 AI 智能推荐</h2>
            <p>根据您的阅读偏好，为您推荐最适合的红色读物</p>
          </div>

          <!-- 偏好设置 -->
          <div class="preference-card">
            <h3>📝 告诉我们您的阅读偏好</h3>
            <div class="pref-form">
              <div class="pref-item">
                <label>感兴趣的主题</label>
                <div class="tag-selector">
                  <span 
                    v-for="tag in interestTags" 
                    :key="tag"
                    :class="{ selected: selectedInterests.includes(tag) }"
                    @click="toggleInterest(tag)"
                  >{{ tag }}</span>
                </div>
              </div>
              <div class="pref-item">
                <label>阅读目的</label>
                <el-select v-model="readingPurpose" placeholder="请选择">
                  <el-option label="学习了解红色历史" value="history" />
                  <el-option label="党史学习教育" value="party" />
                  <el-option label="文学欣赏" value="literature" />
                  <el-option label="青少年教育" value="youth" />
                </el-select>
              </div>
              <el-button type="primary" @click="getAIRecommendations" :loading="recommendLoading">
                🚀 获取 AI 推荐
              </el-button>
            </div>
          </div>

          <!-- 推荐结果 -->
          <div v-if="recommendations.length > 0" class="recommendations">
            <div class="personal-tip" v-if="personalizedTip">
              <span class="tip-icon">💡</span>
              <span>{{ personalizedTip }}</span>
            </div>
            <div class="recommend-list">
              <div v-for="rec in recommendations" :key="rec.bookId" class="recommend-card">
                <div class="match-score">
                  <span class="score">{{ rec.matchScore }}</span>
                  <span class="label">匹配度</span>
                </div>
                <div class="rec-content">
                  <h4>{{ rec.title }}</h4>
                  <p class="author">{{ rec.author }} · {{ rec.category }}</p>
                  <p class="reason">{{ rec.reason }}</p>
                  <div class="highlights">
                    <span v-for="h in rec.highlights" :key="h">✓ {{ h }}</span>
                  </div>
                  <el-button type="primary" size="small">查看详情</el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import MainLayout from '@/layouts/MainLayout.vue'
import { 
  getRedNewsList, searchRedNews, getNewsDetail, likeNews,
  getRedBookList, searchRedBooks, getBookDetail, favoriteBook,
  getAIBookRecommendations,
  type RedNews, type RedBook, type BookRecommendation
} from '@/api/academy'

const activeTab = ref('news')

// 新闻相关
const newsCategory = ref('all')
const newsKeyword = ref('')
const newsLoading = ref(false)
const newsList = ref<RedNews[]>([])
const topNews = ref<RedNews[]>([])

const newsCategories = [
  { key: 'all', label: '全部' },
  { key: '党建动态', label: '党建动态' },
  { key: '红色故事', label: '红色故事' },
  { key: '纪念活动', label: '纪念活动' },
  { key: '教育实践', label: '教育实践' }
]

// 书籍相关
const bookCategory = ref('all')
const bookKeyword = ref('')
const booksLoading = ref(false)
const booksList = ref<RedBook[]>([])
const aiRecommendation = ref('')

const bookCategories = [
  { key: 'all', label: '全部' },
  { key: '红色经典', label: '红色经典' },
  { key: '党史著作', label: '党史著作' },
  { key: '人物传记', label: '人物传记' },
  { key: '纪实文学', label: '纪实文学' },
  { key: '青少年读物', label: '青少年读物' }
]

// AI推荐相关
const selectedInterests = ref<string[]>([])
const readingPurpose = ref('history')
const recommendLoading = ref(false)
const recommendations = ref<BookRecommendation[]>([])
const personalizedTip = ref('')

const interestTags = ['西柏坡', '抗战历史', '革命英雄', '塞罕坝', '白洋淀', '党史', '红色文学', '青少年教育']

// 加载新闻
async function loadNews() {
  newsLoading.value = true
  try {
    const category = newsCategory.value === 'all' ? undefined : newsCategory.value
    const res = await getRedNewsList(category)
    console.log('loadNews response:', res)
    if (res && res.success && res.news) {
      topNews.value = res.news.filter(n => n.isTop)
      newsList.value = res.news.filter(n => !n.isTop)
    } else if (res && Array.isArray(res)) {
      // 如果直接返回数组
      topNews.value = (res as any[]).filter(n => n.isTop)
      newsList.value = (res as any[]).filter(n => !n.isTop)
    }
  } catch (e) {
    console.error('loadNews error:', e)
  } finally {
    newsLoading.value = false
  }
}

async function searchNews() {
  if (!newsKeyword.value.trim()) {
    loadNews()
    return
  }
  newsLoading.value = true
  try {
    const res = await searchRedNews(newsKeyword.value)
    if (res.success) {
      topNews.value = []
      newsList.value = res.news
    }
  } finally {
    newsLoading.value = false
  }
}

async function viewNewsDetail(news: RedNews) {
  // 直接使用百度搜索新闻标题，确保能找到相关内容
  const searchQuery = encodeURIComponent(news.title + ' ' + (news.source || '河北'))
  const url = `https://www.baidu.com/s?wd=${searchQuery}`
  console.log('Opening URL:', url)
  
  // 使用 a 标签方式打开，避免被浏览器拦截
  const link = document.createElement('a')
  link.href = url
  link.target = '_blank'
  link.rel = 'noopener noreferrer'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

// 根据新闻来源生成搜索链接
function getNewsSearchUrl(news: RedNews): string {
  const title = encodeURIComponent(news.title)
  const source = news.source
  
  // 根据来源选择对应的网站
  if (source.includes('河北日报')) {
    return `https://hebei.hebnews.cn/search?keyword=${title}`
  } else if (source.includes('河北新闻网') || source.includes('长城网')) {
    return `https://www.hebnews.cn/search?keyword=${title}`
  } else if (source.includes('燕赵都市报')) {
    return `https://yzdsb.hebnews.cn/search?keyword=${title}`
  } else {
    // 默认使用百度搜索
    return `https://www.baidu.com/s?wd=${title}+${encodeURIComponent(source)}`
  }
}

// 加载书籍
async function loadBooks() {
  booksLoading.value = true
  try {
    const category = bookCategory.value === 'all' ? undefined : bookCategory.value
    const res = await getRedBookList(category)
    console.log('loadBooks response:', res)
    if (res && res.success && res.books) {
      booksList.value = res.books
      aiRecommendation.value = res.aiRecommendation || ''
    } else if (res && Array.isArray(res)) {
      booksList.value = res as any[]
    }
  } finally {
    booksLoading.value = false
  }
}

async function searchBooks() {
  if (!bookKeyword.value.trim()) {
    loadBooks()
    return
  }
  booksLoading.value = true
  try {
    const res = await searchRedBooks(bookKeyword.value)
    if (res.success) {
      booksList.value = res.books
    }
  } finally {
    booksLoading.value = false
  }
}

function viewBookDetail(book: RedBook) {
  // 直接使用京东搜索书籍，确保能找到购买链接
  const searchQuery = encodeURIComponent(book.title + ' ' + (book.author || ''))
  const url = `https://search.jd.com/Search?keyword=${searchQuery}`
  console.log('Opening Book URL:', url)
  
  // 使用 a 标签方式打开，避免被浏览器拦截
  const link = document.createElement('a')
  link.href = url
  link.target = '_blank'
  link.rel = 'noopener noreferrer'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

// AI推荐
function toggleInterest(tag: string) {
  const idx = selectedInterests.value.indexOf(tag)
  if (idx > -1) {
    selectedInterests.value.splice(idx, 1)
  } else {
    selectedInterests.value.push(tag)
  }
}

async function loadRecommendations() {
  if (recommendations.value.length === 0) {
    await getAIRecommendations()
  }
}

async function getAIRecommendations() {
  recommendLoading.value = true
  try {
    const res = await getAIBookRecommendations({
      interests: selectedInterests.value,
      purpose: readingPurpose.value
    })
    if (res.success) {
      recommendations.value = res.recommendations
      personalizedTip.value = res.personalizedTip
      ElMessage.success('AI 推荐已生成')
    }
  } finally {
    recommendLoading.value = false
  }
}

// 工具函数
function formatTime(time: string) {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
  return date.toLocaleDateString()
}

function formatNumber(num: number) {
  if (num >= 10000) return (num / 10000).toFixed(1) + '万'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num.toString()
}

function getBookGradient(category: string) {
  const gradients: Record<string, string> = {
    '红色经典': 'linear-gradient(135deg, #c41e3a, #8b1e3f)',
    '党史著作': 'linear-gradient(135deg, #8b1e3f, #d4956c)',
    '人物传记': 'linear-gradient(135deg, #d4956c, #c41e3a)',
    '纪实文学': 'linear-gradient(135deg, #c41e3a, #d4956c)',
    '青少年读物': 'linear-gradient(135deg, #2e7d32, #66bb6a)'
  }
  return gradients[category] || 'linear-gradient(135deg, #c41e3a, #8b1e3f)'
}

function parseBookTags(tags: string | string[]): string[] {
  if (Array.isArray(tags)) return tags
  if (typeof tags === 'string' && tags) return tags.split(',')
  return []
}

onMounted(() => {
  loadNews()
  loadBooks()
})
</script>

<style scoped>
.academy-page {
  min-height: 100vh;
  background: #f5f7fa;
}

/* Hero Banner */
.hero-banner {
  background: radial-gradient(circle at 70% 30%, #a0182f, #2b0c0e);
  color: white;
  padding: 6rem 2rem 4rem;
  text-align: center;
  position: relative;
}

.hero-tag {
  display: inline-block;
  padding: 8px 20px;
  background: rgba(255,255,255,0.15);
  border-radius: 30px;
  font-size: 1rem;
  margin-bottom: 1.5rem;
}

.hero-banner h1 {
  font-size: 3rem;
  font-weight: 800;
  margin-bottom: 1rem;
  background: linear-gradient(to right, #fff, #ffd700);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.hero-banner p {
  font-size: 1.2rem;
  opacity: 0.9;
  margin-bottom: 2rem;
}

.hero-tabs {
  display: flex;
  justify-content: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.hero-tabs button {
  padding: 12px 28px;
  background: rgba(255,255,255,0.1);
  border: 1px solid rgba(255,255,255,0.3);
  border-radius: 30px;
  color: white;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.hero-tabs button:hover {
  background: rgba(255,255,255,0.2);
}

.hero-tabs button.active {
  background: white;
  color: #a0182f;
  font-weight: 600;
}

.tab-icon {
  font-size: 1.2rem;
}

/* Main Content */
.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.section-header h2 {
  font-size: 1.5rem;
  color: #2c3e50;
}

.search-box {
  display: flex;
  gap: 0.5rem;
}

.search-box .el-input {
  width: 250px;
}

/* Category Tabs */
.category-tabs {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
}

.category-tabs span {
  padding: 8px 20px;
  background: #fff;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #eee;
  font-size: 0.95rem;
}

.category-tabs span:hover {
  background: #fff5f5;
  border-color: #c41e3a;
}

.category-tabs span.active {
  background: linear-gradient(135deg, #c41e3a, #a0182f);
  color: white;
  border-color: transparent;
}

/* News Section */
.top-news {
  margin-bottom: 2rem;
}

.top-news-card {
  background: linear-gradient(135deg, #fff5f5, #fff);
  border: 1px solid rgba(196, 30, 58, 0.2);
  border-radius: 12px;
  padding: 1.5rem;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.top-news-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(196, 30, 58, 0.15);
}

.top-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: #c41e3a;
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.8rem;
}

.top-news-card h3 {
  font-size: 1.3rem;
  margin-bottom: 0.8rem;
  color: #2c3e50;
}

.top-news-card p {
  color: #666;
  margin-bottom: 1rem;
  line-height: 1.6;
}

.news-meta {
  display: flex;
  gap: 1.5rem;
  font-size: 0.85rem;
  color: #999;
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.news-item {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #f0f0f0;
}

.news-item:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
  border-color: rgba(196, 30, 58, 0.2);
}

.news-badges {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 0.8rem;
}

.badge {
  padding: 2px 10px;
  border-radius: 10px;
  font-size: 0.75rem;
}

.badge.hot {
  background: #fff5f5;
  color: #c41e3a;
}

.badge.category {
  background: #f5f5f5;
  color: #666;
}

.badge.external {
  background: #e3f2fd;
  color: #1976d2;
}

.external-badge {
  position: absolute;
  bottom: 12px;
  right: 12px;
  background: rgba(255,255,255,0.9);
  padding: 4px 8px;
  border-radius: 8px;
  font-size: 0.9rem;
}

.news-item h3 {
  font-size: 1.1rem;
  margin-bottom: 0.5rem;
  color: #2c3e50;
}

.news-item p {
  color: #666;
  font-size: 0.95rem;
  margin-bottom: 0.8rem;
  line-height: 1.5;
}

.news-footer {
  display: flex;
  gap: 1.5rem;
  font-size: 0.85rem;
  color: #999;
}

/* Books Section */
.ai-tip {
  background: linear-gradient(135deg, #fff8e1, #fff);
  border: 1px solid #ffd54f;
  border-radius: 12px;
  padding: 1rem 1.5rem;
  margin-bottom: 1.5rem;
  display: flex;
  align-items: center;
  gap: 0.8rem;
}

.ai-icon {
  font-size: 1.5rem;
}

.books-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
}

.book-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #f0f0f0;
}

.book-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(196, 30, 58, 0.12);
}

.book-cover {
  height: 160px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.book-icon {
  font-size: 4rem;
  filter: drop-shadow(0 4px 8px rgba(0,0,0,0.2));
}

.recommend-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(255,255,255,0.9);
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.8rem;
  color: #c41e3a;
}

.book-body {
  padding: 1.2rem;
}

.book-body h3 {
  font-size: 1.1rem;
  margin-bottom: 0.5rem;
  color: #2c3e50;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.book-body .author {
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 0.5rem;
}

.book-body .desc {
  font-size: 0.85rem;
  color: #888;
  line-height: 1.5;
  margin-bottom: 0.8rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.book-tags {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 0.8rem;
  flex-wrap: wrap;
}

.book-tags span {
  padding: 2px 8px;
  background: #f5f5f5;
  border-radius: 8px;
  font-size: 0.75rem;
  color: #666;
}

.book-stats {
  display: flex;
  justify-content: space-between;
  font-size: 0.85rem;
  color: #999;
  margin-bottom: 1rem;
}

.book-stats .rating {
  color: #f5a623;
}

.book-actions {
  display: flex;
  gap: 0.5rem;
}

/* Recommend Section */
.preference-card {
  background: white;
  border-radius: 16px;
  padding: 2rem;
  margin-bottom: 2rem;
  box-shadow: 0 4px 16px rgba(0,0,0,0.05);
}

.preference-card h3 {
  margin-bottom: 1.5rem;
  color: #2c3e50;
}

.pref-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.pref-item label {
  display: block;
  margin-bottom: 0.8rem;
  font-weight: 500;
  color: #333;
}

.tag-selector {
  display: flex;
  gap: 0.8rem;
  flex-wrap: wrap;
}

.tag-selector span {
  padding: 8px 16px;
  background: #f5f5f5;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.tag-selector span:hover {
  background: #fff5f5;
}

.tag-selector span.selected {
  background: linear-gradient(135deg, #c41e3a, #a0182f);
  color: white;
}

.recommendations {
  margin-top: 2rem;
}

.personal-tip {
  background: linear-gradient(135deg, #e8f5e9, #fff);
  border: 1px solid #81c784;
  border-radius: 12px;
  padding: 1rem 1.5rem;
  margin-bottom: 1.5rem;
  display: flex;
  align-items: center;
  gap: 0.8rem;
}

.tip-icon {
  font-size: 1.5rem;
}

.recommend-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.recommend-card {
  background: white;
  border-radius: 16px;
  padding: 1.5rem;
  display: flex;
  gap: 1.5rem;
  border: 1px solid #f0f0f0;
  transition: all 0.3s;
}

.recommend-card:hover {
  box-shadow: 0 8px 24px rgba(0,0,0,0.08);
}

.match-score {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #c41e3a, #a0182f);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.match-score .score {
  font-size: 1.8rem;
  font-weight: 700;
}

.match-score .label {
  font-size: 0.7rem;
  opacity: 0.9;
}

.rec-content {
  flex: 1;
}

.rec-content h4 {
  font-size: 1.2rem;
  margin-bottom: 0.3rem;
  color: #2c3e50;
}

.rec-content .author {
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 0.5rem;
}

.rec-content .reason {
  font-size: 0.95rem;
  color: #555;
  margin-bottom: 0.8rem;
  line-height: 1.5;
}

.highlights {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
  flex-wrap: wrap;
}

.highlights span {
  font-size: 0.85rem;
  color: #2e7d32;
}

/* Dialogs */
.news-detail .detail-meta {
  display: flex;
  gap: 1.5rem;
  font-size: 0.9rem;
  color: #999;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #f0f0f0;
}

.news-detail .detail-content p {
  line-height: 1.8;
  color: #333;
  margin-bottom: 1rem;
}

.news-detail .news-body {
  line-height: 2;
  color: #333;
  font-size: 1rem;
  text-align: justify;
  margin-bottom: 1.5rem;
}

.news-detail .news-actions {
  display: flex;
  gap: 1rem;
  padding-top: 1.5rem;
  border-top: 1px solid #f0f0f0;
}

.news-detail .loading-content {
  padding: 1rem 0;
}

.book-detail .detail-header {
  display: flex;
  gap: 2rem;
  margin-bottom: 2rem;
}

.book-detail .detail-cover {
  width: 180px;
  height: 240px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.book-detail .detail-cover .book-icon {
  font-size: 5rem;
}

.book-detail .detail-info h2 {
  font-size: 1.5rem;
  margin-bottom: 0.8rem;
}

.book-detail .detail-info p {
  margin-bottom: 0.5rem;
  color: #666;
}

.book-detail .stats {
  display: flex;
  gap: 1.5rem;
  margin: 1rem 0;
  font-size: 0.95rem;
}

.book-detail .tags {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.book-detail .tags span {
  padding: 4px 12px;
  background: #f5f5f5;
  border-radius: 12px;
  font-size: 0.85rem;
}

.book-detail .detail-desc {
  margin-bottom: 2rem;
}

.book-detail .detail-desc h4 {
  margin-bottom: 0.8rem;
  color: #2c3e50;
}

.book-detail .detail-desc p {
  line-height: 1.8;
  color: #555;
}

.book-detail .detail-actions {
  display: flex;
  gap: 1rem;
}

.loading-state {
  padding: 2rem;
}

/* Responsive */
@media (max-width: 768px) {
  .hero-banner h1 {
    font-size: 2rem;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .search-box {
    width: 100%;
  }
  
  .search-box .el-input {
    flex: 1;
  }
  
  .books-grid {
    grid-template-columns: 1fr;
  }
  
  .recommend-card {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .book-detail .detail-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
}
</style>
