package dev.danielpredel.storeapi.product.unit;

import dev.danielpredel.storeapi.common.exception.ResourceNotFoundException;
import dev.danielpredel.storeapi.product.dto.ProductResponse;
import dev.danielpredel.storeapi.product.entity.Product;
import dev.danielpredel.storeapi.product.mapper.ProductMapper;
import dev.danielpredel.storeapi.product.repository.ProductRepository;
import dev.danielpredel.storeapi.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldFindAllActiveProducts() {
        Pageable pageable = PageRequest.of(0, 10);

        Product product = new Product();
        ProductResponse response =
                new ProductResponse(1L, "Laptop", BigDecimal.valueOf(1200), 5, "image.jpg");

        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productRepository.findByActiveTrue(pageable))
                .thenReturn(productPage);

        when(productMapper.toResponse(product))
                .thenReturn(response);

        Page<ProductResponse> result = productService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        verify(productRepository).findByActiveTrue(pageable);
        verify(productMapper).toResponse(product);
    }

    @Test
    void shouldFindProductById() {
        Long id = 1L;

        Product product = new Product();
        ProductResponse response =
                new ProductResponse(1L, "Laptop", BigDecimal.valueOf(1200), 5, "image.jpg");

        when(productRepository.findByIdAndActiveTrue(id))
                .thenReturn(Optional.of(product));

        when(productMapper.toResponse(product))
                .thenReturn(response);

        ProductResponse result = productService.findById(id);

        assertNotNull(result);
        verify(productRepository).findByIdAndActiveTrue(id);
        verify(productMapper).toResponse(product);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        Long id = 1L;

        when(productRepository.findByIdAndActiveTrue(id))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.findById(id));

        verify(productRepository).findByIdAndActiveTrue(id);
        verifyNoInteractions(productMapper);
    }
}
