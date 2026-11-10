package funn.j2k.web.auth.dto;

public record TokenResponse (
        String access,
        String refresh,
        long expiresIn,
        String tokenType
) {
}
