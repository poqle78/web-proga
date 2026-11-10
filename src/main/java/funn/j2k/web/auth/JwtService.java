package funn.j2k.web.auth;

import funn.j2k.web.auth.dto.TokenResponse;
import funn.j2k.web.config.ConfigKeys;
import funn.j2k.web.config.ConfigProvider;
import funn.j2k.web.user.User;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class JwtService {
    private SecretKey key;
    private String issuer;
    private long accessTtl;
    private long refreshTtl;
    private JwtParser parser;

    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";

    @PostConstruct
    void init() {
        String secret = ConfigProvider.getOrDefault(ConfigKeys.JWT_SECRET, null);
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("APP_JWT_SECRET is not set");
        }
        byte[] bytes = Base64.getDecoder().decode(secret.trim());
        if (bytes.length < 32) {
            throw new IllegalStateException("APP_JWT_SECRET must be at least 32 bytes");
        }
        key = Keys.hmacShaKeyFor(bytes);
        accessTtl = ConfigProvider.getLong(ConfigKeys.JWT_ACCESS_TTL_SECONDS);
        refreshTtl = ConfigProvider.getLong(ConfigKeys.JWT_REFRESH_TTL_SECONDS);

        parser = Jwts.parser()
                .verifyWith(key)
                .clockSkewSeconds(30)
                .build();
    }

    public String generateAccess(User user) {
        return buildToken(user, TYPE_ACCESS, accessTtl);
    }

    public String generateRefresh(User user) {
        return buildToken(user, TYPE_REFRESH, refreshTtl);
    }

    private String buildToken(User user, String type, long ttlSeconds) {
        Instant now = Instant.now();
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getUsername())
                .claim("roles", user.getRoles().stream().map(Enum::name).toList())
                .claim("type", type)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(ttlSeconds)))
                .signWith(key)
                .compact();
    }

    public Optional<AuthenticatedUser> verify(String token) {
        try {
            Claims claims = parser.parseSignedClaims(token).getPayload();
            Set<String> roles = new HashSet<>();
            if (claims.get("roles") instanceof Collection<?> raw) {
                raw.forEach(r -> roles.add(String.valueOf(r)));
            }
            return Optional.of(new AuthenticatedUser(
                    Long.valueOf(claims.getSubject()),
                    claims.get("username", String.class),
                    roles));
        } catch (JwtException | IllegalArgumentException e) {   // подпись, срок, формат
            return Optional.empty();
        }
    }
}