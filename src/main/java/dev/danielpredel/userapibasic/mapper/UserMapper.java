package dev.danielpredel.userapibasic.mapper;

import dev.danielpredel.userapibasic.dto.UserRequest;
import dev.danielpredel.userapibasic.dto.UserResponse;
import dev.danielpredel.userapibasic.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(UserEntity user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }

    public UserEntity toEntity(UserRequest dto) {
        return new UserEntity(dto.name(), dto.email(), dto.password(), dto.address());
    }
}
