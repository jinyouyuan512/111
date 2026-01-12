<template>
  <header class="navbar" :class="{ scrolled: isScrolled }">
    <div class="navbar-container">
      <div class="logo-area" @click="$router.push('/')">
        <Logo size="medium" width="45px" height="45px" />
        <div class="logo-text">
          <h1 class="logo-title">冀忆红途</h1>
          <p class="logo-subtitle">河北红色文化数字平台</p>
        </div>
      </div>

      <nav class="main-nav">
        <router-link to="/" class="nav-link">首页</router-link>
        <router-link to="/academy" class="nav-link">传承学院</router-link>
        <router-link to="/tourism" class="nav-link">智慧旅游</router-link>
        <router-link to="/creative" class="nav-link">众创空间</router-link>
        <router-link to="/mall" class="nav-link">文创商城</router-link>
        <router-link to="/social" class="nav-link">红色社区</router-link>
      </nav>

      <div class="navbar-actions">
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
          <router-link to="/login" class="login-link">
            <el-button type="danger">登录</el-button>
          </router-link>
        </template>
      </div>
      
      <!-- Mobile Menu Toggle -->
      <div class="mobile-menu-toggle" @click="toggleMobileMenu">
        <span class="bar"></span>
        <span class="bar"></span>
        <span class="bar"></span>
      </div>
    </div>
    
    <!-- Mobile Menu -->
    <div class="mobile-menu" :class="{ open: isMobileMenuOpen }">
        <router-link to="/" class="mobile-link" @click="closeMobileMenu">首页</router-link>
        <router-link to="/academy" class="mobile-link" @click="closeMobileMenu">传承学院</router-link>
        <router-link to="/tourism" class="mobile-link" @click="closeMobileMenu">智慧旅游</router-link>
        <router-link to="/creative" class="mobile-link" @click="closeMobileMenu">众创空间</router-link>
        <router-link to="/mall" class="mobile-link" @click="closeMobileMenu">文创商城</router-link>
        <router-link to="/social" class="mobile-link" @click="closeMobileMenu">红色社区</router-link>
        <div class="mobile-divider"></div>
        <template v-if="userStore.token">
          <a v-if="userStore.userInfo?.role === 'admin'" class="mobile-link admin-link" @click="handleCommand('admin')">
            ⚙️ 管理后台
          </a>
          <a class="mobile-link" @click="handleCommand('profile')">个人中心</a>
          <a class="mobile-link" @click="handleCommand('logout')">退出登录</a>
        </template>
        <template v-else>
          <router-link to="/login" class="mobile-link" @click="closeMobileMenu">登录 / 注册</router-link>
        </template>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import Logo from '@/components/Logo.vue'

const props = defineProps({
  transparent: {
    type: Boolean,
    default: false
  }
})

const router = useRouter()
const userStore = useUserStore()

const isScrolled = ref(false)
const isMobileMenuOpen = ref(false)

const handleScroll = () => {
  isScrolled.value = window.scrollY > 50
}

const toggleMobileMenu = () => {
  isMobileMenuOpen.value = !isMobileMenuOpen.value
}

const closeMobileMenu = () => {
  isMobileMenuOpen.value = false
}

const handleCommand = (command: string) => {
  closeMobileMenu()
  switch (command) {
    case 'admin':
      router.push('/admin')
      break
    case 'profile':
      router.push('/profile')
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
      router.push('/')
      break
  }
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background: rgba(139, 30, 63, 0.95);
  backdrop-filter: blur(10px);
  z-index: 1000;
  transition: all 0.3s;
  border-bottom: 2px solid rgba(212, 149, 108, 0.3);
}

.navbar.scrolled {
  background: rgba(139, 30, 63, 0.98);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.navbar-container {
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

.logo-title {
  font-size: 1.4rem;
  font-weight: 700;
  color: #ffd700;
  margin: 0;
  letter-spacing: 3px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.logo-subtitle {
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
  white-space: nowrap;
}

.nav-link:hover,
.nav-link.router-link-active {
  color: #ffd700;
  border-bottom-color: #ffd700;
}

.navbar-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.login-link {
  text-decoration: none;
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

.mobile-menu-toggle {
  display: none;
  flex-direction: column;
  gap: 5px;
  cursor: pointer;
}

.bar {
  width: 25px;
  height: 3px;
  background-color: #ffd700;
  border-radius: 2px;
}

.mobile-menu {
  position: fixed;
  top: 75px;
  left: 0;
  right: 0;
  background: rgba(139, 30, 63, 0.98);
  backdrop-filter: blur(10px);
  z-index: 999;
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.3s ease;
  border-top: 2px solid rgba(212, 149, 108, 0.3);
}

.mobile-menu.open {
  max-height: calc(100vh - 75px);
  overflow-y: auto;
}

.mobile-link {
  padding: 1rem 2rem;
  color: rgba(255, 255, 255, 0.9);
  font-size: 1.1rem;
  border-bottom: 1px solid rgba(212, 149, 108, 0.2);
  display: block;
  text-decoration: none;
  transition: all 0.3s;
}

.mobile-link:hover,
.mobile-link.router-link-active {
  background: rgba(255, 255, 255, 0.1);
  color: #ffd700;
}

.mobile-divider {
  height: 1px;
  background: rgba(212, 149, 108, 0.3);
  margin: 0.5rem 0;
}

@media (max-width: 1200px) {
  .navbar-container {
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
  .navbar-container {
    padding: 0 2rem;
    height: 68px;
  }
  .main-nav {
    gap: 1rem;
  }
  .nav-link {
    font-size: 0.85rem;
    padding: 0.4rem 0;
  }
  .logo-title {
    font-size: 1.25rem;
    letter-spacing: 2px;
  }
  .logo-subtitle {
    font-size: 0.7rem;
  }
}

@media (max-width: 768px) {
  .navbar-container {
    padding: 0 1.5rem;
    height: 64px;
  }
  .main-nav {
    display: none;
  }
  .navbar-actions {
    display: none;
  }
  .mobile-menu-toggle {
    display: flex;
  }
  .mobile-menu {
    top: 64px;
  }
  .logo-title {
    font-size: 1.15rem;
  }
}
</style>

