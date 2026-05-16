package dev.danielpredel.storeapi.product.controller.admin;

import dev.danielpredel.storeapi.product.dto.admin.AdminProductResponse;
import dev.danielpredel.storeapi.product.dto.ProductRequest;
import dev.danielpredel.storeapi.service.AdminProductService;
import dev.danielpredel.storeapi.service.impl.AdminProductServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Validated
@RestController
@RequestMapping("/admin/products")
public class AdminProductController {
    private final AdminProductService adminProductService;

    public AdminProductController(AdminProductServiceImpl adminProductService) {
        this.adminProductService = adminProductService;
    }

    @PostMapping
    public ResponseEntity<AdminProductResponse> create(@Valid @RequestBody ProductRequest dto) {
        AdminProductResponse product = adminProductService.save(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(product.id())
                .toUri();

        return ResponseEntity.created(location).body(product);
    }

    @GetMapping
    public ResponseEntity<Page<AdminProductResponse>> findAll(
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
        return ResponseEntity.ok(adminProductService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminProductResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(adminProductService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminProductResponse> update(@PathVariable Long id, @Valid @RequestBody ProductRequest dto) {
        return  ResponseEntity.ok(adminProductService.update(id, dto));
    }
}
