package com.huongcung.paymentservice.external.vnpay.service;

import com.huongcung.paymentservice.configuration.VnpayConfig;
import com.huongcung.paymentservice.service.PaymentConfirmationService;
import com.huongcung.paymentservice.utils.PaymentUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class VnpayIPN {

    private final VnpayConfig vnpayConfig;
    private final PaymentConfirmationService paymentConfirmationService;

    @Transactional
    public Map<String, String> processIpn(Map<String, String> requestParams) {

        String vnp_SecureHash = requestParams.get("vnp_SecureHash");

        Map<String, String> fields = new HashMap<>(requestParams);
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }

        String signValue = PaymentUtils.hmacSHA512(vnpayConfig.secretKey, hashAllFields(fields));

        if (signValue.equals(vnp_SecureHash)) {
            String orderId = fields.get("vnp_TxnRef");
            String responseCode = fields.get("vnp_ResponseCode");

            long vnpAmount = Long.parseLong(fields.get("vnp_Amount"));
            long amount = vnpAmount / 100;

            String bankTranNo = fields.get("vnp_BankTranNo");
            String transactionNo = fields.get("vnp_TransactionNo");

            if (!paymentConfirmationService.checkValidTransaction(orderId)) {
                return Map.of("RspCode", "01", "Message", "Order not Found");
            }

            if (!paymentConfirmationService.checkReceivedAmountForTransaction(orderId, amount)) {
                return Map.of("RspCode", "04", "Message", "Invalid Amount");
            }

            if ("00".equals(responseCode)) {
                log.info("Payment Success for Order: {}", orderId);
                paymentConfirmationService.handlePaymentSuccess(orderId, amount, bankTranNo, transactionNo, fields);
            } else {
                log.info("Payment Failed for Order: {} with code: {}", orderId, responseCode);
                paymentConfirmationService.handlePaymentFailed(orderId, bankTranNo, transactionNo, fields);
            }

            return Map.of("RspCode", "00", "Message", "Confirm Success");

        } else {
            return Map.of("RspCode", "97", "Message", "Invalid Checksum");
        }
    }

    public static String hashAllFields(Map<String, String> fields) {
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = fields.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                sb.append(fieldName);
                sb.append("=");
                sb.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    sb.append("&");
                }
            }
        }
        return sb.toString();
    }
}
