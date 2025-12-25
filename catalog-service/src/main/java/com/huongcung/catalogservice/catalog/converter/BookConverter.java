package com.huongcung.catalogservice.catalog.converter;

import com.huongcung.catalogservice.catalog.model.dto.*;
import com.huongcung.catalogservice.catalog.model.dto.response.BookFrontPageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookConverter implements Converter<BookDTO, BookFrontPageDTO> {

    @Override
    public BookFrontPageDTO convert(BookDTO book) {
        BookFrontPageDTO bookFrontPageDTO = new BookFrontPageDTO();
        populate(book, bookFrontPageDTO);
        return bookFrontPageDTO;
    }

    private void populate(BookDTO source, BookFrontPageDTO target) {
        target.setCode(source.getCode());
        target.setTitle(source.getTitle());
        target.setAuthors(source.getAuthors().parallelStream().map(a -> {
            AuthorDTO authorDTO = new AuthorDTO();
            authorDTO.setName(a.getName());
            return authorDTO;
        }).toList());
        target.setCoverUrl(source.getImages()
                .stream()
                .filter(BookImageDTO::isCover)
                .map(BookImageDTO::getUrl)
                .findFirst().orElse(null)
        );

        // Handle physical book price - may be null if book is ebook-only
        PhysicalBookInformationDTO physicalInfo = source.getPhysicalBookInfo();
        log.info("PhysicalBookInformation {}", physicalInfo == null);
        if (physicalInfo != null) {
            target.setPhysicalPrice(physicalInfo.getCurrentPrice());
        } else {
            target.setPhysicalPrice(null);
        }

        // Handle ebook price - may be null if book is physical-only
        EbookInformationDTO ebookInfo = source.getEbookInfo();
        if (ebookInfo != null) {
            target.setEbookPrice(ebookInfo.getCurrentPrice());
        } else {
            target.setEbookPrice(null);
        }
    }
}
