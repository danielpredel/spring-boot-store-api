package dev.danielpredel.userapibasic.dto;

import dev.danielpredel.userapibasic.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long userId,
        List<OrderItemResponse> orderItems,
        BigDecimal totalAmount,
        LocalDateTime purchaseDate,
        OrderStatus status
) {}
