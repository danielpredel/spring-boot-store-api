package dev.danielpredel.userapibasic.service;

import dev.danielpredel.userapibasic.dto.OrderPreviewRequest;
import dev.danielpredel.userapibasic.dto.OrderPreviewResponse;
import dev.danielpredel.userapibasic.dto.OrderRequest;
import dev.danielpredel.userapibasic.dto.OrderResponse;
import dev.danielpredel.userapibasic.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse save(Long id, OrderRequest dto);
    Page<OrderResponse> findAll(CustomUserDetails user, Pageable pageable);
    OrderResponse findById(CustomUserDetails user, Long id);
    OrderResponse cancel(CustomUserDetails user, Long id);
    OrderResponse deliver(Long id);
    OrderPreviewResponse preview(OrderPreviewRequest dto);
}
