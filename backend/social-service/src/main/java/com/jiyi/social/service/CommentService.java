package com.jiyi.social.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiyi.common.exception.BusinessException;
import com.jiyi.social.dto.CommentCreateRequest;
import com.jiyi.social.dto.CommentVO;
import com.jiyi.social.entity.Comment;
import com.jiyi.social.entity.Post;
import com.jiyi.social.entity.PostLike;
import com.jiyi.social.mapper.CommentMapper;
import com.jiyi.social.mapper.PostMapper;
import com.jiyi.social.mapper.PostLikeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    
    private final CommentMapper commentMapper;
    private final PostMapper postMapper;
    private final PostLikeMapper postLikeMapper;
    
    @Transactional
    public CommentVO createComment(Long postId, Long userId, CommentCreateRequest request) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(404, "动态不存在");
        }
        
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        comment.setParentId(request.getParentId());
        comment.setLikes(0);
        comment.setStatus("published");
        
        commentMapper.insert(comment);
        
        // 更新动态评论数
        post.setComments(post.getComments() + 1);
        postMapper.updateById(post);
        
        return convertToVO(comment, userId);
    }
    
    public List<CommentVO> getComments(Long postId, Integer page, Integer size, Long currentUserId) {
        Page<Comment> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getPostId, postId)
               .isNull(Comment::getParentId)
               .orderByDesc(Comment::getCreatedAt);
        
        Page<Comment> result = commentMapper.selectPage(pageObj, wrapper);
        return result.getRecords().stream()
                .map(comment -> convertToVO(comment, currentUserId))
                .collect(Collectors.toList());
    }
    
    // 保留旧方法以兼容
    public List<CommentVO> getComments(Long postId, Integer page, Integer size) {
        return getComments(postId, page, size, null);
    }
    
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException(404, "评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权删除此评论");
        }
        
        commentMapper.deleteById(commentId);
        
        // 更新动态评论数
        Post post = postMapper.selectById(comment.getPostId());
        if (post != null && post.getComments() > 0) {
            post.setComments(post.getComments() - 1);
            postMapper.updateById(post);
        }
    }
    
    @Transactional
    public void likeComment(Long commentId, Long userId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException(404, "评论不存在");
        }
        
        // 检查是否已点赞
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getUserId, userId)
               .eq(PostLike::getTargetType, "comment")
               .eq(PostLike::getTargetId, commentId);
        
        if (postLikeMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "已经点赞过了");
        }
        
        // 创建点赞记录
        PostLike like = new PostLike();
        like.setUserId(userId);
        like.setTargetType("comment");
        like.setTargetId(commentId);
        postLikeMapper.insert(like);
        
        // 更新评论点赞数
        comment.setLikes(comment.getLikes() + 1);
        commentMapper.updateById(comment);
    }
    
    @Transactional
    public void unlikeComment(Long commentId, Long userId) {
        // 删除点赞记录
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getUserId, userId)
               .eq(PostLike::getTargetType, "comment")
               .eq(PostLike::getTargetId, commentId);
        postLikeMapper.delete(wrapper);
        
        // 更新评论点赞数
        Comment comment = commentMapper.selectById(commentId);
        if (comment != null && comment.getLikes() > 0) {
            comment.setLikes(comment.getLikes() - 1);
            commentMapper.updateById(comment);
        }
    }
    
    private CommentVO convertToVO(Comment comment, Long currentUserId) {
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setPostId(comment.getPostId());
        vo.setUserId(comment.getUserId());
        vo.setContent(comment.getContent());
        vo.setParentId(comment.getParentId());
        vo.setCreatedAt(comment.getCreatedAt());
        
        // 映射计数字段（数据库字段名 -> VO字段名）
        vo.setLikesCount(comment.getLikes());
        
        // 检查当前用户是否已点赞
        if (currentUserId != null) {
            LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PostLike::getUserId, currentUserId)
                   .eq(PostLike::getTargetType, "comment")
                   .eq(PostLike::getTargetId, comment.getId());
            vo.setLiked(postLikeMapper.selectCount(wrapper) > 0);
        } else {
            vo.setLiked(false);
        }
        
        // TODO: 从user-service获取用户信息
        vo.setUserNickname("用户" + comment.getUserId());
        vo.setUserAvatar("https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png");
        
        return vo;
    }
    
    // 保留旧方法以兼容
    private CommentVO convertToVO(Comment comment) {
        return convertToVO(comment, null);
    }
}
