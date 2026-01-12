<template>
  <div class="dashboard">
    <h1 class="page-title">管理后台总览</h1>
    
    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card users">
        <div class="stat-icon">👥</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.totalUsers }}</div>
          <div class="stat-label">总用户数</div>
          <div class="stat-trend">+{{ stats.newUsersToday }} 今日新增</div>
        </div>
      </div>
      
      <div class="stat-card posts">
        <div class="stat-icon">📝</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.totalPosts }}</div>
          <div class="stat-label">总动态数</div>
          <div class="stat-trend">+{{ stats.newPostsToday }} 今日新增</div>
        </div>
      </div>
      
      <div class="stat-card orders">
        <div class="stat-icon">🛒</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.totalOrders }}</div>
          <div class="stat-label">总订单数</div>
          <div class="stat-trend">¥{{ stats.todayRevenue }} 今日收入</div>
        </div>
      </div>
      
      <div class="stat-card reports">
        <div class="stat-icon">⚠️</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.pendingReports }}</div>
          <div class="stat-label">待处理举报</div>
          <div class="stat-trend" :class="{ warning: stats.pendingReports > 0 }">
            需要处理
          </div>
        </div>
      </div>
    </div>

    <!-- 快捷操作 -->
    <div class="quick-actions">
      <h2>快捷操作</h2>
      <div class="action-grid">
        <router-link to="/admin/users" class="action-card">
          <div class="action-icon">👥</div>
          <div class="action-title">用户管理</div>
          <div class="action-desc">管理用户账号和权限</div>
        </router-link>
        
        <router-link to="/admin/content" class="action-card">
          <div class="action-icon">📝</div>
          <div class="action-title">内容管理</div>
          <div class="action-desc">管理动态、评论等内容</div>
        </router-link>
        
        <router-link to="/admin/orders" class="action-card">
          <div class="action-icon">🛒</div>
          <div class="action-title">订单管理</div>
          <div class="action-desc">查看和处理订单</div>
        </router-link>
        
        <router-link to="/admin/products" class="action-card">
          <div class="action-icon">🎁</div>
          <div class="action-title">商品管理</div>
          <div class="action-desc">管理商品信息</div>
        </router-link>
        
        <router-link to="/admin/reports" class="action-card">
          <div class="action-icon">⚠️</div>
          <div class="action-title">举报审核</div>
          <div class="action-desc">处理用户举报</div>
        </router-link>
        
        <router-link to="/admin/settings" class="action-card">
          <div class="action-icon">⚙️</div>
          <div class="action-title">系统设置</div>
          <div class="action-desc">配置系统参数</div>
        </router-link>
      </div>
    </div>

    <!-- 最近活动 -->
    <div class="recent-activity">
      <h2>最近活动</h2>
      <el-table :data="recentActivities" stripe>
        <el-table-column prop="time" label="时间" width="180" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getActivityType(row.type)">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="user" label="用户" width="150" />
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as adminApi from '@/api/admin'

const stats = ref({
  totalUsers: 0,
  newUsersToday: 0,
  totalPosts: 0,
  newPostsToday: 0,
  totalOrders: 0,
  todayRevenue: 0,
  pendingReports: 0
})

const recentActivities = ref<any[]>([])

const loadStats = async () => {
  try {
    const data = await adminApi.getDashboardStats()
    stats.value = data
  } catch (e) {
    console.log('加载统计数据失败，使用默认值')
  }
}

const loadRecentActivities = async () => {
  // TODO: 从API加载最近活动
  recentActivities.value = [
    { time: '2026-01-12 18:30:00', type: '新用户', description: '用户 newuser123 注册', user: 'newuser123' },
    { time: '2026-01-12 18:25:00', type: '新订单', description: '订单 #12345 已创建', user: 'testuser' },
    { time: '2026-01-12 18:20:00', type: '新举报', description: '动态 #789 被举报', user: 'reporter' },
    { time: '2026-01-12 18:15:00', type: '新动态', description: '用户发布了新动态', user: 'activeuser' },
    { time: '2026-01-12 18:10:00', type: '新评论', description: '用户评论了动态', user: 'commenter' }
  ]
}

const getActivityType = (type: string) => {
  const typeMap: Record<string, string> = {
    '新用户': 'success',
    '新订单': 'primary',
    '新举报': 'warning',
    '新动态': 'info',
    '新评论': ''
  }
  return typeMap[type] || ''
}

onMounted(() => {
  loadStats()
  loadRecentActivities()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.page-title {
  font-size: 2rem;
  margin-bottom: 30px;
  color: #2c3e50;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.stat-card {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  display: flex;
  align-items: center;
  gap: 20px;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
}

.stat-icon {
  font-size: 3rem;
  width: 70px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
}

.stat-card.users .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-card.posts .stat-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-card.orders .stat-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-card.reports .stat-icon {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 2rem;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 8px;
}

.stat-trend {
  font-size: 0.85rem;
  color: #52c41a;
}

.stat-trend.warning {
  color: #fa8c16;
}

.quick-actions, .recent-activity {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  margin-bottom: 30px;
}

.quick-actions h2, .recent-activity h2 {
  font-size: 1.3rem;
  margin-bottom: 20px;
  color: #2c3e50;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.action-card {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  transition: all 0.3s;
  text-decoration: none;
  color: inherit;
  border: 2px solid transparent;
}

.action-card:hover {
  background: white;
  border-color: #a0182f;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(160, 24, 47, 0.1);
}

.action-icon {
  font-size: 2.5rem;
  margin-bottom: 12px;
}

.action-title {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 8px;
  color: #2c3e50;
}

.action-desc {
  font-size: 0.85rem;
  color: #666;
}
</style>
