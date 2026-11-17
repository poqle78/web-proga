package funn.j2k.web.security;

import funn.j2k.web.exception.AccessDeniedException;
import funn.j2k.web.exception.AuthenticationException;
import jakarta.annotation.Priority;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter {

    @Context
    ResourceInfo resource;

    @Override
    public void filter(ContainerRequestContext ctx) {
        if ("OPTIONS".equalsIgnoreCase(ctx.getMethod())) {
            return;
        }

        if (has(PermitAll.class)) return;

        SecurityContext sc = ctx.getSecurityContext();
        if (sc.getUserPrincipal() == null) {
            throw new AuthenticationException("Authentication required");
        }

        Set<String> required = requiredRoles(ctx.getMethod());
        if (required.stream().noneMatch(sc::isUserInRole)) {
            throw new AccessDeniedException("Insufficient role");
        }
    }

    private Set<String> requiredRoles(String httpMethod) {
        RolesAllowed explicit = find(resource.getResourceMethod(), RolesAllowed.class);
        if (explicit == null) {
            explicit = find(resource.getResourceClass(), RolesAllowed.class);
        }

        if (explicit != null) {
            return Set.of(explicit.value());
        }

        boolean readOnly = "GET".equalsIgnoreCase(httpMethod) || "HEAD".equalsIgnoreCase(httpMethod);
        return readOnly ? Set.of("USER", "ADMIN") : Set.of("ADMIN");
    }

    private boolean has(Class<? extends Annotation> type) {
        return find(resource.getResourceMethod(), type) != null
                || find(resource.getResourceClass(), type) != null;
    }

    private static <A extends Annotation> A find(Method m, Class<A> type) {
        return m.getAnnotation(type);
    }

    private static <A extends Annotation> A find(Class<?> c, Class<A> type) {
        for (; c != null; c = c.getSuperclass()) {
            A a = c.getAnnotation(type);
            if (a != null) return a;
        }
        return null;
    }
}
