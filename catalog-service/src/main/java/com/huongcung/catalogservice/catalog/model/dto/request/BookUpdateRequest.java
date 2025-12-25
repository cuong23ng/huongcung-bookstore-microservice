package com.huongcung.catalogservice.catalog.model.dto.request;

import com.huongcung.catalogservice.common.enumeration.Language;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for updating an existing book entry
 * All fields are optional for partial updates
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookUpdateRequest {
    
    @Size(min = 1, max = 500, message = "Title must be between 1 and 500 characters")
    private String title;
    
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;
    
    private Language language;
    
    private LocalDate publicationDate;
    
    private Integer pageCount;
    
    private Integer edition;
    
    private List<Long> authorIds;
    
    private List<Long> translatorIds;
    
    private Long publisherId;
    
    private List<Long> genreIds;

}


