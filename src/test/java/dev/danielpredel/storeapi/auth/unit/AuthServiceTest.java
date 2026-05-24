package dev.danielpredel.storeapi.auth.unit;

import dev.danielpredel.storeapi.auth.dto.AuthRequest;
import dev.danielpredel.storeapi.auth.dto.AuthResponse;
import dev.danielpredel.storeapi.auth.security.CustomUserDetails;
import dev.danielpredel.storeapi.auth.security.CustomUserDetailsService;
import dev.danielpredel.storeapi.auth.security.jwt.JwtTokenProvider;
import dev.danielpredel.storeapi.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private AuthRequest authRequest;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest(
                "test@mail.com",
                "password123"
        );

        userDetails = new CustomUserDetails(
                1L,
                "test@mail.com",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    void shouldLogin() {
        String expectedToken = "jwt-token";

        when(userDetailsService.loadUserByUsername(authRequest.email()))
                .thenReturn(userDetails);

        when(jwtTokenProvider.generateToken(userDetails))
                .thenReturn(expectedToken);

        AuthResponse response = authService.login(authRequest);

        assertNotNull(response);
        assertEquals(expectedToken, response.token());

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.email(),
                        authRequest.password()
                )
        );

        verify(userDetailsService)
                .loadUserByUsername(authRequest.email());

        verify(jwtTokenProvider)
                .generateToken(userDetails);
    }

    @Test
    void shouldThrowWhenCredentialsAreInvalid() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(authRequest)
        );

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.email(),
                        authRequest.password()
                )
        );

        verify(userDetailsService, never())
                .loadUserByUsername(anyString());

        verify(jwtTokenProvider, never())
                .generateToken(any());
    }

    @Test
    void shouldLoadUserByEmail() {
        when(userDetailsService.loadUserByUsername(authRequest.email()))
                .thenReturn(userDetails);

        when(jwtTokenProvider.generateToken(any()))
                .thenReturn("token");

        authService.login(authRequest);

        verify(userDetailsService)
                .loadUserByUsername("test@mail.com");
    }

    @Test
    void shouldGenerateToken() {
        when(userDetailsService.loadUserByUsername(authRequest.email()))
                .thenReturn(userDetails);

        when(jwtTokenProvider.generateToken(userDetails))
                .thenReturn("token");

        authService.login(authRequest);

        verify(jwtTokenProvider)
                .generateToken(userDetails);
    }
}
