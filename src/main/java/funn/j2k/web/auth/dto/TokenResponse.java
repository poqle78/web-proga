package funn.j2k.web.auth.dto;

public record TokenResponse (
        String accessToken,
        String refreshToken
) {
}
