package dev.danielpredel.userapibasic.service;

import dev.danielpredel.userapibasic.dto.OrderRequest;
import dev.danielpredel.userapibasic.dto.OrderResponse;

public interface OrderService {
    OrderResponse save(OrderRequest dto);
}
