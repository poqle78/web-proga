package funn.j2k.web.student;

import funn.j2k.web.student.dto.CreateStudentRequest;
import funn.j2k.web.student.dto.StudentResponse;
import funn.j2k.web.student.dto.UpdateStudentRequest;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class StudentMapper {
    public StudentResponse toResponse(Student s) {
        return new StudentResponse(s.getIsu(), s.getName(), s.getGroup(), s.getDormitory(), s.getRoom(), s.getCheckIn(), s.getIsForeigner(), s.getNotes());
    }
    public Student toEntity(CreateStudentRequest r) {
        Student s = new Student();
        s.setIsu(r.isu());
        s.setName(r.name());
        s.setGroup(r.group());
        s.setDormitory(r.dormitory());
        s.setRoom(r.room());
        s.setCheckIn(r.check_in());
        s.setIsForeigner(r.is_foreigner());
        s.setNotes(r.notes());
        return s;
    }

    public Student updateEntity(UpdateStudentRequest r) {
        Student s = new Student();
        if (r.isu() != null)
            s.setIsu(r.isu());
        return s;
    }
}