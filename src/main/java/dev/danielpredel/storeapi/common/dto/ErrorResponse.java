package dev.danielpredel.storeapi.common.dto;

import java.util.List;
import java.util.Map;

public record ErrorResponse (String message, int status, String timestamp, Map<String, List<String>> errors) {}
