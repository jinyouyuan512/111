<template>
  <div class="mall-applications-page">
    <div class="page-header">
      <h2>商城上架审核</h2>
      <p>审核众创空间作品的商城上架申请</p>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-radio-group v-model="statusFilter" @change="loadApplications">
        <el-radio-button label="all">全部</el-radio-button>
        <el-radio-button label="pending">待审核</el-radio-button>
        <el-radio-button label="approved">已通过</el-radio-button>
        <el-radio-button label="rejected">已拒绝</el-radio-button>
      </el-radio-group>
      <el-button type="primary" @click="loadApplications" :loading="loading">
        刷新
      </el-button>
    </div>

    <!-- 申请列表 -->
    <el-table :data="applications" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="productName" label="商品名称" min-width="150" />
      <el-table-column prop="category" label="分类" width="120" />
      <el-table-column label="价格" width="100">
        <template #default="{ row }">
          ¥{{ row.suggestedPrice }}
        </template>
      </el-table-column>
      <el-table-column prop="initialStock" label="库存" width="80" />
      <el-table-column label="图标" width="60">
        <template #default="{ row }">
          <span style="font-size: 24px;">{{ row.icon }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" width="160">
        <template #default="{ row }">
          {{ formatDate(row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button 
            v-if="row.status === 'pending'"
            type="success" 
            size="small"
            @click="handleReview(row, true)"
          >
            通过
          </el-button>
          <el-button 
            v-if="row.status === 'pending'"
            type="danger" 
            size="small"
            @click="handleReview(row, false)"
          >
            拒绝
          </el-button>
          <el-button 
            type="info" 
            size="small"
            @click="viewDetail(row)"
          >
            详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadApplications"
        @current-change="loadApplications"
      />
    </div>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="申请详情" width="600px">
      <div v-if="currentApplication" class="application-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="商品名称" :span="2">
            {{ currentApplication.productName }}
          </el-descriptions-item>
          <el-descriptions-item label="分类">
            {{ currentApplication.category }}
          </el-descriptions-item>
          <el-descriptions-item label="图标">
            <span style="font-size: 32px;">{{ currentApplication.icon }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="建议价格">
            ¥{{ currentApplication.suggestedPrice }}
          </el-descriptions-item>
          <el-descriptions-item label="初始库存">
            {{ currentApplication.initialStock }}
          </el-descriptions-item>
          <el-descriptions-item label="商品描述" :span="2">
            {{ currentApplication.description || '无' }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentApplication.status)">
              {{ getStatusText(currentApplication.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="申请时间">
            {{ formatDate(currentApplication.createdAt) }}
          </el-descriptions-item>
          <el-descriptions-item v-if="currentApplication.reviewComment" label="审核意见" :span="2">
            {{ currentApplication.reviewComment }}
          </el-descriptions-item>
          <el-descriptions-item v-if="currentApplication.productId" label="商品ID">
            {{ currentApplication.productId }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- 审核弹窗 -->
    <el-dialog v-model="reviewVisible" :title="reviewApproved ? '通过申请' : '拒绝申请'" width="500px">
      <el-form label-position="top">
        <el-form-item label="审核意见">
          <el-input 
            v-model="reviewComment" 
            type="textarea" 
            :rows="3"
            :placeholder="reviewApproved ? '可选：填写通过理由' : '请填写拒绝理由'"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewVisible = false">取消</el-button>
        <el-button 
          :type="reviewApproved ? 'success' : 'danger'" 
          @click="submitReview"
          :loading="submitting"
        >
          {{ reviewApproved ? '确认通过' : '确认拒绝' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { creativeApi, type MallApplicationVO } from '@/api/creative'

const loading = ref(false)
const applications = ref<MallApplicationVO[]>([])
const statusFilter = ref('all')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const detailVisible = ref(false)
const currentApplication = ref<MallApplicationVO | null>(null)

const reviewVisible = ref(false)
const reviewApproved = ref(false)
const reviewComment = ref('')
const reviewingId = ref<number | null>(null)
const submitting = ref(false)

const loadApplications = async () => {
  loading.value = true
  try {
    const response = await creativeApi.getMallApplicationList({
      page: currentPage.value,
      size: pageSize.value,
      status: statusFilter.value === 'all' ? undefined : statusFilter.value
    })
    
    const data = response.data || response
    applications.value = data.records || []
    total.value = data.total || 0
  } catch (error) {
    console.error('加载申请列表失败:', error)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝'
  }
  return map[status] || status
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

const viewDetail = (row: MallApplicationVO) => {
  currentApplication.value = row
  detailVisible.value = true
}

const handleReview = (row: MallApplicationVO, approved: boolean) => {
  reviewingId.value = row.id
  reviewApproved.value = approved
  reviewComment.value = ''
  reviewVisible.value = true
}

const submitReview = async () => {
  if (!reviewingId.value) return
  
  if (!reviewApproved.value && !reviewComment.value.trim()) {
    ElMessage.warning('请填写拒绝理由')
    return
  }
  
  submitting.value = true
  try {
    await creativeApi.reviewMallApplication(reviewingId.value, {
      approved: reviewApproved.value,
      comment: reviewComment.value
    })
    
    ElMessage.success(reviewApproved.value ? '已通过，商品已上架' : '已拒绝')
    reviewVisible.value = false
    loadApplications()
  } catch (error: any) {
    console.error('审核失败:', error)
    ElMessage.error(error.response?.data?.message || '审核失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadApplications()
})
</script>

<style scoped>
.mall-applications-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #2c3e50;
}

.page-header p {
  margin: 0;
  color: #666;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.application-detail {
  padding: 10px 0;
}
</style>
