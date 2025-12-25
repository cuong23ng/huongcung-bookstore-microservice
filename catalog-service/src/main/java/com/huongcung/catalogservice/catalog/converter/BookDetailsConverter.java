package com.huongcung.catalogservice.catalog.converter;

import com.huongcung.catalogservice.catalog.model.dto.*;
import com.huongcung.catalogservice.catalog.model.dto.response.GetBookDetailsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookDetailsConverter implements Converter<BookDTO, GetBookDetailsResponse> {

    @Override
    public GetBookDetailsResponse convert(BookDTO book) {
        GetBookDetailsResponse response = new GetBookDetailsResponse();
        populate(book, response);
        return response;
    }

    private void populate(BookDTO source, GetBookDetailsResponse target) {
        target.setId(source.getId());
        target.setCode(source.getCode());
        target.setTitle(source.getTitle());
        target.setEdition(source.getEdition());
        target.setLanguage(source.getLanguage());
        target.setPageCount(source.getPageCount());
        target.setDescription(source.getDescription());

        target.setGenres(source.getGenres());

        target.setAuthors(source.getAuthors().parallelStream().map(a -> {
            AuthorDTO authorDTO = new AuthorDTO();
            authorDTO.setName(a.getName());
            return authorDTO;
        }).toList());

        target.setTranslators(source.getTranslators());

        PublisherDTO publisherDTO = new PublisherDTO();
        publisherDTO.setName(source.getPublisher().getName());
        target.setPublisher(publisherDTO);

        target.setImages(source.getImages());

        if (source.hasPhysicalEdition()) {
            target.setHasPhysicalEdition(true);
            target.setPhysicalBookInfo(source.getPhysicalBookInfo());
        }

        if (source.hasEbookEdition()) {
            target.setHasEbookEdition(true);
            target.setEbookInfo(source.getEbookInfo());
        }

        // Include review only if it exists and is PUBLISHED
//        if (source.getReview() != null && source.getReview().getStatus() == ReviewStatus.PUBLISHED) {
//            // Convert ReviewSource to ReviewSourceDTO and force load to avoid lazy initialization
//            List<ReviewSourceDTO> sourceDTOs = new ArrayList<>();
//            if (source.getReview().getSources() != null) {
//                // Force load by copying to new ArrayList
//                List<ReviewSource> sources = new ArrayList<>(source.getReview().getSources());
//                for (ReviewSource reviewSource : sources) {
//                    ReviewSourceDTO sourceDTO = ReviewSourceDTO.builder()
//                            .title(reviewSource.getTitle())
//                            .url(reviewSource.getUrl())
//                            .build();
//                    sourceDTOs.add(sourceDTO);
//                }
//            }
//
//            BookReviewDTO reviewDTO = BookReviewDTO.builder()
//                    .title(source.getReview().getTitle())
//                    .content(source.getReview().getComment())
//                    .sources(sourceDTOs)
//                    .build();
//            target.setReview(reviewDTO);
//        }
    }
}
