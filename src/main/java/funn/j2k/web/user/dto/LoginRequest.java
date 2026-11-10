package funn.j2k.web.user.dto;


import jakarta.validation.constraints.NotNull;

public record LoginRequest (
        String usernameOrEmail,

        String password) {

    @Override
    public @NotNull String toString() {
        return "LoginRequest[username=" + usernameOrEmail + ", password=***]";
    }
}
