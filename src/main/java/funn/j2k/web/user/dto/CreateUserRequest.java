package funn.j2k.web.user.dto;

import funn.j2k.web.user.Role;
import jakarta.validation.constraints.*;

import java.util.Set;

public record CreateUserRequest (
    @NotBlank @Size(min = 3, max = 50)
    @Pattern(regexp = "[A-Za-z0-9._-]+") String username,
    @NotBlank @Email @Size(max = 255) String email,

    @NotBlank @Size(min = 8, max = 128) String password,

    Set<Role> roles) {

    @Override
    public @NotNull String toString() {
        return "CreateUserRequest[username=" + username + ", password=***]";
    }
}
