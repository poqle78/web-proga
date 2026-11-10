package funn.j2k.web.student;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Stateless
public class StudentRepository {
    private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @PersistenceContext
    private EntityManager em;

    public Student create(Student student) {
        logger.info("Creating student " + student.getName());
        em.persist(student);

        return student;
    }

    public List<Student> findAll() {
        logger.info("Getting all student");
        return em.createQuery("SELECT c FROM Student c", Student.class).getResultList();
    }

    public Optional<Student> findByIsu(Integer isu) {
        logger.info("Getting student by isu " + isu);
        return Optional.ofNullable(em.find(Student.class, isu));
    }

    public void delete(Integer isu) {
        logger.info("Deleting student by isu " + isu);
        var student = findByIsu(isu)
                .orElseThrow(() -> new IllegalArgumentException("Invalid isu student Isu:" + isu));
        em.remove(student);
    }

    public Student update(Student student) {
        logger.info("Updating student " + student.getName());
        return em.merge(student);
    }
}
