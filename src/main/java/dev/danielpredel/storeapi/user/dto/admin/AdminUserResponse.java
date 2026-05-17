package dev.danielpredel.storeapi.user.dto.admin;

import dev.danielpredel.storeapi.common.enums.Role;

public record AdminUserResponse(Long id, String name, String email, Role role, boolean active) {}
