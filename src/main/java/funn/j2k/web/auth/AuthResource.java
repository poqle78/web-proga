package funn.j2k.web.auth;

import funn.j2k.web.user.UserService;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    UserService userService;

    @Inject
    AuthService authService;

    @POST
    @Path("/register")
    public Response register(@Valid RegisterRequestDto req) {
        UserResponseDto dto = userService.register(req);
        return Response.status(Response.Status.CREATED).entity(dto).build();
    }

    @POST
    @Path("/login")
    public Response login(@Valid LoginRequestDto req) {
        return Response.ok(authService.login(req)).build();
    }

    @POST
    @Path("/refresh")
    public Response refresh(@Valid RefreshRequestDto req) {
        return Response.ok(authService.refresh(req.getRefreshToken())).build();
    }
}