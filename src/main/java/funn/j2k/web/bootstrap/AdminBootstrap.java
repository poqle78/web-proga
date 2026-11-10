package funn.j2k.web.bootstrap;

import funn.j2k.web.config.ConfigKeys;
import funn.j2k.web.config.ConfigProvider;
import funn.j2k.web.user.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import java.util.logging.Logger;

@ApplicationScoped
public class AdminBootstrap {
    @Inject
    UserService users;

    void onStart(@Observes @Initialized(ApplicationScoped.class) Object ignored) {
        String username = ConfigProvider.getOrDefault(ConfigKeys.ADMIN_USERNAME, null);
        String password = ConfigProvider.getOrDefault(ConfigKeys.ADMIN_PASSWORD, null);
        String email = ConfigProvider.getOrDefault(ConfigKeys.ADMIN_EMAIL, null);

        if (username == null || password == null || email == null) {
            return;
        }

        try {
            users.createAdminIfNotExists(username, email, password);
            Logger.getGlobal().info("Admin create: " + username);
        } catch (Exception ex) {
            Logger.getGlobal().warning("Admin creating failed: " + ex.getMessage());
        }

    }
}
