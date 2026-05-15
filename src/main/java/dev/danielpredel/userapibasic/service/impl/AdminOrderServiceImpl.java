package dev.danielpredel.userapibasic.service.impl;

import dev.danielpredel.userapibasic.dto.*;
import dev.danielpredel.userapibasic.mapper.OrderMapper;
import dev.danielpredel.userapibasic.repository.OrderRepository;
import dev.danielpredel.userapibasic.service.AdminOrderService;
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
}
