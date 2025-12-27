package com.huongcung.inventoryservice.service.impl;

import com.huongcung.inventoryservice.common.enumeration.City;
import com.huongcung.inventoryservice.common.model.dto.PaginationInfo;
import com.huongcung.inventoryservice.enumeration.StockStatus;
import com.huongcung.inventoryservice.mapper.BookMapper;
import com.huongcung.inventoryservice.mapper.StockAdjustmentMapper;
import com.huongcung.inventoryservice.model.dto.StockAdjustmentDTO;
import com.huongcung.inventoryservice.model.dto.request.StockAdjustmentRequest;
import com.huongcung.inventoryservice.model.dto.request.StockInitRequest;
import com.huongcung.inventoryservice.model.dto.response.StockAdjustmentResponse;
import com.huongcung.inventoryservice.model.dto.response.StockLevelResponse;
import com.huongcung.inventoryservice.model.entity.BookEntity;
import com.huongcung.inventoryservice.model.entity.StockAdjustmentEntity;
import com.huongcung.inventoryservice.model.entity.StockLevelEntity;
import com.huongcung.inventoryservice.model.entity.WarehouseEntity;
import com.huongcung.inventoryservice.repository.BookRepository;
import com.huongcung.inventoryservice.repository.StockAdjustmentRepository;
import com.huongcung.inventoryservice.repository.StockLevelRepository;
import com.huongcung.inventoryservice.repository.WarehouseRepository;
import com.huongcung.inventoryservice.search.enumeration.SearchType;
import com.huongcung.inventoryservice.search.dto.SearchRequest;
import com.huongcung.inventoryservice.search.dto.SearchResponse;
import com.huongcung.inventoryservice.search.service.SearchService;
import com.huongcung.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final StockLevelRepository stockLevelRepository;
    private final BookRepository bookRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final SearchService searchService;
    private final StockAdjustmentMapper stockAdjustmentMapper;
    private final BookMapper bookMapper;

    @Override
    public StockLevelResponse getStockLevels(Pageable pageable, String q, SearchType searchBy, City city,
                                             String warehouseCode, StockStatus status) {

        log.info("Fetching all stock levels - searchBy: {}, cities: {}, warehouseCodes: {}, page: {}, size: {}, stockStatus: {}",
                searchBy, city, warehouseCode, pageable.getPageNumber(), pageable.getPageSize(), status);

        SearchRequest.SearchRequestBuilder requestBuilder
                = SearchRequest.builder().q(q).searchType(searchBy);
        
        if (warehouseCode != null && !warehouseCode.isEmpty()) {
            requestBuilder.warehouseCodes(List.of(warehouseCode));
        }
        
        if (city != null) {
            requestBuilder.cities(List.of(city.name()));
        }
        
        if (status != null) {
            requestBuilder.status(List.of(status.name()));
        }
        
        SearchRequest request = requestBuilder.build();

        SearchResponse searchResponse = searchService.searchStocks(request);

        log.info("Found {} stock levels (page {} of {}) in {}ms",
                searchResponse.getPagination().getTotalResults(),
                searchResponse.getPagination().getCurrentPage(),
                searchResponse.getPagination().getTotalPages(),
                searchResponse.getExecutionTimeMs());

        return StockLevelResponse.builder()
                .stockLevels(searchResponse.getStockLevels())
                .pagination(searchResponse.getPagination())
                .facets(searchResponse.getFacets())
                .highlightedFields(searchResponse.getHighlightedFields())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public StockAdjustmentResponse getStockAdjustments(Long stockLevelId, Pageable pageable) {
        log.debug("Fetching stock adjustments for stock level ID: {}, page: {}, size: {}",
                stockLevelId, pageable.getPageNumber(), pageable.getPageSize());

        Page<StockAdjustmentEntity> entities = stockAdjustmentRepository.findByStockLevelIdOrderByAdjustedAtDesc(stockLevelId, pageable);

        Page<StockAdjustmentDTO> pageDtos = entities.map(stockAdjustmentMapper::toDto);

        PaginationInfo pagination = PaginationInfo.builder()
                .currentPage(pageDtos.getNumber() + 1)
                .pageSize(pageDtos.getSize())
                .totalResults(pageDtos.getTotalElements())
                .totalPages(pageDtos.getTotalPages())
                .hasNext(pageDtos.hasNext())
                .hasPrevious(pageDtos.hasPrevious())
                .build();

        return StockAdjustmentResponse.builder()
                .adjustments(pageDtos.getContent())
                .pagination(pagination)
                .build();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void adjustStock(Long stockLevelId, StockAdjustmentRequest request) {

//        CustomUserDetails userDetails = staffServiceV2.getCurrentUser();
//
//        log.info("Adjusting stock level ID: {} to quantity: {} by user: {}",
//                stockLevelId, request.getNewQuantity(), userDetails.getUsername());
//
//        StaffEntity staff = staffRepository.findById(userDetails.getId())
//                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + userDetails.getId()));

        StockLevelEntity stockLevel = stockLevelRepository.findById(stockLevelId)
                .orElseThrow(() -> new RuntimeException("Stock level not found with ID: " + stockLevelId));

        WarehouseEntity warehouse = stockLevel.getWarehouse();

//        if (staff.getStaffType() != StaffType.STORE_MANAGER && staff.getStaffType() != StaffType.ADMIN) {
//            throw new IllegalStateException("Only ADMIN and STORE_MANAGER can adjust the stock level.");
//        }

        // For Store Managers, validate they can only adjust stock for warehouses in their assigned city
//        if (staff.getStaffType() == StaffType.STORE_MANAGER && staff.getAssignedCity() != null) {
//            if (warehouse.getCity() != staff.getAssignedCity()) {
//                throw new IllegalStateException(
//                        "Store Manager can only adjust stock for warehouses in their assigned city. " +
//                                "Stock level belongs to warehouse: " + stockLevel.getWarehouse().getCode() +
//                                " in city: " + warehouse.getCity() +
//                                ", but Store Manager is assigned to: " + staff.getAssignedCity());
//            }
//        }


        // Calculate difference
        Integer previousQuantity = stockLevel.getQuantity();
        Integer newTotalQuantity = stockLevel.getQuantity() + request.getNewQuantity();
        Integer difference = newTotalQuantity - previousQuantity;

        stockLevel.setQuantity(stockLevel.getQuantity() + request.getNewQuantity());
        stockLevelRepository.save(stockLevel);

        StockAdjustmentEntity adjustment = StockAdjustmentEntity.builder()
                .stockLevel(stockLevel)
                .previousQuantity(previousQuantity)
                .newQuantity(newTotalQuantity)
                .reason(request.getReason())
                .adjustedBy("test")
                .adjustedAt(LocalDateTime.now())
                .build();

        stockAdjustmentRepository.save(adjustment);

        log.info("Stock level adjusted successfully - ID: {}, previous: {}, new: {}, difference: {}",
                stockLevelId, previousQuantity, newTotalQuantity, difference);
    }

    @Override
    public void initStock(StockInitRequest request) {
        log.info("Init stock for book: {}, sku: {}", request.getBook().getTitle(), request.getBook().getSku());
        if (!bookRepository.existsBySku(request.getBook().getSku())) {
            BookEntity bookEntity = bookMapper.toEntity(request.getBook());
            bookEntity = bookRepository.save(bookEntity);

            List<WarehouseEntity> warehouses = warehouseRepository.findAll();

            for (WarehouseEntity warehouse : warehouses) {
                if (!stockLevelRepository.existsByBookAndWarehouse(bookEntity, warehouse)) {
                    StockLevelEntity stockLevel = new StockLevelEntity();
                    stockLevel.setBook(bookEntity);
                    stockLevel.setWarehouse(warehouse);
                    stockLevel.setLastRestocked(LocalDateTime.now());
                    stockLevelRepository.save(stockLevel);
                }
            }
        }
    }
}

