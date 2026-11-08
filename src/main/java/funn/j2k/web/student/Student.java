package funn.j2k.web.student;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
public class Student implements Serializable {

    @Id
    private Integer isu;

    @Column(nullable = false)
    private String name;
    @Column(name = "group_name", nullable = false)
    private String group;
    @Column(nullable = false)
    private Integer dormitory;
    @Column(nullable = false)
    private Integer room;
    @Column(nullable = false)
    private LocalDate check_in;
    @Column(nullable = false)
    private Boolean is_foreigner;
    @Column(nullable = false)
    private String notes = "";

    public Student() {
    }

    public Student(Integer isu, String name, String group, Integer dormitory,
                   Integer room, LocalDate check_in, Boolean is_foreigner, String notes) {
        this.isu = isu;
        this.name = name;
        this.group = group;
        this.dormitory = dormitory;
        this.room = room;
        this.check_in = check_in;
        this.is_foreigner = is_foreigner;
        this.notes = notes;
    }

    public Integer getIsu() {
        return isu;
    }

    public void setIsu(Integer isu) {
        this.isu = isu;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Integer getDormitory() {
        return dormitory;
    }

    public void setDormitory(Integer dormitory) {
        this.dormitory = dormitory;
    }

    public Integer getRoom() {
        return room;
    }

    public void setRoom(Integer room) {
        this.room = room;
    }

    public LocalDate getCheckIn() {
        return check_in;
    }

    public void setCheckIn(LocalDate check_in) {
        this.check_in = check_in;
    }

    public Boolean getIsForeigner() {
        return is_foreigner;
    }

    public void setIsForeigner(Boolean is_foreigner) {
        this.is_foreigner = is_foreigner;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
