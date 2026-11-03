package funn.j2k.web.student;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/api/v1/")
public class StudentApplication extends Application {
    // Needed to enable Jakarta REST and specify path.
}
