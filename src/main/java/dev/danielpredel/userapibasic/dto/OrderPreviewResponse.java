package dev.danielpredel.userapibasic.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderPreviewResponse(
    List<OrderItemPreviewResponse> orderItems,
    BigDecimal totalAmount
) {
}
