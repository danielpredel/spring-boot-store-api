package dev.danielpredel.storeapi.service;

import dev.danielpredel.storeapi.order.dto.admin.AdminOrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminOrderService {
    Page<AdminOrderResponse> findAll(Pageable pageable);
    AdminOrderResponse findById(Long id);
    AdminOrderResponse deliver(Long id);
}
