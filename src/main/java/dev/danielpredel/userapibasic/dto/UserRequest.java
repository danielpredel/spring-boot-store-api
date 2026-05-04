package dev.danielpredel.userapibasic.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest (
    @NotBlank(message = "Can't be blank.") String name,

    @NotBlank(message = "Can't be blank.")
    @Email(message = "Invalid format.") String email,

    @NotBlank(message = "Can't be blank.")
    @Size(min = 8, message = "Too short.") String password,

    @NotBlank(message = "Can't be blank.") String address
) {}
