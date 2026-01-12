<template>
  <MainLayout>
    <div class="mall-page">
      <!-- 商城 Hero / 轮播图 -->
      <div class="mall-hero" v-motion-fade-visible>
        <el-carousel height="400px" indicator-position="outside">
          <el-carousel-item v-for="item in featuredItems" :key="item.id">
            <div class="carousel-content" :style="{ backgroundImage: `url(${item.image})` }">
              <div class="carousel-overlay">
                <div class="carousel-info" v-motion-slide-visible-up :delay="300">
                  <span class="tag">本季精选</span>
                  <h2>{{ item.title }}</h2>
                  <p>{{ item.desc }}</p>
                  <el-button type="primary" size="large" class="btn-hero">立即查看</el-button>
                </div>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>

      <div class="mall-container">
        <!-- 侧边栏过滤器 -->
        <aside class="mall-sidebar">
          <div class="sidebar-section" v-motion-slide-visible-left :delay="200">
            <h3 class="sidebar-title">
              <el-icon><Menu /></el-icon> 商品分类
            </h3>
            <ul class="category-list">
              <li 
                v-for="cat in categories" 
                :key="cat"
                :class="{ active: category === cat }"
                @click="selectCategory(cat)"
              >
                {{ cat }}
                <span class="count" v-if="categoryCounts[cat]">{{ categoryCounts[cat] }}</span>
              </li>
            </ul>
          </div>

          <div class="sidebar-section" v-motion-slide-visible-left :delay="300">
            <h3 class="sidebar-title">
              <el-icon><Money /></el-icon> 价格区间
            </h3>
            <div class="price-range">
              <el-slider v-model="priceRange" range :max="500" />
              <div class="price-inputs">
                <span>¥{{ priceRange[0] }}</span>
                <span>-</span>
                <span>¥{{ priceRange[1] }}</span>
              </div>
            </div>
          </div>
          
          <div class="promotion-banner" v-motion-slide-visible-left :delay="400">
            <div class="promo-badge">限时特惠</div>
            <h4>红色文创节</h4>
            <p class="promo-desc">全场满200减30</p>
            <p class="promo-sub">截止日期：12月31日</p>
            <el-button class="promo-btn">查看详情 ></el-button>
          </div>
        </aside>

        <!-- 主内容区 -->
        <main class="mall-main">
          <!-- 顶部工具栏 -->
          <div class="toolbar">
            <div class="toolbar-left">
              <h2 class="current-category">{{ category === 'all' ? '全部商品' : category }}</h2>
              <span class="result-count">共 {{ total }} 件商品</span>
            </div>
            
            <div class="toolbar-right">
              <div class="search-box">
                <el-input
                  v-model="keyword"
                  placeholder="搜索文创产品..."
                  prefix-icon="Search"
                  clearable
                  @input="handleFilter"
                />
              </div>
              
              <el-select v-model="sort" placeholder="排序" style="width: 140px" @change="handleFilter">
                <el-option label="综合推荐" value="default" />
                <el-option label="销量优先" value="sales_desc" />
                <el-option label="价格: 低到高" value="price_asc" />
                <el-option label="价格: 高到低" value="price_desc" />
              </el-select>
            </div>
          </div>
          
          <!-- 快速筛选标签 -->
          <div class="quick-filters" v-motion-fade-visible>
            <span class="filter-label">快速筛选：</span>
            <el-tag 
              v-for="tag in quickFilters" 
              :key="tag.value"
              :type="activeQuickFilter === tag.value ? 'danger' : 'info'"
              effect="plain"
              class="filter-tag"
              @click="applyQuickFilter(tag.value)"
            >
              {{ tag.label }}
            </el-tag>
          </div>

          <!-- 商品列表 -->
          <div v-if="loading" class="products-loading">
            <el-skeleton v-for="i in 9" :key="i" animated class="product-skeleton">
              <template #template>
                <el-skeleton-item variant="image" style="width: 100%; height: 200px" />
                <div style="padding: 14px">
                  <el-skeleton-item variant="h3" style="width: 50%" />
                  <el-skeleton-item variant="text" style="margin-top: 10px" />
                  <el-skeleton-item variant="text" style="width: 30%; margin-top: 10px" />
                </div>
              </template>
            </el-skeleton>
          </div>

          <div v-else class="product-grid">
            <el-empty v-if="pagedProducts.length === 0" description="暂无符合条件的商品" />
            
            <div
              v-for="(p, index) in pagedProducts"
              :key="p.id"
              class="product-card"
              @click="openDetail(p)"
              v-motion-slide-visible-up
              :delay="index * 50"
            >
              <div class="product-image">
                <div class="placeholder-img" :class="`img-${p.id % 4}`">
                  <div class="product-icon-large">{{ p.icon || '🎁' }}</div>
                </div>
                <div v-if="p.stock < 10 && p.stock > 0" class="stock-badge low">仅剩 {{ p.stock }} 件</div>
                <div v-if="p.stock === 0" class="stock-badge out">已售罄</div>
                <div v-if="p.sales > 1000" class="hot-badge">🔥 热销</div>
              </div>
              
              <div class="product-info">
                <div class="product-category">{{ p.category }}</div>
                <h3 class="product-title">{{ p.name }}</h3>
                <div class="product-bottom">
                  <div class="product-price">
                    <span class="currency">¥</span>
                    <span class="amount">{{ p.price }}</span>
                  </div>
                  <span class="product-sales">已售 {{ p.sales }}</span>
                </div>
                <div class="product-stock" :class="{ 'low': p.stock < 10, 'out': p.stock === 0 }">
                  库存: {{ p.stock > 0 ? p.stock + ' 件' : '已售罄' }}
                </div>
              </div>
            </div>
          </div>
          
          <!-- 分页 -->
          <div class="pagination-container" v-if="pagedProducts.length > 0">
            <el-pagination
              background
              layout="total, prev, pager, next, jumper"
              :total="total" 
              :page-size="pageSize"
              :current-page="currentPage"
              @current-change="handlePageChange"
            />
          </div>
        </main>
      </div>
      
      <!-- 商品详情弹窗 -->
      <el-dialog 
        v-model="detailVisible" 
        width="1400px" 
        custom-class="product-dialog" 
        align-center
        :close-on-click-modal="false"
      >
        <template #header>
          <div class="dialog-header">
            <h3>商品详情</h3>
          </div>
        </template>
        
        <div class="product-detail" v-if="currentProduct">
          <div class="detail-main-three-columns">
            <!-- 第1列：商品信息 -->
            <div class="detail-info">
              <h2 class="detail-title">{{ currentProduct.name }}</h2>
              
              <!-- 价格和销量 -->
              <div class="detail-price-box">
                <div class="price-row">
                  <span class="price-label">价格</span>
                  <span class="detail-price">¥{{ currentProduct.price }}</span>
                  <span class="original-price" v-if="currentProduct.price < 100">¥{{ (currentProduct.price * 1.5).toFixed(2) }}</span>
                </div>
                <div class="meta-row">
                  <span class="meta-item">
                    <el-icon><Star /></el-icon>
                    评分 4.9
                  </span>
                  <span class="meta-item">
                    销量 {{ currentProduct.sales }}+
                  </span>
                  <span class="meta-item">
                    库存 {{ currentProduct.stock }}
                  </span>
                </div>
              </div>
              
              <!-- 商品描述 -->
              <div class="detail-desc-box">
                <p class="detail-desc">
                  {{ currentProduct.description || '这款红色文创产品结合了传统工艺与现代设计，寓意深刻，制作精良。是馈赠亲友、收藏留念的绝佳选择。' }}
                </p>
              </div>
              
              <!-- 商品特点 -->
              <div class="detail-features">
                <div class="feature-item">
                  <el-icon color="#c41e3a"><Check /></el-icon>
                  <span>正品保证</span>
                </div>
                <div class="feature-item">
                  <el-icon color="#c41e3a"><Check /></el-icon>
                  <span>7天无理由退换</span>
                </div>
                <div class="feature-item">
                  <el-icon color="#c41e3a"><Check /></el-icon>
                  <span>全国包邮</span>
                </div>
                <div class="feature-item">
                  <el-icon color="#c41e3a"><Check /></el-icon>
                  <span>精美包装</span>
                </div>
              </div>
              
              <!-- 规格选择 -->
              <div class="detail-options">
                <div class="option-row">
                  <label>规格</label>
                  <div class="options">
                    <span 
                      v-for="(spec, index) in productSpecs" 
                      :key="index"
                      class="option-tag" 
                      :class="{ active: selectedSpec === index }"
                      @click="selectedSpec = index"
                    >
                      {{ spec.name }}
                      <span v-if="spec.extraPrice > 0" class="extra-price">+¥{{ spec.extraPrice }}</span>
                    </span>
                  </div>
                </div>
                
                <div class="option-row">
                  <label>数量</label>
                  <el-input-number 
                    v-model="buyCount" 
                    :min="1" 
                    :max="currentProduct.stock" 
                    size="large"
                  />
                  <span class="stock-tip">库存 {{ currentProduct.stock }} 件</span>
                </div>
              </div>
              
              <!-- 操作按钮 -->
              <div class="detail-actions">
                <el-button 
                  type="primary" 
                  size="large" 
                  :icon="ShoppingCart" 
                  @click="addToCart(currentProduct)"
                  class="btn-add-cart"
                >
                  加入购物车
                </el-button>
                <el-button 
                  size="large" 
                  type="danger" 
                  @click="buyNow(currentProduct)"
                  class="btn-buy-now"
                >
                  立即购买
                </el-button>
              </div>
            </div>
            
            <!-- 第2列：商品图片 -->
            <div class="detail-gallery-column">
              <div class="detail-gallery">
                <!-- 如果有真实图片 -->
                <template v-if="productImages.length > 0">
                  <div class="main-image real-image">
                    <img :src="getImageUrl(productImages[selectedImageIndex])" :alt="currentProduct.name" />
                    <div class="image-badge" v-if="currentProduct.stock < 10">
                      <span>仅剩{{ currentProduct.stock }}件</span>
                    </div>
                  </div>
                  <div class="thumbs">
                    <div 
                      v-for="(img, index) in productImages.slice(0, 5)" 
                      :key="index"
                      class="thumb real-thumb" 
                      :class="{ active: selectedImageIndex === index }"
                      @click="selectedImageIndex = index"
                    >
                      <img :src="getImageUrl(img)" :alt="`图片${index + 1}`" />
                    </div>
                  </div>
                </template>
                <!-- 没有真实图片时显示占位图 -->
                <template v-else>
                  <div class="main-image" :class="`img-${currentProduct.id % 4}`">
                    <div class="detail-icon-large">{{ currentProduct.icon || '🎁' }}</div>
                    <div class="image-badge" v-if="currentProduct.stock < 10">
                      <span>仅剩{{ currentProduct.stock }}件</span>
                    </div>
                  </div>
                  <div class="thumbs">
                    <div 
                      v-for="i in 4" 
                      :key="i"
                      class="thumb" 
                      :class="{ active: i === 1, [`img-${(currentProduct.id + i - 1) % 4}`]: true }"
                    >
                      <div class="thumb-icon">{{ currentProduct.icon || '🎁' }}</div>
                    </div>
                  </div>
                </template>
              </div>
            </div>
            
            <!-- 第3列：标签导航 + 标签内容 -->
            <div class="detail-tabs-column">
              <!-- 标签导航 -->
              <div class="tabs-nav-vertical">
                <div 
                  v-for="tab in detailTabs" 
                  :key="tab.key"
                  class="tab-item-vertical"
                  :class="{ active: activeTab === tab.key }"
                  @click="activeTab = tab.key"
                >
                  <span class="tab-icon">{{ tab.icon }}</span>
                  <span class="tab-label">{{ tab.label }}</span>
                </div>
              </div>
              
              <!-- 标签页内容 -->
              <div class="tabs-content-vertical">
                <!-- 产品特色 -->
                <div v-show="activeTab === 'features'" class="tab-pane">
                <h3 class="pane-title">产品特色</h3>
                <ul class="feature-list">
                  <li>精选优质材料，工艺精湛</li>
                  <li>融入河北红色文化元素</li>
                  <li>设计独特，富有纪念意义</li>
                  <li>适合收藏或馈赠亲友</li>
                  <li>每件产品都经过严格质检</li>
                </ul>
              </div>
              
              <!-- 文化背景 -->
              <div v-show="activeTab === 'culture'" class="tab-pane">
                <h3 class="pane-title">文化背景</h3>
                <p class="pane-text">{{ currentProduct.description || '这款产品深度融合了河北红色文化元素，传承革命精神，展现燕赵风采。每一个细节都蕴含着深厚的历史底蕴和文化内涵。' }}</p>
                <p class="pane-text">河北作为革命老区，拥有丰富的红色文化资源。从西柏坡的"赶考"精神，到狼牙山五壮士的英勇事迹，再到李大钊的革命理想，这些都是我们宝贵的精神财富。</p>
                <p class="pane-text">本产品的设计灵感来源于这些红色文化元素，通过现代设计手法，将历史与当代完美融合，让红色文化以更加生动的形式走进人们的生活。</p>
              </div>
              
              <!-- 产品参数 -->
              <div v-show="activeTab === 'params'" class="tab-pane">
                <h3 class="pane-title">产品参数</h3>
                <table class="param-table">
                  <tbody>
                    <tr>
                      <td class="param-label">商品名称</td>
                      <td>{{ currentProduct.name }}</td>
                    </tr>
                    <tr>
                      <td class="param-label">商品分类</td>
                      <td>{{ currentProduct.category || '文创商品' }}</td>
                    </tr>
                    <tr>
                      <td class="param-label">材质</td>
                      <td>优质材料</td>
                    </tr>
                    <tr>
                      <td class="param-label">产地</td>
                      <td>河北</td>
                    </tr>
                    <tr>
                      <td class="param-label">保质期</td>
                      <td>长期</td>
                    </tr>
                    <tr>
                      <td class="param-label">重量</td>
                      <td>约 {{ Math.floor(Math.random() * 500 + 100) }}g</td>
                    </tr>
                    <tr>
                      <td class="param-label">尺寸</td>
                      <td>{{ ['10×10×5cm', '15×15×8cm', '20×15×10cm'][currentProduct.id % 3] }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
              
              <!-- 用户评价 -->
              <div v-show="activeTab === 'reviews'" class="tab-pane">
                <h3 class="pane-title">用户评价 ({{ reviewTotal }}条)</h3>
                <div class="review-summary-simple">
                  <div class="rating-display">
                    <span class="rating-score-large">{{ avgRating.toFixed(1) }}</span>
                    <div>
                      <div class="rating-stars-large">{{ '⭐'.repeat(Math.round(avgRating)) }}</div>
                      <div class="rating-text">基于 {{ reviewTotal }} 条评价</div>
                    </div>
                  </div>
                </div>
                
                <!-- 评论提交表单 -->
                <div class="review-form" v-if="canSubmitReview">
                  <h4>发表评价</h4>
                  <div class="rating-input">
                    <span>评分：</span>
                    <el-rate v-model="newReview.rating" />
                  </div>
                  <el-input
                    v-model="newReview.content"
                    type="textarea"
                    :rows="3"
                    placeholder="分享您的购物体验..."
                  />
                  <el-button type="primary" @click="submitReview" :loading="submittingReview">
                    提交评价
                  </el-button>
                </div>
                <div class="review-tip" v-else-if="userStore.token">
                  <el-alert type="info" :closable="false">
                    购买并确认收货后可以评价此商品
                  </el-alert>
                </div>
                <div class="review-tip" v-else>
                  <el-alert type="warning" :closable="false">
                    请先登录后查看是否可以评价
                  </el-alert>
                </div>
                
                <!-- 评论列表 -->
                <div class="review-list-simple" v-loading="reviewLoading">
                  <el-empty v-if="reviews.length === 0" description="暂无评价" />
                  <div class="review-card" v-for="review in reviews" :key="review.id">
                    <div class="review-header-simple">
                      <el-avatar size="small">{{ review.username?.charAt(0) || '用' }}</el-avatar>
                      <span class="reviewer-name-simple">{{ review.username || '匿名用户' }}</span>
                      <span class="review-rating-simple">{{ '⭐'.repeat(review.rating) }}</span>
                      <span class="review-date">{{ formatDate(review.createTime) }}</span>
                    </div>
                    <p class="review-text">{{ review.content }}</p>
                  </div>
                </div>
                
                <!-- 评论翻页 -->
                <div class="review-pagination" v-if="reviewPages > 1">
                  <el-pagination
                    small
                    background
                    layout="prev, pager, next"
                    :total="reviewTotal"
                    :page-size="reviewPageSize"
                    :current-page="reviewPage"
                    @current-change="handleReviewPageChange"
                  />
                </div>
              </div>
              
              <!-- 售后保障 -->
              <div v-show="activeTab === 'service'" class="tab-pane">
                <h3 class="pane-title">售后保障</h3>
                <div class="service-grid">
                  <div class="service-card">
                    <div class="service-icon-large">🛡️</div>
                    <h4>正品保证</h4>
                    <p>所有商品均为正品，支持专柜验货</p>
                  </div>
                  <div class="service-card">
                    <div class="service-icon-large">🔄</div>
                    <h4>7天无理由退换</h4>
                    <p>签收后7天内，支持无理由退换货</p>
                  </div>
                  <div class="service-card">
                    <div class="service-icon-large">🚚</div>
                    <h4>全国包邮</h4>
                    <p>全国范围内免运费配送</p>
                  </div>
                  <div class="service-card">
                    <div class="service-icon-large">📦</div>
                    <h4>精美包装</h4>
                    <p>每件商品都配有精美礼盒包装</p>
                  </div>
                </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-dialog>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search, ShoppingCart, Star, Menu, Money, Check } from '@element-plus/icons-vue'
import MainLayout from '@/layouts/MainLayout.vue'
import { ElMessage } from 'element-plus'
import { mallApi } from '@/api/mall'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 状态
const loading = ref(true)
const keyword = ref('')
const category = ref('all')
const sort = ref('default')
const priceRange = ref([0, 500])
const detailVisible = ref(false)
const currentProduct = ref<any>(null)
const buyCount = ref(1)
const activeTab = ref('features')
const selectedImageIndex = ref(0)

// 计算商品图片列表
const productImages = computed(() => {
  if (!currentProduct.value || !currentProduct.value.images) return []
  try {
    const images = typeof currentProduct.value.images === 'string' 
      ? JSON.parse(currentProduct.value.images) 
      : currentProduct.value.images
    return Array.isArray(images) ? images : []
  } catch (e) {
    return []
  }
})

// 获取图片完整URL
const getImageUrl = (url: string) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  // 处理相对路径
  return `http://localhost:8087${url.startsWith('/') ? '' : '/'}${url}`
}

// 评论相关状态
const reviews = ref<any[]>([])
const reviewTotal = ref(0)
const reviewPage = ref(1)
const reviewPageSize = ref(5)
const reviewPages = ref(0)
const avgRating = ref(5.0)
const reviewLoading = ref(false)
const canSubmitReview = ref(false)
const submittingReview = ref(false)
const newReview = ref({
  rating: 5,
  content: ''
})

// 详情标签页配置
const detailTabs = [
  { key: 'features', label: '产品特色', icon: '✨' },
  { key: 'culture', label: '文化背景', icon: '📖' },
  { key: 'params', label: '产品参数', icon: '📋' },
  { key: 'reviews', label: '用户评价', icon: '💬' },
  { key: 'service', label: '售后保障', icon: '🛡️' }
]
const activeQuickFilter = ref('')
const selectedSpec = ref(0)

// 商品规格选项
const productSpecs = [
  { name: '标准版', extraPrice: 0 },
  { name: '礼盒装', extraPrice: 20 },
  { name: '豪华版', extraPrice: 50 }
]

const quickFilters = [
  { label: '🔥 热销商品', value: 'hot' },
  { label: '🆕 新品上架', value: 'new' },
  { label: '💰 特价优惠', value: 'discount' },
  { label: '⭐ 高评分', value: 'rating' }
]

const applyQuickFilter = (value: string) => {
  if (activeQuickFilter.value === value) {
    activeQuickFilter.value = ''
    sort.value = 'default'
  } else {
    activeQuickFilter.value = value
    switch (value) {
      case 'hot':
        sort.value = 'sales_desc'
        break
      case 'new':
        // TODO: 按创建时间排序
        break
      case 'discount':
        sort.value = 'price_asc'
        break
      case 'rating':
        // TODO: 按评分排序
        break
    }
  }
  handleFilter()
}

// 商品数据
const products = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)

// 模拟数据
const categories = ['全部商品', '创意生活', '文化周边', '设计师推荐']
const categoryCounts: Record<string, number> = {
  '创意生活': 0,
  '文化周边': 0,
  '设计师推荐': 0
}

const featuredItems = [
  { id: 101, title: '西柏坡纪念册', desc: '记录光辉岁月，传承“赶考”精神', image: 'https://images.unsplash.com/photo-1544376798-89aa6b82c6cd?q=80&w=2574&auto=format&fit=crop' },
  { id: 102, title: '雄安新区建设纪念章', desc: '见证千年大计，收藏未来之城', image: 'https://images.unsplash.com/photo-1555589942-156327b07049?q=80&w=2670&auto=format&fit=crop' },
  { id: 103, title: '蔚县剪纸·红色系列', desc: '国家级非遗技艺，展现燕赵红色风采', image: 'https://images.unsplash.com/photo-1605648916361-9bc12ad6a569?q=80&w=2670&auto=format&fit=crop' },
]

// 生成模拟商品
const generateProducts = () => {
  const list = []
  const types = ['红色书籍', '纪念徽章', '非遗文创', '文具用品', '家居装饰']
  const prefixes = ['西柏坡', '雄安', '白洋淀', '狼牙山', '李大钊', '冉庄地道', '塞罕坝', '正定', '燕赵', '唐山', '董存瑞', '涉县', '阜平', '乐亭', '抗大二分校']
  const suffixes = ['纪念章', '书签', '笔记本', '摆件', '剪纸', '画册', '文化衫', '帆布袋', '邮册', '泥塑', '皮影']
  
  for (let i = 1; i <= 24; i++) {
    const prefix = prefixes[Math.floor(Math.random() * prefixes.length)]
    const suffix = suffixes[Math.floor(Math.random() * suffixes.length)]
    list.push({
      id: i,
      name: `${prefix}红色主题${suffix}`,
      category: types[i % 5],
      price: Math.floor(Math.random() * 200) + 20,
      sales: Math.floor(Math.random() * 1000),
      stock: Math.floor(Math.random() * 50),
      isNew: Math.random() > 0.8,
      rating: (4 + Math.random()).toFixed(1)
    })
  }
  return list
}

// 加载商品列表
const loadProducts = async () => {
  try {
    loading.value = true
    const response = await mallApi.getProductList({
      page: currentPage.value,
      size: pageSize.value,
      keyword: keyword.value || undefined,
      category: category.value === 'all' || category.value === '全部商品' ? undefined : category.value,
      sort: sort.value === 'default' ? undefined : sort.value,
      onlyStock: undefined,
      minPrice: priceRange.value[0] > 0 ? priceRange.value[0] : undefined,
      maxPrice: priceRange.value[1] < 500 ? priceRange.value[1] : undefined
    })
    
    // request.ts 拦截器已经提取了 data，所以 response 直接就是 Page 对象
    products.value = response.records || []
    total.value = response.total || 0
    
    // 更新分类计数
    if (response.records) {
      const counts: Record<string, number> = {}
      response.records.forEach((p: any) => {
        counts[p.category] = (counts[p.category] || 0) + 1
      })
      Object.assign(categoryCounts, counts)
    }
  } catch (error) {
    console.error('加载商品列表失败:', error)
    ElMessage.error('加载商品列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadProducts()
  
  // 检查是否从订单页面跳转过来要评价
  const productId = route.query.productId
  const action = route.query.action
  
  if (productId && action === 'review') {
    // 找到对应商品并打开详情弹窗
    const product = products.value.find(p => p.id === Number(productId))
    if (product) {
      openDetail(product)
      // 切换到评价标签
      activeTab.value = 'reviews'
      ElMessage.success('请在下方评价区域提交您的评价')
    } else {
      // 如果商品不在当前列表中，尝试从API获取
      try {
        const productData = await mallApi.getProductById(Number(productId))
        if (productData) {
          openDetail(productData)
          activeTab.value = 'reviews'
          ElMessage.success('请在下方评价区域提交您的评价')
        }
      } catch (error) {
        console.error('获取商品详情失败:', error)
        ElMessage.warning('未找到对应商品，请手动选择商品进行评价')
      }
    }
    
    // 清除URL参数
    router.replace({ path: '/mall' })
  }
})

// 监听价格区间变化
watch(priceRange, () => {
  handleFilter()
}, { deep: true })

// 筛选逻辑
const pagedProducts = computed(() => {
  return products.value
})

// 操作
const handleFilter = () => {
  currentPage.value = 1
  loadProducts()
}

const selectCategory = (cat: string) => {
  category.value = cat
  handleFilter()
}

// 加载商品评论
const loadReviews = async (productId: number) => {
  reviewLoading.value = true
  try {
    const res = await fetch(`http://localhost:8085/api/mall/reviews/product/${productId}?page=${reviewPage.value}&size=${reviewPageSize.value}`)
    const data = await res.json()
    if (data.code === 200) {
      reviews.value = data.data.records || []
      reviewTotal.value = data.data.total || 0
      reviewPages.value = data.data.pages || 0
      avgRating.value = data.data.avgRating || 5.0
    }
  } catch (e) {
    console.error('加载评论失败', e)
  } finally {
    reviewLoading.value = false
  }
}

// 评论翻页
const handleReviewPageChange = (page: number) => {
  reviewPage.value = page
  if (currentProduct.value) {
    loadReviews(currentProduct.value.id)
  }
}

// 商品列表翻页
const handlePageChange = (page: number) => {
  currentPage.value = page
  loadProducts()
  // 滚动到商品列表顶部
  window.scrollTo({ top: 400, behavior: 'smooth' })
}

// 检查是否可以评论
const checkCanReview = async (productId: number) => {
  if (!userStore.token || !userStore.userInfo?.id) {
    canSubmitReview.value = false
    return
  }
  try {
    const res = await fetch(`http://localhost:8085/api/mall/reviews/can-review?userId=${userStore.userInfo.id}&productId=${productId}`)
    const data = await res.json()
    canSubmitReview.value = data.code === 200 && data.data === true
  } catch (e) {
    console.error('检查评论权限失败', e)
    canSubmitReview.value = false
  }
}

// 提交评论
const submitReview = async () => {
  if (!currentProduct.value || !userStore.userInfo?.id) return
  if (!newReview.value.content.trim()) {
    ElMessage.warning('请输入评价内容')
    return
  }
  
  submittingReview.value = true
  try {
    const res = await fetch('http://localhost:8085/api/mall/reviews', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${userStore.token}`
      },
      body: JSON.stringify({
        productId: currentProduct.value.id,
        userId: userStore.userInfo.id,
        username: userStore.userInfo.username || userStore.userInfo.nickname,
        rating: newReview.value.rating,
        content: newReview.value.content
      })
    })
    const data = await res.json()
    if (data.code === 200) {
      ElMessage.success('评价提交成功')
      newReview.value = { rating: 5, content: '' }
      canSubmitReview.value = false
      reviewPage.value = 1
      loadReviews(currentProduct.value.id)
    } else {
      ElMessage.error(data.message || '评价提交失败')
    }
  } catch (e) {
    console.error('提交评论失败', e)
    ElMessage.error('评价提交失败')
  } finally {
    submittingReview.value = false
  }
}

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const openDetail = (product: any) => {
  currentProduct.value = product
  detailVisible.value = true
  selectedImageIndex.value = 0  // 重置图片索引
  reviewPage.value = 1
  newReview.value = { rating: 5, content: '' }
  loadReviews(product.id)
  checkCanReview(product.id)
  buyCount.value = 1
}

const addToCart = async (product: any, quantity?: number) => {
  // 检查是否登录
  console.log('addToCart - userStore.token:', userStore.token)
  console.log('addToCart - localStorage token:', localStorage.getItem('token'))
  console.log('addToCart - localStorage userInfo:', localStorage.getItem('userInfo'))
  
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  const qty = quantity || buyCount.value || 1
  
  try {
    console.log('准备加入购物车:', { productId: product.id, quantity: qty })
    
    const response = await mallApi.addToCart({
      productId: product.id,
      quantity: qty
    })
    
    console.log('加入购物车成功:', response)
    ElMessage.success(`已将 ${qty} 件 ${product.name} 加入购物车`)
    detailVisible.value = false
  } catch (error: any) {
    console.error('加入购物车失败:', error)
    console.error('错误详情:', error.response)
    console.error('错误数据:', error.response?.data)
    
    if (error.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      userStore.logout()
      router.push('/login')
    } else if (error.response?.status === 400) {
      const errorMsg = error.response?.data?.message || '请求参数错误'
      ElMessage.error(`加入购物车失败：${errorMsg}`)
      console.error('400错误 - 请求数据:', { productId: product.id, quantity: qty })
      console.error('400错误 - 用户信息:', localStorage.getItem('userInfo'))
    } else {
      ElMessage.error(error.response?.data?.message || '加入购物车失败，请稍后重试')
    }
  }
}

const toggleFavorite = async (product: any) => {
  try {
    // TODO: 调用收藏API（收藏功能后端还未实现）
    ElMessage.success('已添加到收藏夹')
  } catch (error) {
    console.error('收藏失败:', error)
    ElMessage.error('收藏失败')
  }
}

const buyNow = async (product: any) => {
  // 检查是否登录
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  try {
    // 先加入购物车
    await mallApi.addToCart({
      productId: product.id,
      quantity: buyCount.value
    })
    
    ElMessage.success('正在跳转到购物车...')
    detailVisible.value = false
    
    // 跳转到购物车页面
    setTimeout(() => {
      router.push('/cart')
    }, 500)
  } catch (error: any) {
    console.error('购买失败:', error)
    if (error.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      userStore.logout()
      router.push('/login')
    } else {
      ElMessage.error(error.response?.data?.message || '购买失败')
    }
  }
}
</script>

<style scoped>
.mall-page {
  background-color: #f5f7fa;
  min-height: 100vh;
  background-image: radial-gradient(rgba(160, 24, 47, 0.05) 1px, transparent 1px), radial-gradient(rgba(160, 24, 47, 0.05) 1px, #f5f7fa 1px);
  background-size: 20px 20px;
  background-position: 0 0, 10px 10px;
}

/* Hero Carousel */
.mall-hero {
  margin-bottom: 2rem;
  background: white;
  box-shadow: 0 4px 20px rgba(0,0,0,0.05);
}

.carousel-content {
  height: 100%;
  background-size: cover;
  background-position: center;
  position: relative;
}

.carousel-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, rgba(160, 24, 47, 0.9) 0%, rgba(160, 24, 47, 0.4) 60%, rgba(0,0,0,0) 100%);
  display: flex;
  align-items: center;
  padding-left: 12%;
}

.carousel-info {
  color: white;
  max-width: 600px;
  padding: 40px;
  border-left: 5px solid #a0182f;
  background: rgba(0,0,0,0.4);
  backdrop-filter: blur(8px);
  border-radius: 0 16px 16px 0;
  box-shadow: 0 8px 32px rgba(0,0,0,0.2);
}

.carousel-info .tag {
  background: #a0182f;
  padding: 6px 14px;
  font-size: 0.9rem;
  border-radius: 4px;
  margin-bottom: 1.2rem;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-weight: 700;
  letter-spacing: 1.5px;
  box-shadow: 0 4px 10px rgba(196, 30, 58, 0.4);
}

.carousel-info h2 {
  font-size: 3.5rem;
  margin-bottom: 1rem;
  font-weight: 800;
  line-height: 1.2;
  text-shadow: 0 4px 12px rgba(0,0,0,0.6);
  background: linear-gradient(to right, #fff, #ffd700);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.carousel-info p {
  font-size: 1.2rem;
  margin-bottom: 2.5rem;
  opacity: 0.95;
  line-height: 1.6;
  max-width: 90%;
  text-shadow: 0 2px 4px rgba(0,0,0,0.5);
}

.btn-hero {
  background: #a0182f;
  border-color: #a0182f;
  padding: 24px 40px;
  font-weight: 700;
  font-size: 1.2rem;
  transition: all 0.3s;
  letter-spacing: 2px;
}

.btn-hero:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 8px 20px rgba(196, 30, 58, 0.6);
  background: #a0182f;
  border-color: #a0182f;
}

/* Main Layout */
.mall-container {
  max-width: 1300px;
  margin: 0 auto;
  padding: 0 20px 60px;
  display: flex;
  gap: 30px;
}

/* Sidebar */
.mall-sidebar {
  width: 280px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.sidebar-section {
  background: white;
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.04);
  transition: all 0.3s;
  border: 1px solid #f0f0f0;
  position: relative;
  overflow: hidden;
}

.sidebar-section:hover {
  box-shadow: 0 8px 24px rgba(196, 30, 58, 0.08);
  transform: translateY(-4px);
  border-color: rgba(196, 30, 58, 0.1);
}

.sidebar-title {
  font-size: 1.2rem;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f0f0;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 800;
  letter-spacing: 0.5px;
}

.sidebar-title .el-icon {
  color: #a0182f;
  font-size: 1.4rem;
  background: rgba(196, 30, 58, 0.1);
  padding: 6px;
  border-radius: 8px;
}

.category-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.category-list li {
  padding: 14px 18px;
  cursor: pointer;
  border-radius: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #555;
  transition: all 0.3s;
  margin-bottom: 10px;
  border: 1px solid transparent;
  background: #fcfcfc;
}

.category-list li:hover {
  background: #fff5f5;
  color: #a0182f;
  border-color: #fecaca;
  transform: translateX(5px);
  padding-left: 22px;
}

.category-list li.active {
  background: linear-gradient(135deg, #a0182f, #c41e3a);
  color: white;
  font-weight: 600;
  box-shadow: 0 6px 15px rgba(196, 30, 58, 0.25);
  transform: scale(1.02);
  border: none;
}

.category-list li.active .count {
  background: rgba(255,255,255,0.25);
  color: white;
}

.count {
  font-size: 0.8rem;
  background: #fff5f5;
  padding: 2px 10px;
  border-radius: 12px;
  color: #a0182f;
  transition: all 0.2s;
  font-weight: 600;
}

.price-range {
  padding: 0 10px;
}

.price-inputs {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
}

.promotion-banner {
  background: linear-gradient(135deg, #c41e3a 0%, #8b1e3f 100%);
  padding: 30px;
  border-radius: 12px;
  color: white;
  position: relative;
  overflow: hidden;
  text-align: center;
  box-shadow: 0 12px 30px rgba(196, 30, 58, 0.3);
}

.promotion-banner::before {
  content: '';
  position: absolute;
  top: -50px;
  right: -50px;
  width: 150px;
  height: 150px;
  background: rgba(255,255,255,0.1);
  border-radius: 50%;
  pointer-events: none;
}

.promotion-banner::after {
  content: '';
  position: absolute;
  bottom: -30px;
  left: -30px;
  width: 100px;
  height: 100px;
  background: rgba(255,255,255,0.1);
  border-radius: 50%;
  pointer-events: none;
}

.promo-badge {
  display: inline-block;
  background: rgba(255,255,255,0.25);
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.85rem;
  margin-bottom: 16px;
  backdrop-filter: blur(4px);
  border: 1px solid rgba(255,255,255,0.3);
  font-weight: 600;
}

.promotion-banner h4 {
  font-size: 2rem;
  margin-bottom: 12px;
  font-weight: 800;
  letter-spacing: 2px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.promo-desc {
  font-size: 1.2rem;
  margin-bottom: 8px;
  font-weight: 600;
}

.promo-sub {
  font-size: 0.9rem;
  opacity: 0.9;
  margin-bottom: 24px;
}

.promo-btn {
  color: #a0182f;
  font-weight: 700;
  padding: 20px 36px;
  border: none;
  border-radius: 30px;
  box-shadow: 0 6px 15px rgba(0,0,0,0.2);
  transition: all 0.3s;
}

.promo-btn:hover {
  transform: translateY(-2px) scale(1.05);
  background: white;
  box-shadow: 0 8px 20px rgba(0,0,0,0.3);
}

/* Main Content */
.mall-main {
  flex: 1;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  background: white;
  padding: 20px 24px;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.04);
  border: 1px solid #f0f0f0;
}

.quick-filters {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  padding: 16px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.filter-label {
  font-weight: 600;
  color: #666;
  font-size: 0.9rem;
}

.filter-tag {
  cursor: pointer;
  transition: all 0.3s;
  font-size: 0.9rem;
  padding: 8px 16px;
}

.filter-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.toolbar-left h2 {
  font-size: 1.6rem;
  margin: 0 0 4px 0;
  color: #2c3e50;
  font-weight: 800;
  background: linear-gradient(to right, #a0182f, #c41e3a);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.result-count {
  color: #999;
  font-size: 0.9rem;
}

.toolbar-right {
  display: flex;
  gap: 16px;
  align-items: center;
}

.search-box {
  width: 240px;
}

/* Product Grid */
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 24px;
}

.product-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  border: 1px solid #f0f0f0;
  position: relative;
}

.product-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0,0,0,0.1);
  border-color: rgba(196, 30, 58, 0.3);
}

.product-image {
  height: 260px;
  position: relative;
  overflow: hidden;
  background: #f8f9fa;
}

/* Placeholders for images */
.placeholder-img {
  width: 100%;
  height: 100%;
  transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.product-icon-large {
  font-size: 5rem;
  opacity: 0.9;
  filter: drop-shadow(0 4px 8px rgba(0,0,0,0.2));
  transition: all 0.3s;
}

.product-card:hover .product-icon-large {
  transform: scale(1.1) rotate(5deg);
  opacity: 1;
}

.img-0 { background: linear-gradient(135deg, #ff9a9e 0%, #ffc3a0 100%); }
.img-1 { background: linear-gradient(135deg, #a0182f 0%, #8b1e3f 100%); }
.img-2 { background: linear-gradient(135deg, #c41e3a 0%, #e63946 100%); }
.img-3 { background: linear-gradient(135deg, #fce38a 0%, #f38181 100%); }

.product-card:hover .placeholder-img {
  transform: scale(1.05);
}

.stock-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  background: rgba(255,255,255,0.95);
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.75rem;
  color: #333;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  z-index: 1;
}

.stock-badge.low {
  color: #e6a23c;
  font-weight: 600;
  border: 1px solid #e6a23c;
}

.stock-badge.out {
  color: #909399;
  font-weight: 600;
  border: 1px solid #909399;
  background: rgba(144, 147, 153, 0.1);
}

.hot-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: linear-gradient(135deg, #ff416c, #ff4b2b);
  color: white;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(196, 30, 58, 0.3);
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.new-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: linear-gradient(135deg, #ff416c, #ff4b2b);
  color: white;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(196, 30, 58, 0.3);
}

.recommend-badge {
  position: absolute;
  top: 40px; /* Below new badge */
  right: 12px;
  background: linear-gradient(135deg, #fce38a, #f38181);
  color: white;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
  box-shadow: 0 2px 8px rgba(230, 162, 60, 0.3);
}

.product-info {
  padding: 20px;
}

.product-category {
  font-size: 0.8rem;
  color: #999;
  margin-bottom: 6px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.product-title {
  font-size: 1.1rem;
  margin-bottom: 10px;
  color: #2c3e50;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: 700;
}

.product-tags {
  display: flex;
  gap: 6px;
  margin-bottom: 12px;
}

.product-tags .tag {
  font-size: 0.7rem;
  padding: 2px 6px;
  border: 1px solid #ffebee;
  border-radius: 4px;
  color: #d32f2f;
  background: #fff5f5;
}

.product-bottom {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-top: auto;
}

.product-price {
  color: #d32f2f;
  font-weight: 800;
  display: flex;
  align-items: baseline;
}

.currency {
  font-size: 0.9rem;
  margin-right: 2px;
}

.amount {
  font-size: 1.4rem;
}

.product-sales {
  font-size: 0.8rem;
  color: #999;
}

.product-stock {
  font-size: 0.8rem;
  color: #52c41a;
  margin-top: 6px;
}

.product-stock.low {
  color: #faad14;
}

.product-stock.out {
  color: #ff4d4f;
}

/* Pagination */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 60px;
  margin-bottom: 20px;
}

:deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
  background-color: #a0182f !important;
  box-shadow: 0 4px 10px rgba(160, 24, 47, 0.3);
}

:deep(.el-pagination.is-background .el-pager li:hover) {
  color: #a0182f;
}

/* Detail Dialog */
.product-detail {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 10px;
  max-height: 80vh;
}

.detail-gallery {
  width: 100%;
  flex-shrink: 0;
}

/* 右侧区域：图片+标签导航+内容 */
.detail-right-section {
  width: 45%;
  flex-shrink: 0;
  order: 2;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 横向标签导航（在图片下方） */
.tabs-nav-horizontal {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 8px;
}

.tab-item-horizontal {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 12px 8px;
  background: white;
  border: 2px solid #f0f0f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  text-align: center;
}

.tab-item-horizontal:hover {
  border-color: #ffcdd2;
  background: #fff5f5;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(196, 30, 58, 0.1);
}

.tab-item-horizontal.active {
  background: linear-gradient(135deg, #c41e3a, #a0182f);
  border-color: #c41e3a;
  color: white;
  box-shadow: 0 4px 16px rgba(196, 30, 58, 0.3);
  transform: translateY(-2px) scale(1.05);
}

.tab-item-horizontal .tab-icon {
  font-size: 1.5rem;
  display: block;
  transition: transform 0.3s;
}

.tab-item-horizontal.active .tab-icon {
  transform: scale(1.1);
}

.tab-item-horizontal .tab-label {
  font-size: 0.75rem;
  font-weight: 600;
  letter-spacing: 0.3px;
}

/* 标签页内容区域（在右侧） */
.tabs-content-right {
  background: #fafafa;
  border-radius: 12px;
  padding: 20px;
  overflow-y: auto;
  max-height: 350px;
  min-height: 200px;
}

.main-image {
  width: 100%;
  height: 350px;
  border-radius: 12px;
  margin-bottom: 16px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

/* 真实图片样式 */
.main-image.real-image {
  background: #f5f5f5;
}

.main-image.real-image img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.thumb.real-thumb {
  background: #f5f5f5;
  overflow: hidden;
}

.thumb.real-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detail-icon-large {
  font-size: 7.5rem;
  filter: drop-shadow(0 8px 16px rgba(0,0,0,0.3));
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.thumbs {
  display: flex;
  gap: 12px;
}

.thumb {
  width: 75px;
  height: 75px;
  border-radius: 8px;
  cursor: pointer;
  opacity: 0.6;
  transition: all 0.2s;
  border: 2px solid transparent;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.thumb-icon {
  font-size: 2.2rem;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.2));
}

.thumb:hover,
.thumb.active {
  opacity: 1;
  border-color: #a0182f;
  transform: scale(1.05);
}

.detail-info {
  flex: 1;
  overflow-y: auto;
  max-height: 500px;
  order: 1;
}

.detail-title {
  font-size: 2rem;
  margin-bottom: 16px;
  color: #1a1a1a;
  font-weight: 800;
  line-height: 1.3;
}

.detail-meta {
  display: flex;
  gap: 24px;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid rgba(0,0,0,0.06);
}

.detail-price {
  font-size: 2.4rem;
  color: #d32f2f;
  font-weight: 800;
  letter-spacing: -1px;
}

.detail-sales, .detail-rating {
  color: #666;
  font-size: 0.95rem;
  display: flex;
  align-items: center;
  gap: 4px;
}

.detail-desc {
  color: #555;
  line-height: 1.8;
  margin-bottom: 30px;
  font-size: 1rem;
}

.option-row {
  margin-bottom: 28px;
}

.option-row label {
  display: block;
  margin-bottom: 14px;
  color: #333;
  font-weight: 600;
  font-size: 1.05rem;
}

.options {
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
}

.option-tag {
  padding: 10px 24px;
  border: 2px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 1rem;
}

.option-tag:hover {
  border-color: #ffcdd2;
  color: #d32f2f;
}

.option-tag.active {
  border-color: #d32f2f;
  color: #d32f2f;
  background: #fff5f5;
  font-weight: 600;
}

.detail-actions {
  margin-top: 32px;
  display: flex;
  gap: 16px;
}

.detail-tabs {
  margin-top: 40px;
  border-top: 1px solid #f0f0f0;
  padding-top: 20px;
}

.tab-content {
  padding: 20px 0;
}

.tab-content h4 {
  font-size: 1.1rem;
  margin-bottom: 12px;
  color: #2c3e50;
  font-weight: 700;
}

.feature-list {
  list-style: none;
  padding: 0;
  margin: 0 0 24px 0;
}

.feature-list li {
  padding: 8px 0;
  color: #555;
  font-size: 0.95rem;
  line-height: 1.6;
}

.review-summary {
  text-align: center;
  padding: 30px;
  background: #f8f9fa;
  border-radius: 12px;
  margin-bottom: 30px;
}

.rating-score {
  font-size: 3rem;
  font-weight: 800;
  color: #a0182f;
  margin-bottom: 8px;
}

.rating-stars {
  font-size: 1.5rem;
  margin-bottom: 8px;
}

.rating-count {
  color: #999;
  font-size: 0.9rem;
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-item {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.reviewer-name {
  font-weight: 600;
  color: #2c3e50;
}

.review-time {
  color: #999;
  font-size: 0.85rem;
  margin-left: auto;
}

.review-rating {
  margin-bottom: 8px;
  font-size: 0.9rem;
}

.review-content {
  color: #555;
  line-height: 1.6;
  margin: 0;
}

/* Mobile Responsiveness */
@media (max-width: 992px) {
  .mall-container {
    flex-direction: column;
    padding: 0 1.5rem 3rem;
  }
  
  .mall-sidebar {
    width: 100%;
  }
  
  .category-list {
    display: flex;
    overflow-x: auto;
    gap: 10px;
    padding-bottom: 0.5rem;
    -webkit-overflow-scrolling: touch;
  }
  
  .category-list li {
    white-space: nowrap;
    margin-bottom: 0;
    flex-shrink: 0;
  }
  
  .product-detail {
    flex-direction: column;
    gap: 2rem;
  }
  
  .detail-gallery {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .mall-hero {
    margin-bottom: 1.5rem;
  }
  
  .carousel-info {
    padding: 1.5rem;
    max-width: 100%;
    border-radius: 0;
    border-left: none;
    border-top: 4px solid #a0182f;
  }
  
  .carousel-info h2 {
    font-size: 2rem;
  }
  
  .toolbar {
    flex-direction: column;
    align-items: stretch;
    gap: 1rem;
    padding: 1rem;
  }
  
  .toolbar-right {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-box {
    width: 100%;
  }
  
  .quick-filters {
    flex-wrap: wrap;
    padding: 12px;
  }
  
  .filter-label {
    width: 100%;
    margin-bottom: 8px;
  }
  
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
  
  .product-image {
    height: 180px;
  }
  
  .product-icon-large {
    font-size: 3rem;
  }
  
  .product-info {
    padding: 12px;
  }
  
  .product-title {
    font-size: 0.9rem;
  }
  
  .detail-title {
    font-size: 1.6rem;
  }
  
  .detail-price {
    font-size: 2rem;
  }
  
  .detail-actions {
    flex-direction: column;
  }
  
  .detail-actions .el-button {
    width: 100%;
  }
  
  .detail-icon-large {
    font-size: 5rem;
  }
}

/* 新增：完善的商品详情样式 */
.dialog-header h3 {
  margin: 0;
  font-size: 1.3rem;
  color: #333;
}

/* 三列布局 */
.detail-main-three-columns {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 32px;
  margin-bottom: 24px;
  padding: 8px;
}

.detail-info {
  overflow-y: auto;
  max-height: 650px;
  padding: 0 8px;
}

/* 第2列：图片 */
.detail-gallery-column {
  display: flex;
  flex-direction: column;
  max-height: 650px;
  overflow: hidden;
}

.detail-gallery {
  width: 100%;
}

/* 第3列：标签导航 + 内容 */
.detail-tabs-column {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-height: 650px;
  overflow: hidden;
}

/* 垂直标签导航 */
.tabs-nav-vertical {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.tab-item-vertical {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 18px;
  background: white;
  border: 2px solid #f0f0f0;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
}

.tab-item-vertical:hover {
  border-color: #ffcdd2;
  background: #fff5f5;
  transform: translateX(-4px);
  box-shadow: -4px 0 12px rgba(196, 30, 58, 0.1);
}

.tab-item-vertical.active {
  background: linear-gradient(135deg, #c41e3a, #a0182f);
  border-color: #c41e3a;
  color: white;
  box-shadow: -4px 0 16px rgba(196, 30, 58, 0.3);
  transform: translateX(-4px);
}

.tab-item-vertical .tab-icon {
  font-size: 1.4rem;
  transition: transform 0.3s;
}

.tab-item-vertical.active .tab-icon {
  transform: scale(1.1);
}

.tab-item-vertical .tab-label {
  font-size: 0.95rem;
  font-weight: 600;
  letter-spacing: 0.5px;
}

/* 标签页内容（垂直布局） */
.tabs-content-vertical {
  background: #fafafa;
  border-radius: 12px;
  padding: 24px;
  overflow-y: auto;
  max-height: 480px;
  flex: 1;
}

.detail-main {
  display: flex;
  gap: 40px;
  margin-bottom: 20px;
  flex-shrink: 0;
}

.image-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(211, 47, 47, 0.9);
  color: white;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
}

.detail-price-box {
  background: linear-gradient(135deg, #fff5f5 0%, #ffebee 100%);
  padding: 24px;
  border-radius: 12px;
  margin-bottom: 24px;
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 16px;
  margin-bottom: 16px;
}

.price-label {
  font-size: 1rem;
  color: #666;
  font-weight: 500;
}

.detail-price {
  font-size: 2.2rem;
  color: #d32f2f;
  font-weight: 800;
  letter-spacing: -1px;
}

.original-price {
  font-size: 1.2rem;
  color: #999;
  text-decoration: line-through;
}

.meta-row {
  display: flex;
  gap: 24px;
  font-size: 1rem;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
}

.detail-desc-box {
  margin-bottom: 24px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 10px;
  border-left: 4px solid #c41e3a;
}

.detail-desc {
  color: #555;
  line-height: 1.8;
  font-size: 1rem;
  margin: 0;
}

.detail-features {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 28px;
  padding: 20px;
  background: #fafafa;
  border-radius: 10px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 1rem;
  color: #555;
}

.extra-price {
  font-size: 0.9rem;
  color: #d32f2f;
  margin-left: 6px;
}

.stock-tip {
  margin-left: 16px;
  font-size: 0.95rem;
  color: #999;
}

.btn-add-cart {
  flex: 1;
  height: 48px;
  font-size: 1.05rem;
  font-weight: 600;
}

.btn-buy-now {
  flex: 1;
  height: 48px;
  font-size: 1.05rem;
  font-weight: 600;
}

.detail-tabs-container {
  border-top: 2px solid #f0f0f0;
  padding-top: 20px;
  flex: 1;
  overflow-y: auto;
  min-height: 0;
}

.review-count {
  color: #999;
  font-size: 0.85rem;
}

.detail-section {
  margin-bottom: 30px;
}

.param-table {
  width: 100%;
  border-collapse: collapse;
}

.param-table tr {
  border-bottom: 1px solid #f0f0f0;
}

.param-table td {
  padding: 14px 0;
  font-size: 1rem;
}

.param-label {
  color: #999;
  width: 130px;
  font-weight: 500;
}

.rating-box {
  flex: 1;
}

.review-summary {
  display: flex;
  gap: 40px;
  align-items: center;
}

.rating-distribution {
  flex: 2;
}

.rating-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
  font-size: 0.9rem;
}

.rating-bar span:first-child {
  width: 40px;
  color: #666;
}

.rating-bar span:last-child {
  width: 40px;
  text-align: right;
  color: #999;
}

.bar {
  flex: 1;
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

.fill {
  height: 100%;
  background: linear-gradient(90deg, #ffd700, #ffa500);
  transition: width 0.3s;
}

.review-item {
  padding: 20px;
  background: #fafafa;
  border-radius: 12px;
  transition: all 0.2s;
}

.review-item:hover {
  background: #f5f5f5;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.reviewer-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.reviewer-name {
  font-weight: 600;
  color: #333;
}

.review-time {
  font-size: 0.85rem;
  color: #999;
}

.review-rating {
  font-size: 0.9rem;
  color: #ffa500;
}

.review-content {
  color: #555;
  line-height: 1.6;
  margin-bottom: 12px;
}

.review-images {
  display: flex;
  gap: 8px;
}

.review-img {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.2s;
}

.review-img:hover {
  transform: scale(1.05);
}

.img-icon {
  font-size: 2rem;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.2));
}

.service-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.service-item {
  display: flex;
  gap: 16px;
  padding: 20px;
  background: #fafafa;
  border-radius: 12px;
  transition: all 0.2s;
}

.service-item:hover {
  background: #f5f5f5;
  transform: translateX(4px);
}

.service-icon {
  font-size: 2.5rem;
  flex-shrink: 0;
}

.service-content h4 {
  margin: 0 0 8px 0;
  font-size: 1.1rem;
  color: #333;
}

.service-content p {
  margin: 0;
  color: #666;
  font-size: 0.9rem;
  line-height: 1.6;
}

/* 标签页内容面板 */
.tab-pane {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.pane-title {
  font-size: 1.4rem;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #c41e3a;
  display: flex;
  align-items: center;
  gap: 10px;
}

.pane-title::before {
  content: '';
  width: 4px;
  height: 24px;
  background: linear-gradient(to bottom, #c41e3a, #a0182f);
  border-radius: 2px;
}

.pane-text {
  color: #555;
  line-height: 1.8;
  font-size: 1rem;
  margin-bottom: 16px;
  text-align: justify;
}

.feature-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.feature-list li {
  padding: 12px 0;
  color: #555;
  font-size: 1rem;
  line-height: 1.6;
  display: flex;
  align-items: center;
  gap: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.feature-list li:last-child {
  border-bottom: none;
}

.feature-list li::before {
  content: '✓';
  color: #c41e3a;
  font-weight: bold;
  font-size: 1.2rem;
  flex-shrink: 0;
}

.param-table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.param-table tr {
  border-bottom: 1px solid #f0f0f0;
}

.param-table tr:last-child {
  border-bottom: none;
}

.param-table td {
  padding: 16px;
  font-size: 0.95rem;
}

.param-label {
  color: #999;
  width: 140px;
  font-weight: 600;
  background: #fafafa;
}

.review-summary-simple {
  background: white;
  padding: 24px;
  border-radius: 12px;
  margin-bottom: 24px;
}

.rating-display {
  display: flex;
  align-items: center;
  gap: 24px;
}

.rating-score-large {
  font-size: 3.2rem;
  font-weight: 800;
  color: #c41e3a;
  line-height: 1;
}

.rating-stars-large {
  font-size: 1.3rem;
  margin-bottom: 6px;
}

.rating-text {
  font-size: 0.9rem;
  color: #999;
}

.review-list-simple {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-card {
  background: white;
  padding: 20px;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
  transition: all 0.3s;
}

.review-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  transform: translateY(-2px);
}

.review-header-simple {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.reviewer-name-simple {
  font-weight: 600;
  color: #333;
  flex: 1;
  font-size: 1rem;
}

.review-rating-simple {
  color: #ffa500;
  font-size: 0.95rem;
}

.review-date {
  font-size: 0.9rem;
  color: #999;
}

.review-text {
  color: #555;
  line-height: 1.7;
  margin: 0;
  font-size: 1rem;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.service-card {
  background: white;
  padding: 20px;
  border-radius: 12px;
  text-align: center;
  transition: all 0.3s;
  border: 1px solid #f0f0f0;
}

.service-card:hover {
  box-shadow: 0 6px 16px rgba(0,0,0,0.1);
  transform: translateY(-4px);
  border-color: #c41e3a;
}

.service-icon-large {
  font-size: 2.8rem;
  margin-bottom: 10px;
}

.service-card h4 {
  font-size: 1.05rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 8px 0;
}

.service-card p {
  font-size: 0.9rem;
  color: #666;
  line-height: 1.6;
  margin: 0;
}

@media (max-width: 768px) {
  .detail-main {
    flex-direction: column;
    gap: 20px;
  }
  
  .detail-gallery {
    width: 100%;
  }
  
  .detail-features {
    grid-template-columns: 1fr;
  }
  
  .review-summary {
    flex-direction: column;
    gap: 20px;
  }
  
  .detail-actions {
    flex-direction: column;
  }
  
  .btn-add-cart,
  .btn-buy-now {
    width: 100%;
  }
  
  .service-grid {
    grid-template-columns: 1fr;
  }
  
  /* 移动端布局调整为单列 */
  .detail-main-three-columns {
    grid-template-columns: 1fr;
  }
  
  .detail-info,
  .tabs-content-vertical {
    max-height: 400px;
  }
}

.review-pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.review-loading {
  padding: 20px;
}

.stock-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 12px 0;
  font-size: 0.9rem;
}

.stock-info.in-stock {
  color: #52c41a;
}

.stock-info.low-stock {
  color: #faad14;
}

.stock-info.out-of-stock {
  color: #ff4d4f;
}

/* 评论表单样式 */
.review-form {
  background: #f9f9f9;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 20px;
}

.review-form h4 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 1.1rem;
}

.rating-input {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.rating-input span {
  color: #666;
}

.review-form .el-textarea {
  margin-bottom: 15px;
}

.review-form .el-button {
  background: #c41e3a;
  border-color: #c41e3a;
}

.review-form .el-button:hover {
  background: #a0182f;
  border-color: #a0182f;
}

.review-tip {
  margin-bottom: 20px;
}

/* 评论翻页样式 */
.review-pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}
</style>





