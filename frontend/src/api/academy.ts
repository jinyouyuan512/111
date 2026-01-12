import request from './request'
import axios from 'axios'

// n8n 客户端配置
const N8N_BASE_URL = import.meta.env.VITE_N8N_URL || 'http://localhost:5678'
const n8nClient = axios.create({
  baseURL: N8N_BASE_URL,
  timeout: 30000,
  headers: { 'Content-Type': 'application/json' }
})

// 数据获取优先级：1. 后端API(数据库) -> 2. 本地模拟数据
// n8n 工作流负责定时爬取数据写入数据库，前端只从数据库读取

// ==================== 本地模拟数据（后备方案）====================

const mockNewsData: RedNews[] = [
  {
    id: 1,
    title: '西柏坡纪念馆举办"新中国从这里走来"主题展览',
    summary: '展览全面展示了中共中央在西柏坡的光辉历程，通过珍贵文物、历史照片和多媒体展示，生动再现了那段波澜壮阔的革命岁月。',
    category: '纪念活动',
    source: '河北日报',
    author: '张明',
    viewCount: 125800,
    likeCount: 8560,
    isTop: true,
    isHot: true,
    publishTime: new Date().toISOString(),
    externalUrl: 'https://hebei.hebnews.cn/node_50.htm'
  },
  {
    id: 2,
    title: '河北省开展"传承红色基因"主题教育活动',
    summary: '全省各地积极组织党员干部深入红色教育基地，接受革命传统教育，推动党史学习教育走深走实。',
    category: '教育实践',
    source: '河北新闻网',
    author: '李华',
    viewCount: 89200,
    likeCount: 5620,
    isTop: false,
    isHot: true,
    publishTime: new Date(Date.now() - 86400000).toISOString(),
    externalUrl: 'https://www.hebnews.cn/'
  },
  {
    id: 3,
    title: '狼牙山五壮士纪念地迎来参观高峰',
    summary: '清明节期间，大批群众前往狼牙山缅怀革命先烈，传承英雄精神，接受爱国主义教育。',
    category: '红色故事',
    source: '燕赵都市报',
    author: '王强',
    viewCount: 67500,
    likeCount: 4230,
    isTop: false,
    isHot: false,
    publishTime: new Date(Date.now() - 172800000).toISOString(),
    externalUrl: 'https://yzdsb.hebnews.cn/'
  },
  {
    id: 4,
    title: '河北省委召开党史学习教育推进会',
    summary: '会议强调要深入学习贯彻习近平总书记重要讲话精神，推动党史学习教育走深走实。',
    category: '党建动态',
    source: '河北日报',
    author: '赵刚',
    viewCount: 156800,
    likeCount: 9870,
    isTop: false,
    isHot: true,
    publishTime: new Date(Date.now() - 259200000).toISOString(),
    externalUrl: 'https://hebei.hebnews.cn/node_50.htm'
  },
  {
    id: 5,
    title: '白洋淀雁翎队纪念馆完成升级改造',
    summary: '改造后的纪念馆运用现代科技手段，生动再现雁翎队抗日斗争历史，成为雄安新区重要的红色教育基地。',
    category: '纪念活动',
    source: '长城网',
    author: '刘芳',
    viewCount: 43200,
    likeCount: 2890,
    isTop: false,
    isHot: false,
    publishTime: new Date(Date.now() - 345600000).toISOString(),
    externalUrl: 'https://www.hebnews.cn/xiongan/'
  },
  {
    id: 6,
    title: '塞罕坝精神宣讲团走进高校',
    summary: '宣讲团成员讲述三代务林人艰苦奋斗的感人故事，激励青年学子传承塞罕坝精神。',
    category: '教育实践',
    source: '河北新闻网',
    author: '陈静',
    viewCount: 78900,
    likeCount: 5120,
    isTop: false,
    isHot: false,
    publishTime: new Date(Date.now() - 432000000).toISOString(),
    externalUrl: 'https://www.hebnews.cn/'
  },
  {
    id: 7,
    title: '李大钊故居纪念馆举办专题展览',
    summary: '展览以"播火者"为主题，全面展示李大钊同志传播马克思主义、创建中国共产党的光辉历程。',
    category: '红色故事',
    source: '唐山晚报',
    author: '周明',
    viewCount: 52300,
    likeCount: 3450,
    isTop: false,
    isHot: false,
    publishTime: new Date(Date.now() - 518400000).toISOString(),
    externalUrl: 'https://www.tswb.com.cn/'
  },
  {
    id: 8,
    title: '董存瑞烈士陵园举行公祭活动',
    summary: '社会各界代表齐聚董存瑞烈士陵园，缅怀革命先烈，传承英雄精神。',
    category: '纪念活动',
    source: '承德日报',
    author: '孙伟',
    viewCount: 68700,
    likeCount: 4560,
    isTop: false,
    isHot: true,
    publishTime: new Date(Date.now() - 604800000).toISOString(),
    externalUrl: 'https://www.cdnews.cn/'
  }
]

const mockBooksData: RedBook[] = [
  {
    id: 1,
    title: '西柏坡：新中国从这里走来',
    author: '中共河北省委党史研究室',
    publisher: '河北人民出版社',
    isbn: '978-7-202-12345-6',
    description: '本书全面记录了中共中央在西柏坡的光辉历程，深入解读了西柏坡精神的科学内涵和时代价值。',
    category: '党史著作',
    pageCount: 380,
    publishDate: '2023-07',
    rating: 4.9,
    ratingCount: 2580,
    readCount: 125800,
    favoriteCount: 8960,
    isRecommended: true,
    hasEbook: true,
    tags: '西柏坡,党史,必读,新中国',
    externalUrl: 'https://search.jd.com/Search?keyword=西柏坡新中国从这里走来'
  },
  {
    id: 2,
    title: '狼牙山五壮士',
    author: '沈重',
    publisher: '人民文学出版社',
    isbn: '978-7-020-12346-7',
    description: '本书讲述了1941年狼牙山战斗中，五位八路军战士为掩护群众和主力撤退，在弹尽粮绝后跳崖的壮烈故事。',
    category: '纪实文学',
    pageCount: 256,
    publishDate: '2021-09',
    rating: 4.8,
    ratingCount: 3650,
    readCount: 98500,
    favoriteCount: 6780,
    isRecommended: true,
    hasEbook: true,
    tags: '抗战,英雄,经典,狼牙山',
    externalUrl: 'https://search.jd.com/Search?keyword=狼牙山五壮士'
  },
  {
    id: 3,
    title: '李大钊传',
    author: '朱志敏',
    publisher: '红旗出版社',
    isbn: '978-7-505-12347-8',
    description: '本书全面记述了中国共产主义运动先驱李大钊的革命生涯，展现了他为传播马克思主义、创建中国共产党所作出的卓越贡献。',
    category: '人物传记',
    pageCount: 420,
    publishDate: '2022-04',
    rating: 4.7,
    ratingCount: 1890,
    readCount: 67200,
    favoriteCount: 4560,
    isRecommended: true,
    hasEbook: true,
    tags: '李大钊,先驱,传记,马克思主义',
    externalUrl: 'https://search.jd.com/Search?keyword=李大钊传'
  },
  {
    id: 4,
    title: '塞罕坝精神',
    author: '河北省林业厅',
    publisher: '中国林业出版社',
    isbn: '978-7-503-12348-9',
    description: '本书记录了三代塞罕坝人艰苦创业、绿色发展的感人事迹，深刻诠释了"牢记使命、艰苦创业、绿色发展"的塞罕坝精神。',
    category: '纪实文学',
    pageCount: 320,
    publishDate: '2022-08',
    rating: 4.8,
    ratingCount: 2120,
    readCount: 85600,
    favoriteCount: 5890,
    isRecommended: true,
    hasEbook: true,
    tags: '塞罕坝,生态,奋斗,绿色发展',
    externalUrl: 'https://search.jd.com/Search?keyword=塞罕坝精神'
  },
  {
    id: 5,
    title: '白洋淀纪事',
    author: '孙犁',
    publisher: '人民文学出版社',
    isbn: '978-7-020-12349-0',
    description: '孙犁代表作，以白洋淀为背景，描绘了抗日战争时期冀中人民的斗争生活。作品语言优美，人物形象鲜明，是中国现代文学的经典之作。',
    category: '红色经典',
    pageCount: 280,
    publishDate: '2020-05',
    rating: 4.9,
    ratingCount: 5680,
    readCount: 156800,
    favoriteCount: 12350,
    isRecommended: true,
    hasEbook: true,
    tags: '白洋淀,孙犁,文学经典,抗战',
    externalUrl: 'https://search.jd.com/Search?keyword=白洋淀纪事孙犁'
  },
  {
    id: 6,
    title: '晋察冀边区革命史',
    author: '中共河北省委党史研究室',
    publisher: '中共党史出版社',
    isbn: '978-7-509-12350-1',
    description: '本书系统介绍了晋察冀抗日根据地的创建、发展和历史贡献，是研究晋察冀边区历史的重要参考书。',
    category: '党史著作',
    pageCount: 560,
    publishDate: '2021-12',
    rating: 4.6,
    ratingCount: 980,
    readCount: 32500,
    favoriteCount: 2340,
    isRecommended: false,
    hasEbook: true,
    tags: '晋察冀,根据地,抗战,边区',
    externalUrl: 'https://search.jd.com/Search?keyword=晋察冀边区革命史'
  },
  {
    id: 7,
    title: '红色少年读本：河北英雄故事',
    author: '河北省教育厅',
    publisher: '河北少年儿童出版社',
    isbn: '978-7-537-12351-2',
    description: '精选河北革命英雄故事，用生动的语言和精美的插图，向青少年讲述革命先辈的英勇事迹。',
    category: '青少年读物',
    pageCount: 180,
    publishDate: '2023-03',
    rating: 4.7,
    ratingCount: 1560,
    readCount: 78900,
    favoriteCount: 5670,
    isRecommended: true,
    hasEbook: true,
    tags: '青少年,英雄故事,教育,插图',
    externalUrl: 'https://search.jd.com/Search?keyword=河北英雄故事'
  },
  {
    id: 8,
    title: '董存瑞',
    author: '赵寰',
    publisher: '解放军文艺出版社',
    isbn: '978-7-503-12352-3',
    description: '本书讲述了舍身炸碉堡的战斗英雄董存瑞的英勇事迹，展现了他短暂而光辉的一生。',
    category: '人物传记',
    pageCount: 240,
    publishDate: '2022-05',
    rating: 4.8,
    ratingCount: 2890,
    readCount: 112500,
    favoriteCount: 7890,
    isRecommended: true,
    hasEbook: true,
    tags: '董存瑞,英雄,解放战争,隆化',
    externalUrl: 'https://search.jd.com/Search?keyword=董存瑞传'
  },
  {
    id: 9,
    title: '冀中一日',
    author: '孙犁等',
    publisher: '花山文艺出版社',
    isbn: '978-7-551-12353-4',
    description: '1941年5月27日，冀中区党委组织千余名作者，记录下这一天冀中平原上发生的事情，真实反映了抗战时期冀中人民的生活和斗争。',
    category: '红色经典',
    pageCount: 350,
    publishDate: '2021-05',
    rating: 4.6,
    ratingCount: 1230,
    readCount: 45600,
    favoriteCount: 3210,
    isRecommended: false,
    hasEbook: true,
    tags: '冀中,抗战,纪实,群众',
    externalUrl: 'https://search.jd.com/Search?keyword=冀中一日'
  },
  {
    id: 10,
    title: '雁翎队',
    author: '穆青',
    publisher: '河北人民出版社',
    isbn: '978-7-202-12354-5',
    description: '本书讲述了抗日战争时期活跃在白洋淀地区的水上游击队——雁翎队的英勇事迹。',
    category: '纪实文学',
    pageCount: 290,
    publishDate: '2022-07',
    rating: 4.7,
    ratingCount: 1680,
    readCount: 56700,
    favoriteCount: 4120,
    isRecommended: true,
    hasEbook: true,
    tags: '雁翎队,白洋淀,抗战,游击队',
    externalUrl: 'https://search.jd.com/Search?keyword=雁翎队'
  }
]

// ==================== 红色新闻接口（数据库） ====================

export interface RedNews {
  id: number
  title: string
  summary: string
  content?: string
  coverImage?: string
  category: string
  source: string
  author?: string
  viewCount: number
  likeCount?: number
  isTop: boolean
  isHot: boolean
  publishTime: string
  externalUrl?: string
}

export interface NewsListResponse {
  success: boolean
  news: RedNews[]
  total: number
}

/**
 * 获取红色新闻列表
 * 优先级：1. 后端API(数据库) -> 2. 本地模拟数据
 * n8n 工作流定时爬取数据写入数据库
 */
export async function getRedNewsList(category?: string): Promise<NewsListResponse> {
  // 1. 尝试从后端 API 获取（数据来自数据库，由n8n定时同步）
  try {
    const res = await request({
      url: '/academy/news',
      method: 'get',
      params: { category }
    })
    console.log('getRedNewsList raw response:', res)
    if (res && typeof res === 'object') {
      if ('success' in res && 'news' in res) {
        console.log('从数据库获取新闻数据成功')
        return res as unknown as NewsListResponse
      }
      if ('data' in res && res.data) {
        return res.data as unknown as NewsListResponse
      }
    }
  } catch (error) {
    console.log('后端API不可用，使用本地数据')
  }

  // 2. 使用本地模拟数据（后备方案）
  return getLocalNewsList(category)
}

/**
 * 获取本地新闻数据
 */
function getLocalNewsList(category?: string): NewsListResponse {
  let news = [...mockNewsData]
  if (category && category !== 'all') {
    news = news.filter(n => n.category === category)
  }
  return {
    success: true,
    news,
    total: news.length
  }
}

/**
 * 获取新闻详情
 */
export async function getNewsDetail(id: number): Promise<{ success: boolean; news: RedNews | null }> {
  try {
    const res = await request({
      url: `/academy/news/${id}`,
      method: 'get'
    }) as { success: boolean; news: RedNews }
    return res
  } catch (error) {
    return { success: false, news: null }
  }
}

/**
 * 搜索红色新闻
 */
export async function searchRedNews(keyword: string): Promise<NewsListResponse> {
  try {
    const res = await request({
      url: '/academy/news/search',
      method: 'get',
      params: { keyword }
    }) as NewsListResponse
    return res
  } catch (error) {
    // 本地搜索
    const news = mockNewsData.filter(n => 
      n.title.includes(keyword) || n.summary.includes(keyword)
    )
    return { success: true, news, total: news.length }
  }
}

/**
 * 获取热门新闻
 */
export async function getHotNews(): Promise<NewsListResponse> {
  try {
    const res = await request({
      url: '/academy/news/hot',
      method: 'get'
    }) as NewsListResponse
    return res
  } catch (error) {
    return { success: false, news: [], total: 0 }
  }
}

/**
 * 点赞新闻
 */
export async function likeNews(id: number): Promise<{ success: boolean }> {
  try {
    const res = await request({
      url: `/academy/news/${id}/like`,
      method: 'post'
    }) as { success: boolean }
    return res
  } catch (error) {
    return { success: false }
  }
}

// ==================== 红色读物接口（数据库） ====================

export interface RedBook {
  id: number
  title: string
  author: string
  publisher?: string
  isbn?: string
  coverImage?: string
  description: string
  category: string
  pageCount?: number
  publishDate?: string
  rating: number
  ratingCount: number
  readCount: number
  favoriteCount?: number
  isRecommended: boolean
  hasEbook: boolean
  ebookUrl?: string
  tags: string
  externalUrl?: string
}

export interface BookListResponse {
  success: boolean
  books: RedBook[]
  total: number
}

/**
 * 获取红色读物列表
 * 优先级：1. 后端API(数据库) -> 2. 本地模拟数据
 * n8n 工作流定时爬取数据写入数据库
 */
export async function getRedBookList(category?: string): Promise<BookListResponse> {
  // 1. 尝试从后端 API 获取（数据来自数据库，由n8n定时同步）
  try {
    const res = await request({
      url: '/academy/books',
      method: 'get',
      params: { category }
    })
    console.log('getRedBookList raw response:', res)
    if (res && typeof res === 'object') {
      if ('success' in res && 'books' in res) {
        console.log('从数据库获取书籍数据成功')
        return res as unknown as BookListResponse
      }
      if ('data' in res && res.data) {
        return res.data as unknown as BookListResponse
      }
    }
  } catch (error) {
    console.log('后端API不可用，使用本地数据')
  }

  // 2. 使用本地模拟数据（后备方案）
  return getLocalBookList(category)
}

/**
 * 获取本地书籍数据
 */
function getLocalBookList(category?: string): BookListResponse {
  let books = [...mockBooksData]
  if (category && category !== 'all') {
    books = books.filter(b => b.category === category)
  }
  return {
    success: true,
    books,
    total: books.length
  }
}

/**
 * 获取书籍详情
 */
export async function getBookDetail(id: number): Promise<{ success: boolean; book: RedBook | null }> {
  try {
    const res = await request({
      url: `/academy/books/${id}`,
      method: 'get'
    }) as { success: boolean; book: RedBook }
    return res
  } catch (error) {
    return { success: false, book: null }
  }
}

/**
 * 搜索红色读物
 */
export async function searchRedBooks(keyword: string): Promise<BookListResponse> {
  try {
    const res = await request({
      url: '/academy/books/search',
      method: 'get',
      params: { keyword }
    }) as BookListResponse
    return res
  } catch (error) {
    // 本地搜索
    const books = mockBooksData.filter(b => 
      b.title.includes(keyword) || b.author.includes(keyword) || b.description.includes(keyword)
    )
    return { success: true, books, total: books.length }
  }
}

/**
 * 获取推荐书籍
 */
export async function getRecommendedBooks(): Promise<BookListResponse> {
  try {
    const res = await request({
      url: '/academy/books/recommended',
      method: 'get'
    }) as BookListResponse
    return res
  } catch (error) {
    return { success: false, books: [], total: 0 }
  }
}

/**
 * 收藏书籍
 */
export async function favoriteBook(id: number): Promise<{ success: boolean }> {
  try {
    const res = await request({
      url: `/academy/books/${id}/favorite`,
      method: 'post'
    }) as { success: boolean }
    return res
  } catch (error) {
    return { success: false }
  }
}

// ==================== AI 增强功能（n8n 工作流） ====================

export interface BookRecommendation {
  bookId: number
  title: string
  author: string
  category: string
  matchScore: number
  reason: string
  highlights: string[]
}

export interface BookRecommendResponse {
  success: boolean
  recommendations: BookRecommendation[]
  personalizedTip: string
}

/**
 * 获取 AI 个性化推荐（通过 n8n 工作流）
 */
export async function getAIBookRecommendations(params: {
  preferences?: string
  readBooks?: string[]
  interests?: string[]
  purpose?: string
}): Promise<BookRecommendResponse> {
  try {
    const response = await n8nClient.post('/webhook/academy/book-recommend', params)
    return response.data
  } catch (error) {
    // 降级方案：返回基于数据库的推荐
    return getMockRecommendations()
  }
}

function getMockRecommendations(): BookRecommendResponse {
  return {
    success: true,
    recommendations: [
      {
        bookId: 1,
        title: '西柏坡：新中国从这里走来',
        author: '中共河北省委党史研究室',
        category: '党史著作',
        matchScore: 95,
        reason: '了解河北红色历史的必读之作，全面展现西柏坡精神。',
        highlights: ['权威党史资料', '图文并茂', '深度解读']
      },
      {
        bookId: 5,
        title: '白洋淀纪事',
        author: '孙犁',
        category: '红色经典',
        matchScore: 92,
        reason: '文学性与革命性完美结合，感受冀中人民的抗战精神。',
        highlights: ['文学经典', '生动感人', '历史价值']
      },
      {
        bookId: 4,
        title: '塞罕坝精神',
        author: '河北省林业厅',
        category: '纪实文学',
        matchScore: 88,
        reason: '当代红色精神的典范，展现艰苦奋斗的时代价值。',
        highlights: ['时代精神', '生态文明', '奋斗故事']
      }
    ],
    personalizedTip: '建议您从《西柏坡》开始阅读，了解河北在中国革命史上的重要地位。'
  }
}

// ==================== 原有课程接口（保留兼容）====================

export interface Course {
  id: number
  title: string
  category: string
  description: string
  instructor: string
  coverImage: string
  totalHours: number
  level: string
  enrollmentCount: number
  createdAt: string
}

export function getCourseList(category?: string) {
  return request<Course[]>({
    url: '/api/academy/courses',
    method: 'get',
    params: { category }
  })
}

export function getCourseDetail(courseId: number) {
  return request<Course>({
    url: `/api/academy/courses/${courseId}`,
    method: 'get'
  })
}
