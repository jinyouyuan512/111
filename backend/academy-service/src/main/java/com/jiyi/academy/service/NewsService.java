package com.jiyi.academy.service;

import com.jiyi.academy.entity.News;
import java.util.List;

public interface NewsService {
    List<News> getNewsList(String category);
    News getNewsDetail(Long id);
    List<News> getTopNews();
    List<News> getHotNews();
    List<News> searchNews(String keyword);
    void incrementViewCount(Long id);
    void incrementLikeCount(Long id);
    void addNews(News news);
}
