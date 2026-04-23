package dev.danielpredel.userapibasic.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class UserResponse {
    @Setter
    private Long id;
    @Setter
    private String name;
    @Setter
    private String email;
}
