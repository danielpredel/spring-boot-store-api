package dev.danielpredel.userapibasic.dto;

import dev.danielpredel.userapibasic.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public static UserResponseDTO toUserResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public User toUser(UserRequestDTO dto) {
        return new User(dto.getName(), dto.getEmail(), dto.getPassword(), dto.getAddress());
    }

    public List<UserResponseDTO> toUserResponseDTOList(List<User> users) {
        return users.stream()
                .map(UserMapper::toUserResponseDTO)
                .collect(Collectors.toList());
    }
}
