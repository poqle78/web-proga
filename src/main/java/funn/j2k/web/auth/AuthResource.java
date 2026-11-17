package funn.j2k.web.auth;

import funn.j2k.web.auth.dto.RefreshRequest;
import funn.j2k.web.user.UserService;
import funn.j2k.web.user.dto.CreateUserRequest;
import funn.j2k.web.user.dto.LoginRequest;
import funn.j2k.web.user.dto.UserResponse;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    UserService userService;

    @Inject
    AuthService authService;

    @POST
    @Path("/register")
    @PermitAll
    public Response register(@Valid CreateUserRequest req) {
        UserResponse dto = userService.create(req);
        return Response.status(Response.Status.CREATED).entity(dto).build();
    }

    @POST
    @Path("/login")
    @PermitAll
    public Response login(@Valid LoginRequest req) {
        return Response.ok(authService.login(req)).build();
    }

    @POST
    @Path("/refresh")
    @PermitAll
    public Response refresh(@Valid RefreshRequest req) {
        return Response.ok(authService.refresh(req.refreshToken())).build();
    }
}
