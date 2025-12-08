package com.BMS.backend.api;

import com.BMS.backend.domain.User;
import com.BMS.backend.exception.ApiResponse;
import com.BMS.backend.repository.UserRepository;
import com.BMS.backend.domain.Book;
import com.BMS.backend.dto.Book.BookRequestDTO;
import com.BMS.backend.dto.Book.BookResponseDTO;
import com.BMS.backend.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final UserRepository userRepository;

    private Long getUserIdFromAuth(Authentication authentication) {
        String email= (String) authentication.getPrincipal();
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new IllegalArgumentException("User not found"));
        return user.getId();
    }

    @GetMapping
    public ApiResponse<List<BookResponseDTO>> getBooks(
            Authentication authentication) {
        Long  userId = getUserIdFromAuth(authentication);
        List<BookResponseDTO> books = bookService.getMyBooks(userId)
                .stream()
                .map(BookResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ApiResponse.created(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(book -> ResponseEntity.ok(BookResponseDTO.fromEntity(book)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/books")
    public ApiResponse<BookResponseDTO> createBook(
            @RequestBody BookRequestDTO bookRequestDTO,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        Book book = bookRequestDTO.toEntity();
        Book savedBook = bookService.createBook(book, userId);

        return ApiResponse.success(new BookResponseDTO(savedBook));
    }

    /**
     * 4. 도서 수정
     * API 정의서: PUT /api/v1/books/:id
     */
    @PutMapping("/books/{id}")
    public ResponseEntity<BookResponseDTO> updateBook(
            @PathVariable Long id,
            @RequestBody BookRequestDTO bookRequestDTO,
            Authentication authentication) {
        Long  userId = getUserIdFromAuth(authentication);
        Book book = bookRequestDTO.toEntity();
        Book updatedBook = bookService.updateBook(id, book, userId);
        return ResponseEntity.ok(BookResponseDTO.fromEntity(updatedBook));
    }

    /**
     * 5. 도서 삭제
     * API 정의서: DELETE /api/v1/books/:id
     * 반환: 204 No Content
     */
    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(
            @PathVariable Long id,
            Authentication authentication) {
        Long  userId = getUserIdFromAuth(authentication);
        bookService.deleteBook(id, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 6. 표지 저장 (추가된 기능)
     * API 정의서: PUT /api/v1/books/:id/cover
     * 입력: { "coverImageUrl": "..." }
     * 주의: 이 기능을 쓰려면 BookRequestDTO와 Entity에 coverImageUrl 필드가 있어야 합니다.
     */
    @PutMapping("/books/cover/{id}")
    public ResponseEntity<BookResponseDTO> updateBookCover(
            @PathVariable Long id,
            @RequestBody Map<String, String> coverMap, // { "coverImageUrl": "url..." } 형태 받기 위함
            Authentication authentication) {
        Long  userId = getUserIdFromAuth(authentication);

        String coverImageUrl = coverMap.get("coverImageUrl");

        // 주의: Service에 updateBookCover 메서드를 새로 만드셔야 합니다!
        // 현재는 코드가 없으므로, 기존 updateBook을 응용하거나 Service에 추가해야 함을 알리는 주석입니다.
        // Book updatedBook = bookService.updateBookCover(id, coverImageUrl, userId);

        // 임시 리턴 (Service 구현 후 주석 해제하세요)
        return ResponseEntity.ok().build();
    }
}