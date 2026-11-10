package funn.j2k.web.student;

import funn.j2k.web.student.dto.*;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.lang.annotation.*;
import java.net.URI;
import java.util.logging.Logger;


@Path("students")
@Produces(MediaType.APPLICATION_JSON)
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
    public PageResponse<StudentResponse> list(
            @QueryParam("q") String q,
            @QueryParam("group") String group,
            @QueryParam("dormitory") String dormitory,
            @QueryParam("room") String room,
            @QueryParam("is_foreigner") String isForeigner,
            @QueryParam("sort") String sort,
            @QueryParam("order") String order,
            @QueryParam("page") String page,
            @QueryParam("size") String size) {
        logger.info("GET /students");
        return service.search(new StudentSearchRequest(
                q, group,
                parseInt("dormitory", dormitory),
                parseInt("room", room),
                parseBoolean("is_foreigner", isForeigner),
                sort, order,
                parseInt("page", page),
                parseInt("size", size)));
    }

    @QUERY
    @Consumes(MediaType.APPLICATION_JSON)
    public PageResponse<StudentResponse> query(StudentSearchRequest criteria) {
        logger.info("QUERY /students");
        return service.search(orEmpty(criteria));
    }

    @POST
    @Path("search")
    @Consumes(MediaType.APPLICATION_JSON)
    public PageResponse<StudentResponse> searchPost(StudentSearchRequest criteria) {
        logger.info("POST /students/search");
        return service.search(orEmpty(criteria));
    }

    @GET
    @Path("{isu}")
    public StudentResponse get(@PathParam("isu") Integer isu) {
        logger.info("GET /students/" + isu);
        return service.get(isu);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(@Valid CreateStudentRequest req, @Context UriInfo uriInfo) {
        requireBody(req);
        StudentResponse created = service.create(req);
        URI location = uriInfo.getAbsolutePathBuilder().path("{isu}").build(created.isu());
        logger.info("POST /students -> создан isu=" + created.isu());
        return Response.created(location).entity(created).build(); // 201 + заголовок Location
    }

    @PUT
    @Path("{isu}")
    @Consumes(MediaType.APPLICATION_JSON)
    public StudentResponse update(@PathParam("isu") Integer isu, @Valid UpdateStudentRequest req) {
        requireBody(req);
        requireSameIsu(isu, req.isu());
        logger.info("PUT /students/" + isu);
        return service.update(isu, req);
    }

    @PATCH
    @Path("{isu}")
    @Consumes(MediaType.APPLICATION_JSON)
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
        return Response.noContent().build(); // 204
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


    private static StudentSearchRequest orEmpty(StudentSearchRequest criteria) {
        return criteria != null
                ? criteria
                : new StudentSearchRequest(null, null, null, null, null, null, null, null, null);
    }

    private static Integer parseInt(String name, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException e) {
            throw new BadRequestException("Параметр '" + name + "' должен быть целым числом");
        }
    }

    private static Boolean parseBoolean(String name, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        if (value.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        }
        if (value.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        }
        throw new BadRequestException("Параметр '" + name + "' должен быть true или false");
    }
}