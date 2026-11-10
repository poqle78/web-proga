package funn.j2k.web.security;


import jakarta.security.enterprise.SecurityContext;

import java.security.Principal;
import java.util.Set;


public record JwtPayload(String username, Role role, String type, Instant expiresAt) {
}