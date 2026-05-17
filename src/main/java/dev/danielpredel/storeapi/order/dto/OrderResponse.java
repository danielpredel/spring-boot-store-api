package dev.danielpredel.storeapi.order.dto;

import dev.danielpredel.storeapi.common.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        List<OrderItemResponse> orderItems,
        BigDecimal totalAmount,
        LocalDateTime purchaseDate,
        OrderStatus status
) {}
