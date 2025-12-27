package com.huongcung.inventoryservice.repository;

import com.huongcung.inventoryservice.common.enumeration.City;
import com.huongcung.inventoryservice.model.entity.WarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<WarehouseEntity, Long> {
    List<WarehouseEntity> findByCity(City city);
    Optional<WarehouseEntity> findByCode(String code);
}

