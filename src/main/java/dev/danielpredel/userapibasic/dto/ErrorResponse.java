package dev.danielpredel.userapibasic.dto;

import java.util.List;
import java.util.Map;

public record ErrorResponse (String message, int status, String timestamp, Map<String, List<String>> errors) {}
