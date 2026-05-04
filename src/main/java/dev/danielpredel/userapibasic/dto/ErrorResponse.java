package dev.danielpredel.userapibasic.dto;

import java.util.Map;

public record ErrorResponse (String message, int status, String timestamp, Map<String, String> errors) {}
