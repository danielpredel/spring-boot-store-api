package dev.danielpredel.userapibasic.mapper;

import dev.danielpredel.userapibasic.dto.AdminUserResponse;
import dev.danielpredel.userapibasic.dto.UserRequest;
import dev.danielpredel.userapibasic.dto.UserResponse;
import dev.danielpredel.userapibasic.entity.User;
import dev.danielpredel.userapibasic.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }

    public User toEntity(UserRequest dto) {
        return new User(dto.name(), dto.email(), dto.password(), dto.address(), Role.USER, true);
    }

    public AdminUserResponse toAdminResponse(User user) {
        return new AdminUserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.isActive());
    }
}
