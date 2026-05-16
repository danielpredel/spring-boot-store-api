package dev.danielpredel.storeapi.service.impl;

import dev.danielpredel.storeapi.order.dto.admin.AdminOrderResponse;
import dev.danielpredel.storeapi.order.entity.Order;
import dev.danielpredel.storeapi.enums.OrderStatus;
import dev.danielpredel.storeapi.exception.InvalidOrderStateException;
import dev.danielpredel.storeapi.exception.ResourceNotFoundException;
import dev.danielpredel.storeapi.order.mapper.OrderMapper;
import dev.danielpredel.storeapi.order.repository.OrderRepository;
import dev.danielpredel.storeapi.service.AdminOrderService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdminOrderServiceImpl implements AdminOrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public AdminOrderServiceImpl(
            OrderRepository orderRepository,
            OrderMapper orderMapper
    ) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Page<AdminOrderResponse> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderMapper::toAdminOrderResponse);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public AdminOrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order Not Found"));

        return orderMapper.toAdminOrderResponse(order);
    }

    @Override
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
