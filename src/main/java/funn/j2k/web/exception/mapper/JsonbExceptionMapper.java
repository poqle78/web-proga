package funn.j2k.web.exception.mapper;

import funn.j2k.web.exception.FieldError;
import jakarta.json.bind.JsonbException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Provider
public class JsonbExceptionMapper implements ExceptionMapper<JsonbException> {
    private static final Pattern PROPERTY = Pattern.compile("property '([^']+)'");

    @Override
    public Response toResponse(JsonbException e) {
        Matcher m = PROPERTY.matcher(String.valueOf(e.getMessage()));
        List<FieldError> errors = m.find()
                ? List.of(new FieldError(m.group(1), "InvalidValue", "Недопустимое значение или формат"))
                : null;
        return Response.status(400).type("application/problem+json").entity(errors).build();
    }
}
