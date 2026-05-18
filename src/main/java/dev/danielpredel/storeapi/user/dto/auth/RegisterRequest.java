package dev.danielpredel.storeapi.user.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank
    @Size(max = 100)
    String name,

    @NotBlank
    @Email
    @Size(max = 100)
    String email,

    @NotBlank
    @Size(min = 8, max = 64)
    @Pattern(
            regexp = "^(?=(?:.*[A-Z]){2,})(?=(?:.*[a-z]){2,})(?=(?:.*\\d){2,})(?=(?:.*[@$!%*?&]){2,}).*$",
            message = "Must have at least 2 uppercase, 2 lowercase, 2 digits, and 2 symbols"
    )
    String password,

    @NotBlank
    @Size(max = 150)
    String address
) {}
