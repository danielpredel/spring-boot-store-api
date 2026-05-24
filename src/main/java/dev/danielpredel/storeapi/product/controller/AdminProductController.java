package dev.danielpredel.storeapi.product.controller;

import dev.danielpredel.storeapi.common.dto.ApiResponse;
import dev.danielpredel.storeapi.product.dto.admin.AdminProductResponse;
import dev.danielpredel.storeapi.product.dto.ProductRequest;
import dev.danielpredel.storeapi.product.service.AdminProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@Validated
@RestController
@RequestMapping("/admin/products")
@Tag(name = "Admin Products")
public class AdminProductController {
    private final AdminProductService adminProductService;

    public AdminProductController(AdminProductService adminProductService) {
        this.adminProductService = adminProductService;
    }

    @PostMapping
    @Operation(summary = "Create product")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<AdminProductResponse>> create(@Valid @RequestBody ProductRequest dto) {
        AdminProductResponse product = adminProductService.save(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(product.id())
                .toUri();

        return ResponseEntity.created(location).body(
                new ApiResponse<>(
                        "Product created successfully",
                        HttpStatus.CREATED.value(),
                        Instant.now().toString(),
                        product
                )
        );
    }

    @GetMapping
    @Operation(summary = "Get all products")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Page<AdminProductResponse>>> findAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        List<String> allowed = List.of("id", "name", "price", "stock", "active");
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
                        "Products retrieved successfully",
                        HttpStatus.OK.value(),
                        Instant.now().toString(),
                        adminProductService.findAll(pageable)
                )
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by id")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<AdminProductResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Product retrieved successfully",
                        HttpStatus.OK.value(),
                        Instant.now().toString(),
                        adminProductService.findById(id)
                )
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<AdminProductResponse>> update(@PathVariable Long id, @Valid @RequestBody ProductRequest dto) {
        return  ResponseEntity.ok(
                new ApiResponse<>(
                        "Product updated successfully",
                        HttpStatus.OK.value(),
                        Instant.now().toString(),
                        adminProductService.update(id, dto)
                )
        );
    }
}
