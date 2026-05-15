package dev.danielpredel.userapibasic.service;

import dev.danielpredel.userapibasic.dto.AdminOrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminOrderService {
    Page<AdminOrderResponse> findAll(Pageable pageable);
}
