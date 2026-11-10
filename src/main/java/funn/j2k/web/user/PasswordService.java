package funn.j2k.web.user;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;

import java.util.Arrays;
import java.util.Map;

@ApplicationScoped
public class PasswordService {
    @Inject
    Pbkdf2PasswordHash hasher;
    private String dummyHash;

    private static final int COST = 600000;

    @PostConstruct
    void init() {
        hasher.initialize(Map.of(
                "Pbkdf2PasswordHash.Algorithm", "PBKDF2WithHmacSHA256",
                "Pbkdf2PasswordHash.Iterations", String.valueOf(COST),
                "Pbkdf2PasswordHash.SaltSizeBytes", "32",
                "Pbkdf2PasswordHash.KeySizeBytes", "32"));
        dummyHash = hasher.generate("dummy-password".toCharArray());
    }

    public String hash(String password) {
        char[] chars = password.toCharArray();
        try { return hasher.generate(chars); }
        finally { Arrays.fill(chars, '\0'); }
    }

    public boolean verify(String password, String storedHash) {
        if (password == null || storedHash == null) return false;
        char[] chars = password.toCharArray();
        try {
            return hasher.verify(chars, storedHash);
        } finally {
            Arrays.fill(chars, '\0');
        }
    }

    public boolean verifyDummy(String password) {
        verify(password, dummyHash);
        return false;
    }
}

