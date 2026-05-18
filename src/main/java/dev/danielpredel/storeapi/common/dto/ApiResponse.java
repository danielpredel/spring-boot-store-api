package dev.danielpredel.storeapi.common.dto;

public record ApiResponse<T>(String message, int status, String timestamp, T data) {}
