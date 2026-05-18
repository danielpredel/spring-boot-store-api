package dev.danielpredel.storeapi.auth.security;

import dev.danielpredel.storeapi.auth.security.jwt.JwtClaimsExtractor;
import dev.danielpredel.storeapi.auth.security.jwt.JwtValidator;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtClaimsExtractor jwtClaimsExtractor;
    private final JwtValidator jwtValidator;

    public JwtAuthenticationFilter(JwtClaimsExtractor jwtClaimsExtractor, JwtValidator jwtValidator) {
        this.jwtClaimsExtractor =  jwtClaimsExtractor;
        this.jwtValidator = jwtValidator;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                // Validate token signature & expiration
                if (jwtValidator.isTokenValid(token)) {

                    // Extract info from JWT
                    String email = jwtClaimsExtractor.extractUsername(token);
                    List<GrantedAuthority> authorities = jwtClaimsExtractor.extractAuthorities(token);
                    Long id = jwtClaimsExtractor.extractUserId(token);

                    CustomUserDetails userDetails =
                            new CustomUserDetails(id, email, authorities);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

            } catch (JwtException | IllegalArgumentException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
                return; // Stop further processing
            }
        }
        filterChain.doFilter(request, response);
    }

}
