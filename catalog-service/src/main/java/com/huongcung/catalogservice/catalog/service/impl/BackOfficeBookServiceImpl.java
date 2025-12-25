package com.huongcung.catalogservice.catalog.service.impl;

import com.huongcung.catalogservice.catalog.converter.BookDetailsConverter;
import com.huongcung.catalogservice.catalog.enumeration.BookType;
import com.huongcung.catalogservice.catalog.utils.BookUtils;
import com.huongcung.catalogservice.common.enumeration.City;
import com.huongcung.catalogservice.common.enumeration.Language;
import com.huongcung.catalogservice.catalog.model.dto.AuthorListDTO;
import com.huongcung.catalogservice.catalog.model.dto.BookDTO;
import com.huongcung.catalogservice.catalog.model.dto.BookListDTO;
import com.huongcung.catalogservice.catalog.model.dto.request.BookCreateRequest;
import com.huongcung.catalogservice.catalog.model.dto.request.BookUpdateRequest;
import com.huongcung.catalogservice.catalog.model.dto.request.EbookUpdateRequest;
import com.huongcung.catalogservice.catalog.model.dto.request.PhysicalBookUpdateRequest;
import com.huongcung.catalogservice.catalog.model.dto.response.GetBookCatalogPageResponse;
import com.huongcung.catalogservice.catalog.model.entity.*;
import com.huongcung.catalogservice.catalog.repository.*;
import com.huongcung.catalogservice.catalog.service.BackOfficeBookService;
import com.huongcung.catalogservice.catalog.service.BookService;
import com.huongcung.catalogservice.common.dto.PaginationInfo;
import com.huongcung.catalogservice.media.enumeration.MediaStatus;
import com.huongcung.catalogservice.media.model.dto.request.BulkUploadRequest;
import com.huongcung.catalogservice.media.model.dto.request.UploadRequest;
import com.huongcung.catalogservice.media.model.dto.response.BulkUploadResponse;
import com.huongcung.catalogservice.media.model.dto.response.UploadResponse;
import com.huongcung.catalogservice.media.service.ImageService;
import com.huongcung.catalogservice.search.model.dto.SearchRequest;
import com.huongcung.catalogservice.search.model.dto.SearchResponse;
import com.huongcung.catalogservice.search.service.SearchIndexService;
import com.huongcung.catalogservice.search.service.SearchService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.huongcung.catalogservice.media.constant.FolderConstants.BOOKS;
import static com.huongcung.catalogservice.media.constant.FolderConstants.IMAGES;

@Service
@RequiredArgsConstructor
@Slf4j
public class BackOfficeBookServiceImpl implements BackOfficeBookService {

    private final BookRepository bookRepository;
    private final BookService bookService;
    private final ImageService imageService;
    private final SearchIndexService searchIndexService;
    private final PhysicalBookRepository physicalBookRepository;
    private final EbookRepository ebookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final TranslatorRepository translatorRepository;
    private final GenreRepository genreRepository;
    private final SearchService searchService;

    @Override
    public GetBookCatalogPageResponse getOrSearchBooks(Pageable pageable, String q, List<String> genres, List<Language> languages, List<BookType> bookTypes, List<City> cities, String sort) {

        log.info("Search request - query: '{}', filters: genre={}, language={}, format={}, city={}, page={}, size={}, sort={}",
                q, genres, languages, bookTypes, cities, pageable.getPageNumber(), pageable.getPageSize(), sort);

        SearchRequest request = SearchRequest.builder()
                .q(q)
                .genres(genres)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .sort(sort)
                .build();

        if (languages != null) request.setLanguages(languages.stream().map(Language::name).toList());
        if (cities != null) request.setLanguages(cities.stream().map(City::name).toList());
        if (bookTypes != null) request.setFormats(bookTypes.stream().map(BookType::name).toList());

        SearchResponse searchResponse = searchService.searchBooks(request);

        List<BookListDTO> bookDTOs = searchResponse.getBooks().stream()
                .map(b -> {
                    BookListDTO bookListDTO = new BookListDTO();
                    populate(b, bookListDTO);
                    return bookListDTO;
                })
                .collect(Collectors.toList());

        log.info("Found {} books (page {} of {}) in {}ms",
                searchResponse.getPagination().getTotalResults(),
                searchResponse.getPagination().getCurrentPage(),
                searchResponse.getPagination().getTotalPages(),
                searchResponse.getExecutionTimeMs());

        return GetBookCatalogPageResponse.builder()
                .books(bookDTOs)
                .pagination(searchResponse.getPagination())
                .facets(searchResponse.getFacets())
                .highlightedFields(searchResponse.getHighlightedFields())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BookDTO getBookById(Long bookId) {
        log.debug("Fetching book by ID: {}", bookId);
        BookDTO book = bookService.findById(bookId);
        return book;
    }

    @Override
    @Transactional
    public void createBook(BookCreateRequest request) {
        log.info("Creating book: title={}, physical={}, ebook={}",
                request.getTitle(), request.getHasPhysicalEdition(), request.getHasElectricEdition());

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }

        if (request.getAuthorIds() == null || request.getAuthorIds().isEmpty()) {
            throw new IllegalArgumentException("At least one author is required");
        }

        BookEntity book = new BookEntity();
        populate(request, book);

        BookEntity savedBook = bookRepository.save(book);

        List<BookEntity> savedBooks = new ArrayList<>();
        savedBooks.add(savedBook);

        if (request.getHasPhysicalEdition()) {
            PhysicalBookEntity physicalBook = new PhysicalBookEntity();

            physicalBook.setPublicationDate(request.getPublicationDate());
            physicalBook.setIsbn(request.getIsbn());
            physicalBook.setWeightGrams(request.getWeightGrams());
            physicalBook.setHeightCm(request.getHeightCm());
            physicalBook.setWidthCm(request.getWidthCm());
            physicalBook.setLengthCm(request.getLengthCm());
            physicalBook.setCurrentPrice(request.getPhysicalBookPrice());
            physicalBook.setCoverType(request.getCoverType());

            physicalBook.setBook(savedBook);
            physicalBook.setSku(BookUtils.generatePhysicalBookSku(physicalBook));
            savedBook.setPhysicalBookInfo(physicalBook);

            physicalBookRepository.save(physicalBook);
        }

        if (request.getHasElectricEdition()) {
            EbookEntity ebook = new EbookEntity();

            ebook.setPublicationDate(request.getPublicationDate());
            ebook.setCurrentPrice(request.getEbookPrice());
            if (request.getEisbn() != null) {
                ebook.setIsbn(request.getEisbn());
            }
            ebook.setBook(savedBook);
            ebook.setSku(BookUtils.generateEbookFileSku(ebook));
            savedBook.setEbookInfo(ebook);

            ebookRepository.save(ebook);
        }

        if (searchIndexService != null) {
            searchIndexService.indexBook(savedBook);
            log.debug("Book indexed in search service: {}", savedBook.getId());
        }

        if (!CollectionUtils.isEmpty(request.getImages())) {
            imageService.saveBookImagesFromBase64(book, request.getImages());
        }
    }

    @Override
    @Transactional
    public void updateBook(Long bookId, BookUpdateRequest request) {
        log.info("Updating book ID: {}", bookId);

        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + bookId));

        // Track changes for audit logging
        StringBuilder changes = new StringBuilder();

        if (request.getTitle() != null) {
            changes.append("title updated; ");
            book.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            changes.append("description updated; ");
            book.setDescription(request.getDescription());
        }
        if (request.getLanguage() != null) {
            changes.append("language updated; ");
            book.setLanguage(request.getLanguage());
        }
        if (request.getPageCount() != null) {
            changes.append("pageCount updated; ");
            book.setPageCount(request.getPageCount());
        }
        if (request.getEdition() != null) {
            changes.append("edition updated; ");
            book.setEdition(request.getEdition());
        }

        if (request.getAuthorIds() != null) {
            List<AuthorEntity> authors = authorRepository.findByIdIn(request.getAuthorIds());
            if (authors.size() != request.getAuthorIds().size()) {
                throw new RuntimeException("One or more author IDs not found");
            }
            if (!Objects.equals(book.getAuthors(), authors)) {
                changes.append("authors updated; ");
                book.setAuthors(authors);
            }
        }

        if (request.getTranslatorIds() != null) {
            List<TranslatorEntity> translators = translatorRepository.findByIdIn(request.getTranslatorIds());
            if (translators.size() != request.getTranslatorIds().size()) {
                throw new RuntimeException("One or more translator IDs not found");
            }
            if (!Objects.equals(book.getTranslators(), translators)) {
                changes.append("translators updated; ");
                book.setTranslators(translators);
            }
        }

        if (request.getPublisherId() != null) {
            PublisherEntity publisher = publisherRepository.findById(request.getPublisherId())
                    .orElseThrow(() -> new RuntimeException("Publisher not found with ID: " + request.getPublisherId()));
            if (!Objects.equals(book.getPublisher() != null ? book.getPublisher().getId() : null, publisher.getId())) {
                changes.append("publisher updated; ");
                book.setPublisher(publisher);
            }
        }

        if (request.getGenreIds() != null) {
            List<GenreEntity> genres = genreRepository.findByIdIn(request.getGenreIds());
            if (genres.size() != request.getGenreIds().size()) {
                throw new RuntimeException("One or more genre IDs not found");
            }
            if (!Objects.equals(book.getGenres(), genres)) {
                changes.append("genres updated; ");
                book.setGenres(genres);
            }
        }

        bookRepository.save(book);

        // Audit logging
        String changeLog = !changes.isEmpty() ? changes.toString() : "no changes";
        log.info("Book updated: bookId={}, changes={}, timestamp={}",
                bookId, changeLog, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void createOrUpdatePhysicalInfo(Long bookId, PhysicalBookUpdateRequest request) {

        log.info("Updating book ID: {}", bookId);

        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + bookId));

        // Track changes for audit logging
        StringBuilder changes = new StringBuilder();

        PhysicalBookEntity physicalBook = book.getPhysicalBookInfo();

        if (physicalBook == null) {
            physicalBook = new PhysicalBookEntity();
        }

        if (request.getIsbn() != null) {
            changes.append("isbn updated; ");
            physicalBook.setIsbn(request.getIsbn());
        }
        if (request.getCoverType() != null) {
            changes.append("coverType updated; ");
            physicalBook.setCoverType(request.getCoverType());
        }
        if (request.getWeightGrams() != null) {
            changes.append("weightGrams updated; ");
            physicalBook.setWeightGrams(request.getWeightGrams());
        }
        if (request.getHeightCm() != null) {
            changes.append("heightCm updated; ");
            physicalBook.setHeightCm(request.getHeightCm());
        }
        if (request.getWidthCm() != null) {
            changes.append("widthCm updated; ");
            physicalBook.setWidthCm(request.getWidthCm());
        }
        if (request.getLengthCm() != null) {
            changes.append("lengthCm updated; ");
            physicalBook.setLengthCm(request.getLengthCm());
        }
        if (request.getCurrentPrice() != null) {
            changes.append("currentPrice updated; ");
            physicalBook.setCurrentPrice(request.getCurrentPrice());
        }
        if (request.getPublicationDate() != null) {
            changes.append("publicationDate updated; ");
            physicalBook.setPublicationDate(request.getPublicationDate());
        }
        physicalBook.setBook(book);
        physicalBookRepository.save(physicalBook);

        if (book.getPhysicalBookInfo() == null) {
            book.setPhysicalBookInfo(physicalBook);
            bookRepository.save(book);
        }

        // Audit logging
        String changeLog = !changes.isEmpty() ? changes.toString() : "no changes";
        log.info("Physical Book updated: bookId={}, changes={}, timestamp={}",
                bookId, changeLog, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void createOrUpdateEbookInfo(Long bookId, EbookUpdateRequest request) {
        log.info("Updating book ID: {}", bookId);

        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + bookId));

        // Track changes for audit logging
        StringBuilder changes = new StringBuilder();

        EbookEntity ebook = book.getEbookInfo();

        if (ebook == null) {
            ebook = new EbookEntity();
        }

        if (request.getIsbn() != null) {
            changes.append("isbn updated; ");
            ebook.setIsbn(request.getIsbn());
        }
        if (request.getCurrentPrice() != null) {
            changes.append("currentPrice updated; ");
            ebook.setCurrentPrice(request.getCurrentPrice());
        }
        if (request.getPublicationDate() != null) {
            changes.append("publicationDate updated; ");
            ebook.setPublicationDate(request.getPublicationDate());
        }
        ebook.setBook(book);
        ebookRepository.save(ebook);

        if (book.getEbookInfo() == null) {
            book.setEbookInfo(ebook);
            bookRepository.save(book);
        }

        // Audit logging
        String changeLog = !changes.isEmpty() ? changes.toString() : "no changes";
        log.info("Ebook Book updated: bookId={}, changes={}, timestamp={}",
                bookId, changeLog, LocalDateTime.now());
    }

    @Override
    @Deprecated
    public void uploadBookImages(Long bookId, MultipartFile[] files) {
        log.info("Uploading {} images for book ID: {}", files.length, bookId);

        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + bookId));

        String folderPath = IMAGES + "/" + BOOKS;
        List<BookImageEntity> savedImages = imageService.saveBookImages(book, files, folderPath);
        book.setImages(savedImages);
        bookRepository.save(book);
    }

    @Override
    public UploadResponse prepareImageUpload(Long bookId, Integer position, String fileName, String contentType) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + bookId));
        BookImageEntity bookImageEntity = new BookImageEntity();
        bookImageEntity.setBook(book);
        bookImageEntity.setStatus(MediaStatus.PENDING);
        bookImageEntity.setPosition(position);
        //bookImageRepository.save(bookImageEntity);
        String folderPath = IMAGES + "/" + BOOKS;
        return imageService.prepareUpload(fileName, position, folderPath, contentType);
    }

    @Override
    public BulkUploadResponse prepareImagesUpload(Long bookId, BulkUploadRequest uploadRequest) {
        List<UploadRequest> uploadFiles = uploadRequest.getUploadFiles();
        List<UploadResponse> uploadedFiles = uploadFiles.parallelStream()
                .map(u -> prepareImageUpload(bookId, u.getId(), u.getFileName(), u.getContentType()))
                .toList();
        return BulkUploadResponse.builder()
                .uploadedFiles(uploadedFiles)
                .build();
    }


    // HELPER
    private void populate(BookDTO source, BookListDTO target) {
        target.setId(source.getId());
        target.setCode(source.getCode());
        target.setLanguage(source.getLanguage());
        target.setTitle(source.getTitle());
        List<AuthorListDTO> authors = source.getAuthors()
                .parallelStream()
                .map(a -> {
                    AuthorListDTO author = new AuthorListDTO();
                    author.setId(a.getId());
                    author.setName(a.getName());
                    return author;
                }).toList();
        target.setAuthors(authors);
        target.setCreatedAt(source.getCreatedAt());
        target.setHasEbookEdition(source.getEbookInfo() != null);
        target.setHasPhysicalEdition(source.getPhysicalBookInfo() != null);
    }

    private void populate(BookCreateRequest source, BookEntity target) {
        // Generate unique book code
        String bookCode = generateBookCode(source.getTitle(), source.getEdition());

        // Load related entities
        List<AuthorEntity> authors = authorRepository.findByIdIn(source.getAuthorIds());
        if (authors.size() != source.getAuthorIds().size()) {
            throw new RuntimeException("One or more author IDs not found");
        }

        List<TranslatorEntity> translators = null;
        if (source.getTranslatorIds() != null && !source.getTranslatorIds().isEmpty()) {
            translators = translatorRepository.findByIdIn(source.getTranslatorIds());
            if (translators.size() != source.getTranslatorIds().size()) {
                throw new RuntimeException("One or more translator IDs not found");
            }
        }

        PublisherEntity publisher = null;
        if (source.getPublisherId() != null) {
            publisher = publisherRepository.findById(source.getPublisherId())
                    .orElseThrow(() -> new RuntimeException("Publisher not found with ID: " + source.getPublisherId()));
        }

        List<GenreEntity> genres = null;
        if (source.getGenreIds() != null && !source.getGenreIds().isEmpty()) {
            genres = genreRepository.findByIdIn(source.getGenreIds());
            if (genres.size() != source.getGenreIds().size()) {
                throw new RuntimeException("One or more genre IDs not found");
            }
        }

        target.setCode(bookCode);
        target.setTitle(source.getTitle());
        target.setDescription(source.getDescription());
        target.setLanguage(source.getLanguage());
        target.setPageCount(source.getPageCount());
        target.setEdition(source.getEdition());
        target.setAuthors(authors);
        target.setTranslators(translators);
        target.setPublisher(publisher);
        target.setGenres(genres);
    }

    private String generateBookCode(String title, Integer edition) {

        // Normalize Vietnamese characters (remove diacritics)
        String normalized = Normalizer.normalize(title, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String withoutDiacritics = pattern.matcher(normalized).replaceAll("");

        return withoutDiacritics
                .toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("\\s+", "-")  // Replace multiple spaces with single hyphen
                .replaceAll("[^a-z0-9-]", "")  // Remove special characters except hyphens
                .replaceAll("-+", "-")  // Replace multiple hyphens with single hyphen
                .replaceAll("^-|-$", "")
                .concat("-" + edition);
    }
}
