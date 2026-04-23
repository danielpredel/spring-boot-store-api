package dev.danielpredel.userapibasic.controller;

import dev.danielpredel.userapibasic.dto.UserMapper;
import dev.danielpredel.userapibasic.dto.UserRequestDTO;
import dev.danielpredel.userapibasic.dto.UserResponseDTO;
import dev.danielpredel.userapibasic.model.User;
import dev.danielpredel.userapibasic.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO dto) {
        User user = userMapper.toUser(dto);
        User savedUser = userService.save(user);
        return ResponseEntity.ok(UserMapper.toUserResponseDTO(savedUser));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(userMapper.toUserResponseDTOList(users));
    }

    @GetMapping("{id}")
    public ResponseEntity<UserResponseDTO> finById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(UserMapper.toUserResponseDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }
}
