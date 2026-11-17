package funn.j2k.web.student.dto;

import funn.j2k.web.student.Student;

import java.util.List;

public record SearchResult(List<Student> items, long total) {}
