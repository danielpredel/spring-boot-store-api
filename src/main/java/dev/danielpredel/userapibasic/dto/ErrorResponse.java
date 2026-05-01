package dev.danielpredel.userapibasic.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ErrorResponse {
    @Setter
    String message;

    @Setter
    int status;

    @Setter
    String timestamp;
}
