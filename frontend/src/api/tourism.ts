/**
 * 智慧旅游 API - 调用后端 tourism-service
 */
import request from './request'

// ==================== 景点导览 ====================

export interface RedSpot {
  id: number
  name: string
  icon: string
  gradient: string
  slogan: string
  location: string
  rating: number
  isFree: boolean
  needReserve: boolean
  tags: string[]
  introduction: string
  history: string
  tips: string[]
  audioGuides: AudioGuide[]
  category: string
  ticketPrice: number
  openingHours: string
  images: string[]
}

export interface AudioGuide {
  id: number
  spotId: number
  title: string
  duration: number
  transcript: string
  audioUrl?: string
  orderNum: number
}

/**
 * 获取所有红色景�?
 */
export function getRedSpots() {
  return request.get<RedSpot[]>('/tourism/spots/red')
}

/**
 * 获取景点详情
 */
export function getSpotDetail(spotId: number) {
  return request.get<RedSpot>(`/api/tourism/spots/${spotId}`)
}

/**
 * 获取景点语音讲解
 */
export function getAudioGuides(spotId: number) {
  return request.get<AudioGuide[]>(`/api/tourism/spots/${spotId}/audio-guides`)
}

/**
 * 搜索景点
 */
export function searchSpots(params: { keyword?: string; category?: string; freeOnly?: boolean }) {
  return request.get<RedSpot[]>('/tourism/spots/search', { params })
}

// ==================== 智能路线 ====================

export interface HotRoute {
  id: string
  rank: number
  name: string
  spots: number[]
  duration: string
  views: number
  rating: number
}

export interface TripPlanRequest {
  spots: string[]
  startDate?: string
  duration?: number
  pace?: string
  interests?: string[]
  budget?: number
}

export interface TripPlan {
  id?: number
  title: string
  description: string
  days: DayPlan[]
  totalDistance: number
  estimatedCost: number
  tips: string[]
}

export interface DayPlan {
  day: number
  date: string
  spots: SpotPlan[]
  meals?: string[]
  accommodation?: string
}

export interface SpotPlan {
  order: number
  name: string
  duration: string
  tips: string
  arrivalTime?: string
  departureTime?: string
}

/**
 * 获取热门路线
 */
export function getHotRoutes(limit: number = 10) {
  return request.get<HotRoute[]>('/tourism/smart-route/hot', { params: { limit } })
}

/**
 * AI生成行程规划
 */
export function generateTripPlan(data: TripPlanRequest) {
  return request.post<TripPlan>('/tourism/smart-route/generate', data)
}

/**
 * 保存行程规划
 */
export function saveTripPlan(plan: TripPlan) {
  return request.post<number>('/tourism/smart-route/save', plan)
}

/**
 * 获取用户保存的行�?
 */
export function getMyPlans() {
  return request.get<TripPlan[]>('/tourism/smart-route/my-plans')
}

/**
 * 路线优化
 */
export function optimizeRoute(data: { spots: string[]; startPoint?: string; optimizeFor?: string }) {
  return request.post<TripPlan>('/tourism/smart-route/optimize', data)
}

// ==================== 实时信息 ====================

export interface SpotWeather {
  spotName: string
  date: string
  condition: string
  temperature: { min: number; max: number }
  humidity: number
  suggestion: string
  windDirection?: string
  windPower?: string
  aqi?: number
}

export interface CrowdInfo {
  spotId: number
  name: string
  icon: string
  percent: number
  level: string
  levelText: string
  waitTime: number
  bestTime: string
  currentVisitors?: number
  maxCapacity?: number
}

export interface TravelTip {
  icon: string
  title: string
  content: string
  type: string
}

export interface SpotStatus {
  spotId: number
  name: string
  isOpen: boolean
  openTime: string
  closeTime: string
  currentVisitors: number
  maxCapacity: number
  crowdLevel: string
  waitTime: number
  notices: string[]
  weather?: SpotWeather
}

export interface Dashboard {
  todayVisitors: number
  activeUsers: number
  totalBookings: number
  popularSpots: { name: string; visits: number }[]
  recentAlerts: { type: string; message: string; time: string }[]
  systemHealth: { weatherApi: string; trafficApi: string; aiService: string; database: string }
}

/**
 * 获取景点天气预报
 */
export function getSpotWeather(spotNames?: string[]) {
  return request.get<SpotWeather[]>('/tourism/realtime/weather', { 
    params: spotNames ? { spotNames: spotNames.join(',') } : {} 
  })
}

/**
 * 获取景点人流�?
 */
export function getCrowdInfo() {
  return request.get<CrowdInfo[]>('/tourism/realtime/crowd')
}

/**
 * 获取出行建议
 */
export function getTravelTips() {
  return request.get<TravelTip[]>('/tourism/realtime/tips')
}

/**
 * 获取景区实时状�?
 */
export function getSpotStatus(spotId: number) {
  return request.get<SpotStatus>(`/api/tourism/realtime/status/${spotId}`)
}

/**
 * 获取综合仪表盘数�?
 */
export function getDashboard() {
  return request.get<Dashboard>('/tourism/realtime/dashboard')
}

// ==================== 门票预订 ====================

export interface Ticket {
  id: number
  name: string
  icon: string
  gradient: string
  address: string
  openTime: string
  rating: number
  price: number
  originalPrice?: number
  discount?: string
  needReserve: boolean
  sold: number
  stock: number
  description?: string
}

export interface TicketBookRequest {
  ticketId: number
  visitDate: string
  quantity: number
  visitorName?: string
  visitorPhone?: string
  visitorIdCard?: string
}

export interface BookingResult {
  orderId: number
  orderNo: string
  ticketName: string
  visitDate: string
  quantity: number
  totalPrice: number
  status: string
  qrCode: string
  message: string
}

export interface TicketOrder {
  id: number
  orderNo: string
  ticketName: string
  spotName: string
  visitDate: string
  quantity: number
  totalPrice: number
  status: string
  qrCode: string
  createdAt: string
}

export interface TicketAvailability {
  ticketId: number
  date: string
  totalStock: number
  soldCount: number
  availableCount: number
  isAvailable: boolean
  message: string
}

/**
 * 获取门票列表
 */
export function getTickets() {
  return request.get<Ticket[]>('/tourism/tickets')
}

/**
 * 获取门票详情
 */
export function getTicketDetail(ticketId: number) {
  return request.get<Ticket>(`/api/tourism/tickets/${ticketId}`)
}

/**
 * 预订门票
 */
export function bookTicket(data: TicketBookRequest) {
  return request.post<BookingResult>('/tourism/tickets/book', data)
}

/**
 * 获取用户订单
 */
export function getMyOrders() {
  return request.get<TicketOrder[]>('/tourism/tickets/orders')
}

/**
 * 取消订单
 */
export function cancelOrder(orderId: number) {
  return request.post(`/api/tourism/tickets/orders/${orderId}/cancel`)
}

/**
 * 检查门票可用�?
 */
export function checkAvailability(ticketId: number, date: string) {
  return request.get<TicketAvailability>(`/api/tourism/tickets/${ticketId}/availability`, { params: { date } })
}

// ==================== AI助手 ====================

export interface AIChatRequest {
  question: string
  sessionId?: string
}

export interface AIChatResponse {
  answer: string
  type: string
  sessionId: string
  suggestions: string[]
  timestamp: string
}

/**
 * AI智能问答
 */
export function aiChat(data: AIChatRequest) {
  return request.post<AIChatResponse>('/tourism/ai/chat', data)
}

// ==================== 原有路线API ====================

export interface Route {
  id: number
  name: string
  description: string
  days: number
  estimatedCost: number
  difficulty: string
  season: string
  tags: string[]
  coverImage: string
  attractions: any[]
}

/**
 * 获取所有路�?
 */
export function getAllRoutes() {
  return request.get<Route[]>('/tourism/routes')
}

/**
 * 获取路线详情
 */
export function getRouteDetail(routeId: number) {
  return request.get<Route>(`/api/tourism/routes/${routeId}`)
}

/**
 * 推荐路线
 */
export function recommendRoutes(preferences: any) {
  return request.post<Route[]>('/tourism/routes/recommend', preferences)
}

export default {
  // 景点导览
  getRedSpots,
  getSpotDetail,
  getAudioGuides,
  searchSpots,
  // 智能路线
  getHotRoutes,
  generateTripPlan,
  saveTripPlan,
  getMyPlans,
  optimizeRoute,
  // 实时信息
  getSpotWeather,
  getCrowdInfo,
  getTravelTips,
  getSpotStatus,
  getDashboard,
  // 门票预订
  getTickets,
  getTicketDetail,
  bookTicket,
  getMyOrders,
  cancelOrder,
  checkAvailability,
  // AI助手
  aiChat,
  // 原有路线
  getAllRoutes,
  getRouteDetail,
  recommendRoutes
}
