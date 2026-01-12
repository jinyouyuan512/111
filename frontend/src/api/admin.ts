/**
 * 管理后台 API
 */
import request from './request'

// ==================== 用户管理 ====================

export function getUsers(params: { page?: number; size?: number; keyword?: string; role?: string }) {
  return request.get('/admin/users', { params })
}

export function getUserDetail(userId: number) {
  return request.get(`/admin/users/${userId}`)
}

export function updateUser(userId: number, data: any) {
  return request.put(`/admin/users/${userId}`, data)
}

export function deleteUser(userId: number) {
  return request.delete(`/admin/users/${userId}`)
}

export function toggleUserStatus(userId: number) {
  return request.post(`/admin/users/${userId}/toggle-status`)
}

export function changeUserRole(userId: number, role: string) {
  return request.post(`/admin/users/${userId}/role`, { role })
}

export function getUserStatistics() {
  return request.get('/admin/users/statistics')
}

// ==================== 内容管理 ====================

export function getPosts(params: { page?: number; size?: number; keyword?: string; status?: string }) {
  return request.get('/admin/content/posts', { params })
}

export function getPostDetail(postId: number) {
  return request.get(`/admin/content/posts/${postId}`)
}

export function hidePost(postId: number) {
  return request.post(`/admin/content/posts/${postId}/hide`)
}

export function showPost(postId: number) {
  return request.post(`/admin/content/posts/${postId}/show`)
}

export function deletePost(postId: number) {
  return request.delete(`/admin/content/posts/${postId}`)
}

export function getComments(params: { page?: number; size?: number; keyword?: string }) {
  return request.get('/admin/content/comments', { params })
}

export function deleteComment(commentId: number) {
  return request.delete(`/admin/content/comments/${commentId}`)
}

export function getReports(params: { page?: number; size?: number; status?: string }) {
  return request.get('/admin/content/reports', { params })
}

export function resolveReport(reportId: number) {
  return request.post(`/admin/content/reports/${reportId}/resolve`)
}

export function rejectReport(reportId: number) {
  return request.post(`/admin/content/reports/${reportId}/reject`)
}

export function getPendingReportCount() {
  return request.get('/admin/content/reports/pending-count')
}

export function getContentStatistics() {
  return request.get('/admin/content/statistics')
}

// ==================== 订单管理 ====================

export function getOrders(params: { page?: number; size?: number; keyword?: string; status?: string; startDate?: string; endDate?: string }) {
  return request.get('/admin/orders', { params })
}

export function getOrderDetail(orderId: number) {
  return request.get(`/admin/orders/${orderId}`)
}

export function shipOrder(orderId: number, data: { expressCompany: string; trackingNo: string }) {
  return request.post(`/admin/orders/${orderId}/ship`, data)
}

export function cancelOrder(orderId: number) {
  return request.post(`/admin/orders/${orderId}/cancel`)
}

export function refundOrder(orderId: number) {
  return request.post(`/admin/orders/${orderId}/refund`)
}

export function getOrderStatistics() {
  return request.get('/admin/orders/statistics')
}

// ==================== 商品管理 ====================

export function getProducts(params: { page?: number; size?: number; keyword?: string; category?: string; status?: string }) {
  return request.get('/admin/products', { params })
}

export function getProductDetail(productId: number) {
  return request.get(`/admin/products/${productId}`)
}

export function addProduct(data: any) {
  return request.post('/admin/products', data)
}

export function updateProduct(productId: number, data: any) {
  return request.put(`/admin/products/${productId}`, data)
}

export function deleteProduct(productId: number) {
  return request.delete(`/admin/products/${productId}`)
}

export function putProductOnSale(productId: number) {
  return request.post(`/admin/products/${productId}/on`)
}

export function putProductOffSale(productId: number) {
  return request.post(`/admin/products/${productId}/off`)
}

export function updateProductStock(productId: number, stock: number) {
  return request.post(`/admin/products/${productId}/stock`, { stock })
}

export function getProductStatistics() {
  return request.get('/admin/products/statistics')
}

// ==================== 仪表盘统计 ====================

export async function getDashboardStats() {
  try {
    const [userStats, contentStats, orderStats] = await Promise.all([
      getUserStatistics().catch(() => ({ data: {} })),
      getContentStatistics().catch(() => ({ data: {} })),
      getOrderStatistics().catch(() => ({ data: {} }))
    ])
    
    return {
      totalUsers: userStats.data?.totalUsers || 0,
      newUsersToday: userStats.data?.newUsersToday || 0,
      totalPosts: contentStats.data?.totalPosts || 0,
      newPostsToday: contentStats.data?.newPostsToday || 0,
      totalOrders: orderStats.data?.totalOrders || 0,
      todayRevenue: orderStats.data?.todayRevenue || 0,
      pendingReports: contentStats.data?.pendingReports || 0
    }
  } catch {
    return {
      totalUsers: 0,
      newUsersToday: 0,
      totalPosts: 0,
      newPostsToday: 0,
      totalOrders: 0,
      todayRevenue: 0,
      pendingReports: 0
    }
  }
}

export default {
  // 用户管理
  getUsers,
  getUserDetail,
  updateUser,
  deleteUser,
  toggleUserStatus,
  changeUserRole,
  getUserStatistics,
  // 内容管理
  getPosts,
  getPostDetail,
  hidePost,
  showPost,
  deletePost,
  getComments,
  deleteComment,
  getReports,
  resolveReport,
  rejectReport,
  getPendingReportCount,
  getContentStatistics,
  // 订单管理
  getOrders,
  getOrderDetail,
  shipOrder,
  cancelOrder,
  refundOrder,
  getOrderStatistics,
  // 商品管理
  getProducts,
  getProductDetail,
  addProduct,
  updateProduct,
  deleteProduct,
  putProductOnSale,
  putProductOffSale,
  updateProductStock,
  getProductStatistics,
  // 仪表盘
  getDashboardStats
}
