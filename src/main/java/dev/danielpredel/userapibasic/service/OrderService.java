package dev.danielpredel.userapibasic.service;

import dev.danielpredel.userapibasic.dto.OrderRequest;
import dev.danielpredel.userapibasic.dto.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse save(OrderRequest dto);
    Page<OrderResponse> findAll(Pageable pageable);
}
