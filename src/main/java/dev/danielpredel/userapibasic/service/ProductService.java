package dev.danielpredel.userapibasic.service;

import dev.danielpredel.userapibasic.dto.ProductRequest;
import dev.danielpredel.userapibasic.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponse save(ProductRequest dto);
    Page<ProductResponse> findAll(Pageable pageable);
    ProductResponse findById(Long id);
    ProductResponse update(Long id, ProductRequest dto);
    void deleteById(Long id);
}
