package dev.danielpredel.userapibasic.mapper;

import dev.danielpredel.userapibasic.dto.AdminProductResponse;
import dev.danielpredel.userapibasic.dto.ProductRequest;
import dev.danielpredel.userapibasic.dto.ProductResponse;
import dev.danielpredel.userapibasic.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getPrice(), product.getStock(), product.getImageUrl());
    }

    public Product toEntity(ProductRequest dto) {
        return new Product(dto.name(), dto.price(), dto.stock(), dto.imageUrl(), dto.active());
    }

    public AdminProductResponse toAdminResponse(Product product) {
        return new AdminProductResponse(product.getId(), product.getName(), product.getPrice(), product.getStock(), product.getImageUrl(), product.isActive());
    }
}
