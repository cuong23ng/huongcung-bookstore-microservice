package com.core.notificationservice.service;

import com.core.notificationservice.dto.OrderItemDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface EmailService {
    void sendOrderConfirmationEmail(
            String orderNumber,
            String customerEmail,
            String customerName,
            BigDecimal subTotal,
            BigDecimal totalAmount,
            LocalDateTime orderDate,
            String paymentMethod,
            String shippingAddress,
            List<OrderItemDTO> orderItems);
}
