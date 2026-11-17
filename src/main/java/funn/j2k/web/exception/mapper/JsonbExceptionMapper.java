package funn.j2k.web.exception.mapper;

import funn.j2k.web.exception.dto.FieldError;
import jakarta.json.bind.JsonbException;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Provider
public class JsonbExceptionMapper implements ExceptionMapper<ValidationException> {
    private static final Pattern PROPERTY = Pattern.compile("property '([^']+)'");

    @Override
    public Response toResponse(ValidationException e) {
        String message = e.getMessage();
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"" + message + "\"}")
                .type("application/json")
                .build();
    }

}
