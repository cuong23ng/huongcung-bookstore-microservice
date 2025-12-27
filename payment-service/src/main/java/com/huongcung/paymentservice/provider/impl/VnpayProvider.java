package com.huongcung.paymentservice.provider.impl;

import com.huongcung.paymentservice.configuration.VnpayConfig;
import com.huongcung.paymentservice.enumeration.PaymentMethod;
import com.huongcung.paymentservice.provider.PaymentProvider;
import com.huongcung.paymentservice.utils.PaymentUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "payment.type", havingValue = "vnpay")
@Component
public class VnpayProvider implements PaymentProvider {

    private final VnpayConfig vnpayConfig;

    @Override
    public String createPaymentUrl(String orderId, Long amount, String ipAddress) {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = orderId;
        String vnp_IpAddr = ipAddress;
        String vnp_TmnCode = vnpayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh_Toan_Don_Hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnpayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        TimeZone vietnamTimeZone = TimeZone.getTimeZone(vnpayConfig.timezone);
        Calendar cld = Calendar.getInstance(vietnamTimeZone);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(vietnamTimeZone);
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        // Expire date (15 phút)
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Build Query URL
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                // 1. Encode chuẩn UTF-8
                String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8);

                // 2. FIX QUAN TRỌNG: Thay thế '+' bằng '%20'
                // VNPay thường yêu cầu khoảng trắng là %20 để tính checksum chính xác
                encodedValue = encodedValue.replace("+", "%20");

                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(encodedValue); // Dùng giá trị đã fix

                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8));
                query.append('=');
                query.append(encodedValue); // Dùng giá trị đã fix

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        log.info("VNPAY Raw Hash: {}", hashData);
        log.info("Secret Key: {}", vnpayConfig.secretKey);

        String queryUrl = query.toString();
        String vnp_SecureHash = PaymentUtils.hmacSHA512(vnpayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        log.info("Payment URL: {}", vnpayConfig.vnp_PayUrl + "?" + queryUrl);

        return vnpayConfig.vnp_PayUrl + "?" + queryUrl;
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.VNPAY;
    }

}
