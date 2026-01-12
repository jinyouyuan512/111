<template>
  <MainLayout>
    <div class="profile-page">
      <div class="profile-header">
        <div class="cover"></div>
        <div class="info-card">
          <div class="avatar-wrapper">
            <el-avatar :size="100" :src="userInfo?.avatar">{{ username }}</el-avatar>
            <el-button class="change-avatar-btn" size="small" @click="triggerAvatarUpload" :loading="avatarUploading">更换头像</el-button>
            <input ref="avatarInput" type="file" accept="image/*" style="display: none" @change="handleAvatarUpload" />
          </div>
          <h1>{{ userInfo?.username || '游客' }}</h1>
          <p>{{ userInfo?.email || '未设置邮箱' }}</p>
        </div>
      </div>
      <div class="profile-content">
        <el-card>
          <h2>个人信息</h2>
          <el-form :model="form" label-width="100px">
            <el-form-item label="用户名"><el-input v-model="form.username" /></el-form-item>
            <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
            <el-form-item><el-button type="primary" @click="save">保存</el-button></el-form-item>
          </el-form>
        </el-card>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import MainLayout from '@/layouts/MainLayout.vue'

const userInfo = ref<any>(null)
const form = ref({ username: '', email: '' })
const avatarInput = ref<HTMLInputElement | null>(null)
const avatarUploading = ref(false)

const username = computed(() => userInfo.value?.username?.charAt(0).toUpperCase() || 'U')

onMounted(() => {
  const str = localStorage.getItem('userInfo')
  if (str) {
    userInfo.value = JSON.parse(str)
    form.value.username = userInfo.value.username || ''
    form.value.email = userInfo.value.email || ''
  }
})

const save = () => {
  if (userInfo.value) {
    userInfo.value = { ...userInfo.value, ...form.value }
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    ElMessage.success('保存成功')
  }
}

const triggerAvatarUpload = () => avatarInput.value?.click()

const handleAvatarUpload = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return
  if (!file.type.startsWith('image/')) { ElMessage.error('请选择图片文件'); return }
  if (file.size > 5 * 1024 * 1024) { ElMessage.error('图片大小不能超过5MB'); return }
  avatarUploading.value = true
  try {
    const token = localStorage.getItem('token')
    if (!token) { ElMessage.error('请先登录'); return }
    const formData = new FormData()
    formData.append('file', file)
    const response = await fetch('http://localhost:8081/api/users/avatar', {
      method: 'POST',
      headers: { 'Authorization': 'Bearer ' + token },
      body: formData
    })
    const result = await response.json()
    if (result.code === 200 && result.data) {
      if (userInfo.value) {
        userInfo.value.avatar = result.data
        localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
      }
      ElMessage.success('头像更新成功！')
    } else {
      ElMessage.error(result.message || '头像上传失败')
    }
  } catch (error) {
    ElMessage.error('头像上传失败')
  } finally {
    avatarUploading.value = false
    target.value = ''
  }
}
</script>

<style scoped>
.profile-page { min-height: 100vh; background: #f5f7fa; }
.profile-header { position: relative; margin-bottom: 2rem; }
.cover { height: 200px; background: linear-gradient(135deg, #c41e3a, #8b1e3f); }
.info-card { max-width: 800px; margin: -50px auto 0; background: white; border-radius: 12px; padding: 2rem; text-align: center; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.avatar-wrapper { display: flex; flex-direction: column; align-items: center; gap: 12px; }
.change-avatar-btn { background: linear-gradient(135deg, #c41e3a, #a0182f); color: white; border: none; }
.info-card h1 { margin: 1rem 0 0.5rem; font-size: 2rem; color: #333; }
.info-card p { color: #666; margin: 0; }
.profile-content { max-width: 800px; margin: 0 auto; padding: 0 2rem 2rem; }
</style>