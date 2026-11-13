package proyecto.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JWTUtils {
    private final SecretKey key;
    private static final long VIGENCIA_MS = 24 * 60 * 60 * 1000L;

    public JWTUtils(Environment environment) {
        String secret = environment.getProperty("app.jwt.secret");
        if (!StringUtils.hasText(secret) || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("Configura JWT_SECRET con al menos 32 bytes antes de iniciar");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public static String getToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")
            ? bearer.substring(7) : null;
    }

    public String generateToken(Authentication authentication) {
        Date now = new Date();
        return Jwts.builder().subject(authentication.getName()).issuedAt(now)
            .expiration(new Date(now.getTime() + VIGENCIA_MS))
            .signWith(key, Jwts.SIG.HS256).compact();
    }

    public String getUsernameOfToken(String jwt) {
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload();
        return claims.getSubject();
    }
}
