package funn.j2k.web.auth;


import funn.j2k.web.auth.dto.TokenResponse;
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

import java.util.Locale;
import java.util.Optional;

@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class AuthService {
    @Inject
    UserRepository users;
    @Inject
    PasswordService passwords;
    @Inject
    JwtService jwt;

    public TokenResponse login(LoginRequest req) {
        String user_login = req.usernameOrEmail().trim();
        Optional<User> found = users.findByUsername(user_login)
                .or(() -> users.findByEmail(
                        user_login.toLowerCase(Locale.ROOT)
                ));

        boolean ok = found
                .map(user -> passwords.verify(req.password(), user.getPasswordHash()))
                .orElseGet(() -> passwords.verifyDummy(req.password()));

        if (!ok || found.isEmpty()) {
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
                    .header(HttpHeaders.WWW_AUTHENTICATE, "Bearer").build());
        }
        return issueToken(found.get());
    }


    private TokenResponse issueToken(User user) {
        String access = jwt.generateAccess(user);
        String refresh = jwt.generateRefresh(user);

        return new TokenResponse(access, refresh, jwt.getAccessTtlSeconds());
    }
}
