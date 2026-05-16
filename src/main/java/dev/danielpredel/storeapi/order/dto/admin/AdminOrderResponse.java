package dev.danielpredel.storeapi.order.dto.admin;

import dev.danielpredel.storeapi.enums.OrderStatus;
import dev.danielpredel.storeapi.order.dto.OrderItemResponse;

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
