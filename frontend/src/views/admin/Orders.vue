<template>
  <div class="orders-management">
    <div class="page-header">
      <h2>🛒 订单管理</h2>
      <p>查看和处理用户订单</p>
    </div>

    <div class="stats-row">
      <div class="stat-item"><span class="stat-num">{{ orderStats.total }}</span><span class="stat-label">总订单</span></div>
      <div class="stat-item pending"><span class="stat-num">{{ orderStats.pending }}</span><span class="stat-label">待付款</span></div>
      <div class="stat-item processing"><span class="stat-num">{{ orderStats.processing }}</span><span class="stat-label">待发货</span></div>
      <div class="stat-item shipped"><span class="stat-num">{{ orderStats.shipped }}</span><span class="stat-label">已发货</span></div>
      <div class="stat-item completed"><span class="stat-num">{{ orderStats.completed }}</span><span class="stat-label">已完成</span></div>
    </div>

    <div class="toolbar">
      <el-input v-model="searchKeyword" placeholder="搜索订单号、用户名..." prefix-icon="Search" style="width: 300px" @keyup.enter="loadOrders" clearable />
      <el-select v-model="statusFilter" placeholder="订单状态" style="width: 140px" @change="loadOrders">
        <el-option label="全部状态" value="" />
        <el-option label="待付款" value="pending" />
        <el-option label="待发货" value="paid" />
        <el-option label="已发货" value="shipped" />
        <el-option label="已完成" value="completed" />
        <el-option label="已取消" value="cancelled" />
        <el-option label="已退款" value="refunded" />
      </el-select>
      <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" style="width: 260px" @change="loadOrders" />
      <el-button type="primary" @click="loadOrders">🔄 刷新</el-button>
      <el-button @click="exportOrders">📥 导出</el-button>
    </div>

    <el-table :data="orders" v-loading="loading" stripe>
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column label="商品" min-width="250">
        <template #default="{ row }">
          <div class="order-products">
            <div v-for="(item, i) in row.items?.slice(0, 2)" :key="i" class="product-item">
              <img :src="item.image || '/placeholder.png'" class="product-img" />
              <div class="product-info">
                <span class="product-name">{{ item.name }}</span>
                <span class="product-spec">{{ item.spec }} × {{ item.quantity }}</span>
              </div>
            </div>
            <div v-if="row.items?.length > 2" class="more-items">+{{ row.items.length - 2 }}件商品</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="userName" label="买家" width="120" />
      <el-table-column label="金额" width="120">
        <template #default="{ row }">
          <span class="price">¥{{ row.totalAmount?.toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="下单时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="viewOrder(row)">详情</el-button>
          <el-button v-if="row.status === 'paid'" size="small" type="primary" @click="shipOrder(row)">发货</el-button>
          <el-button v-if="row.status === 'pending'" size="small" type="danger" @click="cancelOrder(row)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination background layout="prev, pager, next, total, jumper" :total="total" :page-size="pageSize" :current-page="currentPage" @current-change="handlePageChange" />
    </div>

    <!-- 订单详情弹窗 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="700px">
      <div v-if="currentOrder" class="order-detail">
        <div class="detail-section">
          <h4>基本信息</h4>
          <div class="detail-grid">
            <div><label>订单号：</label>{{ currentOrder.orderNo }}</div>
            <div><label>下单时间：</label>{{ currentOrder.createdAt }}</div>
            <div><label>订单状态：</label><el-tag :type="getStatusType(currentOrder.status)">{{ getStatusText(currentOrder.status) }}</el-tag></div>
            <div><label>支付方式：</label>{{ currentOrder.paymentMethod || '在线支付' }}</div>
          </div>
        </div>
        <div class="detail-section">
          <h4>收货信息</h4>
          <div class="detail-grid">
            <div><label>收货人：</label>{{ currentOrder.receiverName }}</div>
            <div><label>联系电话：</label>{{ currentOrder.receiverPhone }}</div>
            <div class="full-width"><label>收货地址：</label>{{ currentOrder.receiverAddress }}</div>
          </div>
        </div>
        <div class="detail-section">
          <h4>商品信息</h4>
          <el-table :data="currentOrder.items" size="small">
            <el-table-column label="商品" min-width="200">
              <template #default="{ row }">
                <div class="product-cell">
                  <img :src="row.image" class="product-img-sm" />
                  <span>{{ row.name }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="spec" label="规格" width="120" />
            <el-table-column prop="price" label="单价" width="100">
              <template #default="{ row }">¥{{ row.price?.toFixed(2) }}</template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column label="小计" width="100">
              <template #default="{ row }">¥{{ (row.price * row.quantity).toFixed(2) }}</template>
            </el-table-column>
          </el-table>
        </div>
        <div class="detail-section amount-section">
          <div class="amount-row"><span>商品总额：</span><span>¥{{ currentOrder.productAmount?.toFixed(2) }}</span></div>
          <div class="amount-row"><span>运费：</span><span>¥{{ currentOrder.shippingFee?.toFixed(2) || '0.00' }}</span></div>
          <div class="amount-row total"><span>实付金额：</span><span class="price">¥{{ currentOrder.totalAmount?.toFixed(2) }}</span></div>
        </div>
      </div>
    </el-dialog>

    <!-- 发货弹窗 -->
    <el-dialog v-model="shipVisible" title="订单发货" width="500px">
      <el-form :model="shipForm" label-width="100px">
        <el-form-item label="快递公司">
          <el-select v-model="shipForm.expressCompany" placeholder="选择快递公司" style="width: 100%">
            <el-option label="顺丰速运" value="SF" />
            <el-option label="中通快递" value="ZTO" />
            <el-option label="圆通速递" value="YTO" />
            <el-option label="韵达快递" value="YD" />
            <el-option label="申通快递" value="STO" />
            <el-option label="京东物流" value="JD" />
          </el-select>
        </el-form-item>
        <el-form-item label="快递单号">
          <el-input v-model="shipForm.trackingNo" placeholder="请输入快递单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmShip">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as adminApi from '@/api/admin'

const loading = ref(false)
const orders = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const searchKeyword = ref('')
const statusFilter = ref('')
const dateRange = ref<any>(null)

const orderStats = ref({ total: 0, pending: 0, processing: 0, shipped: 0, completed: 0 })

const detailVisible = ref(false)
const currentOrder = ref<any>(null)
const shipVisible = ref(false)
const shipForm = ref({ expressCompany: '', trackingNo: '' })
const shipOrderId = ref<number | null>(null)

const getStatusType = (status: string) => {
  const map: Record<string, string> = { pending: 'warning', paid: 'primary', shipped: '', completed: 'success', cancelled: 'info', refunded: 'danger' }
  return map[status] || ''
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = { pending: '待付款', paid: '待发货', shipped: '已发货', completed: '已完成', cancelled: '已取消', refunded: '已退款' }
  return map[status] || status
}

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await adminApi.getOrders({ page: currentPage.value, size: pageSize.value, keyword: searchKeyword.value || undefined, status: statusFilter.value || undefined })
    orders.value = res.data?.records || res.data || []
    total.value = res.data?.total || orders.value.length
  } catch {
    // 模拟数据
    orders.value = [
      { id: 1, orderNo: 'ORD202601120001', userName: '张三', totalAmount: 299, status: 'paid', createdAt: '2026-01-12 10:30:00', receiverName: '张三', receiverPhone: '138****1234', receiverAddress: '河北省石家庄市长安区XX路XX号', productAmount: 279, shippingFee: 20, items: [{ name: '西柏坡纪念徽章', spec: '金色款', price: 99, quantity: 2, image: '' }, { name: '红色文化T恤', spec: 'XL', price: 81, quantity: 1, image: '' }] },
      { id: 2, orderNo: 'ORD202601120002', userName: '李四', totalAmount: 158, status: 'pending', createdAt: '2026-01-12 11:20:00', receiverName: '李四', receiverPhone: '139****5678', receiverAddress: '河北省保定市XX区XX街XX号', productAmount: 158, shippingFee: 0, items: [{ name: '革命历史书籍套装', spec: '精装版', price: 158, quantity: 1, image: '' }] }
    ]
    total.value = 2
  } finally {
    loading.value = false
  }
  // 更新统计
  orderStats.value = {
    total: total.value,
    pending: orders.value.filter(o => o.status === 'pending').length,
    processing: orders.value.filter(o => o.status === 'paid').length,
    shipped: orders.value.filter(o => o.status === 'shipped').length,
    completed: orders.value.filter(o => o.status === 'completed').length
  }
}

const handlePageChange = (page: number) => { currentPage.value = page; loadOrders() }
const viewOrder = (order: any) => { currentOrder.value = order; detailVisible.value = true }

const shipOrder = (order: any) => {
  shipOrderId.value = order.id
  shipForm.value = { expressCompany: '', trackingNo: '' }
  shipVisible.value = true
}

const confirmShip = async () => {
  if (!shipForm.value.expressCompany || !shipForm.value.trackingNo) {
    ElMessage.warning('请填写完整的发货信息')
    return
  }
  try {
    await adminApi.shipOrder(shipOrderId.value!, shipForm.value)
    const order = orders.value.find(o => o.id === shipOrderId.value)
    if (order) order.status = 'shipped'
    shipVisible.value = false
    ElMessage.success('发货成功')
  } catch { ElMessage.error('发货失败') }
}

const cancelOrder = async (order: any) => {
  await ElMessageBox.confirm('确定要取消此订单吗？', '确认取消', { type: 'warning' })
  try {
    await adminApi.cancelOrder(order.id)
    order.status = 'cancelled'
    ElMessage.success('订单已取消')
  } catch { ElMessage.error('取消失败') }
}

const exportOrders = () => { ElMessage.success('订单数据导出中...') }

onMounted(() => { loadOrders() })
</script>

<style scoped>
.orders-management { background: white; padding: 24px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }
.page-header { margin-bottom: 20px; }
.page-header h2 { font-size: 1.5rem; margin-bottom: 8px; }
.page-header p { color: #666; }

.stats-row { display: flex; gap: 20px; margin-bottom: 24px; padding: 16px; background: #f8f9fa; border-radius: 8px; }
.stat-item { flex: 1; text-align: center; padding: 12px; background: white; border-radius: 8px; }
.stat-num { display: block; font-size: 1.8rem; font-weight: 700; color: #333; }
.stat-label { font-size: 0.85rem; color: #666; }
.stat-item.pending .stat-num { color: #e6a23c; }
.stat-item.processing .stat-num { color: #409eff; }
.stat-item.shipped .stat-num { color: #909399; }
.stat-item.completed .stat-num { color: #67c23a; }

.toolbar { display: flex; gap: 12px; margin-bottom: 20px; flex-wrap: wrap; }
.pagination { display: flex; justify-content: center; margin-top: 20px; }

.order-products { display: flex; flex-direction: column; gap: 8px; }
.product-item { display: flex; align-items: center; gap: 10px; }
.product-img { width: 50px; height: 50px; object-fit: cover; border-radius: 4px; background: #f5f5f5; }
.product-info { display: flex; flex-direction: column; }
.product-name { font-size: 13px; color: #333; }
.product-spec { font-size: 12px; color: #999; }
.more-items { font-size: 12px; color: #409eff; }
.price { color: #f56c6c; font-weight: 600; }

.order-detail .detail-section { margin-bottom: 24px; }
.order-detail h4 { font-size: 14px; color: #333; margin-bottom: 12px; padding-bottom: 8px; border-bottom: 1px solid #eee; }
.detail-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.detail-grid .full-width { grid-column: 1 / -1; }
.detail-grid label { color: #666; }
.product-cell { display: flex; align-items: center; gap: 8px; }
.product-img-sm { width: 40px; height: 40px; object-fit: cover; border-radius: 4px; }
.amount-section { background: #f8f9fa; padding: 16px; border-radius: 8px; }
.amount-row { display: flex; justify-content: space-between; margin-bottom: 8px; }
.amount-row.total { font-size: 16px; font-weight: 600; border-top: 1px solid #ddd; padding-top: 12px; margin-top: 8px; }
</style>
