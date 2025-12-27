package com.huongcung.catalogservice.search.event;

import com.huongcung.catalogservice.catalog.model.entity.BookEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event published when a book is updated
 */
@Getter
public class BookUpdatedEvent extends ApplicationEvent {

    private final BookEntity book;
    
    public BookUpdatedEvent(Object source, BookEntity book) {
        super(source);
        this.book = book;
    }
}

