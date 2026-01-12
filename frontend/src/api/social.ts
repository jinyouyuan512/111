import request from './request'

// 获取动态列表
export const getPosts = (params?: {
  category?: string
  keyword?: string
  sortBy?: string
  page?: number
  size?: number
}) => {
  return request({
    url: '/posts',
    method: 'get',
    params,
    headers: {
      'Authorization': localStorage.getItem('token') ? `Bearer ${localStorage.getItem('token')}` : ''
    }
  })
}

// 发布动态
export const createPost = (data: {
  title?: string
  content: string
  images?: string[]
  video?: { url: string; thumbnail: string; duration: number }
  tags?: string[]
  location?: string
  category: string
}) => {
  return request({
    url: '/posts',
    method: 'post',
    data
  })
}

// 点赞动态
export const likePost = (postId: number) => {
  return request({
    url: `/posts/${postId}/like`,
    method: 'post'
  })
}

// 取消点赞
export const unlikePost = (postId: number) => {
  return request({
    url: `/posts/${postId}/like`,
    method: 'delete'
  })
}

// 获取评论列表
export const getComments = (postId: number) => {
  return request({
    url: `/posts/${postId}/comments`,
    method: 'get'
  })
}

// 发表评论
export const createComment = (postId: number, content: string) => {
  return request({
    url: `/posts/${postId}/comments`,
    method: 'post',
    data: { content }
  })
}

// 点赞评论
export const likeComment = (postId: number, commentId: number) => {
  return request({
    url: `/posts/${postId}/comments/${commentId}/like`,
    method: 'post'
  })
}

// 取消评论点赞
export const unlikeComment = (postId: number, commentId: number) => {
  return request({
    url: `/posts/${postId}/comments/${commentId}/like`,
    method: 'delete'
  })
}

// 获取热门话题
export const getHotTopics = () => {
  return request({
    url: '/topics/hot',
    method: 'get'
  })
}

// 获取活跃用户（红色达人）
export const getActiveUsers = () => {
  return request({
    url: '/topics/users/active',
    method: 'get'
  })
}

// 关注用户
export const followUser = (userId: number) => {
  return request({
    url: `/users/${userId}/follow`,
    method: 'post'
  })
}

// 取消关注
export const unfollowUser = (userId: number) => {
  return request({
    url: `/users/${userId}/follow`,
    method: 'delete'
  })
}

// 上传图片
export const uploadImage = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/upload/image',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 批量上传图片
export const uploadImages = (files: File[]) => {
  const formData = new FormData()
  files.forEach(file => {
    formData.append('files', file)
  })
  return request({
    url: '/upload/images',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 上传视频
export const uploadVideo = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/upload/video',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export default {
  getPosts,
  createPost,
  likePost,
  unlikePost,
  getComments,
  createComment,
  likeComment,
  unlikeComment,
  getHotTopics,
  getActiveUsers,
  followUser,
  unfollowUser,
  uploadImage,
  uploadImages,
  uploadVideo
}
