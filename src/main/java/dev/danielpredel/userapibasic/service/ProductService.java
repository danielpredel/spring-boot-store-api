package dev.danielpredel.userapibasic.service;

import dev.danielpredel.userapibasic.dto.ProductRequest;
import dev.danielpredel.userapibasic.dto.ProductResponse;

public interface ProductService {
    ProductResponse save(ProductRequest dto);
}
