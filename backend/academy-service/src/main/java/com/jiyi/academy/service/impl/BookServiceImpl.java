package com.jiyi.academy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiyi.academy.entity.Book;
import com.jiyi.academy.mapper.BookMapper;
import com.jiyi.academy.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    
    private final BookMapper bookMapper;
    
    @Override
    public List<Book> getBookList(String category) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getDeleted, 0)
               .eq(Book::getStatus, "active");
        
        if (StringUtils.hasText(category) && !"all".equals(category)) {
            wrapper.eq(Book::getCategory, category);
        }
        
        wrapper.orderByDesc(Book::getIsRecommended)
               .orderByDesc(Book::getRating);
        
        return bookMapper.selectList(wrapper);
    }
    
    @Override
    public Book getBookDetail(Long id) {
        return bookMapper.selectById(id);
    }
    
    @Override
    public List<Book> getRecommendedBooks() {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getDeleted, 0)
               .eq(Book::getStatus, "active")
               .eq(Book::getIsRecommended, true)
               .orderByDesc(Book::getRating)
               .last("LIMIT 10");
        return bookMapper.selectList(wrapper);
    }
    
    @Override
    public List<Book> searchBooks(String keyword) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getDeleted, 0)
               .eq(Book::getStatus, "active")
               .and(w -> w.like(Book::getTitle, keyword)
                         .or()
                         .like(Book::getAuthor, keyword)
                         .or()
                         .like(Book::getDescription, keyword)
                         .or()
                         .like(Book::getTags, keyword))
               .orderByDesc(Book::getRating);
        return bookMapper.selectList(wrapper);
    }
    
    @Override
    public void incrementReadCount(Long id) {
        bookMapper.incrementReadCount(id);
    }
    
    @Override
    public void incrementFavoriteCount(Long id) {
        bookMapper.incrementFavoriteCount(id);
    }
    
    @Override
    public void addBook(Book book) {
        if (book.getRating() == null) book.setRating(new java.math.BigDecimal("0"));
        if (book.getRatingCount() == null) book.setRatingCount(0);
        if (book.getReadCount() == null) book.setReadCount(0);
        if (book.getFavoriteCount() == null) book.setFavoriteCount(0);
        if (book.getIsRecommended() == null) book.setIsRecommended(false);
        if (book.getHasEbook() == null) book.setHasEbook(false);
        if (book.getStatus() == null) book.setStatus("active");
        if (book.getDeleted() == null) book.setDeleted(0);
        bookMapper.insert(book);
    }
}
