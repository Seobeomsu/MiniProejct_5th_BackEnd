package com.BMS.backend.dto;

import com.BMS.backend.domain.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDTO {

    private String title;
    private String author;
    private LocalDate publishedDate;
    private String description;

    // DTO to Entity
    public Book toEntity() {
        Book book = new Book();
        book.setTitle(this.title);
        book.setAuthor(this.author);
        book.setPublishedDate(this.publishedDate);
        book.setDescription(this.description);
        return book;
    }
}
