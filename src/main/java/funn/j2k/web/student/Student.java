package funn.j2k.web.student;

import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.io.Serializable;
import java.util.Date;

@Entity
public class Student implements Serializable {

    @Id
    private Integer isu;

    @Column(nullable = false)
    private String name;
    @Column(name = "group_name")
    private String group;
    private Integer dormitory;
    private Integer room;
    @JsonbDateFormat("dd-MM-yyyy")
    private Date checkIn;
    private Boolean isForeigner;
    private String notes;

    public Student() {
    }

    public Student(Integer isu, String name, String group, Integer dormitory,
                   Integer room, Date checkIn, Boolean isForeigner, String notes) {
        this.isu = isu;
        this.name = name;
        this.group = group;
        this.dormitory = dormitory;
        this.room = room;
        this.checkIn = checkIn;
        this.isForeigner = isForeigner;
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

    public Date getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Date checkIn) {
        this.checkIn = checkIn;
    }

    public Boolean getIsForeigner() {
        return isForeigner;
    }

    public void setIsForeigner(Boolean isForeigner) {
        this.isForeigner = isForeigner;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
