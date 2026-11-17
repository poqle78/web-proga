package funn.j2k.web.user;

import funn.j2k.web.exception.ConflictException;
import funn.j2k.web.user.dto.CreateUserRequest;
import funn.j2k.web.user.dto.UserResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
@Transactional
public class UserService {
    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    @Inject
    UserRepository repo;
    @Inject PasswordService passwords;
    @Inject UserMapper mapper;

    public UserResponse create(CreateUserRequest r) {
        String username = r.username().trim();
        String email = r.email().trim().toLowerCase(Locale.ROOT);

        if (repo.existsByUsername(username)) {
            throw new ConflictException("Username already taken");
        }
        if (repo.existsByEmail(email)) {
            throw new ConflictException("Email already registered");
        }

        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPasswordHash(passwords.hash(r.password()));
        u.setRoles(r.roles() == null || r.roles().isEmpty() ? Set.of(Role.USER) : r.roles());
        repo.persist(u);

        logger.log(Level.INFO, "User registered: username={0}", username);

        return mapper.toResponse(u);
    }

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
