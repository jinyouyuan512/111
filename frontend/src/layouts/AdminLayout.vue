<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <aside class="admin-sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
        <div class="logo">
          <span class="logo-icon">🎯</span>
          <span v-if="!sidebarCollapsed" class="logo-text">冀忆红途</span>
        </div>
        <button class="collapse-btn" @click="toggleSidebar">
          <span v-if="sidebarCollapsed">→</span>
          <span v-else>←</span>
        </button>
      </div>
      
      <nav class="sidebar-nav">
        <router-link to="/admin" class="nav-item" exact-active-class="active">
          <span class="nav-icon">📊</span>
          <span v-if="!sidebarCollapsed" class="nav-text">总览</span>
        </router-link>
        
        <router-link to="/admin/users" class="nav-item" active-class="active">
          <span class="nav-icon">👥</span>
          <span v-if="!sidebarCollapsed" class="nav-text">用户管理</span>
        </router-link>
        
        <router-link to="/admin/content" class="nav-item" active-class="active">
          <span class="nav-icon">📝</span>
          <span v-if="!sidebarCollapsed" class="nav-text">内容管理</span>
        </router-link>
        
        <router-link to="/admin/products" class="nav-item" active-class="active">
          <span class="nav-icon">🎁</span>
          <span v-if="!sidebarCollapsed" class="nav-text">商品管理</span>
        </router-link>
        
        <router-link to="/admin/orders" class="nav-item" active-class="active">
          <span class="nav-icon">🛒</span>
          <span v-if="!sidebarCollapsed" class="nav-text">订单管理</span>
        </router-link>
        
        <router-link to="/admin/mall-applications" class="nav-item" active-class="active">
          <span class="nav-icon">📝</span>
          <span v-if="!sidebarCollapsed" class="nav-text">上架审核</span>
        </router-link>
        
        <router-link to="/admin/reports" class="nav-item" active-class="active">
          <span class="nav-icon">⚠️</span>
          <span v-if="!sidebarCollapsed" class="nav-text">举报审核</span>
          <span v-if="!sidebarCollapsed && pendingReports > 0" class="badge">{{ pendingReports }}</span>
        </router-link>
        
        <router-link to="/admin/settings" class="nav-item" active-class="active">
          <span class="nav-icon">⚙️</span>
          <span v-if="!sidebarCollapsed" class="nav-text">系统设置</span>
        </router-link>
      </nav>
      
      <div class="sidebar-footer">
        <router-link to="/" class="nav-item">
          <span class="nav-icon">🏠</span>
          <span v-if="!sidebarCollapsed" class="nav-text">返回首页</span>
        </router-link>
      </div>
    </aside>

    <!-- 主内容区 -->
    <div class="admin-main">
      <!-- 顶部栏 -->
      <header class="admin-header">
        <div class="header-left">
          <h1 class="page-title">{{ pageTitle }}</h1>
        </div>
        <div class="header-right">
          <el-dropdown>
            <div class="user-info">
              <el-avatar :size="36">管</el-avatar>
              <span class="username">{{ username }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="goToProfile">个人资料</el-dropdown-item>
                <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 内容区域 -->
      <main class="admin-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const sidebarCollapsed = ref(false)
const pendingReports = ref(5)
const username = ref('管理员')

const pageTitle = computed(() => {
  const titleMap: Record<string, string> = {
    '/admin': '管理后台总览',
    '/admin/users': '用户管理',
    '/admin/content': '内容管理',
    '/admin/products': '商品管理',
    '/admin/orders': '订单管理',
    '/admin/mall-applications': '上架审核',
    '/admin/reports': '举报审核',
    '/admin/settings': '系统设置'
  }
  return titleMap[route.path] || '管理后台'
})

const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

const goToProfile = () => {
  router.push('/profile')
}

const logout = () => {
  localStorage.removeItem('token')
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  height: 100vh;
  background: #f5f7fa;
}

.admin-sidebar {
  width: 250px;
  background: linear-gradient(180deg, #2c3e50 0%, #34495e 100%);
  color: white;
  display: flex;
  flex-direction: column;
  transition: width 0.3s;
  box-shadow: 2px 0 8px rgba(0,0,0,0.1);
}

.admin-sidebar.collapsed {
  width: 70px;
}

.sidebar-header {
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  font-size: 2rem;
}

.logo-text {
  font-size: 1.3rem;
  font-weight: 700;
}

.collapse-btn {
  background: rgba(255,255,255,0.1);
  border: none;
  color: white;
  width: 30px;
  height: 30px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.collapse-btn:hover {
  background: rgba(255,255,255,0.2);
}

.sidebar-nav {
  flex: 1;
  padding: 20px 0;
  overflow-y: auto;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 20px;
  color: rgba(255,255,255,0.8);
  text-decoration: none;
  transition: all 0.3s;
  position: relative;
}

.nav-item:hover {
  background: rgba(255,255,255,0.1);
  color: white;
}

.nav-item.active {
  background: rgba(160, 24, 47, 0.3);
  color: white;
  border-left: 4px solid #a0182f;
}

.nav-icon {
  font-size: 1.5rem;
  min-width: 30px;
  text-align: center;
}

.nav-text {
  flex: 1;
}

.badge {
  background: #ff4d4f;
  color: white;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 0.75rem;
  font-weight: 600;
}

.sidebar-footer {
  padding: 20px 0;
  border-top: 1px solid rgba(255,255,255,0.1);
}

.admin-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.admin-header {
  background: white;
  padding: 0 30px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  z-index: 10;
}

.page-title {
  font-size: 1.5rem;
  color: #2c3e50;
  margin: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.3s;
}

.user-info:hover {
  background: #f5f7fa;
}

.username {
  font-weight: 600;
  color: #2c3e50;
}

.admin-content {
  flex: 1;
  overflow-y: auto;
  padding: 30px;
}

/* 滚动条样式 */
.sidebar-nav::-webkit-scrollbar,
.admin-content::-webkit-scrollbar {
  width: 6px;
}

.sidebar-nav::-webkit-scrollbar-thumb {
  background: rgba(255,255,255,0.2);
  border-radius: 3px;
}

.admin-content::-webkit-scrollbar-thumb {
  background: rgba(0,0,0,0.2);
  border-radius: 3px;
}
</style>
