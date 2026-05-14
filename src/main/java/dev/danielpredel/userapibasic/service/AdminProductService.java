package dev.danielpredel.userapibasic.service;

import dev.danielpredel.userapibasic.dto.AdminProductResponse;
import dev.danielpredel.userapibasic.dto.ProductRequest;

public interface AdminProductService {
    AdminProductResponse save(ProductRequest dto);
}
