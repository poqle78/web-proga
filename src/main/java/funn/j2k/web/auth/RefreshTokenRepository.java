package funn.j2k.web.auth;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.Optional;


@ApplicationScoped
@Transactional
public class RefreshTokenRepository {
    @PersistenceContext
    private EntityManager em;

    public Optional<RefreshToken> findByHash(String tokenHash) {
        return Optional.ofNullable(em.find(RefreshToken.class, tokenHash));
    }

    public boolean revokeIfActive(String hash) {
        Instant now = Instant.now();
        int updated = em.createQuery("""
                        update RefreshToken t
                           set t.revokedAt = :now
                         where t.tokenHash = :hash
                           and t.revokedAt is null
                           and t.expiresAt > :now""")
                .setParameter("now", now)
                .setParameter("hash", hash)
                .executeUpdate();
        return updated == 1;
    }

    public int revokeAllForUser(Long userId) {
        return em.createQuery("""
                        update RefreshToken t
                           set t.revokedAt = :now
                         where t.user.id = :uid and t.revokedAt is null""")
                .setParameter("now", Instant.now())
                .setParameter("uid", userId)
                .executeUpdate();
    }

    public RefreshToken create(RefreshToken refreshToken) {
        em.persist(refreshToken);
        return refreshToken;
    }

    public void markRevoked(RefreshToken refreshToken) {
        em.createQuery("""
                        update RefreshToken t
                           set t.revokedAt = :now
                         where t.tokenHash = :token and t.revokedAt is null""")
                .setParameter("now", Instant.now())
                .setParameter("token", refreshToken)
                .executeUpdate();
    }
}
