package dev.danielpredel.userapibasic.service;

import dev.danielpredel.userapibasic.dto.AuthRequest;
import dev.danielpredel.userapibasic.dto.AuthResponse;
import dev.danielpredel.userapibasic.security.JwtService;
import dev.danielpredel.userapibasic.service.impl.CustomUserDetailsService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
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

        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }
}
