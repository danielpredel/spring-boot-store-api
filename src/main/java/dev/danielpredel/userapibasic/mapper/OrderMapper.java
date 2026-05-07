package dev.danielpredel.userapibasic.mapper;

import dev.danielpredel.userapibasic.dto.OrderItemResponse;
import dev.danielpredel.userapibasic.dto.OrderResponse;
import dev.danielpredel.userapibasic.entity.Order;
import dev.danielpredel.userapibasic.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {
    public OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getOrderItems()
                .stream()
                .map(this::toResponse)
                .toList();
        return new OrderResponse(order.getId(), items, order.getTotalAmount(), order.getPurchaseDate(), order.getStatus());
    }

    public OrderItemResponse toResponse(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getQuantity(),
                orderItem.getPriceAtPurchase());
    }
}
