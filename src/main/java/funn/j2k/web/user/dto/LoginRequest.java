package funn.j2k.web.user.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest (
        String login,
        @NotBlank String password) {
}
