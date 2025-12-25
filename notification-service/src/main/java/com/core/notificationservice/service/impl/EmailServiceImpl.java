package com.core.notificationservice.service.impl;

import com.core.notificationservice.dto.OrderItemDTO;
import com.core.notificationservice.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    @Async
    public void sendOrderConfirmationEmail(
            String orderNumber,
            String customerEmail,
            String customerName,
            BigDecimal subTotal,
            BigDecimal totalAmount,
            LocalDateTime orderDate,
            String paymentMethod,
            String shippingAddress,
            List<OrderItemDTO> orderItems) {
        log.info("Sending confirmation email for order: {}", orderNumber);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("customerName", customerName);
            context.setVariable("orderNumber", orderNumber);
            context.setVariable("subTotal", subTotal);
            context.setVariable("totalAmount", totalAmount);
            context.setVariable("orderDate", orderDate);
            context.setVariable("paymentMethod", paymentMethod);
            context.setVariable("shippingAddress", shippingAddress);
            context.setVariable("orderItems", orderItems);

            String htmlContent = templateEngine.process("order-confirmation", context);

            helper.setTo(customerEmail);
            helper.setSubject("Hương Cung Bookstore - Xác nhận đơn hàng #" + orderNumber);
            helper.setText(htmlContent, true);
            helper.setFrom("Huong Cung Bookstore <noreply@huongcung.com>");

            mailSender.send(message);
            log.info("Email sent successfully to {}", customerEmail);
        } catch (MessagingException e) {
            log.error("Failed to send email for order {}", orderNumber, e);
        }
    }

}
