package dev.danielpredel.storeapi.product.mapper;

import dev.danielpredel.storeapi.product.dto.admin.AdminProductResponse;
import dev.danielpredel.storeapi.product.dto.ProductRequest;
import dev.danielpredel.storeapi.product.dto.ProductResponse;
import dev.danielpredel.storeapi.product.entity.Product;
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
