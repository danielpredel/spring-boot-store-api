package dev.danielpredel.storeapi.order.service;

import dev.danielpredel.storeapi.auth.security.AuthenticationFacade;
import dev.danielpredel.storeapi.order.dto.*;
import dev.danielpredel.storeapi.order.entity.Order;
import dev.danielpredel.storeapi.order.entity.OrderItem;
import dev.danielpredel.storeapi.product.entity.Product;
import dev.danielpredel.storeapi.product.service.AdminProductService;
import dev.danielpredel.storeapi.user.entity.User;
import dev.danielpredel.storeapi.common.enums.OrderStatus;
import dev.danielpredel.storeapi.common.exception.InsufficientStockException;
import dev.danielpredel.storeapi.common.exception.InvalidOrderStateException;
import dev.danielpredel.storeapi.common.exception.ResourceNotFoundException;
import dev.danielpredel.storeapi.order.mapper.OrderMapper;
import dev.danielpredel.storeapi.order.repository.OrderRepository;
import dev.danielpredel.storeapi.product.repository.ProductRepository;
import dev.danielpredel.storeapi.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final AuthenticationFacade authenticationFacade;
    private static final Logger log =
            LoggerFactory.getLogger(OrderService.class);

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            OrderMapper orderMapper,
            AuthenticationFacade authenticationFacade
    ) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderMapper = orderMapper;
        this.authenticationFacade = authenticationFacade;
    }

    @Transactional
    public OrderResponse save(OrderRequest dto) {
        Long userId = authenticationFacade.getCurrentUserId();
        User user = userRepository.findByIdAndActiveTrue(userId)
                .orElseThrow(() -> {
                    log.warn("Inactive or non existent user {} tried to create and order", userId);

                    return new ResourceNotFoundException("User not found");
                });

        Order order = new Order(LocalDateTime.now(), OrderStatus.CREATED);
        List<OrderItem> items = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItemRequest item: dto.items()) {
            Product product = productRepository.findByIdAndActiveTrue(item.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product with id " + item.productId() + " not found"));

            if (product.getStock() < item.quantity()) {
                log.warn(
                        "User {} requested {} units of product {} but only {} available",
                        userId,
                        item.quantity(),
                        item.productId(),
                        product.getStock()
                );

                throw new InsufficientStockException("Requested quantity exceeds stock for product " + item.productId());
            }

            product.setStock(product.getStock() - item.quantity());

            OrderItem orderItem = new OrderItem(product.getName(), item.quantity(), product.getPrice());
            orderItem.setOrder(order);
            orderItem.setProduct(product);

            totalPrice = totalPrice.add(product.getPrice().multiply(new BigDecimal(item.quantity())));

            items.add(orderItem);
        }

        order.setUser(user);
        order.setOrderItems(items);
        order.setTotalAmount(totalPrice);

        order = orderRepository.save(order);

        return orderMapper.toOrderResponse(order);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Page<OrderResponse> findAll(Pageable pageable) {
        Long id = authenticationFacade.getCurrentUserId();

        return orderRepository.findByUserId(id, pageable)
                    .map(orderMapper::toOrderResponse);
    }

    @PreAuthorize("@orderSecurity.isOwner(#id, authentication.principal.id)")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Order Not Found"));

        return orderMapper.toOrderResponse(order);
    }

    @PreAuthorize("@orderSecurity.isOwner(#id, authentication.principal.id)")
    @Transactional
    public OrderResponse cancel(Long id) {
        Long userId = authenticationFacade.getCurrentUserId();

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User {} tried to cancel a non existent order {}", userId, id);

                    return new ResourceNotFoundException("Order not found");
                });

        if(!order.getStatus().equals(OrderStatus.CREATED)) {
            log.warn("User {} tried to cancel the order {} with status {}", userId, id, order.getStatus());

            throw new InvalidOrderStateException("Invalid order status transition");
        }

        for (OrderItem orderItem: order.getOrderItems()) {
            orderItem.getProduct().setStock(orderItem.getProduct().getStock() + orderItem.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);

        log.info("User {} canceled the order {}", userId, id);

        return  orderMapper.toOrderResponse(order);
    }

    public OrderPreviewResponse preview(OrderPreviewRequest dto) {
        List<OrderItemPreviewResponse> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest item: dto.items()) {
            Product product = productRepository.findByIdAndActiveTrue(item.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product with id " + item.productId() + " not found"));

            OrderItemPreviewResponse orderItem = new OrderItemPreviewResponse(
                    product.getId(),
                    product.getName(),
                    item.quantity(),
                    product.getStock(),
                    product.getPrice(),
                    product.getPrice().multiply(new BigDecimal(item.quantity())));

            totalAmount = totalAmount.add(orderItem.subtotal());

            items.add(orderItem);
        }

        return new OrderPreviewResponse(items, totalAmount);
    }
}
