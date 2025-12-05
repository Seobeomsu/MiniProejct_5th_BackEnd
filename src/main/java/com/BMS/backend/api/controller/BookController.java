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
// RestController : REST API 만드는 컨트롤러 -> 프론트엔드가 http 요청으로 데이터를 주고받는 규칙
// 메서드가 리턴하는 객체를 json으로 자동 변환
// RequestMapping("/api/books") : 컨트롤러의 모든 메서드는 /api/books로 시작
// RequiredArgsConstructor : final이 붙은 필드를 자동으로 생성자에 넣어줌(Lombok)
public class BookController {

    private final BookService bookService;

    // 내 책만 조회
    // TODO: JWT에서 userId 추출하는 로직으로 변경 필요 (1번 개발자가 구현 예정)
    @GetMapping("/my")
    public ResponseEntity<List<BookResponseDTO>> getMyBooks(
            @RequestHeader("X-User-Id") Long userId) {
        List<BookResponseDTO> books = bookService.getMyBooks(userId)
                .stream()
                .map(BookResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(books);
    }
    //RequestHeader("X-User-Id") Long userId : http 헤더에서 X-User-Id 값을 가져와 usedId 변수에 저장
    //List<BookResponseDTO> books = bookService.getMyBooks(userId) : Service에게 책 목록 가져고 books에 저장
    //stream : 리스트 to 스트림 변환
    //map ~ :  Book을 BookResponseDTO로 변환
    //collect : 다시 리스트로 모음


    // 모든 책 조회 (관리자용)
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
    //PathVariable Long id : url 경로에 있는 id 값을 가져옴
    //bookService.getBookById(id) : Optional<Book> 리턴
    // 책이 있으면 map : ok + 책 정보
    // 책이 없으면 orElse : not found

    // 새 책 생성
    // TODO: JWT에서 userId 추출하는 로직으로 변경 필요 (1번 개발자가 구현 예정)
    @PostMapping
    public ResponseEntity<BookResponseDTO> createBook(
            @RequestBody BookRequestDTO bookRequestDTO,
            @RequestHeader("X-User-Id") Long userId) {
        Book book = bookRequestDTO.toEntity();
        Book savedBook = bookService.createBook(book, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BookResponseDTO.fromEntity(savedBook));
    }
    //equestBody BookRequestDTO bookRequestDTO : http 요청 본문(body)에 있는 json -> BookRequestDTO 객체로 변환
    //동작 순서 :
    // 1. DTO -> Entity변환
    // 2. Service에 책 생성 요청
    // 3. HTTOP 201 Created 상태 코드와 함께 생성된 책 정보 리턴


    // 책 수정 (권한 체크 포함)
    // TODO: JWT에서 userId 추출하는 로직으로 변경 필요 (1번 개발자가 구현 예정)
    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDTO> updateBook(
            @PathVariable Long id,
            @RequestBody BookRequestDTO bookRequestDTO,
            @RequestHeader("X-User-Id") Long userId) {
        Book book = bookRequestDTO.toEntity();
        Book updatedBook = bookService.updateBook(id, book, userId);
        return ResponseEntity.ok(BookResponseDTO.fromEntity(updatedBook));
    }

    // 책 삭제 (권한 체크 포함)
    // TODO: JWT에서 userId 추출하는 로직으로 변경 필요 (1번 개발자가 구현 예정)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        bookService.deleteBook(id, userId);
        return ResponseEntity.noContent().build();
    }
}
