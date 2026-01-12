<template>
  <MainLayout>
    <div class="upload-page">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-content">
          <el-button 
            icon="ArrowLeft" 
            @click="goBack"
            class="back-btn"
          >
            返回
          </el-button>
          <h1 class="page-title">上传作品</h1>
          <p class="page-subtitle">分享您的创意设计，传承红色文化</p>
        </div>
      </div>

      <!-- 上传表单 -->
      <div class="upload-container">
        <el-form
          ref="uploadFormRef"
          :model="uploadForm"
          :rules="formRules"
          label-width="120px"
          class="upload-form"
        >
          <!-- 基本信息 -->
          <div class="form-section">
            <h3 class="section-title">基本信息</h3>
            
            <el-form-item label="作品标题" prop="title" required>
              <el-input
                v-model="uploadForm.title"
                placeholder="请输入作品标题"
                maxlength="100"
                show-word-limit
                clearable
              />
            </el-form-item>

            <el-form-item label="作品分类" prop="categoryType" required>
              <el-select
                v-model="uploadForm.categoryType"
                placeholder="请选择作品分类"
                style="width: 100%"
              >
                <el-option label="海报设计" :value="1" />
                <el-option label="Logo设计" :value="2" />
                <el-option label="文创产品" :value="3" />
                <el-option label="视频动画" :value="4" />
              </el-select>
            </el-form-item>

            <el-form-item label="作品描述" prop="description" required>
              <el-input
                v-model="uploadForm.description"
                type="textarea"
                :rows="6"
                placeholder="请详细描述您的作品，包括创作背景、设计理念等"
                maxlength="1000"
                show-word-limit
              />
            </el-form-item>
          </div>

          <!-- 设计理念 -->
          <div class="form-section">
            <h3 class="section-title">设计理念</h3>
            
            <el-form-item label="设计理念" prop="designConcept">
              <el-input
                v-model="uploadForm.designConcept"
                type="textarea"
                :rows="5"
                placeholder="请分享您的设计理念和创作思路"
                maxlength="1000"
                show-word-limit
              />
            </el-form-item>
          </div>

          <!-- 作品文件 -->
          <div class="form-section">
            <h3 class="section-title">作品文件</h3>
            
            <el-form-item label="封面图片" prop="coverImage" required>
              <el-upload
                class="cover-uploader"
                :action="uploadUrl + '/image'"
                :headers="uploadHeaders"
                :show-file-list="false"
                :on-success="handleCoverSuccess"
                :on-error="handleUploadError"
                :before-upload="beforeCoverUpload"
                accept="image/jpeg,image/jpg,image/png,image/gif,image/webp"
              >
                <img v-if="uploadForm.coverImage" :src="uploadForm.coverImage" class="cover-image" />
                <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
              </el-upload>
              <div class="form-tip">
                提示：支持 JPG、PNG、GIF、WEBP 格式，建议尺寸 800x600 像素，大小不超过 10MB
              </div>
            </el-form-item>

            <el-form-item label="作品文件" prop="files" required>
              <el-upload
                class="work-file-uploader"
                :http-request="customUploadRequest"
                :headers="uploadHeaders"
                :on-error="handleUploadError"
                :before-upload="beforeWorkFileUpload"
                :file-list="workFileList"
                :on-remove="handleWorkFileRemove"
                accept="image/*,video/*,.pdf,.psd,.ai,.sketch"
                multiple
                :limit="5"
              >
                <el-button type="primary" :icon="Upload">
                  点击上传作品文件
                </el-button>
              </el-upload>
              <div class="form-tip">
                提示：支持图片（最大10MB）、视频（最大500MB）、PDF、PSD、AI、Sketch 等格式，最多上传 5 个文件
              </div>
            </el-form-item>
          </div>

          <!-- 版权信息 -->
          <div class="form-section">
            <h3 class="section-title">版权信息</h3>
            
            <el-form-item label="版权声明" prop="copyrightStatement">
              <el-input
                v-model="uploadForm.copyrightStatement"
                type="textarea"
                :rows="3"
                placeholder="请输入版权声明，例如：本作品版权归作者所有，仅供学习交流使用"
                maxlength="500"
                show-word-limit
              />
            </el-form-item>

            <el-form-item label="作品标签" prop="tags">
              <el-input
                v-model="uploadForm.tags"
                placeholder="请输入标签，多个标签用逗号分隔，例如：西柏坡,红色文化,海报设计"
                clearable
              >
                <template #prepend>
                  <el-icon><PriceTag /></el-icon>
                </template>
              </el-input>
              <div class="form-tip">
                提示：标签有助于其他用户发现您的作品
              </div>
            </el-form-item>
          </div>

          <!-- 提交按钮 -->
          <div class="form-actions">
            <el-button size="large" @click="goBack">
              取消
            </el-button>
            <el-button
              type="primary"
              size="large"
              @click="submitWork"
              :loading="submitting"
            >
              {{ submitting ? '提交中...' : '提交作品' }}
            </el-button>
          </div>
        </el-form>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { ArrowLeft, Picture, Document, PriceTag, Plus, Upload } from '@element-plus/icons-vue'
import MainLayout from '@/layouts/MainLayout.vue'
import { creativeApi } from '@/api/creative'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const uploadFormRef = ref<FormInstance>()
const submitting = ref(false)

// 文件上传配置
const uploadUrl = 'http://localhost:8083/api/upload'
const uploadHeaders = {
  'X-User-Id': userStore.userId?.toString() || ''
}

// 作品文件列表
const workFileList = ref<any[]>([])

// 上传表单数据
const uploadForm = reactive({
  title: '',
  categoryType: undefined as number | undefined,  // 改为 undefined
  description: '',
  designConcept: undefined as string | undefined,  // 改为 undefined
  coverImage: undefined as string | undefined,  // 改为 undefined
  files: [] as string[],
  copyrightStatement: undefined as string | undefined,  // 改为 undefined
  tags: undefined as string | undefined  // 改为 undefined
})

// 表单验证规则
const formRules: FormRules = {
  title: [
    { required: true, message: '请输入作品标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  categoryType: [
    { required: true, message: '请选择作品分类', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入作品描述', trigger: 'blur' },
    { min: 10, max: 1000, message: '描述长度在 10 到 1000 个字符', trigger: 'blur' }
  ],
  coverImage: [
    { required: true, message: '请上传封面图片', trigger: 'change' }
  ],
  files: [
    { 
      required: true, 
      validator: (rule: any, value: any, callback: any) => {
        if (!value || value.length === 0) {
          callback(new Error('请上传至少一个作品文件'))
        } else {
          callback()
        }
      },
      trigger: 'change' 
    }
  ]
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 封面图片上传前验证
const beforeCoverUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB!')
    return false
  }
  return true
}

// 封面图片上传成功
const handleCoverSuccess = (response: any) => {
  console.log('=== 封面上传成功回调 ===')
  console.log('响应数据:', response)
  console.log('响应类型:', typeof response)
  
  // 检查响应是否存在
  if (!response) {
    console.error('响应为空')
    ElMessage.error('上传失败：服务器无响应')
    return
  }
  
  // 处理不同的响应格式
  let result = response
  
  // 如果响应是字符串，尝试解析
  if (typeof response === 'string') {
    try {
      result = JSON.parse(response)
    } catch (e) {
      console.error('JSON解析失败:', e)
      ElMessage.error('上传失败：响应格式错误')
      return
    }
  }
  
  console.log('处理后的结果:', result)
  
  // 检查响应格式
  if (result.code === 200 && result.data) {
    const imageUrl = result.data.url || result.data
    console.log('封面URL:', imageUrl)
    uploadForm.coverImage = imageUrl
    // 触发表单验证
    uploadFormRef.value?.validateField('coverImage')
    ElMessage.success('封面上传成功')
  } else if (result.url) {
    // 直接返回URL的情况
    console.log('直接URL格式:', result.url)
    uploadForm.coverImage = result.url
    uploadFormRef.value?.validateField('coverImage')
    ElMessage.success('封面上传成功')
  } else {
    console.error('响应格式不正确:', result)
    ElMessage.error(result.message || '封面上传失败')
  }
}

// 作品文件上传前验证
const beforeWorkFileUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isVideo = file.type.startsWith('video/')
  const isPDF = file.type === 'application/pdf'
  const isDesignFile = ['.psd', '.ai', '.sketch'].some(ext => file.name.toLowerCase().endsWith(ext))
  
  // 根据文件类型设置不同的大小限制
  let maxSize = 100 // 默认 100MB
  let sizeLimit = ''
  
  if (isVideo) {
    maxSize = 500 // 视频最大 500MB
    sizeLimit = '500MB'
  } else if (isImage) {
    maxSize = 10 // 图片最大 10MB
    sizeLimit = '10MB'
  } else if (isPDF || isDesignFile) {
    maxSize = 100 // 设计文件最大 100MB
    sizeLimit = '100MB'
  }
  
  const isLtMaxSize = file.size / 1024 / 1024 < maxSize

  if (!isLtMaxSize) {
    ElMessage.error(`文件大小不能超过 ${sizeLimit}!`)
    return false
  }
  
  return true
}

// 根据文件类型获取上传端点
const getUploadAction = (file: File) => {
  if (file.type.startsWith('video/')) {
    return uploadUrl + '/video'
  } else {
    return uploadUrl + '/image'
  }
}

// 自定义上传请求
const customUploadRequest = async (options: any) => {
  const { file, onError } = options
  
  try {
    const formData = new FormData()
    formData.append('file', file)
    
    // 根据文件类型选择上传端点
    const action = getUploadAction(file)
    
    console.log('=== 开始上传文件 ===')
    console.log('文件名:', file.name)
    console.log('文件类型:', file.type)
    console.log('文件大小:', file.size)
    console.log('上传端点:', action)
    console.log('请求头:', uploadHeaders)
    
    const response = await fetch(action, {
      method: 'POST',
      headers: uploadHeaders,
      body: formData
    })
    
    console.log('=== 收到响应 ===')
    console.log('响应状态:', response.status)
    console.log('响应OK:', response.ok)
    
    if (!response.ok) {
      throw new Error(`HTTP错误: ${response.status}`)
    }
    
    const contentType = response.headers.get('content-type')
    console.log('响应类型:', contentType)
    
    let result
    if (contentType && contentType.includes('application/json')) {
      result = await response.json()
      console.log('JSON响应:', result)
    } else {
      const text = await response.text()
      console.log('文本响应:', text)
      try {
        result = JSON.parse(text)
      } catch (e) {
        console.error('JSON解析失败，使用文本作为URL')
        result = { url: text }
      }
    }
    
    console.log('最终结果:', result)
    
    // 确保result不为空
    if (!result) {
      throw new Error('服务器返回空响应')
    }
    
    // 直接处理成功逻辑，不调用onSuccess
    let fileUrl = ''
    if (result.code === 200 && result.data) {
      fileUrl = result.data.url || result.data
    } else if (result.url) {
      fileUrl = result.url
    } else {
      throw new Error(result.message || '上传失败：响应格式错误')
    }
    
    console.log('文件URL:', fileUrl)
    
    // 添加到文件列表
    uploadForm.files.push(fileUrl)
    workFileList.value.push({
      name: file.name,
      url: fileUrl,
      uid: file.uid
    })
    
    // 触发表单验证
    uploadFormRef.value?.validateField('files')
    ElMessage.success('文件上传成功')
    
  } catch (error: any) {
    console.error('=== 上传错误 ===')
    console.error('错误信息:', error)
    console.error('错误堆栈:', error.stack)
    onError(error)
    ElMessage.error(error.message || '上传失败，请稍后重试')
  }
}

// 作品文件上传成功（已废弃 - 现在在 customUploadRequest 中直接处理）
// const handleWorkFileSuccess = (response: any, file: any) => {
//   console.log('=== 文件上传成功回调 ===')
//   console.log('响应数据:', response)
//   console.log('文件信息:', file)
//   console.log('响应类型:', typeof response)
//   
//   // 检查响应是否存在
//   if (!response) {
//     console.error('响应为空')
//     ElMessage.error('上传失败：服务器无响应')
//     return
//   }
//   
//   // 处理不同的响应格式
//   let result = response
//   
//   // 如果响应是字符串，尝试解析
//   if (typeof response === 'string') {
//     try {
//       result = JSON.parse(response)
//     } catch (e) {
//       console.error('JSON解析失败:', e)
//       ElMessage.error('上传失败：响应格式错误')
//       return
//     }
//   }
//   
//   // 检查是否是对象
//   if (typeof result !== 'object') {
//     console.error('响应不是对象:', result)
//     ElMessage.error('上传失败：响应格式错误')
//     return
//   }
//   
//   console.log('处理后的结果:', result)
//   
//   // 检查响应格式
//   if (result.code === 200 && result.data) {
//     const fileUrl = result.data.url || result.data
//     console.log('文件URL:', fileUrl)
//     
//     uploadForm.files.push(fileUrl)
//     // 添加到文件列表显示
//     workFileList.value.push({
//       name: file.name,
//       url: fileUrl,
//       uid: file.uid
//     })
//     // 触发表单验证
//     uploadFormRef.value?.validateField('files')
//     ElMessage.success('文件上传成功')
//   } else if (result.url) {
//     // 直接返回URL的情况
//     console.log('直接URL格式:', result.url)
//     uploadForm.files.push(result.url)
//     workFileList.value.push({
//       name: file.name,
//       url: result.url,
//       uid: file.uid
//     })
//     uploadFormRef.value?.validateField('files')
//     ElMessage.success('文件上传成功')
//   } else {
//     console.error('响应格式不正确:', result)
//     ElMessage.error(result.message || '文件上传失败')
//   }
// }

// 移除作品文件
const handleWorkFileRemove = (file: any) => {
  const index = workFileList.value.findIndex(f => f.uid === file.uid)
  if (index > -1) {
    uploadForm.files.splice(index, 1)
    workFileList.value.splice(index, 1)
    // 触发表单验证
    uploadFormRef.value?.validateField('files')
  }
}

// 上传错误处理
const handleUploadError = (error: any) => {
  console.error('上传失败:', error)
  ElMessage.error('上传失败，请稍后重试')
}

// 提交作品
const submitWork = async () => {
  if (!uploadFormRef.value) return

  try {
    // 表单验证
    await uploadFormRef.value.validate()
  } catch (error: any) {
    // 表单验证失败
    console.log('表单验证失败:', error)
    ElMessage.warning('请检查表单填写是否完整')
    return
  }

  // 验证必填项
  if (!uploadForm.coverImage) {
    ElMessage.warning('请上传封面图片')
    return
  }
  if (uploadForm.files.length === 0) {
    ElMessage.warning('请上传至少一个作品文件')
    return
  }

  submitting.value = true

  try {
    // 构建请求数据，只包含必填字段
    const designData: any = {
      title: uploadForm.title,
      description: uploadForm.description,
      files: uploadForm.files // 直接发送数组
    }

    // 只添加非空的可选字段
    if (uploadForm.categoryType !== null && uploadForm.categoryType !== undefined && uploadForm.categoryType !== '') {
      designData.categoryType = uploadForm.categoryType
    }
    if (uploadForm.designConcept && uploadForm.designConcept.trim()) {
      designData.designConcept = uploadForm.designConcept
    }
    if (uploadForm.coverImage && uploadForm.coverImage.trim()) {
      designData.coverImage = uploadForm.coverImage
    }
    if (uploadForm.copyrightStatement && uploadForm.copyrightStatement.trim()) {
      designData.copyrightStatement = uploadForm.copyrightStatement
    }
    if (uploadForm.tags && uploadForm.tags.trim()) {
      designData.tags = uploadForm.tags
    }

    console.log('提交数据:', designData)
    console.log('可选字段状态:', {
      categoryType: uploadForm.categoryType,
      designConcept: uploadForm.designConcept,
      coverImage: uploadForm.coverImage,
      copyrightStatement: uploadForm.copyrightStatement,
      tags: uploadForm.tags
    })

    await creativeApi.createDesign(designData)
    
    ElMessage.success('作品上传成功！')
    
    // 延迟跳转，让用户看到成功提示
    setTimeout(() => {
      router.push('/creative')
    }, 1500)
  } catch (error: any) {
    console.error('提交失败 - 完整错误:', error)
    console.error('错误响应:', error.response)
    console.error('错误数据:', error.response?.data)
    console.error('状态码:', error.response?.status)
    console.error('错误消息:', error.response?.data?.message)
    
    // 显示详细错误信息
    const errorMsg = error.response?.data?.message || error.message || '提交失败，请稍后重试'
    ElMessage.error(errorMsg)
    
    // 如果有详细错误数据，也打印出来
    if (error.response?.data?.data) {
      console.error('详细错误:', error.response.data.data)
    }
  } finally {
    submitting.value = false
  }
}

// 检查登录状态
if (!userStore.isLoggedIn) {
  ElMessage.warning('请先登录')
  router.push('/login')
}
</script>

<style scoped>
.upload-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 60px;
}

/* 页面头部 */
.page-header {
  background: linear-gradient(135deg, #8b1538 0%, #a0182f 25%, #c41e3a 75%, #d4243f 100%);
  color: white;
  padding: 3rem 2rem;
  box-shadow: 0 4px 12px rgba(160, 24, 47, 0.2);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
}

.back-btn {
  margin-bottom: 1rem;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
}

.page-subtitle {
  font-size: 1.1rem;
  opacity: 0.9;
}

/* 上传容器 */
.upload-container {
  max-width: 900px;
  margin: -2rem auto 0;
  padding: 0 2rem;
}

.upload-form {
  background: white;
  border-radius: 16px;
  padding: 3rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

/* 表单分区 */
.form-section {
  margin-bottom: 3rem;
  padding-bottom: 2rem;
  border-bottom: 2px solid #f0f0f0;
}

.form-section:last-of-type {
  border-bottom: none;
  margin-bottom: 2rem;
}

.section-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 1.5rem;
  padding-left: 1rem;
  border-left: 4px solid #c41e3a;
}

/* 表单提示 */
.form-tip {
  margin-top: 0.5rem;
  font-size: 0.875rem;
  color: #909399;
  line-height: 1.5;
}

/* 图片预览 */
.image-preview {
  margin-top: 1rem;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid #e4e7ed;
}

.image-preview img {
  width: 100%;
  max-width: 400px;
  height: auto;
  display: block;
}

/* 封面上传器 */
.cover-uploader {
  width: 100%;
}

:deep(.cover-uploader .el-upload) {
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
  width: 400px;
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

:deep(.cover-uploader .el-upload:hover) {
  border-color: #c41e3a;
}

.cover-uploader-icon {
  font-size: 48px;
  color: #8c939d;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

/* 作品文件上传器 */
.work-file-uploader {
  width: 100%;
}

:deep(.work-file-uploader .el-upload-list) {
  margin-top: 1rem;
}

:deep(.work-file-uploader .el-upload-list__item) {
  border-radius: 8px;
  transition: all 0.3s;
}

:deep(.work-file-uploader .el-upload-list__item:hover) {
  background-color: #f5f7fa;
}

/* 表单操作按钮 */
.form-actions {
  display: flex;
  justify-content: center;
  gap: 1rem;
  padding-top: 2rem;
  border-top: 2px solid #f0f0f0;
}

.form-actions .el-button {
  min-width: 150px;
  padding: 12px 32px;
  font-size: 1rem;
}

/* Element Plus 样式覆盖 */
:deep(.el-form-item__label) {
  font-weight: 600;
  color: #333;
}

:deep(.el-input__inner),
:deep(.el-textarea__inner) {
  border-radius: 8px;
}

:deep(.el-select) {
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    padding: 2rem 1rem;
  }

  .page-title {
    font-size: 1.8rem;
  }

  .page-subtitle {
    font-size: 1rem;
  }

  .upload-container {
    padding: 0 1rem;
  }

  .upload-form {
    padding: 2rem 1.5rem;
  }

  .section-title {
    font-size: 1.1rem;
  }

  .form-actions {
    flex-direction: column;
  }

  .form-actions .el-button {
    width: 100%;
  }
}
</style>
