package dev.danielpredel.storeapi.auth.unit.security.jwt;

import dev.danielpredel.storeapi.auth.security.CustomUserDetails;
import dev.danielpredel.storeapi.auth.security.jwt.JwtKeyProvider;
import dev.danielpredel.storeapi.auth.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtTokenProviderTest {

    @Mock
    private JwtKeyProvider jwtKeyProvider;

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    private Key key;

    @BeforeEach
    void setUp() {
        String secret = Base64.getEncoder()
                .encodeToString("this-is-a-very-long-secret-key-not-for-production".getBytes());

        key = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );

        when(jwtKeyProvider.getSignKey()).thenReturn(key);
    }

    @Test
    void shouldGenerateToken() {
        CustomUserDetails userDetails = new CustomUserDetails(
                1L,
                "test@mail.com",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String token = jwtTokenProvider.generateToken(userDetails);

        assertNotNull(token);

        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals("test@mail.com", claims.getSubject());
        assertEquals(1L, claims.get("userId", Long.class));

        List<String> roles = claims.get("roles", List.class);
        assertTrue(roles.contains("ROLE_USER"));
    }
}
