package funn.j2k.web.student;

import funn.j2k.web.student.dto.CreateStudentRequest;
import funn.j2k.web.student.dto.StudentResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@ApplicationScoped
public class StudentService {
    @Inject StudentRepository repo;
    @Inject StudentMapper mapper;

    public StudentResponse create(CreateStudentRequest req) {
        Student s = mapper.toEntity(req);
        repo.create(s);
        return mapper.toResponse(s);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public StudentResponse get(Integer isu) {
        Optional<Student> s = repo.findByIsu(isu);
        if (s.isEmpty()) throw new NotFoundException();
        return mapper.toResponse(s.get());
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<StudentResponse> list() {
        return repo.findAll().stream().map(mapper::toResponse).toList();
    }

    public StudentResponse delete(Integer isu) {
        return mapper.toResponse(repo.findByIsu(isu).orElseThrow(() -> new NotFoundException("Student " + isu)));
    }
}


