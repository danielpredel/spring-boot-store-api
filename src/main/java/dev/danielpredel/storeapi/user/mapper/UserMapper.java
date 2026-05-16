package dev.danielpredel.storeapi.user.mapper;

import dev.danielpredel.storeapi.user.dto.admin.AdminUserResponse;
import dev.danielpredel.storeapi.user.dto.auth.RegisterRequest;
import dev.danielpredel.storeapi.user.dto.UserResponse;
import dev.danielpredel.storeapi.user.entity.User;
import dev.danielpredel.storeapi.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getAddress());
    }

    public User toEntity(RegisterRequest dto) {
        return new User(dto.name(), dto.email(), dto.password(), dto.address(), Role.USER, true);
    }

    public AdminUserResponse toAdminResponse(User user) {
        return new AdminUserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.isActive());
    }
}
