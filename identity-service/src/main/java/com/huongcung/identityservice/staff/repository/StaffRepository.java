package com.huongcung.identityservice.staff.repository;

import com.huongcung.identityservice.staff.entity.StaffEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<StaffEntity, Long> {
    Optional<StaffEntity> findByUsername(String userName);
    Optional<StaffEntity> findByEmail(String email);
    Optional<StaffEntity> findByUid(String uid);
}

