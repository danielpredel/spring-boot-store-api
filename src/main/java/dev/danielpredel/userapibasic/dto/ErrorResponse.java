package dev.danielpredel.userapibasic.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ErrorResponse {
    String message;
    int status;
    String timestamp;
    Map<String, String> errors;
}
