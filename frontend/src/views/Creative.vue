<template>
  <MainLayout>
    <div class="creative-page">
      <!-- Hero Section -->
      <div class="creative-hero" v-motion-fade-visible>
        <div class="hero-content">
          <div class="hero-icon-wrapper">
            <span class="hero-icon">🎨</span>
          </div>
          <h1 class="hero-title">燕赵文创 · 众创空间</h1>
          <p class="hero-subtitle">汇聚西柏坡、塞罕坝等河北红色元素，打造新时代燕赵文创精品</p>
          <div class="hero-actions">
            <el-button type="primary" size="large" icon="Upload" @click="uploadWork" class="action-btn primary">
              上传作品
            </el-button>
            <el-button 
              size="large" 
              :icon="showingMyWorks ? 'Grid' : 'User'" 
              @click="showingMyWorks ? showAllWorks() : showMyWorks()" 
              class="action-btn secondary"
            >
              {{ showingMyWorks ? '全部作品' : '我的作品' }}
            </el-button>
            <el-button 
              size="large" 
              icon="Service"
              @click="contactAdminDialogVisible = true" 
              class="action-btn contact-admin"
            >
              联系管理员
            </el-button>
          </div>
        </div>
      </div>

      <div class="creative-container">
        <!-- 我的作品提示条 -->
        <div v-if="showingMyWorks" class="my-works-banner" v-motion-slide-visible-down>
          <div class="banner-content">
            <el-icon class="banner-icon"><User /></el-icon>
            <span class="banner-text">正在查看我的作品</span>
            <el-button type="primary" size="small" plain @click="showAllWorks" class="banner-btn">
              返回全部作品
            </el-button>
          </div>
        </div>
        
        <!-- Filter Tabs -->
        <div class="category-tabs" v-motion-slide-visible-up>
          <div 
            v-for="(cat, index) in categories" 
            :key="cat.key" 
            class="category-tab"
            :class="{ active: activeCategory === cat.key }" 
            @click="activeCategory = cat.key"
            v-motion-slide-visible-up
            :delay="index * 50"
          >
            <span class="tab-icon">{{ cat.icon }}</span>
            <span class="tab-label">{{ cat.label }}</span>
            <span class="tab-count">{{ cat.count }}</span>
          </div>
        </div>

        <!-- Content Grid -->
        <div v-if="loading" class="works-loading">
          <div class="skeleton-grid">
            <div v-for="i in 6" :key="i" class="skeleton-card">
              <el-skeleton animated>
                <template #template>
                  <div class="skeleton-cover"></div>
                  <div class="skeleton-content">
                    <el-skeleton-item variant="h3" style="width: 80%; margin-bottom: 12px;" />
                    <el-skeleton-item variant="text" style="width: 60%; margin-bottom: 8px;" />
                    <el-skeleton-item variant="text" style="width: 100%;" />
                    <el-skeleton-item variant="text" style="width: 90%;" />
                  </div>
                </template>
              </el-skeleton>
            </div>
          </div>
        </div>
        
        <div v-else-if="filteredWorks.length === 0" class="works-empty" v-motion-pop-visible>
          <div class="empty-animation">
            <div class="empty-icon-wrapper">
              <span class="empty-icon">{{ showingMyWorks ? '📝' : '🎨' }}</span>
              <div class="empty-circle"></div>
              <div class="empty-circle-2"></div>
            </div>
          </div>
          <h3 class="empty-title">{{ showingMyWorks ? '还没有作品' : '暂无作品' }}</h3>
          <p class="empty-text">{{ showingMyWorks ? '您还没有上传任何作品' : '该分类下暂时没有作品' }}</p>
          <p class="empty-subtext">{{ showingMyWorks ? '快去上传您的第一个创意作品吧！' : '成为第一个分享创意的人吧！' }}</p>
          <el-button type="primary" size="large" icon="Upload" @click="uploadWork" class="empty-action-btn">
            立即上传作品
          </el-button>
          <el-button v-if="showingMyWorks" size="large" @click="showAllWorks" class="empty-action-btn secondary" style="margin-left: 12px;">
            浏览全部作品
          </el-button>
        </div>

        <div v-else class="works-grid">
          <div 
            v-for="(work, index) in filteredWorks" 
            :key="work.id" 
            class="work-card" 
            @click="viewWorkDetail(work)"
            v-motion-slide-visible-up
            :delay="index * 50"
          >
            <div class="work-cover-wrapper">
              <div class="work-cover" :style="{ backgroundImage: `url(${work.coverImage})` }">
                <div class="work-overlay">
                  <div class="overlay-content">
                    <el-button type="primary" circle icon="View" size="large" class="view-btn" />
                    <p class="overlay-text">查看详情</p>
                  </div>
                </div>
                <div class="work-badge">
                  <span class="badge-icon">{{ getCategoryIcon(work.category) }}</span>
                  <span class="badge-text">{{ work.type }}</span>
                </div>
                <div class="work-hot-badge" v-if="work.views > 3000">
                  <span class="hot-icon">🔥</span>
                  <span class="hot-text">热门</span>
                </div>
              </div>
            </div>
            <div class="work-info">
              <h3 class="work-title">{{ work.title }}</h3>
              <div class="designer-row">
                <el-avatar :size="28" class="designer-avatar">
                  <span class="avatar-text">{{ work.designer.charAt(0) }}</span>
                </el-avatar>
                <span class="work-designer">{{ work.designer }}</span>
                <el-tag size="small" effect="plain" class="designer-badge">设计师</el-tag>
              </div>
              <p class="work-description">{{ work.description }}</p>
              <div class="work-meta">
                <div class="meta-item">
                  <el-icon class="meta-icon"><View /></el-icon>
                  <span class="meta-value">{{ formatNumber(work.views) }}</span>
                </div>
                <div class="meta-item">
                  <el-icon class="meta-icon"><Star /></el-icon>
                  <span class="meta-value">{{ formatNumber(work.votes) }}</span>
                </div>
                <div class="meta-item">
                  <el-icon class="meta-icon"><ChatDotRound /></el-icon>
                  <span class="meta-value">{{ formatNumber(work.comments) }}</span>
                </div>
              </div>
              <div class="work-footer">
                <div class="work-tags">
                  <el-tag 
                    v-for="(tag, idx) in work.tags.slice(0, 3)" 
                    :key="tag" 
                    size="small" 
                    effect="plain"
                    class="custom-tag"
                  >
                    {{ tag }}
                  </el-tag>
                  <el-tag 
                    v-if="work.tags.length > 3"
                    size="small" 
                    effect="plain"
                    class="custom-tag more-tag"
                  >
                    +{{ work.tags.length - 3 }}
                  </el-tag>
                </div>
                <span class="work-time">{{ work.createTime }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Detail Dialog -->
      <el-dialog 
        v-model="workDialogVisible" 
        width="1000px" 
        class="work-dialog"
        destroy-on-close
        :show-close="false"
      >
        <div v-if="currentWork" class="work-detail">
          <!-- Close Button -->
          <div class="detail-close" @click="workDialogVisible = false">
            <el-icon><Close /></el-icon>
          </div>
          
          <!-- Media Display -->
          <div class="detail-media" style="min-height: 400px; background: #000; position: relative;">
            <!-- 主要媒体展示：封面图片或视频 -->
            <div v-if="isVideoWork(currentWork)" class="media-video">
              <video 
                :src="getMediaUrl(currentWork, 'video')" 
                controls 
                class="video-player"
                :poster="currentWork.coverImage"
                preload="metadata"
                @error="handleMediaError"
                style="width: 100%; max-height: 500px; object-fit: contain;"
              >
                您的浏览器不支持视频播放
              </video>
            </div>
            <div v-else class="media-image" style="display: flex; align-items: center; justify-content: center; min-height: 350px; width: 100%;">
              <img 
                v-if="currentWork.coverImage || (currentWork.files && currentWork.files.length > 0)"
                :src="currentWork.coverImage || currentWork.files[0]" 
                :alt="currentWork.title" 
                class="detail-image"
                @error="handleMediaError"
                style="display: block !important; width: auto !important; max-width: 100% !important; max-height: 450px !important; object-fit: contain !important;"
              />
              <div v-else class="image-tip">
                <el-icon class="tip-icon"><Picture /></el-icon>
                <p>图片文件暂未上传</p>
              </div>
            </div>
            <div class="media-badge">
              <span class="badge-icon">{{ getCategoryIcon(currentWork.category) }}</span>
              <span class="badge-text">{{ currentWork.type }}</span>
            </div>
          </div>
          
          <!-- 作品文件展示区域 -->
          <div v-if="currentWork.files && currentWork.files.length > 0" class="work-files-section" style="background: #f8f9fa; padding: 20px; border-bottom: 1px solid #eee;">
            <h3 style="margin: 0 0 15px 0; font-size: 16px; color: #333; display: flex; align-items: center; gap: 8px;">
              <el-icon><Document /></el-icon>
              作品文件 ({{ currentWork.files.length }} 个)
            </h3>
            <div class="files-grid" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(150px, 1fr)); gap: 12px;">
              <div 
                v-for="(file, index) in currentWork.files" 
                :key="index" 
                class="file-item"
                style="border-radius: 8px; overflow: hidden; background: white; box-shadow: 0 2px 8px rgba(0,0,0,0.1); cursor: pointer;"
                @click="openFileInNewTab(file)"
              >
                <!-- 判断是图片还是视频 -->
                <div v-if="isVideoFile(file)" style="position: relative;">
                  <video 
                    :src="file" 
                    style="width: 100%; height: 120px; object-fit: cover; background: #000;"
                    preload="metadata"
                  ></video>
                  <div style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); background: rgba(0,0,0,0.6); border-radius: 50%; width: 40px; height: 40px; display: flex; align-items: center; justify-content: center;">
                    <el-icon style="color: white; font-size: 20px;"><VideoPlay /></el-icon>
                  </div>
                </div>
                <img 
                  v-else
                  :src="file" 
                  :alt="'文件 ' + (index + 1)"
                  style="width: 100%; height: 120px; object-fit: cover;"
                  @error="(e) => e.target.src = 'https://via.placeholder.com/150x120?text=加载失败'"
                />
                <div style="padding: 8px; font-size: 12px; color: #666; text-align: center; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                  {{ isVideoFile(file) ? '🎬 视频' : '🖼️ 图片' }} {{ index + 1 }}
                </div>
              </div>
            </div>
            <p style="margin: 12px 0 0 0; font-size: 12px; color: #999;">
              💡 点击文件可在新标签页中查看原图/视频
            </p>
          </div>
          
          <!-- Content -->
          <div class="detail-body">
            <!-- Title & Designer -->
            <div class="detail-header-info">
              <h2 class="detail-title">{{ currentWork.title }}</h2>
              <div class="designer-info">
                <el-avatar :size="48" class="designer-avatar-large">
                  <span class="avatar-text">{{ currentWork.designer.charAt(0) }}</span>
                </el-avatar>
                <div class="designer-details">
                  <div class="designer-name">{{ currentWork.designer }}</div>
                  <div class="designer-role">
                    <el-tag size="small" effect="plain" class="role-tag">认证设计师</el-tag>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- Stats -->
            <div class="detail-stats">
              <div class="stat-card">
                <el-icon class="stat-icon" color="#a0182f"><View /></el-icon>
                <div class="stat-info">
                  <div class="stat-value">{{ formatNumber(currentWork.views) }}</div>
                  <div class="stat-label">浏览量</div>
                </div>
              </div>
              <div class="stat-card">
                <el-icon class="stat-icon" color="#ffd700"><Star /></el-icon>
                <div class="stat-info">
                  <div class="stat-value">{{ formatNumber(currentWork.votes) }}</div>
                  <div class="stat-label">点赞数</div>
                </div>
              </div>
            </div>
            
            <!-- Description -->
            <div class="detail-section">
              <div class="section-header">
                <el-icon class="section-icon"><Document /></el-icon>
                <h3 class="section-title">作品简介</h3>
              </div>
              <p class="section-content">{{ currentWork.description }}</p>
            </div>
            
            <!-- Tags -->
            <div class="detail-section">
              <div class="section-header">
                <el-icon class="section-icon"><PriceTag /></el-icon>
                <h3 class="section-title">作品标签</h3>
              </div>
              <div class="tags-list">
                <el-tag 
                  v-for="tag in currentWork.tags" 
                  :key="tag" 
                  size="large"
                  effect="plain"
                  class="detail-tag"
                >
                  {{ tag }}
                </el-tag>
              </div>
            </div>
            
            <!-- Meta Info -->
            <div class="detail-meta">
              <div class="meta-info-item">
                <span class="meta-info-label">作品类型</span>
                <span class="meta-info-value">{{ currentWork.type }}</span>
              </div>
              <div class="meta-info-item">
                <span class="meta-info-label">发布时间</span>
                <span class="meta-info-value">{{ currentWork.createTime }}</span>
              </div>
            </div>
            
            <!-- Actions -->
            <div class="detail-actions">
              <el-button 
                :type="currentWork?.hasVoted ? 'info' : 'danger'" 
                size="large"
                class="action-button like-button"
                @click="likeWork"
              >
                <el-icon class="button-icon"><Star /></el-icon>
                <span>{{ currentWork?.hasVoted ? '已点赞' : '点赞作品' }}</span>
              </el-button>
              <el-button 
                type="primary" 
                size="large"
                plain
                class="action-button contact-button"
                @click="contactDesigner"
              >
                <el-icon class="button-icon"><Message /></el-icon>
                <span>联系设计师</span>
              </el-button>
              <el-button 
                v-if="isMyWork(currentWork)"
                type="warning" 
                size="large"
                class="action-button mall-button"
                @click="openMallApplicationDialog"
                :disabled="currentWork?.hasAppliedToMall"
              >
                <el-icon class="button-icon"><Shop /></el-icon>
                <span>{{ currentWork?.hasAppliedToMall ? '已申请上架' : '申请上架商城' }}</span>
              </el-button>
            </div>
          </div>
        </div>
      </el-dialog>

      <!-- Mall Application Dialog -->
      <el-dialog 
        v-model="mallApplicationDialogVisible" 
        title="申请上架商城" 
        width="600px" 
        class="mall-application-dialog"
        destroy-on-close
      >
        <div class="mall-application-content" v-if="currentWork">
          <div class="application-preview">
            <div class="preview-image" :style="{ backgroundImage: `url(${currentWork.coverImage})` }"></div>
            <div class="preview-info">
              <h4>{{ currentWork.title }}</h4>
              <p>设计师：{{ currentWork.designer }}</p>
            </div>
          </div>
          
          <el-divider />
          
          <el-form :model="mallApplicationForm" label-position="top">
            <el-form-item label="商品名称" required>
              <el-input 
                v-model="mallApplicationForm.productName" 
                placeholder="请输入商品名称"
                maxlength="100"
              />
            </el-form-item>
            
            <el-form-item label="商品分类" required>
              <el-select v-model="mallApplicationForm.category" placeholder="请选择分类" style="width: 100%">
                <el-option label="文化周边" value="文化周边" />
                <el-option label="创意生活" value="创意生活" />
                <el-option label="艺术收藏" value="艺术收藏" />
                <el-option label="红色纪念" value="红色纪念" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="商品描述">
              <el-input 
                v-model="mallApplicationForm.description" 
                type="textarea" 
                :rows="3"
                placeholder="请输入商品描述"
                maxlength="500"
              />
            </el-form-item>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="建议价格 (元)" required>
                  <el-input-number 
                    v-model="mallApplicationForm.suggestedPrice" 
                    :min="1" 
                    :max="9999"
                    :precision="2"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="初始库存" required>
                  <el-input-number 
                    v-model="mallApplicationForm.initialStock" 
                    :min="1" 
                    :max="9999"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-form-item label="商品图标">
              <div class="icon-selector">
                <span 
                  v-for="icon in productIcons" 
                  :key="icon" 
                  class="icon-option"
                  :class="{ active: mallApplicationForm.icon === icon }"
                  @click="mallApplicationForm.icon = icon"
                >
                  {{ icon }}
                </span>
              </div>
            </el-form-item>
          </el-form>
          
          <div class="application-tips">
            <el-icon><InfoFilled /></el-icon>
            <span>提交后将由管理员审核，审核通过后商品将自动上架商城</span>
          </div>
        </div>
        
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="mallApplicationDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitMallApplication" :loading="submittingApplication">
              提交申请
            </el-button>
          </div>
        </template>
      </el-dialog>

      <!-- Upload Dialog -->
      <el-dialog 
        v-model="uploadDialogVisible" 
        title="上传作品" 
        width="700px" 
        class="upload-dialog"
        destroy-on-close
      >
        <el-form :model="uploadForm" label-width="100px" label-position="top">
          <el-form-item label="作品标题" required>
            <el-input 
              v-model="uploadForm.title" 
              placeholder="请输入作品标题"
              maxlength="100"
              show-word-limit
            />
          </el-form-item>
          
          <el-form-item label="作品分类" required>
            <el-select v-model="uploadForm.categoryType" placeholder="请选择作品分类" style="width: 100%">
              <el-option label="海报设计" :value="1" />
              <el-option label="Logo设计" :value="2" />
              <el-option label="文创产品" :value="3" />
              <el-option label="视频动画" :value="4" />
              <el-option label="插画绘本" :value="5" />
              <el-option label="手工艺品" :value="6" />
              <el-option label="数字艺术" :value="7" />
              <el-option label="雕塑模型" :value="8" />
              <el-option label="书法篆刻" :value="9" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="作品描述" required>
            <el-input 
              v-model="uploadForm.description" 
              type="textarea" 
              :rows="4"
              placeholder="请描述您的作品"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
          
          <el-form-item label="设计理念">
            <el-input 
              v-model="uploadForm.designConcept" 
              type="textarea" 
              :rows="3"
              placeholder="请分享您的设计理念和创作思路"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
          
          <el-form-item label="封面图片">
            <el-input 
              v-model="uploadForm.coverImage" 
              placeholder="请输入封面图片URL"
            />
            <div class="form-tip">提示：请输入图片URL地址</div>
          </el-form-item>
          
          <el-form-item label="作品文件">
            <el-input 
              v-model="uploadForm.files[0]" 
              placeholder="请输入作品文件URL"
            />
            <div class="form-tip">提示：请输入作品文件URL地址</div>
          </el-form-item>
          
          <el-form-item label="版权声明">
            <el-input 
              v-model="uploadForm.copyrightStatement" 
              type="textarea" 
              :rows="2"
              placeholder="请输入版权声明"
            />
          </el-form-item>
        </el-form>
        
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="uploadDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitWork">提交作品</el-button>
          </div>
        </template>
      </el-dialog>

      <!-- Contact Designer Dialog -->
      <el-dialog 
        v-model="contactDialogVisible" 
        title="联系设计师" 
        width="500px" 
        class="contact-dialog"
        destroy-on-close
      >
        <div class="contact-designer-content" v-if="currentWork">
          <div class="designer-profile">
            <el-avatar :size="64" class="designer-avatar-contact">
              <span class="avatar-text-large">{{ currentWork.designer.charAt(0) }}</span>
            </el-avatar>
            <div class="designer-info-contact">
              <h3 class="designer-name-contact">{{ currentWork.designer }}</h3>
              <p class="designer-work-contact">作品：{{ currentWork.title }}</p>
              <el-tag size="small" effect="plain" class="designer-tag">认证设计师</el-tag>
            </div>
          </div>
          
          <el-divider />
          
          <el-form label-position="top">
            <el-form-item label="留言内容">
              <el-input 
                v-model="contactMessage" 
                type="textarea" 
                :rows="4"
                placeholder="请输入您想对设计师说的话，例如：合作意向、作品咨询等..."
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
          </el-form>
          
          <div class="contact-tips">
            <el-icon><InfoFilled /></el-icon>
            <span>设计师会在24小时内回复您的留言</span>
          </div>
        </div>
        
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="contactDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="sendContactMessage">
              <el-icon class="button-icon"><Message /></el-icon>
              发送留言
            </el-button>
          </div>
        </template>
      </el-dialog>

      <!-- Contact Admin Dialog -->
      <el-dialog 
        v-model="contactAdminDialogVisible" 
        title="联系管理员" 
        width="550px" 
        class="contact-admin-dialog"
        destroy-on-close
      >
        <div class="contact-admin-content">
          <div class="admin-profile">
            <div class="admin-avatar">
              <el-icon :size="40" color="#a0182f"><Service /></el-icon>
            </div>
            <div class="admin-info">
              <h3 class="admin-title">众创空间管理员</h3>
              <p class="admin-desc">如有任何问题，欢迎联系我们</p>
            </div>
          </div>
          
          <el-divider />
          
          <div class="contact-methods">
            <div class="contact-method-item">
              <el-icon class="method-icon"><Message /></el-icon>
              <div class="method-info">
                <span class="method-label">客服邮箱</span>
                <span class="method-value">admin@jiyi-creative.com</span>
              </div>
              <el-button size="small" type="primary" plain @click="copyToClipboard('admin@jiyi-creative.com')">复制</el-button>
            </div>
            <div class="contact-method-item">
              <el-icon class="method-icon"><Phone /></el-icon>
              <div class="method-info">
                <span class="method-label">客服电话</span>
                <span class="method-value">400-888-9999</span>
              </div>
              <el-button size="small" type="primary" plain @click="copyToClipboard('400-888-9999')">复制</el-button>
            </div>
            <div class="contact-method-item">
              <el-icon class="method-icon"><ChatDotRound /></el-icon>
              <div class="method-info">
                <span class="method-label">微信公众号</span>
                <span class="method-value">燕赵文创众创空间</span>
              </div>
            </div>
            <div class="contact-method-item">
              <el-icon class="method-icon"><Clock /></el-icon>
              <div class="method-info">
                <span class="method-label">工作时间</span>
                <span class="method-value">周一至周五 9:00-18:00</span>
              </div>
            </div>
          </div>
          
          <el-divider />
          
          <el-form label-position="top">
            <el-form-item label="在线留言">
              <el-input 
                v-model="adminContactMessage" 
                type="textarea" 
                :rows="4"
                placeholder="请描述您遇到的问题或建议，我们会尽快回复..."
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
          </el-form>
          
          <div class="contact-tips">
            <el-icon><InfoFilled /></el-icon>
            <span>管理员会在1-2个工作日内回复您的留言</span>
          </div>
        </div>
        
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="contactAdminDialogVisible = false">关闭</el-button>
            <el-button type="primary" @click="sendAdminMessage" :disabled="!adminContactMessage.trim()">
              <el-icon class="button-icon"><Message /></el-icon>
              发送留言
            </el-button>
          </div>
        </template>
      </el-dialog>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { View, Star, ChatDotRound, Upload, User, Message, Close, Document, PriceTag, VideoCamera, Picture, VideoPlay, InfoFilled, Grid, Shop, Service, Phone, Clock } from '@element-plus/icons-vue'
import MainLayout from '@/layouts/MainLayout.vue'
import { creativeApi, type DesignVO } from '@/api/creative'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

interface Work {
  id: number
  title: string
  category: string
  description: string
  designer: string
  designerId: number
  type: string
  coverImage: string
  files?: string[]  // 添加 files 字段
  views: number
  votes: number
  likes: number
  comments: number
  tags: string[]
  createTime: string
  hasVoted?: boolean
  hasAppliedToMall?: boolean
}

const loading = ref(true)
const activeCategory = ref('all')
const workDialogVisible = ref(false)
const uploadDialogVisible = ref(false)
const currentWork = ref<Work | null>(null)
const showingMyWorks = ref(false)  // 是否显示我的作品

// 上传表单数据
const uploadForm = ref({
  title: '',
  categoryType: 1,
  description: '',
  designConcept: '',
  coverImage: '',
  files: [''],
  copyrightStatement: ''
})

// 分类 - 文创作品类型
const categories = [
  { key: 'all', label: '全部作品', icon: '🎨', count: 0 },
  { key: 'poster', label: '海报设计', icon: '🖼️', count: 0 },
  { key: 'logo', label: 'Logo设计', icon: '🎯', count: 0 },
  { key: 'product', label: '文创产品', icon: '🎁', count: 0 },
  { key: 'video', label: '视频动画', icon: '🎬', count: 0 },
  { key: 'illustration', label: '插画绘本', icon: '🎭', count: 0 },
  { key: 'craft', label: '手工艺品', icon: '🏺', count: 0 },
  { key: 'digital', label: '数字艺术', icon: '💻', count: 0 },
  { key: 'sculpture', label: '雕塑模型', icon: '🗿', count: 0 },
  { key: 'calligraphy', label: '书法篆刻', icon: '✒️', count: 0 }
]

const works = ref<Work[]>([
  { id: '1', title: '西柏坡精神宣传海报', category: 'poster', description: '以“新中国从这里走来”为主题的宣传海报设计，采用现代极简风格与传统红色元素结合，展现新时代西柏坡精神的传承与发展。海报主体以红色为主色调，融入了西柏坡中共中央旧址标志性建筑剪影。',
    designer: '张设计师', type: '海报设计', coverImage: 'https://images.unsplash.com/photo-1558591710-4b4a1ae0f04d?w=800&auto=format&fit=crop',
    views: 3280, likes: 256, comments: 48, tags: ['海报', '西柏坡', '赶考精神'], createTime: '2024-12-15' },
  { id: '2', title: '雄安新区红色文化Logo', category: 'logo', description: '雄安新区红色文化品牌Logo设计方案，融合了白洋淀荷花、未来城市轮廓和红色五角星元素，寓意着雄安新区作为“千年大计”的红色底蕴与未来展望。',
    designer: '李设计师', type: 'Logo设计', coverImage: 'https://images.unsplash.com/photo-1626785774573-4b799314346d?w=800&auto=format&fit=crop',
    views: 2890, likes: 198, comments: 35, tags: ['Logo', '雄安新区', '未来之城'], createTime: '2024-12-10' },
  { id: '3', title: '李大钊故居纪念文创', category: 'product', description: '以李大钊故居为灵感的文创产品设计，包含“铁肩担道义”主题笔记本、马克杯、书签等系列产品，旨在让大钊精神融入日常生活。',
    designer: '王设计师', type: '文创产品', coverImage: 'https://images.unsplash.com/photo-1544816155-12df9643f363?w=800&auto=format&fit=crop',
    views: 4120, likes: 312, comments: 67, tags: ['文创', '李大钊', '红色先驱'], createTime: '2024-12-08' },
  { id: '4', title: '地道战动画短片', category: 'video', description: '讲述冀中平原地道战故事的动画短片创作，通过生动的画面和感人的剧情，向年轻一代讲述冉庄地道战的智慧与勇气。',
    designer: '赵设计师', type: '视频动画', coverImage: 'https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=800&auto=format&fit=crop',
    views: 5670, likes: 445, comments: 89, tags: ['动画', '地道战', '保定'], createTime: '2024-12-05' },
  { id: '5', title: '塞罕坝精神绘本插画', category: 'poster', description: '塞罕坝精神主题系列绘本插画，以三代务林人的奋斗故事为线索，展现“荒原变林海”的人间奇迹。',
    designer: '刘设计师', type: '插画设计', coverImage: 'https://images.unsplash.com/photo-1558591710-4b4a1ae0f04d?w=800&auto=format&fit=crop',
    views: 2950, likes: 223, comments: 42, tags: ['插画', '塞罕坝', '生态文明'], createTime: '2024-12-01' },
  { id: '6', title: '狼牙山五壮士雕塑模型', category: 'product', description: '狼牙山五壮士3D打印雕塑模型设计，生动还原五位英雄英勇跳崖的悲壮瞬间，作为爱国主义教育的生动教材。',
    designer: '陈设计师', type: '模型设计', coverImage: 'https://images.unsplash.com/photo-1544816155-12df9643f363?w=800&auto=format&fit=crop',
    views: 3450, likes: 267, comments: 51, tags: ['模型', '狼牙山', '英雄'], createTime: '2024-11-28' },
  { id: '7', title: '白洋淀雁翎队主题剪纸', category: 'product', description: '采用蔚县剪纸工艺创作的白洋淀雁翎队主题作品，生动刻画了水上游击队英勇抗日的场景，展现了非遗与红色文化的完美融合。',
    designer: '赵非遗', type: '剪纸艺术', coverImage: 'https://images.unsplash.com/photo-1605648916361-9bc12ad6a569?w=800&auto=format&fit=crop',
    views: 3100, likes: 245, comments: 38, tags: ['剪纸', '白洋淀', '非遗'], createTime: '2024-11-25' },
  { id: '8', title: '唐山抗震纪念碑3D模型', category: 'product', description: '高精度复刻唐山抗震纪念碑，铭记抗震救灾精神，展现公而忘私、患难与共的英雄气概。',
    designer: '孙建模', type: '模型设计', coverImage: 'https://images.unsplash.com/photo-1596634327092-234255018652?w=800&auto=format&fit=crop',
    views: 2560, likes: 189, comments: 24, tags: ['模型', '唐山', '抗震精神'], createTime: '2024-11-20' },
  { id: '9', title: '吴桥杂技·红色记忆', category: 'video', description: '将国家级非遗吴桥杂技与红色故事相结合，通过高难度的杂技动作演绎革命战争年代的惊险与传奇。',
    designer: '周导演', type: '演艺视频', coverImage: 'https://images.unsplash.com/photo-1516280440614-6697288d5d38?w=800&auto=format&fit=crop',
    views: 4200, likes: 356, comments: 62, tags: ['杂技', '非遗', '红色演绎'], createTime: '2024-11-15' }
])

const filteredWorks = computed(() => activeCategory.value === 'all' ? works.value : works.value.filter(w => w.category === activeCategory.value))

const updateCategoryCounts = () => {
  categories.forEach(cat => {
    cat.count = cat.key === 'all' ? works.value.length : works.value.filter(w => w.category === cat.key).length
  })
}

const viewWorkDetail = async (work: Work) => { 
  currentWork.value = work
  workDialogVisible.value = true
  
  // 调用获取详情接口会自动增加浏览量（后端实现）
  if (work.id && typeof work.id === 'number') {
    try {
      const detail = await creativeApi.getDesignById(work.id)
      if (detail) {
        // 更新本地浏览量
        work.views = detail.views || (work.views || 0) + 1
        currentWork.value.views = work.views
      }
      
      // 检查是否已申请上架商城
      const checkResult = await creativeApi.checkMallApplication(work.id)
      if (checkResult && checkResult.data) {
        work.hasAppliedToMall = checkResult.data.hasApplied
        currentWork.value.hasAppliedToMall = checkResult.data.hasApplied
      }
    } catch (error) {
      // 静默处理，不影响用户体验
      work.views = (work.views || 0) + 1
    }
  } else {
    // 本地模拟增加浏览量（示例数据）
    work.views = (work.views || 0) + 1
  }
}

// 点赞作品
const likeWork = async () => {
  if (!currentWork.value) return
  
  // 检查登录状态
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再点赞')
    router.push('/login')
    return
  }
  
  const workId = currentWork.value.id
  
  // 判断是否为真实数据（数字ID）还是示例数据（字符串ID）
  const isRealData = typeof workId === 'number'
  
  if (isRealData) {
    // 真实数据：调用后端API
    try {
      if (currentWork.value.hasVoted) {
        // 取消点赞
        await creativeApi.unvoteDesign(workId)
        currentWork.value.hasVoted = false
        currentWork.value.votes = Math.max(0, (currentWork.value.votes || 1) - 1)
        ElMessage.success('已取消点赞')
      } else {
        // 点赞
        await creativeApi.voteDesign(workId)
        currentWork.value.hasVoted = true
        currentWork.value.votes = (currentWork.value.votes || 0) + 1
        ElMessage.success('点赞成功！感谢您的支持')
      }
      
      // 同步更新列表中的数据
      const workInList = works.value.find(w => w.id === workId)
      if (workInList) {
        workInList.hasVoted = currentWork.value.hasVoted
        workInList.votes = currentWork.value.votes
      }
    } catch (error: any) {
      console.error('点赞操作失败:', error)
      // 检查是否是401未授权错误
      const status = error?.response?.status
      const message = error?.message || ''
      
      if (status === 401 || message.includes('401') || message.includes('未授权')) {
        ElMessage.warning('请先登录')
        router.push('/login')
      } else {
        // 后端可能暂时不可用（500错误等），使用本地模拟
        console.log('后端不可用，使用本地模拟点赞')
        toggleLocalVote()
      }
    }
  } else {
    // 示例数据：本地模拟点赞
    toggleLocalVote()
  }
}

// 本地模拟点赞（用于示例数据或后端不可用时）
const toggleLocalVote = () => {
  if (!currentWork.value) return
  
  if (currentWork.value.hasVoted) {
    currentWork.value.hasVoted = false
    currentWork.value.votes = Math.max(0, (currentWork.value.votes || 1) - 1)
    ElMessage.success('已取消点赞')
  } else {
    currentWork.value.hasVoted = true
    currentWork.value.votes = (currentWork.value.votes || 0) + 1
    ElMessage.success('点赞成功！感谢您的支持')
  }
  
  // 同步更新列表中的数据
  const workInList = works.value.find(w => w.id === currentWork.value?.id)
  if (workInList) {
    workInList.hasVoted = currentWork.value.hasVoted
    workInList.votes = currentWork.value.votes
  }
}

// 联系设计师弹窗状态
const contactDialogVisible = ref(false)
const contactMessage = ref('')

// 联系管理员弹窗状态
const contactAdminDialogVisible = ref(false)
const adminContactMessage = ref('')

// 复制到剪贴板
const copyToClipboard = (text: string) => {
  navigator.clipboard.writeText(text).then(() => {
    ElMessage.success('已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败，请手动复制')
  })
}

// 发送管理员留言
const sendAdminMessage = async () => {
  if (!adminContactMessage.value.trim()) {
    ElMessage.warning('请输入留言内容')
    return
  }
  
  // 检查登录状态
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再发送留言')
    router.push('/login')
    return
  }
  
  try {
    // 这里可以调用后端API发送留言给管理员
    // 暂时模拟发送成功
    ElMessage.success('留言已发送，管理员会尽快回复您')
    contactAdminDialogVisible.value = false
    adminContactMessage.value = ''
  } catch (error: any) {
    console.error('发送留言失败:', error)
    ElMessage.error('发送失败，请稍后重试')
  }
}

// 商城上架申请相关
const mallApplicationDialogVisible = ref(false)
const submittingApplication = ref(false)
const mallApplicationForm = ref({
  productName: '',
  category: '文化周边',
  description: '',
  suggestedPrice: 99,
  initialStock: 100,
  icon: '🎁'
})
const productIcons = ['🎁', '🎨', '📚', '🖼️', '👕', '☕', '🎭', '🏮', '🎪', '🎯']

// 判断是否是自己的作品
const isMyWork = (work: Work | null) => {
  if (!work || !userStore.isLoggedIn) return false
  return work.designerId === userStore.userInfo?.id
}

// 打开商城申请弹窗
const openMallApplicationDialog = () => {
  if (!currentWork.value) return
  
  // 检查登录状态
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  // 预填表单
  mallApplicationForm.value = {
    productName: currentWork.value.title + ' 文创商品',
    category: '文化周边',
    description: currentWork.value.description,
    suggestedPrice: 99,
    initialStock: 100,
    icon: '🎁'
  }
  
  mallApplicationDialogVisible.value = true
}

// 提交商城上架申请
const submitMallApplication = async () => {
  if (!currentWork.value) return
  
  // 验证表单
  if (!mallApplicationForm.value.productName.trim()) {
    ElMessage.warning('请输入商品名称')
    return
  }
  if (!mallApplicationForm.value.suggestedPrice || mallApplicationForm.value.suggestedPrice <= 0) {
    ElMessage.warning('请输入有效的价格')
    return
  }
  
  submittingApplication.value = true
  
  try {
    await creativeApi.submitMallApplication({
      designId: currentWork.value.id,
      productName: mallApplicationForm.value.productName,
      category: mallApplicationForm.value.category,
      description: mallApplicationForm.value.description,
      suggestedPrice: mallApplicationForm.value.suggestedPrice,
      initialStock: mallApplicationForm.value.initialStock,
      icon: mallApplicationForm.value.icon
    })
    
    ElMessage.success('申请提交成功，请等待管理员审核')
    mallApplicationDialogVisible.value = false
    
    // 标记作品已申请
    if (currentWork.value) {
      currentWork.value.hasAppliedToMall = true
    }
  } catch (error: any) {
    console.error('提交申请失败:', error)
    ElMessage.error(error.response?.data?.message || '提交失败，请稍后重试')
  } finally {
    submittingApplication.value = false
  }
}

// 联系设计师
const contactDesigner = () => {
  if (!currentWork.value) return
  
  // 检查登录状态
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再联系设计师')
    router.push('/login')
    return
  }
  
  // 打开联系弹窗
  contactDialogVisible.value = true
  contactMessage.value = ''
}

// 发送联系消息
const sendContactMessage = async () => {
  if (!contactMessage.value.trim()) {
    ElMessage.warning('请输入留言内容')
    return
  }
  
  if (!currentWork.value) return
  
  try {
    // 这里可以调用后端API发送私信
    // 暂时模拟发送成功
    ElMessage.success(`已向设计师 ${currentWork.value.designer} 发送留言`)
    contactDialogVisible.value = false
    contactMessage.value = ''
  } catch (error: any) {
    console.error('发送消息失败:', error)
    ElMessage.error('发送失败，请稍后重试')
  }
}

// 判断是否为视频作品
const isVideoWork = (work: Work) => {
  if (!work) {
    return false
  }
  
  // 检查 category
  const categoryCheck = work.category === 'video'
  
  // 检查 type
  const typeCheck = work.type && (
    work.type.includes('视频') || 
    work.type.includes('动画') ||
    work.type.includes('演艺')
  )
  
  // 检查特定ID（临时方案）
  const idCheck = work.id === 4 || work.id === 9 || work.id === '4' || work.id === '9'
  
  const result = categoryCheck || typeCheck || idCheck
  
  return result
}

// 获取视频URL
const getVideoUrl = (work: Work) => {
  // 只有视频作品才返回视频URL
  if (isVideoWork(work)) {
    return 'https://www.w3schools.com/html/mov_bbb.mp4'
  }
  
  // 非视频作品返回空字符串
  return ''
}

// 获取媒体URL（统一处理封面、图片、视频）
const getMediaUrl = (work: Work, type: 'cover' | 'image' | 'video') => {
  if (!work) {
    console.warn('作品数据为空')
    return ''
  }
  
  console.log('=== 获取媒体URL ===')
  console.log('作品:', work.title)
  console.log('类型:', type)
  console.log('coverImage:', work.coverImage)
  console.log('files:', work.files)
  console.log('files 类型:', typeof work.files, Array.isArray(work.files))
  
  // 处理 files 字段（确保是数组）
  let filesArray: string[] = []
  if (work.files) {
    if (Array.isArray(work.files)) {
      filesArray = work.files.filter(f => f && typeof f === 'string' && f.trim().length > 0)
    } else if (typeof work.files === 'string') {
      // 尝试解析 JSON
      try {
        const parsed = JSON.parse(work.files)
        if (Array.isArray(parsed)) {
          filesArray = parsed.filter(f => f && typeof f === 'string' && f.trim().length > 0)
        } else {
          filesArray = work.files.split(',').map(f => f.trim()).filter(f => f.length > 0)
        }
      } catch (e) {
        filesArray = work.files.split(',').map(f => f.trim()).filter(f => f.length > 0)
      }
    }
  }
  
  console.log('解析后的文件数组:', filesArray)
  
  // 根据类型返回URL
  if (type === 'cover') {
    // 封面：优先使用 coverImage，否则使用第一个文件
    const url = (work.coverImage && work.coverImage.trim()) || (filesArray.length > 0 ? filesArray[0] : '')
    console.log('封面URL:', url)
    return url
  } else if (type === 'video') {
    // 视频：优先使用 files 中的视频文件，否则使用测试视频
    const videoFile = filesArray.find(f => 
      f.toLowerCase().includes('.mp4') || 
      f.toLowerCase().includes('.webm') || 
      f.toLowerCase().includes('.mov') || 
      f.toLowerCase().includes('video')
    )
    const url = videoFile || (filesArray.length > 0 ? filesArray[0] : 'https://www.w3schools.com/html/mov_bbb.mp4')
    console.log('视频URL:', url)
    return url
  } else if (type === 'image') {
    // 图片：优先使用 coverImage，否则使用 files 中的第一个文件
    const url = (work.coverImage && work.coverImage.trim()) || (filesArray.length > 0 ? filesArray[0] : '')
    console.log('图片URL:', url)
    return url
  }
  
  return ''
}

// 处理媒体加载错误
const handleMediaError = (event: Event) => {
  const target = event.target as HTMLImageElement | HTMLVideoElement
  console.error('媒体加载失败:', target.src)
  ElMessage.warning('媒体文件加载失败，请检查文件URL是否正确')
}

// 判断是否为视频文件
const isVideoFile = (url: string) => {
  if (!url) return false
  const lowerUrl = url.toLowerCase()
  return lowerUrl.includes('.mp4') || 
         lowerUrl.includes('.webm') || 
         lowerUrl.includes('.mov') || 
         lowerUrl.includes('.avi') ||
         lowerUrl.includes('/videos/')
}

// 在新标签页中打开文件
const openFileInNewTab = (url: string) => {
  window.open(url, '_blank')
}

// 根据分类获取图标
const getCategoryIcon = (category: string) => {
  const iconMap: Record<string, string> = {
    'poster': '🖼️',
    'logo': '🎯',
    'product': '🎁',
    'video': '🎬'
  }
  return iconMap[category] || '🎨'
}

// 格式化数字显示
const formatNumber = (num: number) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

// 上传作品
const uploadWork = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后上传作品')
    router.push('/login')
    return
  }
  // 跳转到上传页面
  router.push('/creative/upload')
}

// 提交作品
const submitWork = async () => {
  if (!uploadForm.value.title || !uploadForm.value.description) {
    ElMessage.warning('请填写作品标题和描述')
    return
  }
  
  try {
    const designData = {
      title: uploadForm.value.title,
      categoryType: uploadForm.value.categoryType,
      description: uploadForm.value.description,
      designConcept: uploadForm.value.designConcept,
      coverImage: uploadForm.value.coverImage,
      files: uploadForm.value.files.filter(f => f.trim().length > 0).join(','),
      copyrightStatement: uploadForm.value.copyrightStatement,
      tags: ''
    }
    
    await creativeApi.createDesign(designData)
    ElMessage.success('作品上传成功！')
    uploadDialogVisible.value = false
    
    // 重置表单
    uploadForm.value = {
      title: '',
      categoryType: 1,
      description: '',
      designConcept: '',
      coverImage: '',
      files: [''],
      copyrightStatement: ''
    }
    
    // 重新加载作品列表
    setTimeout(() => {
      updateCategoryCounts()
      loading.value = false
    }, 500)
  } catch (error: any) {
    console.error('上传失败:', error)
    ElMessage.error(error.response?.data?.message || '上传失败，请稍后重试')
  }
}

// 查看我的作品
const showMyWorks = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  try {
    loading.value = true
    showingMyWorks.value = true
    
    const response = await creativeApi.getMyDesigns({ page: 1, size: 100 })
    console.log('=== 我的作品 API 响应 ===')
    console.log('response:', response)
    
    // 处理响应
    let designsData: any[] = []
    
    if (response && response.code === 200 && response.data && response.data.records) {
      designsData = response.data.records
    } else if (response && response.code === 200 && Array.isArray(response.data)) {
      designsData = response.data
    } else if (Array.isArray(response)) {
      designsData = response
    }
    
    if (designsData && designsData.length > 0) {
      // 转换后端数据格式为前端格式
      works.value = designsData.map((design: DesignVO) => {
        // 处理 files 字段
        let filesArray: string[] = []
        if (design.files) {
          if (Array.isArray(design.files)) {
            filesArray = design.files.filter(f => f && f.trim && f.trim().length > 0)
          } else if (typeof design.files === 'string') {
            try {
              const parsed = JSON.parse(design.files)
              if (Array.isArray(parsed)) {
                filesArray = parsed.filter(f => f && f.trim && f.trim().length > 0)
              } else {
                filesArray = design.files.split(',').map(f => f.trim()).filter(f => f.length > 0)
              }
            } catch (e) {
              filesArray = design.files.split(',').map(f => f.trim()).filter(f => f.length > 0)
            }
          }
        }
        
        let coverImage = design.coverImage
        if (!coverImage || coverImage.trim() === '') {
          coverImage = filesArray.length > 0 ? filesArray[0] : 'https://images.unsplash.com/photo-1558591710-4b4a1ae0f04d?w=800&auto=format&fit=crop'
        }
        
        return {
          id: design.id,
          title: design.title,
          category: getCategoryKey(design.categoryType),
          description: design.description,
          designer: '设计师' + design.designerId,
          designerId: design.designerId,
          type: getCategoryLabel(design.categoryType),
          coverImage: coverImage,
          files: filesArray,
          views: design.views || 0,
          votes: design.votes || 0,
          likes: design.votes || 0,
          comments: 0,
          tags: design.tags ? design.tags.split(',') : [],
          createTime: design.createdAt ? new Date(design.createdAt).toLocaleDateString() : '',
          hasVoted: design.hasVoted
        }
      })
      
      updateCategoryCounts()
      ElMessage.success(`找到 ${works.value.length} 个我的作品`)
    } else {
      works.value = []
      updateCategoryCounts()
      ElMessage.info('您还没有上传作品，快去创作吧！')
    }
  } catch (error: any) {
    console.error('加载我的作品失败:', error)
    // 检查是否是401未授权错误
    const status = error?.response?.status
    if (status === 401) {
      ElMessage.warning('请先登录')
      router.push('/login')
    } else {
      ElMessage.error('加载失败，请稍后重试')
    }
    showingMyWorks.value = false
  } finally {
    loading.value = false
  }
}

// 返回全部作品
const showAllWorks = async () => {
  showingMyWorks.value = false
  await loadDesigns()
}

onMounted(() => { 
  loadDesigns()
})

// 加载作品列表
const loadDesigns = async () => {
  try {
    loading.value = true
    
    // 使用 getDesigns 获取所有作品（不只是热门作品）
    const response = await creativeApi.getDesigns({ page: 1, size: 100 })
    console.log('=== API 响应 ===')
    console.log('response:', response)
    console.log('response 类型:', typeof response)
    console.log('response.code:', response?.code)
    console.log('response.data:', response?.data)
    console.log('response.data.records:', response?.data?.records)
    
    // 处理响应：可能是多种格式
    let designsData: any[] = []
    
    // 1. 检查是否是新格式: {code: 200, data: {records: [...], total: ...}}
    if (response && response.code === 200 && response.data && response.data.records) {
      designsData = response.data.records
      console.log('✅ 使用新格式 (records)，作品数量:', designsData.length)
    }
    // 2. 检查是否是标准格式: {code: 200, data: [...]}
    else if (response && response.code === 200 && Array.isArray(response.data)) {
      designsData = response.data
      console.log('使用标准格式，作品数据:', designsData)
    } 
    // 3. 检查是否是分页格式: {records: [...], total: ...}
    else if (response && response.records) {
      designsData = response.records
      console.log('分页格式，作品数据:', designsData)
    } 
    // 4. 检查是否是直接数组
    else if (Array.isArray(response)) {
      designsData = response
      console.log('直接数组格式，作品数据:', designsData)
    } 
    // 5. 检查是否是对象格式（键为数字索引）
    else if (response && typeof response === 'object') {
      // 尝试从对象中提取数组
      const keys = Object.keys(response).filter(k => !isNaN(Number(k)))
      if (keys.length > 0) {
        designsData = keys.map(k => (response as any)[k])
        console.log('对象索引格式，作品数据:', designsData)
      } else if (response.data && Array.isArray(response.data)) {
        designsData = response.data
        console.log('data 数组格式，作品数据:', designsData)
      }
    }
    
    if (designsData && designsData.length > 0) {
      console.log('作品数量:', designsData.length)
      
      // 转换后端数据格式为前端格式
      works.value = designsData.map((design: DesignVO) => {
        console.log('=== 处理作品数据 ===')
        console.log('原始 design:', design)
        console.log('design.files 类型:', typeof design.files)
        console.log('design.files 值:', design.files)
        console.log('design.coverImage:', design.coverImage)
        
        // 处理 files 字段 - 后端返回的是数组（已经从JSON解析）
        let filesArray: string[] = []
        if (design.files) {
          if (Array.isArray(design.files)) {
            // 后端已经将 JSON 解析为数组
            filesArray = design.files.filter(f => f && f.trim && f.trim().length > 0)
          } else if (typeof design.files === 'string') {
            // 如果是字符串，尝试解析 JSON
            try {
              const parsed = JSON.parse(design.files)
              if (Array.isArray(parsed)) {
                filesArray = parsed.filter(f => f && f.trim && f.trim().length > 0)
              } else {
                // 如果不是数组，按逗号分隔
                filesArray = design.files.split(',').map(f => f.trim()).filter(f => f.length > 0)
              }
            } catch (e) {
              // JSON 解析失败，按逗号分隔
              filesArray = design.files.split(',').map(f => f.trim()).filter(f => f.length > 0)
            }
          }
        }
        
        console.log('解析后的 filesArray:', filesArray)
        
        // 如果没有 coverImage，使用 files 中的第一个文件
        let coverImage = design.coverImage
        if (!coverImage || coverImage.trim() === '') {
          coverImage = filesArray.length > 0 ? filesArray[0] : 'https://images.unsplash.com/photo-1558591710-4b4a1ae0f04d?w=800&auto=format&fit=crop'
        }
        
        console.log('最终 coverImage:', coverImage)
        
        const work = {
          id: design.id,
          title: design.title,
          category: getCategoryKey(design.categoryType),
          description: design.description,
          designer: '设计师' + design.designerId,
          designerId: design.designerId,
          type: getCategoryLabel(design.categoryType),
          coverImage: coverImage,
          files: filesArray,  // 保存为数组
          views: design.views || 0,
          votes: design.votes || 0,
          likes: design.votes || 0,
          comments: 0,
          tags: design.tags ? design.tags.split(',') : [],
          createTime: design.createdAt ? new Date(design.createdAt).toLocaleDateString() : '',
          hasVoted: design.hasVoted
        }
        console.log('转换后的作品:', work)
        return work
      })
      
      console.log('最终 works 数组:', works.value)
      console.log('works 数量:', works.value.length)
      
      updateCategoryCounts()
    } else {
      console.error('API 响应格式错误或无数据:', response)
      works.value = []
    }
  } catch (error) {
    console.error('加载作品失败:', error)
    ElMessage.error('加载作品失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 根据分类类型获取分类key
const getCategoryKey = (categoryType?: number) => {
  const map: Record<number, string> = {
    1: 'poster',
    2: 'logo',
    3: 'product',
    4: 'video',
    5: 'illustration',
    6: 'craft',
    7: 'digital',
    8: 'sculpture',
    9: 'calligraphy'
  }
  return categoryType ? map[categoryType] || 'poster' : 'poster'
}

// 根据分类类型获取分类标签
const getCategoryLabel = (categoryType?: number) => {
  const map: Record<number, string> = {
    1: '海报设计',
    2: 'Logo设计',
    3: '文创产品',
    4: '视频动画',
    5: '插画绘本',
    6: '手工艺品',
    7: '数字艺术',
    8: '雕塑模型',
    9: '书法篆刻'
  }
  return categoryType ? map[categoryType] || '海报设计' : '海报设计'
}
</script>

<style scoped>
.creative-page { 
  min-height: 100vh; 
  background: #f5f7fa;
  background-image: radial-gradient(rgba(160, 24, 47, 0.05) 1px, transparent 1px), radial-gradient(rgba(160, 24, 47, 0.05) 1px, #f5f7fa 1px);
  background-size: 20px 20px;
  background-position: 0 0, 10px 10px;
}

/* Hero Section */
.creative-hero {
  background: linear-gradient(135deg, #8b1538 0%, #a0182f 25%, #c41e3a 75%, #d4243f 100%);
  color: white;
  padding: 7rem 2rem 6rem;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  margin-bottom: 2rem;
  box-shadow: 0 10px 40px rgba(160, 24, 47, 0.3);
}

.creative-hero::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    radial-gradient(circle at 15% 85%, rgba(255, 215, 0, 0.15) 0%, transparent 25%),
    radial-gradient(circle at 85% 15%, rgba(255, 255, 255, 0.12) 0%, transparent 25%),
    radial-gradient(circle at 50% 50%, rgba(255, 255, 255, 0.08) 0%, transparent 40%);
  opacity: 0.8;
  animation: heroGlow 8s ease-in-out infinite;
}

@keyframes heroGlow {
  0%, 100% { opacity: 0.8; }
  50% { opacity: 1; }
}

.creative-hero::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.06'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
  animation: patternMove 30s linear infinite;
}

@keyframes patternMove {
  0% { background-position: 0 0; }
  100% { background-position: 60px 60px; }
}

.hero-content {
  position: relative;
  z-index: 2;
  max-width: 800px;
}

.hero-icon-wrapper {
  width: 110px;
  height: 110px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.2), rgba(255, 215, 0, 0.15));
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 2rem;
  backdrop-filter: blur(15px);
  border: 2px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 15px 50px rgba(0,0,0,0.2), inset 0 0 20px rgba(255,255,255,0.1);
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  animation: iconFloat 3s ease-in-out infinite;
}

@keyframes iconFloat {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

.hero-icon-wrapper:hover {
  transform: scale(1.15) rotate(10deg);
  box-shadow: 0 20px 60px rgba(255, 215, 0, 0.4), inset 0 0 30px rgba(255,255,255,0.2);
  border-color: rgba(255, 215, 0, 0.6);
}

.hero-icon {
  font-size: 4rem;
  filter: drop-shadow(0 4px 8px rgba(0,0,0,0.2));
}

.hero-title {
  font-size: 3.8rem;
  font-weight: 900;
  margin-bottom: 1.2rem;
  letter-spacing: 3px;
  text-shadow: 0 6px 20px rgba(0,0,0,0.4), 0 2px 4px rgba(0,0,0,0.3);
  background: linear-gradient(135deg, #fff 0%, #ffd700 50%, #fff 100%);
  background-size: 200% auto;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: shimmer 3s linear infinite;
}

@keyframes shimmer {
  0% { background-position: 0% center; }
  100% { background-position: 200% center; }
}

.hero-subtitle {
  font-size: 1.4rem;
  opacity: 0.98;
  margin-bottom: 3rem;
  max-width: 650px;
  margin-left: auto;
  margin-right: auto;
  line-height: 1.8;
  font-weight: 400;
  text-shadow: 0 2px 8px rgba(0,0,0,0.3);
  letter-spacing: 0.5px;
}

.hero-actions {
  display: flex;
  justify-content: center;
  gap: 1.5rem;
}

.action-btn {
  padding: 12px 35px;
  font-weight: 600;
  letter-spacing: 1px;
  border-radius: 30px;
  transition: all 0.3s;
  font-size: 1.1rem;
}

.action-btn.primary {
  background: linear-gradient(135deg, #fff 0%, #fffacd 100%);
  color: #a0182f;
  border: none;
  box-shadow: 0 6px 20px rgba(255, 215, 0, 0.3), 0 2px 8px rgba(0,0,0,0.1);
  position: relative;
  overflow: hidden;
}

.action-btn.primary::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.6), transparent);
  transition: left 0.5s;
}

.action-btn.primary:hover::before {
  left: 100%;
}

.action-btn.primary:hover {
  background: linear-gradient(135deg, #fffacd 0%, #ffd700 100%);
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 10px 30px rgba(255, 215, 0, 0.5), 0 4px 12px rgba(0,0,0,0.2);
}

.action-btn.secondary {
  background: rgba(255, 255, 255, 0.18);
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(10px);
  position: relative;
  overflow: hidden;
}

.action-btn.secondary::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  transform: translate(-50%, -50%);
  transition: width 0.5s, height 0.5s;
}

.action-btn.secondary:hover::before {
  width: 300px;
  height: 300px;
}

.action-btn.secondary:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-4px) scale(1.05);
  border-color: #fff;
  box-shadow: 0 8px 25px rgba(255,255,255,0.3);
}

.creative-container { 
  max-width: 1400px; 
  margin: 0 auto; 
  padding: 0 2rem 4rem; 
  position: relative;
  z-index: 3;
  margin-top: -4rem;
}

/* 我的作品提示条 */
.my-works-banner {
  background: linear-gradient(135deg, #fff5f7 0%, #ffe8ec 100%);
  border: 1px solid rgba(160, 24, 47, 0.2);
  border-radius: 16px;
  padding: 1rem 1.5rem;
  margin-bottom: 1.5rem;
  box-shadow: 0 4px 15px rgba(160, 24, 47, 0.1);
}

.my-works-banner .banner-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.my-works-banner .banner-icon {
  font-size: 20px;
  color: #a0182f;
}

.my-works-banner .banner-text {
  font-size: 15px;
  color: #a0182f;
  font-weight: 500;
  flex: 1;
}

.my-works-banner .banner-btn {
  border-color: #a0182f;
  color: #a0182f;
}

.my-works-banner .banner-btn:hover {
  background: #a0182f;
  color: white;
}

/* Tabs */
.category-tabs { 
  display: flex; 
  flex-wrap: wrap;
  gap: 0.8rem; 
  margin-bottom: 3rem; 
  padding: 1.5rem;
  background: linear-gradient(135deg, #ffffff 0%, #fafafa 100%);
  border-radius: 24px;
  box-shadow: 0 15px 50px rgba(0,0,0,0.1), inset 0 1px 0 rgba(255,255,255,0.8);
  justify-content: center;
  border: 1px solid rgba(160, 24, 47, 0.08);
  position: relative;
}

.category-tabs::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #a0182f, #ffd700, #a0182f);
  background-size: 200% auto;
  animation: gradientSlide 3s linear infinite;
}

@keyframes gradientSlide {
  0% { background-position: 0% center; }
  100% { background-position: 200% center; }
}

.category-tab { 
  display: flex; 
  align-items: center; 
  gap: 0.5rem; 
  padding: 0.7rem 1.2rem; 
  background: linear-gradient(135deg, #fff5f5 0%, #ffe6e6 100%); 
  border-radius: 12px; 
  cursor: pointer; 
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1); 
  white-space: nowrap; 
  border: 2px solid rgba(160, 24, 47, 0.12);
  color: #a0182f;
  position: relative;
  overflow: hidden;
  font-size: 0.9rem;
}

.category-tab::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.5), transparent);
  transition: left 0.6s;
}

.category-tab:hover::before {
  left: 100%;
}

.category-tab:hover { 
  transform: translateY(-5px) scale(1.03); 
  background: linear-gradient(135deg, #ffe6e6 0%, #ffd6d6 100%);
  box-shadow: 0 8px 25px rgba(160, 24, 47, 0.2); 
  border-color: rgba(160, 24, 47, 0.4);
}

.category-tab.active { 
  background: linear-gradient(135deg, #8b1538 0%, #a0182f 50%, #c41e3a 100%);
  color: white; 
  box-shadow: 0 12px 30px rgba(160, 24, 47, 0.4), inset 0 1px 0 rgba(255,255,255,0.2);
  border-color: transparent;
  transform: translateY(-3px) scale(1.05);
}

.tab-icon { font-size: 1.2rem; }
.tab-label { font-weight: 600; font-size: 1.05rem; }

.tab-count { 
  background: rgba(160, 24, 47, 0.1); 
  padding: 0.2rem 0.6rem; 
  border-radius: 10px; 
  font-size: 0.85rem; 
  font-weight: 700;
  color: #a0182f;
}

.category-tab.active .tab-count { 
  background: rgba(255, 255, 255, 0.25); 
  color: white; 
}

/* Grid */
.works-grid { 
  display: grid; 
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr)); 
  gap: 2rem; 
  animation: fadeIn 0.6s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.work-card { 
  background: #ffffff; 
  border-radius: 20px; 
  overflow: hidden; 
  cursor: pointer; 
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1); 
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(160, 24, 47, 0.05);
  position: relative;
}

.work-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #a0182f, #ffd700, #a0182f);
  opacity: 0;
  transition: opacity 0.4s;
}

.work-card:hover::before {
  opacity: 1;
}

.work-card:hover { 
  transform: translateY(-12px); 
  box-shadow: 0 20px 50px rgba(160, 24, 47, 0.15), 0 8px 20px rgba(0,0,0,0.08); 
  border-color: rgba(160, 24, 47, 0.2);
}

.work-cover-wrapper {
  position: relative;
  overflow: hidden;
}

.work-cover { 
  height: 280px; 
  background-size: cover; 
  background-position: center; 
  position: relative; 
  overflow: hidden;
  transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.work-card:hover .work-cover {
  transform: scale(1.08);
}

.work-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(160, 24, 47, 0.85) 0%, rgba(139, 21, 56, 0.9) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(10px);
}

.overlay-content {
  text-align: center;
  transform: translateY(20px);
  transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.work-card:hover .work-overlay {
  opacity: 1;
}

.work-card:hover .overlay-content {
  transform: translateY(0);
}

.view-btn {
  background: linear-gradient(135deg, #fff 0%, #fffacd 100%);
  color: #a0182f;
  border: none;
  box-shadow: 0 8px 25px rgba(255, 215, 0, 0.4);
  transition: all 0.3s;
  width: 60px;
  height: 60px;
  font-size: 1.5rem;
}

.view-btn:hover {
  transform: scale(1.15) rotate(10deg);
  box-shadow: 0 12px 35px rgba(255, 215, 0, 0.6);
}

.overlay-text {
  color: white;
  font-size: 1rem;
  font-weight: 600;
  margin-top: 1rem;
  letter-spacing: 1px;
  text-shadow: 0 2px 8px rgba(0,0,0,0.3);
}

.work-badge { 
  position: absolute; 
  top: 1rem; 
  right: 1rem; 
  background: linear-gradient(135deg, rgba(160, 24, 47, 0.95) 0%, rgba(196, 30, 58, 0.95) 100%); 
  color: white; 
  padding: 0.5rem 1rem; 
  border-radius: 20px; 
  font-weight: 700; 
  font-size: 0.85rem; 
  box-shadow: 0 4px 15px rgba(0,0,0,0.3);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255,255,255,0.2);
  display: flex;
  align-items: center;
  gap: 0.4rem;
  transition: all 0.3s;
  z-index: 2;
}

.badge-icon {
  font-size: 1rem;
}

.badge-text {
  letter-spacing: 0.5px;
}

.work-card:hover .work-badge {
  transform: scale(1.05);
  box-shadow: 0 6px 20px rgba(0,0,0,0.4);
}

.work-hot-badge {
  position: absolute;
  top: 1rem;
  left: 1rem;
  background: linear-gradient(135deg, rgba(255, 87, 34, 0.95) 0%, rgba(244, 67, 54, 0.95) 100%);
  color: white;
  padding: 0.4rem 0.8rem;
  border-radius: 15px;
  font-weight: 700;
  font-size: 0.75rem;
  box-shadow: 0 4px 15px rgba(255, 87, 34, 0.4);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255,255,255,0.3);
  display: flex;
  align-items: center;
  gap: 0.3rem;
  z-index: 2;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.hot-icon {
  font-size: 0.9rem;
  animation: flicker 1.5s ease-in-out infinite;
}

@keyframes flicker {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

.hot-text {
  letter-spacing: 0.5px;
}

.work-info { 
  padding: 1.5rem; 
}

.work-title { 
  font-size: 1.25rem; 
  font-weight: 800; 
  color: #1a1a1a; 
  margin-bottom: 1rem;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: all 0.3s;
}

.work-card:hover .work-title {
  color: #a0182f;
}

.designer-row {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #f0f0f0;
}

.designer-avatar {
  background: linear-gradient(135deg, #a0182f 0%, #c41e3a 100%);
  color: white;
  font-size: 0.85rem;
  font-weight: 700;
  box-shadow: 0 2px 8px rgba(160, 24, 47, 0.3);
  transition: all 0.3s;
}

.work-card:hover .designer-avatar {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(160, 24, 47, 0.4);
}

.avatar-text {
  display: block;
}

.work-designer { 
  font-size: 0.9rem; 
  color: #555; 
  font-weight: 600;
  flex: 1;
}

.designer-badge {
  background: linear-gradient(135deg, #fff5f5 0%, #ffe6e6 100%);
  border: 1px solid rgba(160, 24, 47, 0.15);
  color: #a0182f;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 8px;
}

.work-description { 
  font-size: 0.9rem; 
  color: #777; 
  line-height: 1.7; 
  margin-bottom: 1rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 2.8em;
}

.work-meta { 
  display: flex; 
  gap: 1.5rem; 
  margin-bottom: 1rem; 
}

.meta-item { 
  display: flex; 
  align-items: center; 
  gap: 0.4rem; 
  font-size: 0.85rem; 
  color: #999;
  transition: all 0.3s;
}

.meta-item:hover {
  color: #a0182f;
  transform: translateY(-2px);
}

.meta-icon {
  font-size: 1rem;
}

.meta-value {
  font-weight: 600;
}

.work-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.work-tags {
  display: flex;
  gap: 0.4rem;
  flex-wrap: wrap;
  flex: 1;
}

.custom-tag {
  border-radius: 10px;
  background: linear-gradient(135deg, #fff5f5 0%, #ffe6e6 100%);
  border: 1px solid rgba(160, 24, 47, 0.12);
  color: #a0182f;
  transition: all 0.3s ease;
  font-weight: 600;
  padding: 3px 10px;
  box-shadow: 0 2px 6px rgba(160, 24, 47, 0.06);
  font-size: 0.75rem;
}

.custom-tag:hover {
  background: linear-gradient(135deg, #ffe6e6 0%, #ffd6d6 100%);
  border-color: rgba(160, 24, 47, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(160, 24, 47, 0.12);
}

.more-tag {
  background: linear-gradient(135deg, #f0f0f0 0%, #e0e0e0 100%);
  border-color: #d0d0d0;
  color: #666;
}

.work-time {
  font-size: 0.75rem;
  color: #aaa;
  white-space: nowrap;
  font-weight: 500;
}

/* Empty State */
.works-empty {
  background: linear-gradient(135deg, #ffffff 0%, #fafafa 100%);
  border-radius: 24px;
  padding: 5rem 2rem;
  text-align: center;
  box-shadow: 0 8px 30px rgba(0,0,0,0.06);
  border: 1px solid rgba(160, 24, 47, 0.08);
  position: relative;
  overflow: hidden;
}

.works-empty::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 50% 50%, rgba(160, 24, 47, 0.03) 0%, transparent 70%);
  pointer-events: none;
}

.empty-animation {
  position: relative;
  margin-bottom: 2rem;
}

.empty-icon-wrapper {
  position: relative;
  display: inline-block;
}

.empty-icon {
  font-size: 6rem;
  display: block;
  filter: grayscale(0.3) opacity(0.6);
  animation: emptyIconFloat 3s ease-in-out infinite;
}

@keyframes emptyIconFloat {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-15px) rotate(5deg); }
}

.empty-circle {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 120px;
  height: 120px;
  border: 3px solid rgba(160, 24, 47, 0.1);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  animation: emptyCirclePulse 2s ease-in-out infinite;
}

.empty-circle-2 {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 150px;
  height: 150px;
  border: 2px solid rgba(160, 24, 47, 0.05);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  animation: emptyCirclePulse 2s ease-in-out infinite 0.5s;
}

@keyframes emptyCirclePulse {
  0%, 100% { transform: translate(-50%, -50%) scale(1); opacity: 1; }
  50% { transform: translate(-50%, -50%) scale(1.2); opacity: 0.5; }
}

.empty-title {
  font-size: 1.8rem;
  font-weight: 800;
  color: #2c3e50;
  margin-bottom: 0.8rem;
  position: relative;
}

.empty-text {
  font-size: 1.1rem;
  font-weight: 600;
  color: #666;
  margin-bottom: 0.5rem;
}

.empty-subtext {
  font-size: 0.95rem;
  color: #999;
  margin-bottom: 2rem;
}

.empty-action-btn {
  padding: 14px 40px;
  font-weight: 700;
  letter-spacing: 1px;
  border-radius: 30px;
  background: linear-gradient(135deg, #a0182f 0%, #c41e3a 100%);
  border: none;
  box-shadow: 0 8px 25px rgba(160, 24, 47, 0.3);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.empty-action-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
  transition: left 0.6s;
}

.empty-action-btn:hover::before {
  left: 100%;
}

.empty-action-btn:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 12px 35px rgba(160, 24, 47, 0.4);
}

/* Loading State */
.works-loading {
  padding: 2rem 0;
}

.skeleton-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 2rem;
}

.skeleton-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.skeleton-cover {
  height: 280px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: skeletonLoading 1.5s ease-in-out infinite;
}

@keyframes skeletonLoading {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

.skeleton-content {
  padding: 1.5rem;
}

/* Dialog Styles */
.work-dialog {
  border-radius: 24px;
  overflow: hidden;
}

.work-dialog :deep(.el-dialog__header) {
  display: none;
}

.work-dialog :deep(.el-dialog__body) {
  padding: 0;
  background: #f8f9fa;
}

.work-detail {
  position: relative;
  max-height: 90vh;
  overflow-y: auto;
}

.detail-close {
  position: absolute;
  top: 1.5rem;
  right: 1.5rem;
  width: 48px;
  height: 48px;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 100;
  transition: all 0.3s;
  color: white;
  font-size: 1.5rem;
}

.detail-close:hover {
  background: rgba(160, 24, 47, 0.9);
  transform: scale(1.1) rotate(90deg);
  box-shadow: 0 8px 25px rgba(160, 24, 47, 0.4);
}

/* Media Display */
.detail-media {
  position: relative;
  width: 100%;
  background: #000;
  overflow: hidden;
}

.media-video,
.media-image {
  width: 100%;
  max-height: 600px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #000;
}

.video-player {
  width: 100%;
  max-height: 600px;
  object-fit: contain;
  background: #000;
}

.video-tip {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  color: white;
  z-index: 10;
}

.tip-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
  opacity: 0.6;
}

.video-tip p {
  font-size: 1.2rem;
  font-weight: 600;
  opacity: 0.8;
}

.image-tip {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  color: #999;
  z-index: 10;
  background: rgba(255, 255, 255, 0.9);
  padding: 2rem;
  border-radius: 12px;
}

.image-tip .tip-icon {
  color: #ccc;
}

.image-tip p {
  font-size: 1.2rem;
  font-weight: 600;
  color: #666;
}

.detail-image {
  width: 100%;
  max-height: 600px;
  object-fit: contain;
  display: block;
}

.media-badge {
  position: absolute;
  top: 1.5rem;
  left: 1.5rem;
  background: linear-gradient(135deg, rgba(160, 24, 47, 0.95) 0%, rgba(196, 30, 58, 0.95) 100%);
  color: white;
  padding: 0.6rem 1.2rem;
  border-radius: 20px;
  font-weight: 700;
  font-size: 0.9rem;
  box-shadow: 0 4px 15px rgba(0,0,0,0.3);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255,255,255,0.2);
  display: flex;
  align-items: center;
  gap: 0.5rem;
  z-index: 10;
}

.badge-icon {
  font-size: 1.1rem;
}

/* Detail Body */
.detail-body {
  background: white;
  padding: 2.5rem;
}

.detail-header-info {
  margin-bottom: 2rem;
  padding-bottom: 2rem;
  border-bottom: 2px solid #f0f0f0;
}

.detail-title {
  font-size: 2rem;
  font-weight: 900;
  color: #1a1a1a;
  margin-bottom: 1.5rem;
  line-height: 1.4;
  background: linear-gradient(135deg, #2c3e50 0%, #a0182f 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.designer-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.designer-avatar-large {
  background: linear-gradient(135deg, #a0182f 0%, #c41e3a 100%);
  color: white;
  font-weight: 700;
  box-shadow: 0 4px 15px rgba(160, 24, 47, 0.3);
}

.designer-details {
  flex: 1;
}

.designer-name {
  font-size: 1.1rem;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 0.3rem;
}

.role-tag {
  background: linear-gradient(135deg, #fff5f5 0%, #ffe6e6 100%);
  border: 1px solid rgba(160, 24, 47, 0.2);
  color: #a0182f;
  font-weight: 600;
}

/* Stats */
.detail-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.5rem;
  margin-bottom: 2.5rem;
  max-width: 500px;
  margin-left: auto;
  margin-right: auto;
}

.stat-card {
  background: linear-gradient(135deg, #fff5f5 0%, #ffe6e6 100%);
  border-radius: 16px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  border: 1px solid rgba(160, 24, 47, 0.1);
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(160, 24, 47, 0.15);
  border-color: rgba(160, 24, 47, 0.3);
}

.stat-icon {
  font-size: 2rem;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: 900;
  color: #a0182f;
  line-height: 1;
  margin-bottom: 0.3rem;
}

.stat-label {
  font-size: 0.85rem;
  color: #666;
  font-weight: 600;
}

/* Sections */
.detail-section {
  margin-bottom: 2rem;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  margin-bottom: 1rem;
}

.section-icon {
  font-size: 1.5rem;
  color: #a0182f;
}

.section-title {
  font-size: 1.3rem;
  font-weight: 800;
  color: #2c3e50;
  margin: 0;
}

.section-content {
  font-size: 1.05rem;
  line-height: 1.8;
  color: #555;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 12px;
  border-left: 4px solid #a0182f;
}

.tags-list {
  display: flex;
  gap: 0.8rem;
  flex-wrap: wrap;
}

.detail-tag {
  padding: 0.6rem 1.2rem;
  font-size: 0.95rem;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #fff5f5 0%, #ffe6e6 100%);
  border: 1px solid rgba(160, 24, 47, 0.2);
  color: #a0182f;
  transition: all 0.3s;
}

.detail-tag:hover {
  background: linear-gradient(135deg, #ffe6e6 0%, #ffd6d6 100%);
  border-color: rgba(160, 24, 47, 0.4);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(160, 24, 47, 0.15);
}

/* Meta Info */
.detail-meta {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
  margin-bottom: 2rem;
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 16px;
}

.meta-info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.8rem;
  background: white;
  border-radius: 12px;
}

.meta-info-label {
  font-size: 0.9rem;
  color: #888;
  font-weight: 600;
}

.meta-info-value {
  font-size: 1rem;
  color: #2c3e50;
  font-weight: 700;
}

/* Actions */
.detail-actions {
  display: flex;
  gap: 1rem;
  padding-top: 1.5rem;
  border-top: 2px solid #f0f0f0;
}

.action-button {
  flex: 1;
  height: 56px;
  font-size: 1.05rem;
  font-weight: 700;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.6rem;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}

.action-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
  transition: left 0.6s;
}

.action-button:hover::before {
  left: 100%;
}

.like-button {
  background: linear-gradient(135deg, #a0182f 0%, #c41e3a 100%);
  border: none;
  box-shadow: 0 6px 20px rgba(160, 24, 47, 0.3);
}

.like-button:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 30px rgba(160, 24, 47, 0.4);
}

.contact-button {
  border: 2px solid #409eff;
  color: #409eff;
}

.contact-button:hover {
  background: #409eff;
  color: white;
  transform: translateY(-4px);
  box-shadow: 0 10px 30px rgba(64, 158, 255, 0.3);
}

.button-icon {
  font-size: 1.2rem;
}

/* Responsive */
@media (max-width: 768px) {
  .work-dialog {
    width: 95% !important;
    margin: 2.5vh auto !important;
  }
  
  .detail-body {
    padding: 1.5rem;
  }
  
  .detail-title {
    font-size: 1.5rem;
  }
  
  .detail-stats {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .stat-card {
    padding: 1rem;
  }
  
  .stat-value {
    font-size: 1.5rem;
  }
  
  .detail-meta {
    grid-template-columns: 1fr;
  }
  
  .detail-actions {
    flex-direction: column;
  }
  
  .action-button {
    width: 100%;
  }
  
  .media-video,
  .media-image {
    max-height: 400px;
  }
  
  .video-player {
    max-height: 400px;
  }
  
  .detail-image {
    max-height: 400px;
  }
}

@media (max-width: 992px) {
  .creative-container {
    padding: 0 1.5rem 3rem;
    margin-top: -3rem;
  }
  
  .works-grid,
  .skeleton-grid {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 1.5rem;
  }
  
  .work-cover {
    height: 240px;
  }
  
  .detail-content {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .detail-sidebar {
    order: -1;
  }
}

@media (max-width: 768px) { 
  .creative-hero {
    padding: 5rem 1rem 4rem;
  }
  
  .hero-icon-wrapper {
    width: 90px;
    height: 90px;
    margin-bottom: 1.5rem;
  }
  
  .hero-icon {
    font-size: 3rem;
  }
  
  .hero-title {
    font-size: 2.5rem;
    letter-spacing: 2px;
  }
  
  .hero-subtitle {
    font-size: 1.1rem;
    padding: 0 1rem;
    margin-bottom: 2rem;
  }

  .hero-actions {
    flex-direction: column;
    gap: 1rem;
    padding: 0 2rem;
  }

  .action-btn {
    width: 100%;
    padding: 12px 30px;
  }
  
  .creative-container {
    padding: 0 1rem 2rem;
    margin-top: -2rem;
  }
  
  .category-tabs { 
    justify-content: flex-start;
    padding: 1rem;
    margin: 0 -1rem 2rem;
    border-radius: 0;
    border-left: none;
    border-right: none;
    -webkit-overflow-scrolling: touch;
    gap: 0.8rem;
  } 

  .category-tab {
    padding: 0.8rem 1.5rem;
    flex-shrink: 0;
    font-size: 0.9rem;
  }
  
  .tab-icon {
    font-size: 1.1rem;
  }
  
  .tab-label {
    font-size: 0.95rem;
  }
  
  .works-grid,
  .skeleton-grid { 
    grid-template-columns: 1fr; 
    gap: 1.5rem;
  }
  
  .work-card {
    border-radius: 16px;
  }
  
  .work-cover {
    height: 220px;
  }
  
  .work-info {
    padding: 1.2rem;
  }
  
  .work-title {
    font-size: 1.15rem;
  }
  
  .work-description {
    font-size: 0.85rem;
  }
  
  .work-meta {
    gap: 1rem;
  }
  
  .meta-item {
    font-size: 0.8rem;
  }
  
  .work-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.8rem;
  }
  
  .work-time {
    align-self: flex-end;
  }
  
  .works-empty {
    padding: 3rem 1.5rem;
    border-radius: 16px;
  }
  
  .empty-icon {
    font-size: 4.5rem;
  }
  
  .empty-title {
    font-size: 1.5rem;
  }
  
  .empty-text {
    font-size: 1rem;
  }
  
  .empty-subtext {
    font-size: 0.9rem;
    margin-bottom: 1.5rem;
  }
  
  .empty-action-btn {
    width: 100%;
    padding: 12px 30px;
  }
  
  .detail-cover {
    height: 250px;
  }
  
  .dialog-footer {
    flex-direction: column-reverse;
    gap: 1rem;
    align-items: stretch;
  }
  
  .footer-actions {
    width: 100%;
    flex-direction: column;
    gap: 0.8rem;
  }
  
  .footer-actions .el-button {
    width: 100%;
    margin-left: 0 !important;
  }
  
  .dialog-footer > .el-button {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .hero-title {
    font-size: 2rem;
  }
  
  .hero-subtitle {
    font-size: 1rem;
  }
  
  .category-tabs {
    padding: 0.8rem;
    gap: 0.6rem;
  }
  
  .category-tab {
    padding: 0.7rem 1.2rem;
  }
  
  .work-badge {
    top: 0.8rem;
    right: 0.8rem;
    padding: 0.4rem 0.8rem;
    font-size: 0.75rem;
  }
  
  .work-hot-badge {
    top: 0.8rem;
    left: 0.8rem;
    padding: 0.3rem 0.6rem;
    font-size: 0.7rem;
  }
}

/* Upload Dialog Styles */
.upload-dialog .el-form-item {
  margin-bottom: 1.5rem;
}

.form-tip {
  font-size: 0.85rem;
  color: #999;
  margin-top: 0.5rem;
}

.upload-dialog .el-textarea__inner,
.upload-dialog .el-input__inner {
  border-radius: 8px;
}

.upload-dialog .el-form-item__label {
  font-weight: 600;
  color: #2c3e50;
}

/* Contact Designer Dialog Styles */
.contact-dialog .el-dialog__body {
  padding: 20px 24px;
}

.contact-designer-content {
  padding: 0;
}

.designer-profile {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: linear-gradient(135deg, #fff5f5 0%, #ffe6e6 100%);
  border-radius: 12px;
  border: 1px solid rgba(160, 24, 47, 0.1);
}

.designer-avatar-contact {
  background: linear-gradient(135deg, #a0182f 0%, #c41e3a 100%);
  flex-shrink: 0;
}

.avatar-text-large {
  font-size: 1.5rem;
  font-weight: 700;
  color: white;
}

.designer-info-contact {
  flex: 1;
}

.designer-name-contact {
  font-size: 1.2rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 4px 0;
}

.designer-work-contact {
  font-size: 0.9rem;
  color: #666;
  margin: 0 0 8px 0;
}

.designer-tag {
  background: rgba(160, 24, 47, 0.1);
  color: #a0182f;
  border-color: rgba(160, 24, 47, 0.2);
}

.contact-tips {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #f0f9ff;
  border-radius: 8px;
  font-size: 0.85rem;
  color: #0369a1;
  margin-top: 16px;
}

.contact-tips .el-icon {
  font-size: 1rem;
}

.contact-dialog .el-divider {
  margin: 20px 0;
}

.contact-dialog .el-form-item__label {
  font-weight: 600;
  color: #2c3e50;
}

.contact-dialog .el-textarea__inner {
  border-radius: 8px;
}

.contact-dialog .dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.contact-dialog .dialog-footer .el-button {
  padding: 10px 24px;
  border-radius: 8px;
}

.contact-dialog .dialog-footer .button-icon {
  margin-right: 6px;
}

/* Mall Application Dialog Styles */
.mall-application-dialog .el-dialog__body {
  padding: 20px 24px;
}

.application-preview {
  display: flex;
  gap: 16px;
  align-items: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
}

.preview-image {
  width: 80px;
  height: 80px;
  border-radius: 12px;
  background-size: cover;
  background-position: center;
  flex-shrink: 0;
}

.preview-info h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #2c3e50;
}

.preview-info p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

.icon-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.icon-option {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.icon-option:hover {
  border-color: #c41e3a;
  background: #fff5f5;
}

.icon-option.active {
  border-color: #c41e3a;
  background: #fff0f0;
  box-shadow: 0 0 0 3px rgba(196, 30, 58, 0.1);
}

.application-tips {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #fff7e6;
  border-radius: 8px;
  color: #d48806;
  font-size: 14px;
  margin-top: 16px;
}

.mall-button {
  background: linear-gradient(135deg, #f5a623, #f78b00) !important;
  border: none !important;
}

.mall-button:hover:not(:disabled) {
  background: linear-gradient(135deg, #f78b00, #e67e00) !important;
}

.mall-button:disabled {
  background: #ccc !important;
  cursor: not-allowed;
}

/* Contact Admin Button Style */
.action-btn.contact-admin {
  background: rgba(255, 255, 255, 0.15);
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(10px);
}

.action-btn.contact-admin:hover {
  background: rgba(255, 255, 255, 0.25);
  border-color: rgba(255, 255, 255, 0.6);
  transform: translateY(-3px);
}

/* Contact Admin Dialog Styles */
.contact-admin-dialog .el-dialog__body {
  padding: 20px 24px;
}

.contact-admin-content {
  padding: 0;
}

.admin-profile {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: linear-gradient(135deg, #fff5f5 0%, #ffe6e6 100%);
  border-radius: 12px;
  border: 1px solid rgba(160, 24, 47, 0.1);
}

.admin-avatar {
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, #a0182f 0%, #c41e3a 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.admin-info {
  flex: 1;
}

.admin-title {
  font-size: 1.3rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 6px 0;
}

.admin-desc {
  font-size: 0.95rem;
  color: #666;
  margin: 0;
}

.contact-methods {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.contact-method-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  background: #f8f9fa;
  border-radius: 10px;
  transition: all 0.2s;
}

.contact-method-item:hover {
  background: #f0f2f5;
}

.method-icon {
  font-size: 22px;
  color: #a0182f;
  flex-shrink: 0;
}

.method-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.method-label {
  font-size: 12px;
  color: #999;
}

.method-value {
  font-size: 15px;
  color: #2c3e50;
  font-weight: 500;
}

.contact-admin-dialog .contact-tips {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #e6f7ff;
  border-radius: 8px;
  color: #1890ff;
  font-size: 14px;
  margin-top: 16px;
}

.contact-admin-dialog .dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.contact-admin-dialog .dialog-footer .el-button {
  padding: 10px 24px;
  border-radius: 8px;
}

.contact-admin-dialog .dialog-footer .button-icon {
  margin-right: 6px;
}
</style>
