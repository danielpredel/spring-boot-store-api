package dev.danielpredel.userapibasic.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class UserRequestDTO {
    @Setter
    private String name;
    @Setter
    private String email;
    @Setter
    private String password;
    @Setter
    private String address;
}
