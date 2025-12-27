package com.huongcung.paymentservice.external.vnpay.webhook;

import com.huongcung.paymentservice.external.vnpay.service.VnpayIPN;
import com.huongcung.paymentservice.provider.impl.VnpayProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class VnpayWebhookController {

    private final VnpayIPN vnpayIPN;

    @GetMapping("/vnpay-ipn")
    public ResponseEntity<?> vnpayIpn(HttpServletRequest request) {
        log.info("vnpayIpn: request {}", request.getPathInfo());

        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                fields.put(fieldName, fieldValue);
            }
        }

        Map<String, String> result = vnpayIPN.processIpn(fields);

        return ResponseEntity.ok(result);
    }

}
