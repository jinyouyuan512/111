/**
 * n8n API
 */
import axios from 'axios'

const N8N_BASE_URL = ''

const n8nClient = axios.create({
  baseURL: N8N_BASE_URL,
  timeout: 300000,
  headers: { 'Content-Type': 'application/json' }
})

async function triggerWebhook<T>(webhookPath: string, data: any): Promise<T> {
  console.log('[n8n] Calling webhook:', webhookPath, data)
  try {
    const response = await n8nClient.post('/webhook/' + webhookPath, data)
    console.log('[n8n] Response:', response.data)
    return response.data
  } catch (error: any) {
    console.error('[n8n] Error:', error.message)
    throw error
  }
}

export interface WeatherInfo {
  spotName: string
  date: string
  condition: string
  temperature: { min: number; max: number }
  humidity: number
  suggestion: string
}

export interface TripPlanRequest {
  spots: string[]
  startDate?: string
  duration?: number
  preferences?: {
    pace: 'relaxed' | 'moderate' | 'intensive'
    interests: string[]
    budget?: number
  }
  userId?: string
}

export interface TripPlanResponse {
  success: boolean
  plan: {
    title: string
    description: string
    days: {
      day: number
      date: string
      spots: { name: string; duration: string; tips: string; order: number }[]
      meals: string[]
      accommodation?: string
    }[]
    totalDistance: number
    estimatedCost: number
    tips: string[]
  }
  weather?: WeatherInfo[]
}

export interface AIChatRequest {
  question: string
  sessionId?: string
  userId?: string
  context?: Record<string, any>
}

export interface AIChatResponse {
  answer: string
  type: 'spot_info' | 'route' | 'food' | 'weather' | 'general'
  sessionId: string
  suggestions: string[]
  relatedSpots?: string[]
  timestamp: string
}

export interface AudioGuideRequest {
  spotId?: number
  spotName: string
  language?: 'zh' | 'en'
  style?: 'standard' | 'story' | 'kids'
}

export interface AudioGuideResponse {
  success: boolean
  spotName: string
  guide: {
    intro: string
    chapters: { id: number; title: string; content: string; duration: number; audioUrl?: string }[]
    totalDuration: number
    totalChapters: number
  }
}

export interface HotRoute {
  id: string
  rank: number
  name: string
  spots: string[]
  duration: string
  views: number
  rating: number
}

export async function generateAITripPlan(request: TripPlanRequest): Promise<TripPlanResponse> {
  return triggerWebhook<TripPlanResponse>('tourism/ai-plan', request)
}

export async function getSpotWeather(spotNames: string[], date?: string): Promise<WeatherInfo[]> {
  const response = await triggerWebhook<{ weather: WeatherInfo[] }>('tourism/weather', {
    spots: spotNames,
    date: date || new Date().toISOString().split('T')[0]
  })
  return response.weather
}

export async function aiChat(request: AIChatRequest): Promise<AIChatResponse> {
  return triggerWebhook<AIChatResponse>('tourism/ai-chat', request)
}

export async function getAudioGuide(request: AudioGuideRequest): Promise<AudioGuideResponse> {
  return triggerWebhook<AudioGuideResponse>('tourism/audio-guide', request)
}

export async function getHotRoutes(): Promise<HotRoute[]> {
  const response = await triggerWebhook<{ success: boolean; routes: HotRoute[] }>('tourism/routes', {})
  return response.routes || []
}

export async function getRedSpots(): Promise<any[]> {
  const response = await triggerWebhook<{ success: boolean; spots: any[] }>('tourism/spots', {})
  return response.spots || []
}

export async function getDashboardData(): Promise<any> {
  return triggerWebhook('tourism/dashboard', {})
}

export default {
  generateAITripPlan,
  getSpotWeather,
  aiChat,
  getAudioGuide,
  getHotRoutes,
  getRedSpots,
  getDashboardData
}
