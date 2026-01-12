import request from './request'

/**
 * 商城API接口
 */

// 类型定义
export interface Product {
  id: number
  name: string
  description: string
  price: number
  stock: number
  category: string
  image: string
  images?: string[]
  createdAt: string
  updatedAt: string
}

export interface OrderItem {
  id: number
  orderId: number
  productId: number
  productName: string
  productDescription?: string
  productImage?: string
  price: number
  quantity: number
  subtotal: number
}

export interface Order {
  id: number
  orderNumber: string
  userId: number
  status: string
  totalAmount: number
  paymentMethod?: string
  shippingAddress?: string
  items?: OrderItem[]
  createdAt?: string
  createTime?: string
  updatedAt?: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export interface CartItem {
  id: number
  userId: number
  productId: number
  quantity: number
  selected: boolean
  product?: Product
  createdAt: string
}

export interface Address {
  id: number
  userId: number
  receiverName: string
  receiverPhone: string
  province: string
  city: string
  district: string
  detailAddress: string
  isDefault: boolean
  createdAt: string
}

// 商品相关接口
export const mallApi = {
  /**
   * 获取商品列表
   */
  getProductList(params: {
    page?: number
    size?: number
    keyword?: string
    category?: string
    sort?: string
    onlyStock?: boolean
    minPrice?: number
    maxPrice?: number
  }) {
    return request.get('/mall/products', { params })
  },

  /**
   * 获取商品详情
   */
  getProductById(id: number) {
    return request.get(`/mall/products/${id}`)
  },

  /**
   * 创建商品
   */
  createProduct(data: any) {
    return request.post('/mall/products', data)
  },

  /**
   * 更新商品
   */
  updateProduct(id: number, data: any) {
    return request.put(`/mall/products/${id}`, data)
  },

  /**
   * 删除商品
   */
  deleteProduct(id: number) {
    return request.delete(`/mall/products/${id}`)
  },

  /**
   * 获取订单列表
   */
  getOrderList(params: {
    page?: number
    size?: number
    userId?: number
    status?: string
  }) {
    return request.get('/mall/orders', { params })
  },

  /**
   * 获取订单详情
   */
  getOrderById(id: number) {
    return request.get(`/mall/orders/${id}`)
  },

  /**
   * 创建订单
   */
  createOrder(data: {
    userId: number
    items: Array<{
      productId: number
      quantity: number
    }>
    shippingAddress: string
    paymentMethod: string
  }) {
    return request.post('/mall/orders', data)
  },

  /**
   * 更新订单状态
   */
  updateOrderStatus(id: number, status: string) {
    return request.put(`/mall/orders/${id}/status`, null, {
      params: { status }
    })
  },

  /**
   * 取消订单
   */
  cancelOrder(id: number) {
    return request.delete(`/mall/orders/${id}`)
  },

  /**
   * 支付订单
   */
  payOrder(id: number, paymentMethod: string) {
    return request.post(`/mall/orders/${id}/pay`, null, {
      params: { paymentMethod }
    })
  },

  /**
   * 确认收货
   */
  confirmOrder(id: number) {
    return request.post(`/mall/orders/${id}/confirm`)
  },

  // 购物车相关接口
  /**
   * 添加到购物车
   */
  addToCart(data: { productId: number; quantity: number }) {
    return request.post('/mall/cart', data)
  },

  /**
   * 获取购物车列表
   */
  getCartList() {
    return request.get('/mall/cart')
  },

  /**
   * 更新购物车项
   */
  updateCartItem(id: number, data: { quantity?: number; selected?: boolean }) {
    return request.put(`/mall/cart/${id}`, data)
  },

  /**
   * 删除购物车项
   */
  deleteCartItem(id: number) {
    return request.delete(`/mall/cart/${id}`)
  },

  /**
   * 批量删除购物车项
   */
  batchDeleteCartItems(ids: number[]) {
    return request.delete('/mall/cart/batch', { data: ids })
  },

  /**
   * 全选/取消全选
   */
  selectAll(selected: boolean) {
    return request.put('/mall/cart/select-all', null, { params: { selected } })
  },

  /**
   * 清空购物车
   */
  clearCart() {
    return request.delete('/mall/cart/clear')
  },

  // 地址相关接口
  /**
   * 添加收货地址
   */
  addAddress(data: any) {
    return request.post('/mall/addresses', data)
  },

  /**
   * 获取地址列表
   */
  getAddressList() {
    return request.get('/mall/addresses')
  },

  /**
   * 获取地址详情
   */
  getAddressById(id: number) {
    return request.get(`/mall/addresses/${id}`)
  },

  /**
   * 更新收货地址
   */
  updateAddress(id: number, data: any) {
    return request.put(`/mall/addresses/${id}`, data)
  },

  /**
   * 删除收货地址
   */
  deleteAddress(id: number) {
    return request.delete(`/mall/addresses/${id}`)
  },

  /**
   * 设置默认地址
   */
  setDefaultAddress(id: number) {
    return request.put(`/mall/addresses/${id}/default`)
  },

  /**
   * 获取默认地址
   */
  getDefaultAddress() {
    return request.get('/mall/addresses/default')
  }
}

export default mallApi
