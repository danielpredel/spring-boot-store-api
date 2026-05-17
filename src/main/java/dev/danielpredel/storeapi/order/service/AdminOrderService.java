package dev.danielpredel.storeapi.order.service;

import dev.danielpredel.storeapi.order.dto.admin.AdminOrderResponse;
import dev.danielpredel.storeapi.order.entity.Order;
import dev.danielpredel.storeapi.common.enums.OrderStatus;
import dev.danielpredel.storeapi.common.exception.InvalidOrderStateException;
import dev.danielpredel.storeapi.common.exception.ResourceNotFoundException;
import dev.danielpredel.storeapi.order.mapper.OrderMapper;
import dev.danielpredel.storeapi.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdminOrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public AdminOrderService(
            OrderRepository orderRepository,
            OrderMapper orderMapper
    ) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Page<AdminOrderResponse> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderMapper::toAdminOrderResponse);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public AdminOrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order Not Found"));

        return orderMapper.toAdminOrderResponse(order);
    }

    @Transactional
    public AdminOrderResponse deliver(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if(!order.getStatus().equals(OrderStatus.CREATED)) {
            throw new InvalidOrderStateException("Invalid order status transition");
        }

        order.setStatus(OrderStatus.DELIVERED);

        return  orderMapper.toAdminOrderResponse(order);
    }
}
