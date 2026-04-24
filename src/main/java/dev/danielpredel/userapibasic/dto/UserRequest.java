package dev.danielpredel.userapibasic.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
public class UserRequest {
    @Setter
    @NotBlank
    private String name;

    @Setter
    @NotBlank
    @Email
    private String email;

    @Setter
    @NotBlank
    @Size(min = 8)
    private String password;

    @Setter
    @NotBlank
    private String address;
}
