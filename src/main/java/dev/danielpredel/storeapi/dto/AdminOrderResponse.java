package dev.danielpredel.storeapi.dto;

import dev.danielpredel.storeapi.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AdminOrderResponse(
        Long id,
        Long userId,
        List<OrderItemResponse> orderItems,
        BigDecimal totalAmount,
        LocalDateTime purchaseDate,
        OrderStatus status
) {}
