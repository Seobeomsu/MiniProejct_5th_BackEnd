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

    // PUT update Book's Cover
    @PutMapping("/cover/{id}")
    public ResponseEntity<BookResponse> updateBookCover(
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