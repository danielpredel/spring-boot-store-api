package dev.danielpredel.userapibasic.mapper;

import dev.danielpredel.userapibasic.dto.UserRequest;
import dev.danielpredel.userapibasic.dto.UserResponse;
import dev.danielpredel.userapibasic.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }

    public User toEntity(UserRequest dto) {
        return new User(dto.name(), dto.email(), dto.password(), dto.address());
    }
}
