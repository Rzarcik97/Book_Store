package bookstore.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final Key secret;

    @Value("${jwt.expiration}")
    private long expirationInSeconds;

    public JwtUtil(@Value("${jwt.secret}") String secretString) {
        secret = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + getExpirationInMillis()))
                .signWith(secret)
                .compact();
    }

    public boolean isValidToken(String token) {
        try {
            return !Jwts.parser()
                    .verifyWith((SecretKey) secret)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration()
                    .before(new Date());
        } catch (ExpiredJwtException | SecurityException e) {
            return false;
        }
    }

    public String getUserName(String token) {
        return getClaimsFromToken(token, Claims::getSubject);
    }

    private <T> T getClaimsFromToken(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = Jwts.parser()
                .verifyWith((SecretKey) secret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsTFunction.apply(claims);
    }

    public long getExpirationInMillis() {
        return expirationInSeconds * 1000;
    }
}
