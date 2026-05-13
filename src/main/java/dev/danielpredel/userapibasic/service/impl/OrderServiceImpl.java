package dev.danielpredel.userapibasic.service.impl;

import dev.danielpredel.userapibasic.dto.*;
import dev.danielpredel.userapibasic.entity.Order;
import dev.danielpredel.userapibasic.entity.OrderItem;
import dev.danielpredel.userapibasic.entity.Product;
import dev.danielpredel.userapibasic.entity.User;
import dev.danielpredel.userapibasic.enums.OrderStatus;
import dev.danielpredel.userapibasic.exception.InsufficientStockException;
import dev.danielpredel.userapibasic.exception.InvalidOrderStateException;
import dev.danielpredel.userapibasic.exception.ResourceNotFoundException;
import dev.danielpredel.userapibasic.mapper.OrderMapper;
import dev.danielpredel.userapibasic.repository.OrderRepository;
import dev.danielpredel.userapibasic.repository.ProductRepository;
import dev.danielpredel.userapibasic.repository.UserRepository;
import dev.danielpredel.userapibasic.security.CustomUserDetails;
import dev.danielpredel.userapibasic.service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            Product product = productRepository.findById(item.productId())
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
    public Page<OrderResponse> findAll(CustomUserDetails user, Pageable pageable) {
        if (user.isAdmin()) {
            return orderRepository.findAll(pageable)
                    .map(orderMapper::toOrderResponse);
        }
        else {
            return orderRepository.findByUserId(user.getId(), pageable)
                    .map(orderMapper::toOrderResponse);
        }
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public OrderResponse findById(CustomUserDetails user, Long id) {
        Order order = user.isAdmin()
                ? orderRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found"))
                : orderRepository.findByIdAndUserId(id, user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        return orderMapper.toOrderResponse(order);
    }

    @Override
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
    @Transactional
    public OrderResponse deliver(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if(!order.getStatus().equals(OrderStatus.CREATED)) {
            throw new InvalidOrderStateException("Invalid order status transition");
        }

        order.setStatus(OrderStatus.DELIVERED);

        return  orderMapper.toOrderResponse(order);
    }

    @Override
    public OrderPreviewResponse preview(OrderPreviewRequest dto) {
        List<OrderItemPreviewResponse> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest item: dto.items()) {
            Product product = productRepository.findById(item.productId())
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
