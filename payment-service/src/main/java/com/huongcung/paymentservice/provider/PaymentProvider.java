package com.huongcung.paymentservice.provider;

import com.huongcung.paymentservice.enumeration.PaymentMethod;

import java.util.Map;

public interface PaymentProvider {
    String createPaymentUrl(String orderId, Long amount, String ipAddress);
    PaymentMethod getPaymentMethod();
}
