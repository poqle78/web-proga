package funn.j2k.web.security;

import java.util.Set;


public record JwtPayload(Long userId,  Set<String> role, String type) {
}
