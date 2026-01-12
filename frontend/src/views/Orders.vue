<template>
  <MainLayout>
    <div class="orders-page">
      <div class="orders-header" v-motion-fade-visible>
        <div class="header-left">
          <div class="header-icon-box">
            <span class="orders-icon">📦</span>
          </div>
          <div>
            <h1>我的订单</h1>
            <p>查看和管理您的所有订单</p>
          </div>
        </div>
        <el-button type="primary" plain @click="gotoMall" class="shop-btn">
          继续购物
        </el-button>
      </div>

      <div v-if="loading" class="loading-state" v-loading="loading">
        <el-skeleton :rows="3" animated />
      </div>

      <div v-else-if="orders.length === 0" class="empty-orders" v-motion-pop-visible>
        <div class="empty-icon-wrapper">
          <div class="empty-icon">📄</div>
        </div>
        <p class="empty-text">您还没有订单</p>
        <p class="empty-hint">快去商城挑选心仪的红色文创商品吧！</p>
        <el-button type="primary" size="large" @click="gotoMall" class="go-mall-btn">
          去商城逛逛
        </el-button>
      </div>

      <div v-else class="orders-list" v-motion-slide-visible-up>
        <div 
          v-for="(order, index) in orders" 
          :key="order.orderNumber" 
          class="order-card"
          v-motion-slide-visible-up
          :delay="index * 100"
        >
          <div class="order-header">
            <div class="order-info">
              <span class="order-id">订单号：{{ order.orderNumber }}</span>
              <span class="order-time">{{ order.createTime }}</span>
            </div>
            <div class="order-status" :class="order.status">
              {{ getStatusText(order.status) }}
            </div>
          </div>

          <div class="order-items">
            <div 
              v-for="item in order.items" 
              :key="item.id" 
              class="order-item"
            >
              <div class="item-image" :style="{ background: item.color || '#f0f0f0' }">
                <span class="item-icon">{{ item.icon || '🎁' }}</span>
              </div>
              <div class="item-details">
                <h4 class="item-name">{{ item.name }}</h4>
                <p class="item-desc">{{ item.description }}</p>
              </div>
              <div class="item-price-qty">
                <span class="item-price">¥{{ item.price }}</span>
                <span class="item-qty">x{{ item.quantity }}</span>
              </div>
            </div>
          </div>

          <div class="order-footer">
            <div class="order-total">
              共 {{ getOrderCount(order) }} 件商品，实付：
              <span class="total-amount">¥{{ order.totalAmount.toFixed(2) }}</span>
            </div>
            <div class="order-actions">
              <el-button 
                v-if="order.status === 'pending'" 
                type="danger" 
                size="default"
                @click="payOrder(order)"
              >
                立即支付
              </el-button>
              <el-button 
                v-if="order.status === 'shipped'" 
                type="primary" 
                size="default"
                @click="confirmReceipt(order)"
              >
                确认收货
              </el-button>
              <el-button 
                v-if="order.status === 'completed'" 
                type="warning" 
                size="default"
                @click="goToReview(order)"
              >
                去评价
              </el-button>
              <el-button 
                v-if="['completed', 'cancelled'].includes(order.status)" 
                type="danger" 
                plain
                size="default"
                @click="deleteOrder(order)"
              >
                删除订单
              </el-button>
              <el-button 
                v-if="order.status === 'pending'"
                text
                @click="cancelOrder(order)"
              >
                取消订单
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import MainLayout from '@/layouts/MainLayout.vue'
import { mallApi } from '@/api/mall'
import type { Order as OrderType } from '@/api/mall'

const router = useRouter()

interface OrderItem {
  id: number
  productId: number
  name: string
  description: string
  price: number
  quantity: number
  icon?: string
  color?: string
  image?: string
}

interface Order {
  id: number
  orderNumber: string
  createTime: string
  status: 'pending' | 'paid' | 'shipped' | 'completed' | 'cancelled'
  items: OrderItem[]
  totalAmount: number
  userId: number
}

const orders = ref<Order[]>([])
const loading = ref(false)

onMounted(() => {
  loadOrders()
})

const loadOrders = async () => {
  try {
    loading.value = true
    const response = await mallApi.getOrderList({ page: 1, size: 100 })
    
    // 转换后端数据格式
    orders.value = response.records.map((order: OrderType) => ({
      id: order.id,
      orderNumber: order.orderNumber,
      createTime: order.createdAt || order.createTime,
      status: mapBackendStatus(order.status),
      items: order.items?.map(item => ({
        id: item.id,
        productId: item.productId,
        name: item.productName,
        description: item.productDescription || '红色文创商品',
        price: item.price,
        quantity: item.quantity,
        image: item.productImage,
        icon: getProductIcon(item.productName),
        color: getProductColor(item.productName)
      })) || [],
      totalAmount: order.totalAmount,
      userId: order.userId
    }))
  } catch (error: any) {
    console.error('加载订单失败:', error)
    // 如果API失败，尝试从localStorage加载
    loadOrdersFromLocal()
  } finally {
    loading.value = false
  }
}

const loadOrdersFromLocal = () => {
  const ordersRaw = localStorage.getItem('orders')
  if (ordersRaw) {
    try {
      orders.value = JSON.parse(ordersRaw)
    } catch (e) {
      console.error('Failed to parse orders', e)
      orders.value = []
    }
  }
}

const saveOrdersToLocal = () => {
  localStorage.setItem('orders', JSON.stringify(orders.value))
}

// 映射后端状态到前端状态
const mapBackendStatus = (status: string): 'pending' | 'paid' | 'shipped' | 'completed' | 'cancelled' => {
  const statusMap: Record<string, 'pending' | 'paid' | 'shipped' | 'completed' | 'cancelled'> = {
    '待支付': 'pending',
    '待发货': 'paid',
    '已发货': 'shipped',
    '已完成': 'completed',
    '已取消': 'cancelled',
    'PENDING': 'pending',
    'PAID': 'paid',
    'SHIPPED': 'shipped',
    'COMPLETED': 'completed',
    'CANCELLED': 'cancelled',
    'pending': 'pending',
    'paid': 'paid',
    'shipped': 'shipped',
    'completed': 'completed',
    'cancelled': 'cancelled'
  }
  return statusMap[status] || 'pending'
}

// 根据商品名称获取图标
const getProductIcon = (name: string): string => {
  if (name.includes('书签')) return '🔖'
  if (name.includes('笔记本')) return '📓'
  if (name.includes('徽章')) return '🎖️'
  if (name.includes('明信片')) return '📮'
  if (name.includes('帆布包')) return '👜'
  if (name.includes('T恤')) return '👕'
  if (name.includes('杯子') || name.includes('马克杯')) return '☕'
  if (name.includes('钥匙扣')) return '🔑'
  return '🎁'
}

// 根据商品名称获取颜色
const getProductColor = (name: string): string => {
  const colors = [
    'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
    'linear-gradient(135deg, #30cfd0 0%, #330867 100%)',
    'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
    'linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)'
  ]
  const hash = name.split('').reduce((acc, char) => acc + char.charCodeAt(0), 0)
  return colors[hash % colors.length]
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    pending: '待支付',
    paid: '已支付',
    shipped: '已发货',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[status] || status
}

const getOrderCount = (order: Order) => {
  return order.items.reduce((sum, item) => sum + item.quantity, 0)
}

const gotoMall = () => router.push('/mall')

const payOrder = async (order: Order) => {
  try {
    await mallApi.payOrder(order.id, 'alipay')
    ElMessage.success('支付成功！')
    order.status = 'paid'
    saveOrdersToLocal()
    
    // 模拟发货流程
    setTimeout(async () => {
      try {
        await mallApi.updateOrderStatus(order.id, '已发货')
        order.status = 'shipped'
        saveOrdersToLocal()
        ElMessage.success('订单已发货！')
      } catch (error) {
        console.error('更新发货状态失败:', error)
      }
    }, 2000)
  } catch (error: any) {
    console.error('支付失败:', error)
    ElMessage.error(error.message || '支付失败，请稍后重试')
  }
}

const cancelOrder = (order: Order) => {
  ElMessageBox.confirm(
    '确定要取消该订单吗？',
    '取消订单',
    {
      confirmButtonText: '确定',
      cancelButtonText: '暂不',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await mallApi.cancelOrder(order.id)
      order.status = 'cancelled'
      saveOrdersToLocal()
      ElMessage.success('订单已取消')
    } catch (error: any) {
      console.error('取消订单失败:', error)
      ElMessage.error(error.message || '取消失败，请稍后重试')
    }
  }).catch(() => {})
}

const deleteOrder = (order: Order) => {
  ElMessageBox.confirm(
    '确定要删除该订单记录吗？删除后不可恢复',
    '删除订单',
    {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await mallApi.cancelOrder(order.id)
      const index = orders.value.findIndex(o => o.id === order.id)
      if (index > -1) {
        orders.value.splice(index, 1)
        saveOrdersToLocal()
        ElMessage.success('订单已删除')
      }
    } catch (error: any) {
      console.error('删除订单失败:', error)
      ElMessage.error(error.message || '删除失败，请稍后重试')
    }
  }).catch(() => {})
}

// 确认收货
const confirmReceipt = async (order: Order) => {
  try {
    await mallApi.confirmOrder(order.id)
    order.status = 'completed'
    saveOrdersToLocal()
    ElMessage.success('确认收货成功')
  } catch (error: any) {
    console.error('确认收货失败:', error)
    ElMessage.error(error.message || '确认收货失败')
  }
}

// 去评价 - 跳转到商城页面并打开商品详情
const goToReview = (order: Order) => {
  // 获取订单中第一个商品的ID
  const firstItem = order.items[0]
  if (firstItem && firstItem.productId) {
    // 跳转到商城页面，带上商品ID和评价标记
    router.push({
      path: '/mall',
      query: {
        productId: firstItem.productId.toString(),
        action: 'review'
      }
    })
  } else {
    // 如果没有商品ID，直接跳转到商城
    router.push('/mall')
    ElMessage.info('请在商城中找到对应商品进行评价')
  }
}
</script>

<style scoped>
/* Orders Page */
.orders-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 3rem 2rem;
  background-image: radial-gradient(rgba(160, 24, 47, 0.05) 1px, transparent 1px), radial-gradient(rgba(160, 24, 47, 0.05) 1px, #f5f7fa 1px);
  background-size: 20px 20px;
  background-position: 0 0, 10px 10px;
}

.orders-header {
  max-width: 1000px;
  margin: 0 auto 2.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 2.5rem 3rem;
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(160, 24, 47, 0.08);
  border-left: 8px solid #c41e3a;
  position: relative;
  overflow: hidden;
}

.orders-header::after {
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

.orders-header:hover .header-icon-box {
  transform: rotate(0deg) scale(1.05);
}

.orders-icon {
  font-size: 2.5rem;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.1));
}

.orders-header h1 {
  font-size: 2.2rem;
  font-weight: 800;
  color: #2c3e50;
  margin: 0 0 0.5rem 0;
  letter-spacing: 1px;
}

.orders-header p {
  font-size: 1.1rem;
  color: #666;
  margin: 0;
}

.shop-btn {
  font-weight: 600;
  border-radius: 12px;
  padding: 12px 24px;
  transition: all 0.3s;
}

.shop-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(160, 24, 47, 0.2);
}

/* Empty State */
.loading-state {
  max-width: 1000px;
  margin: 0 auto;
  background: white;
  padding: 3rem;
  border-radius: 20px;
  box-shadow: 0 4px 16px rgba(160, 24, 47, 0.04);
}

.empty-orders {
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
  background: linear-gradient(135deg, #c41e3a, #8b1e3f);
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

/* Orders List */
.orders-list {
  max-width: 1000px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.order-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(160, 24, 47, 0.04);
  border: 1px solid rgba(160, 24, 47, 0.05);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.order-card:hover {
  box-shadow: 0 16px 40px rgba(160, 24, 47, 0.15);
  transform: translateY(-4px);
  border-color: rgba(160, 24, 47, 0.2);
}

.order-header {
  padding: 1.5rem 2rem;
  background: linear-gradient(to right, #fff5f5, #fff);
  border-bottom: 1px solid rgba(160, 24, 47, 0.05);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-info {
  display: flex;
  gap: 2rem;
  font-size: 1rem;
  color: #666;
  align-items: center;
}

.order-id {
  font-weight: 700;
  color: #2c3e50;
  font-family: 'Roboto Mono', monospace;
  background: #f5f7fa;
  padding: 4px 12px;
  border-radius: 8px;
}

.order-time {
  color: #999;
}

.order-status {
  font-weight: 700;
  padding: 6px 16px;
  border-radius: 30px;
  font-size: 0.95rem;
  display: flex;
  align-items: center;
  gap: 6px;
}

.order-status::before {
  content: '';
  display: block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: currentColor;
}

.order-status.pending {
  background: #fff1f0;
  color: #ff4d4f;
  border: 1px solid rgba(255, 77, 79, 0.2);
}

.order-status.paid {
  background: rgba(160, 24, 47, 0.08);
  color: #a0182f;
  border: 1px solid rgba(160, 24, 47, 0.2);
}

.order-status.shipped {
  background: rgba(139, 30, 63, 0.1);
  color: #8b1e3f;
  border: 1px solid rgba(139, 30, 63, 0.2);
}

.order-status.completed {
  background: rgba(82, 196, 26, 0.1);
  color: #52c41a;
  border: 1px solid rgba(82, 196, 26, 0.2);
}

.order-status.cancelled {
  background: #f5f5f5;
  color: #999;
  border: 1px solid #e8e8e8;
}

.order-items {
  padding: 2rem;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 2rem;
  padding: 1.5rem 0;
  border-bottom: 1px dashed rgba(160, 24, 47, 0.1);
  transition: all 0.3s ease;
  border-radius: 12px;
}

.order-item:hover {
  background: linear-gradient(to right, rgba(160, 24, 47, 0.03), transparent);
  margin: 0 -1rem;
  padding: 1.5rem 1rem;
}

.order-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.order-item:last-child:hover {
  padding-bottom: 1.5rem;
}

.order-item:first-child {
  padding-top: 0;
}

.order-item:first-child:hover {
  padding-top: 1.5rem;
}

.item-image {
  width: 80px;
  height: 80px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

.item-icon {
  font-size: 3rem;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.1));
}

.item-details {
  flex: 1;
}

.item-name {
  font-size: 1.2rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 0.5rem 0;
}

.item-desc {
  font-size: 0.95rem;
  color: #888;
  margin: 0;
  line-height: 1.5;
}

.item-price-qty {
  text-align: right;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 0.4rem;
}

.item-price {
  font-size: 1.2rem;
  font-weight: 700;
  color: #2c3e50;
}

.item-qty {
  font-size: 1rem;
  color: #999;
  background: #f5f7fa;
  padding: 2px 8px;
  border-radius: 6px;
}

.order-footer {
  padding: 1.5rem 2rem;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
}

.order-total {
  font-size: 1.05rem;
  color: #666;
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.total-amount {
  font-size: 1.8rem;
  font-weight: 800;
  color: #c41e3a;
  font-family: 'Roboto', sans-serif;
}

.order-actions {
  display: flex;
  gap: 1.2rem;
}

@media (max-width: 768px) {
  .orders-page {
    padding: 1.5rem 1rem;
  }

  .orders-header {
    flex-direction: column;
    gap: 1.5rem;
    text-align: center;
    padding: 1.5rem;
  }

  .header-left {
    flex-direction: column;
    gap: 1rem;
  }

  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
    padding: 1.25rem;
  }

  .order-info {
    flex-direction: column;
    gap: 0.5rem;
    align-items: flex-start;
    width: 100%;
  }

  .order-status {
    align-self: flex-start;
  }

  .order-items {
    padding: 1rem;
  }

  .order-item {
    gap: 1rem;
    padding: 1.25rem 0;
  }

  .item-image {
    width: 64px;
    height: 64px;
  }

  .item-name {
    font-size: 1rem;
  }

  .order-footer {
    flex-direction: column;
    gap: 1.2rem;
    align-items: stretch;
    padding: 1.25rem;
  }

  .order-total {
    width: 100%;
    justify-content: flex-end;
  }

  .order-actions {
    display: flex;
    flex-direction: column;
    gap: 0.8rem;
  }

  .order-actions .el-button {
    width: 100%;
    margin-left: 0 !important;
  }

  .empty-orders {
    padding: 3rem 1.5rem;
    margin: 2rem auto;
  }
}
</style>
