package funn.j2k.web.auth;

import funn.j2k.web.auth.dto.TokenResponse;
import funn.j2k.web.exception.AuthenticationException;
import funn.j2k.web.user.UserService;
import funn.j2k.web.user.dto.CreateUserRequest;
import funn.j2k.web.user.dto.LoginRequest;
import funn.j2k.web.user.dto.UserResponse;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {
    private static final String ACCESS_COOKIE = "access_token";
    private static final String REFRESH_COOKIE = "refresh_token";
    private static final String COOKIE_PATH = "/";

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
        return tokensToCookie(authService.login(req));
    }

    @POST
    @Path("/refresh")
    @PermitAll
    public Response refresh(
            @CookieParam(REFRESH_COOKIE) String refreshToken
    ) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new AuthenticationException(
                    "Refresh token not found"
            );
        }
        return tokensToCookie(authService.refresh(refreshToken));
    }


    @POST
    @Path("/logout")
    @PermitAll
    public Response logout(
            @CookieParam(REFRESH_COOKIE) String refreshToken
    ) {
        authService.logout(refreshToken);

        return Response
                .noContent()
                .cookie(
                        clearCookie(ACCESS_COOKIE),
                        clearCookie(REFRESH_COOKIE)
                )
                .build();
    }

    private Response tokensToCookie(TokenResponse tokens) {
        return Response
                .noContent()
                .cookie(
                        createCookie(
                                ACCESS_COOKIE,
                                tokens.accessToken(),
                                authService.getAccessTtl()
                        ),
                        createCookie(
                                REFRESH_COOKIE,
                                tokens.refreshToken(),
                                authService.getRefreshTtl()
                        )
                )
                .build();
    }

    private NewCookie createCookie(
            String name,
            String value,
            long maxAgeSeconds
    ) {
        return new NewCookie.Builder(name)
                .value(value)
                .path(COOKIE_PATH)
                .maxAge(Math.toIntExact(maxAgeSeconds))
                .httpOnly(true)
                .secure(false)
                .sameSite(NewCookie.SameSite.STRICT)
                .build();
    }


    private NewCookie clearCookie(String name) {
        return new NewCookie.Builder(name)
                .value("")
                .path(COOKIE_PATH)
                .maxAge(0)
                .httpOnly(true)
                .secure(false)
                .sameSite(NewCookie.SameSite.STRICT)
                .build();
    }
}
