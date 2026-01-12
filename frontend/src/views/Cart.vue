<template>
  <MainLayout>
    <div class="cart-page">
      <div class="cart-header" v-motion-fade-visible>
        <div class="header-left">
          <div class="header-icon-box">
            <span class="cart-icon">🛒</span>
          </div>
          <div>
            <h1>购物车</h1>
            <p>{{ cartItems.length > 0 ? `共 ${cartItems.length} 件商品` : '购物车是空的' }}</p>
          </div>
        </div>
        <el-button type="danger" @click="gotoMall" class="continue-btn" icon="ArrowLeft">继续购物</el-button>
      </div>

      <div v-if="cartItems.length === 0" class="empty-cart" v-motion-pop-visible>
        <div class="empty-icon-wrapper">
          <div class="empty-icon">🛒</div>
        </div>
        <p class="empty-text">购物车还是空的</p>
        <p class="empty-hint">快去挑选心仪的红色文创商品吧！</p>
        <el-button type="primary" size="large" @click="gotoMall" class="go-mall-btn">
          去商城逛逛
        </el-button>
      </div>

      <div v-else class="cart-content" v-motion-slide-visible-up>
        <div class="cart-list">
          <div class="list-header">
            <el-checkbox 
              v-model="selectAll" 
              :indeterminate="isIndeterminate"
              @change="handleSelectAll"
            >
              全选
            </el-checkbox>
            <span class="header-label">商品信息</span>
            <span class="header-label">单价</span>
            <span class="header-label">数量</span>
            <span class="header-label">小计</span>
            <span class="header-label">操作</span>
          </div>

          <div 
            v-for="(item, index) in cartItems" 
            :key="item.id"
            class="cart-item"
            v-motion-slide-visible-up
            :delay="index * 50"
          >
          <el-checkbox 
            v-model="item.selected"
            @change="() => handleItemSelect(item)"
          />
          
          <div class="item-product">
            <div class="product-image" :style="{ background: item.color }">
              <div class="product-icon">{{ item.icon }}</div>
            </div>
            <div class="product-info">
              <h4 class="product-name">{{ item.name }}</h4>
              <p class="product-category">{{ item.category }}</p>
              <el-tag v-if="item.designer" size="small" type="warning">
                设计师：{{ item.designer }}
              </el-tag>
            </div>
          </div>

          <div class="item-price">
            <span class="price-value">¥{{ item.price }}</span>
          </div>

          <div class="item-quantity">
            <el-input-number 
              v-model="item.quantity" 
              :min="1" 
              :max="99"
              size="small"
              @change="(value) => handleQuantityChange(item, value)"
            />
          </div>

          <div class="item-subtotal">
            <span class="subtotal-value">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
          </div>

          <div class="item-actions">
            <el-button 
              link 
              type="primary"
              @click="moveToWishlist(item)"
            >
              移入心愿单
            </el-button>
            <el-button 
              link 
              type="danger"
              @click="removeItem(item)"
            >
              删除
            </el-button>
          </div>
        </div>
      </div>
    </div>

      <div class="cart-summary">
        <div class="summary-card">
          <h3 class="summary-title">订单摘要</h3>
          
          <div class="summary-row">
            <span class="summary-label">已选商品：</span>
            <span class="summary-value">{{ selectedCount }} 件</span>
          </div>

          <div class="summary-row">
            <span class="summary-label">商品总价：</span>
            <span class="summary-value">¥{{ subtotal.toFixed(2) }}</span>
          </div>

          <div class="summary-row">
            <span class="summary-label">运费：</span>
            <span class="summary-value shipping">
              {{ shippingFee === 0 ? '免运费' : `¥${shippingFee.toFixed(2)}` }}
            </span>
          </div>

          <el-divider />

          <div class="summary-total">
            <span class="total-label">应付总额：</span>
            <span class="total-value">¥{{ totalAmount.toFixed(2) }}</span>
          </div>

          <el-button 
            type="danger" 
            size="large"
            class="checkout-btn"
            :disabled="selectedCount === 0"
            @click="handleCheckout"
          >
            去结算 ({{ selectedCount }})
          </el-button>

          <div class="summary-tips">
            <p>💡 满99元免运费</p>
            <p>🎁 支持7天无理由退换</p>
          </div>
        </div>

        <div class="recommend-card">
          <h4 class="recommend-title">🔥 为你推荐</h4>
          <div class="recommend-list">
            <div 
              v-for="product in recommendProducts" 
              :key="product.id"
              class="recommend-item"
              @click="addToCart(product)"
            >
              <div class="recommend-image" :style="{ background: product.color }">
                <div class="recommend-icon">{{ product.icon }}</div>
              </div>
              <div class="recommend-info">
                <p class="recommend-name">{{ product.name }}</p>
                <p class="recommend-price">¥{{ product.price }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import MainLayout from '@/layouts/MainLayout.vue'
import mallApi from '@/api/mall'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

interface CartItem {
  id: number
  productId: number
  name: string
  price: number
  quantity: number
  icon: string
  color: string
  category: string
  designer?: string
  selected: boolean
  stock?: number
}

interface Product {
  id: number
  name: string
  price: number
  icon: string
  color: string
  category: string
  designer?: string
}

const cartItems = ref<CartItem[]>([])
const selectAll = ref(false)

// 推荐商品
const recommendProducts = ref<Product[]>([
  { id: 21, icon: '🎨', name: '红色主题帆布画', price: 168, category: '文化周边', color: 'linear-gradient(135deg, #c41e3a, #8b1e3f)' },
  { id: 22, icon: '📱', name: '文创手机壳', price: 58, category: '创意生活', color: 'linear-gradient(135deg, #d4956c, #c41e3a)' },
  { id: 23, icon: '🎧', name: '红色主题耳机', price: 298, category: '创意生活', color: 'linear-gradient(135deg, #8b1e3f, #d4956c)' }
])

// 计算属性
const selectedCount = computed(() => {
  return cartItems.value.filter(item => item.selected).length
})

const isIndeterminate = computed(() => {
  const selected = selectedCount.value
  return selected > 0 && selected < cartItems.value.length
})

const subtotal = computed(() => {
  return cartItems.value
    .filter(item => item.selected)
    .reduce((sum, item) => sum + item.price * item.quantity, 0)
})

const shippingFee = computed(() => {
  return subtotal.value >= 99 ? 0 : 10
})

const totalAmount = computed(() => {
  return subtotal.value + shippingFee.value
})

// 方法
const handleSelectAll = async (value: boolean) => {
  try {
    // 调用后端API全选/取消全选
    await mallApi.selectAll(value)
    
    // 更新前端状态
    cartItems.value.forEach(item => {
      item.selected = value
    })
  } catch (error) {
    console.error('全选操作失败:', error)
    ElMessage.error('操作失败')
    // 重新加载购物车数据
    await loadCart()
  }
}

const handleItemSelect = async (item: CartItem) => {
  try {
    // 调用后端API更新选中状态
    await mallApi.updateCartItem(item.id, { selected: item.selected })
    
    // 更新全选状态
    const allSelected = cartItems.value.every(item => item.selected)
    const noneSelected = cartItems.value.every(item => !item.selected)
    
    if (allSelected) {
      selectAll.value = true
    } else if (noneSelected) {
      selectAll.value = false
    }
  } catch (error) {
    console.error('更新选中状态失败:', error)
    ElMessage.error('操作失败')
    // 重新加载购物车数据
    await loadCart()
  }
}

const handleQuantityChange = async (item: CartItem, value: number) => {
  try {
    // 调用后端API更新数量
    await mallApi.updateCartItem(item.id, { quantity: value })
  } catch (error) {
    console.error('更新数量失败:', error)
    ElMessage.error('更新数量失败')
    // 重新加载购物车数据
    await loadCart()
  }
}

const moveToWishlist = (item: CartItem) => {
  ElMessageBox.confirm(
    `确认将"${item.name}"移入心愿单？`,
    '移入心愿单',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(() => {
    // 添加到心愿单
    const key = 'wishlist'
    const raw = localStorage.getItem(key)
    const list = raw ? JSON.parse(raw) as number[] : []
    if (!list.includes(item.id)) {
      list.push(item.id)
      localStorage.setItem(key, JSON.stringify(list))
    }
    
    // 从购物车移除
    const index = cartItems.value.findIndex(i => i.id === item.id)
    if (index > -1) {
      cartItems.value.splice(index, 1)
      saveCart()
      ElMessage.success('已移入心愿单')
    }
  }).catch(() => {
    ElMessage.info('已取消')
  })
}

const removeItem = async (item: CartItem) => {
  ElMessageBox.confirm(
    `确认删除"${item.name}"？`,
    '删除商品',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // 调用后端API删除
      await mallApi.deleteCartItem(item.id)
      
      // 从前端数组中移除
      const index = cartItems.value.findIndex(i => i.id === item.id)
      if (index > -1) {
        cartItems.value.splice(index, 1)
      }
      
      ElMessage.success('已删除')
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  }).catch(() => {
    ElMessage.info('已取消')
  })
}

const addToCart = (product: Product) => {
  const existingItem = cartItems.value.find(item => item.id === product.id)
  
  if (existingItem) {
    existingItem.quantity++
    ElMessage.success('已增加数量')
  } else {
    cartItems.value.push({
      ...product,
      quantity: 1,
      selected: true
    })
    ElMessage.success('已加入购物车')
  }
  
  saveCart()
}

const handleCheckout = async () => {
  if (selectedCount.value === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }

  ElMessageBox.confirm(
    `确认结算 ${selectedCount.value} 件商品，总计 ¥${totalAmount.value.toFixed(2)}？`,
    '确认结算',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(async () => {
    try {
      // 获取选中的商品
      const selectedItems = cartItems.value.filter(item => item.selected)
      
      console.log('选中的商品:', selectedItems)
      console.log('用户信息:', userStore.userInfo)
      
      // 调用后端 API 创建订单
      const orderData = {
        userId: userStore.userInfo?.id,
        items: selectedItems.map(item => ({
          productId: item.productId || item.id,
          quantity: item.quantity
        })),
        shippingAddress: '默认地址',
        paymentMethod: '在线支付'
      }
      
      console.log('订单数据:', orderData)
      
      const response = await mallApi.createOrder(orderData)
      console.log('订单创建成功:', response)
      
      // 删除购物车中已结算的商品
      for (const item of selectedItems) {
        try {
          await mallApi.deleteCartItem(item.id)
        } catch (e) {
          console.error('删除购物车项失败:', e)
        }
      }
      
      // 更新前端购物车列表
      cartItems.value = cartItems.value.filter(item => !item.selected)
      
      ElMessage.success('订单创建成功！')
      
      // 跳转到订单页面
      setTimeout(() => {
        router.push('/orders')
      }, 1000)
    } catch (error: any) {
      console.error('创建订单失败:', error)
      ElMessage.error(error.response?.data?.message || '创建订单失败，请稍后重试')
    }
  }).catch(() => {
    ElMessage.info('已取消结算')
  })
}

const gotoMall = () => {
  router.push('/mall')
}

const saveCart = () => {
  // 不再使用 localStorage，购物车数据由后端管理
  // localStorage.setItem('cart', JSON.stringify(cartItems.value))
}

const loadCart = async () => {
  try {
    // 从后端 API 加载购物车数据
    const response = await mallApi.getCartList()
    console.log('购物车数据:', response)
    
    // response 已经被 request.ts 拦截器提取了 data
    if (Array.isArray(response)) {
      cartItems.value = response.map((item: any) => ({
        id: item.id,
        productId: item.productId || item.product?.id,
        name: item.product?.name || item.productName || '未知商品',
        price: parseFloat(item.product?.price || item.price || 0),
        quantity: item.quantity || 1,
        selected: item.selected !== undefined ? item.selected : false,
        icon: item.product?.icon || item.icon || '🎁',
        stock: item.product?.stock || item.stock || 999,
        color: item.product?.color || 'linear-gradient(135deg, #c41e3a, #8b1e3f)',
        category: item.product?.category || item.category || '商品',
        designer: item.product?.designer || item.designer
      }))
    } else {
      cartItems.value = []
    }
  } catch (error) {
    console.error('加载购物车失败:', error)
    // 如果加载失败，尝试从 localStorage 恢复（兼容旧数据）
    const raw = localStorage.getItem('cart')
    if (raw) {
      try {
        cartItems.value = JSON.parse(raw)
      } catch (e) {
        console.error('Failed to load cart from localStorage:', e)
        cartItems.value = []
      }
    }
  }
}

onMounted(() => {
  loadCart()
})
</script>

<style scoped>
/* Cart Page */
.cart-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 3rem 2rem;
  background-image: radial-gradient(rgba(160, 24, 47, 0.05) 1px, transparent 1px), radial-gradient(rgba(160, 24, 47, 0.05) 1px, #f5f7fa 1px);
  background-size: 20px 20px;
  background-position: 0 0, 10px 10px;
}

.cart-header {
  max-width: 1200px;
  margin: 0 auto 2.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 2.5rem 3rem;
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(160, 24, 47, 0.08);
  border-left: 8px solid var(--primary-color);
  position: relative;
  overflow: hidden;
}

.cart-header::after {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 200px;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(160, 24, 47, 0.03));
  pointer-events: none;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 2rem;
}

.header-icon-box {
  width: 72px;
  height: 72px;
  background: linear-gradient(135deg, rgba(160, 24, 47, 0.1), rgba(196, 30, 58, 0.2));
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(160, 24, 47, 0.1);
  transform: rotate(-5deg);
  transition: transform 0.3s;
}

.cart-header:hover .header-icon-box {
  transform: rotate(0deg) scale(1.05);
}

.cart-icon {
  font-size: 2.5rem;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.1));
}

.cart-header h1 {
  font-size: 2.2rem;
  font-weight: 800;
  color: #2c3e50;
  margin: 0 0 0.5rem 0;
  letter-spacing: 1px;
}

.cart-header p {
  font-size: 1.1rem;
  color: #666;
  margin: 0;
}

.continue-btn {
  font-weight: 600;
  letter-spacing: 1px;
  border-radius: 12px;
  padding: 12px 24px;
  transition: all 0.3s;
}

.continue-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(160, 24, 47, 0.2);
}

/* Empty State */
.empty-cart {
  max-width: 800px;
  margin: 4rem auto;
  text-align: center;
  background: white;
  padding: 6rem 3rem;
  border-radius: 24px;
  box-shadow: 0 16px 40px rgba(160, 24, 47, 0.08);
  border: 1px solid rgba(160, 24, 47, 0.05);
}

.empty-icon-wrapper {
  width: 140px;
  height: 140px;
  background: linear-gradient(135deg, #f8f9fa, #fff0f0);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 2.5rem;
  box-shadow: inset 0 0 20px rgba(160, 24, 47, 0.05);
}

.empty-icon {
  font-size: 5rem;
  opacity: 0.8;
  filter: drop-shadow(0 4px 8px rgba(0,0,0,0.1));
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.empty-text {
  font-size: 2rem;
  font-weight: 800;
  color: #2c3e50;
  margin-bottom: 1rem;
}

.empty-hint {
  font-size: 1.2rem;
  color: #888;
  margin-bottom: 3rem;
}

.go-mall-btn {
  padding: 16px 48px;
  font-size: 1.2rem;
  font-weight: 700;
  letter-spacing: 2px;
  background: linear-gradient(135deg, var(--primary-color), #8b1e3f);
  border: none;
  border-radius: 50px;
  box-shadow: 0 8px 24px rgba(160, 24, 47, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.go-mall-btn:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: 0 12px 32px rgba(160, 24, 47, 0.4);
  background: linear-gradient(135deg, #c41e3a, #a0182f);
}

/* Cart Content */
.cart-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 2.5rem;
  align-items: start;
}

.cart-list {
  background: white;
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(0,0,0,0.05);
}

.list-header {
  display: grid;
  grid-template-columns: 50px 2fr 1fr 1fr 1fr 1fr;
  gap: 1rem;
  padding: 1.2rem;
  background: #f8f9fa;
  border-radius: 12px;
  margin-bottom: 1.5rem;
  font-weight: 700;
  color: #555;
  font-size: 0.95rem;
  border: 1px solid #eee;
}

.header-label {
  text-align: center;
}

.cart-item {
  display: grid;
  grid-template-columns: 50px 2fr 1fr 1fr 1fr 1fr;
  gap: 1rem;
  padding: 1.5rem 1rem;
  border-bottom: 1px dashed #e0e0e0;
  align-items: center;
  transition: all 0.3s;
  border-radius: 12px;
}

.cart-item:hover {
  background-color: #fffcfc;
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.03);
}

.cart-item:last-child {
  border-bottom: none;
}

.item-product {
  display: flex;
  gap: 1.5rem;
  align-items: center;
}

.product-image {
  width: 90px;
  height: 90px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transition: transform 0.3s;
}

.cart-item:hover .product-image {
  transform: scale(1.05);
}

.product-icon {
  font-size: 3rem;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.1));
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: 1.15rem;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 0.5rem;
  line-height: 1.4;
}

.product-category {
  font-size: 0.9rem;
  color: #888;
  margin-bottom: 0.6rem;
}

.item-price,
.item-quantity,
.item-subtotal {
  text-align: center;
}

.price-value {
  font-size: 1.1rem;
  font-weight: 600;
  color: #555;
}

.subtotal-value {
  font-size: 1.25rem;
  font-weight: 800;
  color: var(--primary-color);
  font-family: 'Roboto', sans-serif;
}

.item-actions {
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
  align-items: center;
}

/* Summary Card */
.cart-summary {
  position: sticky;
  top: 2rem;
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.summary-card,
.recommend-card {
  background: white;
  border-radius: 20px;
  padding: 2.5rem;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(0,0,0,0.05);
}

.summary-title {
  font-size: 1.5rem;
  font-weight: 800;
  color: #2c3e50;
  margin-bottom: 2rem;
  border-bottom: 2px solid #f0f0f0;
  padding-bottom: 1rem;
  display: flex;
  align-items: center;
  gap: 10px;
}

.summary-title::before {
  content: '';
  display: block;
  width: 6px;
  height: 24px;
  background: var(--primary-color);
  border-radius: 4px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.2rem;
  font-size: 1.05rem;
}

.summary-label {
  color: #666;
}

.summary-value {
  font-weight: 700;
  color: #333;
}

.summary-value.shipping {
  color: #52c41a;
  background: rgba(82, 196, 26, 0.1);
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.95rem;
}

.summary-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 1.5rem 0 2rem;
  padding-top: 1.5rem;
  border-top: 2px dashed #f0f0f0;
}

.total-label {
  font-size: 1.2rem;
  font-weight: 700;
  color: #333;
}

.total-value {
  font-size: 2.2rem;
  font-weight: 900;
  color: var(--primary-color);
  font-family: 'Roboto', sans-serif;
}

.checkout-btn {
  width: 100%;
  padding: 1.5rem;
  font-size: 1.3rem;
  font-weight: 800;
  letter-spacing: 2px;
  margin-bottom: 1.5rem;
  background: linear-gradient(135deg, var(--primary-color), #8b1e3f);
  border: none;
  border-radius: 16px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 8px 24px rgba(160, 24, 47, 0.25);
}

.checkout-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #c41e3a, #a0182f);
  transform: translateY(-4px) scale(1.02);
  box-shadow: 0 12px 32px rgba(160, 24, 47, 0.35);
}

.checkout-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.summary-tips {
  padding-top: 1.5rem;
  border-top: 1px solid #f0f0f0;
}

.summary-tips p {
  font-size: 0.95rem;
  color: #888;
  margin: 0.8rem 0;
  display: flex;
  align-items: center;
  gap: 0.8rem;
}

.recommend-title {
  font-size: 1.3rem;
  font-weight: 800;
  color: #2c3e50;
  margin-bottom: 1.5rem;
  display: flex;
  align-items: center;
  gap: 8px;
}

.recommend-list {
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
}

.recommend-item {
  display: flex;
  gap: 1.2rem;
  padding: 1.2rem;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: #f8f9fa;
  border: 1px solid transparent;
}

.recommend-item:hover {
  background: #fff;
  border-color: rgba(160, 24, 47, 0.1);
  transform: translateX(8px);
  box-shadow: 0 8px 24px rgba(160, 24, 47, 0.12);
}

.recommend-image {
  width: 72px;
  height: 72px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transition: transform 0.3s;
}

.recommend-item:hover .recommend-image {
  transform: scale(1.1) rotate(5deg);
}

.recommend-icon {
  font-size: 2.4rem;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.1));
}

.recommend-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.recommend-name {
  font-size: 1.1rem;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.recommend-price {
  font-size: 1.2rem;
  font-weight: 800;
  color: var(--primary-color);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .cart-content {
    grid-template-columns: 1fr 340px;
  }
}

@media (max-width: 992px) {
  .cart-content {
    grid-template-columns: 1fr;
  }

  .cart-summary {
    position: static;
  }

  .list-header {
    display: none;
  }

  .cart-item {
    grid-template-columns: 40px 1fr;
    gap: 1rem;
    padding: 1.5rem;
    position: relative;
  }

  .cart-item:hover {
    background-color: #fff5f5;
    border-color: rgba(160, 24, 47, 0.1);
  }

  .summary-card {
    background: linear-gradient(135deg, #fff, #fff5f5);
  }

  .item-product {
    grid-column: 2;
  }

  .item-price {
    display: none;
  }

  .item-quantity {
    grid-column: 2;
    text-align: left;
    margin-top: 1rem;
    display: flex;
    align-items: center;
    gap: 1rem;
  }
  
  .item-quantity::before {
    content: '数量:';
    color: #999;
    font-size: 0.9rem;
  }

  .item-subtotal {
    grid-column: 2;
    text-align: left;
    margin-top: 0.5rem;
    display: flex;
    align-items: center;
    gap: 1rem;
  }
  
  .item-subtotal::before {
    content: '小计:';
    color: #999;
    font-size: 0.9rem;
  }

  .item-actions {
    position: absolute;
    top: 1.5rem;
    right: 1.5rem;
    flex-direction: row;
  }
}

@media (max-width: 640px) {
  .cart-page {
    padding: 1.5rem 1rem;
  }

  .cart-header {
    flex-direction: column;
    gap: 1.5rem;
    text-align: center;
    padding: 1.5rem;
  }

  .header-left {
    flex-direction: column;
    gap: 1rem;
  }

  .product-image {
    width: 70px;
    height: 70px;
  }

  .product-icon {
    font-size: 2.5rem;
  }
}
</style>
