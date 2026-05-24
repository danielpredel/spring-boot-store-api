package dev.danielpredel.storeapi.order.service;

import dev.danielpredel.storeapi.auth.security.AuthenticationFacade;
import dev.danielpredel.storeapi.order.dto.admin.AdminOrderResponse;
import dev.danielpredel.storeapi.order.entity.Order;
import dev.danielpredel.storeapi.common.enums.OrderStatus;
import dev.danielpredel.storeapi.common.exception.InvalidOrderStateException;
import dev.danielpredel.storeapi.common.exception.ResourceNotFoundException;
import dev.danielpredel.storeapi.order.mapper.OrderMapper;
import dev.danielpredel.storeapi.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdminOrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final AuthenticationFacade authenticationFacade;
    private static final Logger log =
            LoggerFactory.getLogger(AdminOrderService.class);

    public AdminOrderService(
            OrderRepository orderRepository,
            OrderMapper orderMapper,
            AuthenticationFacade authenticationFacade
    ) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.authenticationFacade = authenticationFacade;
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
                .orElseThrow(() -> {
                    log.warn("Admin {} tried to deliver a missing order {}", authenticationFacade.getCurrentUserId(), id);

                    return new ResourceNotFoundException("Order not found");
                });

        if(!order.getStatus().equals(OrderStatus.CREATED)) {
            log.warn("Admin {} tried to deliver the order {} with status {}", authenticationFacade.getCurrentUserId(), id, order.getStatus());

            throw new InvalidOrderStateException("Invalid order status transition");
        }

        order.setStatus(OrderStatus.DELIVERED);

        log.info("Admin {} delivered the order {}", authenticationFacade.getCurrentUserId(), order.getId());

        return orderMapper.toAdminOrderResponse(order);
    }
}
