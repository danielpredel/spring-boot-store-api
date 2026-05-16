package dev.danielpredel.storeapi.product.dto;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;

public record ProductRequest(
    @NotBlank
    @Size(max = 100)
    String name,

    @NotNull
    @Positive
    @Digits(integer = 10, fraction = 2)
    BigDecimal price,

    @NotNull
    @PositiveOrZero
    Integer stock,

    @URL
    String imageUrl,

    @NotNull
    Boolean active
) {}
