package com.BMS.backend.repository;

import com.BMS.backend.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // 제목으로 책 찾기 (부분 일치)
    Optional<Book> findByTitleContaining(String title);

    // 특정 사용자의 모든 책 조회
    List<Book> findByUserId(Long userId);

    // 특정 사용자의 특정 책 조회 (권한 체크용)
    Optional<Book> findByIdAndUserId(Long id, Long userId);
}
