package dev.danielpredel.storeapi.auth.service;

import dev.danielpredel.storeapi.auth.dto.AuthRequest;
import dev.danielpredel.storeapi.auth.dto.AuthResponse;
import dev.danielpredel.storeapi.auth.security.CustomUserDetailsService;
import dev.danielpredel.storeapi.auth.security.jwt.JwtTokenProvider;
import dev.danielpredel.storeapi.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private static final Logger log =
            LoggerFactory.getLogger(AuthService.class);

    public AuthService(
            AuthenticationManager authenticationManager,
            CustomUserDetailsService userDetailsService,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        UserDetails user = userDetailsService
                .loadUserByUsername(request.email());

        log.info("User {} logged in successfully", request.email());

        String token = jwtTokenProvider.generateToken(user);

        return new AuthResponse(token);
    }
}
