package com.huongcung.paymentservice.entity;

import com.huongcung.paymentservice.common.entity.BaseEntity;
import com.huongcung.paymentservice.enumeration.PaymentMethod;
import com.huongcung.paymentservice.enumeration.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.Map;

@Entity
@Table(name = "payment_transaction")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentTransactionEntity extends BaseEntity {

    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "paid_amount", precision = 10, scale = 2)
    private BigDecimal paidAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false)
    private PaymentMethod method;

    @Column(name = "bank_tran_no")
    private String bankTranNo;

    @Column(name = "transaction_no")
    private String transactionNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_response")
    private Map<String, String> rawResponse;
}
