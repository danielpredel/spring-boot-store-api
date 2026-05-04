package dev.danielpredel.userapibasic.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    @NotBlank(message = "Can't be blank.")
    private String name;

    @NotBlank(message = "Can't be blank.")
    @Email(message = "Invalid format.")
    private String email;

    @NotBlank(message = "Can't be blank.")
    @Size(min = 8, message = "Too short.")
    private String password;

    @NotBlank(message = "Can't be blank.")
    private String address;
}
