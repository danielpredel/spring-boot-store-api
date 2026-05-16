package dev.danielpredel.storeapi.service.impl;

import dev.danielpredel.storeapi.dto.AdminProductResponse;
import dev.danielpredel.storeapi.dto.ProductRequest;
import dev.danielpredel.storeapi.product.entity.Product;
import dev.danielpredel.storeapi.exception.ResourceNotFoundException;
import dev.danielpredel.storeapi.product.mapper.ProductMapper;
import dev.danielpredel.storeapi.product.repository.ProductRepository;
import dev.danielpredel.storeapi.service.AdminProductService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdminProductServiceImpl implements AdminProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public AdminProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public AdminProductResponse save(ProductRequest dto) {
        Product product = productMapper.toEntity(dto);
        product = productRepository.save(product);
        return productMapper.toAdminResponse(product);
    }

    @Override
    public Page<AdminProductResponse> findAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toAdminResponse);
    }

    @Override
    public AdminProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product Not Found"));

        return productMapper.toAdminResponse(product);
    }

    @Override
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
