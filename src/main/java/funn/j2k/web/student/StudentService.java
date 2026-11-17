package funn.j2k.web.student;

import funn.j2k.web.student.dto.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class StudentService {

    private static final Logger logger = Logger.getLogger(StudentService.class.getName());

    @Inject
    StudentRepository repo;

    @Inject
    StudentMapper mapper;


    @Transactional(Transactional.TxType.SUPPORTS)
    public StudentResponse get(Integer isu) {
        return mapper.toResponse(findOrThrow(isu));
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<StudentResponse> list() {
        return repo.findAll().stream().map(mapper::toResponse).toList();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public PageResponse<StudentResponse> search(StudentSearchRequest req) {
        SearchResult r = repo.search(req);
        return mapper.searchResultToPage(r, req);
    }

    @Transactional
    public StudentResponse create(CreateStudentRequest req) {
        if (repo.findByIsu(req.isu()).isPresent()) {
            throw new ClientErrorException(
                    "student " + req.isu() + " already exists", Response.Status.CONFLICT);
        }
        Student s = mapper.toEntity(req);
        if (s.getNotes() == null) {
            s.setNotes("");
        }
        repo.create(s);
        logger.info("student created isu=" + s.getIsu());
        return mapper.toResponse(s);
    }

    @Transactional
    public StudentResponse update(Integer isu, UpdateStudentRequest req) {
        Student s = findOrThrow(isu);
        s.setName(req.name().trim());
        s.setIsu(req.isu());
        s.setGroup(req.group().trim());
        s.setDormitory(req.dormitory());
        s.setRoom(req.room());
        s.setCheckIn(req.check_in());
        s.setIsForeigner(req.is_foreigner());
        s.setNotes(req.notes() == null ? "" : req.notes());
        logger.info("(put) update student" + isu);
        return mapper.toResponse(repo.update(s));
    }

    @Transactional
    public StudentResponse patch(Integer isu, PatchStudentRequest req) {
        boolean nothingToChange = req.name() == null && req.group() == null
                && req.dormitory() == null && req.room() == null
                && req.check_in() == null && req.is_foreigner() == null
                && req.notes() == null;
        if (nothingToChange) {
            throw new BadRequestException("no changes!");
        }

        Student s = findOrThrow(isu);
        s.setIsu(req.isu());
        if (req.name() != null) s.setName(req.name().trim());
        if (req.group() != null) s.setGroup(req.group().trim());
        if (req.dormitory() != null) s.setDormitory(req.dormitory());
        if (req.room() != null) s.setRoom(req.room());
        if (req.check_in() != null) s.setCheckIn(req.check_in());
        if (req.is_foreigner() != null) s.setIsForeigner(req.is_foreigner());
        if (req.notes() != null) s.setNotes(req.notes());
        logger.info("(patch) update student" + isu);
        return mapper.toResponse(repo.update(s));
    }

    @Transactional
    public void delete(Integer isu) {
        findOrThrow(isu);
        repo.delete(isu);
        logger.info("(delete) delete student" + isu);
    }


    private Student findOrThrow(Integer isu) {
        return repo.findByIsu(isu)
                .orElseThrow(() -> new NotFoundException("student" + isu + "is not found"));
    }
}