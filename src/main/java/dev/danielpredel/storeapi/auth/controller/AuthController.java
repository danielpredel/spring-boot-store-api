package dev.danielpredel.storeapi.auth.controller;

import dev.danielpredel.storeapi.auth.dto.AuthRequest;
import dev.danielpredel.storeapi.auth.dto.AuthResponse;
import dev.danielpredel.storeapi.common.dto.ApiResponse;
import dev.danielpredel.storeapi.user.dto.auth.RegisterRequest;
import dev.danielpredel.storeapi.auth.service.AuthService;
import dev.danielpredel.storeapi.user.dto.auth.RegisterResponse;
import dev.danielpredel.storeapi.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Instant;

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
    public ResponseEntity<ApiResponse<RegisterResponse>> create(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse user = userService.save(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/users/{id}")
                .buildAndExpand(user.id())
                .toUri();

        return ResponseEntity.created(location).body(
                new ApiResponse<>(
                        "User registered successfully",
                        HttpStatus.OK.value(),
                        Instant.now().toString(),
                        user
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Login successful",
                        HttpStatus.OK.value(),
                        Instant.now().toString(),
                        authService.login(request)
                )
        );
    }
}
