package com.BMS.backend.repository;

import com.BMS.backend.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // ISBN으로 책 찾기
    Optional<Book> findByIsbn(String isbn);

    // 제목으로 책 찾기 (부분 일치)
    Optional<Book> findByTitleContaining(String title);
}
