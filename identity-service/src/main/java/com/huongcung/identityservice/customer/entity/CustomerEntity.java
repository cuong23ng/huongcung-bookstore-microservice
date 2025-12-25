package com.huongcung.identityservice.customer.entity;

import com.huongcung.identityservice.common.entity.UserEntity;
import com.huongcung.identityservice.customer.enumeration.CustomerTier;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "customer")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerEntity extends UserEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_tier")
    private CustomerTier customerTier;

    @Column(name = "loyalty_points")
    private Integer loyaltyPoints = 0;

    @Column(name = "total_spent")
    private Double totalSpent = 0.0;
    
    // Relationships will be added when Order entities are complete
}
