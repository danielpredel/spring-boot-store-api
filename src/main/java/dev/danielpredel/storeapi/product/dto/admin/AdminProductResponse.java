package dev.danielpredel.storeapi.product.dto.admin;

import java.math.BigDecimal;

public record AdminProductResponse(
        Long id,
        String name,
        BigDecimal price,
        int stock,
        String imageUrl,
        boolean active
) {}
