package com.BMS.backend.service;

import com.BMS.backend.domain.User;
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

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    @Transactional
    public Book createBook(Book book, Long userId) {
        // User 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User not found with id: " + userId, HttpStatus.NOT_FOUND));

        // Book에 User 설정
        book.setUser(user);
        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(Long id, Book book, Long userId) {
        // 책이 존재하는지 확인
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new CustomException("Book not found with id: " + id,  HttpStatus.NOT_FOUND));

        // 권한 체크: 본인의 책인지 확인
        if (!existingBook.getUser().getId().equals(userId)) {
            throw new CustomException("You don't have permission to update this book", HttpStatus.FORBIDDEN);
        }

        // 책 정보 업데이트
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setPublishedDate(book.getPublishedDate());
        existingBook.setPrice(book.getPrice());
        existingBook.setDescription(book.getDescription());

        return bookRepository.save(existingBook);
    }

    @Transactional
    public void deleteBook(Long id, Long userId) {
        // 책이 존재하는지 확인
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new CustomException("Book not found with id: " + id,   HttpStatus.NOT_FOUND));

        // 권한 체크: 본인의 책인지 확인
        if (!existingBook.getUser().getId().equals(userId)) {
            throw new CustomException("You don't have permission to delete this book",  HttpStatus.FORBIDDEN);
        }

        bookRepository.deleteById(id);
    }

    @Transactional
    public Book updateBookCover(Long id, String coverImageUrl, Long userId) {
        // 1. 책 찾기
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new CustomException("Book not found with id: " + id,   HttpStatus.NOT_FOUND));

        // 2. 권한 체크
        if (!existingBook.getUser().getId().equals(userId)) {
            throw new CustomException("You don't have permission to update this book cover",   HttpStatus.FORBIDDEN);
        }

        // 3. 표지 이미지 업데이트
        existingBook.setCoverImageUrl(coverImageUrl);

        return bookRepository.save(existingBook);
    }
}
