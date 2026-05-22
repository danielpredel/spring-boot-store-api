package dev.danielpredel.storeapi.order.unit;

import dev.danielpredel.storeapi.auth.security.AuthenticationFacade;
import dev.danielpredel.storeapi.common.enums.OrderStatus;
import dev.danielpredel.storeapi.common.exception.InvalidOrderStateException;
import dev.danielpredel.storeapi.common.exception.ResourceNotFoundException;
import dev.danielpredel.storeapi.order.dto.admin.AdminOrderResponse;
import dev.danielpredel.storeapi.order.entity.Order;
import dev.danielpredel.storeapi.order.mapper.OrderMapper;
import dev.danielpredel.storeapi.order.repository.OrderRepository;
import dev.danielpredel.storeapi.order.service.AdminOrderService;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @InjectMocks
    private AdminOrderService adminOrderService;

    private Order order;
    private AdminOrderResponse response;

    @BeforeEach
    void setUp() {
        order = new Order(LocalDateTime.now(), OrderStatus.CREATED);

        response = new AdminOrderResponse(
                1L,
                10L,
                List.of(),
                BigDecimal.TEN,
                LocalDateTime.now(),
                OrderStatus.CREATED
        );
    }

    @Test
    void shouldReturnPagedOrders() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Order> orderPage = new PageImpl<>(List.of(order));

        when(orderRepository.findAll(pageable)).thenReturn(orderPage);
        when(orderMapper.toAdminOrderResponse(order)).thenReturn(response);

        Page<AdminOrderResponse> result = adminOrderService.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(response);

        verify(orderRepository).findAll(pageable);
        verify(orderMapper).toAdminOrderResponse(order);
    }

    @Test
    void shouldReturnOrderById() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toAdminOrderResponse(order)).thenReturn(response);

        AdminOrderResponse result = adminOrderService.findById(1L);

        assertThat(result).isEqualTo(response);

        verify(orderRepository).findById(1L);
        verify(orderMapper).toAdminOrderResponse(order);
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFoundById() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminOrderService.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Order Not Found");

        verify(orderRepository).findById(1L);
        verifyNoInteractions(orderMapper);
    }

    @Test
    void shouldDeliverOrderSuccessfully() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toAdminOrderResponse(order)).thenReturn(response);
        when(authenticationFacade.getCurrentUserId()).thenReturn(99L);

        AdminOrderResponse result = adminOrderService.deliver(1L);

        assertThat(result).isEqualTo(response);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);

        verify(orderRepository).findById(1L);
        verify(orderMapper).toAdminOrderResponse(order);
    }

    @Test
    void shouldThrowExceptionWhenDeliverOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        when(authenticationFacade.getCurrentUserId()).thenReturn(99L);

        assertThatThrownBy(() -> adminOrderService.deliver(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Order not found");

        verify(orderRepository).findById(1L);
        verifyNoInteractions(orderMapper);
    }

    @Test
    void shouldThrowExceptionWhenDeliverOrderHasInvalidStatus() {
        order.setStatus(OrderStatus.DELIVERED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(authenticationFacade.getCurrentUserId()).thenReturn(99L);

        assertThatThrownBy(() -> adminOrderService.deliver(1L))
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessage("Invalid order status transition");

        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);

        verify(orderRepository).findById(1L);
        verifyNoInteractions(orderMapper);
    }
}
