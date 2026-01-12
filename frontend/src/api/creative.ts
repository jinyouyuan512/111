import request from './request'

// 众创空间首页数据
export interface CreativeSpaceVO {
  contests: ContestVO[]
  creativeCalls: CreativeCallVO[]
  featuredDesigns: DesignVO[]
}

// 设计大赛
export interface ContestVO {
  id: number
  title: string
  description: string
  theme: string
  coverImage: string
  prizePool: number
  startTime: string
  endTime: string
  votingEndTime: string
  status: string
  participantCount: number
  createdAt: string
}

// 创意征集
export interface CreativeCallVO {
  id: number
  title: string
  description: string
  requirements: string
  budget: number
  deadline: string
  status: string
  publisherId: number
  publisherType: string
  submissionCount: number
  createdAt: string
}

// 设计作品
export interface DesignVO {
  id: number
  designerId: number
  designerName?: string
  contestId?: number
  contestTitle?: string
  callId?: number
  callTitle?: string
  title: string
  description: string
  designConcept: string
  files: string[]
  coverImage: string
  copyrightStatement: string
  categoryType?: number
  tags?: string
  status: string
  rejectReason?: string
  votes: number
  views: number
  commentCount?: number
  hasVoted?: boolean
  createTime?: string
  createdAt: string
}

// 设计作品提交
export interface DesignSubmitRequest {
  contestId?: number
  callId?: number
  title: string
  categoryType?: number
  description: string
  designConcept?: string
  files: string[]  // 修改为数组类型
  coverImage?: string
  copyrightStatement?: string
  tags?: string
}

// 设计师
export interface DesignerVO {
  id: number
  userId: number
  realName: string
  bio: string
  skills: string[]
  portfolioUrl: string
  experienceYears: number
  rating: number
  completedProjects: number
  verified: boolean
  matchScore?: number
}

// 获取众创空间首页数据
export const getCreativeSpace = () => {
  return request.get<CreativeSpaceVO>('/creative/space')
}

// 获取所有设计大赛
export const getContests = () => {
  return request.get<ContestVO[]>('/creative/contests')
}

// 获取大赛详情
export const getContestById = (contestId: number) => {
  return request.get<ContestVO>(`/creative/contests/${contestId}`)
}

// 获取所有创意征集
export const getCreativeCalls = () => {
  return request.get<CreativeCallVO[]>('/creative/calls')
}

// 获取创意征集详情
export const getCreativeCallById = (callId: number) => {
  return request.get<CreativeCallVO>(`/creative/calls/${callId}`)
}

// 提交设计作品
export const submitDesign = (data: DesignSubmitRequest) => {
  return request.post<DesignVO>('/creative/designs', data)
}

// 创建设计作品（别名）
export const createDesign = (data: DesignSubmitRequest) => {
  return request.post<DesignVO>('/creative/designs', data)
}

// 获取所有作品（使用排行榜接口）
export const getDesigns = (params?: { page?: number; size?: number; categoryType?: number }) => {
  return request.get<DesignVO[]>('/creative/designs/top', { 
    params: { limit: params?.size || 100 } 
  }).then(res => {
    // res 已经是 data 数组（由 request.ts 的拦截器处理）
    const designs = Array.isArray(res) ? res : []
    return {
      code: 200,
      data: {
        records: designs,
        total: designs.length
      }
    }
  })
}

// 获取作品详情（会自动增加浏览量）
export const getDesignById = (designId: number) => {
  return request.get<DesignVO>(`/creative/designs/${designId}`)
}

// 获取大赛作品列表
export const getDesignsByContest = (contestId: number) => {
  return request.get<DesignVO[]>(`/creative/contests/${contestId}/designs`)
}

// 获取征集作品列表
export const getDesignsByCall = (callId: number) => {
  return request.get<DesignVO[]>(`/creative/calls/${callId}/designs`)
}

// 获取我的作品
export const getMyDesigns = (_params?: { page?: number; size?: number }) => {
  return request.get<DesignVO[]>('/creative/designs/my').then(res => {
    // res 已经是 data 数组（由 request.ts 的拦截器处理）
    const designs = Array.isArray(res) ? res : []
    return {
      code: 200,
      data: {
        records: designs,
        total: designs.length
      }
    }
  })
}

// 投票
export const voteDesign = (designId: number) => {
  return request.post(`/creative/designs/${designId}/vote`)
}

// 取消投票
export const unvoteDesign = (designId: number) => {
  return request.delete(`/creative/designs/${designId}/vote`)
}

// 获取排行榜
export const getTopDesigns = (limit: number = 10) => {
  return request.get<DesignVO[]>('/creative/designs/top', { params: { limit } })
}

// 匹配设计师
export const matchDesigners = (requirementId: number) => {
  return request.get<DesignerVO[]>(`/creative/requirements/${requirementId}/match`)
}

// ============ 商城上架申请相关 API ============

// 上架申请请求
export interface MallApplicationRequest {
  designId: number
  productName: string
  category: string
  description: string
  suggestedPrice: number
  initialStock: number
  icon?: string
}

// 上架申请响应
export interface MallApplicationVO {
  id: number
  designId: number
  userId: number
  productName: string
  category: string
  description: string
  suggestedPrice: number
  initialStock: number
  icon: string
  status: 'pending' | 'approved' | 'rejected'
  reviewerId?: number
  reviewComment?: string
  reviewedAt?: string
  productId?: number
  createdAt: string
  updatedAt: string
}

// 提交上架申请
export const submitMallApplication = (data: MallApplicationRequest) => {
  return request.post('/creative/mall-applications', data)
}

// 获取我的申请列表
export const getMyMallApplications = (params?: { page?: number; size?: number }) => {
  return request.get('/creative/mall-applications/my', { params })
}

// 检查作品是否已申请
export const checkMallApplication = (designId: number) => {
  return request.get(`/creative/mall-applications/check/${designId}`)
}

// 获取申请列表（管理员）
export const getMallApplicationList = (params?: { page?: number; size?: number; status?: string }) => {
  return request.get('/creative/mall-applications', { params })
}

// 审核申请（管理员）
export const reviewMallApplication = (id: number, data: { approved: boolean; comment?: string }) => {
  return request.post(`/creative/mall-applications/${id}/review`, data)
}

// 导出所有API为一个对象
export const creativeApi = {
  getCreativeSpace,
  getContests,
  getContestById,
  getCreativeCalls,
  getCreativeCallById,
  submitDesign,
  createDesign,
  getDesigns,
  getDesignById,
  getDesignsByContest,
  getDesignsByCall,
  getMyDesigns,
  voteDesign,
  unvoteDesign,
  getTopDesigns,
  matchDesigners,
  // 商城上架申请
  submitMallApplication,
  getMyMallApplications,
  checkMallApplication,
  getMallApplicationList,
  reviewMallApplication
}
