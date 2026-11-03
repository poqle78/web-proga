package funn.j2k.web.student;

import funn.j2k.web.student.dto.CreateStudentRequest;
import funn.j2k.web.student.dto.StudentResponse;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.logging.Logger;

@Path("students")
public class StudentResource {
    private final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject private StudentService service;

    @GET
    @Path("{isu}")
    @Produces("application/json")
    public StudentResponse findStudent(@PathParam("isu") Integer isu) {
        logger.info("Getting student by isu " + isu);
        return service.get(isu);
    }

    @GET
    @Produces("application/json")
    public List<StudentResponse> findAll() {
        logger.info("Getting all student");
        return service.list();
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public StudentResponse create(CreateStudentRequest student) {
        logger.info("Creating student " + student.name());
        try{
            return service.create(student);
        } catch (PersistenceException ex){
            logger.info("Error creating student " + student.name());
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
    }

    @DELETE
    @Path("{isu}")
    public void delete(@PathParam("isu") Integer isu) {
        logger.info("Deleting student by isu " + isu);
        try{
            service.delete(isu);
        }catch (IllegalArgumentException e){
            logger.info("Error deleting student by isu " + isu);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }


//    @PUT
//    @Consumes("application/json")
//    @Produces("application/json")
//    public Student update(Student student) {
//        logger.info("Updating student " + student.getName());
//        try{
//            return service.create(student);
//        }catch (PersistenceException ex){
//            logger.info("Error updating student " + student.getName());
//            throw new WebApplicationException(Response.Status.BAD_REQUEST);
//        }
//    }
}