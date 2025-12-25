package com.core.notificationservice.listener;

import com.core.notificationservice.dto.OrderPlacedEvent;
import com.core.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "order-placed-topic", groupId = "notification-group")
    public void listenOrderPlaced(
            @Payload OrderPlacedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        try {
            log.info("Nhận được sự kiện đặt hàng từ topic: {}, partition: {}, offset: {} - Order: {}, Email: {}", 
                topic, partition, offset, event.getOrderNumber(), event.getCustomerEmail());
            
            emailService.sendOrderConfirmationEmail(
                event.getOrderNumber(), 
                event.getCustomerEmail(), 
                event.getCustomerName(),
                event.getSubTotal(),
                event.getTotalAmount(),
                event.getOrderDate(),
                event.getPaymentMethod(),
                event.getShippingAddress(),
                event.getOrderItems()
            );
            
            log.info("Đã gửi email xác nhận cho đơn: {}", event.getOrderNumber());
        } catch (Exception e) {
            log.error("Lỗi khi xử lý sự kiện đặt hàng cho đơn: {} - Lỗi: {}", 
                event != null ? event.getOrderNumber() : "unknown", e.getMessage(), e);
            throw e;
        }
    }
}
