package com.BMS.backend.dto.Book;

import com.BMS.backend.domain.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {

    private Long id;
    private Long userId; // 책 소유자 ID(FK)
    private String title;
    private String author;
    private String description;
    private String bookCoverUrl;

    // Entity to DTO
    public BookResponse(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.userId = book.getUser().getId();
        this.author = book.getAuthor();
        this.description = book.getDescription();
        this.bookCoverUrl = book.getBookCoverUrl();
    }
}
