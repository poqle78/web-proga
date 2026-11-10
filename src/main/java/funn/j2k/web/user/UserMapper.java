package funn.j2k.web.user;

import funn.j2k.web.user.dto.UserResponse;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class UserMapper {
    public UserResponse toResponse(User u) {
        if (u == null) {
            return null;
        }
        return new UserResponse(
                u.getId(),
                u.getUsername(),
                u.getEmail(),
                copyRoles(u.getRoles())
        );
    }

    private Set<Role> copyRoles(Set<Role> roles) {
        return roles == null ? new HashSet<>() : new HashSet<>(roles);
    }
}