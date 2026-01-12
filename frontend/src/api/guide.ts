import request from './request'

export interface LocationRequest {
  latitude: number
  longitude: number
}

export interface ScenicAreaVO {
  id: number
  name: string
  description: string
  address: string
  latitude: number
  longitude: number
  mapData: string
  status: string
  pois: PoiVO[]
}

export interface PoiVO {
  id: number
  name: string
  description: string
  latitude: number
  longitude: number
  type: string
  triggerRadius: number
  qrCode: string
}

export interface AudioGuideVO {
  id: number
  poiId: number
  title: string
  content: string
  audioUrl: string
  duration: number
  language: string
}

export interface ArSceneVO {
  id: number
  poiId: number
  name: string
  description: string
  sceneDataUrl: string
  previewImage: string
}

export interface GuideRouteVO {
  id: number
  name: string
  description: string
  duration: number
  distance: number
  difficulty: string
  pathData: string
  pois?: PoiVO[]
}

export interface NavigationRequest {
  startLatitude: number
  startLongitude: number
  targetPoiId: number
}

export interface TrackPointRequest {
  visitId: number
  latitude: number
  longitude: number
}

export interface TravelLogVO {
  id: number
  userId: number
  visitId: number
  title: string
  content: string
  images: string[]
  trackMapUrl: string
  createdAt: string
}

/**
 * 检测位置并识别景区
 */
export function detectLocation(data: LocationRequest) {
  return request<ScenicAreaVO>({
    url: '/guide/detect-location',
    method: 'post',
    data
  })
}

/**
 * 获取景点信息
 */
export function getPoiInfo(poiId: number) {
  return request<PoiVO>({
    url: `/guide/poi/${poiId}`,
    method: 'get'
  })
}

/**
 * 触发语音讲解
 */
export function triggerAudioGuide(poiId: number, language: string = 'zh') {
  return request<AudioGuideVO>({
    url: `/guide/audio-guide/${poiId}`,
    method: 'get',
    params: { language }
  })
}

/**
 * 扫描二维码加载AR场景
 */
export function loadArScene(qrCode: string) {
  return request<ArSceneVO>({
    url: `/guide/ar-scene/${qrCode}`,
    method: 'get'
  })
}

/**
 * 获取导览路线
 */
export function getGuideRoutes(scenicAreaId: number) {
  return request<GuideRouteVO[]>({
    url: `/guide/routes/${scenicAreaId}`,
    method: 'get'
  })
}

/**
 * 规划导航路径
 */
export function planNavigation(data: NavigationRequest) {
  return request<string>({
    url: '/guide/navigation',
    method: 'post',
    data
  })
}

/**
 * 开始游览
 */
export function startVisit(userId: number, scenicAreaId: number) {
  return request<number>({
    url: '/guide/visit/start',
    method: 'post',
    params: { userId, scenicAreaId }
  })
}

/**
 * 记录轨迹点
 */
export function recordTrackPoint(data: TrackPointRequest) {
  return request<void>({
    url: '/guide/visit/track',
    method: 'post',
    data
  })
}

/**
 * 结束游览
 */
export function endVisit(visitId: number) {
  return request<void>({
    url: `/guide/visit/end/${visitId}`,
    method: 'post'
  })
}

/**
 * 生成游记
 */
export function generateTravelLog(userId: number, visitId: number) {
  return request<TravelLogVO>({
    url: '/guide/travel-log/generate',
    method: 'post',
    params: { userId, visitId }
  })
}

/**
 * 获取用户游记列表
 */
export function getUserTravelLogs(userId: number) {
  return request<TravelLogVO[]>({
    url: `/guide/travel-log/user/${userId}`,
    method: 'get'
  })
}
