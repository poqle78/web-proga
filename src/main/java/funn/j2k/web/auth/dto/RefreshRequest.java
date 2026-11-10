package funn.j2k.web.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest (
    @NotBlank String refreshToken
) {
}
