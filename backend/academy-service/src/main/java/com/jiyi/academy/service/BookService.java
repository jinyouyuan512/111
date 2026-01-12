package com.jiyi.academy.service;

import com.jiyi.academy.entity.Book;
import java.util.List;

public interface BookService {
    List<Book> getBookList(String category);
    Book getBookDetail(Long id);
    List<Book> getRecommendedBooks();
    List<Book> searchBooks(String keyword);
    void incrementReadCount(Long id);
    void incrementFavoriteCount(Long id);
    void addBook(Book book);
}
