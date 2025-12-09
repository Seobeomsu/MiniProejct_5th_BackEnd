package com.BMS.backend.service;

import com.BMS.backend.domain.User;
import com.BMS.backend.dto.Book.BookCreateRequest;
import com.BMS.backend.dto.Book.BookUpdateRequest;
import com.BMS.backend.exception.CustomException;
import com.BMS.backend.repository.UserRepository;
import com.BMS.backend.domain.Book;
import com.BMS.backend.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> getMyBooks(Long userId) {
        return bookRepository.findByUserId(userId);
    }

    public Book getBook(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(()-> new CustomException("Cannot find Book with id " + id, HttpStatus.NOT_FOUND));
    }

    @Transactional
    public Book createBook(BookCreateRequest request, Long userId) {
        // User 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User not found with id: " + userId, HttpStatus.NOT_FOUND));

        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .description(request.getDescription())
                .user(user)
                .build();
        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(Long id, BookUpdateRequest request, Long userId) {
        // 책이 존재하는지 확인
        Book existingBook = getVerifiedBook(id, userId);
        // 책 정보 업데이트
        existingBook.update(
                request.getTitle(),
                request.getAuthor(),
                request.getDescription()
        );
        return bookRepository.save(existingBook);
    }

    @Transactional
    public void deleteBook(Long id, Long userId) {
        Book existingBook = getVerifiedBook(id, userId);
        bookRepository.delete(existingBook);
    }

    /**
     * 책 표지 이미지 URL 업데이트
     *
     * @param id 책 ID
     * @param coverImageUrl 표지 이미지 URL
     * @param userId 사용자 ID
     * @return 업데이트된 책
     */
    @Transactional
    public Book updateBookCover(Long id, String coverImageUrl, Long userId) {
        // 1. 책 찾기 & 권한 확인
        Book existingBook = getVerifiedBook(id, userId);

        // 2. 표지 이미지 업데이트
        existingBook.updateCover(coverImageUrl);

        return bookRepository.save(existingBook);
    }

    private Book getVerifiedBook(Long id, Long userId){
        // 책이 존재하는지 확인
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new CustomException("Book not found with id: " + id,   HttpStatus.NOT_FOUND));

        // 권한 체크: 본인의 책인지 확인
        if (book.getUser() == null || !book.getUser().getId().equals(userId)) {
            throw new CustomException("You don't have permission to delete this book",  HttpStatus.FORBIDDEN);
        }
        return book;
    }
}
