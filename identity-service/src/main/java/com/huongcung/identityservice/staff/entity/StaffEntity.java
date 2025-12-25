package com.huongcung.identityservice.staff.entity;

import com.huongcung.identityservice.common.entity.UserEntity;
import com.huongcung.identityservice.common.enumeration.City;
import com.huongcung.identityservice.staff.enumeration.StaffType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "staff")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StaffEntity extends UserEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "staff_type", nullable = false)
    private StaffType staffType;

    @Enumerated(EnumType.STRING)
    @Column(name = "city")
    private City city;
    
    @Column(name = "warehouse")
    private String warehouse;
    
    @Column(name = "hire_date")
    private LocalDate hireDate;
}
