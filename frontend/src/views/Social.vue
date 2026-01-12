<template>
  <MainLayout>
    <div class="social-page">
      <!-- 社区 Hero -->
      <div class="social-hero">
        <div class="hero-content">
          <div class="hero-text" v-motion-slide-visible-up :delay="200">
            <h1>燕赵红色社区 · 薪火相传</h1>
            <p>分享河北红色记忆，讲述燕赵初心故事</p>
          </div>
          <div class="hero-stats" v-motion-slide-visible-up :delay="400">
            <div class="stat-item">
              <span class="count">1.2W+</span>
              <span class="label">红色故事</span>
            </div>
            <div class="stat-item">
              <span class="count">5000+</span>
              <span class="label">活跃用户</span>
            </div>
            <div class="stat-item">
              <span class="count">800+</span>
              <span class="label">每日新增</span>
            </div>
          </div>
        </div>
      </div>

      <div class="social-container">
        <!-- 左侧主要内容区 -->
        <main class="social-main">
          <!-- 发帖框 -->
          <div class="create-post-card" v-motion-fade-visible>
            <div class="input-area" @click="openPostDialog">
              <el-avatar :size="40" icon="UserFilled" class="user-avatar" />
              <div class="fake-input">分享你今天的红色感悟...</div>
            </div>
            <div class="action-bar">
              <div class="action-btn">
                <el-icon><Picture /></el-icon> 图片
              </div>
              <div class="action-btn">
                <el-icon><VideoCamera /></el-icon> 视频
              </div>
              <div class="action-btn">
                <el-icon><Location /></el-icon> 打卡
              </div>
              <el-button type="primary" round size="small" @click="openPostDialog" class="btn-publish">发布</el-button>
            </div>
          </div>

          <!-- 搜索和筛选区域 -->
          <div class="search-filter-section" v-motion-fade-visible>
            <div class="search-box">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索帖子标题或内容..."
                clearable
                @keyup.enter="handleSearch"
                @clear="handleClearSearch"
                class="search-input"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
                <template #append>
                  <el-button @click="handleSearch" :loading="isSearching">
                    搜索
                  </el-button>
                </template>
              </el-input>
            </div>
            <div class="sort-options">
              <span class="sort-label">排序：</span>
              <el-radio-group v-model="sortBy" size="small" @change="handleSortChange">
                <el-radio-button value="latest">最新</el-radio-button>
                <el-radio-button value="hot">最热</el-radio-button>
                <el-radio-button value="comments">评论最多</el-radio-button>
              </el-radio-group>
            </div>
          </div>

          <!-- 分类标签 -->
          <div class="category-tabs" v-motion-fade-visible>
            <div 
              v-for="cat in categories" 
              :key="cat.key" 
              class="category-tab"
              :class="{ active: activeCategory === cat.key }" 
              @click="activeCategory = cat.key"
            >
              {{ cat.label }}
            </div>
          </div>

          <!-- 动态列表 -->
          <div v-if="loading" class="posts-loading">
            <el-skeleton v-for="i in 3" :key="i" animated class="post-skeleton">
              <template #template>
                <div style="display: flex; gap: 16px; margin-bottom: 16px;">
                  <el-skeleton-item variant="circle" style="width: 48px; height: 48px" />
                  <div style="flex: 1">
                    <el-skeleton-item variant="text" style="width: 30%" />
                    <el-skeleton-item variant="text" style="width: 20%; margin-top: 8px" />
                  </div>
                </div>
                <el-skeleton-item variant="p" style="width: 100%" />
                <el-skeleton-item variant="p" style="width: 80%" />
                <el-skeleton-item variant="image" style="width: 100%; height: 200px; margin-top: 16px" />
              </template>
            </el-skeleton>
          </div>

          <div v-else class="posts-list">
            <div 
              v-for="(post, index) in filteredPosts" 
              :key="post.id" 
              class="post-card"
              v-motion-slide-visible-up
              :delay="index * 100"
              @dblclick="handleDoubleTapLike(post, $event)"
            >
              <!-- 双击点赞动画 -->
              <transition name="heart-pop">
                <div v-if="post.showHeartAnimation" class="double-tap-heart">
                  <el-icon><StarFilled /></el-icon>
                </div>
              </transition>
              <div class="post-header">
                <el-avatar :size="48" :src="post.userAvatar" class="post-avatar">
                  {{ post.userName.charAt(0) }}
                </el-avatar>
                <div class="post-meta">
                  <div class="user-name">
                    {{ post.userName }}
                    <el-tag size="small" type="danger" effect="dark" v-if="post.isVip" class="vip-tag">红色传承人</el-tag>
                  </div>
                  <div class="post-time">{{ post.createTime }} · {{ post.location || '未知地点' }}</div>
                </div>
                <el-button icon="More" circle text class="more-btn"></el-button>
              </div>

              <div class="post-content">
                <h3 v-if="post.title" class="post-title">{{ post.title }}</h3>
                <p class="post-text">{{ post.content }}</p>
                
                <div v-if="post.images && post.images.length > 0" class="post-images" :class="`grid-${Math.min(post.images.length, 3)}`">
                  <div 
                    v-for="(img, index) in post.images" 
                    :key="index" 
                    class="post-image"
                    :style="{ backgroundImage: `url(${img})` }"
                    @click="previewImage(img)"
                  ></div>
                </div>

                <!-- 视频显示 -->
                <div v-if="post.video" class="post-video">
                  <video 
                    :src="post.video.url" 
                    :poster="post.video.thumbnail"
                    controls
                    class="video-player"
                  ></video>
                </div>
                
                <div class="post-tags" v-if="post.tags && post.tags.length">
                  <span v-for="tag in post.tags" :key="tag" class="hashtag">
                    <el-icon><PriceTag /></el-icon> {{ tag }}
                  </span>
                </div>
              </div>

              <div class="post-footer">
                <div 
                  class="interaction-btn like-btn" 
                  :class="{ active: post.isLiked, 'like-animating': post.likeAnimating }" 
                  @click="toggleLike(post)"
                >
                  <div class="like-icon-wrapper">
                    <el-icon class="like-icon"><component :is="post.isLiked ? 'StarFilled' : 'Star'" /></el-icon>
                    <!-- 点赞粒子效果 -->
                    <div class="like-particles" v-if="post.showParticles">
                      <span v-for="i in 6" :key="i" class="particle" :style="{ '--i': i }"></span>
                    </div>
                  </div>
                  <transition name="like-count" mode="out-in">
                    <span :key="post.likes" class="like-count">{{ formatLikeCount(post.likes) }}</span>
                  </transition>
                </div>
                <div class="interaction-btn" @click="showComments(post)">
                  <el-icon><ChatDotRound /></el-icon>
                  <span>{{ post.comments }}</span>
                </div>
                <div class="interaction-btn" @click="sharePost(post)">
                  <el-icon><Share /></el-icon>
                  <span>{{ post.shares }}</span>
                </div>
              </div>
            </div>
            
            <div class="load-more">
              <el-button text type="info">加载更多动态</el-button>
            </div>
          </div>
        </main>

        <!-- 右侧侧边栏 -->
        <aside class="social-sidebar">
          <!-- 热门话题 -->
          <div class="sidebar-card" v-motion-slide-visible-right :delay="200">
            <h3 class="card-title">🔥 热门话题</h3>
            <ul class="topic-list">
              <li v-for="(topic, index) in hotTopics" :key="index" class="topic-item">
                <span class="topic-rank" :class="{ top: index < 3 }">{{ index + 1 }}</span>
                <span class="topic-name">#{{ topic.name }}#</span>
                <span class="topic-hot">{{ topic.hot }}</span>
              </li>
            </ul>
          </div>

          <!-- 活跃用户 -->
          <div class="sidebar-card" v-motion-slide-visible-right :delay="400">
            <h3 class="card-title">🏆 红色达人</h3>
            <div class="active-users">
              <div v-for="user in activeUsers" :key="user.id" class="active-user">
                <el-avatar :size="40" :src="user.avatar">{{ user.name.charAt(0) }}</el-avatar>
                <div class="user-detail">
                  <span class="name">{{ user.name }}</span>
                  <span class="desc">{{ user.desc }}</span>
                </div>
                <el-button 
                  type="primary" 
                  size="small" 
                  plain 
                  icon="Plus" 
                  circle
                  @click="handleFollowUser(user)"
                ></el-button>
              </div>
            </div>
          </div>

          <!-- 公告 -->
          <div class="sidebar-card announcement" v-motion-slide-visible-right :delay="600">
            <h3 class="card-title">📢 社区公告</h3>
            <p>欢迎来到河北红色基因库社区！请文明发言，共同维护燕赵红色文化交流氛围。</p>
            <el-button type="primary" link>查看社区规范 ></el-button>
          </div>
        </aside>
      </div>

      <!-- 发帖弹窗 -->
      <el-dialog 
        v-model="postDialogVisible" 
        title="发布动态" 
        width="600px" 
        align-center
        :close-on-click-modal="false"
        class="post-dialog"
      >
        <div class="post-form">
          <!-- 标题输入 -->
          <el-input
            v-model="newPost.title"
            placeholder="添加标题（可选）"
            maxlength="50"
            show-word-limit
            class="title-input"
          />

          <!-- 内容输入 -->
          <el-input
            v-model="newPost.content"
            type="textarea"
            :rows="6"
            placeholder="分享你的红色记忆、学习心得、旅游见闻..."
            maxlength="500"
            show-word-limit
            class="content-input"
          />

          <!-- 媒体内容提示 -->
          <div class="media-summary" v-if="newPost.images.length > 0 || newPost.video">
            <el-tag v-if="newPost.images.length > 0" type="success" size="small">
              <el-icon><Picture /></el-icon>
              {{ newPost.images.length }} 张图片
            </el-tag>
            <el-tag v-if="newPost.video" type="warning" size="small">
              <el-icon><VideoCamera /></el-icon>
              1 个视频
            </el-tag>
          </div>

          <!-- 图片上传区域 -->
          <div class="upload-section" v-if="showImageUpload">
            <div class="upload-title">
              <el-icon><Picture /></el-icon>
              <span>添加图片（最多9张）</span>
            </div>
            <div class="image-upload-list">
              <div 
                v-for="(img, index) in newPost.images" 
                :key="index" 
                class="upload-image-item"
              >
                <img :src="img" alt="preview" />
                <div class="image-actions">
                  <el-icon class="remove-icon" @click="removeImage(index)"><Close /></el-icon>
                </div>
              </div>
              <div 
                v-if="newPost.images.length < 9" 
                class="upload-trigger"
                @click="triggerImageUpload"
              >
                <el-icon><Plus /></el-icon>
                <span>添加图片</span>
              </div>
            </div>
            <input 
              ref="imageInput" 
              type="file" 
              accept="image/*" 
              multiple 
              style="display: none"
              @change="handleImageUpload"
            />
          </div>

          <!-- 视频上传区域 -->
          <div class="upload-section" v-if="showVideoUpload">
            <div class="upload-title">
              <el-icon><VideoCamera /></el-icon>
              <span>添加视频（最大100MB）</span>
            </div>
            <div v-if="newPost.video" class="video-preview">
              <div class="video-player-wrapper">
                <video 
                  :src="newPost.video.url" 
                  controls
                  class="video-preview-player"
                  style="width: 100%; max-height: 300px; border-radius: 8px;"
                >
                  您的浏览器不支持视频播放
                </video>
                <div class="video-info-badge">
                  <span>{{ formatDuration(newPost.video.duration) }}</span>
                </div>
              </div>
              <div class="video-actions">
                <el-button type="danger" size="small" @click="removeVideo" :icon="Delete">
                  删除视频
                </el-button>
              </div>
            </div>
            <div v-else class="video-upload-trigger" @click="triggerVideoUpload">
              <el-icon class="upload-icon"><VideoCamera /></el-icon>
              <span class="upload-text">点击上传视频</span>
              <span class="upload-hint">支持 MP4、AVI、MOV 格式，最大100MB</span>
            </div>
            <input 
              ref="videoInput" 
              type="file" 
              accept="video/*" 
              style="display: none"
              @change="handleVideoUpload"
            />
          </div>

          <!-- 话题标签 -->
          <div class="tags-section">
            <div class="section-title">
              <el-icon><PriceTag /></el-icon>
              <span>添加话题</span>
            </div>
            <div class="tags-input-area">
              <el-tag
                v-for="tag in newPost.tags"
                :key="tag"
                closable
                @close="removeTag(tag)"
                type="danger"
                effect="plain"
                class="tag-item"
              >
                #{{ tag }}
              </el-tag>
              <el-input
                v-if="tagInputVisible"
                ref="tagInput"
                v-model="tagInputValue"
                size="small"
                @keyup.enter="handleTagInputConfirm"
                @blur="handleTagInputConfirm"
                class="tag-input"
                placeholder="输入话题"
                maxlength="10"
              />
              <el-button
                v-else
                size="small"
                @click="showTagInput"
                icon="Plus"
                class="add-tag-btn"
              >
                添加话题
              </el-button>
            </div>
            <div class="hot-tags">
              <span class="hot-tag-label">热门话题：</span>
              <el-tag
                v-for="tag in recommendTags"
                :key="tag"
                size="small"
                @click="addRecommendTag(tag)"
                class="recommend-tag"
                effect="plain"
              >
                #{{ tag }}
              </el-tag>
            </div>
          </div>

          <!-- 位置选择 -->
          <div class="location-section">
            <div class="section-title">
              <el-icon><Location /></el-icon>
              <span>添加位置</span>
            </div>
            <el-select
              v-model="newPost.location"
              placeholder="选择红色景点"
              clearable
              filterable
              class="location-select"
            >
              <el-option
                v-for="loc in redLocations"
                :key="loc"
                :label="loc"
                :value="loc"
              />
            </el-select>
          </div>

          <!-- 分类选择 -->
          <div class="category-section">
            <div class="section-title">
              <el-icon><Folder /></el-icon>
              <span>选择分类</span>
            </div>
            <el-radio-group v-model="newPost.category" class="category-radio">
              <el-radio-button 
                v-for="cat in categories.filter(c => c.key !== 'all')" 
                :key="cat.key" 
                :label="cat.key"
              >
                {{ cat.label }}
              </el-radio-button>
            </el-radio-group>
          </div>
        </div>

        <template #footer>
          <div class="dialog-footer">
            <div class="footer-tools">
              <el-button 
                :icon="showImageUpload ? 'Check' : 'Picture'" 
                circle 
                @click="showImageUpload = !showImageUpload"
                :type="showImageUpload ? 'primary' : 'default'"
              />
              <el-button 
                :icon="showVideoUpload ? 'Check' : 'VideoCamera'" 
                circle 
                @click="showVideoUpload = !showVideoUpload"
                :type="showVideoUpload ? 'primary' : 'default'"
              />
              <span class="tool-tip" v-if="showImageUpload">{{ newPost.images.length }}/9 图片</span>
              <span class="tool-tip" v-if="showVideoUpload && newPost.video">已添加视频</span>
            </div>
            <div class="footer-actions">
              <el-button @click="postDialogVisible = false">取消</el-button>
              <el-button 
                type="primary" 
                @click="submitPost"
                :loading="submitting"
                :disabled="!newPost.content.trim()"
              >
                发布
              </el-button>
            </div>
          </div>
        </template>
      </el-dialog>

      <!-- 评论弹窗 -->
      <el-dialog 
        v-model="commentDialogVisible" 
        :title="`评论 (${currentPost?.comments || 0})`"
        width="600px" 
        align-center
        class="comment-dialog"
      >
        <div class="comment-section">
          <!-- 原动态内容 -->
          <div class="original-post" v-if="currentPost">
            <div class="post-author">
              <el-avatar :size="40" :src="currentPost.userAvatar">
                {{ currentPost.userName?.charAt(0) }}
              </el-avatar>
              <div>
                <div class="author-name">{{ currentPost.userName }}</div>
                <div class="post-time">{{ currentPost.createTime }}</div>
              </div>
            </div>
            <div class="post-content">{{ currentPost.content }}</div>
          </div>

          <!-- 评论列表 -->
          <div class="comments-list">
            <div v-if="loadingComments" class="loading-comments">
              <el-skeleton animated :rows="3" />
            </div>
            <div v-else-if="comments.length === 0" class="no-comments">
              <el-empty description="暂无评论，快来抢沙发吧~" :image-size="100" />
            </div>
            <div v-else>
              <div v-for="comment in comments" :key="comment.id" class="comment-item">
                <el-avatar :size="36" :src="comment.userAvatar">
                  {{ comment.userNickname?.charAt(0) }}
                </el-avatar>
                <div class="comment-content">
                  <div class="comment-author">{{ comment.userNickname }}</div>
                  <div class="comment-text">{{ comment.content }}</div>
                  <div class="comment-meta">
                    <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
                    <span 
                      class="comment-like-btn" 
                      :class="{ active: comment.liked }"
                      @click="toggleCommentLike(comment)"
                    >
                      <el-icon><component :is="comment.liked ? 'StarFilled' : 'Star'" /></el-icon>
                      {{ comment.likesCount || 0 }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 发表评论 -->
          <div class="comment-input-area">
            <el-input
              v-model="newComment"
              type="textarea"
              :rows="3"
              placeholder="说点什么..."
              maxlength="200"
              show-word-limit
            />
            <el-button 
              type="primary" 
              @click="submitComment"
              :disabled="!newComment.trim()"
              style="margin-top: 12px; width: 100%;"
            >
              发表评论
            </el-button>
          </div>
        </div>
      </el-dialog>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { useRouter } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'
import { 
  UserFilled, Picture, VideoCamera, Location, 
  Star, StarFilled, ChatDotRound, Share, More,
  Plus, PriceTag, Close, Folder, VideoPlay, Delete, Search
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import socialApi from '@/api/social'

const router = useRouter()

// 状态
const loading = ref(true)
const activeCategory = ref('all')
const postDialogVisible = ref(false)
const submitting = ref(false)
const showImageUpload = ref(false)
const showVideoUpload = ref(false)
const tagInputVisible = ref(false)
const tagInputValue = ref('')
const tagInput = ref()
const imageInput = ref()
const videoInput = ref()

// 搜索相关
const searchKeyword = ref('')
const sortBy = ref('latest') // latest, hot, comments
const isSearching = ref(false)

// 新帖子数据
const newPost = ref({
  title: '',
  content: '',
  images: [] as string[],
  video: null as { url: string; thumbnail: string; duration: number } | null,
  tags: [] as string[],
  location: '',
  category: 'travel'
})

// 推荐话题
const recommendTags = ['西柏坡', '红色旅游', '党史学习', '志愿服务', '不忘初心']

// 红色景点列表
const redLocations = [
  '西柏坡纪念馆',
  '河北省博物馆',
  '冉庄地道战遗址',
  '狼牙山五壮士纪念馆',
  '李大钊纪念馆',
  '华北军区烈士陵园',
  '晋察冀边区革命纪念馆',
  '前南峪抗日军政大学陈列馆'
]

// 分类 - 河北红色文化相关分类
const categories = [
  { key: 'all', label: '推荐' },
  { key: 'travel', label: '旅游打卡' },
  { key: 'study', label: '学习心得' },
  { key: 'history', label: '党史故事' },
  { key: 'activity', label: '志愿活动' },
  { key: 'heritage', label: '红色遗址' },
  { key: 'hero', label: '英雄人物' },
  { key: 'spirit', label: '革命精神' },
  { key: 'memory', label: '红色记忆' },
  { key: 'education', label: '党建学习' }
]

// 模拟数据
const hotTopics = ref<any[]>([])
const activeUsers = ref<any[]>([])
const posts = ref<any[]>([])

// 用户信息缓存
const userCache = new Map<number, any>()

// 获取用户信息
const getUserInfo = async (userId: number) => {
  // 检查缓存
  if (userCache.has(userId)) {
    return userCache.get(userId)
  }
  
  try {
    const response = await fetch(`/api/users/${userId}`, {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    
    if (response.ok) {
      const result = await response.json()
      const userInfo = result.data || result
      // 缓存用户信息
      userCache.set(userId, userInfo)
      return userInfo
    }
  } catch (error) {
    console.error(`获取用户${userId}信息失败:`, error)
  }
  
  // 返回默认信息
  return {
    id: userId,
    username: `用户${userId}`,
    nickname: `用户${userId}`,
    avatar: ''
  }
}

// 加载动态列表
const loadPosts = async () => {
  loading.value = true
  try {
    const response = await socialApi.getPosts({
      category: activeCategory.value === 'all' ? undefined : activeCategory.value,
      keyword: searchKeyword.value.trim() || undefined,
      sortBy: sortBy.value === 'latest' ? undefined : sortBy.value,
      page: 1,
      size: 20
    })
    
    console.log('加载动态成功:', response)
    
    // 转换后端数据格式为前端格式
    // 注意：axios 拦截器已经提取了 response.data.data，所以 response 直接就是数组
    if (response && Array.isArray(response)) {
      // 先转换基本数据
      const postsData = response.map((post: any) => ({
        id: post.id,
        userId: post.userId,
        userName: post.userNickname || '用户' + post.userId,
        userAvatar: post.userAvatar || '',
        isVip: false,
        createTime: formatTime(post.createdAt),
        location: post.location || '未知地点',
        title: post.title || '',
        content: post.content,
        images: post.images || [],
        video: post.video || null,
        tags: [],
        category: post.category || 'travel',
        likes: post.likesCount || 0,
        comments: post.commentsCount || 0,
        shares: post.sharesCount || 0,
        isLiked: post.liked || false
      }))
      
      posts.value = postsData
      
      // 异步加载用户信息
      postsData.forEach(async (post) => {
        const userInfo = await getUserInfo(post.userId)
        post.userName = userInfo.nickname || userInfo.username || '用户' + post.userId
        post.userAvatar = userInfo.avatar || ''
      })
    } else {
      // 如果后端返回空数据，使用模拟数据
      console.warn('后端返回的数据格式不正确:', response)
      posts.value = generatePosts()
    }
  } catch (error) {
    console.error('加载动态失败:', error)
    // 加载失败时使用模拟数据
    posts.value = generatePosts()
  } finally {
    loading.value = false
  }
}

// 格式化时间
const formatTime = (dateStr: string) => {
  if (!dateStr) return '刚刚'
  
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  
  return date.toLocaleDateString('zh-CN')
}

// 加载热门话题
const loadHotTopics = async () => {
  try {
    const response = await socialApi.getHotTopics()
    if (response && Array.isArray(response) && response.length > 0) {
      hotTopics.value = response
    } else {
      // 使用默认数据
      hotTopics.value = [
        { name: '建党百年', hot: '12.5w' },
        { name: '西柏坡精神', hot: '8.2w' },
        { name: '红色旅游打卡', hot: '5.6w' },
        { name: '重走长征路', hot: '3.4w' },
        { name: '我的入党故事', hot: '2.1w' }
      ]
    }
  } catch (error) {
    console.error('加载热门话题失败:', error)
    // 使用默认数据
    hotTopics.value = [
      { name: '建党百年', hot: '12.5w' },
      { name: '西柏坡精神', hot: '8.2w' },
      { name: '红色旅游打卡', hot: '5.6w' },
      { name: '重走长征路', hot: '3.4w' },
      { name: '我的入党故事', hot: '2.1w' }
    ]
  }
}

// 加载活跃用户
const loadActiveUsers = async () => {
  try {
    const response = await socialApi.getActiveUsers()
    if (response && Array.isArray(response) && response.length > 0) {
      // 加载用户详细信息
      const usersWithInfo = await Promise.all(
        response.map(async (user: any) => {
          const userInfo = await getUserInfo(user.userId || user.id)
          return {
            id: user.userId || user.id,
            name: userInfo.nickname || userInfo.username || '用户' + user.id,
            desc: user.desc || user.description || '活跃用户',
            avatar: userInfo.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
          }
        })
      )
      activeUsers.value = usersWithInfo
    } else {
      // 使用默认数据
      activeUsers.value = [
        { id: 1, name: '红色追梦人', desc: '分享红色摄影作品', avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png' },
        { id: 2, name: '党史研究员', desc: '专注党史科普', avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png' },
        { id: 3, name: '志愿者小李', desc: '记录志愿服务日常', avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png' }
      ]
    }
  } catch (error) {
    console.error('加载活跃用户失败:', error)
    // 使用默认数据
    activeUsers.value = [
      { id: 1, name: '红色追梦人', desc: '分享红色摄影作品', avatar: '' },
      { id: 2, name: '党史研究员', desc: '专注党史科普', avatar: '' },
      { id: 3, name: '志愿者小李', desc: '记录志愿服务日常', avatar: '' }
    ]
  }
}

// 生成模拟帖子
const generatePosts = () => {
  const list = [
    {
      id: 1,
      userName: '张建国',
      userAvatar: '',
      isVip: true,
      createTime: '2小时前',
      location: '西柏坡纪念馆',
      content: '今天再次来到西柏坡，参观了中共中央旧址。看着那些简朴的陈设，更能体会到老一辈革命家艰苦奋斗的精神。"两个务必"的教导依然振聋发聩。',
      images: [
        'https://images.unsplash.com/photo-1599526725208-a9c6833777d9?q=80&w=2670&auto=format&fit=crop',
        'https://images.unsplash.com/photo-1599526724673-863f69b56350?q=80&w=2670&auto=format&fit=crop'
      ],
      tags: ['西柏坡', '红色精神', '不忘初心'],
      category: 'travel',
      likes: 342,
      comments: 56,
      shares: 28,
      isLiked: false
    },
    {
      id: 2,
      userName: '李小红',
      userAvatar: '',
      isVip: false,
      createTime: '4小时前',
      location: '河北省博物馆',
      content: '周末带孩子来参观《抗日烽火》展览，让下一代了解历史，铭记先烈。孩子们听得很认真，这种现场教育比书本更生动。',
      images: [
        'https://images.unsplash.com/photo-1580130601254-05fa235bcabd?q=80&w=2669&auto=format&fit=crop'
      ],
      tags: ['亲子教育', '红色研学'],
      category: 'study',
      likes: 215,
      comments: 32,
      shares: 15,
      isLiked: true
    },
    {
      id: 3,
      userName: '老党员老王',
      userAvatar: '',
      isVip: true,
      createTime: '昨天',
      location: '社区党群服务中心',
      title: '关于加强社区红色文化宣传的几点建议',
      content: '最近在社区组织了几次红色故事分享会，发现大家参与热情很高。建议可以结合传统节日，多举办一些形式多样的文化活动...',
      images: [],
      tags: ['社区建设', '建言献策'],
      category: 'activity',
      likes: 568,
      comments: 124,
      shares: 89,
      isLiked: false
    }
  ]
  return list
}

onMounted(() => {
  loadPosts()
  loadHotTopics()
  loadActiveUsers()
})

// 监听分类变化，重新加载数据
watch(activeCategory, () => {
  loadPosts()
})

const filteredPosts = computed(() => {
  if (activeCategory.value === 'all') return posts.value
  return posts.value.filter(p => p.category === activeCategory.value)
})

// 搜索方法
const handleSearch = async () => {
  isSearching.value = true
  await loadPosts()
  isSearching.value = false
}

const handleClearSearch = () => {
  searchKeyword.value = ''
  loadPosts()
}

const handleSortChange = () => {
  loadPosts()
}

// 操作方法
const openPostDialog = () => {
  postDialogVisible.value = true
  // 重置表单
  newPost.value = {
    title: '',
    content: '',
    images: [],
    video: null,
    tags: [],
    location: '',
    category: 'travel'
  }
  showImageUpload.value = false
  showVideoUpload.value = false
}

const submitPost = async () => {
  if (!newPost.value.content.trim()) {
    ElMessage.warning('请输入内容')
    return
  }
  
  submitting.value = true
  
  try {
    console.log('准备发布动态:', newPost.value)
    
    // 调用后端API发布动态
    const response = await socialApi.createPost({
      title: newPost.value.title,
      content: newPost.value.content,
      images: newPost.value.images,
      video: newPost.value.video,
      tags: newPost.value.tags,
      location: newPost.value.location,
      category: newPost.value.category
    })
    
    console.log('发布成功:', response)
    ElMessage.success('发布成功！')
    postDialogVisible.value = false
    
    // 重新加载动态列表以显示新发布的内容
    await loadPosts()
    
  } catch (error: any) {
    console.error('发布失败:', error)
    console.error('错误详情:', error.response)
    console.error('错误数据:', error.response?.data)
    
    if (error.response?.status === 401) {
      ElMessage.error('请先登录')
      router.push('/login')
    } else {
      const errorMsg = error.response?.data?.message || error.message || '发布失败，请稍后重试'
      ElMessage.error(errorMsg)
    }
  } finally {
    submitting.value = false
  }
}

// 图片上传
const triggerImageUpload = () => {
  imageInput.value?.click()
}

const handleImageUpload = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const files = target.files
  if (!files || files.length === 0) return
  
  const remainingSlots = 9 - newPost.value.images.length
  const filesToProcess = Array.from(files).slice(0, remainingSlots)
  
  // 显示上传进度
  ElMessage.info(`正在上传 ${filesToProcess.length} 张图片...`)
  
  try {
    // 批量上传图片
    const response = await socialApi.uploadImages(filesToProcess)
    
    if (response && Array.isArray(response)) {
      // 添加上传成功的图片URL
      response.forEach((item: any) => {
        if (item.url) {
          newPost.value.images.push(item.url)
        }
      })
      
      ElMessage.success(`成功上传 ${response.length} 张图片`)
    }
  } catch (error) {
    console.error('图片上传失败:', error)
    ElMessage.error('图片上传失败，请重试')
  }
  
  // 重置input
  target.value = ''
}

const removeImage = (index: number) => {
  newPost.value.images.splice(index, 1)
}

// 标签管理
const showTagInput = () => {
  tagInputVisible.value = true
  nextTick(() => {
    tagInput.value?.focus()
  })
}

const handleTagInputConfirm = () => {
  const value = tagInputValue.value.trim()
  if (value && !newPost.value.tags.includes(value) && newPost.value.tags.length < 5) {
    newPost.value.tags.push(value)
  }
  tagInputVisible.value = false
  tagInputValue.value = ''
}

const removeTag = (tag: string) => {
  const index = newPost.value.tags.indexOf(tag)
  if (index > -1) {
    newPost.value.tags.splice(index, 1)
  }
}

const addRecommendTag = (tag: string) => {
  if (!newPost.value.tags.includes(tag) && newPost.value.tags.length < 5) {
    newPost.value.tags.push(tag)
  }
}

// 视频上传
const triggerVideoUpload = () => {
  videoInput.value?.click()
}

const handleVideoUpload = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return
  
  // 检查文件类型
  if (!file.type.startsWith('video/')) {
    ElMessage.error('请选择视频文件')
    return
  }
  
  // 检查文件大小（限制100MB）
  const maxSize = 100 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.error('视频文件不能超过100MB')
    return
  }
  
  // 显示上传进度
  ElMessage.info('正在上传视频，请稍候...')
  
  try {
    // 获取视频时长
    const duration = await getVideoDuration(file)
    console.log('视频时长:', duration, '秒')
    
    // 上传视频到服务器
    const response = await socialApi.uploadVideo(file)
    
    if (response && response.url) {
      newPost.value.video = {
        url: response.url,
        thumbnail: response.thumbnail || response.url,
        duration: duration  // 使用实际获取的时长
      }
      
      ElMessage.success('视频上传成功')
    }
  } catch (error) {
    console.error('视频上传失败:', error)
    ElMessage.error('视频上传失败，请重试')
  }
  
  // 重置input
  target.value = ''
}

// 获取视频时长
const getVideoDuration = (file: File): Promise<number> => {
  return new Promise((resolve, reject) => {
    const video = document.createElement('video')
    video.preload = 'metadata'
    
    video.onloadedmetadata = () => {
      window.URL.revokeObjectURL(video.src)
      const duration = Math.floor(video.duration)
      resolve(duration)
    }
    
    video.onerror = () => {
      reject(new Error('无法读取视频信息'))
    }
    
    video.src = URL.createObjectURL(file)
  })
}

const removeVideo = () => {
  newPost.value.video = null
}

// 格式化视频时长
const formatDuration = (seconds: number) => {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

// 格式化点赞数量
const formatLikeCount = (count: number) => {
  if (count >= 10000) {
    return (count / 10000).toFixed(1) + 'w'
  } else if (count >= 1000) {
    return (count / 1000).toFixed(1) + 'k'
  }
  return count.toString()
}

// 点赞操作锁，防止重复点击
const likingPosts = new Set<number>()

const toggleLike = async (post: any) => {
  // 检查是否登录
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录后再点赞')
    router.push('/login')
    return
  }
  
  // 防止重复点击
  if (likingPosts.has(post.id)) {
    return
  }
  
  likingPosts.add(post.id)
  
  // 乐观更新UI
  const wasLiked = post.isLiked
  const previousLikes = post.likes
  
  try {
    if (wasLiked) {
      // 取消点赞
      post.isLiked = false
      post.likes = Math.max(0, post.likes - 1)
      
      await socialApi.unlikePost(post.id)
    } else {
      // 点赞 - 触发动画
      post.isLiked = true
      post.likes += 1
      post.likeAnimating = true
      post.showParticles = true
      
      // 动画结束后清除状态
      setTimeout(() => {
        post.likeAnimating = false
      }, 600)
      setTimeout(() => {
        post.showParticles = false
      }, 800)
      
      await socialApi.likePost(post.id)
    }
  } catch (error: any) {
    console.error('点赞操作失败:', error)
    
    // 回滚UI状态
    post.isLiked = wasLiked
    post.likes = previousLikes
    post.likeAnimating = false
    post.showParticles = false
    
    // 检查错误类型
    const errorMessage = error.message || ''
    const isTokenError = errorMessage.includes('Token') || 
                         errorMessage.includes('登录') ||
                         error.response?.status === 401
    
    if (isTokenError) {
      // Token 无效或过期，清除登录状态
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('userInfo')
      ElMessage.error('登录已过期，请重新登录')
      router.push('/login')
    } else if (errorMessage.includes('已经点赞')) {
      // 已经点赞过，同步状态
      post.isLiked = true
      post.likes = previousLikes
    } else {
      ElMessage.error('操作失败，请稍后重试')
    }
  } finally {
    likingPosts.delete(post.id)
  }
}

// 双击点赞功能
const handleDoubleTapLike = async (post: any, event: MouseEvent) => {
  // 如果已经点赞，不重复操作
  if (post.isLiked) {
    // 只显示动画
    showHeartAnimation(post, event)
    return
  }
  
  // 显示动画
  showHeartAnimation(post, event)
  
  // 执行点赞
  await toggleLike(post)
}

// 显示心形动画
const showHeartAnimation = (post: any, event: MouseEvent) => {
  post.showHeartAnimation = true
  
  // 动画结束后隐藏
  setTimeout(() => {
    post.showHeartAnimation = false
  }, 800)
}

// 评论相关状态
const commentDialogVisible = ref(false)
const currentPost = ref<any>(null)
const comments = ref<any[]>([])
const newComment = ref('')
const loadingComments = ref(false)

const showComments = async (post: any) => {
  currentPost.value = post
  commentDialogVisible.value = true
  await loadComments(post.id)
}

const loadComments = async (postId: number) => {
  loadingComments.value = true
  try {
    const response = await socialApi.getComments(postId)
    const commentsData = response || []
    
    // 加载评论用户信息
    const commentsWithUserInfo = await Promise.all(
      commentsData.map(async (comment: any) => {
        const userInfo = await getUserInfo(comment.userId)
        return {
          ...comment,
          userNickname: userInfo.nickname || userInfo.username || '用户' + comment.userId,
          userAvatar: userInfo.avatar || ''
        }
      })
    )
    
    comments.value = commentsWithUserInfo
  } catch (error) {
    console.error('加载评论失败:', error)
    comments.value = []
  } finally {
    loadingComments.value = false
  }
}

const submitComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  
  try {
    await socialApi.createComment(currentPost.value.id, newComment.value)
    ElMessage.success('评论成功')
    newComment.value = ''
    // 重新加载评论
    await loadComments(currentPost.value.id)
    // 更新动态的评论数
    currentPost.value.comments += 1
  } catch (error: any) {
    console.error('评论失败:', error)
    if (error.response?.status === 401) {
      ElMessage.error('请先登录')
      router.push('/login')
    } else {
      ElMessage.error('评论失败，请稍后重试')
    }
  }
}

// 评论点赞操作锁
const likingComments = new Set<number>()

const toggleCommentLike = async (comment: any) => {
  // 检查是否登录
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录后再点赞')
    router.push('/login')
    return
  }
  
  // 防止重复点击
  if (likingComments.has(comment.id)) {
    return
  }
  
  likingComments.add(comment.id)
  
  // 乐观更新UI
  const wasLiked = comment.liked
  const previousLikes = comment.likesCount || 0
  
  try {
    if (wasLiked) {
      // 取消点赞
      comment.liked = false
      comment.likesCount = Math.max(0, previousLikes - 1)
      await socialApi.unlikeComment(currentPost.value.id, comment.id)
    } else {
      // 点赞
      comment.liked = true
      comment.likesCount = previousLikes + 1
      await socialApi.likeComment(currentPost.value.id, comment.id)
    }
  } catch (error: any) {
    console.error('评论点赞操作失败:', error)
    
    // 回滚UI状态
    comment.liked = wasLiked
    comment.likesCount = previousLikes
    
    if (error.response?.status === 401) {
      ElMessage.error('请先登录')
      router.push('/login')
    } else if (error.response?.status === 400 && error.response?.data?.message?.includes('已经点赞')) {
      // 已经点赞过，同步状态
      comment.liked = true
    } else {
      ElMessage.error('操作失败，请稍后重试')
    }
  } finally {
    likingComments.delete(comment.id)
  }
}

const sharePost = (post: any) => {
  ElMessage.success('分享链接已复制到剪贴板')
}

const previewImage = (img: string) => {
  // 实际可以使用 el-image-viewer
  console.log('Preview', img)
}

// 关注用户
const handleFollowUser = async (user: any) => {
  try {
    await socialApi.followUser(user.id)
    ElMessage.success(`已关注 ${user.name}`)
    // 可以更新用户状态
  } catch (error) {
    console.error('关注失败:', error)
    ElMessage.error('关注失败，请稍后重试')
  }
}
</script>

<style scoped>
.social-page {
  background-color: #f5f7fa;
  min-height: 100vh;
  background-image: radial-gradient(rgba(160, 24, 47, 0.05) 1px, transparent 1px), radial-gradient(rgba(160, 24, 47, 0.05) 1px, #f5f7fa 1px);
  background-size: 20px 20px;
  background-position: 0 0, 10px 10px;
}

/* Hero Section */
.social-hero {
  height: 300px;
  background: linear-gradient(135deg, #a0182f 0%, #c41e3a 50%, #8b1e3f 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-bottom: 40px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(160, 24, 47, 0.2);
}

.social-hero::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.1'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
  opacity: 0.6;
}

.hero-content {
  text-align: center;
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 900px;
  padding: 0 20px;
}

.hero-text h1 {
  font-size: 3.2rem;
  margin-bottom: 16px;
  font-weight: 800;
  letter-spacing: 2px;
  text-shadow: 0 4px 16px rgba(0,0,0,0.3);
}

.hero-text p {
  font-size: 1.3rem;
  opacity: 0.95;
  margin-bottom: 40px;
  font-weight: 300;
  letter-spacing: 1px;
}

.hero-stats {
  display: inline-flex;
  justify-content: center;
  gap: 80px;
  background: rgba(255,255,255,0.15);
  backdrop-filter: blur(8px);
  padding: 24px 60px;
  border-radius: 20px;
  border: 1px solid rgba(255,255,255,0.2);
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
  transition: transform 0.3s;
}

.hero-stats:hover {
  transform: translateY(-5px);
  background: rgba(255,255,255,0.2);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-item .count {
  font-size: 2.2rem;
  font-weight: 700;
  margin-bottom: 8px;
  text-shadow: 0 2px 8px rgba(0,0,0,0.2);
}

.stat-item .label {
  font-size: 1rem;
  opacity: 0.9;
  letter-spacing: 1px;
  text-transform: uppercase;
}

/* Container */
.social-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px 60px;
  display: flex;
  gap: 32px;
}

/* Main Content */
.social-main {
  flex: 1;
  min-width: 0;
}

/* Create Post Card */
.create-post-card {
  background: linear-gradient(135deg, #ffffff 0%, #fff5f7 100%);
  border-radius: 24px;
  padding: 28px 36px;
  margin-bottom: 32px;
  box-shadow: 0 8px 32px rgba(160, 24, 47, 0.08);
  border: 2px solid rgba(196, 30, 58, 0.08);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.create-post-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #c41e3a, #ff6b6b, #c41e3a);
  background-size: 200% 100%;
  animation: shimmer 3s linear infinite;
}

@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

.create-post-card:hover {
  box-shadow: 0 16px 48px rgba(160, 24, 47, 0.15);
  transform: translateY(-4px);
  border-color: rgba(196, 30, 58, 0.15);
}

.input-area {
  display: flex;
  gap: 20px;
  align-items: center;
  margin-bottom: 24px;
  cursor: text;
}

.user-avatar {
  border: 3px solid rgba(196, 30, 58, 0.1);
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.fake-input {
  flex: 1;
  background: white;
  padding: 18px 28px;
  border-radius: 50px;
  color: #999;
  font-size: 1.05rem;
  transition: all 0.3s;
  border: 2px solid rgba(196, 30, 58, 0.08);
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.input-area:hover .fake-input {
  background: white;
  border-color: rgba(196, 30, 58, 0.2);
  color: #666;
  box-shadow: 0 4px 16px rgba(196, 30, 58, 0.1);
  transform: translateX(4px);
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 24px;
  border-top: 2px solid rgba(196, 30, 58, 0.06);
  gap: 12px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #666;
  cursor: pointer;
  font-size: 1rem;
  padding: 12px 20px;
  border-radius: 16px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: 600;
  background: white;
  border: 2px solid transparent;
  position: relative;
  overflow: hidden;
}

.action-btn::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(196, 30, 58, 0.1);
  transform: translate(-50%, -50%);
  transition: width 0.4s, height 0.4s;
}

.action-btn:hover::before {
  width: 200%;
  height: 200%;
}

.action-btn:hover {
  background: linear-gradient(135deg, #fff0f0, #ffe8e8);
  color: #c41e3a;
  transform: translateY(-2px);
  border-color: rgba(196, 30, 58, 0.15);
  box-shadow: 0 6px 20px rgba(196, 30, 58, 0.15);
}

.action-btn .el-icon {
  font-size: 1.3rem;
  position: relative;
  z-index: 1;
}

.action-btn span {
  position: relative;
  z-index: 1;
}

.btn-publish {
  background: linear-gradient(135deg, #c41e3a 0%, #8b1e3f 100%);
  border: none;
  padding: 12px 40px;
  font-weight: 700;
  font-size: 1.05rem;
  box-shadow: 0 6px 20px rgba(196, 30, 58, 0.35);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  letter-spacing: 0.5px;
  position: relative;
  overflow: hidden;
}

.btn-publish::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
  transition: left 0.5s;
}

.btn-publish:hover::before {
  left: 100%;
}

.btn-publish:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 8px 28px rgba(196, 30, 58, 0.45);
  background: linear-gradient(135deg, #d41e3a 0%, #9b1e3f 100%);
}

/* Search and Filter Section */
.search-filter-section {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  padding: 16px 0;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.search-box {
  flex: 1;
  min-width: 280px;
  max-width: 500px;
}

.search-input {
  :deep(.el-input__wrapper) {
    border-radius: 24px;
    box-shadow: 0 2px 8px rgba(160, 24, 47, 0.08);
    border: 1px solid #f0d0d5;
    transition: all 0.3s;
    
    &:hover, &:focus-within {
      border-color: #a0182f;
      box-shadow: 0 4px 12px rgba(160, 24, 47, 0.15);
    }
  }
  
  :deep(.el-input-group__append) {
    border-radius: 0 24px 24px 0;
    background: linear-gradient(135deg, #a0182f 0%, #c41e3a 100%);
    border: none;
    
    .el-button {
      color: white;
      font-weight: 600;
      
      &:hover {
        background: transparent;
      }
    }
  }
}

.sort-options {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.sort-label {
  font-size: 0.9rem;
  color: #666;
  white-space: nowrap;
}

.sort-options :deep(.el-radio-button__inner) {
  border-color: #f0d0d5;
  color: #a0182f;
  
  &:hover {
    color: #c41e3a;
  }
}

.sort-options :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: linear-gradient(135deg, #a0182f 0%, #c41e3a 100%);
  border-color: #a0182f;
  box-shadow: -1px 0 0 0 #a0182f;
}

/* Category Tabs */
.category-tabs {
  display: flex;
  gap: 0.6rem;
  padding: 0 0 20px;
  background: transparent;
  border-radius: 0;
  border-bottom: none;
  box-shadow: none;
  flex-wrap: wrap;
}

.category-tab {
  padding: 8px 16px;
  cursor: pointer;
  font-weight: 600;
  color: #a0182f;
  background: #fff5f5;
  border-radius: 20px;
  transition: all 0.3s;
  font-size: 0.85rem;
  border: 1px solid rgba(160, 24, 47, 0.1);
}

.category-tab:hover {
  background: #ffe6e6;
  color: #8b1e3f;
  transform: translateY(-2px);
  border-color: rgba(160, 24, 47, 0.3);
  box-shadow: 0 4px 12px rgba(160, 24, 47, 0.1);
}

.category-tab.active {
  background: linear-gradient(135deg, #a0182f, #c41e3a);
  color: white;
  box-shadow: 0 6px 16px rgba(160, 24, 47, 0.3);
  transform: scale(1.05);
  border-color: transparent;
}

.category-tab.active::after {
  display: none;
}

/* Post Card */
.posts-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.post-card {
  background: white;
  padding: 32px;
  border-radius: 0 0 20px 20px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  transition: all 0.3s;
  border: 1px solid transparent;
  position: relative;
  overflow: hidden;
}

/* 双击点赞动画 */
.double-tap-heart {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 100;
  pointer-events: none;
}

.double-tap-heart .el-icon {
  font-size: 80px;
  color: #c41e3a;
  filter: drop-shadow(0 4px 12px rgba(196, 30, 58, 0.4));
  animation: heartPopAnimation 0.8s ease-out forwards;
}

@keyframes heartPopAnimation {
  0% {
    transform: scale(0);
    opacity: 0;
  }
  15% {
    transform: scale(1.2);
    opacity: 1;
  }
  30% {
    transform: scale(0.95);
  }
  45% {
    transform: scale(1.1);
  }
  60% {
    transform: scale(1);
    opacity: 1;
  }
  100% {
    transform: scale(1);
    opacity: 0;
  }
}

.heart-pop-enter-active {
  animation: heartPopAnimation 0.8s ease-out;
}

.heart-pop-leave-active {
  animation: heartPopAnimation 0.8s ease-out reverse;
}

.category-tabs + .posts-list .post-card:first-child {
  border-top-left-radius: 0;
  border-top-right-radius: 0;
  border-top: none;
}

.post-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0,0,0,0.08);
}

.post-header {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.post-meta {
  flex: 1;
}

.user-name {
  font-weight: 700;
  font-size: 1.1rem;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 6px;
}

.vip-tag {
  border-color: transparent;
  background: linear-gradient(135deg, #c41e3a, #ff6b6b);
  color: white;
  font-weight: 600;
  box-shadow: 0 2px 6px rgba(196, 30, 58, 0.2);
}

.post-time {
  font-size: 0.9rem;
  color: #999;
}

.post-content {
  margin-bottom: 24px;
  padding-left: 68px;
}

.post-title {
  font-size: 1.3rem;
  font-weight: 800;
  margin-bottom: 12px;
  color: #1a1a1a;
  line-height: 1.4;
}

.post-text {
  font-size: 1.1rem;
  line-height: 1.8;
  color: #4a4a4a;
  margin-bottom: 20px;
  white-space: pre-wrap;
}

.post-images {
  display: grid;
  gap: 12px;
  margin-bottom: 20px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

.grid-1 { grid-template-columns: 1fr; }
.grid-2 { grid-template-columns: 1fr 1fr; }
.grid-3 { grid-template-columns: 1fr 1fr 1fr; }

.post-image {
  aspect-ratio: 16/9;
  background-size: cover;
  background-position: center;
  cursor: pointer;
  transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.post-image:hover {
  transform: scale(1.05);
}

.post-tags {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  margin-top: 12px;
}

.hashtag {
  border-radius: 6px;
  background-color: #fff5f5;
  border: 1px solid rgba(160, 24, 47, 0.1);
  color: #a0182f;
  padding: 4px 10px;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: all 0.3s ease;
  font-size: 0.9rem;
  cursor: pointer;
  font-weight: 500;
}

.hashtag:hover {
  background-color: #ffe6e6;
  border-color: rgba(160, 24, 47, 0.3);
  transform: translateY(-1px);
  color: #8b1e3f;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  border-top: 1px solid #f0f0f0;
  padding-top: 20px;
  margin-left: 68px;
}

.interaction-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 12px 16px;
  border-radius: 12px;
  font-weight: 500;
  position: relative;
  overflow: hidden;
}

.interaction-btn::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(196, 30, 58, 0.08);
  transform: translate(-50%, -50%);
  transition: width 0.4s, height 0.4s;
  z-index: 0;
}

.interaction-btn:hover::before {
  width: 200%;
  height: 200%;
}

.interaction-btn:hover {
  background: rgba(196, 30, 58, 0.05);
  color: #c41e3a;
  transform: translateY(-2px);
}

.interaction-btn .el-icon {
  font-size: 1.3rem;
  position: relative;
  z-index: 1;
  transition: transform 0.3s;
}

.interaction-btn:hover .el-icon {
  transform: scale(1.15);
}

.interaction-btn span {
  position: relative;
  z-index: 1;
  font-size: 0.95rem;
}

.interaction-btn.active {
  color: #c41e3a;
  background: rgba(196, 30, 58, 0.08);
}

.interaction-btn.active .el-icon {
  animation: likeAnimation 0.5s ease;
}

/* 点赞按钮增强样式 */
.like-btn {
  position: relative;
}

.like-icon-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.like-icon {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.like-btn.active .like-icon {
  color: #c41e3a;
  filter: drop-shadow(0 0 4px rgba(196, 30, 58, 0.4));
}

.like-btn.like-animating .like-icon {
  animation: heartBeat 0.6s ease-in-out;
}

/* 心跳动画 */
@keyframes heartBeat {
  0% { transform: scale(1); }
  15% { transform: scale(1.3); }
  30% { transform: scale(1); }
  45% { transform: scale(1.2); }
  60% { transform: scale(1); }
}

/* 点赞粒子效果 */
.like-particles {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  pointer-events: none;
}

.particle {
  position: absolute;
  width: 6px;
  height: 6px;
  background: #c41e3a;
  border-radius: 50%;
  animation: particleExplode 0.8s ease-out forwards;
  opacity: 0;
}

.particle:nth-child(1) { --angle: 0deg; }
.particle:nth-child(2) { --angle: 60deg; }
.particle:nth-child(3) { --angle: 120deg; }
.particle:nth-child(4) { --angle: 180deg; }
.particle:nth-child(5) { --angle: 240deg; }
.particle:nth-child(6) { --angle: 300deg; }

@keyframes particleExplode {
  0% {
    opacity: 1;
    transform: translate(-50%, -50%) rotate(var(--angle)) translateY(0) scale(1);
  }
  100% {
    opacity: 0;
    transform: translate(-50%, -50%) rotate(var(--angle)) translateY(-25px) scale(0);
  }
}

/* 点赞数量变化动画 */
.like-count {
  display: inline-block;
  min-width: 20px;
  text-align: center;
}

.like-count-enter-active,
.like-count-leave-active {
  transition: all 0.25s ease;
}

.like-count-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.like-count-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

@keyframes likeAnimation {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.3);
  }
}

.load-more {
  text-align: center;
  padding: 30px 0;
}

/* Sidebar */
.social-sidebar {
  width: 340px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.sidebar-card {
  background: white;
  border-radius: 20px;
  padding: 28px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.05);
  transition: all 0.3s;
  border: 1px solid #f8f9fa;
}

.sidebar-card:hover {
  box-shadow: 0 12px 28px rgba(0,0,0,0.08);
  transform: translateY(-4px);
}

.card-title {
  font-size: 1.25rem;
  font-weight: 700;
  margin-bottom: 24px;
  color: #2c3e50;
  padding-bottom: 16px;
  border-bottom: 2px solid #f0f0f0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.topic-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.topic-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.2s;
  border-radius: 12px;
  margin-bottom: 4px;
}

.topic-item:hover {
  background: rgba(160, 24, 47, 0.03);
  transform: translateX(4px);
}

.topic-item:hover .topic-name {
  color: #c41e3a;
}

.topic-rank {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  color: #999;
  margin-right: 16px;
  border-radius: 8px;
  background: #f5f7fa;
  font-size: 0.95rem;
}

.topic-item:nth-child(1) .topic-rank {
  background: linear-gradient(135deg, #ff4d4f, #f5222d);
  color: white;
  box-shadow: 0 4px 10px rgba(245, 34, 45, 0.3);
}
.topic-item:nth-child(2) .topic-rank {
  background: linear-gradient(135deg, #ff7875, #ff4d4f);
  color: white;
  box-shadow: 0 4px 10px rgba(255, 77, 79, 0.3);
}
.topic-item:nth-child(3) .topic-rank {
  background: linear-gradient(135deg, #ffccc7, #ff7875);
  color: #cf1322;
}

.topic-name {
  flex: 1;
  color: #444;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: 600;
  font-size: 1rem;
}

.topic-hot {
  font-size: 0.9rem;
  color: #999;
  font-family: 'Roboto Mono', monospace;
  font-weight: 500;
}

.active-users {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.active-user {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px;
  border-radius: 12px;
  transition: all 0.2s;
}

.active-user:hover {
  background: #f9fafc;
}

.user-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-detail .name {
  font-weight: 700;
  font-size: 1.05rem;
  color: #333;
}

.user-detail .desc {
  font-size: 0.85rem;
  color: #999;
  line-height: 1.4;
}

.announcement {
  background: linear-gradient(135deg, #fff 0%, #fff5f5 100%);
  border: 1px solid rgba(196, 30, 58, 0.2);
  position: relative;
  overflow: hidden;
}

.announcement::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 100px;
  height: 100px;
  background: radial-gradient(circle, rgba(196, 30, 58, 0.1) 0%, transparent 70%);
  transform: translate(30%, -30%);
}

.announcement p {
  font-size: 1rem;
  color: #555;
  line-height: 1.7;
  margin-bottom: 20px;
  position: relative;
  z-index: 1;
}

/* Responsive */
@media (max-width: 992px) {
  .social-container {
    flex-direction: column;
  }
  
  .social-sidebar {
    width: 100%;
  }

  .post-content, .post-footer {
    padding-left: 0;
  }
}

@media (max-width: 768px) {
  .social-hero {
    height: auto;
    padding: 40px 0;
  }

  .hero-stats {
    gap: 24px;
    padding: 16px;
    flex-wrap: wrap;
    width: 100%;
    box-sizing: border-box;
  }
  
  .stat-item {
    min-width: 80px;
  }
  
  .stat-item .count {
    font-size: 1.4rem;
  }
  
  .post-images.grid-3 {
    grid-template-columns: 1fr 1fr;
  }

  .search-filter-section {
    flex-direction: column;
    align-items: stretch;
    gap: 1rem;
  }
  
  .search-box {
    max-width: 100%;
  }
  
  .sort-options {
    justify-content: center;
  }

  .category-tabs {
    flex-wrap: nowrap;
    overflow-x: auto;
    padding-bottom: 12px;
    -webkit-overflow-scrolling: touch;
    margin: 0 -16px 24px;
    padding-left: 16px;
    padding-right: 16px;
  }

  .category-tab {
    flex-shrink: 0;
    white-space: nowrap;
  }

  .social-container {
    padding: 0 16px 60px;
  }
}

/* Post Dialog Styles */
:deep(.post-dialog) {
  border-radius: 20px;
  overflow: hidden;
}

:deep(.post-dialog .el-dialog__header) {
  background: linear-gradient(135deg, #a0182f, #c41e3a);
  color: white;
  padding: 24px 32px;
  margin: 0;
}

:deep(.post-dialog .el-dialog__title) {
  color: white;
  font-size: 1.3rem;
  font-weight: 700;
}

:deep(.post-dialog .el-dialog__headerbtn .el-dialog__close) {
  color: white;
  font-size: 20px;
}

:deep(.post-dialog .el-dialog__body) {
  padding: 32px;
  max-height: 70vh;
  overflow-y: auto;
}

.post-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.title-input {
  font-size: 1.1rem;
}

:deep(.title-input .el-input__inner) {
  font-size: 1.1rem;
  font-weight: 600;
  border: none;
  border-bottom: 2px solid #f0f0f0;
  border-radius: 0;
  padding: 12px 0;
}

:deep(.title-input .el-input__inner:focus) {
  border-bottom-color: #c41e3a;
}

.content-input {
  font-size: 1rem;
}

:deep(.content-input .el-textarea__inner) {
  font-size: 1rem;
  line-height: 1.8;
  border: 2px solid #f0f0f0;
  border-radius: 12px;
  padding: 16px;
  resize: none;
}

:deep(.content-input .el-textarea__inner:focus) {
  border-color: #c41e3a;
}

.upload-section {
  background: #f9fafc;
  padding: 20px;
  border-radius: 12px;
  border: 2px dashed #e0e0e0;
}

.upload-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #666;
  margin-bottom: 16px;
  font-size: 1rem;
}

.image-upload-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 12px;
}

.upload-image-item {
  position: relative;
  aspect-ratio: 1;
  border-radius: 8px;
  overflow: hidden;
  background: #f0f0f0;
}

.upload-image-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-actions {
  position: absolute;
  top: 0;
  right: 0;
  background: rgba(0,0,0,0.5);
  padding: 4px;
  border-radius: 0 0 0 8px;
}

.remove-icon {
  color: white;
  cursor: pointer;
  font-size: 18px;
}

.remove-icon:hover {
  color: #ff4d4f;
}

.upload-trigger {
  aspect-ratio: 1;
  border: 2px dashed #d0d0d0;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.3s;
  background: white;
  color: #999;
}

.upload-trigger:hover {
  border-color: #c41e3a;
  color: #c41e3a;
  background: #fff5f5;
}

.upload-trigger .el-icon {
  font-size: 24px;
}

.upload-trigger span {
  font-size: 0.85rem;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #666;
  margin-bottom: 12px;
  font-size: 1rem;
}

.tags-section {
  background: #f9fafc;
  padding: 20px;
  border-radius: 12px;
}

.tags-input-area {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.tag-item {
  font-size: 0.95rem;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
}

.tag-input {
  width: 120px;
}

:deep(.tag-input .el-input__inner) {
  border-radius: 6px;
}

.add-tag-btn {
  border-radius: 6px;
  border-style: dashed;
}

.hot-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #e8e8e8;
}

.hot-tag-label {
  font-size: 0.9rem;
  color: #999;
  font-weight: 500;
}

.recommend-tag {
  cursor: pointer;
  transition: all 0.2s;
}

.recommend-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(196, 30, 58, 0.2);
}

.location-section,
.category-section {
  background: #f9fafc;
  padding: 20px;
  border-radius: 12px;
}

.location-select {
  width: 100%;
}

:deep(.location-select .el-input__inner) {
  border-radius: 8px;
}

.category-radio {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

:deep(.category-radio .el-radio-button__inner) {
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  padding: 8px 20px;
}

:deep(.category-radio .el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: linear-gradient(135deg, #a0182f, #c41e3a);
  border-color: #a0182f;
  box-shadow: 0 2px 8px rgba(160, 24, 47, 0.3);
}

.dialog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 32px;
  border-top: 1px solid #f0f0f0;
}

.footer-tools {
  display: flex;
  align-items: center;
  gap: 12px;
}

.tool-tip {
  font-size: 0.9rem;
  color: #999;
}

.footer-actions {
  display: flex;
  gap: 12px;
}

:deep(.dialog-footer .el-button--primary) {
  background: linear-gradient(135deg, #c41e3a, #8b1e3f);
  border: none;
  padding: 10px 32px;
  border-radius: 8px;
}

:deep(.dialog-footer .el-button--primary:hover) {
  background: linear-gradient(135deg, #a0182f, #c41e3a);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(160, 24, 47, 0.3);
}

/* Video Upload Styles */
.video-preview {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.video-player-wrapper {
  position: relative;
  width: 100%;
  border-radius: 12px;
  overflow: hidden;
  background: #000;
}

.video-preview-player {
  display: block;
  width: 100%;
  max-height: 300px;
  background: #000;
}

.video-info-badge {
  position: absolute;
  bottom: 12px;
  right: 12px;
  background: rgba(0,0,0,0.75);
  color: white;
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 0.9rem;
  font-weight: 600;
  font-family: 'Roboto Mono', monospace;
  pointer-events: none;
}

.video-thumbnail {
  position: relative;
  width: 100%;
  aspect-ratio: 16/9;
  border-radius: 12px;
  overflow: hidden;
  background: #000;
}

.video-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-play-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 64px;
  height: 64px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: #c41e3a;
  box-shadow: 0 4px 16px rgba(0,0,0,0.3);
}

.video-duration {
  position: absolute;
  bottom: 12px;
  right: 12px;
  background: rgba(0,0,0,0.75);
  color: white;
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 0.9rem;
  font-weight: 600;
  font-family: 'Roboto Mono', monospace;
}

.video-actions {
  display: flex;
  justify-content: center;
}

.video-upload-trigger {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  border: 2px dashed #d0d0d0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  background: white;
  gap: 12px;
}

.video-upload-trigger:hover {
  border-color: #c41e3a;
  background: #fff5f5;
}

.video-upload-trigger .upload-icon {
  font-size: 48px;
  color: #c41e3a;
}

.video-upload-trigger .upload-text {
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
}

.video-upload-trigger .upload-hint {
  font-size: 0.9rem;
  color: #999;
}

/* Post Video Display */
.post-video {
  margin-bottom: 20px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  background: #000;
}

.video-player {
  width: 100%;
  max-height: 500px;
  display: block;
  background: #000;
}

.video-player::-webkit-media-controls-panel {
  background: linear-gradient(transparent, rgba(0,0,0,0.8));
}

</style>

/* Comment Dialog Styles */
:deep(.comment-dialog) {
  border-radius: 20px;
}

:deep(.comment-dialog .el-dialog__header) {
  background: linear-gradient(135deg, #a0182f, #c41e3a);
  color: white;
  padding: 20px 24px;
}

:deep(.comment-dialog .el-dialog__title) {
  color: white;
  font-size: 1.2rem;
  font-weight: 600;
}

:deep(.comment-dialog .el-dialog__headerbtn .el-dialog__close) {
  color: white;
  font-size: 20px;
}

:deep(.comment-dialog .el-dialog__body) {
  padding: 0;
  max-height: 70vh;
  overflow-y: auto;
}

.comment-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.original-post {
  padding: 20px 24px;
  background: #f8f9fa;
  border-bottom: 1px solid #e0e0e0;
}

.post-author {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.author-name {
  font-weight: 600;
  color: #333;
}

.post-time {
  font-size: 0.85rem;
  color: #999;
}

.post-content {
  color: #666;
  line-height: 1.6;
}

.comments-list {
  padding: 20px 24px;
  min-height: 200px;
}

.loading-comments {
  padding: 20px 0;
}

.no-comments {
  padding: 40px 0;
  text-align: center;
}

.comment-item {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.comment-content {
  flex: 1;
}

.comment-author {
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
}

.comment-text {
  color: #666;
  line-height: 1.6;
  margin-bottom: 8px;
}

.comment-meta {
  display: flex;
  gap: 16px;
  font-size: 0.85rem;
  color: #999;
}

.comment-like-btn {
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 2px 8px;
  border-radius: 12px;
}

.comment-like-btn:hover {
  color: #c41e3a;
  background: rgba(196, 30, 58, 0.08);
}

.comment-like-btn.active {
  color: #c41e3a;
}

.comment-like-btn.active .el-icon {
  animation: commentLikeAnim 0.4s ease;
}

@keyframes commentLikeAnim {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.3); }
}

.comment-likes {
  cursor: pointer;
  transition: color 0.3s;
}

.comment-likes:hover {
  color: #c41e3a;
}

.comment-input-area {
  padding: 20px 24px;
  background: #f8f9fa;
  border-top: 1px solid #e0e0e0;
}

/* Media Summary */
.media-summary {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  flex-wrap: wrap;
}

.media-summary .el-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
}

.media-summary .el-icon {
  font-size: 14px;
}
