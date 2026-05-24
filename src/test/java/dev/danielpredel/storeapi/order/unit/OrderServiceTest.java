package dev.danielpredel.storeapi.order.unit;

import dev.danielpredel.storeapi.auth.security.AuthenticationFacade;
import dev.danielpredel.storeapi.common.enums.OrderStatus;
import dev.danielpredel.storeapi.common.enums.Role;
import dev.danielpredel.storeapi.common.exception.InsufficientStockException;
import dev.danielpredel.storeapi.common.exception.InvalidOrderStateException;
import dev.danielpredel.storeapi.common.exception.ResourceNotFoundException;
import dev.danielpredel.storeapi.order.dto.*;
import dev.danielpredel.storeapi.order.entity.Order;
import dev.danielpredel.storeapi.order.entity.OrderItem;
import dev.danielpredel.storeapi.order.mapper.OrderMapper;
import dev.danielpredel.storeapi.order.repository.OrderRepository;
import dev.danielpredel.storeapi.order.service.OrderService;
import dev.danielpredel.storeapi.product.entity.Product;
import dev.danielpredel.storeapi.product.repository.ProductRepository;
import dev.danielpredel.storeapi.user.entity.User;
import dev.danielpredel.storeapi.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        user = new User(
                "John",
                "john@test.com",
                "password",
                "address",
                Role.USER,
                true
        );

        product = new Product();
        product.setName("Laptop");
        product.setPrice(new BigDecimal("100.00"));
        product.setStock(10);
        product.setActive(true);
    }

    @Test
    void shouldCreateOrder() {
        OrderItemRequest itemRequest = new OrderItemRequest(1L, 2);

        OrderRequest request = new OrderRequest(List.of(itemRequest));

        OrderResponse response = mock(OrderResponse.class);

        when(authenticationFacade.getCurrentUserId()).thenReturn(1L);

        when(userRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(user));

        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(product));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(orderMapper.toOrderResponse(any(Order.class)))
                .thenReturn(response);

        OrderResponse result = orderService.save(request);

        assertThat(result).isEqualTo(response);

        assertThat(product.getStock()).isEqualTo(8);

        verify(orderRepository).save(any(Order.class));
        verify(orderMapper).toOrderResponse(any(Order.class));
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        OrderRequest request = new OrderRequest(List.of());

        when(authenticationFacade.getCurrentUserId()).thenReturn(1L);

        when(userRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.save(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");

        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenProductNotFound() {
        OrderItemRequest itemRequest = new OrderItemRequest(1L, 2);

        OrderRequest request = new OrderRequest(List.of(itemRequest));

        when(authenticationFacade.getCurrentUserId()).thenReturn(1L);

        when(userRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(user));

        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.save(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product with id 1 not found");
    }

    @Test
    void shouldThrowWhenStockIsInsufficient() {
        product.setStock(1);

        OrderItemRequest itemRequest = new OrderItemRequest(1L, 5);

        OrderRequest request = new OrderRequest(List.of(itemRequest));

        when(authenticationFacade.getCurrentUserId()).thenReturn(1L);

        when(userRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(user));

        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(product));

        assertThatThrownBy(() -> orderService.save(request))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessage("Requested quantity exceeds stock for product 1");
    }

    @Test
    void shouldReturnPagedOrders() {
        Pageable pageable = PageRequest.of(0, 10);

        Order order = new Order(LocalDateTime.now(), OrderStatus.CREATED);

        OrderResponse response = mock(OrderResponse.class);

        Page<Order> orderPage = new PageImpl<>(List.of(order));

        when(authenticationFacade.getCurrentUserId()).thenReturn(1L);

        when(orderRepository.findByUserId(1L, pageable))
                .thenReturn(orderPage);

        when(orderMapper.toOrderResponse(order))
                .thenReturn(response);

        Page<OrderResponse> result = orderService.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(response);
    }

    @Test
    void shouldReturnOrderById() {
        Order order = new Order(LocalDateTime.now(), OrderStatus.CREATED);

        OrderResponse response = mock(OrderResponse.class);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderMapper.toOrderResponse(order))
                .thenReturn(response);

        OrderResponse result = orderService.findById(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void shouldThrowWhenOrderNotFound() {
        when(orderRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Order Not Found");
    }

    @Test
    void shouldCancelOrder() {
        product.setStock(5);

        Order order = new Order(LocalDateTime.now(), OrderStatus.CREATED);

        OrderItem item = new OrderItem(
                "Laptop",
                2,
                new BigDecimal("100.00")
        );

        item.setProduct(product);
        item.setOrder(order);

        order.setOrderItems(List.of(item));

        OrderResponse response = mock(OrderResponse.class);

        when(authenticationFacade.getCurrentUserId()).thenReturn(1L);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderMapper.toOrderResponse(order))
                .thenReturn(response);

        OrderResponse result = orderService.cancel(1L);

        assertThat(result).isEqualTo(response);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);

        assertThat(product.getStock()).isEqualTo(7);
    }

    @Test
    void shouldThrowWhenCancelingNonExistingOrder() {
        when(authenticationFacade.getCurrentUserId()).thenReturn(1L);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.cancel(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Order not found");
    }

    @Test
    void shouldThrowWhenCancelingInvalidOrder() {
        Order order = new Order(LocalDateTime.now(), OrderStatus.DELIVERED);

        when(authenticationFacade.getCurrentUserId()).thenReturn(1L);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancel(1L))
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessage("Invalid order status transition");
    }

    @Test
    void shouldPreviewOrder() {
        OrderItemRequest itemRequest = new OrderItemRequest(1L, 2);

        OrderPreviewRequest request =
                new OrderPreviewRequest(List.of(itemRequest));

        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(product));

        OrderPreviewResponse result = orderService.preview(request);

        assertThat(result.orderItems()).hasSize(1);

        assertThat(result.totalAmount())
                .isEqualByComparingTo("200.00");
    }

    @Test
    void shouldThrowWhenPreviewProductNotFound() {
        OrderItemRequest itemRequest = new OrderItemRequest(1L, 2);

        OrderPreviewRequest request =
                new OrderPreviewRequest(List.of(itemRequest));

        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.preview(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product with id 1 not found");
    }
}
