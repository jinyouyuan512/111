package com.jiyi.academy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiyi.academy.entity.News;
import com.jiyi.academy.mapper.NewsMapper;
import com.jiyi.academy.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {
    
    private final NewsMapper newsMapper;
    
    @Override
    public List<News> getNewsList(String category) {
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(News::getDeleted, 0)
               .eq(News::getStatus, "published");
        
        if (StringUtils.hasText(category) && !"all".equals(category)) {
            wrapper.eq(News::getCategory, category);
        }
        
        wrapper.orderByDesc(News::getIsTop)
               .orderByDesc(News::getPublishTime);
        
        return newsMapper.selectList(wrapper);
    }
    
    @Override
    public News getNewsDetail(Long id) {
        return newsMapper.selectById(id);
    }
    
    @Override
    public List<News> getTopNews() {
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(News::getDeleted, 0)
               .eq(News::getStatus, "published")
               .eq(News::getIsTop, true)
               .orderByDesc(News::getPublishTime);
        return newsMapper.selectList(wrapper);
    }
    
    @Override
    public List<News> getHotNews() {
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(News::getDeleted, 0)
               .eq(News::getStatus, "published")
               .eq(News::getIsHot, true)
               .orderByDesc(News::getViewCount)
               .last("LIMIT 10");
        return newsMapper.selectList(wrapper);
    }
    
    @Override
    public List<News> searchNews(String keyword) {
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(News::getDeleted, 0)
               .eq(News::getStatus, "published")
               .and(w -> w.like(News::getTitle, keyword)
                         .or()
                         .like(News::getSummary, keyword)
                         .or()
                         .like(News::getContent, keyword))
               .orderByDesc(News::getPublishTime);
        return newsMapper.selectList(wrapper);
    }
    
    @Override
    public void incrementViewCount(Long id) {
        newsMapper.incrementViewCount(id);
    }
    
    @Override
    public void incrementLikeCount(Long id) {
        newsMapper.incrementLikeCount(id);
    }
    
    @Override
    public void addNews(News news) {
        if (news.getViewCount() == null) news.setViewCount(0);
        if (news.getLikeCount() == null) news.setLikeCount(0);
        if (news.getIsTop() == null) news.setIsTop(false);
        if (news.getIsHot() == null) news.setIsHot(false);
        if (news.getStatus() == null) news.setStatus("published");
        if (news.getDeleted() == null) news.setDeleted(0);
        newsMapper.insert(news);
    }
}
