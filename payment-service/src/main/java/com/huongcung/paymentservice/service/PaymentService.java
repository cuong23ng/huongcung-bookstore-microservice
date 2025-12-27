package com.huongcung.paymentservice.service;

import com.huongcung.paymentservice.entity.PaymentTransactionEntity;
import com.huongcung.paymentservice.enumeration.PaymentMethod;
import com.huongcung.paymentservice.enumeration.PaymentStatus;

public interface PaymentService {
    String createPaymentUrl(String orderId, Double amount, String ipAddress);
    PaymentTransactionEntity createPaymentTransaction(String orderId, Double amount, PaymentMethod method);
    PaymentTransactionEntity updatePaymentTransactionStatus(String orderId, Double paidAmount, PaymentStatus status);
}
