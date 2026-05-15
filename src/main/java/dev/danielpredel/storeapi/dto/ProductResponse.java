package dev.danielpredel.storeapi.dto;

import java.math.BigDecimal;

public record ProductResponse(Long id, String name, BigDecimal price, int stock, String imageUrl) {}
