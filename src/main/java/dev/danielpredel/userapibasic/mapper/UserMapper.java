package dev.danielpredel.userapibasic.mapper;

import dev.danielpredel.userapibasic.dto.UserRequest;
import dev.danielpredel.userapibasic.dto.UserResponse;
import dev.danielpredel.userapibasic.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public static UserResponse toUserResponse(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public User toUser(Long id, UserRequest dto) {
        return new User(id, dto.getName(), dto.getEmail(), dto.getPassword(), dto.getAddress());
    }

    public List<UserResponse> toUserResponseList(List<User> users) {
        return users.stream()
                .map(UserMapper::toUserResponse)
                .collect(Collectors.toList());
    }
}
