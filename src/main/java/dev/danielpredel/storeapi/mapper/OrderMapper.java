package dev.danielpredel.storeapi.mapper;

import dev.danielpredel.storeapi.dto.AdminOrderResponse;
import dev.danielpredel.storeapi.dto.OrderItemResponse;
import dev.danielpredel.storeapi.dto.OrderResponse;
import dev.danielpredel.storeapi.order.entity.Order;
import dev.danielpredel.storeapi.order.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {
    public OrderResponse toOrderResponse(Order order) {
        List<OrderItemResponse> items = order.getOrderItems()
                .stream()
                .map(this::toOrderItemResponse)
                .toList();
        return new OrderResponse(order.getId(), items, order.getTotalAmount(), order.getPurchaseDate(), order.getStatus());
    }

    public OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getQuantity(),
                orderItem.getPriceAtPurchase());
    }

    public AdminOrderResponse toAdminOrderResponse(Order order) {
        List<OrderItemResponse> items = order.getOrderItems()
                .stream()
                .map(this::toOrderItemResponse)
                .toList();
        return new AdminOrderResponse(order.getId(), order.getUser().getId(), items, order.getTotalAmount(), order.getPurchaseDate(), order.getStatus());
    }
}
