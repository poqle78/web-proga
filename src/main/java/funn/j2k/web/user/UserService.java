package funn.j2k.web.user;

import funn.j2k.web.user.dto.CreateUserRequest;
import funn.j2k.web.user.dto.UserResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.Locale;
import java.util.Optional;
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

    @Transactional
    public UserResponse createAdminIfNotExists(String username, String email, String rawPassword) {
        Optional<User> existing = repo.findByUsername(username);
        if (existing.isPresent()) {
            return mapper.toResponse(existing.get());
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email.toLowerCase(Locale.ROOT));
        user.setPasswordHash(passwords.hash(rawPassword));
        user.setRoles(Set.of(Role.ADMIN));

        repo.persist(user);
        return mapper.toResponse(user);
    }
}
