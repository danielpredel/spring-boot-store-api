package dev.danielpredel.storeapi.dto;

import java.math.BigDecimal;

public record OrderItemPreviewResponse(
        Long productId,
        String productName,
        int requestedQuantity,
        int availableStock,
        BigDecimal price,
        BigDecimal subtotal
) {
}
