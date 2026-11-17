package funn.j2k.web.security;

import funn.j2k.web.auth.JwtService;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthenticationFilter implements ContainerRequestFilter {
    static final String INVALID_TOKEN = "jwt.invalid";
    private static final String BEARER_PREFIX = "Bearer ";

    @Inject
    JwtService jwt;

    @Override
    public void filter(ContainerRequestContext ctx) {
        String header = ctx.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (header == null) return;

        if (!header.regionMatches(true, 0, BEARER_PREFIX, 0, 7)) {
            ctx.setProperty(INVALID_TOKEN, true);
            return;
        }

        JwtPayload payload = jwt.parseAccess(header.substring(BEARER_PREFIX.length()).trim());

        ctx.setSecurityContext(new JwtSecurityContext(payload, ctx.getSecurityContext().isSecure()));
    }
}