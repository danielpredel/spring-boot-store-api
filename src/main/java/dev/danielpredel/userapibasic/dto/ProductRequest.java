package dev.danielpredel.userapibasic.dto;

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

    @Min(0)
    int stock,

    @URL
    String imageUrl,

    boolean active
) {}
