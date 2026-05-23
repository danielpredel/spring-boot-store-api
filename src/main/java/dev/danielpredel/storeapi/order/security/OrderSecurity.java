package dev.danielpredel.storeapi.order.security;

import dev.danielpredel.storeapi.common.exception.ResourceNotFoundException;
import dev.danielpredel.storeapi.order.repository.OrderRepository;
import org.springframework.stereotype.Component;

@Component
public class OrderSecurity {
    private final OrderRepository orderRepository;

    public OrderSecurity(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public boolean isOwner(Long orderId, Long userId) {
        return orderRepository.findById(orderId)
                .map(order -> order.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Order Not Found"));
    }
}
