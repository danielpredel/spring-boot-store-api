package dev.danielpredel.userapibasic.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
public class UserRequest {
    @Setter
    @NotBlank(message = "Can't be blank.")
    private String name;

    @Setter
    @NotBlank(message = "Can't be blank.")
    @Email(message = "Invalid format.")
    private String email;

    @Setter
    @NotBlank(message = "Can't be blank.")
    @Size(min = 8, message = "Too short.")
    private String password;

    @Setter
    @NotBlank(message = "Can't be blank.")
    private String address;
}
