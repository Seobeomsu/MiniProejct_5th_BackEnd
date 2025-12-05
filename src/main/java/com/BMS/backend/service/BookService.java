package com.BMS.backend.service;

import com.BMS.backend.domain.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {

    // 모든 책 조회
    List<Book> getAllBooks();

    // ID로 책 조회
    Optional<Book> getBookById(Long id);

    // ISBN으로 책 조회
    Optional<Book> getBookByIsbn(String isbn);

    // 책 생성
    Book createBook(Book book);

    // 책 수정
    Book updateBook(Long id, Book book);

    // 책 삭제
    void deleteBook(Long id);
}
