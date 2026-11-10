package funn.j2k.web.user.dto;

import funn.j2k.web.user.Role;

import java.util.Set;

public record UserResponse (Long id, String username, String email, Set<Role> roles) {

}
