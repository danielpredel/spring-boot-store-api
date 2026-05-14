package dev.danielpredel.userapibasic.service.impl;

import dev.danielpredel.userapibasic.dto.AdminProductResponse;
import dev.danielpredel.userapibasic.dto.ProductRequest;
import dev.danielpredel.userapibasic.entity.Product;
import dev.danielpredel.userapibasic.mapper.ProductMapper;
import dev.danielpredel.userapibasic.repository.ProductRepository;
import dev.danielpredel.userapibasic.service.AdminProductService;
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
}
