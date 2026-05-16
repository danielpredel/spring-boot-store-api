package dev.danielpredel.storeapi.order.dto;

import java.math.BigDecimal;

public record OrderItemResponse(Long productId, String productName, int quantity, BigDecimal priceAtPurchase) {}
