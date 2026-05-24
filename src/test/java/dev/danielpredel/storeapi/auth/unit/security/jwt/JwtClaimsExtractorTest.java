package dev.danielpredel.storeapi.auth.unit.security.jwt;

import dev.danielpredel.storeapi.auth.security.CustomUserDetails;
import dev.danielpredel.storeapi.auth.security.jwt.JwtClaimsExtractor;
import dev.danielpredel.storeapi.auth.security.jwt.JwtKeyProvider;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtClaimsExtractorTest {

    @Mock
    private JwtKeyProvider jwtKeyProvider;

    @InjectMocks
    private JwtClaimsExtractor jwtClaimsExtractor;

    private Key key;
    private String token;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        String secret = Base64.getEncoder()
                .encodeToString("this-is-a-very-long-secret-key-not-for-production".getBytes());

        key = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );

        when(jwtKeyProvider.getSignKey()).thenReturn(key);

        userDetails = new CustomUserDetails(
                1L,
                "test@mail.com",
                List.of(
                        new SimpleGrantedAuthority("ROLE_USER")
                )
        );

        token = Jwts.builder()
                .subject(userDetails.getUsername())
                .claim(
                        "roles",
                        userDetails.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .toList()
                )
                .claim("userId", userDetails.getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith((SecretKey) key)
                .compact();
    }

    @Test
    void shouldExtractAllClaims() {
        Claims claims = jwtClaimsExtractor.extractAllClaims(token);

        assertEquals("test@mail.com", claims.getSubject());
        assertEquals(1L, claims.get("userId", Number.class).longValue());
    }

    @Test
    void shouldExtractUsername() {
        String username = jwtClaimsExtractor.extractUsername(token);

        assertEquals("test@mail.com", username);
    }

    @Test
    void shouldExtractExpiration() {
        Date expiration = jwtClaimsExtractor.extractExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void shouldExtractAuthorities() {
        List<GrantedAuthority> authorities =
                jwtClaimsExtractor.extractAuthorities(token);

        assertEquals(1, authorities.size());

        assertTrue(authorities.contains(
                new SimpleGrantedAuthority("ROLE_USER")
        ));
    }

    @Test
    void shouldExtractUserId() {
        Long userId = jwtClaimsExtractor.extractUserId(token);

        assertEquals(1L, userId);
    }
}
