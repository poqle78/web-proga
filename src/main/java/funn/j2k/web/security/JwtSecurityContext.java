package funn.j2k.web.security;

import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;

public record JwtSecurityContext(JwtPayload payload, boolean secure) implements SecurityContext {
    @Override public Principal getUserPrincipal() { return () -> String.valueOf(payload.userId()); }
    @Override public boolean isUserInRole(String role) { return payload.role().contains(role); }
    @Override public boolean isSecure() { return secure; }
    @Override public String getAuthenticationScheme() { return "Bearer"; }
}
