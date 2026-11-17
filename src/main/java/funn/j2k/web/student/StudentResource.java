package funn.j2k.web.student;

import funn.j2k.web.student.dto.*;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.lang.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;


@Path("students")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StudentResource {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @HttpMethod("QUERY")
    public @interface QUERY {
    }

    private static final Logger logger = Logger.getLogger(StudentResource.class.getName());

    @Inject
    private StudentService service;

    @GET
    public List<StudentResponse> list() {
        logger.info("GET /students");
        return service.list();
    }

    @QUERY
    public PageResponse<StudentResponse> search(@Valid StudentSearchRequest req) {
        logger.info("QUERY /students");
        return service.search(req == null ? new StudentSearchRequest(null, null, null, null, null, null, null, null, null, null, null, null) : req);
    }

    @GET
    @Path("{isu}")
    public StudentResponse get(@PathParam("isu") Integer isu) {
        logger.info("GET /students/" + isu);
        return service.get(isu);
    }

    @POST
    public Response create(@Valid CreateStudentRequest req, @Context UriInfo uriInfo) {
        requireBody(req);
        StudentResponse created = service.create(req);
        URI location = uriInfo.getAbsolutePathBuilder().path("{isu}").build(created.isu());
        logger.info("POST /students -> создан isu=" + created.isu());
        return Response.created(location).entity(created).build(); // 201 + заголовок Location
    }

    @PUT
    @Path("{isu}")
    public StudentResponse update(@PathParam("isu") Integer isu, @Valid UpdateStudentRequest req) {
        requireBody(req);
        requireSameIsu(isu, req.isu());
        logger.info("PUT /students/" + isu);
        return service.update(isu, req);
    }

    @PATCH
    @Path("{isu}")
    public StudentResponse patch(@PathParam("isu") Integer isu, @Valid PatchStudentRequest req) {
        requireBody(req);
        requireSameIsu(isu, req.isu());
        logger.info("PATCH /students/" + isu);
        return service.patch(isu, req);
    }

    @DELETE
    @Path("{isu}")
    public Response delete(@PathParam("isu") Integer isu) {
        logger.info("DELETE /students/" + isu);
        service.delete(isu);
        return Response.noContent().build();
    }


    private static void requireBody(Object body) {
        if (body == null) {
            throw new BadRequestException("Тело запроса обязательно");
        }
    }


    private static void requireSameIsu(Integer pathIsu, Integer bodyIsu) {
        if (bodyIsu != null && !bodyIsu.equals(pathIsu)) {
            throw new BadRequestException("ИСУ в теле запроса не совпадает с ИСУ в адресе");
        }
    }
}