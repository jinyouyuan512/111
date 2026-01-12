<template>
  <div class="content-management">
    <div class="page-header">
      <h2>📝 内容管理</h2>
      <p>管理用户发布的动态、评论等内容</p>
    </div>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="动态管理" name="posts">
        <div class="toolbar">
          <el-input v-model="postSearch" placeholder="搜索动态内容..." prefix-icon="Search" style="width: 300px" @input="searchPosts" clearable />
          <el-select v-model="postStatus" placeholder="状态筛选" style="width: 120px" @change="loadPosts">
            <el-option label="全部" value="" />
            <el-option label="正常" value="normal" />
            <el-option label="已隐藏" value="hidden" />
            <el-option label="待审核" value="pending" />
          </el-select>
          <el-button type="primary" @click="loadPosts">🔄 刷新</el-button>
        </div>

        <el-table :data="posts" v-loading="postsLoading" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column label="内容" min-width="300">
            <template #default="{ row }">
              <div class="post-content">
                <p>{{ row.content?.substring(0, 100) }}{{ row.content?.length > 100 ? '...' : '' }}</p>
                <div v-if="row.images?.length" class="post-images">
                  <span>📷 {{ row.images.length }}张图片</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="authorName" label="作者" width="120" />
          <el-table-column label="互动" width="150">
            <template #default="{ row }">
              <span>👍 {{ row.likeCount || 0 }}</span>
              <span style="margin-left: 10px">💬 {{ row.commentCount || 0 }}</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag v-if="row.status === 'normal'" type="success">正常</el-tag>
              <el-tag v-else-if="row.status === 'hidden'" type="danger">已隐藏</el-tag>
              <el-tag v-else type="warning">待审核</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="发布时间" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="viewPost(row)">查看</el-button>
              <el-button v-if="row.status === 'normal'" size="small" type="warning" @click="hidePost(row)">隐藏</el-button>
              <el-button v-else size="small" type="success" @click="showPost(row)">显示</el-button>
              <el-button size="small" type="danger" @click="deletePost(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination">
          <el-pagination background layout="prev, pager, next, total" :total="postsTotal" :page-size="20" :current-page="postsPage" @current-change="handlePostsPageChange" />
        </div>
      </el-tab-pane>

      <el-tab-pane label="评论管理" name="comments">
        <div class="toolbar">
          <el-input v-model="commentSearch" placeholder="搜索评论内容..." prefix-icon="Search" style="width: 300px" clearable />
          <el-button type="primary" @click="loadComments">🔄 刷新</el-button>
        </div>

        <el-table :data="comments" v-loading="commentsLoading" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="content" label="评论内容" min-width="300" />
          <el-table-column prop="authorName" label="评论者" width="120" />
          <el-table-column prop="postId" label="动态ID" width="100" />
          <el-table-column prop="createdAt" label="评论时间" width="180" />
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button size="small" type="danger" @click="deleteComment(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="举报处理" name="reports">
        <div class="toolbar">
          <el-select v-model="reportStatus" placeholder="状态筛选" style="width: 120px" @change="loadReports">
            <el-option label="全部" value="" />
            <el-option label="待处理" value="pending" />
            <el-option label="已处理" value="resolved" />
            <el-option label="已驳回" value="rejected" />
          </el-select>
          <el-button type="primary" @click="loadReports">🔄 刷新</el-button>
        </div>

        <el-table :data="reports" v-loading="reportsLoading" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column label="举报类型" width="120">
            <template #default="{ row }">
              <el-tag>{{ row.targetType === 'post' ? '动态' : '评论' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="reason" label="举报原因" min-width="200" />
          <el-table-column prop="reporterName" label="举报人" width="120" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag v-if="row.status === 'pending'" type="warning">待处理</el-tag>
              <el-tag v-else-if="row.status === 'resolved'" type="success">已处理</el-tag>
              <el-tag v-else type="info">已驳回</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="举报时间" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <template v-if="row.status === 'pending'">
                <el-button size="small" type="success" @click="resolveReport(row)">通过</el-button>
                <el-button size="small" type="info" @click="rejectReport(row)">驳回</el-button>
              </template>
              <span v-else class="handled-text">已处理</span>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 查看动态弹窗 -->
    <el-dialog v-model="postDialogVisible" title="动态详情" width="600px">
      <div v-if="currentPost" class="post-detail">
        <div class="detail-row"><label>作者：</label><span>{{ currentPost.authorName }}</span></div>
        <div class="detail-row"><label>发布时间：</label><span>{{ currentPost.createdAt }}</span></div>
        <div class="detail-row"><label>内容：</label></div>
        <div class="detail-content">{{ currentPost.content }}</div>
        <div v-if="currentPost.images?.length" class="detail-images">
          <img v-for="(img, i) in currentPost.images" :key="i" :src="img" alt="图片" />
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as adminApi from '@/api/admin'

const activeTab = ref('posts')

// 动态管理
const posts = ref<any[]>([])
const postsLoading = ref(false)
const postsTotal = ref(0)
const postsPage = ref(1)
const postSearch = ref('')
const postStatus = ref('')
const postDialogVisible = ref(false)
const currentPost = ref<any>(null)

// 评论管理
const comments = ref<any[]>([])
const commentsLoading = ref(false)
const commentSearch = ref('')

// 举报管理
const reports = ref<any[]>([])
const reportsLoading = ref(false)
const reportStatus = ref('pending')

const loadPosts = async () => {
  postsLoading.value = true
  try {
    const res = await adminApi.getPosts({ page: postsPage.value, size: 20, keyword: postSearch.value || undefined, status: postStatus.value || undefined })
    posts.value = res.data?.records || res.data || []
    postsTotal.value = res.data?.total || posts.value.length
  } catch (e) {
    // 使用模拟数据
    posts.value = [
      { id: 1, content: '今天参观了西柏坡纪念馆，深受教育，革命先辈的精神永远值得我们学习！', authorName: '红色旅行者', likeCount: 128, commentCount: 23, status: 'normal', createdAt: '2026-01-12 10:30:00', images: ['img1.jpg'] },
      { id: 2, content: '白洋淀的风景真美，雁翎队的故事让人感动', authorName: '历史爱好者', likeCount: 89, commentCount: 15, status: 'normal', createdAt: '2026-01-11 15:20:00' },
      { id: 3, content: '这是一条待审核的内容...', authorName: '新用户', likeCount: 0, commentCount: 0, status: 'pending', createdAt: '2026-01-12 18:00:00' }
    ]
    postsTotal.value = 3
  } finally {
    postsLoading.value = false
  }
}

const searchPosts = () => { postsPage.value = 1; loadPosts() }
const handlePostsPageChange = (page: number) => { postsPage.value = page; loadPosts() }
const viewPost = (post: any) => { currentPost.value = post; postDialogVisible.value = true }
const hidePost = async (post: any) => {
  await ElMessageBox.confirm('确定要隐藏这条动态吗？', '确认')
  try {
    await adminApi.hidePost(post.id)
    post.status = 'hidden'
    ElMessage.success('已隐藏')
  } catch { ElMessage.error('操作失败') }
}
const showPost = async (post: any) => {
  try {
    await adminApi.showPost(post.id)
    post.status = 'normal'
    ElMessage.success('已显示')
  } catch { ElMessage.error('操作失败') }
}
const deletePost = async (post: any) => {
  await ElMessageBox.confirm('确定要删除这条动态吗？此操作不可恢复', '确认删除', { type: 'warning' })
  try {
    await adminApi.deletePost(post.id)
    posts.value = posts.value.filter(p => p.id !== post.id)
    ElMessage.success('删除成功')
  } catch { ElMessage.error('删除失败') }
}

const loadComments = async () => {
  commentsLoading.value = true
  try {
    const res = await adminApi.getComments({ page: 1, size: 50 })
    comments.value = res.data?.records || res.data || []
  } catch {
    comments.value = [
      { id: 1, content: '写得真好，学习了！', authorName: '用户A', postId: 1, createdAt: '2026-01-12 11:00:00' },
      { id: 2, content: '下次我也要去看看', authorName: '用户B', postId: 1, createdAt: '2026-01-12 12:30:00' },
      { id: 3, content: '感谢分享', authorName: '用户C', postId: 2, createdAt: '2026-01-11 16:00:00' }
    ]
  } finally {
    commentsLoading.value = false
  }
}

const deleteComment = async (comment: any) => {
  await ElMessageBox.confirm('确定要删除这条评论吗？', '确认删除', { type: 'warning' })
  try {
    await adminApi.deleteComment(comment.id)
    comments.value = comments.value.filter(c => c.id !== comment.id)
    ElMessage.success('删除成功')
  } catch { ElMessage.error('删除失败') }
}

const loadReports = async () => {
  reportsLoading.value = true
  try {
    const res = await adminApi.getReports({ status: reportStatus.value || undefined })
    reports.value = res.data?.records || res.data || []
  } catch {
    reports.value = [
      { id: 1, targetType: 'post', targetId: 5, reason: '内容不实', reporterName: '举报者1', status: 'pending', createdAt: '2026-01-12 14:00:00' },
      { id: 2, targetType: 'comment', targetId: 10, reason: '言语不当', reporterName: '举报者2', status: 'pending', createdAt: '2026-01-12 15:30:00' },
      { id: 3, targetType: 'post', targetId: 3, reason: '广告内容', reporterName: '举报者3', status: 'resolved', createdAt: '2026-01-11 10:00:00' }
    ]
  } finally {
    reportsLoading.value = false
  }
}

const resolveReport = async (report: any) => {
  await ElMessageBox.confirm('确定通过此举报并处理相关内容？', '确认')
  try {
    await adminApi.resolveReport(report.id)
    report.status = 'resolved'
    ElMessage.success('已处理')
  } catch { ElMessage.error('操作失败') }
}

const rejectReport = async (report: any) => {
  await ElMessageBox.confirm('确定驳回此举报？', '确认')
  try {
    await adminApi.rejectReport(report.id)
    report.status = 'rejected'
    ElMessage.success('已驳回')
  } catch { ElMessage.error('操作失败') }
}

const handleTabChange = (tab: string) => {
  if (tab === 'posts') loadPosts()
  else if (tab === 'comments') loadComments()
  else if (tab === 'reports') loadReports()
}

onMounted(() => { loadPosts() })
</script>

<style scoped>
.content-management { background: white; padding: 24px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }
.page-header { margin-bottom: 20px; }
.page-header h2 { font-size: 1.5rem; margin-bottom: 8px; }
.page-header p { color: #666; }
.toolbar { display: flex; gap: 12px; margin-bottom: 20px; }
.pagination { display: flex; justify-content: center; margin-top: 20px; }
.post-content p { margin: 0; color: #333; }
.post-images { margin-top: 8px; color: #999; font-size: 12px; }
.handled-text { color: #999; }
.post-detail .detail-row { margin-bottom: 12px; }
.post-detail .detail-row label { font-weight: 600; margin-right: 8px; }
.detail-content { background: #f5f5f5; padding: 12px; border-radius: 8px; margin: 12px 0; white-space: pre-wrap; }
.detail-images { display: flex; gap: 10px; flex-wrap: wrap; }
.detail-images img { width: 150px; height: 150px; object-fit: cover; border-radius: 8px; }
</style>
