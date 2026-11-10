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

    private static final List<String> SORT_FIELDS =
            List.of("isu", "name", "group", "dormitory", "room", "check_in");
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    @Inject
    StudentRepository repo;

    @Inject
    StudentMapper mapper;


    @Transactional(Transactional.TxType.SUPPORTS)
    public StudentResponse get(Integer isu) {
        return mapper.toResponse(findOrThrow(isu));
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public PageResponse<StudentResponse> search(StudentSearchRequest c) {
        String sort = c.sort() != null ? c.sort() : "name";
        String order = c.order() != null ? c.order() : "asc";
        int page = c.page() != null ? c.page() : 0;
        int size = c.size() != null ? c.size() : DEFAULT_PAGE_SIZE;

        if (!SORT_FIELDS.contains(sort)) {
            throw new BadRequestException(
                    "not allowed sort: '" + sort + "'");
        }
        if (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc")) {
            throw new BadRequestException("order must be asc or desc");
        }
        if (page < 0) {
            throw new BadRequestException("page mustn't be negative");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new BadRequestException("size must be from 1 to " + MAX_PAGE_SIZE);
        }

        boolean ascending = order.equalsIgnoreCase("asc");
        List<StudentResponse> items = repo
                .search(c.q(), c.group(), c.dormitory(), c.room(), c.is_foreigner(), sort, ascending, page, size)
                .stream().map(mapper::toResponse).toList();
        long total = repo.count(c.q(), c.group(), c.dormitory(), c.room(), c.is_foreigner());

        int totalPages = (int) ((total + size - 1) / size);
        return new PageResponse<>(items, page, size, total, totalPages);
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
        findOrThrow(isu); // если студента нет, здесь вылетит 404
        repo.delete(isu);
        logger.info("(delete) delete student" + isu);
    }


    private Student findOrThrow(Integer isu) {
        return repo.findByIsu(isu)
                .orElseThrow(() -> new NotFoundException("student" + isu + "is not found"));
    }
}