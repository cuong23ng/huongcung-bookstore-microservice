package com.huongcung.catalogservice.catalog.listener;

import com.huongcung.catalogservice.catalog.model.entity.BookEntity;
import com.huongcung.catalogservice.search.event.BookCreatedEvent;
import com.huongcung.catalogservice.search.event.BookDeletedEvent;
import com.huongcung.catalogservice.search.event.BookUpdatedEvent;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

/**
 * JPA Entity Listener for BookEntity
 * Publishes Spring events when books are created, updated, or deleted
 * 
 * Note: This listener is registered via META-INF/orm.xml
 * Since JPA entity listeners are not Spring-managed, we use a static reference
 * to ApplicationEventPublisher that is initialized by BookEntityListenerInitializer
 */
@Slf4j
public class BookEntityListener {

    /**
     * -- SETTER --
     *  Set the ApplicationEventPublisher (called by BookEntityListenerInitializer)
     */
    @Setter
    private static ApplicationEventPublisher eventPublisher;

    /**
     * Called after an AbstractBookEntity is persisted (created)
     */
    @PostPersist
    public void postPersist(BookEntity book) {
        if (eventPublisher != null && book != null) {
            log.debug("AbstractBookEntity persisted, publishing BookCreatedEvent for book ID: {}", book.getId());
            eventPublisher.publishEvent(new BookCreatedEvent(this, book));
        } else if (eventPublisher == null) {
            log.warn("ApplicationEventPublisher not initialized in BookEntityListener");
        }
    }
    
    /**
     * Called after an AbstractBookEntity is updated
     */
    @PostUpdate
    public void postUpdate(BookEntity book) {
        if (eventPublisher != null && book != null) {
            log.debug("AbstractBookEntity updated, publishing BookUpdatedEvent for book ID: {}", book.getId());
            eventPublisher.publishEvent(new BookUpdatedEvent(this, book));
        } else if (eventPublisher == null) {
            log.warn("ApplicationEventPublisher not initialized in BookEntityListener");
        }
    }
    
    /**
     * Called after an AbstractBookEntity is removed (deleted)
     */
    @PostRemove
    public void postRemove(BookEntity book) {
        if (eventPublisher != null && book != null) {
            log.debug("AbstractBookEntity removed, publishing BookDeletedEvent for book ID: {}", book.getId());
            eventPublisher.publishEvent(new BookDeletedEvent(this, book.getId()));
        } else if (eventPublisher == null) {
            log.warn("ApplicationEventPublisher not initialized in BookEntityListener");
        }
    }
}

