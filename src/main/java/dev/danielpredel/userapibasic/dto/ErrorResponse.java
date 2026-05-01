package dev.danielpredel.userapibasic.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
public class ErrorResponse {
    @Setter
    String message;

    @Setter
    int status;

    @Setter
    String timestamp;

    @Setter
    Map<String, String> errors;
}
