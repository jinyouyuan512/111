<template>
  <div class="users-management">
    <div class="toolbar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索用户名、邮箱..."
        prefix-icon="Search"
        style="width: 300px"
        @input="handleSearch"
      />
      <el-select v-model="roleFilter" placeholder="角色筛选" style="width: 150px" @change="loadUsers">
        <el-option label="全部" value="" />
        <el-option label="普通用户" value="user" />
        <el-option label="设计师" value="designer" />
        <el-option label="管理员" value="admin" />
      </el-select>
      <el-button type="primary" @click="loadUsers">刷新</el-button>
    </div>

    <el-table :data="users" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="150" />
      <el-table-column prop="email" label="邮箱" width="200" />
      <el-table-column prop="nickname" label="昵称" width="150" />
      <el-table-column label="角色" width="120">
        <template #default="{ row }">
          <el-tag v-if="row.role === 'admin'" type="danger">管理员</el-tag>
          <el-tag v-else-if="row.role === 'designer'" type="warning">设计师</el-tag>
          <el-tag v-else>普通用户</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="level" label="等级" width="80" />
      <el-table-column prop="points" label="积分" width="100" />
      <el-table-column prop="createdAt" label="注册时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="viewUser(row)">查看</el-button>
          <el-button size="small" type="warning" @click="editUser(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteUser(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

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
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as adminApi from '@/api/admin'

const loading = ref(false)
const users = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const searchKeyword = ref('')
const roleFilter = ref('')

const loadUsers = async () => {
  try {
    loading.value = true
    const response = await adminApi.getUsers({
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value || undefined,
      role: roleFilter.value || undefined
    })
    
    users.value = response.data?.records || []
    total.value = response.data?.total || 0
  } catch (error) {
    console.error('加载用户列表失败:', error)
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadUsers()
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  loadUsers()
}

const viewUser = (user: any) => {
  ElMessage.info(`查看用户: ${user.username}`)
}

const editUser = (user: any) => {
  ElMessage.info(`编辑用户: ${user.username}`)
}

const deleteUser = async (user: any) => {
  try {
    await ElMessageBox.confirm(`确定要删除用户 ${user.username} 吗？`, '确认删除', { type: 'warning' })
    await adminApi.deleteUser(user.id)
    ElMessage.success('删除成功')
    loadUsers()
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.users-management {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
