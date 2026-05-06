package dev.danielpredel.userapibasic.service.impl;

import dev.danielpredel.userapibasic.dto.ProductRequest;
import dev.danielpredel.userapibasic.dto.ProductResponse;
import dev.danielpredel.userapibasic.entity.Product;
import dev.danielpredel.userapibasic.exception.ResourceNotFoundException;
import dev.danielpredel.userapibasic.mapper.ProductMapper;
import dev.danielpredel.userapibasic.repository.ProductRepository;
import dev.danielpredel.userapibasic.service.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public ProductResponse save(ProductRequest dto) {
        Product product = productMapper.toEntity(dto);
        product = productRepository.save(product);
        return productMapper.toResponse(product);
    }

    @Override
    public Page<ProductResponse> findAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toResponse);
    }

    @Override
    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product Not Found"));

        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product Not Found"));

        product.setName(dto.name());
        product.setPrice(dto.price());
        product.setStock(dto.stock());
        product.setImageUrl(dto.imageUrl());
        product.setActive(dto.active());

        return productMapper.toResponse(product);
    }

    @Override
    public void deleteById(Long id) {
        if(!productRepository.existsById(id)){
            throw new ResourceNotFoundException("Product Not Found");
        }

        productRepository.deleteById(id);
    }
}
