package dev.danielpredel.userapibasic.mapper;

import dev.danielpredel.userapibasic.dto.UserRequest;
import dev.danielpredel.userapibasic.dto.UserResponse;
import dev.danielpredel.userapibasic.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public UserResponse toResponse(UserEntity user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public UserEntity toEntity(UserRequest dto) {
        return new UserEntity(dto.getName(), dto.getEmail(), dto.getPassword(), dto.getAddress());
    }

    public List<UserResponse> toResponseList(List<UserEntity> users) {
        return users.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
