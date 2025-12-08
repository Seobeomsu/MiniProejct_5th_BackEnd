package com.BMS.backend.dto.Book;


import lombok.Getter;

@Getter
public class BookUpdateRequest {
    public String title;
    public String author;
    public String description;
}
