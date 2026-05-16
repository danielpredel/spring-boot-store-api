package dev.danielpredel.storeapi.controller;

import dev.danielpredel.storeapi.dto.AuthRequest;
import dev.danielpredel.storeapi.dto.AuthResponse;
import dev.danielpredel.storeapi.dto.UserRequest;
import dev.danielpredel.storeapi.user.dto.UserResponse;
import dev.danielpredel.storeapi.service.AuthService;
import dev.danielpredel.storeapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        UserResponse savedUser = userService.save(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/users/{id}")
                .buildAndExpand(savedUser.id())
                .toUri();

        return ResponseEntity.created(location).body(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
