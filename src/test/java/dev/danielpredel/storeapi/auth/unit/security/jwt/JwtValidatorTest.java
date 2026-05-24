package dev.danielpredel.storeapi.auth.unit.security.jwt;

import dev.danielpredel.storeapi.auth.security.jwt.JwtClaimsExtractor;
import dev.danielpredel.storeapi.auth.security.jwt.JwtValidator;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtValidatorTest {

    @Mock
    private JwtClaimsExtractor jwtClaimsExtractor;

    @InjectMocks
    private JwtValidator jwtValidator;

    @Test
    void shouldReturnTrueWhenTokenIsValidAndNotExpired() {
        String token = "valid.jwt.token";
        Date futureDate = new Date(System.currentTimeMillis() + 10000);

        when(jwtClaimsExtractor.extractAllClaims(token)).thenReturn(mock(io.jsonwebtoken.Claims.class));
        when(jwtClaimsExtractor.extractExpiration(token)).thenReturn(futureDate);

        boolean result = jwtValidator.isTokenValid(token);

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenTokenIsExpired() {
        String token = "expired.token";
        Date pastDate = new Date(System.currentTimeMillis() - 10000);

        when(jwtClaimsExtractor.extractAllClaims(token)).thenReturn(mock(io.jsonwebtoken.Claims.class));
        when(jwtClaimsExtractor.extractExpiration(token)).thenReturn(pastDate);

        boolean result = jwtValidator.isTokenValid(token);

        assertFalse(result);
    }

    @Test
    void shouldReturnFalseWhenJwtExceptionIsThrown() {
        String token = "invalid.token";

        when(jwtClaimsExtractor.extractAllClaims(token))
                .thenThrow(new JwtException("Invalid signature"));

        boolean result = jwtValidator.isTokenValid(token);

        assertFalse(result);
    }

    @Test
    void shouldReturnFalseWhenIllegalArgumentExceptionIsThrown() {
        String token = "bad.token";

        when(jwtClaimsExtractor.extractAllClaims(token))
                .thenThrow(new IllegalArgumentException());

        boolean result = jwtValidator.isTokenValid(token);

        assertFalse(result);
    }
}
