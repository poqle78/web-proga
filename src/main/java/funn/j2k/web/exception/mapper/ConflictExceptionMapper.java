package funn.j2k.web.exception.mapper;

import funn.j2k.web.exception.ConflictException;
import funn.j2k.web.exception.dto.ErrorResponse;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.logging.Logger;

@Provider
public class ConflictExceptionMapper implements ExceptionMapper<ConflictException> {
    private static final Logger logger =
            Logger.getLogger(
                    ConflictExceptionMapper.class.getName()
            );
    @Override
    public Response toResponse(ConflictException e) {
        logger.warning(
                "Conflict (409): "
                        + e.getMessage()
        );
        ErrorResponse error = new ErrorResponse(
                409,
                "Conflict",
                e.getMessage()
        );

        return Response.status(Response.Status.CONFLICT)
                .entity(error)
                .build();
    }
}
