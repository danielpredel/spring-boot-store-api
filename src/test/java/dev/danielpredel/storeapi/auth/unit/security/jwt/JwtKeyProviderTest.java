package dev.danielpredel.storeapi.auth.unit.security.jwt;

import dev.danielpredel.storeapi.auth.security.jwt.JwtKeyProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.security.Key;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JwtKeyProviderTest {
    private JwtKeyProvider jwtKeyProvider;

    @BeforeEach
    void setUp() throws Exception {
        jwtKeyProvider = new JwtKeyProvider();

        Field field = JwtKeyProvider.class.getDeclaredField("secretKey");
        field.setAccessible(true);
        field.set(jwtKeyProvider, "test-secret-not-for-production");
    }

    @Test
    void shouldGetSignKey() {
        Key key = jwtKeyProvider.getSignKey();

        assertNotNull(key);
    }
}
