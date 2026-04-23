package dev.danielpredel.userapibasic.service.impl;

import dev.danielpredel.userapibasic.dto.UserMapper;
import dev.danielpredel.userapibasic.dto.UserRequestDTO;
import dev.danielpredel.userapibasic.dto.UserResponseDTO;
import dev.danielpredel.userapibasic.exception.ResourceNotFoundException;
import dev.danielpredel.userapibasic.model.User;
import dev.danielpredel.userapibasic.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private Long userCount = 1L;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserResponseDTO save(UserRequestDTO dto) {
        Long id = userCount++;
        User newUser = new User(id, dto.getName(), dto.getEmail(), dto.getPassword(), dto.getEmail());
        users.put(id, newUser);
        return UserMapper.toUserResponseDTO(newUser);
    }

    @Override
    public List<UserResponseDTO> findAll() {
        return userMapper.toUserResponseDTOList(users.values().stream().toList());
    }

    @Override
    public UserResponseDTO findById(Long id) {
        User user = Optional.ofNullable(users.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        return UserMapper.toUserResponseDTO(user);
    }
}
