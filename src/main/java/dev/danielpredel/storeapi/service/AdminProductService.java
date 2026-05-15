package dev.danielpredel.storeapi.service;

import dev.danielpredel.storeapi.dto.AdminProductResponse;
import dev.danielpredel.storeapi.dto.ProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminProductService {
    AdminProductResponse save(ProductRequest dto);
    Page<AdminProductResponse> findAll(Pageable pageable);
    AdminProductResponse findById(Long id);
    AdminProductResponse update(Long id, ProductRequest dto);
}
