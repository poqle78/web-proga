package funn.j2k.web.auth;


import funn.j2k.web.auth.dto.TokenResponse;
import funn.j2k.web.exception.AuthenticationException;
import funn.j2k.web.user.PasswordService;
import funn.j2k.web.user.User;
import funn.j2k.web.user.UserRepository;
import funn.j2k.web.user.dto.LoginRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import java.time.Instant;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class AuthService {
    private static final Logger logger = Logger.getLogger(AuthService.class.getName());

    @Inject
    UserRepository users;
    @Inject
    PasswordService passwords;
    @Inject
    JwtService jwt;
    @Inject
    RefreshTokenRepository refreshTokens;

    public TokenResponse login(LoginRequest req) {
        String login = req.login().trim();
        Optional<User> found = users.findByUsername(login)
                .or(() -> users.findByEmail(
                        login
                ));

        boolean ok = found
                .map(user -> passwords.verify(req.password(), user.getPasswordHash()))
                .orElseGet(() -> passwords.verifyDummy(req.password()));

        if (!ok || found.isEmpty()) {
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
                    .header(HttpHeaders.WWW_AUTHENTICATE, "Bearer").build());
        }
        return issueTokens(found.get());
    }

    @Transactional(dontRollbackOn = AuthenticationException.class)
    public TokenResponse refresh(String refreshTokenRaw) {
        jwt.parseRefresh(refreshTokenRaw);

        String hash = jwt.hashToken(refreshTokenRaw);
        RefreshToken stored = refreshTokens.findByHash(hash)
                .orElseThrow(() -> new AuthenticationException("Refresh token not recognized"));

        if (!refreshTokens.revokeIfActive(hash)) {
            throw new AuthenticationException("Refresh token revoked");
        }
        User user = stored.getUser();

        logger.log(Level.INFO, "Refresh token rotated: user={0}", user.getUsername());
        return issueTokens(stored.getUser());
    }

    private TokenResponse issueTokens(User user) {
        String access = jwt.generateAccess(user);
        String refresh = jwt.generateRefresh(user);

        RefreshToken stored = new RefreshToken();
        stored.setUser(user);
        stored.setTokenHash(jwt.hashToken(refresh));
        stored.setExpiresAt(Instant.now().plusSeconds(jwt.getRefreshTtl()));
        refreshTokens.create(stored);

        return new TokenResponse(access, refresh);
    }


    @Transactional
    public void logout(String refreshTokenRaw) {
        if (
                refreshTokenRaw == null ||
                        refreshTokenRaw.isBlank()
        ) {
            return;
        }

        String hash =
                jwt.hashToken(refreshTokenRaw);

        refreshTokens
                .findByHash(hash)
                .ifPresent(
                        refreshTokens::markRevoked
                );
    }

    public long getRefreshTtl() {
        return jwt.getRefreshTtl();
    }

    public long getAccessTtl() {
        return jwt.getAccessTtl();
    }
}
