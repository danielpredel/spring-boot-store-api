package dev.danielpredel.userapibasic.dto;

import dev.danielpredel.userapibasic.enums.Role;

public record AdminUserResponse(Long id, String name, String email, Role role, boolean active) {}
