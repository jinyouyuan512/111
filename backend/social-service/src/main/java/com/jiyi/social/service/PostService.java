package com.jiyi.social.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiyi.common.exception.BusinessException;
import com.jiyi.social.client.UserServiceClient;
import com.jiyi.social.dto.*;
import com.jiyi.social.entity.Post;
import com.jiyi.social.entity.PostLike;
import com.jiyi.social.entity.MediaFile;
import com.jiyi.social.entity.Topic;
import com.jiyi.social.entity.PostTopic;
import com.jiyi.social.mapper.PostLikeMapper;
import com.jiyi.social.mapper.PostMapper;
import com.jiyi.social.mapper.MediaFileMapper;
import com.jiyi.social.mapper.TopicMapper;
import com.jiyi.social.mapper.PostTopicMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostMapper postMapper;
    private final PostLikeMapper postLikeMapper;
    private final ObjectMapper objectMapper;
    private final MediaFileMapper mediaFileMapper;
    private final TopicMapper topicMapper;
    private final PostTopicMapper postTopicMapper;
    private final UserServiceClient userServiceClient;
    
    @Transactional
    public PostVO createPost(Long userId, PostCreateRequest request) {
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setLocationName(request.getLocation());
        post.setType("normal");
        post.setVisibility("public");
        post.setStatus("published");
        post.setLikes(0);
        post.setComments(0);
        post.setShares(0);
        post.setViews(0);
        
        // images 和 category 是非数据库字段，不需要设置
        
        postMapper.insert(post);
        
        // 保存图片到media_file表
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            int orderNum = 1;
            for (String imageUrl : request.getImages()) {
                MediaFile mediaFile = new MediaFile();
                mediaFile.setPostId(post.getId());
                mediaFile.setType("image");
                mediaFile.setUrl(imageUrl);
                mediaFile.setThumbnail(imageUrl); // 使用相同URL作为缩略图
                mediaFile.setOrderNum(orderNum++);
                mediaFileMapper.insert(mediaFile);
            }
        }
        
        // 保存视频到media_file表
        if (request.getVideo() != null && request.getVideo().getUrl() != null) {
            MediaFile mediaFile = new MediaFile();
            mediaFile.setPostId(post.getId());
            mediaFile.setType("video");
            mediaFile.setUrl(request.getVideo().getUrl());
            mediaFile.setThumbnail(request.getVideo().getThumbnail());
            mediaFile.setDuration(request.getVideo().getDuration());
            mediaFile.setOrderNum(1);
            mediaFileMapper.insert(mediaFile);
        }
        
        // 处理话题标签
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            System.out.println("开始处理话题标签，数量: " + request.getTags().size());
            for (String tagName : request.getTags()) {
                System.out.println("处理话题: " + tagName);
                // 查找或创建话题
                LambdaQueryWrapper<Topic> topicWrapper = new LambdaQueryWrapper<>();
                topicWrapper.eq(Topic::getName, tagName);
                Topic topic = topicMapper.selectOne(topicWrapper);
                
                if (topic == null) {
                    // 话题不存在，创建新话题
                    System.out.println("话题不存在，创建新话题: " + tagName);
                    topic = new Topic();
                    topic.setName(tagName);
                    topic.setDescription("用户创建的话题");
                    topic.setPostCount(0);
                    topic.setFollowCount(0);
                    topic.setViewCount(0);
                    topic.setStatus("active");
                    topicMapper.insert(topic);
                    System.out.println("新话题创建成功，ID: " + topic.getId());
                } else {
                    System.out.println("找到已存在话题，ID: " + topic.getId());
                }
                
                // 创建动态和话题的关联
                PostTopic postTopic = new PostTopic();
                postTopic.setPostId(post.getId());
                postTopic.setTopicId(topic.getId());
                postTopicMapper.insert(postTopic);
                System.out.println("话题关联创建成功，post_id: " + post.getId() + ", topic_id: " + topic.getId());
            }
        } else {
            System.out.println("没有话题标签需要处理");
        }
        
        return convertToVO(post, userId);
    }
    
    public List<PostVO> getPosts(String category, Integer page, Integer size, Long currentUserId) {
        return searchPosts(null, category, null, page, size, currentUserId);
    }
    
    public List<PostVO> searchPosts(String keyword, String category, String sortBy, Integer page, Integer size, Long currentUserId) {
        Page<Post> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索（标题或内容）
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like(Post::getTitle, kw).or().like(Post::getContent, kw));
        }
        
        wrapper.eq(Post::getStatus, "published");
        
        // 排序方式
        if ("hot".equals(sortBy)) {
            // 热门：按点赞数排序
            wrapper.orderByDesc(Post::getLikes);
        } else if ("comments".equals(sortBy)) {
            // 评论最多
            wrapper.orderByDesc(Post::getComments);
        } else {
            // 默认：最新
            wrapper.orderByDesc(Post::getCreatedAt);
        }
        
        Page<Post> result = postMapper.selectPage(pageObj, wrapper);
        
        return result.getRecords().stream()
                .map(post -> convertToVO(post, currentUserId))
                .collect(Collectors.toList());
    }
    
    // 保留旧方法以兼容
    public List<PostVO> getPosts(String category, Integer page, Integer size) {
        return getPosts(category, page, size, null);
    }
    
    public PostVO getPostById(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(404, "动态不存在");
        }
        return convertToVO(post, null);
    }
    
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(404, "动态不存在");
        }
        if (!post.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权删除此动态");
        }
        postMapper.deleteById(postId);
    }
    
    @Transactional
    public void likePost(Long postId, Long userId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(404, "动态不存在");
        }
        
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getUserId, userId)
               .eq(PostLike::getTargetType, "post")
               .eq(PostLike::getTargetId, postId);
        
        if (postLikeMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "已经点赞过了");
        }
        
        PostLike postLike = new PostLike();
        postLike.setUserId(userId);
        postLike.setTargetType("post");
        postLike.setTargetId(postId);
        postLikeMapper.insert(postLike);
        
        post.setLikes(post.getLikes() + 1);
        postMapper.updateById(post);
    }
    
    @Transactional
    public void unlikePost(Long postId, Long userId) {
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getUserId, userId)
               .eq(PostLike::getTargetType, "post")
               .eq(PostLike::getTargetId, postId);
        postLikeMapper.delete(wrapper);
        
        Post post = postMapper.selectById(postId);
        if (post != null && post.getLikes() > 0) {
            post.setLikes(post.getLikes() - 1);
            postMapper.updateById(post);
        }
    }
    
    @Transactional
    public void sharePost(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(404, "动态不存在");
        }
        post.setShares(post.getShares() + 1);
        postMapper.updateById(post);
    }
    
    public List<PostVO> getUserPosts(Long userId, Integer page, Integer size) {
        Page<Post> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getUserId, userId).orderByDesc(Post::getCreatedAt);
        
        Page<Post> result = postMapper.selectPage(pageObj, wrapper);
        return result.getRecords().stream()
                .map(post -> convertToVO(post, userId))
                .collect(Collectors.toList());
    }
    
    private PostVO convertToVO(Post post, Long currentUserId) {
        PostVO vo = new PostVO();
        vo.setId(post.getId());
        vo.setUserId(post.getUserId());
        vo.setTitle(post.getTitle());
        vo.setContent(post.getContent());
        vo.setCreatedAt(post.getCreatedAt());
        
        // 设置位置
        vo.setLocation(post.getLocationName());
        
        // 映射计数字段（数据库字段名 -> VO字段名）
        vo.setLikesCount(post.getLikes());
        vo.setCommentsCount(post.getComments());
        vo.setSharesCount(post.getShares());
        
        // 加载媒体文件（图片和视频）
        LambdaQueryWrapper<MediaFile> mediaWrapper = new LambdaQueryWrapper<>();
        mediaWrapper.eq(MediaFile::getPostId, post.getId())
                   .orderByAsc(MediaFile::getOrderNum);
        List<MediaFile> mediaFiles = mediaFileMapper.selectList(mediaWrapper);
        
        // 分离图片和视频
        List<String> images = new ArrayList<>();
        for (MediaFile media : mediaFiles) {
            if ("image".equals(media.getType())) {
                images.add(media.getUrl());
            } else if ("video".equals(media.getType())) {
                // 设置视频信息
                VideoVO videoVO = new VideoVO();
                videoVO.setUrl(media.getUrl());
                videoVO.setThumbnail(media.getThumbnail());
                videoVO.setDuration(media.getDuration());
                vo.setVideo(videoVO);
            }
        }
        vo.setImages(images);
        
        // 从 user-service 获取用户信息
        UserServiceClient.UserInfo userInfo = userServiceClient.getUserById(post.getUserId());
        vo.setUserNickname(userInfo.getNickname() != null ? userInfo.getNickname() : "用户" + post.getUserId());
        vo.setUserAvatar(userInfo.getAvatar() != null ? userInfo.getAvatar() : "");
        
        if (currentUserId != null) {
            LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PostLike::getUserId, currentUserId)
                   .eq(PostLike::getTargetType, "post")
                   .eq(PostLike::getTargetId, post.getId());
            vo.setLiked(postLikeMapper.selectCount(wrapper) > 0);
        } else {
            vo.setLiked(false);
        }
        
        return vo;
    }
}
