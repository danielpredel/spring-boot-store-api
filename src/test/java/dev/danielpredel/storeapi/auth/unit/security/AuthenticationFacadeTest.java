package dev.danielpredel.storeapi.auth.unit.security;

import dev.danielpredel.storeapi.auth.security.AuthenticationFacade;
import dev.danielpredel.storeapi.auth.security.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthenticationFacadeTest {

    private final AuthenticationFacade authenticationFacade =
            new AuthenticationFacade();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnCurrentUserId() {
        // given
        CustomUserDetails user = new CustomUserDetails(123L, null, null);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // when
        Long result = authenticationFacade.getCurrentUserId();

        // then
        assertEquals(123L, result);
    }
}
