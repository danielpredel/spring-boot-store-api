package dev.danielpredel.storeapi.service.impl;

import dev.danielpredel.storeapi.dto.*;
import dev.danielpredel.storeapi.order.entity.Order;
import dev.danielpredel.storeapi.order.entity.OrderItem;
import dev.danielpredel.storeapi.product.entity.Product;
import dev.danielpredel.storeapi.user.entity.User;
import dev.danielpredel.storeapi.enums.OrderStatus;
import dev.danielpredel.storeapi.exception.InsufficientStockException;
import dev.danielpredel.storeapi.exception.InvalidOrderStateException;
import dev.danielpredel.storeapi.exception.ResourceNotFoundException;
import dev.danielpredel.storeapi.mapper.OrderMapper;
import dev.danielpredel.storeapi.repository.OrderRepository;
import dev.danielpredel.storeapi.product.repository.ProductRepository;
import dev.danielpredel.storeapi.user.repository.UserRepository;
import dev.danielpredel.storeapi.service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            OrderMapper orderMapper
    ) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional
    public OrderResponse save(Long id, OrderRequest dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = new Order(LocalDateTime.now(), OrderStatus.CREATED);
        List<OrderItem> items = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItemRequest item: dto.items()) {
            Product product = productRepository.findByIdAndActiveTrue(item.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product with id " + item.productId() + " not found"));

            if (product.getStock() < item.quantity()) {
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

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Page<OrderResponse> findAll(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                    .map(orderMapper::toOrderResponse);
    }

    @Override
    @PreAuthorize("@orderSecurity.isOwner(#id, authentication.principal.id)")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Order Not Found"));

        return orderMapper.toOrderResponse(order);
    }

    @Override
    @PreAuthorize("@orderSecurity.isOwner(#id, authentication.principal.id)")
    @Transactional
    public OrderResponse cancel(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if(!order.getStatus().equals(OrderStatus.CREATED)) {
            throw new InvalidOrderStateException("Invalid order status transition");
        }

        for (OrderItem orderItem: order.getOrderItems()) {
            orderItem.getProduct().setStock(orderItem.getProduct().getStock() + orderItem.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);

        return  orderMapper.toOrderResponse(order);
    }

    @Override
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
