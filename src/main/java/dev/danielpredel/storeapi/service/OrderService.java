package dev.danielpredel.storeapi.service;

import dev.danielpredel.storeapi.order.dto.OrderPreviewRequest;
import dev.danielpredel.storeapi.order.dto.OrderPreviewResponse;
import dev.danielpredel.storeapi.order.dto.OrderRequest;
import dev.danielpredel.storeapi.order.dto.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse save(Long id, OrderRequest dto);
    Page<OrderResponse> findAll(Long userId, Pageable pageable);
    OrderResponse findById(Long id);
    OrderResponse cancel(Long id);
    OrderPreviewResponse preview(OrderPreviewRequest dto);
}
