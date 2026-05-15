package dev.danielpredel.userapibasic.service;

import dev.danielpredel.userapibasic.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductResponse> findAll(Pageable pageable);
    ProductResponse findById(Long id);
}
