package dev.danielpredel.storeapi.user.controller;

import dev.danielpredel.storeapi.common.dto.ApiResponse;
import dev.danielpredel.storeapi.user.dto.UserResponse;
import dev.danielpredel.storeapi.user.dto.UserUpdateRequest;
import dev.danielpredel.storeapi.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Validated
@RestController
@RequestMapping("/users")
@Tag(name = "Users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get current user profile (USER only)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<UserResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "User retrieved successfully",
                        HttpStatus.OK.value(),
                        Instant.now().toString(),
                        userService.findById(id)
                )
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update current user's profile (USER only)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest  dto) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "User updated successfully",
                        HttpStatus.OK.value(),
                        Instant.now().toString(),
                        userService.update(id, dto)
                )
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate current user's account (USER only)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
