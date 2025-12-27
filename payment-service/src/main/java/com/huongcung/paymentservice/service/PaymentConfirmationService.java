package com.huongcung.paymentservice.service;

import java.util.Map;

public interface PaymentConfirmationService {

    void handlePaymentSuccess(String orderId, Long paidAmount, String bankTranNo, String transactionNo, Map<String, String> rawResponse);

    void handlePaymentFailed(String orderId, String bankTranNo, String transactionNo, Map<String, String> rawResponse);

    boolean checkValidTransaction(String orderId);

    boolean checkReceivedAmountForTransaction(String orderId, Long amount);
}
