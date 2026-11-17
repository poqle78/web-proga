package funn.j2k.web.student;

import funn.j2k.web.student.dto.SearchResult;
import funn.j2k.web.student.dto.StudentSearchRequest;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

    public SearchResult search(StudentSearchRequest req) {
        final var cb = em.getCriteriaBuilder();

        final var query = cb.createQuery(Student.class);
        final var student = query.from(Student.class);
        query.select(student).where(predicates(cb, student, req));

        List<Order> orders;
        String sortBy = req.sortBy() != null ? req.sortBy() : "id";
        Order mainOrder = req.sortDirection().equalsIgnoreCase("desc") ? cb.desc(student.get(sortBy)) : cb.asc(student.get(sortBy));
        if (!sortBy.equals("id")) {
            orders = List.of(mainOrder, cb.asc(student.get("id")));
        } else {
            orders = List.of(mainOrder);
        }
        query.orderBy(orders);
        List<Student> items = em.createQuery(query)
                .setFirstResult(req.page() * req.size())
                .setMaxResults(req.size())
                .getResultList();

        final var cq = cb.createQuery(Long.class);
        final var c = cq.from(Student.class);
        cq.select(cb.count(c)).where(predicates(cb, c, req));
        long total = em.createQuery(cq).getSingleResult();

        return new SearchResult(items, total);
    }

    private List<Predicate> predicates(CriteriaBuilder cb, Root<Student> s, StudentSearchRequest r) {
        List<Predicate> p = new ArrayList<>();
        if (r.isu() != null) p.add(cb.equal(s.get("isu"), r.isu()));
        if (r.name() != null) p.add(cb.like(cb.lower(s.get("name")), "%" + r.name().toLowerCase(Locale.ROOT) + "%"));
        if (r.group() != null) p.add(cb.equal(s.get("group"), r.group()));
        if (r.dormitory() != null) p.add(cb.equal(s.get("dormitory"), r.dormitory()));
        if (r.room() != null) p.add(cb.equal(s.get("room"), r.room()));
        if (r.checkIn() != null) p.add(cb.equal(s.get("checkIn"), r.checkIn()));
        if (r.isForeigner() != null) p.add(cb.equal(s.get("foreigner"), r.isForeigner()));
        if (r.notes() != null) p.add(cb.like(cb.lower(s.get("notes")), "%" + r.notes().toLowerCase(Locale.ROOT) + "%"));
        return p;
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
