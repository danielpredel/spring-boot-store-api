package dev.danielpredel.userapibasic.service;

import dev.danielpredel.userapibasic.dto.AdminProductResponse;
import dev.danielpredel.userapibasic.dto.ProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminProductService {
    AdminProductResponse save(ProductRequest dto);
    Page<AdminProductResponse> findAll(Pageable pageable);
}
