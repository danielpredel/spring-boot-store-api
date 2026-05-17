package dev.danielpredel.storeapi.product.service;

import dev.danielpredel.storeapi.product.dto.admin.AdminProductResponse;
import dev.danielpredel.storeapi.product.dto.ProductRequest;
import dev.danielpredel.storeapi.product.entity.Product;
import dev.danielpredel.storeapi.common.exception.ResourceNotFoundException;
import dev.danielpredel.storeapi.product.mapper.ProductMapper;
import dev.danielpredel.storeapi.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdminProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public AdminProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public AdminProductResponse save(ProductRequest dto) {
        Product product = productMapper.toEntity(dto);
        product = productRepository.save(product);
        return productMapper.toAdminResponse(product);
    }

    public Page<AdminProductResponse> findAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toAdminResponse);
    }

    public AdminProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product Not Found"));

        return productMapper.toAdminResponse(product);
    }

    @Transactional
    public AdminProductResponse update(Long id, ProductRequest dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product Not Found"));

        product.setName(dto.name());
        product.setPrice(dto.price());
        product.setStock(dto.stock());
        product.setImageUrl(dto.imageUrl());
        product.setActive(dto.active());

        return productMapper.toAdminResponse(product);
    }
}
