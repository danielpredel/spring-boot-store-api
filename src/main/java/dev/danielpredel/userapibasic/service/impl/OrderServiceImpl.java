package dev.danielpredel.userapibasic.service.impl;

import dev.danielpredel.userapibasic.dto.OrderItemRequest;
import dev.danielpredel.userapibasic.dto.OrderRequest;
import dev.danielpredel.userapibasic.dto.OrderResponse;
import dev.danielpredel.userapibasic.entity.Order;
import dev.danielpredel.userapibasic.entity.OrderItem;
import dev.danielpredel.userapibasic.entity.Product;
import dev.danielpredel.userapibasic.entity.User;
import dev.danielpredel.userapibasic.enums.OrderStatus;
import dev.danielpredel.userapibasic.exception.InsufficientStockException;
import dev.danielpredel.userapibasic.exception.ResourceNotFoundException;
import dev.danielpredel.userapibasic.mapper.OrderMapper;
import dev.danielpredel.userapibasic.repository.OrderRepository;
import dev.danielpredel.userapibasic.repository.ProductRepository;
import dev.danielpredel.userapibasic.repository.UserRepository;
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
    public OrderResponse save(OrderRequest dto) {
        User user = userRepository.findById(dto.userId())
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

            totalPrice = totalPrice.add(product.getPrice());

            items.add(orderItem);
        }

        order.setUser(user);
        order.setOrderItems(items);
        order.setTotalAmount(totalPrice);

        order = orderRepository.save(order);

        return orderMapper.toResponse(order);
    }

    @Override
    public Page<OrderResponse> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        return orderMapper.toResponse(order);
    }
}
