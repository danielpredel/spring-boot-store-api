package dev.danielpredel.userapibasic.controller;

import dev.danielpredel.userapibasic.dto.ProductRequest;
import dev.danielpredel.userapibasic.dto.ProductResponse;
import dev.danielpredel.userapibasic.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest dto) {
        ProductResponse savedProduct = productService.save(dto);

        URI location = URI.create("/api/products/" + savedProduct.id());

        return ResponseEntity.created(location).body(savedProduct);
    }
}
