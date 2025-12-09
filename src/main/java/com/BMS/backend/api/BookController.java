package com.BMS.backend.api;

import com.BMS.backend.domain.User;
import com.BMS.backend.dto.Book.BookUpdateRequest;
import com.BMS.backend.exception.ApiResponse;
import com.BMS.backend.exception.CustomException;
import com.BMS.backend.repository.UserRepository;
import com.BMS.backend.domain.Book;
import com.BMS.backend.dto.Book.BookCreateRequest;
import com.BMS.backend.dto.Book.BookResponse;
import com.BMS.backend.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final UserRepository userRepository;
    private final com.BMS.backend.service.CoverGenerationService coverGenerationService;

    private Long getUserIdFromAuth(Authentication authentication) {
        String email= (String) authentication.getPrincipal();
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new CustomException("User not found", HttpStatus.NOT_FOUND));
        return user.getId();
    }

    // GET ALL Books
    @GetMapping
    public ApiResponse<List<BookResponse>> getAllBooks(){
        List<Book> list = bookService.getAllBooks();
        return ApiResponse.success(list.stream().map(BookResponse::new).toList());
    }

    // GET Users Books
    @GetMapping("/user")
    public ApiResponse<List<BookResponse>> getBooksById(
            Authentication authentication
    ){
        Long  userId = getUserIdFromAuth(authentication);
        List<Book> list= bookService.getMyBooks(userId);
        return ApiResponse.success(list.stream().map(BookResponse::new).toList());
    }

    // GET {id} Book's Info
    @GetMapping("/{id}")
    public ApiResponse<BookResponse> getBookById(@PathVariable Long id){
        Book book = bookService.getBook(id);
        return ApiResponse.success(new BookResponse(book));
    }

    // POST create Book
    @PostMapping
    public ApiResponse<BookResponse> createBook(
            @RequestBody BookCreateRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        Book savedBook = bookService.createBook(request, userId);
        return ApiResponse.success(new BookResponse(savedBook));
    }

    // PUT update Book
    @PutMapping("/{id}")
    public ApiResponse<BookResponse> updateBook(
            @PathVariable Long id,
            @RequestBody BookUpdateRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        Book updated = bookService.updateBook(id, request, userId);
        return ApiResponse.success(new BookResponse(updated));
    }

    // Delete Book
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteBook(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        bookService.deleteBook(id, userId);
        return ApiResponse.success(null);
    }

    /**
     * 표지 이미지 URL 수동 업데이트
     * PUT /api/v1/books/cover/{id}
     * Body: { "coverImageUrl": "https://..." }
     */
    @PutMapping("/cover/{id}")
    public ApiResponse<BookResponse> updateBookCover(
            @PathVariable Long id,
            @RequestBody Map<String, String> coverMap,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        String coverImageUrl = coverMap.get("coverImageUrl");

        Book updatedBook = bookService.updateBookCover(id, coverImageUrl, userId);
        return ApiResponse.success(new BookResponse(updatedBook));
    }

    /**
     * AI로 표지 이미지 자동 생성
     * POST /api/v1/books/{id}/generate-cover
     */
    @PostMapping("/{id}/generate-cover")
    public ApiResponse<BookResponse> generateBookCover(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);

        // 1️⃣ 책 정보 조회
        Book book = bookService.getBook(id);

        // 2️⃣ 권한 확인 (본인의 책인지)
        if (!book.getUser().getId().equals(userId)) {
            throw new CustomException("You don't have permission to generate cover for this book", HttpStatus.FORBIDDEN);
        }

        // 3️⃣ AI로 표지 생성 (DALL-E API 호출 + 로컬 저장)
        String generatedCoverUrl = coverGenerationService.generateAndSaveBookCover(
                book.getTitle(),
                book.getAuthor()
        );

        // 4️⃣ DB에 저장
        Book updatedBook = bookService.updateBookCover(id, generatedCoverUrl, userId);

        return ApiResponse.success(new BookResponse(updatedBook));
    }
}