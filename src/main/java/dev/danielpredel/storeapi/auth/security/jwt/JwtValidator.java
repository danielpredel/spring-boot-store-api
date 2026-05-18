package dev.danielpredel.storeapi.auth.security.jwt;

import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtValidator {
    private final JwtClaimsExtractor jwtClaimsExtractor;

    public JwtValidator(JwtClaimsExtractor jwtClaimsExtractor) {
        this.jwtClaimsExtractor = jwtClaimsExtractor;
    }

    public boolean isTokenValid(String token) {
        try {
            // Will throw exception if invalid signature
            jwtClaimsExtractor.extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        return jwtClaimsExtractor.extractExpiration(token).before(new Date());
    }
}
