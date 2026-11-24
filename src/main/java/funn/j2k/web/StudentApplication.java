package funn.j2k.web;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/api")
public class StudentApplication extends Application {
    // Needed to enable Jakarta REST and specify path.
}
