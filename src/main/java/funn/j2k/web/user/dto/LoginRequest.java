package funn.j2k.web.user.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest (
        String username,
        String email,
        @NotBlank String password) {

    @Override
    public @NotNull String toString() {
        return "LoginRequest[username=" + username + ", password=***]";
    }
}
