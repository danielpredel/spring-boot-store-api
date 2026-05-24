package dev.danielpredel.storeapi.auth.unit.security;

import dev.danielpredel.storeapi.auth.security.CustomUserDetails;
import dev.danielpredel.storeapi.auth.security.CustomUserDetailsService;
import dev.danielpredel.storeapi.common.enums.Role;
import dev.danielpredel.storeapi.common.exception.ResourceNotFoundException;
import dev.danielpredel.storeapi.user.entity.User;
import dev.danielpredel.storeapi.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    public void shouldLoadUserByUsername() {
        // given
        String email = "test@mail.com";
        String password = "testpassword";
        User user = new User("Test User", email, password, "Some Test", Role.USER, true);

        when(userRepository.findByEmailAndActiveTrue(email))
                .thenReturn(Optional.of(user));

        // when
        CustomUserDetails result = (CustomUserDetails) customUserDetailsService.loadUserByUsername(email);

        // then
        assertEquals(email, result.getUsername());
        assertEquals(password, result.getPassword());
        assertEquals(new CustomUserDetails(user).getAuthorities(), result.getAuthorities());
        assertFalse(result.isAdmin());
    }

    @Test
    public void shouldThrowExceptionWhenUserNotFound() {
        // given
        String email = "missing@mail.com";

        when(userRepository.findByEmailAndActiveTrue(email))
                .thenReturn(Optional.empty());

        // when + then
        assertThrows(
                ResourceNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(email)
        );
    }
}
