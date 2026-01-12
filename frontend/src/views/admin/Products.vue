<template>
  <div class="products-management">
    <div class="page-header">
      <h2>🎁 商品管理</h2>
      <p>管理商城商品信息</p>
    </div>

    <div class="toolbar">
      <el-input v-model="searchKeyword" placeholder="搜索商品名称..." prefix-icon="Search" style="width: 250px" @keyup.enter="loadProducts" clearable />
      <el-select v-model="categoryFilter" placeholder="商品分类" style="width: 140px" @change="loadProducts">
        <el-option label="全部分类" value="" />
        <el-option label="红色文创" value="cultural" />
        <el-option label="纪念品" value="souvenir" />
        <el-option label="书籍" value="book" />
        <el-option label="服饰" value="clothing" />
        <el-option label="食品特产" value="food" />
      </el-select>
      <el-select v-model="statusFilter" placeholder="商品状态" style="width: 120px" @change="loadProducts">
        <el-option label="全部状态" value="" />
        <el-option label="上架中" value="on" />
        <el-option label="已下架" value="off" />
      </el-select>
      <el-button type="primary" @click="loadProducts">🔄 刷新</el-button>
      <el-button type="success" @click="openAddDialog">➕ 添加商品</el-button>
    </div>

    <el-table :data="products" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="商品" min-width="280">
        <template #default="{ row }">
          <div class="product-cell">
            <img :src="row.image || '/placeholder.png'" class="product-img" />
            <div class="product-info">
              <span class="product-name">{{ row.name }}</span>
              <span class="product-category">{{ getCategoryName(row.category) }}</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="价格" width="150">
        <template #default="{ row }">
          <div class="price-cell">
            <span class="current-price">¥{{ row.price?.toFixed(2) }}</span>
            <span v-if="row.originalPrice" class="original-price">¥{{ row.originalPrice?.toFixed(2) }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="100">
        <template #default="{ row }">
          <span :class="{ 'low-stock': row.stock < 10 }">{{ row.stock }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="sales" label="销量" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-switch v-model="row.status" active-value="on" inactive-value="off" @change="toggleStatus(row)" />
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="editProduct(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteProduct(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination background layout="prev, pager, next, total" :total="total" :page-size="pageSize" :current-page="currentPage" @current-change="handlePageChange" />
    </div>

    <!-- 添加/编辑商品弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑商品' : '添加商品'" width="600px">
      <el-form :model="productForm" label-width="100px">
        <el-form-item label="商品名称" required>
          <el-input v-model="productForm.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品分类" required>
          <el-select v-model="productForm.category" placeholder="选择分类" style="width: 100%">
            <el-option label="红色文创" value="cultural" />
            <el-option label="纪念品" value="souvenir" />
            <el-option label="书籍" value="book" />
            <el-option label="服饰" value="clothing" />
            <el-option label="食品特产" value="food" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品图片">
          <el-input v-model="productForm.image" placeholder="图片URL" />
        </el-form-item>
        <el-form-item label="商品价格" required>
          <el-input-number v-model="productForm.price" :min="0" :precision="2" style="width: 150px" />
        </el-form-item>
        <el-form-item label="原价">
          <el-input-number v-model="productForm.originalPrice" :min="0" :precision="2" style="width: 150px" />
        </el-form-item>
        <el-form-item label="库存" required>
          <el-input-number v-model="productForm.stock" :min="0" style="width: 150px" />
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="productForm.description" type="textarea" :rows="3" placeholder="请输入商品描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveProduct">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as adminApi from '@/api/admin'

const loading = ref(false)
const products = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const searchKeyword = ref('')
const categoryFilter = ref('')
const statusFilter = ref('')

const dialogVisible = ref(false)
const isEdit = ref(false)
const productForm = ref({ id: null, name: '', category: '', image: '', price: 0, originalPrice: 0, stock: 0, description: '' })

const getCategoryName = (category: string) => {
  const map: Record<string, string> = { cultural: '红色文创', souvenir: '纪念品', book: '书籍', clothing: '服饰', food: '食品特产' }
  return map[category] || category
}

const loadProducts = async () => {
  loading.value = true
  try {
    const res = await adminApi.getProducts({ page: currentPage.value, size: pageSize.value, keyword: searchKeyword.value || undefined, category: categoryFilter.value || undefined, status: statusFilter.value || undefined })
    products.value = res.data?.records || res.data || []
    total.value = res.data?.total || products.value.length
  } catch {
    // 模拟数据
    products.value = [
      { id: 1, name: '西柏坡纪念徽章', category: 'souvenir', image: '', price: 99, originalPrice: 129, stock: 156, sales: 328, status: 'on', createdAt: '2026-01-01' },
      { id: 2, name: '红色文化T恤', category: 'clothing', image: '', price: 89, originalPrice: null, stock: 89, sales: 156, status: 'on', createdAt: '2026-01-02' },
      { id: 3, name: '革命历史书籍套装', category: 'book', image: '', price: 158, originalPrice: 198, stock: 45, sales: 89, status: 'on', createdAt: '2026-01-03' }
    ]
    total.value = 3
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page: number) => { currentPage.value = page; loadProducts() }

const toggleStatus = async (product: any) => {
  try {
    if (product.status === 'on') {
      await adminApi.putProductOnSale(product.id)
    } else {
      await adminApi.putProductOffSale(product.id)
    }
    ElMessage.success(`商品已${product.status === 'on' ? '上架' : '下架'}`)
  } catch { ElMessage.error('操作失败') }
}

const openAddDialog = () => {
  isEdit.value = false
  productForm.value = { id: null, name: '', category: '', image: '', price: 0, originalPrice: 0, stock: 0, description: '' }
  dialogVisible.value = true
}

const editProduct = (product: any) => {
  isEdit.value = true
  productForm.value = { ...product }
  dialogVisible.value = true
}

const saveProduct = async () => {
  if (!productForm.value.name || !productForm.value.category) {
    ElMessage.warning('请填写必填项')
    return
  }
  try {
    if (isEdit.value) {
      await adminApi.updateProduct(productForm.value.id!, productForm.value)
      const idx = products.value.findIndex(p => p.id === productForm.value.id)
      if (idx >= 0) products.value[idx] = { ...productForm.value }
    } else {
      const res = await adminApi.addProduct(productForm.value)
      products.value.unshift({ ...productForm.value, id: res.data, sales: 0, status: 'on', createdAt: new Date().toISOString().split('T')[0] })
    }
    dialogVisible.value = false
    ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
  } catch { ElMessage.error('保存失败') }
}

const deleteProduct = async (product: any) => {
  await ElMessageBox.confirm(`确定要删除商品"${product.name}"吗？`, '确认删除', { type: 'warning' })
  try {
    await adminApi.deleteProduct(product.id)
    products.value = products.value.filter(p => p.id !== product.id)
    ElMessage.success('删除成功')
  } catch { ElMessage.error('删除失败') }
}

onMounted(() => { loadProducts() })
</script>

<style scoped>
.products-management { background: white; padding: 24px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }
.page-header { margin-bottom: 20px; }
.page-header h2 { font-size: 1.5rem; margin-bottom: 8px; }
.page-header p { color: #666; }
.toolbar { display: flex; gap: 12px; margin-bottom: 20px; flex-wrap: wrap; }
.pagination { display: flex; justify-content: center; margin-top: 20px; }

.product-cell { display: flex; align-items: center; gap: 12px; }
.product-img { width: 60px; height: 60px; object-fit: cover; border-radius: 8px; background: #f5f5f5; }
.product-info { display: flex; flex-direction: column; }
.product-name { font-size: 14px; font-weight: 500; color: #333; }
.product-category { font-size: 12px; color: #999; margin-top: 4px; }

.price-cell { display: flex; flex-direction: column; }
.current-price { font-size: 16px; font-weight: 600; color: #f56c6c; }
.original-price { font-size: 12px; color: #999; text-decoration: line-through; }

.low-stock { color: #f56c6c; font-weight: 600; }
</style>
