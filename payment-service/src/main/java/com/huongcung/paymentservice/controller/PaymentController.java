package com.huongcung.paymentservice.controller;

import com.huongcung.paymentservice.common.dto.BaseResponse;
import com.huongcung.paymentservice.enumeration.PaymentMethod;
import com.huongcung.paymentservice.enumeration.PaymentStatus;
import com.huongcung.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/create-url")
    public ResponseEntity<BaseResponse> createPaymentUrl(@RequestParam String orderId,
                                                      @RequestParam Double amount,
                                                      @RequestParam String ipAddress) {

        String paymentUrl = paymentService.createPaymentUrl(orderId, amount, ipAddress);

        return ResponseEntity.ok(BaseResponse.builder()
                .message("Payment URL created")
                .data(paymentUrl)
                .build());
    }

    @PostMapping("/create")
    public ResponseEntity<BaseResponse> createPayment(@RequestParam String orderId,
                                                      @RequestParam Double amount) {

        paymentService.createPaymentTransaction(orderId, amount, PaymentMethod.COD);

        return ResponseEntity.ok(BaseResponse.builder()
                .message("Payment Transaction created")
                .build());
    }

    @PostMapping("/update")
    public ResponseEntity<BaseResponse> updatePayment(@RequestParam String orderId,
                                                      @RequestParam Double paidAmount,
                                                      @RequestParam PaymentStatus status) {

        paymentService.updatePaymentTransactionStatus(orderId, paidAmount, status);

        return ResponseEntity.ok(BaseResponse.builder()
                .message("Payment Transaction updated")
                .build());
    }
}
