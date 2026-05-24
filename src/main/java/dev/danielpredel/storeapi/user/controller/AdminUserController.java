package dev.danielpredel.storeapi.user.controller;

import dev.danielpredel.storeapi.common.dto.ApiResponse;
import dev.danielpredel.storeapi.user.dto.admin.AdminUserResponse;
import dev.danielpredel.storeapi.user.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@Validated
@RestController
@RequestMapping("/admin/users")
@Tag(name = "Admin Users")
public class AdminUserController {
    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    @Operation(summary = "Get all users")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Page<AdminUserResponse>>> findAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        List<String> allowed = List.of("id", "email", "name", "role", "active");
        if (!allowed.contains(sortBy)) sortBy = "id";

        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC,
            sortBy
        );

        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Users retrieved successfully",
                        HttpStatus.OK.value(),
                        Instant.now().toString(),
                        adminUserService.findAll(pageable)
                )
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<AdminUserResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "User retrieved successfully",
                        HttpStatus.OK.value(),
                        Instant.now().toString(),
                        adminUserService.findById(id)
                )
        );
    }
}
