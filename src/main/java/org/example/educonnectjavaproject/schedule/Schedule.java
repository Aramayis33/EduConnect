package org.example.educonnectjavaproject.schedule;

import jakarta.persistence.*;
import org.example.educonnectjavaproject.teacher.Teacher;
import org.example.educonnectjavaproject.groupinfo.GroupInfo;
import org.example.educonnectjavaproject.subject.Subjects;

@Entity
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int weekday;
    private int classHour;

    @ManyToOne
    @JoinColumn(name="subject_id")
    private Subjects subject;

    @ManyToOne
    @JoinColumn(name="group_id")
    private GroupInfo group;

    @ManyToOne
    @JoinColumn(name="teacher_id")
    private Teacher teacher;
    private String classroom;

    private int semester;
    private String semesterPart;

    public Schedule() {
    }

    public Schedule(int weekday, int classHour, Subjects subject, GroupInfo group, Teacher teacher, String classroom, int semester, String semesterPart) {
        this.weekday = weekday;
        this.classHour = classHour;
        this.subject = subject;
        this.group = group;
        this.teacher = teacher;
        this.classroom = classroom;
        this.semester = semester;
        this.semesterPart = semesterPart;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

    public int getClassHour() {
        return classHour;
    }

    public void setClassHour(int classHour) {
        this.classHour = classHour;
    }

    public Subjects getSubject() {
        return subject;
    }

    public void setSubject(Subjects subject) {
        this.subject = subject;
    }

    public GroupInfo getGroup() {
        return group;
    }

    public void setGroup(GroupInfo group) {
        this.group = group;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getSemesterPart() {
        return semesterPart;
    }

    public void setSemesterPart(String semesterPart) {
        this.semesterPart = semesterPart;
    }
}
