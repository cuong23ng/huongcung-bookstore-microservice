package com.huongcung.paymentservice.service.impl;

import com.huongcung.paymentservice.entity.PaymentTransactionEntity;
import com.huongcung.paymentservice.enumeration.PaymentStatus;
import com.huongcung.paymentservice.repository.PaymentTransactionRepository;
import com.huongcung.paymentservice.service.PaymentConfirmationService;
import com.huongcung.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentConfirmationServiceImpl implements PaymentConfirmationService {

    private final ApplicationEventPublisher eventPublisher;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void handlePaymentSuccess(String orderId, Long paidAmount, String bankTranNo, String transactionNo, Map<String, String> rawResponse) {
        log.info("Processing payment success for order id: {}", orderId);

        PaymentTransactionEntity paymentTransaction = paymentTransactionRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Transaction"));
        paymentTransaction.setStatus(PaymentStatus.SUCCESS);
        paymentTransaction.setTransactionNo(transactionNo);
        paymentTransaction.setBankTranNo(bankTranNo);
        paymentTransaction.setRawResponse(rawResponse);
        paymentTransaction.setPaidAmount(BigDecimal.valueOf(paidAmount));
        paymentTransactionRepository.save(paymentTransaction);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void handlePaymentFailed(String orderId, String bankTranNo, String transactionNo, Map<String, String> rawResponse) {
        log.info("Processing payment failed for order id: {}", orderId);

        PaymentTransactionEntity paymentTransaction = paymentTransactionRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Transaction"));
        paymentTransaction.setStatus(PaymentStatus.FAILED);
        paymentTransaction.setTransactionNo(transactionNo);
        paymentTransaction.setBankTranNo(bankTranNo);
        paymentTransaction.setRawResponse(rawResponse);
        paymentTransactionRepository.save(paymentTransaction);
    }

    @Override
    public boolean checkValidTransaction(String orderId) {
        PaymentTransactionEntity paymentTransaction = paymentTransactionRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Transaction"));
        return Objects.nonNull(paymentTransaction);
    }

    @Override
    public boolean checkReceivedAmountForTransaction(String orderId, Long amount) {
        PaymentTransactionEntity paymentTransaction = paymentTransactionRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Transaction"));
        return paymentTransaction.getAmount().longValue() == amount;
    }
}
