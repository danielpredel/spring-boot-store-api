package dev.danielpredel.storeapi.product.unit;

import dev.danielpredel.storeapi.auth.security.AuthenticationFacade;
import dev.danielpredel.storeapi.common.exception.ResourceNotFoundException;
import dev.danielpredel.storeapi.product.dto.ProductRequest;
import dev.danielpredel.storeapi.product.dto.admin.AdminProductResponse;
import dev.danielpredel.storeapi.product.entity.Product;
import dev.danielpredel.storeapi.product.mapper.ProductMapper;
import dev.danielpredel.storeapi.product.repository.ProductRepository;
import dev.danielpredel.storeapi.product.service.AdminProductService;
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
public class AdminProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @InjectMocks
    private AdminProductService adminProductService;

    @Test
    void shouldSaveProduct() {
        ProductRequest request =
                new ProductRequest("Laptop", BigDecimal.valueOf(1200),  5,  "image.jpg",  true);

        Product product = mock(Product.class);

        when(product.getId()).thenReturn(1L);

        AdminProductResponse response =
                new AdminProductResponse(1L, "Laptop", BigDecimal.valueOf(1200), 5, "image.jpg", true);

        when(productMapper.toEntity(request))
                .thenReturn(product);

        when(productRepository.save(product))
                .thenReturn(product);

        when(productMapper.toAdminResponse(product))
                .thenReturn(response);

        when(authenticationFacade.getCurrentUserId())
                .thenReturn(99L);

        AdminProductResponse result = adminProductService.save(request);

        assertNotNull(result);

        verify(productMapper).toEntity(request);
        verify(productRepository).save(product);
        verify(productMapper).toAdminResponse(product);
        verify(authenticationFacade).getCurrentUserId();
    }

    @Test
    void shouldFindAllProducts() {
        Pageable pageable = PageRequest.of(0, 10);

        Product product = new Product();

        AdminProductResponse response =
                new AdminProductResponse(1L, "Laptop", BigDecimal.valueOf(1200), 5, "image.jpg", true);

        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productRepository.findAll(pageable))
                .thenReturn(productPage);

        when(productMapper.toAdminResponse(product))
                .thenReturn(response);

        Page<AdminProductResponse> result =
                adminProductService.findAll(pageable);

        assertEquals(1, result.getTotalElements());

        verify(productRepository).findAll(pageable);
        verify(productMapper).toAdminResponse(product);
    }

    @Test
    void shouldFindProductById() {
        Long id = 1L;

        Product product = new Product();

        AdminProductResponse response =
                new AdminProductResponse(1L, "Laptop", BigDecimal.valueOf(1200), 5, "image.jpg", true);

        when(productRepository.findById(id))
                .thenReturn(Optional.of(product));

        when(productMapper.toAdminResponse(product))
                .thenReturn(response);

        AdminProductResponse result =
                adminProductService.findById(id);

        assertNotNull(result);

        verify(productRepository).findById(id);
        verify(productMapper).toAdminResponse(product);
    }

    @Test
    void shouldThrowExceptionWhenFindByIdProductNotFound() {
        Long id = 1L;

        when(productRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> adminProductService.findById(id));

        verify(productRepository).findById(id);
        verifyNoInteractions(productMapper);
    }

    @Test
    void shouldUpdateProduct() {
        Long id = 1L;

        ProductRequest request =
                new ProductRequest("Updated Laptop", BigDecimal.valueOf(1500), 10, "updated.jpg", true);

        Product product = new Product();

        AdminProductResponse response =
                new AdminProductResponse(id, "Updated Laptop", BigDecimal.valueOf(1500), 10, "updated.jpg", true);

        when(productRepository.findById(id))
                .thenReturn(Optional.of(product));

        when(productMapper.toAdminResponse(product))
                .thenReturn(response);

        when(authenticationFacade.getCurrentUserId())
                .thenReturn(99L);

        AdminProductResponse result =
                adminProductService.update(id, request);

        assertNotNull(result);

        assertEquals("Updated Laptop", product.getName());
        assertEquals(BigDecimal.valueOf(1500), product.getPrice());
        assertEquals(10, product.getStock());
        assertEquals("updated.jpg", product.getImageUrl());
        assertTrue(product.isActive());

        verify(productRepository).findById(id);
        verify(productMapper).toAdminResponse(product);
        verify(authenticationFacade).getCurrentUserId();
    }

    @Test
    void shouldThrowExceptionWhenUpdatingMissingProduct() {
        Long id = 1L;

        ProductRequest request =
                new ProductRequest("Laptop", BigDecimal.valueOf(1200), 5, "image.jpg", true);

        when(productRepository.findById(id))
                .thenReturn(Optional.empty());

        when(authenticationFacade.getCurrentUserId())
                .thenReturn(99L);

        assertThrows(ResourceNotFoundException.class,
                () -> adminProductService.update(id, request));

        verify(productRepository).findById(id);
        verify(authenticationFacade).getCurrentUserId();
        verifyNoInteractions(productMapper);
    }
}
