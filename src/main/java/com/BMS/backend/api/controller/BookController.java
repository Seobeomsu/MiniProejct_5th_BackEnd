package com.BMS.backend.api.controller;

import com.BMS.backend.domain.Book;
import com.BMS.backend.dto.BookRequestDTO;
import com.BMS.backend.dto.BookResponseDTO;
import com.BMS.backend.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // 모든 책 조회
    @GetMapping
    public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
        List<BookResponseDTO> books = bookService.getAllBooks()
                .stream()
                .map(BookResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(books);
    }

    // 특정 책 조회 (ID)
    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(book -> ResponseEntity.ok(BookResponseDTO.fromEntity(book)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ISBN으로 책 조회
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookResponseDTO> getBookByIsbn(@PathVariable String isbn) {
        return bookService.getBookByIsbn(isbn)
                .map(book -> ResponseEntity.ok(BookResponseDTO.fromEntity(book)))
                .orElse(ResponseEntity.notFound().build());
    }

    // 새 책 생성
    @PostMapping
    public ResponseEntity<BookResponseDTO> createBook(@RequestBody BookRequestDTO bookRequestDTO) {
        Book book = bookRequestDTO.toEntity();
        Book savedBook = bookService.createBook(book);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BookResponseDTO.fromEntity(savedBook));
    }

    // 책 수정
    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDTO> updateBook(
            @PathVariable Long id,
            @RequestBody BookRequestDTO bookRequestDTO) {
        try {
            Book book = bookRequestDTO.toEntity();
            Book updatedBook = bookService.updateBook(id, book);
            return ResponseEntity.ok(BookResponseDTO.fromEntity(updatedBook));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 책 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
