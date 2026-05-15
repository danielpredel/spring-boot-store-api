package dev.danielpredel.storeapi.dto;

import dev.danielpredel.storeapi.enums.Role;

public record AdminUserResponse(Long id, String name, String email, Role role, boolean active) {}
