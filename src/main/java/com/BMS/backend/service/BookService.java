package com.BMS.backend.service;

import com.BMS.backend.domain.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {

    // 모든 책 조회 (관리자용)
    List<Book> getAllBooks();

    // 내 책만 조회
    List<Book> getMyBooks(Long userId);

    // ID로 책 조회
    Optional<Book> getBookById(Long id);

    // 책 생성 (userId 포함)
    Book createBook(Book book, Long userId);

    // 책 수정 (권한 체크)
    Book updateBook(Long id, Book book, Long userId);

    // 책 삭제 (권한 체크)
    void deleteBook(Long id, Long userId);
}
