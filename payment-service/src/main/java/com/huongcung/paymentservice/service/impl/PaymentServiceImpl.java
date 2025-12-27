package com.huongcung.paymentservice.service.impl;

import com.huongcung.paymentservice.entity.PaymentTransactionEntity;
import com.huongcung.paymentservice.enumeration.PaymentMethod;
import com.huongcung.paymentservice.enumeration.PaymentStatus;
import com.huongcung.paymentservice.provider.PaymentProvider;
import com.huongcung.paymentservice.repository.PaymentTransactionRepository;
import com.huongcung.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentProvider paymentProvider;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Override
    public String createPaymentUrl(String orderId, Double amount, String ipAddress) {

        createPaymentTransaction(orderId, amount, paymentProvider.getPaymentMethod());

        Long longAmount = Double.valueOf(amount * 100).longValue();
        return paymentProvider.createPaymentUrl(orderId, longAmount, ipAddress);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public PaymentTransactionEntity createPaymentTransaction(String orderId, Double amount, PaymentMethod method) {
        PaymentTransactionEntity paymentTransaction = new PaymentTransactionEntity();
        paymentTransaction.setOrderId(orderId);
        paymentTransaction.setAmount(BigDecimal.valueOf(amount));
        paymentTransaction.setMethod(method);
        paymentTransaction.setStatus(PaymentStatus.PENDING);
        return paymentTransactionRepository.save(paymentTransaction);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public PaymentTransactionEntity updatePaymentTransactionStatus(String orderId, Double paidAmount, PaymentStatus status) {
        PaymentTransactionEntity paymentTransaction = paymentTransactionRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Invalid transaction"));
        paymentTransaction.setStatus(status);
        paymentTransaction.setPaidAmount(BigDecimal.valueOf(paidAmount));
        return paymentTransactionRepository.save(paymentTransaction);
    }
}
