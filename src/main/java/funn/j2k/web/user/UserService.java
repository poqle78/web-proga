package funn.j2k.web.user;

import funn.j2k.web.user.dto.CreateUserRequest;
import funn.j2k.web.user.dto.UserResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.Set;

@ApplicationScoped
@Transactional
public class UserService {
    @Inject
    UserRepository repo;
    @Inject PasswordService passwords;
    @Inject UserMapper mapper;

    public UserResponse create(CreateUserRequest r) {
        if (repo.findByUsername(r.username()).isPresent()) {
            throw new WebApplicationException("Username already taken", Response.Status.CONFLICT);
        }
        User u = new User();
        u.setUsername(r.username());
        u.setPasswordHash(passwords.hash(r.password()));
        u.setRoles(r.roles() == null || r.roles().isEmpty() ? Set.of(Role.USER) : r.roles());
        repo.persist(u);
        return mapper.toResponse(u);
    }
}
