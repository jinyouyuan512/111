<template>
  <MainLayout>
    <div class="admin-page">
      <PageHeader title="社区审核管理" subtitle="管理用户举报和内容审核" />
      
      <div class="admin-container">
        <!-- 统计卡片 -->
        <div class="stats-cards">
          <div class="stat-card pending">
            <div class="stat-icon">⏳</div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pending }}</div>
              <div class="stat-label">待处理</div>
            </div>
          </div>
          <div class="stat-card processing">
            <div class="stat-icon">🔍</div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.processing }}</div>
              <div class="stat-label">处理中</div>
            </div>
          </div>
          <div class="stat-card resolved">
            <div class="stat-icon">✅</div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.resolved }}</div>
              <div class="stat-label">已解决</div>
            </div>
          </div>
          <div class="stat-card rejected">
            <div class="stat-icon">❌</div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.rejected }}</div>
              <div class="stat-label">已驳回</div>
            </div>
          </div>
        </div>

        <!-- 筛选工具栏 -->
        <div class="toolbar">
          <el-select v-model="filters.status" placeholder="状态" style="width: 150px" @change="loadReports">
            <el-option label="全部" value="" />
            <el-option label="待处理" value="pending" />
            <el-option label="处理中" value="processing" />
            <el-option label="已解决" value="resolved" />
            <el-option label="已驳回" value="rejected" />
          </el-select>
          
          <el-select v-model="filters.targetType" placeholder="类型" style="width: 150px" @change="loadReports">
            <el-option label="全部" value="" />
            <el-option label="动态" value="post" />
            <el-option label="评论" value="comment" />
            <el-option label="用户" value="user" />
          </el-select>
          
          <el-button type="primary" @click="loadReports">刷新</el-button>
        </div>

        <!-- 举报列表 -->
        <div class="reports-list">
          <el-table :data="reports" v-loading="loading" stripe>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column label="举报类型" width="120">
              <template #default="{ row }">
                <el-tag v-if="row.targetType === 'post'" type="primary">动态</el-tag>
                <el-tag v-else-if="row.targetType === 'comment'" type="success">评论</el-tag>
                <el-tag v-else type="warning">用户</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="reason" label="举报原因" width="150" />
            <el-table-column prop="description" label="详细说明" show-overflow-tooltip />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag v-if="row.status === 'pending'" type="warning">待处理</el-tag>
                <el-tag v-else-if="row.status === 'processing'" type="info">处理中</el-tag>
                <el-tag v-else-if="row.status === 'resolved'" type="success">已解决</el-tag>
                <el-tag v-else type="danger">已驳回</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="举报时间" width="180" />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button size="small" @click="viewDetail(row)">查看</el-button>
                <el-button v-if="row.status === 'pending'" size="small" type="primary" @click="handleReport(row, 'resolved')">
                  通过
                </el-button>
                <el-button v-if="row.status === 'pending'" size="small" type="danger" @click="handleReport(row, 'rejected')">
                  驳回
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <div class="pagination">
            <el-pagination
              background
              layout="prev, pager, next, total"
              :total="total"
              :page-size="pageSize"
              :current-page="currentPage"
              @current-change="handlePageChange"
            />
          </div>
        </div>
      </div>

      <!-- 详情对话框 -->
      <el-dialog v-model="detailVisible" title="举报详情" width="600px">
        <div v-if="currentReport" class="report-detail">
          <div class="detail-row">
            <span class="label">举报ID:</span>
            <span class="value">{{ currentReport.id }}</span>
          </div>
          <div class="detail-row">
            <span class="label">举报类型:</span>
            <span class="value">{{ getTargetTypeText(currentReport.targetType) }}</span>
          </div>
          <div class="detail-row">
            <span class="label">目标ID:</span>
            <span class="value">{{ currentReport.targetId }}</span>
          </div>
          <div class="detail-row">
            <span class="label">举报原因:</span>
            <span class="value">{{ currentReport.reason }}</span>
          </div>
          <div class="detail-row">
            <span class="label">详细说明:</span>
            <span class="value">{{ currentReport.description || '无' }}</span>
          </div>
          <div class="detail-row">
            <span class="label">举报时间:</span>
            <span class="value">{{ currentReport.createdAt }}</span>
          </div>
          <div class="detail-row">
            <span class="label">状态:</span>
            <span class="value">{{ getStatusText(currentReport.status) }}</span>
          </div>
          <div v-if="currentReport.handleResult" class="detail-row">
            <span class="label">处理结果:</span>
            <span class="value">{{ currentReport.handleResult }}</span>
          </div>
        </div>
        <template #footer>
          <el-button @click="detailVisible = false">关闭</el-button>
        </template>
      </el-dialog>

      <!-- 处理对话框 -->
      <el-dialog v-model="handleVisible" title="处理举报" width="500px">
        <el-form :model="handleForm" label-width="100px">
          <el-form-item label="处理结果">
            <el-radio-group v-model="handleForm.result">
              <el-radio label="resolved">通过</el-radio>
              <el-radio label="rejected">驳回</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="处理说明">
            <el-input
              v-model="handleForm.description"
              type="textarea"
              :rows="4"
              placeholder="请输入处理说明"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="handleVisible = false">取消</el-button>
          <el-button type="primary" @click="submitHandle">确定</el-button>
        </template>
      </el-dialog>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import MainLayout from '@/layouts/MainLayout.vue'
import PageHeader from '@/components/PageHeader.vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

// 状态
const loading = ref(false)
const reports = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const detailVisible = ref(false)
const handleVisible = ref(false)
const currentReport = ref<any>(null)

const stats = reactive({
  pending: 0,
  processing: 0,
  resolved: 0,
  rejected: 0
})

const filters = reactive({
  status: '',
  targetType: ''
})

const handleForm = reactive({
  result: 'resolved',
  description: ''
})

// 加载举报列表
const loadReports = async () => {
  try {
    loading.value = true
    const response = await request.get('/social/reports', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        status: filters.status || undefined,
        targetType: filters.targetType || undefined
      }
    })
    
    reports.value = response.data.records || []
    total.value = response.data.total || 0
    
    // 更新统计
    updateStats()
  } catch (error) {
    console.error('加载举报列表失败:', error)
    ElMessage.error('加载举报列表失败')
  } finally {
    loading.value = false
  }
}

// 更新统计
const updateStats = async () => {
  try {
    const [pending, processing, resolved, rejected] = await Promise.all([
      request.get('/social/reports', { params: { page: 1, size: 1, status: 'pending' } }),
      request.get('/social/reports', { params: { page: 1, size: 1, status: 'processing' } }),
      request.get('/social/reports', { params: { page: 1, size: 1, status: 'resolved' } }),
      request.get('/social/reports', { params: { page: 1, size: 1, status: 'rejected' } })
    ])
    
    stats.pending = pending.data.total || 0
    stats.processing = processing.data.total || 0
    stats.resolved = resolved.data.total || 0
    stats.rejected = rejected.data.total || 0
  } catch (error) {
    console.error('更新统计失败:', error)
  }
}

// 查看详情
const viewDetail = (report: any) => {
  currentReport.value = report
  detailVisible.value = true
}

// 处理举报
const handleReport = (report: any, result: string) => {
  currentReport.value = report
  handleForm.result = result
  handleForm.description = ''
  handleVisible.value = true
}

// 提交处理
const submitHandle = async () => {
  try {
    await request.put(`/social/reports/${currentReport.value.id}/handle`, null, {
      params: {
        result: handleForm.result,
        description: handleForm.description || undefined
      }
    })
    
    ElMessage.success('处理成功')
    handleVisible.value = false
    loadReports()
  } catch (error) {
    console.error('处理失败:', error)
    ElMessage.error('处理失败')
  }
}

// 分页
const handlePageChange = (page: number) => {
  currentPage.value = page
  loadReports()
}

// 辅助函数
const getTargetTypeText = (type: string) => {
  const map: Record<string, string> = {
    post: '动态',
    comment: '评论',
    user: '用户'
  }
  return map[type] || type
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    pending: '待处理',
    processing: '处理中',
    resolved: '已解决',
    rejected: '已驳回'
  }
  return map[status] || status
}

onMounted(() => {
  loadReports()
})
</script>

<style scoped>
.admin-page {
  background: #f5f7fa;
  min-height: 100vh;
}

.admin-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
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

.stat-card.pending .stat-icon {
  background: #fff7e6;
}

.stat-card.processing .stat-icon {
  background: #e6f7ff;
}

.stat-card.resolved .stat-icon {
  background: #f6ffed;
}

.stat-card.rejected .stat-icon {
  background: #fff1f0;
}

.stat-info {
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
}

.toolbar {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  margin-bottom: 20px;
  display: flex;
  gap: 12px;
  align-items: center;
}

.reports-list {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.report-detail {
  padding: 10px 0;
}

.detail-row {
  display: flex;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.detail-row:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.detail-row .label {
  width: 100px;
  color: #666;
  font-weight: 600;
}

.detail-row .value {
  flex: 1;
  color: #2c3e50;
}
</style>
