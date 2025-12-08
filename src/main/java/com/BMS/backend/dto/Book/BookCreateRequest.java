package com.BMS.backend.dto.Book;

import com.BMS.backend.domain.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class BookCreateRequest {

    private String title;
    private String author;
    private String description;
}
