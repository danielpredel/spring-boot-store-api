package dev.danielpredel.storeapi.auth.unit.security;

import dev.danielpredel.storeapi.auth.security.CustomUserDetails;
import dev.danielpredel.storeapi.auth.security.JwtAuthenticationFilter;
import dev.danielpredel.storeapi.auth.security.jwt.JwtClaimsExtractor;
import dev.danielpredel.storeapi.auth.security.jwt.JwtValidator;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtClaimsExtractor jwtClaimsExtractor;

    @Mock
    private JwtValidator jwtValidator;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtClaimsExtractor, jwtValidator);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateUserWhenTokenIsValid() throws Exception {
        // given
        String token = "valid.jwt.token";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        when(jwtValidator.isTokenValid(token)).thenReturn(true);
        when(jwtClaimsExtractor.extractUsername(token))
                .thenReturn("test@mail.com");
        when(jwtClaimsExtractor.extractAuthorities(token))
                .thenReturn(authorities);
        when(jwtClaimsExtractor.extractUserId(token))
                .thenReturn(1L);

        // when
        filter.doFilter(request, response, filterChain);

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        // then
        assertNotNull(authentication);
        assertTrue(authentication.isAuthenticated());

        CustomUserDetails principal =
                (CustomUserDetails) authentication.getPrincipal();

        assertEquals(1L, principal.getId());
        assertEquals("test@mail.com", principal.getUsername());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldContinueFilterChainWhenNoAuthorizationHeader() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        filter.doFilter(request, response, filterChain);

        // then
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldReturn401WhenJwtThrowsException() throws Exception {
        // given
        String token = "bad.token";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtValidator.isTokenValid(token))
                .thenThrow(new JwtException("Invalid token"));

        // when
        filter.doFilter(request, response, filterChain);

        // then
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED,
                response.getStatus());

        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void shouldNotAuthenticateWhenTokenIsInvalid() throws Exception {
        // given
        String token = "invalid.token";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtValidator.isTokenValid(token)).thenReturn(false);

        // when
        filter.doFilter(request, response, filterChain);

        // then
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(filterChain).doFilter(request, response);
    }
}
