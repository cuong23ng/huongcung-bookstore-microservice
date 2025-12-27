package com.huongcung.paymentservice.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@Configuration
public class VnpayConfig {
    @Value("${payment.vnpay.url}")
    public String vnp_PayUrl;

    @Value("${payment.vnpay.return-url}")
    public String vnp_ReturnUrl;

    @Value("${payment.vnpay.tmn-code}")
    public String vnp_TmnCode;

    @Value("${payment.vnpay.hash-secret}")
    public String secretKey;

    @Value("${payment.vnpay.timezone}")
    public String timezone;

    @Value("${payment.vnpay.api-url}")
    public String vnp_ApiUrl;
}