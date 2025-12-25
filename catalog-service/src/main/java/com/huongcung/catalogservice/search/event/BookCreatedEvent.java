package com.huongcung.catalogservice.search.event;

import com.huongcung.catalogservice.catalog.model.entity.BookEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event published when a book is created
 */
@Getter
public class BookCreatedEvent extends ApplicationEvent {
    
    private final BookEntity book;
    
    public BookCreatedEvent(Object source, BookEntity book) {
        super(source);
        this.book = book;
    }
}

