package dev.danielpredel.storeapi.order.unit;

import dev.danielpredel.storeapi.common.exception.ResourceNotFoundException;
import dev.danielpredel.storeapi.order.entity.Order;
import dev.danielpredel.storeapi.order.repository.OrderRepository;
import dev.danielpredel.storeapi.order.security.OrderSecurity;
import dev.danielpredel.storeapi.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderSecurityTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderSecurity orderSecurity;

    @Test
    void shouldReturnTrueWhenUserOwnsOrder() {
        User user = mock(User.class);
        Order order = mock(Order.class);

        when(user.getId()).thenReturn(1L);
        when(order.getUser()).thenReturn(user);
        when(orderRepository.findById(100L))
                .thenReturn(Optional.of(order));

        boolean result = orderSecurity.isOwner(100L, 1L);

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenUserDoesNotOwnOrder() {
        User user = mock(User.class);
        Order order = mock(Order.class);

        when(user.getId()).thenReturn(2L);
        when(order.getUser()).thenReturn(user);
        when(orderRepository.findById(100L))
                .thenReturn(Optional.of(order));

        boolean result = orderSecurity.isOwner(100L, 1L);

        assertFalse(result);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenOrderDoesNotExist() {
        when(orderRepository.findById(100L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> orderSecurity.isOwner(100L, 1L));
    }
}
