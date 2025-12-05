package com.BMS.backend.service;

import com.BMS.backend.domain.Book;
import com.BMS.backend.domain.User;
import com.BMS.backend.exception.ForbiddenException;
import com.BMS.backend.exception.ResourceNotFoundException;
import com.BMS.backend.repository.BookRepository;
import com.BMS.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public List<Book> getMyBooks(Long userId) {
        return bookRepository.findByUserId(userId);
    }

    @Override
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    @Transactional
    public Book createBook(Book book, Long userId) {
        // User 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Book에 User 설정
        book.setUser(user);
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book updateBook(Long id, Book book, Long userId) {
        // 책이 존재하는지 확인
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        // 권한 체크: 본인의 책인지 확인
        if (!existingBook.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to update this book");
        }

        // 책 정보 업데이트
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setPublishedDate(book.getPublishedDate());
        existingBook.setPrice(book.getPrice());
        existingBook.setDescription(book.getDescription());

        return bookRepository.save(existingBook);
    }

    @Override
    @Transactional
    public void deleteBook(Long id, Long userId) {
        // 책이 존재하는지 확인
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        // 권한 체크: 본인의 책인지 확인
        if (!existingBook.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to delete this book");
        }

        bookRepository.deleteById(id);
    }
}
