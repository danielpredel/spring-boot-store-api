package dev.danielpredel.userapibasic.service;

import dev.danielpredel.userapibasic.dto.OrderPreviewRequest;
import dev.danielpredel.userapibasic.dto.OrderPreviewResponse;
import dev.danielpredel.userapibasic.dto.OrderRequest;
import dev.danielpredel.userapibasic.dto.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse save(Long id, OrderRequest dto);
    Page<OrderResponse> findAll(Long userId, Pageable pageable);
    OrderResponse findById(Long id);
    OrderResponse cancel(Long id);
    OrderPreviewResponse preview(OrderPreviewRequest dto);
}
