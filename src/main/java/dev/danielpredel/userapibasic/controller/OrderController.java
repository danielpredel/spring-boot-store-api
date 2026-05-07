package dev.danielpredel.userapibasic.controller;

import dev.danielpredel.userapibasic.dto.OrderRequest;
import dev.danielpredel.userapibasic.dto.OrderResponse;
import dev.danielpredel.userapibasic.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest dto) {
        OrderResponse orderResponse = orderService.save(dto);

        URI location = URI.create("/api/orders" + orderResponse.id());

        return ResponseEntity.created(location).body(orderResponse);
    }
}
