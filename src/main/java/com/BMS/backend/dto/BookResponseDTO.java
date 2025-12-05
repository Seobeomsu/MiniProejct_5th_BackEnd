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
public class BookResponseDTO {

    private Long id;
    private String title;
    private String author;
    private LocalDate publishedDate;
    private Integer price;
    private String description;
    private Long userId; // 책 소유자 ID

    // Entity to DTO
    public static BookResponseDTO fromEntity(Book book) {
        BookResponseDTO dto = new BookResponseDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setPublishedDate(book.getPublishedDate());
        dto.setPrice(book.getPrice());
        dto.setDescription(book.getDescription());
        dto.setUserId(book.getUser() != null ? book.getUser().getId() : null);
        return dto;
    }
}
