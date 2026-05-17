package dev.danielpredel.storeapi.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderPreviewRequest(
        @NotNull
        @NotEmpty
        @Valid
        List<OrderItemRequest> items
) {
}
