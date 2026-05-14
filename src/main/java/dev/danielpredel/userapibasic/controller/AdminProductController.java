package dev.danielpredel.userapibasic.controller;

import dev.danielpredel.userapibasic.dto.AdminProductResponse;
import dev.danielpredel.userapibasic.dto.ProductRequest;
import dev.danielpredel.userapibasic.service.AdminProductService;
import dev.danielpredel.userapibasic.service.impl.AdminProductServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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
}
